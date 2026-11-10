import { describe, expect, it, vi } from 'vitest'
import { useCrudPage } from '@/composables/useCrudPage'

vi.mock('@/utils/modal', () => ({
  confirmAction: vi.fn(async () => true),
  showError: vi.fn(),
  showSuccess: vi.fn(),
}))

describe('useCrudPage', () => {
  it('loads page data into table state', async () => {
    const api = {
      page: vi.fn(async () => ({ list: [{ id: 1, name: 'a' }], total: 1 })),
    }
    const { loadData, tableData, total, loading } = useCrudPage(api)
    await loadData({ keyword: 'a' })
    expect(api.page).toHaveBeenCalledWith({ page: 1, limit: 10, keyword: 'a' })
    expect(tableData.value).toEqual([{ id: 1, name: 'a' }])
    expect(total.value).toBe(1)
    expect(loading.value).toBe(false)
  })

  it('clears lastQuery when loadData({}) is called (reset)', async () => {
    const api = {
      page: vi.fn(async () => ({ list: [], total: 0 })),
    }
    const { loadData, pageNum } = useCrudPage(api)
    await loadData({ status: 1 })
    expect(api.page).toHaveBeenLastCalledWith({ page: 1, limit: 10, status: 1 })

    pageNum.value = 3
    await loadData({})
    expect(pageNum.value).toBe(1)
    expect(api.page).toHaveBeenLastCalledWith({ page: 1, limit: 10 })

    await loadData()
    expect(api.page).toHaveBeenLastCalledWith({ page: 1, limit: 10 })
  })

  it('openCreate resets editing state', () => {
    const api = { page: vi.fn() }
    const { openCreate, editingId, dialogVisible, form } = useCrudPage(api)
    openCreate({ title: 'demo' })
    expect(editingId.value).toBeNull()
    expect(dialogVisible.value).toBe(true)
    expect(form.title).toBe('demo')
  })

  it('removeRow deletes and reloads', async () => {
    const api = {
      page: vi.fn(async () => ({ list: [], total: 0 })),
      delete: vi.fn(async () => ({})),
    }
    const { removeRow } = useCrudPage(api)
    await removeRow({ id: 5 })
    expect(api.delete).toHaveBeenCalledWith({ id: 5 })
    expect(api.page).toHaveBeenCalled()
  })

  it('openEdit closes dialog on info error', async () => {
    const { showError } = await import('@/utils/modal')
    const api = {
      page: vi.fn(async () => ({ list: [], total: 0 })),
      info: vi.fn(async () => {
        throw { msg: '详情失败' }
      }),
    }
    const { openEdit, dialogVisible } = useCrudPage(api)
    await openEdit({ id: 3 })
    expect(showError).toHaveBeenCalledWith('详情失败')
    expect(dialogVisible.value).toBe(false)
  })
})