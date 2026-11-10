import { apiPostJson } from './http.js';

export const updateMessageNotifyEnabled = (payload) => apiPostJson('/app/base/user/messageNotify', payload);
