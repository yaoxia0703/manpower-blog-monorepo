package com.manpowergroup.springboot.springboot3web.system.domain.model.user;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/** ユーザーとロールの関連。 */
@Getter
@TableName("t_sys_user_role")
public class UserRole {

    // 主キーID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // ユーザーID
    private Long userId;

    // ロールID
    private Long roleId;

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

    protected UserRole() {
    }

    private UserRole(Long userId, Long roleId) {
        this.userId = Objects.requireNonNull(userId, "ユーザーIDは必須です");
        this.roleId = Objects.requireNonNull(roleId, "ロールIDは必須です");
        this.isDeleted = 0;
    }

    /** 新しいユーザー・ロール関連を作成する。 */
    public static UserRole create(Long userId, Long roleId) {
        return new UserRole(userId, roleId);
    }
}
