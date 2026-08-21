package com.manpowergroup.springboot.springboot3web.system.application.query.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

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
