export const toBrandCooperationPayload = (form = {}) => ({
	brandName: `${form.brandName || ''}`.trim(),
	brandImage: `${form.brandImage || ''}`.trim(),
	contactName: `${form.contact || ''}`.trim(),
	mobile: `${form.mobile || ''}`.trim(),
	province: form.region?.[0] || '',
	city: form.region?.[1] || '',
	district: form.region?.[2] || '',
	address: `${form.address || ''}`.trim()
});

export const validateBrandCooperationForm = (form = {}) => {
	if (!`${form.brandName || ''}`.trim()) {
		return '请输入品牌名称';
	}
	if (!`${form.contact || ''}`.trim()) {
		return '请输入联系人';
	}
	if (!/^1\d{10}$/.test(`${form.mobile || ''}`.trim())) {
		return '请输入正确联系电话';
	}
	if (!form.region?.length || form.region.length < 3) {
		return '请选择完整省市区';
	}
	if (!`${form.address || ''}`.trim()) {
		return '请输入联系地址';
	}
	return '';
};
