package com.manpowergroup.blog.module.member.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manpowergroup.blog.shared.support.DomainGuard;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 会員プロフィール。
 *
 * <p>{@code t_member} と1対1で対応し、{@code member_id} を主キーとする。
 * 論理削除フラグを持たないため、会員の退会時の扱いは
 * アプリケーション層で明示的に決める必要がある。</p>
 *
 * @author YAOXIA
 * @since 2026-08-24
 */
@Getter
@TableName("t_member_profile")
public class MemberProfile {

    // 会員ID（t_member.id と同値）
    @TableId("member_id")
    private Long memberId;

    // 表示名
    private String displayName;

    // 公開用ユーザー名（未設定可。設定時は有効レコード内で一意）
    private String handle;

    // アバターURL
    private String avatarUrl;

    // 自己紹介
    private String bio;

    // WebサイトURL
    private String websiteUrl;

    // 言語設定
    private String locale;

    // タイムゾーン
    private String timezone;

    // 作成日時
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 更新日時
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** MyBatis-Plus がインスタンス化するための既定コンストラクタ。 */
    protected MemberProfile() {
    }

    private MemberProfile(Long memberId, String displayName) {
        this.memberId = DomainGuard.requireNonNull(memberId, "会員ID");
        this.displayName = DomainGuard.requireText(displayName, "表示名");
    }

    /**
     * 新しいプロフィールを作成する。
     *
     * <p>必須項目は会員IDと表示名のみ。それ以外は会員自身が後から設定する。</p>
     */
    public static MemberProfile create(Long memberId, String displayName) {
        return new MemberProfile(memberId, displayName);
    }

    /** 表示名を変更する。 */
    public void changeDisplayName(String displayName) {
        this.displayName = DomainGuard.requireText(displayName, "表示名");
    }

    /**
     * 公開用ユーザー名を設定する。
     *
     * <p>一意性はDBの制約で担保する。永続化層への問い合わせが必要なため、
     * 重複確認は本メソッドの責務としない。</p>
     */
    public void changeHandle(String handle) {
        this.handle = DomainGuard.requireText(handle, "公開用ユーザー名");
    }

    /** 公開用ユーザー名を未設定に戻す。 */
    public void clearHandle() {
        this.handle = null;
    }

    /**
     * 任意項目をまとめて更新する。
     *
     * <p>いずれも未設定を許容するため null を受け付ける。
     * 空文字は未設定として扱い、DB上の表現を揃える。</p>
     */
    public void updateOptionalInfo(String avatarUrl, String bio, String websiteUrl,
                                   String locale, String timezone) {
        this.avatarUrl = DomainGuard.normalizeText(avatarUrl);
        this.bio = DomainGuard.normalizeText(bio);
        this.websiteUrl = DomainGuard.normalizeText(websiteUrl);
        this.locale = DomainGuard.normalizeText(locale);
        this.timezone = DomainGuard.normalizeText(timezone);
    }
}
