import config from './config';

// 提取重复的401处理逻辑
const handle401 = () => {
	uni.removeStorageSync("token");
	uni.removeStorageSync("userId");
	uni.removeStorageSync("phone");
	uni.removeStorageSync("openId");
	uni.showToast({
		title: '用户信息失效，请重新登录！',
		icon: 'none'
	});
};

// 提取通用的请求逻辑
const sendRequest = (url, data, method, header, hasToken = true) => {
	return new Promise((succ, error) => {
		const token = hasToken ? uni.getStorageSync("token") : null;
		uni.request({
			url,
			data,
			method,
			header: {
				"content-type": header,
				...(hasToken && {
					token
				})
			},
			success: (result) => {
				const code = result.data?.code;
				if ((code === 401 || code === 50002) && token) {
					handle401();
				}
				succ(result.data);
			},
			fail: (e) => {
				error(e);
			}
		});
	});
};
// POST请求
export const post = (url, data, header = "application/x-www-form-urlencoded") => {
	const apiHost = config.APIHOST;
	const fullUrl = apiHost + url;
	return sendRequest(fullUrl, data, "POST", header);
};

// POSTT请求
export const postT = (url, data, header = "application/x-www-form-urlencoded") => {
	const apiHost1 = config.APIHOST;
	const fullUrl = apiHost1 + url;
	return sendRequest(fullUrl, data, "POST", header);
};

// POSTJson请求
export const postJson = (url, data, header = "application/json") => {
	const apiHost1 = config.APIHOST;
	const fullUrl = apiHost1 + url;
	return sendRequest(fullUrl, data, "POST", header);
};

// GETT请求
export const getT = (url, data, header = "application/x-www-form-urlencoded") => {
	const apiHost1 = config.APIHOST;
	const fullUrl = apiHost1 + url;
	return sendRequest(fullUrl, data, "GET", header);
};

// GET请求
export const get = (url, data, header = "application/x-www-form-urlencoded") => {
	const apiHost = config.APIHOST;
	const fullUrl = apiHost + url;
	return sendRequest(fullUrl, data, "GET", header);
};

export const postJsonNoAuth = (url, data) => {
	return sendRequest(config.APIHOST + url, data, "POST", "application/json", false);
};

export const getNoAuth = (url, data) => {
	return sendRequest(config.APIHOST + url, data, "GET", "application/x-www-form-urlencoded", false);
};

export const uploadFile = (url, filePath, name = 'file', hasToken = true) => {
	return new Promise((resolve, reject) => {
		const token = hasToken ? uni.getStorageSync('token') : null;
		uni.uploadFile({
			url: config.APIHOST + url,
			filePath,
			name,
			header: hasToken && token ? { token } : {},
			success: (result) => {
				try {
					const data = typeof result.data === 'string' ? JSON.parse(result.data) : result.data;
					const code = data?.code;
					if ((code === 401 || code === 50002) && token) {
						handle401();
					}
					resolve(data);
				} catch (error) {
					reject(new Error('上传响应解析失败'));
				}
			},
			fail: (error) => {
				reject(error);
			}
		});
	});
};