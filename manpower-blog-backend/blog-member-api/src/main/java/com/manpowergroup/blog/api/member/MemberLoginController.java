package com.manpowergroup.blog.api.member;

import com.manpowergroup.blog.framework.security.jwt.JwtTokenProvider;
import com.manpowergroup.blog.framework.security.jwt.PrincipalType;
import com.manpowergroup.blog.framework.security.jwt.TokenSubject;
import com.manpowergroup.blog.module.member.application.assembler.auth.LoginAssembler;
import com.manpowergroup.blog.module.member.application.dto.request.auth.LoginRequest;
import com.manpowergroup.blog.module.member.application.dto.response.auth.LoginMember;
import com.manpowergroup.blog.module.member.application.service.auth.MemberLoginAppService;
import com.manpowergroup.blog.shared.api.LoginResponse;
import com.manpowergroup.blog.shared.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会員認証API。
 *
 * <p>本エンドポイントは会員面のチェーンで permitAll に設定されている。
 * 発行するトークンは会員面の Provider によるものであり、
 * 運用者面では署名検証を通過しない。</p>
 */
@RestController
@RequestMapping("/api/member/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "会員認証", description = "会員のログイン")
public class MemberLoginController {

    private final MemberLoginAppService memberLoginAppService;

    /**
     * 会員面の Provider。
     *
     * <p>フィールド名を Bean 名と一致させ、運用者面の Provider が
     * 注入されることを防ぐ。取り違えた場合は {@code TokenSubject} の
     * 種別照合により起動後の初回発行時に例外となる。</p>
     */
    private final JwtTokenProvider memberJwtTokenProvider;

    @Operation(summary = "会員ログイン")
    @PostMapping("/login")
    public Result<LoginResponse<LoginMember>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        final LoginMember loginMember =
                memberLoginAppService.login(LoginAssembler.toCommand(loginRequest));

        // framework 層へは業務DTOではなく識別情報のみを渡す
        final String token = memberJwtTokenProvider.generateToken(
                new TokenSubject(PrincipalType.MEMBER, loginMember.memberId(), loginMember.accountId())
        );

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return Result.ok(new LoginResponse<>(token, loginMember));
    }
}
