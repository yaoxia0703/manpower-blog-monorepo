package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.PageRequest;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.UserErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.framework.security.PasswordService;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.UserAccountAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.UserAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.*;
import com.manpowergroup.springboot.springboot3web.system.application.service.UserAccountAppService;
import com.manpowergroup.springboot.springboot3web.system.application.service.UserRoleAppService;
import com.manpowergroup.springboot.springboot3web.system.application.vo.user.UserPageVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.User;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserAccount;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserRole;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.user.UserMapper;
import com.manpowergroup.springboot.springboot3web.system.application.service.UserAppService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * ユーザーマスタ
 * </p>
 *
 * @author YAOXIA
 * @since 2025-12-18
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserAppServiceImpl extends ServiceImpl<UserMapper, User> implements UserAppService {

    private final UserRepository userRepository;
    private final UserAccountAppService userAccountAppService;
    private final UserRoleAppService userRoleAppService;
    private final PasswordService passwordService;

    @Override
    public LoginUser getCurrentUserContext(Long userId, Long accountId) {
        return userRepository.getCurrentUserContext(userId, accountId);
    }

    @Override
    public JoinPageResult<UserPageVo> pageUsers(PageRequest pageRequest, UserQueryRequest query) {
        log.info("[UserAppService#pageUsers] start keyword={}, status={}", query.keyword(), query.status());
        return userRepository.selectUserPage(query, pageRequest);
    }

    @Override
    @Transactional
    public Long createUser(UserCreateRequest req) {
        log.info("[UserAppService#createUser] start accountValue={}", req.accountValue());

        // 1. アカウント重複チェック
        if (userAccountAppService.existsByAccountTypeAndAccountValue(AccountType.EMAIL, req.accountValue())) {
            log.warn("[UserAppService#createUser] account already exists. accountValue={}", req.accountValue());
            throw BizException.withDetail(UserErrorCode.ACCOUNT_ALREADY_EXISTS, "このメールアドレスは既に登録されています。");
        }

        // 2. ユーザー保存
        User user = UserAssembler.toCreateEntity(req);
        baseMapper.insert(user);
        log.info("[UserAppService#createUser] user saved. userId={}", user.getId());

        // 3. ユーザーアカウント保存（パスワードはbcryptハッシュ化）
        final var encodedPassword = passwordService.encrypt(req.password());
        UserAccount userAccount = UserAccountAssembler.toCreateEntity(req, user.getId(), encodedPassword);
        userAccountAppService.save(userAccount);
        log.info("[UserAppService#createUser] account saved. accountId={}", userAccount.getId());

        // 4. ユーザーロール保存
        UserRole userRole = UserRole.builder()
                .roleId(req.roleId())
                .userId(user.getId())
                .build();
        userRoleAppService.save(userRole);
        log.info("[UserAppService#createUser] role assigned. userId={} roleId={}", user.getId(), req.roleId());

        log.info("[UserAppService#createUser] end userId={}", user.getId());
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateRequest req) {
        log.info("[UserAppService#updateUser] start userId={}", req.userId());

        // 1. ユーザー存在チェック（DBから取得）
        final var existing = ensureUserExists(req.userId());

        // 2. ユーザー更新（nickName / status）
        UserAssembler.toUpdateEntity(req, existing);
        baseMapper.updateById(existing);
        log.info("[UserAppService#updateUser] user updated. userId={}", req.userId());

        // 3. アカウント状態更新（userStatusと連動）
        final var existingAccount = ensureUserAccountExists(req.accountId());
        UserAccountAssembler.toUpdateEntity(req, existingAccount);
        userAccountAppService.updateById(existingAccount);
        log.info("[UserAppService#updateUser] account status updated. accountId={}", req.accountId());

        // 4. ユーザーロール更新（ロールが変更された場合のみ）
        UserRole currentRole = userRoleAppService.getOne(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, req.userId())
                        .last("LIMIT 1")
        );

        if (currentRole == null || !currentRole.getRoleId().equals(req.roleId())) {
            log.info("[UserAppService#updateUser] role changed. userId={} oldRoleId={} newRoleId={}",
                    req.userId(),
                    currentRole != null ? currentRole.getRoleId() : null,
                    req.roleId());

            // 既存ロールを削除
            if (currentRole != null) {
                userRoleAppService.remove(
                        new LambdaQueryWrapper<UserRole>()
                                .eq(UserRole::getUserId, req.userId())
                );
            }

            // 新しいロールを挿入
            UserRole userRole = UserRole.builder()
                    .userId(req.userId())
                    .roleId(req.roleId())
                    .build();
            userRoleAppService.save(userRole);
            log.info("[UserAppService#updateUser] role updated. userId={} newRoleId={}", req.userId(), req.roleId());
        }

        log.info("[UserAppService#updateUser] end userId={}", req.userId());
    }

    @Override
    @Transactional
    public void deleteUser(UserDeleteRequest req) {
        log.info("[UserAppService#deleteUser] start userId={}", req.userId());

        // 1. ユーザー存在チェック
        ensureUserExists(req.userId());

        // 2. ユーザー論理削除
        baseMapper.deleteById(req.userId());
        log.info("[UserAppService#deleteUser] user deleted. userId={}", req.userId());

        // 3. アカウント論理削除
        userAccountAppService.removeById(req.accountId());
        log.info("[UserAppService#deleteUser] account deleted. accountId={}", req.accountId());

        // 4. ユーザーロール論理削除
        userRoleAppService.remove(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, req.userId())
        );
        log.info("[UserAppService#deleteUser] role deleted. userId={}", req.userId());

        log.info("[UserAppService#deleteUser] end userId={}", req.userId());
    }

    @Override
    @Transactional
    public void updateUserStatus(UserChangeStatusRequest req) {
        log.info("[UserAppService#updateUserStatus] start userId={}", req.userId());

        // 1. ユーザー存在チェック（DBから取得）
        final var existing = ensureUserExists(req.userId());

        // 2. ユーザーステータス更新（userStatus と accountStatus を連動）
        existing.setStatus(req.status());
        baseMapper.updateById(existing);
        log.info("[UserAppService#updateUserStatus] user status updated. userId={}", req.userId());

        // 3. アカウントステータス更新
        UserAccount userAccount = UserAccountAssembler.toChangeStatusEntity(req);
        userAccountAppService.updateById(userAccount);
        log.info("[UserAppService#updateUserStatus] account status updated. accountId={}", req.accountId());

        log.info("[UserAppService#updateUserStatus] end userId={}", req.userId());
    }

    @Override
    public UserPageVo getUserDetail(UserDetailQueryRequest request) {
        log.info("[UserAppService#getUserById] start userId={}", request.userId());

        UserPageVo pageVo = userRepository.getUserDetail(request)
                .orElseThrow(() -> {
                    log.warn("[UserAppService#getUserById] user not found. userId={}", request.userId());
                    return BizException.withDetail(UserErrorCode.ACCOUNT_NOT_FOUND, "ユーザーが見つかりません。");
                });

        log.info("[UserAppService#getUserById] end userId={}", request.userId());
        return pageVo;
    }

    /**
     * ユーザー存在チェック（存在しない場合は例外をスロー）
     *
     * @param userId ユーザーID
     * @return 存在するユーザーエンティティ
     */
    private User ensureUserExists(Long userId) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            log.warn("[UserAppService#ensureUserExists] user not found. userId={}", userId);
            throw BizException.withDetail(UserErrorCode.ACCOUNT_NOT_FOUND, "ユーザーが見つかりません。");
        }
        return user;
    }

    private UserAccount ensureUserAccountExists(Long accountId) {
        UserAccount userAccount = userAccountAppService.getById(accountId);
        if (userAccount == null) {
            log.warn("[UserAppService#ensureUserAccountExists] account not found. accountId={}", accountId);
            throw BizException.withDetail(UserErrorCode.ACCOUNT_NOT_FOUND, "アカウントが見つかりません。");
        }
        return userAccount;
    }


}
