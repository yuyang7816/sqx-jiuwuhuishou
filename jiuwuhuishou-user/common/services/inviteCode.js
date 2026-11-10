const STORAGE_KEY = 'superiorInvitationCode';

export const extractInvitationCode = (options = {}) => {
	let code = options?.invitationCode || options?.inviteCode || '';
	if (!code && options?.scene) {
		try {
			code = decodeURIComponent(options.scene);
		} catch (error) {
			code = options.scene;
		}
	}
	return `${code}`.trim();
};

const extractInvitationCodeFromLocation = () => {
	// #ifdef H5
	try {
		if (typeof window === 'undefined' || !window.location) {
			return '';
		}
		const search = new URLSearchParams(window.location.search || '');
		const hash = `${window.location.hash || ''}`;
		const hashQuery = hash.includes('?') ? hash.slice(hash.indexOf('?') + 1) : '';
		const hashParams = new URLSearchParams(hashQuery);
		return `${
			search.get('invitationCode')
			|| search.get('inviteCode')
			|| hashParams.get('invitationCode')
			|| hashParams.get('inviteCode')
			|| ''
		}`.trim();
	} catch (error) {
		return '';
	}
	// #endif
	// #ifndef H5
	return '';
	// #endif
};

export const saveInvitationCode = (code) => {
	const normalized = `${code || ''}`.trim();
	if (!normalized) {
		return;
	}
	uni.setStorageSync(STORAGE_KEY, normalized);
};

export const getInvitationCode = () => `${uni.getStorageSync(STORAGE_KEY) || ''}`.trim();

export const clearInvitationCode = () => {
	try {
		uni.removeStorageSync(STORAGE_KEY);
	} catch (error) {
		// ignore
	}
};

export const captureInvitationCode = (options = {}) => {
	const code = extractInvitationCode(options) || extractInvitationCodeFromLocation();
	if (code) {
		saveInvitationCode(code);
	}
	return code || getInvitationCode();
};
