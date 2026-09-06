package com.manpowergroup.blog.module.member.infrastructure.persistence.repository.member;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.manpowergroup.blog.module.member.domain.model.member.MemberAccount;
import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberAccountRepository;
import com.manpowergroup.blog.module.member.infrastructure.persistence.mapper.member.MemberAccountMapper;
import com.manpowergroup.blog.shared.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberAccountRepositoryImpl implements MemberAccountRepository {

    private final MemberAccountMapper memberAccountMapper;

    @Override
    public Optional<MemberAccount> findByAccountId(Long accountId) {
        return Optional.ofNullable(memberAccountMapper.selectById(accountId));
    }

    @Override
    public boolean existsByAccountTypeAndAccountValue(MemberAccountType accountType, String accountValue) {
        return memberAccountMapper.selectCount(
                Wrappers.<MemberAccount>lambdaQuery()
                        .eq(MemberAccount::getAccountType, accountType)
                        .eq(MemberAccount::getAccountValue, accountValue)) > 0;
    }

    @Override
    public void create(MemberAccount memberAccount) {
        memberAccountMapper.insert(memberAccount);
    }

    @Override
    public void delete(Long memberId) {
        memberAccountMapper.delete(
                Wrappers.<MemberAccount>lambdaQuery()
                        .eq(MemberAccount::getMemberId, memberId));
    }

    @Override
    public void changeStatusByMemberId(MemberAccount account) {
        memberAccountMapper.update(account, Wrappers.<MemberAccount>lambdaUpdate()
                .eq(MemberAccount::getMemberId, account.getMemberId())
                .set(MemberAccount::getStatus,account.getStatus()));
    }
}
