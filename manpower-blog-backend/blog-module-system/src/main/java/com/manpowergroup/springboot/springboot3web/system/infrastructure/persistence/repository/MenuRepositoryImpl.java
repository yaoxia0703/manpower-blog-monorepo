package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.MenuRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.menu.MenuMapper;
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
    public List<Menu> findByIds(Collection<Long> ids) {
        return ids == null || ids.isEmpty() ? List.of() : menuMapper.selectBatchIds(ids);
    }

    @Override
    public List<Menu> findAll() {
        return menuMapper.selectList(orderedQuery());
    }

    @Override
    public List<Menu> findEnabled() {
        return menuMapper.selectList(orderedQuery().eq(Menu::getStatus, Status.ENABLED));
    }

    @Override
    public List<Menu> findEnabledDirectories() {
        return menuMapper.selectList(orderedQuery()
                .eq(Menu::getStatus, Status.ENABLED)
                .eq(Menu::getType, MenuType.DIRECTORY));
    }

    @Override
    public List<Menu> findByUserId(Long userId) {
        return menuMapper.selectMenusByUserId(userId);
    }

    @Override
    public void save(Menu menu) {
        menuMapper.insert(menu);
    }

    @Override
    public void update(Menu menu) {
        menuMapper.updateById(menu);
    }

    @Override
    public void deleteById(Long id) {
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
    public List<Long> findAllDescendantIds(Long parentId) {
        return menuMapper.selectAllDescendantIds(parentId);
    }

    @Override
    public void updateStatusBatch(List<Long> ids, Status status) {
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
