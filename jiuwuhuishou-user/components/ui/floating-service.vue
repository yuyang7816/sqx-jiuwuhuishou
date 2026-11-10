<template>
	<view class="floating-service" @click="goService">
		<image class="floating-service__image" src="/static/floating-service-button.png" mode="aspectFit"></image>
	</view>
</template>

<script setup>
	const goService = () => {
		uni.navigateTo({
			url: '/packageCommon/service/chat'
		});
	};
</script>

<style lang="scss">
	.floating-service {
		position: fixed;
		right: 28rpx;
		bottom: calc(180rpx + env(safe-area-inset-bottom));
		z-index: 18;
		width: 108rpx;
		height: 108rpx;
	}

	.floating-service__image {
		width: 100%;
		height: 100%;
	}
</style>
