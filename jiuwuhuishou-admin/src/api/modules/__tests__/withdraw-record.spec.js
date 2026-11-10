import { beforeEach, describe, expect, it, vi } from 'vitest'
import { post } from '@/api/http'
import { confirmWithdrawRecordApi, withdrawRecordApi } from '@/api/modules/withdraw-record'

describe('withdrawRecordApi', () => {
  beforeEach(() => {
    vi.mocked(post).mockClear()
    vi.mocked(post).mockResolvedValue({})
  })

  it('exposes CRUD page methods under admin/withdrawRecord', () => {
    expect(typeof withdrawRecordApi.page).toBe('function')
    expect(typeof withdrawRecordApi.info).toBe('function')
    expect(typeof withdrawRecordApi.save).toBe('function')
    expect(typeof withdrawRecordApi.update).toBe('function')
    expect(typeof withdrawRecordApi.delete).toBe('function')
  })

  it('confirmWithdrawRecordApi posts confirm payload', async () => {
    await confirmWithdrawRecordApi({ id: 9, status: 1, remark: 'ok' })
    expect(post).toHaveBeenCalledWith('admin/withdrawRecord/confirm', {
      id: 9,
      status: 1,
      remark: 'ok',
    })
  })
})
