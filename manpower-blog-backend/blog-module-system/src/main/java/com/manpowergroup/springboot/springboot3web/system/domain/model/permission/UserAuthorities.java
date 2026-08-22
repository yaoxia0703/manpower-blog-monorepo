package com.manpowergroup.springboot.springboot3web.system.domain.model.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.support.DomainGuard;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * ユーザーが保持するロールと権限をまとめた実効権限。
 *
 * <p>設計意図：
 * 「管理者ロールは全権限を持つ」という業務ルールは、画面のボタン制御（/me API）と
 * バックエンドの API 認可（DynamicAuthorizationManager）の双方が必要とする。
 * 以前はこのルールが Controller と framework の2箇所に、
 * 素のロールコードと ROLE_ 接頭辞付き Authority という異なる表現で重複していた。</p>
 *
 * <p>特権ロールを追加する際に片方だけ修正され、
 * 「画面にボタンは出るが API は 403」という不整合を生む危険があったため、
 * 実効権限の算出をこの値オブジェクトへ一元化する。</p>
 */
public record UserAuthorities(Set<String> roleCodes, Set<String> permissionCodes) {

    /** 全権限を表すワイルドカード。 */
    public static final String WILDCARD = "*";

    /** Spring Security のロール表現に用いる接頭辞。 */
    private static final String ROLE_PREFIX = "ROLE_";

    /** 全権限を持つ特権ロール。追加する場合はここだけを変更する。 */
    private static final Set<String> SUPER_ADMIN_ROLES = Set.of("SUPER_ADMIN", "ADMIN");

    public UserAuthorities {
        roleCodes = normalize(roleCodes);
        permissionCodes = normalize(permissionCodes);
    }

    /** ロールコードと権限コードから実効権限を生成する。 */
    public static UserAuthorities of(List<String> roleCodes, List<String> permissionCodes) {
        return new UserAuthorities(
                roleCodes == null ? Set.of() : new LinkedHashSet<>(roleCodes),
                permissionCodes == null ? Set.of() : new LinkedHashSet<>(permissionCodes)
        );
    }

    /** 全権限を持つ特権ロールを保持しているか判定する。 */
    public boolean isSuperAdmin() {
        return roleCodes.stream().anyMatch(SUPER_ADMIN_ROLES::contains);
    }

    /**
     * 画面制御・API認可の双方が参照する実効権限コード。
     *
     * <p>特権ロール保持者はワイルドカード1件を返し、
     * 個別権限の付与状況に関わらず全操作を許可する。</p>
     */
    public Set<String> effectivePermissionCodes() {
        return isSuperAdmin() ? Set.of(WILDCARD) : permissionCodes;
    }

    /**
     * Spring Security の Authority 表現へ変換する。
     *
     * <p>ロールには ROLE_ 接頭辞を付与し、実効権限コードをそのまま連結する。</p>
     */
    public List<String> toGrantedAuthorities() {
        return Stream.concat(
                        roleCodes.stream().map(code -> ROLE_PREFIX + code),
                        effectivePermissionCodes().stream())
                .distinct()
                .toList();
    }

    /** null・空白を除去し、順序を保った不変Setへ正規化する。 */
    private static Set<String> normalize(Set<String> values) {
        DomainGuard.requireNonNull(values, "コード一覧");
        final LinkedHashSet<String> normalized = values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
        return Set.copyOf(normalized);
    }
}
