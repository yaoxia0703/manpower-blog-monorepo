package com.manpowergroup.blog.api.portal.article;

import com.manpowergroup.blog.shared.api.PageResult;
import com.manpowergroup.blog.shared.api.Result;
import com.manpowergroup.blog.module.content.application.assembler.ArticleAssembler;
import com.manpowergroup.blog.module.content.application.dto.request.PublishedArticleQueryRequest;
import com.manpowergroup.blog.module.content.application.dto.response.ArticleResponse;
import com.manpowergroup.blog.module.content.application.service.PublishedArticleQueryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal/article")
@RequiredArgsConstructor
public class PortalArticleController {

    private final PublishedArticleQueryService publishedArticleQueryService;

    @Operation(summary = "公開記事一覧取得（ページング）")
    @GetMapping("/page")
    public Result<PageResult<ArticleResponse>> page(PublishedArticleQueryRequest request) {
        return Result.ok(publishedArticleQueryService.page(ArticleAssembler.toQuery(request)));
    }

    @Operation(summary = "公開記事詳細取得")
    @GetMapping("/{id}")
    public Result<ArticleResponse> findById(
            @PathVariable @Positive(message = "記事IDが不正です") Long id) {
        return Result.ok(publishedArticleQueryService.findById(id));
    }
}
