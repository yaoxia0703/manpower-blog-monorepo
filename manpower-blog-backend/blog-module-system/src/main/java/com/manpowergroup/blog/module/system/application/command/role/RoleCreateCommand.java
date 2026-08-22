package com.manpowergroup.blog.module.system.application.command.role;

import com.manpowergroup.blog.shared.enums.Status;

/** ロール作成コマンド。 */
public record RoleCreateCommand(
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
