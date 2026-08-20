package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.blog.common.util.TreeUtils;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.MenuAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuStatusUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuDetailVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuOptionVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuTreeVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;
import com.manpowergroup.springboot.springboot3web.system.domain.model.role.RoleMenu;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.MenuRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.menu.MenuMapper;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.permission.PermissionMapper;
import com.manpowergroup.springboot.springboot3web.system.application.service.MenuAppService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.role.RoleMenuMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * システムメニュー管理テーブル
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
@Service
@Slf4j
@AllArgsConstructor
public class MenuAppServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuAppService {

    private final MenuRepository menuRepository;
    private final RoleMenuMapper roleMenuMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public List<MenuTreeVo> getAllMenuTree() {
        final List<Menu> menus = baseMapper.selectList(
                Wrappers.<Menu>lambdaQuery()
                        .orderByAsc(Menu::getSort)
                        .orderByAsc(Menu::getId)
        );

        if (menus.isEmpty()) {
            return List.of();
        }

        final List<MenuTreeVo> voList = menus.stream()
                .map(MenuAssembler::toTreeVo)
                .toList();

        return TreeUtils.buildTree(voList, 0L);
    }

    @Override
    public List<MenuTreeVo> selectMenusByUserId(Long userId) {
        if (userId == null) {
            log.warn("[MenuAppService#selectMenusByUserId] userId is null");
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ユーザーIDが指定されていません");
        }

        final List<MenuTreeVo> list = menuRepository.selectMenusByUserId(userId);

        // 公共 TreeUtils に統一
        return TreeUtils.buildTree(list, 0L);
    }

    @Override
    public List<MenuOptionVo> getMenuOptions() {
        return baseMapper.selectList(
                        Wrappers.<Menu>lambdaQuery()
                                .eq(Menu::getStatus, Status.ENABLED)
                                .eq(Menu::getType, MenuType.DIRECTORY)
                                .orderByAsc(Menu::getSort)
                                .orderByAsc(Menu::getId)
                ).stream()
                .map(MenuAssembler::toOptionVo)
                .toList();
    }

    @Override
    public MenuDetailVo getMenuDetail(Long id) {
        final var menu = baseMapper.selectById(id);
        if (menu == null) {
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "メニューが存在しません。id=" + id);
        }
        return MenuAssembler.toDetailVo(menu);
    }

    @Override
    @Transactional
    public Long createMenu(MenuCreateRequest request) {
        log.info("[MenuAppService#createMenu] start: request={}", request);

        // 1. 親メニュー存在チェック
        if (request.parentId() != 0) {
            final var parent = baseMapper.selectById(request.parentId());
            if (parent == null) {
                log.warn("[MenuAppService#createMenu] parent not found parentId={}", request.parentId());
                throw BizException.withDetail(ErrorCode.BAD_REQUEST, "親メニューが存在しません");
            }
        }

        // 2. 同一階層の名称重複チェック
        final var nameExists = menuRepository.existsByParentIdAndName(
                request.parentId(),
                request.name()
        );

        // 3. Entity生成 → ドメインバリデーション
        final var entity = MenuAssembler.toCreateEntity(request);
        entity.validateDuplicateName(nameExists > 0);
        validatePath(entity.getPath(), null, entity.getType());

        // 4. 保存
        baseMapper.insert(entity);

        log.info("[MenuAppService#createMenu] success: id={}", entity.getId());

        return entity.getId();
    }

    @Override
    @Transactional
    public void updateMenu(Long id, MenuUpdateRequest request) {
        log.info("[MenuAppService#updateMenu] start: id={}, request={}", id, request);

        // 1. 既存チェック
        final var existing = baseMapper.selectById(id);
        if (existing == null) {
            log.warn("[MenuAppService#updateMenu] not found id={}", id);
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "メニューが存在しません");
        }

        // 2. 同一階層の名称重複チェック（自身を除く）
        final var nameCount = menuRepository.countByParentIdAndNameExcludeId(
                existing.getParentId(),
                request.name(),
                id
        );
        existing.validateDuplicateName(nameCount > 0);

        // 3. 更新前のステータスを退避 → Entity に反映
        final var oldStatus = existing.getStatus();
        MenuAssembler.toUpdateEntity(request, existing);
        validatePath(existing.getPath(), id, existing.getType());
        baseMapper.updateById(existing);

        // 4. ステータスが「無効」に変更された場合のみ → 全子孫を連動して無効化
        if (request.status() == Status.DISABLED && request.status() != oldStatus) {
            final var descendantIds = menuRepository.selectAllDescendantIds(id);
            if (!descendantIds.isEmpty()) {
                menuRepository.updateStatusBatch(descendantIds, Status.DISABLED);
                log.info("[MenuAppService#updateMenu] cascade disable: count={}, ids={}",
                        descendantIds.size(), descendantIds);
            }
        }

        log.info("[MenuAppService#updateMenu] success: id={}", id);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        // 存在チェック
        final var existing = baseMapper.selectById(id);
        if (existing == null) {
            log.warn("[MenuAppService#deleteMenu] menu not found id={}", id);
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "メニューが存在しません");
        }

        // 子ノード・使用中チェック
        final var childCount = menuRepository.countByParentId(id);
        final long usedCount = roleMenuMapper.selectCount(
                Wrappers.<RoleMenu>lambdaQuery().eq(RoleMenu::getMenuId, id)
        );
        existing.validateDeletable(childCount > 0, usedCount > 0);

        final long permissionCount = permissionMapper.selectCount(
                Wrappers.<Permission>lambdaQuery().eq(Permission::getMenuId, id)
        );
        if (permissionCount > 0) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "権限が紐づいているメニューは削除できません");
        }

        // 関連するロール-メニューの紐付け削除
        roleMenuMapper.delete(
                Wrappers.<RoleMenu>lambdaQuery().eq(RoleMenu::getMenuId, id)
        );
        log.info("[MenuAppService#deleteMenu] role-menu associations deleted: menuId={}", id);

        baseMapper.deleteById(id);
        log.info("[MenuAppService#deleteMenu] success: id={}", id);
    }

    @Override
    @Transactional
    public void changeMenuStatus(Long id, MenuStatusUpdateRequest request) {
        log.info("[MenuAppService#changeStatus] start: id={}, status={}", id, request.status());

        final var menu = baseMapper.selectById(id);
        if (menu == null) {
            log.warn("[MenuAppService#changeStatus] menu not found id={}", id);
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "メニューが存在しません");
        }

        // ステータスが変更なしの場合はスキップ
        final var oldStatus = menu.getStatus();
        if (oldStatus == request.status()) {
            log.info("[MenuAppService#changeStatus] no change, skip: id={}, status={}", id, oldStatus);
            return;
        }

        // 自身のステータスを更新
        menu.changeStatus(request.status());
        baseMapper.updateById(menu);

        // 無効化の場合のみ → 全子孫を連動して無効化
        if (request.status() == Status.DISABLED) {
            final var descendantIds = menuRepository.selectAllDescendantIds(id);
            if (!descendantIds.isEmpty()) {
                menuRepository.updateStatusBatch(descendantIds, Status.DISABLED);
                log.info("[MenuAppService#changeStatus] cascade disable: count={}, ids={}",
                        descendantIds.size(), descendantIds);
            }
        }

        log.info("[MenuAppService#changeStatus] success: id={}, {} → {}", id, oldStatus, request.status());
    }

    @Override
    public List<MenuTreeVo> getActiveMenuTree() {
        final List<Menu> menus = baseMapper.selectList(
                Wrappers.<Menu>lambdaQuery()
                        .eq(Menu::getIsDeleted, 0)
                        .eq(Menu::getStatus, Status.ENABLED)
                        .orderByAsc(Menu::getSort)
                        .orderByAsc(Menu::getId)
        );
        if (menus.isEmpty()) return List.of();
        return TreeUtils.buildTree(
                menus.stream().map(MenuAssembler::toTreeVo).toList(), 0L);
    }

    /**
     * 親子関係の循環チェック（子ノードを親に設定することを防止）
     */
    private void checkParentNotChild(Long id, Long parentId) {
        Long current = parentId;
        while (current != 0) {
            if (current.equals(id)) {
                throw BizException.withDetail(ErrorCode.BAD_REQUEST, "子ノードを親に設定できません");
            }
            final var menu = baseMapper.selectById(current);
            if (menu == null) {
                throw BizException.withDetail(ErrorCode.BAD_REQUEST, "メニュー構造が不正です");
            }
            current = menu.getParentId();
        }
    }

    private void validatePath(String path, Long excludeId, MenuType type) {
        if (type == MenuType.MENU && (path == null || path.isBlank())) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "MENU path is required");
        }
        if (path == null || path.isBlank()) {
            return;
        }

        final int count = excludeId == null
                ? menuRepository.countByPath(path)
                : menuRepository.countByPathExcludeId(path, excludeId);
        if (count > 0) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "menu path already exists");
        }
    }
}
