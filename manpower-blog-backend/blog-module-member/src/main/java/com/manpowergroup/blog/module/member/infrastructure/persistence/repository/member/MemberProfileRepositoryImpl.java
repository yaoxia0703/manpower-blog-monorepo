package com.manpowergroup.blog.module.member.infrastructure.persistence.repository.member;

import com.manpowergroup.blog.module.member.domain.model.member.MemberProfile;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberProfileRepository;
import com.manpowergroup.blog.module.member.infrastructure.persistence.mapper.member.MemberProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberProfileRepositoryImpl implements MemberProfileRepository {


    private final MemberProfileMapper memberProfileMapper;

    @Override
    public Optional<MemberProfile> findByMemberId(Long memberId) {
        return Optional.ofNullable(memberProfileMapper.selectById(memberId));
    }

    @Override
    public void create(MemberProfile memberProfile) {
        memberProfileMapper.insert(memberProfile);
    }

    @Override
    public void update(MemberProfile memberProfile) {
        memberProfileMapper.updateById(memberProfile);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        memberProfileMapper.deleteById(memberId);
    }
}
