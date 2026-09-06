package com.manpowergroup.blog.framework.security;

import com.manpowergroup.blog.framework.config.CorsProperties;
import com.manpowergroup.blog.framework.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

/**
 * セキュリティ設定クラス
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DynamicAuthorizationManager dynamicAuthorizationManager;
    private final CorsProperties corsProperties;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            DynamicAuthorizationManager dynamicAuthorizationManager,
            CorsProperties corsProperties
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.dynamicAuthorizationManager = dynamicAuthorizationManager;
        this.corsProperties = corsProperties;
    }

    /**
     * SecurityFilterChain 設定
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF無効化
                .csrf(AbstractHttpConfigurer::disable)

                // CORS設定適用
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // セッションを使用しない（JWT前提）
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 認証・認可エラーハンドリング
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
                )

                // アクセス制御
                .authorizeHttpRequests(auth -> auth
                        // 認証不要
                        .requestMatchers(HttpMethod.POST, "/api/system/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/portal/**").permitAll()

                        // 基本リソース
                        .requestMatchers(
                                "/error/**",
                                "/favicon.ico",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()

                        // その他はすべて DB 権限ルールで判定（ルールなしは拒否）
                        .anyRequest().access(dynamicAuthorizationManager)
                )

                        // JWTフィルタ適用
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS設定
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

        CorsConfiguration configuration = new CorsConfiguration();

        // フロントエンド許可オリジン
        configuration.setAllowedOrigins(allowedOrigins);

        // 許可HTTPメソッド
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 許可ヘッダー
        configuration.setAllowedHeaders(List.of("*"));

        // Cookie / Authorization許可
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
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
