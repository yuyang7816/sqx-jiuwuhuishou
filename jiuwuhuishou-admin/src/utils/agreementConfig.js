/** 系统配置 · 协议类键与富文本编解码 */

export const AGREEMENT_PARAM_KEYS = new Set([
  'user_service_agreement',
  'user_privacy_agreement',
  'user_about_us',
  'referral_rules_json',
])

export function isAgreementConfigKey(paramKey = '') {
  return AGREEMENT_PARAM_KEYS.has(String(paramKey || ''))
}

/** 是否按协议富文本编辑（协议分类下全部配置） */
export function isAgreementRichTextRow(row = {}, isAgreementTab = false) {
  if (isAgreementTab) return true
  const type = String(row.paramType || '')
  const desc = String(row.paramTypeDesc || '')
  if (type === 'agreement' || desc === '协议配置') return true
  return isAgreementConfigKey(row.paramKey)
}

function escapeHtml(text = '') {
  return String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

export function sectionsToHtml(sections = []) {
  if (!Array.isArray(sections) || !sections.length) return ''
  return sections
    .map((section) => {
      const parts = []
      if (section?.title) {
        parts.push(`<h3>${escapeHtml(section.title)}</h3>`)
      }
      if (section?.text) {
        parts.push(`<p>${escapeHtml(section.text)}</p>`)
      }
      if (Array.isArray(section?.bullets) && section.bullets.length) {
        const items = section.bullets
          .filter(Boolean)
          .map((b) => `<li>${escapeHtml(b)}</li>`)
          .join('')
        if (items) parts.push(`<ul>${items}</ul>`)
      }
      return parts.join('')
    })
    .filter(Boolean)
    .join('')
}

/** 旧版分佣规则结构化 JSON → HTML */
export function referralRulesObjectToHtml(parsed = {}) {
  const parts = []
  if (parsed.intro) {
    parts.push(`<p>${escapeHtml(parsed.intro)}</p>`)
  }
  if (Array.isArray(parsed.levels) && parsed.levels.length) {
    parts.push('<h3>分佣比例说明</h3>')
    parsed.levels.forEach((item) => {
      const rate = item?.rate ? `（${escapeHtml(item.rate)}）` : ''
      if (item?.title) {
        parts.push(`<h4>${escapeHtml(item.title)}${rate}</h4>`)
      }
      if (item?.desc) {
        parts.push(`<p>${escapeHtml(item.desc)}</p>`)
      }
    })
  }
  if (parsed.example) {
    parts.push(`<p>${escapeHtml(parsed.example)}</p>`)
  }
  if (Array.isArray(parsed.settles) && parsed.settles.length) {
    parts.push('<h3>提现与结算</h3>')
    parsed.settles.forEach((item) => {
      if (item?.title) parts.push(`<h4>${escapeHtml(item.title)}</h4>`)
      if (item?.desc) parts.push(`<p>${escapeHtml(item.desc)}</p>`)
    })
  }
  if (Array.isArray(parsed.faqs) && parsed.faqs.length) {
    parts.push('<h3>合规条款</h3>')
    parsed.faqs.forEach((item) => {
      if (item?.q) parts.push(`<h4>${escapeHtml(item.q)}</h4>`)
      if (item?.a) parts.push(`<p>${escapeHtml(item.a)}</p>`)
    })
  }
  if (parsed.footerArt?.desc) {
    parts.push(`<p>${escapeHtml(parsed.footerArt.desc)}</p>`)
  }
  return parts.join('')
}

function isLegacyReferralRulesObject(parsed) {
  if (!parsed || typeof parsed !== 'object') return false
  return Boolean(
    parsed.intro
    || parsed.levels
    || parsed.example
    || parsed.settles
    || parsed.faqs
    || parsed.footerArt,
  )
}

/**
 * 解析协议配置值为编辑表单：{ title, html }
 * 兼容旧 sections JSON / 新 html JSON / 旧分佣规则结构 / 纯 HTML 字符串
 */
export function parseAgreementValue(raw, fallbackTitle = '协议') {
  const text = String(raw ?? '').trim()
  if (!text) {
    return { title: fallbackTitle, html: '' }
  }
  try {
    const parsed = JSON.parse(text)
    if (parsed && typeof parsed === 'object') {
      const title = parsed.title || fallbackTitle
      if (typeof parsed.html === 'string' && parsed.html.trim()) {
        return { title, html: parsed.html }
      }
      if (Array.isArray(parsed.sections)) {
        return { title, html: sectionsToHtml(parsed.sections) }
      }
      if (typeof parsed.content === 'string') {
        return { title, html: parsed.content }
      }
      if (isLegacyReferralRulesObject(parsed)) {
        return { title, html: referralRulesObjectToHtml(parsed) }
      }
    }
  } catch {
    // 非 JSON，当作纯 HTML
  }
  if (text.startsWith('<')) {
    return { title: fallbackTitle, html: text }
  }
  return { title: fallbackTitle, html: `<p>${escapeHtml(text)}</p>` }
}

export function serializeAgreementValue(title, html) {
  return JSON.stringify({
    title: String(title || '').trim() || '协议',
    html: String(html || ''),
  })
}
