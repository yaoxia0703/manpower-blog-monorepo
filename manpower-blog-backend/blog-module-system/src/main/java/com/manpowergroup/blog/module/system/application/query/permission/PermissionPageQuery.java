package com.manpowergroup.blog.module.system.application.query.permission;

import com.manpowergroup.blog.shared.enums.HttpMethod;
import com.manpowergroup.blog.shared.enums.Status;

/** 権限一覧のページ検索クエリ。 */
public record PermissionPageQuery(
        Long pageNum,
        Long pageSize,
        String keyword,
        Long menuId,
        HttpMethod method,
        Status status
) {
}
