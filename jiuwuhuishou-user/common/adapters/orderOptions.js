export const DEFAULT_CANCEL_REASONS = [
	'时间安排有变/冲突',
	'回收价格不符预期',
	'已通过其他途径处理',
	'选错了物品分类/重量'
];

export const DEFAULT_EXCEPTION_TYPES = [
	'回收员迟到',
	'联系不上',
	'价格争议',
	'物品拒收',
	'服务态度问题',
	'其他异常'
];

export const DEFAULT_EXCEPTION_EXPECTS = [
	'重新上门',
	'改约处理',
	'客服回电',
	'取消订单',
	'发起申诉'
];

const parseJsonArray = (value, fallback = []) => {
	if (!value) {
		return fallback;
	}
	try {
		const parsed = JSON.parse(value);
		return Array.isArray(parsed) ? parsed.filter(Boolean) : fallback;
	} catch (error) {
		return fallback;
	}
};

const parseJsonObject = (value, fallback = {}) => {
	if (!value) {
		return fallback;
	}
	try {
		const parsed = JSON.parse(value);
		return parsed && typeof parsed === 'object' ? parsed : fallback;
	} catch (error) {
		return fallback;
	}
};

export const adaptCancelReasonsFromConfig = (value = '') => {
	const reasons = parseJsonArray(value, DEFAULT_CANCEL_REASONS);
	return reasons.length ? reasons : DEFAULT_CANCEL_REASONS;
};

export const adaptExceptionOptionsFromConfig = (value = '') => {
	const options = parseJsonObject(value, {});
	const types = Array.isArray(options.types) ? options.types.filter(Boolean) : DEFAULT_EXCEPTION_TYPES;
	const expects = Array.isArray(options.expects) ? options.expects.filter(Boolean) : DEFAULT_EXCEPTION_EXPECTS;
	return {
		types: types.length ? types : DEFAULT_EXCEPTION_TYPES,
		expects: expects.length ? expects : DEFAULT_EXCEPTION_EXPECTS
	};
};
