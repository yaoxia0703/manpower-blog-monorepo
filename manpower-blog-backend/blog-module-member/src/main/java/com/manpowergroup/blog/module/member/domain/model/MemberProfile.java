package com.manpowergroup.blog.module.member.domain.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 会員プロフィール
 * </p>
 *
 * @author YAOXIA
 * @since 2026-08-24
 */
@Getter
@Setter
@TableName("t_member_profile")
public class MemberProfile {

    /**
     * 会員ID
     */
    @TableId("member_id")
    private Long memberId;

    /**
     * 表示名
     */
    private String displayName;

    /**
     * 公開用ユーザー名
     */
    private String handle;

    /**
     * アバターURL
     */
    private String avatarUrl;

    /**
     * 自己紹介
     */
    private String bio;

    /**
     * WebサイトURL
     */
    private String websiteUrl;

    /**
     * 言語設定
     */
    private String locale;

    /**
     * タイムゾーン
     */
    private String timezone;

    /**
     * 作成日時
     */
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    private LocalDateTime updatedAt;
}
