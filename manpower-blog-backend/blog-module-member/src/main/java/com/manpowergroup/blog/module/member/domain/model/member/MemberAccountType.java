package com.manpowergroup.blog.module.member.domain.model.member;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 会員ログインアカウントの種別。
 *
 * <p>運用者側の {@code AccountType} とは共有しない。
 * 共有すると {@code UserAccount} が GOOGLE 等の外部認証種別を
 * 取り得ることになり、存在し得ない状態が表現可能になるため。</p>
 */
@Getter
@Schema(description = "会員アカウント種別")
public enum MemberAccountType {

    LOCAL_EMAIL("LOCAL_EMAIL", true, true),
    LOCAL_PHONE("LOCAL_PHONE", true, true),
    GOOGLE("GOOGLE", false, false),
    GITHUB("GITHUB", false, false);

    @EnumValue
    private final String code;

    private final boolean passwordRequired;

    private final boolean selfRegistrable;

    MemberAccountType(String code, boolean passwordRequired, boolean selfRegistrable) {
        this.code = code;
        this.passwordRequired = passwordRequired;
        this.selfRegistrable = selfRegistrable;
    }

    /**
     * パスワード認証を用いる種別か。
     *
     * <p>外部認証はパスワードを持たない。種別ごとの判定をここへ集約することで、
     * 種別追加時に検証ロジックを探し回らずに済む。</p>
     */
    public boolean requiresPassword() {
        return passwordRequired;
    }

    /**
     * 利用者自身の入力だけで登録できる種別か。
     *
     * <p>外部認証の識別子は、外部プロバイダの認証を経て初めて本人のものと言える。
     * 自己登録で受け付けると、他人の外部アカウント識別子を名乗る会員を作成できるため、
     * 外部認証の種別は外部認証フロー経由でのみ登録する。</p>
     *
     * <p>{@link #requiresPassword()} と現状は一致するが、意味が異なるため独立させる。
     * 例えばパスワードを持たないメールリンク認証を追加した場合、両者は分岐する。</p>
     */
    public boolean isSelfRegistrable() {
        return selfRegistrable;
    }
}
