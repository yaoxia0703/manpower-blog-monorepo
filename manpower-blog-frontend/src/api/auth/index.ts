import http from '@/api/common/http'
import type { Result } from '@/types/common/result'
import type { LoginResponse, MeResponse } from '@/types/auth/loginUser'
import type { LoginRequest } from '@/types/auth/loginRequest'

/**
 * ログインAPI
 * ユーザー認証を行い、JWTトークンおよびログイン情報を取得する
 */
export function loginApi(loginRequest: LoginRequest) {
  return http.post<Result<LoginResponse>>(
    '/api/system/auth/login',
    loginRequest,
    { silent: true } // ログイン失敗時のエラーメッセージを個別制御する
  )
}

/**
 * ログアウトAPI
 * サーバー側のログイン状態を破棄する
 */
export function logoutApi() {
  return http.post<Result<null>>(
    '/api/system/auth/logout',
    {},
    { silent: true } // セッション切れ時に共通エラーメッセージを表示しない
  )
}

/**
 * ログインユーザー情報取得API
 * メニュー・権限・ユーザー情報を取得する
 */
export function getMeApi() {
  return http.get<Result<MeResponse>>(
    '/api/system/auth/me',
    { silent: true } // ログイン期限切れ時に共通エラーメッセージを表示しない
  )
}