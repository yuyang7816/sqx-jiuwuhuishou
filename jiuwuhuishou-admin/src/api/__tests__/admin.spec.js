import { describe, expect, it } from 'vitest'
import { createPageApi, resolvePageData } from '@/api/admin'

describe('resolvePageData', () => {
  it('parses records + total', () => {
    expect(resolvePageData({ records: [{ id: 1 }], total: 3 })).toEqual({
      list: [{ id: 1 }],
      total: 3,
    })
  })

  it('parses nested page + totalCount', () => {
    expect(resolvePageData({ page: { list: [{ id: 2 }], totalCount: '8' } })).toEqual({
      list: [{ id: 2 }],
      total: 8,
    })
  })

  it('returns empty defaults for blank input', () => {
    expect(resolvePageData(null)).toEqual({ list: [], total: 0 })
    expect(resolvePageData(undefined)).toEqual({ list: [], total: 0 })
  })
})

describe('createPageApi', () => {
  it('exposes page/info/save/update/delete methods', () => {
    const api = createPageApi('admin/demo')
    expect(typeof api.page).toBe('function')
    expect(typeof api.info).toBe('function')
    expect(typeof api.save).toBe('function')
    expect(typeof api.update).toBe('function')
    expect(typeof api.delete).toBe('function')
  })
})
