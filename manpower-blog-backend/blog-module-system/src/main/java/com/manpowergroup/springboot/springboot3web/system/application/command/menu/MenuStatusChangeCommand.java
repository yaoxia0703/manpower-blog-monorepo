package com.manpowergroup.springboot.springboot3web.system.application.command.menu;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

/**
 * メニュー状態変更コマンド。
 *
 * @param id メニューID
 * @param status 変更後の状態
 */
public record MenuStatusChangeCommand(
        // メニューID
        Long id,
        // 変更後の状態
        Status status
) {
}
