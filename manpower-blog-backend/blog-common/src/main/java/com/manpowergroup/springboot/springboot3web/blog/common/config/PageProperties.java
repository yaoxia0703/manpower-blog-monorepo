package com.manpowergroup.springboot.springboot3web.blog.common.config;

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
}
