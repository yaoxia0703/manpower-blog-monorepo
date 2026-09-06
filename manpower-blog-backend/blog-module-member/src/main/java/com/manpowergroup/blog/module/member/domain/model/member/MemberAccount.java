package com.manpowergroup.blog.module.member.domain.model.member;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.UserErrorCode;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.shared.support.DomainGuard;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 会員ログインアカウント。
 *
 * <p>1人の会員が複数のアカウント（パスワード認証・外部認証）を持ち得る。
 * 種別と識別子の組が有効レコード内で一意となる。</p>
 *
 * @author YAOXIA
 * @since 2026-08-23
 */
@Getter
@TableName("t_member_account")
public class MemberAccount {

    // アカウントID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 会員ID
    private Long memberId;

    // アカウント種別
    private MemberAccountType accountType;

    // ログイン識別子（メール、電話番号または外部認証のユーザーID）
    private String accountValue;

    // パスワードハッシュ（外部認証の場合は null）
    private String password;

    // 認証済みフラグ
    private VerifiedStatus verified;

    // アカウント状態
    private Status status;

    // 最終ログイン日時
    private LocalDateTime lastLoginAt;

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
    protected MemberAccount() {
    }

    private MemberAccount(Long memberId, MemberAccountType accountType, String accountValue,
                          String encodedPassword, VerifiedStatus verified, Status status) {
        this.memberId = DomainGuard.requireNonNull(memberId, "会員ID");
        this.accountType = DomainGuard.requireNonNull(accountType, "アカウント種別");
        this.accountValue = DomainGuard.requireText(accountValue, "ログイン識別子");
        this.password = resolvePassword(this.accountType, encodedPassword);
        this.verified = DomainGuard.requireNonNull(verified, "認証状態");
        this.status = DomainGuard.requireNonNull(status, "状態");
        this.isDeleted = 0;
    }

    /**
     * パスワード認証のアカウントを作成する。
     *
     * @param encodedPassword ハッシュ化済みパスワード
     */
    public static MemberAccount createWithPassword(
            Long memberId, MemberAccountType accountType, String accountValue,
            String encodedPassword, VerifiedStatus verified, Status status) {
        return new MemberAccount(
                memberId, accountType, accountValue, encodedPassword, verified, status);
    }

    /**
     * 外部認証のアカウントを作成する。
     *
     * <p>パスワードを引数に持たない。呼び出し側が誤って値を渡す余地をなくすため。</p>
     *
     * @param accountValue 外部認証プロバイダ側のユーザーID
     */
    public static MemberAccount createWithExternalAuth(
            Long memberId, MemberAccountType accountType, String accountValue,
            VerifiedStatus verified, Status status) {
        return new MemberAccount(memberId, accountType, accountValue, null, verified, status);
    }

    /**
     * 種別に応じてパスワードの整合性を検証する。
     *
     * <p>外部認証で値が入っている場合も拒否する。単に null を許すだけでは、
     * 外部認証アカウントにパスワードが設定され、
     * 本来の認証経路を迂回してログインできる状態が生まれ得るため。</p>
     */
    private static String resolvePassword(MemberAccountType accountType, String encodedPassword) {
        if (accountType.requiresPassword()) {
            return DomainGuard.requireText(encodedPassword, "パスワード");
        }
        DomainGuard.requireTrue(encodedPassword == null,
                "外部認証アカウントはパスワードを保持できません");
        return null;
    }

    /**
     * パスワードを変更する。
     *
     * <p>外部認証アカウントには設定できない。</p>
     *
     * @param encodedPassword ハッシュ化済みパスワード
     */
    public void changePassword(String encodedPassword) {
        DomainGuard.requireTrue(accountType.requiresPassword(),
                "外部認証アカウントにはパスワードを設定できません");
        this.password = DomainGuard.requireText(encodedPassword, "パスワード");
    }

    /** アカウント状態を変更する。 */
    public void changeStatus(Status status) {
        this.status = DomainGuard.requireNonNull(status, "状態");
    }

    /** 識別子の認証を完了させる。 */
    public void verify() {
        this.verified = VerifiedStatus.VERIFIED;
    }

    /**
     * ログインを記録する。
     *
     * <p>時刻は呼び出し側から受け取る。内部で {@code now()} を呼ぶと
     * テストで固定時刻を注入できないため。</p>
     */
    public void recordLogin(LocalDateTime occurredAt) {
        this.lastLoginAt = DomainGuard.requireNonNull(occurredAt, "ログイン日時");
    }

    /** ログイン可能なアカウント状態か検証する。 */
    public void ensureLoginAllowed() {
        if (status == Status.DISABLED) {
            throw BizException.withDetail(
                    UserErrorCode.ACCOUNT_DISABLED, "アカウントは無効化されています");
        }
    }
}
