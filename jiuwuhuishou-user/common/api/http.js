import { get, postJson, postJsonNoAuth, getNoAuth } from '../httpRequest.js';

export const isSuccess = (res) => res && (res.code === 0 || res.code === '0');

export const assertSuccess = (res) => {
	if (!isSuccess(res)) {
		throw new Error(res?.msg || '请求失败');
	}
	return res.data;
};

export const apiGet = async (url, data = {}) => assertSuccess(await get(url, data));

export const apiGetNoAuth = async (url, data = {}) => assertSuccess(await getNoAuth(url, data));

export const apiPostJson = async (url, data = {}) => assertSuccess(await postJson(url, data));

export const apiPostJsonNoAuth = async (url, data = {}) => assertSuccess(await postJsonNoAuth(url, data));
