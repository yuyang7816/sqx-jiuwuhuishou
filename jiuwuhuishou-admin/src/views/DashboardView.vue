<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from 'echarts/components'
import VChart from 'vue-echarts'
import {
  fetchDatacenterBreakdown,
  fetchDatacenterGrowth,
  fetchDatacenterOverview,
  fetchDatacenterTrend,
} from '@/api/modules/datacenter'
import { BRAND_ADMIN_TITLE, BRAND_NAME } from '@/constants/brand'
import { useAuthStore } from '@/stores/auth'

use([CanvasRenderer, LineChart, PieChart, GridComponent, LegendComponent, TooltipComponent])

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const rangePreset = ref('7d')
const customRange = ref([])
const overview = ref(null)
const trend = ref(null)
const breakdown = ref(null)
const growth = ref(null)

const rangePresets = [
  { key: 'today', label: '今日' },
  { key: '7d', label: '近7日' },
  { key: '30d', label: '近30日' },
  { key: 'custom', label: '自定义' },
]

function pad(n) {
  return String(n).padStart(2, '0')
}

function formatDateTime(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

function startOfDay(date) {
  const d = new Date(date)
  d.setHours(0, 0, 0, 0)
  return d
}

function endOfDay(date) {
  const d = new Date(date)
  d.setHours(23, 59, 59, 0)
  return d
}

function resolveRange() {
  const now = new Date()
  if (rangePreset.value === 'today') {
    return { startTime: formatDateTime(startOfDay(now)), endTime: formatDateTime(endOfDay(now)) }
  }
  if (rangePreset.value === '30d') {
    const start = startOfDay(now)
    start.setDate(start.getDate() - 29)
    return { startTime: formatDateTime(start), endTime: formatDateTime(endOfDay(now)) }
  }
  if (rangePreset.value === 'custom' && customRange.value?.length === 2) {
    return {
      startTime: `${customRange.value[0]} 00:00:00`,
      endTime: `${customRange.value[1]} 23:59:59`,
    }
  }
  const start = startOfDay(now)
  start.setDate(start.getDate() - 6)
  return { startTime: formatDateTime(start), endTime: formatDateTime(endOfDay(now)) }
}

function formatMoney(value) {
  const num = Number(value || 0)
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function formatPercent(rate) {
  if (rate === null || rate === undefined || Number.isNaN(Number(rate))) return '-'
  return `${(Number(rate) * 100).toFixed(1)}%`
}

function formatChange(rate) {
  if (rate === null || rate === undefined || Number.isNaN(Number(rate))) return '—'
  const pct = Number(rate) * 100
  const sign = pct > 0 ? '+' : ''
  return `${sign}${pct.toFixed(1)}%`
}

function changeType(rate) {
  if (rate === null || rate === undefined || Number.isNaN(Number(rate))) return 'info'
  if (Number(rate) > 0) return 'success'
  if (Number(rate) < 0) return 'danger'
  return 'info'
}

const kpiCards = computed(() => {
  const data = overview.value || {}
  const compare = data.compare || {}
  return [
    {
      key: 'orderCount',
      label: '订单量',
      value: data.orderCount ?? 0,
      hint: '期内创建，含取消',
      change: compare.orderCountChangeRate,
    },
    {
      key: 'gmv',
      label: '成交额 GMV',
      value: `¥${formatMoney(data.gmv)}`,
      hint: 'COMPLETED · actual_amount',
      change: compare.gmvChangeRate,
    },
    {
      key: 'completionRate',
      label: '完成率',
      value: formatPercent(data.completionRate),
      hint: `${data.completedCount ?? 0} 单完成`,
      change: null,
      sub: `上期 ${formatPercent(compare.completionRate)}`,
    },
    {
      key: 'cancelRate',
      label: '取消率',
      value: formatPercent(data.cancelRate),
      hint: `${data.cancelledCount ?? 0} 单取消`,
      change: null,
      sub: `上期 ${formatPercent(compare.cancelRate)}`,
    },
    {
      key: 'backlogCount',
      label: '待处理积压',
      value: data.backlogCount ?? 0,
      hint: '审核/分配/异常/争议',
      change: null,
    },
    {
      key: 'newUserCount',
      label: '新增用户',
      value: data.newUserCount ?? 0,
      hint: `下单用户 ${data.orderUserCount ?? 0}`,
      change: compare.newUserCountChangeRate,
    },
    {
      key: 'amountDiff',
      label: '价差（实-估）',
      value: `¥${formatMoney(data.amountDiff)}`,
      hint: `估 ${formatMoney(data.estimateAmountSum)} / 实 ${formatMoney(data.actualAmountSum)}`,
      change: null,
    },
    {
      key: 'payoutSuccessRate',
      label: '打款成功率',
      value: formatPercent(data.payoutSuccessRate),
      hint: `成功 ${data.payoutSuccessCount ?? 0} / 失败 ${data.payoutFailCount ?? 0}`,
      change: null,
      sub: `上期 ${formatPercent(compare.payoutSuccessRate)}`,
    },
  ]
})

const fundItems = computed(() => {
  const data = overview.value || {}
  return [
    { label: '待确认结算金额', value: `¥${formatMoney(data.pendingSettlementAmount)}` },
    { label: '待打款金额', value: `¥${formatMoney(data.pendingPayoutAmount)}` },
    { label: '当期打款成功', value: `${data.payoutSuccessCount ?? 0} 笔` },
    { label: '当期打款失败', value: `${data.payoutFailCount ?? 0} 笔` },
  ]
})

const funnelMax = computed(() => {
  const list = overview.value?.funnel || []
  return Math.max(1, ...list.map((item) => Number(item.count || 0)))
})

const pickupCards = computed(() => {
  const pickup = breakdown.value?.pickup || {}
  return [
    { label: '待上门', value: pickup.pendingCount ?? 0 },
    { label: '取件中', value: pickup.runningCount ?? 0 },
    { label: '已完成', value: pickup.doneCount ?? 0 },
    { label: '超时未完成', value: pickup.overdueCount ?? 0 },
    { label: '期内完成', value: pickup.periodDoneCount ?? 0 },
    { label: '平均耗时(时)', value: Number(pickup.avgFinishHours || 0).toFixed(1) },
  ]
})

const qualityCards = computed(() => {
  const quality = breakdown.value?.quality || {}
  return [
    { label: '异常创建', value: quality.exceptionCreatedCount ?? 0 },
    { label: '异常未结', value: quality.exceptionOpenCount ?? 0 },
    { label: '争议创建', value: quality.disputeCreatedCount ?? 0 },
    { label: '争议未结', value: quality.disputePendingCount ?? 0 },
    { label: '争议解决率', value: formatPercent(quality.disputeResolveRate) },
    { label: '评价均分', value: Number(quality.reviewAvgScore || 0).toFixed(2) },
    { label: '评价数', value: quality.reviewCount ?? 0 },
    { label: '评价回复率', value: formatPercent(quality.reviewReplyRate) },
  ]
})

const withdrawCards = computed(() => {
  const data = growth.value?.withdraw || {}
  return [
    { label: '待审笔数', value: data.pendingCount ?? 0 },
    { label: '待审金额', value: `¥${formatMoney(data.pendingAmount)}` },
    { label: '期内发起', value: data.periodCreatedCount ?? 0 },
    { label: '期内成功', value: data.periodSuccessCount ?? 0 },
    { label: '期内拒绝', value: data.periodRejectCount ?? 0 },
    { label: '期内成功金额', value: `¥${formatMoney(data.periodSuccessAmount)}` },
  ]
})

const referralCards = computed(() => {
  const data = growth.value?.referral || {}
  return [
    { label: '佣金发放额', value: `¥${formatMoney(data.commissionAmount)}` },
    { label: '佣金笔数', value: data.commissionCount ?? 0 },
    { label: '邀请新增', value: data.inviteNewUserCount ?? 0 },
  ]
})

const welfareCards = computed(() => {
  const data = growth.value?.welfare || {}
  return [
    { label: '签到 UV', value: data.signUv ?? 0 },
    { label: '签到次数', value: data.signCount ?? 0 },
    { label: '期内发金币', value: data.periodCoinRewardSum ?? 0 },
    { label: '金币池余额', value: data.coinPoolBalance ?? 0 },
  ]
})

const serviceChatCards = computed(() => {
  const data = growth.value?.serviceChat || {}
  return [
    { label: '进行中会话', value: data.openSessionCount ?? 0 },
    { label: '已关闭会话', value: data.closedSessionCount ?? 0 },
    { label: '期内新建', value: data.periodCreatedCount ?? 0 },
  ]
})

const walletCards = computed(() => {
  const data = growth.value?.wallet || {}
  return [
    { label: '余额合计', value: `¥${formatMoney(data.balanceSum)}` },
    { label: '期内流入', value: `¥${formatMoney(data.inflowAmount)}` },
    { label: '期内流出', value: `¥${formatMoney(data.outflowAmount)}` },
    { label: '净流入', value: `¥${formatMoney(data.netInflowAmount)}` },
  ]
})

const withdrawFunnelMax = computed(() => {
  const list = growth.value?.withdraw?.funnel || []
  return Math.max(1, ...list.map((item) => Number(item.count || 0)))
})

const brandFunnelMax = computed(() => {
  const list = growth.value?.brand?.funnel || []
  return Math.max(1, ...list.map((item) => Number(item.count || 0)))
})

const trendOption = computed(() => {
  const points = trend.value?.points || []
  return {
    color: ['#409eff', '#67c23a'],
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单量', '成交额'] },
    grid: { left: 40, right: 48, top: 40, bottom: 28 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: points.map((item) => item.date),
    },
    yAxis: [
      { type: 'value', name: '单', minInterval: 1 },
      { type: 'value', name: '元', splitLine: { show: false } },
    ],
    series: [
      {
        name: '订单量',
        type: 'line',
        smooth: true,
        data: points.map((item) => Number(item.orderCount || 0)),
      },
      {
        name: '成交额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: points.map((item) => Number(item.gmv || 0)),
      },
    ],
  }
})

const categoryOption = computed(() => {
  const categories = breakdown.value?.categories || []
  return {
    color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399'],
    tooltip: {
      trigger: 'item',
      formatter: (params) =>
        `${params.name}<br/>订单 ${params.value}<br/>占比 ${params.percent}%`,
    },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        name: '品类订单',
        type: 'pie',
        radius: ['36%', '62%'],
        center: ['50%', '46%'],
        itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
        label: { formatter: '{b}\n{c}' },
        data: categories.map((item) => ({
          name: item.orderTypeLabel || item.orderType,
          value: Number(item.orderCount || 0),
        })),
      },
    ],
  }
})

async function loadDashboard() {
  if (rangePreset.value === 'custom' && (!customRange.value || customRange.value.length !== 2)) {
    return
  }
  loading.value = true
  const payload = resolveRange()
  try {
    const [overviewRes, trendRes, breakdownRes, growthRes] = await Promise.all([
      fetchDatacenterOverview(payload),
      fetchDatacenterTrend(payload),
      fetchDatacenterBreakdown(payload),
      fetchDatacenterGrowth(payload),
    ])
    overview.value = overviewRes
    trend.value = trendRes
    breakdown.value = breakdownRes
    growth.value = growthRes
  } finally {
    loading.value = false
  }
}

function goTo(path) {
  router.push(path)
}

watch(rangePreset, (val) => {
  if (val !== 'custom') {
    loadDashboard()
  }
})

watch(customRange, () => {
  if (rangePreset.value === 'custom') {
    loadDashboard()
  }
})

onMounted(loadDashboard)
</script>

<template>
  <div v-loading="loading" class="dashboard-page">
    <el-card shadow="never" class="page-card">
      <div class="dashboard-header">
        <div>
          <h2 class="dashboard-title">{{ BRAND_ADMIN_TITLE }} · 数据中心</h2>
          <p class="dashboard-subtitle">
            欢迎，{{ authStore.profile?.name || '管理员' }} · {{ BRAND_NAME }}
          </p>
        </div>
        <div class="dashboard-filters">
          <el-radio-group v-model="rangePreset" size="default">
            <el-radio-button
              v-for="item in rangePresets"
              :key="item.key"
              :label="item.key"
            >
              {{ item.label }}
            </el-radio-button>
          </el-radio-group>
          <el-date-picker
            v-if="rangePreset === 'custom'"
            v-model="customRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="margin-left: 12px; width: 260px"
          />
          <el-button style="margin-left: 12px" @click="loadDashboard">刷新</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="section-header">
          <strong>核心指标</strong>
          <span class="section-hint">环比为上一同等时长</span>
        </div>
      </template>
      <el-row :gutter="12">
        <el-col
          v-for="item in kpiCards"
          :key="item.key"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <div class="kpi-card">
            <div class="kpi-card__label">{{ item.label }}</div>
            <div class="kpi-card__value">{{ item.value }}</div>
            <div class="kpi-card__meta">
              <span>{{ item.hint }}</span>
              <el-tag
                v-if="item.change !== null && item.change !== undefined"
                size="small"
                :type="changeType(item.change)"
                effect="plain"
              >
                {{ formatChange(item.change) }}
              </el-tag>
              <span v-else-if="item.sub" class="kpi-card__sub">{{ item.sub }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="section-header">
              <strong>日趋势</strong>
              <span class="section-hint">订单量按 create_time · GMV 按完成 update_time</span>
            </div>
          </template>
          <VChart class="chart-box" :option="trendOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="section-header">
              <strong>品类结构</strong>
              <span class="section-hint">按 order_type 订单量</span>
            </div>
          </template>
          <VChart class="chart-box" :option="categoryOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="section-header">
              <strong>取件履约</strong>
              <span class="section-hint">状态为快照 · 平均耗时看期内完成</span>
            </div>
          </template>
          <el-row :gutter="12">
            <el-col
              v-for="item in pickupCards"
              :key="item.label"
              :xs="12"
              :sm="8"
            >
              <div class="metric-tile">
                <div class="metric-tile__value">{{ item.value }}</div>
                <div class="metric-tile__label">{{ item.label }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="section-header">
              <strong>质量</strong>
              <span class="section-hint">异常 / 争议 / 评价</span>
            </div>
          </template>
          <el-row :gutter="12">
            <el-col
              v-for="item in qualityCards"
              :key="item.label"
              :xs="12"
              :sm="8"
              :md="6"
            >
              <div class="metric-tile">
                <div class="metric-tile__value">{{ item.value }}</div>
                <div class="metric-tile__label">{{ item.label }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="section-header">
              <strong>订单漏斗</strong>
              <span class="section-hint">期内下单 · 按当前状态</span>
            </div>
          </template>
          <div v-if="overview?.funnel?.length" class="funnel-list">
            <div v-for="item in overview.funnel" :key="item.status" class="funnel-row">
              <div class="funnel-row__label">{{ item.statusLabel }}</div>
              <div class="funnel-row__bar-wrap">
                <div
                  class="funnel-row__bar"
                  :style="{ width: `${(Number(item.count || 0) / funnelMax) * 100}%` }"
                />
              </div>
              <div class="funnel-row__count">{{ item.count ?? 0 }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无订单数据" :image-size="72" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="page-card">
          <template #header>
            <div class="section-header">
              <strong>资金条</strong>
              <span class="section-hint">结算/打款为当前快照</span>
            </div>
          </template>
          <div class="fund-list">
            <div v-for="item in fundItems" :key="item.label" class="fund-item">
              <span class="fund-item__label">{{ item.label }}</span>
              <span class="fund-item__value">{{ item.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="section-header">
          <strong>增长与运营</strong>
          <span class="section-hint">提现待审 / 分销 / 签到福利 / 客服 / 品牌 / 钱包</span>
        </div>
      </template>

      <div class="growth-block">
        <div class="growth-block__title">提现</div>
        <el-row :gutter="12">
          <el-col
            v-for="item in withdrawCards"
            :key="item.label"
            :xs="12"
            :sm="8"
            :md="4"
          >
            <div class="metric-tile">
              <div class="metric-tile__value">{{ item.value }}</div>
              <div class="metric-tile__label">{{ item.label }}</div>
            </div>
          </el-col>
        </el-row>
        <div v-if="growth?.withdraw?.funnel?.length" class="mini-funnel">
          <div
            v-for="item in growth.withdraw.funnel"
            :key="`wd-${item.status}`"
            class="funnel-row"
          >
            <div class="funnel-row__label">{{ item.statusLabel }}</div>
            <div class="funnel-row__bar-wrap">
              <div
                class="funnel-row__bar"
                :style="{ width: `${(Number(item.count || 0) / withdrawFunnelMax) * 100}%` }"
              />
            </div>
            <div class="funnel-row__count">{{ item.count ?? 0 }}</div>
          </div>
        </div>
      </div>

      <el-row :gutter="16" class="growth-grid">
        <el-col :xs="24" :md="12">
          <div class="growth-block">
            <div class="growth-block__title">分销</div>
            <el-row :gutter="12">
              <el-col v-for="item in referralCards" :key="item.label" :span="24">
                <div class="metric-tile metric-tile--row">
                  <div class="metric-tile__label">{{ item.label }}</div>
                  <div class="metric-tile__value">{{ item.value }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-col>
        <el-col :xs="24" :md="12">
          <div class="growth-block">
            <div class="growth-block__title">客服</div>
            <el-row :gutter="12">
              <el-col v-for="item in serviceChatCards" :key="item.label" :span="24">
                <div class="metric-tile metric-tile--row">
                  <div class="metric-tile__label">{{ item.label }}</div>
                  <div class="metric-tile__value">{{ item.value }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="growth-grid">
        <el-col :xs="24" :md="12">
          <div class="growth-block">
            <div class="growth-block__title">签到福利</div>
            <div class="metric-grid metric-grid--2">
              <div v-for="item in welfareCards" :key="item.label" class="metric-tile metric-tile--grid">
                <div class="metric-tile__value">{{ item.value }}</div>
                <div class="metric-tile__label">{{ item.label }}</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :md="12">
          <div class="growth-block">
            <div class="growth-block__title">钱包</div>
            <div class="metric-grid metric-grid--2">
              <div v-for="item in walletCards" :key="item.label" class="metric-tile metric-tile--grid">
                <div class="metric-tile__value">{{ item.value }}</div>
                <div class="metric-tile__label">{{ item.label }}</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="growth-grid">
        <el-col :span="24">
          <div class="growth-block">
            <div class="growth-block__title">
              品牌合作线索
              <span class="section-hint">
                期内新建 {{ growth?.brand?.periodCreatedCount ?? 0 }} · 待跟进
                {{ growth?.brand?.pendingFollowCount ?? 0 }}
              </span>
            </div>
            <div v-if="growth?.brand?.funnel?.length" class="mini-funnel">
              <div
                v-for="item in growth.brand.funnel"
                :key="`bd-${item.status}`"
                class="funnel-row"
              >
                <div class="funnel-row__label">{{ item.statusLabel }}</div>
                <div class="funnel-row__bar-wrap">
                  <div
                    class="funnel-row__bar funnel-row__bar--accent"
                    :style="{ width: `${(Number(item.count || 0) / brandFunnelMax) * 100}%` }"
                  />
                </div>
                <div class="funnel-row__count">{{ item.count ?? 0 }}</div>
              </div>
            </div>
            <el-empty v-else description="暂无线索" :image-size="56" />
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="page-card">
      <template #header><strong>快捷入口</strong></template>
      <el-space wrap>
        <el-button type="primary" @click="goTo('/business/orders')">订单中心</el-button>
        <el-button @click="goTo('/business/pickup-tasks')">取件任务</el-button>
        <el-button @click="goTo('/business/order-exceptions')">异常工单</el-button>
        <el-button @click="goTo('/business/disputes')">争议工单</el-button>
        <el-button @click="goTo('/business/reviews')">评价管理</el-button>
        <el-button @click="goTo('/business/settlements')">结算确认</el-button>
        <el-button @click="goTo('/business/payouts')">打款管理</el-button>
        <el-button @click="goTo('/business/withdraw-records')">提现记录</el-button>
        <el-button @click="goTo('/business/referral-commissions')">分销佣金</el-button>
        <el-button @click="goTo('/business/service-chat')">在线客服</el-button>
        <el-button @click="goTo('/business/brand-cooperation')">品牌合作</el-button>
        <el-button @click="goTo('/business/wallets')">用户钱包</el-button>
        <el-button @click="goTo('/users')">用户中心</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dashboard-header {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.dashboard-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.3;
}

.dashboard-subtitle {
  margin: 6px 0 0;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.dashboard-filters {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.section-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.section-hint {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-weight: 400;
}

.kpi-card {
  margin-bottom: 12px;
  padding: 14px 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  background: var(--el-fill-color-blank);
}

.kpi-card__label {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.kpi-card__value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.2;
  word-break: break-all;
}

.kpi-card__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.kpi-card__sub {
  color: var(--el-text-color-placeholder);
}

.chart-box {
  width: 100%;
  height: 300px;
}

.metric-grid {
  display: grid;
  gap: 12px;
  margin-bottom: 12px;
}

.metric-grid--2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.metric-tile {
  margin-bottom: 12px;
  padding: 12px;
  border: 1px solid var(--el-border-color-extra-light);
  border-radius: 8px;
  text-align: center;
}

.metric-tile--grid {
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 88px;
  aspect-ratio: 1.6 / 1;
}

.metric-tile__value {
  font-size: 22px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  line-height: 1.2;
}

.metric-tile__label {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.metric-tile--row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-align: left;
  gap: 12px;
}

.metric-tile--row .metric-tile__label {
  margin-top: 0;
}

.metric-tile--row .metric-tile__value {
  font-size: 18px;
}

.growth-grid {
  margin-top: 8px;
}

.growth-block {
  margin-bottom: 8px;
  padding: 12px 12px 4px;
  border: 1px solid var(--el-border-color-extra-light);
  border-radius: 8px;
}

.growth-block__title {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 650;
}

.mini-funnel {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin: 4px 0 12px;
}

.funnel-row__bar--accent {
  background: var(--el-color-warning);
}

.funnel-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.funnel-row {
  display: grid;
  grid-template-columns: 96px 1fr 48px;
  gap: 10px;
  align-items: center;
}

.funnel-row__label {
  font-size: 13px;
  color: var(--el-text-color-regular);
}

.funnel-row__bar-wrap {
  height: 10px;
  border-radius: 999px;
  background: var(--el-fill-color);
  overflow: hidden;
}

.funnel-row__bar {
  height: 100%;
  min-width: 0;
  border-radius: 999px;
  background: var(--el-color-primary);
  transition: width 0.25s ease;
}

.funnel-row__count {
  text-align: right;
  font-variant-numeric: tabular-nums;
  font-size: 13px;
}

.fund-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.fund-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-extra-light);
}

.fund-item:last-child {
  border-bottom: none;
}

.fund-item__label {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.fund-item__value {
  font-size: 18px;
  font-weight: 650;
  font-variant-numeric: tabular-nums;
}
</style>
