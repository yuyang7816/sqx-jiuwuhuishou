import config from '../config.js';
import * as addressApi from '../api/address.js';
import {
	getMockAddresses,
	getMockAddressById,
	saveMockAddress,
	deleteMockAddress,
	formatAddressCard as mockFormatAddressCard
} from '../mock-data.js';
import {
	adaptAddressFromApi,
	formatAddressCard,
	toAddressSavePayload,
	toAddressUpdatePayload,
	toBookingAddressOption
} from '../adapters/address.js';

export { formatAddressCard, toBookingAddressOption };

export const listAddresses = async () => {
	if (config.USE_MOCK) {
		return getMockAddresses();
	}
	const list = await addressApi.fetchAddressList();
	return (list || []).map(adaptAddressFromApi);
};

export const getAddressById = async (id) => {
	if (config.USE_MOCK) {
		return getMockAddressById(id);
	}
	const list = await listAddresses();
	return list.find((item) => `${item.id}` === `${id}`) || null;
};

export const saveAddressInfo = async (payload, id) => {
	if (config.USE_MOCK) {
		return saveMockAddress({ id, ...payload });
	}
	if (id) {
		await addressApi.updateAddress(toAddressUpdatePayload(id, payload));
		return;
	}
	await addressApi.saveAddress(toAddressSavePayload(payload));
};

export const removeAddress = async (id) => {
	if (config.USE_MOCK) {
		deleteMockAddress(id);
		return;
	}
	await addressApi.deleteAddress(Number(id));
};

export const setAddressAsDefault = async (id) => {
	if (config.USE_MOCK) {
		const address = getMockAddressById(id);
		if (!address) {
			throw new Error('地址不存在');
		}
		saveMockAddress({
			id: address.id,
			contact: address.name,
			mobile: address.mobile,
			region: address.region,
			address: address.address,
			tag: address.tag || address.label,
			isDefault: true
		});
		return;
	}
	await addressApi.setDefaultAddress(Number(id));
};

export const listBookingAddresses = async () => {
	const list = await listAddresses();
	return list.map((item) => {
		const card = config.USE_MOCK ? mockFormatAddressCard(item) : formatAddressCard(item);
		return {
			id: item.id,
			name: item.tag || item.label || '常用地址',
			address: card.detail,
			user: card.userText,
			phone: card.phoneText || item.phone || item.mobile || '',
			default: item.default,
			icon: item.icon || 'home',
			province: item.province,
			city: item.city,
			district: item.district,
			region: item.region || [item.province, item.city, item.district].filter(Boolean)
		};
	});
};

export const getDefaultAddress = async () => {
	const list = await listBookingAddresses();
	return list.find((item) => item.default) || list[0] || null;
};
