<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import StaffSelect from '@/components/common/StaffSelect.vue'
import {
  assignPickupTaskApi,
  finishPickupTaskApi,
  getPickupTaskDetail,
  pickupTaskApi,
  reassignPickupTaskApi,
} from '@/api/modules/pickup-task'
import { validateForm } from '@/utils/form'
import { confirmAction, showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'
import { pickupStatusKeyMap, tagMeta } from '@/utils/display'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const filters = reactive({ orderNo: '', staffName: '', statusKey: '' })

const detailDrawerVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

const assignDialogVisible = ref(false)
const assignLoading = ref(false)
const assignFormRef = ref()
const assignForm = reactive({ id: null, staffId: null, isFirstAssign: false })
const assignRules = {
  staffId: [{ required: true, message: '请选择取件员', trigger: 'change' }],
}

const customColumnSlots = new Set(['statusKey', 'addressText', 'itemSummary'])

const columns = [
  { prop: 'taskNo', label: '任务号', minWidth: 140 },
  { prop: 'orderNo', label: '订单号', minWidth: 150 },
  { prop: 'staffName', label: '取件员', minWidth: 120 },
  { prop: 'statusKey', label: '状态', width: 100 },
  { prop: 'categoryName', label: '品类', minWidth: 120 },
  { prop: 'itemSummary', label: '物品摘要', minWidth: 140, showOverflowTooltip: true },
  { prop: 'contactName', label: '联系人', minWidth: 100 },
  { prop: 'contactMobile', label: '联系电话', minWidth: 130 },
  { prop: 'addressText', label: '取件地址', minWidth: 200, showOverflowTooltip: true },
  { prop: 'appointmentTime', label: '预约时间', minWidth: 170 },
  { prop: 'finishedAt', label: '完成时间', minWidth: 170 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

const assignDialogTitle = computed(() => (assignForm.isFirstAssign ? '分配取件员' : '更改取件员'))

function formatText(value) {
  if (value == null || value === '') return '—'
  return value
}

function buildQuery() {
  return {
    orderNo: filters.orderNo || undefined,
    staffName: filters.staffName || undefined,
    statusKey: filters.statusKey || undefined,
  }
}

async function loadData(extra = {}) {
  loading.value = true
  try {
    const data = await pickupTaskApi.page({
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
  Object.assign(filters, { orderNo: '', staffName: '', statusKey: '' })
  pageNum.value = 1
  loadData()
}

function canOperate(row) {
  return row.statusKey === 'pending' || row.statusKey === 'running'
      || row.status === 'PENDING' || row.status === 'RUNNING'
}

function needsAssign(row) {
  return !row.staffId && !row.staffName
}

function openAssign(row) {
  assignForm.id = row.id
  assignForm.staffId = row.staffId || null
  assignForm.isFirstAssign = needsAssign(row)
  assignDialogVisible.value = true
}

async function submitAssign() {
  const valid = await validateForm(assignFormRef)
  if (!valid) return
  assignLoading.value = true
  try {
    const payload = { id: assignForm.id, staffId: assignForm.staffId }
    if (assignForm.isFirstAssign) {
      await assignPickupTaskApi(payload)
      showSuccess('取件员已分配')
    } else {
      await reassignPickupTaskApi(payload)
      showSuccess('取件员已更改')
    }
    assignDialogVisible.value = false
    await loadData()
    if (detailDrawerVisible.value && detail.value?.id === assignForm.id) {
      await loadDetail(assignForm.id)
    }
  } finally {
    assignLoading.value = false
  }
}

async function handleQuickFinish(row) {
  await confirmAction(`确认完成取件任务 ${row.taskNo} 吗？`)
  await finishPickupTaskApi({ id: row.id })
  showSuccess('取件任务已完成')
  await loadData()
  if (detailDrawerVisible.value && detail.value?.id === row.id) {
    await loadDetail(row.id)
  }
}

async function loadDetail(id) {
  detailLoading.value = true
  try {
    detail.value = await getPickupTaskDetail(id)
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
        <strong>取件任务</strong>
      </div>
    </template>

    <el-form inline @submit.prevent="handleSearch">
      <el-form-item label="订单号"><el-input v-model="filters.orderNo" clearable placeholder="订单号" /></el-form-item>
      <el-form-item label="取件员"><el-input v-model="filters.staffName" clearable placeholder="取件员姓名" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.statusKey" clearable placeholder="全部" style="width: 120px">
          <el-option label="待取件" value="pending" />
          <el-option label="取件中" value="running" />
          <el-option label="已完成" value="done" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
      <el-table-column label="编号" width="70" align="center" fixed="left">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column
        v-for="column in columns"
        :key="column.prop || column.label"
        v-bind="column"
      >
        <template v-if="customColumnSlots.has(column.prop)" #default="{ row }">
          <template v-if="column.prop === 'statusKey'">
            <el-tag :type="tagMeta(pickupStatusKeyMap, row.statusKey).type">{{ tagMeta(pickupStatusKeyMap, row.statusKey).label }}</el-tag>
          </template>
          <template v-else>
            {{ formatText(row[column.prop]) }}
          </template>
        </template>
        <template v-else #default="{ row }">
          {{ formatText(row[column.prop]) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="240" fixed="right">
        <template #default="{ row }">
          <el-space wrap>
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.orderId" link type="primary" @click="openOrder(row)">查看订单</el-button>
            <el-button v-if="canOperate(row) && needsAssign(row) && isAuth(PERMS.pickupAssign)" link type="primary" @click="openAssign(row)">分配取件员</el-button>
            <el-button v-if="canOperate(row) && !needsAssign(row) && isAuth(PERMS.pickupAssign)" link type="primary" @click="openAssign(row)">更改取件员</el-button>
            <el-button v-if="canOperate(row) && isAuth(PERMS.pickupComplete)" link type="success" @click="handleQuickFinish(row)">完成</el-button>
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

    <AppDialog v-model="assignDialogVisible" :title="assignDialogTitle" :loading="assignLoading" width="520px" @confirm="submitAssign">
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="96px">
        <el-form-item label="取件员" prop="staffId">
          <StaffSelect v-model="assignForm.staffId" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <el-drawer v-model="detailDrawerVisible" title="取件任务详情" size="520px" destroy-on-close>
      <div v-loading="detailLoading">
        <template v-if="detail">
          <div class="drawer-actions">
            <el-button v-if="detail.orderId" type="primary" plain @click="openOrder(detail)">查看订单</el-button>
            <el-button v-if="canOperate(detail) && needsAssign(detail) && isAuth(PERMS.pickupAssign)" type="primary" @click="openAssign(detail)">分配取件员</el-button>
            <el-button v-if="canOperate(detail) && !needsAssign(detail) && isAuth(PERMS.pickupAssign)" @click="openAssign(detail)">更改取件员</el-button>
            <el-button v-if="canOperate(detail) && isAuth(PERMS.pickupComplete)" type="success" @click="handleQuickFinish(detail)">完成取件</el-button>
          </div>

          <el-descriptions :column="1" border class="detail-block">
            <el-descriptions-item label="任务号">{{ detail.taskNo }}</el-descriptions-item>
            <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="取件员">{{ detail.staffName || '未分配' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="tagMeta(pickupStatusKeyMap, detail.statusKey).type">
                {{ detail.statusText || tagMeta(pickupStatusKeyMap, detail.statusKey).label }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="品类">{{ detail.categoryName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="物品摘要">{{ detail.itemSummary || '—' }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ detail.contactName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detail.contactMobile || '—' }}</el-descriptions-item>
            <el-descriptions-item label="取件地址">{{ detail.addressText || '—' }}</el-descriptions-item>
            <el-descriptions-item label="预约时间">{{ detail.appointmentTime || '—' }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ detail.finishedAt || '—' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detail.createTime || '—' }}</el-descriptions-item>
          </el-descriptions>

          <div class="detail-block">
            <strong class="section-title">取件流程</strong>
            <el-timeline v-if="detail.logs?.length">
              <el-timeline-item
                v-for="(item, index) in detail.logs"
                :key="index"
                :timestamp="item.createTime"
                :type="item.active ? 'primary' : undefined"
              >
                <div class="log-title">{{ item.title || item.actionType }}</div>
                <div v-if="item.content" class="log-content">{{ item.content }}</div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无流程记录" />
          </div>
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
}
.drawer-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}
.detail-block {
  margin-bottom: 20px;
}
.section-title {
  display: block;
  margin-bottom: 12px;
}
.log-title {
  font-weight: 500;
}
.log-content {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
