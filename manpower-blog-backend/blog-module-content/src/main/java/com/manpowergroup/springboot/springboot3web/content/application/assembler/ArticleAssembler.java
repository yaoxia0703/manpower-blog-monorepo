package com.manpowergroup.springboot.springboot3web.content.application.assembler;

import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleCreateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleUpdateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.dto.request.ArticleCreateRequest;
import com.manpowergroup.springboot.springboot3web.content.application.dto.request.ArticleQueryRequest;
import com.manpowergroup.springboot.springboot3web.content.application.dto.request.ArticleUpdateRequest;
import com.manpowergroup.springboot.springboot3web.content.application.dto.response.ArticleResponse;
import com.manpowergroup.springboot.springboot3web.content.application.query.ArticlePageQuery;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleView;

/** 記事の入出力変換を一元管理する。 */
public final class ArticleAssembler {

    private ArticleAssembler() {
    }

    public static ArticleCreateCommand toCommand(ArticleCreateRequest request) {
        return new ArticleCreateCommand(
                request.title(), request.summary(), request.content(), request.categoryId(),
                request.authorId(), request.status()
        );
    }

    public static ArticleUpdateCommand toCommand(ArticleUpdateRequest request) {
        return new ArticleUpdateCommand(
                request.id(), request.title(), request.summary(), request.content(),
                request.categoryId(), request.status()
        );
    }

    public static ArticlePageQuery toQuery(ArticleQueryRequest request) {
        return new ArticlePageQuery(
                request.pageNum(), request.pageSize(), request.title(), request.status(), request.categoryId()
        );
    }

    public static ArticleResponse toResponse(ArticleView view) {
        return new ArticleResponse(
                view.id(), view.title(), view.summary(), view.content(), view.authorName(),
                view.categoryName(), view.createdAt(), view.updatedAt()
        );
    }
}
