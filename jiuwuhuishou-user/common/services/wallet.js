import config from '../config.js';
import * as walletApi from '../api/wallet.js';
import { adaptWalletTransactionFromApi, filterWalletTransactions } from '../adapters/wallet.js';
import { DEFAULT_WALLET_SUMMARY, loadMyWalletSummary } from './my.js';

const DEFAULT_TRANSACTIONS = [
	{
		id: 1,
		type: 'income',
		title: '旧衣回收到账',
		desc: '订单 RE20260701001 完成结算',
		time: '今天 14:20',
		amount: '+￥38.50',
		status: '已到账',
		icon: 'gift',
		iconColor: '#cf1432',
		iconClass: 'is-red',
		amountClass: 'is-income'
	},
	{
		id: 2,
		type: 'income',
		title: '邀请奖励到账',
		desc: '好友首单回收奖励发放',
		time: '今天 10:12',
		amount: '+￥12.00',
		status: '已到账',
		icon: 'share-square',
		iconColor: '#8a6400',
		iconClass: 'is-gold',
		amountClass: 'is-income'
	},
	{
		id: 3,
		type: 'withdraw',
		title: '微信提现申请',
		desc: '提现到微信零钱',
		time: '昨天 18:36',
		amount: '-￥200.00',
		status: '审核中',
		icon: 'coupon',
		iconColor: '#6d6a6a',
		iconClass: 'is-gray',
		amountClass: 'is-withdraw'
	},
	{
		id: 4,
		type: 'withdraw',
		title: '微信提现成功',
		desc: '已转入张**微信零钱账户',
		time: '06-29 09:18',
		amount: '-￥500.00',
		status: '已完成',
		icon: 'checkmark-circle',
		iconColor: '#00b96b',
		iconClass: 'is-green',
		amountClass: 'is-withdraw'
	}
];

const fetchWalletTransactions = async () => {
	const page = await walletApi.fetchWalletTransactionPage({
		page: 1,
		limit: 100
	});
	return (page?.records || []).map(adaptWalletTransactionFromApi);
};

export const loadBalanceDetailPageData = async () => {
	const [walletSummary, transactions] = await Promise.all([
		loadMyWalletSummary(),
		config.USE_MOCK ? Promise.resolve(DEFAULT_TRANSACTIONS) : fetchWalletTransactions()
	]);

	return {
		walletSummary,
		transactions
	};
};

export { filterWalletTransactions, DEFAULT_TRANSACTIONS };
