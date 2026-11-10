<template>
	<view class="app-page settlement-page">
		<top-nav :title="pageTitle"></top-nav>
		<scroll-view scroll-y class="page-body compact settlement-body">
			<view class="card hero-soft">
				<view v-if="!isEstimateConfirmed" style="font-size: 40rpx; font-weight: 800;">实际价格已生成，请确认成交</view>
				<view v-else style="font-size: 40rpx; font-weight: 800;">实际价格已确认</view>
				<view class="muted" style="margin-top: 14rpx;">{{ heroDesc }}</view>
			</view>
			<view class="card" style="margin-top: 24rpx;">
				<template v-if="pageLoading">
					<view class="compare-row">
						<text>预估金额</text>
						<text class="compare-row__loading">加载中...</text>
					</view>
					<view class="compare-row compare-row--actual">
						<text>实际成交金额</text>
						<text class="compare-row__loading compare-row__loading--main">加载中...</text>
					</view>
				</template>
				<template v-else>
					<view class="compare-row"><text>预估金额</text><text class="compare-row__price">{{ estimateAmountText }}</text></view>
					<view class="compare-row compare-row--actual"><text>实际成交金额</text><text class="compare-row__price compare-row__price--main">{{ actualAmountText }}</text></view>
				</template>
			</view>
			<template v-if="!pageLoading && !isEstimateConfirmed">
				<view class="section-title"><text>不接受当前价格？</text></view>
				<view class="card">
					<view class="muted">你可以发起价格争议，平台会进入人工复核；若仍不接受，可转客服介入或取消成交。</view>
					<view class="btn-ghost settlement-dispute-btn" @click="openDispute">发起价格争议</view>
				</view>
			</template>
		</scroll-view>
		<view class="footer-bar">
			<template v-if="isEstimateConfirmed">
				<view class="btn-main settlement-footer__back" @click="goOrderList">返回订单</view>
			</template>
			<template v-else>
				<view class="btn-ghost" :class="{ 'is-disabled': pageLoading || cancelling }" @click="!pageLoading && !cancelling && openCancel()">取消订单</view>
				<view class="btn-ghost" :class="{ 'is-disabled': pageLoading || cancelling }" @click="!pageLoading && !cancelling && go('/packageCommon/order/exception')">异常反馈</view>
				<view class="btn-main" :class="{ 'is-disabled': pageLoading || cancelling }" @click="!pageLoading && !cancelling && confirmDeal()">确认成交</view>
			</template>
		</view>

		<u-popup :show="showCancel" mode="center" :round="28" @close="closeCancel">
			<view class="settlement-cancel-popup">
				<view class="settlement-cancel-popup__icon-wrap">
					<view class="settlement-cancel-popup__icon"><u-icon name="warning" size="26" color="#cf1432"></u-icon></view>
				</view>
				<view class="settlement-cancel-popup__title">确认取消订单？</view>
				<view class="settlement-cancel-popup__desc">请告诉我们您的取消原因，我们会努力优化服务体验</view>
				<view class="settlement-cancel-popup__reasons">
					<view
						v-for="reason in cancelReasons"
						:key="reason"
						class="settlement-cancel-popup__reason"
						:class="{ 'is-selected': selectedCancelReason === reason }"
						@click="selectedCancelReason = reason"
					>{{ reason }}</view>
				</view>
				<view class="settlement-cancel-popup__textarea-wrap">
					<textarea
						v-model="customCancelReason"
						class="settlement-cancel-popup__textarea"
						maxlength="120"
						placeholder="其他原因说明（选填）"
						placeholder-class="settlement-cancel-popup__placeholder"
						auto-height
					/>
				</view>
				<view class="settlement-cancel-popup__confirm" :class="{ 'is-disabled': cancelling }" @click="confirmCancel">
					{{ cancelling ? '取消中...' : '确认取消' }}
				</view>
				<view class="settlement-cancel-popup__wait" :class="{ 'is-disabled': cancelling }" @click="closeCancel">再等等看</view>
			</view>
		</u-popup>

		<u-popup :show="showDisputePopup" mode="center" :round="28" @close="closeDispute">
			<view class="settlement-dispute-popup">
				<view class="settlement-dispute-popup__title">发起价格争议</view>
				<view class="settlement-dispute-popup__desc">请说明不接受当前成交价的原因，平台将进入人工复核</view>
				<view class="settlement-dispute-popup__field">
					<text class="settlement-dispute-popup__label">
						争议说明
						<text class="settlement-dispute-popup__required">*</text>
					</text>
					<textarea
						v-model="disputeDescription"
						class="settlement-dispute-popup__textarea"
						maxlength="200"
						placeholder="请填写争议说明"
					/>
				</view>
				<view class="settlement-dispute-popup__actions">
					<view class="settlement-dispute-popup__btn settlement-dispute-popup__btn--ghost" @click="closeDispute">取消</view>
					<view class="settlement-dispute-popup__btn settlement-dispute-popup__btn--main" @click="submitDispute">
						{{ submittingDispute ? '提交中...' : '提交争议' }}
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import {
		DEFAULT_SETTLEMENT_CONFIRMED_DESC,
		confirmSettlementDeal,
		loadSettlementConfirmedDesc,
		loadSettlementPageData,
		submitSettlementDispute
	} from '@/common/services/settlement.js';
	import { cancelOrderById } from '@/common/services/order.js';
	import { loadCancelReasons } from '@/common/services/orderOptions.js';

	const currentOrder = ref(null);
	const settlement = ref(null);
	const orderId = ref('');
	const pageLoading = ref(true);
	const confirmedDesc = ref(DEFAULT_SETTLEMENT_CONFIRMED_DESC);
	const showDisputePopup = ref(false);
	const disputeDescription = ref('');
	const submittingDispute = ref(false);
	const showCancel = ref(false);
	const cancelReasons = ref([]);
	const selectedCancelReason = ref('');
	const customCancelReason = ref('');
	const cancelling = ref(false);

	const isEstimateConfirmed = computed(() => Boolean(settlement.value?.confirmed || currentOrder.value?.estimateAmountConfirmed));
	const isMailOrder = computed(() => currentOrder.value?.fulfillType === 'mail');
	const pageTitle = computed(() => (isEstimateConfirmed.value ? '实际价格已确认' : '结算确认'));
	const heroDesc = computed(() => (
		isEstimateConfirmed.value
			? confirmedDesc.value
			: '当实物检测结果和预估有差异时，需要用户明确确认，避免争议自动成交。'
	));
	const estimateAmountText = computed(() => {
		if (currentOrder.value?.estimateText) {
			return currentOrder.value.estimateText;
		}
		const amount = Number(settlement.value?.estimateAmount || currentOrder.value?.settlementEstimate || 0);
		return `¥ ${amount.toFixed(2)}`;
	});
	const resolveRawActualAmount = () => {
		const fromSettlement = settlement.value?.actualAmount;
		if (fromSettlement != null && fromSettlement !== '') {
			const amount = Number(fromSettlement);
			if (!Number.isNaN(amount)) {
				return amount;
			}
		}
		const fromOrder = currentOrder.value?.actualAmount ?? currentOrder.value?.settlementActual;
		if (fromOrder != null && fromOrder !== '') {
			const amount = Number(fromOrder);
			if (!Number.isNaN(amount)) {
				return amount;
			}
		}
		return null;
	};
	const actualAmountText = computed(() => {
		const amount = resolveRawActualAmount();
		if (amount == null) {
			return isMailOrder.value ? '--' : '¥ 0.00';
		}
		return `¥ ${amount.toFixed(2)}`;
	});

	const syncPage = async () => {
		if (!orderId.value) {
			pageLoading.value = false;
			return;
		}
		pageLoading.value = true;
		try {
			const data = await loadSettlementPageData(orderId.value);
			currentOrder.value = data.order;
			settlement.value = data.settlement;
			if (data.settlement?.confirmed || data.order?.estimateAmountConfirmed) {
				confirmedDesc.value = await loadSettlementConfirmedDesc();
			}
		} catch (error) {
			uni.showToast({ title: error?.message || '加载失败', icon: 'none' });
		} finally {
			pageLoading.value = false;
		}
	};

	onLoad(async (options) => {
		orderId.value = options.id || '';
		loadCancelReasons().then((list) => {
			cancelReasons.value = list;
			if (!selectedCancelReason.value && list.length) {
				selectedCancelReason.value = list[0];
			}
		});
		await syncPage();
	});

	const go = (url) => uni.navigateTo({ url: `${url}?id=${orderId.value}` });
	const goOrderList = () => {
		try {
			uni.setStorageSync('orderListTab', '全部');
		} catch (error) {
			// ignore storage errors
		}
		uni.switchTab({ url: '/pages/order/order' });
	};
	const confirmDeal = async () => {
		if (pageLoading.value || cancelling.value) {
			return;
		}
		try {
			const result = await confirmSettlementDeal(orderId.value);
			uni.showToast({ title: result?.message || '已确认成交', icon: 'success' });
			setTimeout(() => {
				uni.navigateTo({ url: `/packageCommon/order/payout?id=${orderId.value}` });
			}, 500);
		} catch (error) {
			uni.showToast({ title: error?.message || '确认失败', icon: 'none' });
		}
	};

	const openCancel = () => {
		if (pageLoading.value || cancelling.value || isEstimateConfirmed.value) {
			return;
		}
		if (!selectedCancelReason.value && cancelReasons.value.length) {
			selectedCancelReason.value = cancelReasons.value[0];
		}
		customCancelReason.value = '';
		showCancel.value = true;
	};

	const closeCancel = () => {
		if (cancelling.value) {
			return;
		}
		showCancel.value = false;
	};

	const confirmCancel = async () => {
		if (cancelling.value || !orderId.value) {
			return;
		}
		cancelling.value = true;
		try {
			await cancelOrderById(orderId.value);
			showCancel.value = false;
			uni.showToast({ title: '订单已取消', icon: 'success' });
			setTimeout(() => goOrderList(), 600);
		} catch (error) {
			uni.showToast({ title: error?.message || '取消失败', icon: 'none' });
		} finally {
			cancelling.value = false;
		}
	};

	const openDispute = () => {
		if (pageLoading.value || submittingDispute.value || cancelling.value) {
			return;
		}
		disputeDescription.value = '';
		showDisputePopup.value = true;
	};

	const closeDispute = () => {
		if (submittingDispute.value) {
			return;
		}
		showDisputePopup.value = false;
	};

	const submitDispute = async () => {
		if (submittingDispute.value) {
			return;
		}
		const description = `${disputeDescription.value || ''}`.trim();
		if (!description) {
			uni.showToast({ title: '请填写争议说明', icon: 'none' });
			return;
		}
		submittingDispute.value = true;
		try {
			await submitSettlementDispute({
				orderId: orderId.value,
				reason: '价格争议',
				description
			});
			showDisputePopup.value = false;
			uni.showToast({ title: '争议已提交', icon: 'success' });
			setTimeout(() => goOrderList(), 600);
		} catch (error) {
			uni.showToast({ title: error?.message || '提交失败', icon: 'none' });
		} finally {
			submittingDispute.value = false;
		}
	};
</script>

<style lang="scss">
	.settlement-page { background: linear-gradient(180deg, #fff8f3 0%, #fff 320rpx); }
	.settlement-body { padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
	.compare-row { display: flex; align-items: center; justify-content: space-between; padding: 14rpx 0; font-size: 28rpx; color: #5b4a4a; }
	.compare-row--actual { margin-top: 10rpx; font-weight: 700; color: #2f2424; }
	.compare-row__price { font-size: 32rpx; font-weight: 700; }
	.compare-row__price--main { color: #cf1432; font-size: 42rpx; }
	.compare-row__loading { font-size: 28rpx; font-weight: 500; color: #9a8888; }
	.compare-row__loading--main { font-size: 32rpx; color: #b89a9a; }
	.settlement-dispute-btn { margin-top: 24rpx; }
	.settlement-footer__back { flex: 1; }
	.footer-bar .btn-main.is-disabled,
	.footer-bar .btn-ghost.is-disabled { opacity: 0.55; pointer-events: none; }

	.settlement-cancel-popup { width: 640rpx; padding: 34rpx 30rpx 28rpx; background: #fff; border-radius: 28rpx; box-sizing: border-box; }
	.settlement-cancel-popup__icon-wrap { display: flex; justify-content: center; }
	.settlement-cancel-popup__icon { width: 82rpx; height: 82rpx; border-radius: 50%; background: #ffe5e8; display: flex; align-items: center; justify-content: center; }
	.settlement-cancel-popup__title { margin-top: 20rpx; text-align: center; font-size: 36rpx; font-weight: 800; }
	.settlement-cancel-popup__desc { margin-top: 12rpx; text-align: center; font-size: 24rpx; color: #7d6969; }
	.settlement-cancel-popup__reasons { display: flex; flex-wrap: wrap; gap: 14rpx; margin-top: 24rpx; }
	.settlement-cancel-popup__reason { padding: 16rpx 20rpx; border-radius: 20rpx; background: #f7f3f3; font-size: 24rpx; color: #554545; }
	.settlement-cancel-popup__reason.is-selected { background: #fff1f3; color: #cf1432; }
	.settlement-cancel-popup__textarea-wrap { margin-top: 18rpx; padding: 22rpx 24rpx; border-radius: 24rpx; background: #fff8f8; border: 2rpx solid #f1dddd; }
	.settlement-cancel-popup__textarea { display: block; width: 100%; min-height: 150rpx; font-size: 26rpx; line-height: 1.7; color: #2f2424; background: transparent; }
	.settlement-cancel-popup__placeholder { color: #b6a5a5; font-size: 26rpx; }
	.settlement-cancel-popup__confirm,
	.settlement-cancel-popup__wait { height: 84rpx; margin-top: 18rpx; border-radius: 999rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 700; }
	.settlement-cancel-popup__confirm { background: linear-gradient(135deg, #d01231 0%, #b80826 100%); color: #fff; }
	.settlement-cancel-popup__wait { background: #f7f2f2; color: #5b4949; }
	.settlement-cancel-popup__confirm.is-disabled,
	.settlement-cancel-popup__wait.is-disabled { opacity: 0.55; pointer-events: none; }

	.settlement-dispute-popup { width: 620rpx; padding: 36rpx 32rpx 28rpx; box-sizing: border-box; }
	.settlement-dispute-popup__title { font-size: 34rpx; font-weight: 800; color: #302525; text-align: center; }
	.settlement-dispute-popup__desc { margin-top: 12rpx; font-size: 24rpx; line-height: 1.6; color: #8a7171; text-align: center; }
	.settlement-dispute-popup__field { margin-top: 28rpx; }
	.settlement-dispute-popup__label { display: block; font-size: 24rpx; color: #6b5959; }
	.settlement-dispute-popup__required { margin-left: 6rpx; color: #cf1432; }
	.settlement-dispute-popup__textarea {
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
	.settlement-dispute-popup__actions { display: flex; gap: 18rpx; margin-top: 32rpx; }
	.settlement-dispute-popup__btn {
		flex: 1;
		height: 80rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		font-weight: 700;
	}
	.settlement-dispute-popup__btn--ghost { background: #f7f1f1; color: #6b5959; }
	.settlement-dispute-popup__btn--main { background: #cf1432; color: #fff; }
</style>
