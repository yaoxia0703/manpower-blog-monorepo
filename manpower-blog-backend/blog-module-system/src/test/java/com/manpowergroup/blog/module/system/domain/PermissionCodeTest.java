package com.manpowergroup.blog.module.system.domain;

import com.manpowergroup.blog.module.system.domain.model.permission.Permission;
import com.manpowergroup.blog.module.system.domain.model.permission.PermissionCode;
import com.manpowergroup.blog.shared.enums.HttpMethod;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.exception.BizException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 権限コードの形式規約 {@code <ドメイン>:<リソース>:<動詞>[修飾語]} を検証する。
 *
 * <p>権限コードは画面のボタン制御と API 認可の双方で文字列一致により照合される。
 * 規約外のコードが登録されてもエラーは発生せず、ボタンが表示されない・
 * API が 403 になるという形でしか現れないため、登録の時点で拒否する。</p>
 */
class PermissionCodeTest {

    @Test
    void 規約に沿ったコードを受け付ける() {
        final List<String> valid = List.of(
                "system:user:list",
                "system:user:changeStatus",
                "content:article:detail",
                "member:member:delete",
                "system:menu:listEnabled",
                "system:menu:listOptions",
                "system:role:detailAuthorization",
                "system:role:updateAuthorization");

        valid.forEach(code -> assertThat(PermissionCode.of(code).value()).isEqualTo(code));
    }

    @Test
    void 前後の空白は除去する() {
        assertThat(PermissionCode.of("  system:user:list ").value()).isEqualTo("system:user:list");
    }

    /**
     * 未知のドメイン名を拒否する。
     *
     * <p>第1段をドメイン名の列挙で制限するのは、略記（{@code sys}）や綴り誤りを
     * 受け付けると、同じドメインが複数の表記で存在する状態に戻るためである。</p>
     */
    @Test
    void 未知のドメイン名を拒否する() {
        assertRejected("sys:user:list");
        assertRejected("System:user:list");
        assertRejected("blog:user:list");
    }

    @Test
    void 三段以外の構成を拒否する() {
        assertRejected("system:role:authorization:list");
        assertRejected("system:user");
        assertRejected("user:list");
    }

    /**
     * 基本動詞に該当しない動詞を拒否する。
     *
     * <p>修飾語は基本動詞の後ろにのみ付けられる。動詞そのものを自由に作れると、
     * 同じ操作が {@code activeTree} / {@code enabledTree} のように複数の名前で登録され得る。</p>
     */
    @Test
    void 基本動詞に該当しない動詞を拒否する() {
        assertRejected("system:menu:activeTree");
        assertRejected("system:menu:parentOptions");
        assertRejected("system:role:assignAuthorization");
        assertRejected("system:user:view");
    }

    @Test
    void 修飾語の形式が不正なものを拒否する() {
        // 修飾語は大文字で始まる（listenabled は list + enabled と区別できない）
        assertRejected("system:menu:listenabled");
        assertRejected("system:menu:list_enabled");
        assertRejected("system:user:List");
    }

    @Test
    void 空白やnullは業務例外とする() {
        assertRejected(null);
        assertRejected("   ");
    }

    /** 権限の生成時に規約が強制され、呼び忘れの余地がないこと。 */
    @Test
    void 権限の生成時に規約外のコードを拒否する() {
        assertThatThrownBy(() -> Permission.create(
                1L, "参照", "sys:user:list", "/api/system/user/page",
                HttpMethod.GET, 1, Status.ENABLED))
                .isInstanceOf(BizException.class);
    }

    private static void assertRejected(String code) {
        assertThatThrownBy(() -> PermissionCode.of(code))
                .as("code=%s", code)
                .isInstanceOf(BizException.class);
    }
}
