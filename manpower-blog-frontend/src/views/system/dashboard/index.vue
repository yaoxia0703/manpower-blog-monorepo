<template>
  <div class="dashboard-container">
    <h1>Dashboard</h1>

    <p>これはホームページです</p>

    <button @click="goLogin">
      ログアウト
    </button>
  </div>
</template>

<script setup lang="ts">
/**
 * ダッシュボード画面
 */
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

/**
 * ログアウト処理
 * ユーザー情報および認証情報を削除する
 */
const goLogin = async () => {
  await userStore.logout()
}
</script>

<style scoped>
.dashboard-container {
  padding: 40px;
}
</style>