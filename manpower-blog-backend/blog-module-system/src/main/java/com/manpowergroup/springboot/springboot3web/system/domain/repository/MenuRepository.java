package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * メニュー永続化ポート。
 */
public interface MenuRepository {

    Optional<Menu> findById(Long id);

    List<Menu> findByIds(Collection<Long> ids);

    List<Menu> findAll();

    List<Menu> findEnabled();

    List<Menu> findEnabledDirectories();

    List<Menu> findByUserId(Long userId);

    void save(Menu menu);

    void update(Menu menu);

    void deleteById(Long id);

    boolean existsByParentIdAndName(Long parentId, String name, Long excludeId);

    boolean existsByPath(String path, Long excludeId);

    long countByParentId(Long parentId);

    List<Long> findAllDescendantIds(Long parentId);

    void updateStatusBatch(List<Long> ids, Status status);
}
