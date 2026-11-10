import { apiGetNoAuth, apiPostJsonNoAuth } from './http.js';

export const fetchConfigValue = (key) => apiPostJsonNoAuth('/app/config/info', { key });

export const fetchBannerPage = (payload) => apiPostJsonNoAuth('/app/base/banner/page', payload);

export const fetchMessagePage = (payload) => apiPostJsonNoAuth('/app/messages/page', payload);

export const fetchRecycleCategories = () => apiGetNoAuth('/app/recycle/category/list');
