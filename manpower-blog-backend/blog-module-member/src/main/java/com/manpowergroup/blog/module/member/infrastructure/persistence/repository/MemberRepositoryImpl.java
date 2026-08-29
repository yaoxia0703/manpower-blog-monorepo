package com.manpowergroup.blog.module.member.infrastructure.persistence.repository;

import com.manpowergroup.blog.module.member.domain.model.Member;
import com.manpowergroup.blog.module.member.domain.repository.MemberRepository;
import com.manpowergroup.blog.module.member.infrastructure.persistence.mapper.MemberMapper;
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
    public void delete(Long id, Long accountId) {
        memberMapper.deleteById(id);
    }
}
