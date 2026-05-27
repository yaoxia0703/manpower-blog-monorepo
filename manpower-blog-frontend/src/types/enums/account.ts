/**
 * アカウント状態定義
 */
export const ACCOUNT_STATUS = {
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
 * アカウント状態型
 */
export type AccountStatus =
  (typeof ACCOUNT_STATUS)[keyof typeof ACCOUNT_STATUS]

/**
 * 認証状態定義
 */
export const VERIFIED_STATUS = {
  /**
   * 未認証
   */
  UNVERIFIED: 0,

  /**
   * 認証済み
   */
  VERIFIED: 1,
} as const

/**
 * 認証状態型
 */
export type VerifiedStatus =
  (typeof VERIFIED_STATUS)[keyof typeof VERIFIED_STATUS]

/**
 * アカウント種別定義
 */
export const ACCOUNT_TYPE = {
  /**
   * メールアドレス
   */
  EMAIL: 'EMAIL',

  /**
   * 電話番号
   */
  PHONE: 'PHONE',
} as const

/**
 * アカウント種別型
 */
export type AccountType =
  (typeof ACCOUNT_TYPE)[keyof typeof ACCOUNT_TYPE]