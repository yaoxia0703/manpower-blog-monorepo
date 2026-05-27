package com.manpowergroup.springboot.springboot3web.system.domain.model.permission;

import com.baomidou.mybatisplus.annotation.*;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.PermissionType;

import java.time.LocalDateTime;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 権限マスタ（MENU/BUTTON/API）
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
     * 親権限ID（0=ルート）
     */
    private Long parentId;

    /**
     * 権限名
     */
    private String name;

    /**
     * 権限制御コード（例：user:add / article:edit）
     */
    private String code;

    /**
     * 権限種別（1=MENU, 2=BUTTON, 3=API）
     */
    private PermissionType type;

    /**
     * 対象パス（MENU/API 用）
     */
    private String path;

    /**
     * HTTPメソッド（API 用：GET/POST/PUT/DELETE/PATCH）
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

    public void validate() {
        switch (this.type) {
            case MENU -> {
                // MENUはルートレベルのみ（parentId = 0）
                if (this.parentId != null && this.parentId != 0) {
                    throw BizException.withDetail(ErrorCode.BAD_REQUEST, "MENUはトップレベル（parentId=0）のみ設定できます");
                }
                // MENUにmethodは不要
                if (this.method != null) {
                    throw BizException.withDetail(ErrorCode.BAD_REQUEST, "MENUの場合、methodは指定できません");
                }
                // pathは必須
                if (this.path == null || this.path.isBlank()) {
                    throw BizException.withDetail(ErrorCode.BAD_REQUEST, "MENUの場合、pathは必須です");
                }
            }
            case BUTTON -> {
                // BUTTONは必ず子ノード
                if (this.parentId == null || this.parentId == 0) {
                    throw BizException.withDetail(ErrorCode.BAD_REQUEST, "BUTTONは親権限が必要です");
                }
                // pathは必須
                if (this.path == null || this.path.isBlank()) {
                    throw BizException.withDetail(ErrorCode.BAD_REQUEST, "BUTTONの場合、pathは必須です");
                }
                // methodは必須
                if (this.method == null) {
                    throw BizException.withDetail(ErrorCode.BAD_REQUEST, "BUTTONの場合、methodは必須です");
                }
            }
            case API -> {
                // APIは必ず子ノード
                if (this.parentId == null || this.parentId == 0) {
                    throw BizException.withDetail(ErrorCode.BAD_REQUEST, "APIは親権限が必要です");
                }
                // methodは必須
                if (this.method == null) {
                    throw BizException.withDetail(ErrorCode.BAD_REQUEST, "APIの場合、methodは必須です");
                }
            }
        }
    }
}
