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
import com.manpowergroup.blog.module.system.domain.service.PasswordEncryptor;
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
     * パスワードを知らない相手にはアカウントの状態を伝えないこと。
     *
     * <p>状態検証がパスワード照合より先に行われると、無効・未認証といった
     * 状態が資格情報なしで判別でき、アカウントの列挙が可能になる。
     * 特に未認証は 403 を返すため、detail を秘匿しても
     * HTTP ステータスの違いだけで存在が漏れる。</p>
     *
     * <p>本テストは authenticate の検証順序を固定する。
     * 順序を戻すとエラーは発生せず、応答が親切になるだけであるため、
     * テストが無いと退行に気付けない。</p>
     */
    @Test
    void 無効なアカウントでもパスワード誤りなら状態を明かさない() {
        final User user = User.create("テストユーザー", Status.ENABLED);
        final UserAccount account = UserAccount.create(
                1L, AccountType.EMAIL, "test@example.com", "encoded-password",
                VerifiedStatus.UNVERIFIED, Status.DISABLED);

        // 常に不一致を返す照合器。パスワードを知らない相手を表す。
        final PasswordEncryptor rejectAll = new PasswordEncryptor() {
            @Override
            public String encrypt(String rawPassword) {
                return rawPassword;
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return false;
            }
        };

        assertThatThrownBy(() -> account.authenticate("wrong-password", user, rejectAll))
                .isInstanceOf(BizException.class)
                .satisfies(e -> {
                    final BizException biz = (BizException) e;
                    // 無効・未認証のいずれでもなく、資格情報の誤りとして返ること
                    assertThat(biz.getCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
                    assertThat(biz.getDetail())
                            .isEqualTo("アカウントまたはパスワードが正しくありません");
                });
    }

    /**
     * パスワードが一致すれば状態の詳細を返すこと。
     *
     * <p>照合に成功した相手は所有者とみなせるため、無効化されている等の
     * 理由を伝えてよい。列挙対策のために所有者への案内まで
     * 失わせないことを確認する。</p>
     */
    @Test
    void パスワードが一致すれば無効である旨を返す() {
        final User user = User.create("テストユーザー", Status.ENABLED);
        final UserAccount account = UserAccount.create(
                1L, AccountType.EMAIL, "test@example.com", "encoded-password",
                VerifiedStatus.VERIFIED, Status.DISABLED);

        final PasswordEncryptor acceptAll = new PasswordEncryptor() {
            @Override
            public String encrypt(String rawPassword) {
                return rawPassword;
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return true;
            }
        };

        assertThatThrownBy(() -> account.authenticate("correct-password", user, acceptAll))
                .isInstanceOf(BizException.class)
                .satisfies(e -> assertThat(((BizException) e).getDetail())
                        .isEqualTo("アカウントは無効化されています"));
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
