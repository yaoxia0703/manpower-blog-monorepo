package com.manpowergroup.blog.module.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 会員
 * </p>
 *
 * @author YAOXIA
 * @since 2026-08-23
 */
@Getter
@Setter
@TableName("t_member")
public class Member {

    /**
     * 会員ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 外部公開用会員番号
     */
    private String memberNo;

    /**
     * 状態（0=無効、1=有効）
     */
    private Byte status;

    /**
     * 最終活動日時
     */
    private LocalDateTime lastActiveAt;

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
