package com.manpowergroup.springboot.springboot3web.content.domain.model;

/** 記事検索条件。 */
public record ArticleSearchCriteria(
        String title,
        ArticleStatus status,
        Long categoryId
) {
}
