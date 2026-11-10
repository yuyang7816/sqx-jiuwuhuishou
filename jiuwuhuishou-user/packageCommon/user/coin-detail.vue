<template>
	<view class="app-page coin-page">
		<top-nav title="积分明细"></top-nav>
		<scroll-view scroll-y class="page-body compact coin-body">
			<view class="coin-hero">
				<view class="coin-hero__label">当前积分</view>
				<view class="coin-hero__amount">{{ coinBalanceText }}</view>
				<view class="coin-hero__sub">签到、邀请奖励会记入积分明细</view>
			</view>

			<view class="coin-tabs">
				<view
					v-for="tab in tabs"
					:key="tab.value"
					class="coin-tabs__item"
					:class="{ 'is-active': activeTab === tab.value }"
					@click="activeTab = tab.value"
				>{{ tab.label }}</view>
			</view>

			<view class="coin-list">
				<view v-if="loading" class="coin-empty">加载中...</view>
				<template v-else>
					<view v-for="item in filteredLogs" :key="item.id" class="coin-item">
						<view class="coin-item__left">
							<view class="coin-item__icon" :class="item.iconClass">
								<u-icon :name="item.icon" size="18" :color="item.iconColor"></u-icon>
							</view>
							<view class="coin-item__info">
								<view class="coin-item__title">{{ item.title }}</view>
								<view class="coin-item__desc">{{ item.desc }}</view>
								<view class="coin-item__time">{{ item.time }}</view>
							</view>
						</view>
						<view class="coin-item__right">
							<view class="coin-item__amount" :class="item.amountClass">{{ item.amount }}</view>
							<view class="coin-item__status">{{ item.status }}</view>
						</view>
					</view>
					<view v-if="filteredLogs.length === 0" class="coin-empty">暂无积分明细</view>
				</template>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import {
		filterCoinLogs,
		formatCoinBalance,
		loadCoinDetailPageData
	} from '@/common/services/welfare.js';

	const tabs = [
		{ label: '全部', value: 'all' },
		{ label: '收入', value: 'income' },
		{ label: '支出', value: 'expense' }
	];
	const activeTab = ref('all');
	const loading = ref(false);
	const coinBalance = ref(0);
	const logs = ref([]);

	const coinBalanceText = computed(() => formatCoinBalance(coinBalance.value));
	const filteredLogs = computed(() => filterCoinLogs(logs.value, activeTab.value));

	const loadPageData = async () => {
		if (!ensureLoggedIn()) {
			coinBalance.value = 0;
			logs.value = [];
			return;
		}
		loading.value = true;
		try {
			const data = await loadCoinDetailPageData();
			coinBalance.value = data.summary?.coinBalance || 0;
			logs.value = data.logs || [];
		} catch (error) {
			coinBalance.value = 0;
			logs.value = [];
			uni.showToast({
				title: error.message || '加载积分明细失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.coin-page { background: linear-gradient(180deg, #fff8f5 0%, #fff 340rpx); }
	.coin-body { padding-top: 14rpx; padding-bottom: calc(80rpx + env(safe-area-inset-bottom)); }
	.coin-hero,
	.coin-item { background: #fff; border-radius: 28rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06); }
	.coin-hero { padding: 28rpx; text-align: center; background: linear-gradient(135deg, #ffe1a6 0%, #ffc81b 100%); box-shadow: 0 20rpx 40rpx rgba(255, 191, 31, 0.22); }
	.coin-hero__label { font-size: 24rpx; color: #6d4e00; }
	.coin-hero__amount { margin-top: 16rpx; font-size: 64rpx; font-weight: 800; line-height: 1; color: #342500; }
	.coin-hero__sub { margin-top: 16rpx; font-size: 24rpx; line-height: 1.6; color: rgba(78, 56, 0, 0.82); }
	.coin-tabs { display: flex; gap: 16rpx; margin-top: 24rpx; }
	.coin-tabs__item { min-width: 120rpx; height: 56rpx; padding: 0 22rpx; border-radius: 999rpx; background: #f1ecec; color: #7c6868; font-size: 24rpx; display: flex; align-items: center; justify-content: center; box-sizing: border-box; }
	.coin-tabs__item.is-active { background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%); color: #fff; box-shadow: 0 12rpx 24rpx rgba(207, 20, 50, 0.2); }
	.coin-list { margin-top: 22rpx; display: flex; flex-direction: column; gap: 18rpx; }
	.coin-empty { padding: 48rpx 24rpx; text-align: center; font-size: 26rpx; color: #9a8888; }
	.coin-item { padding: 24rpx; display: flex; align-items: center; justify-content: space-between; gap: 18rpx; }
	.coin-item__left { display: flex; align-items: center; gap: 16rpx; flex: 1; min-width: 0; }
	.coin-item__icon { width: 68rpx; height: 68rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
	.coin-item__icon.is-red { background: #ffe5e8; }
	.coin-item__icon.is-gray { background: #efebeb; }
	.coin-item__info { flex: 1; min-width: 0; }
	.coin-item__title { font-size: 30rpx; font-weight: 700; color: #2f2424; }
	.coin-item__desc { margin-top: 10rpx; font-size: 24rpx; line-height: 1.6; color: #8a7676; }
	.coin-item__time { margin-top: 10rpx; font-size: 22rpx; color: #b19f9f; }
	.coin-item__right { text-align: right; }
	.coin-item__amount { font-size: 32rpx; font-weight: 800; }
	.coin-item__amount.is-income { color: #cf1432; }
	.coin-item__amount.is-expense { color: #5f4b4b; }
	.coin-item__status { margin-top: 10rpx; font-size: 22rpx; color: #8b7979; }
</style>
