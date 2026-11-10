<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import { getMenuListApi } from '@/api/modules/menus'
import { createRoleApi, deleteRoleApi, getRoleInfoApi, getRolePage, updateRoleApi } from '@/api/modules/roles'
import { validateForm } from '@/utils/form'
import { confirmAction, showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'

const TEMP_KEY = -666666

const loading = ref(false)
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const detailLoading = ref(false)
const formRef = ref()
const menuTreeRef = ref()
const tableData = ref([])
const tableSelections = ref([])
const menuTree = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const editingId = ref(0)

const filters = reactive({
  roleName: '',
})

const form = reactive({
  id: 0,
  roleName: '',
  remark: '',
})

const rules = {
  roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
}

const treeProps = {
  label: 'name',
  children: 'children',
}

function buildMenuTree(list = []) {
  const map = new Map()
  const roots = []

  list.forEach((item) => {
    map.set(item.menuId, {
      ...item,
      list: undefined,
      children: [],
    })
  })

  map.forEach((node) => {
    const parent = map.get(node.parentId)
    if (parent) {
      parent.children.push(node)
      return
    }
    roots.push(node)
  })

  const sortNodes = (nodes) => {
    nodes.sort((a, b) => {
      const orderDiff = Number(a.orderNum || 0) - Number(b.orderNum || 0)
      if (orderDiff !== 0) return orderDiff
      return Number(a.menuId || 0) - Number(b.menuId || 0)
    })
    nodes.forEach((node) => sortNodes(node.children || []))
    return nodes
  }

  return sortNodes(roots)
}

function resetForm() {
  Object.assign(form, {
    id: 0,
    roleName: '',
    remark: '',
  })
}

function normalizeRoleRow(item = {}) {
  return {
    ...item,
    roleId: item.roleId,
    roleName: item.roleName || '',
    remark: item.remark || '',
    createTime: item.createTime || '',
  }
}

function normalizeMenuIds(menuIdList = []) {
  if (!Array.isArray(menuIdList)) {
    return []
  }
  const nextList = [...menuIdList]
  const idx = nextList.indexOf(TEMP_KEY)
  if (idx !== -1) {
    nextList.splice(idx, nextList.length - idx)
  }
  return nextList
    .map((id) => Number(id))
    .filter((id) => id > 0 && id !== TEMP_KEY)
}

/** 从树中筛出「在已授权列表里」的叶子节点，用于回显勾选 */
function pickLeafCheckedKeys(nodes = [], menuIdList = []) {
  const idSet = new Set(normalizeMenuIds(menuIdList))
  const leafKeys = []
  const walk = (list) => {
    list.forEach((node) => {
      const children = node.children || []
      if (children.length) {
        walk(children)
        return
      }
      if (idSet.has(Number(node.menuId))) {
        leafKeys.push(node.menuId)
      }
    })
  }
  walk(nodes)
  return leafKeys
}

/**
 * 回显勾选：只能 set 叶子节点。
 * 保存时会把半选父节点一并入库；若回显时把父节点也 setCheckedKeys，
 * el-tree 会当成全选，把已取消的子权限又勾回来。
 */
async function applyMenuCheckedKeys(menuIdList = []) {
  await nextTick()
  const tree = menuTreeRef.value
  if (!tree) return
  tree.setCheckedKeys([])
  await nextTick()
  tree.setCheckedKeys(pickLeafCheckedKeys(menuTree.value, menuIdList))
}

function buildSubmitPayload() {
  const checked = menuTreeRef.value?.getCheckedKeys(false) || []
  const halfChecked = menuTreeRef.value?.getHalfCheckedKeys() || []
  // 全选节点 + 半选父节点都要入库（菜单树展示依赖父级）；回显时再按叶子还原
  const menuIdList = [...new Set([...checked, ...halfChecked])]
    .map((id) => Number(id))
    .filter((id) => id > 0 && id !== TEMP_KEY)

  return {
    roleId: editingId.value || undefined,
    roleName: form.roleName.trim(),
    remark: form.remark.trim(),
    menuIdList,
  }
}

async function loadData() {
  loading.value = true
  try {
    const data = await getRolePage({
      page: pageNum.value,
      limit: pageSize.value,
      roleName: filters.roleName,
    })
    tableData.value = (data.list || []).map(normalizeRoleRow)
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleReset() {
  filters.roleName = ''
  pageNum.value = 1
  loadData()
}

function handleSelectionChange(value) {
  tableSelections.value = value
}

async function loadMenuTree() {
  const menuList = await getMenuListApi()
  menuTree.value = buildMenuTree(menuList)
}

async function openCreate() {
  editingId.value = 0
  resetForm()
  dialogVisible.value = true
  detailLoading.value = true
  try {
    await loadMenuTree()
    await nextTick()
    formRef.value?.clearValidate()
    await applyMenuCheckedKeys([])
  } finally {
    detailLoading.value = false
  }
}

async function openEdit(row) {
  editingId.value = row.roleId
  resetForm()
  dialogVisible.value = true
  detailLoading.value = true
  try {
    const [menus, detail] = await Promise.all([getMenuListApi(), getRoleInfoApi(row.roleId)])
    menuTree.value = buildMenuTree(menus)
    Object.assign(form, {
      id: detail.roleId || 0,
      roleName: detail.roleName || '',
      remark: detail.remark || '',
    })
    formRef.value?.clearValidate()
    await applyMenuCheckedKeys(detail.menuIdList)
  } finally {
    detailLoading.value = false
  }
}

async function handleSubmit() {
  const valid = await validateForm(formRef)
  if (!valid) return
  dialogLoading.value = true
  try {
    const payload = buildSubmitPayload()
    if (editingId.value) {
      await updateRoleApi(payload)
      showSuccess('角色已更新')
    } else {
      await createRoleApi(payload)
      showSuccess('角色已新增')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    dialogLoading.value = false
  }
}

async function handleDelete(row) {
  const roleIds = row ? [row.roleId] : tableSelections.value.map((item) => item.roleId)
  if (!roleIds.length) {
    return
  }
  const roleNames = row ? [row.roleName] : tableSelections.value.map((item) => item.roleName).filter(Boolean)
  const message = row
    ? `确认删除角色 ${row.roleName} 吗？`
    : `确认批量删除这些角色：${roleNames.join('、')} 吗？`
  await confirmAction(message)
  await deleteRoleApi(roleIds)
  showSuccess('操作成功')
  await loadData()
}

function handleDialogVisible(value) {
  dialogVisible.value = value
  if (!value) {
    editingId.value = 0
    resetForm()
    menuTreeRef.value?.setCheckedKeys([])
  }
}

onMounted(loadData)
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <strong>角色管理</strong>
        <el-space>
          <el-button v-if="isAuth('sys:role:save')" type="primary" @click="openCreate">新增角色</el-button>
          <el-button
            v-if="isAuth('sys:role:delete')"
            type="danger"
            :disabled="!tableSelections.length"
            @click="handleDelete()"
          >
            批量删除
          </el-button>
        </el-space>
      </div>
    </template>

    <el-form :inline="true" class="search-grid" @submit.prevent>
      <el-form-item label="角色名称">
        <el-input v-model="filters.roleName" clearable placeholder="请输入角色名称" @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item>
        <el-space>
          <el-button @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-space>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="roleName" label="角色名称" min-width="160" align="center" />
      <el-table-column prop="remark" label="备注" min-width="220" align="center" />
      <el-table-column prop="createTime" label="创建时间" min-width="180" align="center" />
      <el-table-column label="操作" fixed="right" min-width="150" align="center">
        <template #default="{ row }">
          <el-space>
            <el-button v-if="isAuth('sys:role:update')" link type="primary" @click="openEdit(row)">修改</el-button>
            <el-button v-if="isAuth('sys:role:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>

    <BasePagination
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="[10, 20, 30, 50, 100]"
      :total="total"
      @update:current-page="pageNum = $event; loadData()"
      @update:page-size="pageSize = $event; pageNum = 1; loadData()"
    />

    <AppDialog
      :model-value="dialogVisible"
      :title="editingId ? '修改角色' : '新增角色'"
      :loading="dialogLoading"
      width="720px"
      @update:modelValue="handleDialogVisible"
      @confirm="handleSubmit"
    >
      <div v-loading="detailLoading">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="form.roleName" placeholder="角色名称" maxlength="40" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" placeholder="备注" maxlength="120" />
          </el-form-item>
          <el-form-item label="授权">
            <el-tree
              ref="menuTreeRef"
              class="role-menu-tree"
              :data="menuTree"
              :props="treeProps"
              node-key="menuId"
              show-checkbox
              default-expand-all
              :check-strictly="false"
            />
          </el-form-item>
        </el-form>
      </div>
    </AppDialog>
  </el-card>
</template>

<style scoped>
.role-menu-tree {
  width: 100%;
  max-height: 420px;
  padding: 12px;
  overflow: auto;
  border: 1px solid var(--el-border-color);
  border-radius: 12px;
}
</style>
