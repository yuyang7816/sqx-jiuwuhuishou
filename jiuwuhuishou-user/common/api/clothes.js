import { apiGetNoAuth } from './http.js';

export const fetchClothesCategories = () => apiGetNoAuth('/app/recycle/clothes/category/list');

export const fetchClothesEstimateConfig = () => apiGetNoAuth('/app/recycle/clothes/estimate/config');
