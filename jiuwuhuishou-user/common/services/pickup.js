import config from '../config.js';
import * as pickupApi from '../api/pickup.js';
import {
	adaptPickupDetailFromApi,
	adaptPickupStatsFromApi,
	adaptPickupTaskFromApi,
	buildPickupTimeline
} from '../adapters/pickup.js';
import { finishMockPickup, getMockPickupById, getMockPickups, startMockPickup } from './order.js';

const DEFAULT_STATS = {
	pendingCount: 0,
	runningCount: 0,
	doneCount: 0
};

const buildMockStats = (list = []) => ({
	pendingCount: list.filter((item) => item.status === 'pending').length,
	runningCount: list.filter((item) => item.status === 'running').length,
	doneCount: list.filter((item) => item.status === 'done').length
});

const buildMockDetail = (item = {}) => {
	if (!item) {
		return null;
	}
	const estimateAmount = item.estimateAmount != null ? Number(item.estimateAmount) : 128;
	const actualAmount = item.actualAmount != null ? Number(item.actualAmount) : null;
	return adaptPickupDetailFromApi({
		...item,
		statusKey: item.status,
		estimateAmount,
		actualAmount,
		actualAmountRemark: item.actualAmountRemark || '',
		canStart: item.status === 'pending',
		canFinish: item.status === 'pending' || item.status === 'running',
		canEditActual: item.status === 'pending' || item.status === 'running' || item.status === 'done',
		canCancel: item.status === 'pending' || item.status === 'running' || item.status === 'done',
		logs: buildPickupTimeline([], item.status).map((log) => ({
			title: log.title,
			content: log.desc,
			createTime: log.time,
			active: log.active
		}))
	});
};

export const loadPickupListPageData = async (statusKey = 'all') => {
	if (config.USE_MOCK) {
		const list = getMockPickups();
		const filteredList = statusKey === 'all' ? list : list.filter((item) => item.status === statusKey);
		return {
			stats: buildMockStats(list),
			list: filteredList
		};
	}

	const [statsRes, pageRes] = await Promise.all([
		pickupApi.fetchPickupTaskStats(),
		pickupApi.fetchPickupTaskPage({
			page: 1,
			limit: 100,
			statusKey: statusKey === 'all' ? undefined : statusKey
		})
	]);

	return {
		stats: adaptPickupStatsFromApi(statsRes || DEFAULT_STATS),
		list: (pageRes?.records || []).map(adaptPickupTaskFromApi)
	};
};

export const loadPickupDetail = async (id) => {
	if (config.USE_MOCK) {
		return buildMockDetail(getMockPickupById(id));
	}
	const detail = await pickupApi.fetchPickupTaskInfo({ id });
	return adaptPickupDetailFromApi(detail);
};

export const startPickup = async (id) => {
	if (config.USE_MOCK) {
		return buildMockDetail(startMockPickup(id));
	}
	const detail = await pickupApi.startPickupTask({ id });
	return adaptPickupDetailFromApi(detail);
};

export const finishPickup = async (id, remark = '') => {
	if (config.USE_MOCK) {
		finishMockPickup(id);
		return {
			taskId: id,
			message: '已完成取件，用户订单已进入待确认'
		};
	}
	return pickupApi.finishPickupTask({ id, remark });
};

export const updatePickupActual = async (id, actualAmount, remark = '') => {
	const amount = Number(actualAmount);
	if (Number.isNaN(amount) || amount < 0) {
		throw new Error('请输入有效的实际打款金额');
	}
	if (config.USE_MOCK) {
		const current = getMockPickupById(id);
		if (!current) {
			throw new Error('取件任务不存在');
		}
		const previousAmount = current.actualAmount != null ? Number(current.actualAmount) : null;
		if (previousAmount != null && previousAmount !== amount && !`${remark || ''}`.trim()) {
			throw new Error('修改金额时请填写修改说明');
		}
		const pickups = getMockPickups().map((item) => (`${item.id}` === `${id}` ? {
			...item,
			actualAmount: amount,
			actualAmountRemark: `${remark || ''}`.trim() || item.actualAmountRemark || ''
		} : item));
		uni.setStorageSync('mock-pickups-v1', pickups);
		return buildMockDetail({
			...current,
			actualAmount: amount,
			actualAmountRemark: `${remark || ''}`.trim() || current.actualAmountRemark || ''
		});
	}
	const detail = await pickupApi.updatePickupActualAmount({ id, actualAmount: amount, remark });
	return adaptPickupDetailFromApi(detail);
};

export const cancelPickupOrder = async (id, reason = '') => {
	if (config.USE_MOCK) {
		const current = getMockPickupById(id);
		if (!current) {
			throw new Error('取件任务不存在');
		}
		const pickups = getMockPickups().map((item) => (`${item.id}` === `${id}` ? {
			...item,
			status: 'cancelled',
			statusText: '已取消',
			statusClass: 'is-done'
		} : item));
		uni.setStorageSync('mock-pickups-v1', pickups);
		return buildMockDetail({
			...current,
			status: 'cancelled',
			statusText: '已取消',
			statusClass: 'is-done',
			canStart: false,
			canFinish: false,
			canEditActual: false,
			canCancel: false
		});
	}
	const detail = await pickupApi.cancelPickupTask({ id, reason });
	return adaptPickupDetailFromApi(detail);
};
