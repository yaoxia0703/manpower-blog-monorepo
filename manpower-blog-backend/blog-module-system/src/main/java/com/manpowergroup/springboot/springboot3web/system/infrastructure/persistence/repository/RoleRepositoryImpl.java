package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.springboot.springboot3web.system.domain.model.role.Role;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.role.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;

    @Override
    public List<Role> list() {
        return roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .orderByAsc(Role::getSort)
                .orderByDesc(Role::getUpdatedAt));
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(roleMapper.selectById(id));
    }

    @Override
    public List<Role> listByIds(Collection<Long> ids) {
        return ids == null || ids.isEmpty() ? List.of() : roleMapper.selectBatchIds(ids);
    }

    @Override
    public void create(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        roleMapper.updateById(role);
    }

    @Override
    public void delete(Long id) {
        roleMapper.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code, Long excludeId) {
        return roleMapper.exists(new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, code)
                .ne(excludeId != null, Role::getId, excludeId));
    }
}
