import config from '../config.js';
import * as walletApi from '../api/wallet.js';
import * as referralApi from '../api/referral.js';
import * as welfareApi from '../api/welfare.js';
import { adaptMyWalletSummary } from '../adapters/my.js';

const DEFAULT_WALLET_SUMMARY = adaptMyWalletSummary({
	balance: 0,
	pendingAmount: 0,
	ecoPoints: 0
});

const MOCK_WALLET_SUMMARY = adaptMyWalletSummary({
	balance: 2856.4,
	pendingAmount: 45,
	ecoPoints: 1240
});

const fetchWalletBalance = async () => {
	try {
		const wallet = await walletApi.fetchUserWallet();
		return Number(wallet?.amount || 0);
	} catch (error) {
		return 0;
	}
};

const fetchPendingAmount = async () => {
	try {
		const info = await referralApi.fetchReferralCommissionInfo();
		return Number(info?.commission || 0);
	} catch (error) {
		return 0;
	}
};

const fetchEcoPoints = async () => {
	try {
		const summary = await welfareApi.fetchWelfareSummary();
		return Number(summary?.coinBalance || 0);
	} catch (error) {
		return 0;
	}
};

export const loadMyWalletSummary = async () => {
	if (!config.USE_MOCK) {
		const [balance, pendingAmount, ecoPoints] = await Promise.all([
			fetchWalletBalance(),
			fetchPendingAmount(),
			fetchEcoPoints()
		]);
		return adaptMyWalletSummary({ balance, pendingAmount, ecoPoints });
	}
	return MOCK_WALLET_SUMMARY;
};

export { DEFAULT_WALLET_SUMMARY };
