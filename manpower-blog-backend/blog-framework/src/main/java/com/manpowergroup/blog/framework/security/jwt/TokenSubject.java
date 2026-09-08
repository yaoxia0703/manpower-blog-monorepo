package com.manpowergroup.blog.framework.security.jwt;

/**
 * JWTトークンに格納する認証主体の識別情報。
 *
 * <p>トークン発行に必要な最小限の情報のみを保持する。
 * 各業務モジュールのログインDTO（{@code LoginUser} 等）を直接受け取らないのは、
 * ログイン面が増えるたびにframework層のシグネチャが変わることを避けるため。
 * framework層は「誰がログインしたか」の業務的な意味を知る必要がない。</p>
 *
 * <p>IDは {@code long} で保持する。{@code Long} にすると実行時のnull検査が
 * 必要になるが、識別子が未設定のままトークンを発行し得る状況は存在しないため、
 * 型で排除する。</p>
 *
 * @param principalId 認証主体のID（トークンのsubjectとなる）
 * @param accountId   ログインアカウントID
 */
public record TokenSubject(long principalId, long accountId) {
}
