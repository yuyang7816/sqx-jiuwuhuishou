import { applianceConfigs, createDefaultSpec as createLocalDefaultSpec } from '../../packageRecycle/appliance/spec-data.js';

const CONDITION_MAP = {
	九成新: 'good',
	良好: 'good',
	正常老化: 'fair',
	成色一般: 'fair',
	外观磨损明显: 'poor'
};

export const isMultiSelectField = (field) => Number(field?.multiSelect) === 1;

const adaptSpecFields = (fields = []) =>
	(fields || [])
		.filter((field) => field?.key && Array.isArray(field.options) && field.options.length > 0)
		.map((field) => ({
			key: field.key,
			label: field.label || field.key,
			options: field.options,
			multiSelect: isMultiSelectField(field) ? 1 : 0
		}));

const adaptOptionList = (options = []) =>
	(options || []).map((item) => String(item).trim()).filter(Boolean);

/** 仅返回型号已绑定规格；未选中或未配置时不展示品类模板 */
export const resolveModelFields = (model) => {
	if (!model || !Array.isArray(model.fields)) {
		return [];
	}
	return adaptSpecFields(model.fields);
};

export const resolveModelAssessOptions = (model) => ({
	damageOptions: adaptOptionList(model?.damageOptions),
	conditionOptions: adaptOptionList(model?.conditionOptions),
	ageOptions: adaptOptionList(model?.ageOptions)
});

export const hasModelBoundSpecs = (model) => {
	if (!model) {
		return false;
	}
	const assess = resolveModelAssessOptions(model);
	return (
		resolveModelFields(model).length > 0 ||
		assess.damageOptions.length > 0 ||
		assess.conditionOptions.length > 0 ||
		assess.ageOptions.length > 0
	);
};

const toValidOptionList = (raw, options = []) => {
	let list = [];
	if (Array.isArray(raw)) {
		list = raw;
	} else if (typeof raw === 'string' && raw.includes(',')) {
		list = raw.split(',');
	} else if (raw) {
		list = [raw];
	}
	return list.map((item) => String(item).trim()).filter((item) => options.includes(item));
};

/** 按规格是否多选规范化 details 取值：多选为数组，单选为字符串 */
export const resolveDetailValue = (field, raw, fallbackRaw) => {
	if (!field?.key) {
		return '';
	}
	const options = field.options || [];
	if (isMultiSelectField(field)) {
		const fromRaw = toValidOptionList(raw, options);
		if (fromRaw.length) {
			return fromRaw;
		}
		const fromFallback = toValidOptionList(fallbackRaw, options);
		if (fromFallback.length) {
			return fromFallback;
		}
		return options[0] ? [options[0]] : [];
	}
	if (options.includes(raw)) {
		return raw;
	}
	if (options.includes(fallbackRaw)) {
		return fallbackRaw;
	}
	return options[0] || '';
};

export const isDetailOptionSelected = (selected, option) => {
	if (Array.isArray(selected)) {
		return selected.includes(option);
	}
	return selected === option;
};

export const toggleDetailOption = (field, current, option) => {
	if (!isMultiSelectField(field)) {
		return option;
	}
	const list = Array.isArray(current) ? [...current] : current ? [current] : [];
	const index = list.indexOf(option);
	if (index >= 0) {
		list.splice(index, 1);
	} else {
		list.push(option);
	}
	return list.filter((item) => (field.options || []).includes(item));
};

export const flattenDetailValues = (details = {}) =>
	Object.values(details || {}).flatMap((value) => {
		if (Array.isArray(value)) {
			return value.filter(Boolean);
		}
		return value ? [value] : [];
	});

const buildDetailsFromFields = (fields, rawDetails = {}, modelSpecs = {}) => {
	const details = {};
	(fields || []).forEach((field) => {
		details[field.key] = resolveDetailValue(field, rawDetails?.[field.key], modelSpecs?.[field.key]);
	});
	return details;
};

const pickAssessDefaults = (model) => {
	const assess = resolveModelAssessOptions(model);
	if (!hasModelBoundSpecs(model)) {
		return { damage: '', condition: '', age: '' };
	}
	return {
		damage: assess.damageOptions[0] || '',
		condition: assess.conditionOptions[1] || assess.conditionOptions[0] || '',
		age: assess.ageOptions[1] || assess.ageOptions[0] || ''
	};
};

const pickAssessValue = (options, rawValue, fallback) =>
	options.includes(rawValue) ? rawValue : fallback;

export const adaptApplianceSpecConfig = (config = {}) => {
	const brands = (config.brands || []).map((brand) => ({
		name: brand.name,
		models: (brand.models || []).map((model) => ({
			id: model.id,
			name: model.name,
			basePrice: Number(model.basePrice || 0),
			minPrice: Number(model.minPrice ?? model.basePrice ?? 0),
			maxPrice: Number(model.maxPrice ?? model.basePrice ?? 0),
			specs: model.specs || {},
			fields: adaptSpecFields(model.fields),
			damageOptions: adaptOptionList(model.damageOptions),
			conditionOptions: adaptOptionList(model.conditionOptions),
			ageOptions: adaptOptionList(model.ageOptions)
		}))
	}));

	return {
		categoryId: config.categoryId,
		category: config.categoryName || '',
		title: config.title || '',
		subtitle: config.subtitle || '',
		icon: config.icon || 'home',
		cardLabel: config.cardLabel || '',
		reward: Number(config.reward || 0),
		brands,
		brandMap: brands.reduce((result, brand) => {
			result[brand.name] = brand.models;
			return result;
		}, {}),
		// 品类公共规格不再下发，前端一律读型号绑定数据
		fields: [],
		damageOptions: [],
		conditionOptions: [],
		ageOptions: []
	};
};

export const adaptApplianceSpecPage = (page = {}) => ({
	categories: (page.categories || []).map((item) => item.name).filter(Boolean),
	categoryItems: page.categories || [],
	config: adaptApplianceSpecConfig(page.config || {})
});

export const createDefaultSpecFromConfig = (config, categoryName = '') => {
	if (!config?.brands?.length) {
		return createLocalDefaultSpec(categoryName || '冰箱');
	}
	const brand = config.brands[0];
	const model = brand.models?.[0];
	const fields = resolveModelFields(model);
	return {
		categoryId: config.categoryId,
		category: config.category || categoryName || '',
		brand: brand.name,
		model: model?.name || '',
		modelId: model?.id || null,
		minPrice: Number(model?.minPrice ?? model?.basePrice ?? 0),
		maxPrice: Number(model?.maxPrice ?? model?.basePrice ?? 0),
		...pickAssessDefaults(model),
		details: buildDetailsFromFields(fields, {}, model?.specs || {})
	};
};

export const normalizeSpecWithConfig = (rawSpec, config, fallbackCategory = '冰箱') => {
	const base = createDefaultSpecFromConfig(config, rawSpec?.category || fallbackCategory);
	const brandGroup = config.brands.find((item) => item.name === rawSpec?.brand) || config.brands[0];
	const modelItem =
		brandGroup?.models.find((item) => item.id != null && rawSpec?.modelId != null && `${item.id}` === `${rawSpec.modelId}`) ||
		brandGroup?.models.find((item) => item.name === rawSpec?.model) ||
		brandGroup?.models?.[0];
	const fields = resolveModelFields(modelItem);
	const assess = resolveModelAssessOptions(modelItem);
	const assessDefaults = pickAssessDefaults(modelItem);

	return {
		categoryId: config.categoryId,
		category: config.category || fallbackCategory,
		brand: brandGroup?.name || base.brand,
		model: modelItem?.name || base.model,
		modelId: modelItem?.id || base.modelId,
		minPrice: Number(modelItem?.minPrice ?? modelItem?.basePrice ?? base.minPrice ?? 0),
		maxPrice: Number(modelItem?.maxPrice ?? modelItem?.basePrice ?? base.maxPrice ?? 0),
		damage: pickAssessValue(assess.damageOptions, rawSpec?.damage, assessDefaults.damage),
		condition: pickAssessValue(assess.conditionOptions, rawSpec?.condition, assessDefaults.condition),
		age: pickAssessValue(assess.ageOptions, rawSpec?.age, assessDefaults.age),
		details: buildDetailsFromFields(fields, rawSpec?.details || {}, modelItem?.specs || {})
	};
};

export const mapConditionToApi = (condition) => CONDITION_MAP[condition] || 'fair';

export const buildEstimatePayload = (spec) => {
	const params = {
		condition: spec?.condition || '',
		damage: spec?.damage || '',
		age: spec?.age || '',
		...(spec?.details || {})
	};
	Object.keys(params).forEach((key) => {
		const value = params[key];
		if (Array.isArray(value)) {
			const joined = value.filter(Boolean).join(',');
			if (joined) {
				params[key] = joined;
			} else {
				delete params[key];
			}
			return;
		}
		if (!value) {
			delete params[key];
		}
	});
	return {
		modelId: spec?.modelId,
		condition: spec?.condition ? mapConditionToApi(spec.condition) : undefined,
		damage: spec?.damage || undefined,
		age: spec?.age || undefined,
		params
	};
};

export const buildLocalConfigFromLegacy = (categoryName) => {
	const legacy = applianceConfigs[categoryName] || applianceConfigs['冰箱'];
	return adaptApplianceSpecConfig({
		categoryName,
		title: legacy.title,
		subtitle: legacy.subtitle,
		icon: legacy.icon,
		cardLabel: legacy.cardLabel,
		reward: legacy.reward,
		fields: [],
		damageOptions: [],
		conditionOptions: [],
		ageOptions: [],
		brands: Object.entries(legacy.brands || {}).map(([name, models]) => ({
			name,
			models: (models || []).map((modelName, index) => ({
				id: index + 1,
				name: modelName,
				basePrice: legacy.modelBase,
				minPrice: legacy.modelBase,
				maxPrice: legacy.modelBase,
				specs: {},
				// mock 无 product_spec，规格挂在型号上模拟管理端绑定
				fields: legacy.fields,
				damageOptions: legacy.damageOptions,
				conditionOptions: legacy.conditionOptions,
				ageOptions: legacy.ageOptions
			}))
		}))
	});
};
