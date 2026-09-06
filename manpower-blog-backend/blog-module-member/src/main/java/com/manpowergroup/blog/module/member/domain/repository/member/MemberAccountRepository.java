package com.manpowergroup.blog.module.member.domain.repository.member;

import com.manpowergroup.blog.module.member.domain.model.member.MemberAccount;
import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;
import com.manpowergroup.blog.shared.enums.Status;

import java.util.Optional;

/**
 * 会員アカウントリポジトリ
 */
public interface MemberAccountRepository {

    /**
     * 会員アカウントをアカウントIDで検索
     *
     * @param accountId アカウントID
     * @return 会員アカウント情報
     */
    Optional<MemberAccount> findByAccountId(Long accountId);


    /**
     * 会員アカウントをアカウントタイプとアカウント値で検索
     *
     * @param accountType  会員アカウントタイプ
     * @param accountValue 会員アカウント値
     * @return 会員アカウント情報
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
     * @param memberId  会員ID
     */
    void delete( Long memberId);

    /**
     * 会員アカウントを更新
     * 会員IDで検索し、情報を更新する
     * @param memberAccount 会員アカウント情報
     */
    void updateByMemberId(MemberAccount memberAccount);
}
