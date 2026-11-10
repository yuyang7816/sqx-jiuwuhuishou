import { beforeEach, describe, expect, it, vi } from 'vitest'
import { post } from '@/api/http'
import {
  assignPickupApi,
  cancelOrderApi,
  completeOrderApi,
  confirmOrderPayoutApi,
  getOrderDetail,
  getOrderExpressTracks,
  handleOrderApi,
  orderApi,
  updateOrderEstimateApi,
  updateOrderExpressApi,
  updateOrderStatusApi,
} from '@/api/modules/order'

describe('orderApi', () => {
  beforeEach(() => {
    vi.mocked(post).mockClear()
    vi.mocked(post).mockResolvedValue({})
  })

  it('exposes CRUD page methods under admin/order', () => {
    expect(typeof orderApi.page).toBe('function')
    expect(typeof orderApi.info).toBe('function')
    expect(typeof orderApi.save).toBe('function')
    expect(typeof orderApi.update).toBe('function')
    expect(typeof orderApi.delete).toBe('function')
  })

  it('getOrderDetail posts info with id', async () => {
    await getOrderDetail(17)
    expect(post).toHaveBeenCalledWith('admin/order/info', { id: 17 })
  })

  it('cancelOrderApi posts cancel payload', async () => {
    await cancelOrderApi({ id: 1, reason: '用户取消' })
    expect(post).toHaveBeenCalledWith('admin/order/cancel', { id: 1, reason: '用户取消' })
  })

  it('updateOrderStatusApi posts status payload', async () => {
    await updateOrderStatusApi({ id: 2, status: 3 })
    expect(post).toHaveBeenCalledWith('admin/order/updateStatus', { id: 2, status: 3 })
  })

  it('assignPickupApi posts assign payload', async () => {
    await assignPickupApi({ id: 3, staffId: 8 })
    expect(post).toHaveBeenCalledWith('admin/order/assignPickup', { id: 3, staffId: 8 })
  })

  it('updateOrderEstimateApi posts actual amount payload', async () => {
    await updateOrderEstimateApi({ id: 4, actualAmount: 120 })
    expect(post).toHaveBeenCalledWith('admin/order/updateEstimate', { id: 4, actualAmount: 120 })
  })

  it('confirmOrderPayoutApi / completeOrderApi / handleOrderApi post actions', async () => {
    await confirmOrderPayoutApi({ id: 5 })
    await completeOrderApi({ id: 6 })
    await handleOrderApi({ id: 7, action: 'note' })
    expect(post).toHaveBeenCalledWith('admin/order/confirmPayout', { id: 5 })
    expect(post).toHaveBeenCalledWith('admin/order/complete', { id: 6 })
    expect(post).toHaveBeenCalledWith('admin/order/handle', { id: 7, action: 'note' })
  })

  it('getOrderExpressTracks posts express tracks with id', async () => {
    await getOrderExpressTracks(19)
    expect(post).toHaveBeenCalledWith('admin/order/express/tracks', { id: 19 })
  })

  it('updateOrderExpressApi posts express no payload', async () => {
    await updateOrderExpressApi({ id: 20, expressNo: 'SF1234567890' })
    expect(post).toHaveBeenCalledWith('admin/order/updateExpressNo', { id: 20, expressNo: 'SF1234567890' })
  })
})
