package com.manpowergroup.blog.module.system.domain.model.permission;

import com.manpowergroup.blog.shared.support.DomainGuard;

import java.util.regex.Pattern;

/**
 * 権限コード。
 *
 * <p>形式は {@code <ドメイン>:<リソース>:<動詞>[修飾語]}。</p>
 * <ul>
 *   <li>ドメイン：{@code system} / {@code content} / {@code member} のいずれか。
 *       API パス（接入面）ではなく、リソースが属するドメインを表す</li>
 *   <li>リソース：小文字で始まる英数字（lowerCamelCase）</li>
 *   <li>動詞：{@code list} / {@code create} / {@code update} / {@code delete} /
 *       {@code changeStatus} / {@code detail} のいずれか</li>
 *   <li>修飾語：任意。大文字で始まる英数字。派生した操作を基本動詞の後ろに付けて表す
 *       （例：{@code listEnabled}、{@code updateAuthorization}）</li>
 * </ul>
 *
 * <p>権限コードは画面のボタン制御と API 認可の双方で文字列一致により照合される。
 * 規約外のコードが登録されてもエラーは発生せず、「ボタンが表示されない」
 * 「API が 403 になる」という形でしか現れないため、生成時に拒否する。</p>
 *
 * <p>ドメインを列挙で制限するのは、略記や綴り誤りを受け付けると、
 * 同じドメインが複数の表記で存在する状態に戻るためである。ドメインを追加する場合は本クラスも更新する。</p>
 *
 * <p>永続化上は文字列のまま保持し、{@link Permission} の生成時にのみ本型で検証する。
 * 型として保持するには TypeHandler の追加と読み取りモデルの変更が必要になるが、
 * 権限コードは生成後に変更されない（{@link Permission#updateRule} は対象外）ため、
 * 生成時の検証で規約は保たれる。</p>
 *
 * @param value 権限コード
 */
public record PermissionCode(String value) {

    private static final Pattern FORMAT = Pattern.compile(
            "^(system|content|member)"
                    + ":[a-z][a-zA-Z0-9]*"
                    + ":(list|create|update|delete|changeStatus|detail)([A-Z][a-zA-Z0-9]*)?$");

    public PermissionCode {
        value = DomainGuard.requireText(value, "権限制御コード");
        DomainGuard.requireTrue(FORMAT.matcher(value).matches(),
                "権限制御コードの形式が不正です（<ドメイン>:<リソース>:<動詞>）: " + value);
    }

    /**
     * 文字列から権限コードを生成する。
     *
     * @param value 権限コード
     * @return 検証済みの権限コード
     */
    public static PermissionCode of(String value) {
        return new PermissionCode(value);
    }
}
