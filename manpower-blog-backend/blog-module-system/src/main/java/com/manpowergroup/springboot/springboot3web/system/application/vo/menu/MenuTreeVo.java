package com.manpowergroup.springboot.springboot3web.system.application.vo.menu;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.util.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "メニューツリー")
public class MenuTreeVo  implements TreeNode<MenuTreeVo> {

    @Schema(description = "メニューID")
    private Long id;

    @Schema(description = "親メニューID")
    private Long parentId;

    @Schema(description = "メニュー名称")
    private String name;

    @Schema(description = "アイコン")
    private String icon;

    @Schema(description = "メニュー種別（1=ディレクトリ、2=メニュー、3=ボタン）")
    private MenuType type;

    @Schema(description = "表示順")
    private Integer sort;

    @Schema(description = "状態（0=無効、1=有効）")
    private Status status;

    @Schema(description = "対応するPermissionのパス（ルーティングマッチング用）")
    private String permissionPath;

    @Schema(description = "作成日時")
    private LocalDateTime createdAt;

    @Schema(description = "更新日時")
    private LocalDateTime updatedAt;

    @Schema(description = "子メニュー")
    private List<MenuTreeVo> children = new ArrayList<>();

}