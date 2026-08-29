package com.manpowergroup.blog.module.member.domain.repository;

import com.manpowergroup.blog.module.member.domain.model.Member;

import java.util.Optional;

/**
 * 会員リポジトリ
 */
public interface MemberRepository {
    /**
     * 会員をIDで検索
     *
     * @param id 会员ID
     * @return 会员信息
     */
    Optional<Member> findById(Long id);

    /**
     * 会員を新規開発
     *
     * @param member 会员信息
     */
    void create(Member member);


    /**
     * 会員を倫理削除
     * @param id 　社員ID
     * @param accountId　社員アカウントID
     */
    void delete(Long id,Long accountId);
}
