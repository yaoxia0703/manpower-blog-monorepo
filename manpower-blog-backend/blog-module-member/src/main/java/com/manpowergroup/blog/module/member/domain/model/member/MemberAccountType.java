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

    LOCAL_EMAIL("LOCAL_EMAIL", true),
    LOCAL_PHONE("LOCAL_PHONE", true),
    GOOGLE("GOOGLE", false),
    GITHUB("GITHUB", false);

    @EnumValue
    private final String code;

    private final boolean passwordRequired;

    MemberAccountType(String code, boolean passwordRequired) {
        this.code = code;
        this.passwordRequired = passwordRequired;
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
}
