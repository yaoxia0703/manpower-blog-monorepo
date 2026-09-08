package com.manpowergroup.blog.framework.security.jwt;

/**
 * JWT認証後に SecurityContext へ格納する最小 principal。
 *
 * <p>{@code principalId} は種別によって指す実体が異なる。
 * USER なら {@code t_sys_user.id}、MEMBER なら {@code t_member.id} であり、
 * 両者は独立した採番のため同じ値が両方に存在し得る。
 * ID 単独では主体を特定できないため、必ず種別と組で扱う。</p>
 *
 * @param principalType 認証主体の種別
 * @param principalId   認証主体のID
 * @param accountId     ログインアカウントID
 */
public record LoginPrincipal(PrincipalType principalType, Long principalId, Long accountId) {

    /** 運用者面の principal か。 */
    public boolean isUser() {
        return principalType == PrincipalType.USER;
    }

    /** 会員面の principal か。 */
    public boolean isMember() {
        return principalType == PrincipalType.MEMBER;
    }
}
