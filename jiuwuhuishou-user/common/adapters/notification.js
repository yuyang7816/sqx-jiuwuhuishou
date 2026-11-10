const formatTime = (value = '') => {
	if (!value) {
		return '';
	}
	return `${value}`.replace('T', ' ').slice(0, 16);
};

export const adaptNotificationFromApi = (item = {}) => ({
	id: item.id,
	title: item.title || '系统通知',
	content: item.content || '',
	time: formatTime(item.createTime),
	isRead: item.isRead === 1 || item.isRead === true,
	url: item.url || '',
	dataId: item.dataId || null
});

export const adaptNotificationPageFromApi = (page = {}) => ({
	records: (page.records || []).map(adaptNotificationFromApi),
	total: Number(page.total || 0),
	unreadCount: (page.records || []).filter((item) => !(item.isRead === 1 || item.isRead === true)).length
});
