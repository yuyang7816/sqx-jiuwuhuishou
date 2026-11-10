const DEFAULT_SECTIONS = {
	digital: [
		{ key: 'storage', label: '容量版本', options: ['128G', '256G', '512G', '1TB'], layout: 'grid' },
		{ key: 'color', label: '机身颜色', options: ['深空黑', '银色', '蓝色', '紫色'], layout: 'grid' },
		{ key: 'damage', label: '损伤情况', options: ['无明显故障', '轻微磕碰', '屏幕细纹', '电池损耗明显', '无法开机'], layout: 'grid' },
		{ key: 'condition', label: '新旧程度', options: ['九成新', '良好', '正常老化', '成色一般'], layout: 'column' }
	],
	luxury: [
		{ key: 'series', label: '款式系列', options: ['经典款', '热门款', '限定款', '常规款'], layout: 'grid' },
		{ key: 'material', label: '材质', options: ['牛皮', '帆布', '羊皮', '拼接材质'], layout: 'grid' },
		{ key: 'damage', label: '损伤情况', options: ['无明显瑕疵', '轻微磨损', '五金划痕', '边角磨损', '需专业修复'], layout: 'grid' },
		{ key: 'condition', label: '新旧程度', options: ['成色优秀', '成色良好', '正常使用痕迹', '成色一般'], layout: 'column' }
	]
};

const SCENE_LABEL_MAP = {
	digital: '数码回收',
	luxury: '奢侈品回收'
};

const adaptSections = (sections = []) =>
	(sections || [])
		.map((section) => ({
			key: section.key,
			label: section.label || section.key,
			layout: section.layout || 'grid',
			options: (section.options || []).map((item) => String(item).trim()).filter(Boolean)
		}))
		.filter((section) => section.key && section.options.length > 0);

export const adaptDigitalSpecPage = (page = {}) => {
	const scene = page.scene === 'luxury' ? 'luxury' : 'digital';

	return {
		scene,
		sceneLabel: page.sceneLabel || SCENE_LABEL_MAP[scene] || '商品回收',
		brandId: page.brandId || null,
		brandName: page.brandName || '',
		modelId: page.modelId || null,
		modelName: page.modelName || '',
		basePrice: Number(page.basePrice || 0),
		// 严格使用接口返回的型号绑定规格，不再回填公共模板
		sections: adaptSections(page.sections)
	};
};

export const buildFallbackSpecPage = ({ scene = 'digital', brandName = '', modelName = '', basePrice = 0, useMockSections = false } = {}) => {
	const normalizedScene = scene === 'luxury' ? 'luxury' : 'digital';
	return {
		scene: normalizedScene,
		sceneLabel: SCENE_LABEL_MAP[normalizedScene],
		brandId: null,
		brandName,
		modelId: null,
		modelName: modelName || '未命名商品',
		basePrice: Number(basePrice || 0),
		sections: useMockSections ? DEFAULT_SECTIONS[normalizedScene] : []
	};
};

export const createDefaultSelectedSpec = (sections = []) => {
	const selected = {};
	sections.forEach((section) => {
		selected[section.key] = section.options?.[0] || '';
	});
	if (selected.condition && sections.some((item) => item.key === 'condition')) {
		const conditionSection = sections.find((item) => item.key === 'condition');
		selected.condition = conditionSection?.options?.[1] || selected.condition;
	}
	return selected;
};

export const mapConditionToApi = (condition = '') => {
	const map = {
		九成新: 'good',
		良好: 'good',
		正常老化: 'fair',
		成色一般: 'fair',
		成色优秀: 'good',
		成色良好: 'good',
		正常使用痕迹: 'fair'
	};
	return map[condition] || 'fair';
};

/** 组装估价参数：保留中文规格原文，供后端按 product_spec.attPrice 累加 */
export const buildDigitalEstimatePayload = (modelId, selectedSpec = {}) => {
	const params = {};
	Object.entries(selectedSpec || {}).forEach(([key, value]) => {
		if (!key || value == null) {
			return;
		}
		const text = String(value).trim();
		if (!text) {
			return;
		}
		params[key] = text;
	});
	return {
		modelId: Number(modelId),
		condition: mapConditionToApi(params.condition),
		params
	};
};
