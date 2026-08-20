package com.manpowergroup.springboot.springboot3web.system.application.command.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

/**
 * 権限作成コマンド。
 *
 * @param menuId 所属メニューID
 * @param name 権限名
 * @param code 権限制御コード
 * @param path APIパス
 * @param method HTTPメソッド
 * @param sort 表示順
 * @param status 状態
 */
public record PermissionCreateCommand(
        // 所属メニューID
        Long menuId,
        // 権限名
        String name,
        // 権限制御コード
        String code,
        // APIパス
        String path,
        // HTTPメソッド
        HttpMethod method,
        // 表示順
        Integer sort,
        // 状態
        Status status
) {
}
