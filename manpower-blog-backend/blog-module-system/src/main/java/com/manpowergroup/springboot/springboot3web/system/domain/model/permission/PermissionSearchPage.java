package com.manpowergroup.springboot.springboot3web.system.domain.model.permission;

import java.util.List;

/** 権限検索のページ結果。 */
public record PermissionSearchPage(
        List<Permission> records,
        long total,
        long pageNum,
        long pageSize
) {
}
