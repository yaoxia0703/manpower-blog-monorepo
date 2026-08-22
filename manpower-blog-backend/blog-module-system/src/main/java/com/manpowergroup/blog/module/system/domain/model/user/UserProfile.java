package com.manpowergroup.blog.module.system.domain.model.user;

import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;

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
