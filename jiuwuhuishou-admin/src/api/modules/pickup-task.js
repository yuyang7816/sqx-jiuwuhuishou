import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const pickupTaskApi = createPageApi('admin/pickup/task')

export function getPickupTaskDetail(id) {
  return post('admin/pickup/task/info', { id }).then(resolveBusinessResponse)
}

export function assignPickupTaskApi(data) {
  return post('admin/pickup/task/assign', data).then(resolveBusinessResponse)
}

export function reassignPickupTaskApi(data) {
  return post('admin/pickup/task/reassign', data).then(resolveBusinessResponse)
}

export function finishPickupTaskApi(data) {
  return post('admin/pickup/task/finish', data).then(resolveBusinessResponse)
}
