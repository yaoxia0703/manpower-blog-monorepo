import type { MenuType } from '../../enums/menu'
import type { Status } from '../../enums/status'

export interface MenuTreeVO {
  id: number
  parentId: number
  name: string
  path?: string | null
  component?: string | null
  icon?: string
  type: MenuType
  sort?: number
  status?: Status
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
  name: string
  path?: string | null
  component?: string | null
  type: MenuType
  sort?: number
  icon?: string
  status: Status
}
