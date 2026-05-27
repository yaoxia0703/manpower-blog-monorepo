package com.manpowergroup.springboot.springboot3web.blog.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.page")
public class PageProperties {

    private long defaultPageNum = 1;//デフォルトのページ番号
    private long defaultPageSize = 10;//デフォルトのページサイズ
    private long maxPageSize = 100;//最大ページサイズ
}
