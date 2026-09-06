package com.manpowergroup.blog.module.member.domain.repository.member;


import com.manpowergroup.blog.module.member.domain.model.member.MemberProfile;

import java.util.Optional;

/**
 * 会員プロフィールリポジトリ
 */
public interface MemberProfileRepository {

    /**
     * 会員プロフィールを会員IDで検索
     *
     * @param memberId 会員ID
     * @return 会員プロフィール信息
     */
    Optional<MemberProfile> findByMemberId(Long memberId);

    /**
     * 会員プロフィールを新規開発
     *
     * @param memberProfile 会員プロフィール信息
     */
    void create(MemberProfile memberProfile);

    /**
     * 会員プロフィールを更新
     *
     * @param memberProfile 会員プロフィール信息
     */
    void update(MemberProfile memberProfile);


    /**
     * 会員プロフィールを会員IDで削除
     *
     * @param memberId 会員ID
     */
    void deleteByMemberId(Long memberId);
}
