import http from '@/api/common/http'
import type { Result } from '@/types/common/result'
import type { MenuCreateRequest, MenuUpdateRequest } from '@/types/system/menu/menuRequest'
import type { MenuDetailVo, MenuOptionVo, MenuTreeVO} from '@/types/system/menu/menuResponse'

/**
 * メニュー一覧取得API
 * システムのメニュー情報一覧を取得する
 */
export function getMenuTreeApi() {
  return http.get<Result<MenuTreeVO[]>>('/api/system/menu/tree')
}

export function getMenuOptionsApi() {
  return http.get<Result<MenuOptionVo[]>>('/api/system/menu/parent-options')
}

export function getMenuDetailApi(id: number) {
  return http.get<Result<MenuDetailVo>>(`/api/system/menu/${id}`)
}

export function createMenuApi(data: MenuCreateRequest) {
  return http.post<Result<number>>('/api/system/menu', data)
}
export function updateMenuApi(id: number, data: MenuUpdateRequest) {
  return http.put<Result<void>>(`/api/system/menu/${id}`, data)
}

export function getActiveMenuTreeApi() {
  return http.get<Result<MenuTreeVO[]>>('/api/system/menu/active-tree')
}