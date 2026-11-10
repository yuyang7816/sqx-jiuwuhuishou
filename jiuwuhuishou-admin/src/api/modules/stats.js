import { fetchDatacenterOverview } from './datacenter'

/**
 * @deprecated 首页请使用 fetchDatacenterOverview；保留以免旧引用报错。
 */
export async function getDashboardStats() {
  return fetchDatacenterOverview()
}
