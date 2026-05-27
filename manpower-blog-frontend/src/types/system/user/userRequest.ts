import type { AccountType } from "@/types/enums/account";
import type { Status } from "@/types/enums/status";

export interface UserPageQueryRequest {
    keyword?: string,
    status?: Status,
    pageNum?: number,
    pageSize?: number,
}

export interface UserChangeStatusRequest {
    userId: number,
    accountId: number,
    status: Status
}

export interface UserUpdateRequest {
    userId: number,
    accountId: number,
    nickName: string,
    status: Status,
    roleId: number
}

export interface UserCreateRequest {
    nickName: string,

    roleId: number,

    accountType: AccountType,

    accountValue: string,

    password: string,

    status: Status
}