package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.permission;

import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 指定ユーザーに紐づく権限コード一覧を取得する
     */
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    List<ApiPermission> selectEnabledApiPermissions();

}
