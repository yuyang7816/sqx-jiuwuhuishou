<template>
	<view class="app-page">
		<top-nav title="回收规则详情"></top-nav>
		<scroll-view scroll-y class="page-body compact">
			<view class="card hero-red" style="text-align: center;">
				<view class="dot-badge" style="margin: 0 auto; width: 116rpx; height: 116rpx; background: rgba(255,255,255,.18); color: #fff;"><u-icon name="info-circle" size="42" color="#fff"></u-icon></view>
				<view style="margin-top: 22rpx; font-size: 38rpx; font-weight: 800;">公正透明 · 碳绿新生</view>
				<view style="margin-top: 16rpx; font-size: 28rpx; line-height: 1.6; opacity: .92;">致力于打造价格透明、服务标准、低碳环保的家电回收体系</view>
			</view>

			<view class="section-title"><text>家电分类计价标准</text></view>
			<view class="feature-list">
				<view class="card" v-for="group in groups" :key="group.title">
					<view class="row-between"><text style="font-size: 34rpx; font-weight: 800;">{{ group.title }}</text><text style="font-size: 34rpx; font-weight: 800; color: #8a6400;">{{ group.top }}</text></view>
					<view class="feature-list" style="margin-top: 18rpx; gap: 12rpx;">
						<view class="row-between" v-for="item in group.items" :key="item.name"><text class="muted">{{ item.name }}</text><text class="brand-text">{{ item.price }}</text></view>
					</view>
				</view>
			</view>
			<view class="muted" style="margin-top: 14rpx;">* 以上价格仅供参考，实际根据机型、年份及损坏程度面议。</view>

			<view class="section-title"><text>服务及增值费说明</text></view>
			<view class="feature-list">
				<view class="card" v-for="extra in extras" :key="extra.title">
					<view class="row-between" style="justify-content: flex-start;">
						<view class="dot-badge" :style="extra.badgeStyle"><u-icon :name="extra.icon" size="22" :color="extra.iconColor"></u-icon></view>
						<view>
							<view style="font-size: 34rpx; font-weight: 800;">{{ extra.title }}</view>
							<view class="muted" style="margin-top: 8rpx;">{{ extra.desc }}</view>
						</view>
					</view>
					<view v-if="extra.tags" class="row-between" style="margin-top: 18rpx; justify-content: flex-start; gap: 12rpx; flex-wrap: wrap;">
						<view v-for="tag in extra.tags" :key="tag" class="ghost-panel" style="padding: 14rpx 18rpx;">{{ tag }}</view>
					</view>
				</view>
			</view>

			<view class="section-title"><text>温馨提示</text></view>
			<view class="feature-list">
				<view class="faq-item" v-for="(faq, index) in faqs" :key="faq.q" @click="toggle(index)">
					<view class="faq-question"><text>{{ faq.q }}</text><u-icon :name="faq.open ? 'arrow-up' : 'arrow-down'" size="16" color="#7a6161"></u-icon></view>
					<view v-if="faq.open" class="faq-answer">{{ faq.a }}</view>
				</view>
			</view>

			<view class="card" style="margin-top: 28rpx; text-align: center; background: #7d7b7b; color: #fff;">
				<u-icon name="leaf" size="36" color="#fff"></u-icon>
				<view style="margin-top: 18rpx; font-size: 32rpx;">每回收一台冰箱，约等于种下 3 棵树</view>
				<view style="margin-top: 12rpx; font-size: 24rpx; opacity: .88;">感谢您为地球减碳事业做出的每一份贡献</view>
			</view>
		</scroll-view>
		<floating-service></floating-service>
		<view class="footer-bar">
			<view class="btn-ghost" @click="uni.navigateBack()">返回查看</view>
			<view class="btn-main" @click="go('/packageRecycle/appliance/model-select?category=冰箱')">立即预约回收</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	const groups = [
		{ title: '大型家电', top: '￥80起', items: [{ name: '空调（挂机/柜机）', price: '￥120~450' }, { name: '冰箱（单门/多门）', price: '￥80~260' }, { name: '洗衣机（滚筒/波轮）', price: '￥60~180' }] },
		{ title: '厨房家电', top: '￥20起', items: [{ name: '微波炉/烤箱', price: '￥20~50' }, { name: '油烟机（集成/侧吸）', price: '￥30~80' }, { name: '洗碗机/消毒柜', price: '￥50~120' }] }
	];
	const extras = [
		{ title: '超远距离费', desc: '服务半径超过 10 公里后开始计费。10-15 公里加收 30 元，15-25 公里加收 60 元。', icon: 'map', iconColor: '#8a6400', badgeStyle: 'background:#fff3cc;' },
		{ title: '特殊拆机服务费', desc: '针对高难度或高空作业收取的专业人工费。', icon: 'tools', iconColor: '#b14141', badgeStyle: 'background:#ffe7ea;', tags: ['高空空调拆除 ￥100/台', '嵌入式厨电拆卸 ￥30/台'] }
	];
	const faqs = ref([
		{ q: '什么样的家电不符合回收标准？', a: '严重污染、涉危品或来源不明的设备不予回收。', open: false },
		{ q: '关于数据隐私处理', a: '含存储介质的家电将按照标准流程进行清除或拆解。', open: false }
	]);
	const toggle = (index) => (faqs.value[index].open = !faqs.value[index].open);
	const go = (url) => uni.navigateTo({ url });
</script>
