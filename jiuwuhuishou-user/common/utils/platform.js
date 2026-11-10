/**
 * 客户端注册/登录渠道标识（写入用户 platform 字段）
 * H5 → H5；小程序 → mp；App → app
 */
export const resolveClientPlatform = () => {
	// #ifdef APP-PLUS
	return 'app';
	// #endif
	// #ifdef MP
	return 'mp';
	// #endif
	// #ifdef H5
	return 'H5';
	// #endif
	try {
		const info = uni.getSystemInfoSync?.() || {};
		const uniPlatform = `${info.uniPlatform || ''}`.toLowerCase();
		if (uniPlatform.startsWith('mp') || uniPlatform.includes('weixin') || uniPlatform.includes('alipay')) {
			return 'mp';
		}
		if (uniPlatform === 'app' || uniPlatform.startsWith('app-')) {
			return 'app';
		}
		if (uniPlatform === 'web' || uniPlatform === 'h5') {
			return 'H5';
		}
	} catch (error) {
		// ignore
	}
	return 'H5';
};
