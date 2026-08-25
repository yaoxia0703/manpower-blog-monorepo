package com.manpowergroup.blog.shared.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 一覧検索の結果を表す共通のページオブジェクト。
 *
 * <p>単表・結合を問わず、ページングを伴う一覧応答は本型で返す。
 * 本型は HTTP 応答の形状であり、フロントエンドのページャ描画に必要な
 * 派生値（総ページ数）を含むため、domain 層からは参照しない。</p>
 */
@Schema(name = "PageResult", description = "一覧検索結果のページオブジェクト")
public record PageResult<T>(
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
) {

    /**
     * 検索結果を不変リストへ正規化する。
     *
     * <p>正規コンストラクタを含む全ての生成経路を通るため、
     * 呼び出し側が保持する元リストへの変更がページ結果へ波及しない。</p>
     */
    public PageResult {
        records = records == null ? List.of() : List.copyOf(records);
    }

    /**
     * ページ総数を一元的に計算してページ結果を生成する。
     *
     * @param records 検索結果
     * @param total 総件数
     * @param pageNum 現在ページ
     * @param pageSize 1ページあたりの件数
     */
    public PageResult(List<T> records, long total, long pageNum, long pageSize) {
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
    public static <T> PageResult<T> of(List<T> records, long total, long pageNum, long pageSize) {
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    /**
     * 該当件数0件のページ結果を生成する。
     *
     * <p>件数取得のみを先に実行し、0件だった場合に本体検索を省略する用途を想定する。
     * リクエストされたページ情報をそのまま回し戻すため、
     * フロントエンドは結果の有無に関わらず同一の描画処理を利用できる。</p>
     *
     * @param pageNum 現在ページ
     * @param pageSize 1ページあたりの件数
     * @return 空のページ結果
     */
    public static <T> PageResult<T> empty(long pageNum, long pageSize) {
        return new PageResult<>(List.of(), 0, pageNum, pageSize);
    }

    /**
     * ページ情報を持たない空のページ結果を生成する。
     *
     * <p>ページ番号・件数ともに0を返すため、ページャを描画しない場面
     * （入力値不正による早期リターン等）に限って使用する。
     * 通常の検索結果が0件の場合は {@link #empty(long, long)} を使用し、
     * リクエストされたページ情報を保持すること。</p>
     *
     * @return ページ情報を持たない空のページ結果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0, 0, 0);
    }

    private static long calculatePages(long total, long pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (long) Math.ceil((double) total / pageSize);
    }
}
