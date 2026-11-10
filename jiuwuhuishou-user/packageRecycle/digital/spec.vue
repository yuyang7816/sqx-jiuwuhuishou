<template>
	<view class="app-page digital-spec-page">
		<top-nav title="商品规格" title-color="#1f1f1f" :show-more="false"></top-nav>

		<view v-if="loading" class="digital-spec-loading">加载中...</view>

		<scroll-view v-else scroll-y class="page-body compact digital-spec-body">
			<view class="digital-spec-hero">
				<view class="digital-spec-hero__label">精准估价</view>
				<view class="digital-spec-hero__title">{{ modelName }}</view>
				<view class="digital-spec-hero__desc">{{ brandName ? `${brandName} · ${sceneLabel}` : sceneLabel }}</view>
				<view class="digital-spec-hero__thumb" :class="thumbClass">
					<view class="digital-spec-hero__device"></view>
				</view>
			</view>

			<view v-for="section in sections" :key="section.key" class="digital-spec-card">
				<view class="digital-spec-card__title">{{ section.label }}</view>
				<view class="digital-spec-options" :class="section.layout === 'column' ? 'is-column' : ''">
					<view
						v-for="option in section.options"
						:key="option"
						class="digital-spec-chip"
						:class="[{ 'is-active': selectedSpec[section.key] === option }, section.layout === 'column' ? 'is-full' : '']"
						@click="selectOption(section.key, option)"
					>
						{{ option }}
					</view>
				</view>
			</view>

			<view class="digital-spec-preview">
				<view class="digital-spec-preview__title">当前预估</view>
				<view class="digital-spec-preview__price">{{ estimatePriceText }}</view>
				<view class="digital-spec-preview__desc">规格越完整、成色越好，预估价格越接近最终成交价。</view>
				<view class="digital-spec-preview__tags">
					<text v-for="tag in summaryTags" :key="tag" class="digital-spec-preview__tag">{{ tag }}</text>
				</view>
			</view>
		</scroll-view>

		<view v-if="!loading" class="digital-spec-bottom-bar">
			<view>
				<view class="digital-spec-bottom-bar__label">预估回收价</view>
				<view class="digital-spec-bottom-bar__price">{{ estimatePriceText }}</view>
			</view>
			<view class="digital-spec-bottom-bar__btn" @click="goNext">下一步</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, reactive, ref, watch } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import {
		createDefaultSelectedSpec,
		fetchDigitalEstimatePrice,
		loadDigitalSpecPage
	} from '@/common/services/digitalSpec.js';

	const scene = ref('digital');
	const loading = ref(true);
	const estimating = ref(false);
	const sceneLabel = ref('数码回收');
	const brandName = ref('');
	const modelName = ref('');
	const thumbClass = ref('is-deep');
	const modelId = ref('');
	const basePrice = ref(0);
	const sections = ref([]);
	const selectedSpec = reactive({});
	const estimateAmount = ref(0);
	let estimateSeq = 0;

	const decodeValue = (value = '') => {
		try {
			return decodeURIComponent(value);
		} catch (error) {
			return value;
		}
	};

	const parsePrice = (value = '') => {
		const raw = `${decodeValue(value)}`.replace(/[￥¥,\s]/g, '');
		return Number(raw) || 0;
	};

	const resetSelectedSpec = (list) => {
		Object.keys(selectedSpec).forEach((key) => {
			delete selectedSpec[key];
		});
		const defaults = createDefaultSelectedSpec(list);
		Object.assign(selectedSpec, defaults);
	};

	const formatPrice = (amount) =>
		`¥${Number(amount || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

	const estimatePriceText = computed(() => formatPrice(estimateAmount.value));
	const summaryTags = computed(() => Object.values(selectedSpec).filter(Boolean).slice(0, 6));

	const refreshEstimate = async () => {
		const seq = ++estimateSeq;
		estimating.value = true;
		try {
			const price = await fetchDigitalEstimatePrice({
				modelId: modelId.value,
				selectedSpec: { ...selectedSpec },
				basePrice: basePrice.value
			});
			if (seq === estimateSeq) {
				estimateAmount.value = price;
			}
		} finally {
			if (seq === estimateSeq) {
				estimating.value = false;
			}
		}
	};

	const selectOption = (key, value) => {
		selectedSpec[key] = value;
	};

	const loadPageData = async (options = {}) => {
		loading.value = true;
		try {
			const page = await loadDigitalSpecPage({
				modelId: options.modelId,
				scene: options.scene,
				brandName: options.brand,
				modelName: options.model,
				basePrice: parsePrice(options.price)
			});
			scene.value = page.scene;
			sceneLabel.value = page.sceneLabel;
			brandName.value = page.brandName;
			modelName.value = page.modelName;
			modelId.value = page.modelId || options.modelId || '';
			basePrice.value = page.basePrice || parsePrice(options.price);
			sections.value = page.sections;
			resetSelectedSpec(page.sections);
			await refreshEstimate();
		} catch (error) {
			uni.showToast({ title: error.message || '加载失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	const buildNextQuery = () => {
		const modelText = [modelName.value, selectedSpec.storage || selectedSpec.series || '', selectedSpec.color || selectedSpec.material || '']
			.filter(Boolean)
			.join(' ');
		const query = [
			`scene=${scene.value}`,
			`model=${encodeURIComponent(modelText)}`,
			`price=${encodeURIComponent(formatPrice(estimateAmount.value).replace('¥', '￥'))}`
		];
		if (modelId.value) {
			query.push(`modelId=${modelId.value}`);
		}
		return query.join('&');
	};

	const goNext = () => {
		if (!modelId.value) {
			uni.showToast({ title: '缺少商品型号', icon: 'none' });
			return;
		}
		if (estimating.value) {
			uni.showToast({ title: '预估价格计算中，请稍候', icon: 'none' });
			return;
		}
		uni.navigateTo({
			url: `/packageRecycle/digital/settlement?${buildNextQuery()}`
		});
	};

	watch(
		() => JSON.stringify(selectedSpec),
		() => {
			if (!loading.value && modelId.value) {
				refreshEstimate();
			}
		}
	);

	onLoad((options) => {
		thumbClass.value = options.thumbClass || 'is-deep';
		loadPageData({
			scene: options.scene === 'luxury' ? 'luxury' : 'digital',
			brand: decodeValue(options.brand || ''),
			model: decodeValue(options.model || '未命名商品'),
			price: options.price,
			modelId: options.modelId
		});
	});
</script>

<style lang="scss">
	.digital-spec-page {
		background: linear-gradient(180deg, #faf7f6 0%, #fefefe 320rpx, #ffffff 100%);
	}

	.digital-spec-loading {
		padding: 160rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #8a7a7a;
	}

	.digital-spec-body {
		padding-top: 14rpx;
		padding-bottom: calc(210rpx + env(safe-area-inset-bottom));
	}

	.digital-spec-hero,
	.digital-spec-card,
	.digital-spec-preview {
		background: #fff;
		border-radius: 28rpx;
		box-shadow: 0 16rpx 36rpx rgba(108, 78, 78, 0.06);
	}

	.digital-spec-hero {
		position: relative;
		padding: 30rpx 28rpx;
		overflow: hidden;
		background: linear-gradient(135deg, #fff8f2 0%, #fff4f4 56%, #fff9e8 100%);
	}

	.digital-spec-hero__label {
		display: inline-flex;
		align-items: center;
		height: 42rpx;
		padding: 0 16rpx;
		border-radius: 999rpx;
		background: rgba(255, 255, 255, 0.72);
		font-size: 22rpx;
		font-weight: 700;
		color: #b86b16;
	}

	.digital-spec-hero__title {
		margin-top: 18rpx;
		max-width: 440rpx;
		font-size: 40rpx;
		font-weight: 800;
		line-height: 1.25;
		color: #2f2424;
	}

	.digital-spec-hero__desc {
		margin-top: 12rpx;
		font-size: 24rpx;
		line-height: 1.7;
		color: #7c6969;
	}

	.digital-spec-hero__thumb {
		position: absolute;
		right: 28rpx;
		top: 28rpx;
		width: 120rpx;
		height: 120rpx;
		border-radius: 32rpx;
		background: rgba(255, 255, 255, 0.74);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.digital-spec-hero__device {
		width: 44rpx;
		height: 84rpx;
		border-radius: 18rpx;
		transform: rotate(14deg);
		box-shadow: inset 0 0 0 2rpx rgba(255, 255, 255, 0.28);
	}

	.is-deep .digital-spec-hero__device { background: linear-gradient(180deg, #5f676f 0%, #2a2f35 100%); }
	.is-silver .digital-spec-hero__device { background: linear-gradient(180deg, #d7d0cb 0%, #9a908a 100%); }
	.is-purple .digital-spec-hero__device { background: linear-gradient(180deg, #705275 0%, #2f2432 100%); }
	.is-cyan .digital-spec-hero__device { background: linear-gradient(180deg, #90d8dc 0%, #4e9aa0 100%); }
	.is-light .digital-spec-hero__device { background: linear-gradient(180deg, #fbfbfb 0%, #d6d6d6 100%); }
	.is-gold .digital-spec-hero__device { background: linear-gradient(180deg, #e7c978 0%, #b58a1c 100%); }
	.is-brown .digital-spec-hero__device { background: linear-gradient(180deg, #9b7a61 0%, #5c4135 100%); }
	.is-black .digital-spec-hero__device { background: linear-gradient(180deg, #4f4f55 0%, #17171b 100%); }
	.is-gray .digital-spec-hero__device { background: linear-gradient(180deg, #bcbcc3 0%, #74747b 100%); }
	.is-orange .digital-spec-hero__device { background: linear-gradient(180deg, #f2a65a 0%, #d96b16 100%); }

	.digital-spec-card {
		margin-top: 22rpx;
		padding: 26rpx 24rpx;
	}

	.digital-spec-card__title {
		font-size: 30rpx;
		font-weight: 800;
		color: #2f2424;
	}

	.digital-spec-options {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
		margin-top: 20rpx;
	}

	.digital-spec-options.is-column {
		flex-direction: column;
		flex-wrap: nowrap;
	}

	.digital-spec-chip {
		min-width: 140rpx;
		height: 68rpx;
		padding: 0 24rpx;
		border-radius: 22rpx;
		background: #f8f3f3;
		border: 2rpx solid #efe3e3;
		box-sizing: border-box;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 24rpx;
		color: #5f4b4b;
	}

	.digital-spec-chip.is-full {
		width: 100%;
	}

	.digital-spec-chip.is-active {
		background: #fff3f5;
		border-color: #ff3554;
		color: #e21635;
		font-weight: 700;
	}

	.digital-spec-preview {
		margin-top: 24rpx;
		padding: 28rpx 24rpx;
		background: linear-gradient(135deg, #fff8ef 0%, #fff2f5 100%);
	}

	.digital-spec-preview__title {
		font-size: 26rpx;
		color: #7d6868;
	}

	.digital-spec-preview__price {
		margin-top: 14rpx;
		font-size: 44rpx;
		font-weight: 800;
		color: #db1736;
	}

	.digital-spec-preview__desc {
		margin-top: 10rpx;
		font-size: 24rpx;
		line-height: 1.7;
		color: #7d6868;
	}

	.digital-spec-preview__tags {
		display: flex;
		flex-wrap: wrap;
		gap: 12rpx;
		margin-top: 18rpx;
	}

	.digital-spec-preview__tag {
		padding: 10rpx 16rpx;
		border-radius: 999rpx;
		background: rgba(255, 255, 255, 0.84);
		font-size: 22rpx;
		color: #715d5d;
	}

	.digital-spec-bottom-bar {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 24rpx;
		padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
		background: rgba(255, 255, 255, 0.96);
		box-shadow: 0 -14rpx 32rpx rgba(53, 31, 31, 0.08);
		backdrop-filter: blur(16rpx);
	}

	.digital-spec-bottom-bar__label {
		font-size: 24rpx;
		color: #8a7777;
	}

	.digital-spec-bottom-bar__price {
		margin-top: 8rpx;
		font-size: 44rpx;
		font-weight: 800;
		color: #d91535;
	}

	.digital-spec-bottom-bar__btn {
		flex-shrink: 0;
		min-width: 300rpx;
		height: 92rpx;
		padding: 0 44rpx;
		border-radius: 999rpx;
		background: linear-gradient(180deg, #dc1838 0%, #c30f2d 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 34rpx;
		font-weight: 700;
		color: #fff;
	}
</style>
