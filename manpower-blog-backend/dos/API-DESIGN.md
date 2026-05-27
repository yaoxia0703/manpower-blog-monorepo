# API 設計書（manpower-blog-backend）

## 1. 文書目的
本書は、`manpower-blog-backend` の**現行実装コード**（Controller / DTO / VO / Security 設定 / ExceptionHandler / Mapper / SQL）を根拠に、API 設計を整理した技術設計書である。  
README 等の記述ではなく、実装を単一の正とする。

## 2. 対象モジュールと API 分離

### 2.1 モジュール分離（実装ベース）
- `blog-admin-api`
  - 管理系 API（認証、ロール、メニュー、ユーザー参照）
- `blog-portal-api`
  - 公開系 API（Portal ping、記事 API）
- 実行時は `blog-starter` が両 API モジュールを依存関係として取り込み、単一アプリで公開する。

### 2.2 URL 境界（現行）
- 管理系 API: `/api/system/**` + `/api/users/**`
- 公開系 API: `/api/portal/**` + `/api/articles/**`

> 注: `/api/users/**` は admin モジュール所属だが、`/api/system/**` 配下ではない。

---

## 3. 共通レスポンス仕様（`Result<T>`）

## 3.1 JSON 構造
全 Controller は `Result<T>` を返却する。

| フィールド | 型 | 説明 |
|---|---|---|
| `code` | int | アプリケーションコード（200/4xx/5xx など） |
| `message` | String | メッセージまたは i18n キー |
| `data` | T | 正常時データ（必要時のみ） |
| `traceId` | String | `TraceIdResponseAdvice` により付与（`Result` の場合） |
| `timestamp` | Long | `Result` 生成時の epoch millis |
| `detail` | String | 追加詳細（主にエラー時。prod ではマスクされる） |

- `@JsonInclude(NON_NULL)` のため `null` は出力されない。
- `Result.ok()` のデフォルト `message` は `success.ok`。

### 3.2 主なファクトリメソッド
- 正常系: `ok()`, `ok(data)`, `ok(data, msg)`, `okMsg(msg)`, `of(code, msg, data)`
- 異常系: `error(msg)`, `error(code, msg)`, `errorWithDetail(...)`, `fail(...)`
- 補助: `withDetail(...)`, `withTraceId(...)`

---

## 4. 例外と共通エラー応答

## 4.1 例外ハンドリング
`GlobalExceptionHandler`（`@RestControllerAdvice`）が以下を `Result` に統一する。

- `BizException`
- `MethodArgumentNotValidException` / `ConstraintViolationException`
- `MissingServletRequestParameterException`
- `DuplicateKeyException`
- `HttpMessageNotReadableException`
- `HttpRequestMethodNotSupportedException`
- `HttpMediaTypeNotSupportedException`
- `AccessDeniedException`
- `NoResourceFoundException`
- `MaxUploadSizeExceededException`
- `Exception`（その他）

## 4.2 i18n / detail / trace の扱い
- `messageSource` で i18n キーをロケール解決。
- `detail` は non-prod で返却、prod では `null`（`safeDetail`）。
- `traceId` は `TraceIdFilter` が MDC/レスポンスヘッダ `X-Trace-Id` に設定し、`Result` の場合は `TraceIdResponseAdvice` が body にも付与。

## 4.3 セキュリティ例外の応答差分
Security 設定の `authenticationEntryPoint` / `accessDeniedHandler` は `Result` ではなく固定 JSON を直接返す。

- 401: `{"code":401,"message":"認証エラー"}`
- 403: `{"code":403,"message":"権限がありません"}`

したがってこの経路では `traceId`/`timestamp`/`detail` は `Result` 形式で返らない。

---

## 5. Spring Security + JWT 認証・認可

## 5.1 SecurityFilterChain（現行）
- CSRF: 無効
- CORS: 許可 origin は `http://localhost:5173`
- Session: `STATELESS`
- Method Security: `@EnableMethodSecurity` 有効
- 認可ルール:
  - `permitAll`: `/api/system/auth/**`, `/error/**`, `/favicon.ico`
  - `authenticated`: `/api/system/**`
  - その他: `permitAll`
- `JwtAuthenticationFilter` を `UsernamePasswordAuthenticationFilter` の前に配置

### 5.2 JWT 発行
`POST /api/system/auth/login` 成功時に `JwtTokenProvider.generateToken(LoginUser)` で HS256 JWT を発行。

- issuer: `security.jwt.issuer`（既定 `springboot3web`）
- subject: `userId`
- exp: `security.jwt.expire-seconds`（既定 7200 秒、最低 60 秒）
- claims:
  - `roles`（カンマ連結）
  - `nickName`
  - `accountId`

レスポンスは以下 2 箇所にトークンを返却:
- Header: `Authorization: Bearer <token>`
- Body: `LoginResponse.accessToken`

### 5.3 JWT 検証と SecurityContext 構築
`JwtAuthenticationFilter` の処理:
1. `Authorization: Bearer <token>` を解釈
2. `JwtTokenProvider.validate(token)` で署名/issuer/期限検証
3. `userId` + `accountId` を claim から取得
4. `UserAuthorityProvider.loadPermissionCodes(userId)` で権限コード取得
5. 権限コードを `SimpleGrantedAuthority` 化して `SecurityContext` に設定
   - principal は `LoginPrincipal(userId, accountId)`

### 5.4 JWT フィルタ除外パス
`shouldNotFilter` は以下のみ除外:
- `/api/system/auth/login`
- `/api/system/auth/logout`
- `/error/`
- `/favicon.ico`

**重要**: `/api/system/auth/me` は除外対象ではないため、JWT フィルタが適用される。

---

## 6. `/me` API 設計（責務とレスポンス構造）

## 6.1 責務
`GET /api/system/auth/me` は、ログイン後の画面初期化に必要な**統合コンテキスト**を返す。

1. 現在ユーザー情報（`LoginUser`）
2. 利用可能メニュー（`List<MenuTreeVo>`）
3. 利用可能権限コード（`List<String>`）

## 6.2 実装フロー
- `SecurityContext` の `Authentication.principal` が `LoginPrincipal` であることを検証
- `userId/accountId` で `userAppService.getCurrentUserContext(...)` を取得
- `menuAppService.selectMenusByUserId(userId)` を取得
- `permissionAppService.selectPermissionCodesByUserId(userId)` を取得
- `MeResponse { user, menus, permissions }` を返却

未認証/不正 principal/ユーザー未取得時は `BizException(UNAUTHORIZED)` を送出。

## 6.3 `MeResponse` 構造
```json
{
  "code": 200,
  "message": "success.ok",
  "data": {
    "user": {
      "userId": 1,
      "accountId": 1,
      "nickName": "...",
      "accountType": "EMAIL",
      "accountValue": "...",
      "roleNames": ["管理者"]
    },
    "menus": [
      {
        "id": 1,
        "parentId": 0,
        "name": "システム管理",
        "path": "/admin",
        "permission": null,
        "type": "DIRECTORY",
        "children": []
      }
    ],
    "permissions": ["sys:role:list", "sys:role:create"]
  },
  "traceId": "...",
  "timestamp": 0
}
```

---

## 7. RBAC 設計（User / Role / Permission / Menu）

## 7.1 データ関係
実装・SQL からの関係は以下。

- User - Role: `t_sys_user_role`（多対多）
- Role - Permission: `t_sys_role_permission`（多対多）
- Role - Menu: `t_sys_role_menu`（多対多）
- User - Account: `t_sys_user_account`（ログイン識別）

### 7.2 認可（Authority）決定経路
Spring Security の `GrantedAuthority` は**Permission.code** 由来。

`userId -> user_role -> role_permission -> permission.code`

`SystemUserAuthorityProvider` が `PermissionAppService.selectPermissionCodesByUserId` を呼び出し、`SimpleGrantedAuthority` を構築する。

### 7.3 `@PreAuthorize` の実装状況
管理 API は permission code を直接要求する（例）。

- `hasAuthority('sys:role:list')`
- `hasAuthority('sys:role:detail')`
- `hasAuthority('sys:menu:list')`
- `hasAuthority('sys:menu:update')`

### 7.4 Menu と Permission の関係（重要）
現行実装には **2 系統**が存在する。

1. **認可評価で使う権限**: `t_sys_permission.code`（Role-Permission 経由）
2. **UI メニュー属性としての permission**: `t_sys_menu.permission`（メニュー項目属性）

つまり、`t_sys_menu.permission` は画面表示上の属性であり、Spring Security の認可判定は `t_sys_permission` 側を使用する。

### 7.5 Menu 取得経路
`/me` の menus は `t_sys_role_menu` 経由で取得される。

`userId -> user_role -> role_menu -> menu(type IN 1,2, status=1)`

ボタン種別（type=3）は `/me` メニューには含めない実装。

---

## 8. API 一覧（Controller 実装ベース）

## 8.1 Admin API

### 8.1.1 認証 API（`/api/system/auth`）

| Method | Path | 処理 | Request | Response |
|---|---|---|---|---|
| POST | `/api/system/auth/login` | ログイン + JWT発行 | `LoginRequest` | `Result<LoginResponse<LoginUser>>` |
| POST | `/api/system/auth/logout` | ログアウト（SecurityContext clear） | なし | `Result<Void>` |
| GET | `/api/system/auth/me` | 現在ユーザー統合情報取得 | なし（Bearer 必須） | `Result<MeResponse>` |

### 8.1.2 ロール API（`/api/system/role`）

| Method | Path | 認可 | Response |
|---|---|---|---|
| GET | `/pageList` | `sys:role:list` | `Result<JoinPageResult<Role>>` |
| GET | `/{id}` | `sys:role:detail` | `Result<Role>` |
| POST | `` | `sys:role:create` | `Result<Long>` |
| PUT | `/{id}` | `sys:role:update` | `Result<Void>` |
| DELETE | `/{id}` | `sys:role:delete` | `Result<Void>` |
| PATCH | `/{id}/status` | `sys:role:changeStatus` | `Result<Void>` |

### 8.1.3 メニュー API（`/api/system/menu`）

| Method | Path | 認可 | Response |
|---|---|---|---|
| GET | `` | `sys:menu:list` | `Result<List<MenuTreeVo>>` |
| GET | `/{id}` | `sys:menu:detail` | `Result<MenuDetailVo>` |
| POST | `` | `sys:menu:create` | `Result<Long>` |
| PUT | `/{id}` | `sys:menu:update` | `Result<Void>` |
| DELETE | `/{id}` | `sys:menu:delete` | `Result<Void>` |
| PATCH | `/{id}/status` | `sys:menu:changeStatus` | `Result<Void>` |

### 8.1.4 ユーザー参照 API

| Method | Path | 備考 |
|---|---|---|
| GET | `/api/users/{id}` | `SecurityConfig` 上は `anyRequest().permitAll()` のため匿名アクセス可能 |

## 8.2 Portal API

### 8.2.1 ヘルス
| Method | Path | Response |
|---|---|---|
| GET | `/api/portal/ping` | `Result<String>` (`"pong"`) |

### 8.2.2 記事 API（`/api/articles`）
| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/add` | `ArticleCreateReq` | `Result<Long>` |
| GET | `/pageList` | `ArticleQueryRequest`（`@RequestBody`） | `Result<JoinPageResult<ArticleVo>>` |
| PUT | `/update` | `ArticleUpdateReq` | `Result<Boolean>` |
| DELETE | `/{id}` | Path id | `Result<Boolean>` |

---

## 9. 主要 DTO / VO（現行 API で重要なもの）

## 9.1 認証系
- `LoginRequest`
  - `accountType`, `accountValue`, `password`
- `LoginResponse<LoginUser>`
  - `accessToken`, `user`
- `LoginUser`
  - `userId`, `accountId`, `nickName`, `accountType`, `accountValue`, `roleNames`, `permissions`
- `LoginPrincipal`
  - `userId`, `accountId`
- `MeResponse`
  - `user: LoginUser`
  - `menus: List<MenuTreeVo>`
  - `permissions: List<String>`

## 9.2 RBAC 系
- `RoleSaveOrUpdateRequest`: `code`, `name`, `sort`, `status`
- `MenuSaveOrUpdateRequest`: `parentId`, `name`, `path`, `component`, `permission`, `type`, `sort`, `icon`, `status`
- `PermissionSaveOrUpdateRequest`: `name`, `code`, `type`, `path`, `method`, `status`（種別別バリデーションあり）

---


## 10. 本改訂での更新方針
本書は、以下を実装に合わせて更新済み。

- Spring Security + JWT の最新実装差分（`/me` の扱い含む）
- `/me` API の責務とレスポンス構造
- RBAC の実データフロー（user/role/permission/menu）
- Menu と Permission の関係整理
- Admin API / Portal API の分離と URL 境界
- `Result<T>` 統一レスポンスと共通エラー応答（Security 例外の差分含む）
