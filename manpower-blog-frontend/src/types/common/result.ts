// src/types/common/result.ts

/**
 * 共通レスポンス構造
 * バックエンドAPIの統一返却形式
 */
export interface Result<T> {
  /**
   * レスポンスコード
   */
  code: number

  /**
   * レスポンスメッセージ
   */
  message: string

  /**
   * レスポンスデータ
   */
  data: T

  /**
   * リクエスト追跡用ID
   */
  traceId?: string

  /**
   * サーバー応答時刻（エポックミリ秒）
   */
  timestamp?: number

  /**
   * 開発環境向け詳細メッセージ
   */
  detail?: string
}
