import { describe, expect, it } from 'vitest'
import {
  calcListIndex,
  mapMenuUrlToPath,
  normalizePermissions,
} from '@/utils/tool'

describe('calcListIndex', () => {
  it('computes continuous index by page', () => {
    expect(calcListIndex(1, 10, 0)).toBe(1)
    expect(calcListIndex(2, 10, 0)).toBe(11)
    expect(calcListIndex(3, 20, 4)).toBe(45)
  })
})

describe('mapMenuUrlToPath', () => {
  it('maps known short urls', () => {
    expect(mapMenuUrlToPath('userList')).toBe('/users')
    expect(mapMenuUrlToPath('serviceChat')).toBe('/business/service-chat')
    expect(mapMenuUrlToPath('serviceConfig')).toBe('/business/service-config')
    expect(mapMenuUrlToPath('configGroups')).toBe('/system/config-groups')
  })

  it('keeps absolute known prefixes', () => {
    expect(mapMenuUrlToPath('/business/orders')).toBe('/business/orders')
  })

  it('returns empty for unknown', () => {
    expect(mapMenuUrlToPath('unknownMenu')).toBe('')
  })
})

describe('normalizePermissions', () => {
  it('parses string array and comma separators', () => {
    expect(normalizePermissions(['a', 'b,c', ''])).toEqual(['a', 'b', 'c'])
  })
})
