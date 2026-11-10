import { createPageApi } from '@/api/admin'
import {
  getUserSignRecordPageApi,
  updateUserWelfareCoinApi,
} from '@/api/modules/app-user'

export const userWelfareApi = createPageApi('admin/welfare/user')

export { getUserSignRecordPageApi, updateUserWelfareCoinApi }
