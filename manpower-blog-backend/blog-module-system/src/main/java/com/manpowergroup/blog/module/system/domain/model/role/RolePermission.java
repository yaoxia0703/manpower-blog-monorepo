package com.manpowergroup.blog.module.system.domain.model.role;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.blog.shared.support.DomainGuard;
import lombok.Getter;

import java.time.LocalDateTime;

/** ロールと権限の関連。 */
@Getter
@TableName("t_sys_role_permission")
public class RolePermission {

    // 主キーID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // ロールID
    private Long roleId;

    // 権限ID
    private Long permissionId;

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

    protected RolePermission() {
    }

    private RolePermission(Long roleId, Long permissionId) {
        this.roleId = DomainGuard.requireNonNull(roleId, "ロールID");
        this.permissionId = DomainGuard.requireNonNull(permissionId, "権限ID");
        this.isDeleted = 0;
    }

    /** 新しいロール・権限関連を作成する。 */
    public static RolePermission create(Long roleId, Long permissionId) {
        return new RolePermission(roleId, permissionId);
    }
}
