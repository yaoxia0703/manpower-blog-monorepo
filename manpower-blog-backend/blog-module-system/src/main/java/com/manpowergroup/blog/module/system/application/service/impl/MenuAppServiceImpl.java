package com.manpowergroup.blog.module.system.application.service.impl;

import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.enums.MenuType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.module.system.application.assembler.MenuAssembler;
import com.manpowergroup.blog.module.system.application.command.menu.MenuCreateCommand;
import com.manpowergroup.blog.module.system.application.command.menu.MenuStatusChangeCommand;
import com.manpowergroup.blog.module.system.application.command.menu.MenuUpdateCommand;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuDetailResponse;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuOptionResponse;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuTreeResponse;
import com.manpowergroup.blog.module.system.application.service.MenuAppService;
import com.manpowergroup.blog.module.system.domain.model.menu.Menu;
import com.manpowergroup.blog.module.system.domain.repository.MenuRepository;
import com.manpowergroup.blog.module.system.domain.repository.PermissionRepository;
import com.manpowergroup.blog.module.system.domain.repository.RoleMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * メニューユースケースの実装。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuAppServiceImpl implements MenuAppService {

    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<MenuTreeResponse> listTree() {
        return MenuAssembler.toTreeResponse(menuRepository.list(), 0L);
    }

    @Override
    public List<MenuTreeResponse> listTreeByUserId(Long userId) {
        if (userId == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ユーザーIDが指定されていません");
        }
        return MenuAssembler.toTreeResponse(menuRepository.listByUserId(userId), 0L);
    }

    @Override
    public List<MenuOptionResponse> listOptions() {
        return menuRepository.listEnabledDirectories().stream()
                .map(MenuAssembler::toOptionResponse)
                .toList();
    }

    @Override
    public MenuDetailResponse findById(Long id) {
        return MenuAssembler.toDetailResponse(getRequiredMenu(id));
    }

    @Override
    @Transactional
    public Long create(MenuCreateCommand command) {
        validateParent(command.parentId());

        final Menu menu = Menu.create(
                command.parentId(), command.name(), command.path(), command.component(),
                command.type(), command.sort(), command.icon(), command.status()
        );
        validateUnique(menu, null);
        menuRepository.create(menu);
        log.info("メニューを作成しました。id={}", menu.getId());
        return menu.getId();
    }

    @Override
    @Transactional
    public void update(MenuUpdateCommand command) {
        final Menu menu = getRequiredMenu(command.id());
        final Status previousStatus = menu.getStatus();
        menu.updateDetails(
                command.name(), command.path(), command.component(), command.sort(),
                command.icon(), command.status()
        );
        validateUnique(menu, menu.getId());
        menuRepository.update(menu);

        if (previousStatus != Status.DISABLED && menu.getStatus() == Status.DISABLED) {
            disableDescendants(menu.getId());
        }
        log.info("メニューを更新しました。id={}", menu.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        final Menu menu = getRequiredMenu(id);
        menu.validateDeletable(
                menuRepository.countByParentId(id) > 0,
                roleMenuRepository.existsByMenuId(id),
                permissionRepository.existsByMenuId(id)
        );
        menuRepository.delete(id);
        log.info("メニューを削除しました。id={}", id);
    }

    @Override
    @Transactional
    public void changeStatus(MenuStatusChangeCommand command) {
        final Menu menu = getRequiredMenu(command.id());
        if (menu.getStatus() == command.status()) {
            return;
        }
        menu.changeStatus(command.status());
        menuRepository.update(menu);
        if (command.status() == Status.DISABLED) {
            disableDescendants(menu.getId());
        }
        log.info("メニュー状態を変更しました。id={}, status={}", menu.getId(), command.status());
    }

    @Override
    public List<MenuTreeResponse> listEnabledTree() {
        return MenuAssembler.toTreeResponse(menuRepository.listEnabled(), 0L);
    }

    @Override
    public boolean allExist(Collection<Long> ids) {
        return ids != null && menuRepository.listByIds(ids).size() == ids.size();
    }

    private Menu getRequiredMenu(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> BizException.withDetail(ErrorCode.NOT_FOUND, "メニューが存在しません。id=" + id));
    }

    private void validateParent(Long parentId) {
        if (parentId == null || parentId == 0) {
            return;
        }
        final Menu parent = menuRepository.findById(parentId)
                .orElseThrow(() -> BizException.withDetail(ErrorCode.BAD_REQUEST, "親メニューが存在しません"));
        if (parent.getType() != MenuType.DIRECTORY) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ディレクトリのみ親メニューに指定できます");
        }
    }

    private void validateUnique(Menu menu, Long excludeId) {
        menu.validateDuplicateName(menuRepository.existsByParentIdAndName(
                menu.getParentId(), menu.getName(), excludeId));
        if (menuRepository.existsByPath(menu.getPath(), excludeId)) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "メニューパスは既に存在しています");
        }
    }

    private void disableDescendants(Long menuId) {
        final List<Long> descendantIds = menuRepository.listDescendantIds(menuId);
        menuRepository.changeStatusBatch(descendantIds, Status.DISABLED);
    }
}
