import { apiPostJson } from './http.js';

export const fetchPayoutInfo = (payload) => apiPostJson('/app/payout/info', payload);
