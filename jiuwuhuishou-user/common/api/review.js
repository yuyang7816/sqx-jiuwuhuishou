import { apiPostJson } from './http.js';

export const fetchReviewInfo = (payload) => apiPostJson('/app/review/info', payload);

export const saveReview = (payload) => apiPostJson('/app/review/save', payload);
