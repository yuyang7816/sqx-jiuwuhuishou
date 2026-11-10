<template>
	<view class="app-page my-page">
		<top-nav title="我的个人主页" :showBack="false"></top-nav>
		<scroll-view scroll-y class="page-body compact my-body">
			<view class="my-hero">
				<view class="my-hero__glow"></view>
				<view class="my-user">
					<view class="my-avatar-wrap" @click="navTo('/packageCommon/user/profile')">
						<view class="my-avatar">
							<image v-if="profile.avatar" :src="profile.avatar" mode="aspectFill" class="my-avatar__img"></image>
							<template v-else>
								<view class="my-avatar__face"></view>
								<view class="my-avatar__eye my-avatar__eye--left"></view>
								<view class="my-avatar__eye my-avatar__eye--right"></view>
								<view class="my-avatar__body"></view>
							</template>
						</view>
						<view v-if="profile.isVip" class="my-avatar__badge" @click.stop="navTo('/packageCommon/user/vip')">VIP</view>
					</view>
					<view class="my-user__info" @click="navTo('/packageCommon/user/profile')">
						<view class="my-user__name">{{ profile.nickname || '未登录' }}</view>
						<view class="my-user__phone">{{ profile.maskedMobile || '登录后查看手机号' }}</view>
					</view>
				</view>
			</view>

			<view class="wallet-card" @click="navTo('/packageCommon/user/balance-detail')">
				<view class="row-between" style="align-items: flex-start;">
					<view>
						<view class="wallet-card__label">账户余额 (元)</view>
						<view class="wallet-card__amount">{{ walletSummary.balanceText }}</view>
					</view>
					<view class="wallet-card__btn">查看明细 <u-icon name="arrow-right" size="12" color="#6b4b00"></u-icon></view>
				</view>
				<view class="wallet-card__line"></view>
				<view class="wallet-card__stats">
					<view class="wallet-card__stat" @click.stop="navTo('/packageCommon/user/referral')">
						<view class="wallet-card__stat-label">预估待入账</view>
						<view class="wallet-card__stat-value">{{ walletSummary.pendingAmountText }}</view>
					</view>
					<view class="wallet-card__divider"></view>
					<view class="wallet-card__stat" @click.stop="switchTab('/pages/welfare/welfare')">
						<view class="wallet-card__stat-label">环保积分</view>
						<view class="wallet-card__stat-value">{{ walletSummary.ecoPointsText }}</view>
					</view>
				</view>
			</view>

			<view class="my-section card-shell">
				<view class="my-section__head row-between">
					<text class="my-section__title">我的订单</text>
					<text class="my-section__link" @click="goOrderTab('全部')">查看全部 ></text>
				</view>
				<view class="my-order-grid">
					<view v-for="entry in orderEntries" :key="entry.title" class="my-order-grid__item" @click="goOrderTab(entry.title)">
						<view class="my-order-grid__icon-wrap">
							<view v-if="entry.dot" class="my-order-grid__dot"></view>
							<u-icon :name="entry.icon" size="22" :color="entry.color"></u-icon>
						</view>
						<text class="my-order-grid__text">{{ entry.title }}</text>
					</view>
				</view>
			</view>

			<view class="my-menu card-shell">
				<view v-for="item in menus" :key="item.title" class="my-menu__item" @click="item.action">
					<view class="my-menu__left">
						<view class="my-menu__icon" :class="item.iconClass">
							<u-icon :name="item.icon" size="18" :color="item.iconColor"></u-icon>
						</view>
						<text class="my-menu__text">{{ item.title }}</text>
					</view>
					<u-icon name="arrow-right" size="16" color="#b2a0a0"></u-icon>
				</view>
			</view>

			<view v-if="isEmployee" class="my-menu card-shell my-menu--employee">
				<view class="my-menu__item my-menu__item--pickup" @click="navTo('/packageCommon/pickup/list')">
					<view class="my-menu__left">
						<view class="my-menu__icon is-red-badge">
							<u-icon name="car" size="18" color="#cf1432"></u-icon>
						</view>
						<view>
							<text class="my-menu__text">我的取件</text>
							<view class="my-menu__sub">查看今日待取件、进行中和已完成取件任务</view>
						</view>
					</view>
					<view class="my-menu__right-tag">员工</view>
				</view>
			</view>

			<view class="my-version">版本号 4.1.0-release</view>
		</scroll-view>

		<floating-service></floating-service>
	</view>
</template>


<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn, fetchProfile } from '@/common/services/auth.js';
	import { DEFAULT_WALLET_SUMMARY, loadMyWalletSummary } from '@/common/services/my.js';

	const profile = ref({
		nickname: '',
		maskedMobile: '',
		avatar: '',
		isVip: false,
		userType: 1
	});
	const walletSummary = ref(DEFAULT_WALLET_SUMMARY);

	const isEmployee = computed(() => profile.value.userType === 3);

	const loadProfile = async () => {
		if (!ensureLoggedIn()) {
			return;
		}
		try {
			profile.value = await fetchProfile();
		} catch (error) {
			uni.showToast({ title: error.message || '加载用户信息失败', icon: 'none' });
		}
	};

	const loadWalletSummary = async () => {
		if (!ensureLoggedIn()) {
			walletSummary.value = DEFAULT_WALLET_SUMMARY;
			return;
		}
		try {
			walletSummary.value = await loadMyWalletSummary();
		} catch (error) {
			walletSummary.value = DEFAULT_WALLET_SUMMARY;
			uni.showToast({ title: error.message || '加载账户数据失败', icon: 'none' });
		}
	};

	const loadPageData = async () => {
		await Promise.all([loadProfile(), loadWalletSummary()]);
	};

	onShow(() => {
		loadPageData();
	});

	const orderEntries = [
		{ title: '全部', icon: 'file-text', color: '#776565' },
		{ title: '进行中', icon: 'car', color: '#d42035', dot: true },
		{ title: '待确认', icon: 'hourglass', color: '#c48a1a' },
		{ title: '已完成', icon: 'checkmark-circle', color: '#776565' }
	];

	const navTo = (url) => uni.navigateTo({ url });
	const switchTab = (url) => uni.switchTab({ url });
	const goOrderTab = (tab) => {
		try {
			uni.setStorageSync('orderListTab', tab || '全部');
		} catch (error) {
			// ignore storage errors
		}
		switchTab('/pages/order/order');
	};

	const menus = [
		{ title: '我的推荐', icon: 'share-square', iconClass: 'is-gold', iconColor: '#9e7b0b', action: () => navTo('/packageCommon/user/invite') },
		{ title: '客服中心', icon: 'kefu-ermai', iconClass: 'is-yellow', iconColor: '#8b6900', action: () => navTo('/packageCommon/service/chat') },
		{ title: '常用地址', icon: 'map', iconClass: 'is-light', iconColor: '#8b6900', action: () => navTo('/packageCommon/user/address') },
		{ title: '个人设置', icon: 'setting', iconClass: 'is-gold', iconColor: '#8b6900', action: () => navTo('/packageCommon/user/settings') },
		{ title: '关于我们', icon: 'info-circle', iconClass: 'is-yellow', iconColor: '#8b6900', action: () => navTo('/packageCommon/user/agreement') }
	];
</script>

<style lang="scss">
	.my-page {
		background: linear-gradient(180deg, #fff7f5 0%, #fff 360rpx);
	}

	.my-body {
		padding-top: 14rpx;
		padding-bottom: calc(170rpx + env(safe-area-inset-bottom));
	}

	.my-hero {
		position: relative;
		padding-top: 4rpx;
	}

	.my-hero__glow {
		position: absolute;
		left: -24rpx;
		top: -18rpx;
		width: 420rpx;
		height: 220rpx;
		border-radius: 50%;
		background: radial-gradient(circle, rgba(255, 231, 224, 0.92) 0%, rgba(255, 231, 224, 0.2) 58%, rgba(255, 231, 224, 0) 80%);
	}

	.my-user {
		position: relative;
		z-index: 2;
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.my-avatar-wrap {
		position: relative;
		width: 110rpx;
		height: 110rpx;
		flex-shrink: 0;
	}

	.my-avatar {
		width: 110rpx;
		height: 110rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #ffe1d8 0%, #ffcfc8 100%);
		border: 6rpx solid #fff;
		box-shadow: 0 12rpx 28rpx rgba(123, 79, 79, 0.1);
		position: relative;
		overflow: hidden;
	}

	.my-avatar__img {
		width: 100%;
		height: 100%;
	}

	.my-avatar__face {
		position: absolute;
		left: 30rpx;
		top: 22rpx;
		width: 50rpx;
		height: 46rpx;
		border-radius: 18rpx;
		background: linear-gradient(180deg, #ffb69e 0%, #f59a80 100%);
		border: 4rpx solid #fff2ed;
	}

	.my-avatar__eye {
		position: absolute;
		top: 40rpx;
		width: 8rpx;
		height: 8rpx;
		border-radius: 50%;
		background: #4a2f2f;
	}

	.my-avatar__eye--left {
		left: 46rpx;
	}

	.my-avatar__eye--right {
		left: 62rpx;
	}

	.my-avatar__body {
		position: absolute;
		left: 38rpx;
		bottom: 12rpx;
		width: 34rpx;
		height: 30rpx;
		border-radius: 16rpx 16rpx 12rpx 12rpx;
		background: linear-gradient(180deg, #f9a684 0%, #ef7e63 100%);
	}

	.my-avatar__badge {
		position: absolute;
		right: -2rpx;
		bottom: -2rpx;
		min-width: 40rpx;
		height: 30rpx;
		padding: 0 8rpx;
		border-radius: 999rpx;
		background: linear-gradient(180deg, #dc1835 0%, #bc0e2b 100%);
		color: #fff;
		font-size: 18rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 8rpx 18rpx rgba(188, 14, 43, 0.22);
	}

	.my-user__info {
		flex: 1;
	}

	.my-user__name {
		font-size: 30rpx;
		font-weight: 700;
		color: #322727;
	}

	.my-user__phone {
		margin-top: 12rpx;
		font-size: 30rpx;
		color: #5d4949;
	}

	.wallet-card {
		margin-top: 24rpx;
		background: linear-gradient(180deg, #ffc812 0%, #f5b400 100%);
		border-radius: 26rpx;
		padding: 24rpx 24rpx 20rpx;
		box-shadow: 0 22rpx 42rpx rgba(255, 191, 31, 0.22);
	}

	.wallet-card__label {
		font-size: 22rpx;
		color: #6f4c00;
	}

	.wallet-card__amount {
		margin-top: 12rpx;
		font-size: 38rpx;
		font-weight: 800;
		color: #3b2a00;
	}

	.wallet-card__btn {
		height: 54rpx;
		padding: 0 18rpx;
		border-radius: 999rpx;
		background: rgba(255, 243, 201, 0.74);
		display: inline-flex;
		align-items: center;
		gap: 8rpx;
		font-size: 24rpx;
		color: #6b4b00;
		font-weight: 600;
	}

	.wallet-card__line {
		height: 2rpx;
		background: rgba(103, 76, 0, 0.16);
		margin: 18rpx 0;
	}

	.wallet-card__stats {
		display: flex;
		align-items: center;
	}

	.wallet-card__stat {
		flex: 1;
	}

	.wallet-card__divider {
		width: 2rpx;
		height: 72rpx;
		background: rgba(103, 76, 0, 0.16);
	}

	.wallet-card__stat-label {
		font-size: 22rpx;
		color: #7a5b00;
	}

	.wallet-card__stat-value {
		margin-top: 10rpx;
		font-size: 34rpx;
		font-weight: 800;
		color: #3b2a00;
	}

	.card-shell {
		margin-top: 24rpx;
		background: #fff;
		border-radius: 28rpx;
		padding: 24rpx;
		box-shadow: 0 14rpx 34rpx rgba(112, 79, 79, 0.07);
	}

	.my-section__title {
		font-size: 32rpx;
		font-weight: 700;
		color: #352929;
	}

	.my-section__link {
		font-size: 24rpx;
		color: #8f7d7d;
	}

	.my-order-grid {
		margin-top: 24rpx;
		display: grid;
		grid-template-columns: repeat(4, minmax(0, 1fr));
		gap: 12rpx;
	}

	.my-order-grid__item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.my-order-grid__icon-wrap {
		width: 72rpx;
		height: 72rpx;
		border-radius: 50%;
		background: #f6f1f1;
		display: flex;
		align-items: center;
		justify-content: center;
		position: relative;
	}

	.my-order-grid__dot {
		position: absolute;
		right: 6rpx;
		top: 6rpx;
		width: 12rpx;
		height: 12rpx;
		border-radius: 50%;
		background: #e22339;
		box-shadow: 0 0 0 3rpx #fff;
	}

	.my-order-grid__text {
		margin-top: 14rpx;
		font-size: 24rpx;
		color: #504040;
	}

	.my-order-grid__count {
		margin-top: 8rpx;
		font-size: 22rpx;
		font-weight: 700;
		color: #cf1432;
	}

	.my-menu {
		padding: 0;
		overflow: hidden;
	}

	.my-menu--employee {
		margin-top: 24rpx;
		padding: 0;
	}

	.my-menu__item {
		height: 94rpx;
		padding: 0 24rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		border-bottom: 2rpx solid #f2e9e9;
		box-sizing: border-box;
	}

	.my-menu__item:last-child {
		border-bottom: 0;
	}

	.my-menu__item--pickup {
		height: auto;
		padding-top: 24rpx;
		padding-bottom: 24rpx;
		align-items: flex-start;
	}

	.my-menu__left {
		display: flex;
		align-items: center;
		gap: 18rpx;
	}

	.my-menu__icon {
		width: 32rpx;
		height: 32rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.my-menu__text {
		font-size: 30rpx;
		color: #3f3131;
	}

	.my-menu__sub {
		margin-top: 10rpx;
		font-size: 22rpx;
		line-height: 1.6;
		color: #8d7d7d;
	}

	.my-menu__right-tag {
		min-width: 78rpx;
		height: 42rpx;
		padding: 0 14rpx;
		border-radius: 999rpx;
		background: #fff1f3;
		color: #cf1432;
		font-size: 22rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		box-sizing: border-box;
	}

	.my-version {
		text-align: center;
		margin-top: 42rpx;
		font-size: 22rpx;
		color: #c0b1b1;
	}

	.is-gold {
		color: #9e7b0b;
	}

	.is-yellow {
		color: #8b6900;
	}

	.is-light {
		color: #8b6900;
	}

	.is-red-badge {
		color: #cf1432;
	}
</style>
