<template>
    <el-dialog v-model="visible" :title="isEdit ? '役割編集' : '役割新規追加'" width="500px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @keyup.enter.prevent="handleSubmit">
            <!-- コード -->
            <el-form-item label="コード" prop="code">
                <el-input v-model="form.code" placeholder="コードを入力" :disabled="isEdit" />
            </el-form-item>

            <!-- 名前 -->
            <el-form-item label="名前" prop="name">
                <el-input v-model="form.name" placeholder="名前を入力" />
            </el-form-item>

            <!-- 状態 -->
            <el-form-item label="状態" prop="status">
                <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>

            <!-- 順序 -->
            <el-form-item label="順序" prop="sort">
                <el-input-number v-model="form.sort" :min="1" class="input-number" />
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
/**
 * ロール編集ダイアログ
 * ロール新規作成・更新処理を行う
 */
import {
    reactive,
    ref,
    watch,
} from 'vue'

import {
    ElMessage,
    type FormInstance,
    type FormRules,
} from 'element-plus'

import type { RoleVO } from '@/types/system/role/roleResponse'

import {
    createRoleApi,
    updateRoleApi,
} from '@/api/system/role'

/**
 * 受け取り値
 */
const props = defineProps<{
    modelValue: boolean
    data?: RoleVO | null
}>()

/**
 * 通知イベント
 */
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

/**
 * フォームデータ
 */
const form = reactive({
    id: undefined as
        | number
        | undefined,

    code: '',
    name: '',
    status: 1,
    sort: 0,
})

/**
 * バリデーションルール
 */
const rules: FormRules = {
    code: [
        {
            required: true,
            message:
                'コードを入力してください',
            trigger: 'blur',
        },
    ],

    name: [
        {
            required: true,
            message:
                '名前を入力してください',
            trigger: 'blur',
        },
    ],
}

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
    form.id = undefined
    form.code = ''
    form.name = ''
    form.status = 1
    form.sort = 0
}

/**
 * キャンセル処理
 */
function handleCancel() {
    emit('update:modelValue', false)
}

/****************** 役割一覧操作 ******************/
/**
 * 保存処理
 */
function handleSubmit() {
    formRef.value?.validate(
        async (valid) => {
            if (!valid) return

            submitLoading.value = true

            try {
                if (isEdit.value) {
                    await updateRoleApi(form.id!, form)
                } else {
                    await createRoleApi(form)
                }

                ElMessage.success('保存成功しました')
                emit('success')
                handleCancel()
            } catch (error: any) {
                console.error('リクエスト失敗:', error)
                // エラーメッセージはインターセプターで処理済み
            } finally {
                submitLoading.value = false
            }
        },
    )
}
</script>

<style scoped>
.input-number {
    width: 120px;
}
</style>
