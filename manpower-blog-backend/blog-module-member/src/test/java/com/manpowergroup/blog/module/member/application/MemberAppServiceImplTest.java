package com.manpowergroup.blog.module.member.application;

import com.manpowergroup.blog.module.member.application.command.member.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberProfileUpdateCommand;
import com.manpowergroup.blog.module.member.application.service.impl.member.MemberAppServiceImpl;
import com.manpowergroup.blog.module.member.domain.model.member.Member;
import com.manpowergroup.blog.module.member.domain.model.member.MemberAccount;
import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;
import com.manpowergroup.blog.module.member.domain.model.member.MemberProfile;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberAccountRepository;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberProfileRepository;
import com.manpowergroup.blog.module.member.domain.repository.member.MemberRepository;
import com.manpowergroup.blog.module.member.domain.service.PasswordEncryptor;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;
import com.manpowergroup.blog.shared.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 会員登録・プロフィール更新のユースケースを検証する。
 *
 * <p>重点は認証方式の判定にある。アカウント種別とパスワードの対応を誤ると、
 * 外部認証アカウントにパスワードが設定され、本来の認証経路を迂回できる
 * 状態が生まれ得るため、種別ごとの分岐を網羅して固定する。</p>
 */
class MemberAppServiceImplTest {

    private static final Long MEMBER_ID = 1L;

    private static final String ENCODED_PASSWORD = "encoded-password";

    private final MemberRepository repository = mock(MemberRepository.class);
    private final MemberAccountRepository accountRepository = mock(MemberAccountRepository.class);
    private final MemberProfileRepository profileRepository = mock(MemberProfileRepository.class);
    private final PasswordEncryptor passwordEncryptor = mock(PasswordEncryptor.class);

    private final MemberAppServiceImpl service = new MemberAppServiceImpl(
            repository, accountRepository, profileRepository, passwordEncryptor);

    /**
     * 採番済みIDの払い出しを模倣する。
     *
     * <p>本番では MyBatis-Plus が insert 後に自増IDを書き戻す。
     * 集約は setter を持たないため、テストではリフレクションで同じ状態を作る。</p>
     */
    @BeforeEach
    void stubIdAssignment() {
        doAnswer(invocation -> {
            final Member member = invocation.getArgument(0);
            ReflectionTestUtils.setField(member, "id", MEMBER_ID);
            return null;
        }).when(repository).create(any(Member.class));

        when(passwordEncryptor.encrypt(anyString())).thenReturn(ENCODED_PASSWORD);
    }

    private static MemberCreateCommand commandOf(MemberAccountType accountType, String password) {
        return new MemberCreateCommand(
                Status.ENABLED, accountType, "john.doe@example.com", password,
                VerifiedStatus.VERIFIED, "John Doe");
    }

    private MemberAccount capturedAccount() {
        final ArgumentCaptor<MemberAccount> captor = ArgumentCaptor.forClass(MemberAccount.class);
        verify(accountRepository).create(captor.capture());
        return captor.getValue();
    }

    /* ============ 会員登録：パスワード認証 ============ */

    @Test
    void createEncryptsPasswordForLocalAccountType() {
        assertThat(service.create(commandOf(MemberAccountType.LOCAL_EMAIL, "raw-password")))
                .isEqualTo(MEMBER_ID);

        verify(passwordEncryptor).encrypt("raw-password");

        final MemberAccount account = capturedAccount();
        assertThat(account.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(account.getMemberId()).isEqualTo(MEMBER_ID);
    }

    /**
     * パスワード未指定は業務例外になることを保証する。
     *
     * <p>暗号化器へ null を渡すと実行時例外となり HTTP 500 で返る。
     * 入力不備は業務例外として扱うため、暗号化前に検証する。</p>
     */
    @Test
    void createRejectsMissingPasswordForLocalAccountType() {
        assertThatThrownBy(() -> service.create(commandOf(MemberAccountType.LOCAL_PHONE, null)))
                .isInstanceOf(BizException.class);

        verify(passwordEncryptor, never()).encrypt(anyString());
        verify(accountRepository, never()).create(any());
    }

    /* ============ 会員登録：外部認証 ============ */

    @Test
    void createOmitsPasswordForExternalAccountType() {
        assertThat(service.create(commandOf(MemberAccountType.GOOGLE, null)))
                .isEqualTo(MEMBER_ID);

        assertThat(capturedAccount().getPassword()).isNull();
        verify(passwordEncryptor, never()).encrypt(anyString());
    }

    /**
     * 外部認証にパスワードが渡された場合は拒否することを保証する。
     *
     * <p>受け入れると、外部認証アカウントが本来の認証経路を迂回して
     * ログインできる状態が生まれる。判定はアカウント種別のみで行うため、
     * 暗号化を実行する前に失敗する。</p>
     */
    @Test
    void createRejectsPasswordForExternalAccountType() {
        assertThatThrownBy(() -> service.create(commandOf(MemberAccountType.GITHUB, "raw-password")))
                .isInstanceOf(BizException.class);

        verify(passwordEncryptor, never()).encrypt(anyString());
        verify(accountRepository, never()).create(any());
    }

    /* ============ 会員登録：その他 ============ */

    @Test
    void createRejectsDuplicateAccountValue() {
        when(accountRepository.existsByAccountTypeAndAccountValue(
                MemberAccountType.LOCAL_EMAIL, "john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(commandOf(MemberAccountType.LOCAL_EMAIL, "raw-password")))
                .isInstanceOf(BizException.class);

        verify(repository, never()).create(any());
        verify(profileRepository, never()).create(any());
    }

    /**
     * 会員番号が集約内部で採番されることを保証する。
     *
     * <p>採番を呼び出し側へ移すと、書式違反や重複した番号を渡す余地が生まれる。</p>
     */
    @Test
    void createAssignsMemberNoWithoutCallerInput() {
        service.create(commandOf(MemberAccountType.LOCAL_EMAIL, "raw-password"));

        final ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(repository).create(captor.capture());
        assertThat(captor.getValue().getMemberNo().value()).hasSize(25).startsWith("M");
    }

    /** 登録時のプロフィールは表示名のみを持ち、任意項目は未設定であること。 */
    @Test
    void createRegistersProfileWithDisplayNameOnly() {
        service.create(commandOf(MemberAccountType.LOCAL_EMAIL, "raw-password"));

        final ArgumentCaptor<MemberProfile> captor = ArgumentCaptor.forClass(MemberProfile.class);
        verify(profileRepository).create(captor.capture());
        assertThat(captor.getValue().getDisplayName()).isEqualTo("John Doe");
        assertThat(captor.getValue().getHandle()).isNull();
    }

    /* ============ プロフィール更新 ============ */

    private static MemberProfileUpdateCommand updateCommandOf(String handle) {
        return new MemberProfileUpdateCommand(
                MEMBER_ID, "New Name", handle, null, null, null, null, null);
    }

    private MemberProfile existingProfileWithHandle() {
        final MemberProfile profile = MemberProfile.create(MEMBER_ID, "John Doe");
        profile.changeHandle("johndoe");
        when(profileRepository.findByMemberId(MEMBER_ID)).thenReturn(Optional.of(profile));
        return profile;
    }

    @Test
    void updateProfileSetsHandleWhenProvided() {
        final MemberProfile profile = existingProfileWithHandle();

        service.updateProfile(updateCommandOf("new-handle"));

        assertThat(profile.getHandle()).isEqualTo("new-handle");
        assertThat(profile.getDisplayName()).isEqualTo("New Name");
    }

    /**
     * handle 未指定は未設定へ戻すことを保証する。
     *
     * <p>無条件に {@code changeHandle} を呼ぶ実装では必須項目となり、
     * 一度設定した会員が取り消せなくなる。</p>
     */
    @Test
    void updateProfileClearsHandleWhenNull() {
        final MemberProfile profile = existingProfileWithHandle();

        service.updateProfile(updateCommandOf(null));

        assertThat(profile.getHandle()).isNull();
        verify(profileRepository).update(profile);
    }

    /** 空白のみの入力も未設定として扱い、DB上の表現を揃える。 */
    @Test
    void updateProfileClearsHandleWhenBlank() {
        final MemberProfile profile = existingProfileWithHandle();

        service.updateProfile(updateCommandOf("   "));

        assertThat(profile.getHandle()).isNull();
    }

    @Test
    void updateProfileRejectsUnknownMember() {
        when(profileRepository.findByMemberId(MEMBER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfile(updateCommandOf("new-handle")))
                .isInstanceOf(BizException.class);

        verify(profileRepository, never()).update(any());
    }
}
