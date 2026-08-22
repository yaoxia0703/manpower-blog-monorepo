package com.manpowergroup.blog.module.system.domain.model.permission;

import java.util.List;

/** 権限検索のページ結果。 */
public record PermissionSearchPage(
        List<Permission> records,
        long total,
        long pageNum,
        long pageSize
) {
}
