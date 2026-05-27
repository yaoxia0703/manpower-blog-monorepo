package com.manpowergroup.springboot.springboot3web.system.application.assembler;

import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuDetailVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuOptionVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuTreeVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;

/**
 * Menu のリクエストDTO ⇔ Entity 変換ユーティリティ
 * 目的：
 * - Service から normalize / default 値埋め を分離して読みやすくする
 * - DTO は「入力 + 検証」に専念し、変換はここで一元化する
 */
public final class MenuAssembler {

    private MenuAssembler() {
    }

    /**
     * 新規作成用：Request -> Entity
     */
    public static Menu toCreateEntity(MenuCreateRequest req) {
        return Menu.builder()
                .parentId(req.parentId())
                .name(req.name())
                .path(normalize(req.path()))
                .component(normalize(req.component()))
                .icon(req.icon())
                .type(req.type())
                .sort(defaultSort(req.sort()))
                .status(req.status())
                .build();
    }

    /**
     * 更新用：Request -> 既存Entityへ反映
     * ※ parentId / type は変更不可
     */
    public static void toUpdateEntity(MenuUpdateRequest req, Menu existing) {
        existing.setName(req.name());
        existing.setPath(normalize(req.path()));
        existing.setComponent(normalize(req.component()));
        existing.setIcon(req.icon());
        existing.setSort(defaultSort(req.sort()));
        existing.setStatus(req.status());
    }

    /**
     * 表示順：デフォルト値埋め
     */
    private static Integer defaultSort(Integer sort) {
        return sort != null ? sort : 999;
    }

    /**
     * Entity -> TreeVo
     * menu.path is used by the frontend for navigation and breadcrumbs.
     */
    public static MenuTreeVo toTreeVo(Menu menu) {
        MenuTreeVo vo = new MenuTreeVo();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setName(menu.getName());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setIcon(menu.getIcon());
        vo.setType(menu.getType());
        vo.setSort(menu.getSort());
        vo.setStatus(menu.getStatus());
        vo.setCreatedAt(menu.getCreatedAt());
        vo.setUpdatedAt(menu.getUpdatedAt());
        return vo;
    }

    /**
     * Entity -> DetailVo
     * menu and permission are intentionally decoupled.
     */
    public static MenuDetailVo toDetailVo(Menu menu) {
        return new MenuDetailVo(
                menu.getId(),
                menu.getParentId(),
                menu.getName(),
                menu.getPath(),
                menu.getComponent(),
                menu.getType(),
                menu.getSort(),
                menu.getIcon(),
                menu.getStatus()
        );
    }

    public static MenuOptionVo toOptionVo(Menu menu) {
        return new MenuOptionVo(
                menu.getId(),
                menu.getName()
        );
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
