import config from '../config.js';

const sexToGender = (sex) => {
	if (sex === 1) return '男';
	if (sex === 2) return '女';
	return '保密';
};

const genderToSex = (gender) => {
	if (gender === '男') return 1;
	if (gender === '女') return 2;
	return undefined;
};

export const maskMobile = (mobile = '') => `${mobile}`.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2');

export const resolveAssetUrl = (url = '') => {
	if (!url) {
		return '';
	}
	if (/^https?:\/\//i.test(url) || url.startsWith('wxfile://') || url.startsWith('file://') || url.startsWith('blob:')) {
		return url;
	}
	if (url.startsWith('//')) {
		return `https:${url}`;
	}
	const path = url.startsWith('/') ? url : `/${url}`;
	return `${config.APIHOST}${path}`;
};

export const adaptUserFromApi = (user = {}) => {
	const mobile = user.phone || '';
	return {
		userId: user.userId,
		nickname: user.userName || '',
		mobile,
		maskedMobile: maskMobile(mobile),
		avatar: resolveAssetUrl(user.avatar || ''),
		gender: sexToGender(user.sex),
		sex: user.sex,
		age: user.age,
		userType: user.userType,
		isVip: user.isVip === 1,
		messageNotifyEnabled: user.messageNotifyEnabled !== 0 && user.messageNotifyEnabled !== false,
		invitationCode: user.invitationCode || '',
		platform: user.platform || '',
		status: user.status,
		createTime: user.createTime
	};
};

export const toUserUpdatePayload = (form, userId) => ({
	userId,
	userName: form.nickname,
	avatar: form.avatar,
	sex: genderToSex(form.gender),
	age: form.age || undefined
});
