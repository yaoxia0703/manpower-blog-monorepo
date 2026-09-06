package com.manpowergroup.blog.module.system.application;

import com.manpowergroup.blog.module.system.application.assembler.UserAssembler;
import com.manpowergroup.blog.module.system.application.query.user.UserPageQuery;
import com.manpowergroup.blog.module.system.application.service.impl.UserAppServiceImpl;
import com.manpowergroup.blog.module.system.domain.repository.RoleRepository;
import com.manpowergroup.blog.module.system.domain.repository.UserAccountRepository;
import com.manpowergroup.blog.module.system.domain.repository.UserRepository;
import com.manpowergroup.blog.module.system.domain.repository.UserRoleRepository;
import com.manpowergroup.blog.module.system.domain.service.PasswordEncryptor;
import com.manpowergroup.blog.shared.api.PageResult;
import com.manpowergroup.blog.shared.config.PageProperties;
import com.manpowergroup.blog.shared.dto.PageRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ユーザー一覧のページング挙動を検証する。
 *
 * <p>ページ値の丸めは {@code PageQuery.clamped} の一箇所のみで行う。
 * かつては {@code PageRequest} の生成時にも上限を適用しており、
 * コード上の定数が設定値より先に働くため
 * {@code app.page.max-page-size} が反映されなかった。
 * 丸めが設定値に基づくことを本テストで固定する。</p>
 */
class UserAppServicePagingTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PageProperties pageProperties = new PageProperties();

    private final UserAppServiceImpl service = new UserAppServiceImpl(
            userRepository,
            mock(UserAccountRepository.class),
            mock(UserRoleRepository.class),
            mock(RoleRepository.class),
            mock(PasswordEncryptor.class),
            pageProperties
    );

    /**
     * 丸めが行われる地点が一箇所であることを、リクエストから通しで確認する。
     *
     * <p>{@code PageRequest} が値を素通しすることまで検証する。
     * ここで再び補正を入れると、設定値より先にコード上の定数が働き、
     * {@code app.page.max-page-size} が反映されなくなる。</p>
     */
    @Test
    void 上限を超えるページサイズは設定値へ丸められる() {
        pageProperties.setMaxPageSize(50);
        when(userRepository.count(any())).thenReturn(0L);

        final UserPageQuery query = UserAssembler.toQuery(new PageRequest(3L, 500L), null);

        // PageRequest は補正しない。丸めは PageQuery.clamped だけが担う
        assertThat(query.pageSize()).isEqualTo(500L);

        final PageResult<?> result = service.page(query);

        assertThat(result.pageSize()).isEqualTo(50);
        assertThat(result.pageNum()).isEqualTo(3);
    }

    @Test
    void 未指定のページ値は既定値で補われる() {
        when(userRepository.count(any())).thenReturn(0L);

        final PageResult<?> result =
                service.page(new UserPageQuery(null, null, null, null));

        assertThat(result.pageNum()).isEqualTo(pageProperties.getDefaultPageNum());
        assertThat(result.pageSize()).isEqualTo(pageProperties.getDefaultPageSize());
    }

    @Test
    void 負のページ値は既定値で補われる() {
        when(userRepository.count(any())).thenReturn(0L);

        final PageResult<?> result =
                service.page(new UserPageQuery(-1L, -1L, null, null));

        assertThat(result.pageNum()).isEqualTo(pageProperties.getDefaultPageNum());
        assertThat(result.pageSize()).isEqualTo(pageProperties.getDefaultPageSize());
    }
}
