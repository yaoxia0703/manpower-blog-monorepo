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
        meta: { title: 'ユーザー管理', permission: 'sys:user:list' },
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: 'ロール管理', permission: 'sys:role:list' },
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/system/permission/index.vue'),
        meta: { title: '権限管理', permission: 'sys:permission:list' },
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: 'メニュー管理', permission: 'sys:menu:list' },
      },

    ],
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/errors/ForbiddenView.vue'),
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

  // ページ権限は permission code で判定する。メニューは表示制御専用。
  const requiredPermission = to.meta.permission
  if (
    requiresAuth &&
    typeof requiredPermission === 'string' &&
    !permissionStore.hasPermission(requiredPermission)
  ) {
    return '/403'
  }

  return true
})

export default router
