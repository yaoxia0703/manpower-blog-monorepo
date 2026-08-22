package com.manpowergroup.blog.shared.support;

import com.manpowergroup.blog.shared.enums.ErrorCode;
import com.manpowergroup.blog.shared.exception.BizException;

/**
 * ドメインモデルの不変条件を検証するガード。
 *
 * <p>設計意図：
 * ドメインモデルの不変条件違反はクライアント入力に起因するため、
 * {@link IllegalArgumentException} や {@link NullPointerException} ではなく
 * {@link BizException}（HTTP 400）として送出する。</p>
 *
 * <p>これらの標準例外は GlobalExceptionHandler に登録されておらず、
 * 汎用ハンドラへ落ちて HTTP 500 を返してしまうため、
 * 「入力不正なのにサーバーエラー」という誤ったレスポンスの原因になっていた。
 * 本クラスを経由することで、業務例外の意味づけと HTTP ステータスを一致させる。</p>
 */
public final class DomainGuard {

    private DomainGuard() {
    }

    /**
     * 必須オブジェクトを検証する。
     *
     * @param value     検証対象
     * @param fieldName エラーメッセージに使用する項目名
     * @return 検証済みの値
     */
    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, fieldName + "は必須です");
        }
        return value;
    }

    /**
     * 必須文字列を検証し、前後の空白を除去して返す。
     *
     * @param value     検証対象
     * @param fieldName エラーメッセージに使用する項目名
     * @return 正規化済みの文字列
     */
    public static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, fieldName + "は必須です");
        }
        return value.trim();
    }

    /**
     * 任意文字列を正規化する。空文字・空白のみは null として扱う。
     *
     * @param value 対象文字列
     * @return 正規化済みの文字列、または null
     */
    public static String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /**
     * 0以上の数値であることを検証する。
     *
     * @param value     検証対象
     * @param fieldName エラーメッセージに使用する項目名
     * @return 検証済みの値
     */
    public static Long requireNonNegative(Long value, String fieldName) {
        if (value == null || value < 0) {
            throw BizException.withDetail(
                    ErrorCode.BAD_REQUEST, fieldName + "は0以上でなければなりません");
        }
        return value;
    }

    /**
     * 条件が満たされない場合に業務例外を送出する。
     *
     * @param condition 満たすべき条件
     * @param message   条件違反時のメッセージ
     */
    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, message);
        }
    }
}
