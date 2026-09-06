package com.manpowergroup.blog.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * レイヤ依存方向を検証するアーキテクチャテスト。
 *
 * <p>本プロジェクトの依存方向は infrastructure -&gt; domain &lt;- application とし、
 * domain 層はいかなる外側の層にも依存しない。
 * この制約はレビューだけでは容易に崩れるため、テストとして固定し CI で強制する。</p>
 *
 * <p>本クラスを blog-starter 配下に置く理由：
 * 全業務モジュールへ依存する唯一のモジュールであり、
 * ここでのみ system / content / member を横断して解析できる。
 * 各業務モジュールは互いに依存しないため、
 * 個別モジュールの test 配下では自モジュールしか検証できない。</p>
 *
 * <p>専用エンジン（archunit-junit5 の {@code @ArchTest}）は JUnit Platform 1.11 系で
 * ルールが検出されず、テスト0件のまま成功扱いになる。誤検知を避けるため、
 * コア API を通常の {@code @Test} から呼び出す方式を採用している。</p>
 *
 * <p>補足：ドメインモデルには MyBatis-Plus のアノテーションを意図的に残しているため、
 * 「domain が com.baomidou に依存しない」ルールはあえて定義していない。
 * この設計判断の理由は ARCHITECTURE.md を参照。</p>
 *
 * <p>本クラスは依存方向のほかに、設定ファイルとコードの乖離も検証する。
 * {@code type-handlers-package} のような手書きの設定値は、
 * 追記漏れが実行時まで表面化しない類の不整合を生むため、
 * 実在するパッケージとの突き合わせをテストとして固定している。</p>
 */
class LayerDependencyTest {

    private static final String BASE = "com.manpowergroup.blog";
    private static final String MODULE_BASE = BASE + ".module";

    /* ============ 解析対象パッケージ ============ */

    /** 業務モジュール配下の各レイヤ。{@code ..} により全モジュールへ一括で適用する。 */
    private static final String DOMAIN = MODULE_BASE + "..domain..";
    private static final String APPLICATION = MODULE_BASE + "..application..";
    private static final String INFRASTRUCTURE = MODULE_BASE + "..infrastructure..";
    private static final String FRAMEWORK = BASE + ".framework..";

    /**
     * API 契約パッケージ。
     *
     * <p>{@code ..api..} のような省略形は接入面の
     * {@code com.manpowergroup.blog.api.admin} 等にも一致してしまい、
     * ルールが意図せず別の対象へ働く。完全修飾名で指定すること。</p>
     */
    private static final String SHARED_API = BASE + ".shared.api..";

    /**
     * Mapper パッケージ。
     *
     * <p>省略形（{@code ..mapper..} 等）は他パッケージへ誤って一致し、
     * ルールが意図せず緩む・または空振りする恐れがあるため、
     * 中間の {@code infrastructure.persistence.mapper} まで明示する。
     * 新モジュールもこの構成に揃えること。</p>
     */
    private static final String MAPPER = MODULE_BASE + "..infrastructure.persistence.mapper..";

    private static final String REPOSITORY = MODULE_BASE + "..domain.repository..";

    /**
     * TypeHandler を配置するパッケージ。
     *
     * <p>値オブジェクトと列型の変換はエンティティ側で {@code typeHandler} を名指しすると
     * domain -&gt; infrastructure の依存が生まれるため、設定ファイルへのパッケージ登録で解決している。
     * その登録漏れを検出するための突き合わせ対象。</p>
     */
    private static final String TYPE_HANDLER = MODULE_BASE + "..infrastructure.persistence.handler..";

    /** 解析対象の設定ファイル。blog-starter の main リソースがテスト classpath に載る。 */
    private static final String APPLICATION_YML = "application.yml";

    /** TypeHandler の登録先を示す設定キー。 */
    private static final String TYPE_HANDLER_PACKAGE_KEY = "mybatis-plus.type-handlers-package";

    /* ============ モジュール登録 ============ */

    /**
     * domain 層を持つ全業務モジュール。
     *
     * <p>新規モジュール追加時はここへ追記する。
     * 追記漏れは {@link #全業務モジュールが番人に登録されていること()} が検出する。</p>
     */
    private static final List<String> ALL_MODULES = List.of("system", "content", "member");

    /**
     * application / infrastructure まで実装済みのモジュール。
     *
     * <p>domain だけを持つ段階のモジュールはここへ含めない。
     * 実装が application / infrastructure へ及んだ時点で追加する。</p>
     */
    private static final List<String> IMPLEMENTED_MODULES = List.of("system", "content", "member");

    /* ============ 参照系モデルの命名規約 ============ */

    /** 参照系モデルを識別する接尾辞。命名を変える場合はここも更新すること。 */
    private static final String VIEW_SUFFIX = "View";
    private static final String SEARCH_PAGE_SUFFIX = "SearchPage";
    private static final String SEARCH_CRITERIA_SUFFIX = "SearchCriteria";

    /** 集約のみを受け取るべき書き込み系メソッド名。 */
    private static final Set<String> WRITE_METHOD_NAMES =
            Set.of("create", "update", "save", "delete", "insert");

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    /* ============ 番人：解析対象が実在することを保証する ============ */

    /**
     * 全業務モジュールが番人の登録リストに含まれていることを確認する。
     *
     * <p>{@link #ALL_MODULES} は手書きの定数であり、
     * モジュール追加時に更新を忘れると、そのモジュールは
     * 以降の番人による存在確認から静かに漏れる。
     * 実際に読み込まれたパッケージ名と突き合わせることで、
     * 登録漏れそのものを検出する。</p>
     */
    @Test
    void 全業務モジュールが番人に登録されていること() {
        final Pattern modulePattern =
                Pattern.compile(Pattern.quote(MODULE_BASE) + "\\.([^.]+)\\.");

        final Set<String> detected = new TreeSet<>();
        for (JavaClass clazz : CLASSES) {
            final Matcher matcher = modulePattern.matcher(clazz.getPackageName() + ".");
            if (matcher.lookingAt()) {
                detected.add(matcher.group(1));
            }
        }

        assertThat(detected)
                .as("実際に読み込まれた業務モジュール（ALL_MODULES の更新漏れがないこと）")
                .containsExactlyInAnyOrderElementsOf(ALL_MODULES);
    }

    /**
     * 各レイヤのクラスが実際に読み込めていることを確認する番人。
     *
     * <p>パッケージ指定が誤っていると {@code noClasses().that().resideInAPackage(...)} は
     * 対象0件のまま無条件で成功する。空振りによる偽の成功を防ぐため、
     * 各モジュール・各レイヤに最低1クラス存在することを先に保証する。</p>
     */
    @Test
    void 各モジュールの解析対象クラスが読み込めていること() {
        assertThat(CLASSES).isNotEmpty();

        assertThat(classCountIn(SHARED_API))
                .as("shared.api（API 契約）のクラス数")
                .isPositive();

        for (String module : ALL_MODULES) {
            assertThat(classCountIn(layerOf(module, "domain")))
                    .as("%s モジュールの domain 層のクラス数", module)
                    .isPositive();
        }

        for (String module : IMPLEMENTED_MODULES) {
            assertThat(classCountIn(layerOf(module, "application")))
                    .as("%s モジュールの application 層のクラス数", module)
                    .isPositive();
            assertThat(classCountIn(layerOf(module, "infrastructure")))
                    .as("%s モジュールの infrastructure 層のクラス数", module)
                    .isPositive();
            assertThat(classCountIn(layerOf(module, "infrastructure.persistence.mapper")))
                    .as("%s モジュールの mapper パッケージのクラス数", module)
                    .isPositive();
            assertThat(classCountIn(layerOf(module, "domain.repository")))
                    .as("%s モジュールの repository パッケージのクラス数", module)
                    .isPositive();
        }
    }

    private static String layerOf(String module, String layer) {
        return MODULE_BASE + "." + module + "." + layer + "..";
    }

    private long classCountIn(String packageIdentifier) {
        return CLASSES.stream()
                .filter(JavaClass.Predicates.resideInAPackage(packageIdentifier))
                .count();
    }

    /* ============ 番人：TypeHandler の登録漏れを検出する ============ */

    /**
     * 全ての TypeHandler パッケージが設定ファイルへ登録されていることを確認する。
     *
     * <p>{@code mybatis-plus.type-handlers-package} は単一の文字列であり、
     * モジュール追加時の追記漏れはコンパイルもテストも通過してしまう。
     * 症状は「値オブジェクトと列型の変換が効かない」という形で
     * 実行時のクエリ発行まで表面化せず、単体テストでは検出できない。
     * バイトコードから検出した実在のパッケージと設定値を突き合わせることで、
     * 追記漏れとパッケージ名の誤記の双方を検出する。</p>
     *
     * <p>MyBatis-Plus は登録されたパッケージの配下も走査するため、
     * 親パッケージによる一括登録も網羅済みとして扱う。</p>
     */
    @Test
    void TypeHandlerのパッケージが全て登録されていること() {
        final Set<String> detected = CLASSES.stream()
                .filter(JavaClass.Predicates.resideInAPackage(TYPE_HANDLER))
                .map(JavaClass::getPackageName)
                .collect(Collectors.toCollection(TreeSet::new));

        assertThat(detected)
                .as("検出された TypeHandler パッケージ（本テストが空振りしていないこと）")
                .isNotEmpty();

        final Set<String> configured = configuredTypeHandlerPackages();

        final List<String> missing = detected.stream()
                .filter(pkg -> configured.stream().noneMatch(entry -> isCoveredBy(pkg, entry)))
                .toList();

        assertThat(missing)
                .as("%s への登録漏れ（現在の設定値=%s）", TYPE_HANDLER_PACKAGE_KEY, configured)
                .isEmpty();
    }

    /**
     * 設定ファイルから TypeHandler の登録パッケージを読み出す。
     *
     * <p>設定キー自体の消失や空値も検出対象とする。
     * 値が取れない状態を素通りさせると、突き合わせが常に失敗するか、
     * あるいは空集合との比較で意味を失うため、ここで先に弾く。</p>
     */
    private static Set<String> configuredTypeHandlerPackages() {
        final YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource(APPLICATION_YML));
        final Properties properties = factory.getObject();

        assertThat(properties)
                .as("%s を読み込めていること", APPLICATION_YML)
                .isNotNull();

        final String raw = properties.getProperty(TYPE_HANDLER_PACKAGE_KEY);

        assertThat(raw)
                .as("%s が設定されていること", TYPE_HANDLER_PACKAGE_KEY)
                .isNotBlank();

        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * 設定されたパッケージが対象パッケージを網羅しているかを判定する。
     *
     * <p>MyBatis-Plus は配下のパッケージも走査するため、前方一致を許容する。
     * ただし {@code foo.barbaz} が {@code foo.bar} に一致しないよう、
     * 区切り文字まで含めて比較する。</p>
     */
    private static boolean isCoveredBy(String target, String configured) {
        return target.equals(configured) || target.startsWith(configured + ".");
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

    /**
     * ドメイン層は API 契約に依存しない。
     *
     * <p>{@code shared.api} 配下は HTTP 応答の形状を表す。
     * {@code @Schema} を持ち、総ページ数のようにフロントエンドの
     * 描画都合で決まる派生値を含む。
     * domain 層がこれに依存すると、表示要件の変更が
     * リポジトリのインタフェースを揺らす向きの依存が生まれる。</p>
     *
     * <p>{@code shared} 配下が一律に中立なわけではない点に注意する。
     * {@code shared.dto} の {@code PageRequest} のような素のデータは
     * domain から参照してよいが、{@code shared.api} の
     * {@code Result} / {@code PageResult} / {@code LoginResponse} は
     * API 契約であり参照してはならない。この区別のために
     * パッケージを分離している。</p>
     */
    @Test
    void ドメイン層はAPI契約に依存しない() {
        noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat().resideInAPackage(SHARED_API)
                .as("domain 層が shared.api（API 契約）に依存してはならない")
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

    /* ============ 参照系モデルと集約の混同を防ぐ ============ */

    /**
     * 読み取り専用モデルを名前で識別できることを確認する番人。
     *
     * <p>以降の2ルールは接尾辞による名前一致で対象を絞り込む。
     * 命名が規約から外れると対象0件のまま成功するため、
     * 実際に対象クラスが存在することを先に保証する。</p>
     */
    @Test
    void 読み取り専用モデルが命名規約で識別できること() {
        assertThat(readModelCount())
                .as("View / SearchPage / SearchCriteria で終わるクラス数")
                .isPositive();
    }

    private static boolean isReadModelName(String simpleName) {
        return simpleName.endsWith(VIEW_SUFFIX)
                || simpleName.endsWith(SEARCH_PAGE_SUFFIX)
                || simpleName.endsWith(SEARCH_CRITERIA_SUFFIX);
    }

    private long readModelCount() {
        return CLASSES.stream()
                .filter(clazz -> isReadModelName(clazz.getSimpleName()))
                .count();
    }

    /**
     * 参照系モデルは不変であること。
     *
     * <p>読み取り専用モデルに setter や振る舞いが生えると、
     * 集約と区別が付かなくなり書き込み経路へ流用される温床になる。
     * record に限定することで不変性を構造として担保する。</p>
     */
    @Test
    void 参照系モデルはrecordで定義される() {
        classes().that().haveSimpleNameEndingWith(VIEW_SUFFIX)
                .or().haveSimpleNameEndingWith(SEARCH_PAGE_SUFFIX)
                .or().haveSimpleNameEndingWith(SEARCH_CRITERIA_SUFFIX)
                .should().beRecords()
                .as("参照系モデルは record で定義し、可変な振る舞いを持たせてはならない")
                .check(CLASSES);
    }

    /**
     * 書き込み系メソッドは参照系モデルを受け取らない。
     *
     * <p>参照系モデルは JOIN 結果の投影であり集約ではない。
     * これを create / update / delete に渡せてしまうと、
     * 不変条件を通さない書き込み経路が生まれる。
     * 引数の型で禁止し、コンパイル可能な誤用を CI で検出する。</p>
     */
    @Test
    void 書き込み系メソッドは参照系モデルを引数に取らない() {
        final List<String> violations = CLASSES.stream()
                .filter(JavaClass.Predicates.resideInAPackage(REPOSITORY))
                .flatMap(clazz -> clazz.getMethods().stream())
                .filter(method -> WRITE_METHOD_NAMES.contains(method.getName()))
                .filter(method -> method.getRawParameterTypes().stream()
                        .anyMatch(param -> isReadModelName(param.getSimpleName())))
                .map(method -> method.getOwner().getSimpleName() + "#" + method.getName())
                .toList();

        assertThat(violations)
                .as("書き込み系メソッドは集約のみを受け取り、参照系モデルを受け取ってはならない")
                .isEmpty();
    }
}
