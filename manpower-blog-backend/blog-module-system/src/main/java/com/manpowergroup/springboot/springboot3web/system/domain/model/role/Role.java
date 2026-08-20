package com.manpowergroup.springboot.springboot3web.system.domain.model.role;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/** ロール。 */
@Getter
@TableName("t_sys_role")
public class Role {

    // 主キーID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // ロールコード
    private String code;

    // ロール名
    private String name;

    // 表示順
    private Integer sort;

    // 状態
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

    protected Role() {
    }

    private Role(String code, String name, Integer sort, Status status) {
        changeDetails(code, name, sort, status);
        this.isDeleted = 0;
    }

    /** 新しいロールを作成する。 */
    public static Role create(String code, String name, Integer sort, Status status) {
        return new Role(code, name, sort, status);
    }

    /** ロールの基本情報を更新する。 */
    public void changeDetails(String code, String name, Integer sort, Status status) {
        this.code = normalizeCode(code);
        this.name = normalizeRequired(name, "ロール名");
        this.sort = Objects.requireNonNullElse(sort, 0);
        this.status = Objects.requireNonNull(status, "状態は必須です");
    }

    /** ロールを有効化する。 */
    public void enable() {
        this.status = Status.ENABLED;
    }

    /** ロールを無効化する。 */
    public void disable() {
        this.status = Status.DISABLED;
    }

    /** ロール状態を変更する。 */
    public void changeStatus(Status status) {
        this.status = Objects.requireNonNull(status, "状態は必須です");
    }

    private static String normalizeCode(String code) {
        return normalizeRequired(code, "ロールコード").toUpperCase(Locale.ROOT);
    }

    private static String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "は必須です");
        }
        return value.trim();
    }
}
