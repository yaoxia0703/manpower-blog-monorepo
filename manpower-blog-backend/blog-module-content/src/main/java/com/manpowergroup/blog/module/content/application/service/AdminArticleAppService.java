package com.manpowergroup.blog.module.content.application.service;

import com.manpowergroup.blog.shared.api.PageResult;
import com.manpowergroup.blog.module.content.application.command.ArticleCreateCommand;
import com.manpowergroup.blog.module.content.application.command.ArticleStatusChangeCommand;
import com.manpowergroup.blog.module.content.application.command.ArticleUpdateCommand;
import com.manpowergroup.blog.module.content.application.dto.response.ArticleResponse;
import com.manpowergroup.blog.module.content.application.query.ArticlePageQuery;

/** 管理画面向けの記事ユースケースを提供する。 */
public interface AdminArticleAppService {

    PageResult<ArticleResponse> page(ArticlePageQuery query);

    ArticleResponse findById(Long id);

    Long create(ArticleCreateCommand command);

    void update(ArticleUpdateCommand command);

    void delete(Long id);

    void changeStatus(ArticleStatusChangeCommand command);
}
