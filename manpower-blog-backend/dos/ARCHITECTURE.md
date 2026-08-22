# manpower-blog-backend アーキテクチャ設計書

## 1. 目的

`manpower-blog-backend` は manpower-blog のバックエンド API プロジェクトである。管理画面向けの System API、公開側 Portal API、認証、RBAC、メニュー、権限、記事ドメインを Spring Boot 3 のマルチモジュール構成で提供する。

本設計書は現在のコードを正とし、特に以下の更新後設計を反映する。

- API 認可は `@PreAuthorize` ではなく、framework の `DynamicAuthorizationManager` で集中制御する。
- 権限は `method + path + code` の三位一体で管理する。
- メニューと権限の実行責務は分離し、権限の `menuId` は管理画面上の分類にのみ利用する。
- メニューは `path` と `component` を持ち、管理画面のナビゲーションとパンくずの元データになる。
- フロントエンドのルート定義は当面静的ルートを維持する。

## 2. モジュール構成

| Module | 役割 |
|---|---|
| `blog-starter` | Spring Boot 起動モジュール。アプリケーションのエントリポイント。 |
| `blog-admin-api` | 管理画面向け Controller。System API と記事管理 API を公開する。 |
| `blog-portal-api` | 匿名公開側 Controller。公開済み記事の参照 API、疎通確認 API を公開する。 |
| `blog-module-system` | system ドメイン。User、Role、Permission、Menu、Login の業務処理と永続化。 |
| `blog-module-content` | content ドメイン。Article の業務処理と永続化。 |
| `blog-module-member` | member ドメイン。会員機能の業務処理と永続化（構築中）。 |
| `blog-member-api` | 会員画面向け Controller（構築中）。 |
| `blog-framework` | 横断基盤。Spring Security、JWT、API 認可フィルタ、MyBatis、例外処理、Swagger、TraceId。 |
| `blog-common` | 共通 DTO、Result、例外、Enum、ドメインガード、ユーティリティ。 |
| `blog-infra` | 開発支援、コード生成などの infra 補助。 |

### 2.1 パッケージ命名規約

ルートは `com.manpowergroup.blog`。モジュール境界がパッケージ名から一意に読み取れることを優先する。

| Prefix | 対象 |
|---|---|
| `com.manpowergroup.blog.bootstrap` | 起動モジュール |
| `com.manpowergroup.blog.api.<面>` | Controller（`admin` / `portal` / `member`） |
| `com.manpowergroup.blog.module.<ドメイン>` | 業務モジュール（`system` / `content` / `member`） |
| `com.manpowergroup.blog.framework` | 横断基盤 |
| `com.manpowergroup.blog.shared` | 共通 DTO・例外・Enum・ガード |

業務モジュール配下は `application` / `domain` / `infrastructure` の3層に限定する。第4のトップレベルパッケージを追加しない（アダプタ実装は `infrastructure` 配下へ置く）。

> 注意：`LayerDependencyTest` はパッケージ名を**文字列**で指定している。
> パッケージ構成を変更した際は必ず同テストの定数も更新すること。
> 更新漏れがあると対象0件となり、ルールが空振りしたまま成功する。

依存方向は API -> module -> framework/common を基本とし、framework は system の実装詳細に依存しない。ユーザー別権限ロードは `UserAuthorityProvider` インターフェースで抽象化し、system 側の `SystemUserAuthorityProvider` が実装する。

## 3. レイヤ構成

### 3.1 Controller

配置先は `blog-admin-api` と `blog-portal-api`。

Controller は HTTP 入出力の境界であり、以下を担当する。

- `@RequestMapping` / `@GetMapping` などのエンドポイント定義
- `@Valid` による入力検証
- application service の呼び出し
- `Result<T>` 形式でのレスポンス返却

Controller では API 権限注解を持たない。認可は Spring Security の `DynamicAuthorizationManager` が実施する。

### 3.2 Application Service

配置先は各業務モジュール。ユースケース単位の手続きを担当する。

- 入力は `application.command` / `application.query` のコマンド・クエリオブジェクト
- 出力は `application.dto.response` のレスポンスオブジェクト
- 複数集約にまたがる手続きの調整
- Repository / ポート経由での永続化・外部技術アクセス

Application Service は MyBatis-Plus の `IService` / `ServiceImpl` を継承しない。ORM の CRUD がユースケース契約へ露出すると、Controller から集約の不変条件を迂回できてしまうためである。同じ理由で `Mapper` と `framework` の具象クラスも直接参照しない。

### 3.3 Domain

ドメインモデルは業務ルールを内部に保持する（充血モデル）。

- public setter を持たない（`@Data` / `@Setter` / `@Builder` は使用しない）
- 生成は静的ファクトリメソッド経由とし、private コンストラクタ内で不変条件を検証する
- 状態変更は意図を表す振る舞いメソッドで公開する（`changeStatus` / `updateProfile` 等）
- 「呼び忘れると不正状態が残る validate」は作らない

system ドメインの主な構成要素:

- User / UserAccount / UserRole
- Role / RoleAuthorization / RolePermission / RoleMenu
- Permission / UserAuthorities
- Menu

content ドメインでは Article を扱う。

### 3.4 Infrastructure

Repository 実装、MyBatis Mapper/XML、および framework ポートのアダプタ実装を配置する。

`framework` が定義するポート（`UserAuthorityProvider`、`PermissionRuleProvider`）の実装は、モジュール直下ではなく `infrastructure.security` 配下へ置く。これらは外部技術との接続点であり、3層構造の外に第4のパッケージを作らないためである。

### 3.5 Framework

`blog-framework` は以下の横断機能を提供する。

- `SecurityConfig`
- `JwtAuthenticationFilter`
- `DynamicAuthorizationManager`
- `JwtTokenProvider`
- `PasswordService`
- `GlobalExceptionHandler`
- MyBatis-Plus 設定
- Swagger / OpenAPI 設定
- TraceId filter / response advice

## 4. 認証設計

### 4.1 ログイン

ログイン API は `/api/system/auth/login`。

処理フロー:

1. クライアントが accountType、accountValue、password を送信する。
2. `LoginAppService` がアカウントとパスワードを検証する。
3. `JwtTokenProvider` が JWT を発行する。
4. フロントエンドは token を保存し、以降 `Authorization: Bearer <token>` を付与する。

### 4.2 JWT 認証

`JwtAuthenticationFilter` がリクエストの Bearer token を検証し、成功時に `SecurityContext` へ `LoginPrincipal` を設定する。

`LoginPrincipal` は以下のようなログイン主体情報を保持する。

- userId
- accountId
- username / nickname
- authorities

## 5. API 認可設計

### 5.1 方針

API 認可は `DynamicAuthorizationManager` で一元化する。Controller の `@PreAuthorize` は使用しない。

権限定義は `t_sys_permission` の以下 3 要素を中心に扱う。`menu_id` は管理 UI の分類用であり、認可判定には使用しない。

| Field | 意味 |
|---|---|
| `method` | HTTP method。例: `GET`, `POST`, `PUT`, `DELETE`, `PATCH` |
| `path` | API path。例: `/api/system/menu/{id}` |
| `code` | 権限コード。例: `sys:menu:detail` |

実際の API 判定では `method + path` で有効なルールを特定し、対応する `code` がログインユーザーの Authority に存在するかを照合する。ルール未登録のリクエストは拒否する。

ただしワイルドカード Authority `*` を保持する場合は、ルールを読み込まず即座に許可する。

「どのロールが全権限を持つか」は業務ルールであり、`DynamicAuthorizationManager` はこれを判定しない。framework 層はロール名を一切持たず、`*` の有無のみを機械的に解釈する。特権ロールの定義と実効権限の算出は system ドメインの `UserAuthorities` に一元化されており、画面制御（`/me` API のレスポンス）と API 認可は必ず同一のルールから導出される。

### 5.2 Filter chain

`SecurityConfig` の概要:

- CSRF 無効
- CORS 有効
- Session は stateless
- `/api/system/auth/login`、公開記事 GET、疎通確認、API ドキュメントは permit
- `/api/system/auth/me`、logout、my-menu は authenticated-only
- その他はすべて `DynamicAuthorizationManager` で認可し、未登録ルールは拒否
- `JwtAuthenticationFilter` を username/password filter の前に配置
- `DynamicAuthorizationManager` を Spring Security の request authorization に設定

### 5.3 認可フロー

```mermaid
sequenceDiagram
    participant Client
    participant JwtFilter as JwtAuthenticationFilter
    participant AuthzManager as DynamicAuthorizationManager
    participant Provider as PermissionRuleProvider
    participant Controller

    Client->>JwtFilter: Authorization Bearer token
    JwtFilter->>JwtFilter: token validation
    JwtFilter->>AuthzManager: authorities in SecurityContext
    AuthzManager->>Provider: loadEnabledRules()
    Provider-->>AuthzManager: ApiPermission(method,path,code)[]
    AuthzManager->>AuthzManager: method/path -> code -> authority matching
    alt allowed
        AuthzManager->>Controller: continue
    else denied
        AuthzManager-->>Client: 403 permission denied
    end
```

### 5.4 Path matching

`DynamicAuthorizationManager` は `AntPathMatcher` を使って path を照合する。

対応する形式:

- 完全一致: `/api/system/menu/tree`
- path variable: `/api/system/menu/{id}`
- pattern: `/api/system/menu/**`

親 path から子 path を暗黙に許可する fallback は使用しない。複数階層を許可する場合は `*` または `**` を権限 path に明示する。

## 6. RBAC 設計

### 6.1 関係

```mermaid
erDiagram
    USER ||--o{ USER_ROLE : has
    ROLE ||--o{ USER_ROLE : assigned
    ROLE ||--o{ ROLE_PERMISSION : has
    PERMISSION ||--o{ ROLE_PERMISSION : assigned
    ROLE ||--o{ ROLE_MENU : has
    MENU ||--o{ ROLE_MENU : assigned
    MENU o|--o{ PERMISSION : groups
```

### 6.2 権限

権限は API アクセス制御専用のデータである。

- `Permission` は必須の `code`, `method`, `path` と `status` を持つ。
- `menuId` は任意で、ロール設定画面における権限のグループ表示に利用する。
- Permission 自体に親子関係や MENU/BUTTON/API 種別は持たせない。
- `RolePermission` で role と permission を紐づける。
- ユーザーの API 権限は `user -> role -> role_permission -> permission` で取得する。

### 6.3 メニュー

メニューは UI ナビゲーション専用のデータである。

- `Menu` は `path` と `component` を持つ。
- `RoleMenu` で role と menu を紐づける。
- ユーザーの表示可能メニューは `user -> role -> role_menu -> menu` で取得する。
- メニューは permission id を持たない。
- ロール設定ではメニューと権限を一つの `/authorization` API でまとめて取得・保存する。

この分離により、画面表示制御と API 認可制御を独立して変更できる。

## 7. Menu / Permission 分離後の責務

| 項目 | Menu | Permission |
|---|---|---|
| 主用途 | 画面ナビゲーション、パンくず | API 認可 |
| 主なキー | `path`, `component` | `method`, `path`, `code`（`menuId` は分類専用） |
| role 紐づけ | `t_sys_role_menu` | `t_sys_role_permission` |
| frontend での用途 | sidebar、breadcrumb、route permission | button permission、権限管理 UI |
| backend 認可での用途 | 使わない | 使う |

## 8. DB 設計概要

主要テーブル:

- `t_sys_user`
- `t_sys_user_account`
- `t_sys_role`
- `t_sys_user_role`
- `t_sys_permission`
- `t_sys_role_permission`
- `t_sys_menu`
- `t_sys_role_menu`

`t_sys_menu` は `permission_id` を持たない。`path` と `component` を持つ。

`t_sys_permission` は API 権限定義として `method`, `path`, `code` を持つ。

## 9. API グループ

| Group | Base path | Module |
|---|---|---|
| Auth | `/api/system/auth` | `blog-admin-api` |
| User | `/api/system/user` | `blog-admin-api` |
| Role | `/api/system/role` | `blog-admin-api` |
| Permission | `/api/system/permission` | `blog-admin-api` |
| Menu | `/api/system/menu` | `blog-admin-api` |
| Article Management | `/api/system/article` | `blog-admin-api` |
| Portal Ping | `/api/portal/ping` | `blog-portal-api` |
| Published Article | `/api/portal/article` | `blog-portal-api` |

## 10. フロントエンド連携

フロントエンドは `/api/system/auth/me` で以下を取得する。

- login user
- menus
- permissions

`menus` は sidebar と breadcrumb に利用する。`permissions` は必要に応じてボタン表示などの UI 制御に利用する。

現在の frontend route は静的定義である。menu の `path` は静的 route と一致させ、動的 route 生成は将来拡張とする。

## 11. DDD 設計判断

本プロジェクトは厳密な DDD ではなく、保守コストとのバランスを取った実用的な設計を採用している。本章では「何を採用し、何を意図的に採用しなかったか」を記録する。

### 11.1 レイヤ依存方向

業務モジュール内部の依存方向は **infrastructure → domain ← application** とする。domain 層は application / infrastructure / framework / Spring のいずれにも依存しない。

この制約は `LayerDependencyTest`（ArchUnit）がテストとして固定し、CI で強制する。レビューだけでは容易に崩れるためである。

検証しているルール:

| ルール | 内容 |
|---|---|
| domain → application 禁止 | ドメインが上位層の DTO / VO を知らない |
| domain → infrastructure 禁止 | ドメインが永続化実装を知らない |
| domain → framework 禁止 | ドメインが横断基盤を知らない |
| domain → Spring / Servlet 禁止 | ドメインがフレームワーク非依存である |
| application → infrastructure 禁止 | ユースケースが実装詳細に依存しない |
| application → MyBatis-Plus 禁止 | ORM への依存を infrastructure に閉じる |
| application → framework 禁止 | 具象ではなく domain のポートを介す |
| Mapper の外部参照禁止 | Mapper を infrastructure 内部に閉じる |

同テストには「各レイヤのクラスが1件以上読み込めていること」を確認する番人テストを併設している。ArchUnit の `noClasses().that().resideInAPackage(X)` は X が0件の場合に無条件で成功するため、パッケージ指定の誤りがルールの空振りとして表面化しないのを防ぐ目的である。

> ArchUnit 専用エンジン（`archunit-junit5` の `@ArchTest`）は JUnit Platform 1.11 系でルールが検出されず、テスト0件のまま成功扱いになる事象を確認したため、コア API を通常の `@Test` から呼び出す方式を採用している。

### 11.2 責務の配置基準

| 判定 | 配置先 | 例 |
|---|---|---|
| 自身の状態のみで判定できる規則 | エンティティ | `User#ensureLoginAllowed` |
| 生成時の不変条件 | ファクトリメソッド + private コンストラクタ | `Permission#create` |
| 永続化層への問い合わせが必要な規則 | ドメインサービス | 一意性チェック |
| 複数集約にまたがる手続き | アプリケーションサービス | `UserAppService#create` |
| 形式規約を持つ単一の値 | 値オブジェクト | `UserAuthorities` |
| 外部技術への依存 | domain にポート、infrastructure にアダプタ | `PasswordEncryptor` |

### 11.3 ドメイン例外の方針

ドメイン層の不変条件違反は `DomainGuard`（`blog.shared.support`）を経由し、`BizException`（HTTP 400）として送出する。

`IllegalArgumentException` および `Objects.requireNonNull` は使用しない。これらは `GlobalExceptionHandler` に登録されておらず、汎用ハンドラへ落ちて **HTTP 500** を返してしまうためである。「入力不正なのにサーバーエラー」という誤ったレスポンスを構造的に防ぐ。

`DomainGuard` は `requireNonNull` / `requireText` / `normalizeText` / `requireNonNegative` / `requireTrue` を提供し、各モデルに重複していた検証ヘルパーを集約している。

### 11.4 認証ロジックのドメイン集約

パスワード照合はログインの中核業務ルールだが、ハッシュ方式（BCrypt）はインフラの関心事である。そのため domain 層に `PasswordEncryptor` ポートを定義し、`infrastructure.security.BCryptPasswordEncryptor` が framework の `PasswordService` へ委譲する。

ポートの実装を framework 側に置かない理由は、モジュール依存方向が module → framework であり、framework から module のインターフェースを参照できないためである。アダプタを infrastructure に置くことで依存方向を保ったまま依存性逆転を実現している。

結果として `UserAccount#authenticate` がユーザー状態・アカウント状態・認証状態・パスワード照合を一括で検証し、ログイン規則がドメインモデル内で完結する。

### 11.5 実効権限の一元化

「管理者ロールは全権限を持つ」という業務ルールは、画面のボタン制御（`/me`）と API 認可（`DynamicAuthorizationManager`）の双方が必要とする。

以前はこのルールが Controller と framework の2箇所に、素のロールコードと `ROLE_` 接頭辞付き Authority という**異なる表現**で重複していた。特権ロールを追加する際に片方だけ修正され、「画面にボタンは出るが API は 403」という不整合を生む危険があった。

現在は `UserAuthorities`（値オブジェクト）が唯一の判定元である。

- `effectivePermissionCodes()` — 画面制御用。特権ロール保持者にはワイルドカード1件を返す
- `toGrantedAuthorities()` — API 認可用。ロールに `ROLE_` を付与し実効権限を連結する

特権ロールを追加する場合は `UserAuthorities.SUPER_ADMIN_ROLES` のみを変更する。

### 11.6 参照系の扱い

一覧・ツリー・JOIN を伴う参照系はドメインモデルを経由せず、読み取り専用モデル（`SearchCriteria` / `SearchPage` / `Profile`）へ直接マッピングする。

採用理由:

- 参照系にドメインの不変条件は不要であり、モデルを経由すると過剰なオブジェクト生成を招く
- ドメインモデルが画面都合の項目に引きずられることを防げる

読み取り専用モデルは domain 配下に定義する。これにより Repository インターフェースが application 層の DTO / VO を参照する必要がなくなり、依存方向が保たれる。

### 11.7 永続化アノテーションをドメインモデルに残す判断

厳密な DDD ではドメインモデルは永続化技術から独立すべきであり、PO（Persistence Object）とドメインモデルを分離する方式が理想である。

本プロジェクトでは以下の理由により、ドメインモデルに MyBatis-Plus のアノテーションを残す方式を採用した。

- モデル数に対して変換コードの量が過大になり、保守コストが利点を上回ると判断したため
- 論理削除（`@TableLogic`）・自動採番フィールド（`@TableField(fill)`）は全モデル共通であり、変換層で再実装する意義が薄いため
- 将来 ORM を差し替える蓋然性が低いため

ただしビジネスルールは全てドメインモデル内に閉じており、アノテーション以外の永続化関心事（SQL・Wrapper・Mapper）は infrastructure 層に隔離している。

この判断に整合させるため、`LayerDependencyTest` には「domain が `com.baomidou` に依存しない」ルールを**あえて定義していない**。ルールと実際の設計判断が食い違うとテストが形骸化するためである。

### 11.8 Permission と Menu の関係

`t_sys_permission.menu_id` は管理 UI 上の分類（どのメニュー配下の操作か）を表すための項目であり、**API 認可判定には一切使用しない**。

メニューと権限は依然として別の関心事である。

- `menu.path` — フロントエンドのルーティング・ナビゲーション用
- `permission.path` — バックエンドの API 認可マッチング用

`menu_id` は前者から後者への参照ではなく、管理画面で権限一覧をグルーピング表示するための表示上の属性である。認可ロジックがこの項目を読まないことは 5.1 の通りであり、この分離は維持する。

## 12. 今後の拡張

- DynamicAuthorizationManager の権限ロード結果を cache する場合は、必要になった段階で cache 基盤を追加する。
- permission path の pattern 設計を管理画面で明確化する。
- frontend dynamic route を導入する場合、menu `component` と frontend component registry を対応させる。
- 会員投稿を追加する場合は member API/module として管理者用・匿名公開用から分離する。
