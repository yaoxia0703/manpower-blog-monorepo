package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.RoleAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.command.role.RoleCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.role.RoleStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.role.RoleUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.role.RoleResponse;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAppService;
import com.manpowergroup.springboot.springboot3web.system.domain.model.role.Role;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleMenuRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RolePermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleAppServiceImpl implements RoleAppService {

    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public List<RoleResponse> getRoleList() {
        return roleRepository.findAll().stream().map(RoleAssembler::toResponse).toList();
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        return RoleAssembler.toResponse(getRequiredRole(id));
    }

    @Override
    @Transactional
    public Long createRole(RoleCreateCommand command) {
        final Role role = Role.create(command.code(), command.name(), command.sort(), command.status());
        ensureUniqueCode(role);
        roleRepository.save(role);
        log.info("ロールを作成しました。id={}, code={}", role.getId(), role.getCode());
        return role.getId();
    }

    @Override
    @Transactional
    public void updateRole(RoleUpdateCommand command) {
        final Role role = getRequiredRole(command.id());
        role.changeDetails(command.code(), command.name(), command.sort(), command.status());
        ensureUniqueCode(role);
        roleRepository.update(role);
        log.info("ロールを更新しました。id={}", role.getId());
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        final Role role = getRequiredRole(id);
        if (userRoleRepository.existsByRoleId(id)) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ユーザーに割り当てられているロールは削除できません");
        }
        roleMenuRepository.replaceMenus(id, List.of());
        rolePermissionRepository.replacePermissions(id, List.of());
        roleRepository.deleteById(id);
        log.info("ロールを削除しました。id={}, code={}", role.getId(), role.getCode());
    }

    @Override
    @Transactional
    public void changeStatus(RoleStatusChangeCommand command) {
        final Role role = getRequiredRole(command.id());
        role.changeStatus(command.status());
        roleRepository.update(role);
        log.info("ロール状態を変更しました。id={}, status={}", role.getId(), role.getStatus());
    }

    @Override
    public boolean allExist(Collection<Long> ids) {
        return ids != null && roleRepository.findByIds(ids).size() == ids.size();
    }

    private Role getRequiredRole(Long id) {
        if (id == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ロールIDが指定されていません");
        }
        return roleRepository.findById(id)
                .orElseThrow(() -> BizException.withDetail(ErrorCode.NOT_FOUND, "ロールが存在しません。id=" + id));
    }

    private void ensureUniqueCode(Role role) {
        if (roleRepository.existsByCode(role.getCode(), role.getId())) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ロールコードは既に存在しています");
        }
    }
}
