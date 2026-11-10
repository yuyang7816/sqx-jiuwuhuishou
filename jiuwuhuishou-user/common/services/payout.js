import config from '../config.js';
import * as payoutApi from '../api/payout.js';
import { adaptPayoutFromApi } from '../adapters/settlement.js';
import { getOrderDetail } from './order.js';

export const loadPayoutPageData = async (orderId) => {
	if (config.USE_MOCK) {
		const order = await getOrderDetail(orderId);
		return {
			order,
			payout: {
				amount: order?.settlementActual || 0,
				statusText: order?.payoutStatus || '打款中',
				payChannelText: '用户钱包',
				accountText: order?.contact || '',
				logs: order?.logs || []
			}
		};
	}

	const [order, payoutRes] = await Promise.all([
		getOrderDetail(orderId),
		payoutApi.fetchPayoutInfo({ orderId })
	]);

	return {
		order,
		payout: adaptPayoutFromApi(payoutRes)
	};
};
