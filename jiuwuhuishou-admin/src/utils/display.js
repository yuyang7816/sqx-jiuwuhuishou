import { getApiBaseUrl } from '@/utils/tool'

export function resolveFileUrl(path) {
  if (!path) return ''
  if (/^https?:\/\//i.test(path)) return path
  const base = getApiBaseUrl().replace(/\/sqx_fast\/?$/, '')
  return `${base}${path.startsWith('/') ? path.slice(1) : path}`
}

export function formatUserLabel(row = {}) {
  const user = row.userEntity || row
  const name = user.userName || row.userName
  const phone = user.phone || row.phone
  if (name && phone) return `${name}（${phone}）`
  return name || phone || '—'
}

export function formatUserName(row = {}) {
  const user = row.userEntity || row
  return user.userName || row.userName || '—'
}

export function formatUserPhone(row = {}) {
  const user = row.userEntity || row
  return user.phone || row.phone || '—'
}

export const withdrawStatusMap = {
  1: { label: '审核中', type: 'warning' },
  2: { label: '成功', type: 'success' },
  3: { label: '已拒绝', type: 'danger' },
}

export const messageTypeMap = {
  1: { label: '系统公告', type: 'primary' },
  2: { label: '用户消息', type: 'success' },
  3: { label: '订单消息', type: 'warning' },
}

export const withdrawAccountTypeMap = {
  1: { label: '微信', type: 'success' },
  2: { label: '支付宝', type: 'primary' },
}

export const withdrawalAccountTypeMap = {
  1: { label: '支付宝', type: 'primary' },
  2: { label: '微信', type: 'success' },
}

export const bannerClassifyMap = {
  1: { label: '首页轮播图', type: 'primary' },
  2: { label: '首页底部配置', type: 'warning' },
  3: { label: '其他配置分类', type: 'info' },
  4: { label: '邀请背景图', type: 'success' },
  5: { label: '首页分类', type: 'danger' },
  6: { label: '首页公告消息', type: 'warning' },
}

export const bannerStateMap = {
  1: { label: '显示', type: 'success' },
  2: { label: '隐藏', type: 'info' },
}

export const referralTypeMap = {
  0: { label: '提现', type: 'warning' },
  1: { label: '订单佣金', type: 'success' },
}

export const directionMap = {
  1: { label: '收入', type: 'success' },
  2: { label: '支出', type: 'danger' },
}

export const readStatusMap = {
  0: { label: '未读', type: 'info' },
  1: { label: '已读', type: 'success' },
}

export const enableStateMap = {
  1: { label: '启用', type: 'success' },
  2: { label: '禁用', type: 'info' },
}

/** 回收规则 rule_type */
export const recycleRuleTypeMap = {
  general: { label: '通用规则', type: 'info' },
  fulfillment: { label: '履约说明', type: 'primary' },
  pricing: { label: '质检与价格', type: 'warning' },
  appliance_science: { label: '家电科普', type: 'success' },
  appliance_faq: { label: '家电常见问题', type: '' },
  appliance_more_pricing: { label: '家电价格说明', type: 'warning' },
  appliance_more_service: { label: '家电服务费用', type: 'danger' },
  appliance_more_tip: { label: '家电温馨提示', type: 'info' },
}

export const appUserTypeMap = {
  0: { label: '未认证', type: 'info' },
  1: { label: '普通用户', type: '' },
  3: { label: '员工', type: 'primary' },
}

/** 用户注册渠道 platform */
export const userPlatformMap = {
  H5: { label: 'H5', type: 'warning' },
  mp: { label: '小程序', type: 'success' },
  app: { label: 'APP', type: 'primary' },
  // 历史值兼容
  APP: { label: 'APP', type: 'primary' },
  JS: { label: '小程序', type: 'success' },
}

export const userPlatformOptions = [
  { label: 'H5', value: 'H5' },
  { label: '小程序', value: 'mp' },
  { label: 'APP', value: 'app' },
]

/** 可编辑/筛选的用户类型（已去掉企业用户） */
export const appUserTypeOptions = [
  { label: '未认证', value: 0 },
  { label: '普通用户', value: 1 },
  { label: '员工', value: 3 },
]

export const orderTypeMap = {
  clothes: { label: '旧衣', type: 'success' },
  appliance: { label: '家电', type: 'warning' },
  digital: { label: '数码', type: 'primary' },
  book: { label: '书籍', type: 'success' },
  luxury: { label: '奢侈品', type: 'danger' },
}

export const fulfillTypeMap = {
  visit: { label: '上门订单', type: 'warning' },
  mail: { label: '邮寄订单', type: 'primary' },
}

export const orderStatusKeyMap = {
  waitAssign: { label: '待分配', type: 'warning' },
  running: { label: '进行中', type: 'primary' },
  pendingConfirm: { label: '待确认', type: 'warning' },
  paying: { label: '打款中', type: 'warning' },
  done: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'info' },
  exception: { label: '异常处理', type: 'danger' },
  dispute: { label: '争议处理', type: 'danger' },
}

export const pickupStatusKeyMap = {
  pending: { label: '待取件', type: 'warning' },
  running: { label: '取件中', type: 'primary' },
  done: { label: '已完成', type: 'success' },
}

export const settlementStatusMap = {
  PENDING: { label: '待确认', type: 'warning' },
  CONFIRMED: { label: '已确认', type: 'success' },
}

export const payoutStatusMap = {
  PENDING: { label: '待打款', type: 'info' },
  PROCESSING: { label: '打款中', type: 'warning' },
  SUCCESS: { label: '已到账', type: 'success' },
}

/** 打款渠道 payChannel */
export const payoutChannelMap = {
  WALLET: { label: '用户钱包', type: 'success' },
  WECHAT: { label: '微信', type: 'success' },
  ALIPAY: { label: '支付宝', type: 'primary' },
  BANK: { label: '银行卡', type: 'info' },
}

export const disputeStatusMap = {
  PENDING: { label: '待处理', type: 'warning' },
  RESOLVED: { label: '已裁决', type: 'success' },
}

export const brandCooperationStatusMap = {
  0: { label: '待跟进', type: 'warning' },
  1: { label: '已联系', type: 'primary' },
  2: { label: '已合作', type: 'success' },
  3: { label: '已拒绝', type: 'info' },
}

export const serviceSessionStatusMap = {
  1: { label: '进行中', type: 'success' },
  2: { label: '已关闭', type: 'info' },
}

export function tagMeta(map, value, fallback = '—') {
  const item = map[value]
  if (!item) return { label: value || fallback, type: 'info' }
  return item
}
