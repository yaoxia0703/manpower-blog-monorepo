package com.manpowergroup.blog.module.member.application.service.impl.member;

import com.manpowergroup.blog.module.member.application.command.member.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberProfileUpdateCommand;
import com.manpowergroup.blog.module.member.application.service.member.MemberAppService;
import com.manpowergroup.blog.module.member.domain.model.member.Member;
import com.manpowergroup.blog.module.member.domain.model.member.MemberAccount;
import com.manpowergroup.blog.module.member.domain.model.member.MemberProfile;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberAccountRepository;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberProfileRepository;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberRepository;
import com.manpowergroup.blog.module.member.domain.service.PasswordEncryptor;
import com.manpowergroup.blog.shared.enums.UserErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import com.manpowergroup.blog.shared.support.DomainGuard;
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

        final MemberAccount memberAccount = createMemberAccount(member.getId(), command);
        accountRepository.create(memberAccount);

        final MemberProfile memberProfile = MemberProfile.create(
                member.getId(),
                command.displayName()
        );
        profileRepository.create(memberProfile);

        log.info("会員を新規登録しました。memberId={},accountId={}", member.getId(), memberAccount.getId());
        return member.getId();
    }

    /**
     * アカウント種別に応じた会員アカウントを生成する。
     *
     * <p>分岐はアカウント種別のみで判定する。パスワードの有無で判定すると、
     * 種別と認証方式の対応がドメインの外へ散らばり、
     * 外部認証に誤ってパスワードが渡された場合も不要なハッシュ計算を経てから
     * 失敗することになるため。</p>
     */
    private MemberAccount createMemberAccount(Long memberId, MemberCreateCommand command) {
        if (!command.accountType().requiresPassword()) {
            DomainGuard.requireTrue(
                    command.password() == null || command.password().isBlank(),
                    "外部認証アカウントにはパスワードを指定できません");
            return MemberAccount.createWithExternalAuth(
                    memberId,
                    command.accountType(),
                    command.accountValue(),
                    command.verified(),
                    command.status());
        }

        // 暗号化前に検証する。null のまま暗号化器へ渡すと業務例外ではなく実行時例外になる
        final String rawPassword = DomainGuard.requireText(command.password(), "パスワード");
        return MemberAccount.createWithPassword(
                memberId,
                command.accountType(),
                command.accountValue(),
                passwordEncryptor.encrypt(rawPassword),
                command.verified(),
                command.status());
    }

    @Override
    @Transactional
    public void updateProfile(MemberProfileUpdateCommand command) {
        final MemberProfile profile = getRequiredProfile(command.memberId());
        profile.changeDisplayName(command.displayName());
        applyHandle(profile, command.handle());
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

    @Override
    public void delete(Long memberId) {

        accountRepository.delete(memberId);
        profileRepository.deleteByMemberId(memberId);
        repository.delete(memberId);

        log.info("会員を削除しました。memberId={}", memberId);
    }

    /**
     * 公開用ユーザー名を設定、または未設定へ戻す。
     *
     * <p>handle は任意項目であり、未入力は「設定しない」を意味する。
     * 無条件に {@code changeHandle} を呼ぶと必須項目になり、
     * 一度設定した会員が取り消せなくなるため、空入力は明示的に未設定へ戻す。</p>
     */
    private static void applyHandle(MemberProfile profile, String handle) {
        if (DomainGuard.normalizeText(handle) == null) {
            profile.clearHandle();
            return;
        }
        profile.changeHandle(handle);
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
