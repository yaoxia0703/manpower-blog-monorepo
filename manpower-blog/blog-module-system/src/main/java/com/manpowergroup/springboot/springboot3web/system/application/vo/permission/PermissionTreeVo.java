package com.manpowergroup.springboot.springboot3web.system.application.vo.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.PermissionType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.blog.common.util.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "権限ツリーVO")
@Builder
public class PermissionTreeVo implements TreeNode<PermissionTreeVo> {

        @Schema(description = "ID")
        private Long id;

        @Schema(hidden = true)
        private Long parentId;

        @Schema(description = "権限名")
        private String name;

        @Schema(description = "権限制御コード")
        private String code;

        @Schema(description = "権限種別（1=MENU, 2=BUTTON, 3=API）")
        private PermissionType type;

        @Schema(description = "対象パス")
        private String path;

        @Schema(description = "HTTPメソッド")
        private HttpMethod method;

        @Schema(description = "ソート順")
        private Integer sort;

        @Schema(description = "権限ステータス")
        private Status status;

        @Schema(description = "権限作成日時")
        private LocalDateTime createdAt;

        @Schema(description = "権限更新日時")
        private LocalDateTime updatedAt;

        @Setter(AccessLevel.NONE)
        @Schema(description = "子権限リスト")
        private List<PermissionTreeVo> children;

        @Override
        public void setChildren(List<PermissionTreeVo> children) {
                this.children = children;
        }
}