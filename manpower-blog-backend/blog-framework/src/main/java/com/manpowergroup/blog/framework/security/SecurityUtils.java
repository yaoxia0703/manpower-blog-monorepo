package com.manpowergroup.blog.framework.security;

import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.framework.security.jwt.LoginPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityContext ユーティリティ
 * ログインユーザー情報の取得を一元管理する
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

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
