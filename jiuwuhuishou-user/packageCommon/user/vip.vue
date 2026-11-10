<template>
	<view class="app-page vip-page">
		<top-nav title="会员中心"></top-nav>
		<scroll-view scroll-y class="page-body compact vip-body">
			<view class="vip-hero" :class="{ 'is-active': pageData.isVip }">
				<view class="vip-hero__badge">{{ pageData.isVip ? 'VIP 会员' : '开通会员' }}</view>
				<view class="vip-hero__title">{{ pageData.isVip ? '您已享受会员权益' : '升级会员，回收更省心' }}</view>
				<view class="vip-hero__desc">优先上门、专属客服、加价回收等多项权益等你解锁</view>
			</view>

			<view class="section-title"><text>会员套餐</text></view>
			<view class="vip-plan-list">
				<view
					v-for="plan in pageData.plans"
					:key="plan.id"
					class="vip-plan-card"
					:class="{ 'is-selected': selectedPlanId === plan.id }"
					@click="selectedPlanId = plan.id"
				>
					<view class="vip-plan-card__name">{{ plan.name }}</view>
					<view class="vip-plan-card__price">
						<text class="vip-plan-card__amount">￥{{ plan.amountText }}</text>
						<text v-if="plan.originalAmountText" class="vip-plan-card__origin">￥{{ plan.originalAmountText }}</text>
					</view>
					<view class="vip-plan-card__month">{{ plan.month }} 个月</view>
				</view>
			</view>

			<view class="section-title"><text>会员权益</text></view>
			<view class="vip-right-grid">
				<view v-for="right in pageData.rights" :key="right.id" class="vip-right-item">
					<image v-if="right.img" :src="right.img" mode="aspectFill" class="vip-right-item__img"></image>
					<view v-else class="vip-right-item__icon">
						<u-icon name="star-fill" size="18" color="#d1a056"></u-icon>
					</view>
					<text class="vip-right-item__name">{{ right.name }}</text>
				</view>
			</view>
		</scroll-view>

		<view class="footer-bar one">
			<view class="btn-main" :class="{ 'is-disabled': pageData.isVip || paying }" @click="handlePurchase">
				{{ pageData.isVip ? '已是会员' : paying ? '支付中...' : '立即开通' }}
			</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { loadVipPageData, purchaseVipPlan } from '@/common/services/vip.js';

	const loading = ref(false);
	const paying = ref(false);
	const pageData = ref({
		isVip: false,
		plans: [],
		rights: []
	});
	const selectedPlanId = ref(null);

	const selectedPlan = computed(() => pageData.value.plans.find((item) => item.id === selectedPlanId.value) || null);

	const loadPage = async () => {
		if (!ensureLoggedIn()) {
			return;
		}
		loading.value = true;
		try {
			const data = await loadVipPageData();
			pageData.value = data;
			selectedPlanId.value = data.plans[0]?.id || null;
		} catch (error) {
			uni.showToast({ title: error.message || '加载会员信息失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	const handlePurchase = async () => {
		if (pageData.value.isVip || paying.value) {
			return;
		}
		if (!selectedPlan.value) {
			uni.showToast({ title: '请选择会员套餐', icon: 'none' });
			return;
		}
		paying.value = true;
		try {
			await purchaseVipPlan(selectedPlan.value);
			uni.showToast({ title: '支付成功', icon: 'success' });
			await loadPage();
		} catch (error) {
			uni.showToast({ title: error.message || '支付失败', icon: 'none' });
		} finally {
			paying.value = false;
		}
	};

	onShow(() => {
		loadPage();
	});
</script>

<style lang="scss">
	.vip-page { background: linear-gradient(180deg, #fff8ef 0%, #fff 360rpx); }
	.vip-body { padding-top: 14rpx; padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
	.vip-hero { padding: 30rpx 28rpx; border-radius: 30rpx; background: linear-gradient(135deg, #3a2f2f 0%, #5b4030 100%); color: #fff; box-shadow: 0 18rpx 40rpx rgba(91, 64, 48, 0.22); }
	.vip-hero.is-active { background: linear-gradient(135deg, #d1a056 0%, #b8842f 100%); }
	.vip-hero__badge { display: inline-flex; padding: 8rpx 18rpx; border-radius: 999rpx; background: rgba(255, 255, 255, 0.16); font-size: 22rpx; font-weight: 700; }
	.vip-hero__title { margin-top: 18rpx; font-size: 40rpx; font-weight: 800; }
	.vip-hero__desc { margin-top: 12rpx; font-size: 24rpx; line-height: 1.7; color: rgba(255, 255, 255, 0.86); }
	.vip-plan-list { display: flex; flex-direction: column; gap: 16rpx; }
	.vip-plan-card { background: #fff; border-radius: 24rpx; padding: 24rpx; box-shadow: 0 12rpx 28rpx rgba(111, 79, 79, 0.06); border: 2rpx solid transparent; }
	.vip-plan-card.is-selected { border-color: #d1a056; background: #fffaf2; }
	.vip-plan-card__name { font-size: 30rpx; font-weight: 700; color: #3d3030; }
	.vip-plan-card__price { margin-top: 12rpx; display: flex; align-items: baseline; gap: 12rpx; }
	.vip-plan-card__amount { font-size: 40rpx; font-weight: 800; color: #cf1432; }
	.vip-plan-card__origin { font-size: 24rpx; color: #b0a0a0; text-decoration: line-through; }
	.vip-plan-card__month { margin-top: 8rpx; font-size: 24rpx; color: #8d7d7d; }
	.vip-right-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16rpx; }
	.vip-right-item { background: #fff; border-radius: 22rpx; padding: 22rpx 18rpx; text-align: center; box-shadow: 0 10rpx 24rpx rgba(111, 79, 79, 0.05); }
	.vip-right-item__img, .vip-right-item__icon { width: 64rpx; height: 64rpx; margin: 0 auto 12rpx; border-radius: 50%; }
	.vip-right-item__icon { background: #fff5e8; display: flex; align-items: center; justify-content: center; }
	.vip-right-item__name { font-size: 24rpx; color: #4d3c3c; }
	.btn-main.is-disabled { opacity: 0.55; }
</style>
