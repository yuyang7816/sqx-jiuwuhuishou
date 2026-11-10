<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import CrudPage from '@/components/common/CrudPage.vue'
import AppDialog from '@/components/common/AppDialog.vue'
import { handleOrderExceptionApi, orderExceptionApi } from '@/api/modules/order-exception'
import { formatUserLabel, orderStatusKeyMap, tagMeta } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { showSuccess } from '@/utils/modal'

const crudRef = ref()
const router = useRouter()

const filters = reactive({ orderNo: '', userName: '', phone: '' })
const columns = [
  { prop: 'orderNo', label: '订单号', minWidth: 160 },
  { prop: 'user', label: '用户', minWidth: 160 },
  { prop: 'exceptionType', label: '异常类型', minWidth: 120 },
  { prop: 'expectAction', label: '期望处理', minWidth: 120 },
  { prop: 'description', label: '问题描述', minWidth: 200, showOverflowTooltip: true },
  { prop: 'orderStatusDesc', label: '订单状态', minWidth: 120 },
  { prop: 'createTime', label: '提交时间', minWidth: 170 },
]

const handleDialogVisible = ref(false)
const handleLoading = ref(false)
const handleFormRef = ref()
const handleForm = reactive({ id: null, handleType: 'RESOLVED', remark: '' })
const handleRules = {
  handleType: [{ required: true, message: '请选择处理方式', trigger: 'change' }],
}

function buildQuery() {
  return {
    orderNo: filters.orderNo || undefined,
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
  }
}

function canHandle(row) {
  return row.orderStatus === 'EXCEPTION_PENDING'
}

function openOrder(row) {
  if (!row.orderId) return
  router.push(`/business/orders/${row.orderId}`)
}

function openHandle(row) {
  Object.assign(handleForm, { id: row.id, handleType: 'RESOLVED', remark: '' })
  handleDialogVisible.value = true
}

async function submitHandle() {
  const valid = await validateForm(handleFormRef)
  if (!valid) return
  handleLoading.value = true
  try {
    await handleOrderExceptionApi({ ...handleForm })
    showSuccess('异常已处理')
    handleDialogVisible.value = false
    crudRef.value?.loadData(buildQuery())
  } finally {
    handleLoading.value = false
  }
}
</script>

<template>
  <CrudPage
    ref="crudRef"
    title="异常工单"
    :api="orderExceptionApi"
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
    <template #column-orderStatusDesc="{ row }">
      <el-tag :type="tagMeta(orderStatusKeyMap, row.orderStatus === 'EXCEPTION_PENDING' ? 'exception' : 'running').type">
        {{ row.orderStatusDesc || '—' }}
      </el-tag>
    </template>
    <template #actions="{ row }">
      <el-button v-if="row.orderId" link type="primary" @click="openOrder(row)">查看订单</el-button>
      <el-button v-if="canHandle(row)" link type="warning" @click="openHandle(row)">处理</el-button>
    </template>
  </CrudPage>

  <AppDialog v-model="handleDialogVisible" title="处理异常" :loading="handleLoading" width="520px" @confirm="submitHandle">
    <el-form ref="handleFormRef" :model="handleForm" :rules="handleRules" label-width="96px">
      <el-form-item label="处理方式" prop="handleType">
        <el-radio-group v-model="handleForm.handleType">
          <el-radio label="RESOLVED">已处理（恢复进行中）</el-radio>
          <el-radio label="UNRESOLVED">未处理（取消订单）</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="处理说明">
        <el-input v-model="handleForm.remark" type="textarea" :rows="4" placeholder="请输入处理说明" />
      </el-form-item>
    </el-form>
  </AppDialog>
</template>
