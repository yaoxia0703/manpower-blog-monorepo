package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.dto.auth.LoginAccountUserDTO;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserAccountRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.user.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAccountRepositoryImpl implements UserAccountRepository {
    private final UserAccountMapper userAccountMapper;

    @Override
    public LoginAccountUserDTO findLoginUserByAccountTypeAndAccountValue(String accountType, String accountValue) {
        return userAccountMapper.findLoginUserByAccountTypeAndAccountValue(accountType, accountValue);
    }

    @Override
    public boolean existsByAccountTypeAndAccountValue(AccountType accountType, String accountValue) {
        return userAccountMapper.existsByAccountTypeAndAccountValue(accountType, accountValue);
    }
}
