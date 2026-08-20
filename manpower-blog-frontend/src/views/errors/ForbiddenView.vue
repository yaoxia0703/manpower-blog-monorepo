<template>
  <main class="forbidden-page">
    <el-result
      icon="warning"
      title="403"
      sub-title="このページを表示する権限がありません"
    >
      <template #extra>
        <el-button type="primary" @click="goHome">ホームへ戻る</el-button>
      </template>
    </el-result>
  </main>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/system/dashboard')
}
</script>

<style scoped>
.forbidden-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: #f5f7fa;
}
</style>
