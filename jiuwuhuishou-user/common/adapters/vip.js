import { formatCommissionAmount } from './referral.js';

export const adaptVipPlanFromApi = (item = {}) => ({
	id: item.id,
	name: item.name || '会员套餐',
	month: Number(item.month || 1),
	amount: Number(item.amount || 0),
	amountText: formatCommissionAmount(item.amount || 0),
	originalAmount: Number(item.originalAmount || 0),
	originalAmountText: item.originalAmount ? formatCommissionAmount(item.originalAmount) : '',
	sort: Number(item.sort || 0)
});

export const adaptVipRightFromApi = (item = {}) => ({
	id: item.id,
	name: item.name || '会员权益',
	img: item.img || '',
	sort: Number(item.sort || 0)
});

export const adaptVipPageData = ({ plans = [], rights = [], isVip = false } = {}) => ({
	isVip,
	plans: plans.sort((a, b) => a.sort - b.sort),
	rights: rights.sort((a, b) => a.sort - b.sort)
});
