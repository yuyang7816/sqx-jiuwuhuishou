const STORAGE_KEYS = {
	addresses: 'mock-addresses-v1',
	orders: 'mock-orders-v1',
	pickups: 'mock-pickups-v1'
};

const clone = (value) => JSON.parse(JSON.stringify(value));

const pad = (value) => `${value}`.padStart(2, '0');

const formatMaskedMobile = (mobile = '') => `${mobile}`.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2');

const formatDateLabel = (date) => `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;

const createOrderNo = () => {
	const now = new Date();
	return `RE${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`;
};

const createId = (prefix) => `${prefix}-${Date.now()}-${Math.floor(Math.random() * 1000)}`;

const defaultAddresses = [
	{
		id: 1,
		name: '张伟',
		label: '公司',
		mobile: '13812348888',
		phone: '138****8888',
		region: ['上海市', '浦东新区', '张江镇'],
		address: '祖冲之路2277号世和中心12楼1205室',
		default: true,
		icon: 'map'
	},
	{
		id: 2,
		name: '李梅',
		label: '家',
		mobile: '15066778899',
		phone: '150****8899',
		region: ['上海市', '杨浦区', '五角场街道'],
		address: '翔殷路88号绿地幸福里7号楼2201室',
		default: false,
		icon: 'home'
	}
];

const defaultOrders = [
	{
		id: 'order-1001',
		orderNo: 'RE20260703001',
		title: '旧衣回收',
		type: 'clothes',
		status: '进行中',
		statusKey: 'running',
		time: '2026-07-03 14:00',
		slotLabel: '今日 14:00 - 16:00',
		desc: '已分配回收员，支持改约和异常反馈',
		icon: 'reload',
		iconClass: 'is-pink',
		iconColor: '#cf1432',
		address: '上海市静安区南京西路1618号久光百货',
		contact: '张先生 138****8888',
		categoryText: '旧衣 / 鞋帽 / 家纺',
		detailText: '约 18kg 可回收旧衣',
		estimateText: '¥18.00'
	},
	{
		id: 'order-1002',
		orderNo: 'RE20260703002',
		title: '数码回收',
		type: 'digital',
		status: '待确认',
		statusKey: 'pendingConfirm',
		time: '2026-07-03 10:30',
		slotLabel: '今日 10:30 - 12:00',
		desc: '质检结果已出，请确认实际成交价',
		icon: 'grid',
		iconClass: 'is-gold',
		iconColor: '#8a6400',
		address: '上海市浦东新区张江路365号纳贤中心',
		contact: '李女士 150****6677',
		categoryText: 'iPhone 15 Pro / 256G',
		detailText: '设备已完成质检，等待用户确认',
		estimateText: '¥6,120.00',
		actualText: '¥5,980.00'
	},
	{
		id: 'order-1003',
		orderNo: 'RE20260702001',
		title: '家电回收',
		type: 'appliance',
		status: '已完成',
		statusKey: 'done',
		time: '2026-07-02 09:00',
		slotLabel: '昨日 09:00 - 11:00',
		desc: '已完成打款，可评价服务或发起投诉',
		icon: 'home',
		iconClass: 'is-gray',
		iconColor: '#6d6565',
		address: '上海市杨浦区翔殷路88号绿地幸福里',
		contact: '王阿姨 137****8899',
		categoryText: '冰箱回收',
		detailText: '海尔 318L 双开门冰箱 1 台',
		estimateText: '¥226.00',
		actualText: '¥208.00'
	}
];

const defaultPickups = [
	{
		id: 'pickup-1001',
		orderId: 'order-1001',
		title: '张先生取件任务',
		address: '上海市静安区南京西路1618号久光百货',
		contact: '张先生 138****8888',
		time: '今日 14:00 - 16:00',
		category: '旧衣回收',
		weight: '约 18kg',
		status: 'running',
		statusText: '取件中',
		statusClass: 'is-running',
		orderNo: 'PK20260703001'
	},
	{
		id: 'pickup-1002',
		orderId: 'order-1002',
		title: '李女士取件任务',
		address: '上海市浦东新区张江路365号纳贤中心',
		contact: '李女士 150****6677',
		time: '今日 10:30 - 12:00',
		category: '数码回收',
		weight: '1 台设备',
		status: 'pending',
		statusText: '待上门',
		statusClass: 'is-pending',
		orderNo: 'PK20260703002'
	},
	{
		id: 'pickup-1003',
		orderId: 'order-1003',
		title: '王阿姨取件任务',
		address: '上海市杨浦区翔殷路88号绿地幸福里',
		contact: '王阿姨 137****8899',
		time: '昨日 09:00 - 11:00',
		category: '家电回收',
		weight: '冰箱 1 台',
		status: 'done',
		statusText: '已完成',
		statusClass: 'is-done',
		orderNo: 'PK20260702001'
	}
];

const readList = (key, fallback) => {
	const local = uni.getStorageSync(key);
	if (!local || !Array.isArray(local) || !local.length) {
		const seeded = clone(fallback);
		uni.setStorageSync(key, seeded);
		return seeded;
	}
	return clone(local);
};

const writeList = (key, list) => {
	const nextList = clone(list);
	uni.setStorageSync(key, nextList);
	return nextList;
};

export const ensureMockData = () => {
	readList(STORAGE_KEYS.addresses, defaultAddresses);
	readList(STORAGE_KEYS.orders, defaultOrders);
	readList(STORAGE_KEYS.pickups, defaultPickups);
};

export const getAddresses = () => {
	ensureMockData();
	return readList(STORAGE_KEYS.addresses, defaultAddresses).map((item) => ({
		...item,
		contactMobile: item.contactMobile || item.mobile || '',
		mobile: item.mobile || item.contactMobile || '',
		phone: item.contactMobile || item.mobile || item.phone || ''
	}));
};

export const getAddressById = (id) => getAddresses().find((item) => `${item.id}` === `${id}`) || null;

export const saveAddress = (payload, mode = 'create') => {
	const list = getAddresses();
	const record = {
		id: mode === 'edit' && payload.id ? Number(payload.id) : Date.now(),
		name: payload.name,
		label: payload.label,
		mobile: payload.mobile,
		phone: formatMaskedMobile(payload.mobile),
		region: payload.region,
		address: payload.address,
		default: Boolean(payload.default),
		icon: payload.icon || (payload.label === '公司' ? 'map' : 'home')
	};
	let nextList = list.filter((item) => `${item.id}` !== `${record.id}`);
	if (record.default) {
		nextList = nextList.map((item) => ({ ...item, default: false }));
	}
	if (!nextList.some((item) => item.default) && !record.default) {
		record.default = true;
	}
	nextList.unshift(record);
	writeList(STORAGE_KEYS.addresses, nextList);
	return record;
};

export const deleteAddress = (id) => {
	const list = getAddresses();
	let nextList = list.filter((item) => `${item.id}` !== `${id}`);
	if (nextList.length && !nextList.some((item) => item.default)) {
		nextList[0].default = true;
	}
	writeList(STORAGE_KEYS.addresses, nextList);
	return nextList;
};

export const getOrders = () => {
	ensureMockData();
	return readList(STORAGE_KEYS.orders, defaultOrders);
};

export const getOrderById = (id) => getOrders().find((item) => `${item.id}` === `${id}`) || null;

export const updateOrder = (id, patch) => {
	const nextList = getOrders().map((item) => (`${item.id}` === `${id}` ? { ...item, ...patch } : item));
	writeList(STORAGE_KEYS.orders, nextList);
	return nextList.find((item) => `${item.id}` === `${id}`) || null;
};

export const removeOrder = (id) => {
	const nextList = getOrders().filter((item) => `${item.id}` !== `${id}`);
	writeList(STORAGE_KEYS.orders, nextList);
	return nextList;
};

export const markOrderDone = (id, actualText) => updateOrder(id, {
	status: '已完成',
	statusKey: 'done',
	desc: '已完成打款，可评价服务或发起投诉',
	actualText: actualText || '¥0.00'
});

export const markOrderPaying = (id, actualText) => updateOrder(id, {
	status: '打款中',
	statusKey: 'paying',
	desc: '已确认成交价，等待平台打款',
	actualText: actualText || '¥0.00',
	estimateAmountConfirmed: true,
	payoutStatus: '打款中'
});

const createPickupFromOrder = (order) => {
	const list = getPickups();
	const record = {
		id: createId('pickup'),
		orderId: order.id,
		title: `${order.contact.split(' ')[0]}取件任务`,
		address: order.address,
		contact: order.contact,
		time: order.slotLabel,
		category: order.title,
		weight: order.detailText,
		status: 'pending',
		statusText: '待上门',
		statusClass: 'is-pending',
		orderNo: `PK${Date.now()}`
	};
	list.unshift(record);
	writeList(STORAGE_KEYS.pickups, list);
	return record;
};

export const createOrder = ({
	title,
	type,
	address,
	contact,
	time,
	slotLabel,
	categoryText,
	detailText,
	estimateText,
	actualText,
	icon,
	iconClass,
	iconColor,
	fulfillType = 'visit',
	desc
}) => {
	const isMail = fulfillType === 'mail';
	const list = getOrders();
	const record = {
		id: createId('order'),
		orderNo: createOrderNo(),
		title,
		type,
		orderType: type,
		status: '进行中',
		statusKey: 'running',
		statusDesc: isMail ? '待顺丰寄件取件' : undefined,
		time,
		slotLabel,
		desc: desc || (isMail ? '待顺丰寄件取件' : '订单已创建成功，等待上门取件'),
		icon,
		iconClass,
		iconColor,
		fulfillType: isMail ? 'mail' : 'visit',
		fulfillTypeLabel: isMail ? '邮寄订单' : '上门订单',
		expressCompany: isMail ? '顺丰速运' : '',
		address,
		contact,
		categoryText,
		detailText,
		estimateText,
		actualText
	};
	list.unshift(record);
	writeList(STORAGE_KEYS.orders, list);
	if (!isMail) {
		createPickupFromOrder(record);
	}
	return record;
};

export const getPickups = () => {
	ensureMockData();
	return readList(STORAGE_KEYS.pickups, defaultPickups);
};

export const getPickupById = (id) => getPickups().find((item) => `${item.id}` === `${id}`) || null;

export const finishPickup = (pickupId) => {
	const pickups = getPickups().map((item) => (`${item.id}` === `${pickupId}` ? {
		...item,
		status: 'done',
		statusText: '已完成',
		statusClass: 'is-done'
	} : item));
	const currentPickup = pickups.find((item) => `${item.id}` === `${pickupId}`);
	writeList(STORAGE_KEYS.pickups, pickups);
	if (currentPickup?.orderId) {
		updateOrder(currentPickup.orderId, {
			status: '待确认',
			statusKey: 'pendingConfirm',
			desc: '已完成上门回收，请确认最终成交价'
		});
	}
	return currentPickup;
};

export const startPickup = (pickupId) => {
	const pickups = getPickups().map((item) => (`${item.id}` === `${pickupId}` ? {
		...item,
		status: 'running',
		statusText: '取件中',
		statusClass: 'is-running'
	} : item));
	const currentPickup = pickups.find((item) => `${item.id}` === `${pickupId}`);
	writeList(STORAGE_KEYS.pickups, pickups);
	return currentPickup;
};

export const formatAddressLabel = (item) => item?.label || item?.name || '';

export const formatAddressUser = (item) => {
	if (!item) {
		return '';
	}
	return `${item.name} ${item.contactMobile || item.mobile || item.phone || ''}`.trim();
};

export const createOrderTimeText = (dateLabel, slot) => {
	const now = new Date();
	return `${formatDateLabel(now)} · ${dateLabel} ${slot}`;
};

export const getMockAddresses = () => getAddresses().map((item) => ({
	...item,
	tag: item.label
}));
export const getMockAddressById = (id) => {
	const current = getAddressById(id);
	return current ? { ...current, tag: current.label } : null;
};
export const saveMockAddress = ({ id, contact, mobile, region, address, tag, isDefault }) => saveAddress({
	id,
	name: contact,
	label: tag,
	mobile,
	region,
	address,
	default: isDefault
	}, id ? 'edit' : 'create');
export const deleteMockAddress = deleteAddress;
export const formatAddressCard = (item) => ({
	phoneText: item.contactMobile || item.mobile || item.phone || '',
	detail: item.region?.length ? `${item.region.join(' ')} ${item.address}` : item.address,
	labelText: formatAddressLabel(item),
	userText: formatAddressUser(item)
});

const orderTypeMap = {
	clothes: { icon: 'reload', iconClass: 'is-pink', iconColor: '#cf1432' },
	book: { icon: 'file-text', iconClass: 'is-red', iconColor: '#cf1432' },
	digital: { icon: 'grid', iconClass: 'is-gold', iconColor: '#8a6400' },
	appliance: { icon: 'home', iconClass: 'is-gray', iconColor: '#6d6565' },
	luxury: { icon: 'coupon', iconClass: 'is-rose', iconColor: '#cf1432' }
};

const buildOrderLogs = (order) => {
	if (order.statusKey === 'done') {
		return [
			{ title: '打款已完成，订单已闭环', time: '2026-07-03 16:12:00', desc: '', active: true },
			{ title: '用户已确认最终成交价', time: '2026-07-03 15:42:00', desc: '', active: false },
			{ title: '回收员已完成上门回收', time: '2026-07-03 14:16:00', desc: '', active: false }
		];
	}
	if (order.statusKey === 'paying') {
		return [
			{ title: '用户已确认成交价，等待平台打款', time: '2026-07-03 15:42:00', desc: '', active: true },
			{ title: '实物检测完成，等待用户确认成交价', time: '2026-07-03 15:02:00', desc: '', active: false },
			{ title: '回收员已完成上门回收', time: '2026-07-03 14:16:00', desc: '', active: false }
		];
	}
	if (order.statusKey === 'pendingConfirm') {
		return [
			{ title: '实物检测完成，等待用户确认成交价', time: '2026-07-03 15:02:00', desc: '', active: true },
			{ title: '回收员已完成上门回收', time: '2026-07-03 14:16:00', desc: '', active: false },
			{ title: '订单创建成功，等待履约', time: '2026-07-03 13:32:00', desc: '', active: false }
		];
	}
	return [
		{ title: '回收员已接单，正在前往途中', time: '2026-07-03 13:50:00', desc: '', active: true },
		{ title: '您的订单已分配给履约人员', time: '2026-07-03 13:40:00', desc: '', active: false },
		{ title: '订单提交成功，等待后台审核', time: '2026-07-03 13:32:00', desc: '', active: false }
	];
};

const decorateOrder = (order) => {
	if (!order) {
		return null;
	}
	const isMail = order.fulfillType === 'mail';
	const decorated = {
		...order,
		category: order.categoryText,
		appointment: order.slotLabel || order.time,
		submitTime: order.submitTime || (order.time ? `${order.time}${`${order.time}`.length === 16 ? ':00' : ''}`.slice(0, 19) : ''),
		estimateAmountConfirmed: Boolean(order.estimateAmountConfirmed) || ['paying', 'done'].includes(order.statusKey),
		settlementEstimate: Number(`${order.estimateText || ''}`.replace(/[^\d.]/g, '')) || 2450,
		settlementActual: Number(`${order.actualText || order.estimateText || ''}`.replace(/[^\d.]/g, '')) || 2275,
		payoutStatus: order.statusKey === 'done' ? '已到账' : '打款中',
		logs: buildOrderLogs(order)
	};
	if (isMail && !decorated.expressTracks?.length) {
		decorated.expressCompany = decorated.expressCompany || '顺丰速运';
		decorated.expressNo = decorated.expressNo || 'SF1324587960123';
		decorated.expressTracks = [
			{ title: '快件已签收', time: '2026-07-21 11:28:00', desc: '已送达平台仓储中心，签收人：平台仓储', active: order.statusKey === 'done' },
			{ title: '派送中', time: '2026-07-21 09:46:00', desc: '快件正在派送，快递员：顺丰小哥 138****6621', active: false },
			{ title: '运输中', time: '2026-07-20 22:18:00', desc: '快件已到达【上海浦东转运中心】', active: order.statusKey !== 'done' },
			{ title: '已揽收', time: '2026-07-20 16:05:00', desc: `顺丰已揽收，运单号 ${decorated.expressNo}`, active: false },
			{ title: '待取件', time: '2026-07-20 10:12:00', desc: '预约寄件成功，等待快递员上门取件', active: false }
		];
	}
	return decorated;
};

export const createMockOrder = ({
	type,
	title,
	category,
	appointment,
	address,
	contact,
	estimateText,
	estimateAmount,
	estimateMinAmount,
	estimateMaxAmount,
	actualAmount,
	fulfillType = 'visit',
	desc
}) => {
	let resolvedEstimateText = estimateText;
	if (!resolvedEstimateText && estimateMinAmount != null && estimateMaxAmount != null
		&& Number(estimateMinAmount) !== Number(estimateMaxAmount)) {
		resolvedEstimateText = `¥${Number(estimateMinAmount).toFixed(2)} - ¥${Number(estimateMaxAmount).toFixed(2)}`;
	} else if (!resolvedEstimateText && typeof estimateAmount === 'number') {
		resolvedEstimateText = `¥${estimateAmount.toFixed(2)}`;
	}
	return createOrder({
		title,
		type,
		address,
		contact,
		time: appointment,
		slotLabel: appointment,
		categoryText: category,
		detailText: resolvedEstimateText,
		estimateText: resolvedEstimateText,
		estimateAmount,
		estimateMinAmount,
		estimateMaxAmount,
		actualText: typeof actualAmount === 'number' ? `¥${actualAmount.toFixed(2)}` : undefined,
		fulfillType,
		desc,
		...orderTypeMap[type || 'clothes']
	});
};

export const getMockOrders = () => getOrders().map((item) => decorateOrder(item));
export const getMockOrderById = (id) => decorateOrder(getOrderById(id));
export const rescheduleMockOrder = (id, appointment, reason, remark) => updateOrder(id, {
	time: appointment,
	slotLabel: appointment,
	appointment,
	desc: `已提交改约：${reason}${remark ? `，${remark}` : ''}`
});
export const confirmMockOrderDeal = (id) => {
	const order = getOrderById(id);
	markOrderPaying(id, order?.actualText || order?.estimateText || '¥2275.00');
};
export const reviewMockOrder = (id) => updateOrder(id, { desc: '已完成评价，感谢您的反馈' });
export const getMockPickups = getPickups;
export const getMockPickupById = getPickupById;
export const finishMockPickup = finishPickup;
export const startMockPickup = startPickup;

const USER_PROFILE_KEY = 'mock-user-profile-v1';

export const loginMockUser = ({ mobile, userType }) => {
	uni.setStorageSync('token', 'mock-token');
	uni.setStorageSync('userId', '1');
	uni.setStorageSync('phone', mobile);
	if (userType) {
		uni.setStorageSync('userType', userType);
	}
};

export const getMockUserProfile = () => {
	const cached = uni.getStorageSync(USER_PROFILE_KEY);
	const mobile = cached?.mobile || uni.getStorageSync('phone') || '';
	const profile = cached || {
		nickname: '回收用户',
		mobile,
		avatar: '',
		gender: '保密',
		age: undefined,
		userType: 1,
		isVip: false
	};
	return {
		userId: 1,
		maskedMobile: formatMaskedMobile(mobile),
		isVip: false,
		userType: uni.getStorageSync('userType') === 'employee' ? 3 : 1,
		...profile,
		mobile: profile.mobile || mobile
	};
};

export const saveMockUserProfile = (profile) => {
	uni.setStorageSync(USER_PROFILE_KEY, profile);
	if (profile?.mobile) {
		uni.setStorageSync('phone', profile.mobile);
	}
};

export const markMockOrderException = (id, exceptionType, expectAction, description) => {
	updateOrder(id, {
		status: '异常处理中',
		statusKey: 'exception',
		desc: `已提交异常：${exceptionType}${description ? `，${description}` : ''}`
	});
};
