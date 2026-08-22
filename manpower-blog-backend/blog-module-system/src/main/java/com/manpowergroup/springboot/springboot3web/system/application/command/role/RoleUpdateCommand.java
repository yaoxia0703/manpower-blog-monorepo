package com.manpowergroup.springboot.springboot3web.system.application.command.role;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

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
