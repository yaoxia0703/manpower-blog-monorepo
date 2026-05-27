package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.manpowergroup.springboot.springboot3web.system.domain.model.role.RoleMenu;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleMenuRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.menu.MenuMapper;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.role.RoleMenuMapper;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAppMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleMenuAppServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleAppMenuService {

    private final RoleMenuRepository roleMenuRepository;
    // DIRECTORYを除外するためMenuMapperを注入
    private final MenuMapper menuMapper;

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        log.info("[RoleMenuAppService#getMenuIdsByRoleId] start: roleId={}", roleId);

        // ① 有効な menu_id を取得（is_deleted=0 のみ）
        final var menuIds = roleMenuRepository.selectAllByRoleIdIncludeDeleted(roleId)
                .stream()
                .filter(r -> Byte.valueOf((byte) 0).equals(r.getIsDeleted()))
                .map(RoleMenu::getMenuId)
                .toList();

        if (menuIds.isEmpty()) {
            log.info("[RoleMenuAppService#getMenuIdsByRoleId] no menus found: roleId={}", roleId);
            return List.of();
        }

        // ② type=MENU(2) のみ返す
        // DIRECTORY は el-tree が子ノード選択時に自動で半選にするため不要
        final var result = menuMapper.selectList(
                Wrappers.<Menu>lambdaQuery()
                        .in(Menu::getId, menuIds)
                        .eq(Menu::getType, MenuType.MENU)
        ).stream().map(Menu::getId).toList();

        log.info("[RoleMenuAppService#getMenuIdsByRoleId] success: roleId={}, count={}", roleId, result.size());
        return result;
    }

    @Override
    @Transactional
    public void saveOrUpdate(Long roleId, Long[] menuIds) {
        log.info("[RoleMenuAppService#saveOrUpdate] start: roleId={}, menuIds={}", roleId, Arrays.toString(menuIds));

        if (roleId == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ロールIDが指定されていません。");
        }
        if (menuIds == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "メニューIDリストが指定されていません。");
        }

        final Set<Long> targetMenuIds = Arrays.stream(menuIds)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        final LocalDateTime now = LocalDateTime.now();

        // 既存関連取得（論理削除含む）
        final List<RoleMenu> existingAll = roleMenuRepository.selectAllByRoleIdIncludeDeleted(roleId);

        // menuId → RoleMenu マッピング
        final Map<Long, RoleMenu> existingMap = existingAll.stream()
                .collect(Collectors.toMap(
                        RoleMenu::getMenuId,
                        Function.identity(),
                        (e1, e2) -> {
                            log.error("[RoleMenuAppService#saveOrUpdate] duplicated rows: roleId={}, menuId={}", roleId, e1.getMenuId());
                            return e1;
                        }
                ));

        // 復活対象 / 新規INSERT対象
        final Set<Long> toRestore = new HashSet<>();
        final List<RoleMenu> toInsert = new ArrayList<>();

        for (Long menuId : targetMenuIds) {
            final RoleMenu row = existingMap.get(menuId);
            if (row == null) {
                toInsert.add(RoleMenu.builder()
                        .roleId(roleId)
                        .menuId(menuId)
                        .isDeleted((byte) 0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build());
            } else {
                if (Byte.valueOf((byte) 1).equals(row.getIsDeleted())) {
                    toRestore.add(menuId);
                }
            }
        }

        // 論理削除対象
        final Set<Long> toDelete = existingAll.stream()
                .filter(r -> Byte.valueOf((byte) 0).equals(r.getIsDeleted()))
                .map(RoleMenu::getMenuId)
                .filter(menuId -> !targetMenuIds.contains(menuId))
                .collect(Collectors.toSet());

        log.info("[RoleMenuAppService#saveOrUpdate] roleId={}, restore={}, insert={}, delete={}",
                roleId, toRestore.size(), toInsert.size(), toDelete.size());

        if (!toRestore.isEmpty()) {
            roleMenuRepository.restoreMenus(roleId, toRestore, now);
            log.info("[RoleMenuAppService#saveOrUpdate] restore executed: count={}", toRestore.size());
        }
        if (!toInsert.isEmpty()) {
            this.saveBatch(toInsert);
            log.info("[RoleMenuAppService#saveOrUpdate] insert executed: count={}", toInsert.size());
        }
        if (!toDelete.isEmpty()) {
            roleMenuRepository.logicalDeleteMenus(roleId, toDelete, now);
            log.info("[RoleMenuAppService#saveOrUpdate] logical delete executed: count={}", toDelete.size());
        }

        log.info("[RoleMenuAppService#saveOrUpdate] completed: roleId={}", roleId);
    }
}