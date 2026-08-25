package com.manpowergroup.blog.shared.config;

import com.manpowergroup.blog.shared.dto.PageLimits;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.page")
public class PageProperties {

    // デフォルトのページ番号
    private long defaultPageNum = 1;

    // デフォルトのページサイズ
    private long defaultPageSize = 10;

    // 最大ページサイズ
    private long maxPageSize = 100;

    /**
     * Spring 非依存の値オブジェクトへ変換する。
     *
     * <p>本クラスは {@code @ConfigurationProperties} であり Spring へ依存するため、
     * ページング値の生成側へそのまま渡すと依存が波及する。
     * 設定値のみを写し取って受け渡す。</p>
     */
    public PageLimits toLimits() {
        return new PageLimits(defaultPageNum, defaultPageSize, maxPageSize);
    }
}
