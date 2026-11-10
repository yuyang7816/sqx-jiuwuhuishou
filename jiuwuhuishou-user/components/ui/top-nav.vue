<template>
	<view class="top-nav" :style="wrapStyle">
		<view class="status-space" :style="{ height: `${statusBarHeight}px` }"></view>
		<view class="nav-row" :style="{ height: `${navHeight}px` }">
			<view class="nav-side left" @click="handleLeft">
				<u-icon v-if="showBack" name="arrow-left" :size="24" :color="iconColor"></u-icon>
				<view v-else class="nav-placeholder"></view>
			</view>
			<view class="nav-center">
				<text class="nav-title" :style="{ color: titleColor }">{{ title }}</text>
			</view>
			<view class="nav-side right">
				<view v-if="city" class="city-chip" @click="$emit('city')">
					<u-icon name="map" size="14" color="#5f4949"></u-icon>
					<text>{{ city }}</text>
				</view>
				<view v-else class="nav-placeholder"></view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { computed } from 'vue';

	const props = defineProps({
		title: {
			type: String,
			default: ''
		},
		showBack: {
			type: Boolean,
			default: true
		},
		city: {
			type: String,
			default: ''
		},
		transparent: {
			type: Boolean,
			default: false
		},
		titleColor: {
			type: String,
			default: '#c8102e'
		},
		iconColor: {
			type: String,
			default: '#3c2626'
		},
		/** 指定返回路径；tab 页用 switchTab，其它页用 reLaunch */
		backUrl: {
			type: String,
			default: ''
		}
	});

	defineEmits(['city']);

	const systemInfo = uni.getSystemInfoSync();
	const statusBarHeight = systemInfo.statusBarHeight || 20;
	const navHeight = 44;
	const TAB_PAGES = new Set([
		'/pages/index/index',
		'/pages/order/order',
		'/pages/welfare/welfare',
		'/pages/my/my'
	]);

	const wrapStyle = computed(() => ({
		background: props.transparent ? 'transparent' : 'rgba(255,255,255,0.96)'
	}));

	const goBackUrl = (url) => {
		if (TAB_PAGES.has(url)) {
			uni.switchTab({ url });
			return;
		}
		uni.reLaunch({ url });
	};

	const handleLeft = () => {
		if (!props.showBack) {
			return;
		}
		if (props.backUrl) {
			goBackUrl(props.backUrl);
			return;
		}
		uni.navigateBack({
			fail() {
				uni.switchTab({
					url: '/pages/index/index'
				});
			}
		});
	};
</script>

<style lang="scss">
	.top-nav {
		position: sticky;
		top: 0;
		left: 0;
		right: 0;
		z-index: 10;
	}

	.nav-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 24rpx;
		box-sizing: border-box;
	}

	.nav-side {
		width: 132rpx;
		display: flex;
		align-items: center;
	}

	.nav-side.right {
		justify-content: flex-end;
	}

	.nav-center {
		flex: 1;
		text-align: center;
	}

	.nav-title {
		font-size: 38rpx;
		font-weight: 800;
	}

	.city-chip {
		display: inline-flex;
		align-items: center;
		gap: 6rpx;
		padding: 10rpx 18rpx;
		border-radius: 999rpx;
		background: #f5f1f1;
		font-size: 24rpx;
		color: #5f4949;
	}

	.nav-placeholder {
		width: 24rpx;
		height: 24rpx;
	}
</style>
