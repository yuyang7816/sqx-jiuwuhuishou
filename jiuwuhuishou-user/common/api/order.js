import { apiPostJson } from './http.js';

export const fetchOrderPage = (payload) => apiPostJson('/app/order/page', payload);

export const fetchOrderInfo = (id) => apiPostJson('/app/order/info', { id: Number(id) });

export const createClothesOrder = (payload) => apiPostJson('/app/order/clothes/save', payload);

export const createApplianceOrder = (payload) => apiPostJson('/app/order/appliance/save', payload);

export const createDigitalOrder = (payload) => apiPostJson('/app/order/digital/save', payload);

export const cancelOrder = (orderId) => apiPostJson('/app/order/cancel', { orderId: Number(orderId) });

export const rescheduleOrder = (payload) => apiPostJson('/app/order/reschedule', payload);

export const submitOrderException = (payload) => apiPostJson('/app/order/exception', payload);

export const fetchOrderCourierInfo = (id) => apiPostJson('/app/order/courier/info', { id: Number(id) });

export const fetchExpressTracks = (id) => apiPostJson('/app/order/express/tracks', { id: Number(id) });

export const shipMailOrder = (payload) => apiPostJson('/app/order/mail/ship', {
	orderId: Number(payload.orderId),
	expressNo: payload.expressNo,
	expressCompany: payload.expressCompany || '顺丰速运'
});
