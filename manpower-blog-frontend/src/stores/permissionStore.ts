import { defineStore } from 'pinia'
import type { MenuTreeVO } from '@/types/system/menu/menuResponse'

/**
 * メニュー再帰検索処理
 * permissionPath でルートパスをマッチングする
 */
function findMenuRecursive(
    menus: MenuTreeVO[],
    targetPath: string,
    parentPath: MenuTreeVO[] = [],
): { node?: MenuTreeVO; path?: MenuTreeVO[] } | null {
    for (const menu of menus) {
        const currentPath = [...parentPath, menu]

        // permissionPath でマッチング（ディレクトリは null のためスキップ）
        if (menu.permissionPath && menu.permissionPath === targetPath) {
            return {
                node: menu,
                path: currentPath,
            }
        }

        // 子メニュー再帰検索
        if (menu.children?.length) {
            const result = findMenuRecursive(
                menu.children,
                targetPath,
                currentPath,
            )

            if (result) {
                return result
            }
        }
    }

    return null
}

/**
 * 権限状態管理Store
 * メニュー・権限情報を管理する
 */
export const usePermissionStore = defineStore(
    'permission',
    {
        state: () => ({
            menus: [] as MenuTreeVO[],
            permissions: [] as string[],
            loaded: false as boolean,
        }),

        actions: {
            /**
             * メニュー一覧設定
             */
            setMenus(menus: MenuTreeVO[]) {
                this.menus = menus
            },

            /**
             * 権限一覧設定
             */
            setPermissions(permissions: string[]) {
                this.permissions = permissions
            },

            /**
             * 読み込み状態設定
             */
            setLoaded(loaded: boolean) {
                this.loaded = loaded
            },

            /**
             * 権限情報初期化
             */
            clearPermissions() {
                this.menus = []
                this.permissions = []
                this.loaded = false
            },

            /**
             * パスからメニューを取得する
             * パンくずリストおよびルートガード用
             */
            findMenuByPath(path: string): MenuTreeVO | null {
                const result = findMenuRecursive(this.menus, path)
                return result?.node || null
            },

            /**
             * 権限コードによる権限判定
             * ボタン・メニューの表示制御用
             */
            hasPermission(permission: string | string[]): boolean {
                if (!permission) {
                    return true
                }

                // OR判定
                if (Array.isArray(permission)) {
                    return permission.some(p =>
                        this.permissions.includes(p),
                    )
                }

                return this.permissions.includes(permission)
            },

            /**
             * ルートアクセス可否判定
             * permissions[] に対応する path が存在するか確認する
             */
            hasRoutePermission(path: string): boolean {
                // トップレベルまたは認証不要のパスは許可
                if (!path || path === '/') {
                    return true
                }

                // menus の permissionPath に一致するものが存在すれば許可
                return findMenuRecursive(this.menus, path) !== null
            },

            /**
             * パンくずリスト用メニューパス取得
             */
            findMenuPath(path: string): MenuTreeVO[] {
                const result = findMenuRecursive(this.menus, path)
                return result?.path || []
            }
        },
    },
)