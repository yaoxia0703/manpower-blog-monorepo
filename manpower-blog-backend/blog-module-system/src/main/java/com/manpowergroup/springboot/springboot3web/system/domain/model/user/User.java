package com.manpowergroup.springboot.springboot3web.system.domain.model.user;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.UserErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/** システムユーザー。 */
@Getter
@TableName("t_sys_user")
public class User {

    // ユーザーID
    @TableId(type = IdType.AUTO)
    private Long id;

    // ニックネーム
    private String nickName;

    // ユーザー状態
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

    protected User() {
    }

    private User(String nickName, Status status) {
        updateProfile(nickName, status);
        this.isDeleted = 0;
    }

    /** 新しいユーザーを作成する。 */
    public static User create(String nickName, Status status) {
        return new User(nickName, status);
    }

    /** ユーザーのプロフィールと状態を更新する。 */
    public void updateProfile(String nickName, Status status) {
        if (nickName == null || nickName.isBlank()) {
            throw new IllegalArgumentException("ニックネームは必須です");
        }
        this.nickName = nickName.trim();
        this.status = Objects.requireNonNull(status, "状態は必須です");
    }

    /** ユーザーを有効化する。 */
    public void enable() {
        this.status = Status.ENABLED;
    }

    /** ユーザーを無効化する。 */
    public void disable() {
        this.status = Status.DISABLED;
    }

    /** ユーザー状態を変更する。 */
    public void changeStatus(Status status) {
        this.status = Objects.requireNonNull(status, "状態は必須です");
    }

    /** ログイン可能なユーザー状態か検証する。 */
    public void ensureLoginAllowed() {
        if (status == Status.DISABLED) {
            throw BizException.withDetail(UserErrorCode.ACCOUNT_DISABLED, "ユーザーは無効化されています");
        }
    }
}
