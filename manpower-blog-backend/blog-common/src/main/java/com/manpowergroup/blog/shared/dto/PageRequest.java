package com.manpowergroup.blog.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ページリクエストDTO")
public record PageRequest(
        @Schema(description = "ページ番号", example = "1")
        Long pageNum,

        @Schema(description = "1ページあたりの件数", example = "20")
        Long pageSize
) {

        /** ページ番号の最小値。 */
        private static final long MIN_PAGE_NUM = 1L;

        /** 1ページあたりの既定件数。 */
        private static final long DEFAULT_PAGE_SIZE = 10L;

        /**
         * 1ページあたりの上限件数。
         *
         * <p>上限を設けない場合、pageSize に巨大な値を渡すことで
         * 全件取得と同等の負荷をかけられる。外部入力を信用しないため上限で丸める。</p>
         */
        private static final long MAX_PAGE_SIZE = 100L;

        /**
         * 生成時に値を正常化する。
         *
         * <p>不正値は例外ではなく既定値・上限値へ丸める。
         * 本レコードは HTTP パラメータのバインディング時に生成されるため、
         * ここで例外を投げると Controller 到達前に発生し、
         * GlobalExceptionHandler が捕捉できず HTTP 500 になる。</p>
         */
        public PageRequest {
                if (pageNum == null || pageNum < MIN_PAGE_NUM) {
                        pageNum = MIN_PAGE_NUM;
                }
                if (pageSize == null || pageSize < MIN_PAGE_NUM) {
                        pageSize = DEFAULT_PAGE_SIZE;
                } else if (pageSize > MAX_PAGE_SIZE) {
                        pageSize = MAX_PAGE_SIZE;
                }
        }
}