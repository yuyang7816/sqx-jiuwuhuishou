import { post, resolveBusinessResponse } from '@/api/http'

/**
 * 数据中心概览（P0）
 * @param {{ startTime?: string, endTime?: string, orderType?: string }} payload
 */
export function fetchDatacenterOverview(payload = {}) {
  return post('admin/datacenter/overview', payload).then(resolveBusinessResponse)
}

/**
 * 日趋势（P1）
 */
export function fetchDatacenterTrend(payload = {}) {
  return post('admin/datacenter/trend', payload).then(resolveBusinessResponse)
}

/**
 * 品类/履约/质量拆解（P1）
 */
export function fetchDatacenterBreakdown(payload = {}) {
  return post('admin/datacenter/breakdown', payload).then(resolveBusinessResponse)
}

/**
 * 增长与运营（P2：提现/分销/福利/客服/品牌/钱包）
 */
export function fetchDatacenterGrowth(payload = {}) {
  return post('admin/datacenter/growth', payload).then(resolveBusinessResponse)
}
