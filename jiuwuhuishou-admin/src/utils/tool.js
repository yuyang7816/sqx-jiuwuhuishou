export function getUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const random = (Math.random() * 16) | 0
    const value = c === 'x' ? random : (random & 0x3) | 0x8
    return value.toString(16)
  })
}

export function getApiBaseUrl() {
  // 优先读 .env.development；默认本地 9135
  // const baseUrl = 'http://192.168.0.128:9135/sqx_fast/'
  const baseUrl = 'https://jwhsadmin.xianmxkj.com/sqx_fast/'
  // 线上：'https://jwhsadmin.xianmxkj.com/sqx_fast/'
  return baseUrl.endsWith('/') ? baseUrl : `${baseUrl}/`
}

export function getCaptchaUrl(uuid) {
  return `${getApiBaseUrl()}captcha.jpg?uuid=${uuid}&_t=${Date.now()}`
}

export function normalizePermissions(raw) {
  if (!raw) return []
  if (Array.isArray(raw)) {
    return raw
      .filter(Boolean)
      .flatMap((item) => String(item).split(','))
      .map((item) => item.trim())
      .filter(Boolean)
  }
  if (typeof raw === 'object') {
    return Object.values(raw)
      .filter((item) => typeof item === 'string')
      .flatMap((item) => item.split(','))
      .map((item) => item.trim())
      .filter(Boolean)
  }
  return []
}

export function isAuth(key) {
  const permissions = normalizePermissions(JSON.parse(window.sessionStorage.getItem('permissions') || '[]'))
  if (permissions.includes(key)) return true
  // 兼容旧菜单权限标识
  const legacyMap = {
    'sys:config:list': 'allocationList:list',
    'sys:config:update': 'allocationList:update',
    'sys:config:save': 'allocationList:update',
    'sys:config:delete': 'allocationList:update',
    'sys:user:list': 'sys:user:list',
  }
  const legacy = legacyMap[key]
  if (legacy && permissions.includes(legacy)) return true
  return permissions.some((item) => item.endsWith(':*') && key.startsWith(item.slice(0, -1)))
}

export function setPermissions(permissions = []) {
  window.sessionStorage.setItem('permissions', JSON.stringify(normalizePermissions(permissions)))
}

export function clearPermissions() {
  window.sessionStorage.removeItem('permissions')
}

export function clearAuthCache() {
  window.sessionStorage.removeItem('admin-auth')
  clearPermissions()
}

const routePathMap = {
  home: '/dashboard',
  dashboard: '/dashboard',
  user: '/system/admins',
  role: '/system/roles',
  menu: '/system/menus',
  dict: '/system/dicts',
  log: '/system/logs',
  permission: '/system/permissions',
  perms: '/system/permissions',
  'sys/user': '/system/admins',
  'modules/sys/user': '/system/admins',
  'modules/sys/user.html': '/system/admins',
  'system/admins': '/system/admins',
  'sys/role': '/system/roles',
  'modules/sys/role': '/system/roles',
  'modules/sys/role.html': '/system/roles',
  'system/roles': '/system/roles',
  'sys/menu': '/system/menus',
  'modules/sys/menu': '/system/menus',
  'modules/sys/menu.html': '/system/menus',
  'system/menus': '/system/menus',
  'sys/dict': '/system/dicts',
  'modules/sys/dict': '/system/dicts',
  'modules/sys/dict.html': '/system/dicts',
  'system/dicts': '/system/dicts',
  'sys/config': '/system/configs',
  'modules/sys/config': '/system/configs',
  'modules/sys/config.html': '/system/configs',
  'system/configs': '/system/configs',
  'sys/log': '/system/logs',
  'modules/sys/log': '/system/logs',
  'modules/sys/log.html': '/system/logs',
  'system/logs': '/system/logs',
  'sys/permission': '/system/permissions',
  'modules/sys/permission': '/system/permissions',
  'system/permissions': '/system/permissions',
  userList: '/users',
  userAddress: '/business/addresses',
  addresses: '/business/addresses',
  bannerList: '/business/banners',
  message: '/business/messages',
  messageList: '/business/messages',
  userWallet: '/business/wallets',
  walletTransaction: '/business/wallet-transactions',
  referralCommission: '/business/referral-commissions',
  referralUserCommission: '/business/referral-user-commissions',
  withdrawRecord: '/business/withdraw-records',
  withdrawalAccount: '/business/withdrawal-accounts',
  smsTemplate: '/business/sms-templates',
  allocationList: '/system/configs',
  common: '/system/configs',
  configGroups: '/system/config-groups',
  'config-groups': '/system/config-groups',
  'system/config-groups': '/system/config-groups',
  orderList: '/business/orders',
  orders: '/business/orders',
  orderReschedule: '/business/order-reschedules',
  orderReschedules: '/business/order-reschedules',
  orderException: '/business/order-exceptions',
  orderExceptions: '/business/order-exceptions',
  pickupTask: '/business/pickup-tasks',
  pickupTasks: '/business/pickup-tasks',
  settlement: '/business/settlements',
  settlements: '/business/settlements',
  payout: '/business/payouts',
  payouts: '/business/payouts',
  dispute: '/business/disputes',
  disputes: '/business/disputes',
  review: '/business/reviews',
  reviews: '/business/reviews',
  recycleCategory: '/business/recycle/categories',
  productCategory: '/business/recycle/product-categories',
  'recycle/category': '/business/recycle/categories',
  'product/category': '/business/recycle/product-categories',
  serviceArea: '/business/recycle/service-areas',
  'recycle/serviceArea': '/business/recycle/service-areas',
  recycleRule: '/business/recycle/rules',
  'recycle/rule': '/business/recycle/rules',
  bookWeightOption: '/business/recycle/book-weight-options',
  'book/weightOption': '/business/recycle/book-weight-options',
  'recycle/bookWeightOption': '/business/recycle/book-weight-options',
  helpCategory: '/business/help/categories',
  'help/category': '/business/help/categories',
  helpArticle: '/business/help/categories?tab=articles',
  'help/article': '/business/help/categories?tab=articles',
  helpCenter: '/business/help/categories',
  'help/center': '/business/help/categories',
  brandCooperation: '/business/brand-cooperation',
  'brand/cooperation': '/business/brand-cooperation',
  welfareTask: '/business/welfare-tasks',
  'welfare/task': '/business/welfare-tasks',
  userWelfare: '/business/user-welfare',
  'welfare/user': '/business/user-welfare',
  serviceChat: '/business/service-chat',
  'service/chat': '/business/service-chat',
  service: '/business/service-chat',
  serviceConfig: '/business/service-config',
  'service/config': '/business/service-config',
}

export function mapMenuUrlToPath(url) {
  const raw = String(url || '').trim()
  if (!raw) return ''

  if (raw.startsWith('/')) {
    const pathOnly = raw.split('?')[0]
    const knownPrefixes = ['/dashboard', '/system/', '/business/', '/users']
    if (knownPrefixes.some((prefix) => pathOnly === prefix || pathOnly.startsWith(prefix))) {
      return pathOnly
    }
  }

  const normalized = raw
    .replace(/^modules\//, '')
    .replace(/\.html$/, '')
    .replace(/^\//, '')

  return routePathMap[normalized] || routePathMap[raw] || ''
}

export function calcListIndex(pageNum = 1, pageSize = 10, index = 0) {
  return (Number(pageNum) - 1) * Number(pageSize) + Number(index) + 1
}

export function collectMenuPaths(menuList = []) {
  return menuList.flatMap((item) => {
    const current = item.path ? [item.path.split('?')[0]] : []
    return current.concat(collectMenuPaths(item.children || []))
  })
}

export function isMenuPathAllowed(menuList = [], path = '') {
  const target = String(path || '').split('?')[0]
  if (!target) return false
  return collectMenuPaths(menuList).includes(target)
}

export function buildMissingPagePath(menuUrl = '', title = '') {
  const query = new URLSearchParams({
    menuUrl,
    title,
  })
  return `/404?${query.toString()}`
}

function isGarbledMenuName(name = '') {
  const text = String(name).trim()
  return !text || /^[?？]+$/.test(text)
}

export function normalizeMenuTree(menuList = []) {
  return menuList
    .map((item) => ({
      id: item.menuId,
      title: item.name,
      icon: item.icon,
      type: item.type,
      perms: item.perms || '',
      path: item.type === 1 ? mapMenuUrlToPath(item.url) || buildMissingPagePath(item.url, item.name) : '',
      children: normalizeMenuTree(item.list || []),
    }))
    .filter((item) => item.type !== 2 && !isGarbledMenuName(item.title))
}
