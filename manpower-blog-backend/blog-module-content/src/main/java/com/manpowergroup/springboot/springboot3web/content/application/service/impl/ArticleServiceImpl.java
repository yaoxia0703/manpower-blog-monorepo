package com.manpowergroup.springboot.springboot3web.content.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.content.application.assembler.ArticleAssembler;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleCreateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleUpdateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.dto.response.ArticleResponse;
import com.manpowergroup.springboot.springboot3web.content.application.query.ArticlePageQuery;
import com.manpowergroup.springboot.springboot3web.content.application.service.ArticleService;
import com.manpowergroup.springboot.springboot3web.content.domain.model.Article;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.springboot.springboot3web.content.domain.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.manpowergroup.springboot.springboot3web.blog.common.util.ServiceHelper.safePageNum;
import static com.manpowergroup.springboot.springboot3web.blog.common.util.ServiceHelper.safePageSize;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    @Transactional(readOnly = true)
    public JoinPageResult<ArticleResponse> queryArticlePage(ArticlePageQuery query) {
        final long pageNum = safePageNum(query.pageNum());
        final long pageSize = safePageSize(query.pageSize());
        final long offset = (pageNum - 1) * pageSize;
        final ArticleSearchCriteria criteria = new ArticleSearchCriteria(
                query.title(), query.status(), query.categoryId());

        return JoinPageResult.of(
                articleRepository.search(criteria, offset, pageSize).stream()
                        .map(ArticleAssembler::toResponse)
                        .toList(),
                articleRepository.count(criteria),
                pageNum,
                pageSize
        );
    }

    @Override
    @Transactional
    public Long addArticle(ArticleCreateCommand command) {
        final Article article = Article.create(
                command.title(), command.summary(), command.content(), command.categoryId(),
                command.authorId(), command.status()
        );
        articleRepository.save(article);
        log.info("記事を作成しました。id={}", article.getId());
        return article.getId();
    }

    @Override
    @Transactional
    public boolean updateArticle(ArticleUpdateCommand command) {
        final Article article = getRequiredArticle(command.id());
        article.update(
                command.title(), command.summary(), command.content(), command.categoryId(), command.status());
        articleRepository.update(article);
        log.info("記事を更新しました。id={}", article.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean deleteArticle(Long id) {
        getRequiredArticle(id);
        articleRepository.deleteById(id);
        log.info("記事を削除しました。id={}", id);
        return true;
    }

    private Article getRequiredArticle(Long id) {
        if (id == null) {
            throw new BizException(ErrorCode.BAD_REQUEST);
        }
        return articleRepository.findById(id)
                .orElseThrow(() -> BizException.withDetail(ErrorCode.NOT_FOUND, "記事が存在しません。id=" + id));
    }
}
