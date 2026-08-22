package com.manpowergroup.blog.shared.util;

/**
 * 文字列操作に関するユーティリティクラス。
 *
 * null安全な変換やトリム処理、空文字判定などを提供する。
 */
public final class StringUtils {

    private StringUtils() {}

    /**
     * null の場合は空文字（""）を返却する。
     *
     * @param value 対象文字列
     * @return null の場合は ""、それ以外は元の文字列
     */
    public static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * 空文字または空白文字のみの場合は null を返却する。
     *
     * @param value 対象文字列
     * @return 有効な文字列の場合は trim 後の値、空または空白のみの場合は null
     */
    public static String emptyToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    /**
     * 有効な文字列（null でなく、空白のみでない）かを判定する。
     *
     * @param value 対象文字列
     * @return 有効な場合 true、それ以外は false
     */
    public static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * 前後の空白を除去する（null 安全）。
     *
     * @param value 対象文字列
     * @return trim 後の文字列（null の場合は null）
     */
    public static String trim(String value) {
        return value == null ? null : value.trim();
    }

    /**
     * 文字列を正規化する。
     * null または空白のみの場合は null を返却し、
     * それ以外は trim した文字列を返却する。
     *
     * @param value 対象文字列
     * @return 正規化された文字列、または null
     */
    public static String normalize(String value) {
        return hasText(value) ? value.trim() : null;
    }
}