import { formatCoinBalance } from './welfare.js';
import { formatCommissionAmount, formatCurrencyText } from './referral.js';

export const adaptMyWalletSummary = ({
	balance = 0,
	pendingAmount = 0,
	ecoPoints = 0
} = {}) => {
	const balanceText = formatWalletBalance(balance);
	const pendingAmountText = formatCurrencyText(pendingAmount);
	const ecoPointsText = formatEcoPoints(ecoPoints);

	return {
		balance: Number(balance || 0),
		pendingAmount: Number(pendingAmount || 0),
		ecoPoints: Number(ecoPoints || 0),
		balanceText,
		pendingAmountText,
		ecoPointsText,
		subText: `预计待入账 ${pendingAmountText} · 环保积分 ${ecoPointsText}`
	};
};

export const formatWalletBalance = (value = 0) => formatCommissionAmount(value);

export const formatEcoPoints = (value = 0) => formatCoinBalance(value);
