import config from '../config.js';
import * as vipApi from '../api/vip.js';
import { fetchProfile } from './auth.js';
import {
	adaptVipPageData,
	adaptVipPlanFromApi,
	adaptVipRightFromApi
} from '../adapters/vip.js';

const MOCK_PLANS = [
	{ id: 1, name: '月度会员', month: 1, amount: 19.9, originalAmount: 29.9, sort: 1 },
	{ id: 2, name: '季度会员', month: 3, amount: 49.9, originalAmount: 89.7, sort: 2 },
	{ id: 3, name: '年度会员', month: 12, amount: 168, originalAmount: 358.8, sort: 3 }
];

const MOCK_RIGHTS = [
	{ id: 1, name: '优先上门', img: '', sort: 1 },
	{ id: 2, name: '专属客服', img: '', sort: 2 },
	{ id: 3, name: '加价回收', img: '', sort: 3 },
	{ id: 4, name: '免手续费提现', img: '', sort: 4 }
];

export const loadVipPageData = async () => {
	if (config.USE_MOCK) {
		return adaptVipPageData({
			isVip: false,
			plans: MOCK_PLANS.map(adaptVipPlanFromApi),
			rights: MOCK_RIGHTS.map(adaptVipRightFromApi)
		});
	}
	const [profile, plans, rights] = await Promise.all([
		fetchProfile().catch(() => ({ isVip: false })),
		vipApi.fetchVipPlanList().catch(() => []),
		vipApi.fetchVipRightsList().catch(() => [])
	]);
	return adaptVipPageData({
		isVip: !!profile?.isVip,
		plans: (plans || []).map(adaptVipPlanFromApi),
		rights: (rights || []).map(adaptVipRightFromApi)
	});
};

const parsePayParams = (payload) => {
	if (!payload) {
		return null;
	}
	if (typeof payload === 'string') {
		try {
			return JSON.parse(payload);
		} catch (error) {
			return null;
		}
	}
	return payload;
};

const requestWeixinPayment = (payParams) => new Promise((resolve, reject) => {
	uni.requestPayment({
		provider: 'wxpay',
		timeStamp: payParams.timeStamp,
		nonceStr: payParams.nonceStr,
		package: payParams.package,
		signType: payParams.signType || 'RSA',
		paySign: payParams.paySign,
		success: resolve,
		fail: reject
	});
});

export const purchaseVipPlan = async (plan) => {
	if (!plan?.id) {
		throw new Error('请选择会员套餐');
	}
	if (config.USE_MOCK) {
		return { success: true, mock: true };
	}
	const userId = Number(uni.getStorageSync('userId') || 0);
	const openId = uni.getStorageSync('openId') || '';
	if (!userId) {
		throw new Error('请先登录');
	}
	if (!openId) {
		throw new Error('请使用微信登录后再开通会员');
	}
	const payResult = await vipApi.createVipPayOrder({
		businessType: 'vip',
		tradeType: 'JSAPI',
		channel: 'wx',
		amount: plan.amount,
		userId,
		openId,
		redirectUri: '/packageCommon/user/vip',
		'extensions[planId]': plan.id
	});
	const payParams = parsePayParams(payResult);
	if (!payParams?.timeStamp || !payParams?.paySign) {
		throw new Error('支付参数获取失败，请稍后重试');
	}
	await requestWeixinPayment(payParams);
	return { success: true };
};
