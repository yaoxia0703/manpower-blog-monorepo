import type { HttpMethod, PermissionType } from "@/types/enums/permission"
import type { Status } from "@/types/enums/status"

export interface PermissionPageQueryRequest {
    keyword?: string
    permissionType?: PermissionType
    method?: HttpMethod
    status?: Status
}

export interface PermissionCreateRequest {
    parentId: number
    name: string
    code: string
    type: PermissionType
    path?: string
    method?: HttpMethod
    sort: number
    status: Status
}

export interface PermissionUpdateRequest {
    parentId: number
    name: string
    type: PermissionType
    path?: string
    method?: HttpMethod
    sort: number
    status: Status
}

export interface PermissionChangeStatusRequest {
    status: Status
}