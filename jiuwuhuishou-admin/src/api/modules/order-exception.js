import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const orderExceptionApi = createPageApi('admin/order/exception')

export function getOrderExceptionPage(params = {}) {
  return orderExceptionApi.page(params)
}

export function getOrderExceptionInfo(id) {
  return post('admin/order/exception/info', { id }).then(resolveBusinessResponse)
}

export function handleOrderExceptionApi(data) {
  return post('admin/order/exception/handle', data).then(resolveBusinessResponse)
}
