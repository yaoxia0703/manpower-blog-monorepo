/**
 * 状態定義
 * 有効・無効状態を表す
 */
export const Status = {
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
 * 状態型
 */
export type Status =
  typeof Status[keyof typeof Status]