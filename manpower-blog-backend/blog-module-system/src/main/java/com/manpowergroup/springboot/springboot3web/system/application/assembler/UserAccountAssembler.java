package com.manpowergroup.springboot.springboot3web.system.application.assembler;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.UserErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.VerifiedStatus;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserChangeStatusRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.dto.auth.LoginAccountUserDTO;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserAccount;
import lombok.extern.slf4j.Slf4j;

/**
 * UserAccountAssemblerは、LoginAccountUserDTOからUserAccountエンティティへの変換を担当するクラスです。
 * ログイン処理において、データベースから取得したアカウント情報をUserAccountエンティティにマッピングするために使用されます。
 *
 * @author YAOXIA
 * @since 2025-12-18
 */
@Slf4j
public final class UserAccountAssembler {
    private UserAccountAssembler() {
    }

    public static UserAccount toEntity(LoginAccountUserDTO dto) {
        if (dto.getAccountId() == null) {
            log.warn("[UserAccountAssembler#toEntity] failed: accountId is null. ");
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "ユーザーIDが存在しません。");
        }
        if (dto.getAccountStatus() == null) {
            log.warn("[UserAccountAssembler#toEntity] failed: accountStatus is null. accountId={}", dto.getAccountId());
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "アカウントステータスが存在しません。");
        }

        return UserAccount.builder()
                .id(dto.getAccountId())
                .status(dto.getAccountStatus())
                .verified(dto.getVerified() != null ? dto.getVerified() : VerifiedStatus.UNVERIFIED)
                .password(dto.getPassword())
                .build();
    }

    public static UserAccount toCreateEntity(UserCreateRequest req, Long userId, String encodedPassword) {
        if (req == null) {
            log.warn("[UserAccountAssembler#toNewEntity] failed: req is null");
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "アカウント作成リクエストがnullです。");
        }
        return UserAccount.builder()
                .userId(userId)
                .accountType(AccountType.EMAIL)        // 固定EMAIL
                .accountValue(req.accountValue())
                .password(encodedPassword)              // Service層でbcryptハッシュ化してから渡す
                .verified(VerifiedStatus.VERIFIED)     // 管理者作成 → デフォルト認証済み
                .status(Status.ENABLED)                // アカウント状態は常に有効で作成
                .build();
    }

    public static void toUpdateEntity(UserUpdateRequest req, UserAccount existingAccount) {
        if (req == null) {
            log.warn("[UserAccountAssembler#toUpdateEntity] failed: req is null");
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "アカウント更新リクエストがnullです。");
        }
        existingAccount.setStatus(req.status());
        // 他のフィールドはDBの値を保持（上書きしない）
    }

    public static UserAccount toChangeStatusEntity(UserChangeStatusRequest req) {
        if (req == null) {
            log.warn("[UserAccountAssembler#toChangeStatusEntity] failed: req is null");
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "アカウント状態変更リクエストがnullです。");
        }
        return UserAccount.builder()
                .id(req.accountId())
                .status(req.status())
                .build();
    }
}
