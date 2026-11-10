<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import {
  getAppUserInfo,
  getAppUserPage,
  getReferralUserCommissionInfoApi,
  getUserAddressListApi,
  getUserSignRecordPageApi,
  getUserWelfareInfoApi,
  updateAppUserApi,
  updateReferralUserCommissionApi,
  updateUserWelfareCoinApi,
} from '@/api/modules/app-user'
import { orderApi } from '@/api/modules/order'
import { pickupTaskApi } from '@/api/modules/pickup-task'
import { referralCommissionApi } from '@/api/modules/referral-commission'
import { updateUserWalletAmountApi, userWalletApi } from '@/api/modules/user-wallet'
import { userWalletTransactionApi } from '@/api/modules/user-wallet-transaction'
import { withdrawRecordApi } from '@/api/modules/withdraw-record'
import { calcListIndex, isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'
import { appUserTypeMap, appUserTypeOptions, orderStatusKeyMap, orderTypeMap, pickupStatusKeyMap, referralTypeMap, resolveFileUrl, tagMeta, userPlatformMap, withdrawStatusMap } from '@/utils/display'
import { showSuccess } from '@/utils/modal'

const STAFF_USER_TYPE = 3
const route = useRoute()
const router = useRouter()

const userId = computed(() => Number(route.params.userId))
const loading = ref(false)
const activeTab = ref('profile')
const user = ref(null)
const walletAmount = ref(0)
const commissionAmount = ref(0)
const commissionId = ref(null)
const coinBalance = ref(0)

const dialogVisible = ref(false)
const dialogLoading = ref(false)
const dialogTitle = ref('')
const dialogForm = reactive({})
let dialogSubmit = null

const tableLoading = ref(false)
const tableData = ref([])
const tableTotal = ref(0)
const tablePageNum = ref(1)
const tablePageSize = ref(10)

const isStaff = computed(() => Number(user.value?.userType) === STAFF_USER_TYPE)
const userTypeLabel = computed(() => appUserTypeMap[user.value?.userType]?.label || '—')
const platformLabel = computed(() => {
  const platform = user.value?.platform
  if (!platform) return '—'
  return userPlatformMap[platform]?.label || platform
})
const platformTag = computed(() => userPlatformMap[user.value?.platform]?.type || 'info')
const userTypeTag = computed(() => appUserTypeMap[user.value?.userType]?.type || 'info')
const statusLabel = computed(() => (user.value?.status === 1 ? '正常' : '禁用'))
const statusType = computed(() => (user.value?.status === 1 ? 'success' : 'danger'))
const sexLabel = computed(() => {
  if (user.value?.sex === 1) return '男'
  if (user.value?.sex === 2) return '女'
  return '未知'
})

const tabs = computed(() => {
  const base = [
    { key: 'profile', label: '用户详情' },
    { key: 'withdraw', label: '提现明细' },
    { key: 'walletTx', label: '钱包明细' },
    { key: 'pointTx', label: '积分明细' },
    { key: 'earnings', label: '收益明细' },
    { key: 'orders', label: '我的下单' },
  ]
  if (isStaff.value) {
    base.push({ key: 'pickups', label: '我的接单' })
  }
  base.push({ key: 'addresses', label: '我的地址' })
  base.push({ key: 'invites', label: '我邀请的用户' })
  return base
})

function resolvePageRequest(api) {
  if (typeof api === 'function') return api
  if (api && typeof api.page === 'function') return api.page.bind(api)
  return null
}

const tabLoaders = {
  withdraw: () => loadPaged(withdrawRecordApi, { userId: userId.value }),
  walletTx: () => loadPaged(userWalletTransactionApi, { userId: userId.value }),
  pointTx: () => loadPaged(getUserSignRecordPageApi, { userId: userId.value }),
  earnings: () => loadPaged(referralCommissionApi, { userId: userId.value }),
  orders: () => loadPaged(orderApi, { userId: userId.value }),
  pickups: () => loadPaged(pickupTaskApi, { staffId: userId.value }),
  invites: () => {
    if (!user.value?.invitationCode) {
      tableData.value = []
      tableTotal.value = 0
      return Promise.resolve()
    }
    return loadPaged(getAppUserPage, { superiorInvitationCode: user.value.invitationCode })
  },
}

async function loadProfile() {
  loading.value = true
  try {
    const userInfo = await getAppUserInfo(userId.value)
    user.value = userInfo
    const [walletPage, commissionInfo, welfareInfo] = await Promise.all([
      userWalletApi.page({ page: 1, limit: 1, phone: userInfo.phone }),
      getReferralUserCommissionInfoApi(userId.value).catch(() => null),
      getUserWelfareInfoApi(userId.value).catch(() => ({ coinBalance: 0 })),
    ])
    walletAmount.value = Number(walletPage.list?.[0]?.amount || 0)
    commissionAmount.value = Number(commissionInfo?.commission || 0)
    commissionId.value = commissionInfo?.id || null
    coinBalance.value = Number(welfareInfo?.coinBalance || 0)
  } finally {
    loading.value = false
  }
}

async function loadPaged(api, params = {}) {
  const request = resolvePageRequest(api)
  if (!request) return
  tableLoading.value = true
  try {
    const data = await request({
      page: tablePageNum.value,
      limit: tablePageSize.value,
      ...params,
    })
    tableData.value = data.list || []
    tableTotal.value = data.total || 0
  } finally {
    tableLoading.value = false
  }
}

async function reloadCurrentTab(resetPage = false) {
  if (resetPage) tablePageNum.value = 1
  const loader = tabLoaders[activeTab.value]
  if (loader) await loader()
}

async function loadAddresses() {
  tableLoading.value = true
  try {
    tableData.value = await getUserAddressListApi(userId.value)
    tableTotal.value = tableData.value.length
  } finally {
    tableLoading.value = false
  }
}

function handleTabChange(tabName) {
  activeTab.value = tabName
  loadActiveTab()
}

async function loadActiveTab() {
  tablePageNum.value = 1
  if (activeTab.value === 'profile') {
    await loadProfile()
    return
  }
  if (!user.value) {
    await loadProfile()
  }
  if (activeTab.value === 'addresses') {
    await loadAddresses()
    return
  }
  const loader = tabLoaders[activeTab.value]
  if (loader) await loader()
}

function openDialog(title, form, submit) {
  dialogTitle.value = title
  Object.keys(dialogForm).forEach((key) => delete dialogForm[key])
  Object.assign(dialogForm, form)
  dialogSubmit = submit
  dialogVisible.value = true
}

async function handleDialogConfirm() {
  dialogLoading.value = true
  try {
    await dialogSubmit()
    showSuccess('已保存')
    dialogVisible.value = false
    await loadProfile()
  } finally {
    dialogLoading.value = false
  }
}

function openWalletDialog() {
  openDialog('修改金额', { amount: 0, operator: 'incr' }, async () => {
    await updateUserWalletAmountApi({ userId: userId.value, amount: dialogForm.amount, operator: dialogForm.operator })
  })
}

function openCommissionDialog() {
  openDialog('修改分销收益', { commission: commissionAmount.value }, async () => {
    await updateReferralUserCommissionApi({
      id: commissionId.value,
      userId: userId.value,
      commission: dialogForm.commission,
    })
  })
}

function openCoinDialog() {
  openDialog('修改积分', { amount: 0, operator: 'incr' }, async () => {
    await updateUserWelfareCoinApi({ userId: userId.value, amount: dialogForm.amount, operator: dialogForm.operator })
  })
}

function openAvatarDialog() {
  openDialog('修改图像', { avatar: user.value?.avatar || '' }, async () => {
    await updateAppUserApi({ userId: userId.value, avatar: dialogForm.avatar })
  })
}

function openFieldDialog(label, field, options = {}) {
  const value = user.value?.[field]
  const form = { value: options.type === 'number' ? Number(value || 0) : (value ?? '') }
  openDialog(`修改${label}`, form, async () => {
    await updateAppUserApi({ userId: userId.value, [field]: form.value })
  })
}

function openSexDialog() {
  openDialog('修改性别', { value: user.value?.sex || 1 }, async () => {
    await updateAppUserApi({ userId: userId.value, sex: dialogForm.value })
  })
}

function openStatusDialog() {
  openDialog('更改状态', { status: user.value?.status ?? 1 }, async () => {
    await updateAppUserApi({ userId: userId.value, status: dialogForm.status })
  })
}

function openUserTypeDialog() {
  const current = Number(user.value?.userType)
  openDialog('修改用户类型', { userType: current === 2 ? 1 : (Number.isFinite(current) ? current : 1) }, async () => {
    await updateAppUserApi({ userId: userId.value, userType: dialogForm.userType })
  })
}

function formatAddress(row) {
  return [row.province, row.city, row.district, row.street, row.detailAddress].filter(Boolean).join('')
}

function formatAmount(row) {
  const amount = Number(row.amount || 0)
  const prefix = row.direction === 2 ? '-' : '+'
  return `${prefix} ${amount}`
}

function formatMoney(value) {
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
        return formatMoney(low)
      }
      return `${formatMoney(low)} ~ ${formatMoney(high)}`
    }
  }
  return formatMoney(row.estimateAmount)
}

function amountClass(row) {
  return row.direction === 2 ? 'amount-out' : 'amount-in'
}

function calcIndex(index) {
  return calcListIndex(tablePageNum.value, tablePageSize.value, index)
}

function goBack() {
  router.push('/users')
}

function openOrderDetail(row) {
  router.push({
    path: `/business/orders/${row.id}`,
    query: {
      from: 'user',
      userId: String(userId.value),
    },
  })
}

watch(userId, () => {
  activeTab.value = (typeof route.query.tab === 'string' && route.query.tab) || 'profile'
  loadActiveTab()
})

watch(
  () => route.query.tab,
  (tab) => {
    if (typeof tab === 'string' && tab && tab !== activeTab.value) {
      activeTab.value = tab
      loadActiveTab()
    }
  },
)

onMounted(() => {
  const tab = route.query.tab
  if (typeof tab === 'string' && tab) {
    activeTab.value = tab
  }
  loadActiveTab()
})
</script>

<template>
  <div v-loading="loading" class="user-detail-page">
    <div class="detail-topbar">
      <div class="detail-topbar-left">
        <el-button class="back-btn" @click="goBack">返回</el-button>
        <span class="detail-title">用户详情</span>
      </div>
      <div class="detail-topbar-right">
        <el-tag :type="statusType" effect="plain">{{ statusLabel }}</el-tag>
        <el-button v-if="isAuth(PERMS.userUpdateStatus)" @click="openStatusDialog">更改状态</el-button>
      </div>
    </div>

    <div class="detail-tabs-wrap">
      <el-tabs v-model="activeTab" class="detail-tabs" @tab-change="handleTabChange">
        <el-tab-pane v-for="tab in tabs" :key="tab.key" :label="tab.label" :name="tab.key" />
      </el-tabs>
    </div>

    <div v-if="activeTab === 'profile' && user" class="detail-panel">
      <table class="info-grid">
        <tbody>
          <tr>
            <td>
              <div class="info-cell">
                <span class="info-label">用户余额</span>
                <span class="info-value">{{ walletAmount.toFixed(2) }}元</span>
                <a v-if="isAuth(PERMS.userUpdateAmount)" class="info-action" @click="openWalletDialog">修改金额</a>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">分销收益</span>
                <span class="info-value">{{ commissionAmount.toFixed(2) }}元</span>
                <a v-if="isAuth(PERMS.userUpdateCommission)" class="info-action" @click="openCommissionDialog">修改金额</a>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">微信名称</span>
                <span class="info-value">{{ user.userName || '—' }}</span>
                <a v-if="isAuth(PERMS.userUpdate)" class="info-action" @click="openFieldDialog('微信名称', 'userName')">修改</a>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="info-cell">
                <span class="info-label">用户编号</span>
                <span class="info-value">{{ user.userId }}</span>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">用户状态</span>
                <span class="info-value">{{ statusLabel }}</span>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">创建时间</span>
                <span class="info-value">{{ user.createTime || '—' }}</span>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="info-cell">
                <span class="info-label">手机号</span>
                <span class="info-value">{{ user.phone || '—' }}</span>
                <a v-if="isAuth(PERMS.userUpdate)" class="info-action" @click="openFieldDialog('手机号', 'phone')">修改</a>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">图像</span>
                <span class="info-value avatar-value">
                  <el-image v-if="user.avatar" :src="resolveFileUrl(user.avatar)" fit="cover" class="avatar-img">
                    <template #error><span class="avatar-fallback">加载失败</span></template>
                  </el-image>
                  <span v-else class="avatar-fallback">—</span>
                </span>
                <a v-if="isAuth(PERMS.userUpdate)" class="info-action" @click="openAvatarDialog">修改</a>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">性别</span>
                <span class="info-value">{{ sexLabel }}</span>
                <a v-if="isAuth(PERMS.userUpdate)" class="info-action" @click="openSexDialog">修改</a>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="info-cell">
                <span class="info-label">邀请人邀请码</span>
                <span class="info-value">{{ user.superiorInvitationCode || '—' }}</span>
                <a v-if="isAuth(PERMS.userUpdate)" class="info-action" @click="openFieldDialog('邀请人邀请码', 'superiorInvitationCode')">修改</a>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">邀请码</span>
                <span class="info-value">{{ user.invitationCode || '—' }}</span>
                <a v-if="isAuth(PERMS.userUpdate)" class="info-action" @click="openFieldDialog('邀请码', 'invitationCode')">修改</a>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">渠道来源</span>
                <span class="info-value">
                  <el-tag v-if="user.platform" :type="platformTag" size="small">{{ platformLabel }}</el-tag>
                  <template v-else>—</template>
                </span>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="info-cell">
                <span class="info-label">用户类型</span>
                <span class="info-value">
                  <el-tag :type="userTypeTag" size="small">{{ userTypeLabel }}</el-tag>
                </span>
                <a v-if="isAuth(PERMS.userUpdate)" class="info-action" @click="openUserTypeDialog">修改</a>
              </div>
            </td>
            <td>
              <div class="info-cell">
                <span class="info-label">积分</span>
                <span class="info-value">{{ coinBalance }}</span>
                <a v-if="isAuth(PERMS.userUpdateCoin)" class="info-action" @click="openCoinDialog">修改</a>
              </div>
            </td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else-if="activeTab === 'withdraw'" class="detail-panel">
      <el-table v-loading="tableLoading" :data="tableData" border>
        <el-table-column label="编号" width="80" align="center">
          <template #default="{ $index }">{{ calcIndex($index) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="提现金额" width="110" />
        <el-table-column prop="actualAmount" label="到账金额" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="withdrawStatusMap[row.status]?.type">{{ withdrawStatusMap[row.status]?.label || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="备注" min-width="160" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>
      <BasePagination
        :current-page="tablePageNum"
        :page-size="tablePageSize"
        :total="tableTotal"
        @update:current-page="tablePageNum = $event; reloadCurrentTab()"
        @update:page-size="tablePageSize = $event; reloadCurrentTab(true)"
      />
    </div>

    <div v-else-if="activeTab === 'walletTx'" class="detail-panel">
      <el-table v-loading="tableLoading" :data="tableData" border>
        <el-table-column label="编号" width="80" align="center">
          <template #default="{ $index }">{{ calcIndex($index) }}</template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="140" />
        <el-table-column prop="description" label="内容" min-width="180" />
        <el-table-column label="金额" width="110">
          <template #default="{ row }">
            <span :class="amountClass(row)">{{ formatAmount(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>
      <BasePagination
        :current-page="tablePageNum"
        :page-size="tablePageSize"
        :total="tableTotal"
        @update:current-page="tablePageNum = $event; reloadCurrentTab()"
        @update:page-size="tablePageSize = $event; reloadCurrentTab(true)"
      />
    </div>

    <div v-else-if="activeTab === 'pointTx'" class="detail-panel">
      <el-table v-loading="tableLoading" :data="tableData" border>
        <el-table-column label="编号" width="80" align="center">
          <template #default="{ $index }">{{ calcIndex($index) }}</template>
        </el-table-column>
        <el-table-column prop="signDate" label="签到日期" min-width="140" />
        <el-table-column prop="coinReward" label="积分奖励" width="110">
          <template #default="{ row }">
            <span class="amount-in">+ {{ row.coinReward || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>
      <BasePagination
        :current-page="tablePageNum"
        :page-size="tablePageSize"
        :total="tableTotal"
        @update:current-page="tablePageNum = $event; reloadCurrentTab()"
        @update:page-size="tablePageSize = $event; reloadCurrentTab(true)"
      />
    </div>

    <div v-else-if="activeTab === 'earnings'" class="detail-panel">
      <el-table v-loading="tableLoading" :data="tableData" border>
        <el-table-column label="编号" width="80" align="center">
          <template #default="{ $index }">{{ calcIndex($index) }}</template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="140" />
        <el-table-column prop="description" label="说明" min-width="180" />
        <el-table-column prop="amount" label="收益金额" width="110">
          <template #default="{ row }">
            <span :class="amountClass(row)">{{ formatAmount(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="110">
          <template #default="{ row }">
            <el-tag :type="referralTypeMap[row.type]?.type">{{ referralTypeMap[row.type]?.label || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>
      <BasePagination
        :current-page="tablePageNum"
        :page-size="tablePageSize"
        :total="tableTotal"
        @update:current-page="tablePageNum = $event; reloadCurrentTab()"
        @update:page-size="tablePageSize = $event; reloadCurrentTab(true)"
      />
    </div>

    <div v-else-if="activeTab === 'orders'" class="detail-panel">
      <el-table v-loading="tableLoading" :data="tableData" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ calcIndex($index) }}</template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" min-width="160" />
        <el-table-column prop="categoryName" label="品类" min-width="120" />
        <el-table-column prop="orderType" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="tagMeta(orderTypeMap, row.orderType).type">{{ tagMeta(orderTypeMap, row.orderType).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="tagMeta(orderStatusKeyMap, row.statusKey).type">{{ row.statusDesc || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="estimateAmount" label="预估金额" width="160" align="right">
          <template #default="{ row }">{{ formatEstimateAmount(row) }}</template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" min-width="170" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openOrderDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <BasePagination
        :current-page="tablePageNum"
        :page-size="tablePageSize"
        :total="tableTotal"
        @update:current-page="tablePageNum = $event; reloadCurrentTab()"
        @update:page-size="tablePageSize = $event; reloadCurrentTab(true)"
      />
    </div>

    <div v-else-if="activeTab === 'pickups'" class="detail-panel">
      <el-table v-loading="tableLoading" :data="tableData" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ calcIndex($index) }}</template>
        </el-table-column>
        <el-table-column prop="taskNo" label="任务号" min-width="140" />
        <el-table-column prop="orderNo" label="订单号" min-width="150" />
        <el-table-column prop="categoryName" label="品类" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="tagMeta(pickupStatusKeyMap, row.statusKey).type">{{ row.statusText || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" min-width="170" />
        <el-table-column prop="addressText" label="地址" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>
      <BasePagination
        :current-page="tablePageNum"
        :page-size="tablePageSize"
        :total="tableTotal"
        @update:current-page="tablePageNum = $event; reloadCurrentTab()"
        @update:page-size="tablePageSize = $event; reloadCurrentTab(true)"
      />
    </div>

    <div v-else-if="activeTab === 'addresses'" class="detail-panel">
      <el-table v-loading="tableLoading" :data="tableData" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactMobile" label="联系电话" width="140" />
        <el-table-column label="地址" min-width="240">
          <template #default="{ row }">{{ formatAddress(row) }}</template>
        </el-table-column>
        <el-table-column prop="tag" label="标签" width="90" />
        <el-table-column prop="isDefault" label="默认" width="80">
          <template #default="{ row }">{{ row.isDefault === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>
    </div>

    <div v-else-if="activeTab === 'invites'" class="detail-panel">
      <el-table v-loading="tableLoading" :data="tableData" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ calcIndex($index) }}</template>
        </el-table-column>
        <el-table-column prop="userName" label="用户名" min-width="140" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>
      <BasePagination
        :current-page="tablePageNum"
        :page-size="tablePageSize"
        :total="tableTotal"
        @update:current-page="tablePageNum = $event; reloadCurrentTab()"
        @update:page-size="tablePageSize = $event; reloadCurrentTab(true)"
      />
    </div>

    <AppDialog v-model="dialogVisible" :title="dialogTitle" :loading="dialogLoading" @confirm="handleDialogConfirm">
      <el-form :model="dialogForm" label-width="96px">
        <template v-if="dialogTitle === '修改金额'">
          <el-form-item label="金额"><el-input-number v-model="dialogForm.amount" :min="0" :precision="2" style="width: 100%" /></el-form-item>
          <el-form-item label="方向">
            <el-select v-model="dialogForm.operator" style="width: 100%">
              <el-option label="增加" value="incr" />
              <el-option label="减少" value="decr" />
            </el-select>
          </el-form-item>
        </template>
        <template v-else-if="dialogTitle === '修改积分'">
          <el-form-item label="积分"><el-input-number v-model="dialogForm.amount" :min="0" :precision="0" style="width: 100%" /></el-form-item>
          <el-form-item label="方向">
            <el-select v-model="dialogForm.operator" style="width: 100%">
              <el-option label="增加" value="incr" />
              <el-option label="减少" value="decr" />
            </el-select>
          </el-form-item>
        </template>
        <template v-else-if="dialogTitle === '修改分销收益'">
          <el-form-item label="收益金额"><el-input-number v-model="dialogForm.commission" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        </template>
        <template v-else-if="dialogTitle === '更改状态'">
          <el-form-item label="状态">
            <el-select v-model="dialogForm.status" style="width: 100%">
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="2" />
            </el-select>
          </el-form-item>
        </template>
        <template v-else-if="dialogTitle === '修改用户类型'">
          <el-form-item label="用户类型">
            <el-select v-model="dialogForm.userType" style="width: 100%">
              <el-option
                v-for="item in appUserTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </template>
        <template v-else-if="dialogTitle === '修改图像'">
          <el-form-item label="头像">
            <ImageUpload v-model="dialogForm.avatar" tip="支持 JPG/PNG，不超过 5MB" />
          </el-form-item>
        </template>
        <template v-else-if="dialogTitle === '修改性别'">
          <el-form-item label="性别">
            <el-select v-model="dialogForm.value" style="width: 100%">
              <el-option label="男" :value="1" />
              <el-option label="女" :value="2" />
            </el-select>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item :label="dialogTitle.replace('修改', '')">
            <el-input v-model="dialogForm.value" />
          </el-form-item>
        </template>
      </el-form>
    </AppDialog>
  </div>
</template>

<style scoped>
.user-detail-page {
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 140px);
}

.detail-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px 0;
}

.detail-topbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.detail-topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-tabs-wrap {
  padding: 0 12px;
  border-bottom: 1px solid #ebeef5;
}

.detail-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.detail-tabs :deep(.el-tabs__item.is-active) {
  color: var(--el-color-primary);
}

.detail-tabs :deep(.el-tabs__active-bar) {
  background-color: var(--el-color-primary);
}

.detail-panel {
  padding: 16px 20px 24px;
}

.info-grid {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  border: 1px solid #ebeef5;
}

.info-grid td {
  border: 1px solid #ebeef5;
  padding: 0;
  width: 33.33%;
  vertical-align: middle;
  height: 1px;
}

.info-cell {
  display: flex;
  align-items: center;
  min-height: 56px;
  height: 100%;
  background: #fff;
}

.info-label {
  flex: 0 0 108px;
  padding: 0 12px;
  background: #fafafa;
  color: #606266;
  line-height: 56px;
  border-right: 1px solid #ebeef5;
  white-space: nowrap;
}

.info-value {
  flex: 1;
  min-width: 0;
  padding: 0 12px;
  color: #303133;
  word-break: break-all;
}

.info-action {
  flex: 0 0 auto;
  padding-right: 12px;
  color: var(--el-color-primary);
  cursor: pointer;
  white-space: nowrap;
}

.info-action:hover {
  opacity: 0.8;
}

.avatar-value {
  display: flex;
  align-items: center;
}

.avatar-img,
.avatar-fallback {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  background: #f5f7fa;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #909399;
}

.amount-in {
  color: #67c23a;
}

.amount-out {
  color: #f56c6c;
}

.base-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
