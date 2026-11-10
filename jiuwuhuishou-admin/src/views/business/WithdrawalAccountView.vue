<script setup>
import { reactive } from 'vue'
import CrudPage from '@/components/common/CrudPage.vue'
import { withdrawalAccountApi } from '@/api/modules/withdrawal-account'
import { formatUserLabel, withdrawalAccountTypeMap } from '@/utils/display'

const filters = reactive({ userName: '', phone: '' })

const columns = [
  { prop: 'user', label: '用户', minWidth: 180 },
  { prop: 'accountName', label: '账户名称', minWidth: 140 },
  { prop: 'accountNumber', label: '账号', minWidth: 160 },
  { prop: 'type', label: '类型', width: 100 },
]

function buildQuery() {
  return {
    source: 1,
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
  }
}

function handleSearch(loadData) {
  loadData(buildQuery())
}

function handleReset(loadData) {
  Object.assign(filters, { userName: '', phone: '' })
  loadData({ source: 1 })
}
</script>

<template>
  <CrudPage
    title="提现账户"
    :api="withdrawalAccountApi"
    :columns="columns"
    :default-query="{ source: 1 }"
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
    <template #column-type="{ row }">
      <el-tag :type="withdrawalAccountTypeMap[row.type]?.type">{{ withdrawalAccountTypeMap[row.type]?.label || row.type }}</el-tag>
    </template>
  </CrudPage>
</template>
