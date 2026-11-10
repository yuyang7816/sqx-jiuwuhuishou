import chinaRegionMap from '../data/china-region-map.json';

export const DIRECT_CITY_NAMES = ['市辖区', '县'];

export const buildRegionOptions = (regionMap = chinaRegionMap) =>
	Object.entries(regionMap['86'] || {}).map(([provinceCode, provinceName]) => {
		const cities = Object.entries(regionMap[provinceCode] || {}).map(([cityCode, cityName]) => {
			const districts = Object.entries(regionMap[cityCode] || {}).map(([districtCode, districtName]) => ({
				code: districtCode,
				name: districtName
			}));
			const normalizedCityName = DIRECT_CITY_NAMES.includes(cityName) ? provinceName : cityName;

			return {
				code: cityCode,
				name: normalizedCityName,
				districts: districts.length ? districts : [{ code: cityCode, name: normalizedCityName }]
			};
		});

		const normalizedCities = cities.length
			? cities
			: [
					{
						code: provinceCode,
						name: provinceName,
						districts: [{ code: provinceCode, name: provinceName }]
					}
				];

		return {
			code: provinceCode,
			name: provinceName,
			cities: normalizedCities
		};
	});

export const regionOptions = buildRegionOptions(chinaRegionMap);

export const resolveRegionSelection = (regionOptionsList, regionNames = [], { fallback = true } = {}) => {
	const [provinceName = '', cityName = '', districtName = ''] = regionNames;
	const fallbackProvince = fallback ? regionOptionsList[0] || null : null;
	const province = regionOptionsList.find((item) => item.name === provinceName) || fallbackProvince;
	const fallbackCity = fallback ? province?.cities?.[0] || null : null;
	const city = province?.cities?.find((item) => item.name === cityName) || fallbackCity;
	const fallbackDistrict = fallback ? city?.districts?.[0] || null : null;
	const district = city?.districts?.find((item) => item.name === districtName) || fallbackDistrict;

	return {
		province,
		city,
		district,
		provinceCode: province?.code || '',
		cityCode: city?.code || '',
		districtCode: district?.code || '',
		region: [province?.name, city?.name, district?.name].filter(Boolean)
	};
};

export const getRegionStepOptions = ({ regionOptionsList, regionStep, provinceCode, cityCode }) => {
	const province = regionOptionsList.find((item) => item.code === provinceCode) || regionOptionsList[0] || null;
	const city = province?.cities?.find((item) => item.code === cityCode) || province?.cities?.[0] || null;

	if (regionStep === 1) {
		return regionOptionsList;
	}
	if (regionStep === 2) {
		return province?.cities || [];
	}
	return city?.districts || [];
};
