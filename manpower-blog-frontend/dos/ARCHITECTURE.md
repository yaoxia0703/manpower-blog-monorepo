# manpower-blog-frontend アーキテクチャ設計書

## 1. 目的

`manpower-blog-frontend` は manpower-blog の管理画面フロントエンドである。Vue 3、TypeScript、Vite、Element Plus、Pinia、Vue Router を利用し、バックエンドの `/api/system/**` API と連携する。

現在の設計方針:

- frontend route は静的定義を維持する。
- backend の menu `path` は sidebar と breadcrumb の元データとして利用する。
- backend の menu `component` は将来の dynamic route 用に保持する。
- backend の permission は API 認可と必要に応じた UI ボタン制御に利用する。

## 2. 技術スタック

| 分類 | 採用 |
|---|---|
| Framework | Vue 3 |
| Language | TypeScript |
| Build | Vite |
| UI | Element Plus |
| State | Pinia |
| Router | Vue Router |
| HTTP | Axios |

## 3. ディレクトリ構成

| Path | 役割 |
|---|---|
| `src/api` | backend API client |
| `src/api/common/http` | axios instance、request/response interceptor |
| `src/router` | 静的 route 定義 |
| `src/stores` | Pinia store |
| `src/layouts/system` | 管理画面 layout |
| `src/views/login` | ログイン画面 |
| `src/views/system` | 管理画面各機能 |
| `src/types` | request / response / common type |

## 4. Route 設計

現在の route は静的定義である。

| Path | View |
|---|---|
| `/login` | login |
| `/system/dashboard` | dashboard |
| `/system/user` | user management |
| `/system/role` | role management |
| `/system/permission` | permission management |
| `/system/menu` | menu management |

`/system` 配下は `meta.requiresAuth = true` を持ち、認証済みユーザーのみアクセスする想定である。

## 5. 認証状態

`user` store がログインユーザーと token を管理する。

- token は `sessionStorage` に保存する。
- axios request interceptor が `Authorization: Bearer <token>` を付与する。
- `fetchUser()` は `/api/system/auth/me` を呼び出し、user、menus、permissions を store に保存する。
- logout 時は user store と permission store をクリアして `/login` に戻す。

## 6. Permission store

`permissionStore` は以下を保持する。

| State | 説明 |
|---|---|
| `menus` | backend から取得した menu tree |
| `permissions` | backend から取得した permission code 配列 |
| `loaded` | 初期ロード済みかどうか |

主な処理:

- `findMenuByPath(path)` は menu `path` から menu node を探す。
- `findMenuPath(path)` は breadcrumb 用の menu path 配列を返す。
- `hasRoutePermission(path)` は menu tree に path が存在するかで route 表示可否を判定する。
- `hasPermission(code)` は permission code による UI 制御に利用できる。

## 7. Menu / Breadcrumb

sidebar は backend の menu tree を利用して表示する。

`MenuItem.vue` は `menu.path` を router link の index として利用する。path がない directory は id を fallback とする。

breadcrumb は現在の route path と menu `path` を照合し、menu tree 上の親子関係から生成する。

## 8. API client

`src/api/common/http/index.ts` が axios 共通設定を持つ。

- `baseURL`: `import.meta.env.VITE_API_BASE_URL`
- `withCredentials`: true
- `timeout`: 10000
- request interceptor で Bearer token を付与
- response interceptor で `Result<T>` の business error を処理
- 401 は login へ戻す
- 403 / 404 / 500 / 502 / 503 は error page へ遷移する

## 9. Backend との責務分担

| 領域 | Frontend | Backend |
|---|---|---|
| 認証 | token 保存、Bearer 付与 | JWT 発行、JWT 検証 |
| API 認可 | UI 表示補助 | `PermissionAuthorizationFilter` で強制 |
| メニュー | sidebar、breadcrumb | role-menu による menu tree 返却 |
| ルート | 静的 route | menu path/component を管理 |
| 権限 | permission code によるボタン制御候補 | method/path/code による API 制御 |

## 10. 今後の拡張

- menu `component` と frontend component registry を対応させて dynamic route を導入する。
- button permission directive を追加し、`permissions` による UI 制御を整理する。
- error page の表示内容を backend `Result` と統一する。
- axios の refresh token / token expiration 対応を追加する。
