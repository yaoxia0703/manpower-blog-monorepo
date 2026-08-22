package com.manpowergroup.springboot.springboot3web.system.domain.model.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

/** ユーザー検索条件。 */
public record UserSearchCriteria(String keyword, Status status) {
}
