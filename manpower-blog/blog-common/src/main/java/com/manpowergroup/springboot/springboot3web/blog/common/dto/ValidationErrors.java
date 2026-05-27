package com.manpowergroup.springboot.springboot3web.blog.common.dto;

import java.util.List;

/**
 * バリデーションエラー情報を保持するDTO。
 * 主に入力チェックの結果として発生したエラー一覧をまとめて返却するために使用する。
 * 複数のフィールドに対するエラーを一括で管理できる構造となっている。
 * 例：
 * ・必須チェックエラー
 * ・フォーマット不正
 * ・桁数超過 など
 *
 * @param errors エラー情報のリスト
 */
public record ValidationErrors(List<ValidationErrors.ErrorItem> errors) {

    /**
     * エラーリストからValidationErrorsインスタンスを生成するファクトリメソッド。
     *
     * @param list エラー情報のリスト
     * @return ValidationErrorsインスタンス
     */
    public static ValidationErrors of(List<ErrorItem> list) {
        return new ValidationErrors(list);
    }

    /**
     * 個別のバリデーションエラー情報を表すDTO。
     *
     * 各フィールドごとに発生したエラー内容を保持する。
     *
     * @param field エラー対象のフィールド名（例：username, emailなど）
     * @param key   エラーメッセージキー（i18n対応用）
     * @param args  メッセージに埋め込むパラメータ（可変長）
     */
    public record ErrorItem(String field, String key, Object[] args) {

        /**
         * ErrorItemインスタンスを生成するファクトリメソッド。
         *
         * @param field エラー対象フィールド
         * @param key   メッセージキー
         * @param args  メッセージパラメータ
         * @return ErrorItemインスタンス
         */
        public static ErrorItem of(String field, String key, Object... args) {
            return new ErrorItem(field, key, args);
        }
    }
}
