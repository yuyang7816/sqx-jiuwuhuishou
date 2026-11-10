import config from '../config.js';
import { fetchBookCategories, fetchBookEstimateConfig, fetchBookWeightOptions } from '../api/book.js';
import { fetchClothesCategories, fetchClothesEstimateConfig } from '../api/clothes.js';
import { fetchConfigValue } from '../api/home.js';
import {
	adaptClothesBookingConfig,
	adaptClothesPageConfig,
	adaptClothesTypeFromApi,
	adaptClothesWeightFromApi,
	getBookingSceneMeta,
	normalizeBookingScene
} from '../adapters/clothes.js';
import { sortByOrder } from '../adapters/home.js';

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

const parseWeightOptionsJson = (rawValue = '') => {
	try {
		const list = JSON.parse(rawValue);
		if (!Array.isArray(list)) {
			return [];
		}
		return sortByOrder(list.map(adaptClothesWeightFromApi).filter((item) => item.title));
	} catch (error) {
		return [];
	}
};

const fetchTypeList = async (sceneMeta) => {
	const fetchCategories = sceneMeta.scene === 'book' ? fetchBookCategories : fetchClothesCategories;
	try {
		const list = await fetchCategories();
		const types = sortByOrder(
			(list || [])
				.filter((item) => item.state === 1)
				.map(adaptClothesTypeFromApi)
				.filter((item) => item.name)
		);
		return types.length ? types : sceneMeta.defaultTypes;
	} catch (error) {
		return sceneMeta.defaultTypes;
	}
};

const fetchWeightOptions = async (sceneMeta) => {
	if (sceneMeta.scene === 'book') {
		try {
			const list = await fetchBookWeightOptions();
			const weights = sortByOrder((list || []).map(adaptClothesWeightFromApi).filter((item) => item.title));
			if (weights.length) {
				return weights;
			}
		} catch (error) {
			// 新列表接口不可用时回退估计配置
		}
		try {
			const data = await fetchBookEstimateConfig();
			const list = sortByOrder((data?.weights || []).map(adaptClothesWeightFromApi).filter((item) => item.title));
			if (list.length) {
				return list;
			}
		} catch (error) {
			// ignore
		}
		return sceneMeta.defaultWeights;
	}

	try {
		const data = await fetchClothesEstimateConfig();
		const list = sortByOrder((data?.weights || []).map(adaptClothesWeightFromApi).filter((item) => item.title));
		if (list.length) {
			return list;
		}
	} catch (error) {
		// 接口未部署时回退到系统配置
	}

	const configValue = await fetchConfigSafe(sceneMeta.weightConfigKey, '');
	const fallbackList = parseWeightOptionsJson(configValue);
	return fallbackList.length ? fallbackList : sceneMeta.defaultWeights;
};

export const loadClothesBookingPageData = async (scene = 'clothes') => {
	const sceneMeta = getBookingSceneMeta(scene);
	const prefix = sceneMeta.configPrefix;

	if (config.USE_MOCK) {
		return {
			scene: sceneMeta.scene,
			pageTitle: sceneMeta.pageTitle,
			heroLabel: sceneMeta.heroLabel,
			heroDesc: sceneMeta.heroDesc,
			scienceArtLabel: sceneMeta.scienceArtLabel,
			scienceArtDesc: sceneMeta.scienceArtDesc,
			...adaptClothesBookingConfig({
				unitPrice: sceneMeta.defaultUnitPrice,
				unitSuffix: sceneMeta.defaultUnitSuffix,
				priceDesc: sceneMeta.defaultPriceDesc
			}),
			...adaptClothesPageConfig({
				serviceRangeDesc: sceneMeta.defaultServiceRangeDesc,
				orderNotice: sceneMeta.defaultOrderNotice,
				scienceTitle: sceneMeta.defaultScienceTitle,
				scienceDesc: sceneMeta.defaultScienceDesc,
				agreementText: sceneMeta.defaultAgreementText
			}),
			types: sceneMeta.defaultTypes,
			weights: sceneMeta.defaultWeights
		};
	}

	const [
		unitPrice,
		unitSuffix,
		priceDesc,
		serviceRangeDesc,
		serviceRangeTag,
		orderNotice,
		scienceTitle,
		scienceDesc,
		agreementText,
		types,
		weights
	] = await Promise.all([
		fetchConfigSafe(`${prefix}_unit_price`, sceneMeta.defaultUnitPrice),
		fetchConfigSafe(`${prefix}_unit_price_suffix`, sceneMeta.defaultUnitSuffix),
		fetchConfigSafe(`${prefix}_price_desc`, sceneMeta.defaultPriceDesc),
		fetchConfigSafe(`${prefix}_service_range_desc`, sceneMeta.defaultServiceRangeDesc),
		fetchConfigSafe(`${prefix}_service_range_tag`, '已覆盖'),
		fetchConfigSafe(`${prefix}_order_notice`, sceneMeta.defaultOrderNotice),
		fetchConfigSafe(`${prefix}_science_title`, sceneMeta.defaultScienceTitle),
		fetchConfigSafe(`${prefix}_science_desc`, sceneMeta.defaultScienceDesc),
		fetchConfigSafe(`${prefix}_agreement_text`, sceneMeta.defaultAgreementText),
		fetchTypeList(sceneMeta),
		fetchWeightOptions(sceneMeta)
	]);

	return {
		scene: sceneMeta.scene,
		pageTitle: sceneMeta.pageTitle,
		heroLabel: sceneMeta.heroLabel,
		heroDesc: sceneMeta.heroDesc,
		scienceArtLabel: sceneMeta.scienceArtLabel,
		scienceArtDesc: sceneMeta.scienceArtDesc,
		...adaptClothesBookingConfig({ unitPrice, unitSuffix, priceDesc }),
		...adaptClothesPageConfig({
			serviceRangeDesc,
			serviceRangeTag,
			orderNotice,
			scienceTitle,
			scienceDesc,
			agreementText
		}),
		types,
		weights
	};
};

export const resolveBookingScene = normalizeBookingScene;
