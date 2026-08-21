package com.manpowergroup.springboot.springboot3web.system.domain.model.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

/** 権限検索条件。 */
public record PermissionSearchCriteria(
        String keyword,
        Long menuId,
        HttpMethod method,
        Status status
) {
}
