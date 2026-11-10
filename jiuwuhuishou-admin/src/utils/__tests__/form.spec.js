import { describe, expect, it } from 'vitest'
import {
  buildPageParams,
  cloneData,
  patchFormModel,
  resetFormModel,
} from '@/utils/form'

describe('form utils', () => {
  it('cloneData deep clones plain objects', () => {
    const source = { a: 1, nested: { b: 2 } }
    const cloned = cloneData(source)
    cloned.nested.b = 9
    expect(source.nested.b).toBe(2)
  })

  it('resetFormModel restores initial values', () => {
    const form = { name: 'old', age: 18 }
    resetFormModel(form, { name: '', age: 0 })
    expect(form).toEqual({ name: '', age: 0 })
  })

  it('patchFormModel merges payload over initial', () => {
    const form = { name: '', age: 0 }
    patchFormModel(form, { name: 'Alice' }, { name: '', age: 0 })
    expect(form).toEqual({ name: 'Alice', age: 0 })
  })

  it('buildPageParams combines filters and pagination', () => {
    expect(buildPageParams({ keyword: 'abc' }, 2, 20)).toEqual({
      keyword: 'abc',
      pageNum: 2,
      pageSize: 20,
    })
  })
})
