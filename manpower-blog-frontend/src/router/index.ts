import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permissionStore'

/**
 * 静的ルート定義
 */
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/system/dashboard',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
  },
  {
    path: '/system',
    name: 'System',
    component: () => import('@/layouts/system/SystemLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/system/dashboard/index.vue'),
        meta: { title: 'ダッシュボード' },
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: 'ユーザー管理' },
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: 'ロール管理' },
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/system/permission/index.vue'),
        meta: { title: '権限管理' },
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: 'メニュー管理' },
      },

    ],
  },
]

/**
 * Vue Router インスタンス
 */
const router = createRouter({
  history: createWebHistory(),
  routes,
})

/**
 * グローバルルートガード
 * ログイン状態および権限チェックを行う
 */
router.beforeEach(async (to) => {
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  const token = userStore.getToken()

  // ログイン済みユーザーがログイン画面へアクセスした場合
  if (to.path === '/login' && token) {
    return '/system/dashboard'
  }

  // 認証が必要なルートか判定
  const requiresAuth = to.matched.some(
    record => record.meta.requiresAuth,
  )

  // 未ログインの場合はログイン画面へ遷移
  if (requiresAuth && !token) {
    return '/login'
  }

  // ページ更新時のユーザー情報復元
  if (token && !userStore.user) {
    try {
      await userStore.fetchUser()
    } catch {
      userStore.clearUser()
      return '/login'
    }
  }

  // 認証が必要なルートの権限チェック
  if (requiresAuth) {
    // permissionPath ベースでルートアクセス可否を判定
    // ダッシュボードは固定ルートのため許可
    const isDashboard = to.path === '/system/dashboard'

    if (!isDashboard && !permissionStore.hasRoutePermission(to.path)) {
      // 権限なし：将来的には 403 ページへ遷移
      await userStore.logout()
      return '/login'
    }
  }

  return true
})

export default router