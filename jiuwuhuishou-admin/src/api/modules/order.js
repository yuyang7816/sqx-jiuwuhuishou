import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const orderApi = createPageApi('admin/order')

export function getOrderDetail(id) {
  return post('admin/order/info', { id }).then(resolveBusinessResponse)
}

export function cancelOrderApi(data) {
  return post('admin/order/cancel', data).then(resolveBusinessResponse)
}

export function updateOrderStatusApi(data) {
  return post('admin/order/updateStatus', data).then(resolveBusinessResponse)
}

export function assignPickupApi(data) {
  return post('admin/order/assignPickup', data).then(resolveBusinessResponse)
}

export function updateOrderEstimateApi(data) {
  return post('admin/order/updateEstimate', data).then(resolveBusinessResponse)
}

export function confirmOrderPayoutApi(data) {
  return post('admin/order/confirmPayout', data).then(resolveBusinessResponse)
}

export function completeOrderApi(data) {
  return post('admin/order/complete', data).then(resolveBusinessResponse)
}

export function handleOrderApi(data) {
  return post('admin/order/handle', data).then(resolveBusinessResponse)
}

export function getOrderExpressTracks(id) {
  return post('admin/order/express/tracks', { id }).then(resolveBusinessResponse)
}

export function updateOrderExpressApi(data) {
  return post('admin/order/updateExpressNo', data).then(resolveBusinessResponse)
}

export function confirmMailReceiveApi(data) {
  return post('admin/order/confirmMailReceive', data).then(resolveBusinessResponse)
}
