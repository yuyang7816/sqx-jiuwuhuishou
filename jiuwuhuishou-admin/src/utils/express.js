/**
 * 邮寄订单模拟物流轨迹（后端未返回 expressTracks 时兜底）
 */
export function buildMockExpressTracks(order = {}) {
  const statusKey = order.statusKey || ''
  const expressNo = order.expressNo || '待生成'
  const pickup = order.pickupAddressText || order.addressText || '用户取件地址'
  const warehouse = order.mailAddressText || '平台仓储中心'

  if (statusKey === 'done' || statusKey === 'paying') {
    return [
      { title: '快件已签收', time: '2026-07-21 11:28:00', desc: `已送达【${warehouse}】，签收人：平台仓储`, active: true },
      { title: '派送中', time: '2026-07-21 09:46:00', desc: '快件正在派送，快递员：顺丰小哥 138****6621', active: false },
      { title: '运输中', time: '2026-07-20 22:18:00', desc: '快件已到达【上海浦东转运中心】', active: false },
      { title: '已揽收', time: '2026-07-20 16:05:00', desc: `顺丰已从【${pickup}】揽收，运单号 ${expressNo}`, active: false },
      { title: '待取件', time: '2026-07-20 10:12:00', desc: '预约寄件成功，等待快递员上门取件', active: false },
    ]
  }
  if (statusKey === 'pendingConfirm') {
    return [
      { title: '快件已签收', time: '2026-07-21 11:28:00', desc: `已送达【${warehouse}】，等待质检确认`, active: true },
      { title: '派送中', time: '2026-07-21 09:46:00', desc: '快件正在派送，快递员：顺丰小哥 138****6621', active: false },
      { title: '运输中', time: '2026-07-20 22:18:00', desc: '快件已到达【上海浦东转运中心】', active: false },
      { title: '已揽收', time: '2026-07-20 16:05:00', desc: `顺丰已从【${pickup}】揽收，运单号 ${expressNo}`, active: false },
      { title: '待取件', time: '2026-07-20 10:12:00', desc: '预约寄件成功，等待快递员上门取件', active: false },
    ]
  }
  return [
    { title: '运输中', time: '2026-07-20 22:18:00', desc: '快件已到达【上海浦东转运中心】', active: true },
    { title: '已揽收', time: '2026-07-20 16:05:00', desc: `顺丰已从【${pickup}】揽收，运单号 ${expressNo}`, active: false },
    { title: '待取件', time: '2026-07-20 10:12:00', desc: '预约寄件成功，等待快递员上门取件', active: false },
  ]
}

export function resolveExpressTracks(order = {}) {
  if (Array.isArray(order.expressTracks) && order.expressTracks.length) {
    return order.expressTracks
  }
  return buildMockExpressTracks(order)
}
