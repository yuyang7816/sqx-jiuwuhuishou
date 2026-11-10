import { apiGet, apiPostJson } from './http.js';

export const fetchPickupTaskStats = () => apiGet('/app/pickup/task/stats');

export const fetchPickupTaskPage = (payload) => apiPostJson('/app/pickup/task/page', payload);

export const fetchPickupTaskInfo = (payload) => apiPostJson('/app/pickup/task/info', payload);

export const startPickupTask = (payload) => apiPostJson('/app/pickup/task/start', payload);

export const finishPickupTask = (payload) => apiPostJson('/app/pickup/task/finish', payload);

export const updatePickupActualAmount = (payload) => apiPostJson('/app/pickup/task/updateActual', payload);

export const cancelPickupTask = (payload) => apiPostJson('/app/pickup/task/cancel', payload);
