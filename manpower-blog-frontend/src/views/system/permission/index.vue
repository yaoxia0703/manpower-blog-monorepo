<template>
    <el-card>
        <!-- ヘッダー -->
        <template #header>
            <div class="card-header">
                <div>
                    <!-- パンくずリスト -->
                    <el-breadcrumb separator="/" class="mb-8">
                        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.path || item.id"
                            :to="getBreadcrumbTo(item, index)">
                            {{ item.name }}
                        </el-breadcrumb-item>
                    </el-breadcrumb>

                    <!-- タイトル -->
                    <h2 class="page-title">
                        権限一覧
                    </h2>
                </div>

                <!-- 操作ボタン -->
                <el-button type="primary" v-permission="'sys:permission:create'" @click="handleAdd">
                    新規追加
                </el-button>
            </div>
        </template>

        <!-- 検索エリア -->
        <div class="search-bar">
            <el-input v-model="searchKeyword" placeholder="権限名 / コードで検索" clearable style="width: 260px"
                @clear="handleSearch" @keyup.enter="handleSearch">
                <template #prefix>
                    <el-icon>
                        <Search />
                    </el-icon>
                </template>
            </el-input>

            <el-tree-select v-model="searchMenuId" :data="menuTree" :props="menuTreeProps" node-key="id"
                check-strictly clearable default-expand-all placeholder="所属メニュー" style="width: 220px" />

            <el-select v-model="searchMethod" placeholder="HTTPメソッド" clearable style="width: 140px">
                <el-option v-for="(val, key) in HttpMethod" :key="key" :label="val" :value="val" />
            </el-select>

            <el-select v-model="searchStatus" placeholder="状態" clearable style="width: 120px">
                <el-option label="有効" :value="Status.ENABLED" />
                <el-option label="無効" :value="Status.DISABLED" />
            </el-select>

            <el-button type="primary" @click="handleSearch">検索</el-button>
            <el-button @click="handleReset">リセット</el-button>
        </div>

        <!-- 一覧テーブル -->
        <el-table :data="tableData" v-loading="tableLoading" style="width: 100%" row-key="id">
            <el-table-column label="権限名" prop="name" min-width="100" />

            <el-table-column label="所属メニュー" min-width="140">
                <template #default="{ row }">
                    {{ row.menuName ?? '所属なし' }}
                </template>
            </el-table-column>

            <el-table-column label="表示順" prop="sort" width="90" align="center" />

            <el-table-column label="権限コード" prop="code" width="300" />

            <el-table-column label="パス" width="300">
                <template #default="{ row }">
                    <span>{{ row.path }}</span>
                </template>
            </el-table-column>

            <el-table-column label="HTTPメソッド" width="120">
                <template #default="{ row }">
                    <el-tag size="small" :type="methodTagType(row.method)">
                        {{ row.method }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="状態" width="100">
                <template #default="{ row }">
                    <el-tag :type="row.status === Status.ENABLED ? 'success' : 'danger'" size="small">
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

        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total"
            :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
            class="pagination" @change="fetchPermissionList" />
    </el-card>
    <PermissionDialog v-model="dialogVisible" :data="dialogData" @success="handleSuccess" />
</template>

<script setup lang="ts">
defineOptions({
    name: 'PermissionIndex',
})

import {
    ref, onMounted
} from 'vue'
import { useBreadcrumb } from '@/composables/useBreadcrumb'
import { Status } from '@/types/enums/status'
import { HttpMethod, type HttpMethod as HttpMethodType } from '@/types/enums/permission'
import { usePermission } from '@/composables/usePermission'
import type { PermissionVO } from '@/types/system/permission/permissionResponse'
import { deletePermissionApi, findPermissionByIdApi, pagePermissionApi } from '@/api/system/permission'
import { ElMessage, ElMessageBox } from 'element-plus'
import PermissionDialog from './components/dialog.vue'
import { listEnabledMenuTreeApi } from '@/api/system/menu'
import type { MenuTreeVO } from '@/types/system/menu/menuResponse'

/****************** パンくずリスト ******************/
const {
    breadcrumbList,
    getBreadcrumbTo,
} = useBreadcrumb()

/****************** テーブル管理 ******************/
const tableData = ref<PermissionVO[]>([])
const { hasPermission, hasAnyPermission } = usePermission()
const tableLoading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const searchMenuId = ref<number | null>(null)
const searchMethod = ref<HttpMethodType | null>(null)
const searchStatus = ref<Status | null>(null)
const menuTree = ref<MenuTreeVO[]>([])
const menuTreeProps = { label: 'name', children: 'children' }

function handleSearch() {
    pageNum.value = 1
    void fetchPermissionList()
}

function handleReset() {
    searchKeyword.value = ''
    searchMenuId.value = null
    searchMethod.value = null
    searchStatus.value = null
    handleSearch()
}

const methodTagType = (method: string) => {
    const map: Record<string, string> = {
        GET: 'success',
        POST: 'primary',
        PUT: 'warning',
        DELETE: 'danger',
        PATCH: 'info',
    }
    return map[method] ?? ''
}

/****************** ダイアログ管理 ******************/
const dialogVisible = ref(false)
const dialogData = ref<PermissionVO | null>(null)

function handleAdd() {
    dialogData.value = null
    dialogVisible.value = true
}
async function handleEdit(row: PermissionVO) {
    try {
        const res = await findPermissionByIdApi(row.id)
        dialogData.value = { ...res.data }
        dialogVisible.value = true
    } catch (error) {
        console.error(error)
    }
}
async function handleDelete(row: PermissionVO) {
    try {
        await ElMessageBox.confirm(`権限「${row.name}」を削除しますか？`, '削除確認', {
            confirmButtonText: '削除',
            cancelButtonText: 'キャンセル',
            type: 'warning',
        })
        await deletePermissionApi(row.id)
        ElMessage.success('削除しました')
        await fetchPermissionList()
    } catch (error: any) {
        if (error !== 'cancel' && error !== 'close') {
            console.error('Failed to delete permission', error)
        }
    }
}
/****************** データ読み込み ******************/
async function fetchPermissionList() {
    tableLoading.value = true
    try {

        const response = await pagePermissionApi({
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            keyword: searchKeyword.value.trim() || undefined,
            menuId: searchMenuId.value ?? undefined,
            method: searchMethod.value ?? undefined,
            status: searchStatus.value ?? undefined,
        })
        tableData.value = response.data.records
        total.value = response.data.total

        if (tableData.value.length === 0 && total.value > 0 && pageNum.value > 1) {
            pageNum.value -= 1
            await fetchPermissionList()
        }
    } catch (error) {
        console.error('Error fetching permission list:', error)
    } finally {
        tableLoading.value = false
    }
}
function handleSuccess() {
    void fetchPermissionList()
}

async function fetchMenuTree() {
    try {
        const response = await listEnabledMenuTreeApi()
        menuTree.value = response.data || []
    } catch (error) {
        console.error('Error fetching menu tree:', error)
        menuTree.value = []
    }
}

onMounted(() => {
    void Promise.all([fetchPermissionList(), fetchMenuTree()])
})
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
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 16px;
}

.pagination {
    margin-top: 16px;
    justify-content: flex-end;
}
</style>
