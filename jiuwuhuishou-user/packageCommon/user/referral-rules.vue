<template>
	<view class="app-page referral-rules-page">
		<top-nav :title="pageTitle"></top-nav>
		<scroll-view scroll-y class="page-body compact referral-rules-body">
			<view v-if="loading" class="referral-rules-loading">加载中...</view>
			<template v-else>
				<rich-text v-if="contentHtml" class="referral-rules-html" :nodes="contentHtml"></rich-text>
				<view v-else class="referral-rules-empty muted">暂无分佣规则内容</view>
			</template>
		</scroll-view>
		<floating-service></floating-service>
		<view class="footer-bar one">
			<view class="btn-main" @click="go('/packageCommon/user/invite')">立即邀请好友赚钱</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { loadReferralRules } from '@/common/services/referralRules.js';
	import {
		DEFAULT_TITLE,
		buildDefaultReferralRulesHtml
	} from '@/common/adapters/referralRules.js';

	const loading = ref(false);
	const pageTitle = ref(DEFAULT_TITLE);
	const contentHtml = ref(buildDefaultReferralRulesHtml());

	const syncPageData = async () => {
		loading.value = true;
		try {
			const data = await loadReferralRules();
			pageTitle.value = data.title || DEFAULT_TITLE;
			contentHtml.value = data.html || '';
		} catch (error) {
			pageTitle.value = DEFAULT_TITLE;
			contentHtml.value = buildDefaultReferralRulesHtml();
			uni.showToast({ title: error.message || '加载分佣规则失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	onShow(() => {
		syncPageData();
	});

	const go = (url) => uni.navigateTo({ url });
</script>

<style lang="scss">
	.referral-rules-page {
		background: linear-gradient(180deg, #fff8f5 0%, #fff 280rpx);
	}

	.referral-rules-body {
		padding-top: 16rpx;
		padding-bottom: calc(150rpx + env(safe-area-inset-bottom));
	}

	.referral-rules-loading,
	.referral-rules-empty {
		padding: 80rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #8f7b7b;
	}

	.referral-rules-html {
		display: block;
		font-size: 30rpx;
		line-height: 1.85;
		color: #4a3a3a;
		word-break: break-word;
	}
</style>
