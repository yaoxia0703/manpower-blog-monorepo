package com.manpowergroup.blog.module.member.domain.service;

/**
 * パスワードの暗号化・照合を抽象化するドメインサービス。
 *
 * <p>設計意図：
 * パスワード照合は会員ログインの中核ビジネスルールだが、実際のハッシュ方式（BCrypt 等）は
 * インフラの関心事である。ドメイン層にポートとして定義し、実装は infrastructure 層の
 * アダプタが framework の実装へ委譲することで、依存方向を
 * infrastructure -&gt; domain に保つ。</p>
 *
 * <p>system モジュールの同名ポートとは共有しない。共有すると
 * 会員側の認証要件（外部認証の追加など）の変更が運用者側の認証へ波及し、
 * 意図的に分離した2つの認証経路が再び結合するため。</p>
 */
public interface PasswordEncryptor {

    /**
     * 平文パスワードを暗号化する。
     *
     * @param rawPassword 平文パスワード
     * @return 暗号化済みパスワード
     */
    String encrypt(String rawPassword);

    /**
     * 平文パスワードと暗号化済みパスワードを照合する。
     *
     * @param rawPassword     平文パスワード
     * @param encodedPassword 暗号化済みパスワード
     * @return 一致する場合 true
     */
    boolean matches(String rawPassword, String encodedPassword);
}
