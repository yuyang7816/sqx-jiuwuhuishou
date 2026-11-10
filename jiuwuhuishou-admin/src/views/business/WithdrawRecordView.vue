<script setup>
import { reactive, ref } from 'vue'
import CrudPage from '@/components/common/CrudPage.vue'
import AppDialog from '@/components/common/AppDialog.vue'
import { confirmWithdrawRecordApi, withdrawRecordApi } from '@/api/modules/withdraw-record'
import { formatUserLabel, withdrawAccountTypeMap, withdrawStatusMap } from '@/utils/display'
import { showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const crudRef = ref()
const rejectDialogVisible = ref(false)
const rejectLoading = ref(false)
const rejectForm = reactive({ id: null, reason: '' })

const filters = reactive({
  userName: '',
  phone: '',
  status: '',
})

const columns = [
  { prop: 'user', label: '用户', minWidth: 180 },
  { prop: 'amount', label: '金额', width: 100 },
  { prop: 'actualAmount', label: '到账金额', width: 100 },
  { prop: 'accountType', label: '账户类型', width: 100 },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'createTime', label: '创建时间', minWidth: 180 },
]

function buildQuery() {
  return {
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
    status: filters.status === '' ? undefined : Number(filters.status),
  }
}

function handleSearch(loadData) {
  loadData(buildQuery())
}

function handleReset(loadData) {
  Object.assign(filters, { userName: '', phone: '', status: '' })
  loadData({})
}

async function approveRow(row) {
  await confirmWithdrawRecordApi({ id: row.id, status: 2 })
  showSuccess('提现已通过')
  crudRef.value?.loadData(buildQuery())
}

function openReject(row) {
  Object.assign(rejectForm, { id: row.id, reason: '' })
  rejectDialogVisible.value = true
}

async function submitReject() {
  rejectLoading.value = true
  try {
    await confirmWithdrawRecordApi({ id: rejectForm.id, status: 3, reason: rejectForm.reason })
    showSuccess('提现已拒绝')
    rejectDialogVisible.value = false
    crudRef.value?.loadData(buildQuery())
  } finally {
    rejectLoading.value = false
  }
}
</script>

<template>
  <CrudPage
    ref="crudRef"
    title="提现记录"
    :api="withdrawRecordApi"
    :columns="columns"
    :show-create="false"
    :show-edit="false"
    :show-delete="false"
  >
    <template #toolbar="{ loadData }">
      <el-form :inline="true" class="search-grid">
        <el-form-item label="用户名">
          <el-input v-model="filters.userName" clearable placeholder="用户名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="filters.phone" clearable placeholder="手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部">
            <el-option label="审核中" :value="1" />
            <el-option label="成功" :value="2" />
            <el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-space>
            <el-button type="primary" @click="handleSearch(loadData)">搜索</el-button>
            <el-button @click="handleReset(loadData)">重置</el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </template>
    <template #column-user="{ row }">{{ formatUserLabel(row) }}</template>
    <template #column-accountType="{ row }">
      <el-tag :type="withdrawAccountTypeMap[row.accountType]?.type">{{ withdrawAccountTypeMap[row.accountType]?.label || row.accountType }}</el-tag>
    </template>
    <template #column-status="{ row }">
      <el-tag :type="withdrawStatusMap[row.status]?.type">{{ withdrawStatusMap[row.status]?.label || row.status }}</el-tag>
    </template>
    <template #actions="{ row }">
      <el-button v-if="row.status === 1 && isAuth(PERMS.withdrawAudit)" link type="success" @click="approveRow(row)">通过</el-button>
      <el-button v-if="row.status === 1 && isAuth(PERMS.withdrawAudit)" link type="danger" @click="openReject(row)">拒绝</el-button>
    </template>
  </CrudPage>
  <AppDialog v-model="rejectDialogVisible" title="拒绝提现" :loading="rejectLoading" @confirm="submitReject">
    <el-form :model="rejectForm" label-width="96px">
      <el-form-item label="拒绝理由">
        <el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请填写拒绝原因" />
      </el-form-item>
    </el-form>
  </AppDialog>
</template>
