package com.manpowergroup.blog.module.system.domain;

import com.manpowergroup.blog.shared.enums.AccountType;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.enums.HttpMethod;
import com.manpowergroup.blog.shared.enums.MenuType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.module.system.domain.model.menu.Menu;
import com.manpowergroup.blog.module.system.domain.model.permission.Permission;
import com.manpowergroup.blog.module.system.domain.model.role.Role;
import com.manpowergroup.blog.module.system.domain.model.role.RoleAuthorization;
import com.manpowergroup.blog.module.system.domain.model.user.User;
import com.manpowergroup.blog.module.system.domain.model.user.UserAccount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SystemDomainModelTest {

    @Test
    void menuRequiresPathForMenuType() {
        assertThatThrownBy(() -> Menu.create(
                0L, "ユーザー管理", null, null, MenuType.MENU, 1, null, Status.ENABLED))
                .isInstanceOf(BizException.class);
    }

    @Test
    void permissionKeepsCodeWhenRuleIsUpdated() {
        final Permission permission = Permission.create(
                1L, "参照", "sys:user:list", "/api/system/user/page",
                HttpMethod.GET, 1, Status.ENABLED);

        permission.updateRule(
                2L, "一覧参照", "/api/system/user/page", HttpMethod.GET, 2, Status.DISABLED);

        assertThat(permission.getCode()).isEqualTo("sys:user:list");
        assertThat(permission.getMenuId()).isEqualTo(2L);
        assertThat(permission.getStatus()).isEqualTo(Status.DISABLED);
    }

    @Test
    void roleNormalizesCodeAndName() {
        final Role role = Role.create(" admin_role ", " 管理者 ", null, Status.ENABLED);

        assertThat(role.getCode()).isEqualTo("ADMIN_ROLE");
        assertThat(role.getName()).isEqualTo("管理者");
        assertThat(role.getSort()).isZero();
    }

    @Test
    void authorizationRemovesDuplicateAndNullIds() {
        final RoleAuthorization authorization = RoleAuthorization.create(
                1L, java.util.Arrays.asList(1L, null, 1L, 2L), List.of(10L, 10L));

        assertThat(authorization.menuIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(authorization.permissionIds()).containsExactly(10L);
    }

    @Test
    void disabledAccountCannotLogin() {
        final User user = User.create("テストユーザー", Status.ENABLED);
        final UserAccount account = UserAccount.create(
                1L, AccountType.EMAIL, "test@example.com", "encoded-password",
                VerifiedStatus.VERIFIED, Status.DISABLED);

        assertThatThrownBy(() -> account.ensureLoginAllowed(user))
                .isInstanceOf(BizException.class);
    }

    /**
     * 必須項目の不正は BizException(400) として送出されること。
     *
     * <p>IllegalArgumentException / NullPointerException は
     * GlobalExceptionHandler に登録されておらず HTTP 500 になってしまうため、
     * ドメイン層からこれらを送出しないことを本テストで担保する。</p>
     */
    @Test
    void blankRequiredFieldIsBadRequestNotServerError() {
        assertThatThrownBy(() -> User.create("  ", Status.ENABLED))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getCode())
                .isEqualTo(ErrorCode.BAD_REQUEST);
    }

    /** null の必須項目も 400 として扱われること。 */
    @Test
    void nullRequiredFieldIsBadRequestNotServerError() {
        assertThatThrownBy(() -> Role.create("ADMIN", "管理者", 1, null))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getCode())
                .isEqualTo(ErrorCode.BAD_REQUEST);

        assertThatThrownBy(() -> RoleAuthorization.create(1L, null, List.of()))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getCode())
                .isEqualTo(ErrorCode.BAD_REQUEST);
    }
}
