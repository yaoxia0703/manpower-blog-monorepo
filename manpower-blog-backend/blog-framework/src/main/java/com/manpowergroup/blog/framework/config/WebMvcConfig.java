package com.manpowergroup.blog.framework.config;

import com.manpowergroup.blog.framework.web.CodedEnumConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC の追加設定。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * クエリパラメータの型変換規則を登録する。
     *
     * <p>登録しない場合、{@code CodedEnum} を実装した列挙は
     * クエリパラメータでは列挙名でしか解決できず、
     * 応答に含まれる数値コードをそのまま検索条件へ渡せない。</p>
     */
    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverterFactory(new CodedEnumConverterFactory());
    }
}
