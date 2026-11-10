import { get, post, resolveBusinessResponse } from '@/api/http'
import { resolvePageData } from '@/api/admin'

export function getAppUserPage(params = {}) {
  return post('admin/base/user/list', params)
    .then(resolveBusinessResponse)
    .then(resolvePageData)
}

export function getAppUserInfo(userId) {
  return get('admin/base/user/info', { userId }).then(resolveBusinessResponse)
}

export function updateAppUserApi(data) {
  return post('admin/base/user/update', data).then(resolveBusinessResponse)
}

export function deleteAppUserApi(data) {
  return post('admin/base/user/delete', data).then(resolveBusinessResponse)
}

export function getUserAddressListApi(userId) {
  return post('admin/base/user/detail/address/list', { userId }).then(resolveBusinessResponse)
}

export function getUserWelfareInfoApi(userId) {
  return post('admin/base/user/detail/welfare/info', { userId }).then(resolveBusinessResponse)
}

export function updateUserWelfareCoinApi(data) {
  return post('admin/base/user/detail/welfare/updateCoin', data).then(resolveBusinessResponse)
}

export function getUserSignRecordPageApi(params = {}) {
  return post('admin/base/user/detail/signRecord/page', params)
    .then(resolveBusinessResponse)
    .then(resolvePageData)
}

export function getReferralUserCommissionInfoApi(userId) {
  return post(`admin/referral/userCommission/info?userId=${userId}`).then(resolveBusinessResponse)
}

export function updateReferralUserCommissionApi(data) {
  return post('admin/referral/userCommission/update', data).then(resolveBusinessResponse)
}
