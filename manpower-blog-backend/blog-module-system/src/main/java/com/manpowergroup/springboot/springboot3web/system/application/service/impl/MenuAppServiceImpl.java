package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.MenuAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.command.menu.MenuCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.menu.MenuStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.menu.MenuUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuDetailResponse;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuOptionResponse;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuTreeResponse;
import com.manpowergroup.springboot.springboot3web.system.application.service.MenuAppService;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.MenuRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.PermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleMenuRepository;
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
    public List<MenuTreeResponse> getAllMenuTree() {
        return MenuAssembler.toTreeResponse(menuRepository.findAll(), 0L);
    }

    @Override
    public List<MenuTreeResponse> selectMenusByUserId(Long userId) {
        if (userId == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ユーザーIDが指定されていません");
        }
        return MenuAssembler.toTreeResponse(menuRepository.findByUserId(userId), 0L);
    }

    @Override
    public List<MenuOptionResponse> getMenuOptions() {
        return menuRepository.findEnabledDirectories().stream()
                .map(MenuAssembler::toOptionResponse)
                .toList();
    }

    @Override
    public MenuDetailResponse getMenuDetail(Long id) {
        return MenuAssembler.toDetailResponse(getRequiredMenu(id));
    }

    @Override
    @Transactional
    public Long createMenu(MenuCreateCommand command) {
        validateParent(command.parentId());

        final Menu menu = Menu.create(
                command.parentId(), command.name(), command.path(), command.component(),
                command.type(), command.sort(), command.icon(), command.status()
        );
        validateUnique(menu, null);
        menuRepository.save(menu);
        log.info("メニューを作成しました。id={}", menu.getId());
        return menu.getId();
    }

    @Override
    @Transactional
    public void updateMenu(MenuUpdateCommand command) {
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
    public void deleteMenu(Long id) {
        final Menu menu = getRequiredMenu(id);
        menu.validateDeletable(
                menuRepository.countByParentId(id) > 0,
                roleMenuRepository.existsByMenuId(id),
                permissionRepository.existsByMenuId(id)
        );
        menuRepository.deleteById(id);
        log.info("メニューを削除しました。id={}", id);
    }

    @Override
    @Transactional
    public void changeMenuStatus(MenuStatusChangeCommand command) {
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
    public List<MenuTreeResponse> getActiveMenuTree() {
        return MenuAssembler.toTreeResponse(menuRepository.findEnabled(), 0L);
    }

    @Override
    public boolean allExist(Collection<Long> ids) {
        return ids != null && menuRepository.findByIds(ids).size() == ids.size();
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
        final List<Long> descendantIds = menuRepository.findAllDescendantIds(menuId);
        menuRepository.updateStatusBatch(descendantIds, Status.DISABLED);
    }
}
