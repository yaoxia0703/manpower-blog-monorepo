import type { UserStatus } from '@/types/enums/user'
import type {
  AccountStatus,
  AccountType,
  VerifiedStatus,
} from '@/types/enums/account'
import type { MenuTreeVO } from '@/types/system/menu/menuResponse'

/**
 * ログインユーザー情報
 */
export interface LoginUser {
  /**
   * ユーザーID
   */
  userId: number

  /**
   * アカウントID
   */
  accountId: number

  /**
   * ニックネーム
   */
  nickName: string

  /**
   * アカウント種別
   */
  accountType: AccountType

  /**
   * アカウント値
   * メールアドレスまたは電話番号
   */
  accountValue: string

  /**
   * ユーザーステータス
   */
  userStatus: UserStatus

  /**
   * アカウントステータス
   */
  accountStatus: AccountStatus

  /**
   * 認証状態
   */
  verified: VerifiedStatus
}

/**
 * ログインレスポンス
 */
export interface LoginResponse {
  /**
   * JWTアクセストークン
   */
  accessToken: string

  /**
   * ログインユーザー情報
   */
  user: LoginUser
}

/**
 * ログインユーザー詳細情報レスポンス
 * ユーザー・メニュー・権限情報を返却する
 */
export interface MeResponse {
  /**
   * ユーザー情報
   */
  user: LoginUser

  /**
   * メニュー一覧
   */
  menus: MenuTreeVO[]  

  /**
   * 権限一覧
   */
  permissions: string[]
}