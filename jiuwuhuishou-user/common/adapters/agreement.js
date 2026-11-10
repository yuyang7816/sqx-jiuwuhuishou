const DEFAULT_TITLE = '用户服务协议';

const DEFAULT_SECTIONS = [
	{
		title: '1. 特别提示',
		text: '欢迎您使用"旧物回收"环保回收平台！在您注册或开始使用本服务前，请您务必仔细阅读并充分理解《旧物回收用户服务协议》（以下简称"本协议"），特别是免责或者限制责任的条款、法律适用和争议解决条款。',
		bullets: []
	},
	{
		title: '2. 服务内容',
		text: '"旧物回收"是一个专注于生活废品上门回收及低碳生活方式引导的互联网平台。我们提供的服务包括但不限于：废旧物资预约上门回收、碳减排数据统计、环保积分（能量值）兑换服务以及相关的社区互动功能。',
		bullets: [
			'回收品类以APP公示的最新列表为准。',
			'回收价格根据市场波动实时调整，以订单生成时的公示价格为准。',
			'所有的回收交易需确保来源合法，严禁交付违法违禁品。'
		]
	},
	{
		title: '3. 隐私与数据保护',
		text: '我们非常重视用户的个人隐私。我们将按照《隐私政策》的规定收集、使用和保护您的个人信息，包括但不限于您的手机号、联系地址及交易记录。您的地址信息仅用于安排上门回收员提供服务。',
		bullets: []
	},
	{
		title: '4. 用户的权利与义务',
		text: '用户应确保提交的回收物来源合法、信息真实，并配合平台及服务人员完成必要的身份、地址和物品校验工作。',
		bullets: []
	}
];

const DEFAULT_PRIVACY_TITLE = '隐私政策';

const DEFAULT_PRIVACY_SECTIONS = [
	{
		title: '1. 信息收集',
		text: '我们可能收集您的手机号、联系地址、交易与设备信息，用于提供回收预约、上门服务、结算打款及客服支持。',
		bullets: []
	},
	{
		title: '2. 信息使用',
		text: '您的个人信息仅用于提供服务、保障交易安全、改进产品体验，以及依法配合监管要求。',
		bullets: []
	},
	{
		title: '3. 信息保护',
		text: '我们采取合理的技术与管理措施保护您的数据安全。',
		bullets: []
	}
];

const parseJsonObject = (value) => {
	if (!value) {
		return null;
	}
	try {
		const parsed = JSON.parse(value);
		return parsed && typeof parsed === 'object' ? parsed : null;
	} catch (error) {
		return null;
	}
};

const escapeHtml = (text = '') =>
	String(text)
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;')
		.replace(/"/g, '&quot;');

const sectionsToHtml = (sections = []) => {
	if (!Array.isArray(sections) || !sections.length) {
		return '';
	}
	return sections
		.map((section) => {
			const parts = [];
			if (section?.title) {
				parts.push(`<h3>${escapeHtml(section.title)}</h3>`);
			}
			if (section?.text) {
				parts.push(`<p>${escapeHtml(section.text)}</p>`);
			}
			if (Array.isArray(section?.bullets) && section.bullets.length) {
				const items = section.bullets
					.filter(Boolean)
					.map((item) => `<li>${escapeHtml(item)}</li>`)
					.join('');
				if (items) {
					parts.push(`<ul>${items}</ul>`);
				}
			}
			return parts.join('');
		})
		.filter(Boolean)
		.join('');
};

const normalizeSections = (sections, fallback = DEFAULT_SECTIONS) => {
	if (!Array.isArray(sections) || !sections.length) {
		return fallback;
	}
	return sections.map((section) => ({
		title: section?.title || '',
		text: section?.text || '',
		bullets: Array.isArray(section?.bullets)
			? section.bullets.filter((item) => item)
			: []
	})).filter((section) => section.title || section.text || section.bullets.length);
};

const buildAgreementView = (value, { defaultTitle, defaultSections }) => {
	const parsed = parseJsonObject(value);
	if (!parsed) {
		const html = String(value || '').trim().startsWith('<')
			? String(value)
			: sectionsToHtml(defaultSections);
		return {
			title: defaultTitle,
			html: html || sectionsToHtml(defaultSections),
			sections: defaultSections
		};
	}

	const title = parsed.title || defaultTitle;
	if (typeof parsed.html === 'string' && parsed.html.trim()) {
		return {
			title,
			html: parsed.html,
			sections: Array.isArray(parsed.sections)
				? normalizeSections(parsed.sections, defaultSections)
				: defaultSections
		};
	}

	const sections = normalizeSections(parsed.sections, defaultSections);
	const finalSections = sections.length ? sections : defaultSections;
	return {
		title,
		html: sectionsToHtml(finalSections),
		sections: finalSections
	};
};

export const adaptServiceAgreementFromConfig = (value) =>
	buildAgreementView(value, {
		defaultTitle: DEFAULT_TITLE,
		defaultSections: DEFAULT_SECTIONS
	});

export const adaptPrivacyAgreementFromConfig = (value) =>
	buildAgreementView(value, {
		defaultTitle: DEFAULT_PRIVACY_TITLE,
		defaultSections: DEFAULT_PRIVACY_SECTIONS
	});

export { DEFAULT_TITLE, DEFAULT_SECTIONS, DEFAULT_PRIVACY_TITLE, DEFAULT_PRIVACY_SECTIONS };

export const DEFAULT_ABOUT_TITLE = '关于我们';

export const DEFAULT_ABOUT_SECTIONS = [
	{
		title: '平台简介',
		text: '「旧物回收」是一个专注于生活废品上门回收及低碳生活方式引导的互联网平台，致力于让每一次正确的废品回收都为地球降温。',
		bullets: []
	},
	{
		title: '我们的服务',
		text: '平台为用户提供便捷、透明的旧物回收服务，覆盖旧衣、家电、数码、书籍等多种品类。',
		bullets: [
			'线上预约，上门回收，流程简单透明。',
			'支持订单跟踪、结算确认与打款查询。',
			'通过环保积分和推荐奖励，鼓励用户参与低碳生活。'
		]
	},
	{
		title: '联系我们',
		text: '如您在使用过程中有任何问题，可通过平台在线客服、帮助中心或订单详情页发起咨询，我们会尽快为您处理。',
		bullets: []
	}
];

export const adaptAboutUsFromConfig = (value) =>
	buildAgreementView(value, {
		defaultTitle: DEFAULT_ABOUT_TITLE,
		defaultSections: DEFAULT_ABOUT_SECTIONS
	});
