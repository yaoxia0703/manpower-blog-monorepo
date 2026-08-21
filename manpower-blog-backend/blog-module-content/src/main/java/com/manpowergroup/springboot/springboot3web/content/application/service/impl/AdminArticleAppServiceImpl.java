package com.manpowergroup.springboot.springboot3web.content.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.content.application.assembler.ArticleAssembler;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleCreateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.content.application.command.ArticleUpdateCommand;
import com.manpowergroup.springboot.springboot3web.content.application.dto.response.ArticleResponse;
import com.manpowergroup.springboot.springboot3web.content.application.query.ArticlePageQuery;
import com.manpowergroup.springboot.springboot3web.content.application.service.AdminArticleAppService;
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
public class AdminArticleAppServiceImpl implements AdminArticleAppService {

    private final ArticleRepository articleRepository;

    @Override
    @Transactional(readOnly = true)
    public JoinPageResult<ArticleResponse> page(ArticlePageQuery query) {
        return pageByCriteria(query);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponse findById(Long id) {
        requireId(id);
        return articleRepository.findViewById(id, null)
                .map(ArticleAssembler::toResponse)
                .orElseThrow(() -> notFound(id));
    }

    @Override
    @Transactional
    public Long create(ArticleCreateCommand command) {
        final Article article = Article.create(
                command.title(), command.summary(), command.content(), command.categoryId(),
                command.authorId(), command.status()
        );
        articleRepository.create(article);
        log.info("記事を作成しました。id={}", article.getId());
        return article.getId();
    }

    @Override
    @Transactional
    public void update(ArticleUpdateCommand command) {
        final Article article = getRequiredArticle(command.id());
        article.update(
                command.title(), command.summary(), command.content(), command.categoryId(), command.status());
        articleRepository.update(article);
        log.info("記事を更新しました。id={}", article.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getRequiredArticle(id);
        articleRepository.delete(id);
        log.info("記事を削除しました。id={}", id);
    }

    @Override
    @Transactional
    public void changeStatus(ArticleStatusChangeCommand command) {
        final Article article = getRequiredArticle(command.id());
        article.changeStatus(command.status());
        articleRepository.update(article);
        log.info("記事状態を変更しました。id={}, status={}", article.getId(), article.getStatus());
    }

    private JoinPageResult<ArticleResponse> pageByCriteria(ArticlePageQuery query) {
        final long pageNum = safePageNum(query.pageNum());
        final long pageSize = safePageSize(query.pageSize());
        final long offset = (pageNum - 1) * pageSize;
        final ArticleSearchCriteria criteria = new ArticleSearchCriteria(
                query.title(), query.status(), query.categoryId());

        return JoinPageResult.of(
                articleRepository.list(criteria, offset, pageSize).stream()
                        .map(ArticleAssembler::toResponse)
                        .toList(),
                articleRepository.count(criteria),
                pageNum,
                pageSize
        );
    }

    private Article getRequiredArticle(Long id) {
        requireId(id);
        return articleRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private static void requireId(Long id) {
        if (id == null || id < 1) {
            throw new BizException(ErrorCode.BAD_REQUEST);
        }
    }

    private static BizException notFound(Long id) {
        return BizException.withDetail(ErrorCode.NOT_FOUND, "記事が存在しません。id=" + id);
    }
}
