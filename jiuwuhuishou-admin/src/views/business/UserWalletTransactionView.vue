<script setup>
import { reactive } from 'vue'
import CrudPage from '@/components/common/CrudPage.vue'
import { userWalletTransactionApi } from '@/api/modules/user-wallet-transaction'
import { directionMap, formatUserLabel } from '@/utils/display'

const filters = reactive({ userName: '', phone: '' })

const columns = [
  { prop: 'user', label: '用户', minWidth: 180 },
  { prop: 'title', label: '标题', minWidth: 140 },
  { prop: 'amount', label: '金额', width: 100 },
  { prop: 'direction', label: '方向', width: 90 },
  { prop: 'status', label: '状态', width: 90 },
  { prop: 'createTime', label: '创建时间', minWidth: 180 },
]

const statusMap = {
  1: { label: '待处理', type: 'warning' },
  2: { label: '已完成', type: 'success' },
  3: { label: '已取消', type: 'info' },
  4: { label: '失败', type: 'danger' },
}

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
</script>

<template>
  <CrudPage
    title="钱包流水"
    :api="userWalletTransactionApi"
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
    <template #column-direction="{ row }">
      <el-tag :type="directionMap[row.direction]?.type">{{ directionMap[row.direction]?.label || row.direction }}</el-tag>
    </template>
    <template #column-status="{ row }">
      <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.label || row.status }}</el-tag>
    </template>
  </CrudPage>
</template>
