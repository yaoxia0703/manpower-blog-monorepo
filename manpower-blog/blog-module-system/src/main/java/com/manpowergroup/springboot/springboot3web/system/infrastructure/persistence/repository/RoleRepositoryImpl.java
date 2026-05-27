package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.role.RoleMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;

    public RoleRepositoryImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public int deleteDeletedByCode(String code) {
        return roleMapper.deleteDeletedByCode(code);
    }
}
