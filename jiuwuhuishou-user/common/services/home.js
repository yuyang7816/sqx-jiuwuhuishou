import config from '../config.js';
import * as homeApi from '../api/home.js';
import {
	adaptBannerFromApi,
	adaptCategoryFromApi,
	adaptHomeCategoryFromBanner,
	adaptHomeModuleFromApi,
	adaptInviteBannerConfig,
	adaptNoticeFromApi,
	adaptPushRemindConfig,
	sortByOrder
} from '../adapters/home.js';

const DEFAULT_BANNERS = [
	{
		id: 1,
		title: '旧衣回收',
		desc: '给旧衣服一个温暖的归宿',
		url: '/packageRecycle/clothes/booking',
		imageUrl: '',
		art: 'cloth',
		theme: 'cloth',
		cta: '立即参与',
		sort: 1
	},
	{
		id: 2,
		title: '推荐分佣',
		desc: '最高返现奖励',
		url: '/packageCommon/user/referral-rules',
		imageUrl: '',
		art: 'coin',
		theme: 'gold',
		cta: '立即赚钱',
		sort: 2
	},
	{
		id: 3,
		title: '数码回收',
		desc: '品牌机型，一键估价',
		url: '/packageRecycle/digital/search',
		imageUrl: '',
		art: 'device',
		theme: 'digital',
		cta: '立即估价',
		sort: 3
	}
];

const DEFAULT_NOTICES = [
	{ id: 1, text: '王** 刚刚回收旧衣获得 15.5 元' },
	{ id: 2, text: '张** 刚刚回收纸箱获得 8.2 元' },
	{ id: 3, text: '李** 刚刚回收旧家电获得 126.0 元' },
	{ id: 4, text: '陈** 刚刚回收数码获得 358.0 元' }
];

const DEFAULT_CATEGORIES = [
	{ id: 1, title: '家电回收', icon: 'home', iconClass: 'is-red', url: '/packageRecycle/appliance/home', sort: 1 },
	{ id: 2, title: '数码回收', icon: 'grid', iconClass: 'is-rose', url: '/packageRecycle/digital/models?scene=digital', sort: 2 },
	{ id: 3, title: '旧衣回收', icon: 'gift', iconClass: 'is-pink', url: '/packageRecycle/clothes/booking', sort: 3 },
	{ id: 4, title: '奢侈品', icon: 'coupon', iconClass: 'is-rose', url: '/packageRecycle/digital/models?scene=luxury', sort: 4 },
	{ id: 5, title: '书籍', icon: 'file-text', iconClass: 'is-red', url: '/packageRecycle/clothes/booking?scene=book', sort: 5 }
];

const DEFAULT_MODULES = [
	{
		id: 1,
		title: '每日签到',
		desc: '连续签到领好礼',
		icon: 'calendar',
		iconClass: 'is-gold',
		iconColor: '#f08b1d',
		badge: '+5',
		url: '/pages/welfare/welfare',
		sort: 1
	},
	{
		id: 2,
		title: '环保红包',
		desc: '下单最高返20元',
		icon: 'red-packet',
		iconClass: 'is-soft-red',
		iconColor: '#f05858',
		badge: '',
		url: '/packageCommon/user/invite',
		sort: 2
	},
	{
		id: 3,
		title: '品牌合作',
		desc: '联名环保周边兑换',
		icon: 'bookmark',
		iconClass: 'is-blue',
		iconColor: '#5c8ef4',
		badge: '',
		url: '/packageCommon/user/brand-cooperation',
		sort: 3
	},
	{
		id: 4,
		title: '家电维修',
		desc: '以修代换更环保',
		icon: 'setting',
		iconClass: 'is-green',
		iconColor: '#2cbf63',
		badge: '',
		url: '/packageCommon/service/chat',
		sort: 4
	}
];

const DEFAULT_INVITE_BANNER = adaptInviteBannerConfig({});

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await homeApi.fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

const fetchBannerList = async (classify) => {
	const page = await homeApi.fetchBannerPage({
		page: 1,
		limit: 20,
		classify,
		state: 1
	});
	return sortByOrder((page?.records || []).map(adaptBannerFromApi));
};

export const fetchPushRemindConfig = async () => {
	if (config.USE_MOCK) {
		return adaptPushRemindConfig('1', '开启推送提醒，回收进度实时看');
	}
	const enableValue = await fetchConfigSafe('app_push_remind_enable', '0');
	const textValue = await fetchConfigSafe('app_push_remind_text', '开启推送提醒，回收进度实时看');
	return adaptPushRemindConfig(enableValue, textValue);
};

export const fetchHomeBanners = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_BANNERS;
	}
	const list = await fetchBannerList(1);
	return list.length ? list : DEFAULT_BANNERS;
};

export const fetchHomeNotices = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_NOTICES;
	}
	try {
		const page = await homeApi.fetchBannerPage({
			page: 1,
			limit: 20,
			classify: 6,
			state: 1
		});
		const list = sortByOrder((page?.records || []).map(adaptNoticeFromApi)).filter((item) => item.text);
		if (list.length) {
			return list;
		}
	} catch (error) {
		// 兼容旧后端：回退系统公告 messages.type=1
	}
	try {
		const page = await homeApi.fetchMessagePage({
			page: 1,
			limit: 20,
			type: 1
		});
		const list = sortByOrder((page?.records || []).map(adaptNoticeFromApi)).filter((item) => item.text);
		if (list.length) {
			return list;
		}
	} catch (error) {
		// ignore
	}
	return DEFAULT_NOTICES;
};

export const fetchHomeCategories = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_CATEGORIES;
	}
	try {
		const page = await homeApi.fetchBannerPage({
			page: 1,
			limit: 20,
			classify: 5,
			state: 1
		});
		const list = sortByOrder((page?.records || []).map(adaptHomeCategoryFromBanner));
		if (list.length) {
			return list;
		}
	} catch (error) {
		// 兼容旧后端：回退 recycle_category
	}
	const list = sortByOrder(
		(await homeApi.fetchRecycleCategories().catch(() => []) || [])
			.filter((item) => item.state === 1)
			.map(adaptCategoryFromApi)
	);
	return list.length ? list : DEFAULT_CATEGORIES;
};

export const fetchHomeModules = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_MODULES;
	}
	const page = await homeApi.fetchBannerPage({
		page: 1,
		limit: 20,
		classify: 2,
		state: 1
	});
	const list = sortByOrder((page?.records || []).map(adaptHomeModuleFromApi));
	return list.length ? list : DEFAULT_MODULES;
};

export const fetchInviteBanner = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_INVITE_BANNER;
	}
	return adaptInviteBannerConfig({
		title: await fetchConfigSafe('home_invite_title', DEFAULT_INVITE_BANNER.title),
		desc: await fetchConfigSafe('home_invite_desc', DEFAULT_INVITE_BANNER.desc),
		btnText: await fetchConfigSafe('home_invite_btn_text', DEFAULT_INVITE_BANNER.btnText),
		url: await fetchConfigSafe('home_invite_url', DEFAULT_INVITE_BANNER.url)
	});
};

export const loadHomePageData = async () => {
	const [pushRemind, banners, notices, categories, inviteBanner, modules] = await Promise.all([
		fetchPushRemindConfig(),
		fetchHomeBanners(),
		fetchHomeNotices(),
		fetchHomeCategories(),
		fetchInviteBanner(),
		fetchHomeModules()
	]);
	return {
		pushRemind,
		banners,
		notices,
		categories,
		inviteBanner,
		modules
	};
};

export const createHomeModuleAction = (item, navigate) => {
	if (!item?.url) {
		return () => {};
	}
	if (item.url.startsWith('/pages/')) {
		return () => uni.switchTab({ url: item.url });
	}
	return () => navigate(item.url);
};
