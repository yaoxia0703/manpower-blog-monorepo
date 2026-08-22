package com.manpowergroup.springboot.springboot3web.content.application.query;

import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleStatus;

/** 記事一覧検索クエリ。 */
public record ArticlePageQuery(
        // ページ番号
        Long pageNum,
        // 1ページあたりの件数
        Long pageSize,
        // タイトル検索条件
        String title,
        // 記事状態
        ArticleStatus status,
        // カテゴリID
        Long categoryId
) {
}
