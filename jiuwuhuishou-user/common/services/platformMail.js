import config from '../config.js';
import { fetchConfigValue } from '../api/home.js';

export const PLATFORM_MAIL_RECEIVER_KEY = 'platform_mail_receiver';
export const PLATFORM_MAIL_RECEIVER_PHONE_KEY = 'platform_mail_receiver_phone';
export const PLATFORM_MAIL_RECEIVER_ADDRESS_KEY = 'platform_mail_receiver_address';

export const DEFAULT_PLATFORM_MAIL = {
	receiver: '平台仓储部',
	phone: '400-800-6688',
	address: '上海市浦东新区张江高科技园区纳贤路365号 旧物回收仓储中心'
};

const fetchConfigSafe = async (key, fallback = '') => {
	try {
		const data = await fetchConfigValue(key);
		return data?.value ?? fallback;
	} catch (error) {
		return fallback;
	}
};

export const formatPlatformMailText = (mail = {}) => {
	const receiver = mail.receiver || DEFAULT_PLATFORM_MAIL.receiver;
	const phone = mail.phone || DEFAULT_PLATFORM_MAIL.phone;
	const address = mail.address || DEFAULT_PLATFORM_MAIL.address;
	return `${address} 收件人：${receiver} 电话：${phone}`;
};

export const loadPlatformMailReceiver = async () => {
	if (config.USE_MOCK) {
		return { ...DEFAULT_PLATFORM_MAIL };
	}
	const [receiver, phone, address] = await Promise.all([
		fetchConfigSafe(PLATFORM_MAIL_RECEIVER_KEY, DEFAULT_PLATFORM_MAIL.receiver),
		fetchConfigSafe(PLATFORM_MAIL_RECEIVER_PHONE_KEY, DEFAULT_PLATFORM_MAIL.phone),
		fetchConfigSafe(PLATFORM_MAIL_RECEIVER_ADDRESS_KEY, DEFAULT_PLATFORM_MAIL.address)
	]);
	return {
		receiver: receiver || DEFAULT_PLATFORM_MAIL.receiver,
		phone: phone || DEFAULT_PLATFORM_MAIL.phone,
		address: address || DEFAULT_PLATFORM_MAIL.address
	};
};
