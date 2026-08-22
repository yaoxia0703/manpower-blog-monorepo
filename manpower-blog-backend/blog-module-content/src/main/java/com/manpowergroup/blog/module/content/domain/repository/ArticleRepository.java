package com.manpowergroup.blog.module.content.domain.repository;

import com.manpowergroup.blog.module.content.domain.model.Article;
import com.manpowergroup.blog.module.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;
import com.manpowergroup.blog.module.content.domain.model.ArticleView;

import java.util.List;
import java.util.Optional;

/** 記事永続化ポート。 */
public interface ArticleRepository {

    List<ArticleView> list(ArticleSearchCriteria criteria, long offset, long size);

    long count(ArticleSearchCriteria criteria);

    Optional<Article> findById(Long id);

    Optional<ArticleView> findViewById(Long id, ArticleStatus status);

    void create(Article article);

    void update(Article article);

    void delete(Long id);
}
