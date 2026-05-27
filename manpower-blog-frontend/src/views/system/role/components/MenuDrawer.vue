<template>
    <el-drawer v-model="visible" :title="`「${roleName}」のメニュー設定`" size="480px" destroy-on-close>
        <div v-loading="loading" style="height: 100%">

            <!-- 検索 -->
            <el-input v-model="filterText" placeholder="メニュー名で検索" clearable style="margin-bottom: 12px" />

            <!-- メニューツリー -->
            <!-- check-strictly を外す → 親子連動が有効になる -->
            <el-tree ref="treeRef" :data="menuTree" :props="treeProps" show-checkbox node-key="id"
                :default-checked-keys="checkedIds" :filter-node-method="filterNode" :default-expand-all="true">
                <template #default="{ data }">
                    <span>{{ data.name }}</span>
                    <el-tag size="small" style="margin-left: 8px"
                        :type="data.type === MenuType.DIRECTORY ? 'primary' : 'success'">
                        {{ data.type === MenuType.DIRECTORY ? 'DIRECTORY' : 'MENU' }}
                    </el-tag>
                </template>
            </el-tree>
        </div>

        <template #footer>
            <el-button @click="handleCancel">キャンセル</el-button>
            <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
        </template>
    </el-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage, type ElTree } from 'element-plus'
import type { MenuTreeVO } from '@/types/system/menu/menuResponse'
import { MenuType } from '@/types/enums/menu'
import { getActiveMenuTreeApi } from '@/api/system/menu'
import { getRoleMenusApi, assignRoleMenusApi } from '@/api/system/role'

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
const menuTree = ref<MenuTreeVO[]>([])
const checkedIds = ref<number[]>([])
const treeRef = ref<InstanceType<typeof ElTree>>()

const treeProps = {
    label: 'name',
    children: 'children',
}

/**
 * ツリーフィルター
 */
function filterNode(value: string, data: MenuTreeVO) {
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
 * 有効メニューツリー + ロール既存メニュー（MENU typeのみ）を並列取得
 * checkedIds は MENU type のみ → el-tree が DIRECTORY を自動で半選にする
 */
async function fetchData() {
    loading.value = true
    try {
        const [treeRes, checkedRes] = await Promise.all([
            getActiveMenuTreeApi(),
            getRoleMenusApi(props.roleId!),
        ])
        menuTree.value = treeRes.data || []
        checkedIds.value = checkedRes.data || []
    } catch (error) {
        console.error('データ取得失敗:', error)
    } finally {
        loading.value = false
    }
}

/**
 * 保存
 * checkedKeys: 完全選択ノード（MENU type）
 * halfCheckedKeys: 半選択ノード（DIRECTORY type）
 * 両方を合算して送信する
 */
async function handleSubmit() {
    submitLoading.value = true
    try {
        const checkedKeys = treeRef.value?.getCheckedKeys(false) as number[]
        const halfCheckedKeys = treeRef.value?.getHalfCheckedKeys() as number[]
        const allKeys = [...checkedKeys, ...halfCheckedKeys]

        await assignRoleMenusApi(props.roleId!, allKeys)
        ElMessage.success('メニュー設定を保存しました')
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
    menuTree.value = []
    checkedIds.value = []
}
</script>