package com.manpowergroup.springboot.springboot3web.system.application.vo.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Schema(description = "API権限VO")
@Builder
public class PermissionVo {

        @Schema(description = "ID")
        private Long id;

        @Schema(description = "所属メニューID（未所属の場合はnull）")
        private Long menuId;

        @Schema(description = "所属メニュー名（未所属の場合はnull）")
        private String menuName;

        @Schema(description = "権限名")
        private String name;

        @Schema(description = "権限制御コード")
        private String code;

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

}
