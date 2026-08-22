package com.manpowergroup.blog.module.system.domain.repository;

import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.module.system.domain.model.menu.Menu;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * メニュー永続化ポート。
 */
public interface MenuRepository {

    Optional<Menu> findById(Long id);

    List<Menu> listByIds(Collection<Long> ids);

    List<Menu> list();

    List<Menu> listEnabled();

    List<Menu> listEnabledDirectories();

    List<Menu> listByUserId(Long userId);

    void create(Menu menu);

    void update(Menu menu);

    void delete(Long id);

    boolean existsByParentIdAndName(Long parentId, String name, Long excludeId);

    boolean existsByPath(String path, Long excludeId);

    long countByParentId(Long parentId);

    List<Long> listDescendantIds(Long parentId);

    void changeStatusBatch(List<Long> ids, Status status);
}
