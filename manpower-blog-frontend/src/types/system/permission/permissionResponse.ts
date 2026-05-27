import { HttpMethod, PermissionType } from "@/types/enums/permission"
import type { Status } from "@/types/enums/status"

// ツリーVO（バックエンドのPermissionTreeVoに対応）
export interface PermissionTreeVO {
    id: number
    name: string
    code: string
    type: PermissionType
    path: string | null
    method: HttpMethod | null
    sort: number
    status: Status
    createdAt: string
    updatedAt: string
    children?: PermissionTreeVO[]
}

// テーブル表示用（_loadingを追加）
export interface PermissionTreeView extends PermissionTreeVO {
    _loading: boolean
    children?: PermissionTreeView[]
}

export interface PermissionOptionVo {
    id: number
    name: string
}