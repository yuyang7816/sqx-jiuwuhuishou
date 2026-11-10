<template>
	<view class="app-page agreement-page">
		<top-nav :title="pageTitle" title-color="#1f1f1f"></top-nav>
		<scroll-view scroll-y class="page-body compact agreement-body">
			<view v-if="loading" class="agreement-loading">加载中...</view>
			<template v-else>
				<!-- 管理端富文本 HTML -->
				<rich-text v-if="contentHtml" class="agreement-html" :nodes="contentHtml"></rich-text>
				<!-- 兼容旧 sections 结构 -->
				<template v-else>
					<view
						v-for="(section, index) in sections"
						:key="`${section.title}-${index}`"
						class="agreement-section"
					>
						<view class="agreement-section__title">{{ section.title }}</view>
						<view v-if="section.text" class="agreement-section__text">{{ section.text }}</view>
						<view v-if="section.bullets.length" class="feature-list agreement-section__bullets">
							<view v-for="(bullet, bulletIndex) in section.bullets" :key="`${section.title}-bullet-${bulletIndex}`" class="muted">
								• {{ bullet }}
							</view>
						</view>
					</view>
				</template>
			</template>
		</scroll-view>
		<view class="footer-bar one">
			<view class="btn-main" @click="back">返回</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import { loadAboutUs, loadPrivacyAgreement, loadServiceAgreement } from '@/common/services/agreement.js';
	import {
		DEFAULT_ABOUT_SECTIONS,
		DEFAULT_ABOUT_TITLE,
		DEFAULT_PRIVACY_SECTIONS,
		DEFAULT_PRIVACY_TITLE,
		DEFAULT_SECTIONS,
		DEFAULT_TITLE
	} from '@/common/adapters/agreement.js';

	const pageType = ref('about');
	const loading = ref(false);
	const pageTitle = ref(DEFAULT_ABOUT_TITLE);
	const sections = ref(DEFAULT_ABOUT_SECTIONS);
	const contentHtml = ref('');

	const resetFallback = () => {
		if (pageType.value === 'privacy') {
			pageTitle.value = DEFAULT_PRIVACY_TITLE;
			sections.value = DEFAULT_PRIVACY_SECTIONS;
			contentHtml.value = '';
			return;
		}
		if (pageType.value === 'service') {
			pageTitle.value = DEFAULT_TITLE;
			sections.value = DEFAULT_SECTIONS;
			contentHtml.value = '';
			return;
		}
		pageTitle.value = DEFAULT_ABOUT_TITLE;
		sections.value = DEFAULT_ABOUT_SECTIONS;
		contentHtml.value = '';
	};

	const loadByType = async () => {
		if (pageType.value === 'privacy') {
			return loadPrivacyAgreement();
		}
		if (pageType.value === 'service') {
			return loadServiceAgreement();
		}
		return loadAboutUs();
	};

	const failMessage = () => {
		if (pageType.value === 'privacy') return '加载隐私政策失败';
		if (pageType.value === 'service') return '加载用户协议失败';
		return '加载关于我们失败';
	};

	const loadPageData = async () => {
		loading.value = true;
		try {
			const data = await loadByType();
			pageTitle.value = data.title;
			sections.value = data.sections || [];
			contentHtml.value = data.html || '';
		} catch (error) {
			resetFallback();
			uni.showToast({
				title: error?.message || failMessage(),
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	onLoad((options) => {
		pageType.value = options?.type || 'about';
		resetFallback();
	});

	onShow(() => {
		loadPageData();
	});

	const back = () => uni.navigateBack();
</script>

<style lang="scss">
	.agreement-page {
		background: linear-gradient(180deg, #fff8f5 0%, #fff 280rpx);
	}

	.agreement-body {
		padding-top: 16rpx;
		padding-bottom: calc(150rpx + env(safe-area-inset-bottom));
	}

	.agreement-loading {
		padding: 80rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #9a8686;
	}

	.agreement-html {
		display: block;
		font-size: 30rpx;
		line-height: 1.85;
		color: #4a3a3a;
		word-break: break-word;
	}

	.agreement-section + .agreement-section {
		margin-top: 42rpx;
	}

	.agreement-section__title {
		font-size: 40rpx;
		font-weight: 800;
		color: #2f2424;
	}

	.agreement-section__text {
		margin-top: 18rpx;
		font-size: 32rpx;
		line-height: 1.9;
		color: #4a3a3a;
	}

	.agreement-section__bullets {
		margin-top: 20rpx;
	}
</style>
