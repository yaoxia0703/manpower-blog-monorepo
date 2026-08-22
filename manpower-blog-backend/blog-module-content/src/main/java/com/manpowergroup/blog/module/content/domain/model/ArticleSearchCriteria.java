package com.manpowergroup.blog.module.content.domain.model;

/** 記事検索条件。 */
public record ArticleSearchCriteria(
        String title,
        ArticleStatus status,
        Long categoryId
) {
}
