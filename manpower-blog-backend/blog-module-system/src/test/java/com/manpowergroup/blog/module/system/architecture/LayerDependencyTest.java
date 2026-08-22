package com.manpowergroup.blog.module.system.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * レイヤ依存方向を検証するアーキテクチャテスト。
 *
 * <p>本プロジェクトの依存方向は infrastructure -&gt; domain &lt;- application とし、
 * domain 層はいかなる外側の層にも依存しない。
 * この制約はレビューだけでは容易に崩れるため、テストとして固定し CI で強制する。</p>
 *
 * <p>専用エンジン（archunit-junit5 の {@code @ArchTest}）は JUnit Platform 1.11 系で
 * ルールが検出されず、テスト0件のまま成功扱いになる。誤検知を避けるため、
 * コア API を通常の {@code @Test} から呼び出す方式を採用している。</p>
 *
 * <p>補足：ドメインモデルには MyBatis-Plus のアノテーションを意図的に残しているため、
 * 「domain が com.baomidou に依存しない」ルールはあえて定義していない。
 * この設計判断の理由は ARCHITECTURE.md を参照。</p>
 */
class LayerDependencyTest {

    private static final String DOMAIN = "com.manpowergroup.blog.module.system.domain..";
    private static final String APPLICATION = "com.manpowergroup.blog.module.system.application..";
    private static final String INFRASTRUCTURE = "com.manpowergroup.blog.module.system.infrastructure..";
    private static final String FRAMEWORK = "com.manpowergroup.blog.framework..";
    private static final String MAPPER =
            "com.manpowergroup.blog.module.system.infrastructure.persistence.mapper..";

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.manpowergroup.blog.module.system");

    /**
     * 各レイヤのクラスが実際に読み込めていることを確認する番人。
     *
     * <p>パッケージ指定が誤っていると {@code noClasses().that().resideInAPackage(...)} は
     * 対象0件のまま無条件で成功する。空振りによる偽の成功を防ぐため、
     * 各レイヤに最低1クラス存在することを先に保証する。</p>
     */
    @Test
    void 各レイヤの解析対象クラスが読み込めていること() {
        assertThat(CLASSES).isNotEmpty();
        assertThat(classCountIn(DOMAIN)).as("domain 層のクラス数").isPositive();
        assertThat(classCountIn(APPLICATION)).as("application 層のクラス数").isPositive();
        assertThat(classCountIn(INFRASTRUCTURE)).as("infrastructure 層のクラス数").isPositive();
        assertThat(classCountIn(MAPPER)).as("mapper パッケージのクラス数").isPositive();
    }

    private long classCountIn(String packageIdentifier) {
        return CLASSES.stream()
                .filter(JavaClass.Predicates.resideInAPackage(packageIdentifier))
                .count();
    }

    /* ============ domain 層は外側のどの層にも依存しない ============ */

    @Test
    void ドメイン層はアプリケーション層に依存しない() {
        noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat().resideInAPackage(APPLICATION)
                .as("domain 層が application 層に依存してはならない")
                .check(CLASSES);
    }

    @Test
    void ドメイン層はインフラ層に依存しない() {
        noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)
                .as("domain 層が infrastructure 層に依存してはならない")
                .check(CLASSES);
    }

    @Test
    void ドメイン層はフレームワーク層に依存しない() {
        noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat().resideInAPackage(FRAMEWORK)
                .as("domain 層が framework 層に依存してはならない")
                .check(CLASSES);
    }

    @Test
    void ドメイン層はSpringに依存しない() {
        noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.servlet..")
                .as("domain 層が Spring / Servlet API に依存してはならない")
                .check(CLASSES);
    }

    /* ============ application 層は永続化の実装詳細に依存しない ============ */

    @Test
    void アプリケーション層はインフラ層に依存しない() {
        noClasses().that().resideInAPackage(APPLICATION)
                .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)
                .as("application 層が infrastructure 層の実装に依存してはならない")
                .check(CLASSES);
    }

    @Test
    void アプリケーション層はMyBatisPlusに依存しない() {
        noClasses().that().resideInAPackage(APPLICATION)
                .should().dependOnClassesThat().resideInAPackage("com.baomidou..")
                .as("application 層が ORM に依存してはならない（Repository ポート経由で永続化する）")
                .check(CLASSES);
    }

    @Test
    void アプリケーション層はフレームワーク層に依存しない() {
        noClasses().that().resideInAPackage(APPLICATION)
                .should().dependOnClassesThat().resideInAPackage(FRAMEWORK)
                .as("application 層が framework の具象に依存してはならない（domain のポート経由で利用する）")
                .check(CLASSES);
    }

    /* ============ Mapper は infrastructure 層に閉じる ============ */

    @Test
    void Mapperはインフラ層からのみ参照される() {
        noClasses().that().resideOutsideOfPackage(INFRASTRUCTURE)
                .should().dependOnClassesThat().resideInAPackage(MAPPER)
                .as("Mapper は infrastructure 層の内部実装であり、外部へ漏らしてはならない")
                .check(CLASSES);
    }
}
