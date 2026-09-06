package com.manpowergroup.blog.module.content.application;

import com.manpowergroup.blog.module.content.application.query.ArticlePageQuery;
import com.manpowergroup.blog.module.content.application.service.impl.PublishedArticleQueryServiceImpl;
import com.manpowergroup.blog.module.content.domain.model.ArticleSearchCriteria;
import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;
import com.manpowergroup.blog.module.content.domain.model.ArticleView;
import com.manpowergroup.blog.module.content.domain.repository.ArticleRepository;
import com.manpowergroup.blog.shared.config.PageProperties;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PublishedArticleQueryServiceImplTest {

    private final ArticleRepository repository = mock(ArticleRepository.class);
    private final PageProperties pageProperties = new PageProperties();
    private final PublishedArticleQueryServiceImpl service =
            new PublishedArticleQueryServiceImpl(repository, pageProperties);

    @Test
    void pageAlwaysUsesPublishedStatus() {
        when(repository.list(any(), anyLong(), anyLong())).thenReturn(List.of());
        when(repository.count(any())).thenReturn(0L);

        service.page(new ArticlePageQuery(1L, 20L, "Spring", ArticleStatus.DRAFT, 1L));

        verify(repository).list(
                eq(new ArticleSearchCriteria("Spring", ArticleStatus.PUBLISHED, 1L)),
                eq(0L),
                eq(20L));
        verify(repository).count(
                eq(new ArticleSearchCriteria("Spring", ArticleStatus.PUBLISHED, 1L)));
    }

    /**
     * 上限を超えるページサイズは設定値へ丸められる。
     *
     * <p>従来は上限がコードへ直接書かれており、設定値を変更しても
     * 反映されなかった。設定が実際に効いていることを固定する。</p>
     */
    @Test
    void pageSizeIsClampedToConfiguredMaximum() {
        pageProperties.setMaxPageSize(50);
        when(repository.list(any(), anyLong(), anyLong())).thenReturn(List.of());
        when(repository.count(any())).thenReturn(0L);

        service.page(new ArticlePageQuery(3L, 500L, null, ArticleStatus.PUBLISHED, null));

        // 3ページ目・1ページ50件のため offset は 100 となる
        verify(repository).list(any(), eq(100L), eq(50L));
    }

    @Test
    void detailRequiresPublishedStatus() {
        final ArticleView view = new ArticleView(
                10L, "title", "summary", "content", 1L, "技術",
                1L, "管理者", ArticleStatus.PUBLISHED,
                LocalDateTime.now(), LocalDateTime.now());
        when(repository.findViewById(10L, ArticleStatus.PUBLISHED)).thenReturn(Optional.of(view));

        assertThat(service.findById(10L).id()).isEqualTo(10L);
        verify(repository).findViewById(10L, ArticleStatus.PUBLISHED);
    }
}
