<template>
	<view class="app-page success-page">
		<top-nav title="预约成功"></top-nav>

		<scroll-view scroll-y class="page-body compact success-body">
			<view class="success-hero">
				<image class="success-hero__image" src="/static/image/order-success-hero.png" mode="aspectFit"></image>
				<view class="success-hero__title">预约成功！</view>
				<view class="success-hero__desc">感谢您为地球减碳做出的贡献，回收小哥将准时到达。</view>
			</view>

			<view class="success-card">
				<view class="success-card__head">
					<u-icon name="file-text" size="18" color="#cf1432"></u-icon>
					<text class="success-card__title">预约详情</text>
				</view>

				<view class="success-card__rows">
					<view class="success-card__row">
						<text class="success-card__label">回收种类</text>
						<text class="success-card__value">{{ categoryText }}</text>
					</view>
					<view v-if="!isMailOrder" class="success-card__row">
						<text class="success-card__label">上门时间</text>
						<text class="success-card__value success-card__value--strong">{{ appointmentText }}</text>
					</view>
					<view v-if="!isMailOrder" class="success-card__row success-card__row--address">
						<text class="success-card__label">上门地址</text>
						<text class="success-card__value">{{ addressText }}</text>
					</view>
					<view class="success-card__row">
						<text class="success-card__label">预估收益</text>
						<view class="success-card__earn">
							<text class="success-card__earn-text">{{ estimateAmountText }}</text>
							<u-icon name="rmb-circle-fill" size="16" color="#e2a100"></u-icon>
						</view>
					</view>
				</view>
			</view>

			<view class="success-brand">
				<image class="success-brand__logo" src="/static/home-logo.png" mode="aspectFit"></image>
				<text class="success-brand__text">拾碳星球 · 守护每一份绿色初衷</text>
			</view>
		</scroll-view>

		<view class="footer-bar success-footer" style="display:block;">
			<button class="btn-main success-share-btn" open-type="share" hover-class="none" @tap="handleShareTap">
				<u-icon name="share-square" size="18" color="#ffffff"></u-icon>
				<text>转发给好友领奖励</text>
			</button>
			<view class="success-footer__row">
				<view class="btn-gold" @click="goOrderDetail">查看订单</view>
				<view class="btn-ghost" @click="goHome">返回首页</view>
			</view>
		</view>

		<floating-service></floating-service>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad, onShareAppMessage } from '@dcloudio/uni-app';
	import { getOrderDetail } from '@/common/services/order.js';
	import { buildInviteShareText, fetchInviteShareConfig } from '@/common/services/invite.js';

	const currentOrder = ref(null);
	const orderId = ref('');
	const preparingShare = ref(false);
	const shareConfig = ref({
		title: '我刚预约了旧物回收，邀请你一起参与环保回收领奖励',
		path: '/pages/index/index',
		shareUrl: '',
		shareText: ''
	});

	const isMailOrder = computed(() => currentOrder.value?.fulfillType === 'mail');

	const categoryText = computed(() =>
		currentOrder.value?.category
		|| currentOrder.value?.categoryText
		|| currentOrder.value?.title
		|| '--'
	);

	const appointmentText = computed(() => {
		const appointment = currentOrder.value?.appointment || currentOrder.value?.slotLabel || currentOrder.value?.time || '';
		if (!appointment) {
			return '--';
		}
		const text = `${appointment}`.replace('T', ' ').trim();
		// 已是「M月d日 HH:mm - HH:mm」等时段文案，直接展示
		if (/\d{1,2}月\d{1,2}日/.test(text) && /\d{1,2}:\d{2}\s*[-~～到至]\s*\d{1,2}:\d{2}/.test(text)) {
			return text.replace(/[～到至]/g, ' - ').replace(/\s*-\s*/g, ' - ');
		}
		const slotMatch = text.match(/(\d{1,2}:\d{2}\s*[-~～到至]\s*\d{1,2}:\d{2})/);
		if (slotMatch) {
			return slotMatch[1].replace(/[～到至]/g, ' - ').replace(/\s*-\s*/g, ' - ');
		}
		if (/^\d{4}-\d{2}-\d{2}\s+\d{1,2}:\d{2}/.test(text)) {
			return text.slice(11, 16);
		}
		return text;
	});

	const addressText = computed(() =>
		currentOrder.value?.address
		|| currentOrder.value?.pickupAddress
		|| '--'
	);

	const estimateAmountText = computed(() => {
		if (currentOrder.value?.estimateText) {
			return currentOrder.value.estimateText;
		}
		const amount = Number(currentOrder.value?.estimateAmount);
		if (!Number.isNaN(amount) && currentOrder.value?.estimateAmount != null && currentOrder.value?.estimateAmount !== '') {
			return `¥${amount.toFixed(2)}`;
		}
		return '--';
	});

	const buildSharePayload = (config = {}) => {
		const category = categoryText.value !== '--' ? categoryText.value : '旧物';
		const title = `我刚预约了${category}回收，邀请你一起参与环保回收领奖励`;
		return {
			invitationCode: config.invitationCode || '',
			title,
			path: config.path || '/pages/index/index',
			shareUrl: config.shareUrl || '',
			shareText: buildInviteShareText(config.invitationCode || '', title)
		};
	};

	const refreshShareConfig = async () => {
		try {
			const config = await fetchInviteShareConfig();
			shareConfig.value = buildSharePayload(config);
		} catch (error) {
			// 分享配置失败时仍可用默认首页路径拉起好友列表
			shareConfig.value = buildSharePayload({});
		}
	};

	const handleShareTap = async () => {
		if (preparingShare.value) {
			return;
		}
		preparingShare.value = true;
		try {
			const config = await fetchInviteShareConfig();
			shareConfig.value = buildSharePayload(config);
			const shareText = shareConfig.value.shareText || shareConfig.value.shareUrl;
			if (!shareText) {
				throw new Error('分享链接生成失败');
			}
			await new Promise((resolve, reject) => {
				uni.setClipboardData({
					data: shareText,
					success: resolve,
					fail: reject
				});
			});
			uni.showToast({
				title: '分享链接已复制',
				icon: 'none'
			});
		} catch (error) {
			uni.showToast({
				title: error.message || '分享准备失败',
				icon: 'none'
			});
		} finally {
			preparingShare.value = false;
		}
	};

	onLoad(async (options) => {
		orderId.value = options?.id || '';
		if (!orderId.value) {
			uni.showToast({ title: '订单信息无效', icon: 'none' });
			return;
		}
		try {
			currentOrder.value = await getOrderDetail(orderId.value);
		} catch (error) {
			uni.showToast({ title: error.message || '加载订单失败', icon: 'none' });
		}
		await refreshShareConfig();
	});

	// 小程序：配合 button[open-type=share] 拉起好友列表；H5：依赖 handleShareTap 复制线上链接
	onShareAppMessage(() => ({
		title: shareConfig.value.title,
		path: shareConfig.value.path
	}));

	const goOrderDetail = () => {
		const id = orderId.value || currentOrder.value?.id || '';
		uni.navigateTo({
			url: `/packageCommon/order/detail?id=${id}`
		});
	};

	const goHome = () => {
		uni.switchTab({
			url: '/pages/index/index'
		});
	};
</script>

<style lang="scss">
	.success-page {
		background: #f7f4f4;

		:deep(.floating-service) {
			bottom: calc(260rpx + env(safe-area-inset-bottom));
		}
	}

	.success-body {
		padding-top: 12rpx;
		padding-bottom: calc(280rpx + env(safe-area-inset-bottom));
	}

	.success-hero {
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
	}

	.success-hero__image {
		width: 360rpx;
		height: 360rpx;
		border-radius: 36rpx;
		background: #ffd7bf;
	}

	.success-hero__title {
		margin-top: 28rpx;
		font-size: 52rpx;
		font-weight: 800;
		color: #2f2424;
		line-height: 1.2;
	}

	.success-hero__desc {
		margin-top: 16rpx;
		padding: 0 40rpx;
		font-size: 28rpx;
		line-height: 1.7;
		color: #8a7575;
	}

	.success-card {
		margin-top: 36rpx;
		padding: 28rpx 28rpx 24rpx;
		border-radius: 28rpx;
		background: #fff;
		box-shadow: 0 16rpx 40rpx rgba(111, 79, 79, 0.08);
		box-sizing: border-box;
	}

	.success-card__head {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.success-card__title {
		font-size: 34rpx;
		font-weight: 800;
		color: #2f2424;
	}

	.success-card__rows {
		margin-top: 12rpx;
	}

	.success-card__row {
		display: flex;
		align-items: flex-start;
		justify-content: space-between;
		gap: 24rpx;
		padding: 22rpx 0;
	}

	.success-card__row--address .success-card__value {
		max-width: 420rpx;
		line-height: 1.55;
	}

	.success-card__label {
		flex-shrink: 0;
		font-size: 28rpx;
		color: #8a7575;
	}

	.success-card__value {
		flex: 1;
		text-align: right;
		font-size: 28rpx;
		color: #2f2424;
		font-weight: 600;
		word-break: break-all;
	}

	.success-card__value--strong {
		font-size: 30rpx;
		font-weight: 800;
	}

	.success-card__earn {
		display: flex;
		align-items: center;
		justify-content: flex-end;
		gap: 8rpx;
	}

	.success-card__earn-text {
		font-size: 30rpx;
		font-weight: 800;
		color: #d9a100;
	}

	.success-brand {
		margin-top: 48rpx;
		margin-bottom: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 12rpx;
	}

	.success-brand__logo {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
	}

	.success-brand__text {
		font-size: 24rpx;
		color: #b3a1a1;
	}

	.success-footer {
		padding-top: 18rpx;
	}

	/* 微信小程序原生 button 需重置默认绿底/边框，才能套用 .btn-main */
	.success-share-btn {
		width: 100%;
		border: 0 !important;
		padding: 0;
		margin: 0;
		line-height: 1;
		font-size: 32rpx;
		font-weight: 700;
		color: #fff !important;
		background: linear-gradient(135deg, #d01231 0%, #b80826 100%) !important;
		box-shadow: 0 16rpx 36rpx rgba(200, 16, 46, 0.25);
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 12rpx;
	}

	.success-share-btn::after {
		border: none;
	}

	/* 避免内部 u-icon/text 拦截点击，保证 open-type=share 直接拉起好友列表 */
	.success-share-btn :deep(*) {
		pointer-events: none;
	}

	.success-footer__row {
		display: flex;
		gap: 18rpx;
		margin-top: 18rpx;
	}
</style>
