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
}