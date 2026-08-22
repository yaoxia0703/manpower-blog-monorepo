package com.manpowergroup.springboot.springboot3web.system.application.command.menu;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

/**
 * メニュー更新コマンド。
 *
 * @param id メニューID
 * @param name メニュー名
 * @param path フロントエンドルート
 * @param component コンポーネントキー
 * @param sort 表示順
 * @param icon アイコン
 * @param status 状態
 */
public record MenuUpdateCommand(
        // メニューID
        Long id,
        // メニュー名
        String name,
        // フロントエンドルート
        String path,
        // コンポーネントキー
        String component,
        // 表示順
        Integer sort,
        // アイコン
        String icon,
        // 状態
        Status status
) {
}
