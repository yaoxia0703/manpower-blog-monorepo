package com.manpowergroup.blog.module.content.application.service;

import com.manpowergroup.blog.shared.api.JoinPageResult;
import com.manpowergroup.blog.module.content.application.dto.response.ArticleResponse;
import com.manpowergroup.blog.module.content.application.query.ArticlePageQuery;

/** 匿名公開用の公開済み記事検索ユースケースを提供する。 */
public interface PublishedArticleQueryService {

    JoinPageResult<ArticleResponse> page(ArticlePageQuery query);

    ArticleResponse findById(Long id);
}
