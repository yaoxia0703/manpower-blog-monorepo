<template>
  <main class="error-page">
    <el-result
      icon="error"
      :title="code"
      :sub-title="message"
    >
      <template #extra>
        <el-button @click="goBack">前のページへ戻る</el-button>
        <el-button type="primary" @click="goHome">ホームへ戻る</el-button>
      </template>
    </el-result>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const DEFAULT_MESSAGES: Readonly<Record<string, string>> = {
  '404': 'ページまたはリソースが見つかりません',
  '500': 'サーバー内部でエラーが発生しました',
  '502': 'サーバーへ接続できません',
  '503': 'サービスを一時的に利用できません',
  '504': 'サーバーからの応答がタイムアウトしました',
}

const code = computed(() => String(route.params.code || '500'))

const message = computed(() => {
  const stateMessage = window.history.state?.message
  if (typeof stateMessage === 'string' && stateMessage.trim()) {
    return stateMessage
  }

  return DEFAULT_MESSAGES[code.value] || '予期しないエラーが発生しました'
})

function goBack(): void {
  if (window.history.length > 1) {
    router.back()
    return
  }

  void router.replace('/system/dashboard')
}

function goHome(): void {
  void router.replace('/system/dashboard')
}
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: #f5f7fa;
}
</style>
