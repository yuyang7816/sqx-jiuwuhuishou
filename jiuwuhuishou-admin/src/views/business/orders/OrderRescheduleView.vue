<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import CrudPage from '@/components/common/CrudPage.vue'
import { orderRescheduleApi } from '@/api/modules/order-reschedule'
import { formatUserLabel } from '@/utils/display'

const crudRef = ref()
const router = useRouter()

const filters = reactive({ orderNo: '', userName: '', phone: '' })
const columns = [
  { prop: 'orderNo', label: '订单号', minWidth: 160 },
  { prop: 'user', label: '用户', minWidth: 160 },
  { prop: 'reason', label: '改约原因', minWidth: 140 },
  { prop: 'newAppointmentTime', label: '新预约时间', minWidth: 170 },
  { prop: 'remark', label: '备注', minWidth: 180, showOverflowTooltip: true },
  { prop: 'createTime', label: '提交时间', minWidth: 170 },
]

function buildQuery() {
  return {
    orderNo: filters.orderNo || undefined,
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
  }
}

function openOrder(row) {
  if (!row.orderId) return
  router.push(`/business/orders/${row.orderId}`)
}
</script>

<template>
  <CrudPage
    ref="crudRef"
    title="改约记录"
    :api="orderRescheduleApi"
    :columns="columns"
    :show-create="false"
    :show-edit="false"
    :show-delete="false"
  >
    <template #toolbar="{ loadData }">
      <el-form inline>
        <el-form-item label="订单号"><el-input v-model="filters.orderNo" clearable placeholder="订单号" /></el-form-item>
        <el-form-item label="用户名"><el-input v-model="filters.userName" clearable placeholder="用户名" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="filters.phone" clearable placeholder="手机号" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData(buildQuery())">查询</el-button>
          <el-button @click="Object.assign(filters, { orderNo: '', userName: '', phone: '' }); loadData({})">重置</el-button>
        </el-form-item>
      </el-form>
    </template>
    <template #column-user="{ row }">{{ formatUserLabel(row) }}</template>
    <template #actions="{ row }">
      <el-button v-if="row.orderId" link type="primary" @click="openOrder(row)">查看订单</el-button>
    </template>
  </CrudPage>
</template>
