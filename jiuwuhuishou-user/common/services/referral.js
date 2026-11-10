import config from '../config.js';
import * as referralApi from '../api/referral.js';
import {
	adaptCommissionLogFromApi,
	adaptReferralOverviewFromApi,
	filterCommissionLogs
} from '../adapters/referral.js';

const DEFAULT_OVERVIEW = {
	pendingCommission: 0,
	monthContributorCount: 0,
	totalCommission: 0,
	inviteCount: 0
};

const DEFAULT_LOGS = [
	{
		id: 1,
		type: 'user',
		title: '林小乐',
		desc: '回收纸板 12.5kg',
		amount: '+￥5.25',
		time: '14:20',
		status: 'income',
		avatarClass: 'is-avatar-warm',
		level: 'LV2',
		amountClass: 'is-red'
	},
	{
		id: 2,
		type: 'badge',
		title: '推广达人奖金',
		desc: '加成收益 10%',
		amount: '+￥12.00',
		time: '12:05',
		status: 'income',
		special: true,
		icon: 'coupon',
		iconColor: '#8a6400',
		badgeClass: 'is-badge-gold',
		amountClass: 'is-gold'
	},
	{
		id: 3,
		type: 'user',
		title: '王阿姨',
		desc: '旧家电回收',
		amount: '+￥28.40',
		time: '昨天 18:30',
		status: 'income',
		avatarClass: 'is-avatar-pink',
		amountClass: 'is-red'
	},
	{
		id: 4,
		type: 'badge',
		title: '提现到钱包',
		desc: '提现到钱包: 10.00',
		amount: '-￥10.00',
		time: '昨天 10:00',
		status: 'expense',
		icon: 'red-packet',
		iconColor: '#cf1432',
		badgeClass: 'is-badge-pink',
		amountClass: 'is-gold'
	}
];

export const fetchReferralOverview = async () => {
	if (config.USE_MOCK) {
		return {
			pendingCommission: 864.25,
			monthContributorCount: 128,
			totalCommission: 4290,
			inviteCount: 256
		};
	}
	const [info, data] = await Promise.all([
		referralApi.fetchReferralCommissionInfo(),
		referralApi.fetchReferralData()
	]);
	return adaptReferralOverviewFromApi(info, data);
};

export const fetchReferralCommissionLogs = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_LOGS;
	}
	const page = await referralApi.fetchReferralCommissionPage({
		page: 1,
		limit: 100
	});
	return (page?.records || []).map(adaptCommissionLogFromApi);
};

export const withdrawCommissionToWallet = async (amount) => {
	if (config.USE_MOCK) {
		return {
			withdrawAmount: Number(amount),
			commission: 0,
			walletAmount: Number(amount)
		};
	}
	return referralApi.withdrawCommissionToWallet({ amount: Number(amount) });
};

export const loadReferralPageData = async (tab = 'all') => {
	const [overview, logs] = await Promise.all([
		fetchReferralOverview(),
		fetchReferralCommissionLogs()
	]);
	return {
		overview,
		logs: filterCommissionLogs(logs, tab)
	};
};

export { filterCommissionLogs, formatCommissionAmount, formatCurrencyText } from '../adapters/referral.js';
