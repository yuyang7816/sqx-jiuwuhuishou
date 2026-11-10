import config from '../config.js';
import * as applianceApi from '../api/appliance.js';
import {
	adaptApplianceSpecPage,
	buildEstimatePayload,
	buildLocalConfigFromLegacy,
	createDefaultSpecFromConfig,
	mapConditionToApi,
	normalizeSpecWithConfig
} from '../adapters/applianceSpec.js';
import { applianceConfigs, buildEstimate as buildLocalEstimate } from '../../packageRecycle/appliance/spec-data.js';

const buildFallbackPage = (categoryName = '冰箱') => {
	const configData = buildLocalConfigFromLegacy(categoryName);
	return {
		categories: Object.keys(applianceConfigs),
		categoryItems: [],
		config: configData
	};
};

export const loadApplianceSpecPage = async (categoryName = '冰箱') => {
	if (config.USE_MOCK) {
		return buildFallbackPage(categoryName);
	}
	try {
		const page = await applianceApi.fetchApplianceSpecPage(categoryName);
		const adapted = adaptApplianceSpecPage(page || {});
		if (adapted.config?.brands?.length) {
			return adapted;
		}
	} catch (error) {
		// 兼容旧版后端
	}
	return buildFallbackPage(categoryName);
};

export const createSpecFromPage = (page, categoryName) => createDefaultSpecFromConfig(page.config, categoryName);

export const normalizeSpecFromPage = (rawSpec, page, fallbackCategory) =>
	normalizeSpecWithConfig(rawSpec, page.config, fallbackCategory);

export const fetchApplianceEstimatePrice = async (spec, page) => {
	if (!spec?.modelId) {
		return {
			estimate: 0,
			reward: 0,
			low: 0,
			high: 0
		};
	}
	if (config.USE_MOCK) {
		const local = buildLocalEstimate(spec);
		return {
			estimate: local.estimate,
			reward: 0,
			low: local.low,
			high: local.high
		};
	}
	try {
		const result = await applianceApi.fetchApplianceEstimate(buildEstimatePayload(spec));
		const estimate = Number(result?.estimatePrice || 0);
		const low = Number(result?.lowPrice ?? estimate);
		const high = Number(result?.highPrice ?? estimate);
		return {
			estimate,
			reward: 0,
			low,
			high
		};
	} catch (error) {
		const local = buildLocalEstimate(spec);
		return {
			estimate: local.estimate,
			reward: 0,
			low: local.low,
			high: local.high
		};
	}
};
