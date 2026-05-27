<template>
    <el-dialog v-model="visible" :title="isEdit ? 'Edit Menu' : 'Create Menu'" width="700px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="140px" @keyup.enter.prevent="handleSubmit">
            <el-form-item label="Parent" prop="parentId">
                <el-select
                    v-model="form.parentId"
                    placeholder="Select parent"
                    :disabled="isEdit"
                    clearable
                    style="width: 100%"
                >
                    <el-option :key="0" label="None" :value="0" />
                    <el-option v-for="item in props.menuOptions" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>

            <el-form-item label="Name" prop="name">
                <el-input v-model="form.name" placeholder="Menu name" />
            </el-form-item>

            <el-form-item label="Path" prop="path">
                <el-input v-model="form.path" placeholder="/system/user" />
            </el-form-item>

            <el-form-item label="Component" prop="component">
                <el-input v-model="form.component" placeholder="system/user/index" />
            </el-form-item>

            <el-form-item label="Type" prop="type">
                <el-select v-model="form.type" placeholder="Select type" style="width: 100%" :disabled="isEdit">
                    <el-option v-for="item in filteredTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>

            <el-form-item label="Icon" prop="icon">
                <el-input v-model="form.icon" placeholder="Element Plus icon name" />
            </el-form-item>

            <el-form-item label="Status" prop="status">
                <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>

            <el-form-item label="Sort" prop="sort">
                <el-input-number v-model="form.sort" :min="1" class="input-number" />
            </el-form-item>
        </el-form>

        <template #footer>
            <el-button @click="handleCancel">Cancel</el-button>
            <el-button type="primary" :loading="submitLoading" @click="handleSubmit">Confirm</el-button>
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
    parentId: [{ required: true, message: 'Select parent', trigger: 'change' }],
    name: [{ required: true, message: 'Enter menu name', trigger: 'blur' }],
    type: isEdit.value ? [] : [{ required: true, message: 'Select menu type', trigger: 'change' }],
}))

const filteredTypeOptions = computed(() => {
    if (!form.parentId || form.parentId === 0) {
        return [
            { label: 'MENU', value: MenuType.MENU },
            { label: 'DIRECTORY', value: MenuType.DIRECTORY },
        ]
    }
    return [{ label: 'MENU', value: MenuType.MENU }]
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
                ElMessage.success('Menu updated')
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
                ElMessage.success('Menu created')
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
