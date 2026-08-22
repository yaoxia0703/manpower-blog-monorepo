package com.manpowergroup.springboot.springboot3web.system.domain.model.role;

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
        Objects.requireNonNull(roleId, "ロールIDは必須です");
        menuIds = normalize(menuIds, "メニューID一覧は必須です");
        permissionIds = normalize(permissionIds, "権限ID一覧は必須です");
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

    private static Set<Long> normalize(Set<Long> ids, String message) {
        if (ids == null) {
            throw new IllegalArgumentException(message);
        }
        final LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        ids.stream().filter(Objects::nonNull).forEach(normalized::add);
        return Set.copyOf(normalized);
    }
}
