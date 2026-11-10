import { apiPostJsonNoAuth } from './http.js';

export const fetchHelpArticlePage = (payload) => apiPostJsonNoAuth('/app/help/article/page', payload);

export const fetchHelpArticleInfo = (payload) => apiPostJsonNoAuth('/app/help/article/info', payload);
