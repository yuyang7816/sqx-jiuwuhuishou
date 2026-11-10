<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import {
  confirmSettlementApi,
  getSettlementDetail,
  saveSettlementApi,
  settlementApi,
} from '@/api/modules/settlement'
import { formatUserLabel, settlementStatusMap, tagMeta } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { confirmAction, showSuccess } from '@/utils/modal'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])

const filters = reactive({ orderNo: '', userName: '', phone: '', status: '' })

const detailDrawerVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

const saveDialogVisible = ref(false)
const saveLoading = ref(false)
const saveFormRef = ref()
const saveForm = reactive({ id: null, actualAmount: null, remark: '' })
const saveRules = {
  actualAmount: [{ required: true, message: '请输入实际金额', trigger: 'blur' }],
}

const columns = [
  { prop: 'settlementNo', label: '结算单号', minWidth: 150 },
  { prop: 'orderNo', label: '订单号', minWidth: 150 },
  { prop: 'user', label: '用户', minWidth: 160 },
  { prop: 'estimateAmount', label: '预估金额', width: 100, align: 'right' },
  { prop: 'actualAmount', label: '实际金额', width: 100, align: 'right' },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

const batchConfirmable = computed(() => selectedRows.value.filter(canConfirm))

function formatAmount(value) {
  if (value == null || value === '') return '—'
  return Number(value).toFixed(2)
}

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
    const data = await settlementApi.page({
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
  selectedRows.value = []
  loadData()
}

function handleReset() {
  Object.assign(filters, { orderNo: '', userName: '', phone: '', status: '' })
  pageNum.value = 1
  selectedRows.value = []
  loadData()
}

function handleSelectionChange(rows) {
  selectedRows.value = rows
}

function canSave(row) {
  return row.status === 'PENDING'
}

function canConfirm(row) {
  return row.status === 'PENDING' && row.actualAmount != null
}

function openSave(row) {
  Object.assign(saveForm, {
    id: row.id,
    actualAmount: row.actualAmount ?? row.estimateAmount ?? null,
    remark: '',
  })
  saveDialogVisible.value = true
}

async function submitSave() {
  const valid = await validateForm(saveFormRef)
  if (!valid) return
  saveLoading.value = true
  try {
    await saveSettlementApi({ ...saveForm })
    showSuccess('实际金额已保存')
    saveDialogVisible.value = false
    await loadData()
    if (detailDrawerVisible.value && detail.value?.id === saveForm.id) {
      await loadDetail(saveForm.id)
    }
  } finally {
    saveLoading.value = false
  }
}

async function confirmRow(row) {
  await confirmAction(`确认结算单 ${row.settlementNo} 并推进用户端待打款吗？`)
  await confirmSettlementApi({ id: row.id })
  showSuccess('结算已确认')
  await loadData()
  if (detailDrawerVisible.value && detail.value?.id === row.id) {
    await loadDetail(row.id)
  }
}

async function handleBatchConfirm() {
  const rows = batchConfirmable.value
  if (!rows.length) return
  await confirmAction(`确认批量结算 ${rows.length} 条记录吗？`)
  let success = 0
  for (const row of rows) {
    try {
      await confirmSettlementApi({ id: row.id })
      success += 1
    } catch {
      // 单条失败不阻断其余记录
    }
  }
  showSuccess(`已确认 ${success}/${rows.length} 条结算单`)
  selectedRows.value = []
  await loadData()
}

async function loadDetail(id) {
  detailLoading.value = true
  try {
    detail.value = await getSettlementDetail(id)
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
      <div class="card-header">
        <strong>结算管理</strong>
        <el-button
          v-if="batchConfirmable.length"
          type="success"
          @click="handleBatchConfirm"
        >
          批量确认（{{ batchConfirmable.length }}）
        </el-button>
      </div>
    </template>

    <el-form inline @submit.prevent="handleSearch">
      <el-form-item label="订单号"><el-input v-model="filters.orderNo" clearable placeholder="订单号" /></el-form-item>
      <el-form-item label="用户名"><el-input v-model="filters.userName" clearable placeholder="用户名" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="filters.phone" clearable placeholder="手机号" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.status" clearable placeholder="全部" style="width: 120px">
          <el-option label="待确认" value="PENDING" />
          <el-option label="已确认" value="CONFIRMED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="48" fixed="left" />
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
        <template v-else-if="column.prop === 'estimateAmount' || column.prop === 'actualAmount'" #default="{ row }">
          {{ formatAmount(row[column.prop]) }}
        </template>
        <template v-else-if="column.prop === 'status'" #default="{ row }">
          <el-tag :type="tagMeta(settlementStatusMap, row.status).type">
            {{ tagMeta(settlementStatusMap, row.status).label }}
          </el-tag>
        </template>
        <template v-else #default="{ row }">
          {{ row[column.prop] ?? '—' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="220" fixed="right">
        <template #default="{ row }">
          <el-space wrap>
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.orderId" link type="primary" @click="openOrder(row)">查看订单</el-button>
            <el-button v-if="canSave(row)" link type="primary" @click="openSave(row)">录入金额</el-button>
            <el-button v-if="canConfirm(row)" link type="success" @click="confirmRow(row)">确认结算</el-button>
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

    <AppDialog v-model="saveDialogVisible" title="录入实际金额" :loading="saveLoading" width="520px" @confirm="submitSave">
      <el-form ref="saveFormRef" :model="saveForm" :rules="saveRules" label-width="96px">
        <el-form-item label="实际金额" prop="actualAmount">
          <el-input-number v-model="saveForm.actualAmount" :min="0" :precision="2" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="saveForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <el-drawer v-model="detailDrawerVisible" title="结算单详情" size="520px" destroy-on-close>
      <div v-loading="detailLoading">
        <template v-if="detail">
          <div class="drawer-actions">
            <el-button v-if="detail.orderId" type="primary" plain @click="openOrder(detail)">查看订单</el-button>
            <el-button v-if="canSave(detail)" type="primary" @click="openSave(detail)">录入金额</el-button>
            <el-button v-if="canConfirm(detail)" type="success" @click="confirmRow(detail)">确认结算</el-button>
          </div>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="结算单号">{{ detail.settlementNo }}</el-descriptions-item>
            <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ formatUserLabel(detail) }}</el-descriptions-item>
            <el-descriptions-item label="预估金额">{{ formatAmount(detail.estimateAmount) }}</el-descriptions-item>
            <el-descriptions-item label="实际金额">{{ formatAmount(detail.actualAmount) }}</el-descriptions-item>
            <el-descriptions-item label="结算状态">
              <el-tag :type="tagMeta(settlementStatusMap, detail.status).type">
                {{ tagMeta(settlementStatusMap, detail.status).label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item v-if="detail.orderStatusDesc" label="订单状态">{{ detail.orderStatusDesc }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ detail.remark || '—' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detail.createTime || '—' }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ detail.updateTime || '—' }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-drawer>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.drawer-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}
</style>
