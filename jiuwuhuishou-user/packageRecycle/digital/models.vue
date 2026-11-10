<template>
	<view class="app-page digital-model-page">
		<top-nav :title="pageTitle" title-color="#1f1f1f" :show-more="false"></top-nav>

		<view class="digital-search" @click="goSearch">
			<view class="digital-search__box">
				<u-icon name="search" size="18" color="#8d7b7b"></u-icon>
				<text class="digital-search__placeholder">{{ searchPlaceholder }}</text>
			</view>
		</view>

		<view class="digital-type-tabs">
			<view
				v-for="category in categories"
				:key="category.code"
				class="digital-type-tabs__item"
				:class="{ 'is-active': activeCategoryCode === category.code }"
				@click="switchCategory(category)"
			>
				{{ category.name }}
			</view>
		</view>

		<view v-if="loading" class="digital-loading">加载中...</view>

		<view v-else class="digital-layout">
			<scroll-view scroll-y class="digital-brand-list">
				<view
					v-for="brand in brands"
					:key="brand.id"
					class="digital-brand-list__item"
					:class="{ 'is-active': activeBrandId === brand.id }"
					@click="selectBrand(brand)"
				>
					{{ brand.name }}
				</view>
			</scroll-view>

			<scroll-view scroll-y class="digital-model-list">
				<view class="digital-model-list__head row-between">
					<view class="digital-model-list__brand">{{ currentHeader.brand }}</view>
					<view class="digital-model-list__count">共 {{ models.length }} 款{{ itemUnit }}</view>
				</view>

				<view v-if="!models.length" class="digital-empty">暂无型号数据</view>

				<view v-else class="digital-model-items">
					<view v-for="item in models" :key="item.id || item.name" class="digital-model-item" @click="goSettlement(item)">
						<view class="digital-model-item__thumb" :class="item.thumbClass">
							<view class="digital-model-item__device"></view>
						</view>
						<view class="digital-model-item__info">
							<view class="digital-model-item__name">{{ item.name }}</view>
							<view class="digital-model-item__sub">{{ item.sub || subLabel }}</view>
							<view class="digital-model-item__price">{{ item.price }}<text>起</text></view>
						</view>
						<u-icon name="arrow-right" size="16" color="#c7bbbb"></u-icon>
					</view>
				</view>

				<view v-if="models.length" class="digital-model-more">
					<view class="digital-model-more__dot"></view>
					<text>已加载全部型号</text>
				</view>
			</scroll-view>
		</view>

		<view v-if="models.length" class="digital-estimate-btn" @click="goSettlement(models[0])">
			<u-icon name="warning" size="18" color="#6d5200"></u-icon>
			<text>一键估价</text>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import {
		fetchDigitalBrandList,
		fetchDigitalCategories,
		fetchDigitalModelList
	} from '@/common/services/digital.js';

	const scene = ref('digital');
	const searchKeyword = ref('');
	const loading = ref(false);
	const categories = ref([]);
	const brands = ref([]);
	const models = ref([]);
	const activeCategoryCode = ref('digital');
	const activeBrandId = ref(null);

	const activeCategory = computed(() =>
		categories.value.find((item) => item.code === activeCategoryCode.value) || categories.value[0] || {}
	);

	const activeBrand = computed(() =>
		brands.value.find((item) => item.id === activeBrandId.value) || brands.value[0] || null
	);

	const pageTitle = computed(() => {
		if (activeCategoryCode.value === 'luxury') {
			return '奢侈品分类';
		}
		return activeCategory.value.name || '机型选择';
	});

	const searchPlaceholder = computed(() =>
		activeCategoryCode.value === 'luxury' ? '搜索品牌、品类' : '搜索机型、品牌'
	);

	const subLabel = computed(() =>
		activeCategoryCode.value === 'luxury' ? '最高回收参考价' : '最高回收价'
	);

	const itemUnit = computed(() => (activeCategoryCode.value === 'luxury' ? '商品' : '机型'));

	const currentHeader = computed(() => ({
		brand: `${activeCategory.value.name || ''} / ${activeBrand.value?.name || ''}`.replace(/^ \/ | \/ $/g, '')
	}));

	const loadModels = async (brandId) => {
		if (!brandId) {
			models.value = [];
			return;
		}
		models.value = await fetchDigitalModelList({
			brandId,
			scene: activeCategoryCode.value,
			keyword: searchKeyword.value || undefined
		});
	};

	const loadBrands = async () => {
		brands.value = await fetchDigitalBrandList(activeCategoryCode.value);
		const presetBrandId = activeBrandId.value;
		const matchedBrand = brands.value.find((item) => `${item.id}` === `${presetBrandId}`);
		activeBrandId.value = matchedBrand?.id || brands.value[0]?.id || null;
		await loadModels(activeBrandId.value);
	};

	const loadPageData = async () => {
		loading.value = true;
		try {
			categories.value = await fetchDigitalCategories();
			if (!categories.value.some((item) => item.code === activeCategoryCode.value)) {
				activeCategoryCode.value = categories.value[0]?.code || scene.value;
			}
			await loadBrands();
		} catch (error) {
			uni.showToast({ title: error.message || '加载失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	const switchCategory = async (category) => {
		if (!category?.code || category.code === activeCategoryCode.value) {
			return;
		}
		activeCategoryCode.value = category.code;
		scene.value = category.code;
		loading.value = true;
		try {
			await loadBrands();
		} finally {
			loading.value = false;
		}
	};

	const selectBrand = async (brand) => {
		if (!brand?.id || brand.id === activeBrandId.value) {
			return;
		}
		activeBrandId.value = brand.id;
		loading.value = true;
		try {
			await loadModels(brand.id);
		} finally {
			loading.value = false;
		}
	};

	onLoad((options) => {
		scene.value = options.scene === 'luxury' ? 'luxury' : 'digital';
		activeCategoryCode.value = scene.value;
		if (options.keyword) {
			searchKeyword.value = decodeURIComponent(options.keyword);
		}
		if (options.brandId) {
			activeBrandId.value = Number(options.brandId) || options.brandId;
		}
	});

	onShow(() => {
		loadPageData();
	});

	const goSearch = () => {
		const query = [`scene=${activeCategoryCode.value}`];
		if (searchKeyword.value) {
			query.push(`keyword=${encodeURIComponent(searchKeyword.value)}`);
		}
		uni.navigateTo({ url: `/packageRecycle/digital/search?${query.join('&')}` });
	};

	const goSettlement = (item) => {
		if (!item) {
			return;
		}
		const query = [
			`scene=${activeCategoryCode.value}`,
			`brand=${encodeURIComponent(activeBrand.value?.name || '')}`,
			`model=${encodeURIComponent(item.name)}`,
			`price=${encodeURIComponent(item.price)}`,
			`thumbClass=${encodeURIComponent(item.thumbClass || '')}`,
			`specJson=${encodeURIComponent(item.specJson || '')}`
		];
		if (item.id) {
			query.push(`modelId=${item.id}`);
		}
		uni.navigateTo({
			url: `/packageRecycle/digital/spec?${query.join('&')}`
		});
	};
</script>

<style lang="scss">
	.digital-model-page {
		background: #fff;
	}

	.digital-loading {
		padding: 120rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #8a7a7a;
	}

	.digital-empty {
		padding: 80rpx 0;
		text-align: center;
		font-size: 26rpx;
		color: #a29292;
	}

	.digital-search {
		padding: 8rpx 24rpx 0;
	}

	.digital-search__box {
		height: 70rpx;
		border-radius: 999rpx;
		background: #f4efef;
		display: flex;
		align-items: center;
		gap: 12rpx;
		padding: 0 22rpx;
		box-sizing: border-box;
	}

	.digital-search__placeholder {
		font-size: 26rpx;
		color: #918080;
	}

	.digital-type-tabs {
		display: flex;
		align-items: center;
		gap: 34rpx;
		padding: 24rpx 22rpx 14rpx;
		border-bottom: 2rpx solid #f3e8e8;
		overflow-x: auto;
		white-space: nowrap;
	}

	.digital-type-tabs__item {
		position: relative;
		font-size: 28rpx;
		color: #5a4a4a;
		padding-bottom: 12rpx;
		line-height: 1;
		flex-shrink: 0;
	}

	.digital-type-tabs__item.is-active {
		color: #cf1432;
		font-weight: 700;
	}

	.digital-type-tabs__item.is-active::after {
		content: '';
		position: absolute;
		left: 0;
		right: 0;
		bottom: 0;
		height: 4rpx;
		border-radius: 999rpx;
		background: #cf1432;
	}

	.digital-layout {
		display: flex;
		height: calc(100vh - 230rpx);
	}

	.digital-brand-list {
		width: 156rpx;
		background: #f6f2f2;
		flex-shrink: 0;
	}

	.digital-brand-list__item {
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		color: #6a5858;
		position: relative;
		background: #f6f2f2;
		padding: 0 12rpx;
		text-align: center;
	}

	.digital-brand-list__item.is-active {
		background: #fff;
		color: #cf1432;
		font-weight: 700;
	}

	.digital-brand-list__item.is-active::before {
		content: '';
		position: absolute;
		left: 0;
		top: 20rpx;
		bottom: 20rpx;
		width: 6rpx;
		border-radius: 0 999rpx 999rpx 0;
		background: #cf1432;
	}

	.digital-model-list {
		flex: 1;
		background: #fff;
		padding: 14rpx 20rpx 150rpx 18rpx;
		box-sizing: border-box;
	}

	.digital-model-list__head {
		padding: 0 4rpx 12rpx;
	}

	.digital-model-list__brand {
		font-size: 28rpx;
		font-weight: 700;
		color: #453636;
	}

	.digital-model-list__count {
		font-size: 20rpx;
		color: #a29292;
	}

	.digital-model-items {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.digital-model-item {
		display: flex;
		align-items: center;
		gap: 18rpx;
		padding: 12rpx 4rpx;
	}

	.digital-model-item__thumb {
		width: 88rpx;
		height: 88rpx;
		border-radius: 8rpx;
		background: #f8f5f5;
		box-shadow: inset 0 0 0 2rpx #f0e8e8;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.digital-model-item__device {
		width: 34rpx;
		height: 60rpx;
		border-radius: 9rpx;
		background: linear-gradient(180deg, #465059 0%, #1d2027 100%);
		box-shadow: 0 8rpx 16rpx rgba(41, 39, 39, 0.16);
	}

	.is-deep .digital-model-item__device { background: linear-gradient(180deg, #5f676f 0%, #2a2f35 100%); }
	.is-silver .digital-model-item__device { background: linear-gradient(180deg, #d7d0cb 0%, #9a908a 100%); transform: rotate(18deg); }
	.is-purple .digital-model-item__device { background: linear-gradient(180deg, #705275 0%, #2f2432 100%); }
	.is-cyan .digital-model-item__device { background: linear-gradient(180deg, #90d8dc 0%, #4e9aa0 100%); }
	.is-light .digital-model-item__device { background: linear-gradient(180deg, #fbfbfb 0%, #d6d6d6 100%); }
	.is-gold .digital-model-item__device { background: linear-gradient(180deg, #e7c978 0%, #b58a1c 100%); }
	.is-brown .digital-model-item__device { background: linear-gradient(180deg, #9b7a61 0%, #5c4135 100%); }
	.is-camel .digital-model-item__device { background: linear-gradient(180deg, #d3a971 0%, #9a6a37 100%); }
	.is-black .digital-model-item__device { background: linear-gradient(180deg, #4f4f55 0%, #17171b 100%); }
	.is-gray .digital-model-item__device { background: linear-gradient(180deg, #bcbcc3 0%, #74747b 100%); }
	.is-orange .digital-model-item__device { background: linear-gradient(180deg, #f2a65a 0%, #d96b16 100%); }

	.digital-model-item__info {
		flex: 1;
		min-width: 0;
	}

	.digital-model-item__name {
		font-size: 30rpx;
		font-weight: 800;
		color: #2e2424;
		line-height: 1.2;
	}

	.digital-model-item__sub {
		margin-top: 8rpx;
		font-size: 22rpx;
		color: #8a7a7a;
	}

	.digital-model-item__price {
		margin-top: 12rpx;
		font-size: 40rpx;
		font-weight: 800;
		line-height: 1;
		color: #cf1432;
	}

	.digital-model-item__price text {
		font-size: 22rpx;
		font-weight: 500;
	}

	.digital-model-more {
		text-align: center;
		margin-top: 24rpx;
		font-size: 22rpx;
		color: #9e8f8f;
	}

	.digital-model-more__dot {
		width: 8rpx;
		height: 8rpx;
		border-radius: 50%;
		background: #d9d0d0;
		margin: 0 auto 10rpx;
	}

	.digital-estimate-btn {
		position: fixed;
		right: 24rpx;
		bottom: calc(48rpx + env(safe-area-inset-bottom));
		height: 72rpx;
		padding: 0 26rpx;
		border-radius: 999rpx;
		background: linear-gradient(180deg, #ffc81b 0%, #f5b900 100%);
		box-shadow: 0 16rpx 28rpx rgba(255, 191, 31, 0.24);
		display: flex;
		align-items: center;
		gap: 10rpx;
		font-size: 28rpx;
		font-weight: 700;
		color: #6d5200;
		z-index: 12;
	}
</style>
