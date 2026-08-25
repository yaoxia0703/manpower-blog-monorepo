package com.manpowergroup.blog.api.admin;

import com.manpowergroup.blog.shared.api.JoinPageResult;
import com.manpowergroup.blog.shared.api.Result;
import com.manpowergroup.blog.module.content.application.assembler.ArticleAssembler;
import com.manpowergroup.blog.module.content.application.dto.request.ArticleCreateRequest;
import com.manpowergroup.blog.module.content.application.dto.request.ArticleQueryRequest;
import com.manpowergroup.blog.module.content.application.dto.request.ArticleStatusUpdateRequest;
import com.manpowergroup.blog.module.content.application.dto.request.ArticleUpdateRequest;
import com.manpowergroup.blog.module.content.application.dto.response.ArticleResponse;
import com.manpowergroup.blog.module.content.application.service.AdminArticleAppService;
import com.manpowergroup.blog.framework.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/system/article")
@RequiredArgsConstructor
public class ArticleController {

    private final AdminArticleAppService adminArticleAppService;

    @Operation(summary = "管理用記事一覧取得（ページング）")
    @GetMapping("/page")
    public Result<JoinPageResult<ArticleResponse>> page(ArticleQueryRequest request) {
        return Result.ok(adminArticleAppService.page(ArticleAssembler.toQuery(request)));
    }

    @Operation(summary = "管理用記事詳細取得")
    @GetMapping("/{id}")
    public Result<ArticleResponse> findById(
            @PathVariable @NotNull(message = "記事IDは必須です") Long id) {
        return Result.ok(adminArticleAppService.findById(id));
    }

    @Operation(summary = "記事の新規作成")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ArticleCreateRequest request) {
        return Result.ok(adminArticleAppService.create(
                ArticleAssembler.toCommand(SecurityUtils.getCurrentUserId(), request)));
    }

    @Operation(summary = "記事の更新")
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable @NotNull(message = "記事IDは必須です") Long id,
            @Valid @RequestBody ArticleUpdateRequest request) {
        adminArticleAppService.update(ArticleAssembler.toCommand(id, request));
        return Result.ok();
    }

    @Operation(summary = "記事の論理削除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable @NotNull(message = "記事IDは必須です") Long id) {
        adminArticleAppService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "記事状態の変更")
    @PatchMapping("/{id}/status")
    public Result<Void> changeStatus(
            @PathVariable @NotNull(message = "記事IDは必須です") Long id,
            @Valid @RequestBody ArticleStatusUpdateRequest request) {
        adminArticleAppService.changeStatus(ArticleAssembler.toCommand(id, request));
        return Result.ok();
    }
}
