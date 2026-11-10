const isAssetPath = (value = '') => /^https?:\/\//i.test(value) || value.startsWith('/') || value.includes('.');

export const DEFAULT_CLOTHES_UNIT_PRICE = '0.8';
export const DEFAULT_CLOTHES_UNIT_SUFFIX = '/kg 起';
export const DEFAULT_CLOTHES_PRICE_DESC = '支持上门回收、称重结算、环保积分返还';

export const DEFAULT_CLOTHES_TYPES = [
	{ id: 1, name: '上衣/裤子', icon: 'integral' },
	{ id: 2, name: '床单/毛毯', icon: 'integral' },
	{ id: 3, name: '鞋帽/包包', icon: 'integral' },
	{ id: 4, name: '儿童旧衣', icon: 'integral' }
];

export const DEFAULT_BOOK_TYPES = [
	{ id: 1, name: '文学小说', icon: 'file-text' },
	{ id: 2, name: '教材教辅', icon: 'order' },
	{ id: 3, name: '儿童绘本', icon: 'gift' },
	{ id: 4, name: '杂志期刊', icon: 'bookmark' }
];

export const DEFAULT_CLOTHES_SERVICE_RANGE_DESC = '当前选择区域：{{area}}，支持免费上门；超区将提示改约或邮寄。';
export const DEFAULT_CLOTHES_SERVICE_RANGE_TAG = '已覆盖';
export const DEFAULT_CLOTHES_ORDER_NOTICE = '请提前打包整理好旧衣物，避免混入潮湿、发霉或不可回收品。若回收员到场后发现不符合回收标准，订单会进入异常处理。';
export const DEFAULT_CLOTHES_SCIENCE_TITLE = '旧衣去向科普';
export const DEFAULT_CLOTHES_SCIENCE_DESC = '每一件旧衣，都能成为新的资源，或温暖远方的他。';
export const DEFAULT_CLOTHES_AGREEMENT_TEXT = '我已知晓最低 5kg 起约、不可回收项和实际称重结算规则';

export const DEFAULT_CLOTHES_WEIGHTS = [
	{ title: '5-20kg', desc: '日常清理', estimateAmount: 18.5, sort: 1 },
	{ title: '20-50kg', desc: '换季储物', estimateAmount: 36, sort: 2 },
	{ title: '>50kg', desc: '搬家回收', estimateAmount: 58, sort: 3 }
];

export const DEFAULT_BOOK_WEIGHTS = [
	{ title: '5-15kg', desc: '日常整理', estimateAmount: 5.5, sort: 1 },
	{ title: '15-30kg', desc: '搬家清理', estimateAmount: 12, sort: 2 },
	{ title: '>30kg', desc: '大批量回收', estimateAmount: 22, sort: 3 }
];

const BOOK_SCENES = new Set(['book', 'books']);

export const normalizeBookingScene = (scene = '') => {
	const value = `${scene}`.trim().toLowerCase();
	return BOOK_SCENES.has(value) ? 'book' : 'clothes';
};

export const getBookingSceneMeta = (scene = '') => {
	const normalized = normalizeBookingScene(scene);
	if (normalized === 'book') {
		return {
			scene: 'book',
			pageTitle: '书籍回收',
			heroLabel: '书籍叠放',
			heroDesc: '知识循环 / 环保再生',
			scienceArtLabel: '书籍新生',
			scienceArtDesc: '循环利用',
			configPrefix: 'book',
			defaultTypes: DEFAULT_BOOK_TYPES,
			defaultWeights: DEFAULT_BOOK_WEIGHTS,
			defaultUnitPrice: '0.45',
			defaultUnitSuffix: '/kg 起',
			defaultPriceDesc: '支持上门回收、按重量结算、环保积分返还',
			defaultServiceRangeDesc: '当前选择区域：{{area}}，支持免费上门；超区将提示改约或邮寄。',
			defaultOrderNotice: '请提前打包整理好书籍，避免混入潮湿、发霉或不可回收品。若回收员到场后发现不符合回收标准，订单会进入异常处理。',
			defaultScienceTitle: '书籍去向科普',
			defaultScienceDesc: '每一本旧书，都能成为新的知识传递，或化作环保再生纸。',
			defaultAgreementText: '我已知晓最低 5kg 起约、不可回收项和实际称重结算规则',
			weightConfigKey: 'book_weight_options'
		};
	}
	return {
		scene: 'clothes',
		pageTitle: '旧衣服回收',
		heroLabel: '旧衣叠放',
		heroDesc: '公益捐赠 / 资源再生',
		scienceArtLabel: '衣物新生',
		scienceArtDesc: '循环利用',
		configPrefix: 'clothes',
		defaultTypes: DEFAULT_CLOTHES_TYPES,
		defaultWeights: DEFAULT_CLOTHES_WEIGHTS,
		defaultUnitPrice: '0.8',
		defaultUnitSuffix: '/kg 起',
		defaultPriceDesc: '支持上门回收、称重结算、环保积分返还',
		defaultServiceRangeDesc: '当前选择区域：{{area}}，支持免费上门；超区将提示改约或邮寄。',
		defaultOrderNotice: '请提前打包整理好旧衣物，避免混入潮湿、发霉或不可回收品。若回收员到场后发现不符合回收标准，订单会进入异常处理。',
		defaultScienceTitle: '旧衣去向科普',
		defaultScienceDesc: '每一件旧衣，都能成为新的资源，或温暖远方的他。',
		defaultAgreementText: '我已知晓最低 5kg 起约、不可回收项和实际称重结算规则',
		weightConfigKey: 'clothes_weight_options'
	};
};

export const formatEstimatePrice = (amount = 0) => {
	const value = Number(amount);
	if (!value) {
		return '请选择重量';
	}
	return `¥${value.toFixed(2)}`;
};

export const adaptClothesBookingConfig = ({
	unitPrice = DEFAULT_CLOTHES_UNIT_PRICE,
	unitSuffix = DEFAULT_CLOTHES_UNIT_SUFFIX,
	priceDesc = DEFAULT_CLOTHES_PRICE_DESC
} = {}) => ({
	unitPrice: `${unitPrice}`.trim() || DEFAULT_CLOTHES_UNIT_PRICE,
	unitSuffix: unitSuffix || DEFAULT_CLOTHES_UNIT_SUFFIX,
	priceDesc: priceDesc || DEFAULT_CLOTHES_PRICE_DESC
});

export const adaptClothesTypeFromApi = (item = {}) => {
	const rawIcon = item.icon || '';
	const icon = isAssetPath(rawIcon) ? 'integral' : rawIcon || 'integral';
	return {
		id: item.id,
		name: item.name || '',
		icon,
		sort: item.sort || 0
	};
};

export const adaptClothesPageConfig = ({
	serviceRangeDesc = DEFAULT_CLOTHES_SERVICE_RANGE_DESC,
	serviceRangeTag = DEFAULT_CLOTHES_SERVICE_RANGE_TAG,
	orderNotice = DEFAULT_CLOTHES_ORDER_NOTICE,
	scienceTitle = DEFAULT_CLOTHES_SCIENCE_TITLE,
	scienceDesc = DEFAULT_CLOTHES_SCIENCE_DESC,
	agreementText = DEFAULT_CLOTHES_AGREEMENT_TEXT
} = {}) => ({
	serviceRangeDesc: serviceRangeDesc || DEFAULT_CLOTHES_SERVICE_RANGE_DESC,
	serviceRangeTag: serviceRangeTag || DEFAULT_CLOTHES_SERVICE_RANGE_TAG,
	orderNotice: orderNotice || DEFAULT_CLOTHES_ORDER_NOTICE,
	scienceTitle: scienceTitle || DEFAULT_CLOTHES_SCIENCE_TITLE,
	scienceDesc: scienceDesc || DEFAULT_CLOTHES_SCIENCE_DESC,
	agreementText: agreementText || DEFAULT_CLOTHES_AGREEMENT_TEXT
});

export const adaptClothesWeightFromApi = (item = {}, index = 0) => ({
	title: item.title || '',
	desc: item.desc || '',
	estimateAmount: Number(item.estimateAmount ?? 0),
	sort: item.sort ?? index + 1
});

export const formatServiceRangeDesc = (template = '', area = '') =>
	`${template}`.replace(/\{\{area\}\}/g, area || '当前区域');
