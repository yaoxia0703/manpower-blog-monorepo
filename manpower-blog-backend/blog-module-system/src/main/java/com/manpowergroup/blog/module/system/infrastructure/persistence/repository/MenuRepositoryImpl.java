package com.manpowergroup.blog.module.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.blog.shared.enums.MenuType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.module.system.domain.model.menu.Menu;
import com.manpowergroup.blog.module.system.domain.repository.MenuRepository;
import com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.menu.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuMapper menuMapper;

    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.ofNullable(menuMapper.selectById(id));
    }

    @Override
    public List<Menu> listByIds(Collection<Long> ids) {
        return ids == null || ids.isEmpty() ? List.of() : menuMapper.selectBatchIds(ids);
    }

    @Override
    public List<Menu> list() {
        return menuMapper.selectList(orderedQuery());
    }

    @Override
    public List<Menu> listEnabled() {
        return menuMapper.selectList(orderedQuery().eq(Menu::getStatus, Status.ENABLED));
    }

    @Override
    public List<Menu> listEnabledDirectories() {
        return menuMapper.selectList(orderedQuery()
                .eq(Menu::getStatus, Status.ENABLED)
                .eq(Menu::getType, MenuType.DIRECTORY));
    }

    @Override
    public List<Menu> listByUserId(Long userId) {
        return menuMapper.selectMenusByUserId(userId);
    }

    @Override
    public void create(Menu menu) {
        menuMapper.insert(menu);
    }

    @Override
    public void update(Menu menu) {
        menuMapper.updateById(menu);
    }

    @Override
    public void delete(Long id) {
        menuMapper.deleteById(id);
    }

    @Override
    public boolean existsByParentIdAndName(Long parentId, String name, Long excludeId) {
        return menuMapper.exists(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, parentId)
                .eq(Menu::getName, name)
                .ne(excludeId != null, Menu::getId, excludeId));
    }

    @Override
    public boolean existsByPath(String path, Long excludeId) {
        if (path == null || path.isBlank()) {
            return false;
        }
        return menuMapper.exists(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPath, path)
                .ne(excludeId != null, Menu::getId, excludeId));
    }

    @Override
    public long countByParentId(Long parentId) {
        return menuMapper.selectCount(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, parentId));
    }

    @Override
    public List<Long> listDescendantIds(Long parentId) {
        return menuMapper.selectAllDescendantIds(parentId);
    }

    @Override
    public void changeStatusBatch(List<Long> ids, Status status) {
        if (ids != null && !ids.isEmpty()) {
            menuMapper.updateStatusBatch(ids, status);
        }
    }

    private LambdaQueryWrapper<Menu> orderedQuery() {
        return new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getSort)
                .orderByAsc(Menu::getId);
    }
}
