import type { AccountType } from '@/types/enums/account'

/**
 * ログインリクエスト
 * ログイン時に使用する認証情報
 */
export interface LoginRequest {
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
     * パスワード
     */
    password: string
}