const tagIconMap = {
	'公司': 'map',
	'家': 'home'
};

export const adaptAddressFromApi = (item) => {
	const region = [item.province, item.city, item.district].filter(Boolean);
	const detail = [...region, item.street, item.detailAddress].filter(Boolean).join(' ');
	return {
		id: item.id,
		name: item.contactName,
		label: item.tag,
		tag: item.tag,
		mobile: item.contactMobile,
		phone: item.contactMobile,
		province: item.province,
		city: item.city,
		district: item.district,
		street: item.street,
		region,
		address: item.detailAddress,
		detail,
		default: item.isDefault === 1,
		isDefault: item.isDefault === 1,
		icon: tagIconMap[item.tag] || 'home'
	};
};

export const formatAddressCard = (item) => ({
	phoneText: item.contactMobile || item.mobile || item.phone || '',
	detail: item.detail || [item.province, item.city, item.district, item.address].filter(Boolean).join(' '),
	labelText: item.label || item.tag || item.name,
	userText: `${item.name}${item.contactMobile || item.mobile || item.phone ? ` ${item.contactMobile || item.mobile || item.phone}` : ''}`
});

export const toAddressSavePayload = ({ contact, mobile, region, address, tag, isDefault }) => ({
	contactName: contact,
	contactMobile: mobile,
	province: region?.[0] || '',
	city: region?.[1] || '',
	district: region?.[2] || '',
	detailAddress: address,
	tag,
	isDefault: isDefault ? 1 : 0
});

export const toAddressUpdatePayload = (id, payload) => ({
	id: Number(id),
	...toAddressSavePayload(payload)
});

export const toBookingAddressOption = (item) => {
	const card = formatAddressCard(item);
	return {
		id: item.id,
		name: item.tag || item.label || '常用地址',
		address: card.detail,
		user: card.userText,
		default: item.default,
		icon: item.icon || 'home'
	};
};
