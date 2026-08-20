package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
}
