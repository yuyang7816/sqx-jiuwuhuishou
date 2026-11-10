import { maskMobile } from './user.js';
import { formatAppointmentDisplay, formatDisplayTime, formatMoney } from '../utils/datetime.js';

const STATUS_CLASS_MAP = {
	pending: 'is-pending',
	running: 'is-running',
	done: 'is-done',
	cancelled: 'is-done'
};

const formatLogTime = (value = '') => {
	if (!value) {
		return '';
	}
	return `${value}`.replace('T', ' ').slice(0, 16);
};

const formatAmountValue = (value) => {
	if (value === null || value === undefined || value === '') {
		return null;
	}
	const num = Number(value);
	return Number.isNaN(num) ? null : num;
};

export const adaptPickupStatsFromApi = (stats = {}) => ({
	pendingCount: Number(stats.pendingCount || 0),
	runningCount: Number(stats.runningCount || 0),
	doneCount: Number(stats.doneCount || 0)
});

const buildBasePickup = (item = {}) => {
	const statusKey = item.statusKey || item.status || 'pending';
	const contactName = item.contactName || (item.contact || '').split(' ')[0] || '';
	const contactMobile = item.contactMobile || '';
	const estimateAmount = formatAmountValue(item.estimateAmount);
	const estimateMinAmount = formatAmountValue(item.estimateMinAmount);
	const estimateMaxAmount = formatAmountValue(item.estimateMaxAmount);
	const actualAmount = formatAmountValue(item.actualAmount);
	const actualAmountRemark = `${item.actualAmountRemark || ''}`.trim();
	let estimateAmountText = '-';
	if (estimateMinAmount != null || estimateMaxAmount != null) {
		const low = estimateMinAmount != null ? estimateMinAmount : estimateAmount;
		const high = estimateMaxAmount != null ? estimateMaxAmount : estimateAmount;
		if (low != null && high != null) {
			estimateAmountText = Number(low) === Number(high)
				? formatMoney(low)
				: `${formatMoney(low)} - ${formatMoney(high)}`;
		}
	} else if (estimateAmount != null) {
		estimateAmountText = formatMoney(estimateAmount);
	}

	const appointment = formatAppointmentDisplay(item.appointmentTime, item.appointmentEndTime)
		|| formatDisplayTime(item.appointmentTime)
		|| item.time
		|| '';

	return {
		id: item.id,
		orderId: item.orderId,
		orderNo: item.taskNo || item.orderNo || '',
		recycleOrderNo: item.recycleOrderNo || '',
		title: contactName ? `${contactName}取件任务` : (item.title || '取件任务'),
		address: item.addressText || item.address || '',
		contactName,
		contactMobile,
		contact: contactMobile ? `${contactName} ${maskMobile(contactMobile)}` : (item.contact || contactName),
		time: appointment,
		category: item.categoryName || item.category || '',
		weight: item.itemSummary || item.weight || '',
		estimateAmount,
		estimateMinAmount,
		estimateMaxAmount,
		actualAmount,
		actualAmountRemark,
		estimateAmountText,
		actualAmountText: actualAmount != null ? formatMoney(actualAmount) : '-',
		status: statusKey,
		statusText: item.statusText || '',
		statusClass: STATUS_CLASS_MAP[statusKey] || 'is-pending',
		canStart: Boolean(item.canStart),
		canFinish: Boolean(item.canFinish),
		canEditActual: Boolean(item.canEditActual),
		canCancel: Boolean(item.canCancel)
	};
};

export const adaptPickupTaskFromApi = (item = {}) => buildBasePickup(item);

export const adaptPickupDetailFromApi = (item = {}) => {
	const base = buildBasePickup(item);
	base.canStart = item.canStart ?? base.status === 'pending';
	base.canFinish = item.canFinish ?? (base.status === 'pending' || base.status === 'running');
	base.canEditActual = item.canEditActual ?? (base.status === 'pending' || base.status === 'running' || base.status === 'done');
	base.canCancel = item.canCancel ?? (base.status === 'pending' || base.status === 'running');
	base.recycleOrderNo = item.recycleOrderNo || '';
	base.timeline = buildPickupTimeline(item.logs || [], base.status);
	return base;
};

export const buildPickupTimeline = (logs = [], status = 'pending') => {
	if (logs.length) {
		return logs.map((item, index) => ({
			title: item.title || item.actionType || '流程节点',
			time: formatLogTime(item.createTime),
			desc: item.content || '',
			active: item.active ?? index === logs.length - 1
		}));
	}

	const fallback = [
		{ title: '任务已分配', time: '待同步', desc: '系统已将任务分配至当前员工', active: status === 'pending' },
		{ title: '正在前往上门地址', time: status === 'pending' ? '待开始' : '进行中', desc: '请按预约时间到达并联系用户', active: status === 'running' },
		{ title: status === 'done' ? '取件已完成' : '等待确认完成取件', time: status === 'done' ? '已完成' : '待完成', desc: '完成物品交接后点击底部按钮确认', active: status === 'done' }
	];
	return fallback;
};
