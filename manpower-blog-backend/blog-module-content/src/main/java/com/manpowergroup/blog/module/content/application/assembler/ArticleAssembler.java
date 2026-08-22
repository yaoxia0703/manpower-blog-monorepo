package com.manpowergroup.blog.module.content.application.assembler;

import com.manpowergroup.blog.module.content.application.command.ArticleCreateCommand;
import com.manpowergroup.blog.module.content.application.command.ArticleStatusChangeCommand;
import com.manpowergroup.blog.module.content.application.command.ArticleUpdateCommand;
import com.manpowergroup.blog.module.content.application.dto.request.ArticleCreateRequest;
import com.manpowergroup.blog.module.content.application.dto.request.ArticleQueryRequest;
import com.manpowergroup.blog.module.content.application.dto.request.ArticleStatusUpdateRequest;
import com.manpowergroup.blog.module.content.application.dto.request.ArticleUpdateRequest;
import com.manpowergroup.blog.module.content.application.dto.request.PublishedArticleQueryRequest;
import com.manpowergroup.blog.module.content.application.dto.response.ArticleResponse;
import com.manpowergroup.blog.module.content.application.query.ArticlePageQuery;
import com.manpowergroup.blog.module.content.domain.model.ArticleView;
import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;

/** 記事の入出力変換を一元管理する。 */
public final class ArticleAssembler {

    private ArticleAssembler() {
    }

    public static ArticleCreateCommand toCommand(Long authorId, ArticleCreateRequest request) {
        return new ArticleCreateCommand(
                request.title(), request.summary(), request.content(), request.categoryId(),
                authorId, request.status()
        );
    }

    public static ArticleUpdateCommand toCommand(Long id, ArticleUpdateRequest request) {
        return new ArticleUpdateCommand(
                id, request.title(), request.summary(), request.content(),
                request.categoryId(), request.status()
        );
    }

    public static ArticleStatusChangeCommand toCommand(Long id, ArticleStatusUpdateRequest request) {
        return new ArticleStatusChangeCommand(id, request.status());
    }

    public static ArticlePageQuery toQuery(ArticleQueryRequest request) {
        return new ArticlePageQuery(
                request.pageNum(), request.pageSize(), request.title(), request.status(), request.categoryId()
        );
    }

    public static ArticlePageQuery toQuery(PublishedArticleQueryRequest request) {
        return new ArticlePageQuery(
                request.pageNum(), request.pageSize(), request.title(),
                ArticleStatus.PUBLISHED, request.categoryId()
        );
    }

    public static ArticleResponse toResponse(ArticleView view) {
        return new ArticleResponse(
                view.id(), view.title(), view.summary(), view.content(),
                view.categoryId(), view.categoryName(), view.authorId(), view.authorName(),
                view.status(), view.createdAt(), view.updatedAt()
        );
    }
}
