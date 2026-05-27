package com.manpowergroup.springboot.springboot3web.system.application.assembler;

import com.manpowergroup.springboot.springboot3web.blog.common.util.StringUtils;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionDetailVo;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionOptionVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionTreeVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;


/**
 * Permission のリクエストDTO ⇔ Entity 変換ユーティリティ
 * 目的：
 * - Service から normalize / default 値埋め を分離して読みやすくする
 * - DTO は「入力 + 検証」に専念し、変換はここで一元化する
 */
public final class PermissionAssembler {

    private PermissionAssembler() {
    }

    /**
     * 新規作成用：Request -> Entity
     */
    public static Permission toCreateEntity(PermissionCreateRequest req) {
        return Permission.builder()
                .parentId(req.parentId())
                .name(normalizeName(req.name()))
                .code(normalizeCode(req.code()))
                .type(req.type())
                .path(normalizePath(req.path()))
                .method(req.method())
                .sort(defaultSort(req.sort()))
                .status(req.status())
                .build();
    }

    /**
     * 更新用：Request -> 既存Entityへ反映
     */
    public static void toUpdateEntity(PermissionUpdateRequest req, Permission existing) {
        existing.setParentId(req.parentId());
        existing.setName(normalizeName(req.name()));
        existing.setType(req.type());
        existing.setPath(normalizePath(req.path()));
        existing.setMethod(req.method());
        existing.setSort(defaultSort(req.sort()));
        existing.setStatus(req.status());
    }

    /**
     * 権限名：トリム・全角半角など normalize
     */
    private static String normalizeName(String name) {
        return StringUtils.normalize(name);
    }

    /**
     * 権限制御コード：基本は normalize のみ
     * - role.code は大文字統一でも良いが、permission.code は「user:add」など小文字文化が多いので、
     * まずは大小変換せずに統一（必要ならここで方針を変える）
     */
    private static String normalizeCode(String code) {
        // 必要なら：.toLowerCase(Locale.ROOT) にする
        return StringUtils.normalize(code);
    }

    /**
     * path：空文字は null 扱いにして保存（DB をきれいにする）
     */
    private static String normalizePath(String path) {
        final var p = StringUtils.normalize(path);
        return (p == null || p.isBlank()) ? null : p;
    }

    /**
     * sort：null は 0 扱い（要件に合わせて変更可）
     */
    private static Integer defaultSort(Integer sort) {
        return sort == null ? 0 : sort;
    }


    public static PermissionTreeVo toTreeVo(Permission permission) {
        return PermissionTreeVo.builder()
                .id(permission.getId())
                .parentId(permission.getParentId())
                .name(permission.getName())
                .code(permission.getCode())
                .type(permission.getType())
                .path(permission.getPath())
                .method(permission.getMethod())
                .sort(permission.getSort())
                .status(permission.getStatus())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }

    public static PermissionDetailVo toDetailVo(Permission permission) {
        return new PermissionDetailVo(
                permission.getId(),
                permission.getParentId(),
                permission.getName(),
                permission.getCode(),
                permission.getType(),
                permission.getPath(),
                permission.getMethod(),
                permission.getSort(),
                permission.getStatus(),
                permission.getCreatedAt(),
                permission.getUpdatedAt()
        );
    }

    public static PermissionOptionVo toOptionVo(Permission permission) {
        return new PermissionOptionVo(
                permission.getId(),
                permission.getName()
        );
    }
}