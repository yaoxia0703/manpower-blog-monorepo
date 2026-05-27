package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.role;

import com.manpowergroup.springboot.springboot3web.system.domain.model.role.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * ロールマスタ Mapper
 * </p>
 *
 * @author YAOXIA
 * @since 2025-12-18
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    int deleteDeletedByCode(@Param("code") String code);
}
