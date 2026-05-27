/**
 * ユーザー状態定義
 */
export const USER_STATUS = {
  /**
   * 無効
   */
  DISABLED: 0,

  /**
   * 有効
   */
  ENABLED: 1,
} as const

/**
 * ユーザー状態型
 */
export type UserStatus =
  (typeof USER_STATUS)[keyof typeof USER_STATUS]