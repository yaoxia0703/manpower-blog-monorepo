package com.manpowergroup.blog.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * 複数テーブル結合検索結果用ページオブジェクト
 */
@Schema(name = "JoinPageResult", description = "複数テーブル結合検索結果用ページオブジェクト")
public record JoinPageResult<T>(
        @Schema(description = "データリスト")
        List<T> records,

        @Schema(description = "総件数")
        long total,

        @Schema(description = "現在のページ番号")
        long pageNum,

        @Schema(description = "1ページあたりの件数")
        long pageSize,

        @Schema(description = "総ページ数")
        long pages
) implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ページ総数を一元的に計算してページ結果を生成する。
     *
     * @param records 検索結果
     * @param total 総件数
     * @param pageNum 現在ページ
     * @param pageSize 1ページあたりの件数
     */
    public JoinPageResult(List<T> records, long total, long pageNum, long pageSize) {
        this(records, total, pageNum, pageSize, calculatePages(total, pageSize));
    }

    /**
     * ページ結果を生成する。
     *
     * @param records 検索結果
     * @param total 総件数
     * @param pageNum 現在ページ
     * @param pageSize 1ページあたりの件数
     * @return ページ結果
     */
    public static <T> JoinPageResult<T> of(List<T> records, long total, long pageNum, long pageSize) {
        return new JoinPageResult<>(records, total, pageNum, pageSize);
    }

    private static long calculatePages(long total, long pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (long) Math.ceil((double) total / pageSize);
    }
}
