package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserAccount;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.UserAccountRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.user.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private final UserAccountMapper userAccountMapper;

    @Override
    public Optional<UserAccount> findById(Long id) {
        return Optional.ofNullable(userAccountMapper.selectById(id));
    }

    @Override
    public Optional<UserAccount> findByAccountTypeAndValue(AccountType accountType, String accountValue) {
        return Optional.ofNullable(userAccountMapper.selectOne(new LambdaQueryWrapper<UserAccount>()
                .eq(UserAccount::getAccountType, accountType)
                .eq(UserAccount::getAccountValue, accountValue)
                .last("LIMIT 1")));
    }

    @Override
    public boolean existsByAccountTypeAndValue(AccountType accountType, String accountValue) {
        return userAccountMapper.exists(new LambdaQueryWrapper<UserAccount>()
                .eq(UserAccount::getAccountType, accountType)
                .eq(UserAccount::getAccountValue, accountValue));
    }

    @Override
    public void save(UserAccount account) {
        userAccountMapper.insert(account);
    }

    @Override
    public void update(UserAccount account) {
        userAccountMapper.updateById(account);
    }

    @Override
    public void deleteById(Long id) {
        userAccountMapper.deleteById(id);
    }
}
