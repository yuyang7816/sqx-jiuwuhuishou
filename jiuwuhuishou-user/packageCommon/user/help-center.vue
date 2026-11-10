<template>
	<view class="app-page help-center-page">
		<top-nav title="帮助中心" title-color="#1f1f1f"></top-nav>
		<scroll-view scroll-y class="page-body compact help-center-body">
			<view class="help-center-hero">
				<view class="help-center-hero__title">常见问题与使用指南</view>
				<view class="help-center-hero__desc">按文章查看账号、订单、回收、提现等常见问题，帮助用户快速自助解决。</view>
			</view>

			<view v-if="loading" class="help-center-loading">加载中...</view>
			<view v-else-if="!articles.length" class="help-center-empty">暂无帮助文章</view>
			<view v-else class="help-center-list">
				<view v-for="item in articles" :key="item.id" class="help-center-item" @click="goDetail(item.id)">
					<view class="help-center-item__content">
						<view class="help-center-item__title">{{ item.title }}</view>
						<view class="help-center-item__desc">{{ item.desc }}</view>
						<view class="help-center-item__meta">{{ item.tag }} · {{ item.updatedAt }}</view>
					</view>
					<u-icon name="arrow-right" size="16" color="#c5b3b3"></u-icon>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { loadHelpArticles } from '@/common/services/help.js';
	import { DEFAULT_HELP_ARTICLES } from '@/common/adapters/help.js';

	const loading = ref(false);
	const articles = ref(DEFAULT_HELP_ARTICLES);

	const loadPageData = async () => {
		loading.value = true;
		try {
			const data = await loadHelpArticles();
			articles.value = data.records;
		} catch (error) {
			articles.value = DEFAULT_HELP_ARTICLES;
			uni.showToast({
				title: error?.message || '加载帮助中心失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	onShow(() => {
		loadPageData();
	});

	const goDetail = (id) => {
		uni.navigateTo({ url: `/packageCommon/user/help-detail?id=${id}` });
	};
</script>

<style lang="scss">
	.help-center-page {
		background: linear-gradient(180deg, #fff8f3 0%, #fff 320rpx);
	}

	.help-center-body {
		padding-top: 16rpx;
		padding-bottom: calc(80rpx + env(safe-area-inset-bottom));
	}

	.help-center-hero,
	.help-center-item {
		background: #fff;
		border-radius: 28rpx;
		box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06);
	}

	.help-center-hero {
		padding: 28rpx;
	}

	.help-center-hero__title {
		font-size: 38rpx;
		font-weight: 800;
		color: #2f2424;
	}

	.help-center-hero__desc {
		margin-top: 14rpx;
		font-size: 24rpx;
		line-height: 1.7;
		color: #867272;
	}

	.help-center-loading,
	.help-center-empty {
		margin-top: 24rpx;
		padding: 80rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #9a8686;
	}

	.help-center-list {
		margin-top: 24rpx;
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.help-center-item {
		padding: 26rpx 24rpx;
		display: flex;
		align-items: center;
		gap: 18rpx;
	}

	.help-center-item__content {
		flex: 1;
		min-width: 0;
	}

	.help-center-item__title {
		font-size: 30rpx;
		font-weight: 700;
		line-height: 1.5;
		color: #302525;
	}

	.help-center-item__desc {
		margin-top: 12rpx;
		font-size: 24rpx;
		line-height: 1.6;
		color: #8a7676;
	}

	.help-center-item__meta {
		margin-top: 14rpx;
		font-size: 22rpx;
		color: #b19f9f;
	}
</style>
