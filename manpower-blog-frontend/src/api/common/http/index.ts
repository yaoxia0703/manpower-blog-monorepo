import axios from 'axios'
import type {
  AxiosInstance,
  AxiosResponse,
  AxiosError,
  InternalAxiosRequestConfig,
} from 'axios'
import { ElMessage } from 'element-plus'
import { resolveErrorMessage } from '@/utils/errorHandler'
import type { Result } from '@/types/common/result'
import router from '@/router'

/**
 * 共通HTTPクライアント
 * 認証トークン付与・共通エラーハンドリング・レスポンス制御を行う
 */
const http: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
  timeout: 10000,
})

/**
 * Result<T> 形式のレスポンスかどうかを判定する
 */
function isResultShape(x: unknown): x is Result<unknown> {
  return (
    typeof x === 'object' &&
    x !== null &&
    'code' in x &&
    typeof (x as Result<unknown>).code === 'number'
  )
}

/**
 * エラーページへ遷移する必要があるかを判定する
 */
function shouldRedirect(code?: number): boolean {
  if (!code) return false
  return [403, 404, 500, 502, 503].includes(code)
}

/**
 * サイレントモードかどうかを判定する
 * true の場合は共通エラーメッセージを表示しない
 */
function isSilent(config?: { silent?: boolean }): boolean {
  return config?.silent === true
}

/* リクエストインターセプター */
http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = sessionStorage.getItem('token')

  // JWTトークンを自動付与
  if (token) {
    config.headers.set('Authorization', `Bearer ${token}`)
  }

  return config
})

/* レスポンスインターセプター */
http.interceptors.response.use(
  (res: AxiosResponse) => {
    const data = res.data as unknown
    const silent = isSilent(res.config)

    // 業務エラー処理（Result<T>）
    if (isResultShape(data) && data.code !== 200) {
      // エラーページへ遷移
      if (shouldRedirect(data.code)) {
        if (data.code === 403) {
          router.replace('/403')
        } else {
          router.replace({
            name: 'ErrorPage',
            state: {
              code: String(data.code),
              message: data.message,
            },
          })
        }
      } else if (!silent) {
        let msg = data.message

        // フィールド単位エラーを優先表示
        const items =
          (data as any)?.data?.items ||
          (data as any)?.data?.errors

        if (Array.isArray(items) && items.length > 0) {
          msg = items[0].message || items[0].key || msg
        }

        ElMessage.error(msg ?? 'エラーが発生しました')
      }

      return Promise.reject({
        ...data,
        __isBizError: true,
      })
    }

    return res.data as any
  },

  (error: AxiosError) => {
    const status = error.response?.status
    const silent = isSilent(error.config)

    // 未認証（401）
    if (status === 401) {
      sessionStorage.clear()
      router.replace('/login')

      return Promise.reject(error)
    }

    // エラーページへ遷移するステータス
    if (shouldRedirect(status)) {
      const body = error.response?.data as any

      const code =
        typeof body?.code === 'number'
          ? body.code
          : status ?? 500

      const message =
        typeof body?.message === 'string'
          ? body.message
          : undefined

      if (code === 403) {
        router.replace('/403')
      } else {
        router.replace({
          name: 'ErrorPage',
          state: {
            code: String(code),
            message,
          },
        })
      }

      return Promise.reject(error)
    }

    // 共通エラーメッセージ表示
    if (!silent) {
      const extracted = resolveErrorMessage(error)

      ElMessage.error(
        typeof extracted === 'string' && extracted.trim()
          ? extracted
          : 'ネットワークエラーが発生しました',
      )
    }

    return Promise.reject(error)
  },
)

declare module 'axios' {
  interface AxiosInstance {
    get<T = any>(url: string, config?: any): Promise<T>
    post<T = any>(url: string, data?: any, config?: any): Promise<T>
    put<T = any>(url: string, data?: any, config?: any): Promise<T>
    delete<T = any>(url: string, config?: any): Promise<T>
    patch<T = any>(url: string, data?: any, config?: any): Promise<T>
  }
}

export default http
