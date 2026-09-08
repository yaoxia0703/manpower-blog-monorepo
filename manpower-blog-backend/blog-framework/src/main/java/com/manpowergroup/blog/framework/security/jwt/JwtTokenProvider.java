package com.manpowergroup.blog.framework.security.jwt;

import com.manpowergroup.blog.shared.util.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * JWTトークンの生成および検証を行うプロバイダ。
 *
 * <p>1インスタンスは1つの認証主体種別（面）に束縛される。
 * 面ごとに秘密鍵と issuer を分けることで、他面で発行されたトークンは
 * 署名検証・issuer検証の段階で失敗する。アプリケーション層の
 * 条件分岐に依存しないため、新しい経路を追加した際の判定漏れが起こらない。</p>
 *
 * <p>Bean としての登録は {@link JwtProviderConfig} が行う。
 * {@code @Component} を付けないのは、面ごとに異なる設定値で
 * 複数インスタンスを生成する必要があるため。</p>
 *
 * 主な機能：
 * ・JWTトークンの生成（認証主体の識別情報をClaimsに格納）
 * ・トークンの有効性検証（署名／issuer／有効期限／主体種別）
 * ・Claims情報の取得（subject、accountId）
 *
 * セキュリティ設定：
 * ・署名アルゴリズム：HS256
 * ・issuerチェックあり
 * ・有効期限付きトークン
 */
public class JwtTokenProvider {

    /** 認証主体の種別を格納するクレーム名。 */
    private static final String CLAIM_PRINCIPAL_TYPE = "principalType";

    /** ログインアカウントIDを格納するクレーム名。 */
    private static final String CLAIM_ACCOUNT_ID = "accountId";

    private final PrincipalType principalType;
    private final SecretKey secretKey;
    private final String issuer;
    private final long expireSeconds;

    /**
     * トークン発行に必要な情報を組み立てる。
     *
     * <p>秘密鍵と issuer はいずれも既定値を持たせない。
     * 秘密鍵に既定値を与えると弱い鍵のまま本番へ到達しうるため、
     * issuer に既定値を与えると設定漏れに気付けないまま
     * 環境間でトークンが相互に通用してしまうため、
     * どちらも未設定なら起動時点で失敗させる。</p>
     *
     * @param principalType 本インスタンスが担当する認証主体の種別
     * @param base64Secret  Base64エンコードされた秘密鍵
     * @param issuer        発行者
     * @param expireSeconds 有効期限（秒）
     */
    public JwtTokenProvider(
            PrincipalType principalType,
            String base64Secret,
            String issuer,
            long expireSeconds
    ) {
        if (principalType == null) {
            throw new IllegalArgumentException("認証主体の種別が指定されていません");
        }
        if (base64Secret == null || base64Secret.isBlank()) {
            throw new IllegalArgumentException(
                    "JWTの秘密鍵が設定されていません: " + principalType);
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException(
                    "JWTのissuerが設定されていません: " + principalType);
        }
        this.principalType = principalType;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.issuer = issuer;
        this.expireSeconds = expireSeconds;
    }

    /** 本インスタンスが担当する認証主体の種別。 */
    public PrincipalType principalType() {
        return principalType;
    }

    /**
     * ログイン成功時にJWTトークンを生成する。
     *
     * <p>ロール・表示名はトークンに載せない。いずれも検証側で参照されておらず、
     * かつJWTのペイロードは署名されているだけで暗号化されていないため、
     * 利用者名を全リクエストのヘッダーへ平文で載せることになるため。
     * 表示用の情報は認証済みの状態で取得する。</p>
     *
     * @param subject 認証主体の識別情報
     * @return 生成されたJWTトークン
     * @throws IllegalArgumentException 本インスタンスの担当種別と一致しない場合
     */
    public String generateToken(TokenSubject subject) {
        Objects.requireNonNull(subject, "認証主体情報は必須です");
        if (subject.principalType() != principalType) {
            throw new IllegalArgumentException(
                    "本Providerは " + principalType + " 用です。"
                            + subject.principalType() + " のトークンは発行できません");
        }

        final Instant now = Instant.now();
        final Instant exp = now.plusSeconds(Math.max(expireSeconds, 60));

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(subject.principalId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim(CLAIM_PRINCIPAL_TYPE, principalType.name())
                .claim(CLAIM_ACCOUNT_ID, subject.accountId())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWTトークンの有効性を検証する。
     *
     * <p>署名・issuer・有効期限に加えて、主体種別が本インスタンスの担当と
     * 一致することを確認する。鍵が分離されているため他面のトークンは
     * 通常ここへ到達しないが、鍵の設定を誤って共有した場合の最後の防波堤となる。</p>
     *
     * @param token JWTトークン
     * @return 有効な場合はtrue、無効な場合はfalse
     */
    public boolean validate(String token) {
        try {
            final Claims claims = parseClaims(token);
            return principalType.name().equals(claims.get(CLAIM_PRINCIPAL_TYPE, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * JWTトークンからClaims情報を取得する。
     *
     * <p>トークンの署名およびissuerを検証した上で、
     * トークンに含まれるペイロード情報（Claims）を取得する。</p>
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
     * JWTトークンから認証主体のID（subject）を取得する。
     *
     * @param token JWTトークン
     * @return 認証主体のID
     */
    public Long getPrincipalId(String token) {
        final String sub = parseClaims(token).getSubject();
        if (!StringUtils.hasText(sub)) {
            throw new IllegalArgumentException("JWTのsubjectが設定されていません");
        }
        return Long.valueOf(sub);
    }

    /**
     * JWTトークンからaccountIdを取得する。
     *
     * @param token JWTトークン
     * @return accountId
     */
    public Long getAccountId(String token) {
        final Object accountId = parseClaims(token).get(CLAIM_ACCOUNT_ID);
        return accountId == null ? null : Long.valueOf(accountId.toString());
    }

}
