package com.manpowergroup.blog.module.member.domain.model.member;

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

import java.time.LocalDate;
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

    /*
     * 外部公開用会員番号。
     *
     * varchar との相互変換は MemberNoTypeHandler が担う。
     * ハンドラは infrastructure 層に置き、mybatis-plus.type-handlers-package で
     * 型単位に登録している。ここで typeHandler を名指しすると
     * domain -> infrastructure の依存が生まれるため参照しない。
     */
    private MemberNo memberNo;

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

    private Member(MemberNo memberNo, Status status) {
        this.memberNo = DomainGuard.requireNonNull(memberNo, "会員番号");
        this.status = DomainGuard.requireNonNull(status, "状態");
        this.isDeleted = 0;
    }

    /**
     * 新しい会員を登録する。
     *
     * <p>会員番号は集約が自ら採番する。呼び出し側に採番させると
     * 書式違反や重複した番号を渡す余地が生まれるため、外部から受け取らない。</p>
     *
     * <p>登録日は呼び出し側から受け取る。内部で {@code now()} を呼ぶと
     * テストで会員番号の日付部を固定できないため。</p>
     *
     * @param status       初期状態
     * @param registeredOn 登録日
     */
    public static Member register(Status status, LocalDate registeredOn) {
        return new Member(MemberNo.generate(registeredOn), status);
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
