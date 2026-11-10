import { apiGet, apiGetNoAuth, apiPostJson } from './http.js';

export const fetchWelfareSummary = () => apiGet('/app/welfare/summary');

export const fetchWelfareTasks = () => apiGetNoAuth('/app/welfare/tasks');

export const signWelfare = () => apiPostJson('/app/welfare/sign');

export const updateContinuousSignEnabled = (payload) => apiPostJson('/app/welfare/continuous-sign', payload);

export const withdrawWelfare = (payload) => apiPostJson('/app/welfare/withdraw', payload);

export const fetchWelfareCoinPage = (payload) => apiPostJson('/app/welfare/coin/page', payload);
