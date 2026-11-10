import config from '../config.js';
import * as digitalApi from '../api/digital.js';
import {
	adaptDigitalSpecPage,
	buildDigitalEstimatePayload,
	buildFallbackSpecPage,
	createDefaultSelectedSpec,
	mapConditionToApi
} from '../adapters/digitalSpec.js';

export const loadDigitalSpecPage = async ({ modelId, scene = 'digital', brandName = '', modelName = '', basePrice = 0 } = {}) => {
	const normalizedScene = scene === 'luxury' ? 'luxury' : 'digital';
	const fallback = buildFallbackSpecPage({
		scene: normalizedScene,
		brandName,
		modelName,
		basePrice,
		useMockSections: !!config.USE_MOCK
	});

	if (config.USE_MOCK) {
		return fallback;
	}

	try {
		const payload = {
			scene: normalizedScene,
			brandName: brandName || undefined,
			modelName: modelName || undefined
		};
		const numericModelId = Number(modelId);
		if (Number.isFinite(numericModelId) && numericModelId > 0) {
			payload.modelId = numericModelId;
		}
		const page = await digitalApi.fetchDigitalSpecPage(payload);
		const adapted = adaptDigitalSpecPage(page || {});
		return {
			...fallback,
			...adapted,
			brandName: adapted.brandName || fallback.brandName,
			modelName: adapted.modelName || fallback.modelName,
			basePrice: adapted.basePrice > 0 ? adapted.basePrice : fallback.basePrice,
			// 接口成功时即使 sections 为空也不回填公共规格
			sections: adapted.sections || []
		};
	} catch (error) {
		return {
			...fallback,
			sections: []
		};
	}
};

export const fetchDigitalEstimatePrice = async ({ modelId, selectedSpec = {}, basePrice = 0 } = {}) => {
	const numericModelId = Number(modelId);
	const fallbackPrice = Math.max(Number(basePrice) || 0, 0);
	if (!Number.isFinite(numericModelId) || numericModelId <= 0) {
		return fallbackPrice;
	}
	if (config.USE_MOCK) {
		return fallbackPrice;
	}
	try {
		const payload = buildDigitalEstimatePayload(numericModelId, selectedSpec);
		const result = await digitalApi.fetchDigitalEstimate(payload);
		const estimate = Number(result?.estimatePrice);
		return Number.isFinite(estimate) ? Math.max(estimate, 0) : fallbackPrice;
	} catch (error) {
		return fallbackPrice;
	}
};

export { createDefaultSelectedSpec, mapConditionToApi };
