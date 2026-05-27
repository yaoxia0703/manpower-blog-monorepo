import type { MenuType } from '../../enums/menu'
import type { Status } from '../../enums/status'

/**
 * メニュー情報
 */
export interface MenuTreeVO {
  /**
   * メニューID
   */
  id: number

  /**
   * 親メニューID
   * 0 の場合はルートメニュー
   */
  parentId: number

  /**
   * メニュー名称
   */
  name: string

  /**
   * メニューアイコン
   */
  icon?: string

  /**
   * メニュー種別
   * DIRECTORY / MENU / BUTTON
   */
  type: MenuType

  /**
   * 表示順
   */
  sort?: number

  /**
   * メニュー状態
   * 0: 無効
   * 1: 有効
   */
  status?: Status

  /**
   * 対応するPermissionのパス
   * ルーティングマッチングおよびパンくずリスト用
   * ディレクトリの場合は null
   */
  permissionPath?: string

  /**
   * 子メニュー一覧
   */
  children?: MenuTreeVO[]
}

export interface MenuView extends MenuTreeVO {
  _loading: boolean
  children?: MenuView[]
}

export interface MenuOptionVo {
  id: number
  name: string
}


export interface MenuDetailVo {
  id: number
  parentId: number
  permissionId?: number
  name: string
  type: MenuType
  sort?: number
  icon?: string
  status: Status
}