const DEFAULT_TITLE = '全民减碳合伙人';

const DEFAULT_INTRO = '邀请邻里共同守护地球，每回收一单，您都能获得由平台提供的环保激励，简单透明，实时结算。';

const DEFAULT_LEVELS = [
	{
		rate: '10%',
		title: '一级分佣',
		desc: '直接邀请的好友产生回收订单',
		icon: 'account',
		color: '#c8102e'
	},
	{
		rate: '5%',
		title: '二级分佣',
		desc: '好友邀请的二级用户产生订单',
		icon: 'man-add',
		color: '#8a6400'
	}
];

const DEFAULT_EXAMPLE = '例：好友 A 回收获得 100 碳元，您可得 10 碳元；A 邀请的好友 B 回收获得 100 碳元，您可得 5 碳元。';

const DEFAULT_SETTLES = [
	{ title: '提现门槛', desc: '钱包累计余额满 ￥10.00 即可申请提现，支持提现至微信零钱或支付宝。' },
	{ title: '到账时间', desc: '提交申请后，系统将在 24 小时内完成审核并打款。法定节假日可能会有 1-3 个工作日延迟。' },
	{ title: '佣金发放', desc: '佣金将在回收员上门取件、订单状态变为“已完成”后，系统自动实时发放至合伙人钱包。' }
];

const DEFAULT_FAQS = [
	{ q: '1. 异常行为处理', a: '若存在刷单、虚假邀约等行为，平台有权冻结相关奖励。' },
	{ q: '2. 佣金有效期说明', a: '未提现佣金长期有效，以平台最新政策为准。' },
	{ q: '3. 税务与代缴', a: '达到相关纳税标准时，将按平台规则履行代扣代缴义务。' }
];

const DEFAULT_FOOTER_ART = {
	label: '钱包收益',
	desc: '如有疑问，请咨询在线客服'
};

const escapeHtml = (text = '') =>
	String(text)
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;')
		.replace(/"/g, '&quot;');

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

const legacyRulesToHtml = (parsed = {}) => {
	const parts = [];
	if (parsed.intro) {
		parts.push(`<p>${escapeHtml(parsed.intro)}</p>`);
	}
	if (Array.isArray(parsed.levels) && parsed.levels.length) {
		parts.push('<h3>分佣比例说明</h3>');
		parsed.levels.forEach((item) => {
			const rate = item?.rate ? `（${escapeHtml(item.rate)}）` : '';
			if (item?.title) {
				parts.push(`<h4>${escapeHtml(item.title)}${rate}</h4>`);
			}
			if (item?.desc) {
				parts.push(`<p>${escapeHtml(item.desc)}</p>`);
			}
		});
	}
	if (parsed.example) {
		parts.push(`<p>${escapeHtml(parsed.example)}</p>`);
	}
	if (Array.isArray(parsed.settles) && parsed.settles.length) {
		parts.push('<h3>提现与结算</h3>');
		parsed.settles.forEach((item) => {
			if (item?.title) {
				parts.push(`<h4>${escapeHtml(item.title)}</h4>`);
			}
			if (item?.desc) {
				parts.push(`<p>${escapeHtml(item.desc)}</p>`);
			}
		});
	}
	if (Array.isArray(parsed.faqs) && parsed.faqs.length) {
		parts.push('<h3>合规条款</h3>');
		parsed.faqs.forEach((item) => {
			if (item?.q) {
				parts.push(`<h4>${escapeHtml(item.q)}</h4>`);
			}
			if (item?.a) {
				parts.push(`<p>${escapeHtml(item.a)}</p>`);
			}
		});
	}
	if (parsed.footerArt?.desc) {
		parts.push(`<p>${escapeHtml(parsed.footerArt.desc)}</p>`);
	}
	return parts.join('');
};

export const buildDefaultReferralRulesHtml = () =>
	legacyRulesToHtml({
		intro: DEFAULT_INTRO,
		levels: DEFAULT_LEVELS,
		example: DEFAULT_EXAMPLE,
		settles: DEFAULT_SETTLES,
		faqs: DEFAULT_FAQS,
		footerArt: DEFAULT_FOOTER_ART
	});

export const adaptReferralRulesFromConfig = (value) => {
	const text = String(value || '').trim();
	if (!text) {
		return {
			title: DEFAULT_TITLE,
			html: buildDefaultReferralRulesHtml()
		};
	}

	if (text.startsWith('<')) {
		return {
			title: DEFAULT_TITLE,
			html: text
		};
	}

	const parsed = parseJsonObject(text);
	if (!parsed) {
		return {
			title: DEFAULT_TITLE,
			html: `<p>${escapeHtml(text)}</p>`
		};
	}

	const title = parsed.title || DEFAULT_TITLE;
	if (typeof parsed.html === 'string' && parsed.html.trim()) {
		return {
			title,
			html: parsed.html
		};
	}

	const html = legacyRulesToHtml(parsed);
	return {
		title,
		html: html || buildDefaultReferralRulesHtml()
	};
};

export {
	DEFAULT_TITLE,
	DEFAULT_INTRO,
	DEFAULT_LEVELS,
	DEFAULT_EXAMPLE,
	DEFAULT_SETTLES,
	DEFAULT_FAQS,
	DEFAULT_FOOTER_ART
};
