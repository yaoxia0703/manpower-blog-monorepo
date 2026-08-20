package com.manpowergroup.springboot.springboot3web.system.domain.model.permission;

import com.baomidou.mybatisplus.annotation.*;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;

import java.time.LocalDateTime;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * API権限ルール。
 */
@Data
@TableName("t_sys_permission")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Permission {

    /**
     * 主キーID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属メニューID（画面上の分類用、API認可には不使用）
     */
    private Long menuId;

    /**
     * 権限名
     */
    private String name;

    /**
     * 権限制御コード（例：user:add / article:edit）
     */
    private String code;

    /**
     * 対象APIパス
     */
    private String path;

    /**
     * HTTPメソッド（GET/POST/PUT/DELETE/PATCH）
     * DBは varchar(10) のままでOK（enum名がそのまま保存される）
     */
    private HttpMethod method;

    /**
     * 表示順
     */
    private Integer sort;

    /**
     * 状態（0=無効、1=有効）
     */
    private Status status;

    /**
     * 作成日時
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 論理削除フラグ（0=未削除、1=削除済み）
     */
    @TableLogic
    @TableField(value = "is_deleted")
    private Byte isDeleted;

}
