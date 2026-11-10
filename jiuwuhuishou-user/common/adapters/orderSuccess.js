const DEFAULT_STATUS_DESC = '订单已创建成功，后续会进入上门、质检、确认、打款完整闭环';
const DEFAULT_PICKUP_TITLE = '上门取件中';
const DEFAULT_PICKUP_DESC = '服务人员正在赶往您的地址';

const MAIL_STATUS_DESC = '订单已创建成功，请按预约时间准备好包裹，顺丰将上门取件';
const MAIL_PICKUP_TITLE = '顺丰寄件取件中';
const MAIL_PICKUP_DESC = '待顺丰小哥上门取件，寄往平台质检仓';
const MAIL_WAIT_DESC = '顺丰签收后会进入质检、确认、打款流程';

const DEFAULT_NEXT_ACTIONS = [
	{ title: '改约', desc: '时间冲突时尽量保留订单', icon: 'calendar', url: '/packageCommon/order/reschedule' },
	{ title: '异常反馈', desc: '迟到、拒收、联系不上都可提交', icon: 'warning', url: '/packageCommon/order/exception' },
	{ title: '价格确认', desc: '实物检测后确认成交结果', icon: 'coupon', url: '/packageCommon/order/settlement-confirm' },
	{ title: '打款进度', desc: '查看到账状态与收款信息', icon: 'coupon', url: '/packageCommon/order/payout' }
];

const DEFAULT_COURIER = {
	name: '王师傅',
	mobile: '13800138001',
	mobileMask: '138****8001'
};

const parseJsonArray = (value, fallback = []) => {
	if (!value) {
		return fallback;
	}
	try {
		const parsed = JSON.parse(value);
		return Array.isArray(parsed) ? parsed : fallback;
	} catch (error) {
		return fallback;
	}
};

export const adaptOrderSuccessConfig = ({
	statusDesc = DEFAULT_STATUS_DESC,
	pickupTitle = DEFAULT_PICKUP_TITLE,
	pickupDesc = DEFAULT_PICKUP_DESC,
	nextActionsJson = JSON.stringify(DEFAULT_NEXT_ACTIONS),
	fulfillType = 'visit'
} = {}) => {
	const isMail = fulfillType === 'mail';
	return {
		fulfillType: isMail ? 'mail' : 'visit',
		statusDesc: isMail ? (statusDesc && statusDesc !== DEFAULT_STATUS_DESC ? statusDesc : MAIL_STATUS_DESC) : (statusDesc || DEFAULT_STATUS_DESC),
		pickupTitle: isMail ? MAIL_PICKUP_TITLE : (pickupTitle || DEFAULT_PICKUP_TITLE),
		pickupDesc: isMail ? MAIL_PICKUP_DESC : (pickupDesc || DEFAULT_PICKUP_DESC),
		waitDesc: isMail ? MAIL_WAIT_DESC : '完成取件后会继续进入后续流程',
		contactLabel: isMail ? '点击联系顺丰客服' : '点击联系快递员',
		nextActions: parseJsonArray(nextActionsJson, DEFAULT_NEXT_ACTIONS).map((item) => ({
			title: item.title || '',
			desc: item.desc || '',
			icon: item.icon || 'arrow-right',
			url: item.url || ''
		})).filter((item) => item.title && item.url)
	};
};

export const adaptCourierFromApi = (courier) => ({
	name: courier?.name || DEFAULT_COURIER.name,
	mobile: courier?.mobile || DEFAULT_COURIER.mobile,
	mobileMask: courier?.mobileMask || DEFAULT_COURIER.mobileMask
});

export {
	DEFAULT_STATUS_DESC,
	DEFAULT_PICKUP_TITLE,
	DEFAULT_PICKUP_DESC,
	MAIL_STATUS_DESC,
	MAIL_PICKUP_TITLE,
	MAIL_PICKUP_DESC,
	DEFAULT_NEXT_ACTIONS,
	DEFAULT_COURIER
};
