import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';
import * as walletApi from '../api/wallet.js';
import * as withdrawApi from '../api/withdraw.js';
import {
	adaptAlipayAccountFromApi,
	adaptWechatAccountFromApi,
	adaptWithdrawPageData,
	adaptWithdrawRecordPageFromApi,
	DEFAULT_ALIPAY_ACCOUNT,
	DEFAULT_WECHAT_ACCOUNT,
	DEFAULT_WITHDRAW_MIN_AMOUNT,
	DEFAULT_WITHDRAW_QUICK_AMOUNTS,
	toAlipayAccountPayload,
	toWechatAccountPayload
} from '../adapters/withdraw.js';

const WITHDRAW_MIN_CONFIG_KEY = 'sys_withdraw_min';
const WITHDRAW_AMOUNT_OPTIONS_CONFIG_KEY = 'sys_withdraw_amount_options';

const DEFAULT_PAGE_DATA = adaptWithdrawPageData();

const MOCK_PAGE_DATA = adaptWithdrawPageData({
	balance: 2856.4,
	minAmount: 10,
	quickAmounts: DEFAULT_WITHDRAW_QUICK_AMOUNTS
});

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

const fetchWalletBalance = async () => {
	try {
		const wallet = await walletApi.fetchUserWallet();
		return Number(wallet?.amount || 0);
	} catch (error) {
		return 0;
	}
};

const fetchWithdrawMinAmount = async () => {
	const value = await fetchConfigSafe(WITHDRAW_MIN_CONFIG_KEY, `${DEFAULT_WITHDRAW_MIN_AMOUNT}`);
	const amount = Number(value);
	return Number.isNaN(amount) ? DEFAULT_WITHDRAW_MIN_AMOUNT : amount;
};

const fetchWithdrawAmountOptions = async () => {
	const value = await fetchConfigSafe(
		WITHDRAW_AMOUNT_OPTIONS_CONFIG_KEY,
		JSON.stringify(DEFAULT_WITHDRAW_QUICK_AMOUNTS)
	);
	return value;
};

export const loadWithdrawPageData = async () => {
	if (config.USE_MOCK) {
		return MOCK_PAGE_DATA;
	}

	const [balance, minAmount, quickAmountsValue] = await Promise.all([
		fetchWalletBalance(),
		fetchWithdrawMinAmount(),
		fetchWithdrawAmountOptions()
	]);

	return adaptWithdrawPageData({
		balance,
		minAmount,
		quickAmounts: quickAmountsValue
	});
};

const fetchWithdrawalAccountSafe = async (type) => {
	try {
		const account = await withdrawApi.fetchUserWithdrawalAccount(type);
		return account || null;
	} catch (error) {
		return null;
	}
};

export const loadWithdrawAccounts = async () => {
	if (config.USE_MOCK) {
		return {
			alipayAccount: { ...DEFAULT_ALIPAY_ACCOUNT },
			wechatAccount: { ...DEFAULT_WECHAT_ACCOUNT }
		};
	}

	const [alipayData, wechatData] = await Promise.all([
		fetchWithdrawalAccountSafe(withdrawApi.WITHDRAWAL_ACCOUNT_TYPE.ALIPAY),
		fetchWithdrawalAccountSafe(withdrawApi.WITHDRAWAL_ACCOUNT_TYPE.WECHAT)
	]);

	return {
		alipayAccount: adaptAlipayAccountFromApi(alipayData),
		wechatAccount: adaptWechatAccountFromApi(wechatData)
	};
};

const upsertWithdrawalAccount = async ({ id, type, accountName, accountNumber }) => {
	const userId = Number(uni.getStorageSync('userId') || 0);
	const payload = {
		type,
		accountName,
		accountNumber
	};

	if (id) {
		await withdrawApi.updateWithdrawalAccount({
			id,
			userId,
			...payload
		});
		return id;
	}

	await withdrawApi.saveWithdrawalAccount(payload);
	const saved = await fetchWithdrawalAccountSafe(type);
	return saved?.id || null;
};

export const saveAlipayWithdrawAccount = async (account = {}) => {
	const form = toAlipayAccountPayload(account);
	if (!form.accountName) {
		throw new Error('请输入支付宝名称');
	}
	if (!form.accountNumber) {
		throw new Error('请输入支付宝账号');
	}

	if (config.USE_MOCK) {
		return {
			id: account.id || 1,
			...form
		};
	}

	const id = await upsertWithdrawalAccount({
		id: account.id,
		type: withdrawApi.WITHDRAWAL_ACCOUNT_TYPE.ALIPAY,
		...form
	});

	return adaptAlipayAccountFromApi({
		id,
		...form
	});
};

export const saveWechatWithdrawAccount = async (account = {}) => {
	const form = toWechatAccountPayload(account);
	if (!form.accountNumber) {
		throw new Error('请上传微信收款码');
	}

	if (config.USE_MOCK) {
		return {
			id: account.id || 2,
			qrImage: form.accountNumber
		};
	}

	const id = await upsertWithdrawalAccount({
		id: account.id,
		type: withdrawApi.WITHDRAWAL_ACCOUNT_TYPE.WECHAT,
		...form
	});

	return adaptWechatAccountFromApi({
		id,
		accountNumber: form.accountNumber
	});
};

export const submitWithdrawRequest = async ({ amount, withdrawType = 'wechat' } = {}) => {
	const withdrawAmount = Number(amount);
	if (!withdrawAmount || Number.isNaN(withdrawAmount)) {
		throw new Error('请输入提现金额');
	}

	const accountType = withdrawType === 'alipay'
		? withdrawApi.WITHDRAW_RECORD_ACCOUNT_TYPE.ALIPAY
		: withdrawApi.WITHDRAW_RECORD_ACCOUNT_TYPE.WECHAT;

	if (config.USE_MOCK) {
		return {
			amount: withdrawAmount,
			accountType
		};
	}

	await withdrawApi.saveWithdrawRecord({
		amount: withdrawAmount,
		accountType
	});

	return {
		amount: withdrawAmount,
		accountType
	};
};

export const loadWithdrawRecords = async ({ page = 1, limit = 50 } = {}) => {
	if (config.USE_MOCK) {
		return adaptWithdrawRecordPageFromApi({
			records: [
				{
					id: 1,
					amount: 100,
					actualAmount: 100,
					accountType: 1,
					status: 2,
					createTime: '2026-07-08 14:20:00'
				},
				{
					id: 2,
					amount: 300,
					accountType: 2,
					status: 1,
					createTime: '2026-07-10 09:10:00'
				}
			],
			total: 2
		});
	}
	const data = await withdrawApi.fetchWithdrawRecordPage({ page, limit });
	return adaptWithdrawRecordPageFromApi(data);
};

export { DEFAULT_PAGE_DATA };
