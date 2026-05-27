import type { Status } from '../../enums/status'

/**
 * ロール情報
 */
export interface RoleVO {
  /**
   * ロールID
   */
  id: number

  /**
   * ロールコード
   */
  code: string

  /**
   * ロール名
   */
  name: string

  /**
   * 状態
   */
  status: Status

  /**
   * 表示順
   */
  sort: number

  createdAt: string

  updatedAt: string
}

/**
 * ロール画面表示用モデル
 * 画面制御用プロパティを含む
 */
export interface RoleView extends RoleVO {
  /**
   * ローディング状態
   */
  _loading: boolean
}