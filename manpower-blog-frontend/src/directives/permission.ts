import type { Directive, DirectiveBinding } from 'vue'
import { usePermissionStore } from '@/stores/permissionStore'

/**
 * 権限チェック処理
 *
 * 対応形式：
 * 1. string                 : 単一権限
 * 2. string[]               : 複数権限（OR判定）
 * 3. { value, mode }        : AND / OR 判定
 */
function checkPermission(
  el: HTMLElement,
  binding: DirectiveBinding,
) {
  const permissionStore = usePermissionStore()
  const value = binding.value

  if (!value) return

  let hasPermission = false

  // 単一権限
  if (typeof value === 'string') {
    hasPermission = permissionStore.hasPermission(value)
  }

  // 複数権限（OR判定）
  else if (Array.isArray(value)) {
    hasPermission = value.some(permission =>
      permissionStore.hasPermission(permission),
    )
  }

  // AND / OR 判定
  else if (typeof value === 'object') {
    const { value: perms, mode = 'or' } = value

    if (Array.isArray(perms)) {
      hasPermission =
        mode === 'and'
          ? perms.every(permission =>
              permissionStore.hasPermission(permission),
            )
          : perms.some(permission =>
              permissionStore.hasPermission(permission),
            )
    }
  }

  // 権限に応じて表示・非表示を切り替える
  el.style.display = hasPermission ? '' : 'none'
}

/**
 * 権限ディレクティブ
 * 要素表示時および更新時に権限チェックを行う
 */
export const permissionDirective: Directive = {
  mounted(el, binding) {
    checkPermission(el, binding)
  },

  updated(el, binding) {
    // 同一値の場合は再計算しない
    if (binding.value === binding.oldValue) {
      return
    }

    checkPermission(el, binding)
  },
}