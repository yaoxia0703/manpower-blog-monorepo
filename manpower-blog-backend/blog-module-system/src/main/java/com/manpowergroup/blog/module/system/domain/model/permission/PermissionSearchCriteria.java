package com.manpowergroup.blog.module.system.domain.model.permission;

import com.manpowergroup.blog.shared.enums.HttpMethod;
import com.manpowergroup.blog.shared.enums.Status;

/** 権限検索条件。 */
public record PermissionSearchCriteria(
        String keyword,
        Long menuId,
        HttpMethod method,
        Status status
) {
}
