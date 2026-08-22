<template>
    <el-dialog v-model="visible" :title="isEdit ? 'メニュー編集' : 'メニュー新規追加'" width="700px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="140px" @keyup.enter.prevent="handleSubmit">
            <el-form-item label="親メニュー" prop="parentId">
                <el-select
                    v-model="form.parentId"
                    placeholder="親メニューを選択"
                    :disabled="isEdit"
                    clearable
                    style="width: 100%"
                >
                    <el-option :key="0" label="なし" :value="0" />
                    <el-option v-for="item in props.menuOptions" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>

            <el-form-item label="メニュー名" prop="name">
                <el-input v-model="form.name" placeholder="メニュー名を入力" />
            </el-form-item>

            <el-form-item label="パス" prop="path">
                <el-input v-model="form.path" placeholder="/system/user" />
            </el-form-item>

            <el-form-item label="コンポーネント" prop="component">
                <el-input v-model="form.component" placeholder="system/user/index" />
            </el-form-item>

            <el-form-item label="メニュータイプ" prop="type">
                <el-select v-model="form.type" placeholder="メニュータイプを選択" style="width: 100%" :disabled="isEdit">
                    <el-option v-for="item in filteredTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>

            <el-form-item label="アイコン" prop="icon">
                <el-input v-model="form.icon" placeholder="アイコン名を入力" />
            </el-form-item>

            <el-form-item label="状態" prop="status">
                <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>

            <el-form-item label="表示順" prop="sort">
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
import type { MenuTreeVO, MenuOptionVo } from '@/types/system/menu/menuResponse'
import { MenuType } from '@/types/enums/menu'
import { createMenuApi, updateMenuApi } from '@/api/system/menu'
import type { Status } from '@/types/enums/status'
import type { MenuUpdateRequest, MenuCreateRequest } from '@/types/system/menu/menuRequest'

const props = defineProps<{
    modelValue: boolean
    data?: MenuTreeVO | null
    menuOptions: MenuOptionVo[]
}>()

const emit = defineEmits<{
    (e: 'update:modelValue', val: boolean): void
    (e: 'success'): void
}>()

const visible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
    id: undefined as number | undefined,
    parentId: undefined as number | undefined,
    type: undefined as MenuType | undefined,
    name: '',
    path: '',
    component: '',
    icon: '',
    status: 1 as Status,
    sort: 1,
})

const rules = computed<FormRules>(() => ({
    parentId: [{ required: true, message: '親メニューを選択してください', trigger: 'change' }],
    name: [{ required: true, message: 'メニュー名を入力してください', trigger: 'blur' }],
    type: isEdit.value ? [] : [{ required: true, message: 'メニュータイプを選択してください', trigger: 'change' }],
}))

const filteredTypeOptions = computed(() => {
    if (!form.parentId || form.parentId === 0) {
        return [
            { label: 'メニュー', value: MenuType.MENU },
            { label: 'ディレクトリ', value: MenuType.DIRECTORY },
        ]
    }
    return [{ label: 'メニュー', value: MenuType.MENU }]
})

watch(() => form.parentId, () => {
    if (isEdit.value) return
    form.type = undefined
})

watch(() => props.modelValue, (val) => {
    visible.value = val
    if (val) init()
})

watch(visible, (val) => {
    emit('update:modelValue', val)
})

function init() {
    if (props.data) {
        isEdit.value = true
        Object.assign(form, {
            ...props.data,
            path: props.data.path || '',
            component: props.data.component || '',
        })
    } else {
        isEdit.value = false
        resetForm()
    }
}

function resetForm() {
    form.id = undefined
    form.parentId = undefined
    form.type = undefined
    form.name = ''
    form.path = ''
    form.component = ''
    form.icon = ''
    form.status = 1
    form.sort = 1
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
                const updateData: MenuUpdateRequest = {
                    name: form.name,
                    path: form.path || null,
                    component: form.component || null,
                    sort: form.sort,
                    icon: form.icon,
                    status: form.status,
                }
                await updateMenuApi(form.id!, updateData)
                ElMessage.success('メニューを更新しました')
            } else {
                const createData: MenuCreateRequest = {
                    parentId: form.parentId!,
                    type: form.type!,
                    name: form.name,
                    path: form.path || null,
                    component: form.component || null,
                    sort: form.sort,
                    icon: form.icon,
                    status: form.status,
                }
                await createMenuApi(createData)
                ElMessage.success('メニューを作成しました')
            }
            emit('success')
            emit('update:modelValue', false)
        } catch (error) {
            console.error(error)
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
