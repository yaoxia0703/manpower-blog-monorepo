package com.manpowergroup.blog.module.member.domain.repository;

import com.manpowergroup.blog.module.member.domain.model.MemberAccount;
import com.manpowergroup.blog.module.member.domain.model.MemberAccountType;

import java.util.Optional;

/**
 * 会員アカウントリポジトリ
 */
public interface MemberAccountRepository {

    /**
     * 会員アカウントをアカウントIDで検索
     * @param accountId アカウントID
     * @return 会員アカウント情報
     */
    Optional<MemberAccount> findByAccountId(Long accountId);


    /**
     * 会員アカウントをアカウントタイプとアカウント値で検索
     *
     * @param accountType  会员账号类型
     * @param accountValue 会员账号值
     * @return 会员账号信息
     */
    boolean existsByAccountTypeAndAccountValue(MemberAccountType accountType, String accountValue);

    /**
     * 会員アカウントを新規開発
     *
     * @param memberAccount 会員アカウント情報
     */
    void create(MemberAccount memberAccount);

    /**
     * 会員アカウントを倫理削除
     *
     * @param accountId アカウントID
     * @param memberId  会員ID
     */
    void delete(Long accountId, Long memberId);
}
