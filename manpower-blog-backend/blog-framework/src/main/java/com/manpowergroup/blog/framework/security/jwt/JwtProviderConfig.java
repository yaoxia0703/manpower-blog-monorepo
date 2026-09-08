package com.manpowergroup.blog.framework.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 面ごとの {@link JwtTokenProvider} を登録する。
 *
 * <p>秘密鍵と issuer を面ごとに分ける。会員はポータルから自己登録できるため、
 * 鍵を共有すると「正規に発行された、署名検証を通るトークン」を
 * 誰でも取得できることになる。鍵が分かれていれば、会員トークンは
 * 運用者面の検証段階で失敗し、アプリケーション層の判定を経由しない。</p>
 *
 * <p>有効期限のみ既定値を持つ。鍵と issuer に既定値を与えないのは、
 * 設定漏れを起動時に失敗させるため。</p>
 */
@Configuration
public class JwtProviderConfig {

    /** 運用者面のトークン発行・検証を担う。 */
    @Bean
    public JwtTokenProvider adminJwtTokenProvider(
            @Value("${security.jwt.admin.secret}") String secret,
            @Value("${security.jwt.admin.issuer}") String issuer,
            @Value("${security.jwt.admin.expire-seconds:7200}") long expireSeconds
    ) {
        return new JwtTokenProvider(PrincipalType.USER, secret, issuer, expireSeconds);
    }

    /**
     * 会員面のトークン発行・検証を担う。
     *
     * <p>有効期限を運用者面より長くしてよいかは運用判断のため、
     * 既定値は同一としつつ設定で個別に変更できるようにしている。</p>
     */
    @Bean
    public JwtTokenProvider memberJwtTokenProvider(
            @Value("${security.jwt.member.secret}") String secret,
            @Value("${security.jwt.member.issuer}") String issuer,
            @Value("${security.jwt.member.expire-seconds:7200}") long expireSeconds
    ) {
        return new JwtTokenProvider(PrincipalType.MEMBER, secret, issuer, expireSeconds);
    }
}
