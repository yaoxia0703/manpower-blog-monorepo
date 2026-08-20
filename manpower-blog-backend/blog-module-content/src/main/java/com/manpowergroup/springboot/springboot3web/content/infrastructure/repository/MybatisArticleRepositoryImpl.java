package com.manpowergroup.springboot.springboot3web.content.infrastructure.repository;

import com.manpowergroup.springboot.springboot3web.content.domain.model.Article;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleView;
import com.manpowergroup.springboot.springboot3web.content.domain.repository.ArticleRepository;
import com.manpowergroup.springboot.springboot3web.content.infrastructure.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisArticleRepositoryImpl implements ArticleRepository {

    private final ArticleMapper articleMapper;

    @Override
    public List<ArticleView> search(ArticleSearchCriteria criteria, long offset, long size) {
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
    public void save(Article article) {
        articleMapper.insert(article);
    }

    @Override
    public void update(Article article) {
        articleMapper.updateById(article);
    }

    @Override
    public void deleteById(Long id) {
        articleMapper.deleteById(id);
    }
}
