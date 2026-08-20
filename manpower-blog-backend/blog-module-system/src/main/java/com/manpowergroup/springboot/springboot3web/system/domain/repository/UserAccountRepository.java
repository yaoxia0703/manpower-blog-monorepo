package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserAccount;

import java.util.Optional;

/** ユーザーアカウント永続化ポート。 */
public interface UserAccountRepository {

    Optional<UserAccount> findById(Long id);

    Optional<UserAccount> findByAccountTypeAndValue(AccountType accountType, String accountValue);

    boolean existsByAccountTypeAndValue(AccountType accountType, String accountValue);

    void save(UserAccount account);

    void update(UserAccount account);

    void deleteById(Long id);
}
