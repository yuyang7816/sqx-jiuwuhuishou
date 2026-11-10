import { createPageApi, resolvePageData } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

const sessionApi = createPageApi('admin/service/session')

export const serviceChatApi = {
  pageSession(data = {}) {
    return sessionApi.page(data)
  },
  sessionInfo(id) {
    return post('admin/service/session/info', { id }).then(resolveBusinessResponse)
  },
  closeSession(id) {
    return post('admin/service/session/close', { id }).then(resolveBusinessResponse)
  },
  pageMessage(data = {}) {
    return post('admin/service/message/page', data)
      .then(resolveBusinessResponse)
      .then(resolvePageData)
  },
  saveMessage(data) {
    return post('admin/service/message/save', data).then(resolveBusinessResponse)
  },
}
