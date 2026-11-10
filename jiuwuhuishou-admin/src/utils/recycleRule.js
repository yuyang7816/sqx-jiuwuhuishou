/** 已知使用 JSON content 的规则类型 */
export const JSON_RULE_TYPES = ['appliance_more_pricing', 'appliance_more_service']

export function looksLikeJson(value) {
  const text = String(value || '').trim()
  return (text.startsWith('{') && text.endsWith('}')) || (text.startsWith('[') && text.endsWith(']'))
}

export function parseJsonSafe(value, fallback = null) {
  if (value == null || value === '') return fallback
  if (typeof value === 'object') return value
  try {
    return JSON.parse(value)
  } catch {
    return fallback
  }
}

export function isJsonRuleType(ruleType) {
  return JSON_RULE_TYPES.includes(ruleType)
}

/** 判断是否为图片路径/URL（而非 uview 图标名） */
export function isImagePath(value = '') {
  const text = String(value || '').trim()
  if (!text) return false
  return /^https?:\/\//i.test(text)
    || text.startsWith('/')
    || /\.(png|jpe?g|gif|webp|svg)(\?|$)/i.test(text)
}

export function resolveRuleImageUrl(meta = {}) {
  if (meta.imageUrl) return String(meta.imageUrl).trim()
  if (isImagePath(meta.icon)) return String(meta.icon).trim()
  return ''
}

export function defaultContentByType(ruleType) {
  if (ruleType === 'appliance_more_pricing') {
    return JSON.stringify({
      icon: 'home',
      imageUrl: '',
      top: '',
      items: [{ name: '', price: '' }],
    })
  }
  if (ruleType === 'appliance_more_service') {
    return JSON.stringify({
      icon: 'map',
      imageUrl: '',
      theme: 'gold',
      desc: '',
      lines: [''],
      tags: [],
    })
  }
  return ''
}

export function parsePricingContent(content) {
  const parsed = parseJsonSafe(content, {}) || {}
  return {
    icon: isImagePath(parsed.icon) ? 'home' : (parsed.icon || 'home'),
    imageUrl: resolveRuleImageUrl(parsed),
    top: parsed.top || '',
    items: Array.isArray(parsed.items) && parsed.items.length
      ? parsed.items.map((item) => ({
        name: item?.name || '',
        price: item?.price || '',
      }))
      : [{ name: '', price: '' }],
  }
}

export function serializePricingContent(model) {
  return JSON.stringify({
    icon: model.icon || 'home',
    imageUrl: model.imageUrl || '',
    top: model.top || '',
    items: (model.items || []).map((item) => ({
      name: item?.name || '',
      price: item?.price || '',
    })),
  })
}

export function parseServiceContent(content) {
  const parsed = parseJsonSafe(content, {}) || {}
  return {
    icon: isImagePath(parsed.icon) ? 'map' : (parsed.icon || 'map'),
    imageUrl: resolveRuleImageUrl(parsed),
    theme: parsed.theme || 'gold',
    desc: parsed.desc || '',
    lines: Array.isArray(parsed.lines) && parsed.lines.length
      ? parsed.lines.map((line) => String(line || ''))
      : [''],
    tags: Array.isArray(parsed.tags)
      ? parsed.tags.map((item) => ({
        name: item?.name || '',
        price: item?.price || '',
      }))
      : [],
  }
}

export function serializeServiceContent(model) {
  return JSON.stringify({
    icon: model.icon || 'map',
    imageUrl: model.imageUrl || '',
    theme: model.theme || 'gold',
    desc: model.desc || '',
    lines: (model.lines || []).map((line) => String(line || '')),
    tags: (model.tags || []).map((item) => ({
      name: item?.name || '',
      price: item?.price || '',
    })),
  })
}

/** 列表展示：普通文本原样；JSON 解析后摘要展示 */
export function formatRuleContentPreview(row = {}) {
  const content = row.content
  if (content == null || content === '') return '—'

  const ruleType = row.ruleType
  const parsed = looksLikeJson(content) ? parseJsonSafe(content) : null

  if (ruleType === 'appliance_more_pricing' || (parsed && Array.isArray(parsed.items))) {
    const meta = parsePricingContent(content)
    const lines = []
    if (meta.top) lines.push(`起步价：${meta.top}`)
    if (meta.imageUrl) lines.push('图标：已上传')
    const items = (meta.items || [])
      .filter((item) => item.name || item.price)
      .map((item) => `· ${item.name || '—'}${item.price ? `  ${item.price}` : ''}`)
    if (items.length) lines.push(...items)
    return lines.join('\n') || '—'
  }

  if (
    ruleType === 'appliance_more_service'
    || (parsed && (parsed.desc != null || Array.isArray(parsed.lines) || Array.isArray(parsed.tags)))
  ) {
    const meta = parseServiceContent(content)
    const lines = []
    if (meta.desc) lines.push(meta.desc)
    if (meta.imageUrl) lines.push('图标：已上传')
    if (meta.theme) lines.push(`主题：${meta.theme}`)
    ;(meta.lines || [])
      .filter(Boolean)
      .forEach((line) => lines.push(`· ${line}`))
    ;(meta.tags || [])
      .filter((item) => item.name || item.price)
      .forEach((item) => lines.push(`· ${item.name || '—'}${item.price ? `  ${item.price}` : ''}`))
    return lines.join('\n') || '—'
  }

  if (parsed) {
    try {
      return JSON.stringify(parsed, null, 2)
    } catch {
      return String(content)
    }
  }

  return String(content)
}
