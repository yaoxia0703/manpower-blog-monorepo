package com.manpowergroup.blog.module.content.application.service.impl;

import com.manpowergroup.blog.shared.api.PageResult;
import com.manpowergroup.blog.shared.config.PageProperties;
import com.manpowergroup.blog.shared.dto.PageQuery;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.module.content.application.assembler.ArticleAssembler;
import com.manpowergroup.blog.module.content.application.dto.response.ArticleResponse;
import com.manpowergroup.blog.module.content.application.query.ArticlePageQuery;
import com.manpowergroup.blog.module.content.application.service.PublishedArticleQueryService;
import com.manpowergroup.blog.module.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;
import com.manpowergroup.blog.module.content.domain.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublishedArticleQueryServiceImpl implements PublishedArticleQueryService {

    private final ArticleRepository articleRepository;
    private final PageProperties pageProperties;

    @Override
    @Transactional(readOnly = true)
    public PageResult<ArticleResponse> page(ArticlePageQuery query) {
        final PageQuery page =
                PageQuery.clamped(query.pageNum(), query.pageSize(), pageProperties.toLimits());
        final ArticleSearchCriteria criteria = new ArticleSearchCriteria(
                query.title(), ArticleStatus.PUBLISHED, query.categoryId());

        return PageResult.of(
                articleRepository.list(criteria, page.offset(), page.limit()).stream()
                        .map(ArticleAssembler::toResponse)
                        .toList(),
                articleRepository.count(criteria),
                page.pageNum(),
                page.pageSize()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponse findById(Long id) {
        if (id == null || id < 1) {
            throw new BizException(ErrorCode.BAD_REQUEST);
        }
        return articleRepository.findViewById(id, ArticleStatus.PUBLISHED)
                .map(ArticleAssembler::toResponse)
                .orElseThrow(() -> BizException.withDetail(
                        ErrorCode.NOT_FOUND, "公開記事が存在しません。id=" + id));
    }
}
