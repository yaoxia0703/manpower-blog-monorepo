export const PermissionType = {
    /**
     * メニュー
     */
    MENU: 1,
    /**
     * ボタン
     */
    BUTTON: 2,

    /**
     * API
     */
    API: 3,
} as const

export type PermissionType =
    typeof PermissionType[keyof typeof PermissionType]

export const HttpMethod = {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    DELETE: 'DELETE',
    PATCH: 'PATCH',
} as const

export type HttpMethod =
    typeof HttpMethod[keyof typeof HttpMethod]