<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import StaffSelect from '@/components/common/StaffSelect.vue'
import { orderApi } from '@/api/modules/order'
import {
  assignPickupApi,
  cancelOrderApi,
  completeOrderApi,
  confirmMailReceiveApi,
  getOrderExpressTracks,
  handleOrderApi,
  updateOrderEstimateApi,
  updateOrderExpressApi,
} from '@/api/modules/order'
import { validateForm } from '@/utils/form'
import { confirmAction, showError, showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'
import { orderStatusKeyMap, orderTypeMap, fulfillTypeMap, tagMeta } from '@/utils/display'
import { resolveExpressTracks } from '@/utils/express'

function formatAmount(value) {
  if (value == null || value === '') return '—'
  return Number(value).toFixed(2)
}

function formatEstimateAmount(row = {}) {
  const orderType = `${row.orderType || ''}`.toLowerCase()
  const min = row.estimateMinAmount
  const max = row.estimateMaxAmount
  if (orderType === 'appliance' && (min != null || max != null)) {
    const low = min ?? row.estimateAmount
    const high = max ?? row.estimateAmount
    if (low != null && high != null) {
      if (Number(low) === Number(high)) {
        return formatAmount(low)
      }
      return `${formatAmount(low)} ~ ${formatAmount(high)}`
    }
  }
  return formatAmount(row.estimateAmount)
}

function formatBool(value) {
  if (value == null) return '—'
  return value ? '是' : '否'
}

function formatText(value) {
  if (value == null || value === '') return '—'
  return value
}

const customColumnSlots = new Set([
  'orderType',
  'fulfillType',
  'statusKey',
  'estimateAmount',
  'actualAmount',
  'estimateAmountConfirmed',
  'addressText',
  'expressNo',
  'remark',
  'userFeedbackSummary',
])

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const filters = reactive({
  orderNo: '',
  userName: '',
  phone: '',
  orderType: '',
  fulfillType: '',
  statusKey: '',
})

const assignDialogVisible = ref(false)
const assignLoading = ref(false)
const assignFormRef = ref()
const assignForm = reactive({ orderId: null, staffId: null })
const assignRules = {
  staffId: [{ required: true, message: '请选择员工', trigger: 'change' }],
}

const actualDialogVisible = ref(false)
const actualLoading = ref(false)
const actualFormRef = ref()
const actualForm = reactive({ id: null, actualAmount: null, remark: '' })
const actualRules = {
  actualAmount: [{ required: true, message: '请输入实际金额', trigger: 'blur' }],
}

const expressEditDialogVisible = ref(false)
const expressEditLoading = ref(false)
const expressEditFormRef = ref()
const expressEditForm = reactive({ id: null, expressNo: '' })
const expressEditRules = {
  expressNo: [{ required: true, message: '请输入快递单号', trigger: 'blur' }],
}

const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelFormRef = ref()
const cancelForm = reactive({ id: null, reason: '' })
const cancelRules = {
  reason: [{ required: true, message: '请输入取消原因', trigger: 'blur' }],
}

const handleDialogVisible = ref(false)
const handleLoading = ref(false)
const handleFormRef = ref()
const handleForm = reactive({ id: null, handleType: 'RESOLVED', remark: '' })
const handleTarget = ref(null)
const handleRules = {
  handleType: [{ required: true, message: '请选择处理类型', trigger: 'change' }],
  remark: [{ required: true, message: '请输入处理说明', trigger: 'blur' }],
}

const columns = [
  { prop: 'orderNo', label: '订单号', minWidth: 160 },
  { prop: 'userName', label: '用户名', minWidth: 120 },
  { prop: 'phone', label: '用户手机', minWidth: 130 },
  { prop: 'orderType', label: '类型', width: 90 },
  { prop: 'fulfillType', label: '履约方式', width: 100 },
  { prop: 'categoryName', label: '品类', minWidth: 120 },
  { prop: 'statusKey', label: '状态', minWidth: 110 },
  { prop: 'statusDesc', label: '状态描述', minWidth: 140 },
  { prop: 'userFeedbackSummary', label: '用户反馈', minWidth: 220, showOverflowTooltip: true },
  { prop: 'estimateAmount', label: '预估金额', width: 160, align: 'right' },
  { prop: 'actualAmount', label: '实际金额', width: 100, align: 'right' },
  { prop: 'estimateAmountConfirmed', label: '金额已确认', width: 110, align: 'center' },
  { prop: 'contactName', label: '联系人', minWidth: 100 },
  { prop: 'contactMobile', label: '联系电话', minWidth: 130 },
  { prop: 'addressText', label: '回收/上门地址', minWidth: 200, showOverflowTooltip: true },
  { prop: 'expressNo', label: '快递单号', minWidth: 220 },
  { prop: 'appointmentTime', label: '预约时间', minWidth: 170 },
  { prop: 'remark', label: '备注', minWidth: 160, showOverflowTooltip: true },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

function buildQuery() {
  return {
    orderNo: filters.orderNo || undefined,
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
    orderType: filters.orderType || undefined,
    fulfillType: filters.fulfillType || undefined,
    statusKey: filters.statusKey || undefined,
  }
}

async function loadData(extra = {}) {
  loading.value = true
  try {
    const data = await orderApi.page({
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
  Object.assign(filters, { orderNo: '', userName: '', phone: '', orderType: '', fulfillType: '', statusKey: '' })
  pageNum.value = 1
  loadData()
}

function openDetail(row) {
  router.push(`/business/orders/${row.id}`)
}

function canAssign(row) {
  if ((row.fulfillType || 'visit') === 'mail') return false
  return row.status === 'WAIT_ASSIGN' || row.status === 'IN_PROGRESS'
}

function canEditActual(row) {
  return row.status === 'WAIT_SETTLEMENT_CONFIRM'
}

function canConfirmPayout(row) {
  return row.status === 'PAYOUT_PROCESSING'
}

function canHandle(row) {
  return row.status === 'EXCEPTION_PENDING' || row.status === 'DISPUTE_PENDING'
}

function canCancel(row) {
  return row.status !== 'CANCELLED' && row.status !== 'COMPLETED'
}

function isMailOrder(row) {
  return (row?.fulfillType || 'visit') === 'mail'
}

function canEditExpressNo(row) {
  return isMailOrder(row) && Boolean(row?.expressNo)
}

function canConfirmMailReceive(row) {
  return isMailOrder(row)
    && row.status === 'IN_PROGRESS'
    && `${row.statusDesc || ''}`.trim() === '待收货'
    && Boolean(row?.expressNo)
}

const expressDialogVisible = ref(false)
const expressDetail = ref(null)
const expressTracks = ref([])
const expressLoading = ref(false)

async function openExpressDetail(row) {
  expressDetail.value = row
  expressTracks.value = Array.isArray(row?.expressTracks) && row.expressTracks.length
    ? row.expressTracks
    : resolveExpressTracks(row)
  expressDialogVisible.value = true
  if (!row?.id) return
  expressLoading.value = true
  try {
    const tracks = await getOrderExpressTracks(row.id)
    if (Array.isArray(tracks) && tracks.length) {
      expressTracks.value = tracks
      expressDetail.value = {
        ...row,
        expressTracks: tracks,
      }
    } else {
      showError('暂未查询到物流轨迹，请确认顺丰管家凭证已配置、单号有效，且收寄件手机后四位匹配')
    }
  } catch (error) {
    // interceptor 已提示错误信息；保留列表兜底轨迹
  } finally {
    expressLoading.value = false
  }
}

function openAssign(row) {
  assignForm.orderId = row.id
  assignForm.staffId = null
  assignDialogVisible.value = true
}

async function submitAssign() {
  const valid = await validateForm(assignFormRef)
  if (!valid) return
  assignLoading.value = true
  try {
    await assignPickupApi({ ...assignForm })
    showSuccess('员工分配成功')
    assignDialogVisible.value = false
    await loadData()
  } finally {
    assignLoading.value = false
  }
}

function openActual(row) {
  actualForm.id = row.id
  actualForm.actualAmount = row.actualAmount ?? null
  actualForm.remark = ''
  actualDialogVisible.value = true
}

async function submitActual() {
  const valid = await validateForm(actualFormRef)
  if (!valid) return
  actualLoading.value = true
  try {
    await updateOrderEstimateApi({ ...actualForm })
    showSuccess('实际金额已更新')
    actualDialogVisible.value = false
    await loadData()
  } finally {
    actualLoading.value = false
  }
}

function openExpressEdit(row) {
  expressEditForm.id = row.id
  expressEditForm.expressNo = row.expressNo || ''
  expressEditDialogVisible.value = true
}

async function submitExpressEdit() {
  const valid = await validateForm(expressEditFormRef)
  if (!valid) return
  expressEditLoading.value = true
  try {
    await updateOrderExpressApi({
      id: expressEditForm.id,
      expressNo: String(expressEditForm.expressNo || '').trim(),
    })
    showSuccess('快递单号已更新')
    expressEditDialogVisible.value = false
    await loadData()
  } finally {
    expressEditLoading.value = false
  }
}

async function handleConfirmPayout(row) {
  await confirmAction(`确认对订单 ${row.orderNo} 完成打款吗？完成后订单将变为已完成。`)
  await completeOrderApi({ id: row.id })
  showSuccess('打款已完成，订单已闭环')
  await loadData()
}

async function handleConfirmMailReceive(row) {
  await confirmAction(`确认已收到订单 ${row.orderNo} 的快递包裹吗？确认后订单将变为待确认。`)
  await confirmMailReceiveApi({ id: row.id })
  showSuccess('已确认收货，订单进入待确认')
  await loadData()
}

function openHandle(row) {
  handleForm.id = row.id
  handleForm.handleType = 'RESOLVED'
  handleForm.remark = ''
  handleTarget.value = row
  handleDialogVisible.value = true
}

async function submitHandle() {
  const valid = await validateForm(handleFormRef)
  if (!valid) return
  handleLoading.value = true
  try {
    await handleOrderApi({ ...handleForm })
    showSuccess('处理已提交')
    handleDialogVisible.value = false
    handleTarget.value = null
    await loadData()
  } finally {
    handleLoading.value = false
  }
}

function openCancel(row) {
  cancelForm.id = row.id
  cancelForm.reason = ''
  cancelDialogVisible.value = true
}

async function submitCancel() {
  const valid = await validateForm(cancelFormRef)
  if (!valid) return
  cancelLoading.value = true
  try {
    await cancelOrderApi({ ...cancelForm })
    showSuccess('订单已取消')
    cancelDialogVisible.value = false
    await loadData()
  } finally {
    cancelLoading.value = false
  }
}

loadData()
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <strong>回收订单</strong>
      </div>
    </template>

    <el-form inline @submit.prevent="handleSearch">
      <el-form-item label="订单号"><el-input v-model="filters.orderNo" clearable placeholder="订单号" /></el-form-item>
      <el-form-item label="用户名"><el-input v-model="filters.userName" clearable placeholder="用户名" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="filters.phone" clearable placeholder="手机号" /></el-form-item>
      <el-form-item label="类型">
        <el-select v-model="filters.orderType" clearable placeholder="全部" style="width: 120px">
          <el-option label="旧衣" value="clothes" />
          <el-option label="家电" value="appliance" />
          <el-option label="数码" value="digital" />
          <el-option label="奢侈品" value="luxury" />
          <el-option label="书籍" value="book" />
        </el-select>
      </el-form-item>
      <el-form-item label="履约方式">
        <el-select v-model="filters.fulfillType" clearable placeholder="全部" style="width: 120px">
          <el-option label="上门订单" value="visit" />
          <el-option label="邮寄订单" value="mail" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.statusKey" clearable placeholder="全部" style="width: 130px">
          <el-option label="待分配" value="waitAssign" />
          <el-option label="进行中" value="running" />
          <el-option label="待确认" value="pendingConfirm" />
          <el-option label="打款中" value="paying" />
          <el-option label="已完成" value="done" />
          <el-option label="已取消" value="cancelled" />
          <el-option label="异常" value="exception" />
          <el-option label="争议" value="dispute" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
      <el-table-column label="编号" prop="id" width="80" align="center" fixed="left" />
      <el-table-column
        v-for="column in columns"
        :key="column.prop || column.label"
        v-bind="column"
      >
        <template v-if="customColumnSlots.has(column.prop)" #default="{ row }">
          <template v-if="column.prop === 'orderType'">
            <el-tag :type="tagMeta(orderTypeMap, row.orderType).type">{{ tagMeta(orderTypeMap, row.orderType).label }}</el-tag>
          </template>
          <template v-else-if="column.prop === 'fulfillType'">
            <el-tag :type="tagMeta(fulfillTypeMap, row.fulfillType || 'visit').type">{{ row.fulfillTypeLabel || tagMeta(fulfillTypeMap, row.fulfillType || 'visit').label }}</el-tag>
          </template>
          <template v-else-if="column.prop === 'statusKey'">
            <el-tag :type="tagMeta(orderStatusKeyMap, row.statusKey).type">{{ tagMeta(orderStatusKeyMap, row.statusKey).label }}</el-tag>
          </template>
          <template v-else-if="column.prop === 'estimateAmount'">
            {{ formatEstimateAmount(row) }}
          </template>
          <template v-else-if="column.prop === 'actualAmount'">
            {{ formatAmount(row.actualAmount) }}
          </template>
          <template v-else-if="column.prop === 'estimateAmountConfirmed'">
            <el-tag :type="row.estimateAmountConfirmed ? 'success' : 'info'">{{ formatBool(row.estimateAmountConfirmed) }}</el-tag>
          </template>
          <template v-else-if="column.prop === 'addressText'">
            {{ formatText(row.addressText) }}
          </template>
          <template v-else-if="column.prop === 'expressNo'">
            <template v-if="isMailOrder(row)">
              <div class="express-no-cell">
                <span class="express-no-text">{{ formatText(row.expressNo) }}</span>
                <el-button
                  v-if="canEditExpressNo(row)"
                  link
                  type="primary"
                  @click="openExpressEdit(row)"
                >
                  修改
                </el-button>
              </div>
            </template>
            <template v-else>—</template>
          </template>
          <template v-else-if="column.prop === 'remark'">
            {{ formatText(row.remark) }}
          </template>
          <template v-else-if="column.prop === 'userFeedbackSummary'">
            {{ formatText(row.userFeedbackSummary) }}
          </template>
        </template>
        <template v-else #default="{ row }">
          {{ formatText(row[column.prop]) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="360" fixed="right">
        <template #default="{ row }">
          <el-space wrap>
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button v-if="canEditExpressNo(row)" link type="primary" @click="openExpressDetail(row)">快递详情</el-button>
            <el-button v-if="canAssign(row) && isAuth(PERMS.orderAssign)" link type="primary" @click="openAssign(row)">分配员工</el-button>
            <el-button v-if="canConfirmMailReceive(row)" link type="primary" @click="handleConfirmMailReceive(row)">确认收货</el-button>
            <el-button v-if="canEditActual(row)" link type="primary" @click="openActual(row)">修改实际金额</el-button>
            <el-button v-if="canConfirmPayout(row)" link type="success" @click="handleConfirmPayout(row)">确认打款</el-button>
            <el-button v-if="canHandle(row)" link type="warning" @click="openHandle(row)">处理</el-button>
            <el-button v-if="canCancel(row) && isAuth(PERMS.orderCancel)" link type="danger" @click="openCancel(row)">取消订单</el-button>
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

    <AppDialog v-model="assignDialogVisible" title="分配员工" :loading="assignLoading" width="520px" @confirm="submitAssign">
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="90px">
        <el-form-item label="取件员工" prop="staffId">
          <StaffSelect v-model="assignForm.staffId" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <AppDialog v-model="actualDialogVisible" title="修改实际金额" :loading="actualLoading" width="520px" @confirm="submitActual">
      <el-form ref="actualFormRef" :model="actualForm" :rules="actualRules" label-width="96px">
        <el-form-item label="实际金额" prop="actualAmount">
          <el-input-number v-model="actualForm.actualAmount" :min="0" :precision="2" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="修改说明" prop="remark">
          <el-input v-model="actualForm.remark" type="textarea" :rows="3" placeholder="请输入修改说明" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <AppDialog v-model="expressEditDialogVisible" title="修改快递单号" :loading="expressEditLoading" width="520px" @confirm="submitExpressEdit">
      <el-form ref="expressEditFormRef" :model="expressEditForm" :rules="expressEditRules" label-width="96px">
        <el-form-item label="快递单号" prop="expressNo">
          <el-input v-model="expressEditForm.expressNo" maxlength="64" clearable placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <AppDialog v-model="handleDialogVisible" title="处理订单" :loading="handleLoading" width="560px" @confirm="submitHandle">
      <div v-if="handleTarget" class="handle-feedback">
        <template v-if="handleTarget.status === 'DISPUTE_PENDING' && handleTarget.latestDispute">
          <div class="handle-feedback__title">用户争议内容</div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="争议原因">{{ formatText(handleTarget.latestDispute.reason) }}</el-descriptions-item>
            <el-descriptions-item label="争议描述">{{ formatText(handleTarget.latestDispute.description) }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatText(handleTarget.latestDispute.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <template v-else-if="handleTarget.status === 'EXCEPTION_PENDING' && handleTarget.latestException">
          <div class="handle-feedback__title">用户异常反馈</div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="异常类型">{{ formatText(handleTarget.latestException.exceptionType) }}</el-descriptions-item>
            <el-descriptions-item label="期望处理">{{ formatText(handleTarget.latestException.expectAction) }}</el-descriptions-item>
            <el-descriptions-item label="问题描述">{{ formatText(handleTarget.latestException.description) }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatText(handleTarget.latestException.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <el-empty v-else description="暂无用户提交内容" :image-size="64" />
      </div>
      <el-form ref="handleFormRef" :model="handleForm" :rules="handleRules" label-width="96px" class="handle-form">
        <el-form-item label="处理类型" prop="handleType">
          <el-radio-group v-model="handleForm.handleType">
            <el-radio label="RESOLVED">已处理</el-radio>
            <el-radio label="UNRESOLVED">未处理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明" prop="remark">
          <el-input v-model="handleForm.remark" type="textarea" :rows="4" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <AppDialog v-model="cancelDialogVisible" title="取消订单" :loading="cancelLoading" width="520px" @confirm="submitCancel">
      <el-form ref="cancelFormRef" :model="cancelForm" :rules="cancelRules" label-width="96px">
        <el-form-item label="取消原因" prop="reason">
          <el-input v-model="cancelForm.reason" type="textarea" :rows="4" placeholder="请输入取消原因" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <el-dialog
      v-model="expressDialogVisible"
      title="物流信息"
      width="560px"
      destroy-on-close
    >
      <div v-loading="expressLoading">
        <template v-if="expressDetail">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ expressDetail.orderNo || '—' }}</el-descriptions-item>
            <el-descriptions-item label="快递公司">{{ expressDetail.expressCompany || '顺丰速运' }}</el-descriptions-item>
            <el-descriptions-item label="快递单号">{{ formatText(expressDetail.expressNo) }}</el-descriptions-item>
            <el-descriptions-item label="物流状态">
              {{ expressTracks.find((item) => item.active)?.title || expressDetail.statusDesc || '—' }}
            </el-descriptions-item>
          </el-descriptions>
          <div class="express-tracks">
            <div class="express-tracks__title">物流轨迹</div>
            <el-timeline v-if="expressTracks.length">
              <el-timeline-item
                v-for="(item, index) in expressTracks"
                :key="`${item.title}-${index}`"
                :timestamp="item.time"
                :type="item.active ? 'primary' : undefined"
                :hollow="!item.active"
              >
                <div class="express-tracks__item-title">{{ item.title }}</div>
                <div v-if="item.desc" class="express-tracks__item-desc">{{ item.desc }}</div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无物流信息" :image-size="72" />
          </div>
        </template>
      </div>
      <template #footer>
        <el-button type="primary" @click="expressDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.express-no-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.express-no-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.express-tracks {
  margin-top: 16px;
}

.express-tracks__title {
  margin-bottom: 12px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.express-tracks__item-title {
  font-weight: 600;
  line-height: 1.4;
}

.express-tracks__item-desc {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}

.handle-feedback {
  margin-bottom: 16px;
}

.handle-feedback__title {
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.handle-form {
  margin-top: 4px;
}
</style>
