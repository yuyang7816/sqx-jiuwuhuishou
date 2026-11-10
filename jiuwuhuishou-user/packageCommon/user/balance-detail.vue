<template>
	<view class="app-page balance-page">
		<top-nav title="余额明细"></top-nav>
		<scroll-view scroll-y class="page-body compact balance-body">
			<view class="balance-hero">
				<view class="balance-hero__label">钱包余额（元）</view>
				<view class="balance-hero__amount">{{ walletSummary.balanceText }}</view>
				<view class="balance-hero__sub">{{ walletSummary.subText }}</view>
				<view class="balance-hero__actions">
					<view class="balance-hero__btn balance-hero__btn--main" @click="go('/packageCommon/user/withdraw')">提现</view>
				</view>
			</view>

			<view class="balance-tabs">
				<view
					v-for="tab in tabs"
					:key="tab.value"
					class="balance-tabs__item"
					:class="{ 'is-active': activeTab === tab.value }"
					@click="activeTab = tab.value"
				>{{ tab.label }}</view>
			</view>

			<view class="balance-list">
				<view v-if="loading" class="balance-empty">加载中...</view>
				<template v-else>
					<view v-for="item in filteredLogs" :key="item.id" class="balance-item">
						<view class="balance-item__left">
							<view class="balance-item__icon" :class="item.iconClass">
								<u-icon :name="item.icon" size="18" :color="item.iconColor"></u-icon>
							</view>
							<view class="balance-item__info">
								<view class="balance-item__title">{{ item.title }}</view>
								<view class="balance-item__desc">{{ item.desc }}</view>
								<view class="balance-item__time">{{ item.time }}</view>
							</view>
						</view>
						<view class="balance-item__right">
							<view class="balance-item__amount" :class="item.amountClass">{{ item.amount }}</view>
							<view class="balance-item__status">{{ item.status }}</view>
						</view>
					</view>
					<view v-if="filteredLogs.length === 0" class="balance-empty">暂无明细记录</view>
				</template>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { DEFAULT_WALLET_SUMMARY } from '@/common/services/my.js';
	import { filterWalletTransactions, loadBalanceDetailPageData } from '@/common/services/wallet.js';

	const tabs = [
		{ label: '全部', value: 'all' },
		{ label: '收入', value: 'income' },
		{ label: '提现', value: 'withdraw' }
	];
	const activeTab = ref('all');
	const loading = ref(false);
	const walletSummary = ref(DEFAULT_WALLET_SUMMARY);
	const logs = ref([]);

	const filteredLogs = computed(() => filterWalletTransactions(logs.value, activeTab.value));

	const loadPageData = async () => {
		if (!ensureLoggedIn()) {
			walletSummary.value = DEFAULT_WALLET_SUMMARY;
			logs.value = [];
			return;
		}
		loading.value = true;
		try {
			const data = await loadBalanceDetailPageData();
			walletSummary.value = data.walletSummary;
			logs.value = data.transactions;
		} catch (error) {
			walletSummary.value = DEFAULT_WALLET_SUMMARY;
			logs.value = [];
			uni.showToast({
				title: error.message || '加载余额明细失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	const go = (url) => uni.navigateTo({ url });

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.balance-page { background: linear-gradient(180deg, #fff8f5 0%, #fff 340rpx); }
	.balance-body { padding-top: 14rpx; padding-bottom: calc(80rpx + env(safe-area-inset-bottom)); }
	.balance-hero,
	.balance-item { background: #fff; border-radius: 28rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06); }
	.balance-hero { padding: 28rpx; text-align: center; background: linear-gradient(135deg, #ffe1a6 0%, #ffc81b 100%); box-shadow: 0 20rpx 40rpx rgba(255, 191, 31, 0.22); }
	.balance-hero__label { font-size: 24rpx; color: #6d4e00; }
	.balance-hero__amount { margin-top: 16rpx; font-size: 64rpx; font-weight: 800; line-height: 1; color: #342500; }
	.balance-hero__sub { margin-top: 16rpx; font-size: 24rpx; line-height: 1.6; color: rgba(78, 56, 0, 0.82); }
	.balance-hero__actions { display: flex; justify-content: center; margin-top: 28rpx; }
	.balance-hero__btn { width: 320rpx; max-width: 100%; height: 80rpx; border-radius: 999rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 700; }
	.balance-hero__btn--main { background: linear-gradient(135deg, #d01231 0%, #b80826 100%); color: #fff; box-shadow: 0 16rpx 30rpx rgba(200, 16, 46, 0.22); }
	.balance-tabs { display: flex; gap: 16rpx; margin-top: 24rpx; }
	.balance-tabs__item { min-width: 120rpx; height: 56rpx; padding: 0 22rpx; border-radius: 999rpx; background: #f1ecec; color: #7c6868; font-size: 24rpx; display: flex; align-items: center; justify-content: center; box-sizing: border-box; }
	.balance-tabs__item.is-active { background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%); color: #fff; box-shadow: 0 12rpx 24rpx rgba(207, 20, 50, 0.2); }
	.balance-list { margin-top: 22rpx; display: flex; flex-direction: column; gap: 18rpx; }
	.balance-empty { padding: 48rpx 24rpx; text-align: center; font-size: 26rpx; color: #9a8888; }
	.balance-item { padding: 24rpx; display: flex; align-items: center; justify-content: space-between; gap: 18rpx; }
	.balance-item__left { display: flex; align-items: center; gap: 16rpx; flex: 1; min-width: 0; }
	.balance-item__icon { width: 68rpx; height: 68rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
	.balance-item__icon.is-red { background: #ffe5e8; }
	.balance-item__icon.is-gold { background: #fff1c8; }
	.balance-item__icon.is-gray { background: #efebeb; }
	.balance-item__icon.is-green { background: #e3f9ee; }
	.balance-item__info { flex: 1; min-width: 0; }
	.balance-item__title { font-size: 30rpx; font-weight: 700; color: #2f2424; }
	.balance-item__desc { margin-top: 10rpx; font-size: 24rpx; line-height: 1.6; color: #8a7676; }
	.balance-item__time { margin-top: 10rpx; font-size: 22rpx; color: #b19f9f; }
	.balance-item__right { text-align: right; }
	.balance-item__amount { font-size: 32rpx; font-weight: 800; }
	.balance-item__amount.is-income { color: #cf1432; }
	.balance-item__amount.is-withdraw { color: #5f4b4b; }
	.balance-item__status { margin-top: 10rpx; font-size: 22rpx; color: #8b7979; }
</style>
