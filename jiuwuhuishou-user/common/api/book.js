import { apiGetNoAuth } from './http.js';

export const fetchBookCategories = () => apiGetNoAuth('/app/recycle/book/category/list');

export const fetchBookWeightOptions = () => apiGetNoAuth('/app/recycle/book/weightOption/list');

export const fetchBookEstimateConfig = () => apiGetNoAuth('/app/recycle/book/estimate/config');
