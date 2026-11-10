<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import {
  disputeApi,
  getDisputeDetail,
  resolveDisputeApi,
} from '@/api/modules/dispute'
import { disputeStatusMap, formatUserLabel, tagMeta } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { showSuccess } from '@/utils/modal'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const filters = reactive({ orderNo: '', userName: '', phone: '', status: '' })

const detailDrawerVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

const resolveDialogVisible = ref(false)
const resolveLoading = ref(false)
const resolveFormRef = ref()
const resolveForm = reactive({ id: null, action: 'PROCEED', actualAmount: null, remark: '' })
const resolveRules = {
  action: [{ required: true, message: '请选择处理方式', trigger: 'change' }],
}

const columns = [
  { prop: 'orderNo', label: '订单号', minWidth: 150 },
  { prop: 'user', label: '用户', minWidth: 160 },
  { prop: 'reason', label: '争议原因', minWidth: 140 },
  { prop: 'description', label: '描述', minWidth: 200 },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

function buildQuery() {
  return {
    orderNo: filters.orderNo || undefined,
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
    status: filters.status || undefined,
  }
}

async function loadData(extra = {}) {
  loading.value = true
  try {
    const data = await disputeApi.page({
      page: pageNum.value,
      limit: pageSize.value,
      ...buildQuery(),
      ...extra,
    })
    tableData.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleReset() {
  Object.assign(filters, { orderNo: '', userName: '', phone: '', status: '' })
  pageNum.value = 1
  loadData()
}

function canResolve(row) {
  return row.status === 'PENDING'
}

function openResolve(row) {
  Object.assign(resolveForm, {
    id: row.id,
    action: 'PROCEED',
    actualAmount: null,
    remark: '',
  })
  resolveDialogVisible.value = true
}

async function submitResolve() {
  const valid = await validateForm(resolveFormRef)
  if (!valid) return
  if (resolveForm.action === 'ADJUST' && resolveForm.actualAmount == null) {
    return
  }
  resolveLoading.value = true
  try {
    await resolveDisputeApi({ ...resolveForm })
    showSuccess('争议已处理')
    resolveDialogVisible.value = false
    await loadData()
    if (detailDrawerVisible.value && detail.value?.id === resolveForm.id) {
      await loadDetail(resolveForm.id)
    }
  } finally {
    resolveLoading.value = false
  }
}

async function loadDetail(id) {
  detailLoading.value = true
  try {
    detail.value = await getDisputeDetail(id)
  } finally {
    detailLoading.value = false
  }
}

async function openDetail(row) {
  detailDrawerVisible.value = true
  detail.value = null
  await loadDetail(row.id)
}

function openOrder(row) {
  const orderId = row.orderId ?? detail.value?.orderId
  if (!orderId) return
  router.push(`/business/orders/${orderId}`)
}

loadData()
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <strong>争议工单</strong>
    </template>

    <el-form inline @submit.prevent="handleSearch">
      <el-form-item label="订单号"><el-input v-model="filters.orderNo" clearable placeholder="订单号" /></el-form-item>
      <el-form-item label="用户名"><el-input v-model="filters.userName" clearable placeholder="用户名" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="filters.phone" clearable placeholder="手机号" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.status" clearable placeholder="全部" style="width: 120px">
          <el-option label="待处理" value="PENDING" />
          <el-option label="已裁决" value="RESOLVED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column
        v-for="column in columns"
        :key="column.prop || column.label"
        v-bind="column"
      >
        <template v-if="column.prop === 'user'" #default="{ row }">
          {{ formatUserLabel(row) }}
        </template>
        <template v-else-if="column.prop === 'status'" #default="{ row }">
          <el-tag :type="tagMeta(disputeStatusMap, row.status).type">
            {{ tagMeta(disputeStatusMap, row.status).label }}
          </el-tag>
        </template>
        <template v-else #default="{ row }">
          {{ row[column.prop] ?? '—' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="200" fixed="right">
        <template #default="{ row }">
          <el-space wrap>
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.orderId" link type="primary" @click="openOrder(row)">查看订单</el-button>
            <el-button v-if="canResolve(row)" link type="warning" @click="openResolve(row)">裁决</el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>

    <BasePagination
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @update:current-page="pageNum = $event; loadData()"
      @update:page-size="pageSize = $event; pageNum = 1; loadData()"
    />

    <AppDialog v-model="resolveDialogVisible" title="争议裁决" :loading="resolveLoading" width="560px" @confirm="submitResolve">
      <el-form ref="resolveFormRef" :model="resolveForm" :rules="resolveRules" label-width="108px">
        <el-form-item label="处理方式" prop="action">
          <el-select v-model="resolveForm.action" style="width: 100%">
            <el-option label="维持原价回待确认" value="PROCEED" />
            <el-option label="调整金额回待确认" value="ADJUST" />
            <el-option label="关闭订单" value="CANCEL" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="resolveForm.action === 'ADJUST'" label="调整后金额" required>
          <el-input-number v-model="resolveForm.actualAmount" :min="0" :precision="2" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="resolveForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <el-drawer v-model="detailDrawerVisible" title="争议详情" size="520px" destroy-on-close>
      <div v-loading="detailLoading">
        <template v-if="detail">
          <div class="drawer-actions">
            <el-button v-if="detail.orderId" type="primary" plain @click="openOrder(detail)">查看订单</el-button>
            <el-button v-if="canResolve(detail)" type="warning" @click="openResolve(detail)">裁决</el-button>
          </div>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ formatUserLabel(detail) }}</el-descriptions-item>
            <el-descriptions-item label="争议原因">{{ detail.reason || '—' }}</el-descriptions-item>
            <el-descriptions-item label="描述">{{ detail.description || '—' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="tagMeta(disputeStatusMap, detail.status).type">
                {{ tagMeta(disputeStatusMap, detail.status).label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item v-if="detail.orderStatusDesc" label="订单状态">{{ detail.orderStatusDesc }}</el-descriptions-item>
            <el-descriptions-item label="裁决备注">{{ detail.resolveRemark || '—' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detail.createTime || '—' }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ detail.updateTime || '—' }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-drawer>
  </el-card>
</template>

<style scoped>
.drawer-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}
</style>
