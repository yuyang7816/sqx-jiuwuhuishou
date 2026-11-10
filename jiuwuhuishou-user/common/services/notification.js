import config from '../config.js';
import * as notificationApi from '../api/notification.js';
import { adaptNotificationFromApi, adaptNotificationPageFromApi } from '../adapters/notification.js';

const USER_MESSAGE_TYPE = 2;

const MOCK_NOTIFICATIONS = [
	{
		id: 1,
		title: '订单状态更新',
		content: '您的旧衣回收订单已进入待确认阶段，请及时查看。',
		createTime: '2026-07-10 10:20:00',
		isRead: 0,
		url: '/pages/order/order'
	},
	{
		id: 2,
		title: '提现审核提醒',
		content: '您提交的提现申请正在审核中，预计 24 小时内完成处理。',
		createTime: '2026-07-09 18:30:00',
		isRead: 1,
		url: '/packageCommon/user/withdraw-records'
	}
];

const getCurrentUserId = () => Number(uni.getStorageSync('userId') || 0);

export const loadUserNotifications = async ({ page = 1, limit = 50 } = {}) => {
	if (config.USE_MOCK) {
		return adaptNotificationPageFromApi({
			records: MOCK_NOTIFICATIONS,
			total: MOCK_NOTIFICATIONS.length
		});
	}
	const userId = getCurrentUserId();
	if (!userId) {
		throw new Error('请先登录');
	}
	const data = await notificationApi.fetchUserNotificationPage({
		page,
		limit,
		type: USER_MESSAGE_TYPE,
		userId
	});
	return adaptNotificationPageFromApi(data);
};

export const loadUnreadNotificationCount = async () => {
	const userId = getCurrentUserId();
	if (!userId) {
		return 0;
	}
	const data = await loadUserNotifications({ page: 1, limit: 20 });
	return data.records.filter((item) => !item.isRead).length;
};

export const markUserNotificationRead = async (id) => {
	if (config.USE_MOCK) {
		const target = MOCK_NOTIFICATIONS.find((item) => `${item.id}` === `${id}`);
		if (target) {
			target.isRead = 1;
		}
		return adaptNotificationFromApi(target || {});
	}
	await notificationApi.markNotificationRead(id);
	const data = await notificationApi.fetchNotificationInfo(id);
	return adaptNotificationFromApi(data);
};
