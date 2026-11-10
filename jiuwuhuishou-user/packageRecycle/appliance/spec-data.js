export const applianceConfigs = {
	'空调': {
		title: '空调估价参数',
		subtitle: '选择品牌、型号和使用情况，系统将实时生成预估回收价。',
		icon: 'snowflake',
		cardLabel: '高频回收家电',
		brandBoost: { '格力': 150, '美的': 120, '海尔': 90, '奥克斯': 40 },
		modelBase: 680,
		reward: 48,
		brands: {
			'格力': ['云佳 1.5匹', '云锦 2匹', '天丽 大1匹'],
			'美的': ['风尊 1.5匹', '酷省电 大1匹', '领鲜者 2匹'],
			'海尔': ['静悦 1.5匹', '净省电 大1匹', '洗空气 2匹'],
			'奥克斯': ['京裕 大1匹', '倾静 1.5匹', '奥知音 2匹']
		},
		fields: [
			{ key: 'type', label: '空调类型', options: ['挂机', '柜机', '中央空调内机', '中央空调外机'] },
			{ key: 'capacity', label: '匹数', options: ['1匹', '1.5匹', '2匹', '3匹及以上'] }
		],
		damageOptions: ['无明显故障', '轻微磕碰', '制冷效果一般', '无法制冷', '缺遥控 / 配件'],
		conditionOptions: ['九成新', '良好', '正常老化', '成色一般'],
		ageOptions: ['1年内', '1-3年', '3-5年', '5年以上']
	},
	'冰箱': {
		title: '冰箱估价参数',
		subtitle: '不同门型、容积和外观成色会直接影响最终回收报价。',
		icon: 'grid',
		cardLabel: '大家电专区',
		brandBoost: { '海尔': 180, '美的': 110, '容声': 90, '西门子': 210 },
		modelBase: 860,
		reward: 66,
		brands: {
			'海尔': ['BCD-501WGHFD14S8U1', 'BCD-535WGHSSEDS9', 'BCD-328WDGF'],
			'美的': ['BCD-478WSPZM(E)', 'BCD-407WSPZM(E)', 'BCD-271VMQ'],
			'容声': ['BCD-509WD18MP', 'BCD-452WD12FP', 'BCD-326WD11MP'],
			'西门子': ['KA92NV02TI', 'KG56NV41EC', 'KM46FA09TI']
		},
		fields: [
			{ key: 'doorType', label: '门型', options: ['双门', '三门', '十字对开门', '多门法式'] },
			{ key: 'capacity', label: '容积', options: ['200L以下', '200-300L', '300-400L', '400L以上'] }
		],
		damageOptions: ['无明显损伤', '轻微划痕', '门封老化', '制冷异常', '压缩机异响'],
		conditionOptions: ['九成新', '良好', '正常老化', '外观磨损明显'],
		ageOptions: ['1年内', '1-3年', '3-5年', '5年以上']
	},
	'洗衣机': {
		title: '洗衣机估价参数',
		subtitle: '滚筒、波轮和容量不同，回收价格区间会有明显差异。',
		icon: 'reload',
		cardLabel: '家电焕新',
		brandBoost: { '小天鹅': 160, '海尔': 120, '美的': 90, '松下': 180 },
		modelBase: 520,
		reward: 42,
		brands: {
			'小天鹅': ['TG100V86WMDY5', 'TD100V618T', 'TB80V23H'],
			'海尔': ['EG100MATE3S', 'XQG100-BD14326L', 'EB80M39TH'],
			'美的': ['MG100VT55DY', 'MD100V11D', 'MB80ECO'],
			'松下': ['XQG100-EGALW', 'XQG100-NA10C', 'XQB80-T8R']
		},
		fields: [
			{ key: 'type', label: '机型', options: ['滚筒', '波轮', '洗烘一体', '迷你洗衣机'] },
			{ key: 'capacity', label: '容量', options: ['6kg以下', '6-8kg', '8-10kg', '10kg以上'] }
		],
		damageOptions: ['无明显问题', '轻微磕碰', '排水异常', '电机噪音大', '无法启动'],
		conditionOptions: ['九成新', '良好', '正常老化', '成色一般'],
		ageOptions: ['1年内', '1-3年', '3-5年', '5年以上']
	},
	'电视': {
		title: '电视估价参数',
		subtitle: '屏幕尺寸、显示状态和品牌溢价会影响回收价格。',
		icon: 'play-right',
		cardLabel: '智能家电',
		brandBoost: { '小米': 100, '海信': 120, 'TCL': 110, '索尼': 260 },
		modelBase: 460,
		reward: 36,
		brands: {
			'小米': ['EA55 2024款', 'S65 Mini LED', 'Redmi A75'],
			'海信': ['55E5N Pro', '65U7N', '75E8N Ultra'],
			'TCL': ['55Q10K', '65T7K', '75Q9K'],
			'索尼': ['XR-55X90L', 'XR-65A80L', 'KD-75X85K']
		},
		fields: [
			{ key: 'screenType', label: '屏幕类型', options: ['液晶 LED', 'OLED', 'Mini LED', '老式等离子'] },
			{ key: 'size', label: '尺寸', options: ['43寸以下', '43-55寸', '55-65寸', '65寸以上'] }
		],
		damageOptions: ['无明显故障', '边框磕碰', '屏幕轻微老化', '显示异常', '碎屏 / 黑屏'],
		conditionOptions: ['九成新', '良好', '正常老化', '成色一般'],
		ageOptions: ['1年内', '1-3年', '3-5年', '5年以上']
	}
};

export const applianceCategories = Object.keys(applianceConfigs);

const getCategoryConfig = (category) => applianceConfigs[category] || applianceConfigs['冰箱'];

export const createDefaultSpec = (category = '冰箱') => {
	const config = getCategoryConfig(category);
	const brands = Object.keys(config.brands);
	const brand = brands[0] || '';
	const models = config.brands[brand] || [];
	const details = {};

	config.fields.forEach((field) => {
		details[field.key] = field.options[0] || '';
	});

	return {
		category,
		brand,
		model: models[0] || '',
		minPrice: config.modelBase,
		maxPrice: config.modelBase,
		damage: config.damageOptions[0] || '',
		condition: config.conditionOptions[1] || config.conditionOptions[0] || '',
		age: config.ageOptions[1] || config.ageOptions[0] || '',
		details
	};
};

export const normalizeSpec = (rawSpec, category) => {
	const base = createDefaultSpec(category || rawSpec?.category || '冰箱');
	const config = getCategoryConfig(base.category);
	const brands = Object.keys(config.brands);
	const brand = brands.includes(rawSpec?.brand) ? rawSpec.brand : base.brand;
	const models = config.brands[brand] || [];
	const model = models.includes(rawSpec?.model) ? rawSpec.model : (models[0] || '');
	const details = {};

	config.fields.forEach((field) => {
		const selected = rawSpec?.details?.[field.key];
		details[field.key] = field.options.includes(selected) ? selected : (base.details[field.key] || field.options[0] || '');
	});

	return {
		category: base.category,
		categoryId: rawSpec?.categoryId ?? null,
		brand,
		model,
		modelId: rawSpec?.modelId ?? null,
		minPrice: Number(rawSpec?.minPrice ?? config.modelBase ?? 0),
		maxPrice: Number(rawSpec?.maxPrice ?? config.modelBase ?? 0),
		damage: config.damageOptions.includes(rawSpec?.damage) ? rawSpec.damage : base.damage,
		condition: config.conditionOptions.includes(rawSpec?.condition) ? rawSpec.condition : base.condition,
		age: config.ageOptions.includes(rawSpec?.age) ? rawSpec.age : base.age,
		details
	};
};

export const parseSpecFromQuery = (value, fallbackCategory) => {
	if (!value) return createDefaultSpec(fallbackCategory || '冰箱');
	try {
		const parsed = JSON.parse(decodeURIComponent(value));
		if (!parsed || typeof parsed !== 'object') {
			return createDefaultSpec(fallbackCategory || '冰箱');
		}
		// 保留用户实际选择，避免用本地旧版目录冲掉 API 机型/参数
		return {
			...parsed,
			category: parsed.category || fallbackCategory || '冰箱',
			details: parsed.details && typeof parsed.details === 'object' ? { ...parsed.details } : {}
		};
	} catch (error) {
		return createDefaultSpec(fallbackCategory || '冰箱');
	}
};

export const encodeSpecToQuery = (spec) => encodeURIComponent(JSON.stringify(spec));

const detailScoreMap = {
	'挂机': 40,
	'柜机': 120,
	'中央空调内机': 80,
	'中央空调外机': 150,
	'1匹': 20,
	'1.5匹': 60,
	'2匹': 110,
	'3匹及以上': 180,
	'双门': 30,
	'三门': 60,
	'十字对开门': 130,
	'多门法式': 160,
	'200L以下': 10,
	'200-300L': 50,
	'300-400L': 90,
	'400L以上': 150,
	'滚筒': 90,
	'波轮': 30,
	'洗烘一体': 130,
	'迷你洗衣机': -40,
	'6kg以下': -20,
	'6-8kg': 20,
	'8-10kg': 70,
	'10kg以上': 120,
	'液晶 LED': 40,
		'OLED': 180,
		'Mini LED': 160,
		'老式等离子': -50,
		'43寸以下': -20,
		'43-55寸': 40,
		'55-65寸': 90,
		'65寸以上': 150
};

const conditionScoreMap = {
	'九成新': 180,
	'良好': 110,
	'正常老化': 40,
	'成色一般': -40,
	'外观磨损明显': -80
};

const ageScoreMap = {
	'1年内': 180,
	'1-3年': 110,
	'3-5年': 40,
	'5年以上': -90
};

const damageScoreMap = {
	'无明显故障': 120,
	'无明显损伤': 120,
	'无明显问题': 120,
	'轻微磕碰': 30,
	'轻微划痕': 40,
	'边框磕碰': 10,
	'制冷效果一般': -60,
	'制冷异常': -120,
	'压缩机异响': -150,
	'排水异常': -80,
	'电机噪音大': -90,
	'显示异常': -140,
	'屏幕轻微老化': -60,
	'门封老化': -40,
	'缺遥控 / 配件': -30,
	'无法制冷': -180,
	'无法启动': -180,
	'碎屏 / 黑屏': -220
};

export const buildEstimate = (spec) => {
	const normalized = normalizeSpec(spec);
	const config = getCategoryConfig(normalized.category);
	const brandScore = config.brandBoost[normalized.brand] || 0;
	const modelScore = Math.max(0, (config.brands[normalized.brand] || []).indexOf(normalized.model)) * 36;
	const detailScore = Object.values(normalized.details || {}).reduce((sum, item) => {
		if (Array.isArray(item)) {
			return sum + item.reduce((inner, value) => inner + (detailScoreMap[value] || 0), 0);
		}
		return sum + (detailScoreMap[item] || 0);
	}, 0);
	const conditionScore = conditionScoreMap[normalized.condition] || 0;
	const ageScore = ageScoreMap[normalized.age] || 0;
	const damageScore = damageScoreMap[normalized.damage] || 0;
	const adjust = brandScore + modelScore + detailScore + conditionScore + ageScore + damageScore;
	const minBase = Number(normalized.minPrice ?? spec?.minPrice ?? config.modelBase ?? 0);
	const maxBase = Number(normalized.maxPrice ?? spec?.maxPrice ?? config.modelBase ?? 0);
	const low = Math.max(60, Math.min(minBase, maxBase) + adjust);
	const high = Math.max(low, Math.max(minBase, maxBase) + adjust);
	const estimate = Number(((low + high) / 2).toFixed(2));

	return {
		estimate,
		reward: 0,
		low,
		high
	};
};

export const formatCurrency = (amount) => `￥${Number(amount || 0).toFixed(2)}`;

/** 展示用：直接使用用户已选参数，避免被本地旧版目录 normalize 成默认机型 */
const flattenDetailDisplayValues = (details = {}) =>
	Object.values(details || {}).flatMap((value) => {
		if (Array.isArray(value)) {
			return value.filter(Boolean);
		}
		return value ? [value] : [];
	});

const resolveDisplaySpec = (spec = {}) => ({
	brand: `${spec.brand || ''}`.trim(),
	model: `${spec.model || ''}`.trim(),
	damage: `${spec.damage || ''}`.trim(),
	condition: `${spec.condition || ''}`.trim(),
	age: `${spec.age || ''}`.trim(),
	details: spec.details && typeof spec.details === 'object' ? spec.details : {}
});

export const buildSpecSummary = (spec) => {
	const display = resolveDisplaySpec(spec);
	const detailText = flattenDetailDisplayValues(display.details).join(' · ');
	const ageText = display.age ? (display.age.startsWith('购入') ? display.age : `购入 ${display.age}`) : '';
	return [detailText, display.condition, display.damage, ageText].filter(Boolean).join(' · ');
};

export const buildSpecTagList = (spec) => {
	const display = resolveDisplaySpec(spec);
	return [
		display.brand,
		display.model,
		...flattenDetailDisplayValues(display.details),
		display.condition,
		display.damage,
		display.age
	].filter(Boolean);
};
