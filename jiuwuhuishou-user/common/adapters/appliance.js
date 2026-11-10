import { resolveAssetUrl } from './user.js';

const CATEGORY_PRICE_ICON = {
	空调: 'star',
	冰箱: 'home',
	洗衣机: 'grid',
	电视: 'photo'
};

const SCIENCE_ICON_PRESETS = [
	{ icon: 'reload', color: '#00b96b', imageUrl: '/static/image/appliance-science-recycle.png' },
	{ icon: 'shield', color: '#3572ff', imageUrl: '/static/image/appliance-science-privacy.png' }
];

const MORE_SERVICE_IMAGE_PRESETS = [
	{ imageUrl: '/static/image/appliance-service-distance.png' },
	{ imageUrl: '/static/image/appliance-service-install.png' }
];

const DEFAULT_FLOWS = [
	{ name: '线上预约', icon: 'edit-pen' },
	{ name: '电话沟通', icon: 'phone' },
	{ name: '上门回收', icon: 'car' },
	{ name: '立即收钱', icon: 'rmb-circle' }
];

const isAssetPath = (value = '') => /^https?:\/\//i.test(value) || value.startsWith('/') || value.includes('.');

const resolveFlowIcon = (rawIcon = '') => {
	if (!rawIcon) {
		return '';
	}
	return isAssetPath(rawIcon) ? resolveAssetUrl(rawIcon) : rawIcon;
};

const normalizeFlowItem = (item = {}) => {
	const rawIcon = item.icon || '';
	return {
		name: item.name || '',
		icon: resolveFlowIcon(rawIcon),
		iconIsImage: isAssetPath(rawIcon)
	};
};

const parseJsonSafe = (value, fallback) => {
	if (!value) {
		return fallback;
	}
	try {
		return JSON.parse(value);
	} catch (error) {
		return fallback;
	}
};

const formatPrice = (value) => {
	const amount = Number(value);
	if (Number.isNaN(amount)) {
		return '0';
	}
	return Number.isInteger(amount) ? `${amount}` : amount.toFixed(0);
};

const buildPriceRange = (models = []) => {
	const prices = models
		.map((item) => Number(item.basePrice))
		.filter((price) => !Number.isNaN(price) && price > 0);
	if (!prices.length) {
		return '￥0';
	}
	const min = Math.min(...prices);
	const max = Math.max(...prices);
	if (min === max) {
		return `￥${formatPrice(min)}`;
	}
	return `￥${formatPrice(min)} - ${formatPrice(max)}`;
};

export const adaptApplianceHeroConfig = ({
	badge,
	titleLine1,
	titleLine2,
	imageUrl,
	flows
}) => ({
	badge: badge || '高价回收 · 绿色环保',
	titleLine1: titleLine1 || '家电旧换新',
	titleLine2: titleLine2 || '省心更省力',
	imageUrl: resolveAssetUrl(imageUrl || ''),
	flows: (Array.isArray(flows) && flows.length ? flows : DEFAULT_FLOWS).map(normalizeFlowItem)
});

export const adaptApplianceCategoryOverviewFromApi = (item = {}) => ({
	id: item.id,
	name: item.name || '',
	icon: item.icon || 'home',
	sort: item.sort || 0,
	priceTitle: item.priceTitle || `${item.name || ''}回收`,
	priceIcon: CATEGORY_PRICE_ICON[item.name] || item.icon || 'star',
	price: item.price || '￥0',
	priceMin: item.priceMin,
	priceMax: item.priceMax,
	models: item.models || []
});

export const adaptApplianceCategoryFromApi = (item = {}, models = []) => {
	const overview = adaptApplianceCategoryOverviewFromApi({
		...item,
		priceTitle: item.displayTitle || item.priceTitle,
		models,
		price: item.price
	});
	if (!item.price) {
		overview.price = buildPriceRange(models);
	}
	return overview;
};

export const adaptApplianceScienceFromApi = (item = {}, index = 0) => {
	const preset = SCIENCE_ICON_PRESETS[index] || SCIENCE_ICON_PRESETS[0];
	return {
		id: item.id,
		title: item.title || '',
		desc: item.content || '',
		icon: preset.icon,
		color: preset.color,
		imageUrl: resolveAssetUrl(preset.imageUrl),
		sort: item.sort || 0
	};
};

export const adaptApplianceFaqFromApi = (item = {}) => ({
	id: item.id,
	q: item.title || '',
	a: item.content || '',
	open: false,
	sort: item.sort || 0
});

export const adaptApplianceMoreHeroConfig = ({ title, desc, icon }) => ({
	title: title || '公正透明 · 碳绿新生',
	desc: desc || '致力于打造价格透明、服务标准、低碳环保的家电回收体系',
	icon: icon || 'info-circle'
});

export const adaptApplianceMoreCarbonConfig = ({ title, desc }) => ({
	title: title || '每回收一台冰箱，约等于种下 3 棵树',
	desc: desc || '感谢您为地球减碳事业做出的每一份贡献'
});

export const adaptApplianceMorePricingFromApi = (item = {}) => {
	const meta = parseJsonSafe(item.content, {});
	const rawIcon = meta.icon || '';
	const imageUrl = resolveAssetUrl(meta.imageUrl || (isAssetPath(rawIcon) ? rawIcon : ''));
	return {
		id: item.id,
		title: item.title || '',
		icon: isAssetPath(rawIcon) ? 'home' : (rawIcon || 'home'),
		imageUrl,
		top: meta.top || '',
		items: Array.isArray(meta.items) ? meta.items : [],
		sort: item.sort || 0
	};
};

export const adaptApplianceMoreServiceFromApi = (item = {}) => {
	const meta = parseJsonSafe(item.content, {});
	const preset = MORE_SERVICE_IMAGE_PRESETS[Math.max((item.sort || 1) - 1, 0)] || MORE_SERVICE_IMAGE_PRESETS[0];
	return {
		id: item.id,
		title: item.title || '',
		desc: meta.desc || '',
		icon: meta.icon || 'map',
		imageUrl: resolveAssetUrl(meta.imageUrl || preset.imageUrl),
		theme: meta.theme || 'gold',
		lines: Array.isArray(meta.lines) ? meta.lines : [],
		tags: Array.isArray(meta.tags) ? meta.tags : [],
		sort: item.sort || 0
	};
};

export const adaptApplianceMoreTipFromApi = (item = {}) => ({
	id: item.id,
	q: item.title || '',
	a: item.content || '',
	open: false,
	sort: item.sort || 0
});

export const sortByOrder = (list = []) => [...list].sort((a, b) => (a.sort || 0) - (b.sort || 0));
