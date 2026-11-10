import config from '../config.js';
import * as applianceApi from '../api/appliance.js';
import {
	adaptApplianceCategoryFromApi,
	adaptApplianceCategoryOverviewFromApi,
	adaptApplianceFaqFromApi,
	adaptApplianceHeroConfig,
	adaptApplianceMoreCarbonConfig,
	adaptApplianceMoreHeroConfig,
	adaptApplianceMorePricingFromApi,
	adaptApplianceMoreServiceFromApi,
	adaptApplianceMoreTipFromApi,
	adaptApplianceScienceFromApi,
	sortByOrder
} from '../adapters/appliance.js';

const DEFAULT_HERO = adaptApplianceHeroConfig({});

const DEFAULT_CATEGORIES = [
	{ id: 1, name: '空调', icon: 'star', sort: 1, priceTitle: '挂机空调回收', priceIcon: 'star', price: '￥200 - 1500' },
	{ id: 2, name: '冰箱', icon: 'home', sort: 2, priceTitle: '十字对开门冰箱', priceIcon: 'home', price: '￥260 - 1800' },
	{ id: 3, name: '洗衣机', icon: 'grid', sort: 3, priceTitle: '滚筒洗衣机回收', priceIcon: 'grid', price: '￥80 - 1200' },
	{ id: 4, name: '电视', icon: 'photo', sort: 4, priceTitle: '液晶电视回收', priceIcon: 'photo', price: '￥100 - 900' }
];

const DEFAULT_SCIENCE = [
	{ id: 1, title: '环保拆解', desc: '正规拆解基地，防止重金属污染，资源循环利用。', icon: 'reload', color: '#00b96b', imageUrl: '/static/image/appliance-science-recycle.png', sort: 1 },
	{ id: 2, title: '隐私清除', desc: '针对智能家电数据彻底清除，确保个人隐私不泄露。', icon: 'shield', color: '#3572ff', imageUrl: '/static/image/appliance-science-privacy.png', sort: 2 }
];

const DEFAULT_FAQS = [
	{ id: 1, q: '坏了的电器也回收吗？', a: '支持大部分损坏家电回收，具体价格会根据品牌、机型和损坏程度评估。', open: false, sort: 1 },
	{ id: 2, q: '回收需要支付上门费吗？', a: '常规服务范围内免费上门，超远距离或特殊拆机可能收取服务费。', open: false, sort: 2 },
	{ id: 3, q: '什么时候能拿到回收款？', a: '现场确认后可当场结算，部分机型会在检测完成后打款。', open: false, sort: 3 }
];

const DEFAULT_MORE_HERO = adaptApplianceMoreHeroConfig({});

const DEFAULT_MORE_PRICING = [
	{
		id: 1,
		title: '大型家电',
		icon: 'home',
		top: '¥80起',
		items: [
			{ name: '空调（挂机/柜机）', price: '¥120~450' },
			{ name: '冰箱（单门/多门）', price: '¥80~260' },
			{ name: '洗衣机（滚筒/波轮）', price: '¥60~180' }
		],
		sort: 1
	},
	{
		id: 2,
		title: '厨房家电',
		icon: 'grid',
		top: '¥20起',
		items: [
			{ name: '微波炉/烤箱', price: '¥20~50' },
			{ name: '油烟机（集成/侧吸）', price: '¥30~80' },
			{ name: '洗碗机/消毒柜', price: '¥50~120' }
		],
		sort: 2
	}
];

const DEFAULT_MORE_SERVICES = [
	{
		id: 1,
		title: '超远距离费',
		desc: '服务半径超过 10 公里后开始计费。',
		icon: 'map',
		imageUrl: '/static/image/appliance-service-distance.png',
		theme: 'gold',
		lines: ['10-15 公里加收 ¥30', '15-25 公里加收 ¥60', '超过 25 公里以上：需与回收员电话协商'],
		tags: [],
		sort: 1
	},
	{
		id: 2,
		title: '特殊拆机服务费',
		desc: '针对高难度或高空作业收取的专业人工费。',
		icon: 'tools',
		imageUrl: '/static/image/appliance-service-install.png',
		theme: 'pink',
		lines: [],
		tags: [
			{ name: '高空空调拆除', price: '¥100 / 台' },
			{ name: '嵌入式厨电拆卸', price: '¥30 / 台' }
		],
		sort: 2
	}
];

const DEFAULT_MORE_TIPS = [
	{ id: 1, q: '什么样的家电不符合回收标准？', a: '严重污染、涉危品、来源不明或已拆解损毁严重的设备不予回收。', open: false, sort: 1 },
	{ id: 2, q: '关于数据隐私处理', a: '含存储介质的智能设备将按标准流程进行数据清除或物理拆解处理。', open: false, sort: 2 }
];

const DEFAULT_MORE_CARBON = adaptApplianceMoreCarbonConfig({});

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await applianceApi.fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

const fetchHeroConfig = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_HERO;
	}
	const flows = await applianceApi.fetchApplianceHeroFlows().catch(() => []);
	const flowList = sortByOrder(
		(Array.isArray(flows) ? flows : []).map((item) => ({
			name: item.name || '',
			icon: item.icon || 'star',
			sort: item.sort || 0
		}))
	).map(({ name, icon }) => ({ name, icon }));
	return adaptApplianceHeroConfig({
		badge: await fetchConfigSafe('appliance_hero_badge', DEFAULT_HERO.badge),
		titleLine1: await fetchConfigSafe('appliance_hero_title_line1', DEFAULT_HERO.titleLine1),
		titleLine2: await fetchConfigSafe('appliance_hero_title_line2', DEFAULT_HERO.titleLine2),
		imageUrl: await fetchConfigSafe('appliance_hero_image', ''),
		flows: flowList
	});
};

const fetchCategoriesWithPrice = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_CATEGORIES;
	}
	try {
		const list = sortByOrder(
			(await applianceApi.fetchApplianceCategoryOverview() || []).map(adaptApplianceCategoryOverviewFromApi)
		);
		if (list.length) {
			return list;
		}
	} catch (error) {
		// 兼容旧版后端：overview 未发布时回退到 category/list + model/list
	}
	const categories = sortByOrder(await applianceApi.fetchApplianceCategories().catch(() => []) || []);
	if (!categories.length) {
		return DEFAULT_CATEGORIES;
	}
	const list = await Promise.all(
		categories.map(async (category) => {
			const models = await applianceApi.fetchApplianceModels(category.id).catch(() => []);
			return adaptApplianceCategoryFromApi(category, models || []);
		})
	);
	return list.length ? list : DEFAULT_CATEGORIES;
};

const fetchScienceList = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_SCIENCE;
	}
	const rules = await applianceApi.fetchRecycleRules().catch(() => []);
	const list = sortByOrder(
		(rules || [])
			.filter((item) => item.ruleType === 'appliance_science' && item.state === 1)
			.map(adaptApplianceScienceFromApi)
	);
	return list.length ? list : DEFAULT_SCIENCE;
};

const fetchFaqList = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_FAQS;
	}
	const rules = await applianceApi.fetchRecycleRules().catch(() => []);
	const list = sortByOrder(
		(rules || [])
			.filter((item) => item.ruleType === 'appliance_faq' && item.state === 1)
			.map(adaptApplianceFaqFromApi)
	);
	return list.length ? list : DEFAULT_FAQS;
};

export const loadApplianceHomeData = async () => {
	const [hero, categories, science, faqs] = await Promise.all([
		fetchHeroConfig(),
		fetchCategoriesWithPrice(),
		fetchScienceList(),
		fetchFaqList()
	]);
	return {
		hero,
		categories,
		science,
		faqs
	};
};

const fetchMoreHero = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_MORE_HERO;
	}
	return adaptApplianceMoreHeroConfig({
		title: await fetchConfigSafe('appliance_more_hero_title', DEFAULT_MORE_HERO.title),
		desc: await fetchConfigSafe('appliance_more_hero_desc', DEFAULT_MORE_HERO.desc),
		icon: await fetchConfigSafe('appliance_more_hero_icon', DEFAULT_MORE_HERO.icon)
	});
};

const fetchMorePricingGroups = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_MORE_PRICING;
	}
	const rules = await applianceApi.fetchRecycleRules().catch(() => []);
	const list = sortByOrder(
		(rules || [])
			.filter((item) => item.ruleType === 'appliance_more_pricing' && item.state === 1)
			.map(adaptApplianceMorePricingFromApi)
	);
	return list.length ? list : DEFAULT_MORE_PRICING;
};

const fetchMoreServiceFees = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_MORE_SERVICES;
	}
	const rules = await applianceApi.fetchRecycleRules().catch(() => []);
	const list = sortByOrder(
		(rules || [])
			.filter((item) => item.ruleType === 'appliance_more_service' && item.state === 1)
			.map(adaptApplianceMoreServiceFromApi)
	);
	return list.length ? list : DEFAULT_MORE_SERVICES;
};

const fetchMoreTips = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_MORE_TIPS;
	}
	const rules = await applianceApi.fetchRecycleRules().catch(() => []);
	const list = sortByOrder(
		(rules || [])
			.filter((item) => item.ruleType === 'appliance_more_tip' && item.state === 1)
			.map(adaptApplianceMoreTipFromApi)
	);
	return list.length ? list : DEFAULT_MORE_TIPS;
};

const fetchMoreCarbon = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_MORE_CARBON;
	}
	return adaptApplianceMoreCarbonConfig({
		title: await fetchConfigSafe('appliance_more_carbon_title', DEFAULT_MORE_CARBON.title),
		desc: await fetchConfigSafe('appliance_more_carbon_desc', DEFAULT_MORE_CARBON.desc)
	});
};

export const loadApplianceMoreData = async () => {
	const [hero, pricingGroups, serviceFees, tips, carbon, priceNote, bookingCategory] = await Promise.all([
		fetchMoreHero(),
		fetchMorePricingGroups(),
		fetchMoreServiceFees(),
		fetchMoreTips(),
		fetchMoreCarbon(),
		fetchConfigSafe('appliance_more_price_note', '* 以上价格仅供参考，实际根据机型、年份及损坏程度面议。'),
		fetchConfigSafe('appliance_more_booking_category', '冰箱')
	]);
	return {
		hero,
		pricingGroups,
		serviceFees,
		tips,
		carbon,
		priceNote,
		bookingCategory
	};
};
