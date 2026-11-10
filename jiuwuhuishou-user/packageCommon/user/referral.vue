<template>
	<view class="app-page referral-page">
		<top-nav title="佣金明细"></top-nav>

		<scroll-view scroll-y class="page-body compact referral-body">
			<view class="referral-hero">
				<view class="referral-hero__label">佣金收益（元）</view>
				<view class="referral-hero__amount-row">
					<text class="referral-hero__amount">{{ pendingCommissionText }}</text>
					<view class="referral-hero__withdraw" @click="openWithdrawPopup">提现</view>
				</view>

				<view class="referral-hero__stats">
					<view class="referral-hero__stat-card">
						<view class="referral-hero__stat-label">本月贡献人数</view>
						<view class="referral-hero__stat-value">{{ overview.monthContributorCount }}</view>
					</view>
					<view class="referral-hero__stat-card">
						<view class="referral-hero__stat-label">累计邀请用户</view>
						<view class="referral-hero__stat-value">{{ overview.inviteCount }}</view>
					</view>
				</view>
			</view>

			<view class="referral-filter">
				<view
					v-for="tab in tabs"
					:key="tab.value"
					class="referral-filter__item"
					:class="{ 'is-active': activeTab === tab.value }"
					@click="switchTab(tab.value)"
				>
					{{ tab.label }}
				</view>
			</view>

			<view v-if="loading" class="referral-empty">加载中...</view>
			<view v-else class="referral-list">
				<view
					v-for="item in filteredLogs"
					:key="item.id"
					class="referral-item"
					:class="{ 'is-special': item.special }"
				>
					<view v-if="item.special" class="referral-item__special-bar"></view>

					<view class="referral-item__left">
						<view v-if="item.type === 'user'" class="referral-item__avatar" :class="item.avatarClass">
							<view class="referral-item__avatar-face"></view>
							<view class="referral-item__avatar-body"></view>
							<view v-if="item.level" class="referral-item__level">{{ item.level }}</view>
						</view>

						<view v-else class="referral-item__badge-icon" :class="item.badgeClass">
							<u-icon :name="item.icon" size="22" :color="item.iconColor"></u-icon>
						</view>

						<view class="referral-item__info">
							<view class="referral-item__title">{{ item.title }}</view>
							<view class="muted referral-item__desc">{{ item.desc }}</view>
						</view>
					</view>

					<view class="referral-item__right">
						<view class="referral-item__amount" :class="item.amountClass">{{ item.amount }}</view>
						<view class="referral-item__time">{{ item.time }}</view>
					</view>
				</view>
			</view>

			<view v-if="!loading && filteredLogs.length === 0" class="referral-empty">
				<u-icon name="file-text" size="72" color="#c8c1c1"></u-icon>
				<view class="referral-empty__text">当前分类下暂无佣金明细</view>
			</view>
		</scroll-view>

		<floating-service></floating-service>

		<u-popup :show="showWithdrawPopup" mode="center" :round="28" @close="closeWithdrawPopup">
			<view class="withdraw-popup">
				<view class="withdraw-popup__title">确定提现到钱包？</view>
				<view class="withdraw-popup__desc">可提现佣金：{{ pendingCommissionText }} 元</view>
				<view class="withdraw-popup__desc">最低提现：{{ withdrawMinAmount }} 元</view>
				<view v-if="withdrawPreviewText" class="withdraw-popup__desc">预计到账：{{ withdrawPreviewText }}</view>
				<input v-model="withdrawAmount" class="withdraw-popup__input" type="digit" placeholder="请输入提现金额" placeholder-class="withdraw-popup__placeholder" />
				<view class="withdraw-popup__btn withdraw-popup__btn--main" @click="confirmWithdraw">{{ withdrawing ? '提交中...' : '确认' }}</view>
				<view class="withdraw-popup__btn withdraw-popup__btn--ghost" @click="closeWithdrawPopup">取消</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import {
		fetchReferralCommissionLogs,
		fetchReferralOverview,
		filterCommissionLogs,
		formatCommissionAmount,
		withdrawCommissionToWallet
	} from '@/common/services/referral.js';

	const tabs = [
		{ label: '全部明细', value: 'all' },
		{ label: '添加', value: 'income' },
		{ label: '支出', value: 'expense' }
	];

	const activeTab = ref('all');
	const loading = ref(false);
	const overview = ref({
		pendingCommission: 0,
		monthContributorCount: 0,
		totalCommission: 0,
		inviteCount: 0
	});
	const logs = ref([]);
	const showWithdrawPopup = ref(false);
	const withdrawAmount = ref('');
	const withdrawing = ref(false);
	const withdrawMinAmount = 0.01;

	const pendingCommissionText = computed(() => formatCommissionAmount(overview.value.pendingCommission));
	const filteredLogs = computed(() => filterCommissionLogs(logs.value, activeTab.value));
	const withdrawPreviewText = computed(() => {
		const amount = Number(withdrawAmount.value);
		if (!withdrawAmount.value || Number.isNaN(amount) || amount <= 0) {
			return '';
		}
		return `￥${amount.toFixed(2)}`;
	});

	const loadOverview = async () => {
		overview.value = await fetchReferralOverview();
	};

	const loadLogs = async () => {
		logs.value = await fetchReferralCommissionLogs();
	};

	const loadPageData = async () => {
		loading.value = true;
		try {
			await Promise.all([loadOverview(), loadLogs()]);
		} catch (error) {
			overview.value = {
				pendingCommission: 0,
				monthContributorCount: 0,
				totalCommission: 0,
				inviteCount: 0
			};
			logs.value = [];
			uni.showToast({
				title: error.message || '加载佣金明细失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	const switchTab = (value) => {
		activeTab.value = value;
	};

	const openWithdrawPopup = () => {
		showWithdrawPopup.value = true;
	};

	const closeWithdrawPopup = () => {
		if (withdrawing.value) {
			return;
		}
		showWithdrawPopup.value = false;
		withdrawAmount.value = '';
	};

	const confirmWithdraw = async () => {
		if (withdrawing.value) {
			return;
		}
		const amount = Number(withdrawAmount.value);
		if (!withdrawAmount.value) {
			uni.showToast({ title: '请输入提现金额', icon: 'none' });
			return;
		}
		if (Number.isNaN(amount) || amount < withdrawMinAmount) {
			uni.showToast({ title: `提现金额不能低于${withdrawMinAmount}元`, icon: 'none' });
			return;
		}
		if (amount > Number(overview.value.pendingCommission || 0)) {
			uni.showToast({ title: '佣金余额不足', icon: 'none' });
			return;
		}
		withdrawing.value = true;
		try {
			await withdrawCommissionToWallet(amount);
			showWithdrawPopup.value = false;
			withdrawAmount.value = '';
			await loadPageData();
			uni.showToast({ title: '已提现到钱包', icon: 'success' });
		} catch (error) {
			uni.showToast({ title: error.message || '提现失败', icon: 'none' });
		} finally {
			withdrawing.value = false;
		}
	};

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.referral-page {
		background: linear-gradient(180deg, #fff8f5 0%, #fff 340rpx);
	}

	.referral-body {
		padding-top: 14rpx;
		padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
	}

	.referral-hero {
		background: linear-gradient(135deg, #f867b8 0%, #9a6df1 100%);
		border-radius: 28rpx;
		padding: 26rpx 26rpx 24rpx;
		box-shadow: 0 20rpx 44rpx rgba(180, 104, 194, 0.2);
	}

	.referral-hero__label {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.88);
	}

	.referral-hero__amount-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16rpx;
		margin-top: 16rpx;
	}

	.referral-hero__amount {
		font-size: 64rpx;
		font-weight: 800;
		line-height: 1;
		color: #fff;
		flex: 1;
		min-width: 0;
	}

	.referral-hero__withdraw {
		flex-shrink: 0;
		min-width: 132rpx;
		height: 72rpx;
		padding: 0 28rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		font-weight: 700;
		color: #8a6400;
		background: linear-gradient(180deg, #ffd11f 0%, #ffbe0f 100%);
		box-sizing: border-box;
	}

	.referral-hero__stats {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 18rpx;
		margin-top: 26rpx;
	}

	.referral-hero__stat-card {
		min-height: 118rpx;
		padding: 18rpx 20rpx;
		border-radius: 18rpx;
		background: rgba(255, 255, 255, 0.12);
		border: 2rpx solid rgba(255, 255, 255, 0.24);
		box-sizing: border-box;
	}

	.referral-hero__stat-label {
		font-size: 22rpx;
		color: rgba(255, 255, 255, 0.72);
	}

	.referral-hero__stat-value {
		margin-top: 18rpx;
		font-size: 44rpx;
		font-weight: 800;
		line-height: 1;
		color: #fff;
	}

	.referral-filter {
		display: flex;
		gap: 16rpx;
		margin-top: 24rpx;
	}

	.referral-filter__item {
		min-width: 136rpx;
		height: 56rpx;
		padding: 0 24rpx;
		border-radius: 999rpx;
		background: #f0ebeb;
		color: #7d6666;
		font-size: 24rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		box-sizing: border-box;
	}

	.referral-filter__item.is-active {
		background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%);
		color: #fff;
		box-shadow: 0 12rpx 26rpx rgba(207, 20, 50, 0.2);
	}

	.referral-list {
		margin-top: 22rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.referral-empty {
		margin-top: 80rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		color: #9a8888;
		font-size: 26rpx;
	}

	.referral-empty__text {
		margin-top: 18rpx;
	}

	.referral-item {
		position: relative;
		background: #fff;
		border-radius: 26rpx;
		padding: 22rpx 24rpx;
		box-shadow: 0 14rpx 32rpx rgba(111, 79, 79, 0.08);
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 18rpx;
		overflow: hidden;
	}

	.referral-item.is-special {
		padding-left: 28rpx;
	}

	.referral-item__special-bar {
		position: absolute;
		left: 0;
		top: 18rpx;
		bottom: 18rpx;
		width: 6rpx;
		border-radius: 0 999rpx 999rpx 0;
		background: #8a6400;
	}

	.referral-item__left {
		display: flex;
		align-items: center;
		gap: 16rpx;
		flex: 1;
		min-width: 0;
	}

	.referral-item__avatar {
		position: relative;
		width: 72rpx;
		height: 72rpx;
		border-radius: 50%;
		overflow: hidden;
		flex-shrink: 0;
		border: 4rpx solid #fff;
		box-shadow: 0 8rpx 20rpx rgba(123, 79, 79, 0.1);
	}

	.referral-item__avatar.is-avatar-warm {
		background: linear-gradient(135deg, #ffe2b6 0%, #f4c56f 100%);
	}

	.referral-item__avatar.is-avatar-pink {
		background: linear-gradient(135deg, #ffd9d9 0%, #f4a8b0 100%);
	}

	.referral-item__avatar.is-avatar-green {
		background: linear-gradient(135deg, #d9fff0 0%, #9fe2bf 100%);
	}

	.referral-item__avatar-face {
		position: absolute;
		left: 20rpx;
		top: 12rpx;
		width: 32rpx;
		height: 28rpx;
		border-radius: 14rpx;
		background: #fbe3d1;
	}

	.referral-item__avatar-body {
		position: absolute;
		left: 16rpx;
		bottom: 6rpx;
		width: 40rpx;
		height: 26rpx;
		border-radius: 20rpx 20rpx 12rpx 12rpx;
		background: rgba(106, 71, 71, 0.28);
	}

	.referral-item__level {
		position: absolute;
		right: -4rpx;
		bottom: -2rpx;
		min-width: 34rpx;
		height: 20rpx;
		padding: 0 8rpx;
		border-radius: 999rpx;
		background: linear-gradient(180deg, #a07e10 0%, #7d5d00 100%);
		color: #fff;
		font-size: 14rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.referral-item__badge-icon {
		width: 72rpx;
		height: 72rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.referral-item__badge-icon.is-badge-gold {
		background: #ffe6a2;
	}

	.referral-item__badge-icon.is-badge-pink {
		background: #ffe2e6;
	}

	.referral-item__info {
		flex: 1;
		min-width: 0;
	}

	.referral-item__title {
		font-size: 28rpx;
		font-weight: 800;
		color: #2f2424;
		line-height: 1.2;
	}

	.referral-item__desc {
		margin-top: 8rpx;
		font-size: 22rpx;
		color: #786363;
		line-height: 1.4;
	}

	.referral-item__right {
		text-align: right;
		flex-shrink: 0;
	}

	.referral-item__amount {
		font-size: 28rpx;
		font-weight: 800;
		line-height: 1.1;
	}

	.referral-item__amount.is-red {
		color: #d11f37;
	}

	.referral-item__amount.is-gold {
		color: #8a6400;
	}

	.referral-item__time {
		margin-top: 10rpx;
		font-size: 22rpx;
		color: #786363;
	}

	.withdraw-popup {
		width: 620rpx;
		padding: 40rpx 32rpx 30rpx;
		background: #fff;
		border-radius: 28rpx;
		box-sizing: border-box;
	}

	.withdraw-popup__title {
		font-size: 36rpx;
		font-weight: 800;
		text-align: center;
		color: #2f2424;
	}

	.withdraw-popup__desc {
		margin-top: 16rpx;
		font-size: 26rpx;
		text-align: center;
		color: #7a6161;
	}

	.withdraw-popup__input {
		width: 100%;
		height: 96rpx;
		margin-top: 28rpx;
		padding: 0 24rpx;
		border-radius: 24rpx;
		background: #fff7f7;
		border: 2rpx solid #f1dddd;
		box-sizing: border-box;
		font-size: 30rpx;
		color: #2f2424;
	}

	.withdraw-popup__placeholder {
		color: #b8a8a8;
	}

	.withdraw-popup__btn {
		height: 88rpx;
		margin-top: 20rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 30rpx;
		font-weight: 700;
	}

	.withdraw-popup__btn--main {
		background: linear-gradient(135deg, #d01231 0%, #b80826 100%);
		color: #fff;
		box-shadow: 0 16rpx 36rpx rgba(200, 16, 46, 0.22);
	}

	.withdraw-popup__btn--ghost {
		background: #f7f2f2;
		color: #5c4a4a;
	}
</style>
