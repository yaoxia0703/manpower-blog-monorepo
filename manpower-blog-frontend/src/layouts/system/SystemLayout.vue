<template>
  <el-container class="layout">
    <!-- ヘッダー -->
    <el-header class="layout-header">
      <SystemHeader />
    </el-header>

    <el-container>
      <!-- サイドバー -->
      <el-aside width="200px" class="layout-sidebar">
        <SystemSidebar />
      </el-aside>

      <!-- メインコンテンツ -->
      <el-main class="layout-main">
        <SystemMain />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
/**
 * システム共通レイアウト
 * Header・Sidebar・Main コンテンツで構成される
 */
import SystemHeader from './components/SystemHeader.vue'
import SystemSidebar from './components/SystemSidebar.vue'
import SystemMain from './components/SystemMain.vue'
</script>

<style scoped>
.layout {
  height: 100vh;
}

.layout-header {
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.layout-sidebar {
  background: #001529;
  color: #fff;
}

.layout-main {
  background: #f5f7fa;
  padding: 20px;
}
</style>