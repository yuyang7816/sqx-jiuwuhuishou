<template>
	<view class="app-page exception-page">
		<top-nav title="异常反馈"></top-nav>
		<scroll-view scroll-y class="page-body compact exception-body">
			<view class="card hero-soft">
				<view style="font-size: 40rpx; font-weight: 800;">履约异常，优先留痕再处理</view>
				<view class="muted" style="margin-top: 14rpx;">提交异常后，平台可按类型分流给客服、履约或质检同学，减少用户来回描述。</view>
			</view>

			<view class="section-title"><text>异常类型</text></view>
			<view class="card"><view class="option-grid"><view v-for="item in types" :key="item" class="option-chip" :class="{ 'is-active': type === item }" @click="type = item">{{ item }}</view></view></view>

			<view class="section-title"><text>期望处理方式</text></view>
			<view class="card"><view class="option-grid"><view v-for="item in expects" :key="item" class="option-chip" :class="{ 'is-active': expected === item }" @click="expected = item">{{ item }}</view></view></view>

			<view class="section-title"><text>问题描述</text></view>
			<view class="card"><textarea v-model="remark" maxlength="200" class="remark-box" placeholder="请补充问题经过，例如：约定 14:00 上门，15:30 仍无人联系" auto-height /></view>
		</scroll-view>

		<view class="footer-bar">
			<view class="btn-ghost" @click="goDetail">返回订单</view>
			<view class="btn-main" @click="submit">提交反馈</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import { submitExceptionById } from '@/common/services/order.js';
	import { loadExceptionOptions } from '@/common/services/orderOptions.js';
	const orderId = ref('');
	const types = ref([]);
	const expects = ref([]);
	const type = ref('');
	const expected = ref('');
	const remark = ref('');
	onLoad(async (options) => {
		orderId.value = options.id || '';
		const optionsData = await loadExceptionOptions();
		types.value = optionsData.types;
		expects.value = optionsData.expects;
		type.value = optionsData.types[0] || '';
		expected.value = optionsData.expects[0] || '';
	});
	const submit = async () => {
		try {
			await submitExceptionById({
				orderId: orderId.value,
				exceptionType: type.value,
				expectAction: expected.value,
				description: remark.value
			});
			uni.showToast({ title: '异常反馈已提交', icon: 'success' });
			setTimeout(() => {
				uni.navigateTo({ url: `/packageCommon/order/detail?id=${orderId.value}` });
			}, 500);
		} catch (error) {
			uni.showToast({ title: error.message || '提交失败', icon: 'none' });
		}
	};
	const goDetail = () => uni.navigateTo({ url: `/packageCommon/order/detail?id=${orderId.value}` });
</script>

<style lang="scss">
	.exception-page { background: linear-gradient(180deg, #fff8f3 0%, #fff 320rpx); }
	.exception-body { padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
	.option-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16rpx; }
	.option-chip { padding: 22rpx 16rpx; border-radius: 22rpx; text-align: center; font-size: 26rpx; color: #4d3c3c; background: #f8f3f3; }
	.option-chip.is-active { background: #fff1f3; border: 2rpx solid #cf1432; box-sizing: border-box; }
	.remark-box { width: 100%; min-height: 220rpx; padding: 24rpx; border-radius: 24rpx; background: #fff8f8; font-size: 28rpx; color: #2f2424; }
</style>
