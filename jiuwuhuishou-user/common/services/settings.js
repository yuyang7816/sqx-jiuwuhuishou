import config from '../config.js';
import { fetchProfile } from './auth.js';
import * as settingsApi from '../api/settings.js';

const PRESERVE_STORAGE_KEYS = ['token', 'userId', 'phone', 'openId', 'userType', 'messageNotifyEnabled', 'invitationCode'];

export const formatCacheSizeText = (sizeKb = 0) => {
	const size = Number(sizeKb || 0);
	if (size < 1024) {
		return `${size}KB`;
	}
	return `${(size / 1024).toFixed(1)}MB`;
};

export const getCacheSizeText = () => {
	try {
		const info = uni.getStorageInfoSync();
		return formatCacheSizeText(info.currentSize || 0);
	} catch (error) {
		return '0KB';
	}
};

export const clearAppCache = async () => {
	const preserved = {};
	PRESERVE_STORAGE_KEYS.forEach((key) => {
		const value = uni.getStorageSync(key);
		if (value !== '' && value !== undefined && value !== null) {
			preserved[key] = value;
		}
	});
	uni.clearStorageSync();
	Object.entries(preserved).forEach(([key, value]) => {
		uni.setStorageSync(key, value);
	});
	return getCacheSizeText();
};

export const adaptMessageNotifyFromApi = (data = {}) => data.messageNotifyEnabled !== false;

export const loadSettingsPageData = async () => {
	if (config.USE_MOCK) {
		const stored = uni.getStorageSync('messageNotifyEnabled');
		return {
			messageNotifyEnabled: stored === '' ? true : stored === '1'
		};
	}
	const profile = await fetchProfile();
	return {
		messageNotifyEnabled: profile.messageNotifyEnabled !== false
	};
};

export const updateMessageNotifyEnabled = async (enabled) => {
	if (config.USE_MOCK) {
		uni.setStorageSync('messageNotifyEnabled', enabled ? '1' : '0');
		return { messageNotifyEnabled: !!enabled };
	}
	const data = await settingsApi.updateMessageNotifyEnabled({ enabled: !!enabled });
	return {
		messageNotifyEnabled: adaptMessageNotifyFromApi(data)
	};
};
