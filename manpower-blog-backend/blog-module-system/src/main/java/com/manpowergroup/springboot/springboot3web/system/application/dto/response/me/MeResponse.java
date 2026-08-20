package com.manpowergroup.springboot.springboot3web.system.application.dto.response.me;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuTreeResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Me API レスポンス")
public record MeResponse(
        @Schema(description = "ログインユーザの情報")
        LoginUser user,

        @Schema(description = "ユーザがアクセス可能なメニューのツリー構造")
        List<MenuTreeResponse> menus,

        @Schema(description = "ユーザが持つ権限コードの一覧")
        List<String> permissions
) {
}
