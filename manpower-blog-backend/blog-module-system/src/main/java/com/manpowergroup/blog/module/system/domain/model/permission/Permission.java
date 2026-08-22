package com.manpowergroup.blog.module.system.domain.model.permission;

import com.baomidou.mybatisplus.annotation.*;
import com.manpowergroup.blog.shared.enums.HttpMethod;

import java.time.LocalDateTime;

import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.support.DomainGuard;
import lombok.Getter;

import java.util.Objects;

/**
 * API権限ルール。
 */
@Getter
@TableName("t_sys_permission")
public class Permission {

    // 主キーID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 所属メニューID（画面上の分類用、API認可には不使用）
    private Long menuId;

    // 権限名
    private String name;

    // 権限制御コード（例：user:add / article:edit）
    private String code;

    // 対象APIパス
    private String path;

    // HTTPメソッド（DBにはenum名を保存）
    private HttpMethod method;

    // 表示順
    private Integer sort;

    // 状態（0=無効、1=有効）
    private Status status;

    // 作成日時
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新日時
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 論理削除フラグ（0=未削除、1=削除済み）
    @TableLogic
    @TableField(value = "is_deleted")
    private Byte isDeleted;

    protected Permission() {
    }

    private Permission(Long menuId, String name, String code, String path,
                       HttpMethod method, Integer sort, Status status) {
        this.menuId = menuId;
        this.name = DomainGuard.requireText(name, "権限名");
        this.code = DomainGuard.requireText(code, "権限制御コード");
        this.path = DomainGuard.requireText(path, "APIパス");
        this.method = DomainGuard.requireNonNull(method, "HTTPメソッド");
        this.sort = Objects.requireNonNullElse(sort, 0);
        this.status = DomainGuard.requireNonNull(status, "状態");
        this.isDeleted = 0;
    }

    /**
     * 新しいAPI権限を作成する。
     *
     * @param menuId 所属メニューID
     * @param name 権限名
     * @param code 権限制御コード
     * @param path APIパス
     * @param method HTTPメソッド
     * @param sort 表示順
     * @param status 状態
     * @return 作成した権限
     */
    public static Permission create(Long menuId, String name, String code, String path,
                                    HttpMethod method, Integer sort, Status status) {
        return new Permission(menuId, name, code, path, method, sort, status);
    }

    /**
     * API権限ルールを更新する。権限制御コードは識別子として変更しない。
     *
     * @param menuId 所属メニューID
     * @param name 権限名
     * @param path APIパス
     * @param method HTTPメソッド
     * @param sort 表示順
     * @param status 状態
     */
    public void updateRule(Long menuId, String name, String path,
                           HttpMethod method, Integer sort, Status status) {
        this.menuId = menuId;
        this.name = DomainGuard.requireText(name, "権限名");
        this.path = DomainGuard.requireText(path, "APIパス");
        this.method = DomainGuard.requireNonNull(method, "HTTPメソッド");
        this.sort = Objects.requireNonNullElse(sort, 0);
        this.status = DomainGuard.requireNonNull(status, "状態");
    }

    /** 権限を有効化する。 */
    public void enable() {
        this.status = Status.ENABLED;
    }

    /** 権限を無効化する。 */
    public void disable() {
        this.status = Status.DISABLED;
    }

}
