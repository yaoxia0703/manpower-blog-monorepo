<template>
  <!-- 子メニューなし -->
  <el-menu-item
    v-if="!hasChildren"
    :index="menu.path || String(menu.id)"
  >
    <el-icon v-if="menu.icon">
      <component :is="menu.icon" />
    </el-icon>
    <span>{{ menu.name }}</span>
  </el-menu-item>

  <!-- 子メニューあり -->
  <el-sub-menu
    v-else
    :index="String(menu.id)"
  >
    <template #title>
      <el-icon v-if="menu.icon">
        <component :is="menu.icon" />
      </el-icon>
      <span>{{ menu.name }}</span>
    </template>

    <template
      v-for="child in props.menu.children || []"
      :key="child.id"
    >
      <MenuItem :menu="child" />
    </template>
  </el-sub-menu>
</template>

<script setup lang="ts">
/**
 * サイドバーメニュー項目コンポーネント
 * 再帰構造でメニュー階層を表示する
 */
import { computed } from 'vue'
import type { MenuTreeVO } from '@/types/system/menu/menuResponse'
import { ElIcon } from 'element-plus'

defineOptions({
  name: 'MenuItem',
})

const props = defineProps<{
  menu: MenuTreeVO
}>()

/**
 * 子メニューを持っているかどうか
 */
const hasChildren = computed(() => {
  return (props.menu.children || []).length > 0
})
</script>
