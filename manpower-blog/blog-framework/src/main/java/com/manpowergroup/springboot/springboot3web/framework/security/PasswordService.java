package com.manpowergroup.springboot.springboot3web.framework.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 平文パスワードを暗号化する（ユーザー登録・パスワード変更時に使用）。
     */
    public String encrypt(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 平文パスワードと保存済みの暗号化パスワードを照合する（ログイン時に使用）。
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
