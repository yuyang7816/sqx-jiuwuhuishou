<template>
	<view class="app-page appliance-more-page">
		<top-nav title="回收规则详情"></top-nav>

		<scroll-view scroll-y class="page-body compact appliance-more-body">
			<view class="more-hero">
				<view class="more-hero__icon">
					<u-icon :name="hero.icon" size="40" color="#ffffff"></u-icon>
				</view>
				<view class="more-hero__title">{{ hero.title }}</view>
				<view class="more-hero__desc">{{ hero.desc }}</view>
			</view>

			<view class="more-section">
				<view class="more-section__head">
					<view class="more-section__bar"></view>
					<text class="more-section__title">家电分类计价标准</text>
				</view>
				<view class="more-card-list">
					<view v-for="group in pricingGroups" :key="group.id || group.title" class="price-card">
						<view class="price-card__head row-between">
							<view class="price-card__head-left">
								<image
									v-if="group.imageUrl"
									:src="group.imageUrl"
									mode="aspectFit"
									class="price-card__icon-image"
								></image>
								<u-icon v-else :name="group.icon" size="18" color="#d11f37"></u-icon>
								<text class="price-card__title">{{ group.title }}</text>
							</view>
							<text class="price-card__top">{{ group.top }}</text>
						</view>
						<view class="price-card__items">
							<view v-for="item in group.items" :key="item.name" class="price-card__item row-between">
								<text class="price-card__item-name">{{ item.name }}</text>
								<text class="price-card__item-price">{{ item.price }}</text>
							</view>
						</view>
					</view>
				</view>
				<view class="more-note">{{ priceNote }}</view>
			</view>

			<view class="more-section">
				<view class="more-section__head">
					<view class="more-section__bar"></view>
					<text class="more-section__title">服务及增值费说明</text>
				</view>
				<view
					v-for="service in serviceFees"
					:key="service.id || service.title"
					class="service-card-more"
					:class="service.theme === 'pink' ? 'service-card-more--pink' : 'service-card-more--gold'"
				>
					<view class="service-card-more__accent" :class="service.theme === 'pink' ? 'is-red' : 'is-gold'"></view>
					<view class="service-card-more__head row-between" style="justify-content:flex-start;">
						<view class="service-card-more__icon" :class="service.theme === 'pink' ? 'is-pink-bg' : 'is-gold-bg'">
							<image v-if="service.imageUrl" :src="service.imageUrl" mode="aspectFit" class="service-card-more__image"></image>
							<u-icon v-else :name="service.icon" size="20" :color="service.theme === 'pink' ? '#8a3434' : '#8a6400'"></u-icon>
						</view>
						<view>
							<view class="service-card-more__title">{{ service.title }}</view>
							<view class="service-card-more__desc">{{ service.desc }}</view>
						</view>
					</view>
					<view v-if="service.lines.length" class="service-card-more__list">
						<view v-for="line in service.lines" :key="line">• {{ line }}</view>
					</view>
					<view v-if="service.tags.length" class="service-tags">
						<view v-for="tag in service.tags" :key="tag.name" class="service-tags__item">
							<view>{{ tag.name }}</view>
							<text>{{ tag.price }}</text>
						</view>
					</view>
				</view>
			</view>

			<view class="more-section">
				<view class="more-section__head">
					<view class="more-section__bar"></view>
					<text class="more-section__title">温馨提示</text>
				</view>
				<view class="tip-list">
					<view v-for="(faq, index) in tips" :key="faq.id || faq.q" class="tip-item" @click="toggle(index)">
						<view class="tip-item__question row-between">
							<text>{{ faq.q }}</text>
							<u-icon :name="faq.open ? 'arrow-up' : 'arrow-down'" size="16" color="#7b6666"></u-icon>
						</view>
						<view v-if="faq.open" class="tip-item__answer">{{ faq.a }}</view>
					</view>
				</view>
			</view>

			<view class="carbon-card">
				<image src="/static/image/appliance-more-carbon-icon.png" mode="aspectFit" class="carbon-card__icon-image"></image>
				<view class="carbon-card__title">{{ carbon.title }}</view>
				<view class="carbon-card__desc">{{ carbon.desc }}</view>
			</view>
		</scroll-view>

		<view class="appliance-more-service-entry" @click="go('/packageCommon/service/chat')">
			<image src="/static/image/appliance-more-service-button.png" mode="aspectFit" class="appliance-more-service-entry__image"></image>
		</view>

		<view class="footer-bar appliance-more-footer">
			<view class="btn-ghost appliance-more-footer__ghost" @click="goBack">返回查看</view>
			<view class="btn-main appliance-more-footer__main" @click="goEstimate">立即预约回收</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { loadApplianceMoreData } from '@/common/services/appliance.js';

	const hero = ref({
		title: '',
		desc: '',
		icon: 'info-circle'
	});
	const pricingGroups = ref([]);
	const serviceFees = ref([]);
	const tips = ref([]);
	const carbon = ref({
		title: '',
		desc: ''
	});
	const priceNote = ref('');
	const bookingCategory = ref('冰箱');

	const toggle = (index) => {
		tips.value[index].open = !tips.value[index].open;
	};

	const go = (url) => {
		uni.navigateTo({ url });
	};

	const goEstimate = () => {
		go(`/packageRecycle/appliance/model-select?category=${encodeURIComponent(bookingCategory.value || '冰箱')}`);
	};

	const goBack = () => {
		uni.navigateBack({
			fail() {
				uni.navigateTo({ url: '/packageRecycle/appliance/home' });
			}
		});
	};

	const loadPageData = async () => {
		try {
			const data = await loadApplianceMoreData();
			hero.value = data.hero;
			pricingGroups.value = data.pricingGroups;
			serviceFees.value = data.serviceFees;
			tips.value = data.tips;
			carbon.value = data.carbon;
			priceNote.value = data.priceNote;
			bookingCategory.value = data.bookingCategory;
		} catch (error) {
			uni.showToast({ title: error.message || '页面数据加载失败', icon: 'none' });
		}
	};

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.appliance-more-page {
		background: linear-gradient(180deg, #fff8f6 0%, #fff 360rpx);
	}

	.appliance-more-body {
		padding-top: 14rpx;
		padding-bottom: calc(190rpx + env(safe-area-inset-bottom));
	}

	.more-hero {
		background: linear-gradient(180deg, #e83444 0%, #dc2d3f 100%);
		border-radius: 24rpx;
		padding: 34rpx 26rpx 30rpx;
		text-align: center;
		box-shadow: 0 18rpx 36rpx rgba(220, 45, 63, 0.18);
	}

	.more-hero__icon {
		width: 84rpx;
		height: 84rpx;
		border-radius: 50%;
		border: 4rpx solid rgba(255,255,255,.94);
		margin: 0 auto;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.more-hero__title {
		margin-top: 18rpx;
		font-size: 34rpx;
		font-weight: 700;
		color: #fff;
	}

	.more-hero__desc {
		margin-top: 14rpx;
		font-size: 24rpx;
		line-height: 1.6;
		color: rgba(255,255,255,.92);
	}

	.more-section {
		margin-top: 28rpx;
	}

	.more-section__head {
		display: flex;
		align-items: center;
		gap: 10rpx;
		margin-bottom: 18rpx;
	}

	.more-section__bar {
		width: 6rpx;
		height: 30rpx;
		border-radius: 999rpx;
		background: #cf1432;
	}

	.more-section__title {
		font-size: 32rpx;
		font-weight: 700;
		color: #322727;
	}

	.more-card-list {
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.price-card {
		background: #fff;
		border-radius: 24rpx;
		padding: 22rpx 22rpx 18rpx;
		border: 2rpx solid #f4d0d5;
		box-shadow: 0 12rpx 28rpx rgba(111, 79, 79, 0.05);
	}

	.price-card__head-left {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.price-card__icon-image {
		width: 36rpx;
		height: 36rpx;
		flex-shrink: 0;
	}

	.price-card__title {
		font-size: 30rpx;
		font-weight: 700;
		color: #403131;
	}

	.price-card__top {
		font-size: 30rpx;
		font-weight: 800;
		color: #8a6400;
	}

	.price-card__items {
		margin-top: 16rpx;
		display: flex;
		flex-direction: column;
		gap: 12rpx;
	}

	.price-card__item-name {
		font-size: 26rpx;
		color: #655353;
	}

	.price-card__item-price {
		font-size: 26rpx;
		font-weight: 700;
		color: #d11f37;
	}

	.more-note {
		margin-top: 14rpx;
		font-size: 22rpx;
		color: #8f7b7b;
		line-height: 1.6;
	}

	.service-card-more {
		position: relative;
		background: #fff;
		border-radius: 24rpx;
		padding: 24rpx 22rpx 22rpx;
		box-shadow: 0 12rpx 28rpx rgba(111, 79, 79, 0.05);
		overflow: hidden;
	}

	.service-card-more + .service-card-more {
		margin-top: 18rpx;
	}

	.service-card-more__accent {
		position: absolute;
		left: 0;
		top: 20rpx;
		bottom: 20rpx;
		width: 6rpx;
		border-radius: 0 999rpx 999rpx 0;
	}

	.service-card-more__accent.is-gold {
		background: #8a6400;
	}

	.service-card-more__accent.is-red {
		background: #cf1432;
	}

	.service-card-more__icon {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 16rpx;
		flex-shrink: 0;
	}

	.service-card-more__image {
		width: 52rpx;
		height: 52rpx;
		display: block;
	}

	.is-gold-bg { background: #ffe7a8; }
	.is-pink-bg { background: #ffe0e3; }

	.service-card-more__title {
		font-size: 30rpx;
		font-weight: 700;
		color: #392d2d;
	}

	.service-card-more__desc {
		margin-top: 8rpx;
		font-size: 24rpx;
		line-height: 1.6;
		color: #6e5b5b;
	}

	.service-card-more__list {
		margin-top: 14rpx;
		padding-left: 86rpx;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
		font-size: 22rpx;
		line-height: 1.5;
		color: #786767;
	}

	.service-tags {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 14rpx;
		margin-top: 18rpx;
		padding-left: 86rpx;
	}

	.service-tags__item {
		background: #f7f2f2;
		border-radius: 12rpx;
		padding: 12rpx 14rpx;
		font-size: 22rpx;
		color: #5f4d4d;
		line-height: 1.5;
	}

	.service-tags__item text {
		display: block;
		margin-top: 6rpx;
		font-weight: 700;
		color: #cf1432;
	}

	.tip-list {
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.tip-item {
		background: #fff;
		border-radius: 20rpx;
		padding: 22rpx 24rpx;
		border: 2rpx solid #f2dcdc;
	}

	.tip-item__question {
		font-size: 28rpx;
		font-weight: 600;
		color: #3f3131;
	}

	.tip-item__answer {
		margin-top: 16rpx;
		font-size: 24rpx;
		line-height: 1.7;
		color: #746161;
	}

	.carbon-card {
		margin-top: 28rpx;
		background: linear-gradient(180deg, #7d7d7d 0%, #707070 100%);
		border-radius: 24rpx;
		padding: 28rpx 24rpx 26rpx;
		text-align: center;
		color: #fff;
	}

	.carbon-card__icon-image {
		width: 60rpx;
		height: 60rpx;
		display: block;
		margin: 0 auto;
	}

	.carbon-card__title {
		margin-top: 14rpx;
		font-size: 30rpx;
		font-weight: 700;
	}

	.carbon-card__desc {
		margin-top: 10rpx;
		font-size: 22rpx;
		color: rgba(255,255,255,.82);
	}

	.appliance-more-service-entry {
		position: fixed;
		right: 28rpx;
		bottom: calc(180rpx + env(safe-area-inset-bottom));
		z-index: 18;
		width: 108rpx;
		height: 108rpx;
	}

	.appliance-more-service-entry__image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.appliance-more-footer {
		background: rgba(255,255,255,.98);
		gap: 18rpx;
	}

	.appliance-more-footer__ghost,
	.appliance-more-footer__main {
		height: 88rpx;
		font-size: 28rpx;
	}

	.appliance-more-footer__ghost {
		flex: 1;
		color: #cf1432;
		border-color: #cf1432;
	}

	.appliance-more-footer__main {
		flex: 1.2;
	}
</style>
