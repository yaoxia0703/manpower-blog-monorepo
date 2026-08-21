package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.UserErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.VerifiedStatus;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.framework.security.PasswordService;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.UserAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserDeleteCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.user.UserResponse;
import com.manpowergroup.springboot.springboot3web.system.application.query.user.UserDetailQuery;
import com.manpowergroup.springboot.springboot3web.system.application.query.user.UserPageQuery;
import com.manpowergroup.springboot.springboot3web.system.application.service.UserAppService;
import com.manpowergroup.springboot.springboot3web.system.domain.model.role.Role;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.User;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserAccount;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserSearchCriteria;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserAccountRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAppServiceImpl implements UserAppService {

    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;

    @Override
    public LoginUser getCurrentUserContext(Long userId, Long accountId) {
        final User user = getRequiredUser(userId);
        final UserAccount account = getRequiredAccount(accountId, userId);
        final List<String> roleNames = roleRepository.listByIds(
                        userRoleRepository.findActiveRoleIds(userId)).stream()
                .map(Role::getName)
                .distinct()
                .toList();
        return new LoginUser(
                user.getId(), account.getId(), user.getNickName(), account.getAccountType(),
                account.getAccountValue(), roleNames, List.of()
        );
    }

    @Override
    public JoinPageResult<UserResponse> page(UserPageQuery query) {
        final var page = userRepository.page(
                new UserSearchCriteria(query.keyword(), query.status()), query.pageNum(), query.pageSize());
        return JoinPageResult.of(
                page.records().stream().map(UserAssembler::toResponse).toList(),
                page.total(), page.pageNum(), page.pageSize()
        );
    }

    @Override
    @Transactional
    public Long create(UserCreateCommand command) {
        ensureRoleExists(command.roleId());
        if (userAccountRepository.existsByAccountTypeAndValue(
                command.accountType(), command.accountValue())) {
            throw BizException.withDetail(
                    UserErrorCode.ACCOUNT_ALREADY_EXISTS, "このログインIDは既に登録されています");
        }

        final User user = User.create(command.nickName(), command.status());
        userRepository.create(user);

        final String encodedPassword = passwordService.encrypt(command.password());
        final UserAccount account = UserAccount.create(
                user.getId(), command.accountType(), command.accountValue(), encodedPassword,
                VerifiedStatus.VERIFIED, command.status()
        );
        userAccountRepository.create(account);
        userRoleRepository.replaceRoles(user.getId(), List.of(command.roleId()));
        log.info("ユーザーを作成しました。userId={}, accountId={}", user.getId(), account.getId());
        return user.getId();
    }

    @Override
    @Transactional
    public void update(UserUpdateCommand command) {
        ensureRoleExists(command.roleId());
        final User user = getRequiredUser(command.userId());
        final UserAccount account = getRequiredAccount(command.accountId(), command.userId());

        user.updateProfile(command.nickName(), command.status());
        account.changeStatus(command.status());
        userRepository.update(user);
        userAccountRepository.update(account);
        userRoleRepository.replaceRoles(user.getId(), List.of(command.roleId()));
        log.info("ユーザーを更新しました。userId={}", user.getId());
    }

    @Override
    @Transactional
    public void delete(UserDeleteCommand command) {
        final User user = getRequiredUser(command.userId());
        final UserAccount account = getRequiredAccount(command.accountId(), command.userId());
        userRoleRepository.replaceRoles(user.getId(), List.of());
        userAccountRepository.delete(account.getId());
        userRepository.delete(user.getId());
        log.info("ユーザーを削除しました。userId={}, accountId={}", user.getId(), account.getId());
    }

    @Override
    @Transactional
    public void changeStatus(UserStatusChangeCommand command) {
        final User user = getRequiredUser(command.userId());
        final UserAccount account = getRequiredAccount(command.accountId(), command.userId());
        user.changeStatus(command.status());
        account.changeStatus(command.status());
        userRepository.update(user);
        userAccountRepository.update(account);
        log.info("ユーザー状態を変更しました。userId={}, status={}", user.getId(), command.status());
    }

    @Override
    public UserResponse findById(UserDetailQuery query) {
        return userRepository.findProfile(query.userId(), query.accountId())
                .map(UserAssembler::toResponse)
                .orElseThrow(() -> BizException.withDetail(
                        UserErrorCode.ACCOUNT_NOT_FOUND, "ユーザーが見つかりません"));
    }

    private User getRequiredUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> BizException.withDetail(
                        UserErrorCode.ACCOUNT_NOT_FOUND, "ユーザーが見つかりません"));
    }

    private UserAccount getRequiredAccount(Long accountId, Long userId) {
        final UserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> BizException.withDetail(
                        UserErrorCode.ACCOUNT_NOT_FOUND, "アカウントが見つかりません"));
        if (!account.belongsTo(userId)) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ユーザーとアカウントの組み合わせが不正です");
        }
        return account;
    }

    private void ensureRoleExists(Long roleId) {
        if (roleId == null || roleRepository.findById(roleId).isEmpty()) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ロールが存在しません。id=" + roleId);
        }
    }
}
