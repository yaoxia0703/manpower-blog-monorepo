package com.manpowergroup.springboot.springboot3web.framework.security.jwt;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.blog.common.util.CollectionUtils;
import com.manpowergroup.springboot.springboot3web.blog.common.util.StringUtils;
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
 * ・JWTトークンの生成（ユーザー情報をClaimsに格納）
 * ・トークンの有効性検証（署名／issuer／有効期限）
 * ・Claims情報の取得（userId、roles、accountId、nickNameなど）
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

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.issuer:springboot3web}") String issuer,
            @Value("${security.jwt.expire-seconds:7200}") long expireSeconds
    ) {
        if (base64Secret == null || base64Secret.isBlank()) {
            throw new IllegalArgumentException("security.jwt.secret is blank");
        }
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.issuer = issuer;
        this.expireSeconds = expireSeconds;
    }

    /**
     * ログイン成功時にJWTトークンを生成する。
     *
     * ユーザー情報（userId、roles、nickName、accountId）をClaimsとして格納し、
     * 署名付きのJWTトークンを発行する。
     *
     * @param user ログインユーザー情報
     * @return 生成されたJWTトークン
     */
    public String generateToken(LoginUser user) {
        Objects.requireNonNull(user, "user is null");
        Objects.requireNonNull(user.getUserId(), "userId is null");

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(Math.max(expireSeconds, 60));

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("roles", String.join(",", CollectionUtils.safeList(user.getRoleNames())))
                .claim("nickName", StringUtils.nullToEmpty(user.getNickName()))
                .claim("accountId", user.getAccountId())
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
            throw new IllegalArgumentException("JWT subject is blank");
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

    /**
     * JWTトークンからロール情報を取得する。
     *
     * rolesはカンマ区切りの文字列として格納されているため、
     * 必要に応じて分割して利用する。
     *
     * @param token JWTトークン
     * @return ロール情報（カンマ区切り文字列）
     */
    public String getRoles(String token) {
        Object roles = parseClaims(token).get("roles");
        return roles == null ? "" : String.valueOf(roles);
    }

    /**
     * JWTトークンからニックネームを取得する。
     *
     * Claimsに格納されたnickNameを取得する。
     *
     * @param token JWTトークン
     * @return ニックネーム
     */
    public String getNickName(String token) {
        Object nickName = parseClaims(token).get("nickName");
        return nickName == null ? "" : String.valueOf(nickName);
    }

}