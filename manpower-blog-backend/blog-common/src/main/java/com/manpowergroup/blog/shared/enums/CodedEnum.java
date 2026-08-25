package com.manpowergroup.blog.shared.enums;

/**
 * DBへ数値コードとして永続化される列挙の共通契約。
 *
 * <p>MyBatis-Plus の {@code @EnumValue} が付いた {@code byte code} を持つ列挙は、
 * 本インタフェースを実装すること。
 * 実装漏れは {@code EnumConventionTest}（ArchUnit）が検出する。</p>
 *
 * <p>本インタフェースの目的は、クエリパラメータの型変換を一括で扱えるようにすること。
 * {@code @JsonCreator} は JSON ボディの逆シリアライズにのみ働き、
 * クエリパラメータは Spring の {@code ConversionService} を通るため、
 * 既定では列挙名でしか解決できない。
 * 結果として「応答は数値、要求は列挙名」という非対称が生じる。
 * {@code CodedEnumConverterFactory} がこの差を埋める。</p>
 *
 * <p>コードが文字列である列挙（{@code AccountType} 等）は対象外とする。
 * それらはコードと列挙名が一致しており、既定の変換で解決できる。</p>
 */
public interface CodedEnum {

    /** DBに格納される数値コード。 */
    byte getCode();
}
