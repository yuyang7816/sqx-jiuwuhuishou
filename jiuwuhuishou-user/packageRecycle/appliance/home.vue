<template>
	<view class="app-page app-page--mint">
		<top-nav title="家电回收" :show-more="false"></top-nav>
		<scroll-view scroll-y class="page-body compact">
			<view class="card hero-soft">
				<view class="row-between">
					<view style="flex: 1;">
						<view class="brand-pill">{{ hero.badge }}</view>
						<view style="margin-top: 18rpx; font-size: 52rpx; font-weight: 800; line-height: 1.16;">{{ hero.titleLine1 }}</view>
						<view style="font-size: 52rpx; font-weight: 800; line-height: 1.16; color: #c8102e;">{{ hero.titleLine2 }}</view>
					</view>
					<view style="width: 270rpx;">
						<image v-if="hero.imageUrl" :src="hero.imageUrl" mode="aspectFill" class="hero-image"></image>
						<mock-art v-else label="冰箱/洗衣机" desc="环保估价" theme="mint"></mock-art>
					</view>
				</view>
				<view class="flow-row" style="margin-top: 28rpx;">
					<view class="flow-item" v-for="item in hero.flows" :key="item.name">
						<view class="dot-badge" style="margin: 0 auto 10rpx;">
							<image
								v-if="item.iconIsImage"
								:src="item.icon"
								mode="aspectFit"
								class="flow-icon-img"
							></image>
							<u-icon v-else :name="item.icon || 'star'" size="18" color="#c8102e"></u-icon>
						</view>
						<view class="txt">{{ item.name }}</view>
					</view>
				</view>
			</view>

			<view class="tab-pills" style="margin-top: 28rpx;">
				<view
					v-for="cate in categories"
					:key="cate.id || cate.name"
					class="tab-pill"
					:class="{ active: currentCategoryId === cate.id }"
					@click="selectCategory(cate)"
				>
					{{ cate.name }}
				</view>
			</view>

			<view class="card" style="margin-top: 24rpx; text-align: center;">
				<view class="dot-badge" style="margin: 0 auto; width: 116rpx; height: 116rpx;">
					<u-icon :name="activeCategory.priceIcon" size="40" color="#c8102e"></u-icon>
				</view>
				<view style="margin-top: 24rpx; font-size: 40rpx; font-weight: 800;">{{ activeCategory.priceTitle }}</view>
				<view class="muted" style="margin-top: 10rpx;">当前品类平均回收价</view>
				<view class="price-main" style="margin-top: 20rpx;">{{ activeCategory.price }}</view>
			</view>

			<view class="section-title"><text>回收去向科普</text><text class="hint" @click="go('/packageRecycle/appliance/more')">了解更多 ></text></view>
			<view class="grid-two">
				<view class="service-card" v-for="item in science" :key="item.id || item.title">
					<image v-if="item.imageUrl" :src="item.imageUrl" mode="aspectFit" class="science-card__image"></image>
					<view v-else class="dot-badge"><u-icon :name="item.icon" size="20" :color="item.color"></u-icon></view>
					<view class="title">{{ item.title }}</view>
					<view class="desc">{{ item.desc }}</view>
				</view>
			</view>

			<view class="section-title"><text>常见问题</text></view>
			<view class="feature-list">
				<view class="faq-item" v-for="(faq, index) in faqs" :key="faq.id || faq.q" @click="toggleFaq(index)">
					<view class="faq-question"><text>{{ faq.q }}</text><u-icon :name="faq.open ? 'arrow-up' : 'arrow-down'" size="16" color="#7a6161"></u-icon></view>
					<view v-if="faq.open" class="faq-answer">{{ faq.a }}</view>
				</view>
			</view>
		</scroll-view>
		<floating-service></floating-service>
		<view class="footer-bar one">
			<view class="btn-main" @click="goEstimate">马上预估回收</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { loadApplianceHomeData } from '@/common/services/appliance.js';

	const hero = ref({
		badge: '',
		titleLine1: '',
		titleLine2: '',
		imageUrl: '',
		flows: []
	});
	const categories = ref([]);
	const science = ref([]);
	const faqs = ref([]);
	const currentCategoryId = ref(null);

	const activeCategory = computed(() => {
		return categories.value.find((item) => item.id === currentCategoryId.value) || categories.value[0] || {};
	});

	const selectCategory = (category) => {
		currentCategoryId.value = category.id;
	};

	const toggleFaq = (index) => {
		faqs.value[index].open = !faqs.value[index].open;
	};

	const goEstimate = () => {
		const categoryName = activeCategory.value?.name || '空调';
		uni.navigateTo({ url: `/packageRecycle/appliance/model-select?category=${encodeURIComponent(categoryName)}` });
	};

	const go = (url) => uni.navigateTo({ url });

	const loadPageData = async () => {
		try {
			const data = await loadApplianceHomeData();
			hero.value = data.hero;
			categories.value = data.categories;
			science.value = data.science;
			faqs.value = data.faqs;
			if (!currentCategoryId.value && categories.value.length) {
				currentCategoryId.value = categories.value[0].id;
			}
		} catch (error) {
			uni.showToast({ title: error.message || '页面数据加载失败', icon: 'none' });
		}
	};

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.hero-image {
		width: 270rpx;
		height: 220rpx;
		border-radius: 24rpx;
	}
	.flow-icon-img {
		width: 36rpx;
		height: 36rpx;
		display: block;
	}
	.science-card__image {
		width: 88rpx;
		height: 88rpx;
		display: block;
		margin-bottom: 8rpx;
	}
</style>
