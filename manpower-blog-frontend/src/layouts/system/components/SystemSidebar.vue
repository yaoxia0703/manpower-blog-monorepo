<template>
  <el-menu
    :default-active="route.path"
    class="menu"
    background-color="#001529"
    text-color="#fff"
    active-text-color="#409eff"
    router
  >
    <!-- 固定ダッシュボード -->
    <el-menu-item index="/system/dashboard">
      <el-icon>
        <component :is="'House'" />
      </el-icon>

      ダッシュボード
    </el-menu-item>

    <!-- 動的メニュー -->
    <template
      v-for="menu in menus"
      :key="menu.id"
    >
      <MenuItem :menu="menu" />
    </template>
  </el-menu>
</template>

<script setup lang="ts">
/**
 * システムサイドバーコンポーネント
 * 権限に応じた動的メニューを表示する
 */
defineOptions({
  name: 'SystemSidebar',
})

import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { usePermissionStore } from '@/stores/permissionStore'
import MenuItem from './MenuItem.vue'

const permissionStore = usePermissionStore()
const route = useRoute()

/**
 * ボタンタイプ(type = 2)を除外したメニュー一覧
 */
const menus = computed(() => {
  return permissionStore.menus.filter(
    menu => menu.type !== 2,
  )
})
</script>

<style scoped>
.menu {
  height: 100%;
  border-right: none;
}
</style>