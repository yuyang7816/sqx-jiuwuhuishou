<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppDialog from '@/components/common/AppDialog.vue'
import StaffSelect from '@/components/common/StaffSelect.vue'
import {
  assignPickupApi,
  cancelOrderApi,
  completeOrderApi,
  confirmMailReceiveApi,
  getOrderDetail,
  getOrderExpressTracks,
  handleOrderApi,
  updateOrderEstimateApi,
} from '@/api/modules/order'
import { handleOrderExceptionApi } from '@/api/modules/order-exception'
import { validateForm } from '@/utils/form'
import { confirmAction, showError, showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'
import { formatUserLabel, orderStatusKeyMap, orderTypeMap, fulfillTypeMap, tagMeta } from '@/utils/display'
import { resolveExpressTracks } from '@/utils/express'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)
const activeTab = ref('basic')

const orderId = computed(() => Number(route.params.id))

const assignDialogVisible = ref(false)
const assignLoading = ref(false)
const assignFormRef = ref()
const assignForm = reactive({ orderId: null, staffId: null })
const assignRules = { staffId: [{ required: true, message: '请选择员工', trigger: 'change' }] }

const actualDialogVisible = ref(false)
const actualLoading = ref(false)
const actualFormRef = ref()
const actualForm = reactive({ id: null, actualAmount: null, remark: '' })
const actualRules = { actualAmount: [{ required: true, message: '请输入实际金额', trigger: 'blur' }] }

const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelFormRef = ref()
const cancelForm = reactive({ id: null, reason: '' })
const cancelRules = { reason: [{ required: true, message: '请输入取消原因', trigger: 'blur' }] }

const handleDialogVisible = ref(false)
const handleLoading = ref(false)
const handleFormRef = ref()
const handleForm = reactive({ id: null, handleType: 'RESOLVED', remark: '' })
const handleRules = {
  handleType: [{ required: true, message: '请选择处理类型', trigger: 'change' }],
  remark: [{ required: true, message: '请输入处理说明', trigger: 'blur' }],
}

const exceptionHandleDialogVisible = ref(false)
const exceptionHandleLoading = ref(false)
const exceptionHandleFormRef = ref()
const exceptionHandleForm = reactive({ id: null, handleType: 'RESOLVED', remark: '' })
const exceptionHandleRules = {
  handleType: [{ required: true, message: '请选择处理方式', trigger: 'change' }],
}

function formatText(value) {
  if (value == null || value === '') return '—'
  return value
}

function formatAmount(value) {
  if (value == null || value === '') return '—'
  return Number(value).toFixed(2)
}

function formatEstimateAmount(detail = {}) {
  const orderType = `${detail.orderType || ''}`.toLowerCase()
  const min = detail.estimateMinAmount
  const max = detail.estimateMaxAmount
  if (orderType === 'appliance' && (min != null || max != null)) {
    const low = min ?? detail.estimateAmount
    const high = max ?? detail.estimateAmount
    if (low != null && high != null) {
      if (Number(low) === Number(high)) {
        return formatAmount(low)
      }
      return `${formatAmount(low)} ~ ${formatAmount(high)}`
    }
  }
  return formatAmount(detail.estimateAmount)
}

const canAssign = computed(() => {
  if (!detail.value) return false
  if ((detail.value.fulfillType || 'visit') === 'mail') return false
  return detail.value.status === 'WAIT_ASSIGN' || detail.value.status === 'IN_PROGRESS'
})
const canEditActual = computed(() => detail.value?.status === 'WAIT_SETTLEMENT_CONFIRM')
const canConfirmPayout = computed(() => detail.value?.status === 'PAYOUT_PROCESSING')
const canConfirmMailReceive = computed(() => {
  if (!detail.value) return false
  if ((detail.value.fulfillType || 'visit') !== 'mail') return false
  if (detail.value.status !== 'IN_PROGRESS') return false
  return `${detail.value.statusDesc || ''}`.trim() === '待收货' && Boolean(detail.value.expressNo)
})

function goBackToList() {
  if (route.query.from === 'user') {
    const uid = route.query.userId || detail.value?.userId
    if (uid) {
      router.push({
        path: `/users/${uid}`,
        query: { tab: 'orders' },
      })
      return
    }
  }
  if (window.history.length > 1) {
    router.back()
    return
  }
  router.push('/business/orders')
}
const canHandle = computed(() => detail.value && (detail.value.status === 'EXCEPTION_PENDING' || detail.value.status === 'DISPUTE_PENDING'))
const canCancel = computed(() => detail.value && detail.value.status !== 'CANCELLED' && detail.value.status !== 'COMPLETED')
const pendingException = computed(() => {
  if (!detail.value || detail.value.status !== 'EXCEPTION_PENDING') return null
  return (detail.value.exceptions || [])[0] || null
})
const pendingDispute = computed(() => {
  if (!detail.value || detail.value.status !== 'DISPUTE_PENDING') return null
  return detail.value.latestDispute || (detail.value.disputes || [])[0] || null
})
const isMailOrder = computed(() => (detail.value?.fulfillType || 'visit') === 'mail')
const expressTracks = computed(() => {
  const tracks = detail.value?.expressTracks
  if (Array.isArray(tracks) && tracks.length) return tracks
  return resolveExpressTracks(detail.value || {})
})
const expressTracksLoading = ref(false)

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getOrderDetail(orderId.value)
  } finally {
    loading.value = false
  }
}

async function refreshExpressTracks() {
  if (!isMailOrder.value || !orderId.value) return
  expressTracksLoading.value = true
  try {
    const tracks = await getOrderExpressTracks(orderId.value)
    if (Array.isArray(tracks) && tracks.length) {
      detail.value = {
        ...detail.value,
        expressTracks: tracks,
      }
    } else {
      showError('暂未查询到物流轨迹，请确认顺丰管家凭证已配置、单号有效，且收寄件手机后四位匹配')
    }
  } catch (error) {
    // interceptor 已提示错误信息
  } finally {
    expressTracksLoading.value = false
  }
}

function openAssign() {
  assignForm.orderId = orderId.value
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
    await loadDetail()
  } finally {
    assignLoading.value = false
  }
}

function openActual() {
  actualForm.id = orderId.value
  actualForm.actualAmount = detail.value?.actualAmount ?? null
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
    await loadDetail()
  } finally {
    actualLoading.value = false
  }
}

async function handleConfirmPayout() {
  await confirmAction(`确认对订单 ${detail.value.orderNo} 完成打款吗？完成后订单将变为已完成。`)
  await completeOrderApi({ id: orderId.value })
  showSuccess('打款已完成，订单已闭环')
  await loadDetail()
}

async function handleConfirmMailReceive() {
  await confirmAction(`确认已收到订单 ${detail.value.orderNo} 的快递包裹吗？确认后订单将变为待确认。`)
  await confirmMailReceiveApi({ id: orderId.value })
  showSuccess('已确认收货，订单进入待确认')
  await loadDetail()
}

function openHandle() {
  handleForm.id = orderId.value
  handleForm.handleType = 'RESOLVED'
  handleForm.remark = ''
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
    await loadDetail()
  } finally {
    handleLoading.value = false
  }
}

function openExceptionHandle(row) {
  exceptionHandleForm.id = row.id
  exceptionHandleForm.handleType = 'RESOLVED'
  exceptionHandleForm.remark = ''
  exceptionHandleDialogVisible.value = true
}

async function submitExceptionHandle() {
  const valid = await validateForm(exceptionHandleFormRef)
  if (!valid) return
  exceptionHandleLoading.value = true
  try {
    await handleOrderExceptionApi({ ...exceptionHandleForm })
    showSuccess('异常反馈已处理')
    exceptionHandleDialogVisible.value = false
    await loadDetail()
  } finally {
    exceptionHandleLoading.value = false
  }
}

function openCancel() {
  cancelForm.id = orderId.value
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
    await loadDetail()
  } finally {
    cancelLoading.value = false
  }
}

onMounted(loadDetail)
</script>

<template>
  <div v-loading="loading">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <strong>订单详情</strong>
          <div class="header-actions">
            <el-button v-if="canAssign && isAuth(PERMS.orderAssign)" type="primary" @click="openAssign">分配员工</el-button>
            <el-button v-if="canConfirmMailReceive" type="primary" @click="handleConfirmMailReceive">确认收货</el-button>
            <el-button v-if="canEditActual" @click="openActual">修改实际金额</el-button>
            <el-button v-if="canConfirmPayout" type="success" @click="handleConfirmPayout">确认打款</el-button>
            <el-button v-if="canHandle" type="warning" @click="openHandle">处理异常/争议</el-button>
            <el-button v-if="pendingException" type="warning" plain @click="openExceptionHandle(pendingException)">处理最新异常</el-button>
            <el-button v-if="canCancel && isAuth(PERMS.orderCancel)" type="danger" plain @click="openCancel">取消订单</el-button>
            <el-button @click="goBackToList">返回列表</el-button>
          </div>
        </div>
      </template>

      <template v-if="detail">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
              <el-descriptions-item label="用户">{{ formatUserLabel(detail) }}</el-descriptions-item>
              <el-descriptions-item label="品类">{{ detail.categoryName }}</el-descriptions-item>
              <el-descriptions-item label="类型">
                <el-tag :type="tagMeta(orderTypeMap, detail.orderType).type">{{ tagMeta(orderTypeMap, detail.orderType).label }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="履约方式">
                <el-tag :type="tagMeta(fulfillTypeMap, detail.fulfillType || 'visit').type">{{ detail.fulfillTypeLabel || tagMeta(fulfillTypeMap, detail.fulfillType || 'visit').label }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="tagMeta(orderStatusKeyMap, detail.statusKey).type">{{ detail.statusDesc || detail.status }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="预约时间">{{ detail.appointmentTime }}</el-descriptions-item>
              <el-descriptions-item label="联系人">{{ detail.contactName }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ detail.contactMobile }}</el-descriptions-item>
              <el-descriptions-item label="回收/上门地址" :span="2">{{ detail.pickupAddressText || detail.addressText || '—' }}</el-descriptions-item>
              <el-descriptions-item v-if="(detail.fulfillType || 'visit') === 'mail'" label="快递单号">{{ detail.expressNo || '—' }}</el-descriptions-item>
              <el-descriptions-item v-if="(detail.fulfillType || 'visit') === 'mail'" label="快递公司">{{ detail.expressCompany || '顺丰速运' }}</el-descriptions-item>
              <el-descriptions-item v-if="(detail.fulfillType || 'visit') === 'mail'" label="收件地址" :span="2">{{ detail.mailAddressText || '—' }}</el-descriptions-item>
              <el-descriptions-item label="预估金额">{{ formatEstimateAmount(detail) }}</el-descriptions-item>
              <el-descriptions-item label="实际金额">{{ detail.actualAmount ?? '—' }}</el-descriptions-item>
              <el-descriptions-item label="用户反馈" :span="2">{{ formatText(detail.userFeedbackSummary) }}</el-descriptions-item>
              <el-descriptions-item label="备注" :span="2">{{ detail.remark || '—' }}</el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>

          <el-tab-pane v-if="isMailOrder" :label="`物流轨迹 (${expressTracks.length})`" name="express">
            <div class="express-panel">
              <div class="express-panel__head">
                <div>
                  <div>快递单号：{{ detail.expressNo || '—' }}</div>
                  <div class="express-panel__sub">快递公司：{{ detail.expressCompany || '顺丰速运' }}</div>
                </div>
                <el-button :loading="expressTracksLoading" @click="refreshExpressTracks">刷新轨迹</el-button>
              </div>
              <el-timeline v-loading="expressTracksLoading" v-if="expressTracks.length">
                <el-timeline-item
                  v-for="(item, index) in expressTracks"
                  :key="`${item.title}-${index}`"
                  :timestamp="item.time"
                  :type="item.active ? 'primary' : undefined"
                  :hollow="!item.active"
                >
                  <div>{{ item.title }}</div>
                  <div v-if="item.desc" class="express-panel__desc">{{ item.desc }}</div>
                </el-timeline-item>
              </el-timeline>
              <el-empty v-else description="暂无物流轨迹" />
            </div>
          </el-tab-pane>

          <el-tab-pane :label="`改约记录 (${detail.reschedules?.length || 0})`" name="reschedule">
            <el-table :data="detail.reschedules || []" border stripe empty-text="暂无改约记录">
              <el-table-column type="index" label="编号" width="70" align="center" />
              <el-table-column prop="reason" label="改约原因" min-width="140" />
              <el-table-column prop="newAppointmentTime" label="新预约时间" min-width="170" />
              <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
              <el-table-column prop="createTime" label="提交时间" min-width="170" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane :label="`异常反馈 (${detail.exceptions?.length || 0})`" name="exception">
            <el-table :data="detail.exceptions || []" border stripe empty-text="暂无异常反馈">
              <el-table-column type="index" label="编号" width="70" align="center" />
              <el-table-column prop="exceptionType" label="异常类型" min-width="120" />
              <el-table-column prop="expectAction" label="期望处理" min-width="120" />
              <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
              <el-table-column prop="orderStatusDesc" label="订单状态" min-width="120" />
              <el-table-column prop="createTime" label="提交时间" min-width="170" />
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button
                    v-if="detail.status === 'EXCEPTION_PENDING' && row.id === pendingException?.id"
                    link
                    type="primary"
                    @click="openExceptionHandle(row)"
                  >
                    处理
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane :label="`争议记录 (${detail.disputes?.length || 0})`" name="dispute">
            <el-table :data="detail.disputes || []" border stripe empty-text="暂无争议记录">
              <el-table-column type="index" label="编号" width="70" align="center" />
              <el-table-column prop="reason" label="争议原因" min-width="140" />
              <el-table-column prop="description" label="争议描述" min-width="200" show-overflow-tooltip />
              <el-table-column prop="status" label="状态" width="100" />
              <el-table-column prop="createTime" label="提交时间" min-width="170" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane :label="`评价投诉 (${detail.reviews?.length || 0})`" name="review">
            <el-table :data="detail.reviews || []" border stripe empty-text="暂无评价/投诉">
              <el-table-column type="index" label="编号" width="70" align="center" />
              <el-table-column prop="score" label="评分" width="80" />
              <el-table-column prop="complaintType" label="投诉类型" min-width="120" />
              <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
              <el-table-column prop="adminReply" label="客服回复" min-width="160" show-overflow-tooltip />
              <el-table-column prop="createTime" label="提交时间" min-width="170" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane :label="`状态时间线 (${detail.logs?.length || 0})`" name="logs">
            <el-timeline v-if="detail.logs?.length">
              <el-timeline-item v-for="item in detail.logs" :key="item.id || item.createTime" :timestamp="item.createTime">
                {{ item.content || item.actionType }}
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无流水" />
          </el-tab-pane>
        </el-tabs>
      </template>
    </el-card>

    <AppDialog v-model="assignDialogVisible" title="分配员工" :loading="assignLoading" width="520px" @confirm="submitAssign">
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="96px">
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
        <el-form-item label="备注"><el-input v-model="actualForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
    </AppDialog>

    <AppDialog v-model="handleDialogVisible" title="处理异常/争议" :loading="handleLoading" width="560px" @confirm="submitHandle">
      <div class="handle-feedback">
        <template v-if="detail?.status === 'DISPUTE_PENDING' && pendingDispute">
          <div class="handle-feedback__title">用户争议内容</div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="争议原因">{{ formatText(pendingDispute.reason) }}</el-descriptions-item>
            <el-descriptions-item label="争议描述">{{ formatText(pendingDispute.description) }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatText(pendingDispute.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <template v-else-if="detail?.status === 'EXCEPTION_PENDING' && pendingException">
          <div class="handle-feedback__title">用户异常反馈</div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="异常类型">{{ formatText(pendingException.exceptionType) }}</el-descriptions-item>
            <el-descriptions-item label="期望处理">{{ formatText(pendingException.expectAction) }}</el-descriptions-item>
            <el-descriptions-item label="问题描述">{{ formatText(pendingException.description) }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatText(pendingException.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <el-empty v-else description="暂无用户提交内容" :image-size="64" />
      </div>
      <el-form ref="handleFormRef" :model="handleForm" :rules="handleRules" label-width="96px" class="handle-form">
        <el-form-item label="处理类型" prop="handleType">
          <el-select v-model="handleForm.handleType" style="width: 100%">
            <el-option label="已处理（恢复履约/待确认）" value="RESOLVED" />
            <el-option label="未处理（取消订单）" value="UNRESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理说明" prop="remark"><el-input v-model="handleForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
    </AppDialog>

    <AppDialog v-model="exceptionHandleDialogVisible" title="处理异常反馈" :loading="exceptionHandleLoading" width="520px" @confirm="submitExceptionHandle">
      <el-form ref="exceptionHandleFormRef" :model="exceptionHandleForm" :rules="exceptionHandleRules" label-width="108px">
        <el-form-item label="处理方式" prop="handleType">
          <el-select v-model="exceptionHandleForm.handleType" style="width: 100%">
            <el-option label="已处理，恢复上门取件" value="RESOLVED" />
            <el-option label="未处理，取消订单" value="UNRESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理说明"><el-input v-model="exceptionHandleForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
    </AppDialog>

    <AppDialog v-model="cancelDialogVisible" title="取消订单" :loading="cancelLoading" width="520px" @confirm="submitCancel">
      <el-form ref="cancelFormRef" :model="cancelForm" :rules="cancelRules" label-width="96px">
        <el-form-item label="取消原因" prop="reason"><el-input v-model="cancelForm.reason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
    </AppDialog>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}
.header-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
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
.express-panel__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}
.express-panel__sub {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
}
.express-panel__desc {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
