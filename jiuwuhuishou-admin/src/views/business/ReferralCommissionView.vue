<script setup>
import { reactive } from 'vue'
import CrudPage from '@/components/common/CrudPage.vue'
import { referralCommissionApi } from '@/api/modules/referral-commission'
import { formatUserLabel, referralTypeMap } from '@/utils/display'

const filters = reactive({
  userName: '',
  phone: '',
  orderNo: '',
})

const columns = [
  { prop: 'user', label: '用户', minWidth: 180 },
  { prop: 'orderNo', label: '订单号', minWidth: 160 },
  { prop: 'amount', label: '佣金', width: 100 },
  { prop: 'rate', label: '比例', width: 80 },
  { prop: 'type', label: '类型', width: 110 },
  { prop: 'createTime', label: '创建时间', minWidth: 180 },
]

function buildQuery() {
  return {
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
    orderNo: filters.orderNo || undefined,
  }
}

function handleSearch(loadData) {
  loadData(buildQuery())
}

function handleReset(loadData) {
  Object.assign(filters, { userName: '', phone: '', orderNo: '' })
  loadData({})
}
</script>

<template>
  <CrudPage
    title="分销佣金"
    :api="referralCommissionApi"
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
        <el-form-item label="订单号">
          <el-input v-model="filters.orderNo" clearable placeholder="订单号" />
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
    <template #column-orderNo="{ row }">{{ row.orderNo || '—' }}</template>
    <template #column-type="{ row }">
      <el-tag :type="referralTypeMap[row.type]?.type">{{ referralTypeMap[row.type]?.label || row.type }}</el-tag>
    </template>
  </CrudPage>
</template>
