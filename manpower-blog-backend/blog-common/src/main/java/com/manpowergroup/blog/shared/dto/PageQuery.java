package com.manpowergroup.blog.shared.dto;

import com.manpowergroup.blog.shared.support.DomainGuard;

/**
 * 検証済みのページング値。
 *
 * <p>{@link PageRequest} が外部入力（HTTP パラメータ）を表すのに対し、
 * 本型は内部で扱う確定値を表す。生成された時点で常に正当な値であることが
 * 保証されるため、利用側に null 判定や範囲判定は不要となる。</p>
 *
 * <p>本型は {@code PageRequest} を参照しない。参照すると、
 * 本型を利用する domain 層が API 契約へ間接的に依存することになるため、
 * 変換責務は application 層（Assembler）に置く。</p>
 *
 * @param pageNum  ページ番号（1以上）
 * @param pageSize 1ページあたりの件数（1以上）
 */
public record PageQuery(long pageNum, long pageSize) {

    /**
     * 本型が常に満たすべき条件を強制する。
     *
     * <p>上限件数は運用方針であり本型固有の性質ではないため、
     * ここでは扱わずファクトリメソッドの責務とする。</p>
     */
    public PageQuery {
        DomainGuard.requireTrue(pageNum >= 1,
                "ページ番号は1以上である必要があります");
        DomainGuard.requireTrue(pageSize >= 1,
                "1ページあたりの件数は1以上である必要があります");
    }

    /**
     * 外部入力から生成する。範囲外の値は丸める。
     *
     * <p>HTTP パラメータは信用できないため、例外ではなく補正で受ける。
     * 巨大な pageSize による全件取得相当の負荷を上限で防ぐ。</p>
     *
     * @param pageNum  ページ番号（null・0以下は既定値へ）
     * @param pageSize 1ページあたりの件数（null・0以下は既定値へ、上限超過は上限へ）
     * @param limits   適用する上下限
     */
    public static PageQuery clamped(Long pageNum, Long pageSize, PageLimits limits) {
        DomainGuard.requireNonNull(limits, "limits");
        final long safeNum = (pageNum == null || pageNum < 1)
                ? limits.defaultPageNum()
                : pageNum;
        final long safeSize = (pageSize == null || pageSize < 1)
                ? limits.defaultPageSize()
                : Math.min(pageSize, limits.maxPageSize());
        return new PageQuery(safeNum, safeSize);
    }

    /**
     * 内部呼び出しから生成する。範囲外は {@code BizException} を送出する。
     *
     * <p>内部コードが上限を超える値を渡すのは実装の誤りであり、
     * 黙って丸めると誤りに気付けないため拒否する。</p>
     *
     * @param pageNum  ページ番号
     * @param pageSize 1ページあたりの件数
     * @param limits   適用する上下限
     */
    public static PageQuery of(long pageNum, long pageSize, PageLimits limits) {
        DomainGuard.requireNonNull(limits, "limits");
        DomainGuard.requireTrue(pageSize <= limits.maxPageSize(),
                "1ページあたりの件数が上限を超えています");
        return new PageQuery(pageNum, pageSize);
    }

    /** SQL の OFFSET 句へ渡す開始位置。 */
    public long offset() {
        return (pageNum - 1) * pageSize;
    }

    /** SQL の LIMIT 句へ渡す取得件数。 */
    public long limit() {
        return pageSize;
    }
}
