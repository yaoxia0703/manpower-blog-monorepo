/**
 * メニュー種別定義
 */
export const MenuType = {
  /**
   * ディレクトリ
   */
  DIRECTORY: 1,

  /**
   * メニュー
   */
  MENU: 2,

  /**
   * ボタン
   */
  // BUTTON: 3,
} as const

/**
 * メニュー種別型
 */
export type MenuType =
  typeof MenuType[keyof typeof MenuType]