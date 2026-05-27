<template>
    <el-dialog v-model="visible" :title="isEdit ? '権限編集' : '権限新規追加'" width="700px" destroy-on-close>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="180px" @keyup.enter.prevent="handleSubmit">


            <el-form-item label="親メニュー" prop="parentId">
                <el-select v-model="form.parentId" placeholder="親メニューを選択" :disabled="isEdit" clearable
                    style="width: 100%">
                    <el-option :key="0" label="無し" :value="0" />
                    <el-option v-for="item in props.menuOptions" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>

            <!-- メニュー名 -->
            <el-form-item label="メニュー名" prop="name">
                <el-input v-model="form.name" placeholder="メニュー名を入力" />
            </el-form-item>


            <!-- メニュータイプ -->
            <el-form-item label="メニュータイプ" prop="type">
                <el-select v-model="form.type" placeholder="メニュータイプを選択" style="width: 100%" :disabled="isEdit">
                    <el-option v-for="item in filteredTypeOptions" :key="item.value" :label="item.label"
                        :value="item.value" />
                </el-select>
            </el-form-item>

            <!-- メニューicon -->
            <el-form-item label="メニューアイコン" prop="icon">
                <el-input v-model="form.icon" placeholder="メニューアイコンを入力" />
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
import type { MenuTreeVO, MenuOptionVo } from '@/types/system/menu/menuResponse';
import { MenuType } from '@/types/enums/menu';
import { createMenuApi, updateMenuApi } from '@/api/system/menu';
import type { Status } from '@/types/enums/status';
import type { MenuUpdateRequest, MenuCreateRequest } from '@/types/system/menu/menuRequest';



/**
 * Props
 */
const props = defineProps<{
    modelValue: boolean
    data?: MenuTreeVO | null
    menuOptions: MenuOptionVo[]
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
    type: undefined as MenuType | undefined,
    name: '',
    icon: '',
    status: 1 as Status,
    sort: 1 as number,
})

/**
 * バリデーションルール
 */
const rules = computed<FormRules>(() => ({
    parentId: [{ required: true, message: '親メニューを選択してください', trigger: 'change' }],
    name: [{ required: true, message: 'メニュー名を入力してください', trigger: 'blur' }],
    type: isEdit.value ? [] : [{ required: true, message: 'メニュータイプを選択してください', trigger: 'change' }],
}))

/**
 * 親メニューに応じて選択できるメニュータイプを絞り込む
 * - parentId = 0 or 未選択 → MENU・DIRECTORY のみ
 * - parentId != 0          → MENUのみ
 */

const filteredTypeOptions = computed(() => {
    if (!form.parentId || form.parentId === 0) {
        return [{ label: 'MENU', value: MenuType.MENU }, { label: 'DIRECTORY', value: MenuType.DIRECTORY }]
    }
    return [
        { label: 'MENU', value: MenuType.MENU },
    ]
})

/**
 * 親メニュー変更時 → type / method / path をリセット
 */
watch(() => form.parentId, () => {
    if (isEdit.value) return // 編集モードでは親メニュー変更不可のため、変更された場合はリセットのみ行う
    form.type = undefined
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
    form.name = ''
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
                    sort: form.sort,
                    icon: form.icon,
                    status: form.status,  
                }
                await updateMenuApi(form.id!, updateData)
                ElMessage.success('メニューが更新されました')
            } else {
                const createData: MenuCreateRequest = {
                    parentId: form.parentId!,
                    type: form.type!,
                    name: form.name,
                    sort: form.sort,
                    icon: form.icon,
                    status: form.status,
                }
                await createMenuApi(createData)
                ElMessage.success('メニューが作成されました')
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