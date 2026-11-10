const THUMB_CLASSES = ['is-deep', 'is-silver', 'is-purple', 'is-cyan', 'is-light'];
const LUXURY_THUMB_CLASSES = ['is-gold', 'is-brown', 'is-black', 'is-camel', 'is-gray'];

const formatPrice = (value) => {
	const amount = Number(value);
	if (Number.isNaN(amount) || amount <= 0) {
		return '￥0';
	}
	return `￥${amount.toLocaleString('zh-CN', { maximumFractionDigits: 0 })}`;
};

export const adaptDigitalCategoryFromApi = (item = {}) => ({
	id: item.id,
	name: item.name || '',
	code: item.code || '',
	icon: item.icon || 'grid',
	description: item.description || '',
	sort: item.sort || 0,
	state: item.state
});

export const adaptDigitalBrandFromApi = (item = {}) => ({
	id: item.id,
	name: item.name || '',
	logo: item.logo || '',
	sort: item.sort || 0
});

export const adaptDigitalModelFromApi = (item = {}, index = 0, scene = 'digital') => {
	const isLuxury = scene === 'luxury';
	const thumbClasses = isLuxury ? LUXURY_THUMB_CLASSES : THUMB_CLASSES;
	return {
		id: item.id,
		brandId: item.brandId,
		name: item.name || '',
		price: formatPrice(item.basePrice),
		basePrice: item.basePrice,
		specJson: item.specJson || '',
		sub: isLuxury ? parseLuxurySpecSummary(item.specJson) : parseSpecSummary(item.specJson),
		thumbClass: thumbClasses[index % thumbClasses.length]
	};
};

export const adaptDigitalSearchResultFromApi = (item = {}, index = 0) => {
	const scene = item.scene === 'luxury' ? 'luxury' : 'digital';
	const model = adaptDigitalModelFromApi(
		{
			id: item.modelId,
			brandId: item.brandId,
			name: item.modelName,
			basePrice: item.basePrice,
			specJson: item.specJson
		},
		index,
		scene
	);
	return {
		...model,
		scene,
		brandName: item.brandName || ''
	};
};

const parseLuxurySpecSummary = (specJson) => {
	if (!specJson) {
		return '成色优秀参考回收价';
	}
	try {
		const spec = typeof specJson === 'string' ? JSON.parse(specJson) : specJson;
		const series = spec.series || spec.style;
		return series ? `${series}参考回收价` : '成色优秀参考回收价';
	} catch (error) {
		return '成色优秀参考回收价';
	}
};

const parseSpecSummary = (specJson) => {
	if (!specJson) {
		return '最高回收价';
	}
	try {
		const spec = typeof specJson === 'string' ? JSON.parse(specJson) : specJson;
		const storage = spec.storage || spec.memory || spec.capacity;
		return storage ? `规格 ${storage}` : '最高回收价';
	} catch (error) {
		return '最高回收价';
	}
};

export const sortByOrder = (list = []) => [...list].sort((a, b) => (a.sort || 0) - (b.sort || 0));
