package com.manpowergroup.blog.framework.security.jwt;

import com.manpowergroup.blog.framework.security.authority.UserAuthorityProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * JWT認証フィルタ。
 *
 * <p>1インスタンスは1つの面に束縛される。担当する {@link JwtTokenProvider} が
 * 他面のトークンを検証段階で弾くため、本フィルタ内に「どの面のトークンか」を
 * 判定する分岐は存在しない。分岐が無いことが、判定漏れが起こらないことの根拠となる。</p>
 *
 * <p>Bean としての登録は {@code SecurityConfig} が面ごとに行う。
 * {@code @Component} を付けないのは、面ごとに異なる依存で
 * 複数インスタンスを生成する必要があるため。</p>
 *
 * 主な処理：
 * ・AuthorizationヘッダーからJWTトークンを取得
 * ・トークンの有効性を検証（署名／issuer／有効期限／主体種別）
 * ・認証主体の識別情報を取得
 * ・権限情報を取得してAuthorityに変換
 * ・SecurityContextへ認証情報を設定
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserAuthorityProvider authorityProvider;
    private final Set<String> skipPaths;

    /**
     * @param jwtTokenProvider  担当する面のトークンプロバイダ
     * @param authorityProvider 権限コードの取得元。権限体系を持たない面では
     *                          空リストを返す実装を渡す
     * @param skipPaths         フィルタを適用しないパス（ログイン等）
     */
    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            UserAuthorityProvider authorityProvider,
            Set<String> skipPaths
    ) {
        this.jwtTokenProvider = Objects.requireNonNull(jwtTokenProvider);
        this.authorityProvider = Objects.requireNonNull(authorityProvider);
        this.skipPaths = Set.copyOf(Objects.requireNonNull(skipPaths));
    }

    /**
     * フィルタ適用対象外のパスを判定する。
     *
     * <p>ログイン等、トークンを持たないことが前提のエンドポイントを除外する。</p>
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getRequestURI();
        if (path == null || path.isBlank()) {
            return false;
        }
        return skipPaths.contains(path);
    }

    /**
     * JWT認証のメイン処理。
     *
     * <p>リクエストからトークンを取得し、検証後に識別情報と権限を読み込み、
     * SecurityContextへ認証情報を設定する。
     * 検証に失敗した場合は認証情報を設定せずに次へ委譲する。
     * 認証されていないリクエストの扱いは認可設定側が決める。</p>
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String token = resolveToken(request);

        // トークンが存在しない場合はそのまま次のフィルタへ
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 署名・issuer・有効期限・主体種別のいずれかが不正な場合はスキップ。
        // 他面で発行されたトークンはここで弾かれる。
        if (!jwtTokenProvider.validate(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 既に認証情報が存在する場合はスキップ
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final Long principalId = jwtTokenProvider.getPrincipalId(token);
        final Long accountId = jwtTokenProvider.getAccountId(token);

        if (principalId == null || accountId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 権限コード一覧を取得する。権限体系を持たない面では空リストが返る。
        final List<String> permissionCodes = authorityProvider.loadAuthorityCodes(principalId);

        // 権限コードをSpring Security用のAuthorityに変換
        final List<SimpleGrantedAuthority> authorities = permissionCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();

        // 認証主体を生成する。種別は Provider が担当する面から決まるため、
        // トークンの内容によって変わることはない。
        final var principal = new LoginPrincipal(
                jwtTokenProvider.principalType(), principalId, accountId);

        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    /**
     * AuthorizationヘッダーからJWTトークンを取得する。
     *
     * <p>Bearerトークン形式（"Bearer xxx"）を解析し、
     * トークン部分のみを抽出して返却する。</p>
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
