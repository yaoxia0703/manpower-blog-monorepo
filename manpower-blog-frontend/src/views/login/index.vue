<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">Manpower Blog</h2>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form" @submit.prevent="handleLogin">
        <el-form-item prop="accountValue">
          <el-input v-model="form.accountValue" placeholder="メールアドレス" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="パスワード" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="login-btn" native-type="submit">
            ログイン
          </el-button>

          <div v-if="loginError" class="error-message">
            {{ loginError }}
          </div>
        </el-form-item>
      </el-form>

      <div class="footer">
        <a href="#">
          パスワードをお忘れですか？
        </a>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
/**
 * ログイン画面
 * ユーザー認証およびログイン処理を行う
 */
defineOptions({
  name: 'SystemLogin',
})

import { reactive, ref } from 'vue'
import {
  ElMessage,
  type FormInstance,
  type FormRules,
} from 'element-plus'
import { useRouter } from 'vue-router'
import { loginApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { ACCOUNT_TYPE } from '@/types/enums/account'
import type { LoginRequest } from '@/types/auth/loginRequest'
import { resolveErrorMessage } from '@/utils/errorHandler'

/**
 * ログインエラーメッセージ
 */
const loginError = ref<string>('')

/**
 * Router
 */
const router = useRouter()

/**
 * ユーザー状態管理
 */
const userStore = useUserStore()

/**
 * ログインフォーム
 */
const form = reactive<LoginRequest>({
  accountType: ACCOUNT_TYPE.EMAIL,
  accountValue: '',
  password: '',
})

/**
 * Form参照
 */
const formRef = ref<FormInstance>()

/**
 * バリデーションルール
 */
const rules: FormRules = {
  accountValue: [
    {
      required: true,
      message: 'IDを入力してください',
      trigger: 'blur',
    },
    {
      min: 8,
      max: 100,
      message: 'IDは8文字以上100文字以内である必要があります',
      trigger: 'blur',
    },
    {
      type: 'email',
      message: '有効なメールアドレスを入力してください',
      trigger: 'blur',
    }
  ],
  password: [
    {
      required: true,
      message: 'パスワードを入力してください',
      trigger: 'blur',
    },
    {
      min: 8,
      message: 'パスワードは8文字以上である必要があります',
      trigger: 'blur',
    },
    {
      max: 16,
      message: 'パスワードは16文字以内である必要があります',
      trigger: 'blur',
    },
  ],
}

/**
 * ログイン処理
 */
const handleLogin = async () => {
  const valid = await formRef.value
    ?.validate()
    .catch(() => false)

  if (!valid) return

  try {
    const result = await loginApi(form)

    // result は Result<LoginResponse>、result.data は LoginResponse
    const data = result.data

    userStore.setToken(data.accessToken)
    await userStore.fetchUser()
    await router.push('/system/dashboard')
    ElMessage.success('ログイン成功')

  } catch (error: unknown) {
    console.error('ログイン失敗:', error)
    loginError.value = resolveErrorMessage(error)
  }
}
</script>

<style scoped>
/* 背景 */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  background-image:
    linear-gradient(rgba(255, 255, 255, 0.45),
      rgba(255, 255, 255, 0.45)),
    url('https://images.unsplash.com/photo-1507525428034-b723cf961d3e');

  background-size: cover;
  background-position: center;
}

/* ログインカード */
.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 16px;

  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(6px);

  border: 1px solid rgba(0, 0, 0, 0.05);

  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.08);

  transition:
    transform 0.2s ease;
}

/* Hover */
.login-card:hover {
  transform: translateY(-2px);
}

/* タイトル */
.title {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

/* ログインボタン */
.login-btn {
  width: 100%;
}

/* Footer */
.footer {
  text-align: right;
  margin-top: 10px;
}

.footer a {
  color: #666;
  text-decoration: none;
}

/* Input */
:deep(.el-input__wrapper) {
  background: #fff;
  border: 1px solid #ddd;
  box-shadow: none;
}

:deep(.el-input__inner) {
  color: #333;
}

:deep(.el-input__inner::placeholder) {
  color: #999;
}

/* Focus */
:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
}

/* Button */
:deep(.el-button--primary) {
  background: #409eff;
  border: none;
}

/* Form */
:deep(.el-form-item) {
  margin-bottom: 28px;
}

:deep(.el-form-item__error) {
  min-height: 18px;
  line-height: 18px;
}

/* エラーメッセージ */
.error-message {
  margin-top: 12px;
  color: #f56c6c;
  font-size: 14px;
  text-align: left;
  line-height: 1.4;
}
</style>