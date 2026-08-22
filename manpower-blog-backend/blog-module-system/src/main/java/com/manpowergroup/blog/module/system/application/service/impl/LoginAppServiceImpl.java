package com.manpowergroup.blog.module.system.application.service.impl;

import com.manpowergroup.blog.shared.dto.LoginUser;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.module.system.domain.service.PasswordEncryptor;
import com.manpowergroup.blog.module.system.application.command.auth.LoginCommand;
import com.manpowergroup.blog.module.system.application.service.LoginAppService;
import com.manpowergroup.blog.module.system.domain.model.role.Role;
import com.manpowergroup.blog.module.system.domain.model.user.User;
import com.manpowergroup.blog.module.system.domain.model.user.UserAccount;
import com.manpowergroup.blog.module.system.domain.repository.RoleRepository;
import com.manpowergroup.blog.module.system.domain.repository.UserAccountRepository;
import com.manpowergroup.blog.module.system.domain.repository.UserRepository;
import com.manpowergroup.blog.module.system.domain.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAppServiceImpl implements LoginAppService {

    private final UserAccountRepository userAccountRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncryptor passwordEncryptor;

    @Override
    public LoginUser login(LoginCommand command) {
        final UserAccount account = userAccountRepository
                .findByAccountTypeAndValue(command.accountType(), command.accountValue())
                .orElseThrow(() -> unauthorized("アカウントまたはパスワードが正しくありません"));
        final User user = userRepository.findById(account.getUserId())
                .orElseThrow(() -> unauthorized("アカウントまたはパスワードが正しくありません"));

        // 状態検証とパスワード照合はドメインモデルへ集約する
        try {
            account.authenticate(command.password(), user, passwordEncryptor);
        } catch (BizException e) {
            log.warn("ログインに失敗しました。accountType={}, detail={}",
                    command.accountType(), e.getDetail());
            throw e;
        }

        final List<String> roleNames = roleRepository.listByIds(
                        userRoleRepository.findActiveRoleIds(user.getId())).stream()
                .map(Role::getName)
                .distinct()
                .toList();
        return new LoginUser(
                user.getId(), account.getId(), user.getNickName(), account.getAccountType(),
                account.getAccountValue(), roleNames
        );
    }

    private BizException unauthorized(String detail) {
        return BizException.withDetail(ErrorCode.UNAUTHORIZED, detail);
    }
}
