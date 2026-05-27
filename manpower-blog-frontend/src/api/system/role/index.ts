import http from '@/api/common/http'
import type { Result } from '@/types/common/result'
import type { RoleVO } from '@/types/system/role/roleResponse'
import type { RoleSaveOrUpdateRequest } from '@/types/system/role/roleRequest'

/**
 * ロール一覧取得API
 */
export function getRoleListApi() {
  return http.get<Result<RoleVO[]>>('/api/system/role/list')
}

/**
 * ロール新規作成API
 */
export function createRoleApi(data: RoleSaveOrUpdateRequest) {
  return http.post<Result<number>>('/api/system/role', data)
}

/**
 * ロール詳細取得API
 */
export function detailRoleApi(id: number) {
  return http.get<Result<RoleVO>>(`/api/system/role/${id}`)
}

/**
 * ロール更新API
 */
export function updateRoleApi(id: number, data: RoleSaveOrUpdateRequest) {
  return http.put<Result<void>>(`/api/system/role/${id}`, data)
}

/**
 * ロール状態変更API
 */
export function changeRoleStatusApi(id: number, status: number) {
  return http.patch<Result<void>>(`/api/system/role/${id}/status`, { status })
}

/**
 * ロール削除API
 */
export function deleteRoleApi(id: number) {
  return http.delete<Result<void>>(`/api/system/role/${id}`)
}

/**
 * ロールに紐づく権限IDリスト取得API
 */
export function getRolePermissionsApi(id: number) {
  return http.get<Result<number[]>>(`/api/system/role/${id}/permissions`)
}

/**
 * ロール権限割り当てAPI
 */
export function assignRolePermissionsApi(id: number, permissionIds: number[]) {
  return http.put<Result<void>>(`/api/system/role/${id}/permissions`, { permissionIds })
}


/**
 * ロールに紐づくメニューIDリスト取得API
 */
export function getRoleMenusApi(id: number) {
  return http.get<Result<number[]>>(`/api/system/role/${id}/menus`)
}

/**
 * ロールメニュー割り当てAPI
 */
export function assignRoleMenusApi(id: number, menuIds: number[]) {
  return http.put<Result<void>>(`/api/system/role/${id}/menus`, { menuIds })
}