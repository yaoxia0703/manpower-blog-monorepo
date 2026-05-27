package com.manpowergroup.springboot.springboot3web.system.domain.model.menu;

import com.baomidou.mybatisplus.annotation.*;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * システムメニュー管理テーブル
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_sys_menu")
public class Menu {

    /**
     * 主キーID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 親メニューID（0は最上位）
     */
    private Long parentId;

    /**
     * 関連するPermissionのID（t_sys_permission.id）
     * ディレクトリ（type=DIRECTORY）の場合はNULL可
     */
    private Long permissionId;

    /**
     * メニュー名称
     */
    private String name;

    /**
     * メニュー種別（1=ディレクトリ 2=メニュー 3=ボタン）
     */
    private MenuType type;

    /**
     * 表示順
     */
    private Integer sort;

    /**
     * アイコン
     */
    private String icon;

    /**
     * 状態（0=無効 1=有効）
     */
    private Status status;

    /**
     * 論理削除フラグ（0=未削除 1=削除済）
     */
    @TableLogic
    @TableField(value = "is_deleted")
    private Byte isDeleted;

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
     * 同一階層の名称重複チェック
     *
     * @param exists 同一階層に同名メニューが存在する場合 true
     */
    public void validateDuplicateName(boolean exists) {
        if (exists) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "同一階層に同名メニューが存在します");
        }
    }

    /**
     * 自身を親に設定できないことを検証する
     *
     * @param id 自身のID
     */
    public void validateNotSelfParent(Long id) {
        if (parentId.equals(id)) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "自身を親に設定できません");
        }
    }

    /**
     * 削除可否チェック
     *
     * @param hasChildren 子メニューが存在する場合 true
     * @param isUsed      他機能で使用されている場合 true
     */
    public void validateDeletable(boolean hasChildren, boolean isUsed) {
        if (hasChildren) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "子メニューが存在するため削除できません");
        }
        if (isUsed) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "該当メニューは使用中のため削除できません");
        }
    }

    /**
     * ステータス変更
     *
     * @param newStatus 変更後ステータス
     */
    public void changeStatus(Status newStatus) {
        if (this.status == newStatus) {
            return;
        }
        this.status = newStatus;
    }
}