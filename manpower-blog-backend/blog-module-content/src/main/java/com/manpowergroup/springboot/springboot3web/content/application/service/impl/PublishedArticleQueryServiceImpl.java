package com.manpowergroup.springboot.springboot3web.content.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.content.application.assembler.ArticleAssembler;
import com.manpowergroup.springboot.springboot3web.content.application.dto.response.ArticleResponse;
import com.manpowergroup.springboot.springboot3web.content.application.query.ArticlePageQuery;
import com.manpowergroup.springboot.springboot3web.content.application.service.PublishedArticleQueryService;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleStatus;
import com.manpowergroup.springboot.springboot3web.content.domain.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.manpowergroup.springboot.springboot3web.blog.common.util.ServiceHelper.safePageNum;
import static com.manpowergroup.springboot.springboot3web.blog.common.util.ServiceHelper.safePageSize;

@Service
@RequiredArgsConstructor
public class PublishedArticleQueryServiceImpl implements PublishedArticleQueryService {

    private final ArticleRepository articleRepository;

    @Override
    @Transactional(readOnly = true)
    public JoinPageResult<ArticleResponse> page(ArticlePageQuery query) {
        final long pageNum = safePageNum(query.pageNum());
        final long pageSize = safePageSize(query.pageSize());
        final long offset = (pageNum - 1) * pageSize;
        final ArticleSearchCriteria criteria = new ArticleSearchCriteria(
                query.title(), ArticleStatus.PUBLISHED, query.categoryId());

        return JoinPageResult.of(
                articleRepository.list(criteria, offset, pageSize).stream()
                        .map(ArticleAssembler::toResponse)
                        .toList(),
                articleRepository.count(criteria),
                pageNum,
                pageSize
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
