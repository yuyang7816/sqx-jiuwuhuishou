import { formatCommissionAmount } from './referral.js';

const STATUS_TEXT = {
	1: '待处理',
	2: '已完成',
	3: '已取消',
	4: '失败'
};

const INCOME_STATUS_TEXT = {
	1: '待入账',
	2: '已到账',
	3: '已取消',
	4: '失败'
};

const WITHDRAW_STATUS_TEXT = {
	1: '审核中',
	2: '已完成',
	3: '已取消',
	4: '失败'
};

const formatDateTime = (value = '') => {
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

const resolveTransactionVisual = (item = {}, isIncome = true) => {
	const transactionType = Number(item.transactionType || 0);
	const bizType = Number(item.bizType || 0);

	if (!isIncome || transactionType === 4) {
		const isCompleted = Number(item.status) === 2;
		return {
			icon: isCompleted ? 'checkmark-circle' : 'coupon',
			iconColor: isCompleted ? '#00b96b' : '#6d6a6a',
			iconClass: isCompleted ? 'is-green' : 'is-gray'
		};
	}

	if (transactionType === 3 || bizType === 3) {
		return {
			icon: 'share-square',
			iconColor: '#8a6400',
			iconClass: 'is-gold'
		};
	}

	return {
		icon: 'gift',
		iconColor: '#cf1432',
		iconClass: 'is-red'
	};
};

const resolveStatusText = (status, isIncome) => {
	const statusMap = isIncome ? INCOME_STATUS_TEXT : WITHDRAW_STATUS_TEXT;
	return statusMap[Number(status)] || STATUS_TEXT[Number(status)] || '';
};

export const adaptWalletTransactionFromApi = (item = {}, index = 0) => {
	const isIncome = Number(item.direction) === 1;
	const type = isIncome ? 'income' : 'withdraw';
	const visual = resolveTransactionVisual(item, isIncome);

	return {
		id: item.id || index,
		type,
		title: item.title || (isIncome ? '账户收入' : '账户提现'),
		desc: item.description || '',
		time: formatDateTime(item.createTime),
		amount: `${isIncome ? '+' : '-'}￥${formatCommissionAmount(item.amount)}`,
		status: resolveStatusText(item.status, isIncome),
		amountClass: isIncome ? 'is-income' : 'is-withdraw',
		...visual
	};
};

export const filterWalletTransactions = (logs = [], tab = 'all') => {
	if (tab === 'all') {
		return logs;
	}
	return logs.filter((item) => item.type === tab);
};
