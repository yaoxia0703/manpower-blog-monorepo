package com.manpowergroup.blog.security;

import com.manpowergroup.blog.bootstrap.ManpowerBlogApplication;
import com.manpowergroup.blog.framework.security.authority.ApiPermission;
import com.manpowergroup.blog.framework.security.authority.PermissionRuleProvider;
import com.manpowergroup.blog.framework.security.authority.UserAuthorityProvider;
import com.manpowergroup.blog.framework.security.jwt.JwtTokenProvider;
import com.manpowergroup.blog.framework.security.jwt.TokenSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 認証主体（運用者 / 会員）の分離を HTTP 境界で検証する。
 *
 * <p>会員はポータルから自己登録できる。したがって「有効な署名を持つトークン」は
 * 攻撃者にとって取得コストが実質ゼロである。運用者面と会員面が署名鍵・issuer を
 * 共有している限り、会員トークンは運用者トークンと機械的に区別できない。
 * 本テストはその区別が成立していることを、内部構造ではなく
 * 外部から観測できる HTTP ステータスのみで表明する。</p>
 *
 * <p>assertion は面の分離方式（FilterChain の分割単位、認可方式）に依存しない。
 * 実装がどう変わっても、ここに書かれた期待値は変わらない。</p>
 *
 * <p>DB へは接続しない。権限の取得元は差し替えており、
 * 「権限を一切持たない主体でも到達できてしまうか」だけを見る。</p>
 */
@SpringBootTest(classes = ManpowerBlogApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrincipalIsolationTest {

    /**
     * 運用者IDと会員IDの衝突値。
     *
     * <p>t_sys_user.id と t_member.id は独立した採番であるため、
     * 同じ値が両方に存在し得る。分離が無い場合、会員 #1 のトークンが
     * 運用者 #1 として解釈される。</p>
     */
    private static final long COLLIDING_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserAuthorityProvider userAuthorityProvider;

    @MockitoBean
    private PermissionRuleProvider permissionRuleProvider;

    @BeforeEach
    void 権限ソースを空にする() {
        // 権限を一切持たない主体を作る。
        // それでも運用者向けエンドポイントへ到達できるならば、
        // 不足しているのは権限データではなく主体の分離そのものである。
        given(userAuthorityProvider.loadAuthorityCodes(anyLong())).willReturn(List.of());
        given(permissionRuleProvider.loadEnabledRules()).willReturn(List.<ApiPermission>of());
    }

    /**
     * 会員トークンでは運用者面のエンドポイントへ到達できない。
     *
     * <p>{@code /api/system/auth/logout} を対象にするのは、
     * DynamicAuthorizationManager の AUTHENTICATED_ONLY_PATHS に含まれており、
     * 権限コードを一切参照せず「認証済みか」だけで通過するため。
     * 権限データの不足ではなく認証主体の区別の欠如を、単独で観測できる。</p>
     */
    @Test
    @DisplayName("会員トークンは運用者エンドポイントで拒否される")
    void 会員トークンは運用者エンドポイントで拒否される() throws Exception {
        final String memberToken = mintTokenForMemberFace(COLLIDING_ID, COLLIDING_ID);

        mockMvc.perform(post("/api/system/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + memberToken))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 運用者トークンでは会員面のエンドポイントへ到達できない。
     *
     * <p>分離前は「認証は通り、認可で拒否される」(403) が、
     * 分離後は「そもそも認証が通らない」(401) となるべきである。
     * 拒否理由の違いが、鍵レベルで分離されているかどうかを示す。</p>
     */
    @Test
    @DisplayName("運用者トークンは会員エンドポイントで拒否される")
    void 運用者トークンは会員エンドポイントで拒否される() throws Exception {
        final String adminToken = mintTokenForAdminFace(COLLIDING_ID, COLLIDING_ID);

        mockMvc.perform(get("/api/member/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isUnauthorized());
    }

    /**
     * ポータルの匿名 GET は認証・認可で拒否されない。
     *
     * <p>面を分割する際、ポータルが誤って認証必須の面へ取り込まれると
     * 匿名の公開閲覧ができなくなる。その退行を検出する。</p>
     */
    @Test
    @DisplayName("ポータルの匿名GETは認証で拒否されない")
    void ポータルの匿名GETは認証で拒否されない() throws Exception {
        mockMvc.perform(get("/api/portal/ping"))
                .andExpect(status().isOk());
    }

    /**
     * 権限ルールが登録されていないパスは既定で拒否される。
     *
     * <p>面を分割する際、いずれの securityMatcher にも該当しないパスが
     * 素通りする事故を防ぐための回帰ガード。</p>
     */
    @Test
    @DisplayName("ルール未登録のパスは既定で拒否される")
    void ルール未登録のパスは既定で拒否される() throws Exception {
        final String adminToken = mintTokenForAdminFace(COLLIDING_ID, COLLIDING_ID);

        mockMvc.perform(get("/api/system/unmapped-resource")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    /**
     * 会員面向けのトークンを発行する。
     *
     * <p>現時点では運用者面と署名鍵・issuer を共有しているため、
     * 発行されるトークンは運用者トークンと機械的に区別が付かない。
     * この区別の欠如こそが本テストの検証対象である。</p>
     *
     * <p>面ごとの分離を実装した後は、本メソッドの実装のみを
     * 会員面の Provider へ差し替える。各テストの assertion は変更しない。</p>
     */
    private String mintTokenForMemberFace(long memberId, long accountId) {
        return jwtTokenProvider.generateToken(new TokenSubject(memberId, accountId));
    }

    /** 運用者面向けのトークンを発行する。 */
    private String mintTokenForAdminFace(long userId, long accountId) {
        return jwtTokenProvider.generateToken(new TokenSubject(userId, accountId));
    }

}
