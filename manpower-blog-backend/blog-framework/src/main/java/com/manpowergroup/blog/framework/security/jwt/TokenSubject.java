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
 * <p>{@code principalType} は面ごとの署名鍵分離に対する二重の防御である。
 * 鍵が分かれていれば他面のトークンは検証段階で弾かれるが、
 * Controller が誤って別面の Provider を注入した場合は鍵だけでは検出できない。
 * 発行時に種別の一致を検査することで、その配線ミスを起動直後に顕在化させる。</p>
 *
 * @param principalType 認証主体の種別
 * @param principalId   認証主体のID（トークンのsubjectとなる）
 * @param accountId     ログインアカウントID
 */
public record TokenSubject(PrincipalType principalType, long principalId, long accountId) {

    public TokenSubject {
        if (principalType == null) {
            throw new IllegalArgumentException("認証主体の種別は必須です");
        }
    }
}
