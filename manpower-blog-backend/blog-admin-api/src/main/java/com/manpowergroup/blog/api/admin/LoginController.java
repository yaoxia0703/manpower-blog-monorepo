package com.manpowergroup.blog.api.admin;

import com.manpowergroup.blog.shared.api.LoginResponse;
import com.manpowergroup.blog.shared.dto.LoginUser;
import com.manpowergroup.blog.shared.api.Result;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.framework.security.SecurityUtils;
import com.manpowergroup.blog.framework.security.jwt.JwtTokenProvider;
import com.manpowergroup.blog.framework.security.jwt.LoginPrincipal;
import com.manpowergroup.blog.module.system.application.assembler.LoginAssembler;
import com.manpowergroup.blog.module.system.application.dto.request.auth.LoginRequest;
import com.manpowergroup.blog.module.system.application.dto.response.me.MeResponse;
import com.manpowergroup.blog.module.system.application.service.LoginAppService;
import com.manpowergroup.blog.module.system.application.service.MenuAppService;
import com.manpowergroup.blog.module.system.application.service.PermissionAppService;
import com.manpowergroup.blog.module.system.application.service.UserAppService;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuTreeResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * ログイン関連のAPIコントローラー
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/api/system/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginAppService loginService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAppService userAppService;
    private final MenuAppService menuAppService;
    private final PermissionAppService permissionAppService;

    /**
     * ログイン処理
     *
     * @param loginRequest ログインリクエスト
     * @param response     HTTPレスポンス（JWTトークンをヘッダーに設定するため）
     * @return ログインユーザー情報とアクセストークンを含むレスポンス
     */
    @PostMapping("/login")
    public Result<LoginResponse<LoginUser>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginUser loginUser = loginService.login(LoginAssembler.toCommand(loginRequest));

        String token = jwtTokenProvider.generateToken(loginUser);

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return Result.ok(
                new LoginResponse<>(token, loginUser)
        );
    }


    /**
     * ログアウト処理
     *
     * @param request HTTPリクエスト（セッションを無効化するため）
     * @return ログアウト成功のレスポンス
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        // JWTトークンはクライアント側で管理されるため、サーバー側でのトークン無効化は行わない。
        // ただし、セッションが存在する場合は無効化する。
        HttpSession session = request.getSession(false);
        // セッションが存在する場合は無効化する（JWTトークンはクライアント側で管理されるため、サーバー側でのトークン無効化は行わない）
        if (session != null) {
            // セッションを無効化する
            session.invalidate();
        }
        // セキュリティコンテキストをクリアする
        SecurityContextHolder.clearContext();
        return Result.ok();
    }


    /**
     * ログインユーザー情報の取得
     *
     * @return ログインユーザー情報
     */
    @GetMapping("/me")
    public Result<MeResponse> me() {

        // SecurityUtils で Principal を取得（未認証時は UNAUTHORIZED を送出）
        final LoginPrincipal principal = SecurityUtils.getLoginPrincipal();

        // 1. ユーザー情報の取得
        final LoginUser loginUser = userAppService.getCurrentUserContext(
                principal.userId(), principal.accountId()
        );
        if (loginUser == null) {
            throw BizException.withDetail(ErrorCode.UNAUTHORIZED, "ユーザーはログインしていません。");
        }

        // 2. メニュー情報の取得
        final List<MenuTreeResponse> menus = menuAppService.listTreeByUserId(principal.userId());

        // 3. 権限情報の取得
        //    特権ロールの扱いを含む実効権限の算出は UserAuthorities に集約されており、
        //    API 認可（DynamicAuthorizationManager）と同一のルールから導出される
        final List<String> permissions = List.copyOf(
                permissionAppService.loadUserAuthorities(principal.userId())
                        .effectivePermissionCodes());

        // 4. レスポンスの組み立て
        return Result.ok(
                new MeResponse(loginUser, menus, permissions)
        );
    }


}
