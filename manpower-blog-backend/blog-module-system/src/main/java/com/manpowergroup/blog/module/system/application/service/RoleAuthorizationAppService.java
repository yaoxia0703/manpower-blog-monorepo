package com.manpowergroup.blog.module.system.application.service;

import com.manpowergroup.blog.module.system.application.command.role.RoleAuthorizationSaveCommand;
import com.manpowergroup.blog.module.system.application.dto.response.role.RoleAuthorizationResponse;

/** ロール認可設定のユースケースを提供する。 */
public interface RoleAuthorizationAppService {

    RoleAuthorizationResponse getAuthorization(Long roleId);

    void saveAuthorization(RoleAuthorizationSaveCommand command);
}
