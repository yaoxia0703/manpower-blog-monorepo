package com.manpowergroup.blog.module.system.domain.model.user;

import java.util.List;

/** ユーザー検索のページ結果。 */
public record UserSearchPage(
        List<UserView> records,
        long total,
        long pageNum,
        long pageSize
) {
}
