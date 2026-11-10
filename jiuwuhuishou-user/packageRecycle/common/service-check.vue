<template>
	<view class="app-page service-check-page">
		<top-nav title="服务范围与规则"></top-nav>

		<scroll-view scroll-y class="page-body compact service-check-body">
			<view v-if="loading" class="service-check-loading">加载中...</view>

			<template v-else>
				<view class="card hero-soft">
					<view class="row-between" style="align-items: flex-start;">
						<view style="flex: 1;">
							<view style="font-size: 40rpx; font-weight: 800; color: #2f2424;">{{ pageData.heroTitle }}</view>
							<view class="muted" style="margin-top: 14rpx;">{{ pageData.heroDesc }}</view>
							<view class="brand-pill" style="margin-top: 18rpx;">{{ pageData.heroTag }}</view>
						</view>
						<view style="width: 210rpx;">
							<mock-art label="规则先看" desc="更省心" theme="sand"></mock-art>
						</view>
					</view>

					<view class="flow-row" style="margin-top: 28rpx;">
						<view v-for="item in pageData.steps" :key="item.txt" class="flow-item">
							<view class="num">{{ item.step }}</view>
							<view class="txt">{{ item.txt }}</view>
						</view>
					</view>
				</view>

				<view class="section-title"><text>地址覆盖查询</text></view>
				<view class="card service-check-area">
					<view class="service-check-area__picker" @click="openAddressPicker">
						<view>
							<view style="font-size: 30rpx; font-weight: 700;">{{ selectedAddressTitle }}</view>
							<view class="muted" style="margin-top: 8rpx;">{{ selectedAddressDesc }}</view>
						</view>
						<u-icon name="arrow-right" size="18" color="#8f7575"></u-icon>
					</view>
					<view class="service-check-area__result row-between">
						<view class="muted" style="flex: 1;">{{ areaCheck.desc }}</view>
						<view class="brand-pill service-check-area__tag" :class="areaCheck.inService ? 'is-covered' : 'is-out'">
							{{ areaCheck.tag }}
						</view>
					</view>
					<view class="service-check-area__action" @click="refreshAreaCheck">{{ checkingArea ? '查询中...' : '重新查询覆盖范围' }}</view>
				</view>

				<view class="section-title"><text>覆盖范围</text></view>
				<view class="card">
					<view class="feature-list">
						<view class="feature-row service-check__coverage-row" v-for="item in pageData.coverage" :key="item.title">
							<view>
								<view style="font-size: 30rpx; font-weight: 700;">{{ item.title }}</view>
								<view class="muted" style="margin-top: 8rpx;">{{ item.desc }}</view>
							</view>
							<view class="brand-pill service-check__coverage-tag">{{ item.tag }}</view>
						</view>
					</view>
				</view>

				<view class="section-title"><text>下单门槛</text></view>
				<view class="grid-two">
					<view v-for="item in pageData.thresholds" :key="item.title" class="service-card">
						<view class="dot-badge"><u-icon :name="item.icon" size="20" color="#c8102e"></u-icon></view>
						<view class="title">{{ item.title }}</view>
						<view class="desc">{{ item.desc }}</view>
					</view>
				</view>

				<view class="section-title"><text>价格与结算规则</text></view>
				<view class="card">
					<view class="feature-list">
						<view class="rule-row" v-for="item in pageData.rules" :key="item.title">
							<view class="rule-row__dot"></view>
							<view style="flex: 1;">
								<view style="font-size: 28rpx; font-weight: 700;">{{ item.title }}</view>
								<view class="muted" style="margin-top: 8rpx;">{{ item.desc }}</view>
							</view>
						</view>
					</view>
				</view>

				<view class="section-title"><text>不可回收项</text></view>
				<view class="card">
					<view class="tag-grid">
						<view v-for="item in pageData.rejects" :key="item" class="reject-chip">{{ item }}</view>
					</view>
				</view>

				<view class="section-title"><text>异常与售后</text></view>
				<view class="card">
					<view class="feature-list">
						<view class="feature-row" v-for="item in pageData.aftersales" :key="item.title">
							<view>
								<view style="font-size: 30rpx; font-weight: 700;">{{ item.title }}</view>
								<view class="muted" style="margin-top: 8rpx;">{{ item.desc }}</view>
							</view>
							<u-icon name="checkmark-circle-fill" size="20" color="#cf1432"></u-icon>
						</view>
					</view>
				</view>
			</template>
		</scroll-view>

		<u-popup :show="showAddressPicker" mode="bottom" @close="closeAddressPicker">
			<view class="sheet-body">
				<view class="sheet-handle"></view>
				<view class="sheet-title">选择查询地址</view>
				<scroll-view scroll-y class="service-check-address-list">
					<view
						v-for="item in addresses"
						:key="item.id"
						class="service-check-address-item"
						:class="{ 'is-active': `${selectedAddress.id}` === `${item.id}` }"
						@click="selectAddress(item)"
					>
						<view style="font-size: 30rpx; font-weight: 700;">{{ item.name }}</view>
						<view class="muted" style="margin-top: 8rpx;">{{ item.address }}</view>
						<view class="muted" style="margin-top: 6rpx;">{{ item.user }}</view>
					</view>
					<view v-if="!addresses.length" class="service-check-empty">暂无地址，请先在地址管理中添加</view>
				</scroll-view>
				<view class="btn-main" @click="goAddressManage">去地址管理</view>
			</view>
		</u-popup>

		<floating-service></floating-service>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { listAddresses } from '@/common/services/address.js';
	import { checkServiceAreaByAddress, loadServiceCheckPageData } from '@/common/services/serviceCheck.js';
	import { DEFAULT_SERVICE_CHECK } from '@/common/adapters/serviceCheck.js';

	const loading = ref(true);
	const checkingArea = ref(false);
	const showAddressPicker = ref(false);
	const pageData = ref({ ...DEFAULT_SERVICE_CHECK });
	const addresses = ref([]);
	const selectedAddress = ref(null);
	const areaCheck = ref({
		inService: false,
		tag: '待查询',
		desc: '请选择地址后查询覆盖范围'
	});

	const selectedAddressTitle = computed(() => selectedAddress.value?.name || '请选择地址');
	const selectedAddressDesc = computed(() => {
		if (!selectedAddress.value) {
			return '支持查询当前地址是否在服务覆盖范围内';
		}
		return selectedAddress.value.address || [selectedAddress.value.province, selectedAddress.value.city, selectedAddress.value.district].filter(Boolean).join(' ');
	});

	const refreshAreaCheck = async () => {
		if (!selectedAddress.value) {
			uni.showToast({ title: '请先选择地址', icon: 'none' });
			return;
		}
		checkingArea.value = true;
		try {
			areaCheck.value = await checkServiceAreaByAddress(selectedAddress.value, pageData.value);
		} finally {
			checkingArea.value = false;
		}
	};

	const syncAddresses = async () => {
		try {
			addresses.value = await listAddresses();
			const current = selectedAddress.value;
			const matched = current
				? addresses.value.find((item) => `${item.id}` === `${current.id}`)
				: null;
			selectedAddress.value = matched || addresses.value.find((item) => item.default) || addresses.value[0] || null;
			if (selectedAddress.value) {
				await refreshAreaCheck();
			}
		} catch (error) {
			addresses.value = [];
			areaCheck.value = {
				inService: false,
				tag: '查询失败',
				desc: error.message || '地址加载失败'
			};
		}
	};

	const loadPageData = async () => {
		loading.value = true;
		try {
			pageData.value = await loadServiceCheckPageData();
			await syncAddresses();
		} catch (error) {
			pageData.value = { ...DEFAULT_SERVICE_CHECK };
			uni.showToast({ title: error.message || '加载失败', icon: 'none' });
		} finally {
			loading.value = false;
		}
	};

	const openAddressPicker = () => {
		showAddressPicker.value = true;
	};

	const closeAddressPicker = () => {
		showAddressPicker.value = false;
	};

	const selectAddress = async (item) => {
		selectedAddress.value = item;
		closeAddressPicker();
		await refreshAreaCheck();
	};

	const goAddressManage = () => {
		closeAddressPicker();
		uni.navigateTo({ url: '/packageCommon/user/address' });
	};

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.service-check-page {
		background: linear-gradient(180deg, #fff7f1 0%, #fff 300rpx, #fffdfc 100%);
	}

	.service-check-body {
		padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
	}

	.service-check-loading,
	.service-check-empty {
		padding: 80rpx 0;
		text-align: center;
		font-size: 26rpx;
		color: #9a8888;
	}

	.service-check-area__picker {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16rpx;
		padding-bottom: 20rpx;
		border-bottom: 2rpx solid #f3e8e8;
	}

	.service-check-area__result {
		align-items: center;
		gap: 16rpx;
		margin-top: 20rpx;
	}

	.service-check-area__tag.is-covered {
		background: #fff3f4;
		color: #c8102e;
	}

	.service-check-area__tag.is-out {
		background: #f7f0d8;
		color: #8a6400;
	}

	.service-check-area__action {
		margin-top: 22rpx;
		height: 76rpx;
		border-radius: 999rpx;
		background: linear-gradient(135deg, #ddb16c 0%, #d5a562 100%);
		color: #fff;
		font-size: 28rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.service-check-address-list {
		max-height: 520rpx;
		margin-top: 18rpx;
	}

	.service-check-address-item {
		padding: 22rpx 0;
		border-bottom: 2rpx solid #f3e8e8;
	}

	.service-check-address-item.is-active {
		background: #fff8f5;
	}

	.rule-row {
		display: flex;
		align-items: flex-start;
		gap: 18rpx;
	}

	.rule-row__dot {
		width: 16rpx;
		height: 16rpx;
		border-radius: 50%;
		background: #cf1432;
		margin-top: 14rpx;
		flex-shrink: 0;
	}

	.tag-grid {
		display: flex;
		gap: 16rpx;
		flex-wrap: wrap;
	}

	.reject-chip {
		padding: 16rpx 22rpx;
		border-radius: 20rpx;
		background: #fff3f4;
		color: #c8102e;
		font-size: 26rpx;
		font-weight: 600;
	}

	.service-check__coverage-row {
		align-items: center;
		gap: 20rpx;
	}

	.service-check__coverage-row > view:first-child {
		flex: 1;
		min-width: 0;
	}

	.service-check__coverage-tag {
		flex-shrink: 0;
		white-space: nowrap;
		min-width: 124rpx;
		text-align: center;
	}
</style>
