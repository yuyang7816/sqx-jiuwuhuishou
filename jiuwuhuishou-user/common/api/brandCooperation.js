import { apiPostJson } from './http.js';

export const saveBrandCooperation = (payload) => apiPostJson('/app/brand/cooperation/save', payload);
