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
            ユーザー一覧
          </h2>
        </div>

        <!-- 操作ボタン -->
        <el-button type="primary" v-permission="'sys:user:create'" @click="handleAdd">
          新規追加
        </el-button>
      </div>
    </template>

    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="アカウント値またはニックネームで検索" clearable style="width: 350px" @clear="handleSearch"
        @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch" style="margin-left: 8px">
        検索
      </el-button>
    </div>

    <!-- 一覧テーブル -->
    <el-table :data="tableData" v-loading="tableLoading" style="width: 100%">
      <el-table-column label="ユーザーID" prop="userId" width="190" />

      <el-table-column label="アカウントID" prop="accountId" width="150" />
      <el-table-column label="ニックネーム" prop="nickName" />
      <el-table-column label="ユーザー状態" width="180" v-if="hasPermission('sys:user:changeStatus')">
        <template #default="scope">
          <el-switch :model-value="scope.row.userStatus" :active-value="1" :inactive-value="0"
            :loading="scope.row._loading" @change="(val: number) => handleUserStatusChange(scope.row, val)" />
        </template>
      </el-table-column>
      <el-table-column label="アカウントタイプ" prop="accountType" width="150" />
      <el-table-column label="アカウント値" prop="accountValue" width="220" />
      <el-table-column label="ロール名" prop="roleName" width="150" />
      <el-table-column label="作成日時" prop="createdAt" width="220" />

      <el-table-column label="操作" width="210">
        <template #default="scope">
          <el-button size="small" v-if="hasPermission('sys:user:update')" @click="handleEdit(scope.row)">
            編集
          </el-button>

          <el-button size="small" type="danger" v-if="hasPermission('sys:user:delete')"
            @click="handleDelete(scope.row)">
            削除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next" style="margin-top: 16px; justify-content: flex-end"
      @change="fetchUserList" />
  </el-card>
  <UserDialog v-model="dialogVisible" :data="dialogData" @success="fetchUserList" />
</template>

<script setup lang="ts">
defineOptions({
  name: 'UserIndex',
})

import {
  ref, onMounted
} from 'vue'

import { useBreadcrumb } from '@/composables/useBreadcrumb'
import type {
  UserView,
  UserVO,
} from '@/types/system/user/userResponse'
import { changeUserStatusApi, deleteUserApi, findUserByIdApi, pageUserApi } from '@/api/system/user'
import UserDialog from './components/dialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Status } from '@/types/enums/status'
import type { UserChangeStatusRequest } from '@/types/system/user/userRequest'
import { usePermission } from '@/composables/usePermission'

/****************** パンくずリスト ******************/
const {
  breadcrumbList,
  getBreadcrumbTo,
} = useBreadcrumb()

/****************** テーブル管理 ******************/
const tableData = ref<UserView[]>([])
const { hasPermission } = usePermission()

const tableLoading = ref(false)

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const handleSearch = () => {
  pageNum.value = 1
  fetchUserList()
}
const fetchUserList = async () => {
  tableLoading.value = true
  try {
    const res = await pageUserApi(
      {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: searchKeyword.value || undefined
      })
    tableData.value = res.data.records.map((item: UserVO) => ({
      ...item,
      _loading: false,
    }))
    total.value = res.data.total
  } finally {
    tableLoading.value = false
  }
}

onMounted(() => fetchUserList())



/****************** ダイアログ操作 ******************/
const dialogVisible = ref(false)
const dialogData = ref<UserVO | null>(null)



/**
 * 新規追加処理
 */
function handleAdd() {
  dialogData.value = null
  dialogVisible.value = true
}
async function handleEdit(row: UserView) {
  try {
    const res = await findUserByIdApi(row.userId, row.accountId)
    dialogData.value = { ...res.data }
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
  }

}
async function handleDelete(row: UserView) {
  try {
    await ElMessageBox.confirm(
      `ユーザー「${row.nickName}」を削除しますか？`,
      '確認',
      {
        confirmButtonText: '確認',
        cancelButtonText: 'キャンセル',
        type: 'warning'
      }
    )

    await deleteUserApi(row.userId, row.accountId)
    ElMessage.success('削除に成功しました')
    fetchUserList()
  } catch (err: any) {
    if (err === 'cancel' || err === 'close') {
      ElMessage.info('操作をキャンセルしました')
    }

  }

}
async function handleUserStatusChange(row: UserView, newStatus: number) {
  console.log('ユーザー状態変更', row, newStatus)
  if (row._loading) return
  const oldStatus = row.userStatus
  try {
    await ElMessageBox.confirm(
      `状態を「${newStatus === 1 ? '有効' : '無効'}」に変更しますか？`,
      '確認',
      {
        confirmButtonText: '確認',
        cancelButtonText: 'キャンセル',
        type: 'warning'
      }
    )

    row._loading = true

    const targetStatus =
      newStatus === 1 ? Status.ENABLED : Status.DISABLED
    const request: UserChangeStatusRequest = {
      accountId: row.accountId,
      status: targetStatus,
    }
    await changeUserStatusApi(row.userId, request)
    row.userStatus = targetStatus
    ElMessage.success('更新に成功しました')
  } catch (err: any) {
    if (err === 'cancel' || err === 'close') {
      ElMessage.info('操作をキャンセルしました')
    } else {
      row.userStatus = oldStatus
      // エラーメッセージは拦截器が処理済み
    }
  } finally {
    row._loading = false
  }
}
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
