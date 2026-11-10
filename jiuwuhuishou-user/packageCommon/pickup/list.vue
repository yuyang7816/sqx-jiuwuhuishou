<template>
	<view class="app-page pickup-page">
		<top-nav title="我的取件"></top-nav>
		<scroll-view scroll-y class="page-body compact pickup-body">
			<view class="pickup-hero">
				<view class="pickup-hero__title">今日取件任务</view>
				<view class="pickup-hero__desc">员工可在这里查看待上门、取件中和已完成的取件任务。</view>
				<view class="pickup-hero__stats">
					<view class="pickup-hero__stat" @click="switchTab('pending')">
						<view class="pickup-hero__stat-label">待上门</view>
						<view class="pickup-hero__stat-value">{{ stats.pendingCount }}</view>
					</view>
					<view class="pickup-hero__stat" @click="switchTab('running')">
						<view class="pickup-hero__stat-label">进行中</view>
						<view class="pickup-hero__stat-value">{{ stats.runningCount }}</view>
					</view>
					<view class="pickup-hero__stat" @click="switchTab('done')">
						<view class="pickup-hero__stat-label">已完成</view>
						<view class="pickup-hero__stat-value">{{ stats.doneCount }}</view>
					</view>
				</view>
			</view>

			<view class="pickup-tabs">
				<view v-for="tab in tabs" :key="tab.value" class="pickup-tabs__item" :class="{ 'is-active': activeTab === tab.value }" @click="activeTab = tab.value">{{ tab.label }}</view>
			</view>

			<view class="pickup-list">
				<view v-if="!pickupList.length" class="pickup-empty">暂无取件任务</view>
				<view v-for="item in pickupList" :key="item.id" class="pickup-card" @click="goDetail(item.id)">
					<view class="pickup-card__head row-between">
						<view>
							<view class="pickup-card__title">{{ item.title }}</view>
							<view class="pickup-card__address">{{ item.address }}</view>
						</view>
						<view class="pickup-card__status" :class="item.statusClass">{{ item.statusText }}</view>
					</view>
					<view class="pickup-card__meta">
						<view class="pickup-card__meta-item"><u-icon name="account" size="14" color="#8f7b7b"></u-icon><text>{{ item.contact }}</text></view>
						<view class="pickup-card__meta-item"><u-icon name="clock" size="14" color="#8f7b7b"></u-icon><text>{{ item.time }}</text></view>
					</view>
					<view class="pickup-card__footer row-between">
						<view class="pickup-card__count">{{ item.category }} · {{ item.weight }}</view>
						<view class="pickup-card__link">{{ item.status === 'pending' ? '开始处理' : '查看详情' }}</view>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { ref, watch } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { loadPickupListPageData } from '@/common/services/pickup.js';

	const tabs = [
		{ label: '全部', value: 'all' },
		{ label: '待上门', value: 'pending' },
		{ label: '取件中', value: 'running' },
		{ label: '已完成', value: 'done' }
	];
	const activeTab = ref('all');
	const pickupList = ref([]);
	const stats = ref({
		pendingCount: 0,
		runningCount: 0,
		doneCount: 0
	});
	const loading = ref(false);

	const syncPickups = async () => {
		if (loading.value) {
			return;
		}
		loading.value = true;
		try {
			const data = await loadPickupListPageData(activeTab.value);
			stats.value = data.stats;
			pickupList.value = data.list;
		} catch (error) {
			uni.showToast({ title: error?.message || '加载失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	onShow(() => {
		syncPickups();
	});

	watch(activeTab, () => {
		syncPickups();
	});

	const switchTab = (value) => {
		activeTab.value = value;
	};

	const goDetail = (id) => {
		uni.navigateTo({ url: `/packageCommon/pickup/detail?id=${id}` });
	};
</script>

<style lang="scss">
	.pickup-page { background: linear-gradient(180deg, #fff8f5 0%, #fff 340rpx); }
	.pickup-body { padding-top: 14rpx; padding-bottom: calc(80rpx + env(safe-area-inset-bottom)); }
	.pickup-hero,
	.pickup-card { background: #fff; border-radius: 28rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06); }
	.pickup-hero { padding: 28rpx; }
	.pickup-hero__title { font-size: 38rpx; font-weight: 800; color: #2f2424; }
	.pickup-hero__desc { margin-top: 14rpx; font-size: 24rpx; line-height: 1.7; color: #867272; }
	.pickup-hero__stats { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 16rpx; margin-top: 24rpx; }
	.pickup-hero__stat { padding: 18rpx 14rpx; border-radius: 20rpx; background: #fff7f7; text-align: center; }
	.pickup-hero__stat:active { transform: scale(0.98); }
	.pickup-hero__stat-label { font-size: 22rpx; color: #8e7a7a; }
	.pickup-hero__stat-value { margin-top: 12rpx; font-size: 40rpx; font-weight: 800; color: #cf1432; }
	.pickup-tabs { display: flex; gap: 16rpx; margin-top: 24rpx; }
	.pickup-tabs__item { min-width: 128rpx; height: 58rpx; padding: 0 24rpx; border-radius: 999rpx; background: #f3eded; color: #7a6767; font-size: 24rpx; display: flex; align-items: center; justify-content: center; box-sizing: border-box; }
	.pickup-tabs__item.is-active { background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%); color: #fff; box-shadow: 0 12rpx 26rpx rgba(207, 20, 50, 0.22); }
	.pickup-list { margin-top: 22rpx; display: flex; flex-direction: column; gap: 18rpx; }
	.pickup-empty { padding: 80rpx 0; text-align: center; font-size: 26rpx; color: #8d7a7a; }
	.pickup-card { padding: 24rpx; }
	.pickup-card__title { font-size: 32rpx; font-weight: 800; color: #2f2424; }
	.pickup-card__address { margin-top: 10rpx; font-size: 24rpx; line-height: 1.6; color: #8d7a7a; }
	.pickup-card__status { min-width: 102rpx; height: 44rpx; padding: 0 14rpx; border-radius: 999rpx; font-size: 22rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; box-sizing: border-box; }
	.pickup-card__status.is-pending { background: #fff4d4; color: #8a6400; }
	.pickup-card__status.is-running { background: #ffe5e8; color: #cf1432; }
	.pickup-card__status.is-done { background: #efebeb; color: #766767; }
	.pickup-card__meta { display: flex; flex-wrap: wrap; gap: 18rpx; margin-top: 18rpx; }
	.pickup-card__meta-item { display: inline-flex; align-items: center; gap: 8rpx; font-size: 22rpx; color: #766565; }
	.pickup-card__footer { margin-top: 20rpx; padding-top: 18rpx; border-top: 2rpx solid #f3eaea; }
	.pickup-card__count { font-size: 24rpx; color: #7e6b6b; }
	.pickup-card__link { font-size: 24rpx; font-weight: 700; color: #cf1432; }
</style>
