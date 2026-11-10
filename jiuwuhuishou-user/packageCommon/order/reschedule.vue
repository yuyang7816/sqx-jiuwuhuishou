<template>
	<view class="app-page order-reschedule-page">
		<top-nav title="改约处理"></top-nav>
		<scroll-view scroll-y class="page-body compact order-reschedule-body">
			<view class="card hero-soft">
				<view style="font-size: 40rpx; font-weight: 800;">修改预约，尽量不让订单流失</view>
				<view class="muted" style="margin-top: 14rpx;">回收员未上门前支持改约，若已接单则会同步给履约侧做二次确认。</view>
				<view class="brand-pill" style="margin-top: 18rpx;">当前订单：{{ orderTitle || '回收订单' }}</view>
			</view>

			<view class="section-title"><text>改约原因</text></view>
			<view class="card">
				<view class="reason-grid">
					<view v-for="item in reasons" :key="item" class="reason-chip" :class="{ 'is-active': reason === item }" @click="reason = item">{{ item }}</view>
				</view>
			</view>

			<view class="section-title"><text>新预约时间</text></view>
			<view class="card">
				<view class="time-dates">
					<view v-for="item in dateList" :key="item.label" class="time-date" :class="{ 'is-active': selectedDate === item.label }" @click="changeAppointmentDate(item.label)">
						<view class="time-date__label">{{ item.label }}</view>
						<view class="time-date__day">{{ item.day }}</view>
					</view>
				</view>
				<view class="feature-list" style="margin-top: 20rpx;">
					<view v-for="item in slots" :key="item.time" class="feature-row slot-row" :class="{ 'is-active': selectedSlot === item.time, 'is-disabled': item.disabled || item.full }" @click="pickSlot(item)">
						<view>
							<view style="font-size: 30rpx; font-weight: 700;">{{ item.time }}</view>
							<view class="muted" style="margin-top: 8rpx;">{{ item.tip || (item.full ? '该时段已约满，请重新选择' : '支持改约，系统将同步履约侧') }}</view>
						</view>
						<view class="brand-pill" :style="item.disabled || item.full ? 'background:#f7f0d8;color:#8a6400;' : ''">{{ item.disabled ? (item.disabledReason === 'expired' ? '已过' : '已满') : item.full ? '已满' : '可约' }}</view>
					</view>
				</view>
			</view>

			<view class="section-title"><text>补充说明</text></view>
			<view class="card">
				<textarea v-model="remark" maxlength="120" class="remark-box" placeholder="例如：临时开会、家里没人、希望改到周末" auto-height />
			</view>
		</scroll-view>

		<view class="footer-bar">
			<view class="btn-ghost" @click="goDetail">返回订单</view>
			<view class="btn-main" @click="submitReschedule">确认改约</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import { getOrderDetail, rescheduleOrderById } from '@/common/services/order.js';
	import { loadAppointmentOptions, pickDefaultAppointment } from '@/common/services/appointment.js';

	const orderId = ref('');
	const orderTitle = ref('');
	const reasons = ['时间冲突', '地址有变', '临时不在家', '希望更早上门'];
	const reason = ref('时间冲突');
	const selectedDate = ref('');
	const selectedSlot = ref('');
	const remark = ref('');
	const appointmentOptions = ref([]);

	const dateList = computed(() => appointmentOptions.value.map(({ label, day }) => ({ label, day })));
	const currentDateOption = computed(() => appointmentOptions.value.find((item) => item.label === selectedDate.value) || appointmentOptions.value[0] || null);
	const slots = computed(() => currentDateOption.value?.slots || []);

	const syncSelectedSlotForDate = () => {
		const current = slots.value.find((item) => item.time === selectedSlot.value);
		if (current && !current.disabled && !current.full) {
			return;
		}
		const firstAvailable = slots.value.find((item) => !item.disabled && !item.full);
		selectedSlot.value = firstAvailable?.time || '';
	};

	const applyAppointmentSelection = (dateLabel = '', slot = '') => {
		if (dateLabel && appointmentOptions.value.some((item) => item.label === dateLabel)) {
			selectedDate.value = dateLabel;
		} else {
			const picked = pickDefaultAppointment(appointmentOptions.value);
			selectedDate.value = picked.dateLabel;
			selectedSlot.value = picked.slot;
			return;
		}
		if (slot) {
			selectedSlot.value = slot;
		}
		syncSelectedSlotForDate();
	};

	const refreshAppointmentOptions = async () => {
		try {
			appointmentOptions.value = await loadAppointmentOptions();
			if (!appointmentOptions.value.length) {
				uni.showToast({ title: '暂无可预约时段', icon: 'none' });
				return;
			}
			if (!selectedDate.value) {
				const picked = pickDefaultAppointment(appointmentOptions.value);
				selectedDate.value = picked.dateLabel;
				selectedSlot.value = picked.slot;
				return;
			}
			syncSelectedSlotForDate();
		} catch (error) {
			uni.showToast({ title: error.message || '加载预约时间失败', icon: 'none' });
		}
	};

	const changeAppointmentDate = (label) => {
		selectedDate.value = label;
		syncSelectedSlotForDate();
	};

	onLoad(async (options) => {
		orderId.value = options.id || '';
		await refreshAppointmentOptions();
		try {
			const currentOrder = await getOrderDetail(options.id);
			orderTitle.value = currentOrder?.title || '';
			if (!currentOrder?.appointment) {
				return;
			}
			const parts = currentOrder.appointment.split(' ');
			applyAppointmentSelection(parts[0], parts.slice(1).join(' '));
		} catch (error) {
			uni.showToast({ title: error.message || '加载订单失败', icon: 'none' });
		}
	});

	const pickSlot = (item) => {
		if (item.disabled || item.full) {
			return;
		}
		selectedSlot.value = item.time;
	};

	const submitReschedule = async () => {
		if (!selectedSlot.value) {
			uni.showToast({ title: '请选择新预约时间', icon: 'none' });
			return;
		}
		try {
			await rescheduleOrderById({
				orderId: orderId.value,
				dateLabel: selectedDate.value,
				dateKey: currentDateOption.value?.dateKey,
				slot: selectedSlot.value,
				reason: reason.value,
				remark: remark.value
			});
			uni.showToast({ title: '改约申请已提交', icon: 'success' });
			setTimeout(() => {
				// 返回原订单详情，触发 onShow 重新拉取最新预约时间
				const pages = getCurrentPages();
				if (pages.length > 1) {
					uni.navigateBack();
				} else {
					uni.redirectTo({ url: `/packageCommon/order/detail?id=${orderId.value}` });
				}
			}, 500);
		} catch (error) {
			uni.showToast({ title: error.message || '改约失败', icon: 'none' });
		}
	};

	const goDetail = () => {
		const pages = getCurrentPages();
		if (pages.length > 1) {
			uni.navigateBack();
			return;
		}
		uni.redirectTo({ url: `/packageCommon/order/detail?id=${orderId.value}` });
	};
</script>

<style lang="scss">
	.order-reschedule-page {
		background: linear-gradient(180deg, #fff8f2 0%, #fff 320rpx);
	}

	.order-reschedule-body {
		padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
	}

	.reason-grid,
	.time-dates {
		display: grid;
		gap: 16rpx;
	}

	.reason-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}

	.reason-chip,
	.time-date {
		padding: 22rpx 18rpx;
		border-radius: 22rpx;
		background: #f8f3f3;
		text-align: center;
		font-size: 26rpx;
		color: #4d3c3c;
	}

	.reason-chip.is-active,
	.time-date.is-active,
	.slot-row.is-active {
		background: #fff1f3;
		border: 2rpx solid #cf1432;
		box-sizing: border-box;
	}

	.time-dates {
		grid-template-columns: repeat(3, minmax(0, 1fr));
	}

	.time-date__label {
		font-size: 28rpx;
		font-weight: 700;
	}

	.time-date__day {
		margin-top: 8rpx;
		font-size: 22rpx;
	}

	.slot-row.is-disabled {
		opacity: 0.72;
	}

	.remark-box {
		width: 100%;
		min-height: 180rpx;
		padding: 24rpx;
		border-radius: 24rpx;
		background: #fff8f8;
		font-size: 28rpx;
		color: #2f2424;
	}
</style>
