package com.manpowergroup.springboot.springboot3web.system.domain;

import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.UserAuthorities;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 実効権限の算出ルールを検証する。
 *
 * <p>「管理者ロールは全権限を持つ」というルールは画面制御と API 認可の双方が参照するため、
 * 表現の揺れが不整合（画面にボタンは出るが API は 403）を生む。
 * 本テストでその一元化を担保する。</p>
 */
class UserAuthoritiesTest {

    @Test
    void 一般ユーザーは付与された権限コードのみを持つ() {
        final UserAuthorities authorities = UserAuthorities.of(
                List.of("EDITOR"), List.of("sys:user:list", "sys:user:detail"));

        assertThat(authorities.isSuperAdmin()).isFalse();
        assertThat(authorities.effectivePermissionCodes())
                .containsExactlyInAnyOrder("sys:user:list", "sys:user:detail");
    }

    @Test
    void ADMINロールはワイルドカードを返す() {
        final UserAuthorities authorities = UserAuthorities.of(
                List.of("ADMIN"), List.of("sys:user:list"));

        assertThat(authorities.isSuperAdmin()).isTrue();
        assertThat(authorities.effectivePermissionCodes())
                .containsExactly(UserAuthorities.WILDCARD);
    }

    @Test
    void SUPER_ADMINロールもワイルドカードを返す() {
        final UserAuthorities authorities = UserAuthorities.of(
                List.of("SUPER_ADMIN"), List.of());

        assertThat(authorities.effectivePermissionCodes())
                .containsExactly(UserAuthorities.WILDCARD);
    }

    @Test
    void Authority表現はロールにROLE_接頭辞を付与する() {
        final UserAuthorities authorities = UserAuthorities.of(
                List.of("EDITOR"), List.of("sys:user:list"));

        assertThat(authorities.toGrantedAuthorities())
                .containsExactlyInAnyOrder("ROLE_EDITOR", "sys:user:list");
    }

    /**
     * 画面制御と API 認可が同じ結論に到達することの担保。
     * 特権ロール保持者の Authority にはワイルドカードが含まれ、
     * DynamicAuthorizationManager 側の "*" 判定と一致する。
     */
    @Test
    void 特権ロールのAuthorityにはワイルドカードが含まれる() {
        final UserAuthorities authorities = UserAuthorities.of(
                List.of("ADMIN"), List.of("sys:user:list"));

        assertThat(authorities.toGrantedAuthorities())
                .contains("ROLE_ADMIN", UserAuthorities.WILDCARD)
                .doesNotContain("sys:user:list");
    }

    @Test
    void nullや空白のコードは除去される() {
        final UserAuthorities authorities = UserAuthorities.of(
                java.util.Arrays.asList("EDITOR", null, "  "),
                java.util.Arrays.asList(" sys:user:list ", null, ""));

        assertThat(authorities.roleCodes()).containsExactly("EDITOR");
        assertThat(authorities.permissionCodes()).containsExactly("sys:user:list");
    }

    @Test
    void ロールも権限も無い場合は空になる() {
        final UserAuthorities authorities = UserAuthorities.of(null, null);

        assertThat(authorities.isSuperAdmin()).isFalse();
        assertThat(authorities.effectivePermissionCodes()).isEmpty();
        assertThat(authorities.toGrantedAuthorities()).isEmpty();
    }
}
