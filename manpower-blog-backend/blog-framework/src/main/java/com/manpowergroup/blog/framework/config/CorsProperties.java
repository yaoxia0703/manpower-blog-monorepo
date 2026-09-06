package com.manpowergroup.blog.framework.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS の設定値。
 *
 * <p>許可オリジンはデプロイ先ごとに異なる環境依存値であり、
 * コードに直接書くと本番環境でフロントエンドからの通信が遮断される。
 * 設定として外部化し、環境変数で上書きできるようにする。</p>
 *
 * <p>本クラスは {@code PageProperties} と同じく
 * {@code @Component} + {@code @ConfigurationProperties} の形をとる。
 * 設定クラスの記述様式をプロジェクト内で揃えるためであり、
 * ドメインモデルにおける setter 禁止の対象外とする。</p>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * 許可するオリジンの一覧。
     *
     * <p>設定ファイルではカンマ区切りで指定する。</p>
     *
     * <p>本プロジェクトは {@code allowCredentials=true} で動作するため、
     * ワイルドカード {@code "*"} は指定できない（実行時に例外となる）。
     * 全オリジンを許可したい場合は
     * {@code CorsConfiguration#setAllowedOriginPatterns} への切り替えが必要になる。</p>
     */
    private List<String> allowedOrigins = List.of();
}
