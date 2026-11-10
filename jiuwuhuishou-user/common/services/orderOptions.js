import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';
import {
	adaptCancelReasonsFromConfig,
	adaptExceptionOptionsFromConfig,
	DEFAULT_CANCEL_REASONS,
	DEFAULT_EXCEPTION_EXPECTS,
	DEFAULT_EXCEPTION_TYPES
} from '../adapters/orderOptions.js';

const CANCEL_REASONS_CONFIG_KEY = 'order_cancel_reasons';
const EXCEPTION_OPTIONS_CONFIG_KEY = 'order_exception_options';

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

export const loadCancelReasons = async () => {
	if (config.USE_MOCK) {
		return [...DEFAULT_CANCEL_REASONS];
	}
	const value = await fetchConfigSafe(CANCEL_REASONS_CONFIG_KEY);
	return adaptCancelReasonsFromConfig(value);
};

export const loadExceptionOptions = async () => {
	if (config.USE_MOCK) {
		return {
			types: [...DEFAULT_EXCEPTION_TYPES],
			expects: [...DEFAULT_EXCEPTION_EXPECTS]
		};
	}
	const value = await fetchConfigSafe(EXCEPTION_OPTIONS_CONFIG_KEY);
	return adaptExceptionOptionsFromConfig(value);
};
