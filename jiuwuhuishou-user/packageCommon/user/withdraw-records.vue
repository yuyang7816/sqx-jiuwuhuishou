<template>
	<view class="app-page withdraw-records-page">
		<top-nav title="提现记录"></top-nav>
		<scroll-view scroll-y class="page-body compact withdraw-records-body">
			<view v-if="loading" class="withdraw-records-empty">加载中...</view>
			<view v-else-if="!records.length" class="withdraw-records-empty">暂无提现记录</view>
			<view v-else class="withdraw-records-list">
				<view v-for="item in records" :key="item.id" class="withdraw-record-card">
					<view class="withdraw-record-card__top">
						<view>
							<view class="withdraw-record-card__amount">{{ item.amountText }}</view>
							<view class="withdraw-record-card__account">{{ item.accountTypeText }}</view>
						</view>
						<view class="withdraw-record-card__status" :class="item.statusClass">{{ item.statusText }}</view>
					</view>
					<view class="withdraw-record-card__time">{{ item.time }}</view>
					<view v-if="item.reason" class="withdraw-record-card__reason">拒绝原因：{{ item.reason }}</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { loadWithdrawRecords } from '@/common/services/withdraw.js';

	const loading = ref(false);
	const records = ref([]);

	const loadPageData = async () => {
		if (!ensureLoggedIn()) {
			records.value = [];
			return;
		}
		loading.value = true;
		try {
			const data = await loadWithdrawRecords();
			records.value = data.records;
		} catch (error) {
			records.value = [];
			uni.showToast({ title: error.message || '加载提现记录失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.withdraw-records-page { background: linear-gradient(180deg, #fff8f5 0%, #fff 320rpx); }
	.withdraw-records-body { padding-top: 14rpx; padding-bottom: calc(80rpx + env(safe-area-inset-bottom)); }
	.withdraw-records-list { display: flex; flex-direction: column; gap: 18rpx; }
	.withdraw-record-card { background: #fff; border-radius: 28rpx; padding: 24rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06); }
	.withdraw-record-card__top { display: flex; align-items: flex-start; justify-content: space-between; gap: 16rpx; }
	.withdraw-record-card__amount { font-size: 38rpx; font-weight: 800; color: #342500; }
	.withdraw-record-card__account { margin-top: 8rpx; font-size: 24rpx; color: #8d7d7d; }
	.withdraw-record-card__status { padding: 8rpx 16rpx; border-radius: 999rpx; font-size: 22rpx; font-weight: 700; }
	.withdraw-record-card__status.is-pending { background: #fff5e8; color: #b8842f; }
	.withdraw-record-card__status.is-success { background: #eef9f1; color: #2cbf63; }
	.withdraw-record-card__status.is-reject { background: #fff1f3; color: #cf1432; }
	.withdraw-record-card__time { margin-top: 16rpx; font-size: 22rpx; color: #b0a0a0; }
	.withdraw-record-card__reason { margin-top: 10rpx; font-size: 24rpx; line-height: 1.6; color: #8d6868; }
	.withdraw-records-empty { padding: 80rpx 24rpx; text-align: center; font-size: 26rpx; color: #9a8888; }
</style>
