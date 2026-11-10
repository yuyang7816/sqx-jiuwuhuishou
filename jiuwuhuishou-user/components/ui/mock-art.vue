<template>
	<view class="mock-art" :class="`theme-${theme}`">
		<view class="glow"></view>
		<view class="cube cube-a"></view>
		<view class="cube cube-b"></view>
		<view class="copy">
			<text class="label">{{ label }}</text>
			<text v-if="desc" class="desc">{{ desc }}</text>
		</view>
	</view>
</template>

<script setup>
	defineProps({
		label: {
			type: String,
			default: '环保插画'
		},
		desc: {
			type: String,
			default: ''
		},
		theme: {
			type: String,
			default: 'mint'
		}
	});
</script>

<style lang="scss">
	.mock-art {
		position: relative;
		height: 220rpx;
		border-radius: 28rpx;
		overflow: hidden;
		padding: 24rpx;
		box-sizing: border-box;
		display: flex;
		align-items: flex-end;
	}

	.mock-art.theme-mint {
		background: linear-gradient(135deg, #95ddd0 0%, #5ab6b1 100%);
	}

	.mock-art.theme-sand {
		background: linear-gradient(135deg, #f4d1b2 0%, #eea26d 100%);
	}

	.mock-art.theme-sky {
		background: linear-gradient(135deg, #cde4ff 0%, #8bc2ff 100%);
	}

	.mock-art.theme-plum {
		background: linear-gradient(135deg, #ff8ebb 0%, #b985ff 100%);
	}

	.glow {
		position: absolute;
		width: 220rpx;
		height: 220rpx;
		right: -30rpx;
		top: -30rpx;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.16);
	}

	.cube {
		position: absolute;
		background: rgba(255, 255, 255, 0.78);
		box-shadow: 0 12rpx 30rpx rgba(0, 0, 0, 0.08);
	}

	.cube-a {
		width: 84rpx;
		height: 104rpx;
		right: 74rpx;
		top: 48rpx;
		border-radius: 18rpx;
		transform: rotate(8deg);
	}

	.cube-b {
		width: 112rpx;
		height: 76rpx;
		right: 32rpx;
		top: 112rpx;
		border-radius: 18rpx;
		transform: rotate(-8deg);
	}

	.copy {
		position: relative;
		z-index: 2;
		max-width: 54%;
		color: #fff;
		display: flex;
		flex-direction: column;
		gap: 10rpx;
	}

	.label {
		font-size: 34rpx;
		font-weight: 800;
		line-height: 1.3;
	}

	.desc {
		font-size: 22rpx;
		line-height: 1.6;
		opacity: 0.9;
	}
</style>
