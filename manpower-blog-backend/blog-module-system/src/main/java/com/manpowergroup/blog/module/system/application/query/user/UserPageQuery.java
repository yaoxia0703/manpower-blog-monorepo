package com.manpowergroup.blog.module.system.application.query.user;

import com.manpowergroup.blog.shared.enums.Status;

/** ユーザー一覧検索クエリ。 */
public record UserPageQuery(
        // ページ番号
        Long pageNum,
        // 1ページあたりの件数
        Long pageSize,
        // 部分一致検索キーワード
        String keyword,
        // ユーザー状態
        Status status
) {
}
