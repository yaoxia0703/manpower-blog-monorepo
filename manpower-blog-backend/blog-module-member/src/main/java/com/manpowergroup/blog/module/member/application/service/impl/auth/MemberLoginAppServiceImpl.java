package com.manpowergroup.blog.module.member.application.service.impl.auth;

import com.manpowergroup.blog.module.member.application.command.auth.LoginCommand;
import com.manpowergroup.blog.module.member.application.dto.response.auth.LoginMember;
import com.manpowergroup.blog.module.member.application.service.auth.MemberLoginAppService;
import com.manpowergroup.blog.module.member.domain.model.member.Member;
import com.manpowergroup.blog.module.member.domain.model.member.MemberAccount;
import com.manpowergroup.blog.module.member.domain.model.member.MemberProfile;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberAccountRepository;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberProfileRepository;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberRepository;
import com.manpowergroup.blog.module.member.domain.service.PasswordEncryptor;
import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * 会員ログインのユースケース。
 *
 * <p>認証の判定そのものは {@link MemberAccount} が持つ。本クラスは
 * 必要な集約を集めて渡し、結果を表示用のDTOへ組み立てる役割に留める。</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberLoginAppServiceImpl implements MemberLoginAppService {

    private final MemberRepository memberRepository;
    private final PasswordEncryptor passwordEncryptor;
    private final MemberAccountRepository accountRepository;
    private final MemberProfileRepository profileRepository;
    private final Clock clock;

    @Override
    @Transactional
    public LoginMember login(LoginCommand command) {
        // accountValue はメールアドレス・電話番号であり個人情報のため出力しない
        log.info("会員ログインを試行します。accountType={}", command.accountType());

        // アカウントが存在しない場合もパスワード誤りと同一の応答を返す。
        // 区別すると、登録済みのメールアドレスを外部から判別できるため。
        final MemberAccount account = accountRepository
                .findByAccountTypeAndValue(command.accountType(), command.accountValue())
                .orElseThrow(() -> BizException.withDetail(
                        ErrorCode.UNAUTHORIZED, "アカウントまたはパスワードが正しくありません"));

        // アカウントが存在するのに会員が引けないのはデータ不整合であり、
        // 利用者の入力誤りではない。認証失敗として扱うと障害が隠れる。
        final Member member = memberRepository.findById(account.getMemberId())
                .orElseThrow(() -> BizException.withDetail(ErrorCode.SERVER_ERROR,
                        "会員が存在しません。memberId=" + account.getMemberId()));

        account.authenticate(command.password(), member, passwordEncryptor);

        account.recordLogin(LocalDateTime.now(clock));
        accountRepository.update(account);

        // t_member_profile は t_member と1対1のため、存在しないのは不整合である。
        final MemberProfile profile = profileRepository.findByMemberId(member.getId())
                .orElseThrow(() -> BizException.withDetail(ErrorCode.SERVER_ERROR,
                        "会員プロフィールが存在しません。memberId=" + member.getId()));

        log.info("会員ログインに成功しました。memberId={}, accountId={}",
                member.getId(), account.getId());

        return new LoginMember(
                member.getId(),
                account.getId(),
                profile.getDisplayName(),
                profile.getHandle(),
                profile.getAvatarUrl()
        );
    }
}
