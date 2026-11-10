import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const payoutApi = createPageApi('admin/payout')

export function getPayoutDetail(id) {
  return post('admin/payout/info', { id }).then(resolveBusinessResponse)
}

export function createPayoutApi(data) {
  return post('admin/payout/create', data).then(resolveBusinessResponse)
}

export function confirmPayoutApi(data) {
  return post('admin/payout/confirm', data).then(resolveBusinessResponse)
}
