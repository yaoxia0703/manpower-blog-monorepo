package com.manpowergroup.blog.module.member.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 会員ログインアカウント
 * </p>
 *
 * @author YAOXIA
 * @since 2026-08-23
 */
@Getter
@Setter
@TableName("t_member_account")
public class MemberAccount {

    /**
     * アカウントID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会員ID
     */
    private Long memberId;

    /**
     * LOCAL_EMAIL / LOCAL_PHONE / GOOGLE / GITHUB
     */
    private String accountType;

    /**
     * メール、電話番号またはOAuthユーザーID
     */
    private String accountValue;

    /**
     * パスワードハッシュ（OAuthの場合はNULL）
     */
    private String password;

    /**
     * 認証済みフラグ
     */
    private Byte verified;

    /**
     * 状態
     */
    private Byte status;

    /**
     * 最終ログイン日時
     */
    private LocalDateTime lastLoginAt;

    /**
     * 作成日時
     */
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    private LocalDateTime updatedAt;

    /**
     * 論理削除フラグ
     */
    private Byte isDeleted;
}
