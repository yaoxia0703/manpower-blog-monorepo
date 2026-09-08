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
     * 現在ログイン中の認証主体IDを取得する。
     *
     * <p>種別によって指す実体が異なるため、呼び出し側は
     * どの面の principal かを前提にせず、必要なら
     * {@link LoginPrincipal#isUser()} 等で確認すること。</p>
     */
    public static Long getCurrentPrincipalId() {
        return getLoginPrincipal().principalId();
    }

    /**
     * 現在ログイン中のアカウントIDを取得する。
     */
    public static Long getCurrentAccountId() {
        return getLoginPrincipal().accountId();
    }
}
