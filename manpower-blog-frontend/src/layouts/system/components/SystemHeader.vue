<template>
  <div class="header">
    <div class="left">
      <h3>Admin System</h3>
    </div>

    <div class="right">
      <span class="user">Admin</span>

      <el-button
        type="danger"
        @click="goLogin"
      >
        ログアウト
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * システムヘッダーコンポーネント
 * ユーザー情報表示およびログアウト機能を提供する
 */
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

/**
 * ログアウト処理
 * ユーザー情報および認証トークンを削除する
 */
const goLogin = async () => {
  await userStore.logout()
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.right {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>