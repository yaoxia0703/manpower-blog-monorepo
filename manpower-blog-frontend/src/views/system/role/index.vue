<template>
  <el-card>
    <!-- ヘッダー -->
    <template #header>
      <div class="card-header">
        <div>
          <el-breadcrumb separator="/" class="mb-8">
            <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.permissionPath"
              :to="getBreadcrumbTo(item, index)">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
          <h2 class="page-title">役割一覧</h2>
        </div>
        <el-button type="primary" v-if="hasPermission('sys:role:create')" @click="handleAdd">
          新規追加
        </el-button>
      </div>
    </template>

    <!-- 検索エリア -->
    <div class="search-bar">
      <el-input v-model="search" placeholder="名前で検索" clearable style="width: 240px" />
    </div>

    <!-- 一覧テーブル -->
    <el-table :data="filterTableData" v-loading="tableLoading" style="width: 100%">
      <el-table-column label="コード" prop="code" width="280" />
      <el-table-column label="名前" prop="name" />

      <el-table-column label="状態" width="120" v-if="hasPermission('sys:role:changeStatus')">
        <template #default="scope">
          <el-switch :model-value="scope.row.status" :active-value="1" :inactive-value="0"
            :loading="scope.row._loading" @change="(val: number) => handleStatusChange(scope.row, val)" />
        </template>
      </el-table-column>

      <el-table-column label="作成日時" prop="createdAt" width="220" />
      <el-table-column label="更新日時" prop="updatedAt" width="220" />

      <el-table-column label="操作" align="right" width="360">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            v-if="hasPermission('sys:role:assignPermission')"
            @click="handlePermission(scope.row)"
          >
            権限設定
          </el-button>
          <el-button
            size="small"
            type="warning"
            v-if="hasPermission('sys:role:assignMenu')"
            @click="handleMenu(scope.row)"
          >
            メニュー設定
          </el-button>
          <el-button
            size="small"
            v-if="hasPermission('sys:role:update')"
            @click="handleEdit(scope.row)"
          >
            編集
          </el-button>
          <el-button
            size="small"
            type="danger"
            v-if="hasPermission('sys:role:delete')"
            @click="handleDelete(scope.row)"
          >
            削除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <RoleDialog v-model="dialogVisible" :data="dialogData" @success="loadData" />

  <!-- 権限設定Drawer -->
  <PermissionDrawer
    v-model="drawerVisible"
    :role-id="drawerRoleId"
    :role-name="drawerRoleName"
  />

  <!-- メニュー設定Drawer -->
  <MenuDrawer
    v-model="menuDrawerVisible"
    :role-id="menuDrawerRoleId"
    :role-name="menuDrawerRoleName"
  />
</template>

<script setup lang="ts">
defineOptions({
  name: 'RoleIndex',
})

import { computed, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useBreadcrumb } from '@/composables/useBreadcrumb'
import {
  changeRoleStatusApi,
  deleteRoleApi,
  detailRoleApi,
  getRoleListApi,
} from '@/api/system/role'
import type { RoleVO, RoleView } from '@/types/system/role/roleResponse'
import { Status } from '@/types/enums/status'
import RoleDialog from './components/dialog.vue'
import PermissionDrawer from './components/PermissionDrawer.vue'
import MenuDrawer from './components/MenuDrawer.vue'
import { usePermission } from '@/composables/usePermission'

/****************** パンくずリスト ******************/
const { breadcrumbList, getBreadcrumbTo } = useBreadcrumb()

/****************** テーブル ******************/
const search = ref('')
const tableLoading = ref(false)
const tableData = ref<RoleView[]>([])
const { hasPermission } = usePermission()

const filterTableData = computed(() => {
  const kw = search.value.trim().toLowerCase()
  return tableData.value.filter(item =>
    (item.name || '').toLowerCase().includes(kw)
  )
})

/****************** ダイアログ操作 ******************/
const dialogVisible = ref(false)
const dialogData = ref<RoleVO | null>(null)

function handleAdd() {
  dialogData.value = null
  dialogVisible.value = true
}

async function handleEdit(row: RoleView) {
  try {
    const res = await detailRoleApi(row.id)
    dialogData.value = { ...res.data }
    dialogVisible.value = true
  } catch (error) {
    console.error('Failed to fetch role details:', error)
  }
}

async function handleDelete(row: RoleView) {
  try {
    await ElMessageBox.confirm(
      `役割「${row.name}」を削除しますか？`,
      '確認',
      { confirmButtonText: '確認', cancelButtonText: 'キャンセル', type: 'warning' }
    )
    await deleteRoleApi(row.id)
    ElMessage.success('削除に成功しました')
    loadData()
  } catch (err: any) {
    if (err === 'cancel' || err === 'close') {
      ElMessage.info('操作をキャンセルしました')
    }
  }
}

async function handleStatusChange(row: RoleView, newStatus: number) {
  if (row._loading) return
  const oldStatus = row.status
  try {
    await ElMessageBox.confirm(
      `状態を「${newStatus === 1 ? '有効' : '無効'}」に変更しますか？`,
      '確認',
      { confirmButtonText: '確認', cancelButtonText: 'キャンセル', type: 'warning' }
    )
    row._loading = true
    const targetStatus = newStatus === 1 ? Status.ENABLED : Status.DISABLED
    await changeRoleStatusApi(row.id, targetStatus)
    row.status = targetStatus
    ElMessage.success('更新に成功しました')
  } catch (err: any) {
    if (err === 'cancel' || err === 'close') {
      ElMessage.info('操作をキャンセルしました')
    } else {
      row.status = oldStatus
    }
  } finally {
    row._loading = false
  }
}

/****************** 権限設定Drawer ******************/
const drawerVisible = ref(false)
const drawerRoleId = ref<number | null>(null)
const drawerRoleName = ref('')

function handlePermission(row: RoleView) {
  drawerRoleId.value = row.id
  drawerRoleName.value = row.name
  drawerVisible.value = true
}

/****************** メニュー設定Drawer ******************/
const menuDrawerVisible = ref(false)
const menuDrawerRoleId = ref<number | null>(null)
const menuDrawerRoleName = ref('')

function handleMenu(row: RoleView) {
  menuDrawerRoleId.value = row.id
  menuDrawerRoleName.value = row.name
  menuDrawerVisible.value = true
}

/****************** データ読み込み ******************/
async function loadData() {
  tableLoading.value = true
  try {
    const res = await getRoleListApi()
    tableData.value = (res.data || []).map((item: RoleVO) => ({
      ...item,
      _loading: false,
    }))
  } catch (error) {
    console.error('Failed to load role data:', error)
  } finally {
    tableLoading.value = false
  }
}

onMounted(() => {
  loadData()
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
  margin-bottom: 16px;
}
</style>