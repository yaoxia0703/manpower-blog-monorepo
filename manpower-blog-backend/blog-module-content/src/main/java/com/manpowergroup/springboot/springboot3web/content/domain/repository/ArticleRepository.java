package com.manpowergroup.springboot.springboot3web.content.domain.repository;

import com.manpowergroup.springboot.springboot3web.content.domain.model.Article;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleView;

import java.util.List;
import java.util.Optional;

/** 記事永続化ポート。 */
public interface ArticleRepository {

    List<ArticleView> search(ArticleSearchCriteria criteria, long offset, long size);

    long count(ArticleSearchCriteria criteria);

    Optional<Article> findById(Long id);

    void save(Article article);

    void update(Article article);

    void deleteById(Long id);
}
