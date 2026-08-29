package com.manpowergroup.blog.module.content.infrastructure.persistence.repository;

import com.manpowergroup.blog.module.content.domain.model.Article;
import com.manpowergroup.blog.module.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;
import com.manpowergroup.blog.module.content.domain.model.ArticleView;
import com.manpowergroup.blog.module.content.domain.repository.ArticleRepository;
import com.manpowergroup.blog.module.content.infrastructure.persistence.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisArticleRepositoryImpl implements ArticleRepository {

    private final ArticleMapper articleMapper;

    @Override
    public List<ArticleView> list(ArticleSearchCriteria criteria, long offset, long size) {
        return articleMapper.search(criteria, offset, size);
    }

    @Override
    public long count(ArticleSearchCriteria criteria) {
        return articleMapper.count(criteria);
    }

    @Override
    public Optional<Article> findById(Long id) {
        return Optional.ofNullable(articleMapper.selectById(id));
    }

    @Override
    public Optional<ArticleView> findViewById(Long id, ArticleStatus status) {
        return Optional.ofNullable(articleMapper.findViewById(id, status));
    }

    @Override
    public void create(Article article) {
        articleMapper.insert(article);
    }

    @Override
    public void update(Article article) {
        articleMapper.updateById(article);
    }

    @Override
    public void delete(Long id) {
        articleMapper.deleteById(id);
    }
}
