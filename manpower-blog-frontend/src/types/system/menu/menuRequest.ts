import type { MenuType } from '../../enums/menu'
import type { Status } from '../../enums/status'

/**
 * メニュー新規作成リクエスト
 */
export interface MenuCreateRequest {
  /**
   * 親メニューID（トップレベルは0）
   */
  parentId: number

  /**
   * メニュー名（100文字以内）
   */
  name: string

  path?: string | null

  component?: string | null

  /**
   * メニュー種別
   */
  type: MenuType

  /**
   * 表示順（0以上）
   */
  sort: number

  /**
   * アイコン（100文字以内）
   */
  icon?: string

  /**
   * 状態
   */
  status: Status
}

/**
 * メニュー更新リクエスト（名称・表示順・アイコン・状態のみ変更可）
 */
export interface MenuUpdateRequest {
  /**
   * メニュー名（100文字以内）
   */
  name: string

  path?: string | null

  component?: string | null

  /**
   * 表示順（0以上）
   */
  sort: number

  /**
   * アイコン（100文字以内）
   */
  icon?: string

  /**
   * 状態
   */
  status: Status
}
