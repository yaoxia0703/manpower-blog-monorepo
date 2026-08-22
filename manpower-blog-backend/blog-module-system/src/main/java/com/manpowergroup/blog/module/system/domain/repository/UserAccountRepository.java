package com.manpowergroup.blog.module.system.domain.repository;

import com.manpowergroup.blog.shared.enums.AccountType;
import com.manpowergroup.blog.module.system.domain.model.user.UserAccount;

import java.util.Optional;

/** ユーザーアカウント永続化ポート。 */
public interface UserAccountRepository {

    Optional<UserAccount> findById(Long id);

    Optional<UserAccount> findByAccountTypeAndValue(AccountType accountType, String accountValue);

    boolean existsByAccountTypeAndValue(AccountType accountType, String accountValue);

    void create(UserAccount account);

    void update(UserAccount account);

    void delete(Long id);
}
