package com.manpowergroup.blog.shared.dto;

import com.manpowergroup.blog.shared.support.DomainGuard;

/**
 * ページングの上下限。
 *
 * <p>設定値（{@code app.page.*}）を Spring 非依存の形へ写し取るための値オブジェクト。
 * {@link PageQuery} が設定クラスを直接参照すると、
 * 値オブジェクトが Spring へ依存し domain 層から利用できなくなるため、
 * 間にこの型を挟む。</p>
 *
 * @param defaultPageNum  既定のページ番号
 * @param defaultPageSize 既定の1ページあたりの件数
 * @param maxPageSize     1ページあたりの上限件数
 */
public record PageLimits(long defaultPageNum, long defaultPageSize, long maxPageSize) {

    /**
     * 生成時に設定値の整合性を検証する。
     *
     * <p>最大値と既定値を取り違えた設定（例：max=10, default=100）は、
     * 検証しなければ最初のページング要求まで表面化しない。
     * 起動直後に気付けるよう生成時点で弾く。</p>
     */
    public PageLimits {
        DomainGuard.requireTrue(defaultPageNum >= 1,
                "既定ページ番号は1以上である必要があります");
        DomainGuard.requireTrue(defaultPageSize >= 1,
                "既定ページサイズは1以上である必要があります");
        DomainGuard.requireTrue(maxPageSize >= defaultPageSize,
                "最大ページサイズは既定ページサイズ以上である必要があります");
    }
}
