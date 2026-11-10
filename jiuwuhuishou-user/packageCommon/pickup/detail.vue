<template>
	<view class="app-page pickup-detail-page">
		<top-nav title="取件详情"></top-nav>
		<scroll-view scroll-y class="page-body compact pickup-detail-body">
			<view class="pickup-detail-status-card">
				<view class="pickup-detail-status-card__title">{{ currentPickup?.statusText || '上门取件中' }}</view>
				<view class="pickup-detail-status-card__desc">{{ statusDesc }}</view>
			</view>

			<view class="pickup-detail-card">
				<view class="pickup-detail-card__title">任务信息</view>
				<view class="pickup-detail-card__row"><text>任务编号</text><text>{{ currentPickup?.orderNo || '-' }}</text></view>
				<view v-if="currentPickup?.recycleOrderNo" class="pickup-detail-card__row"><text>关联订单</text><text>{{ currentPickup.recycleOrderNo }}</text></view>
				<view class="pickup-detail-card__row"><text>回收品类</text><text>{{ currentPickup?.category || '-' }}</text></view>
				<view class="pickup-detail-card__row"><text>预估数量</text><text>{{ currentPickup?.weight || '-' }}</text></view>
				<view class="pickup-detail-card__row"><text>预估金额</text><text>{{ currentPickup?.estimateAmountText || '-' }}</text></view>
				<view class="pickup-detail-card__row pickup-detail-card__row--amount">
					<text>实际成交金额</text>
					<view class="pickup-detail-card__amount-value">
						<template v-if="!hasActualAmount">
							<text class="pickup-detail-card__amount-placeholder">--</text>
							<view
								v-if="currentPickup?.canEditActual"
								class="pickup-detail-card__edit-btn"
								@click.stop="openActualEditor"
							>填写</view>
						</template>
						<template v-else>
							<text>{{ currentPickup?.actualAmountText || '-' }}</text>
							<view
								v-if="currentPickup?.canEditActual"
								class="pickup-detail-card__edit-btn"
								@click.stop="openActualEditor"
							>修改</view>
						</template>
					</view>
				</view>
				<view v-if="hasActualAmount && currentPickup?.actualAmountRemark" class="pickup-detail-card__row pickup-detail-card__row--remark">
					<text>修改说明</text>
					<text class="pickup-detail-card__remark-text">{{ currentPickup.actualAmountRemark }}</text>
				</view>
				<view class="pickup-detail-card__row"><text>预约时间</text><text>{{ currentPickup?.time || '-' }}</text></view>
				<view class="pickup-detail-card__row pickup-detail-card__row--action" @click="callContact">
					<text>联系人</text>
					<view class="pickup-detail-card__action-text">
						<text>{{ currentPickup?.contact || '-' }}</text>
						<u-icon v-if="currentPickup?.contactMobile" name="phone" size="16" color="#cf1432"></u-icon>
					</view>
				</view>
				<view class="pickup-detail-card__row pickup-detail-card__row--action" @click="copyAddress">
					<text>取件地址</text>
					<view class="pickup-detail-card__action-text">
						<text class="pickup-detail-card__row-address">{{ currentPickup?.address || '-' }}</text>
						<u-icon name="file-text" size="16" color="#cf1432"></u-icon>
					</view>
				</view>
			</view>

			<view class="pickup-detail-card" style="margin-top: 22rpx;">
				<view class="pickup-detail-card__title">取件流程</view>
				<view class="pickup-detail-timeline">
					<view v-for="(item, index) in timeline" :key="item.title + index" class="pickup-detail-timeline__item" :class="{ 'is-active': item.active }">
						<view class="pickup-detail-timeline__axis">
							<view v-if="index !== timeline.length - 1" class="pickup-detail-timeline__line"></view>
							<view class="pickup-detail-timeline__dot"></view>
						</view>
						<view class="pickup-detail-timeline__content">
							<view class="pickup-detail-timeline__title">{{ item.title }}</view>
							<view class="pickup-detail-timeline__time">{{ item.time }}</view>
							<view v-if="item.desc" class="pickup-detail-timeline__desc">{{ item.desc }}</view>
						</view>
					</view>
				</view>
			</view>

			<view class="pickup-detail-tip" v-if="currentPickup?.canFinish">
				完成取件后，用户订单将进入「待确认」状态，请确认物品交接无误后再提交。
			</view>

			<view class="pickup-detail-actions">
				<view v-if="currentPickup?.canCancel" class="btn-ghost" @click="handleCancel">取消订单</view>
				<view v-if="currentPickup?.canStart" class="btn-main" @click="handleStart">开始上门</view>
				<view v-else-if="currentPickup?.canFinish" class="btn-main" @click="handleFinish">确认完成取件</view>
				<view v-else-if="currentPickup?.status === 'cancelled'" class="btn-main is-disabled">订单已取消</view>
				<view v-else class="btn-main is-disabled">已完成取件</view>
			</view>
		</scroll-view>

		<u-popup :show="showActualPopup" mode="center" :round="28" @close="closeActualEditor">
			<view class="pickup-actual-popup">
				<view class="pickup-actual-popup__title">{{ actualPopupTitle }}</view>
				<view class="pickup-actual-popup__desc">该金额将作为用户侧实际成交金额与后续打款依据</view>
				<view class="pickup-actual-popup__field">
					<text class="pickup-actual-popup__label">实际成交金额（元）</text>
					<input
						v-model="actualAmountInput"
						class="pickup-actual-popup__input"
						type="digit"
						placeholder="请输入金额"
					/>
				</view>
				<view class="pickup-actual-popup__field">
					<text class="pickup-actual-popup__label">
						修改说明
						<text v-if="isActualAmountChanged" class="pickup-actual-popup__required">*</text>
					</text>
					<textarea
						v-model="actualRemarkInput"
						class="pickup-actual-popup__textarea"
						maxlength="100"
						:placeholder="hasActualAmount ? '修改金额时请填写说明' : '选填'"
					/>
				</view>
				<view class="pickup-actual-popup__actions">
					<view class="pickup-actual-popup__btn pickup-actual-popup__btn--ghost" @click="closeActualEditor">取消</view>
					<view class="pickup-actual-popup__btn pickup-actual-popup__btn--main" @click="submitActualAmount">
						{{ savingActual ? '保存中...' : (hasActualAmount ? '确认修改' : '确认填写') }}
					</view>
				</view>
			</view>
		</u-popup>

		<!-- 操作确认弹框 -->
		<view v-if="confirmDialog.show" class="pickup-confirm-mask" @click="closeConfirmDialog">
			<view class="pickup-confirm" @click.stop>
				<view class="pickup-confirm__icon" :class="'is-' + (confirmDialog.type || 'warn')">
					<u-icon :name="confirmDialog.icon || 'info-circle-fill'" size="36" :color="confirmDialog.iconColor || '#cf1432'"></u-icon>
				</view>
				<view class="pickup-confirm__title">{{ confirmDialog.title }}</view>
				<view class="pickup-confirm__desc">{{ confirmDialog.content }}</view>
				<view class="pickup-confirm__actions">
					<view class="pickup-confirm__btn pickup-confirm__btn--ghost" @click="closeConfirmDialog">取消</view>
					<view
						class="pickup-confirm__btn pickup-confirm__btn--main"
						:class="{ 'is-danger': confirmDialog.danger }"
						@click="onConfirmDialogOk"
					>{{ confirmDialog.confirmText || '确认' }}</view>
				</view>
			</view>
		</view>

		<!-- 操作结果提示：仅展示文字 -->
		<view v-if="resultToast.show" class="pickup-result-toast">
			<view class="pickup-result-toast__card" :class="{ 'is-error': resultToast.type === 'error' }">
				<text class="pickup-result-toast__text">{{ resultToast.message }}</text>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import {
		cancelPickupOrder,
		finishPickup,
		loadPickupDetail,
		startPickup,
		updatePickupActual
	} from '@/common/services/pickup.js';

	const currentPickup = ref(null);
	const pickupId = ref('');
	const loading = ref(false);
	const showActualPopup = ref(false);
	const actualAmountInput = ref('');
	const actualRemarkInput = ref('');
	const savingActual = ref(false);
	const cancelling = ref(false);
	const confirmDialog = ref({
		show: false,
		title: '',
		content: '',
		confirmText: '确认',
		icon: 'info-circle-fill',
		iconColor: '#cf1432',
		type: 'warn',
		danger: false,
		onConfirm: null
	});
	const resultToast = ref({
		show: false,
		message: '',
		type: 'success'
	});
	let resultToastTimer = null;

	const hasActualAmount = computed(() => {
		const amount = currentPickup.value?.actualAmount;
		return amount != null && amount !== '' && !Number.isNaN(Number(amount));
	});
	const actualPopupTitle = computed(() =>
		hasActualAmount.value ? '修改实际成交金额' : '填写实际成交金额'
	);

	const statusDesc = computed(() => {
		if (!currentPickup.value) {
			return '请按预约时间完成上门取件、确认物品和回收交接。';
		}
		if (currentPickup.value.status === 'pending') {
			return '任务待上门，出发前可联系用户确认地址与时间。';
		}
		if (currentPickup.value.status === 'running') {
			return '请按预约时间完成上门取件、确认物品和回收交接。';
		}
		if (currentPickup.value.status === 'cancelled') {
			return '本任务关联订单已取消。';
		}
		return '本任务已完成，用户侧将进入结算确认流程。';
	});

	const timeline = computed(() => currentPickup.value?.timeline || []);

	const isActualAmountChanged = computed(() => {
		const nextAmount = Number(actualAmountInput.value);
		if (actualAmountInput.value === '' || Number.isNaN(nextAmount)) {
			return false;
		}
		const currentAmount = currentPickup.value?.actualAmount;
		if (currentAmount == null || currentAmount === '' || Number.isNaN(Number(currentAmount))) {
			// 首次填写不要求修改说明
			return false;
		}
		return nextAmount !== Number(currentAmount);
	});

	const syncDetail = async () => {
		if (!pickupId.value || loading.value) {
			return;
		}
		loading.value = true;
		try {
			currentPickup.value = await loadPickupDetail(pickupId.value);
		} catch (error) {
			showResultToast(error?.message || '加载失败', 'error');
		} finally {
			loading.value = false;
		}
	};

	onLoad((options) => {
		pickupId.value = options.id || '';
	});

	onShow(() => {
		syncDetail();
	});

	const showConfirmDialog = (options) => {
		confirmDialog.value = {
			show: true,
			title: options.title || '提示',
			content: options.content || '',
			confirmText: options.confirmText || '确认',
			icon: options.icon || 'info-circle-fill',
			iconColor: options.iconColor || '#cf1432',
			type: options.type || 'warn',
			danger: Boolean(options.danger),
			onConfirm: options.onConfirm || null
		};
	};

	const closeConfirmDialog = () => {
		confirmDialog.value = {
			...confirmDialog.value,
			show: false,
			onConfirm: null
		};
	};

	const onConfirmDialogOk = async () => {
		const handler = confirmDialog.value.onConfirm;
		closeConfirmDialog();
		if (typeof handler === 'function') {
			await handler();
		}
	};

	const showResultToast = (message, type = 'success', duration = 1800) => {
		if (resultToastTimer) {
			clearTimeout(resultToastTimer);
			resultToastTimer = null;
		}
		resultToast.value = {
			show: true,
			message: message || '',
			type
		};
		resultToastTimer = setTimeout(() => {
			resultToast.value.show = false;
			resultToastTimer = null;
		}, duration);
	};

	const callContact = () => {
		const mobile = currentPickup.value?.contactMobile;
		if (!mobile) {
			return;
		}
		uni.makePhoneCall({ phoneNumber: mobile });
	};

	const copyAddress = () => {
		const address = currentPickup.value?.address;
		if (!address) {
			return;
		}
		uni.setClipboardData({
			data: address,
			success: () => showResultToast('地址已复制')
		});
	};

	const openActualEditor = () => {
		if (!currentPickup.value?.canEditActual) {
			return;
		}
		const amount = currentPickup.value?.actualAmount;
		actualAmountInput.value = amount != null && amount !== '' ? `${amount}` : '';
		actualRemarkInput.value = currentPickup.value?.actualAmountRemark || '';
		showActualPopup.value = true;
	};

	const closeActualEditor = () => {
		if (savingActual.value) {
			return;
		}
		showActualPopup.value = false;
	};

	const submitActualAmount = async () => {
		if (savingActual.value) {
			return;
		}
		const amount = Number(actualAmountInput.value);
		if (actualAmountInput.value === '' || Number.isNaN(amount) || amount < 0) {
			showResultToast('请输入有效金额', 'error');
			return;
		}
		const remark = `${actualRemarkInput.value || ''}`.trim();
		if (isActualAmountChanged.value && !remark) {
			showResultToast('修改金额时请填写修改说明', 'error');
			return;
		}
		savingActual.value = true;
		const wasFirstFill = !hasActualAmount.value;
		try {
			currentPickup.value = await updatePickupActual(pickupId.value, amount, remark);
			showActualPopup.value = false;
			showResultToast(wasFirstFill ? '实际成交金额已填写' : '实际成交金额已更新');
		} catch (error) {
			showResultToast(error?.message || '保存失败', 'error');
		} finally {
			savingActual.value = false;
		}
	};

	const handleStart = () => {
		showConfirmDialog({
			title: '开始上门',
			content: '确认已出发前往取件地址？',
			confirmText: '开始上门',
			icon: 'car-fill',
			onConfirm: async () => {
				try {
					currentPickup.value = await startPickup(pickupId.value);
					showResultToast('已开始上门');
				} catch (error) {
					showResultToast(error?.message || '操作失败', 'error');
				}
			}
		});
	};

	const handleFinish = () => {
		if (!hasActualAmount.value) {
			openActualEditor();
			return;
		}
		showConfirmDialog({
			title: '确认完成取件',
			content: '请确认已完成物品交接。提交后用户订单将进入待确认状态。',
			confirmText: '确认完成',
			icon: 'checkmark-circle-fill',
			onConfirm: async () => {
				try {
					const result = await finishPickup(pickupId.value);
					showResultToast(result?.message || '已完成取件，用户订单已进入待确认');
					setTimeout(() => {
						uni.navigateBack();
					}, 900);
				} catch (error) {
					showResultToast(error?.message || '操作失败', 'error');
				}
			}
		});
	};

	const handleCancel = () => {
		if (!currentPickup.value?.canCancel || cancelling.value) {
			return;
		}
		showConfirmDialog({
			title: '取消订单',
			content: '确认取消该取件任务关联的订单？取消后不可恢复。',
			confirmText: '确认取消',
			icon: 'close-circle-fill',
			iconColor: '#cf1432',
			danger: true,
			onConfirm: async () => {
				cancelling.value = true;
				try {
					currentPickup.value = await cancelPickupOrder(pickupId.value);
					showResultToast('订单已取消');
					setTimeout(() => {
						uni.navigateBack();
					}, 900);
				} catch (error) {
					showResultToast(error?.message || '取消失败', 'error');
				} finally {
					cancelling.value = false;
				}
			}
		});
	};
</script>

<style lang="scss">
	.pickup-detail-page { background: linear-gradient(180deg, #fff8f5 0%, #fff 340rpx); }
	.pickup-detail-body { padding-top: 16rpx; padding-bottom: calc(100rpx + env(safe-area-inset-bottom)); }
	.pickup-detail-status-card,
	.pickup-detail-card { background: #fff; border-radius: 28rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06); }
	.pickup-detail-status-card { padding: 28rpx; }
	.pickup-detail-status-card__title { font-size: 38rpx; font-weight: 800; color: #cf1432; }
	.pickup-detail-status-card__desc { margin-top: 14rpx; font-size: 24rpx; line-height: 1.7; color: #837171; }
	.pickup-detail-card { margin-top: 22rpx; padding: 26rpx 24rpx; }
	.pickup-detail-card__title { font-size: 32rpx; font-weight: 700; color: #302525; }
	.pickup-detail-card__row { display: flex; align-items: flex-start; justify-content: space-between; gap: 18rpx; padding: 18rpx 0; border-bottom: 2rpx solid #f3eaea; font-size: 26rpx; color: #6b5959; }
	.pickup-detail-card__row:last-child { border-bottom: 0; padding-bottom: 0; }
	.pickup-detail-card__row text:last-child { flex: 1; text-align: right; color: #302525; }
	.pickup-detail-card__row--amount { align-items: center; }
	.pickup-detail-card__amount-value { flex: 1; display: inline-flex; align-items: center; justify-content: flex-end; gap: 16rpx; color: #302525; min-height: 40rpx; }
	.pickup-detail-card__amount-value > text { flex: none; text-align: right; color: #cf1432; font-weight: 700; }
	.pickup-detail-card__amount-placeholder { color: #9a8888 !important; font-weight: 500 !important; }
	.pickup-detail-card__edit-btn {
		flex-shrink: 0;
		padding: 8rpx 20rpx;
		border-radius: 999rpx;
		background: #fff1f3;
		border: 2rpx solid #f3c4cb;
		font-size: 22rpx;
		line-height: 1.2;
		color: #cf1432;
	}
	.pickup-detail-card__row--remark { align-items: flex-start; }
	.pickup-detail-card__remark-text { flex: 1; text-align: right; color: #302525; line-height: 1.6; word-break: break-all; }
	.pickup-detail-card__row--action { cursor: pointer; }
	.pickup-detail-card__action-text { flex: 1; display: inline-flex; align-items: flex-start; justify-content: flex-end; gap: 10rpx; text-align: right; color: #302525; }
	.pickup-detail-card__row-address { line-height: 1.6; }
	.pickup-detail-timeline { margin-top: 20rpx; }
	.pickup-detail-timeline__item { display: flex; gap: 18rpx; }
	.pickup-detail-timeline__item + .pickup-detail-timeline__item { margin-top: 20rpx; }
	.pickup-detail-timeline__axis { position: relative; width: 30rpx; flex-shrink: 0; display: flex; justify-content: center; }
	.pickup-detail-timeline__line { position: absolute; left: 50%; top: 18rpx; bottom: -22rpx; transform: translateX(-50%); width: 4rpx; background: #e9dede; }
	.pickup-detail-timeline__dot { width: 18rpx; height: 18rpx; border-radius: 50%; background: #d8cccc; margin-top: 8rpx; }
	.pickup-detail-timeline__item.is-active .pickup-detail-timeline__dot { background: #cf1432; box-shadow: 0 0 0 8rpx #ffe7ea; }
	.pickup-detail-timeline__title { font-size: 28rpx; font-weight: 700; color: #302525; }
	.pickup-detail-timeline__time,
	.pickup-detail-timeline__desc { margin-top: 8rpx; font-size: 24rpx; color: #847171; line-height: 1.7; }
	.pickup-detail-tip { margin-top: 22rpx; padding: 20rpx 24rpx; border-radius: 20rpx; background: #fff7f7; font-size: 24rpx; line-height: 1.7; color: #8a6464; }
	.pickup-detail-actions { display: flex; gap: 18rpx; margin-top: 28rpx; }
	.pickup-detail-actions .btn-main,
	.pickup-detail-actions .btn-ghost { flex: 1; }
	.pickup-detail-actions .btn-main.is-disabled { opacity: 0.55; }
	.pickup-actual-popup { width: 620rpx; padding: 36rpx 32rpx 28rpx; box-sizing: border-box; }
	.pickup-actual-popup__title { font-size: 34rpx; font-weight: 800; color: #302525; text-align: center; }
	.pickup-actual-popup__desc { margin-top: 12rpx; font-size: 24rpx; line-height: 1.6; color: #8a7171; text-align: center; }
	.pickup-actual-popup__field { margin-top: 28rpx; }
	.pickup-actual-popup__label { display: block; font-size: 24rpx; color: #6b5959; }
	.pickup-actual-popup__required { margin-left: 6rpx; color: #cf1432; }
	.pickup-actual-popup__input {
		margin-top: 14rpx;
		height: 84rpx;
		padding: 0 24rpx;
		border-radius: 18rpx;
		background: #faf6f6;
		border: 2rpx solid #f0e4e4;
		font-size: 30rpx;
		color: #302525;
		box-sizing: border-box;
	}
	.pickup-actual-popup__textarea {
		margin-top: 14rpx;
		width: 100%;
		min-height: 140rpx;
		padding: 20rpx 24rpx;
		border-radius: 18rpx;
		background: #faf6f6;
		border: 2rpx solid #f0e4e4;
		font-size: 26rpx;
		color: #302525;
		line-height: 1.6;
		box-sizing: border-box;
	}
	.pickup-actual-popup__actions { display: flex; gap: 18rpx; margin-top: 32rpx; }
	.pickup-actual-popup__btn {
		flex: 1;
		height: 80rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		font-weight: 700;
	}
	.pickup-actual-popup__btn--ghost { background: #f7f1f1; color: #6b5959; }
	.pickup-actual-popup__btn--main { background: #cf1432; color: #fff; }

	.pickup-confirm-mask {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		z-index: 20000;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 48rpx;
		box-sizing: border-box;
		background: rgba(32, 18, 18, 0.48);
	}
	.pickup-confirm {
		width: 580rpx;
		padding: 40rpx 32rpx 28rpx;
		border-radius: 28rpx;
		background: #fff;
		box-shadow: 0 20rpx 56rpx rgba(80, 36, 36, 0.18);
	}
	.pickup-confirm__icon {
		width: 88rpx;
		height: 88rpx;
		margin: 0 auto;
		border-radius: 50%;
		background: #fff1f3;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.pickup-confirm__title {
		margin-top: 24rpx;
		text-align: center;
		font-size: 34rpx;
		font-weight: 800;
		color: #2f2424;
	}
	.pickup-confirm__desc {
		margin-top: 16rpx;
		text-align: center;
		font-size: 26rpx;
		line-height: 1.7;
		color: #7a6666;
	}
	.pickup-confirm__actions {
		margin-top: 36rpx;
		display: flex;
		gap: 18rpx;
	}
	.pickup-confirm__btn {
		flex: 1;
		height: 80rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		font-weight: 700;
	}
	.pickup-confirm__btn--ghost {
		background: #f6f0f0;
		color: #6b5959;
	}
	.pickup-confirm__btn--main {
		background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%);
		color: #fff;
		box-shadow: 0 10rpx 20rpx rgba(207, 20, 50, 0.22);
	}
	.pickup-confirm__btn--main.is-danger {
		background: linear-gradient(180deg, #e11d3a 0%, #b90d2a 100%);
	}

	.pickup-result-toast {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		z-index: 21000;
		display: flex;
		align-items: center;
		justify-content: center;
		pointer-events: none;
	}
	.pickup-result-toast__card {
		max-width: 520rpx;
		padding: 28rpx 40rpx;
		border-radius: 16rpx;
		background: rgba(47, 30, 30, 0.86);
		box-shadow: 0 12rpx 28rpx rgba(0, 0, 0, 0.16);
	}
	.pickup-result-toast__card.is-error {
		background: rgba(120, 24, 36, 0.9);
	}
	.pickup-result-toast__text {
		font-size: 28rpx;
		line-height: 1.55;
		color: #fff;
		text-align: center;
		font-weight: 600;
	}
</style>
