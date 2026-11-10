import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const brandCooperationApi = {
  page(data = {}) {
    return createPageApi('admin/brand/cooperation').page(data)
  },
  info(id) {
    return post('admin/brand/cooperation/info', { id }).then(resolveBusinessResponse)
  },
  updateStatus(data) {
    return post('admin/brand/cooperation/update', data).then(resolveBusinessResponse)
  },
}
