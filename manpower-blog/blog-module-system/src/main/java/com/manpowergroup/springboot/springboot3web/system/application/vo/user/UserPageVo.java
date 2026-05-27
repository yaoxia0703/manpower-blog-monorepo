package com.manpowergroup.springboot.springboot3web.system.application.vo.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.VerifiedStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "ユーザーページVO")
public record UserPageVo(
        @Schema(description = "ID")
        Long userId,
        @Schema(description = "accountId")
        Long accountId,
        @Schema(description = "ニックネーム")
        String nickName,
        @Schema(description = "ユーザーステータス")
        Status userStatus,
        @Schema(description = "アカウント種別")
        String accountType,
        @Schema(description = "アカウントログインID")
        String accountValue,
        @Schema(description = "アカウントステータス")
        Status accountStatus,
        @Schema(description = "アカウント認証済みフラグ")
        VerifiedStatus verifiedStatus,
        @Schema(description = "ユーザー作成日時")
        LocalDateTime createdAt,
        @Schema(description = "ロールID")
        Long roleId,
        @Schema(description = "ロール名1")
        String roleName

) {


}
