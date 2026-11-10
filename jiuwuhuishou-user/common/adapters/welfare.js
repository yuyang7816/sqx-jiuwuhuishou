import { resolveAssetUrl } from './user.js';

const isAssetPath = (value = '') => /^https?:\/\//i.test(value) || value.startsWith('/') || value.includes('.');

export const adaptWelfareSummaryFromApi = (data = {}) => ({
	coinBalance: Number(data.coinBalance || 0),
	continuousSignDays: Number(data.continuousSignDays || 0),
	continuousSignEnabled: data.continuousSignEnabled !== false,
	signedToday: !!data.signedToday
});

export const adaptWelfareSignResultFromApi = (data = {}) => ({
	coinReward: Number(data.coinReward || 0),
	continuousSignDays: Number(data.continuousSignDays || 0),
	coinBalance: Number(data.coinBalance || 0)
});

export const adaptWelfareWithdrawResultFromApi = (data = {}) => ({
	withdrawAmount: Number(data.withdrawAmount || 0),
	coinBalance: Number(data.coinBalance || 0),
	walletAmount: Number(data.walletAmount || 0)
});

export const adaptWelfareTaskFromApi = (item = {}) => {
	const rawIcon = item.icon || '';
	const iconIsImage = isAssetPath(rawIcon);
	return {
		id: item.id,
		title: item.title || '',
		desc: item.description || '',
		icon: iconIsImage ? resolveAssetUrl(rawIcon) : (rawIcon || 'gift'),
		iconIsImage,
		btnText: item.btnText || '领任务',
		url: item.url || '',
		sort: item.sort || 0
	};
};

export const adaptSignRuleConfig = (totalDaysValue, dailyCoinValue) => ({
	totalDays: Math.max(1, Number(totalDaysValue || 7)),
	dailyCoin: Math.max(0, Number(dailyCoinValue || 5))
});

export const buildSignDayNodes = ({ continuousSignDays = 0, signedToday = false, totalDays = 7 }) => {
	const milestones = totalDays <= 5
		? Array.from({ length: totalDays }, (_, index) => index + 1)
		: [1, 2, 3, 4, totalDays];

	return milestones.map((day) => {
		const isRewardDay = day === totalDays;
		const isDone = day <= continuousSignDays;
		const isCurrent = !signedToday && day === continuousSignDays + 1;

		return {
			day: `${day}天`,
			label: `${day}`,
			done: isDone && !isCurrent,
			current: isCurrent,
			reward: isRewardDay
		};
	});
};

export const formatCoinBalance = (value = 0) => `${Number(value || 0).toLocaleString('zh-CN')}`;

const formatCoinLogTime = (value = '') => {
	if (!value) {
		return '';
	}
	const text = `${value}`.replace('T', ' ');
	const date = new Date(text.replace(/-/g, '/'));
	if (Number.isNaN(date.getTime())) {
		return text.slice(0, 16);
	}
	const now = new Date();
	const isToday = date.toDateString() === now.toDateString();
	const yesterday = new Date(now);
	yesterday.setDate(now.getDate() - 1);
	const isYesterday = date.toDateString() === yesterday.toDateString();
	const timeText = `${`${date.getHours()}`.padStart(2, '0')}:${`${date.getMinutes()}`.padStart(2, '0')}`;
	if (isToday) {
		return `今天 ${timeText}`;
	}
	if (isYesterday) {
		return `昨天 ${timeText}`;
	}
	return `${`${date.getMonth() + 1}`.padStart(2, '0')}-${`${date.getDate()}`.padStart(2, '0')} ${timeText}`;
};

export const adaptWelfareCoinLogFromApi = (item = {}, index = 0) => {
	const isIncome = Number(item.type || 1) !== 2;
	const amount = Number(item.coinAmount || 0);
	return {
		id: item.id || index,
		type: isIncome ? 'income' : 'expense',
		title: item.title || (isIncome ? '积分入账' : '积分支出'),
		desc: item.description || '',
		time: formatCoinLogTime(item.createTime),
		amount: `${isIncome ? '+' : '-'}${amount}`,
		status: isIncome ? '已到账' : '已扣除',
		icon: isIncome ? 'gift' : 'coupon',
		iconColor: isIncome ? '#cf1432' : '#6d6a6a',
		iconClass: isIncome ? 'is-red' : 'is-gray',
		amountClass: isIncome ? 'is-income' : 'is-expense'
	};
};

export const filterCoinLogs = (logs = [], tab = 'all') => {
	if (tab === 'all') {
		return logs;
	}
	return logs.filter((item) => item.type === tab);
};

export const createWelfareTaskAction = (task = {}) => {
	const url = task.url || '';
	if (!url) {
		return () => {};
	}
	if (url.startsWith('/pages/')) {
		return () => uni.switchTab({ url });
	}
	return () => uni.navigateTo({ url });
};
