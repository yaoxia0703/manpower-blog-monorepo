// types/role/roleRequest.ts

/**
 * ロール保存・更新リクエスト
 */
export interface RoleSaveOrUpdateRequest {
  /**
   * ロールコード
   */
  code: string

  /**
   * ロール名
   */
  name: string

  /**
   * 表示順
   */
  sort: number

  /**
   * 状態
   */
  status: number
}