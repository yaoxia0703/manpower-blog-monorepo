import axios from 'axios'

interface ErrorItem {
  key?: string
  message?: string
}

interface ErrorData {
  errors?: ErrorItem[]
  items?: ErrorItem[]
}

/**
 * APIエラーレスポンスの共通構造
 */
export interface ApiErrorPayload {
  code: number
  message?: string
  data?: unknown
  detail?: string
  traceId?: string
}

const DEFAULT_ERROR_MESSAGE = 'エラーが発生しました'

const STATUS_MESSAGES: Readonly<Record<number, string>> = {
  400: 'リクエストが不正です (400)',
  401: '認証が必要です (401)',
  403: '権限がありません (403)',
  404: 'リソースが見つかりません (404)',
  500: 'サーバー内部エラー (500)',
  502: 'サーバーが一時的に利用できません',
  503: 'サーバーが一時的に利用できません',
  504: 'サーバーが一時的に利用できません',
}

/**
 * APIの業務エラー。
 * レスポンス情報と元のエラーを保持する。
 */
export class ApiError extends Error {
  readonly payload: ApiErrorPayload
  readonly status?: number
  readonly originalError?: unknown

  constructor(
    payload: ApiErrorPayload,
    status?: number,
    originalError?: unknown,
  ) {
    super(resolveApiErrorMessage(payload))
    this.name = 'ApiError'
    this.payload = payload
    this.status = status
    this.originalError = originalError
  }

  get code(): number {
    return this.payload.code
  }
}

/**
 * unknown値からAPIエラーレスポンスを取得する。
 */
export function toApiErrorPayload(value: unknown): ApiErrorPayload | null {
  if (typeof value !== 'object' || value === null || !('code' in value)) {
    return null
  }

  const candidate = value as Partial<ApiErrorPayload>
  if (typeof candidate.code !== 'number') {
    return null
  }

  return {
    code: candidate.code,
    message: typeof candidate.message === 'string' ? candidate.message : undefined,
    data: candidate.data,
    detail: typeof candidate.detail === 'string' ? candidate.detail : undefined,
    traceId: typeof candidate.traceId === 'string' ? candidate.traceId : undefined,
  }
}

/**
 * APIレスポンスからユーザー向けメッセージを解決する。
 */
export function resolveApiErrorMessage(
  payload: ApiErrorPayload,
  fallback = DEFAULT_ERROR_MESSAGE,
): string {
  const data = payload.data as ErrorData | undefined
  const items = Array.isArray(data?.errors)
    ? data.errors
    : Array.isArray(data?.items)
      ? data.items
      : []

  const itemMessage = items
    .map(item => item.message || item.key)
    .find((message): message is string => Boolean(message?.trim()))

  if (itemMessage) return itemMessage
  if (payload.detail?.trim()) return payload.detail
  if (payload.message?.trim()) return payload.message

  return STATUS_MESSAGES[payload.code] || fallback
}

/**
 * 業務例外、HTTP例外、通常例外を共通メッセージに変換する。
 */
export function resolveErrorMessage(
  error: unknown,
  fallback = DEFAULT_ERROR_MESSAGE,
): string {
  if (error instanceof ApiError) {
    return resolveApiErrorMessage(error.payload, fallback)
  }

  if (axios.isAxiosError(error)) {
    const payload = toApiErrorPayload(error.response?.data)
    if (payload) {
      return resolveApiErrorMessage(payload, fallback)
    }

    const status = error.response?.status
    if (status && STATUS_MESSAGES[status]) {
      return STATUS_MESSAGES[status]
    }

    return error.response
      ? error.message || fallback
      : 'ネットワークエラーが発生しました'
  }

  if (error instanceof Error) {
    return error.message || fallback
  }

  return fallback
}
