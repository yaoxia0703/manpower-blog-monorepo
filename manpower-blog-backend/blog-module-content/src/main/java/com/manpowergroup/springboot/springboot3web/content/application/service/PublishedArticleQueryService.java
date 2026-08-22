package com.manpowergroup.springboot.springboot3web.content.application.service;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.content.application.dto.response.ArticleResponse;
import com.manpowergroup.springboot.springboot3web.content.application.query.ArticlePageQuery;

/** 匿名公開用の公開済み記事検索ユースケースを提供する。 */
public interface PublishedArticleQueryService {

    JoinPageResult<ArticleResponse> page(ArticlePageQuery query);

    ArticleResponse findById(Long id);
}
