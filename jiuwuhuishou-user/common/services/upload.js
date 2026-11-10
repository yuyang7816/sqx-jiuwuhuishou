import config from '../config.js';
import * as fileApi from '../api/file.js';

export const uploadImage = async (filePath) => {
	if (!filePath) {
		throw new Error('请选择图片');
	}
	if (config.USE_MOCK) {
		return filePath;
	}
	const payload = await fileApi.uploadImageFile(filePath);
	const url = fileApi.resolveUploadedUrl(payload);
	if (!url) {
		throw new Error('上传成功但未返回图片地址');
	}
	return url;
};

const chooseAndUpload = (sourceType, { onPicked } = {}) => new Promise((resolve, reject) => {
	uni.chooseImage({
		count: 1,
		sizeType: ['compressed'],
		sourceType,
		success: async (res) => {
			const tempPath = res.tempFilePaths?.[0];
			if (!tempPath) {
				reject(new Error('未选择图片'));
				return;
			}
			try {
				if (typeof onPicked === 'function') {
					onPicked(tempPath);
				}
				uni.showLoading({ title: '上传中', mask: true });
				const url = await uploadImage(tempPath);
				resolve(url);
			} catch (error) {
				reject(error);
			} finally {
				uni.hideLoading();
			}
		},
		fail: (error) => {
			reject(error);
		}
	});
});

export const chooseAndUploadImage = (options = {}) => chooseAndUpload(['album', 'camera'], options);

export const chooseAlbumAndUploadImage = (options = {}) => chooseAndUpload(['album'], options);
