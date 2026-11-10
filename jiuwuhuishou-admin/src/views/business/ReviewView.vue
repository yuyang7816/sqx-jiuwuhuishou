<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import {
  getReviewDetail,
  replyReviewApi,
  reviewApi,
} from '@/api/modules/review'
import { formatUserLabel } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { showSuccess } from '@/utils/modal'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const filters = reactive({ orderNo: '', userName: '', phone: '', replyStatus: '' })

const detailDrawerVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

const replyDialogVisible = ref(false)
const replyLoading = ref(false)
const replyFormRef = ref()
const replyForm = reactive({ id: null, adminReply: '' })
const replyRules = {
  adminReply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }],
}

const columns = [
  { prop: 'orderNo', label: '订单号', minWidth: 150 },
  { prop: 'user', label: '用户', minWidth: 160 },
  { prop: 'score', label: '评分', width: 80 },
  { prop: 'complaintType', label: '类型', width: 100 },
  { prop: 'content', label: '内容', minWidth: 220 },
  { prop: 'adminReply', label: '客服回复', minWidth: 180 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

function buildQuery() {
  return {
    orderNo: filters.orderNo || undefined,
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
    replyStatus: filters.replyStatus || undefined,
  }
}

async function loadData(extra = {}) {
  loading.value = true
  try {
    const data = await reviewApi.page({
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
  Object.assign(filters, { orderNo: '', userName: '', phone: '', replyStatus: '' })
  pageNum.value = 1
  loadData()
}

function canReply(row) {
  return !row.adminReply
}

function openReply(row) {
  Object.assign(replyForm, { id: row.id, adminReply: row.adminReply || '' })
  replyDialogVisible.value = true
}

async function submitReply() {
  const valid = await validateForm(replyFormRef)
  if (!valid) return
  replyLoading.value = true
  try {
    await replyReviewApi({ ...replyForm })
    showSuccess('回复已提交')
    replyDialogVisible.value = false
    await loadData()
    if (detailDrawerVisible.value && detail.value?.id === replyForm.id) {
      await loadDetail(replyForm.id)
    }
  } finally {
    replyLoading.value = false
  }
}

async function loadDetail(id) {
  detailLoading.value = true
  try {
    detail.value = await getReviewDetail(id)
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
      <strong>评价管理</strong>
    </template>

    <el-form inline @submit.prevent="handleSearch">
      <el-form-item label="订单号"><el-input v-model="filters.orderNo" clearable placeholder="订单号" /></el-form-item>
      <el-form-item label="用户名"><el-input v-model="filters.userName" clearable placeholder="用户名" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="filters.phone" clearable placeholder="手机号" /></el-form-item>
      <el-form-item label="回复状态">
        <el-select v-model="filters.replyStatus" clearable placeholder="全部" style="width: 120px">
          <el-option label="待回复" value="pending" />
          <el-option label="已回复" value="replied" />
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
        <template v-else-if="column.prop === 'score'" #default="{ row }">
          <el-rate :model-value="Number(row.score || 0)" disabled />
        </template>
        <template v-else-if="column.prop === 'adminReply'" #default="{ row }">
          <el-tag v-if="!row.adminReply" type="warning">待回复</el-tag>
          <span v-else>{{ row.adminReply }}</span>
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
            <el-button v-if="canReply(row)" link type="primary" @click="openReply(row)">回复</el-button>
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

    <AppDialog v-model="replyDialogVisible" title="回复评价" :loading="replyLoading" width="560px" @confirm="submitReply">
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="96px">
        <el-form-item label="回复内容" prop="adminReply">
          <el-input v-model="replyForm.adminReply" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
    </AppDialog>

    <el-drawer v-model="detailDrawerVisible" title="评价详情" size="520px" destroy-on-close>
      <div v-loading="detailLoading">
        <template v-if="detail">
          <div class="drawer-actions">
            <el-button v-if="detail.orderId" type="primary" plain @click="openOrder(detail)">查看订单</el-button>
            <el-button v-if="canReply(detail)" type="primary" @click="openReply(detail)">回复</el-button>
          </div>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ formatUserLabel(detail) }}</el-descriptions-item>
            <el-descriptions-item label="评分">
              <el-rate :model-value="Number(detail.score || 0)" disabled />
            </el-descriptions-item>
            <el-descriptions-item label="类型">{{ detail.complaintType || '普通评价' }}</el-descriptions-item>
            <el-descriptions-item label="内容">{{ detail.content || '—' }}</el-descriptions-item>
            <el-descriptions-item label="客服回复">{{ detail.adminReply || '待回复' }}</el-descriptions-item>
            <el-descriptions-item v-if="detail.orderStatusDesc" label="订单状态">{{ detail.orderStatusDesc }}</el-descriptions-item>
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
