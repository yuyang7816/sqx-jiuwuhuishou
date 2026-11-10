import { formatDisplayTime } from '../utils/datetime.js';

export const adaptSettlementFromApi = (data = {}) => ({
	orderId: data.orderId,
	settlementNo: data.settlementNo || '',
	estimateAmount: Number(data.estimateAmount || 0),
	actualAmount: data.actualAmount != null && data.actualAmount !== '' ? Number(data.actualAmount) : null,
	confirmed: Boolean(data.confirmed),
	status: data.status || '',
	orderStatusKey: data.orderStatusKey || ''
});

export const adaptPayoutFromApi = (data = {}) => ({
	orderId: data.orderId,
	payoutNo: data.payoutNo || '',
	amount: Number(data.amount || 0),
	status: data.status || '',
	statusText: data.statusText || '处理中',
	payChannelText: data.payChannelText || '用户钱包',
	accountText: data.accountText || '',
	logs: (data.logs || []).map((item, index, list) => ({
		title: item.title || '',
		time: formatDisplayTime(item.createTime),
		active: item.active ?? index === list.length - 1
	}))
});

export const adaptReviewFromApi = (data = {}) => ({
	orderId: data.orderId,
	score: Number(data.score || 5),
	complaintType: data.complaintType || '',
	content: data.content || '',
	reviewed: Boolean(data.reviewed)
});
