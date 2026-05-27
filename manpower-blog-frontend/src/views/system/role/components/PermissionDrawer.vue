<template>
    <el-drawer
        v-model="visible"
        :title="`「${roleName}」の権限設定`"
        size="480px"
        destroy-on-close
    >
        <!-- ローディング -->
        <div v-loading="loading" style="height: 100%">

            <!-- 検索 -->
            <el-input
                v-model="filterText"
                placeholder="権限名で検索"
                clearable
                style="margin-bottom: 12px"
            />

            <!-- 権限ツリー -->
            <el-tree
                ref="treeRef"
                :data="permissionTree"
                :props="treeProps"
                show-checkbox
                node-key="id"
                :default-checked-keys="checkedIds"
                :filter-node-method="filterNode"
                :default-expand-all="true"
            >
                <template #default="{ node, data }">
                    <span>{{ data.name }}</span>
                    <el-tag
                        size="small"
                        style="margin-left: 8px"
                        :type="data.type === PermissionType.MENU ? 'primary' : data.type === PermissionType.BUTTON ? 'success' : 'warning'"
                    >
                        {{ data.type === PermissionType.MENU ? 'MENU' : data.type === PermissionType.BUTTON ? 'BUTTON' : 'API' }}
                    </el-tag>
                </template>
            </el-tree>
        </div>

        <!-- フッター -->
        <template #footer>
            <el-button @click="handleCancel">キャンセル</el-button>
            <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
        </template>
    </el-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage, type ElTree } from 'element-plus'
import type { PermissionTreeVO } from '@/types/system/permission/permissionResponse'
import { PermissionType } from '@/types/enums/permission'
import { getPermissionTreeApi } from '@/api/system/permission'
import { getRolePermissionsApi, assignRolePermissionsApi } from '@/api/system/role'

/**
 * Props
 */
const props = defineProps<{
    modelValue: boolean
    roleId: number | null
    roleName: string
}>()

/**
 * Emits
 */
const emit = defineEmits<{
    (e: 'update:modelValue', val: boolean): void
    (e: 'success'): void
}>()

const visible = ref(false)
const loading = ref(false)
const submitLoading = ref(false)
const filterText = ref('')
const permissionTree = ref<PermissionTreeVO[]>([])
const checkedIds = ref<number[]>([])
const treeRef = ref<InstanceType<typeof ElTree>>()

const treeProps = {
    label: 'name',
    children: 'children',
}

/**
 * ツリーフィルター
 */
function filterNode(value: string, data: PermissionTreeVO) {
    if (!value) return true
    return data.name.toLowerCase().includes(value.toLowerCase())
}

watch(filterText, (val) => {
    treeRef.value?.filter(val)
})

/**
 * v-model 監視 → 開いた時にデータ取得
 */
watch(() => props.modelValue, async (val) => {
    visible.value = val
    if (val && props.roleId != null) {
        await fetchData()
    }
})

watch(visible, (val) => {
    emit('update:modelValue', val)
    if (!val) resetState()
})

/**
 * 権限ツリー + ロール既存権限を並列取得
 */
async function fetchData() {
    loading.value = true
    try {
        const [treeRes, checkedRes] = await Promise.all([
            getPermissionTreeApi(),
            getRolePermissionsApi(props.roleId!),
        ])
        permissionTree.value = treeRes.data || []
        checkedIds.value = checkedRes.data || []
    } catch (error) {
        console.error('データ取得失敗:', error)
    } finally {
        loading.value = false
    }
}

/**
 * 保存
 */
async function handleSubmit() {
    submitLoading.value = true
    try {
        const checked = treeRef.value?.getCheckedKeys(false) as number[]
        await assignRolePermissionsApi(props.roleId!, checked)
        ElMessage.success('権限設定を保存しました')
        emit('success')
        handleCancel()
    } catch (error) {
        console.error('保存失敗:', error)
    } finally {
        submitLoading.value = false
    }
}

function handleCancel() {
    emit('update:modelValue', false)
}

function resetState() {
    filterText.value = ''
    permissionTree.value = []
    checkedIds.value = []
}
</script>