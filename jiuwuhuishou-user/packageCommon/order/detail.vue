<template>
	<view class="app-page order-detail-page">
		<top-nav title="订单详情"></top-nav>

		<scroll-view scroll-y class="page-body compact order-detail-body" :class="{ 'is-cancelled': isCancelled || isDone }">
			<view class="order-status-card">
				<view class="order-status-card__icon-wrap"><view class="order-status-card__icon-bg"><u-icon name="car" size="34" color="#cf1432"></u-icon></view></view>
				<view class="order-status-card__title">{{ statusTitle }}</view>
				<view class="order-status-card__desc">{{ statusDesc }}</view>
			</view>

			<view v-if="showStatusMachine" class="reward-card row-between">
				<view>
					<view class="reward-card__title">标准订单状态机</view>
					<view class="reward-card__amount">{{ currentOrder?.orderNo || '待审核 → 已分配 → 上门中 → 质检中 → 待确认 → 已完成' }}</view>
				</view>
				<view class="reward-card__btn" :class="{ 'is-confirmed': isSpecEstimateConfirmed }" @click="go('/packageCommon/order/settlement-confirm')">{{ confirmBtnText }}</view>
			</view>

			<view class="detail-card">
				<view class="detail-card__head row-between" style="justify-content:flex-start;"><u-icon name="share" size="18" color="#cf1432"></u-icon><text class="detail-card__title">物流追踪</text></view>
				<template v-if="isMailOrder">
					<view class="express-info">
						<view class="express-info__row">
							<text class="express-info__label">快递公司</text>
							<text class="express-info__value">{{ currentOrder?.expressCompany || '顺丰速运' }}</text>
						</view>
						<view class="express-info__row">
							<text class="express-info__label">快递单号</text>
							<text class="express-info__value express-info__value--em">{{ currentOrder?.expressNo || '待生成' }}</text>
						</view>
						<view class="express-info__row express-info__row--status">
							<text class="express-info__label">物流状态</text>
							<view class="express-info__status">
								<text class="express-info__value">{{ expressStatusText }}</text>
								<view v-if="canShipMail" class="express-track-btn" @click="openShipPopup">发货</view>
								<view v-else-if="showExpressTrackBtn" class="express-track-btn" @click="openExpressTracks">物流轨迹</view>
							</view>
						</view>
					</view>
					<view class="track-section__title">订单流程</view>
				</template>
				<view class="order-timeline">
					<view v-for="(item, index) in timelineItems" :key="'log-' + item.title + index" class="order-timeline__item" :class="{ 'is-active': item.active }">
						<view class="order-timeline__dot-wrap"><view class="order-timeline__line" v-if="index !== timelineItems.length - 1"></view><view class="order-timeline__dot"></view></view>
						<view class="order-timeline__content"><view class="order-timeline__title">{{ item.title }}</view><view class="order-timeline__time">{{ item.time }}</view><view v-if="item.desc" class="order-timeline__desc">{{ item.desc }}</view></view>
					</view>
					<view v-if="!timelineItems.length" class="track-section__empty">暂无订单流程</view>
				</view>
			</view>

			<view class="detail-card" style="margin-top: 22rpx;">
				<view class="detail-card__head row-between" style="justify-content:flex-start;"><u-icon name="file-text" size="18" color="#cf1432"></u-icon><text class="detail-card__title">订单信息</text></view>
				<view class="order-info-grid">
					<view class="order-info-grid__row"><text class="order-info-grid__label">订单编号</text><text class="order-info-grid__value">{{ currentOrder?.orderNo || 'RE202310248899' }}</text></view>
					<view class="order-info-grid__row"><text class="order-info-grid__label">回收品类</text><text class="order-info-grid__value">{{ currentOrder?.category || '废纸/金属/塑料' }}</text></view>
					<view class="order-info-grid__row"><text class="order-info-grid__label">履约方式</text><text class="order-info-grid__value">{{ currentOrder?.fulfillTypeLabel || '上门订单' }}</text></view>
					<view v-if="isMailOrder" class="order-info-grid__row"><text class="order-info-grid__label">快递单号</text><text class="order-info-grid__value">{{ currentOrder?.expressNo || '待生成' }}</text></view>
					<view class="order-info-grid__row"><text class="order-info-grid__label">{{ isMailOrder ? '取件时间' : '上门时间' }}</text><text class="order-info-grid__value">{{ currentOrder?.appointment || '--' }}</text></view>
					<view class="order-info-grid__row"><text class="order-info-grid__label">{{ isMailOrder ? '取件地址' : '上门地址' }}</text><text class="order-info-grid__value">{{ currentOrder?.pickupAddress || currentOrder?.address || '—' }}</text></view>
					<template v-if="isMailOrder">
						<view v-if="currentOrder?.mailReceiver" class="order-info-grid__row"><text class="order-info-grid__label">收件人</text><text class="order-info-grid__value">{{ currentOrder.mailReceiver }}</text></view>
						<view v-if="currentOrder?.mailPhone" class="order-info-grid__row"><text class="order-info-grid__label">收件电话</text><text class="order-info-grid__value">{{ currentOrder.mailPhone }}</text></view>
						<view class="order-info-grid__row"><text class="order-info-grid__label">收件地址</text><text class="order-info-grid__value">{{ currentOrder?.mailAddressOnly || currentOrder?.mailAddress || '—' }}</text></view>
					</template>
					<view class="order-info-grid__row"><text class="order-info-grid__label">履约规则</text><text class="order-info-grid__value brand-text" @click="go('/packageRecycle/common/service-check')">查看服务范围与价格规则</text></view>
					<view class="order-info-grid__row"><text class="order-info-grid__label">预估金额</text><text class="order-info-grid__value">{{ currentOrder?.estimateText || '—' }}</text></view>
					<view class="order-info-grid__row"><text class="order-info-grid__label">实际成交金额</text><text class="order-info-grid__value">{{ currentOrder?.actualText || '—' }}</text></view>
				</view>
				<view v-if="showEstimatePreview" class="order-info-preview">
					<view class="order-info-preview__left">
						<view class="order-info-preview__thumb"><view class="order-info-preview__thumb-block"></view></view>
						<view class="order-info-preview__info"><view class="order-info-preview__title">回收详情预估</view><view class="order-info-preview__desc">{{ currentOrder?.detailText || currentOrder?.estimateText || '约 15.0kg 混合可回收物' }}</view></view>
					</view>
					<text class="order-info-preview__link" :class="{ 'is-confirmed': isEstimateConfirmed }" @click="go('/packageCommon/order/settlement-confirm')">{{ settlementLinkText }}</text>
				</view>
			</view>

			<template v-if="!isCancelled">
				<view class="section-title"><text>快捷处理</text></view>
				<view class="grid-two">
					<view v-for="item in quickActions" :key="item.title" class="service-card" @click="go(item.url)">
						<view class="dot-badge"><u-icon :name="item.icon" size="20" color="#c8102e"></u-icon></view>
						<view class="title">{{ item.title }}</view>
						<view class="desc">{{ item.desc }}</view>
					</view>
				</view>
			</template>
		</scroll-view>

		<floating-service></floating-service>

		<u-popup :show="showExpressTracks" mode="bottom" round="24" @close="showExpressTracks = false">
			<view class="express-track-popup">
				<view class="express-track-popup__head">
					<text class="express-track-popup__title">物流轨迹</text>
					<view class="express-track-popup__close" @click="showExpressTracks = false">
						<u-icon name="close" size="18" color="#857272"></u-icon>
					</view>
				</view>
				<view class="express-track-popup__meta">
					<text>{{ currentOrder?.expressCompany || '顺丰速运' }}</text>
					<text class="express-track-popup__no">{{ currentOrder?.expressNo || '待生成' }}</text>
				</view>
				<scroll-view scroll-y class="express-track-popup__body">
					<view v-if="expressTracks.length" class="order-timeline">
						<view v-for="(item, index) in expressTracks" :key="'express-' + item.title + index" class="order-timeline__item" :class="{ 'is-active': item.active }">
							<view class="order-timeline__dot-wrap"><view class="order-timeline__line" v-if="index !== expressTracks.length - 1"></view><view class="order-timeline__dot"></view></view>
							<view class="order-timeline__content"><view class="order-timeline__title">{{ item.title }}</view><view class="order-timeline__time">{{ item.time }}</view><view v-if="item.desc" class="order-timeline__desc">{{ item.desc }}</view></view>
						</view>
					</view>
					<view v-else class="track-section__empty">暂无物流轨迹</view>
				</scroll-view>
			</view>
		</u-popup>

		<u-popup :show="showShipPopup" mode="bottom" round="24" @close="closeShipPopup">
			<view class="ship-popup">
				<view class="ship-popup__head">
					<text class="ship-popup__title">发货</text>
					<view class="ship-popup__close" @click="closeShipPopup">
						<u-icon name="close" size="18" color="#857272"></u-icon>
					</view>
				</view>
				<view class="ship-popup__body">
					<view class="ship-popup__row">
						<text class="ship-popup__label">快递名称</text>
						<input class="ship-popup__input is-readonly" :value="shipForm.expressCompany" disabled />
					</view>
					<view class="ship-popup__row">
						<text class="ship-popup__label">快递单号</text>
						<input
							class="ship-popup__input"
							v-model="shipForm.expressNo"
							type="text"
							maxlength="64"
							placeholder="请输入快递单号"
						/>
					</view>
				</view>
				<view class="ship-popup__footer">
					<view class="btn-ghost ship-popup__btn" @click="closeShipPopup">取消</view>
					<view class="btn-main ship-popup__btn" @click="submitShip">确认发货</view>
				</view>
			</view>
		</u-popup>

		<u-popup :show="showCourierPopup" mode="bottom" round="24" @close="closeCourierPopup">
			<view class="courier-popup">
				<view class="courier-popup__head">
					<text class="courier-popup__title">联系回收员</text>
					<view class="courier-popup__close" @click="closeCourierPopup">
						<u-icon name="close" size="18" color="#857272"></u-icon>
					</view>
				</view>
				<view class="courier-popup__body">
					<view class="courier-popup__avatar">
						<u-icon name="account-fill" size="36" color="#cf1432"></u-icon>
					</view>
					<text class="courier-popup__name">{{ courierInfo?.name || '回收员' }}</text>
					<text class="courier-popup__phone">{{ courierInfo?.mobileMask || courierInfo?.mobile || '暂无电话' }}</text>
					<text class="courier-popup__hint">点击下方按钮可直接拨打电话</text>
				</view>
				<view class="courier-popup__footer">
					<view class="btn-ghost courier-popup__btn" @click="closeCourierPopup">取消</view>
					<view class="btn-main courier-popup__btn" @click="callCourier">联系</view>
				</view>
			</view>
		</u-popup>

		<view v-if="showFooter" class="footer-bar order-detail-footer">
			<view v-if="canCancel" class="btn-ghost order-detail-footer__ghost" @click="handleCancel">取消订单</view>
			<view v-if="!isMailOrder" class="btn-ghost order-detail-footer__ghost" @click="go('/packageCommon/order/reschedule')">改约</view>
			<view v-if="canShipMail" class="btn-main order-detail-footer__main" @click="openShipPopup">发货</view>
			<view v-else-if="!isMailOrder" class="btn-main order-detail-footer__main" @click="openCourierPopup">联系回收员</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import { getOrderDetail, getExpressTracks, cancelOrderById, shipMailOrderById } from '@/common/services/order.js';
	import { fetchOrderCourier, dialPhoneNumber } from '@/common/services/orderSuccess.js';
	import { isPayoutProcessing, isSpecOrderType, isWeightOrderType, resolveExpressTracks } from '@/common/adapters/order.js';

	const currentOrder = ref(null);
	const orderId = ref('');
	const showExpressTracks = ref(false);
	const showShipPopup = ref(false);
	const showCourierPopup = ref(false);
	const courierLoading = ref(false);
	const courierInfo = ref(null);
	const shipping = ref(false);
	const shipForm = ref({
		expressCompany: '顺丰速运',
		expressNo: ''
	});
	const expressTracksLoading = ref(false);
	const liveExpressTracks = ref([]);
	const cancelling = ref(false);
	const logs = computed(() => currentOrder.value?.logs || []);
	const expressTracks = computed(() => {
		if (liveExpressTracks.value.length) {
			return liveExpressTracks.value;
		}
		return currentOrder.value?.expressTracks || [];
	});
	const isMailOrder = computed(() => currentOrder.value?.fulfillType === 'mail');
	const isCancelled = computed(() => {
		const order = currentOrder.value;
		if (!order) return false;
		return order.statusKey === 'cancelled' || order.status === '已取消';
	});
	const isDone = computed(() => currentOrder.value?.statusKey === 'done' || currentOrder.value?.status === '已完成');
	/** 下方时间线始终展示订单流程轨迹；物流轨迹通过弹窗查看 */
	const timelineItems = computed(() => {
		const items = logs.value || [];
		return items.map((item) => {
			const title = String(item.title || '');
			if (title.includes('顺丰下单跳过')) {
				return { ...item, title: '待顺丰取件' };
			}
			return item;
		});
	});
	const expressStatusText = computed(() => {
		const desc = `${currentOrder.value?.statusDesc || ''}`.trim();
		if (isMailOrder.value && (desc === '待顺丰寄件取件' || desc === '待收货' || desc.includes('待顺丰'))) {
			return desc.includes('待顺丰') ? '待顺丰寄件取件' : desc;
		}
		const latest = expressTracks.value.find((item) => item.active) || expressTracks.value[0];
		return latest?.title || desc || currentOrder.value?.status || '待快递员上门';
	});
	const canShipMail = computed(() => {
		if (!isMailOrder.value || isCancelled.value || isDone.value) {
			return false;
		}
		const desc = `${currentOrder.value?.statusDesc || ''}`.trim();
		const key = currentOrder.value?.statusKey || '';
		return (key === 'running' || currentOrder.value?.status === '进行中')
			&& (desc === '待顺丰寄件取件' || desc.includes('待顺丰'));
	});
	const showExpressTrackBtn = computed(() => {
		if (!isMailOrder.value || canShipMail.value) {
			return false;
		}
		const desc = `${currentOrder.value?.statusDesc || ''}`.trim();
		if (desc === '待顺丰寄件取件' || desc.includes('待顺丰')) {
			return false;
		}
		return true;
	});
	const statusTitle = computed(() => {
		if (isCancelled.value) {
			return currentOrder.value?.status || '已取消';
		}
		if (currentOrder.value?.statusKey === 'dispute' || currentOrder.value?.status === '争议处理中') {
			return '争议处理中';
		}
		if (isDone.value) {
			return '已完成';
		}
		if (isMailOrder.value) {
			return currentOrder.value?.statusDesc || currentOrder.value?.status || '待顺丰寄件取件';
		}
		return currentOrder.value?.status || '上门回收中';
	});
	const statusDesc = computed(() => {
		if (isCancelled.value) {
			return currentOrder.value?.desc || '订单已取消';
		}
		if (currentOrder.value?.statusKey === 'dispute' || currentOrder.value?.status === '争议处理中') {
			return currentOrder.value?.desc || currentOrder.value?.statusDesc || '价格争议处理中，请等待平台复核';
		}
		if (isDone.value && isMailOrder.value) {
			return '快递已签收并完成结算打款';
		}
		if (isDone.value) {
			return currentOrder.value?.desc || '订单已完成';
		}
		if (isMailOrder.value) {
			return currentOrder.value?.desc || '请按预约时间准备好包裹，顺丰将上门取件寄往平台质检';
		}
		return currentOrder.value?.desc || '预计上门取件，已支持改约、异常反馈和联系回收员';
	});
	const isEstimateConfirmed = computed(() => Boolean(currentOrder.value?.estimateAmountConfirmed));
	const isSpecOrder = computed(() => Boolean(currentOrder.value) && isSpecOrderType(currentOrder.value));
	const isWeightOrder = computed(() => Boolean(currentOrder.value) && isWeightOrderType(currentOrder.value));
	const isSpecEstimateConfirmed = computed(() => isSpecOrder.value && isEstimateConfirmed.value);
	const isPendingConfirm = computed(() => {
		const order = currentOrder.value;
		if (!order) return false;
		return order.statusKey === 'pendingConfirm' || order.status === '待确认';
	});
	const isPayingProgress = computed(() => isPayoutProcessing(currentOrder.value || {}));
	const isRunning = computed(() => {
		const order = currentOrder.value;
		if (!order) return false;
		return order.statusKey === 'running' || order.status === '进行中';
	});
	/** 与后端 RecycleOrderStateMachine.canCancel 对齐 */
	const canCancel = computed(() => {
		const key = currentOrder.value?.statusKey || '';
		return ['running', 'waitAssign', 'pendingConfirm', 'exception', 'dispute'].includes(key);
	});
	const showStatusMachine = computed(() => isSpecOrder.value && isPendingConfirm.value);
	/** 进行中不展示结算确认；仅待确认可进入结算 */
	const showEstimatePreview = computed(() => !isCancelled.value && isWeightOrder.value && isPendingConfirm.value && !isRunning.value);
	const showFooter = computed(() => !isCancelled.value && !isDone.value);
	const confirmBtnText = computed(() => (isSpecEstimateConfirmed.value ? '已确认，去查看' : '去确认'));
	const settlementLinkText = computed(() => (isEstimateConfirmed.value ? '已确认结算' : '结算确认'));

	const quickActions = computed(() => {
		if (isDone.value) {
			return [
				{ title: '评价投诉', desc: '对本次回收服务进行评价', icon: 'star', url: '/packageCommon/order/review' }
			];
		}
		const actions = [];
		// 邮寄订单无上门预约，不展示改约
		if (!isMailOrder.value && !isPayingProgress.value) {
			actions.push({ title: '改约处理', desc: '修改时间，尽量保留订单', icon: 'calendar', url: '/packageCommon/order/reschedule' });
		}
		if (!isPayingProgress.value) {
			actions.push({ title: '异常反馈', desc: '迟到、拒收、价格争议都可提交', icon: 'warning', url: '/packageCommon/order/exception' });
		}
		if (isPendingConfirm.value) {
			actions.push({ title: '价格确认', desc: '质检完成后确认实际成交价', icon: 'coupon', url: '/packageCommon/order/settlement-confirm' });
		}
		if (isPayingProgress.value) {
			actions.push({ title: '打款进度', desc: '查看到账状态与收款信息', icon: 'coupon', url: '/packageCommon/order/payout' });
		}
		return actions;
	});

	onLoad(async (options) => {
		orderId.value = options.id || '';
		await loadOrderDetail();
	});

	onShow(async () => {
		if (orderId.value) {
			await loadOrderDetail();
		}
	});

	const loadOrderDetail = async () => {
		try {
			currentOrder.value = await getOrderDetail(orderId.value);
			liveExpressTracks.value = [];
		} catch (error) {
			uni.showToast({ title: error.message || '加载订单失败', icon: 'none' });
		}
	};

	const openExpressTracks = async () => {
		showExpressTracks.value = true;
		if (!isMailOrder.value || !orderId.value || expressTracksLoading.value) {
			return;
		}
		// 与管理端一致：有快递单号时先展示测试轨迹，避免接口失败空白
		if (!liveExpressTracks.value.length && currentOrder.value?.expressNo) {
			const fallback = resolveExpressTracks(currentOrder.value);
			if (fallback.length) {
				liveExpressTracks.value = fallback;
			}
		}
		expressTracksLoading.value = true;
		try {
			const tracks = await getExpressTracks(orderId.value);
			if (Array.isArray(tracks) && tracks.length) {
				liveExpressTracks.value = tracks;
				if (currentOrder.value) {
					currentOrder.value = {
						...currentOrder.value,
						expressTracks: tracks
					};
				}
				return;
			}
			if (currentOrder.value?.expressNo) {
				liveExpressTracks.value = resolveExpressTracks(currentOrder.value);
			}
		} catch (_) {
			// 接口失败（如顺丰凭证未配置）时保留/回落测试轨迹
			if (currentOrder.value?.expressNo) {
				liveExpressTracks.value = resolveExpressTracks({
					...currentOrder.value,
					expressTracks: []
				});
			} else {
				uni.showToast({ title: '物流查询失败', icon: 'none' });
			}
		} finally {
			expressTracksLoading.value = false;
		}
	};

	const openCourierPopup = async () => {
		if (isMailOrder.value || !orderId.value || courierLoading.value) {
			return;
		}
		courierLoading.value = true;
		try {
			courierInfo.value = await fetchOrderCourier(orderId.value);
			showCourierPopup.value = true;
		} catch (error) {
			uni.showToast({ title: error?.message || '暂未分配回收员', icon: 'none' });
		} finally {
			courierLoading.value = false;
		}
	};

	const closeCourierPopup = () => {
		showCourierPopup.value = false;
	};

	const callCourier = async () => {
		const mobile = courierInfo.value?.mobile;
		if (!mobile) {
			uni.showToast({ title: '暂无可用联系电话', icon: 'none' });
			return;
		}
		try {
			await dialPhoneNumber(mobile);
		} catch (error) {
			uni.showToast({ title: error?.message || '拨号失败', icon: 'none' });
		}
	};

	const openShipPopup = () => {
		shipForm.value = {
			expressCompany: '顺丰速运',
			expressNo: currentOrder.value?.expressNo || ''
		};
		showShipPopup.value = true;
	};

	const closeShipPopup = () => {
		if (shipping.value) {
			return;
		}
		showShipPopup.value = false;
	};

	const submitShip = async () => {
		const expressNo = `${shipForm.value.expressNo || ''}`.trim();
		if (!expressNo) {
			uni.showToast({ title: '请填写快递单号', icon: 'none' });
			return;
		}
		if (shipping.value || !orderId.value) {
			return;
		}
		shipping.value = true;
		try {
			await shipMailOrderById({
				orderId: orderId.value,
				expressNo,
				expressCompany: shipForm.value.expressCompany || '顺丰速运'
			});
			uni.showToast({ title: '发货成功', icon: 'success' });
			showShipPopup.value = false;
			await loadOrderDetail();
		} catch (error) {
			uni.showToast({ title: error?.message || '发货失败', icon: 'none' });
		} finally {
			shipping.value = false;
		}
	};

	const go = (url) => uni.navigateTo({ url: `${url}?id=${currentOrder.value?.id || ''}` });

	const handleCancel = () => {
		if (!canCancel.value || cancelling.value || !orderId.value) {
			return;
		}
		uni.showModal({
			title: '取消订单',
			content: '确认取消该订单？取消后不可恢复。',
			success: async (res) => {
				if (!res.confirm) {
					return;
				}
				cancelling.value = true;
				try {
					await cancelOrderById(orderId.value);
					uni.showToast({ title: '订单已取消', icon: 'success' });
					await loadOrderDetail();
				} catch (error) {
					uni.showToast({ title: error?.message || '取消失败', icon: 'none' });
				} finally {
					cancelling.value = false;
				}
			}
		});
	};
</script>

<style lang="scss">
	.order-detail-page { background: linear-gradient(180deg, #fff8f6 0%, #fff 340rpx); }
	.order-detail-body { padding-top: 18rpx; padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
	.order-detail-body.is-cancelled { padding-bottom: calc(40rpx + env(safe-area-inset-bottom)); }
	.order-status-card { text-align: center; padding-top: 8rpx; }
	.order-status-card__icon-wrap { display: flex; justify-content: center; }
	.order-status-card__icon-bg { width: 112rpx; height: 112rpx; border-radius: 50%; background: #ffe5e8; display: flex; align-items: center; justify-content: center; }
	.order-status-card__title { margin-top: 24rpx; font-size: 16px; font-weight: 700; color: #2f2424; }
	.order-status-card__desc { margin-top: 12rpx; font-size: 28rpx; color: #5f4a4a; }
	.reward-card { margin-top: 30rpx; background: linear-gradient(180deg, #ffe39a 0%, #ffd671 100%); border-radius: 24rpx; padding: 24rpx; box-shadow: 0 14rpx 28rpx rgba(255, 206, 104, 0.18); }
	.reward-card__title { font-size: 30rpx; font-weight: 700; color: #5d4216; }
	.reward-card__amount { margin-top: 16rpx; font-size: 28rpx; color: #5d4216; line-height: 1.6; }
	.reward-card__btn { min-width: 120rpx; height: 62rpx; padding: 0 24rpx; border-radius: 999rpx; background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%); font-size: 28rpx; font-weight: 700; color: #fff; display: flex; align-items: center; justify-content: center; }
	.reward-card__btn.is-confirmed { min-width: 220rpx; background: linear-gradient(180deg, #5d4216 0%, #4a3412 100%); }
	.detail-card { margin-top: 22rpx; background: #fff; border-radius: 28rpx; padding: 24rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.07); }
	.detail-card__head { gap: 10rpx; }
	.detail-card__title { font-size: 32rpx; font-weight: 700; }
	.express-info {
		margin-top: 20rpx;
		padding: 20rpx 22rpx;
		border-radius: 22rpx;
		background: #fff8f8;
	}
	.express-info__row {
		display: flex;
		align-items: flex-start;
		justify-content: space-between;
		gap: 18rpx;
	}
	.express-info__row + .express-info__row { margin-top: 14rpx; }
	.express-info__label { flex-shrink: 0; font-size: 24rpx; color: #857272; }
	.express-info__value { flex: 1; text-align: right; font-size: 26rpx; color: #332929; line-height: 1.5; }
	.express-info__value--em { color: #cf1432; font-weight: 700; }
	.express-info__row--status { align-items: center; }
	.express-info__status {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		gap: 14rpx;
		min-width: 0;
	}
	.express-info__status .express-info__value {
		flex: none;
		text-align: right;
	}
	.express-track-btn {
		flex-shrink: 0;
		height: 48rpx;
		padding: 0 18rpx;
		border-radius: 999rpx;
		border: 2rpx solid #cf1432;
		background: #fff;
		color: #cf1432;
		font-size: 22rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.track-section__title {
		margin-top: 28rpx;
		padding-top: 8rpx;
		font-size: 28rpx;
		font-weight: 700;
		color: #3a2c2c;
	}
	.track-section__empty {
		margin-top: 16rpx;
		font-size: 24rpx;
		color: #9a8888;
		text-align: center;
		padding: 40rpx 0;
	}
	.express-track-popup {
		padding: 28rpx 28rpx calc(28rpx + env(safe-area-inset-bottom));
		background: #fff;
	}
	.express-track-popup__head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.express-track-popup__title {
		font-size: 32rpx;
		font-weight: 700;
		color: #2f2424;
	}
	.express-track-popup__close {
		width: 48rpx;
		height: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.express-track-popup__meta {
		margin-top: 12rpx;
		display: flex;
		align-items: center;
		gap: 16rpx;
		font-size: 24rpx;
		color: #7b6969;
	}
	.express-track-popup__no {
		color: #cf1432;
		font-weight: 700;
	}
	.express-track-popup__body {
		margin-top: 8rpx;
		max-height: 62vh;
	}
	.order-timeline__item { display: flex; gap: 18rpx; margin-top: 20rpx; }
	.order-timeline__dot-wrap { position: relative; width: 32rpx; flex-shrink: 0; display: flex; justify-content: center; }
	.order-timeline__line { position: absolute; top: 20rpx; bottom: -30rpx; left: 50%; transform: translateX(-50%); width: 4rpx; background: #ecdede; }
	.order-timeline__dot { width: 18rpx; height: 18rpx; border-radius: 50%; background: #d9cccc; margin-top: 8rpx; }
	.order-timeline__item.is-active .order-timeline__dot { background: #cf1432; box-shadow: 0 0 0 8rpx #ffe3e7; }
	.order-timeline__title { font-size: 28rpx; font-weight: 700; }
	.order-timeline__time,
	.order-timeline__desc { margin-top: 8rpx; font-size: 24rpx; color: #7b6969; }
	.order-info-grid__row { display: flex; align-items: flex-start; justify-content: space-between; gap: 20rpx; padding: 14rpx 0; }
	.order-info-grid__label { font-size: 24rpx; color: #857272; }
	.order-info-grid__value { flex: 1; text-align: right; font-size: 26rpx; color: #332929; }
	.order-info-preview { margin-top: 16rpx; padding: 22rpx; border-radius: 24rpx; background: #fff8f8; display: flex; align-items: center; justify-content: space-between; gap: 16rpx; }
	.order-info-preview__left { display: flex; align-items: center; gap: 16rpx; flex: 1; }
	.order-info-preview__thumb { width: 78rpx; height: 78rpx; border-radius: 20rpx; background: #ffe9ed; display: flex; align-items: center; justify-content: center; }
	.order-info-preview__thumb-block { width: 32rpx; height: 32rpx; border-radius: 10rpx; background: #cf1432; }
	.order-info-preview__title { font-size: 28rpx; font-weight: 700; }
	.order-info-preview__desc { margin-top: 8rpx; font-size: 24rpx; color: #7d6969; }
	.order-info-preview__link { font-size: 26rpx; color: #cf1432; font-weight: 700; }
	.order-info-preview__link.is-confirmed { color: #5d4216; }
	.order-detail-footer__ghost,
	.order-detail-footer__main { flex: 1; }
	.ship-popup {
		padding: 28rpx 28rpx calc(28rpx + env(safe-area-inset-bottom));
		background: #fff;
	}
	.ship-popup__head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.ship-popup__title {
		font-size: 32rpx;
		font-weight: 700;
		color: #2f2424;
	}
	.ship-popup__close {
		width: 48rpx;
		height: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.ship-popup__body {
		margin-top: 28rpx;
	}
	.ship-popup__row + .ship-popup__row {
		margin-top: 22rpx;
	}
	.ship-popup__label {
		display: block;
		font-size: 26rpx;
		color: #857272;
		margin-bottom: 12rpx;
	}
	.ship-popup__input {
		width: 100%;
		height: 84rpx;
		padding: 0 24rpx;
		border-radius: 18rpx;
		background: #f7f2f2;
		font-size: 28rpx;
		color: #332929;
		box-sizing: border-box;
	}
	.ship-popup__input.is-readonly {
		color: #7b6969;
	}
	.ship-popup__footer {
		margin-top: 36rpx;
		display: flex;
		gap: 18rpx;
	}
	.ship-popup__btn {
		flex: 1;
	}
	.courier-popup {
		padding: 28rpx 28rpx calc(28rpx + env(safe-area-inset-bottom));
		background: #fff;
	}
	.courier-popup__head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.courier-popup__title {
		font-size: 32rpx;
		font-weight: 700;
		color: #2f2424;
	}
	.courier-popup__close {
		width: 48rpx;
		height: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.courier-popup__body {
		margin-top: 28rpx;
		padding: 36rpx 24rpx;
		border-radius: 24rpx;
		background: #fff8f8;
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	.courier-popup__avatar {
		width: 96rpx;
		height: 96rpx;
		border-radius: 50%;
		background: #ffe9ed;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.courier-popup__name {
		margin-top: 20rpx;
		font-size: 32rpx;
		font-weight: 700;
		color: #332929;
	}
	.courier-popup__phone {
		margin-top: 12rpx;
		font-size: 30rpx;
		font-weight: 700;
		color: #cf1432;
	}
	.courier-popup__hint {
		margin-top: 12rpx;
		font-size: 24rpx;
		color: #857272;
	}
	.courier-popup__footer {
		margin-top: 36rpx;
		display: flex;
		gap: 18rpx;
	}
	.courier-popup__btn {
		flex: 1;
	}
</style>
