<template>
	<view class="app-page home-page">
		<view class="home-nav" :style="navStyle">
			<view class="home-nav__brand">
				<view class="home-nav__logo">
					<image class="home-nav__logo-image" src="/static/home-logo.png" mode="aspectFit"></image>
				</view>
				<text class="home-nav__title">旧物回收</text>
			</view>
		</view>

		<scroll-view scroll-y class="page-body home-body">
			<view v-if="showRemindBar" class="remind-bar">
				<view class="remind-bar__left">
					<view class="remind-bar__coin">
						<u-icon name="coupon-fill" size="18" color="#8a6400"></u-icon>
					</view>
					<text class="remind-bar__text">{{ pushRemindText }}</text>
				</view>
				<view class="remind-bar__actions">
					<view class="remind-bar__btn" :class="{ 'is-active': remindEnabled }" @click="toggleRemind">
						<text>{{ remindEnabled ? '已开启' : '立即开启' }}</text>
						<u-icon v-if="remindEnabled" name="checkmark-circle-fill" size="16" color="#6f5800"></u-icon>
					</view>
					<view class="remind-bar__close" @click="showRemindBar = false">
						<u-icon name="close" size="12" color="#ffffff"></u-icon>
					</view>
				</view>
			</view>

			<swiper class="hero-swiper" :current="currentBanner" circular autoplay :interval="3500" :duration="500" next-margin="72rpx" @change="handleBannerChange">
				<swiper-item v-for="item in banners" :key="item.id || item.title">
					<view class="hero-slide">
						<view class="hero-card hero-card--main" :class="`hero-card--${item.theme}`" @click="go(item.url)">
							<image v-if="item.imageUrl" :src="item.imageUrl" mode="aspectFill" class="hero-card__image"></image>
							<template v-else>
								<view class="hero-card__glow"></view>
								<view class="hero-card__content">
									<view class="hero-card__title" :class="item.theme === 'gold' ? 'hero-card__title--gold' : ''">{{ item.title }}</view>
									<view class="hero-card__desc" :class="item.theme === 'gold' ? 'hero-card__desc--gold' : ''">{{ item.desc }}</view>
									<view class="hero-card__cta" :class="item.theme === 'gold' ? 'hero-card__cta--gold' : ''">{{ item.cta }}</view>
								</view>

								<view v-if="item.art === 'cloth'" class="hero-card__art hero-card__art--cloth">
									<view class="bin-body"></view>
									<view class="bin-top"></view>
									<view class="cloth-stack"></view>
									<view class="leaf leaf-a"></view>
									<view class="leaf leaf-b"></view>
								</view>

								<view v-else-if="item.art === 'coin'" class="hero-card__art hero-card__art--coin">
									<view class="coin-bg"></view>
									<view class="coin-circle"></view>
									<view class="coin-dot coin-dot-a"></view>
									<view class="coin-dot coin-dot-b"></view>
								</view>

								<view v-else class="hero-card__art hero-card__art--device">
									<view class="device-body"></view>
									<view class="device-screen"></view>
									<view class="device-shadow"></view>
								</view>
							</template>
						</view>
					</view>
				</swiper-item>
			</swiper>

			<view class="hero-dots">
				<view v-for="(item, index) in banners" :key="item.id || item.title" class="hero-dots__item" :class="{ 'hero-dots__active': currentBanner === index }"></view>
			</view>

			<view class="notice-bar">
				<u-icon name="volume" size="18" color="#d11f37"></u-icon>
				<swiper class="notice-swiper" vertical autoplay circular :interval="2600" :duration="400">
					<swiper-item v-for="item in notices" :key="item.id || item.text">
						<view class="notice-bar__item" @click="item.url && go(item.url)">
							<text class="notice-bar__text">{{ item.text }}</text>
						</view>
					</swiper-item>
				</swiper>
			</view>

			<view class="category-grid">
				<view v-for="item in categories" :key="item.id || item.title" class="category-item" @click="go(item.url)">
					<view class="category-item__icon" :class="item.iconClass">
						<image v-if="isImageIcon(item.icon)" :src="item.icon" mode="aspectFill" class="category-item__img"></image>
						<u-icon v-else :name="item.icon" size="22" color="#c8102e"></u-icon>
					</view>
					<text class="category-item__text">{{ item.title }}</text>
				</view>
			</view>

			<view class="invite-banner" @click="go(inviteBanner.url)">
				<view>
					<view class="invite-banner__title">{{ inviteBanner.title }}</view>
					<view class="invite-banner__desc">{{ inviteBanner.desc }}</view>
				</view>
				<view class="invite-banner__btn">{{ inviteBanner.btnText }}</view>
				<view class="invite-banner__shape"></view>
			</view>

			<view class="task-grid">
				<view v-for="item in tasks" :key="item.id || item.title" class="task-card" @click="handleTaskClick(item)">
					<view class="task-card__icon" :class="item.iconClass">
						<image v-if="item.imageUrl" :src="item.imageUrl" mode="aspectFill" class="task-card__img"></image>
						<u-icon v-else :name="item.icon" size="22" :color="item.iconColor"></u-icon>
					</view>
					<view v-if="item.badge" class="task-card__badge">{{ item.badge }}</view>
					<view class="task-card__title">{{ item.title }}</view>
					<view class="task-card__desc">{{ item.desc }}</view>
				</view>
			</view>
		</scroll-view>

		<view class="home-service-entry" @click="go('/packageCommon/service/chat')">
			<image class="home-service-entry__image" src="/static/floating-service-button.png" mode="aspectFit"></image>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import { createHomeModuleAction, loadHomePageData } from '@/common/services/home.js';
	import { captureInvitationCode } from '@/common/services/inviteCode.js';

	const statusBarHeight = uni.getSystemInfoSync().statusBarHeight || 20;
	const navStyle = {
		paddingTop: `${statusBarHeight + 12}px`
	};

	const remindEnabled = ref(false);
	const showRemindBar = ref(false);
	const pushRemindText = ref('开启推送提醒，回收进度实时看');
	const currentBanner = ref(0);
	const banners = ref([]);
	const notices = ref([]);
	const categories = ref([]);
	const inviteBanner = ref({
		title: '',
		desc: '',
		btnText: '立即赚钱',
		url: '/packageCommon/user/referral-rules'
	});
	const tasks = ref([]);

	const go = (url) => {
		if (!url) {
			return;
		}
		if (url.startsWith('/pages/')) {
			uni.switchTab({ url });
			return;
		}
		uni.navigateTo({ url });
	};

	const isImageIcon = (icon) => /^https?:\/\//i.test(icon) || icon.startsWith('/');

	const handleTaskClick = (item) => {
		createHomeModuleAction(item, go)();
	};

	const toggleRemind = () => {
		remindEnabled.value = !remindEnabled.value;
		uni.setStorageSync('pushRemindEnabled', remindEnabled.value ? '1' : '0');
	};

	const handleBannerChange = (event) => {
		currentBanner.value = event.detail.current;
	};

	const loadHomeData = async () => {
		try {
			const data = await loadHomePageData();
			showRemindBar.value = data.pushRemind.show;
			pushRemindText.value = data.pushRemind.text;
			remindEnabled.value = uni.getStorageSync('pushRemindEnabled') === '1';
			banners.value = data.banners;
			notices.value = data.notices;
			categories.value = data.categories;
			inviteBanner.value = data.inviteBanner;
			tasks.value = data.modules;
		} catch (error) {
			uni.showToast({ title: error.message || '首页数据加载失败', icon: 'none' });
		}
	};

	onLoad((options) => {
		captureInvitationCode(options || {});
	});

	onShow(() => {
		loadHomeData();
	});
</script>

<style lang="scss">
	.home-page {
		height: 100vh;
		display: flex;
		flex-direction: column;
		overflow: hidden;
		background: linear-gradient(180deg, #fff7ef 0%, #fffaf3 220rpx, #fff 100%);
		box-sizing: border-box;
	}

	.home-nav {
		position: relative;
		z-index: 20;
		flex-shrink: 0;
		padding-left: 30rpx;
		padding-right: 30rpx;
		padding-bottom: 14rpx;
		display: flex;
		align-items: center;
		justify-content: flex-start;
		background: linear-gradient(180deg, #fff7ef 0%, #fffaf3 100%);
		box-sizing: border-box;
	}

	.home-nav__brand {
		display: flex;
		align-items: center;
		gap: 24rpx;
	}

	.home-nav__logo {
		width: 48rpx;
		height: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 10rpx;
	}

	.home-nav__logo-image {
		width: 100%;
		height: 100%;
	}

	.home-nav__title {
		font-size: 34rpx;
		font-weight: 800;
		color: #c8102e;
	}

	.home-body {
		flex: 1;
		height: 0;
		padding-top: 8rpx;
		padding-bottom: calc(150rpx + env(safe-area-inset-bottom));
		box-sizing: border-box;
	}

	.home-service-entry {
		position: fixed;
		right: 28rpx;
		bottom: calc(180rpx + env(safe-area-inset-bottom));
		z-index: 18;
		width: 108rpx;
		height: 108rpx;
	}

	.home-service-entry__image {
		width: 100%;
		height: 100%;
	}

	.remind-bar {
		height: 78rpx;
		background: linear-gradient(180deg, #ffc817 0%, #f8b800 100%);
		border-radius: 24rpx;
		padding: 0 20rpx 0 16rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		box-sizing: border-box;
	}

	.remind-bar__left {
		display: flex;
		align-items: center;
		min-width: 0;
		flex: 1;
	}

	.remind-bar__coin {
		width: 42rpx;
		height: 42rpx;
		border-radius: 50%;
		background: rgba(113, 77, 0, 0.16);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 12rpx;
	}

	.remind-bar__text {
		font-size: 25rpx;
		font-weight: 500;
		color: #5d3a00;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.remind-bar__btn {
		height: 44rpx;
		padding: 0 16rpx;
		border-radius: 999rpx;
		background: rgba(255, 255, 255, 0.36);
		display: flex;
		align-items: center;
		gap: 8rpx;
		font-size: 24rpx;
		color: #8a6400;
		flex-shrink: 0;
	}

	.remind-bar__btn.is-active {
		background: rgba(111, 88, 0, 0.14);
		font-weight: 700;
	}

	.remind-bar__actions {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.remind-bar__close {
		width: 40rpx;
		height: 40rpx;
		border-radius: 999rpx;
		background: #6f5800;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.hero-swiper {
		margin-top: 24rpx;
		width: 100%;
		height: 268rpx;
	}

	.hero-slide {
		width: 586rpx;
		height: 268rpx;
		padding-right: 2rpx;
		box-sizing: border-box;
	}

	.hero-card {
		width: 586rpx;
		height: 268rpx;
		border-radius: 32rpx;
		position: relative;
		overflow: hidden;
		box-shadow: 0 18rpx 40rpx rgba(146, 73, 46, 0.12);
		background: linear-gradient(135deg, #d33c31 0%, #b6181f 100%);
		flex-shrink: 0;
	}

	.hero-card--main {
		width: 586rpx;
	}

	.hero-card__image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.hero-card--cloth {
		background: linear-gradient(135deg, #d53b31 0%, #c11e22 58%, #a90f19 100%);
	}

	.hero-card--gold {
		background: linear-gradient(135deg, #d5a946 0%, #c89118 100%);
	}

	.hero-card--digital {
		background: linear-gradient(135deg, #f69273 0%, #ef5241 52%, #c81c35 100%);
	}

	.hero-card__glow {
		position: absolute;
		right: 34rpx;
		top: 12rpx;
		width: 280rpx;
		height: 280rpx;
		border-radius: 50%;
		background: radial-gradient(circle, rgba(255, 234, 205, 0.98) 0%, rgba(255, 197, 134, 0.94) 40%, rgba(255, 197, 134, 0) 72%);
	}

	.hero-card--gold .hero-card__glow {
		right: 6rpx;
		top: 36rpx;
		width: 176rpx;
		height: 176rpx;
		background: radial-gradient(circle, rgba(255, 233, 174, 0.82) 0%, rgba(255, 215, 120, 0.52) 46%, rgba(255, 215, 120, 0) 76%);
	}

	.hero-card--digital .hero-card__glow {
		right: 46rpx;
		top: 18rpx;
		background: radial-gradient(circle, rgba(255, 231, 219, 0.98) 0%, rgba(255, 187, 145, 0.9) 42%, rgba(255, 187, 145, 0) 72%);
	}

	.hero-card__content {
		position: absolute;
		left: 30rpx;
		top: 36rpx;
		z-index: 2;
		max-width: 248rpx;
	}

	.hero-card__title {
		font-size: 48rpx;
		font-weight: 800;
		color: #5b2015;
		line-height: 1.1;
	}

	.hero-card__title--gold {
		font-size: 40rpx;
		color: #4e2f00;
	}

	.hero-card__desc {
		margin-top: 10rpx;
		font-size: 24rpx;
		color: rgba(91, 32, 21, 0.82);
	}

	.hero-card__desc--gold {
		color: rgba(78, 47, 0, 0.76);
		line-height: 1.5;
	}

	.hero-card__cta {
		margin-top: 26rpx;
		display: inline-flex;
		align-items: center;
		justify-content: center;
		height: 56rpx;
		padding: 0 26rpx;
		border-radius: 999rpx;
		font-size: 24rpx;
		font-weight: 700;
		color: #fff;
		background: linear-gradient(180deg, #cb1430 0%, #b60e28 100%);
	}

	.hero-card__cta--gold {
		background: rgba(111, 88, 0, 0.5);
	}

	.hero-card__art {
		position: absolute;
		right: 0;
		top: 0;
		bottom: 0;
	}

	.hero-card__art--cloth {
		width: 332rpx;
	}

	.hero-card__art--cloth::before {
		content: '';
		position: absolute;
		width: 262rpx;
		height: 262rpx;
		border-radius: 50%;
		right: 30rpx;
		top: 16rpx;
		background: radial-gradient(circle, #ffd8b1 0%, #ffc47d 42%, rgba(255, 196, 125, 0) 72%);
	}

	.bin-body {
		position: absolute;
		right: 98rpx;
		top: 38rpx;
		width: 112rpx;
		height: 132rpx;
		border-radius: 18rpx 18rpx 24rpx 24rpx;
		background: linear-gradient(180deg, #d6523e 0%, #ba281f 100%);
		box-shadow: 0 20rpx 36rpx rgba(96, 19, 15, 0.28);
	}

	.bin-top {
		position: absolute;
		right: 90rpx;
		top: 24rpx;
		width: 126rpx;
		height: 28rpx;
		border-radius: 14rpx 14rpx 8rpx 8rpx;
		background: #d45d4b;
	}

	.cloth-stack {
		position: absolute;
		right: 58rpx;
		bottom: 28rpx;
		width: 196rpx;
		height: 48rpx;
		border-radius: 26rpx;
		background: linear-gradient(180deg, #d8b09c 0%, #c58a76 100%);
	}

	.leaf {
		position: absolute;
		width: 26rpx;
		height: 16rpx;
		background: #84b544;
		border-radius: 100% 0;
		transform: rotate(-24deg);
	}

	.leaf-a {
		right: 44rpx;
		top: 88rpx;
	}

	.leaf-b {
		right: 236rpx;
		top: 146rpx;
		transform: rotate(24deg);
	}

	.hero-card__art--coin::before {
		content: '';
		position: absolute;
		width: 158rpx;
		height: 158rpx;
		border-radius: 50%;
		right: 8rpx;
		bottom: 14rpx;
		background: rgba(255, 223, 133, 0.28);
	}

	.coin-bg {
		position: absolute;
		right: 24rpx;
		top: 44rpx;
		width: 96rpx;
		height: 126rpx;
		border-radius: 24rpx;
		background: rgba(255, 255, 255, 0.12);
	}

	.coin-circle {
		position: absolute;
		right: 48rpx;
		bottom: 48rpx;
		width: 48rpx;
		height: 48rpx;
		border-radius: 50%;
		background: linear-gradient(180deg, #8d7405 0%, #6f5800 100%);
	}

	.coin-dot {
		position: absolute;
		border-radius: 50%;
		background: rgba(111, 88, 0, 0.72);
	}

	.coin-dot-a {
		width: 16rpx;
		height: 16rpx;
		right: 32rpx;
		top: 88rpx;
	}

	.coin-dot-b {
		width: 12rpx;
		height: 12rpx;
		right: 82rpx;
		top: 124rpx;
	}

	.hero-card__art--device::before {
		content: '';
		position: absolute;
		width: 246rpx;
		height: 246rpx;
		border-radius: 50%;
		right: 28rpx;
		top: 18rpx;
		background: radial-gradient(circle, #ffe1ca 0%, #ffb287 44%, rgba(255, 178, 135, 0) 72%);
	}

	.device-body {
		position: absolute;
		right: 100rpx;
		top: 48rpx;
		width: 118rpx;
		height: 156rpx;
		border-radius: 24rpx;
		background: linear-gradient(180deg, #38404d 0%, #1f2430 100%);
		box-shadow: 0 20rpx 36rpx rgba(41, 34, 46, 0.26);
		transform: rotate(10deg);
	}

	.device-screen {
		position: absolute;
		right: 112rpx;
		top: 62rpx;
		width: 92rpx;
		height: 124rpx;
		border-radius: 18rpx;
		background: linear-gradient(180deg, #a6dff3 0%, #5fb3cf 100%);
		transform: rotate(10deg);
	}

	.device-shadow {
		position: absolute;
		right: 54rpx;
		bottom: 30rpx;
		width: 184rpx;
		height: 40rpx;
		border-radius: 50%;
		background: rgba(94, 39, 19, 0.16);
	}

	.hero-dots {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
		margin-top: 20rpx;
	}

	.hero-dots__item {
		width: 10rpx;
		height: 10rpx;
		border-radius: 50%;
		background: #f2cdd3;
		transition: all 0.25s ease;
	}

	.hero-dots__item.hero-dots__active {
		width: 30rpx;
		border-radius: 999rpx;
		background: #cc1832;
	}

	.notice-bar {
		margin-top: 22rpx;
		height: 74rpx;
		border-radius: 999rpx;
		background: #ffffff;
		box-shadow: 0 14rpx 30rpx rgba(143, 92, 57, 0.08);
		padding: 0 22rpx;
		display: flex;
		align-items: center;
		box-sizing: border-box;
		gap: 12rpx;
	}

	.notice-swiper {
		flex: 1;
		height: 40rpx;
	}

	.notice-bar__item {
		height: 40rpx;
		display: flex;
		align-items: center;
	}

	.notice-bar__text {
		font-size: 25rpx;
		color: #675151;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.category-grid {
		margin-top: 28rpx;
		display: grid;
		grid-template-columns: repeat(5, minmax(0, 1fr));
		gap: 20rpx 14rpx;
	}

	.category-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.category-item__icon {
		width: 86rpx;
		height: 86rpx;
		border-radius: 26rpx;
		background: #ffffff;
		box-shadow: 0 12rpx 28rpx rgba(143, 92, 57, 0.08);
		display: flex;
		align-items: center;
		justify-content: center;
		overflow: hidden;
	}

	.category-item__img {
		width: 100%;
		height: 100%;
	}

	.category-item__text {
		margin-top: 16rpx;
		font-size: 26rpx;
		color: #433535;
		line-height: 1.2;
		text-align: center;
	}

	.invite-banner {
		margin-top: 28rpx;
		background: linear-gradient(135deg, #ff6f59 0%, #ef2c37 55%, #ca132d 100%);
		border-radius: 28rpx;
		padding: 26rpx 28rpx;
		position: relative;
		overflow: hidden;
		display: flex;
		align-items: center;
		justify-content: space-between;
		box-shadow: 0 18rpx 36rpx rgba(224, 68, 70, 0.2);
	}

	.invite-banner__title {
		font-size: 36rpx;
		font-weight: 800;
		color: #ffffff;
	}

	.invite-banner__desc {
		margin-top: 10rpx;
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.88);
	}

	.invite-banner__btn {
		position: relative;
		z-index: 2;
		width: 178rpx;
		height: 62rpx;
		border-radius: 999rpx;
		background: #ffffff;
		color: #d11f37;
		font-size: 28rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.invite-banner__shape {
		position: absolute;
		right: -10rpx;
		bottom: -16rpx;
		width: 150rpx;
		height: 150rpx;
		border-radius: 32rpx;
		background: rgba(255, 255, 255, 0.12);
		transform: rotate(-12deg);
	}

	.task-grid {
		margin-top: 26rpx;
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 20rpx;
	}

	.task-card {
		min-height: 182rpx;
		background: #ffffff;
		border-radius: 30rpx;
		padding: 24rpx;
		box-sizing: border-box;
		box-shadow: 0 16rpx 34rpx rgba(143, 92, 57, 0.08);
		position: relative;
	}

	.task-card__icon {
		width: 70rpx;
		height: 70rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		overflow: hidden;
	}

	.task-card__img {
		width: 100%;
		height: 100%;
	}

	.task-card__badge {
		position: absolute;
		right: 24rpx;
		top: 20rpx;
		min-width: 50rpx;
		height: 28rpx;
		padding: 0 10rpx;
		border-radius: 999rpx;
		background: #fff1e3;
		color: #ff7a00;
		font-size: 18rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		box-sizing: border-box;
	}

	.task-card__title {
		margin-top: 22rpx;
		font-size: 16px;
		font-weight: 700;
		color: #2f2525;
	}

	.task-card__desc {
		margin-top: 14rpx;
		font-size: 26rpx;
		color: #6f6060;
		line-height: 1.4;
	}

	.is-red {
		background: linear-gradient(180deg, #fff1f2 0%, #ffffff 100%);
	}

	.is-rose {
		background: linear-gradient(180deg, #fff4f6 0%, #ffffff 100%);
	}

	.is-pink {
		background: linear-gradient(180deg, #fff5f7 0%, #ffffff 100%);
	}

	.is-gold {
		background: #fff2df;
	}

	.is-soft-red {
		background: #ffecee;
	}

	.is-blue {
		background: #eaf2ff;
	}

	.is-green {
		background: #e8fbef;
	}
</style>
