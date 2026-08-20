package com.manpowergroup.springboot.springboot3web.system.application.command.menu;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

/**
 * メニュー作成コマンド。
 *
 * @param parentId 親メニューID
 * @param name メニュー名
 * @param path フロントエンドルート
 * @param component コンポーネントキー
 * @param type メニュー種別
 * @param sort 表示順
 * @param icon アイコン
 * @param status 状態
 */
public record MenuCreateCommand(
        // 親メニューID
        Long parentId,
        // メニュー名
        String name,
        // フロントエンドルート
        String path,
        // コンポーネントキー
        String component,
        // メニュー種別
        MenuType type,
        // 表示順
        Integer sort,
        // アイコン
        String icon,
        // 状態
        Status status
) {
}
