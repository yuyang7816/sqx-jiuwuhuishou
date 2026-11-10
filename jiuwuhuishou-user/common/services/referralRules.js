import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';
import {
	adaptReferralRulesFromConfig,
	buildDefaultReferralRulesHtml,
	DEFAULT_TITLE
} from '../adapters/referralRules.js';

const REFERRAL_RULES_CONFIG_KEY = 'referral_rules_json';

const buildMockRules = () => ({
	title: DEFAULT_TITLE,
	html: buildDefaultReferralRulesHtml()
});

export const loadReferralRules = async () => {
	if (config.USE_MOCK) {
		return buildMockRules();
	}
	try {
		const data = await fetchConfigValue(REFERRAL_RULES_CONFIG_KEY);
		return adaptReferralRulesFromConfig(data?.value);
	} catch (error) {
		return adaptReferralRulesFromConfig('');
	}
};
