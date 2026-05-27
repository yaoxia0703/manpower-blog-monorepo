import http from '@/api/common/http'
import type { PageResult } from '@/types/common/pageResult'
import type { Result } from '@/types/common/result'
import type { UserChangeStatusRequest, UserCreateRequest, UserPageQueryRequest, UserUpdateRequest, } from '@/types/system/user/userRequest'
import type { UserVO } from '@/types/system/user/userResponse'

export function getUserListApi(data: UserPageQueryRequest) {
  return http.get<Result<PageResult<UserVO>>>(
    '/api/admin/users/page',
    { params: data },
  )
}


export function changeUserStatusApi(data: UserChangeStatusRequest) {
  return http.patch<Result<void>>(`/api/admin/users/status`, data)
}

export function deleteUserApi(userId: number, accountId: number) {
  return http.delete<Result<void>>(`/api/admin/users`, { params: { userId, accountId } })
}

export function getUserDetailApi(userId: number, accountId: number) {
  return http.get<Result<UserVO>>(`/api/admin/users/detail`, { params: { userId, accountId } })
}

export function updateUserApi(data: UserUpdateRequest) {
  return http.put<Result<void>>(`/api/admin/users`, data)
}

export function createUserApi(data: UserCreateRequest) {
  return http.post<Result<void>>(`/api/admin/users`, data)
}