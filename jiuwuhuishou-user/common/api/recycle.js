import { apiPostJsonNoAuth } from './http.js';

export const checkServiceArea = (payload) => apiPostJsonNoAuth('/app/recycle/serviceArea/check', payload);
