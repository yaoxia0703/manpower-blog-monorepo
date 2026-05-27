<template>
    <el-card>
        <!-- ヘッダー -->
        <template #header>
            <div class="card-header">
                <div>
                    <!-- パンくずリスト -->
                    <el-breadcrumb separator="/" class="mb-8">
                        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.permissionPath"
                            :to="getBreadcrumbTo(item, index)">
                            {{ item.name }}
                        </el-breadcrumb-item>
                    </el-breadcrumb>

                    <!-- タイトル -->
                    <h2 class="page-title">
                        メニュー一覧
                    </h2>
                </div>

                <!-- 操作ボタン -->
                <el-button type="primary" v-permission="'sys:menu:create'" @click="handleAdd">
                    新規追加
                </el-button>
            </div>
        </template>

        <!-- 検索エリア -->
        <div class="search-bar" style="display: flex; gap: 12px; margin-bottom: 16px;">
            <el-input v-model="search" placeholder="メニュー名 / コードで検索" clearable style="width: 260px">
                <template #prefix>
                    <el-icon>
                        <Search />
                    </el-icon>
                </template>
            </el-input>


            <el-select v-model="searchStatus" placeholder="状態" clearable style="width: 120px">
                <el-option label="有効" :value="Status.ENABLED" />
                <el-option label="無効" :value="Status.DISABLED" />
            </el-select>
        </div>

        <!-- 一覧テーブル -->
        <el-table :data="filterTableData" v-loading="tableLoading" style="width: 100%" row-key="id"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" default-expand-all>
            <el-table-column label="メニュー名" prop="name" min-width="100" />

            <el-table-column label="メニュータイプ" width="130">
                <template #default="{ row }">
                    <el-tag
                        :type="row.type === MenuType.MENU ? '' : row.type === MenuType.DIRECTORY ? 'success' : 'warning'"
                        size="small">
                        {{ row.type === MenuType.MENU ? 'MENU' : row.type === MenuType.DIRECTORY ? 'DIRECTORY' : 'MENU'
                        }}
                    </el-tag>
                </template>
            </el-table-column>


            <el-table-column label="状態" width="100">
                <template #default="{ row }">
                    <!-- 権限あり: 操作可能な switch -->
                    <!-- <el-switch v-if="hasPermission('sys:permission:changeStatus')" :model-value="row.status"
                        :active-value="Status.ENABLED" :inactive-value="Status.DISABLED" :loading="row._loading"
                        @change="(val: number) => handleStatusChange(row, val)" /> -->
                    <!-- 権限なし: 読み取り専用の tag -->
                    <el-tag  :type="row.status === Status.ENABLED ? 'success' : 'danger'" size="small">
                        {{ row.status === Status.ENABLED ? '有効' : '無効' }}
                    </el-tag>
                    
                </template>
            </el-table-column>

            <el-table-column label="作成日時" prop="createdAt" width="180" />

            <el-table-column label="更新日時" prop="updatedAt" width="180" />

            <el-table-column label="操作" align="right" width="160" fixed="right"
                v-if="hasAnyPermission(['sys:permission:update', 'sys:permission:delete'])">
                <template #default="{ row }">
                    <el-button size="small" v-if="hasPermission('sys:permission:update')" @click="handleEdit(row)">
                        編集
                    </el-button>
                    <el-button size="small" type="danger" v-if="hasPermission('sys:permission:delete')"
                        @click="handleDelete(row)">
                        削除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
    <MenuDialog v-model="dialogVisible" :data="dialogData"  :menu-options="menuOptions"
        @success="handleSuccess" />
</template>

<script lang="ts" setup>
defineOptions({
    name: 'MenuIndex',
})
import {
    ref, onMounted,
    computed
} from 'vue'
import { useBreadcrumb } from '@/composables/useBreadcrumb'
import { Status } from '@/types/enums/status'
import { MenuType } from '@/types/enums/menu'
import { getMenuTreeApi,getMenuOptionsApi, getMenuDetailApi } from '@/api/system/menu'
import type { MenuDetailVo, MenuOptionVo, MenuTreeVO, MenuView } from '@/types/system/menu/menuResponse'
import { usePermission } from '@/composables/usePermission'
import MenuDialog from './components/dialog.vue'


/****************** パンくずリスト ******************/
const {
    breadcrumbList,
    getBreadcrumbTo,
} = useBreadcrumb()


/****************** テーブル管理 ******************/
const tableData = ref<MenuView[]>([])
const { hasPermission, hasAnyPermission } = usePermission()
const tableLoading = ref(false)
const search = ref('')
const searchStatus = ref<Status | null>(null)

const filterTableData = computed(() => {
    const kw = search.value.trim().toLowerCase()
    const hasFilter = kw || searchStatus.value != null

    // フィルター条件がない場合はそのまま返す
    if (!hasFilter) return tableData.value

    const filterTree = (nodes: MenuView[]): MenuView[] => {
        return nodes.reduce<MenuView[]>((acc, node) => {
            const filteredChildren = node.children ? filterTree(node.children) : []
            const matchKeyword = !kw ||
                node.name.toLowerCase().includes(kw)
            const matchStatus = searchStatus.value == null || node.status === searchStatus.value

            // 自身がマッチする場合は子ノードをそのまま含める
            if (matchKeyword && matchStatus) {
                acc.push({ ...node, children: node.children ?? [] })
                // 自身はマッチしないが子ノードがマッチする場合
            } else if (filteredChildren.length > 0) {
                acc.push({ ...node, children: filteredChildren })
            }
            return acc
        }, [])
    }

    return filterTree(tableData.value)
})

/****************** データ読み込み ******************/
async function fetchMenuTree() {
    tableLoading.value = true
    try {

        const res = await getMenuTreeApi()

        tableData.value = mapTreeLoading(res.data || [])
    } catch (error) {
        console.error('Error fetching menu tree:', error)
    } finally {
        tableLoading.value = false
    }
}
function mapTreeLoading(nodes: MenuTreeVO[]): MenuView[] {
    return nodes.map(item => ({
        ...item,
        _loading: false,
        children: item.children ? mapTreeLoading(item.children) : []
    }))
}

const menuOptions = ref<MenuOptionVo[]>([])

async function fetchMenuOptions() {
    try {
        const res = await getMenuOptionsApi()
        menuOptions.value = res.data || []
    } catch (error) {
        console.error('Failed to fetch menu options', error)
    }
}

function handleSuccess() {
    fetchMenuTree()
    fetchMenuOptions()
}
onMounted(() => {
    handleSuccess()
})


/****************** ダイアログ管理 ******************/
const dialogVisible = ref(false)
const dialogData = ref<MenuDetailVo | null>(null)

function handleAdd() {
    console.log('dialogVisible', dialogVisible.value)
    dialogData.value = null
    dialogVisible.value = true
}
async function handleEdit(row: MenuView) {
    try {
        const res = await getMenuDetailApi(row.id)
        dialogData.value = { ...res.data }
        dialogVisible.value = true
        console.log('edit', row)
    } catch (error) {
        console.error(error)
    }
}
function handleDelete(row: MenuView) {
    console.log('delete', row)
}
// function handleStatusChange(row: MenuView, val: number) {
//     console.log('change status', row, val)
// }
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.page-title {
    margin: 4px 0 0;
    font-size: 18px;
    font-weight: 600;
}

.mb-8 {
    margin-bottom: 8px;
}

.search-bar {
    margin-bottom: 16px;
}
</style>