package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.role;

import com.manpowergroup.springboot.springboot3web.system.domain.model.role.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * ロールメニュー関連テーブル Mapper
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {


    List<RoleMenu> selectAllByRoleIdIncludeDeleted(@Param("roleId") Long roleId);

    int restoreMenus(@Param("roleId") Long roleId,
                     @Param("menuIds") Collection<Long> menuIds,
                     @Param("now") LocalDateTime now);

    int logicalDeleteMenus(@Param("roleId") Long roleId,
                           @Param("menuIds") Collection<Long> menuIds,
                           @Param("now") LocalDateTime now);

}
