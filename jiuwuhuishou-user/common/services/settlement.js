import config from '../config.js';
import * as settlementApi from '../api/settlement.js';
import { adaptSettlementFromApi } from '../adapters/settlement.js';
import { confirmMockOrderDeal, getOrderDetail } from './order.js';
import { fetchConfigValue } from '../api/home.js';

export const DEFAULT_SETTLEMENT_CONFIRMED_DESC = '您已确认实际成交价格，订单已进入打款中，请等待平台完成打款。如有疑问，请联系在线客服。';

export const loadSettlementConfirmedDesc = async () => {
	try {
		const data = await fetchConfigValue('order_settlement_confirmed_desc');
		return data?.value || DEFAULT_SETTLEMENT_CONFIRMED_DESC;
	} catch (error) {
		return DEFAULT_SETTLEMENT_CONFIRMED_DESC;
	}
};

export const loadSettlementPageData = async (orderId) => {
	if (config.USE_MOCK) {
		const order = await getOrderDetail(orderId);
		return {
			order,
			settlement: {
				estimateAmount: order?.settlementEstimate || 0,
				actualAmount: order?.settlementActual ?? null,
				confirmed: Boolean(order?.estimateAmountConfirmed)
			}
		};
	}

	const [order, settlementRes] = await Promise.all([
		getOrderDetail(orderId),
		settlementApi.fetchSettlementInfo({ orderId })
	]);

	return {
		order,
		settlement: adaptSettlementFromApi(settlementRes)
	};
};

export const confirmSettlementDeal = async (orderId) => {
	if (config.USE_MOCK) {
		confirmMockOrderDeal(orderId);
		return {
			orderId,
			orderStatusKey: 'paying',
			message: '已确认成交，等待平台打款'
		};
	}
	return settlementApi.confirmSettlement({ orderId });
};

export const submitSettlementDispute = async ({ orderId, reason, description }) => {
	if (config.USE_MOCK) {
		return Promise.resolve();
	}
	return settlementApi.disputeSettlement({ orderId, reason, description });
};
