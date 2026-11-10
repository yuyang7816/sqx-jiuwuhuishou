import { apiPostJson } from './http.js';

export const fetchUserNotificationPage = (payload) => apiPostJson('/app/messages/page', payload);

export const fetchNotificationInfo = (id) => apiPostJson('/app/messages/info', { id: Number(id) });

export const markNotificationRead = (id) => apiPostJson('/app/messages/update', {
	id: Number(id),
	isRead: true
});
