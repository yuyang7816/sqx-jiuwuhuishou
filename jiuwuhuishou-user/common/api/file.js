import config from '../config.js';
import { uploadFile } from '../httpRequest.js';
import { assertSuccess } from './http.js';

export const uploadImageFile = async (filePath) => assertSuccess(await uploadFile('/file/upload', filePath));

export const resolveUploadedUrl = (payload = {}) => {
	let url = '';
	if (typeof payload === 'string') {
		url = payload;
	} else if (payload?.path) {
		url = payload.path;
	} else if (payload?.url) {
		url = payload.url;
	} else if (payload?.relativePath) {
		url = payload.relativePath;
	}
	url = `${url || ''}`.trim();
	if (!url) {
		return '';
	}
	if (/^(https?:|blob:|data:|wxfile:|file:)/i.test(url)) {
		return url;
	}
	const relative = url.startsWith('/') ? url : `/${url}`;
	return `${config.APIHOST}${relative}`;
};
