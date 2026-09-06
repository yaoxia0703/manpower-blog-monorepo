package com.manpowergroup.blog.module.member.infrastructure.security;

import com.manpowergroup.blog.framework.security.PasswordService;
import com.manpowergroup.blog.module.member.domain.service.PasswordEncryptor;
import org.springframework.stereotype.Component;

/**
 * {@link PasswordEncryptor} の実装アダプタ。
 *
 * <p>framework 層の {@link PasswordService}（Spring Security の PasswordEncoder ラッパー）へ
 * 委譲する。framework への依存を infrastructure 層に閉じることで、
 * domain / application 層は具象実装を知らずに済む。</p>
 */
@Component("memberBCryptPasswordEncryptor")
public class BCryptPasswordEncryptor implements PasswordEncryptor {

    private final PasswordService passwordService;

    public BCryptPasswordEncryptor(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public String encrypt(String rawPassword) {
        return passwordService.encrypt(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordService.matches(rawPassword, encodedPassword);
    }
}
