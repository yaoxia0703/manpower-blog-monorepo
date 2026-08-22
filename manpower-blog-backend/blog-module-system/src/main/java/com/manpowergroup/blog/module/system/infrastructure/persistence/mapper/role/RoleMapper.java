package com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.role;

import com.manpowergroup.blog.module.system.domain.model.role.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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

}
