import config from '../config.js';
import * as orderApi from '../api/order.js';
import { buildAppointmentTime } from '../utils/datetime.js';
import { getDefaultAddress } from './address.js';
import {
	createMockOrder,
	getMockOrderById,
	getMockOrders,
	rescheduleMockOrder,
	removeOrder,
	markMockOrderException
} from '../mock-data.js';
import {
	adaptOrderFromApi,
	enrichOrderForList,
	getStatusKeyByTab
} from '../adapters/order.js';

export const listOrders = async (tab = '全部') => {
	if (config.USE_MOCK) {
		const orders = getMockOrders();
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
			pendingConfirm: [
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
			dispute: [
				{ label: '查看详情', type: 'main', action: 'detail' }
			]
		};
		const withActions = orders.map((item) => {
			const actions = [...(actionConfigMap[item.statusKey] || actionConfigMap.running)];
			if (item.status === '打款中' || item.status === 'PAYOUT_PROCESSING') {
				actions.unshift({ label: '打款进度', type: 'ghost', action: 'payout' });
			}
			return {
				...item,
				statusClass: statusClassMap[item.statusKey] || 'is-running',
				actions
			};
		});
		if (tab === '全部') {
			return withActions;
		}
		return withActions.filter((item) => item.status === tab);
	}
	const statusKey = getStatusKeyByTab(tab);
	const page = await orderApi.fetchOrderPage({
		page: 1,
		limit: 100,
		statusKey: statusKey || undefined
	});
	const list = (page?.records || []).map(enrichOrderForList);
	// 已完成仅展示真正完结单，避免争议/异常等被误归入
	if (tab === '已完成') {
		return list.filter((item) => item.statusKey === 'done');
	}
	return list;
};

export const getOrderDetail = async (id) => {
	if (config.USE_MOCK) {
		return getMockOrderById(id);
	}
	const detail = await orderApi.fetchOrderInfo(id);
	return adaptOrderFromApi(detail);
};

export const createClothesOrder = async ({ addressId, dateLabel, slot, dateKey, weight, estimateAmount, categoryName }) => {
	if (config.USE_MOCK) {
		return createMockOrder({
			type: 'clothes',
			title: categoryName || '旧衣回收',
			category: categoryName || '旧衣回收',
			appointment: `${dateLabel} ${slot}`,
			address: '',
			contact: '',
			estimateText: weight,
			estimateAmount,
			actualAmount: estimateAmount
		});
	}
	const orderId = await orderApi.createClothesOrder({
		addressId: Number(addressId),
		appointmentTime: buildAppointmentTime(dateLabel, slot, dateKey),
		weight,
		estimateAmount,
		categoryName
	});
	return { id: orderId };
};

export const createApplianceOrder = async ({
	addressId,
	dateLabel,
	slot,
	dateKey,
	modelId,
	estimateAmount,
	estimateMinAmount,
	estimateMaxAmount,
	remark,
	fulfillType = 'visit'
}) => {
	if (config.USE_MOCK) {
		return createMockOrder({
			type: 'appliance',
			title: '家电回收',
			category: '家电回收',
			appointment: `${dateLabel} ${slot}`,
			address: '',
			contact: '',
			estimateAmount,
			estimateMinAmount,
			estimateMaxAmount,
			actualAmount: estimateAmount,
			fulfillType,
			desc: fulfillType === 'mail' ? '待顺丰寄件取件' : undefined
		});
	}
	if (!modelId) {
		throw new Error('商品型号无效，请返回重新选择');
	}
	const orderId = await orderApi.createApplianceOrder({
		addressId: Number(addressId),
		appointmentTime: buildAppointmentTime(dateLabel, slot, dateKey),
		modelId: Number(modelId),
		estimateAmount,
		estimateMinAmount,
		estimateMaxAmount,
		remark,
		fulfillType: fulfillType === 'mail' ? 'mail' : 'visit'
	});
	return { id: orderId };
};

export const createDigitalOrder = async ({ addressId, dateLabel, slot, dateKey, modelId, estimateAmount, remark, scene }) => {
	if (config.USE_MOCK) {
		const luxuryOrder = scene === 'luxury';
		return createMockOrder({
			type: luxuryOrder ? 'luxury' : 'digital',
			title: luxuryOrder ? '奢侈品回收' : '数码回收',
			category: luxuryOrder ? '奢侈品回收' : '数码回收',
			appointment: `${dateLabel} ${slot}`,
			address: '顺丰免费上门取件',
			contact: '平台回收服务',
			estimateAmount,
			actualAmount: estimateAmount,
			fulfillType: 'mail',
			desc: '待顺丰寄件取件'
		});
	}
	let resolvedAddressId = addressId;
	if (!resolvedAddressId) {
		const defaultAddress = await getDefaultAddress();
		if (!defaultAddress?.id) {
			throw new Error('请先添加收货地址');
		}
		resolvedAddressId = defaultAddress.id;
	}
	const orderId = await orderApi.createDigitalOrder({
		addressId: Number(resolvedAddressId),
		appointmentTime: buildAppointmentTime(dateLabel, slot, dateKey),
		modelId: Number(modelId || 1),
		estimateAmount,
		remark,
		scene
	});
	return { id: orderId };
};

export const cancelOrderById = async (orderId) => {
	if (config.USE_MOCK) {
		removeOrder(orderId);
		return;
	}
	await orderApi.cancelOrder(orderId);
};

export const rescheduleOrderById = async ({ orderId, dateLabel, slot, dateKey, reason, remark }) => {
	if (config.USE_MOCK) {
		rescheduleMockOrder(orderId, `${dateLabel} ${slot}`, reason, remark);
		return;
	}
	await orderApi.rescheduleOrder({
		orderId: Number(orderId),
		reason,
		remark,
		newAppointmentTime: buildAppointmentTime(dateLabel, slot, dateKey)
	});
};

export const submitExceptionById = async ({ orderId, exceptionType, expectAction, description }) => {
	if (config.USE_MOCK) {
		markMockOrderException(orderId, exceptionType, expectAction, description);
		return;
	}
	await orderApi.submitOrderException({
		orderId: Number(orderId),
		exceptionType,
		expectAction,
		description
	});
};

export const shipMailOrderById = async ({ orderId, expressNo, expressCompany = '顺丰速运' }) => {
	if (config.USE_MOCK) {
		return;
	}
	await orderApi.shipMailOrder({ orderId, expressNo, expressCompany });
};

export const getExpressTracks = async (orderId) => {
	if (config.USE_MOCK) {
		const order = getMockOrderById(orderId);
		return order?.expressTracks || [];
	}
	const tracks = await orderApi.fetchExpressTracks(orderId);
	return Array.isArray(tracks) ? tracks : [];
};

// M2 页面仍使用 Mock 的结算/打款/评价能力
export {
	confirmMockOrderDeal,
	reviewMockOrder,
	getMockPickups,
	getMockPickupById,
	finishMockPickup,
	startMockPickup
} from '../mock-data.js';
