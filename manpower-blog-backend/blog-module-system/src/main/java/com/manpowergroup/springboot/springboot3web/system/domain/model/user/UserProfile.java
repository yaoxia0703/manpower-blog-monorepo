package com.manpowergroup.springboot.springboot3web.system.domain.model.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.VerifiedStatus;

import java.time.LocalDateTime;

/** ユーザー一覧・詳細表示用の読み取りモデル。 */
public record UserProfile(
        Long userId,
        Long accountId,
        String nickName,
        Status userStatus,
        String accountType,
        String accountValue,
        Status accountStatus,
        VerifiedStatus verifiedStatus,
        LocalDateTime createdAt,
        Long roleId,
        String roleName
) {
}
