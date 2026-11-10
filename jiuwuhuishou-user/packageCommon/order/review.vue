<template>
	<view class="app-page review-page">
		<top-nav title="评价与投诉"></top-nav>
		<scroll-view scroll-y class="page-body compact review-body">
			<view class="card hero-soft">
				<view style="font-size: 40rpx; font-weight: 800;">完成后别让体验沉默</view>
				<view class="muted" style="margin-top: 14rpx;">正向评价帮助服务优化，异常投诉帮助平台追责，二者都应该留在订单闭环里。</view>
			</view>

			<view v-if="reviewed" class="card review-done-card">
				<view style="font-size: 32rpx; font-weight: 700;">您已提交评价</view>
				<view class="muted" style="margin-top: 12rpx;">感谢反馈，平台将持续优化上门回收服务。</view>
			</view>

			<template v-else>
				<view class="section-title"><text>服务评分</text></view>
				<view class="card score-card">
					<view class="score-stars">
						<view v-for="item in 5" :key="item" class="score-star" @click="score = item">
							<u-icon :name="score >= item ? 'star-fill' : 'star'" size="28" :color="score >= item ? '#f5b21e' : '#d8cfcf'"></u-icon>
						</view>
					</view>
					<view class="muted" style="margin-top: 14rpx; text-align:center;">{{ scoreText }}</view>
				</view>

				<view class="section-title"><text>投诉类型</text></view>
				<view class="card"><view class="tag-grid"><view v-for="item in complaintTypes" :key="item" class="tag-chip" :class="{ 'is-active': selectedType === item }" @click="selectedType = item">{{ item }}</view></view></view>

				<view class="section-title"><text>补充说明</text></view>
				<view class="card"><textarea v-model="remark" maxlength="200" class="remark-box" placeholder="例如：价格解释不清、未按时上门、服务态度一般" auto-height /></view>
			</template>
		</scroll-view>
		<view class="footer-bar">
			<view class="btn-ghost" @click="go('/packageCommon/order/payout')">返回打款</view>
			<view v-if="!reviewed" class="btn-main" @click="submit">提交评价</view>
			<view v-else class="btn-main" @click="goOrderList">返回订单</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import { loadReviewPageData, submitOrderReview } from '@/common/services/review.js';

	const orderId = ref('');
	const reviewed = ref(false);
	const score = ref(5);
	const complaintTypes = ['价格争议', '上门迟到', '服务态度', '物品损坏', '其他'];
	const selectedType = ref('价格争议');
	const remark = ref('');
	const scoreText = computed(() => {
		if (score.value >= 5) return '非常满意，愿意再次使用';
		if (score.value >= 4) return '整体满意，仍有优化空间';
		if (score.value >= 3) return '体验一般，需要继续优化';
		return '体验较差，建议平台重点跟进';
	});

	const syncPage = async () => {
		if (!orderId.value) {
			return;
		}
		try {
			const data = await loadReviewPageData(orderId.value);
			reviewed.value = data.reviewed;
			if (data.reviewed) {
				score.value = data.score || 5;
				selectedType.value = data.complaintType || selectedType.value;
				remark.value = data.content || '';
			}
		} catch (error) {
			uni.showToast({ title: error?.message || '加载失败', icon: 'none' });
		}
	};

	onLoad(async (options) => {
		orderId.value = options.id || '';
		await syncPage();
	});

	const submit = async () => {
		try {
			await submitOrderReview({
				orderId: orderId.value,
				score: score.value,
				complaintType: selectedType.value,
				content: remark.value
			});
			reviewed.value = true;
			uni.showToast({ title: '评价已提交', icon: 'success' });
		} catch (error) {
			uni.showToast({ title: error?.message || '提交失败', icon: 'none' });
		}
	};
	const go = (url) => uni.navigateTo({ url: `${url}?id=${orderId.value}` });
	const goOrderList = () => uni.switchTab({ url: '/pages/order/order' });
</script>

<style lang="scss">
	.review-page { background: linear-gradient(180deg, #fff8f3 0%, #fff 320rpx); }
	.review-body { padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
	.review-done-card { margin-top: 24rpx; }
	.score-card { text-align: center; }
	.score-stars { display: flex; align-items: center; justify-content: center; gap: 18rpx; }
	.score-star { width: 64rpx; height: 64rpx; border-radius: 50%; background: #fff5ef; display: flex; align-items: center; justify-content: center; }
	.tag-grid { display: flex; flex-wrap: wrap; gap: 16rpx; }
	.tag-chip { padding: 16rpx 22rpx; border-radius: 20rpx; background: #f8f3f3; font-size: 26rpx; color: #4d3c3c; }
	.tag-chip.is-active { background: #fff1f3; color: #cf1432; }
	.remark-box { width: 100%; min-height: 220rpx; padding: 24rpx; border-radius: 24rpx; background: #fff8f8; font-size: 28rpx; color: #2f2424; }
</style>
