import axios from 'axios'
import type {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  AxiosError,
  InternalAxiosRequestConfig,
} from 'axios'
import { ElMessage } from 'element-plus'
import {
  ApiError,
  resolveErrorMessage,
  toApiErrorPayload,
} from '@/utils/errorHandler'
import type { Result } from '@/types/common/result'
import router from '@/router'

type HttpMethodName = 'get' | 'post' | 'put' | 'delete' | 'patch'

/**
 * レスポンスインターセプター後の戻り値を表すHTTPクライアント型。
 * Axios全体の型定義に影響を与えず、このインスタンスのみデータを直接返す。
 */
type HttpClient = Omit<AxiosInstance, HttpMethodName> & {
  get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
  post<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<T>
  put<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<T>
  delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
  patch<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<T>
}

/**
 * 共通HTTPクライアント。
 * 認証トークン付与、エラー制御、レスポンス展開を一括して行う。
 */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
}) as HttpClient

const REDIRECT_ERROR_CODES = new Set([403, 404, 500, 502, 503, 504])

/**
 * Result<T>形式のレスポンスかどうかを判定する。
 */
function isResultShape(value: unknown): value is Result<unknown> {
  return toApiErrorPayload(value) !== null
}

/**
 * 共通エラーページへ遷移する必要があるかを判定する。
 */
function shouldRedirect(code?: number): boolean {
  return code !== undefined && REDIRECT_ERROR_CODES.has(code)
}

/**
 * サイレントモードかどうかを判定する。
 * trueの場合は共通UIメッセージのみ抑止する。
 */
function isSilent(config?: AxiosRequestConfig): boolean {
  return config?.silent === true
}

/**
 * 認証情報を破棄し、現在のURLを保持してログイン画面へ遷移する。
 */
function handleUnauthorized(): void {
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('user')

  const currentRoute = router.currentRoute.value
  if (currentRoute.path === '/login') return

  void router.replace({
    path: '/login',
    query: {
      redirect: currentRoute.fullPath,
    },
  })
}

/**
 * エラーコードに応じて共通エラー画面へ遷移する。
 */
function redirectToErrorPage(error: ApiError, code = error.code): void {
  if (code === 403) {
    void router.replace('/403')
    return
  }

  void router.replace({
    name: 'ErrorPage',
    params: {
      code: String(code),
    },
    state: {
      message: error.message,
    },
  })
}

/**
 * AxiosErrorを共通ApiErrorに変換する。
 */
function createApiError(error: AxiosError): ApiError {
  const status = error.response?.status
  const payload = toApiErrorPayload(error.response?.data) || {
    code: status || 0,
    message: status ? undefined : 'ネットワークエラーが発生しました',
  }

  return new ApiError(payload, status, error)
}

/* リクエストインターセプター */
http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = sessionStorage.getItem('token')

  if (token && !config.skipAuth) {
    config.headers.set('Authorization', `Bearer ${token}`)
  }

  return config
})

/* レスポンスインターセプター */
http.interceptors.response.use(
  (response: AxiosResponse) => {
    const data = response.data as unknown

    if (isResultShape(data) && data.code !== 200) {
      const payload = toApiErrorPayload(data)
      if (!payload) return Promise.reject(new Error('APIエラーレスポンスの形式が不正です'))

      const apiError = new ApiError(payload, response.status)

      if (apiError.code === 401 && !response.config.skipAuth) {
        handleUnauthorized()
      } else if (shouldRedirect(apiError.code)) {
        redirectToErrorPage(apiError)
      } else if (!isSilent(response.config)) {
        ElMessage.error(apiError.message)
      }

      return Promise.reject(apiError)
    }

    return response.data
  },
  (error: AxiosError) => {
    const apiError = createApiError(error)

    if ((apiError.status === 401 || apiError.code === 401) && !error.config?.skipAuth) {
      handleUnauthorized()
      return Promise.reject(apiError)
    }

    const redirectCode = shouldRedirect(apiError.code)
      ? apiError.code
      : apiError.status

    if (redirectCode !== undefined && shouldRedirect(redirectCode)) {
      redirectToErrorPage(apiError, redirectCode)
      return Promise.reject(apiError)
    }

    if (!isSilent(error.config)) {
      ElMessage.error(resolveErrorMessage(apiError))
    }

    return Promise.reject(apiError)
  },
)

export default http
