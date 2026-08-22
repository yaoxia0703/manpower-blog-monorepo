package com.manpowergroup.blog.module.system.application.dto.response.user;

import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "ユーザー情報")
public record UserResponse(
        @Schema(description = "ユーザーID") Long userId,
        @Schema(description = "アカウントID") Long accountId,
        @Schema(description = "ニックネーム") String nickName,
        @Schema(description = "ユーザーステータス") Status userStatus,
        @Schema(description = "アカウント種別") String accountType,
        @Schema(description = "アカウントログインID") String accountValue,
        @Schema(description = "アカウントステータス") Status accountStatus,
        @Schema(description = "アカウント認証状態") VerifiedStatus verifiedStatus,
        @Schema(description = "ユーザー作成日時") LocalDateTime createdAt,
        @Schema(description = "ロールID") Long roleId,
        @Schema(description = "ロール名") String roleName
) {
}
