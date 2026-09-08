package com.manpowergroup.blog.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * 時刻取得の設定。
 *
 * <p>現在時刻を {@link Clock} として Bean 化する。各所で
 * {@code LocalDateTime.now()} を直接呼ぶと固定時刻を注入できず、
 * 「ログイン日時が記録されること」のような時刻に依存する振る舞いを
 * テストで検証できないため。</p>
 *
 * <p>タイムゾーンは JVM 既定に委ねず明示する。既定に委ねると
 * 実行環境（開発端末は Asia/Tokyo、コンテナは UTC が多い）によって
 * 記録される時刻がずれるが、例外も警告も発生せず、
 * データ上の齟齬としてのみ現れるため発見が遅れる。</p>
 */
@Configuration
public class TimeConfig {

    /**
     * アプリケーション全体で使用する時計。
     *
     * <p>テストでは {@code Clock.fixed()} を登録して差し替える。</p>
     *
     * @param zoneId タイムゾーンID（既定は Asia/Tokyo）
     */
    @Bean
    public Clock clock(@Value("${app.time.zone:Asia/Tokyo}") String zoneId) {
        return Clock.system(ZoneId.of(zoneId));
    }
}
