import { apiGet, apiPostJson } from './http.js';

export const fetchUserWallet = () => apiGet('/app/userWallet/wallet');

export const fetchWalletTransactionPage = (payload) => apiPostJson('/app/userWalletTransaction/page', payload);
