package com.manpowergroup.blog.api.member;

import com.manpowergroup.blog.module.member.application.assembler.member.MemberAssembler;
import com.manpowergroup.blog.module.member.application.dto.request.member.MemberCreateRequest;
import com.manpowergroup.blog.module.member.application.service.member.MemberAppService;
import com.manpowergroup.blog.shared.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会員管理API。
 *
 * <p>本エンドポイントは会員面のチェーンで permitAll に設定されている。
 * 会員管理は運用者面の管理者が行うため、会員面では認証済みの会員に対して
 * 会員情報を変更することは許可しない。</p>
 */
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "会員CRUD", description = "会員のCRUD操作")
public class MemberController {

    private final MemberAppService service;


    @Operation(summary = "会員を新規作成する")
    @RequestMapping
    public Result<Long> create(@RequestBody @Valid MemberCreateRequest request) {
        log.info("[MemberController#create]リクエストを受信しました");
        return Result.ok(service.create(MemberAssembler.toMemberCreateCommand(request)));
    }

}
