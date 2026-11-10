import { maskMobile, resolveAssetUrl } from './user.js';
import { formatCommissionAmount, formatCurrencyText } from './referral.js';

const AVATAR_CLASSES = ['is-second', 'is-first', 'is-third'];
const ICON_PRESETS = [
	{ icon: 'account', iconColor: '#cf1432', iconClass: 'is-red' },
	{ icon: 'coupon', iconColor: '#8a6400', iconClass: 'is-gold' },
	{ icon: 'man-add', iconColor: '#7f5cff', iconClass: 'is-violet' }
];

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

export const maskDisplayName = (name = '', phone = '') => {
	const text = `${name || phone || ''}`.trim();
	if (!text) {
		return '匿名用户';
	}
	if (/^\d{11}$/.test(text)) {
		return maskMobile(text);
	}
	if (text.length === 1) {
		return `${text}*`;
	}
	if (text.length === 2) {
		return `${text[0]}*`;
	}
	return `${text[0]}*${text.slice(-1)}`;
};

export const buildStatsCutoffText = () => {
	const now = new Date();
	const hour = `${now.getHours()}`.padStart(2, '0');
	return `统计截止至今日 ${hour}:00`;
};

export const adaptInviteStatsFromOverview = (overview = {}) => [
	{
		label: '已邀请(人)',
		value: `${Number(overview.inviteCount || 0)}`,
		className: 'is-red'
	},
	{
		label: '累计收益(元)',
		value: formatCommissionAmount(overview.totalCommission),
		className: 'is-gold'
	},
	{
		label: '待入账(元)',
		value: formatCommissionAmount(overview.pendingCommission),
		className: 'is-dark'
	}
];

const buildRankItem = (item = {}, index = 0) => ({
	id: item.userId || item.id || index,
	rank: index + 1,
	rankText: `${`${index + 1}`.padStart(2, '0')}`,
	name: maskDisplayName(item.userName),
	amount: formatCurrencyText(item.amount),
	amountValue: Number(item.amount || 0),
	count: Number(item.inviteCount || 0),
	avatar: resolveAssetUrl(item.avatar || ''),
	avatarClass: AVATAR_CLASSES[index] || ''
});

export const splitRankBoard = (records = []) => {
	const sorted = [...records]
		.map(buildRankItem)
		.sort((left, right) => {
			if (right.amountValue !== left.amountValue) {
				return right.amountValue - left.amountValue;
			}
			return right.count - left.count;
		})
		.map((item, index) => ({
			...item,
			rank: index + 1,
			rankText: `${`${index + 1}`.padStart(2, '0')}`
		}));

	const top3Raw = sorted.slice(0, 3);
	const podiumOrder = [top3Raw[1], top3Raw[0], top3Raw[2]].filter(Boolean);
	const rankList = sorted.slice(3);

	return {
		podium: podiumOrder.map((item, index) => ({
			...item,
			podiumClass: index === 0 ? 'invite-top3__user--second' : index === 1 ? 'invite-top3__user--first' : 'invite-top3__user--third',
			medalClass: item.rank === 1 ? 'is-first' : item.rank === 3 ? 'is-third' : '',
			amountClass: item.rank === 1 ? 'is-first' : ''
		})),
		rankList
	};
};

export const adaptInviteRecordFromApi = (user = {}, index = 0) => {
	const isDisabled = Number(user.status) === 2;
	return {
		id: user.userId || index,
		name: maskDisplayName(user.userName, user.phone),
		avatar: resolveAssetUrl(user.avatar || ''),
		tag: isDisabled ? '已禁用' : '已注册',
		tagClass: isDisabled ? 'is-gold' : 'is-red',
		desc: '通过邀请码完成注册，等待首单完成后发放奖励',
		time: formatDateTime(user.createTime),
		status: isDisabled ? '已失效' : '跟进中',
		reward: '预计 +￥0'
	};
};

export const adaptInviteCommissionLogFromApi = (item = {}, index = 0) => {
	const isIncome = Number(item.direction) === 1;
	const isOrder = Number(item.type) === 1;
	const isWithdraw = Number(item.type) === 0;
	const isSettled = isIncome || isWithdraw;
	const iconPreset = ICON_PRESETS[index % ICON_PRESETS.length];

	return {
		id: item.id || index,
		title: item.title || (isOrder ? '订单佣金' : '提现到钱包'),
		tag: isSettled ? '已到账' : '待入账',
		desc: item.description || '',
		time: formatDateTime(item.createTime),
		amount: `${isIncome ? '+' : '-'}￥${formatCommissionAmount(item.amount)}`,
		settle: isWithdraw ? '已提现到钱包' : (isSettled ? '已转入收益账户' : '预计 3 日内结算'),
		icon: iconPreset.icon,
		iconColor: iconPreset.iconColor,
		iconClass: iconPreset.iconClass
	};
};
