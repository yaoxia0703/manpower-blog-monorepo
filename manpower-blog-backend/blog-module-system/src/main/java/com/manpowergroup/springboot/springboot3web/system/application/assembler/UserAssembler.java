package com.manpowergroup.springboot.springboot3web.system.application.assembler;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.UserErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserChangeStatusRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.dto.auth.LoginAccountUserDTO;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.User;
import lombok.extern.slf4j.Slf4j;

/**
 * UserAssembler は、各種リクエスト・DTOから User エンティティへの変換を担当するクラスです。
 *
 * @author YAOXIA
 * @since 2025-12-18
 */
@Slf4j
public final class UserAssembler {

    private UserAssembler() {
    }

    /**
     * ログイン用DTO → User エンティティ変換
     */
    public static User toEntity(LoginAccountUserDTO dto) {
        if (dto.getUserId() == null) {
            log.warn("[UserAssembler#toEntity] failed: userId is null");
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "ユーザーIDがnullです。");
        }
        return User.builder()
                .id(dto.getUserId())
                .status(dto.getUserStatus())
                .build();
    }

    /**
     * ユーザー作成リクエスト → User エンティティ変換
     */
    public static User toCreateEntity(UserCreateRequest req) {
        if (req == null) {
            log.warn("[UserAssembler#toCreateEntity] failed: req is null");
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "ユーザー作成リクエストがnullです。");
        }
        return User.builder()
                .nickName(req.nickName())
                .status(req.status())
                .build();
    }

    /**
     * ユーザー更新リクエスト → User エンティティ変換
     */
    public static void toUpdateEntity(UserUpdateRequest req, User existing) {
        if (req == null) {
            log.warn("[UserAssembler#updateEntity] failed: req is null");
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "ユーザー更新リクエストがnullです。");
        }
        existing.setNickName(req.nickName());
        existing.setStatus(req.status());
        // 他のフィールドはDBの値を保持（上書きしない）
    }

    public static User toChangeStatusRequest(UserChangeStatusRequest req) {
        if (req == null) {
            log.warn("[UserAssembler#toChangeStatusRequest] failed: req is null");
            throw BizException.withDetail(UserErrorCode.INVALID_ACCOUNT_DATA, "ユーザーステータス変更リクエストがnullです。");
        }
        return User.builder()
                .id(req.userId())
                .status(req.status())
                .build();

    }
}