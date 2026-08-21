import type { HttpMethod } from "@/types/enums/permission"
import type { Status } from "@/types/enums/status"

export interface PermissionCreateRequest {
    menuId: number | null
    name: string
    code: string
    path: string
    method: HttpMethod
    sort: number
    status: Status
}

export interface PermissionUpdateRequest {
    menuId: number | null
    name: string
    path: string
    method: HttpMethod
    sort: number
    status: Status
}

export interface PermissionPageQueryRequest {
    pageNum?: number
    pageSize?: number
    keyword?: string
    menuId?: number
    method?: HttpMethod
    status?: Status
}
