package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.system.application.command.role.RoleAuthorizationSaveCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.role.RoleAuthorizationResponse;

/** ロール認可設定のユースケースを提供する。 */
public interface RoleAuthorizationAppService {

    RoleAuthorizationResponse getAuthorization(Long roleId);

    void saveAuthorization(RoleAuthorizationSaveCommand command);
}
