import { apiGet, apiPostJson } from './http.js';

export const fetchAddressList = () => apiGet('/app/address/list');

export const saveAddress = (payload) => apiPostJson('/app/address/save', payload);

export const updateAddress = (payload) => apiPostJson('/app/address/update', payload);

export const deleteAddress = (id) => apiPostJson('/app/address/delete', { id });

export const setDefaultAddress = (id) => apiPostJson('/app/address/setDefault', { id });
