package com.manpowergroup.springboot.springboot3web.content.application.service;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleCreateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleUpdateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.dto.response.ArticleResponse;
import com.manpowergroup.springboot.springboot3web.content.application.query.ArticlePageQuery;

/** 管理画面向けの記事ユースケースを提供する。 */
public interface AdminArticleAppService {

    JoinPageResult<ArticleResponse> page(ArticlePageQuery query);

    ArticleResponse findById(Long id);

    Long create(ArticleCreateCommand command);

    void update(ArticleUpdateCommand command);

    void delete(Long id);

    void changeStatus(ArticleStatusChangeCommand command);
}
