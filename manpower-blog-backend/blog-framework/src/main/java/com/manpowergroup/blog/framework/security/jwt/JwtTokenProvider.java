package com.manpowergroup.blog.framework.security.jwt;

import com.manpowergroup.blog.shared.util.StringUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * JWTトークンの生成および検証を行うプロバイダクラス。
 *
 * 本クラスは、ログイン成功時のトークン発行および、
 * リクエスト時のトークン検証・Claims情報の取得を担当する。
 *
 * 主な機能：
 * ・JWTトークンの生成（認証主体の識別情報をClaimsに格納）
 * ・トークンの有効性検証（署名／issuer／有効期限）
 * ・Claims情報の取得（subject、accountId）
 *
 * セキュリティ設定：
 * ・署名アルゴリズム：HS256
 * ・issuerチェックあり
 * ・有効期限付きトークン
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final String issuer;
    private final long expireSeconds;

    /**
     * 設定値からトークン発行に必要な情報を組み立てる。
     *
     * <p>秘密鍵と issuer はいずれも既定値を持たせない。
     * 秘密鍵に既定値を与えると弱い鍵のまま本番へ到達しうるため、
     * issuer に既定値を与えると設定漏れに気付けないまま
     * 環境間でトークンが相互に通用してしまうため、
     * どちらも未設定なら起動時点で失敗させる。</p>
     */
    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expire-seconds:7200}") long expireSeconds
    ) {
        if (base64Secret == null || base64Secret.isBlank()) {
            throw new IllegalArgumentException("security.jwt.secretが設定されていません");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("security.jwt.issuerが設定されていません");
        }
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.issuer = issuer;
        this.expireSeconds = expireSeconds;
    }

    /**
     * ログイン成功時にJWTトークンを生成する。
     *
     * 認証主体の識別情報をClaimsとして格納し、署名付きのJWTトークンを発行する。
     *
     * <p>ロール・表示名はトークンに載せない。いずれも検証側で参照されておらず、
     * かつJWTのペイロードは署名されているだけで暗号化されていないため、
     * 利用者名を全リクエストのヘッダーへ平文で載せることになるため。
     * 表示用の情報は認証済みの状態で {@code /me} から取得する。</p>
     *
     * @param subject 認証主体の識別情報
     * @return 生成されたJWTトークン
     */
    public String generateToken(TokenSubject subject) {
        Objects.requireNonNull(subject, "認証主体情報は必須です");

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(Math.max(expireSeconds, 60));

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(subject.principalId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("accountId", subject.accountId())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWTトークンの有効性を検証する。
     *
     * 署名検証、issuerチェック、有効期限チェック、および形式の検証を行い、
     * 問題がなければtrueを返却する。
     *
     * @param token JWTトークン
     * @return 有効な場合はtrue、無効な場合はfalse
     */
    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * JWTトークンからClaims情報を取得する。
     *
     * トークンの署名およびissuerを検証した上で、
     * トークンに含まれるペイロード情報（Claims）を取得する。
     *
     * @param token JWTトークン
     * @return Claims情報
     */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWTトークンからユーザーID（subject）を取得する。
     *
     * subjectに格納されたユーザーIDを取得し、
     * Long型に変換して返却する。
     *
     * @param token JWTトークン
     * @return ユーザーID
     */
    public Long getUserId(String token) {
        String sub = parseClaims(token).getSubject();
        if (!StringUtils.hasText(sub)) {
            throw new IllegalArgumentException("JWTのsubjectが設定されていません");
        }
        return Long.valueOf(sub);
    }

    /**
     * JWTトークンからaccountIdを取得する。
     *
     * Claimsに格納されたaccountIdを取得し、
     * Long型に変換して返却する。
     *
     * @param token JWTトークン
     * @return accountId
     */
    public Long getAccountId(String token) {
        Object accountId = parseClaims(token).get("accountId");
        return Long.valueOf(accountId.toString());
    }

}
