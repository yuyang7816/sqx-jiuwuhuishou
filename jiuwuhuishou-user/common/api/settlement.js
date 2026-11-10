import { apiPostJson } from './http.js';

export const fetchSettlementInfo = (payload) => apiPostJson('/app/settlement/info', payload);

export const confirmSettlement = (payload) => apiPostJson('/app/settlement/confirm', payload);

export const disputeSettlement = (payload) => apiPostJson('/app/settlement/dispute', payload);
