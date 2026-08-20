package com.manpowergroup.springboot.springboot3web.content.application.service;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleCreateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleUpdateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.dto.response.ArticleResponse;
import com.manpowergroup.springboot.springboot3web.content.application.query.ArticlePageQuery;

/** 記事のユースケースを提供する。 */
public interface ArticleService {

    JoinPageResult<ArticleResponse> queryArticlePage(ArticlePageQuery query);

    Long addArticle(ArticleCreateCommand command);

    boolean updateArticle(ArticleUpdateCommand command);

    boolean deleteArticle(Long id);
}
