import { defineStore } from 'pinia'
import type { LoginUser } from '@/types/auth/loginUser'
import { getMeApi, logoutApi } from '@/api/auth'
import router from '@/router'
import { ElMessage } from 'element-plus'
import { usePermissionStore } from '@/stores/permissionStore'

/**
 * ユーザー状態管理Store
 * ログインユーザー情報および認証状態を管理する
 */
export const useUserStore = defineStore('user', {
    state: () => ({
        user: null as LoginUser | null,
        token: '' as string,
        loading: false as boolean,
    }),

    actions: {
        /**
         * JWTトークン設定
         */
        setToken(token: string) {
            this.token = token
            sessionStorage.setItem('token', token)
        },

        /**
         * ユーザー情報設定
         */
        setUser(user: LoginUser) {
            this.user = user
            sessionStorage.setItem('user', JSON.stringify(user))
        },

        /**
         * JWTトークン取得
         */
        getToken() {
            const storedToken = sessionStorage.getItem('token') || ''

            // 401処理などでStorageが破棄された場合はStoreも同期する
            if (this.token !== storedToken) {
                this.token = storedToken
            }

            return storedToken
        },

        /**
         * ユーザー情報取得
         */
        getUser() {
            if (this.user) return this.user
            const userStr = sessionStorage.getItem('user')
            return userStr ? JSON.parse(userStr) : null
        },

        /**
         * ユーザー情報初期化
         */
        clearUser() {
            this.user = null
            this.token = ''
            sessionStorage.removeItem('token')
            sessionStorage.removeItem('user')
        },

        /**
         * ログアウト処理
         */
        async logout() {
            try {
                await logoutApi()
                ElMessage.success('ログアウトしました')
            } catch (error) {
                console.error('Logout failed:', error)
            } finally {
                this.clearUser()
                const permissionStore = usePermissionStore()
                permissionStore.clearPermissions()
                router.replace('/login')
            }
        },

        /**
         * ログインユーザー情報取得
         */
        async fetchUser() {
            if (this.loading) return

            this.loading = true

            const permissionStore = usePermissionStore()

            try {
                const res = await getMeApi()

                // res は Result<MeResponse>、res.data は MeResponse
                const data = res.data

                this.setUser(data.user)
                permissionStore.setMenus(data.menus)
                permissionStore.setPermissions(data.permissions)
                permissionStore.setLoaded(true)

            } catch (error) {
                console.error('fetchUser failed:', error)
                this.clearUser()
                permissionStore.clearPermissions()
                throw error
            } finally {
                this.loading = false
            }
        },
    },
})
