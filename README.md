# manpower-blog

Spring Boot 3 / Vue 3 で構築している個人開発のブログ・管理画面システム（**継続開発中**）。

機能の網羅よりも「設計判断を説明できること」を重視しており、DDD のレイヤ分離、
ArchUnit による依存方向の強制、認証主体ごとのセキュリティ分離などを、
規約ではなく**構造とテストで担保する**ことを方針としている。
判断の経緯は [Backend Architecture](manpower-blog-backend/docs/ARCHITECTURE.md) の 11 章に記録している。

## 技術スタック

| 領域 | 技術 |
|---|---|
| Backend | Java 21 / Spring Boot 3.4 / Spring Security / JWT / MyBatis-Plus |
| Frontend | Vue 3.5 / TypeScript 5.9 / Vite / Element Plus / Pinia / Vue Router |
| DB | MySQL 8.0 |
| Test | JUnit 5 / ArchUnit 1.3 / Spring MockMvc |
| Build / CI | Maven（マルチモジュール）/ GitHub Actions |
| API ドキュメント | springdoc-openapi（Swagger UI） |

## 設計上の見どころ

- **認証主体ごとのセキュリティ分離** — 運用者・会員・匿名閲覧を `SecurityFilterChain` で面ごとに分割し、
  JWT の署名鍵と issuer も面ごとに分ける。会員が自己登録で得たトークンを運用者面へ送っても
  署名検証の段階で失敗し、条件分岐に頼らない。（[11.13](manpower-blog-backend/docs/ARCHITECTURE.md#1113-認証主体の分離)）
- **動的 API 認可** — Controller に `@PreAuthorize` を書かず、`method + path + code` の権限ルールを
  DB から読み込んで一元判定する。未登録のリクエストは既定で拒否する。（[5 章](manpower-blog-backend/docs/ARCHITECTURE.md#5-api-認可設計)）
- **レイヤ依存の自動検証** — `infrastructure → domain ← application` の依存方向を ArchUnit で CI 強制する。
  対象クラスが0件のままルールが空振りする事故を防ぐ番人テストも併設。（[11.1](manpower-blog-backend/docs/ARCHITECTURE.md#111-レイヤ依存方向)）
- **充血ドメインモデル** — public setter を持たず、不変条件は生成時に強制する。
  違反は `DomainGuard` 経由で業務例外とし、入力不正が HTTP 500 にならない構造にしている。（[11.2〜11.3](manpower-blog-backend/docs/ARCHITECTURE.md#112-責務の配置基準)）
- **意図的な部分結合の封じ込め** — 権限が分類用にメニュー ID を持つ一方、認可層へは投影型
  `ApiPermission` のみを渡し、メニュー ID が認可判定へ届かないことを型で保証する。（[11.8](manpower-blog-backend/docs/ARCHITECTURE.md#118-permission-と-menu-の関係--意図的な部分結合)）
- **ページング方式の使い分け** — 結合検索は `list` + `count` の手書き SQL（条件は `<sql>` 片で共有）、
  単表検索は MyBatis-Plus の標準機能とし、「統一」ではなく「その方式を採る理由」で選ぶ。（[11.9](manpower-blog-backend/docs/ARCHITECTURE.md#119-ページング方式を一律にしない)）

## 実装状況

| 領域 | 機能 | 状態 |
|---|---|---|
| 管理画面 | ログイン / ログアウト / ログインユーザー情報 | 実装済み |
| 管理画面 | ユーザー・ロール・権限・メニュー管理（CRUD） | 実装済み |
| 管理画面 | ロールへのメニュー・権限の一括割当 | 実装済み |
| 管理画面 | 記事管理 API | 実装済み（画面は未実装） |
| 公開側 | 公開済み記事の一覧・詳細 API | 実装済み |
| 会員 | 会員ログイン | 実装済み |
| 会員 | 会員の自己登録 | 実装済み |
| 会員 | プロフィール参照・更新、会員向け機能 | 予定 |
| 公開側 | 公開用フロントエンド | 予定 |

## プロジェクト構成

| Path | 説明 |
|---|---|
| `manpower-blog-backend` | Java / Spring Boot backend（Maven マルチモジュール） |
| `manpower-blog-frontend` | Vue 3 / TypeScript 管理画面 frontend |

### Backend モジュール

| Module | 役割 |
|---|---|
| `blog-starter` | 起動モジュール。アーキテクチャテスト・セキュリティ境界テストも配置 |
| `blog-admin-api` | 管理画面 API（`/api/system/**`） |
| `blog-portal-api` | 匿名公開 API（`/api/portal/**`） |
| `blog-member-api` | 会員 API（`/api/member/**`、構築中） |
| `blog-module-system` | system ドメイン（user / role / permission / menu / auth） |
| `blog-module-content` | content ドメイン（article） |
| `blog-module-member` | member ドメイン（構築中） |
| `blog-framework` | 横断基盤（security / JWT / 動的認可 / 例外処理 / MyBatis / TraceId） |
| `blog-common` | 共通 DTO・例外・Enum・ドメインガード |
| `blog-infra` | 開発時専用ツール（コード生成）。実行時には含まれない |

パッケージのルートは `com.manpowergroup.blog`。接入面は `api.<面>`、業務モジュールは `module.<ドメイン>` に配置し、
業務モジュール配下は `application` / `domain` / `infrastructure` の3層に限定する。

## 認証・認可の概要

| 面 | パス | 認証 | 認可 |
|---|---|---|---|
| ポータル | `/api/portal/**` | なし | GET のみ許可、他は拒否 |
| 会員 | `/api/member/**` | 会員用 JWT | ログイン・自己登録以外は認証済みであること |
| 運用者 | `/api/system/**` | 運用者用 JWT | ログイン以外は動的認可 |
| 既定 | 上記以外 | なし | API ドキュメント・ヘルスチェック以外は拒否 |

運用者の権限は用途ごとに分離している。

| 領域 | 管理対象 | 用途 |
|---|---|---|
| Menu | `path`, `component` | frontend のサイドバー、パンくず、画面遷移 |
| Permission | `method`, `path`, `code` | backend の API 認可 |

「どのロールが全権限を持つか」は業務ルールのため framework 層には持たせず、
`blog-module-system` の `UserAuthorities` が実効権限を算出する。
画面のボタン制御（`/api/system/auth/me`）と API 認可は同一のルールから導出される。

## セットアップ

### 前提

- JDK 21
- Maven 3.9 以上（`mvnw` は同梱していない）
- MySQL 8.0 以上
- Node.js 22

### 1. データベース

`blog_db` の作成を含む DDL と初期データを順に投入する。

```bash
mysql -u root -p < manpower-blog-backend/sql/ddl/blog_mysql_ddl.sql
mysql -u root -p blog_db < manpower-blog-backend/sql/data/blog_data.sql
```

既存の `blog_db` を更新する場合は、`manpower-blog-backend/sql/migration/` 配下のスクリプトを日付順に適用する
（新規構築では不要。`blog_data.sql` は適用後の値で管理している）。

### 2. Backend の設定

`application-dev.yml` は git 管理外である。雛形をコピーし、DB 接続情報と JWT の鍵を設定する。

```bash
cd manpower-blog-backend/blog-starter/src/main/resources
cp application-dev.example.yml application-dev.yml
```

JWT の秘密鍵には既定値がなく、**未設定のまま起動すると失敗する**（弱い鍵のまま本番へ到達することを防ぐため）。
運用者面と会員面には必ず異なる値を指定する。

```bash
openssl rand -base64 32   # 運用者面・会員面それぞれで生成する
```

環境変数で与える場合は `JWT_ADMIN_SECRET` / `JWT_ADMIN_ISSUER` / `JWT_MEMBER_SECRET` /
`JWT_MEMBER_ISSUER` / `CORS_ALLOWED_ORIGINS` を使用する（詳細は ARCHITECTURE.md 11.12）。

### 3. 起動

Backend（`http://localhost:8080`）:

```bash
cd manpower-blog-backend
mvn spring-boot:run -pl blog-starter -am
```

IDE から起動する場合は `manpower-blog-backend` を Maven project として開き、
`blog-starter` の `ManpowerBlogApplication` を実行する。
API ドキュメントは `http://localhost:8080/swagger-ui/index.html` で参照できる。

Frontend（`http://localhost:5173`）:

```bash
cd manpower-blog-frontend
npm install
npm run dev
```

## テスト

```bash
cd manpower-blog-backend
mvn -B test
```

主なアーキテクチャ・境界テスト（いずれも `blog-starter` に配置）:

| テスト | 検証内容 |
|---|---|
| `LayerDependencyTest` | レイヤ依存方向、Mapper の閉じ込め、経路変数への `@Positive` 付与 |
| `EnumConventionTest` | DB に数値コードで保存する列挙が `CodedEnum` を実装していること |
| `PrincipalIsolationTest` | 面をまたいだトークンの拒否、ポータル更新系の拒否、ログインの到達性 |

## CI

`.github/workflows/build.yml` で、`main` への push / pull request 時に以下を独立したジョブとして実行する。

- Backend: `mvn -B clean verify`（テストを含む）
- Frontend: `npm ci` と `npm run build`（型チェックを含む）

## 設計書

| 文書 | 内容 |
|---|---|
| [Backend Architecture](manpower-blog-backend/docs/ARCHITECTURE.md) | モジュール構成、認証・認可、RBAC、DDD 設計判断 |
| [Backend API Design](manpower-blog-backend/docs/API-DESIGN.md) | API 一覧、権限コード、エラー応答形式 |
| [Frontend Architecture](manpower-blog-frontend/docs/ARCHITECTURE.md) | ディレクトリ構成、ルーティング、認証状態・権限ストア |
| [Frontend API Design](manpower-blog-frontend/docs/API-DESIGN.md) | frontend から見た API 連携 |

全体説明は本 README に集約し、各プロジェクトの詳細は `docs` 配下の設計書で管理する。
