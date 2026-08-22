package com.manpowergroup.blog.module.system.application.assembler;

import com.manpowergroup.blog.module.system.application.command.menu.MenuCreateCommand;
import com.manpowergroup.blog.module.system.application.command.menu.MenuStatusChangeCommand;
import com.manpowergroup.blog.module.system.application.command.menu.MenuUpdateCommand;
import com.manpowergroup.blog.module.system.application.dto.request.menu.MenuCreateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.menu.MenuStatusUpdateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.menu.MenuUpdateRequest;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuDetailResponse;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuOptionResponse;
import com.manpowergroup.blog.module.system.application.dto.response.menu.MenuTreeResponse;
import com.manpowergroup.blog.module.system.domain.model.menu.Menu;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * メニューの入出力変換を一元管理する。
 */
public final class MenuAssembler {

    private MenuAssembler() {
    }

    /** 作成リクエストをコマンドへ変換する。 */
    public static MenuCreateCommand toCommand(MenuCreateRequest request) {
        return new MenuCreateCommand(
                request.parentId(), request.name(), request.path(), request.component(),
                request.type(), request.sort(), request.icon(), request.status()
        );
    }

    /** 更新リクエストをコマンドへ変換する。 */
    public static MenuUpdateCommand toCommand(Long id, MenuUpdateRequest request) {
        return new MenuUpdateCommand(
                id, request.name(), request.path(), request.component(),
                request.sort(), request.icon(), request.status()
        );
    }

    /** 状態変更リクエストをコマンドへ変換する。 */
    public static MenuStatusChangeCommand toCommand(Long id, MenuStatusUpdateRequest request) {
        return new MenuStatusChangeCommand(id, request.status());
    }

    /** メニューを詳細レスポンスへ変換する。 */
    public static MenuDetailResponse toDetailResponse(Menu menu) {
        return new MenuDetailResponse(
                menu.getId(), menu.getParentId(), menu.getName(), menu.getPath(),
                menu.getComponent(), menu.getType(), menu.getSort(), menu.getIcon(), menu.getStatus()
        );
    }

    /** メニューを選択肢レスポンスへ変換する。 */
    public static MenuOptionResponse toOptionResponse(Menu menu) {
        return new MenuOptionResponse(menu.getId(), menu.getName());
    }

    /** フラットなメニュー一覧をルートID配下のツリーへ変換する。 */
    public static List<MenuTreeResponse> toTreeResponse(List<Menu> menus, Long rootParentId) {
        final Map<Long, List<Menu>> childrenByParent = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));
        return buildChildren(rootParentId, childrenByParent, new HashSet<>());
    }

    private static List<MenuTreeResponse> buildChildren(
            Long parentId,
            Map<Long, List<Menu>> childrenByParent,
            Set<Long> ancestors
    ) {
        return childrenByParent.getOrDefault(parentId, List.of()).stream()
                .map(menu -> {
                    if (!ancestors.add(menu.getId())) {
                        throw new IllegalStateException("メニュー階層に循環があります。id=" + menu.getId());
                    }
                    final List<MenuTreeResponse> children = buildChildren(
                            menu.getId(), childrenByParent, new HashSet<>(ancestors));
                    return new MenuTreeResponse(
                            menu.getId(), menu.getParentId(), menu.getName(), menu.getPath(),
                            menu.getComponent(), menu.getIcon(), menu.getType(), menu.getSort(),
                            menu.getStatus(), menu.getCreatedAt(), menu.getUpdatedAt(), children
                    );
                })
                .toList();
    }
}
