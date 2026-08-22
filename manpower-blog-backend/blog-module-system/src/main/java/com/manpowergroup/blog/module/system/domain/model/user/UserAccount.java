package com.manpowergroup.blog.module.system.domain.model.user;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.blog.shared.enums.AccountType;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.shared.support.DomainGuard;
import com.manpowergroup.blog.module.system.domain.service.PasswordEncryptor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/** ユーザーログインアカウント。 */
@Getter
@TableName("t_sys_user_account")
public class UserAccount {

    // 主キーID
    @TableId(type = IdType.AUTO)
    private Long id;

    // ユーザーID
    private Long userId;

    // アカウント種別
    private AccountType accountType;

    // ログイン識別子
    private String accountValue;

    // ハッシュ化済みパスワード
    private String password;

    // 認証状態
    private VerifiedStatus verified;

    // アカウント状態
    private Status status;

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

    protected UserAccount() {
    }

    private UserAccount(Long userId, AccountType accountType, String accountValue,
                        String encodedPassword, VerifiedStatus verified, Status status) {
        this.userId = DomainGuard.requireNonNull(userId, "ユーザーID");
        this.accountType = DomainGuard.requireNonNull(accountType, "アカウント種別");
        this.accountValue = DomainGuard.requireText(accountValue, "ログイン識別子");
        this.password = DomainGuard.requireText(encodedPassword, "パスワード");
        this.verified = DomainGuard.requireNonNull(verified, "認証状態");
        this.status = DomainGuard.requireNonNull(status, "状態");
        this.isDeleted = 0;
    }

    /** 新しいログインアカウントを作成する。 */
    public static UserAccount create(Long userId, AccountType accountType, String accountValue,
                                     String encodedPassword, VerifiedStatus verified, Status status) {
        return new UserAccount(userId, accountType, accountValue, encodedPassword, verified, status);
    }

    /** アカウント状態を変更する。 */
    public void changeStatus(Status status) {
        this.status = DomainGuard.requireNonNull(status, "状態");
    }

    /** ハッシュ化済みパスワードへ変更する。 */
    public void changePassword(String encodedPassword) {
        this.password = DomainGuard.requireText(encodedPassword, "パスワード");
    }

    /** アカウントを認証済みにする。 */
    public void verify() {
        this.verified = VerifiedStatus.VERIFIED;
    }

    /** ユーザーとアカウントがログイン可能な状態か検証する。 */
    public void ensureLoginAllowed(User user) {
        if (status == Status.DISABLED) {
            throw BizException.withDetail(ErrorCode.UNAUTHORIZED, "アカウントは無効化されています");
        }
        if (verified == VerifiedStatus.UNVERIFIED) {
            throw BizException.withDetail(ErrorCode.FORBIDDEN, "アカウントは未認証です");
        }
        if (password == null || password.isBlank()) {
            throw BizException.withDetail(ErrorCode.UNAUTHORIZED, "パスワードが設定されていません");
        }
        DomainGuard.requireNonNull(user, "ユーザー").ensureLoginAllowed();
    }

    /**
     * ログイン認証を行う。
     *
     * <p>アカウント状態・認証状態・ユーザー状態の検証に加えてパスワード照合まで
     * ドメインモデル内で完結させる。照合アルゴリズムは {@link PasswordEncryptor} として
     * 外部から注入するため、ドメイン層はハッシュ方式を知らない。</p>
     *
     * @param rawPassword 平文パスワード
     * @param user        紐づくユーザー
     * @param encryptor   パスワード照合の実装
     */
    public void authenticate(String rawPassword, User user, PasswordEncryptor encryptor) {
        ensureLoginAllowed(user);
        final boolean matched = DomainGuard.requireNonNull(encryptor, "パスワード照合器")
                .matches(rawPassword, this.password);
        if (!matched) {
            throw BizException.withDetail(
                    ErrorCode.UNAUTHORIZED, "アカウントまたはパスワードが正しくありません");
        }
    }

    /** 指定ユーザーに属するアカウントか判定する。 */
    public boolean belongsTo(Long userId) {
        return Objects.equals(this.userId, userId);
    }
}
