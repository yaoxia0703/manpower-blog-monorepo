package com.manpowergroup.blog.architecture;

import com.manpowergroup.blog.shared.enums.CodedEnum;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 列挙の実装規約を検証するアーキテクチャテスト。
 *
 * <p>DBへ数値コードとして保存される列挙は {@link CodedEnum} を実装する。
 * 実装を忘れるとクエリパラメータで数値が受け付けられなくなるが、
 * コンパイルは通り、該当の検索条件を実際に叩くまで表面化しない。
 * 規約ではなくテストで強制する。</p>
 */
class EnumConventionTest {

    private static final String BASE = "com.manpowergroup.blog";
    private static final String ENUM_VALUE_ANNOTATION = "com.baomidou.mybatisplus.annotation.EnumValue";

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    /**
     * 数値コードを持つ列挙が {@link CodedEnum} を実装していることを確認する。
     *
     * <p>コードが文字列の列挙（{@code AccountType} 等）は対象外。
     * 列挙名とコードが一致しており、既定の変換で解決できるため。</p>
     */
    @Test
    void 数値コードを持つ列挙はCodedEnumを実装する() {
        final List<String> violations = CLASSES.stream()
                .filter(JavaClass::isEnum)
                .filter(EnumConventionTest::hasNumericEnumValueField)
                .filter(clazz -> !clazz.isAssignableTo(CodedEnum.class))
                .map(JavaClass::getSimpleName)
                .toList();

        assertThat(violations)
                .as("@EnumValue の数値コードを持つ列挙は CodedEnum を実装する必要があります")
                .isEmpty();
    }

    /** 対象となる列挙が実在することを確認する番人。 */
    @Test
    void 数値コードを持つ列挙が検出できていること() {
        final long count = CLASSES.stream()
                .filter(JavaClass::isEnum)
                .filter(EnumConventionTest::hasNumericEnumValueField)
                .count();

        assertThat(count)
                .as("@EnumValue の数値コードを持つ列挙の数（0件成功を防ぐ）")
                .isPositive();
    }

    private static boolean hasNumericEnumValueField(JavaClass clazz) {
        return clazz.getFields().stream()
                .filter(field -> field.isAnnotatedWith(ENUM_VALUE_ANNOTATION))
                .anyMatch(field -> {
                    final String type = field.getRawType().getName();
                    return "byte".equals(type) || "short".equals(type) || "int".equals(type);
                });
    }
}
