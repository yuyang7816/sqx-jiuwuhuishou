import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const settlementApi = createPageApi('admin/settlement')

export function getSettlementDetail(id) {
  return post('admin/settlement/info', { id }).then(resolveBusinessResponse)
}

export function saveSettlementApi(data) {
  return post('admin/settlement/save', data).then(resolveBusinessResponse)
}

export function confirmSettlementApi(data) {
  return post('admin/settlement/confirm', data).then(resolveBusinessResponse)
}
