<template>
    <el-dialog v-model="visible" :title="isEdit ? '権限編集' : '権限新規追加'" width="700px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="180px" @keyup.enter.prevent="handleSubmit">

            <!-- 親権限 -->
            <el-form-item label="親権限" prop="parentId">
                <el-select v-model="form.parentId" placeholder="親権限を選択" :disabled="isEdit" clearable
                    style="width: 100%">
                    <el-option :key="0" label="無し" :value="0" />
                    <el-option v-for="item in props.permissionOptions" :key="item.id" :label="item.name"
                        :value="item.id" />
                </el-select>
            </el-form-item>

            <!-- 権限名 -->
            <el-form-item label="権限名" prop="name">
                <el-input v-model="form.name" placeholder="権限名を入力" />
            </el-form-item>

            <!-- 権限コード -->
            <el-form-item label="権限コード" prop="code">
                <el-input v-model="form.code" placeholder="例：sys:user:view / sys:permission:create"
                    :disabled="isEdit" />
            </el-form-item>

            <!-- 権限タイプ -->
            <el-form-item label="権限タイプ" prop="type">
                <el-select v-model="form.type" placeholder="権限タイプを選択" style="width: 100%" :disabled="isEdit">
                    <el-option v-for="item in filteredTypeOptions" :key="item.value" :label="item.label"
                        :value="item.value" />
                </el-select>
            </el-form-item>

            <!-- パス（MENU / BUTTON / API の時に表示） -->
            <el-form-item v-if="form.type != null" label="パス" prop="path">
                <el-input v-model="form.path" placeholder="例：/api/system/user" />
            </el-form-item>

            <!-- HTTPメソッド（BUTTON / API の時だけ表示） -->
            <el-form-item
                v-if="form.type === PermissionType.BUTTON || form.type === PermissionType.API"
                label="HTTPメソッド"
                prop="method"
            >
                <el-select v-model="form.method" placeholder="HTTPメソッドを選択" style="width: 100%">
                    <el-option label="GET" :value="HttpMethod.GET" />
                    <el-option label="POST" :value="HttpMethod.POST" />
                    <el-option label="PUT" :value="HttpMethod.PUT" />
                    <el-option label="DELETE" :value="HttpMethod.DELETE" />
                    <el-option label="PATCH" :value="HttpMethod.PATCH" />
                </el-select>
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

        <template #footer>
            <el-button @click="handleCancel">キャンセル</el-button>
            <el-button type="primary" :loading="submitLoading" @click="handleSubmit">確認</el-button>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { PermissionOptionVo, PermissionTreeVO } from '@/types/system/permission/permissionResponse'
import { HttpMethod, PermissionType } from '@/types/enums/permission'
import type { PermissionCreateRequest, PermissionUpdateRequest } from '@/types/system/permission/permissionRequest'
import type { Status } from '@/types/enums/status'
import { createPermissionApi, updatePermissionApi } from '@/api/system/permission'

/**
 * Props
 */
const props = defineProps<{
    modelValue: boolean
    data?: PermissionTreeVO | null
    permissionOptions: PermissionOptionVo[]
}>()

/**
 * Emits
 */
const emit = defineEmits<{
    (e: 'update:modelValue', val: boolean): void
    (e: 'success'): void
}>()

const visible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

/**
 * フォームデータ
 */
const form = reactive({
    id: undefined as number | undefined,
    parentId: undefined as number | undefined,
    type: undefined as PermissionType | undefined,
    path: '' as string | undefined,
    method: undefined as HttpMethod | undefined,
    code: '',
    name: '',
    status: 1,
    sort: 0,
})

/**
 * バリデーションルール
 */
const rules = computed<FormRules>(() => ({
    parentId: [{ required: true, message: '親権限を選択してください', trigger: 'change' }],
    name: [{ required: true, message: '権限名を入力してください', trigger: 'blur' }],
    code: [{ required: true, message: '権限コードを入力してください', trigger: 'blur' }],
    type: isEdit.value ? [] : [{ required: true, message: '権限タイプを選択してください', trigger: 'change' }],
    // MENU / BUTTON / API すべて path 必須
    path: form.type != null
        ? [{ required: true, message: 'パスを入力してください', trigger: 'blur' }]
        : [],
    // BUTTON / API の時だけ method 必須
    method: (form.type === PermissionType.BUTTON || form.type === PermissionType.API)
        ? [{ required: true, message: 'HTTPメソッドを選択してください', trigger: 'change' }]
        : [],
}))

/**
 * 親権限に応じて選択できる権限タイプを絞り込む
 * - parentId = 0 or 未選択 → MENU のみ
 * - parentId != 0          → BUTTON / API のみ
 */
const filteredTypeOptions = computed(() => {
    if (!form.parentId || form.parentId === 0) {
        return [{ label: 'MENU', value: PermissionType.MENU }]
    }
    return [
        { label: 'BUTTON', value: PermissionType.BUTTON },
        { label: 'API', value: PermissionType.API },
    ]
})

/**
 * 親権限変更時 → type / path / method をリセット
 */
watch(() => form.parentId, () => {
    if (isEdit.value) return
    form.type = undefined
    form.path = ''
    form.method = undefined
})

/**
 * type 変更時 → path / method をリセット
 */
watch(() => form.type, () => {
    if (isEdit.value) return
    form.path = ''
    form.method = undefined
})

/**
 * v-model 監視
 */
watch(() => props.modelValue, (val) => {
    visible.value = val
    if (val) init()
})

watch(visible, (val) => {
    emit('update:modelValue', val)
})

/**
 * 初期化
 */
function init() {
    if (props.data) {
        isEdit.value = true
        Object.assign(form, props.data)
    } else {
        isEdit.value = false
        resetForm()
    }
}

function resetForm() {
    form.id = undefined
    form.parentId = undefined
    form.type = undefined
    form.path = ''
    form.method = undefined
    form.code = ''
    form.name = ''
    form.status = 1
    form.sort = 0
}

function handleCancel() {
    emit('update:modelValue', false)
}

function handleSubmit() {
    formRef.value?.validate(async (valid) => {
        if (!valid) return
        submitLoading.value = true
        try {
            if (isEdit.value) {
                const request: PermissionUpdateRequest = {
                    parentId: form.parentId!,
                    type: form.type as PermissionType,
                    path: form.path,
                    method: form.method as HttpMethod,
                    name: form.name,
                    status: form.status as Status,
                    sort: form.sort,
                }
                await updatePermissionApi(form.id!, request)
            } else {
                const request: PermissionCreateRequest = {
                    parentId: form.parentId!,
                    type: form.type as PermissionType,
                    path: form.path,
                    method: form.method as HttpMethod,
                    code: form.code,
                    name: form.name,
                    status: form.status as Status,
                    sort: form.sort,
                }
                await createPermissionApi(request)
            }

            ElMessage.success('保存に成功しました')
            emit('success')
            handleCancel()
        } catch (error: any) {
            console.error('リクエスト失敗:', error)
        } finally {
            submitLoading.value = false
        }
    })
}
</script>

<style scoped>
.input-number {
    width: 120px;
}
</style>