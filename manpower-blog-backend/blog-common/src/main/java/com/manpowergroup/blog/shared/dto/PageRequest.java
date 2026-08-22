package com.manpowergroup.blog.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ページリクエストDTO")
public record PageRequest(
        @Schema(description = "ページ番号", example = "1")
        Long pageNum,

        @Schema(description = "1ページあたりの件数", example = "20")
        Long pageSize
) {
}
