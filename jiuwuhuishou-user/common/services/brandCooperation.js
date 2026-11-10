import config from '../config.js';
import * as brandCooperationApi from '../api/brandCooperation.js';
import {
	toBrandCooperationPayload,
	validateBrandCooperationForm
} from '../adapters/brandCooperation.js';

export const submitBrandCooperation = async (form = {}) => {
	const message = validateBrandCooperationForm(form);
	if (message) {
		throw new Error(message);
	}

	const payload = toBrandCooperationPayload(form);
	if (config.USE_MOCK) {
		return payload;
	}

	await brandCooperationApi.saveBrandCooperation(payload);
	return payload;
};
