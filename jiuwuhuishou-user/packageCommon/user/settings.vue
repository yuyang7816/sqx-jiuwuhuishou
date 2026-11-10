<template>
	<view class="app-page settings-page">
		<top-nav title="设置"></top-nav>
		<scroll-view scroll-y class="page-body compact settings-body">
			<view class="settings-card">
				<view class="settings-item settings-item--switch">
					<text class="settings-item__label">消息通知</text>
					<u-switch
						v-model="messageEnabled"
						activeColor="#d1a056"
						inactiveColor="#e8ddd4"
						size="24"
						:loading="updatingMessageNotify"
						asyncChange
						@change="handleMessageNotifyChange"
					></u-switch>
				</view>

				<view class="settings-item" @click="clearCache">
					<text class="settings-item__label">清理缓存</text>
					<view class="settings-item__right">
						<text class="settings-item__value">{{ cacheSize }}</text>
						<u-icon name="arrow-right" size="16" color="#c1ae9e"></u-icon>
					</view>
				</view>

				<view class="settings-item" @click="go('/packageCommon/user/change-password')">
					<text class="settings-item__label">修改密码</text>
					<u-icon name="arrow-right" size="16" color="#c1ae9e"></u-icon>
				</view>

				<view class="settings-item" @click="go('/packageCommon/user/agreement?type=about')">
					<text class="settings-item__label">关于我们</text>
					<u-icon name="arrow-right" size="16" color="#c1ae9e"></u-icon>
				</view>

				<view class="settings-item" @click="go('/packageCommon/user/help-center')">
					<text class="settings-item__label">帮助中心</text>
					<u-icon name="arrow-right" size="16" color="#c1ae9e"></u-icon>
				</view>

				<view class="settings-item" @click="go('/packageCommon/user/agreement?type=service')">
					<text class="settings-item__label">用户协议</text>
					<u-icon name="arrow-right" size="16" color="#c1ae9e"></u-icon>
				</view>

				<view class="settings-item settings-item--last" @click="go('/packageCommon/user/agreement?type=privacy')">
					<text class="settings-item__label">隐私政策</text>
					<u-icon name="arrow-right" size="16" color="#c1ae9e"></u-icon>
				</view>
			</view>

			<view class="settings-logout" @click="logout">退出登录</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn, logout as logoutSession } from '@/common/services/auth.js';
	import { clearAppCache, getCacheSizeText, loadSettingsPageData, updateMessageNotifyEnabled } from '@/common/services/settings.js';

	const messageEnabled = ref(true);
	const cacheSize = ref('0KB');
	const updatingMessageNotify = ref(false);

	const syncSettings = async () => {
		if (!ensureLoggedIn()) {
			return;
		}
		try {
			const data = await loadSettingsPageData();
			messageEnabled.value = data.messageNotifyEnabled;
		} catch (error) {
			uni.showToast({
				title: error?.message || '加载设置失败',
				icon: 'none'
			});
		}
	};

	onShow(() => {
		cacheSize.value = getCacheSizeText();
		syncSettings();
	});

	const handleMessageNotifyChange = async (enabled) => {
		if (updatingMessageNotify.value) {
			return;
		}
		updatingMessageNotify.value = true;
		try {
			const result = await updateMessageNotifyEnabled(enabled);
			messageEnabled.value = result.messageNotifyEnabled;
			uni.setStorageSync('messageNotifyEnabled', result.messageNotifyEnabled ? '1' : '0');
			uni.showToast({
				title: result.messageNotifyEnabled ? '已开启消息通知' : '已关闭消息通知',
				icon: 'none'
			});
		} catch (error) {
			messageEnabled.value = !enabled;
			uni.showToast({
				title: error?.message || '设置失败',
				icon: 'none'
			});
		} finally {
			updatingMessageNotify.value = false;
		}
	};

	const go = (url) => uni.navigateTo({ url });

	const clearCache = async () => {
		try {
			cacheSize.value = await clearAppCache();
			uni.showToast({
				title: '缓存已清理',
				icon: 'success'
			});
		} catch (error) {
			uni.showToast({
				title: error?.message || '清理失败',
				icon: 'none'
			});
		}
	};

	const logout = () => {
		uni.showModal({
			title: '提示',
			content: '确定要退出登录吗？',
			success: async (res) => {
				if (!res.confirm) {
					return;
				}
				await logoutSession();
				uni.showToast({
					title: '已退出登录',
					icon: 'success'
				});
				setTimeout(() => {
					uni.reLaunch({ url: '/packageCommon/user/login' });
				}, 400);
			}
		});
	};
</script>

<style lang="scss">
	.settings-page {
		background: linear-gradient(180deg, #fff8f1 0%, #fffdf9 220rpx, #fff 100%);
	}

	.settings-body {
		padding-top: 18rpx;
		padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
	}

	.settings-card {
		background: #fff;
		border-radius: 30rpx;
		box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06);
		overflow: hidden;
	}

	.settings-item {
		height: 104rpx;
		padding: 0 28rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		border-bottom: 2rpx solid #f3ece6;
		box-sizing: border-box;
	}

	.settings-item--last {
		border-bottom: 0;
	}

	.settings-item__label {
		font-size: 30rpx;
		font-weight: 600;
		color: #3d3030;
	}

	.settings-item__right {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.settings-item__value {
		font-size: 28rpx;
		color: #7d6868;
	}

	.settings-logout {
		height: 92rpx;
		margin-top: 32rpx;
		border-radius: 999rpx;
		border: 2rpx solid rgba(209, 160, 86, 0.72);
		background: #fff;
		color: #d1a056;
		font-size: 32rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		box-sizing: border-box;
	}
</style>
