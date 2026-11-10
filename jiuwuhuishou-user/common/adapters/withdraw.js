import { formatCommissionAmount, formatCurrencyText } from './referral.js';

export const DEFAULT_WITHDRAW_MIN_AMOUNT = 10;
export const DEFAULT_WITHDRAW_QUICK_AMOUNTS = [100, 300, 500];

export const DEFAULT_ALIPAY_ACCOUNT = {
	id: null,
	accountName: '',
	accountNumber: ''
};

export const DEFAULT_WECHAT_ACCOUNT = {
	id: null,
	qrImage: ''
};

const parseJsonArray = (value, fallback = []) => {
	if (!value) {
		return fallback;
	}
	try {
		const parsed = JSON.parse(value);
		return Array.isArray(parsed) ? parsed : fallback;
	} catch (error) {
		return fallback;
	}
};

const normalizeQuickAmounts = (value) => {
	if (Array.isArray(value)) {
		return value
			.map((item) => Number(item))
			.filter((amount) => !Number.isNaN(amount) && amount > 0);
	}
	const source = parseJsonArray(value, DEFAULT_WITHDRAW_QUICK_AMOUNTS);
	return source
		.map((item) => Number(item))
		.filter((amount) => !Number.isNaN(amount) && amount > 0);
};

export const adaptWithdrawPageData = ({
	balance = 0,
	minAmount = DEFAULT_WITHDRAW_MIN_AMOUNT,
	quickAmounts = DEFAULT_WITHDRAW_QUICK_AMOUNTS
} = {}) => {
	const availableBalance = Number(balance || 0);
	const minWithdrawAmount = Math.max(0, Number(minAmount || DEFAULT_WITHDRAW_MIN_AMOUNT));
	const amountOptions = normalizeQuickAmounts(quickAmounts);

	return {
		availableBalance,
		availableBalanceText: formatCurrencyText(availableBalance),
		minAmount: minWithdrawAmount,
		quickAmounts: amountOptions.length ? amountOptions : DEFAULT_WITHDRAW_QUICK_AMOUNTS,
		heroDesc: `可提现金额为钱包余额，最低提现 ${minWithdrawAmount} 元，提交后预计 24 小时内完成审核。`,
		tipsPrefix: `提现说明：从钱包余额提至`,
		tipsSuffix: `，单笔最低 ${minWithdrawAmount} 元。`
	};
};

export const buildWithdrawQuickOptions = (quickAmounts = [], availableBalance = 0) => {
	const options = (quickAmounts.length ? quickAmounts : DEFAULT_WITHDRAW_QUICK_AMOUNTS).map((amount) => ({
		key: `amount-${amount}`,
		label: `${formatCommissionAmount(amount)}`,
		value: `${amount}`
	}));

	options.push({
		key: 'all',
		label: '全部提现',
		value: formatCommissionAmount(availableBalance)
	});

	return options;
};

export const adaptAlipayAccountFromApi = (data) => {
	const source = data || {};
	return {
		id: source.id || null,
		accountName: source.accountName || '',
		accountNumber: source.accountNumber || ''
	};
};

export const adaptWechatAccountFromApi = (data) => {
	const source = data || {};
	return {
		id: source.id || null,
		qrImage: source.accountNumber || ''
	};
};

export const toAlipayAccountPayload = (account = {}) => ({
	accountName: account.accountName?.trim() || '',
	accountNumber: account.accountNumber?.trim() || ''
});

export const toWechatAccountPayload = (account = {}) => ({
	accountName: '微信收款码',
	accountNumber: account.qrImage || ''
});

const WITHDRAW_STATUS_MAP = {
	1: { label: '审核中', className: 'is-pending' },
	2: { label: '提现成功', className: 'is-success' },
	3: { label: '已拒绝', className: 'is-reject' }
};

const WITHDRAW_ACCOUNT_MAP = {
	1: '微信',
	2: '支付宝'
};

const formatDateTime = (value = '') => {
	if (!value) {
		return '';
	}
	return `${value}`.replace('T', ' ').slice(0, 16);
};

export const adaptWithdrawRecordFromApi = (item = {}) => {
	const statusMeta = WITHDRAW_STATUS_MAP[item.status] || WITHDRAW_STATUS_MAP[1];
	return {
		id: item.id,
		amount: Number(item.amount || 0),
		amountText: formatCurrencyText(item.amount || 0),
		actualAmountText: item.actualAmount ? formatCurrencyText(item.actualAmount) : '',
		accountTypeText: WITHDRAW_ACCOUNT_MAP[item.accountType] || '未知账户',
		status: item.status,
		statusText: statusMeta.label,
		statusClass: statusMeta.className,
		reason: item.reason || '',
		time: formatDateTime(item.createTime || item.withdrawTime)
	};
};

export const adaptWithdrawRecordPageFromApi = (page = {}) => ({
	records: (page.records || []).map(adaptWithdrawRecordFromApi),
	total: Number(page.total || 0)
});
