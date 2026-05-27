package com.manpowergroup.springboot.springboot3web.framework.security;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.framework.security.jwt.LoginPrincipal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityContext ユーティリティ
 * ログインユーザー情報の取得を一元管理する
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

    /**
     * 現在の認証情報から LoginPrincipal を取得する。
     * 未認証・不正 Principal の場合は BizException(UNAUTHORIZED) を送出する。
     */
    public static LoginPrincipal getLoginPrincipal() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw BizException.withDetail(ErrorCode.UNAUTHORIZED, "ユーザーはログインしていません。");
        }

        if (!(auth.getPrincipal() instanceof LoginPrincipal p)) {
            throw BizException.withDetail(ErrorCode.UNAUTHORIZED, "ユーザーはログインしていません。");
        }

        return p;
    }

    /**
     * 現在ログイン中のユーザーIDを取得する。
     */
    public static Long getCurrentUserId() {
        return getLoginPrincipal().userId();
    }

    /**
     * 現在ログイン中のアカウントIDを取得する。
     */
    public static Long getCurrentAccountId() {
        return getLoginPrincipal().accountId();
    }
}