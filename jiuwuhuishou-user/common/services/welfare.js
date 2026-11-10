import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';
import * as welfareApi from '../api/welfare.js';
import {
	adaptSignRuleConfig,
	adaptWelfareSignResultFromApi,
	adaptWelfareSummaryFromApi,
	adaptWelfareTaskFromApi,
	adaptWelfareWithdrawResultFromApi,
	adaptWelfareCoinLogFromApi,
	buildSignDayNodes,
	createWelfareTaskAction
} from '../adapters/welfare.js';
import { sortByOrder as sortHomeByOrder } from '../adapters/home.js';

const DEFAULT_SUMMARY = {
	coinBalance: 0,
	continuousSignDays: 0,
	continuousSignEnabled: true,
	signedToday: false
};

const DEFAULT_SIGN_RULE = {
	totalDays: 7,
	dailyCoin: 5
};

const DEFAULT_WITHDRAW_MIN = 10;
const DEFAULT_COIN_WALLET_RATE = 1;

const DEFAULT_TASKS = [
	{
		id: 1,
		title: '邀请好友',
		desc: '最高得 50 元 / 人',
		icon: 'account',
		btnText: '领任务',
		url: '/packageCommon/user/invite',
		sort: 1
	},
	{
		id: 2,
		title: '发布回收',
		desc: '晒单赢实物好礼',
		icon: 'edit-pen',
		btnText: '领任务',
		url: '/pages/index/index',
		sort: 2
	}
];

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

export const fetchWelfareSummary = async () => {
	if (config.USE_MOCK) {
		return { ...DEFAULT_SUMMARY, coinBalance: 1284, continuousSignDays: 3, signedToday: false };
	}
	const data = await welfareApi.fetchWelfareSummary();
	return adaptWelfareSummaryFromApi(data);
};

export const fetchSignRuleConfig = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_SIGN_RULE;
	}
	const [totalDaysValue, dailyCoinValue] = await Promise.all([
		fetchConfigSafe('welfare_sign_total_days', `${DEFAULT_SIGN_RULE.totalDays}`),
		fetchConfigSafe('welfare_sign_daily_coin', `${DEFAULT_SIGN_RULE.dailyCoin}`)
	]);
	return adaptSignRuleConfig(totalDaysValue, dailyCoinValue);
};

export const fetchWithdrawMinAmount = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_WITHDRAW_MIN;
	}
	const value = await fetchConfigSafe('welfare_withdraw_min_amount', `${DEFAULT_WITHDRAW_MIN}`);
	const amount = Number(value);
	return Number.isNaN(amount) ? DEFAULT_WITHDRAW_MIN : amount;
};

export const fetchCoinWalletRate = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_COIN_WALLET_RATE;
	}
	const value = await fetchConfigSafe('welfare_coin_wallet_rate', `${DEFAULT_COIN_WALLET_RATE}`);
	const rate = Number(value);
	return !Number.isNaN(rate) && rate > 0 ? rate : DEFAULT_COIN_WALLET_RATE;
};

export const calcWalletCreditFromCoins = (coins, rate = DEFAULT_COIN_WALLET_RATE) => {
	const amount = Number(coins) * Number(rate);
	if (Number.isNaN(amount) || amount <= 0) {
		return 0;
	}
	return Math.round(amount * 100) / 100;
};

export const fetchWelfareTasks = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_TASKS.map((item) => ({
			...item,
			action: createWelfareTaskAction(item)
		}));
	}
	const list = sortHomeByOrder((await welfareApi.fetchWelfareTasks() || []).map(adaptWelfareTaskFromApi));
	const tasks = (list.length ? list : DEFAULT_TASKS).map((item) => ({
		...item,
		action: createWelfareTaskAction(item)
	}));
	return tasks;
};

export const signWelfare = async () => {
	if (config.USE_MOCK) {
		return {
			coinReward: DEFAULT_SIGN_RULE.dailyCoin,
			continuousSignDays: 4,
			coinBalance: 1289
		};
	}
	const data = await welfareApi.signWelfare();
	return adaptWelfareSignResultFromApi(data);
};

export const updateContinuousSignEnabled = async (enabled) => {
	if (config.USE_MOCK) {
		return {
			...DEFAULT_SUMMARY,
			coinBalance: 1284,
			continuousSignDays: enabled ? 3 : 0,
			continuousSignEnabled: !!enabled
		};
	}
	const data = await welfareApi.updateContinuousSignEnabled({ enabled: !!enabled });
	return adaptWelfareSummaryFromApi(data);
};

export const withdrawWelfare = async (amount) => {
	const withdrawAmount = Number(amount);
	if (config.USE_MOCK) {
		const [summary, rate] = await Promise.all([fetchWelfareSummary(), fetchCoinWalletRate()]);
		const nextBalance = Math.max(0, summary.coinBalance - withdrawAmount);
		return {
			withdrawAmount,
			coinBalance: nextBalance,
			walletAmount: calcWalletCreditFromCoins(withdrawAmount, rate)
		};
	}
	const data = await welfareApi.withdrawWelfare({ amount: withdrawAmount });
	return adaptWelfareWithdrawResultFromApi(data);
};

export const refreshWelfareSummary = async () => {
	const summary = await fetchWelfareSummary();
	return {
		summary,
		signDays: buildSignDayNodes({
			continuousSignDays: summary.continuousSignDays,
			signedToday: summary.signedToday,
			totalDays: (await fetchSignRuleConfig()).totalDays
		})
	};
};

export const loadWelfarePageData = async () => {
	const [summary, signRule, tasks, withdrawMinAmount, coinWalletRate] = await Promise.all([
		fetchWelfareSummary(),
		fetchSignRuleConfig(),
		fetchWelfareTasks(),
		fetchWithdrawMinAmount(),
		fetchCoinWalletRate()
	]);
	return {
		summary,
		signRule,
		tasks,
		withdrawMinAmount,
		coinWalletRate,
		signDays: buildSignDayNodes({
			continuousSignDays: summary.continuousSignDays,
			signedToday: summary.signedToday,
			totalDays: signRule.totalDays
		})
	};
};

const DEFAULT_COIN_LOGS = [
	{
		id: 1,
		type: 'income',
		title: '每日签到',
		desc: '签到日期 2026-09-29',
		time: '今天 08:20',
		amount: '+5',
		status: '已到账',
		icon: 'gift',
		iconColor: '#cf1432',
		iconClass: 'is-red',
		amountClass: 'is-income'
	}
];

export const loadCoinDetailPageData = async () => {
	const [summary, logs] = await Promise.all([
		fetchWelfareSummary(),
		(async () => {
			if (config.USE_MOCK) {
				return DEFAULT_COIN_LOGS;
			}
			const page = await welfareApi.fetchWelfareCoinPage({ page: 1, limit: 100 });
			return (page?.records || []).map(adaptWelfareCoinLogFromApi);
		})()
	]);
	return { summary, logs };
};

export { buildSignDayNodes, formatCoinBalance, filterCoinLogs } from '../adapters/welfare.js';
