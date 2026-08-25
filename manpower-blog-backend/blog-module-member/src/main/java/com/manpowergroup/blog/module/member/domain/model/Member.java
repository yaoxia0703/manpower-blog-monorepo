package com.manpowergroup.blog.module.member.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.UserErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.shared.support.DomainGuard;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 会員。
 *
 * <p>ポータル利用者を表す集約。運用者を表す {@code User} とは別の概念であり、
 * 認証経路・権限体系ともに共有しない。</p>
 *
 * @author YAOXIA
 * @since 2026-08-23
 */
@Getter
@TableName("t_member")
public class Member {

    // 会員ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 外部公開用会員番号
    private String memberNo;

    // 会員状態
    private Status status;

    // 最終活動日時
    private LocalDateTime lastActiveAt;

    // 作成日時
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新日時
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 論理削除フラグ
    @TableLogic
    @TableField(value = "is_deleted")
    private Byte isDeleted;

    /** MyBatis-Plus がインスタンス化するための既定コンストラクタ。 */
    protected Member() {
    }

    private Member(String memberNo, Status status) {
        updateProfile(memberNo, status);
        this.isDeleted = 0;
    }

    /** 新しい会員を作成する。 */
    public static Member create(String memberNo, Status status) {
        return new Member(memberNo, status);
    }

    private void updateProfile(String memberNo, Status status) {
        this.memberNo = DomainGuard.requireText(memberNo, "会員番号");
        this.status = DomainGuard.requireNonNull(status, "状態");
    }

    /** 会員状態を変更する。 */
    public void changeStatus(Status status) {
        this.status = DomainGuard.requireNonNull(status, "状態");
    }

    /**
     * 会員の活動を記録する。
     *
     * <p>時刻は呼び出し側から受け取る。内部で {@code now()} を呼ぶと
     * テストで固定時刻を注入できず、記録時刻の検証ができないため。</p>
     */
    public void recordActivity(LocalDateTime occurredAt) {
        this.lastActiveAt = DomainGuard.requireNonNull(occurredAt, "活動日時");
    }

    /** 会員を有効化する。 */
    public void enable() {
        this.status = Status.ENABLED;
    }

    /** 会員を無効化する。 */
    public void disable() {
        this.status = Status.DISABLED;
    }

    /** ログイン可能な会員状態か検証する。 */
    public void ensureLoginAllowed() {
        if (status == Status.DISABLED) {
            throw BizException.withDetail(UserErrorCode.ACCOUNT_DISABLED, "会員は無効化されています");
        }
    }

}
