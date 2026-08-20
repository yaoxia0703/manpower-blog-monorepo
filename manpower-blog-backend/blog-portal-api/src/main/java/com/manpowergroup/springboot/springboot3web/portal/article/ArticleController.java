package com.manpowergroup.springboot.springboot3web.portal.article;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.Result;
import com.manpowergroup.springboot.springboot3web.content.application.assembler.ArticleAssembler;
import com.manpowergroup.springboot.springboot3web.content.application.dto.request.ArticleCreateRequest;
import com.manpowergroup.springboot.springboot3web.content.application.dto.request.ArticleQueryRequest;
import com.manpowergroup.springboot.springboot3web.content.application.dto.request.ArticleUpdateRequest;
import com.manpowergroup.springboot.springboot3web.content.application.dto.response.ArticleResponse;
import com.manpowergroup.springboot.springboot3web.content.application.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "記事の新規作成")
    @PostMapping("add")
    public Result<Long> add(@Valid @RequestBody ArticleCreateRequest request) {
        return Result.ok(articleService.addArticle(ArticleAssembler.toCommand(request)));
    }

    @Operation(summary = "記事一覧取得（ページング）")
    @GetMapping("pageList")
    public Result<JoinPageResult<ArticleResponse>> pageList(ArticleQueryRequest request) {
        return Result.ok(articleService.queryArticlePage(ArticleAssembler.toQuery(request)));
    }

    @Operation(summary = "記事の更新")
    @PutMapping("update")
    public Result<Boolean> update(@Valid @RequestBody ArticleUpdateRequest request) {
        return Result.ok(articleService.updateArticle(ArticleAssembler.toCommand(request)));
    }

    @Operation(summary = "記事の論理削除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(articleService.deleteArticle(id));
    }
}
