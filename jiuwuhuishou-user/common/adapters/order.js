import { formatAppointmentDisplay, formatDisplayTime, formatFullDateTime, formatMoney } from '../utils/datetime.js';

const orderTypeMeta = {
	clothes: { title: '旧衣回收', icon: 'reload', iconClass: 'is-pink', iconColor: '#cf1432' },
	book: { title: '书籍回收', icon: 'file-text', iconClass: 'is-red', iconColor: '#cf1432' },
	appliance: { title: '家电回收', icon: 'home', iconClass: 'is-gray', iconColor: '#6d6565' },
	digital: { title: '数码回收', icon: 'grid', iconClass: 'is-gold', iconColor: '#8a6400' },
	luxury: { title: '奢侈品回收', icon: 'coupon', iconClass: 'is-rose', iconColor: '#cf1432' }
};

export const isBookOrderType = (order) => {
	const target = order || {};
	if (target.orderType === 'book') {
		return true;
	}
	const categoryName = `${target.categoryName || target.category || ''}`;
	return target.orderType === 'clothes' && categoryName.includes('书籍');
};

export const isLuxuryOrderType = (order) => (order || {}).orderType === 'luxury';

export const isSpecOrderType = (order) => {
	const orderType = (order || {}).orderType || '';
	return orderType === 'appliance' || orderType === 'digital' || orderType === 'luxury';
};

export const isWeightOrderType = (order) => {
	if (isSpecOrderType(order)) {
		return false;
	}
	const target = order || {};
	return target.orderType === 'clothes' || target.orderType === 'book' || isBookOrderType(target);
};

const resolveOrderTitle = (order = {}) => {
	if (order.categoryName) {
		return order.categoryName;
	}
	const meta = orderTypeMeta[order.orderType] || orderTypeMeta.clothes;
	return meta.title;
};

const statusTabTextMap = {
	running: '进行中',
	waitAssign: '待分配',
	pendingConfirm: '待确认',
	paying: '打款中',
	done: '已完成',
	cancelled: '已取消',
	exception: '异常处理中',
	dispute: '争议处理中'
};

const statusCodeTextMap = {
	CREATED: '已创建',
	WAIT_AUDIT: '待审核',
	WAIT_ASSIGN: '待分配',
	IN_PROGRESS: '进行中',
	WAIT_SETTLEMENT_CONFIRM: '待确认',
	WAIT_PAYOUT: '待打款',
	PAYOUT_PROCESSING: '打款中',
	COMPLETED: '已完成',
	CANCELLED: '已取消',
	EXCEPTION_PENDING: '异常处理中',
	DISPUTE_PENDING: '争议处理中'
};

const statusCodeToKeyMap = {
	CREATED: 'running',
	WAIT_AUDIT: 'running',
	WAIT_ASSIGN: 'waitAssign',
	IN_PROGRESS: 'running',
	WAIT_SETTLEMENT_CONFIRM: 'pendingConfirm',
	WAIT_PAYOUT: 'paying',
	PAYOUT_PROCESSING: 'paying',
	COMPLETED: 'done',
	CANCELLED: 'cancelled',
	EXCEPTION_PENDING: 'exception',
	DISPUTE_PENDING: 'dispute'
};

const resolveStatusText = (statusCode) => statusCodeTextMap[statusCode] || statusCode || '';

/** 优先用后端 statusKey；缺失或不识别时按 status 状态码回推，避免争议单被当成已完成 */
const resolveStatusKey = (order = {}) => {
	const rawKey = `${order.statusKey || ''}`.trim();
	if (rawKey && statusTabTextMap[rawKey]) {
		return rawKey;
	}
	const fromCode = statusCodeToKeyMap[`${order.status || ''}`.trim()];
	if (fromCode) {
		return fromCode;
	}
	return rawKey || 'running';
};

const resolveStatusLabel = (order = {}, statusKey = '') => {
	const fromCode = statusCodeTextMap[`${order.status || ''}`.trim()];
	if (fromCode) {
		return fromCode;
	}
	return statusTabTextMap[statusKey] || '进行中';
};

const tabToStatusKey = {
	'全部': '',
	'进行中': 'running',
	'待确认': 'pendingConfirm',
	'已完成': 'done'
};

export const getStatusKeyByTab = (tab) => tabToStatusKey[tab] || '';

/** 解析「地址 收件人：xx 电话：yy」快照为结构化字段 */
export const parseMailAddressParts = (mailAddressText = '') => {
	const text = `${mailAddressText || ''}`.trim();
	if (!text) {
		return { mailReceiver: '', mailPhone: '', mailAddressOnly: '' };
	}
	const matched = text.match(/^(.*?)\s*收件人：\s*(.*?)\s*电话：\s*(.+)$/);
	if (!matched) {
		return { mailReceiver: '', mailPhone: '', mailAddressOnly: text };
	}
	return {
		mailAddressOnly: (matched[1] || '').trim(),
		mailReceiver: (matched[2] || '').trim(),
		mailPhone: (matched[3] || '').trim()
	};
};

/** 邮寄订单模拟物流轨迹（后端未返回 expressTracks 时兜底，与管理端一致） */
export const buildMockExpressTracks = (order = {}) => {
	const statusKey = order.statusKey || '';
	const expressNo = order.expressNo || '待生成';
	const pickup = order.pickupAddressText || order.addressText || '用户取件地址';
	const mailParts = parseMailAddressParts(order.mailAddressText);
	const warehouse = mailParts.mailAddressOnly || order.mailAddressText || '平台仓储中心';

	if (statusKey === 'done' || statusKey === 'paying') {
		return [
			{ title: '快件已签收', time: '2026-07-21 11:28:00', desc: `已送达【${warehouse}】，签收人：平台仓储`, active: true },
			{ title: '派送中', time: '2026-07-21 09:46:00', desc: '快件正在派送，快递员：顺丰小哥 138****6621', active: false },
			{ title: '运输中', time: '2026-07-20 22:18:00', desc: '快件已到达【上海浦东转运中心】', active: false },
			{ title: '已揽收', time: '2026-07-20 16:05:00', desc: `顺丰已从【${pickup}】揽收，运单号 ${expressNo}`, active: false },
			{ title: '待取件', time: '2026-07-20 10:12:00', desc: '预约寄件成功，等待快递员上门取件', active: false }
		];
	}
	if (statusKey === 'pendingConfirm') {
		return [
			{ title: '快件已签收', time: '2026-07-21 11:28:00', desc: `已送达【${warehouse}】，等待质检确认`, active: true },
			{ title: '派送中', time: '2026-07-21 09:46:00', desc: '快件正在派送，快递员：顺丰小哥 138****6621', active: false },
			{ title: '运输中', time: '2026-07-20 22:18:00', desc: '快件已到达【上海浦东转运中心】', active: false },
			{ title: '已揽收', time: '2026-07-20 16:05:00', desc: `顺丰已从【${pickup}】揽收，运单号 ${expressNo}`, active: false },
			{ title: '待取件', time: '2026-07-20 10:12:00', desc: '预约寄件成功，等待快递员上门取件', active: false }
		];
	}
	return [
		{ title: '运输中', time: '2026-07-20 22:18:00', desc: '快件已到达【上海浦东转运中心】', active: true },
		{ title: '已揽收', time: '2026-07-20 16:05:00', desc: `顺丰已从【${pickup}】揽收，运单号 ${expressNo}`, active: false },
		{ title: '待取件', time: '2026-07-20 10:12:00', desc: '预约寄件成功，等待快递员上门取件', active: false }
	];
};

/** 优先真实轨迹；无数据且有快递单号时回落测试轨迹 */
export const resolveExpressTracks = (order = {}) => {
	if (Array.isArray(order.expressTracks) && order.expressTracks.length) {
		return order.expressTracks;
	}
	if ((order.fulfillType || '') === 'mail' && order.expressNo) {
		return buildMockExpressTracks(order);
	}
	return [];
};

const formatEstimateText = (order = {}) => {
	const min = order.estimateMinAmount;
	const max = order.estimateMaxAmount;
	if (order.orderType === 'appliance' && (min != null || max != null)) {
		const low = min != null ? min : order.estimateAmount;
		const high = max != null ? max : order.estimateAmount;
		if (low != null && high != null) {
			if (Number(low) === Number(high)) {
				return formatMoney(low);
			}
			return `${formatMoney(low)} - ${formatMoney(high)}`;
		}
	}
	return formatMoney(order.estimateAmount);
};

export const adaptOrderFromApi = (order) => {
	const meta = orderTypeMeta[order.orderType] || orderTypeMeta.clothes;
	const statusKey = resolveStatusKey(order);
	const tabStatus = resolveStatusLabel(order, statusKey);
	const appointment = formatAppointmentDisplay(order.appointmentTime, order.appointmentEndTime)
		|| formatDisplayTime(order.appointmentTime);
	const title = resolveOrderTitle(order);
	const fulfillType = order.fulfillType || 'visit';
	const isMail = fulfillType === 'mail';
	const expressTracks = isMail
		? resolveExpressTracks({ ...order, statusKey, fulfillType })
		: [];
	return {
		id: order.id,
		orderNo: order.orderNo,
		title,
		type: order.orderType,
		orderType: order.orderType,
		status: tabStatus,
		statusKey,
		statusDesc: order.statusDesc,
		time: appointment,
		slotLabel: appointment,
		appointment,
		submitTime: formatFullDateTime(order.createTime),
		createTime: order.createTime,
		desc: (statusKey === 'dispute' || statusKey === 'exception')
			? (order.statusDesc || order.remark || '')
			: (order.remark || order.statusDesc || ''),
		icon: meta.icon,
		iconClass: meta.iconClass,
		iconColor: meta.iconColor,
		fulfillType,
		fulfillTypeLabel: order.fulfillTypeLabel || (isMail ? '邮寄订单' : '上门订单'),
		address: order.addressText,
		pickupAddress: order.pickupAddressText || order.addressText,
		mailAddress: order.mailAddressText || '',
		...parseMailAddressParts(order.mailAddressText),
		expressNo: order.expressNo || '',
		expressCompany: order.expressCompany || (isMail ? '顺丰速运' : ''),
		expressTracks,
		contact: `${order.contactName || ''} ${order.contactMobile || ''}`.trim(),
		categoryText: order.categoryName,
		category: order.categoryName,
		detailText: order.categoryName,
		estimateText: formatEstimateText(order),
		actualText: order.actualAmount != null ? formatMoney(order.actualAmount) : '',
		estimateAmount: order.estimateAmount,
		estimateMinAmount: order.estimateMinAmount,
		estimateMaxAmount: order.estimateMaxAmount,
		actualAmount: order.actualAmount,
		estimateAmountConfirmed: Boolean(order.estimateAmountConfirmed),
		settlementEstimate: Number(order.estimateAmount) || 0,
		settlementActual: order.actualAmount != null && order.actualAmount !== '' ? Number(order.actualAmount) : null,
		payoutStatus: statusKey === 'done' ? '已到账' : (statusKey === 'paying' ? '打款中' : '待打款'),
		logs: adaptOrderLogs(order.logs || [])
	};
};

const resolveOrderLogTitle = (log = {}) => {
	const content = String(log.content || log.actionType || '');
	// 用户端不展示顺丰凭证配置细节，统一为待取件文案
	if (content.includes('顺丰下单跳过')) {
		return '待顺丰取件';
	}
	return content;
};

export const adaptOrderLogs = (logs = []) => logs.map((log, index) => ({
	title: resolveOrderLogTitle(log),
	time: formatDisplayTime(log.createTime),
	desc: log.toStatus ? `状态：${resolveStatusText(log.toStatus)}` : '',
	active: index === 0
}));

/** 仅「打款中」(PAYOUT_PROCESSING) 展示打款进度；待打款/已完成不展示 */
export const isPayoutProcessing = (order = {}) => {
	const status = `${order.status || ''}`.trim();
	return status === 'PAYOUT_PROCESSING' || status === '打款中';
};

export const enrichOrderForList = (order) => {
	const adapted = adaptOrderFromApi(order);
	const statusClassMap = {
		running: 'is-running',
		waitAssign: 'is-running',
		pendingConfirm: 'is-warn',
		done: 'is-done',
		paying: 'is-running',
		exception: 'is-warn',
		dispute: 'is-warn',
		cancelled: 'is-done'
	};
	const actionConfigMap = {
		running: [
			{ label: '取消订单', type: 'ghost', action: 'cancel' },
			{ label: '改约', type: 'ghost', action: 'reschedule' },
			{ label: '查看详情', type: 'main', action: 'detail' }
		],
		waitAssign: [
			{ label: '取消订单', type: 'ghost', action: 'cancel' },
			{ label: '改约', type: 'ghost', action: 'reschedule' },
			{ label: '查看详情', type: 'main', action: 'detail' }
		],
		pendingConfirm: [
			{ label: '取消订单', type: 'ghost', action: 'cancel' },
			{ label: '异常反馈', type: 'ghost', action: 'exception' },
			{ label: '价格确认', type: 'main', action: 'settlement' }
		],
		done: [
			{ label: '评价投诉', type: 'ghost', action: 'review' },
			{ label: '查看详情', type: 'main', action: 'detail' }
		],
		paying: [
			{ label: '查看详情', type: 'main', action: 'detail' }
		],
		exception: [
			{ label: '查看详情', type: 'main', action: 'detail' }
		],
		dispute: [
			{ label: '查看详情', type: 'main', action: 'detail' }
		],
		cancelled: [
			{ label: '查看详情', type: 'main', action: 'detail' }
		]
	};
	const actions = [...(actionConfigMap[adapted.statusKey] || actionConfigMap.running)];
	if (isPayoutProcessing(order) || isPayoutProcessing(adapted)) {
		actions.unshift({ label: '打款进度', type: 'ghost', action: 'payout' });
	}
	return {
		...adapted,
		statusClass: statusClassMap[adapted.statusKey] || 'is-running',
		actions
	};
};
