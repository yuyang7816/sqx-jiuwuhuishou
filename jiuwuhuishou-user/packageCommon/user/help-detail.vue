<template>
	<view class="app-page help-detail-page">
		<top-nav title="帮助详情" title-color="#1f1f1f"></top-nav>
		<scroll-view scroll-y class="page-body compact help-detail-body">
			<view v-if="loading" class="help-detail-loading">加载中...</view>
			<view v-else class="help-detail-card">
				<view class="help-detail-card__tag">{{ article.tag }}</view>
				<view class="help-detail-card__title">{{ article.title }}</view>
				<view class="help-detail-card__meta">最后更新：{{ article.updatedAt }}</view>
				<view v-if="!article.sections.length" class="help-detail-card__empty">暂无详情内容</view>
				<view v-for="(section, index) in article.sections" :key="index" class="help-detail-card__section">
					<view class="help-detail-card__section-title">{{ section.title }}</view>
					<view class="help-detail-card__section-content">{{ section.content }}</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import { loadHelpArticleDetail } from '@/common/services/help.js';
	import { DEFAULT_HELP_DETAIL } from '@/common/adapters/help.js';

	const articleId = ref('');
	const loading = ref(false);
	const article = ref({
		...DEFAULT_HELP_DETAIL,
		sections: [...DEFAULT_HELP_DETAIL.sections]
	});

	const loadPageData = async () => {
		if (!articleId.value) {
			return;
		}
		loading.value = true;
		try {
			const data = await loadHelpArticleDetail(articleId.value);
			article.value = {
				...data,
				sections: data.sections || []
			};
		} catch (error) {
			article.value = {
				...DEFAULT_HELP_DETAIL,
				sections: [...DEFAULT_HELP_DETAIL.sections]
			};
			uni.showToast({
				title: error?.message || '加载帮助详情失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	onLoad((options) => {
		articleId.value = options?.id || '';
	});

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.help-detail-page {
		background: linear-gradient(180deg, #fff8f3 0%, #fff 320rpx);
	}

	.help-detail-body {
		padding-top: 16rpx;
		padding-bottom: calc(80rpx + env(safe-area-inset-bottom));
	}

	.help-detail-loading,
	.help-detail-card__empty {
		padding: 80rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #9a8686;
	}

	.help-detail-card {
		background: #fff;
		border-radius: 28rpx;
		padding: 30rpx 28rpx;
		box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06);
	}

	.help-detail-card__tag {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		height: 44rpx;
		padding: 0 18rpx;
		border-radius: 999rpx;
		background: #fff1f3;
		color: #cf1432;
		font-size: 22rpx;
		font-weight: 700;
	}

	.help-detail-card__title {
		margin-top: 22rpx;
		font-size: 40rpx;
		font-weight: 800;
		line-height: 1.4;
		color: #2f2424;
	}

	.help-detail-card__meta {
		margin-top: 14rpx;
		font-size: 22rpx;
		color: #b19f9f;
	}

	.help-detail-card__section {
		margin-top: 32rpx;
		padding-top: 28rpx;
		border-top: 2rpx solid #f3eaea;
	}

	.help-detail-card__section-title {
		font-size: 30rpx;
		font-weight: 700;
		color: #342727;
	}

	.help-detail-card__section-content {
		margin-top: 16rpx;
		font-size: 28rpx;
		line-height: 1.9;
		color: #6f5e5e;
	}
</style>
