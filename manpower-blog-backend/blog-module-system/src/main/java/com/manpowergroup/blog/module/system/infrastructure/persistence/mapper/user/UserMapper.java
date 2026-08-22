package com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manpowergroup.blog.module.system.domain.model.user.User;
import com.manpowergroup.blog.module.system.domain.model.user.UserProfile;
import com.manpowergroup.blog.module.system.domain.model.user.UserSearchCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    IPage<UserProfile> selectUserPage(
            Page<UserProfile> page,
            @Param("criteria") UserSearchCriteria criteria
    );

    UserProfile getUserDetail(
            @Param("userId") Long userId,
            @Param("accountId") Long accountId
    );
}
