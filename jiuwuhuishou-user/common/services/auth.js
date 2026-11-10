import config from '../config.js';
import * as authApi from '../api/auth.js';
import { adaptUserFromApi, toUserUpdatePayload } from '../adapters/user.js';
import { clearInvitationCode, getInvitationCode } from './inviteCode.js';
import { resolveClientPlatform } from '../utils/platform.js';
import {
	loginMockUser as mockLogin,
	saveMockUserProfile,
	getMockUserProfile
} from '../mock-data.js';

export const persistSession = (loginVo, extra = {}) => {
	if (!loginVo?.token) {
		return;
	}
	uni.setStorageSync('token', loginVo.token);
	uni.setStorageSync('userId', loginVo.user?.userId);
	uni.setStorageSync('phone', loginVo.user?.phone || loginVo.user?.phoneNo);
	if (extra.openId) {
		uni.setStorageSync('openId', extra.openId);
	}
};

const SESSION_KEYS = ['token', 'userId', 'phone', 'openId', 'userType'];

export const clearSession = () => {
	SESSION_KEYS.forEach((key) => {
		try {
			uni.removeStorageSync(key);
		} catch (error) {
			// ignore
		}
	});
};

export const logout = async () => {
	if (!config.USE_MOCK) {
		try {
			await authApi.logout();
		} catch (error) {
			// 接口失败也继续清理本地登录态，避免用户无法退出
		}
	}
	clearSession();
};

export const login = async (mobile, password) => {
	if (config.USE_MOCK) {
		mockLogin({ mobile, userType: 'user' });
		return { token: 'mock-token', user: { userId: 1, phone: mobile } };
	}
	const data = await authApi.phonePasswordLogin(mobile, password);
	persistSession(data);
	return data;
};

const requestWeixinLoginCode = () => new Promise((resolve, reject) => {
	uni.login({
		provider: 'weixin',
		success: (res) => {
			if (res.code) {
				resolve(res.code);
				return;
			}
			reject(new Error('微信登录失败，请重试'));
		},
		fail: () => reject(new Error('微信登录失败，请重试'))
	});
});

export const weixinQuickLogin = async () => {
	if (config.USE_MOCK) {
		mockLogin({ mobile: '13800000000', userType: 'user' });
		return { token: 'mock-token', user: { userId: 1, phone: '13800000000' } };
	}
	const code = await requestWeixinLoginCode();
	const wxSession = await authApi.wxLogin(code);
	if (!wxSession?.openid) {
		throw new Error(wxSession?.errmsg || '微信授权失败');
	}
	uni.setStorageSync('openId', wxSession.openid);
	if (wxSession.bindPhone && wxSession.loginVO?.token) {
		persistSession(wxSession.loginVO, { openId: wxSession.openid });
		return wxSession.loginVO;
	}
	return {
		needPhoneBind: true,
		openid: wxSession.openid
	};
};

export const weixinBindPhoneLogin = async ({ openid, phoneCode }) => {
	if (config.USE_MOCK) {
		mockLogin({ mobile: '13800000000', userType: 'user' });
		return { token: 'mock-token', user: { userId: 1, phone: '13800000000' } };
	}
	if (!phoneCode) {
		throw new Error('需要授权手机号才能完成登录');
	}
	const currentOpenId = openid || uni.getStorageSync('openId');
	if (!currentOpenId) {
		throw new Error('微信登录状态已失效，请重试');
	}
	const phoneInfo = await authApi.getWxUserPhoneNumber(phoneCode, currentOpenId);
	const phone = phoneInfo?.purePhoneNumber || phoneInfo?.phoneNumber;
	if (!phone) {
		throw new Error('获取手机号失败，请重试');
	}
	const payload = {
		openId: currentOpenId,
		phone,
		username: `用户${phone.slice(-4)}`,
		avatar: '',
		platform: resolveClientPlatform()
	};
	const inviteCode = getInvitationCode();
	if (inviteCode) {
		payload.superiorInvitationCode = inviteCode;
	}
	const data = await authApi.wxJsRegister(payload);
	clearInvitationCode();
	persistSession(data, { openId: currentOpenId });
	return data;
};

export const register = async ({ mobile, code, password, superiorInvitationCode }) => {
	if (config.USE_MOCK) {
		saveMockUserProfile({ mobile, nickname: mobile });
		mockLogin({ mobile, userType: 'user' });
		clearInvitationCode();
		return { token: 'mock-token', user: { userId: 1, phone: mobile } };
	}
	const payload = {
		phoneNo: mobile,
		code,
		password,
		platform: resolveClientPlatform()
	};
	const inviteCode = `${superiorInvitationCode || ''}`.trim();
	if (inviteCode) {
		payload.superiorInvitationCode = inviteCode;
	}
	const data = await authApi.phoneRegister(payload);
	clearInvitationCode();
	persistSession(data);
	return data;
};

export const sendVerifyCode = async (mobile, bizCode = 'register') => {
	if (config.USE_MOCK) {
		return Promise.resolve();
	}
	return authApi.sendPhoneCode(mobile, bizCode);
};

export const resetPassword = async ({ mobile, code, password }) => {
	if (config.USE_MOCK) {
		return Promise.resolve();
	}
	return authApi.resetPassword({ phone: mobile, code, password });
};

export const changePassword = async ({ oldPassword, newPassword }) => {
	if (config.USE_MOCK) {
		return Promise.resolve();
	}
	return authApi.changePassword({ oldPassword, newPassword });
};

export const fetchProfile = async () => {
	if (config.USE_MOCK) {
		return getMockUserProfile();
	}
	const user = await authApi.fetchUserInfo();
	return adaptUserFromApi(user);
};

export const saveProfile = async (form) => {
	if (config.USE_MOCK) {
		saveMockUserProfile(form);
		return;
	}
	const userId = uni.getStorageSync('userId');
	await authApi.updateUserInfo(toUserUpdatePayload(form, userId));
};

export const ensureLoggedIn = () => {
	const token = uni.getStorageSync('token');
	if (!token && !config.USE_MOCK) {
		uni.showToast({ title: '请先登录', icon: 'none' });
		setTimeout(() => {
			uni.navigateTo({ url: '/packageCommon/user/login' });
		}, 400);
		return false;
	}
	return true;
};
