import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const orderRescheduleApi = createPageApi('admin/order/reschedule')

export function getOrderReschedulePage(params = {}) {
  return orderRescheduleApi.page(params)
}

export function getOrderRescheduleInfo(id) {
  return post('admin/order/reschedule/info', { id }).then(resolveBusinessResponse)
}
