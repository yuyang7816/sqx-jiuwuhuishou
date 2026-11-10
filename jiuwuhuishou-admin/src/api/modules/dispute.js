import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const disputeApi = createPageApi('admin/dispute')

export function getDisputeDetail(id) {
  return post('admin/dispute/info', { id }).then(resolveBusinessResponse)
}

export function resolveDisputeApi(data) {
  return post('admin/dispute/resolve', data).then(resolveBusinessResponse)
}
