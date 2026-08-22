package com.manpowergroup.springboot.springboot3web.system.application.query.user;

/** ユーザー詳細検索クエリ。 */
public record UserDetailQuery(
        // ユーザーID
        Long userId,
        // アカウントID
        Long accountId
) {
}
