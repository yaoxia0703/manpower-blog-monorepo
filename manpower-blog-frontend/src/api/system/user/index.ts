import http from '@/api/common/http'
import type { PageResult } from '@/types/common/pageResult'
import type { Result } from '@/types/common/result'
import type { UserChangeStatusRequest, UserCreateRequest, UserPageQueryRequest, UserUpdateRequest, } from '@/types/system/user/userRequest'
import type { UserVO } from '@/types/system/user/userResponse'

export function pageUserApi(data: UserPageQueryRequest) {
  return http.get<Result<PageResult<UserVO>>>(
    '/api/system/user/page',
    { params: data },
  )
}


export function changeUserStatusApi(userId: number, data: UserChangeStatusRequest) {
  return http.patch<Result<void>>(`/api/system/user/${userId}/status`, data)
}

export function deleteUserApi(userId: number, accountId: number) {
  return http.delete<Result<void>>(`/api/system/user/${userId}`, { params: { accountId } })
}

export function findUserByIdApi(userId: number, accountId: number) {
  return http.get<Result<UserVO>>(`/api/system/user/${userId}`, { params: { accountId } })
}

export function updateUserApi(userId: number, data: UserUpdateRequest) {
  return http.put<Result<void>>(`/api/system/user/${userId}`, data)
}

export function createUserApi(data: UserCreateRequest) {
  return http.post<Result<void>>(`/api/system/user`, data)
}
