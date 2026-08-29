package com.manpowergroup.blog.module.member.application.service.impl;

import com.manpowergroup.blog.module.member.application.command.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.MemberUpdateCommand;
import com.manpowergroup.blog.module.member.application.service.MemberAppService;
import com.manpowergroup.blog.module.member.domain.model.Member;
import com.manpowergroup.blog.module.member.domain.model.MemberAccount;
import com.manpowergroup.blog.module.member.domain.model.MemberProfile;
import com.manpowergroup.blog.module.member.domain.repository.MemberAccountRepository;
import com.manpowergroup.blog.module.member.domain.repository.MemberProfileRepository;
import com.manpowergroup.blog.module.member.domain.repository.MemberRepository;
import com.manpowergroup.blog.module.member.domain.service.PasswordEncryptor;
import com.manpowergroup.blog.shared.enums.UserErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberAppServiceImpl implements MemberAppService {

    private final MemberRepository repository;
    private final MemberAccountRepository accountRepository;
    private final MemberProfileRepository profileRepository;
    private final PasswordEncryptor passwordEncryptor;


    @Override
    @Transactional
    public Long create(MemberCreateCommand command) {
        if (accountRepository.existsByAccountTypeAndAccountValue(command.accountType(), command.accountValue())) {
            throw BizException.withDetail(
                    UserErrorCode.ACCOUNT_ALREADY_EXISTS, "この会員IDは既に登録されています");
        }

        final Member member = Member.register(command.status(), LocalDate.now());
        repository.create(member);
        final MemberAccount memberAccount;
        if (command.password().isBlank()) {
            memberAccount = MemberAccount.createWithExternalAuth(
                    member.getId(),
                    command.accountType(),
                    command.accountValue(),
                    command.verified(),
                    command.status());
        } else {
            memberAccount = MemberAccount.createWithPassword(
                    member.getId(),
                    command.accountType(),
                    command.accountValue(),
                    passwordEncryptor.encrypt(command.password()),
                    command.verified(),
                    command.status()
            );
        }
        accountRepository.create(memberAccount);
        final MemberProfile memberProfile = MemberProfile.create(
                member.getId(),
                command.displayName()
        );
        profileRepository.create(memberProfile);

        log.info("会員を新規登録しました。memberId={},accountId={}", member.getId(), memberAccount.getId());
        return member.getId();
    }

    @Override
    @Transactional
    public void updateProfile(MemberUpdateCommand command) {
        final MemberProfile profile = getRequiredProfile(command.memberId());
        profile.changeDisplayName(command.displayName());
        profile.changeHandle(command.handle());
        profile.updateOptionalInfo(
                command.avatarUrl(),
                command.bio(),
                command.websiteUrl(),
                command.locale(),
                command.timezone()
        );
        profileRepository.update(profile);
        log.info("会員プロフィールを更新しました。memberId={}", command.memberId());
    }


    private Member getRequiredMember(Long memberId) {
        return repository.findById(memberId)
                .orElseThrow(() -> BizException.withDetail(
                        UserErrorCode.ACCOUNT_NOT_FOUND, "会員が見つかりません。memberId=" + memberId));
    }

    private MemberAccount getRequiredAccount(Long accountId, Long memberId) {
        return accountRepository.findByAccountId(accountId)
                .filter(account -> account.getMemberId().equals(memberId))
                .orElseThrow(() -> BizException.withDetail(
                        UserErrorCode.ACCOUNT_NOT_FOUND, "会員アカウントが見つかりません。accountId=" + accountId));
    }

    private MemberProfile getRequiredProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> BizException.withDetail(
                        UserErrorCode.ACCOUNT_NOT_FOUND, "会員プロフィールが見つかりません。memberId=" + memberId));
    }
}
