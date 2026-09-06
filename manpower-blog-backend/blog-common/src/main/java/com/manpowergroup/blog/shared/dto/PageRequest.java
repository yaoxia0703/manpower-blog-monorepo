package com.manpowergroup.blog.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ページング用のリクエストDTO。
 *
 * <p>本型は HTTP パラメータをそのまま受け取るだけの入れ物であり、
 * 値の補正は行わない。未指定・範囲外の扱いは
 * {@link PageQuery#clamped(Long, Long, PageLimits)} に一本化する。</p>
 *
 * <p>かつては本型の生成時にも既定値と上限を適用していたが、
 * 上限がコード側の定数と {@code app.page.max-page-size} の二箇所に
 * 存在することになり、設定値を変更しても先に定数で丸められるため
 * 反映されなかった。丸めを行う地点を一つに保つ。</p>
 *
 * <p>なお、補正ではなく例外で弾く方式は採れない。本型は Controller 到達前の
 * バインディング時に生成されるため、ここで例外を投げると
 * {@code GlobalExceptionHandler} が捕捉できず HTTP 500 となる。</p>
 *
 * @param pageNum  ページ番号（未指定可）
 * @param pageSize 1ページあたりの件数（未指定可）
 */
@Schema(description = "ページリクエストDTO")
public record PageRequest(
        @Schema(description = "ページ番号", example = "1")
        Long pageNum,

        @Schema(description = "1ページあたりの件数", example = "20")
        Long pageSize
) {
}
