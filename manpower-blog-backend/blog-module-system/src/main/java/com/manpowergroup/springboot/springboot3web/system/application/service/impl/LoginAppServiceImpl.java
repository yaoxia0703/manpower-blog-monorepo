package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.framework.security.PasswordService;
import com.manpowergroup.springboot.springboot3web.system.application.command.auth.LoginCommand;
import com.manpowergroup.springboot.springboot3web.system.application.service.LoginAppService;
import com.manpowergroup.springboot.springboot3web.system.domain.model.role.Role;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.User;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserAccount;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserAccountRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserRoleRepository;
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
    private final PasswordService passwordService;

    @Override
    public LoginUser login(LoginCommand command) {
        final UserAccount account = userAccountRepository
                .findByAccountTypeAndValue(command.accountType(), command.accountValue())
                .orElseThrow(() -> unauthorized("アカウントまたはパスワードが正しくありません"));
        final User user = userRepository.findById(account.getUserId())
                .orElseThrow(() -> unauthorized("アカウントまたはパスワードが正しくありません"));

        account.ensureLoginAllowed(user);
        if (!passwordService.matches(command.password(), account.getPassword())) {
            log.warn("ログインに失敗しました。accountType={}", command.accountType());
            throw unauthorized("アカウントまたはパスワードが正しくありません");
        }

        final List<String> roleNames = roleRepository.listByIds(
                        userRoleRepository.findActiveRoleIds(user.getId())).stream()
                .map(Role::getName)
                .distinct()
                .toList();
        return new LoginUser(
                user.getId(), account.getId(), user.getNickName(), account.getAccountType(),
                account.getAccountValue(), roleNames, List.of()
        );
    }

    private BizException unauthorized(String detail) {
        return BizException.withDetail(ErrorCode.UNAUTHORIZED, detail);
    }
}
