import { apiGetNoAuth, apiPostJsonNoAuth } from './http.js';
import { fetchConfigValue } from './home.js';

export { fetchConfigValue };

export const fetchApplianceCategories = () => apiGetNoAuth('/app/recycle/appliance/category/list');

export const fetchApplianceCategoryOverview = () => apiGetNoAuth('/app/recycle/appliance/category/overview');

export const fetchApplianceSpecPage = (categoryName) =>
	apiGetNoAuth('/app/recycle/appliance/spec/page', categoryName ? { categoryName } : {});

export const fetchApplianceEstimate = (payload) => apiPostJsonNoAuth('/app/recycle/appliance/estimate', payload);

export const fetchApplianceModels = (categoryId) => apiPostJsonNoAuth('/app/recycle/appliance/model/list', { categoryId });

export const fetchApplianceHeroFlows = () => apiGetNoAuth('/app/recycle/appliance/heroFlow/list');

export const fetchRecycleRules = () => apiGetNoAuth('/app/recycle/rule/list');
