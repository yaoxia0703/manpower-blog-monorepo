// utils/errorHandler.ts
import type { AxiosError } from 'axios'

/**
 * エラーメッセージ解析処理
 * 業務例外・AxiosError・通常Error を統一処理する
 */
export function resolveErrorMessage(
  error: unknown,
  fallback = 'エラーが発生しました',
): string {
  const err = error as any

  /**
   * 業務例外処理（インターセプターが reject した __isBizError）
   */
  if (err?.__isBizError) {
    const errors = err.data?.errors

    // フィールドバリデーションエラー優先
    if (Array.isArray(errors) && errors.length > 0) {
      return errors[0].key || errors[0].message || fallback
    }

    return err.message || fallback
  }

  /**
   * AxiosError 処理（ネットワーク・HTTPエラー）
   */
  if (isAxiosError(error)) {
    const axiosErr = error as AxiosError<any>
    const res = axiosErr.response?.data

    // 1. フィールドバリデーションエラー
    const errors = res?.data?.errors
    if (Array.isArray(errors) && errors.length > 0) {
      return errors[0].key || errors[0].message || fallback
    }

    // 2. 旧構造 items 対応
    const items = res?.data?.items
    if (Array.isArray(items) && items.length > 0) {
      return items[0].message || fallback
    }

    // 3. detail メッセージ
    if (res?.detail) return res.detail

    // 4. 共通メッセージ
    if (res?.message) return res.message

    // 5. HTTP ステータス別メッセージ
    const status = axiosErr.response?.status
    if (status) {
      switch (status) {
        case 400: return 'リクエストが不正です (400)'
        case 401: return '認証が必要です (401)'
        case 403: return '権限がありません (403)'
        case 404: return 'リソースが見つかりません (404)'
        case 500: return 'サーバー内部エラー (500)'
        case 502:
        case 503:
        case 504: return 'サーバーが一時的に利用できません'
      }
    }

    return axiosErr.message || fallback
  }

  /**
   * 標準Error処理
   */
  if (error instanceof Error) {
    return error.message
  }

  return fallback
}

/**
 * AxiosError 判定処理
 */
function isAxiosError(error: unknown): error is AxiosError {
  return (
    typeof error === 'object' &&
    error !== null &&
    'isAxiosError' in error
  )
}