<template>
    <el-dialog v-model="visible" :title="isEdit ? '権限編集' : '権限新規追加'" width="700px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="180px" @keyup.enter.prevent="handleSubmit">

            <el-form-item label="所属メニュー" prop="menuId">
                <el-tree-select
                    v-model="form.menuId"
                    :data="menuTree"
                    :props="menuTreeProps"
                    node-key="id"
                    check-strictly
                    clearable
                    default-expand-all
                    placeholder="未選択の場合は共通権限"
                    style="width: 100%"
                />
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

            <el-form-item label="APIパス" prop="path">
                <el-input v-model="form.path" placeholder="例：/api/system/user" />
            </el-form-item>

            <el-form-item label="HTTPメソッド" prop="method">
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
import type { PermissionVO } from '@/types/system/permission/permissionResponse'
import { HttpMethod } from '@/types/enums/permission'
import type { PermissionCreateRequest, PermissionUpdateRequest } from '@/types/system/permission/permissionRequest'
import type { Status } from '@/types/enums/status'
import { createPermissionApi, updatePermissionApi } from '@/api/system/permission'
import { getActiveMenuTreeApi } from '@/api/system/menu'
import type { MenuTreeVO } from '@/types/system/menu/menuResponse'

/**
 * Props
 */
const props = defineProps<{
    modelValue: boolean
    data?: PermissionVO | null
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
const menuTree = ref<MenuTreeVO[]>([])
const menuTreeProps = { label: 'name', children: 'children' }

/**
 * フォームデータ
 */
const form = reactive({
    id: undefined as number | undefined,
    menuId: null as number | null,
    path: '',
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
    name: [{ required: true, message: '権限名を入力してください', trigger: 'blur' }],
    code: [{ required: true, message: '権限コードを入力してください', trigger: 'blur' }],
    path: [{ required: true, message: 'APIパスを入力してください', trigger: 'blur' }],
    method: [{ required: true, message: 'HTTPメソッドを選択してください', trigger: 'change' }],
}))

/**
 * v-model 監視
 */
watch(() => props.modelValue, (val) => {
    visible.value = val
    if (val) void init()
})

watch(visible, (val) => {
    emit('update:modelValue', val)
})

/**
 * 初期化
 */
async function init() {
    try {
        const response = await getActiveMenuTreeApi()
        menuTree.value = response.data || []
    } catch (error) {
        console.error('メニュー一覧の取得に失敗しました:', error)
        menuTree.value = []
    }

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
    form.menuId = null
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
                    menuId: form.menuId,
                    path: form.path,
                    method: form.method as HttpMethod,
                    name: form.name,
                    status: form.status as Status,
                    sort: form.sort,
                }
                await updatePermissionApi(form.id!, request)
            } else {
                const request: PermissionCreateRequest = {
                    menuId: form.menuId,
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
