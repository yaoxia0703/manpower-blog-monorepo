import type { AccountStatus, AccountType, VerifiedStatus } from "@/types/enums/account"
import type { Status } from "@/types/enums/status"

export interface UserVO {
    /**
     * ユーザーID
     */
    userId: number

    accountId: number


    /**
     * ニックネーム
     */
    nickName: string

    /**
     * ユーザー状態
     */
    userStatus: Status

    /**
     * アカウントタイプ
     */
    accountType: AccountType

    /**
     * アカウント値
     */
    accountValue: string

    /**
     * アカウント状態
     */
    accountStatus: AccountStatus

    /**
     * 認証状態
     */
    verifiedStatus: VerifiedStatus

    /**
     * 作成日時
     */
    createdAt: string

    roleId: number

    roleName: string

}

export interface UserView extends UserVO {
  /**
   * ローディング状態
   */
  _loading: boolean   
}