package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.dto.auth.LoginAccountUserDTO;

public interface UserAccountRepository {
    /**
     * アカウント種別とアカウント値によりログインユーザー情報を取得する
     *
     *
     * @param accountType アカウント種別
     * @param accountValue アカウント値
     * @return ログインユーザー情報
     */
    LoginAccountUserDTO findLoginUserByAccountTypeAndAccountValue(String accountType,String accountValue);

    boolean existsByAccountTypeAndAccountValue(
           AccountType accountType,
            String accountValue
    );
}
