import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';
import { checkServiceArea as checkServiceAreaApi } from '../api/recycle.js';
import {
	adaptServiceCheckFromConfig,
	buildServiceAreaStatus,
	DEFAULT_SERVICE_CHECK
} from '../adapters/serviceCheck.js';

const SERVICE_CHECK_CONFIG_KEY = 'service_check_json';

export const loadServiceCheckPageData = async () => {
	if (config.USE_MOCK) {
		return adaptServiceCheckFromConfig(JSON.stringify(DEFAULT_SERVICE_CHECK));
	}
	try {
		const data = await fetchConfigValue(SERVICE_CHECK_CONFIG_KEY);
		return adaptServiceCheckFromConfig(data?.value);
	} catch (error) {
		return adaptServiceCheckFromConfig('');
	}
};

export const checkServiceArea = async ({ province, city, district } = {}, pageData = null) => {
	const content = pageData || DEFAULT_SERVICE_CHECK;
	if (!province || !city) {
		return {
			inService: false,
			tag: '待查询',
			desc: '请选择或填写完整地址后再查询覆盖范围'
		};
	}
	if (config.USE_MOCK) {
		const inService = `${city}`.includes('上海');
		return buildServiceAreaStatus(inService, content);
	}
	try {
		const result = await checkServiceAreaApi({
			province,
			city,
			district: district || undefined
		});
		return buildServiceAreaStatus(result?.inService, content);
	} catch (error) {
		return {
			inService: false,
			tag: '查询失败',
			desc: error.message || '服务范围查询失败，请稍后重试'
		};
	}
};

export const checkServiceAreaByAddress = async (address = {}, pageData = null) => {
	const region = address.region || [];
	return checkServiceArea(
		{
			province: address.province || region[0] || '',
			city: address.city || region[1] || '',
			district: address.district || region[2] || ''
		},
		pageData
	);
};
