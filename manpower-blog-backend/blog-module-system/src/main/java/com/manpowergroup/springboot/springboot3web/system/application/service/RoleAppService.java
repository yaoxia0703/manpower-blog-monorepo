package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.system.application.command.role.RoleCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.role.RoleStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.role.RoleUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.role.RoleResponse;

import java.util.Collection;
import java.util.List;

/** ロールのユースケースを提供する。 */
public interface RoleAppService {

    List<RoleResponse> list();

    RoleResponse findById(Long id);

    Long create(RoleCreateCommand command);

    void update(RoleUpdateCommand command);

    void delete(Long id);

    void changeStatus(RoleStatusChangeCommand command);

    boolean allExist(Collection<Long> ids);
}
