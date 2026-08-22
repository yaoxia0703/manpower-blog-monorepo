package com.manpowergroup.springboot.springboot3web.framework.security.jwt;

import com.manpowergroup.springboot.springboot3web.framework.security.authority.UserAuthorityProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * JWT認証フィルタ。
 *
 * 各リクエストに対してJWTトークンを検証し、
 * 認証情報（Authentication）をSecurityContextに設定する。
 *
 * 主な処理：
 * ・AuthorizationヘッダーからJWTトークンを取得
 * ・トークンの有効性を検証
 * ・ユーザー情報（userId、accountId）を取得
 * ・権限情報（permission）を取得してAuthorityに変換
 * ・SecurityContextへ認証情報を設定
 *
 * 本フィルタはOncePerRequestFilterを継承しており、
 * 1リクエストにつき1回のみ実行される。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserAuthorityProvider userAuthorityProvider;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            UserAuthorityProvider userAuthorityProvider
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userAuthorityProvider = userAuthorityProvider;
    }

    /**
     * フィルタ適用対象外のパスを判定する。
     *
     * ログイン・ログアウト・エラーページなど、
     * 認証不要なエンドポイントはフィルタをスキップする。
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getRequestURI();
        if (path == null || path.isBlank()) {
            return false;
        }

        return path.equals("/api/system/auth/login")
                || path.startsWith("/error/")
                || path.equals("/favicon.ico");

    }

    /**
     * JWT認証のメイン処理。
     *
     * リクエストからトークンを取得し、検証後にユーザー情報と権限を読み込み、
     * SecurityContextへ認証情報を設定する。
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorizationヘッダーからトークンを取得
        final String token = resolveToken(request);

        // トークンが存在しない場合はそのまま次のフィルタへ
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // トークンが無効な場合はスキップ
        if (!jwtTokenProvider.validate(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 既に認証情報が存在する場合はスキップ
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // トークンからユーザー情報を取得
        final Long userId = jwtTokenProvider.getUserId(token);
        final Long accountId = jwtTokenProvider.getAccountId(token);

        // userIdまたはaccountIdが取得できない場合はスキップ
        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (accountId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // DB等から権限コード一覧を取得
        final List<String> permissionCodes = userAuthorityProvider.loadAuthorityCodes(userId);

        // 権限コードをSpring Security用のAuthorityに変換
        final List<SimpleGrantedAuthority> authorities = permissionCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();

        // 認証主体（Principal）を生成
        final var principal = new LoginPrincipal(userId, accountId);

        // Authenticationオブジェクトを生成
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        // リクエスト詳細情報を設定
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // SecurityContextに認証情報を設定
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 次のフィルタへ処理を委譲
        filterChain.doFilter(request, response);
    }

    /**
     * AuthorizationヘッダーからJWTトークンを取得する。
     *
     * Bearerトークン形式（"Bearer xxx"）を解析し、
     * トークン部分のみを抽出して返却する。
     */
    private String resolveToken(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isBlank()) {
            return null;
        }

        final String prefix = "Bearer ";
        if (header.length() < prefix.length()) {
            return null;
        }
        if (!header.regionMatches(true, 0, prefix, 0, prefix.length())) {
            return null;
        }

        final String token = header.substring(prefix.length()).trim();
        return token.isBlank() ? null : token;
    }
}
