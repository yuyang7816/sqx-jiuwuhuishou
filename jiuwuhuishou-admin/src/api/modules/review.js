import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const reviewApi = createPageApi('admin/review')

export function getReviewDetail(id) {
  return post('admin/review/info', { id }).then(resolveBusinessResponse)
}

export function replyReviewApi(data) {
  return post('admin/review/reply', data).then(resolveBusinessResponse)
}
