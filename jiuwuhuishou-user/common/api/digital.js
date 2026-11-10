import { apiGetNoAuth, apiPostJsonNoAuth } from './http.js';

export const fetchDigitalBrands = (scene = 'digital') => apiGetNoAuth('/app/recycle/digital/brand/list', { scene });

export const fetchDigitalModels = (payload) => apiPostJsonNoAuth('/app/recycle/digital/model/list', payload);

export const fetchDigitalSearch = ({ keyword, scene = 'digital', limit = 20 } = {}) =>
	apiGetNoAuth('/app/recycle/digital/search', {
		...(keyword ? { keyword } : {}),
		scene,
		limit
	});

export const fetchDigitalHotKeywords = (scene = 'digital') => apiGetNoAuth('/app/recycle/digital/search/hot', { scene });

export const fetchDigitalSpecPage = ({ modelId, scene, brandName, modelName } = {}) =>
	apiGetNoAuth('/app/recycle/digital/spec/page', {
		...(modelId ? { modelId } : {}),
		...(scene ? { scene } : {}),
		...(brandName ? { brandName } : {}),
		...(modelName ? { modelName } : {})
	});

export const fetchDigitalEstimate = (payload) => apiPostJsonNoAuth('/app/recycle/digital/estimate', payload);
