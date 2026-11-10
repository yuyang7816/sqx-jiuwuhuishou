<template>
	<view class="app-page payout-page">
		<top-nav title="打款进度"></top-nav>
		<scroll-view scroll-y class="page-body compact payout-body">
			<view class="card hero-soft">
				<view style="font-size: 40rpx; font-weight: 800;">打款流程全程透明</view>
				<view class="muted" style="margin-top: 14rpx;">结算确认后进入待打款、打款中、已到账或失败重试，减少“钱去哪了”的客服咨询。</view>
			</view>

			<view class="card" style="margin-top: 24rpx;">
				<view class="payout-head row-between">
					<view>
						<view style="font-size: 32rpx; font-weight: 700;">当前状态</view>
						<view class="muted" style="margin-top: 8rpx;">{{ statusHint }}</view>
					</view>
					<view class="brand-pill">{{ payoutStatus }}</view>
				</view>
				<view class="price-main" style="margin-top: 24rpx;">¥ {{ actualAmount }}</view>
			</view>

			<view class="section-title"><text>收款信息</text></view>
			<view class="card">
				<view class="feature-list">
					<view class="feature-row">
						<view>
							<view style="font-size: 28rpx; font-weight: 700;">收款方式</view>
							<view class="muted" style="margin-top: 8rpx;">{{ payChannelText }}</view>
						</view>
						<u-icon name="checkmark-circle-fill" size="20" color="#cf1432"></u-icon>
					</view>
				</view>
			</view>

			<view class="section-title"><text>状态记录</text></view>
			<view class="card">
				<view v-if="!logs.length" class="muted">暂无打款记录</view>
				<view class="timeline-item" v-for="item in logs" :key="item.title + item.time">
					<view class="timeline-item__dot" :class="{ 'is-active': item.active }"></view>
					<view style="flex: 1;">
						<view style="font-size: 28rpx; font-weight: 700;">{{ item.title }}</view>
						<view class="muted" style="margin-top: 8rpx;">{{ item.time }}</view>
					</view>
				</view>
			</view>
		</scroll-view>
		<view class="footer-bar">
			<view class="btn-ghost" @click="go('/packageCommon/order/detail')">查看订单</view>
			<view class="btn-main" @click="go('/packageCommon/order/review')">评价服务</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import { loadPayoutPageData } from '@/common/services/payout.js';

	const payout = ref(null);
	const orderId = ref('');
	const logs = computed(() => payout.value?.logs || []);
	const payoutStatus = computed(() => payout.value?.statusText || '打款中');
	const actualAmount = computed(() => Number(payout.value?.amount || 0).toFixed(2));
	const payChannelText = computed(() => payout.value?.payChannelText || '用户钱包');
	const statusHint = computed(() => (payoutStatus.value === '已到账' ? '款项已到账，可前往评价' : '预计 1-2 小时内到账'));

	onLoad(async (options) => {
		orderId.value = options.id || '';
		try {
			const data = await loadPayoutPageData(orderId.value);
			payout.value = data.payout;
		} catch (error) {
			uni.showToast({ title: error?.message || '加载失败', icon: 'none' });
		}
	});

	const go = (url) => uni.navigateTo({ url: `${url}?id=${orderId.value}` });
</script>

<style lang="scss">
	.payout-page { background: linear-gradient(180deg, #fff8f3 0%, #fff 320rpx); }
	.payout-body { padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
	.timeline-item { display: flex; align-items: flex-start; gap: 16rpx; }
	.timeline-item + .timeline-item { margin-top: 22rpx; }
	.timeline-item__dot { width: 18rpx; height: 18rpx; border-radius: 50%; background: #d8cfcf; margin-top: 12rpx; }
	.timeline-item__dot.is-active { background: #cf1432; box-shadow: 0 0 0 8rpx #ffe7ea; }
</style>
