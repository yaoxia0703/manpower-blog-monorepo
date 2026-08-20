package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.system.application.vo.role.RoleAuthorizationVo;

public interface RoleAuthorizationAppService {

    RoleAuthorizationVo getAuthorization(Long roleId);

    void saveAuthorization(Long roleId, Long[] menuIds, Long[] permissionIds);
}
