import { describe, expect, it } from 'vitest'
import { formatUserLabel, serviceSessionStatusMap, tagMeta } from '@/utils/display'

describe('formatUserLabel', () => {
  it('formats name with phone', () => {
    expect(formatUserLabel({ userName: '张三', phone: '13800138000' })).toBe('张三（13800138000）')
  })

  it('falls back to name or phone or dash', () => {
    expect(formatUserLabel({ userName: '李四' })).toBe('李四')
    expect(formatUserLabel({ phone: '139' })).toBe('139')
    expect(formatUserLabel({})).toBe('—')
  })

  it('reads nested userEntity', () => {
    expect(formatUserLabel({ userEntity: { userName: '王五', phone: '137' } })).toBe('王五（137）')
  })
})

describe('tagMeta', () => {
  it('returns mapped label/type', () => {
    expect(tagMeta(serviceSessionStatusMap, 1)).toEqual({ label: '进行中', type: 'success' })
  })

  it('falls back for unknown value', () => {
    expect(tagMeta(serviceSessionStatusMap, 9)).toEqual({ label: 9, type: 'info' })
  })
})
