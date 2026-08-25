package com.manpowergroup.blog.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI（Swagger UI）の設定。
 */
@Configuration
public class SwaggerConfig {

    /** セキュリティスキーム名。SecurityRequirement からの参照キーを兼ねる。 */
    private static final String BEARER_SCHEME = "bearerAuth";

    /**
     * API仕様と認証方式を定義する。
     *
     * <p>認証方式を {@code SecurityScheme} として宣言することで、
     * Swagger UI に Authorize ボタンが表示され、
     * 各リクエストへ自動的に {@code Authorization} ヘッダが付与される。
     * 宣言がない場合、保護されたエンドポイントは全て401となり
     * 画面から動作確認できない。</p>
     *
     * <p>{@code scheme("bearer")} を指定しているため、
     * Swagger UI 側で {@code Bearer } 接頭辞が自動付与される。
     * 入力欄にはトークン本体のみを貼り付ければよい
     * （{@code JwtAuthenticationFilter} は "Bearer " 付きを期待する）。</p>
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("マンパワーブログAPI")
                        .description("Spring Boot 3 + MyBatis-Plus + Vue3")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("ログインAPIで取得したトークン本体を貼り付けてください"
                                        + "（\"Bearer \" は自動で付与されます）")))
                // 全エンドポイントへ既定で適用する。
                // 認証不要なエンドポイントは各Controllerで @SecurityRequirements により解除できる。
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
