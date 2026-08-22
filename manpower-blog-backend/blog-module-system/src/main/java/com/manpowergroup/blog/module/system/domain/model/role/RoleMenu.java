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

/** ロールとメニューの関連。 */
@Getter
@TableName("t_sys_role_menu")
public class RoleMenu {

    // 主キーID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // ロールID
    private Long roleId;

    // メニューID
    private Long menuId;

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

    protected RoleMenu() {
    }

    private RoleMenu(Long roleId, Long menuId) {
        this.roleId = DomainGuard.requireNonNull(roleId, "ロールID");
        this.menuId = DomainGuard.requireNonNull(menuId, "メニューID");
        this.isDeleted = 0;
    }

    /** 新しいロール・メニュー関連を作成する。 */
    public static RoleMenu create(Long roleId, Long menuId) {
        return new RoleMenu(roleId, menuId);
    }
}
