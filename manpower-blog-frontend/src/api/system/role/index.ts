import http from '@/api/common/http'
import type { Result } from '@/types/common/result'
import type { RoleAuthorizationVO, RoleVO } from '@/types/system/role/roleResponse'
import type { RoleAuthorizationSaveRequest, RoleSaveOrUpdateRequest } from '@/types/system/role/roleRequest'

/**
 * ロール一覧取得API
 */
export function listRoleApi() {
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
export function findRoleByIdApi(id: number) {
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

export function getRoleAuthorizationApi(id: number) {
  return http.get<Result<RoleAuthorizationVO>>(`/api/system/role/${id}/authorization`)
}

export function saveRoleAuthorizationApi(id: number, data: RoleAuthorizationSaveRequest) {
  return http.put<Result<void>>(`/api/system/role/${id}/authorization`, data)
}
