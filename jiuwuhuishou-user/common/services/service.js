import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';
import * as serviceApi from '../api/service.js';
import {
	adaptChatConfig,
	adaptQuickAskFromApi,
	adaptServiceMessageFromApi,
	adaptServiceMessagePageFromApi,
	buildMockQuickAskResult,
	DEFAULT_CHAT_QUICK_QUESTIONS,
	DEFAULT_CHAT_SHORTCUTS,
	DEFAULT_CHAT_WELCOME_DESC,
	DEFAULT_CHAT_WELCOME_TITLE
} from '../adapters/service.js';

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

const MOCK_MESSAGES = [];

export const loadChatPageConfig = async () => {
	if (config.USE_MOCK) {
		return adaptChatConfig();
	}
	const [welcomeTitle, welcomeDesc, quickQuestions, shortcuts] = await Promise.all([
		fetchConfigSafe('service_chat_welcome_title', DEFAULT_CHAT_WELCOME_TITLE),
		fetchConfigSafe('service_chat_welcome_desc', DEFAULT_CHAT_WELCOME_DESC),
		serviceApi.fetchServiceQuickQuestions().catch(() => DEFAULT_CHAT_QUICK_QUESTIONS),
		serviceApi.fetchServiceShortcuts().catch(() => DEFAULT_CHAT_SHORTCUTS)
	]);
	return adaptChatConfig({
		welcomeTitle,
		welcomeDesc,
		quickQuestions,
		shortcuts
	});
};

export const loadServiceMessages = async ({ page = 1, limit = 100 } = {}) => {
	if (config.USE_MOCK) {
		return {
			records: [...MOCK_MESSAGES],
			total: MOCK_MESSAGES.length
		};
	}
	const data = await serviceApi.fetchServiceMessagePage({ page, limit });
	return adaptServiceMessagePageFromApi(data);
};

export const sendServiceMessage = async ({ content, messageType = 'text' }) => {
	const text = `${content || ''}`.trim();
	if (!text) {
		throw new Error('请输入消息内容');
	}
	if (config.USE_MOCK) {
		const message = adaptServiceMessageFromApi({
			id: Date.now(),
			content: text,
			messageType,
			senderType: 1,
			createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
		});
		MOCK_MESSAGES.push(message);
		return message;
	}
	const data = await serviceApi.saveServiceMessage({ content: text, messageType });
	return adaptServiceMessageFromApi(data);
};

export const askQuickQuestion = async (question) => {
	const text = `${question || ''}`.trim();
	if (!text) {
		throw new Error('问题不能为空');
	}
	if (config.USE_MOCK) {
		const result = buildMockQuickAskResult(text);
		if (result.userMessage) {
			MOCK_MESSAGES.push(result.userMessage);
		}
		if (result.replyMessage) {
			MOCK_MESSAGES.push(result.replyMessage);
		}
		return result;
	}
	const data = await serviceApi.askServiceQuickQuestion({ question: text });
	return adaptQuickAskFromApi(data);
};

export const DEFAULT_CHAT_POLL_INTERVAL = 3000;

export const loadChatPollInterval = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_CHAT_POLL_INTERVAL;
	}
	const value = await fetchConfigSafe('service_chat_poll_interval', `${DEFAULT_CHAT_POLL_INTERVAL}`);
	const interval = Number(value);
	if (Number.isNaN(interval) || interval < 2000) {
		return DEFAULT_CHAT_POLL_INTERVAL;
	}
	return interval;
};
