package com.manpowergroup.blog.module.member.application.service.impl.member;

import com.manpowergroup.blog.module.member.application.command.account.MemberAccountChangePassworcCommand;
import com.manpowergroup.blog.module.member.application.command.account.MemberAccountCreateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.service.member.MemberAccountAppService;
import com.manpowergroup.blog.module.member.domain.model.member.Member;
import com.manpowergroup.blog.module.member.domain.model.member.MemberAccount;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberAccountRepository;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberRepository;
import com.manpowergroup.blog.module.member.domain.service.PasswordEncryptor;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.support.DomainGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberAccountAppServiceImpl implements MemberAccountAppService {

    private final MemberAccountRepository accountRepository;
    private final PasswordEncryptor passwordEncryptor;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void changeStatusByAccountId(Long accountId, Status status) {
        final MemberAccount account = getRequiredAccount(accountId);
        account.changeStatus(status);
        accountRepository.update(account);
        log.info("会員アカウントの状態を変更しました。accountId={}, status={}", accountId, status);

    }

    @Override
    @Transactional
    public Long createAccount(MemberAccountCreateCommand command) {
        final Member member = getRequiredMember(command.memberId());
        final MemberAccount account = createMemberAccount(member.getId(), command);
        accountRepository.create(account);
        log.info("会員アカウントを新規登録しました。memberId={},accountId={}", member.getId(), account.getId());
        return account.getId();
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId) {
        final MemberAccount account = getRequiredAccount(accountId);
        accountRepository.deleteByMemberId(accountId);
        log.info("会員アカウントを削除しました。accountId={}", accountId);
    }

    @Override
    @Transactional
    public void changePassword(MemberAccountChangePassworcCommand command) {
        final MemberAccount account = getRequiredAccount(command.accountId());
        account.matchedPassword(command.currentPassword(), passwordEncryptor);
        account.changePassword(passwordEncryptor.encrypt(command.newPassword()));
        accountRepository.update(account);
        log.info("会員アカウントのパスワードを変更しました。accountId={}", command.accountId());
    }


    private MemberAccount getRequiredAccount(Long accountId) {
        return accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("会員アカウントが存在しません。accountId=" + accountId));
    }

    private Member getRequiredMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("会員が存在しません。memberId=" + memberId));
    }

    /**
     * アカウント種別に応じた会員アカウントを生成する。
     *
     * <p>分岐はアカウント種別のみで判定する。パスワードの有無で判定すると、
     * 種別と認証方式の対応がドメインの外へ散らばり、
     * 外部認証に誤ってパスワードが渡された場合も不要なハッシュ計算を経てから
     * 失敗することになるため。</p>
     */
    private MemberAccount createMemberAccount(Long memberId, MemberAccountCreateCommand command) {
        if (!command.accountType().requiresPassword()) {
            DomainGuard.requireTrue(command.password() == null || command.password().isBlank(), "外部認証アカウントにはパスワードを指定できません");
            return MemberAccount.createWithExternalAuth(memberId, command.accountType(), command.accountValue(), command.verified(), command.status());
        }

        // 暗号化前に検証する。null のまま暗号化器へ渡すと業務例外ではなく実行時例外になる
        final String rawPassword = DomainGuard.requireText(command.password(), "パスワード");
        return MemberAccount.createWithPassword(memberId, command.accountType(), command.accountValue(), passwordEncryptor.encrypt(rawPassword), command.verified(), command.status());
    }
}
