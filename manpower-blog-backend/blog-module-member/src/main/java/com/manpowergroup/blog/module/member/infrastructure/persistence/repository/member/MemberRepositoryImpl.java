package com.manpowergroup.blog.module.member.infrastructure.persistence.repository.member;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.manpowergroup.blog.module.member.domain.model.member.Member;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberRepository;
import com.manpowergroup.blog.module.member.infrastructure.persistence.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberMapper memberMapper;

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(memberMapper.selectById(id));
    }

    @Override
    public void create(Member member) {
        memberMapper.insert(member);
    }

    @Override
    public void delete(Long id) {
        memberMapper.deleteById(id);
    }

    @Override
    public void changeStatus(Member member) {
        memberMapper.updateById(member);
    }
}
