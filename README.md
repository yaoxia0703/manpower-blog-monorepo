# manpower-blog

`manpower-blog` は Spring Boot 3 backend と Vue 3 frontend で構成された個人ブログ / 管理画面プロジェクトである。

## Project Structure

| Path | 説明 |
|---|---|
| `manpower-blog-backend` | Java / Spring Boot backend |
| `manpower-blog-frontend` | Vue 3 / TypeScript frontend |

## Backend

Backend は Maven マルチモジュール構成で、起動モジュールは `blog-starter`。

主なモジュール:

- `blog-admin-api`: 管理画面 API
- `blog-portal-api`: 公開側 API
- `blog-member-api`: 会員向け API（構築中）
- `blog-module-system`: user / role / permission / menu / auth
- `blog-module-content`: article
- `blog-module-member`: 会員ドメイン（構築中）
- `blog-framework`: security / jwt / 動的認可 / mybatis / common web config
- `blog-common`: common DTO / enum / exception / domain guard / utility
- `blog-infra`: 開発支援・コード生成などの補助

パッケージのルートは `com.manpowergroup.blog`。接入面は `blog.api.<面>`、業務モジュールは `blog.module.<ドメイン>` に配置し、業務モジュール配下は `application` / `domain` / `infrastructure` の3層に限定する。

設計書:

- [Backend Architecture](manpower-blog-backend/dos/ARCHITECTURE.md)
- [Backend API Design](manpower-blog-backend/dos/API-DESIGN.md)

## Frontend

Frontend は Vue 3、TypeScript、Vite、Element Plus、Pinia、Vue Router で構成される。

主な機能:

- Login
- Dashboard
- User management
- Role management
- Permission management
- Menu management

設計書:

- [Frontend Architecture](manpower-blog-frontend/dos/ARCHITECTURE.md)
- [Frontend API Design](manpower-blog-frontend/dos/API-DESIGN.md)

## Auth And Permission

現在の権限設計は以下の分担にしている。

| 領域 | 管理対象 | 用途 |
|---|---|---|
| Menu | `path`, `component` | frontend sidebar、breadcrumb、画面遷移 |
| Permission | `method`, `path`, `code` | backend API 認可 |

Backend API 認可は Controller の `@PreAuthorize` ではなく、`blog-framework` の `DynamicAuthorizationManager` が `method` + `path` + `code` の照合により一元的に行う。ルール未登録のリクエストは拒否する。

「どのロールが全権限を持つか」は業務ルールのため framework 層には持たせず、`blog-module-system` の `UserAuthorities` が実効権限を算出する。画面のボタン制御（`/api/system/auth/me`）と API 認可は同一のルールから導出される。

`t_sys_permission` は管理画面での分類用に `menu_id` を保持するが、認可判定には使用しない。設計判断の詳細は [Backend Architecture](manpower-blog-backend/dos/ARCHITECTURE.md) の 11 章を参照。

## Development

### Frontend

```bash
cd manpower-blog-frontend
npm install
npm run dev
```

Build:

```bash
cd manpower-blog-frontend
npm run build
```

### Backend

IDEA で `manpower-blog-backend` を Maven project として開き、`blog-starter` の `ManpowerBlogApplication` を起動する。

CLI で Maven が利用できる環境では以下のように起動できる。

```bash
cd manpower-blog-backend
mvn spring-boot:run -pl blog-starter -am
```

テストは以下で実行する。

```bash
cd manpower-blog-backend
mvn -B test
```

`blog-module-system` には ArchUnit によるレイヤ依存検証（`LayerDependencyTest`）が含まれる。依存方向を破る変更はここで失敗する。

## GitHub Actions

CI workflow is defined at `.github/workflows/build.yml`.

It runs two independent jobs on push and pull request to `main`:

- Backend test/build in `manpower-blog-backend`
- Frontend build in `manpower-blog-frontend`

## README Policy

この repository は backend と frontend をまとめた親 project なので、全体説明は root の `README.md` に集約する。

個別 project の詳細は README を増やすより、各 project の `dos` 配下に設計書として管理する方針にする。
