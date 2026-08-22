import type { HttpMethod } from "@/types/enums/permission"
import type { Status } from "@/types/enums/status"

export interface PermissionVO {
    id: number
    menuId: number | null
    menuName: string | null
    name: string
    code: string
    path: string
    method: HttpMethod
    sort: number
    status: Status
    createdAt: string
    updatedAt: string
}
