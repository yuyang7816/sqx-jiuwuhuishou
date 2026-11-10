import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';
import {
	adaptAboutUsFromConfig,
	adaptPrivacyAgreementFromConfig,
	adaptServiceAgreementFromConfig,
	DEFAULT_ABOUT_SECTIONS,
	DEFAULT_ABOUT_TITLE,
	DEFAULT_PRIVACY_SECTIONS,
	DEFAULT_PRIVACY_TITLE,
	DEFAULT_SECTIONS,
	DEFAULT_TITLE
} from '../adapters/agreement.js';

const SERVICE_AGREEMENT_CONFIG_KEY = 'user_service_agreement';
const PRIVACY_AGREEMENT_CONFIG_KEY = 'user_privacy_agreement';
const ABOUT_US_CONFIG_KEY = 'user_about_us';

const loadConfigAgreement = async (key, adaptFn, mockPayload) => {
	if (config.USE_MOCK) {
		return adaptFn(JSON.stringify(mockPayload));
	}
	try {
		const data = await fetchConfigValue(key);
		return adaptFn(data?.value);
	} catch (error) {
		return adaptFn('');
	}
};

export const loadServiceAgreement = () =>
	loadConfigAgreement(SERVICE_AGREEMENT_CONFIG_KEY, adaptServiceAgreementFromConfig, {
		title: DEFAULT_TITLE,
		sections: DEFAULT_SECTIONS
	});

export const loadPrivacyAgreement = () =>
	loadConfigAgreement(PRIVACY_AGREEMENT_CONFIG_KEY, adaptPrivacyAgreementFromConfig, {
		title: DEFAULT_PRIVACY_TITLE,
		sections: DEFAULT_PRIVACY_SECTIONS
	});

export const loadAboutUs = () =>
	loadConfigAgreement(ABOUT_US_CONFIG_KEY, adaptAboutUsFromConfig, {
		title: DEFAULT_ABOUT_TITLE,
		sections: DEFAULT_ABOUT_SECTIONS
	});
