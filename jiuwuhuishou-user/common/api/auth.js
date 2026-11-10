import { apiGet, apiGetNoAuth, apiPostJson, apiPostJsonNoAuth } from './http.js';

export const phonePasswordLogin = (phoneNo, password) => apiPostJsonNoAuth('/app/base/auth/phonePasswordLogin', { phoneNo, password });

export const phoneRegister = (payload) => apiPostJsonNoAuth('/app/base/auth/phoneRegister', payload);

export const resetPassword = (payload) => apiPostJsonNoAuth('/app/base/auth/resetPassword', payload);

export const sendPhoneCode = (phone, bizCode) => apiPostJsonNoAuth('/app/base/verifyCode/sendPhoneCode', { phone, bizCode });

export const fetchUserInfo = () => apiGet('/app/base/user/info');

export const updateUserInfo = (payload) => apiPostJson('/app/base/user/update', payload);

export const changePassword = (payload) => apiPostJson('/app/base/user/changePassword', payload);

export const logout = () => apiPostJson('/app/base/auth/logout', {});

export const wxLogin = (code) => apiGetNoAuth('/app/base/auth/wxLogin', { code });

export const wxJsRegister = (payload) => apiPostJsonNoAuth('/app/base/auth/wxJsRegister', payload);

export const getWxUserPhoneNumber = (code, openId) => apiGetNoAuth('/app/base/auth/getWxUserPhoneNumber', { code, openId });
