package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.PermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.permission.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    @Override
    public List<String> selectPermissionCodesByUserId(Long userId) {
        return permissionMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public List<Long> selectAllDescendantIds(Long parentId) {
        return permissionMapper.selectAllDescendantIds(parentId);
    }

    @Override
    public void updateStatusBatch(List<Long> ids, Status status) {
        permissionMapper.updateStatusBatch(ids, status);
    }
}