import { apiGetNoAuth, apiPostJson } from './http.js';

export const fetchServiceMessagePage = (payload) => apiPostJson('/app/service/message/page', payload);

export const saveServiceMessage = (payload) => apiPostJson('/app/service/message/save', payload);

export const askServiceQuickQuestion = (payload) => apiPostJson('/app/service/message/quick', payload);

export const fetchServiceQuickQuestions = () => apiGetNoAuth('/app/service/config/quickQuestion/list');

export const fetchServiceShortcuts = () => apiGetNoAuth('/app/service/config/shortcut/list');
