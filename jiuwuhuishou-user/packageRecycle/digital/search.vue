<template>
	<view class="app-page app-page--blush digital-search-page">
		<top-nav :title="pageTitle" title-color="#1f1f1f"></top-nav>
		<scroll-view scroll-y class="page-body compact">
			<view class="row-between digital-search-bar">
				<view class="ghost-panel digital-search-bar__input">
					<u-icon name="search" size="22" color="#7a6161"></u-icon>
					<input
						v-model="keyword"
						class="digital-search-bar__field"
						type="text"
						confirm-type="search"
						:placeholder="searchPlaceholder"
						placeholder-class="digital-search-bar__placeholder"
						@confirm="submitSearch"
					/>
				</view>
				<text class="brand-text digital-search-bar__action" @click="submitSearch">{{ searching ? '搜索中' : '搜索' }}</text>
			</view>

			<view v-if="loading" class="digital-search-loading">加载中...</view>

			<template v-else>
				<view v-if="showResults" class="digital-search-results">
					<view class="section-title"><text>搜索结果</text><text class="hint">共 {{ results.length }} 条</text></view>
					<view v-if="!results.length" class="digital-search-empty">未找到匹配机型，请换个关键词试试</view>
					<view v-else class="feature-list">
						<view v-for="item in results" :key="item.id" class="feature-row digital-search-result" @click="goSpec(item)">
							<view>
								<view style="font-size: 34rpx; font-weight: 700;">{{ item.name }}</view>
								<view class="muted" style="margin-top: 8rpx;">{{ item.brandName }} · {{ item.sub }}</view>
							</view>
							<view class="digital-search-result__price">{{ item.price }}</view>
						</view>
					</view>
				</view>

				<template v-else>
					<view class="section-title"><text>热门搜索</text></view>
					<view class="digital-search-tags">
						<view
							v-for="item in hotKeywords"
							:key="item"
							class="tab-pill"
							:class="{ active: item === keyword }"
							@click="searchByKeyword(item)"
						>
							{{ item }}
						</view>
					</view>

					<view class="section-title">
						<text>历史记录</text>
						<text v-if="history.length" class="hint" @click="clearHistory">清空</text>
					</view>
					<view v-if="history.length" class="feature-list">
						<view v-for="item in history" :key="item" class="feature-row" @click="searchByKeyword(item)">
							<view class="row-between" style="justify-content: flex-start;">
								<u-icon name="clock" size="20" color="#c1b3b3"></u-icon>
								<text style="font-size: 34rpx;">{{ item }}</text>
							</view>
						</view>
					</view>
					<view v-else class="digital-search-empty">暂无搜索历史</view>

					<view class="section-title"><text>按品牌找</text></view>
					<view class="grid-two digital-search-brands">
						<view
							v-for="item in brands"
							:key="item.id"
							class="service-card digital-search-brand"
							@click="goBrandModels(item)"
						>
							<view class="dot-badge"><u-icon name="grid" size="22" color="#1f1f1f"></u-icon></view>
							<view class="title">{{ item.name }}</view>
						</view>
					</view>
				</template>
			</template>
		</scroll-view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import {
		addSearchHistory,
		clearSearchHistory,
		loadDigitalSearchPageData,
		searchDigitalModels
	} from '@/common/services/digital.js';

	const scene = ref('digital');
	const loading = ref(false);
	const searching = ref(false);
	const keyword = ref('');
	const hotKeywords = ref([]);
	const history = ref([]);
	const brands = ref([]);
	const results = ref([]);
	const showResults = ref(false);

	const pageTitle = computed(() => (scene.value === 'luxury' ? '搜索奢侈品' : '搜索机型'));
	const searchPlaceholder = computed(() =>
		scene.value === 'luxury' ? '搜索品牌、品类' : '搜索机型、品牌'
	);

	const loadPageData = async () => {
		loading.value = true;
		try {
			const data = await loadDigitalSearchPageData(scene.value);
			hotKeywords.value = data.hotKeywords;
			brands.value = data.brands;
			history.value = data.history;
		} catch (error) {
			hotKeywords.value = [];
			brands.value = [];
			history.value = [];
			uni.showToast({ title: error.message || '加载失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	const buildSpecUrl = (item) => {
		const query = [
			`scene=${scene.value}`,
			`brand=${encodeURIComponent(item.brandName || '')}`,
			`model=${encodeURIComponent(item.name)}`,
			`price=${encodeURIComponent(item.price)}`,
			`thumbClass=${encodeURIComponent(item.thumbClass || '')}`,
			`specJson=${encodeURIComponent(item.specJson || '')}`
		];
		if (item.id) {
			query.push(`modelId=${item.id}`);
		}
		return `/packageRecycle/digital/spec?${query.join('&')}`;
	};

	const goSpec = (item) => {
		if (!item) {
			return;
		}
		uni.navigateTo({ url: buildSpecUrl(item) });
	};

	const goBrandModels = (brand) => {
		if (!brand?.id) {
			return;
		}
		uni.navigateTo({
			url: `/packageRecycle/digital/models?scene=${scene.value}&brandId=${brand.id}`
		});
	};

	const submitSearch = async () => {
		const text = `${keyword.value || ''}`.trim();
		if (!text) {
			showResults.value = false;
			results.value = [];
			return;
		}
		searching.value = true;
		try {
			results.value = await searchDigitalModels({ keyword: text, scene: scene.value });
			history.value = addSearchHistory(scene.value, text);
			showResults.value = true;
		} catch (error) {
			results.value = [];
			showResults.value = true;
			uni.showToast({ title: error.message || '搜索失败', icon: 'none' });
		} finally {
			searching.value = false;
		}
	};

	const searchByKeyword = async (text) => {
		keyword.value = text;
		await submitSearch();
	};

	const clearHistory = () => {
		history.value = clearSearchHistory(scene.value);
	};

	onLoad((options) => {
		scene.value = options.scene === 'luxury' ? 'luxury' : 'digital';
		if (options.keyword) {
			keyword.value = decodeURIComponent(options.keyword);
		}
	});

	onShow(async () => {
		await loadPageData();
		if (`${keyword.value || ''}`.trim()) {
			await submitSearch();
		}
	});
</script>

<style lang="scss">
	.digital-search-page {
		background: #fff8f5;
	}

	.digital-search-bar {
		gap: 18rpx;
		align-items: center;
	}

	.digital-search-bar__input {
		flex: 1;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		gap: 16rpx;
		padding: 0 24rpx;
		min-height: 76rpx;
		box-sizing: border-box;
	}

	.digital-search-bar__field {
		flex: 1;
		height: 76rpx;
		font-size: 28rpx;
		color: #2f2424;
	}

	.digital-search-bar__placeholder {
		color: #b6a08d;
		font-size: 28rpx;
	}

	.digital-search-bar__action {
		font-size: 34rpx;
		font-weight: 700;
		flex-shrink: 0;
	}

	.digital-search-loading,
	.digital-search-empty {
		padding: 80rpx 0;
		text-align: center;
		font-size: 26rpx;
		color: #9a8888;
	}

	.digital-search-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 18rpx;
	}

	.digital-search-tags .tab-pill {
		padding: 18rpx 26rpx;
	}

	.digital-search-brands {
		grid-template-columns: repeat(4, minmax(0, 1fr));
	}

	.digital-search-brand {
		text-align: center;
		padding: 28rpx 12rpx;
	}

	.digital-search-brand .dot-badge {
		margin: 0 auto;
	}

	.digital-search-brand .title {
		font-size: 26rpx;
		margin-top: 12rpx;
	}

	.digital-search-result {
		align-items: center;
	}

	.digital-search-result__price {
		font-size: 34rpx;
		font-weight: 800;
		color: #cf1432;
		flex-shrink: 0;
	}
</style>
