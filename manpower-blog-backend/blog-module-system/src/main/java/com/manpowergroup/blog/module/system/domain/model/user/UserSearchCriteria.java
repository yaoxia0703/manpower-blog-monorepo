package com.manpowergroup.blog.module.system.domain.model.user;

import com.manpowergroup.blog.shared.enums.Status;

/** ユーザー検索条件。 */
public record UserSearchCriteria(String keyword, Status status) {
}
