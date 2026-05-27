# API 設計書（manpower-blog-frontend）

## 1. 目的

本書は `manpower-blog-frontend` が利用する backend API と frontend 側の API client 設計を整理する。

frontend は `src/api/**` に API client を置き、backend の `Result<T>` を前提として通信する。

## 2. 共通 HTTP 設計

### 2.1 Axios

共通 client: `src/api/common/http/index.ts`

設定:

| 項目 | 値 |
|---|---|
| `baseURL` | `VITE_API_BASE_URL` |
| `withCredentials` | `true` |
| `timeout` | `10000` |

### 2.2 Request interceptor

`sessionStorage.token` が存在する場合、全 request に以下を付与する。

```http
Authorization: Bearer <token>
```

### 2.3 Response interceptor

backend response が `Result<T>` 形式で、`code !== 200` の場合は business error として扱う。

| 状態 | frontend 処理 |
|---|---|
| 401 | session を clear して `/login` へ遷移 |
| 403 / 404 / 500 / 502 / 503 | error page へ遷移 |
| その他 business error | Element Plus message で表示 |

## 3. Auth API

client: `src/api/auth/index.ts`

| Function | Method | Path | 説明 |
|---|---|---|---|
| `loginApi` | POST | `/api/system/auth/login` | ログイン |
| `logoutApi` | POST | `/api/system/auth/logout` | ログアウト |
| `getMeApi` | GET | `/api/system/auth/me` | ログインユーザー、menu、permission 取得 |

`getMeApi` の結果は以下へ保存する。

- `userStore.user`
- `permissionStore.menus`
- `permissionStore.permissions`

## 4. User API

client: `src/api/system/user/index.ts`

| Function | Method | Path |
|---|---|---|
| `getUserListApi` | GET | `/api/system/user/page` |
| `changeUserStatusApi` | PATCH | `/api/system/user/status` |
| `deleteUserApi` | DELETE | `/api/system/user` |
| `getUserDetailApi` | GET | `/api/system/user/detail` |
| `updateUserApi` | PUT | `/api/system/user` |
| `createUserApi` | POST | `/api/system/user` |

## 5. Role API

client: `src/api/system/role/index.ts`

| Function | Method | Path |
|---|---|---|
| `getRoleListApi` | GET | `/api/system/role/list` |
| `createRoleApi` | POST | `/api/system/role` |
| `getRoleDetailApi` | GET | `/api/system/role/{id}` |
| `updateRoleApi` | PUT | `/api/system/role/{id}` |
| `changeRoleStatusApi` | PATCH | `/api/system/role/{id}/status` |
| `deleteRoleApi` | DELETE | `/api/system/role/{id}` |
| `getRolePermissionsApi` | GET | `/api/system/role/{id}/permissions` |
| `assignRolePermissionsApi` | PUT | `/api/system/role/{id}/permissions` |
| `getRoleMenusApi` | GET | `/api/system/role/{id}/menus` |
| `assignRoleMenusApi` | PUT | `/api/system/role/{id}/menus` |

## 6. Permission API

client: `src/api/system/permission/index.ts`

| Function | Method | Path |
|---|---|---|
| `getPermissionTreeApi` | GET | `/api/system/permission/tree` |
| `getPermissionOptionsApi` | GET | `/api/system/permission/parent-options` |
| `createPermissionApi` | POST | `/api/system/permission` |
| `updatePermissionApi` | PUT | `/api/system/permission/{id}` |
| `getPermissionDetailApi` | GET | `/api/system/permission/{id}` |
| `deletePermissionApi` | DELETE | `/api/system/permission/{id}` |

Permission は API 認可データである。frontend では権限管理画面とロール権限割当に利用する。

主な項目:

- `name`
- `code`
- `type`
- `path`
- `method`
- `status`

## 7. Menu API

client: `src/api/system/menu/index.ts`

| Function | Method | Path |
|---|---|---|
| `getMenuTreeApi` | GET | `/api/system/menu/tree` |
| `getMenuOptionsApi` | GET | `/api/system/menu/parent-options` |
| `getMenuDetailApi` | GET | `/api/system/menu/{id}` |
| `createMenuApi` | POST | `/api/system/menu` |
| `updateMenuApi` | PUT | `/api/system/menu/{id}` |
| `getActiveMenuTreeApi` | GET | `/api/system/menu/active-tree` |

Menu は frontend navigation データである。

主な項目:

- `name`
- `parentId`
- `path`
- `component`
- `type`
- `sort`
- `icon`
- `status`

## 8. Route と Menu の対応

現在の frontend route は静的であるため、backend menu の `path` は frontend route と一致させる。

| Menu path | Frontend route |
|---|---|
| `/system/dashboard` | dashboard |
| `/system/user` | user |
| `/system/role` | role |
| `/system/permission` | permission |
| `/system/menu` | menu |

`component` は将来 dynamic route に切り替えるための key として保持する。

## 9. API 認可との関係

frontend は API 認可を最終保証しない。最終的な強制は backend の `PermissionAuthorizationFilter` が行う。

frontend の責務:

- token を付与する
- menu tree による画面表示を行う
- permissions による UI 表示制御を行う
- 403 などの error response を適切に表示する

backend の責務:

- JWT を検証する
- user に紐づく API permissions を取得する
- request method/path と permission method/path を照合する
- 権限なしの場合 403 を返す
