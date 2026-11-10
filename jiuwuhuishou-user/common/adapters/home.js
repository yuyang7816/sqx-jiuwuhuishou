import { resolveAssetUrl } from './user.js';

const CATEGORY_ROUTE_MAP = {
	clothes: '/packageRecycle/clothes/booking',
	appliance: '/packageRecycle/appliance/home',
	digital: '/packageRecycle/digital/models?scene=digital',
	luxury: '/packageRecycle/digital/models?scene=luxury',
	book: '/packageRecycle/clothes/booking?scene=book',
	books: '/packageRecycle/clothes/booking?scene=book'
};

const CATEGORY_ICON_CLASS = {
	clothes: 'is-pink',
	appliance: 'is-red',
	digital: 'is-rose',
	luxury: 'is-rose',
	book: 'is-red',
	books: 'is-red'
};

const CATEGORY_NAME_FALLBACK = {
	家电回收: { icon: 'home', iconClass: 'is-red', url: '/packageRecycle/appliance/home' },
	数码回收: { icon: 'grid', iconClass: 'is-rose', url: '/packageRecycle/digital/models?scene=digital' },
	旧衣回收: { icon: 'gift', iconClass: 'is-pink', url: '/packageRecycle/clothes/booking' },
	奢侈品: { icon: 'coupon', iconClass: 'is-rose', url: '/packageRecycle/digital/models?scene=luxury' },
	书籍: { icon: 'file-text', iconClass: 'is-red', url: '/packageRecycle/clothes/booking?scene=book' }
};

const BANNER_THEME_MAP = {
	clothes: 'cloth',
	appliance: 'device',
	digital: 'digital',
	referral: 'gold',
	invite: 'gold',
	commission: 'gold'
};

const BANNER_ART_MAP = {
	clothes: 'cloth',
	appliance: 'device',
	digital: 'device',
	referral: 'coin',
	invite: 'coin',
	commission: 'coin'
};

const MODULE_ICON_PRESETS = [
	{ icon: 'calendar', iconClass: 'is-gold', iconColor: '#f08b1d' },
	{ icon: 'red-packet', iconClass: 'is-soft-red', iconColor: '#f05858' },
	{ icon: 'bookmark', iconClass: 'is-blue', iconColor: '#5c8ef4' },
	{ icon: 'setting', iconClass: 'is-green', iconColor: '#2cbf63' }
];

const parseModuleMeta = (description = '') => {
	const parts = `${description}`.split('|').map((item) => item.trim()).filter(Boolean);
	return {
		desc: parts[0] || description || '',
		badge: parts[1] || '',
		icon: parts[2] || ''
	};
};

const inferBannerCode = (item = {}) => `${item.code || item.name || ''}`.toLowerCase();

export const adaptPushRemindConfig = (enableValue, textValue) => ({
	show: `${enableValue}` === '1',
	text: textValue || '开启推送提醒，回收进度实时看'
});

export const adaptInviteBannerConfig = ({ title, desc, btnText, url }) => ({
	title: title || '推荐分佣，现金领不停',
	desc: desc || '每邀请一位好友，立得5元现金',
	btnText: btnText || '立即赚钱',
	url: url || '/packageCommon/user/referral-rules'
});

export const adaptBannerFromApi = (item = {}) => {
	const code = inferBannerCode(item);
	const themeKey = Object.keys(BANNER_THEME_MAP).find((key) => code.includes(key)) || 'cloth';
	return {
		id: item.id,
		title: item.name || '',
		desc: item.description || '',
		url: item.url || '',
		imageUrl: resolveAssetUrl(item.imageUrl || ''),
		theme: BANNER_THEME_MAP[themeKey] || 'cloth',
		art: BANNER_ART_MAP[themeKey] || 'cloth',
		cta: '立即查看',
		sort: item.sort || 0
	};
};

/** 首页公告：优先 banner.name，兼容 messages.content/title */
export const adaptNoticeFromApi = (item = {}) => ({
	id: item.id,
	text: item.name || item.content || item.title || item.description || '',
	url: item.url || '',
	sort: item.sort || 0
});

const isAssetPath = (value = '') => /^https?:\/\//i.test(value) || value.startsWith('/') || value.includes('.');

const parseCategoryMeta = (description = '') => {
	const parts = `${description}`.split('|').map((item) => item.trim()).filter(Boolean);
	return {
		icon: parts[0] || '',
		iconClass: parts[1] || ''
	};
};

export const adaptCategoryFromApi = (item = {}) => {
	const rawIcon = item.icon || '';
	const icon = isAssetPath(rawIcon) ? resolveAssetUrl(rawIcon) : rawIcon;
	return {
		id: item.id,
		title: item.name || '',
		icon: icon || 'home',
		iconClass: CATEGORY_ICON_CLASS[item.code] || 'is-red',
		url: CATEGORY_ROUTE_MAP[item.code] || '/packageRecycle/clothes/booking',
		code: item.code || '',
		sort: item.sort || 0
	};
};

/** 首页分类（banner.classify=5） */
export const adaptHomeCategoryFromBanner = (item = {}) => {
	const fallback = CATEGORY_NAME_FALLBACK[item.name] || {};
	const meta = parseCategoryMeta(item.description || '');
	const rawIcon = item.imageUrl || meta.icon || fallback.icon || 'home';
	const icon = isAssetPath(rawIcon) ? resolveAssetUrl(rawIcon) : rawIcon;
	return {
		id: item.id,
		title: item.name || '',
		icon,
		iconClass: meta.iconClass || fallback.iconClass || 'is-red',
		url: item.url || fallback.url || '/packageRecycle/clothes/booking',
		sort: item.sort || 0
	};
};

export const adaptHomeModuleFromApi = (item = {}, index = 0) => {
	const preset = MODULE_ICON_PRESETS[index] || MODULE_ICON_PRESETS[0];
	const meta = parseModuleMeta(item.description || '');
	return {
		id: item.id,
		title: item.name || '',
		desc: meta.desc,
		url: item.url || '',
		imageUrl: resolveAssetUrl(item.imageUrl || ''),
		badge: meta.badge,
		icon: meta.icon || preset.icon,
		iconClass: preset.iconClass,
		iconColor: preset.iconColor,
		sort: item.sort || 0
	};
};

export const sortByOrder = (list = []) => [...list].sort((a, b) => (a.sort || 0) - (b.sort || 0));
