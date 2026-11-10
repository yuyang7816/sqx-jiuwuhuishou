<template>
	<view class="app-page notification-page">
		<top-nav title="消息中心"></top-nav>
		<scroll-view scroll-y class="page-body compact notification-body">
			<view v-if="loading" class="notification-empty">加载中...</view>
			<view v-else-if="!messages.length" class="notification-empty">暂无消息</view>
			<view v-else class="notification-list">
				<view
					v-for="item in messages"
					:key="item.id"
					class="notification-item"
					:class="{ 'is-unread': !item.isRead }"
					@click="openMessage(item)"
				>
					<view class="notification-item__head">
						<view class="notification-item__title">{{ item.title }}</view>
						<view v-if="!item.isRead" class="notification-item__dot"></view>
					</view>
					<view class="notification-item__content">{{ item.content }}</view>
					<view class="notification-item__time">{{ item.time }}</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { loadUserNotifications, markUserNotificationRead } from '@/common/services/notification.js';

	const loading = ref(false);
	const messages = ref([]);

	const loadPageData = async () => {
		if (!ensureLoggedIn()) {
			messages.value = [];
			return;
		}
		loading.value = true;
		try {
			const data = await loadUserNotifications();
			messages.value = data.records;
		} catch (error) {
			messages.value = [];
			uni.showToast({ title: error.message || '加载消息失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	const navigateByUrl = (url) => {
		if (!url) {
			return;
		}
		if (url.startsWith('/pages/')) {
			uni.switchTab({ url });
			return;
		}
		uni.navigateTo({ url });
	};

	const openMessage = async (item) => {
		try {
			if (!item.isRead) {
				await markUserNotificationRead(item.id);
				item.isRead = true;
			}
			if (item.url) {
				navigateByUrl(item.url);
			}
		} catch (error) {
			uni.showToast({ title: error.message || '操作失败', icon: 'none' });
		}
	};

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.notification-page { background: linear-gradient(180deg, #fff8f5 0%, #fff 320rpx); }
	.notification-body { padding-top: 14rpx; padding-bottom: calc(80rpx + env(safe-area-inset-bottom)); }
	.notification-list { display: flex; flex-direction: column; gap: 18rpx; }
	.notification-item { background: #fff; border-radius: 28rpx; padding: 24rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06); }
	.notification-item.is-unread { border: 2rpx solid rgba(207, 20, 50, 0.12); }
	.notification-item__head { display: flex; align-items: center; justify-content: space-between; gap: 12rpx; }
	.notification-item__title { font-size: 30rpx; font-weight: 700; color: #352929; }
	.notification-item__dot { width: 12rpx; height: 12rpx; border-radius: 50%; background: #cf1432; flex-shrink: 0; }
	.notification-item__content { margin-top: 12rpx; font-size: 26rpx; line-height: 1.7; color: #6d5858; }
	.notification-item__time { margin-top: 14rpx; font-size: 22rpx; color: #b0a0a0; }
	.notification-empty { padding: 80rpx 24rpx; text-align: center; font-size: 26rpx; color: #9a8888; }
</style>
