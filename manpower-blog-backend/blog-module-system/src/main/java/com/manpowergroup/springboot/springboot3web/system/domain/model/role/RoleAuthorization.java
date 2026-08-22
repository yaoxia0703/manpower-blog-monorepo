package com.manpowergroup.springboot.springboot3web.system.domain.model.role;

import com.manpowergroup.springboot.springboot3web.blog.common.support.DomainGuard;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * ロールに付与するメニューと権限を一体として扱う認可設定。
 */
public record RoleAuthorization(
        Long roleId,
        Set<Long> menuIds,
        Set<Long> permissionIds
) {
    public RoleAuthorization {
        DomainGuard.requireNonNull(roleId, "ロールID");
        menuIds = normalizeIds(menuIds, "メニューID一覧");
        permissionIds = normalizeIds(permissionIds, "権限ID一覧");
    }

    /** 認可設定を生成する。 */
    public static RoleAuthorization create(
            Long roleId,
            Collection<Long> menuIds,
            Collection<Long> permissionIds
    ) {
        return new RoleAuthorization(
                roleId,
                menuIds == null ? null : new LinkedHashSet<>(menuIds),
                permissionIds == null ? null : new LinkedHashSet<>(permissionIds)
        );
    }

    /**
     * ID一覧を検証し、null要素を除いた不変Setへ正規化する。
     * 空Setは「全解除」を意味するため許容し、null のみ不正入力として扱う。
     */
    private static Set<Long> normalizeIds(Set<Long> ids, String fieldName) {
        DomainGuard.requireNonNull(ids, fieldName);
        final LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        ids.stream().filter(Objects::nonNull).forEach(normalized::add);
        return Set.copyOf(normalized);
    }
}
