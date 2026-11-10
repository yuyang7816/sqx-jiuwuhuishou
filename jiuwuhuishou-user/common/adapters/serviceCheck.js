const DEFAULT_STEPS = [
	{ step: 1, txt: '看范围' },
	{ step: 2, txt: '看门槛' },
	{ step: 3, txt: '看价格' },
	{ step: 4, txt: '看售后' }
];

const DEFAULT_COVERAGE = [
	{ title: '核心城区上门回收', desc: '支持当日或次日预约，运力充足时可优先派单。', tag: '可约' },
	{ title: '远郊区域', desc: '如超出常规半径，系统会提示改约或切换邮寄回收。', tag: '需确认' },
	{ title: '未开通城市', desc: '当前暂不支持上门，会引导用户收藏开通提醒。', tag: '暂未覆盖' }
];

const DEFAULT_THRESHOLDS = [
	{ title: '旧衣回收', desc: '建议 5kg 起约，需提前打包，潮湿发霉件不可收。', icon: 'gift' },
	{ title: '家电回收', desc: '需确认型号、楼层、电梯和拆装情况，大件默认预约上门。', icon: 'home' },
	{ title: '数码回收', desc: '需填写机型与成色，最终价格以质检结果为准。', icon: 'grid' },
	{ title: '混合可回收物', desc: '需满足最低回收量，系统会提示是否达到上门门槛。', icon: 'reload' }
];

const DEFAULT_RULES = [
	{ title: '预估价不等于成交价', desc: '家电和数码展示的是预估回收价，最终以实物检测、称重或质检结果为准。' },
	{ title: '差价过大需要用户确认', desc: '若实际价格和预估差异明显，系统会生成结算确认单，用户确认后才会成交。' },
	{ title: '打款状态全程可追踪', desc: '完成结算后会进入待打款、打款中、已到账或打款失败状态，并支持补充收款信息。' }
];

const DEFAULT_REJECTS = ['来源不明物品', '涉危涉违禁品', '严重污染物', '进水报废数码', '霉变旧衣', '已拆解家电'];

const DEFAULT_AFTERSALES = [
	{ title: '支持改约', desc: '回收员未上门前，用户可修改时间；部分场景可同步调整地址。' },
	{ title: '支持异常反馈', desc: '如超时未上门、联系不上、物品不符、价格争议等，可直接在订单内提交。' },
	{ title: '支持评价与投诉', desc: '订单完成后可做服务评价，也可发起投诉或补充凭证。' }
];

export const DEFAULT_SERVICE_CHECK = {
	heroTitle: '下单前先确认这 4 件事',
	heroDesc: '覆盖范围、最低门槛、价格规则、不可回收项，先说清楚再下单，能明显降低取消和争议。',
	heroTag: '统一适用于旧衣 / 家电 / 数码',
	steps: DEFAULT_STEPS,
	coverage: DEFAULT_COVERAGE,
	thresholds: DEFAULT_THRESHOLDS,
	rules: DEFAULT_RULES,
	rejects: DEFAULT_REJECTS,
	aftersales: DEFAULT_AFTERSALES,
	areaCheckInServiceDesc: '当前地址支持预约上门回收，可直接选择时段下单。',
	areaCheckOutServiceDesc: '当前地址暂未开通上门回收，可尝试邮寄回收或更换地址后重试。'
};

const parseJsonObject = (value) => {
	if (!value) {
		return null;
	}
	try {
		const parsed = JSON.parse(value);
		return parsed && typeof parsed === 'object' ? parsed : null;
	} catch (error) {
		return null;
	}
};

const normalizeItems = (items, fallback = [], keys = ['title', 'desc']) => {
	if (!Array.isArray(items) || !items.length) {
		return fallback;
	}
	return items.map((item, index) => {
		const base = fallback[index] || {};
		const next = { ...base };
		keys.forEach((key) => {
			if (item?.[key]) {
				next[key] = item[key];
			}
		});
		if (item?.tag) {
			next.tag = item.tag;
		}
		if (item?.icon) {
			next.icon = item.icon;
		}
		if (item?.step) {
			next.step = item.step;
		}
		if (item?.txt) {
			next.txt = item.txt;
		}
		return next;
	}).filter((item) => item.title || item.txt);
};

export const adaptServiceCheckFromConfig = (rawValue = '') => {
	const config = parseJsonObject(rawValue) || {};
	return {
		heroTitle: config.heroTitle || DEFAULT_SERVICE_CHECK.heroTitle,
		heroDesc: config.heroDesc || DEFAULT_SERVICE_CHECK.heroDesc,
		heroTag: config.heroTag || DEFAULT_SERVICE_CHECK.heroTag,
		steps: normalizeItems(config.steps, DEFAULT_STEPS, ['step', 'txt']),
		coverage: normalizeItems(config.coverage, DEFAULT_COVERAGE, ['title', 'desc']).map((item, index) => ({
			...DEFAULT_COVERAGE[index],
			...item,
			tag: config.coverage?.[index]?.tag || item.tag || DEFAULT_COVERAGE[index]?.tag || ''
		})),
		thresholds: normalizeItems(config.thresholds, DEFAULT_THRESHOLDS, ['title', 'desc']).map((item, index) => ({
			...DEFAULT_THRESHOLDS[index],
			...item,
			icon: config.thresholds?.[index]?.icon || item.icon || DEFAULT_THRESHOLDS[index]?.icon || 'grid'
		})),
		rules: normalizeItems(config.rules, DEFAULT_RULES, ['title', 'desc']),
		rejects: Array.isArray(config.rejects) && config.rejects.length ? config.rejects : DEFAULT_REJECTS,
		aftersales: normalizeItems(config.aftersales, DEFAULT_AFTERSALES, ['title', 'desc']),
		areaCheckInServiceDesc: config.areaCheckInServiceDesc || DEFAULT_SERVICE_CHECK.areaCheckInServiceDesc,
		areaCheckOutServiceDesc: config.areaCheckOutServiceDesc || DEFAULT_SERVICE_CHECK.areaCheckOutServiceDesc
	};
};

export const buildServiceAreaStatus = (inService, pageData = DEFAULT_SERVICE_CHECK) => ({
	inService: !!inService,
	tag: inService ? '已覆盖' : '暂未覆盖',
	desc: inService ? pageData.areaCheckInServiceDesc : pageData.areaCheckOutServiceDesc
});
