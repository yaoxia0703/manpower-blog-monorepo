package com.manpowergroup.blog.module.system.application.command.role;

import com.manpowergroup.blog.shared.enums.Status;

/** ロール更新コマンド。 */
public record RoleUpdateCommand(
        // ロールID
        Long id,
        // ロールコード
        String code,
        // ロール名
        String name,
        // 表示順
        Integer sort,
        // 状態
        Status status
) {
}
