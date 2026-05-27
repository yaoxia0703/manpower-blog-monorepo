import { usePermissionStore } from '@/stores/permissionStore'

/**
 * 権限チェックComposable
 * el-table-columnなどのコンポーネントレベルの権限制御に使用
 *
 * 使用例：
 * const { hasPermission, hasAnyPermission, hasAllPermissions } = usePermission()
 *
 * v-if="hasPermission('sys:user:create')"
 * v-if="hasAnyPermission(['sys:user:create', 'sys:user:update'])"
 * v-if="hasAllPermissions(['sys:user:create', 'sys:user:update'])"
 */
export function usePermission() {
  const permissionStore = usePermissionStore()

  /**
   * 単一権限チェック
   */
  const hasPermission = (permission: string): boolean => {
    return permissionStore.hasPermission(permission)
  }

  /**
   * 複数権限チェック（OR判定）
   * いずれかの権限を持っていればtrue
   */
  const hasAnyPermission = (permissions: string[]): boolean => {
    return permissions.some(permission =>
      permissionStore.hasPermission(permission)
    )
  }

  /**
   * 複数権限チェック（AND判定）
   * すべての権限を持っていればtrue
   */
  const hasAllPermissions = (permissions: string[]): boolean => {
    return permissions.every(permission =>
      permissionStore.hasPermission(permission)
    )
  }

  return {
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
  }
}