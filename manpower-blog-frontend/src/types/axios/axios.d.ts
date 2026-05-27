import 'axios' // axios 型定義拡張用

declare module 'axios' {
  /**
   * 外部利用向け Axios リクエスト設定拡張
   */
  export interface AxiosRequestConfig {
    /**
     * true の場合、共通UIエラーメッセージを表示しない
     */
    silent?: boolean

    /**
     * true の場合、Authorizationヘッダーを付与しない
     */
    skipAuth?: boolean

    /**
     * リクエスト識別子
     * ログ追跡などに利用する
     */
    requestId?: string
  }

  /**
   * Axios 内部用リクエスト設定拡張
   */
  export interface InternalAxiosRequestConfig {
    /**
     * サイレントモード
     */
    silent?: boolean

    /**
     * Authorizationヘッダー付与スキップ
     */
    skipAuth?: boolean

    /**
     * リクエスト識別子
     */
    requestId?: string
  }
}