import { apiGet, apiPostJson } from './http.js';

export const WITHDRAWAL_ACCOUNT_TYPE = {
	ALIPAY: 1,
	WECHAT: 2
};

// 提现记录 accountType：1 微信 2 支付宝（与提现账号 type 定义不同）
export const WITHDRAW_RECORD_ACCOUNT_TYPE = {
	WECHAT: 1,
	ALIPAY: 2
};

export const fetchUserWithdrawalAccount = (type) => apiGet('/app/withdrawalAccount/getUserWithdrawalAccount', { type });

export const saveWithdrawalAccount = (payload) => apiPostJson('/app/withdrawalAccount/save', payload);

export const updateWithdrawalAccount = (payload) => apiPostJson('/app/withdrawalAccount/update', payload);

export const saveWithdrawRecord = (payload) => apiPostJson('/app/withdrawRecord/save', payload);

export const fetchWithdrawRecordPage = (payload) => apiPostJson('/app/withdrawRecord/page', payload);
