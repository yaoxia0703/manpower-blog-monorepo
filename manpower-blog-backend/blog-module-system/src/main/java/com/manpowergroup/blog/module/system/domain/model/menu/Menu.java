package com.manpowergroup.blog.module.system.domain.model.menu;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.enums.MenuType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.shared.support.DomainGuard;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * システムメニュー。
 */
@Getter
@TableName("t_sys_menu")
public class Menu {

    // 主キーID
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 親メニューID（0は最上位）
    private Long parentId;

    // メニュー名称
    private String name;

    // フロントエンドルート
    private String path;

    // フロントエンドコンポーネントキー
    private String component;

    // メニュー種別
    private MenuType type;

    // 表示順
    private Integer sort;

    // アイコン
    private String icon;

    // 状態
    private Status status;

    // 論理削除フラグ
    @TableLogic
    @TableField(value = "is_deleted")
    private Byte isDeleted;

    // 作成日時
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新日時
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    protected Menu() {
    }

    private Menu(Long parentId, String name, String path, String component,
                 MenuType type, Integer sort, String icon, Status status) {
        this.parentId = DomainGuard.requireNonNegative(parentId, "親メニューID");
        this.name = DomainGuard.requireText(name, "メニュー名");
        this.path = DomainGuard.normalizeText(path);
        this.component = DomainGuard.normalizeText(component);
        this.type = DomainGuard.requireNonNull(type, "メニュー種別");
        this.sort = Objects.requireNonNullElse(sort, 999);
        this.icon = DomainGuard.normalizeText(icon);
        this.status = DomainGuard.requireNonNull(status, "状態");
        this.isDeleted = 0;
        validateTypeRule();
    }

    /**
     * 新しいメニューを作成する。
     *
     * @return 作成したメニュー
     */
    public static Menu create(Long parentId, String name, String path, String component,
                              MenuType type, Integer sort, String icon, Status status) {
        return new Menu(parentId, name, path, component, type, sort, icon, status);
    }

    /**
     * メニューの表示情報を更新する。親IDと種別はこの操作では変更しない。
     */
    public void updateDetails(String name, String path, String component,
                              Integer sort, String icon, Status status) {
        this.name = DomainGuard.requireText(name, "メニュー名");
        this.path = DomainGuard.normalizeText(path);
        this.component = DomainGuard.normalizeText(component);
        this.sort = Objects.requireNonNullElse(sort, 999);
        this.icon = DomainGuard.normalizeText(icon);
        this.status = DomainGuard.requireNonNull(status, "状態");
        validateTypeRule();
    }

    /**
     * 親メニューを変更する。
     *
     * @param newParentId 新しい親メニューID
     */
    public void changeParent(Long newParentId) {
        final Long normalizedParentId = DomainGuard.requireNonNegative(newParentId, "親メニューID");
        if (id != null && id.equals(normalizedParentId)) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "自身を親に設定できません");
        }
        this.parentId = normalizedParentId;
    }

    /**
     * 同一階層の名称重複を検証する。
     *
     * @param exists 同名メニューが存在する場合true
     */
    public void validateDuplicateName(boolean exists) {
        if (exists) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "同一階層に同名メニューが存在します");
        }
    }

    /**
     * 削除可能か検証する。
     *
     * @param hasChildren 子メニューが存在する場合true
     * @param assignedToRole ロールに割り当てられている場合true
     * @param hasPermissions 権限が紐づいている場合true
     */
    public void validateDeletable(boolean hasChildren, boolean assignedToRole, boolean hasPermissions) {
        if (hasChildren) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "子メニューが存在するため削除できません");
        }
        if (assignedToRole) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ロールに割り当てられているメニューは削除できません");
        }
        if (hasPermissions) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "権限が紐づいているメニューは削除できません");
        }
    }

    /** 状態を変更する。 */
    public void changeStatus(Status newStatus) {
        this.status = DomainGuard.requireNonNull(newStatus, "状態");
    }

    private void validateTypeRule() {
        if (type == MenuType.MENU && path == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "メニューの場合、pathは必須です");
        }
    }
}
