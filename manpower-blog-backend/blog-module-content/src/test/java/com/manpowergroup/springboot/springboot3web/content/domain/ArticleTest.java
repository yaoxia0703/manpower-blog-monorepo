package com.manpowergroup.springboot.springboot3web.content.domain;

import com.manpowergroup.springboot.springboot3web.content.domain.model.Article;
import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArticleTest {

    @Test
    void createsAndNormalizesArticle() {
        final Article article = Article.create(
                " タイトル ", " 概要 ", " 本文 ", 1L, 10L, ArticleStatus.DRAFT);

        assertThat(article.getTitle()).isEqualTo("タイトル");
        assertThat(article.getSummary()).isEqualTo("概要");
        assertThat(article.getStatus()).isEqualTo(ArticleStatus.DRAFT);
    }

    @Test
    void controlsPublicationStatusByBehavior() {
        final Article article = Article.create(
                "タイトル", null, "本文", 1L, 10L, ArticleStatus.DRAFT);

        article.publish();
        assertThat(article.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);

        article.unpublish();
        assertThat(article.getStatus()).isEqualTo(ArticleStatus.UNPUBLISHED);
    }

    @Test
    void rejectsBlankRequiredFields() {
        assertThatThrownBy(() -> Article.create(
                " ", null, "本文", 1L, 10L, ArticleStatus.DRAFT))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
