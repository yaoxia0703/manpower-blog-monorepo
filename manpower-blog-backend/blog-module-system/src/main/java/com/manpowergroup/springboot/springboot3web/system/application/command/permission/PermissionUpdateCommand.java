package com.manpowergroup.springboot.springboot3web.system.application.command.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

/**
 * 権限更新コマンド。
 *
 * @param id 権限ID
 * @param menuId 所属メニューID
 * @param name 権限名
 * @param path APIパス
 * @param method HTTPメソッド
 * @param sort 表示順
 * @param status 状態
 */
public record PermissionUpdateCommand(
        // 権限ID
        Long id,
        // 所属メニューID
        Long menuId,
        // 権限名
        String name,
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
