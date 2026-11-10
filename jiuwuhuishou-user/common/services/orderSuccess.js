import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';
import * as orderApi from '../api/order.js';
import {
	adaptCourierFromApi,
	adaptOrderSuccessConfig,
	DEFAULT_COURIER,
	DEFAULT_NEXT_ACTIONS,
	DEFAULT_PICKUP_DESC,
	DEFAULT_PICKUP_TITLE,
	DEFAULT_STATUS_DESC
} from '../adapters/orderSuccess.js';

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

export const loadOrderSuccessConfig = async () => {
	if (config.USE_MOCK) {
		return adaptOrderSuccessConfig();
	}
	return adaptOrderSuccessConfig({
		statusDesc: await fetchConfigSafe('order_success_status_desc', DEFAULT_STATUS_DESC),
		pickupTitle: await fetchConfigSafe('order_success_pickup_title', DEFAULT_PICKUP_TITLE),
		pickupDesc: await fetchConfigSafe('order_success_pickup_desc', DEFAULT_PICKUP_DESC),
		nextActionsJson: await fetchConfigSafe('order_success_next_actions', JSON.stringify(DEFAULT_NEXT_ACTIONS))
	});
};

export const fetchOrderCourier = async (orderId) => {
	if (config.USE_MOCK) {
		return adaptCourierFromApi(DEFAULT_COURIER);
	}
	const courier = await orderApi.fetchOrderCourierInfo(orderId);
	return adaptCourierFromApi(courier);
};

export const dialPhoneNumber = (mobile) => {
	if (!mobile) {
		return Promise.reject(new Error('暂无可用联系电话'));
	}
	return new Promise((resolve, reject) => {
		uni.makePhoneCall({
			phoneNumber: `${mobile}`,
			success: resolve,
			fail: reject
		});
	});
};
