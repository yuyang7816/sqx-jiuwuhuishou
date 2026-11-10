<template>
	<view class="app-page order-page">
		<top-nav title="我的订单" :showBack="false"></top-nav>
		<view class="order-tabs">
			<view v-for="item in tabs" :key="item" class="order-tabs__item" :class="{ 'is-active': currentTab === item }" @click="switchTab(item)">{{ item }}</view>
		</view>

		<scroll-view scroll-y class="page-body compact order-body">
			<view v-if="loading" class="order-loading">加载中...</view>
			<view v-else class="order-list">
				<view v-for="item in orders" :key="item.id" class="order-card">
					<view class="order-card__top row-between" style="align-items:flex-start;">
						<view class="order-card__left row-between" style="justify-content:flex-start; align-items:flex-start;">
							<view class="order-card__icon" :class="item.iconClass"><u-icon :name="item.icon" size="22" :color="item.iconColor"></u-icon></view>
							<view class="order-card__info">
								<view class="order-card__title" @click="goDetail(item.id)">{{ item.title }}</view>
								<view class="order-card__time">预约时间：{{ item.time }}</view>
								<view class="order-card__sub">{{ item.desc }}</view>
							</view>
						</view>
						<view class="order-card__status" :class="item.statusClass">{{ item.status }}</view>
					</view>

					<view class="order-card__line"></view>

					<view class="order-card__actions">
					<view v-for="action in item.actions" :key="action.label" class="order-card__btn" :class="action.type === 'main' ? 'order-card__btn--main' : 'order-card__btn--ghost'" @click="handleAction({ ...action, orderId: item.id })">{{ action.label }}</view>
					</view>
				</view>
			</view>

			<view class="order-empty" v-if="!loading && orders.length === 0">
				<u-icon name="file-text" size="72" color="#c8c1c1"></u-icon>
				<view class="order-empty__text">当前分类下暂无订单</view>
			</view>
		</scroll-view>

		<floating-service></floating-service>

		<u-popup :show="showCancel" mode="center" :round="28" @close="showCancel = false">
			<view class="order-cancel-popup">
				<view class="order-cancel-popup__icon-wrap"><view class="order-cancel-popup__icon"><u-icon name="warning" size="26" color="#cf1432"></u-icon></view></view>
				<view class="order-cancel-popup__title">确认取消订单？</view>
				<view class="order-cancel-popup__desc">请告诉我们您的取消原因，我们会努力优化服务体验</view>
				<view class="order-cancel-popup__reasons">
					<view v-for="reason in reasons" :key="reason" class="order-cancel-popup__reason" :class="{ 'is-selected': selectedReason === reason }" @click="selectedReason = reason">{{ reason }}</view>
				</view>
				<view class="order-cancel-popup__textarea-wrap">
					<textarea v-model="customReason" class="order-cancel-popup__textarea" maxlength="120" placeholder="其他原因说明（选填）" placeholder-class="order-cancel-popup__placeholder" auto-height />
				</view>
				<view class="order-cancel-popup__confirm" @click="confirmCancel">确认取消</view>
				<view class="order-cancel-popup__wait" @click="showCancel = false">再等等看</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { listOrders, cancelOrderById } from '@/common/services/order.js';
	import { loadCancelReasons } from '@/common/services/orderOptions.js';

	const tabs = ['全部', '进行中', '待确认', '已完成'];
	const currentTab = ref('全部');
	const loading = ref(false);
	const showCancel = ref(false);
	const selectedReason = ref('');
	const customReason = ref('');
	const reasons = ref([]);

	const orders = ref([]);
	const pendingOrderId = ref('');

	const loadOrders = async () => {
		loading.value = true;
		try {
			orders.value = await listOrders(currentTab.value);
		} catch (error) {
			orders.value = [];
			uni.showToast({ title: error.message || '加载订单失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	const switchTab = (tab) => {
		if (currentTab.value === tab) {
			return;
		}
		currentTab.value = tab;
		loadOrders();
	};

	onShow(() => {
		if (!ensureLoggedIn()) {
			return;
		}
		try {
			const preferredTab = uni.getStorageSync('orderListTab');
			if (preferredTab && tabs.includes(preferredTab)) {
				currentTab.value = preferredTab;
			}
			uni.removeStorageSync('orderListTab');
		} catch (error) {
			// ignore storage errors
		}
		loadCancelReasons().then((list) => {
			reasons.value = list;
			if (!selectedReason.value && list.length) {
				selectedReason.value = list[0];
			}
		});
		loadOrders();
	});

	const actionMap = {
		detail: '/packageCommon/order/detail',
		reschedule: '/packageCommon/order/reschedule',
		exception: '/packageCommon/order/exception',
		settlement: '/packageCommon/order/settlement-confirm',
		payout: '/packageCommon/order/payout',
		review: '/packageCommon/order/review'
	};

	const buildActionUrl = (path, id) => `${path}?id=${encodeURIComponent(id)}`;

	const goDetail = (orderId) => {
		uni.navigateTo({ url: buildActionUrl(actionMap.detail, orderId) });
	};

	const handleAction = (action) => {
		if (action.action === 'cancel') {
			pendingOrderId.value = action.orderId;
			if (!selectedReason.value && reasons.value.length) {
				selectedReason.value = reasons.value[0];
			}
			showCancel.value = true;
			return;
		}
		const url = actionMap[action.action];
		if (url) {
			uni.navigateTo({ url: buildActionUrl(url, action.orderId) });
		}
	};

	const confirmCancel = async () => {
		if (!pendingOrderId.value) {
			return;
		}
		try {
			await cancelOrderById(pendingOrderId.value);
			await loadOrders();
			showCancel.value = false;
			pendingOrderId.value = '';
			uni.showToast({ title: '订单已取消', icon: 'success' });
		} catch (error) {
			uni.showToast({ title: error.message || '取消失败', icon: 'none' });
		}
	};
</script>

<style lang="scss">
	.order-page { background: linear-gradient(180deg, #fff8f6 0%, #fff 340rpx); }
	.order-tabs { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); height: 86rpx; background: #fff; border-top: 2rpx solid #f4ecec; border-bottom: 2rpx solid #f2e7e7; }
	.order-tabs__item { position: relative; display: flex; align-items: center; justify-content: center; font-size: 30rpx; color: #463737; }
	.order-tabs__item.is-active { color: #cf1432; font-weight: 700; }
	.order-tabs__item.is-active::after { content: ''; position: absolute; left: 50%; bottom: 8rpx; transform: translateX(-50%); width: 44rpx; height: 6rpx; border-radius: 999rpx; background: #cf1432; }
	.order-body { padding-top: 22rpx; padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
	.order-loading { padding: 80rpx 0; text-align: center; font-size: 26rpx; color: #948383; }
	.order-list { display: flex; flex-direction: column; gap: 22rpx; }
	.order-card { background: #fff; border-radius: 30rpx; padding: 24rpx 24rpx 22rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.07); }
	.order-card__left { flex: 1; gap: 18rpx; }
	.order-card__icon { width: 76rpx; height: 76rpx; border-radius: 18rpx; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
	.order-card__icon.is-pink { background: #ffe6e8; }
	.order-card__icon.is-gold { background: #ffe7a8; }
	.order-card__icon.is-gray { background: #ece9e9; }
	.order-card__info { flex: 1; min-width: 0; }
	.order-card__title { font-size: 16px; font-weight: 800; color: #2d2323; line-height: 1.2; }
	.order-card__time { margin-top: 10rpx; font-size: 24rpx; color: #7d6969; }
	.order-card__sub { margin-top: 8rpx; font-size: 22rpx; color: #9a8383; }
	.order-card__status { min-width: 110rpx; max-width: 200rpx; min-height: 46rpx; padding: 8rpx 16rpx; border-radius: 14rpx; font-size: 22rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; box-sizing: border-box; white-space: nowrap; flex-shrink: 0; }
	.order-card__status.is-running { background: #ffe5e8; color: #cf1432; }
	.order-card__status.is-done { background: #efebeb; color: #766767; }
	.order-card__status.is-warn { background: #fff4d4; color: #8a6400; }
	.order-card__line { height: 2rpx; background: #f2e8e8; margin: 24rpx 0 20rpx; }
	.order-card__actions { display: flex; gap: 10rpx; justify-content: flex-end; flex-wrap: nowrap; }
	.order-card__btn { min-width: 124rpx; height: 60rpx; padding: 0 18rpx; border-radius: 999rpx; display: flex; align-items: center; justify-content: center; font-size: 24rpx; font-weight: 700; white-space: nowrap; }
	.order-card__btn--ghost { background: #fff; border: 2rpx solid rgba(200, 16, 46, 0.22); color: #453535; }
	.order-card__btn--main { background: linear-gradient(135deg, #d01231 0%, #b80826 100%); color: #fff; box-shadow: 0 16rpx 32rpx rgba(200, 16, 46, 0.18); }
	.order-empty {
		min-height: 420rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		text-align: center;
		color: #948383;
	}
	.order-empty__text { margin-top: 18rpx; font-size: 26rpx; }
	.order-cancel-popup { width: 640rpx; padding: 34rpx 30rpx 28rpx; background: #fff; border-radius: 28rpx; }
	.order-cancel-popup__icon-wrap { display: flex; justify-content: center; }
	.order-cancel-popup__icon { width: 82rpx; height: 82rpx; border-radius: 50%; background: #ffe5e8; display: flex; align-items: center; justify-content: center; }
	.order-cancel-popup__title { margin-top: 20rpx; text-align: center; font-size: 36rpx; font-weight: 800; }
	.order-cancel-popup__desc { margin-top: 12rpx; text-align: center; font-size: 24rpx; color: #7d6969; }
	.order-cancel-popup__reasons { display: flex; flex-wrap: wrap; gap: 14rpx; margin-top: 24rpx; }
	.order-cancel-popup__reason { padding: 16rpx 20rpx; border-radius: 20rpx; background: #f7f3f3; font-size: 24rpx; color: #554545; }
	.order-cancel-popup__reason.is-selected { background: #fff1f3; color: #cf1432; }
	.order-cancel-popup__textarea-wrap { margin-top: 18rpx; padding: 22rpx 24rpx; border-radius: 24rpx; background: #fff8f8; border: 2rpx solid #f1dddd; }
	.order-cancel-popup__textarea { display: block; width: 100%; min-height: 150rpx; font-size: 26rpx; line-height: 1.7; color: #2f2424; background: transparent; }
	.order-cancel-popup__placeholder { color: #b6a5a5; font-size: 26rpx; }
	.order-cancel-popup__confirm,
	.order-cancel-popup__wait { height: 84rpx; margin-top: 18rpx; border-radius: 999rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 700; }
	.order-cancel-popup__confirm { background: linear-gradient(135deg, #d01231 0%, #b80826 100%); color: #fff; }
	.order-cancel-popup__wait { background: #f7f2f2; color: #5b4949; }
</style>
