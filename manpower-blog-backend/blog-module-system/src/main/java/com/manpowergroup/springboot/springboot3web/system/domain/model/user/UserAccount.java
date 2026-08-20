package com.manpowergroup.springboot.springboot3web.system.domain.model.user;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.VerifiedStatus;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
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
        this.userId = Objects.requireNonNull(userId, "ユーザーIDは必須です");
        this.accountType = Objects.requireNonNull(accountType, "アカウント種別は必須です");
        this.accountValue = normalizeRequired(accountValue, "ログイン識別子");
        this.password = normalizeRequired(encodedPassword, "パスワード");
        this.verified = Objects.requireNonNull(verified, "認証状態は必須です");
        this.status = Objects.requireNonNull(status, "状態は必須です");
        this.isDeleted = 0;
    }

    /** 新しいログインアカウントを作成する。 */
    public static UserAccount create(Long userId, AccountType accountType, String accountValue,
                                     String encodedPassword, VerifiedStatus verified, Status status) {
        return new UserAccount(userId, accountType, accountValue, encodedPassword, verified, status);
    }

    /** アカウント状態を変更する。 */
    public void changeStatus(Status status) {
        this.status = Objects.requireNonNull(status, "状態は必須です");
    }

    /** ハッシュ化済みパスワードへ変更する。 */
    public void changePassword(String encodedPassword) {
        this.password = normalizeRequired(encodedPassword, "パスワード");
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
        Objects.requireNonNull(user, "ユーザーは必須です").ensureLoginAllowed();
    }

    /** 指定ユーザーに属するアカウントか判定する。 */
    public boolean belongsTo(Long userId) {
        return Objects.equals(this.userId, userId);
    }

    private static String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "は必須です");
        }
        return value.trim();
    }
}
