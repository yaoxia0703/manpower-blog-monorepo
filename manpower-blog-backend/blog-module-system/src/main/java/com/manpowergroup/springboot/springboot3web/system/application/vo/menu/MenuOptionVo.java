package com.manpowergroup.springboot.springboot3web.system.application.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "メニューオプションVO")
public record MenuOptionVo(
        @Schema(description = "ID")
        Long id,

        @Schema(description = "メニュー名")
        String name
) {
}
