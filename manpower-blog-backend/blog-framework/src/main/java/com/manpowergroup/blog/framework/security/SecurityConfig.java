package com.manpowergroup.blog.framework.security;

import com.manpowergroup.blog.framework.config.CorsProperties;
import com.manpowergroup.blog.framework.security.authority.UserAuthorityProvider;
import com.manpowergroup.blog.framework.security.jwt.JwtAuthenticationFilter;
import com.manpowergroup.blog.framework.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Set;

/**
 * セキュリティ設定クラス。
 *
 * <p>面ごとに独立した {@link SecurityFilterChain} を構成する。
 * 単一チェーン内でパス条件により分岐させる方式を採らないのは、
 * 条件の記述漏れが「拒否されるべきリクエストが通る」という形で
 * 静かに現れるため。チェーンを分ければ、担当外のトークンは
 * そもそも検証を通らない。</p>
 *
 * <p>チェーンの順序は securityMatcher の評価順を決める。
 * 最後の既定チェーンは、いずれの面にも属さないパスを受け止める。
 * ここが permitAll になると新規パスが無防備に公開されるため、
 * 明示的に拒否する。</p>
 */
@Configuration
public class SecurityConfig {

    private static final String ADMIN_LOGIN_PATH = "/api/system/auth/login";
    private static final String MEMBER_LOGIN_PATH = "/api/member/auth/login";

    private final DynamicAuthorizationManager dynamicAuthorizationManager;
    private final CorsProperties corsProperties;
    private final JwtTokenProvider adminJwtTokenProvider;
    private final JwtTokenProvider memberJwtTokenProvider;
    private final UserAuthorityProvider userAuthorityProvider;

    public SecurityConfig(
            DynamicAuthorizationManager dynamicAuthorizationManager,
            CorsProperties corsProperties,
            JwtTokenProvider adminJwtTokenProvider,
            JwtTokenProvider memberJwtTokenProvider,
            UserAuthorityProvider userAuthorityProvider
    ) {
        this.dynamicAuthorizationManager = dynamicAuthorizationManager;
        this.corsProperties = corsProperties;
        this.adminJwtTokenProvider = adminJwtTokenProvider;
        this.memberJwtTokenProvider = memberJwtTokenProvider;
        this.userAuthorityProvider = userAuthorityProvider;
    }

    /**
     * ポータル面。匿名の公開閲覧のみを許可する。
     *
     * <p>JWTフィルタを適用しない。ポータルは認証主体を生成しないため、
     * フィルタが無いこと自体が「ここでは principal が存在し得ない」ことを保証する。</p>
     *
     * <p>GET 以外は拒否する。面全体を permitAll にすると更新系まで公開されるが、
     * その退行はエラーを伴わないため、メソッド単位で明示する。</p>
     */
    @Bean
    @Order(1)
    public SecurityFilterChain portalFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/portal/**");
        applyCommon(http);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                .anyRequest().denyAll()
        );

        return http.build();
    }

    /**
     * 会員面。ログイン以外は認証済みであることのみを要求する。
     *
     * <p>{@link DynamicAuthorizationManager} を適用しない。同マネージャが参照する
     * 権限ルールは運用者の権限体系（t_sys_*）であり、会員は該当するルールを持たない。
     * 適用すると既定拒否により全ての会員リクエストが遮断される。</p>
     *
     * <p>認証済みであることは「正当な会員である」ことしか保証しない。
     * 「その会員本人のデータか」は認可設定では表現できないため、
     * 会員IDは常に principal から取得し、リクエストから受け取らないこと。</p>
     */
    @Bean
    @Order(2)
    public SecurityFilterChain memberFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/member/**");
        applyCommon(http);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, MEMBER_LOGIN_PATH).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
        );

        http.addFilterBefore(
                new JwtAuthenticationFilter(
                        memberJwtTokenProvider,
                        // 会員は権限体系を持たない。認可は認証済みか否かのみで判定する。
                        memberId -> List.of(),
                        Set.of(MEMBER_LOGIN_PATH)),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 運用者面。ログイン以外は DB の権限ルールで判定する。
     */
    @Bean
    @Order(3)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/system/**");
        applyCommon(http);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, ADMIN_LOGIN_PATH).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // ルールなしは拒否（default deny）
                .anyRequest().access(dynamicAuthorizationManager)
        );

        http.addFilterBefore(
                new JwtAuthenticationFilter(
                        adminJwtTokenProvider,
                        userAuthorityProvider,
                        Set.of(ADMIN_LOGIN_PATH)),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 既定チェーン。いずれの面にも属さないパスを受け止める。
     *
     * <p>API ドキュメントと死活監視のみを公開し、残りは拒否する。
     * ここを permitAll にすると、面の securityMatcher に追加し忘れた
     * 新規パスが無防備に公開される。</p>
     */
    @Bean
    @Order(4)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        applyCommon(http);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                        "/error/**",
                        "/favicon.ico",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/health"
                ).permitAll()
                .anyRequest().denyAll()
        );

        return http.build();
    }

    /**
     * 全チェーン共通の設定を適用する。
     *
     * <p>セッション・CSRF・CORS・エラー応答は面によって変える理由がないため、
     * 各チェーンで個別に記述せず一箇所に集約する。
     * 記述漏れによって面ごとに挙動が食い違うことを防ぐ。</p>
     */
    private void applyCommon(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":401,\"message\":\"認証エラー\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":403,\"message\":\"権限がありません\"}");
                        })
                );
    }

    /**
     * CORS設定。
     *
     * <p>許可オリジンは環境依存値のため設定から取得する。
     * 未設定のまま起動すると全てのクロスオリジン通信が拒否され、
     * 症状がフロントエンドからの疎通失敗としてしか現れないため、
     * 起動時点で明示的に失敗させる。</p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final List<String> allowedOrigins = corsProperties.getAllowedOrigins();
        if (allowedOrigins.isEmpty()) {
            throw new IllegalStateException("app.cors.allowed-origins が設定されていません");
        }

        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * パスワードエンコーダー
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
