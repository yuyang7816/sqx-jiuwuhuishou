export const DEFAULT_HELP_ARTICLES = [
	{
		id: 1,
		title: '如何预约旧衣、家电和数码回收？',
		desc: '介绍从首页进入品类、填写地址、选择时间到提交订单的完整流程。',
		tag: '下单指南',
		updatedAt: '2026-07-01'
	},
	{
		id: 2,
		title: '订单取消、改约和异常反馈怎么操作？',
		desc: '说明订单页内取消订单、重新预约上门时间以及提交异常工单的入口。',
		tag: '订单问题',
		updatedAt: '2026-07-01'
	},
	{
		id: 3,
		title: '回收价格、质检结算和打款规则说明',
		desc: '说明预估价与成交价差异、质检确认流程以及打款到账时效。',
		tag: '价格结算',
		updatedAt: '2026-07-01'
	},
	{
		id: 4,
		title: '邀请好友、福利任务和提现常见问题',
		desc: '整理邀请分佣、签到奖励、任务领取和申请提现的常见说明。',
		tag: '福利收益',
		updatedAt: '2026-07-01'
	},
	{
		id: 5,
		title: '账号登录、修改密码和资料设置问题',
		desc: '介绍登录注册、忘记密码、修改密码以及编辑个人资料的方法。',
		tag: '账号安全',
		updatedAt: '2026-07-01'
	}
];

export const DEFAULT_HELP_DETAIL = {
	tag: '下单指南',
	title: '如何预约旧衣、家电和数码回收？',
	updatedAt: '2026-07-01',
	sections: [
		{ title: '内容简介', content: '介绍从首页进入品类、填写地址、选择时间到提交订单的完整流程。' },
		{ title: '1. 进入回收品类', content: '在首页选择旧衣回收、家电回收或数码回收，进入对应业务页面。' },
		{ title: '2. 填写预约信息', content: '按页面提示选择地址、预约时间、服务区域或机型信息，确认回收规则后提交订单。' },
		{ title: '3. 查看订单进度', content: '提交成功后可在订单页面查看履约进度，并在需要时进行改约、异常反馈和价格确认。' }
	]
};

const formatDate = (value) => {
	if (!value) {
		return '';
	}
	return `${value}`.slice(0, 10);
};

const stripHtml = (value) => `${value || ''}`
	.replace(/<br\s*\/?>/gi, '\n')
	.replace(/<\/p>/gi, '\n')
	.replace(/<[^>]+>/g, '')
	.replace(/&nbsp;/g, ' ')
	.replace(/\s+/g, ' ')
	.trim();

const extractParagraphs = (html) => {
	const source = `${html || ''}`;
	const matches = source.match(/<p[^>]*>[\s\S]*?<\/p>/gi);
	if (!matches?.length) {
		const text = stripHtml(source);
		return text ? [text] : [];
	}
	return matches.map((item) => stripHtml(item)).filter(Boolean);
};

const buildSections = (paragraphs) => paragraphs.map((text, index) => {
	const numbered = text.match(/^(\d+\.\s*)([\s\S]+)$/);
	if (numbered) {
		const body = numbered[2].trim();
		const titleMatch = body.match(/^([^。！？.]+[。！？.]?)/);
		const title = titleMatch
			? `${numbered[1]}${titleMatch[1]}`.trim()
			: `${numbered[1]}步骤说明`;
		return {
			title,
			content: body
		};
	}
	return {
		title: index === 0 ? '内容简介' : `说明 ${index}`,
		content: text
	};
});

const parseHelpContentToSections = (content) => {
	const paragraphs = extractParagraphs(content);
	if (!paragraphs.length) {
		return [];
	}
	return buildSections(paragraphs);
};

export const adaptHelpArticleFromApi = (item = {}) => ({
	id: item.id,
	title: item.title || '',
	desc: item.summary || '',
	tag: item.tag || '',
	updatedAt: formatDate(item.updateTime)
});

export const adaptHelpArticlePageFromApi = (page = {}) => ({
	records: (page.records || []).map(adaptHelpArticleFromApi),
	total: page.total || 0
});

export const adaptHelpArticleDetailFromApi = (item = {}) => ({
	id: item.id,
	tag: item.tag || '',
	title: item.title || '',
	updatedAt: formatDate(item.updateTime),
	sections: parseHelpContentToSections(item.content)
});
