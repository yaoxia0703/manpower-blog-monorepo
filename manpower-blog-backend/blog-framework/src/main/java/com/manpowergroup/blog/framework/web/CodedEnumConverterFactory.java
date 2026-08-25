package com.manpowergroup.blog.framework.web;

import com.manpowergroup.blog.shared.enums.CodedEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.NonNull;

/**
 * クエリパラメータを {@link CodedEnum} へ変換するファクトリ。
 *
 * <p>数値（DBコード）と列挙名のどちらでも受け付ける。
 * これにより「応答は数値だが要求は列挙名でしか受け付けない」という
 * 非対称を解消する。応答の {@code userStatus: 1} をそのまま
 * {@code ?status=1} として送り返せる。</p>
 *
 * <p>{@code @JsonCreator} ではこの問題を解決できない。
 * 同アノテーションは Jackson による JSON ボディの逆シリアライズ時にのみ働き、
 * クエリパラメータは Spring の {@code ConversionService} を通るためである。</p>
 */
public class CodedEnumConverterFactory implements ConverterFactory<String, CodedEnum> {

    @Override
    @NonNull
    public <T extends CodedEnum> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return new StringToCodedEnum<>(targetType);
    }

    private record StringToCodedEnum<T extends CodedEnum>(Class<T> targetType)
            implements Converter<String, T> {

        @Override
        public T convert(@NonNull String source) {
            final String value = source.trim();
            if (value.isEmpty()) {
                return null;
            }
            return isNumeric(value) ? byCode(value) : byName(value);
        }

        private static boolean isNumeric(String value) {
            for (int i = 0; i < value.length(); i++) {
                if (!Character.isDigit(value.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        /**
         * DBコードで解決する。
         *
         * <p>変換失敗時は {@code IllegalArgumentException} を送出する。
         * Spring がこれを {@code ConversionFailedException} へ包み、
         * 最終的に {@code GlobalExceptionHandler} が項目名付きの
         * 422 応答へ変換するため、独自に例外型を定義する必要はない。</p>
         */
        private T byCode(String value) {
            final int code;
            try {
                code = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "数値が大きすぎます: " + value + "（" + targetType.getSimpleName() + "）");
            }
            for (T candidate : targetType.getEnumConstants()) {
                if (candidate.getCode() == (byte) code) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException(
                    "無効なコードです: " + value + "（" + targetType.getSimpleName() + "）");
        }

        /** 列挙名で解決する。従来どおり ENABLED 等の指定も受け付ける。 */
        private T byName(String value) {
            for (T candidate : targetType.getEnumConstants()) {
                if (((Enum<?>) candidate).name().equalsIgnoreCase(value)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException(
                    "無効な値です: " + value + "（" + targetType.getSimpleName() + "）");
        }
    }
}
