package com.manpowergroup.blog.module.member.infrastructure.persistence.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manpowergroup.blog.module.member.domain.model.member.MemberProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberProfileMapper extends BaseMapper<MemberProfile> {

}
