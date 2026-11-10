<script setup>
import { reactive, ref } from 'vue'
import CrudPage from '@/components/common/CrudPage.vue'
import AppDialog from '@/components/common/AppDialog.vue'
import { updateUserWalletAmountApi, userWalletApi } from '@/api/modules/user-wallet'
import { formatUserLabel } from '@/utils/display'
import { showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const crudRef = ref()
const filters = reactive({ userName: '', phone: '' })
const amountDialogVisible = ref(false)
const amountLoading = ref(false)
const amountForm = reactive({ userId: null, amount: 0, operator: 'incr' })

const columns = [
  { prop: 'user', label: '用户', minWidth: 180 },
  { prop: 'amount', label: '余额', minWidth: 120 },
  { prop: 'createTime', label: '创建时间', minWidth: 180 },
]

function buildQuery() {
  return {
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
  }
}

function handleSearch(loadData) {
  loadData(buildQuery())
}

function handleReset(loadData) {
  Object.assign(filters, { userName: '', phone: '' })
  loadData({})
}

function openAmount(row) {
  Object.assign(amountForm, { userId: row.userId, amount: 0, operator: 'incr' })
  amountDialogVisible.value = true
}

async function submitAmount() {
  amountLoading.value = true
  try {
    await updateUserWalletAmountApi({ ...amountForm })
    showSuccess('余额已调整')
    amountDialogVisible.value = false
    crudRef.value?.loadData(buildQuery())
  } finally {
    amountLoading.value = false
  }
}
</script>

<template>
  <CrudPage
    ref="crudRef"
    title="用户钱包"
    :api="userWalletApi"
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
        <el-form-item>
          <el-space>
            <el-button type="primary" @click="handleSearch(loadData)">搜索</el-button>
            <el-button @click="handleReset(loadData)">重置</el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </template>
    <template #column-user="{ row }">{{ formatUserLabel(row) }}</template>
    <template #actions="{ row }">
      <el-button v-if="isAuth(PERMS.userWalletUpdate)" link type="warning" @click="openAmount(row)">调额</el-button>
    </template>
  </CrudPage>
  <AppDialog v-model="amountDialogVisible" title="调整余额" :loading="amountLoading" @confirm="submitAmount">
    <el-form :model="amountForm" label-width="110px">
      <el-form-item label="金额"><el-input-number v-model="amountForm.amount" :min="0" :precision="2" /></el-form-item>
      <el-form-item label="方向">
        <el-select v-model="amountForm.operator">
          <el-option label="增加" value="incr" />
          <el-option label="减少" value="decr" />
        </el-select>
      </el-form-item>
    </el-form>
  </AppDialog>
</template>
