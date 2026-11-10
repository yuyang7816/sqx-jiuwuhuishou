import { resolveAssetUrl } from './user.js';

const parseJsonArray = (value, fallback = []) => {
	if (Array.isArray(value)) {
		return value.filter(Boolean);
	}
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

export const DEFAULT_CHAT_WELCOME_TITLE = '您好！我是您的环保管家"小碳"。很荣幸为您服务！';
export const DEFAULT_CHAT_WELCOME_DESC = '在旧物回收，每一次正确的废品回收都在为地球降温。请问有什么我可以帮助您的？';
export const DEFAULT_CHAT_QUICK_QUESTIONS = ['如何预约上门回收？', '碳积分如何兑换礼品？', '回收品类及价格标准'];
export const DEFAULT_CHAT_SHORTCUTS = ['价格查询', '订单进度', '积分兑换', '转人工'];
export const DEFAULT_TRANSFER_HINT = '正在转接人工，请稍等···';

const DEFAULT_QUICK_REPLY_MAP = {
	'如何预约上门回收？': '亲，旧家电回收非常方便！\n1. 在首页点击「上门回收」\n2. 选择对应品类并上传照片\n3. 提交订单后，回收员通常会在2小时内联系您确定具体时间。',
	'碳积分如何兑换礼品？': '请进入福利中心查看当前积分与可兑换商品，达标后可一键兑换。',
	'回收品类及价格标准': '平台支持旧衣、家电、数码、书籍等回收，价格以质检后实际成交结果为准。',
	'价格查询': '您可在各品类估价页查看参考价格，最终以质检后确认结果为准。',
	'订单进度': '请前往「我的订单」查看当前状态，包含待上门、取件中、结算确认、打款中等节点。',
	'积分兑换': '请进入福利中心查看当前积分与可兑换商品。'
};

export const adaptChatConfig = ({
	welcomeTitle = DEFAULT_CHAT_WELCOME_TITLE,
	welcomeDesc = DEFAULT_CHAT_WELCOME_DESC,
	quickQuestions = DEFAULT_CHAT_QUICK_QUESTIONS,
	shortcuts = DEFAULT_CHAT_SHORTCUTS
} = {}) => ({
	welcomeTitle,
	welcomeDesc,
	quickQuestions: normalizeQuestionList(quickQuestions, DEFAULT_CHAT_QUICK_QUESTIONS),
	shortcuts: normalizeShortcutList(shortcuts, DEFAULT_CHAT_SHORTCUTS)
});

const normalizeQuestionList = (value, fallback = []) => {
	if (Array.isArray(value)) {
		const list = value
			.map((item) => {
				if (typeof item === 'string') {
					return item.trim();
				}
				return `${item?.question || ''}`.trim();
			})
			.filter(Boolean);
		return list.length ? list : fallback;
	}
	return parseJsonArray(value, fallback);
};

const normalizeShortcutList = (value, fallback = []) => {
	if (Array.isArray(value)) {
		const list = value
			.map((item) => {
				if (typeof item === 'string') {
					return item.trim();
				}
				return `${item?.content || ''}`.trim();
			})
			.filter(Boolean);
		return list.length ? list : fallback;
	}
	return parseJsonArray(value, fallback);
};
export const adaptServiceMessageFromApi = (item = {}) => ({
	id: item.id,
	content: item.messageType === 'image' ? resolveAssetUrl(item.content || '') : (item.content || ''),
	messageType: item.messageType || 'text',
	senderType: item.senderType || 1,
	createTime: item.createTime || '',
	isUser: item.senderType === 1
});

export const adaptServiceMessagePageFromApi = (page = {}) => ({
	records: (page.records || []).map(adaptServiceMessageFromApi),
	total: page.total || 0
});

export const adaptQuickAskFromApi = (data = {}) => ({
	userMessage: data.userMessage ? adaptServiceMessageFromApi(data.userMessage) : null,
	replyMessage: data.replyMessage ? adaptServiceMessageFromApi(data.replyMessage) : null,
	transferring: !!data.transferring,
	transferHint: data.transferHint || DEFAULT_TRANSFER_HINT
});

export const buildMockQuickAskResult = (question) => {
	const userMessage = adaptServiceMessageFromApi({
		id: Date.now(),
		content: question,
		messageType: 'text',
		senderType: 1,
		createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
	});
	if (question === '转人工') {
		return {
			userMessage,
			replyMessage: null,
			transferring: true,
			transferHint: DEFAULT_TRANSFER_HINT
		};
	}
	return {
		userMessage,
		replyMessage: adaptServiceMessageFromApi({
			id: Date.now() + 1,
			content: DEFAULT_QUICK_REPLY_MAP[question] || '您好，已收到您的问题，我们会尽快为您解答。如需人工协助，可点击「转人工」。',
			messageType: 'text',
			senderType: 2,
			createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
		}),
		transferring: false,
		transferHint: ''
	};
};

export const formatChatTime = (time = '') => {
	if (!time) {
		return '';
	}
	const date = new Date(String(time).replace(/-/g, '/'));
	if (Number.isNaN(date.getTime())) {
		return time;
	}
	const hours = `${date.getHours()}`.padStart(2, '0');
	const minutes = `${date.getMinutes()}`.padStart(2, '0');
	return `${hours}:${minutes}`;
};
