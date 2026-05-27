import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { usePermissionStore } from '@/stores/permissionStore'
import { MenuType } from '@/types/enums/menu'

/**
 * パンくずリスト管理Composable
 * 現在のルート情報からパンくずリストを生成する
 */
export function useBreadcrumb() {
  const route = useRoute()
  const permissionStore = usePermissionStore()

  /**
   * 現在ルートに対応するパンくずリスト
   */
  const breadcrumbList = computed(() => {
    return permissionStore.findMenuPath(route.path)
  })

  /**
   * パンくずリンク先取得
   * 最終階層およびディレクトリの場合はリンクを無効化する
   */
  function getBreadcrumbTo(item: any, index: number) {
    const isLast = index === breadcrumbList.value.length - 1

    if (isLast) return undefined

    if (item.type === MenuType.DIRECTORY) {
      return undefined
    }

    return item.path
  }

  return {
    breadcrumbList,
    getBreadcrumbTo,
  }
}