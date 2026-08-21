
import http from '@/api/common/http'
import type { PageResult } from '@/types/common/pageResult'
import type { Result } from '@/types/common/result'
import type { PermissionVO } from '@/types/system/permission/permissionResponse'
import type {
    PermissionCreateRequest,
    PermissionPageQueryRequest,
    PermissionUpdateRequest,
} from '@/types/system/permission/permissionRequest'


export function pagePermissionApi(params: PermissionPageQueryRequest) {
    return http.get<Result<PageResult<PermissionVO>>>(
        '/api/system/permission/page',
        { params },
    )
}

export function createPermissionApi(data: PermissionCreateRequest) {
    return http.post<Result<number>>('/api/system/permission', data)
}

export function updatePermissionApi(id: number, data: PermissionUpdateRequest) {
    return http.put<Result<void>>(`/api/system/permission/${id}`, data)
}

export function findPermissionByIdApi(id: number) {
    return http.get<Result<PermissionVO>>(`/api/system/permission/${id}`)
}

export function deletePermissionApi(id: number) {
    return http.delete<Result<void>>(`/api/system/permission/${id}`)

}
