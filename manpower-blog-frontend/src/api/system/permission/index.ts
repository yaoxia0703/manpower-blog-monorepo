
import http from '@/api/common/http'
import type { Result } from '@/types/common/result'
import type { PermissionVO } from '@/types/system/permission/permissionResponse'
import type { PermissionCreateRequest, PermissionUpdateRequest } from '@/types/system/permission/permissionRequest'


export function getPermissionListApi() {
    return http.get<Result<PermissionVO[]>>('/api/system/permission/list')
}

export function createPermissionApi(data: PermissionCreateRequest) {
    return http.post<Result<number>>('/api/system/permission', data)
}

export function updatePermissionApi(id: number, data: PermissionUpdateRequest) {
    return http.put<Result<void>>(`/api/system/permission/${id}`, data)
}

export function getPermissionDetailApi(id: number) {
    return http.get<Result<PermissionVO>>(`/api/system/permission/${id}`)
}

export function deletePermissionApi(id: number) {
    return http.delete<Result<void>>(`/api/system/permission/${id}`)

}
