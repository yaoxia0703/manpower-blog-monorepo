<template>
  <el-drawer
    v-model="visible"
    :title="`「${roleName}」の認可設定`"
    size="78%"
    destroy-on-close
  >
    <el-alert
      v-if="authorizationWarnings.length"
      type="warning"
      :closable="false"
      show-icon
      class="authorization-warning"
    >
      <template #title>{{ authorizationWarnings.join('；') }}</template>
    </el-alert>

    <el-row v-loading="loading" :gutter="20" class="authorization-body">
      <el-col :xs="24" :md="10">
        <h3>メニュー付与</h3>
        <el-tree
          ref="menuTreeRef"
          :data="menus"
          :props="menuTreeProps"
          show-checkbox
          node-key="id"
          default-expand-all
          @check="syncSelectedMenuIds"
        />
      </el-col>

      <el-col :xs="24" :md="14">
        <h3>権限付与</h3>
        <div class="permission-groups">
          <section v-for="group in permissionGroups" :key="group.key" class="permission-group">
            <h4>{{ group.name }}</h4>
            <el-checkbox-group v-model="selectedPermissionIds">
              <el-row>
                <el-col v-for="permission in group.permissions" :key="permission.id" :xs="24" :lg="12">
                  <el-checkbox :label="permission.id">
                    <span>{{ permission.name }}</span>
                    <span class="permission-code">{{ permission.code }}</span>
                  </el-checkbox>
                </el-col>
              </el-row>
            </el-checkbox-group>
          </section>
        </div>
      </el-col>
    </el-row>

    <template #footer>
      <el-button @click="handleCancel">キャンセル</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">認可設定を保存</el-button>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import { ElMessage, type ElTree } from 'element-plus'
import { getRoleAuthorizationApi, saveRoleAuthorizationApi } from '@/api/system/role'
import type { MenuTreeVO } from '@/types/system/menu/menuResponse'
import type { PermissionVO } from '@/types/system/permission/permissionResponse'

interface PermissionGroup {
  key: string
  name: string
  permissions: PermissionVO[]
}

const props = defineProps<{
  modelValue: boolean
  roleId: number | null
  roleName: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}>()

const visible = ref(false)
const loading = ref(false)
const submitLoading = ref(false)
const menus = ref<MenuTreeVO[]>([])
const permissions = ref<PermissionVO[]>([])
const selectedMenuIds = ref<number[]>([])
const selectedPermissionIds = ref<number[]>([])
const menuTreeRef = ref<InstanceType<typeof ElTree>>()

const menuTreeProps = {
  label: 'name',
  children: 'children',
}

const flatMenus = computed(() => {
  const result: MenuTreeVO[] = []
  const walk = (items: MenuTreeVO[]) => {
    items.forEach(item => {
      result.push(item)
      if (item.children?.length) walk(item.children)
    })
  }
  walk(menus.value)
  return result
})

const permissionGroups = computed<PermissionGroup[]>(() => {
  const menuById = new Map(flatMenus.value.map(menu => [menu.id, menu]))
  const groups = new Map<string, PermissionGroup>()

  permissions.value.forEach(permission => {
    const key = permission.menuId == null ? 'ungrouped' : String(permission.menuId)
    const menu = permission.menuId == null ? undefined : menuById.get(permission.menuId)
    if (!groups.has(key)) {
      groups.set(key, {
        key,
        name: menu?.name ?? '所属メニューなし',
        permissions: [],
      })
    }
    groups.get(key)?.permissions.push(permission)
  })

  return [...groups.values()]
})

const authorizationWarnings = computed(() => {
  const checkedMenus = new Set(selectedMenuIds.value)
  const missing = permissions.value
    .filter(permission => selectedPermissionIds.value.includes(permission.id))
    .filter(permission => permission.menuId != null && !checkedMenus.has(permission.menuId))
    .map(permission => permission.name)

  return missing.length
    ? [`対応メニューが選択されていない権限があります：${[...new Set(missing)].join('、')}`]
    : []
})

watch(() => props.modelValue, async value => {
  visible.value = value
  if (value && props.roleId != null) await fetchData()
})

watch(visible, value => {
  emit('update:modelValue', value)
  if (!value) resetState()
})

async function fetchData() {
  loading.value = true
  try {
    const response = await getRoleAuthorizationApi(props.roleId!)
    const data = response.data
    menus.value = data.menus ?? []
    permissions.value = data.permissions ?? []
    selectedMenuIds.value = [...(data.selectedMenuIds ?? [])]
    selectedPermissionIds.value = [...(data.selectedPermissionIds ?? [])]
    await nextTick()
    menuTreeRef.value?.setCheckedKeys(selectedMenuIds.value, false)
    syncSelectedMenuIds()
  } catch (error) {
    console.error('認可設定の取得に失敗しました:', error)
  } finally {
    loading.value = false
  }
}

function getSelectedMenuIds() {
  const checked = (menuTreeRef.value?.getCheckedKeys(false) ?? []) as number[]
  const halfChecked = (menuTreeRef.value?.getHalfCheckedKeys() ?? []) as number[]
  return [...new Set([...checked, ...halfChecked])]
}

function syncSelectedMenuIds() {
  selectedMenuIds.value = getSelectedMenuIds()
}

async function handleSubmit() {
  if (props.roleId == null) return
  submitLoading.value = true
  try {
    syncSelectedMenuIds()
    await saveRoleAuthorizationApi(props.roleId, {
      menuIds: selectedMenuIds.value,
      permissionIds: selectedPermissionIds.value,
    })
    ElMessage.success('認可設定を保存しました')
    emit('success')
    handleCancel()
  } catch (error) {
    console.error('認可設定の保存に失敗しました:', error)
  } finally {
    submitLoading.value = false
  }
}

function handleCancel() {
  emit('update:modelValue', false)
}

function resetState() {
  menus.value = []
  permissions.value = []
  selectedMenuIds.value = []
  selectedPermissionIds.value = []
}
</script>

<style scoped>
.authorization-warning {
  margin-bottom: 16px;
}

.authorization-body {
  min-height: 520px;
}

.authorization-body h3,
.permission-group h4 {
  margin: 0 0 12px;
}

.permission-group {
  margin-bottom: 18px;
}

.permission-code {
  margin-left: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
