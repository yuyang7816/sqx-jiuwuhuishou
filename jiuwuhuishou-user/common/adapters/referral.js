const AVATAR_CLASSES = ['is-avatar-warm', 'is-avatar-pink', 'is-avatar-green'];
const BADGE_PRESETS = [
	{ icon: 'coupon', iconColor: '#8a6400', badgeClass: 'is-badge-gold' },
	{ icon: 'heart', iconColor: '#d11f37', badgeClass: 'is-badge-pink' },
	{ icon: 'red-packet', iconColor: '#cf1432', badgeClass: 'is-badge-pink' }
];

const formatAmount = (value = 0) => {
	const amount = Number(value || 0);
	return amount.toLocaleString('zh-CN', {
		minimumFractionDigits: amount % 1 === 0 ? 0 : 2,
		maximumFractionDigits: 2
	});
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
		return timeText;
	}
	if (isYesterday) {
		return `昨天 ${timeText}`;
	}
	return `${`${date.getMonth() + 1}`.padStart(2, '0')}-${`${date.getDate()}`.padStart(2, '0')} ${timeText}`;
};

export const adaptReferralOverviewFromApi = (info = {}, data = {}) => ({
	pendingCommission: Number(info.commission || 0),
	monthContributorCount: Number(data.monthNum || 0),
	totalCommission: Number(data.commission || 0),
	inviteCount: Number(data.num || 0)
});

export const adaptCommissionLogFromApi = (item = {}, index = 0) => {
	const isIncome = Number(item.direction) === 1;
	const isOrder = Number(item.type) === 1;
	const isWithdraw = Number(item.type) === 0;
	// direction: 1=收入(添加) 2=支出
	const status = isIncome ? 'income' : 'expense';
	const badgePreset = BADGE_PRESETS[index % BADGE_PRESETS.length];

	return {
		id: item.id || index,
		type: isOrder ? 'user' : 'badge',
		title: item.title || (isOrder ? '订单佣金' : '提现到钱包'),
		desc: item.description || '',
		amount: `${isIncome ? '+' : '-'}￥${formatAmount(item.amount)}`,
		time: formatDateTime(item.createTime),
		status,
		special: !isOrder && isIncome,
		avatarClass: AVATAR_CLASSES[index % AVATAR_CLASSES.length],
		level: isOrder ? '' : '',
		icon: badgePreset.icon,
		iconColor: badgePreset.iconColor,
		badgeClass: badgePreset.badgeClass,
		amountClass: isWithdraw ? 'is-gold' : (!isOrder && isIncome ? 'is-gold' : 'is-red')
	};
};

export const filterCommissionLogs = (logs = [], tab = 'all') => {
	if (tab === 'all') {
		return logs;
	}
	return logs.filter((item) => item.status === tab);
};

export const formatCommissionAmount = (value = 0) => formatAmount(value);

export const formatCurrencyText = (value = 0) => `￥${formatAmount(value)}`;
