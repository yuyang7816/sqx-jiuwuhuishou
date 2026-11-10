import config from '../config.js';
import { fetchRecycleCategories } from '../api/home.js';
import * as digitalApi from '../api/digital.js';
import {
	adaptDigitalBrandFromApi,
	adaptDigitalCategoryFromApi,
	adaptDigitalModelFromApi,
	adaptDigitalSearchResultFromApi,
	sortByOrder
} from '../adapters/digital.js';

const SCENE_CODES = ['digital', 'luxury'];
const HISTORY_KEY_PREFIX = 'digital_search_history_';
const HISTORY_LIMIT = 10;

const DEFAULT_CATEGORIES = [
	{ id: 3, name: '数码回收', code: 'digital', icon: 'grid', description: '手机、平板、笔记本等数码产品', sort: 2, state: 1 },
	{ id: 4, name: '奢侈品', code: 'luxury', icon: 'coupon', description: '奢侈品回收', sort: 4, state: 1 }
];

const DEFAULT_HOT_DIGITAL = ['iPhone 15 Pro', 'Mate 60 Pro', 'iPhone 14'];
const DEFAULT_HOT_LUXURY = ['CarryAll', 'CF 经典口盖包', 'Neverfull'];

const normalizeScene = (scene = 'digital') => (scene === 'luxury' ? 'luxury' : 'digital');

const historyStorageKey = (scene) => `${HISTORY_KEY_PREFIX}${normalizeScene(scene)}`;

export const getSearchHistory = (scene = 'digital') => {
	try {
		const list = uni.getStorageSync(historyStorageKey(scene));
		return Array.isArray(list) ? list : [];
	} catch (error) {
		return [];
	}
};

export const addSearchHistory = (scene = 'digital', keyword = '') => {
	const text = `${keyword || ''}`.trim();
	if (!text) {
		return getSearchHistory(scene);
	}
	const next = [text, ...getSearchHistory(scene).filter((item) => item !== text)].slice(0, HISTORY_LIMIT);
	try {
		uni.setStorageSync(historyStorageKey(scene), next);
	} catch (error) {
		// ignore
	}
	return next;
};

export const clearSearchHistory = (scene = 'digital') => {
	try {
		uni.removeStorageSync(historyStorageKey(scene));
	} catch (error) {
		// ignore
	}
	return [];
};

export const fetchDigitalCategories = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_CATEGORIES;
	}
	const list = sortByOrder(
		(await fetchRecycleCategories() || [])
			.filter((item) => item.state === 1 && SCENE_CODES.includes(item.code))
			.map(adaptDigitalCategoryFromApi)
	);
	return list.length ? list : DEFAULT_CATEGORIES;
};

export const fetchDigitalBrandList = async (scene = 'digital') => {
	const normalizedScene = normalizeScene(scene);
	if (config.USE_MOCK) {
		if (normalizedScene === 'luxury') {
			return [
				{ id: 101, name: 'LV' },
				{ id: 102, name: 'CHANEL' },
				{ id: 103, name: 'GUCCI' }
			];
		}
		return [
			{ id: 1, name: 'Apple' },
			{ id: 2, name: 'Huawei' },
			{ id: 3, name: 'Xiaomi' }
		];
	}
	const list = sortByOrder((await digitalApi.fetchDigitalBrands(normalizedScene) || []).map(adaptDigitalBrandFromApi));
	return list;
};

export const fetchDigitalModelList = async ({ brandId, keyword, scene = 'digital' } = {}) => {
	const normalizedScene = normalizeScene(scene);
	if (config.USE_MOCK) {
		return [];
	}
	if (!brandId) {
		return [];
	}
	const list = (await digitalApi.fetchDigitalModels({ brandId, keyword: keyword || undefined }) || [])
		.map((item, index) => adaptDigitalModelFromApi(item, index, normalizedScene));
	return list;
};

export const fetchDigitalHotKeywords = async (scene = 'digital') => {
	const normalizedScene = normalizeScene(scene);
	if (config.USE_MOCK) {
		return normalizedScene === 'luxury' ? DEFAULT_HOT_LUXURY : DEFAULT_HOT_DIGITAL;
	}
	const list = await digitalApi.fetchDigitalHotKeywords(normalizedScene);
	return Array.isArray(list) && list.length
		? list
		: (normalizedScene === 'luxury' ? DEFAULT_HOT_LUXURY : DEFAULT_HOT_DIGITAL);
};

export const searchDigitalModels = async ({ keyword = '', scene = 'digital', limit = 20 } = {}) => {
	const normalizedScene = normalizeScene(scene);
	if (config.USE_MOCK) {
		return [];
	}
	const list = await digitalApi.fetchDigitalSearch({
		keyword: `${keyword || ''}`.trim() || undefined,
		scene: normalizedScene,
		limit
	});
	return (list || []).map((item, index) => adaptDigitalSearchResultFromApi(item, index));
};

export const loadDigitalModelsPageData = async (scene = 'digital') => {
	const categories = await fetchDigitalCategories();
	const activeCategory = categories.find((item) => item.code === scene) || categories[0] || null;
	const brands = await fetchDigitalBrandList(scene);
	const activeBrand = brands[0] || null;
	const models = activeBrand
		? await fetchDigitalModelList({ brandId: activeBrand.id, scene })
		: [];

	return {
		categories,
		activeCategory,
		brands,
		activeBrand,
		models
	};
};

export const loadDigitalSearchPageData = async (scene = 'digital') => {
	const normalizedScene = normalizeScene(scene);
	const [hotKeywords, brands, history] = await Promise.all([
		fetchDigitalHotKeywords(normalizedScene),
		fetchDigitalBrandList(normalizedScene),
		Promise.resolve(getSearchHistory(normalizedScene))
	]);
	return {
		scene: normalizedScene,
		hotKeywords,
		brands,
		history
	};
};
