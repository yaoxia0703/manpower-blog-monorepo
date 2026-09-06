package com.manpowergroup.blog.module.member.infrastructure.persistence.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manpowergroup.blog.module.member.domain.model.member.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
