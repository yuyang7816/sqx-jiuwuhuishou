import { apiGet, apiPostJson } from './http.js';

export const fetchReferralCommissionInfo = () => apiGet('/app/referral/userCommission/info');

export const fetchReferralData = () => apiGet('/app/referral/userCommission/referralData');

export const fetchReferralCommissionPage = (payload) => apiPostJson('/app/referral/commission/page', payload);

export const fetchReferralUserList = () => apiGet('/app/referral/userCommission/referralUserList');

export const fetchReferralList = () => apiGet('/app/referral/userCommission/referralList');

export const fetchReferralLeaderboard = () => apiGet('/app/referral/leaderboard');

export const fetchInvitePoster = () => apiGet('/app/referral/userCommission/poster');

export const withdrawCommissionToWallet = (payload) => apiPostJson('/app/referral/userCommission/withdraw', payload);
