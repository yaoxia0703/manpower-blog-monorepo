<template>
    <el-dialog v-model="visible" :title="isEdit ? 'ユーザー編集' : 'ユーザー新規追加'" width="700px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="180px" @keyup.enter.prevent="handleSubmit">
            <!-- ニックネーム -->
            <el-form-item label="ニックネーム" prop="nickName" class="mb-4">
                <el-input v-model="form.nickName" placeholder="ニックネームを入力" />
            </el-form-item>

            <!-- ロール -->
            <el-form-item label="ロール" prop="roleId" class="mb-4">
                <el-select v-model="form.roleId" placeholder="ロールを選択" style="width: 100%">
                    <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id" />
                </el-select>
            </el-form-item>

            <!-- アカウントタイプ -->
            <el-form-item label="アカウントタイプ" prop="accountType" class="mb-4">
                <el-switch v-model="form.accountType" :active-value="'EMAIL'" :inactive-value="'PHONE'"
                    active-text="メール" inactive-text="電話" disabled />
            </el-form-item>

            <!-- アカウント値 -->
            <el-form-item label="アカウント値" prop="accountValue" class="mb-4">
                <el-input v-model="form.accountValue" placeholder="アカウント値を入力" :disabled="isEdit" />
            </el-form-item>
            <!-- パスワード -->
            <el-form-item label="パスワード" prop="password" class="mb-4">
                <el-input v-model="form.password" type="password" placeholder="パスワードを入力" show-password
                    :disabled="isEdit" />
            </el-form-item>

            <!-- パスワード確認 -->
            <el-form-item label="パスワード確認" prop="confirmPassword" class="mb-4">
                <el-input v-model="form.confirmPassword" type="password" placeholder="パスワードを再入力" show-password
                    :disabled="isEdit" />
            </el-form-item>

            <!-- 状態 -->
            <el-form-item label="状態" prop="status" class="mb-4">
                <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>
        </el-form>

        <!-- フッター -->
        <template #footer>
            <el-button @click="handleCancel">
                キャンセル
            </el-button>

            <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
                確認
            </el-button>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import {
    computed,
    onMounted,
    reactive,
    ref,
    watch,
} from 'vue'
import {
    ElMessage,
    type FormInstance,
    type FormRules,
} from 'element-plus'

import type { UserVO } from '@/types/system/user/userResponse'
import {
    createUserApi,
    updateUserApi,
} from '@/api/system/user'
import type { UserCreateRequest, UserUpdateRequest } from '@/types/system/user/userRequest';
import type { AccountType } from '@/types/enums/account';
import { listRoleApi } from '@/api/system/role'
import type { RoleVO } from '@/types/system/role/roleResponse'
import type { Status } from '@/types/enums/status';

const props = defineProps<{
    modelValue: boolean
    data?: UserVO | null
}>()

const emit = defineEmits<{
    (
        e: 'update:modelValue',
        val: boolean,
    ): void

    (e: 'success'): void
}>()


/**
 * 状態管理
 */
const visible = ref(false)

const submitLoading = ref(false)

const isEdit = ref(false)

/**
 * フォーム参照
 */
const formRef = ref<FormInstance>()

const form = reactive({
    userId: undefined as
        | number
        | undefined,
    accountId: undefined as
        | number
        | undefined,

    nickName: '',
    roleId: undefined as
        | number
        | undefined,
    accountType: 'EMAIL',
    accountValue: '',
    password: '',
    confirmPassword: '',
    status: 1,
})

/**
 * バリデーションルール
 */

// 編集時はパスワード関連の入力チェックを行わない
const rules = computed<FormRules>(() => ({
    password: isEdit.value ? [] : [
        { required: true, message: 'パスワードを入力してください', trigger: 'blur' },
        { min: 8, max: 255, message: 'パスワードは8文字以上255文字以内で入力してください', trigger: 'blur' }
    ],
    confirmPassword: isEdit.value ? [] : [
        { required: true, message: 'パスワード確認を入力してください', trigger: 'blur' },
        {
            validator: (_rule: any, value: string, callback: any) => {
                if (value !== form.password) {
                    callback(new Error('パスワードと確認が一致しません'))
                } else {
                    callback()
                }
            },
            trigger: 'blur'
        }
    ],
    nickName: [
        { required: true, message: 'ニックネームを入力してください', trigger: 'blur' },
        { min: 2, max: 50, message: 'ニックネームは50文字以内で入力してください', trigger: 'blur' }
    ],
    roleId: [
        { required: true, message: 'ロールを選択してください', trigger: 'change' }
    ],
    accountValue: isEdit.value ? [] : [
        { required: true, message: 'アカウント値を入力してください', trigger: 'blur' },
        { type: 'email', message: '有効なメールアドレスを入力してください', trigger: 'blur' }
    ]
}))

/**
 * v-model 監視
 */
watch(
    () => props.modelValue,
    (val) => {
        visible.value = val

        if (val) {
            init()
        }
    },
)
watch(visible, (val) => {
    emit('update:modelValue', val)
})

/**
 * 初期化処理
 */
function init() {
    // 編集モード
    if (props.data) {
        isEdit.value = true

        Object.assign(
            form,
            props.data,
        )
    } else {
        // 新規モード
        isEdit.value = false

        resetForm()
    }
}

/**
 * フォーム初期化
 */
function resetForm() {
    form.userId = undefined
    form.accountId = undefined
    form.nickName = ''
    form.accountType = 'EMAIL'
    form.accountValue = ''
    form.password = ''
    form.confirmPassword = ''
    form.status = 1
}

/**
 * キャンセル処理
 */
function handleCancel() {
    emit('update:modelValue', false)
}

function handleSubmit() {
    formRef.value?.validate(async (valid) => {
        if (!valid) return

        submitLoading.value = true

        try {
            if (isEdit.value) {
                const request: UserUpdateRequest = {
                    accountId: form.accountId!,
                    nickName: form.nickName,
                    roleId: form.roleId!,
                    status: form.status as Status
                }
                await updateUserApi(form.userId!, request)
                ElMessage.success('ユーザーが更新されました')
            } else {
                const request: UserCreateRequest = {
                    nickName: form.nickName,
                    roleId: form.roleId!,
                    accountType: form.accountType as AccountType,
                    accountValue: form.accountValue,
                    password: form.password,
                    status: form.status as Status
                }
                await createUserApi(request)
                ElMessage.success('ユーザーが作成されました')
            }

            emit('success')
            handleCancel()
        } catch (error) {
            console.error(error)
        } finally {
            submitLoading.value = false
        }
    })
}

/****************** ロールリスト ******************/
const roleList = ref<RoleVO[]>([])

const fetchRoleList = async () => {
    try {
        const res = await listRoleApi()
        roleList.value = res.data
    } catch (error) {
        console.error('ロール一覧の取得に失敗しました', error)
    }
}

onMounted(() => {
    fetchRoleList()
})
</script>
