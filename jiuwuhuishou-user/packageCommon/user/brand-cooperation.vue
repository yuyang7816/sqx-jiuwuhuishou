<template>
	<view class="app-page brand-page">
		<top-nav title="品牌合作"></top-nav>
		<scroll-view scroll-y class="page-body compact brand-body">
			<view class="card brand-hero">
				<view class="brand-hero__title">提交品牌合作信息</view>
				<view class="brand-hero__desc">欢迎洽谈联名周边、环保活动、回收公益合作，提交后会有专人尽快与您联系。</view>
			</view>

			<view class="card">
				<view class="brand-form__list">
					<view class="brand-form__item">
						<view class="brand-form__label">品牌名称</view>
						<input v-model="form.brandName" class="brand-form__input" type="text" maxlength="30" placeholder="请输入品牌名称" placeholder-class="brand-form__placeholder" />
					</view>

					<view class="brand-form__item brand-form__item--upload">
						<view class="brand-form__label">品牌图片</view>
						<view class="brand-form__upload" @click="pickBrandImage">
							<image v-if="form.brandImage" :src="form.brandImage" mode="aspectFill" class="brand-form__upload-img"></image>
							<view v-else class="brand-form__upload-empty">
								<u-icon name="plus" size="20" color="#c7a27a"></u-icon>
								<text>上传品牌图片</text>
							</view>
						</view>
					</view>

					<view class="brand-form__item">
						<view class="brand-form__label">联系人</view>
						<input v-model="form.contact" class="brand-form__input" type="text" maxlength="20" placeholder="请输入联系人姓名" placeholder-class="brand-form__placeholder" />
					</view>

					<view class="brand-form__item">
						<view class="brand-form__label">联系电话</view>
						<input v-model="form.mobile" class="brand-form__input" type="number" maxlength="11" placeholder="请输入联系电话" placeholder-class="brand-form__placeholder" />
					</view>

					<view class="brand-form__item brand-form__picker" @click="openRegionPicker">
						<view class="brand-form__label">联系区域</view>
						<view class="brand-form__value" :class="{ 'is-filled': regionText }">{{ regionText || '请选择省市区' }}</view>
						<u-icon name="arrow-right" size="16" color="#b49d9d"></u-icon>
					</view>

					<view class="brand-form__item brand-form__item--textarea">
						<view class="brand-form__label">联系地址</view>
						<textarea v-model="form.address" class="brand-form__textarea" maxlength="120" placeholder="请输入街道、门牌号、楼层房间号等信息" placeholder-class="brand-form__placeholder" auto-height />
					</view>
				</view>
			</view>
		</scroll-view>

		<view class="footer-bar one" style="background: transparent; box-shadow: none; backdrop-filter: none;">
			<view class="btn-main" @click="submit">{{ submitting ? '提交中...' : '确认提交' }}</view>
		</view>

		<u-popup :show="showRegionPicker" mode="bottom" @close="closeRegionPicker">
			<view class="sheet-body region-sheet">
				<view class="sheet-handle"></view>
				<view class="region-sheet__head">
					<u-icon name="close" size="22" color="#5f4a4a" @click="closeRegionPicker"></u-icon>
					<view class="sheet-title region-sheet__title">选择联系区域</view>
					<view class="region-sheet__placeholder"></view>
				</view>
				<view class="region-sheet__tabs">
					<view class="region-sheet__tab" :class="{ 'is-active': regionStep >= 1 }" @click="regionStep = 1">{{ tempProvinceName || '请选择' }}</view>
					<view class="region-sheet__tab" :class="{ 'is-active': regionStep >= 2 }" @click="tempProvinceCode && (regionStep = 2)">{{ tempCityName || '请选择' }}</view>
					<view class="region-sheet__tab" :class="{ 'is-active': regionStep === 3 }" @click="tempCityCode && (regionStep = 3)">{{ tempDistrictName || '请选择' }}</view>
				</view>
				<scroll-view scroll-y class="region-sheet__panel" v-if="regionStep === 1">
					<view v-for="item in regionOptions" :key="item.code" class="region-sheet__option" :class="{ 'is-selected': tempProvinceCode === item.code }" @click="selectProvince(item)">{{ item.name }}</view>
				</scroll-view>
				<scroll-view scroll-y class="region-sheet__panel" v-else-if="regionStep === 2">
					<view v-for="item in currentCityOptions" :key="item.code" class="region-sheet__option" :class="{ 'is-selected': tempCityCode === item.code }" @click="selectCity(item)">{{ item.name }}</view>
				</scroll-view>
				<scroll-view scroll-y class="region-sheet__panel" v-else>
					<view v-for="item in currentDistrictOptions" :key="item.code" class="region-sheet__option" :class="{ 'is-selected': tempDistrictCode === item.code }" @click="selectDistrict(item)">{{ item.name }}</view>
				</scroll-view>
				<view class="btn-main region-sheet__confirm" @click="confirmRegion">确认选择</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { chooseAndUploadImage } from '@/common/services/upload.js';
	import { submitBrandCooperation } from '@/common/services/brandCooperation.js';
	import { regionOptions, resolveRegionSelection } from '@/common/utils/region.js';

	const form = ref({
		brandName: '',
		brandImage: '',
		contact: '',
		mobile: '',
		region: [],
		address: ''
	});
	const submitting = ref(false);
	const uploading = ref(false);
	const showRegionPicker = ref(false);
	const regionStep = ref(1);
	const tempProvinceCode = ref('');
	const tempCityCode = ref('');
	const tempDistrictCode = ref('');

	const regionText = computed(() => form.value.region.length ? form.value.region.join(' / ') : '');
	const currentProvince = computed(() => regionOptions.find((item) => item.code === tempProvinceCode.value) || regionOptions[0] || null);
	const currentCityOptions = computed(() => currentProvince.value?.cities || []);
	const currentCity = computed(() => currentCityOptions.value.find((item) => item.code === tempCityCode.value) || currentCityOptions.value[0] || null);
	const currentDistrictOptions = computed(() => currentCity.value?.districts || []);
	const currentDistrict = computed(() => currentDistrictOptions.value.find((item) => item.code === tempDistrictCode.value) || currentDistrictOptions.value[0] || null);
	const tempProvinceName = computed(() => currentProvince.value?.name || '');
	const tempCityName = computed(() => currentCity.value?.name || '');
	const tempDistrictName = computed(() => currentDistrict.value?.name || '');

	const syncTempRegion = (regionNames = []) => {
		const selection = resolveRegionSelection(regionOptions, regionNames);
		tempProvinceCode.value = selection.provinceCode;
		tempCityCode.value = selection.cityCode;
		tempDistrictCode.value = selection.districtCode;
	};

	const openRegionPicker = () => {
		syncTempRegion(form.value.region);
		regionStep.value = 1;
		showRegionPicker.value = true;
	};

	const closeRegionPicker = () => {
		showRegionPicker.value = false;
		regionStep.value = 1;
	};

	const selectProvince = (item) => {
		tempProvinceCode.value = item.code;
		tempCityCode.value = item.cities[0]?.code || '';
		tempDistrictCode.value = item.cities[0]?.districts?.[0]?.code || '';
		regionStep.value = 2;
	};

	const selectCity = (item) => {
		tempCityCode.value = item.code;
		tempDistrictCode.value = item.districts?.[0]?.code || '';
		regionStep.value = 3;
	};

	const selectDistrict = (item) => {
		tempDistrictCode.value = item.code;
	};

	const confirmRegion = () => {
		const region = [tempProvinceName.value, tempCityName.value, tempDistrictName.value].filter(Boolean);
		if (region.length < 3) {
			uni.showToast({ title: '请选择完整省市区', icon: 'none' });
			return;
		}
		form.value.region = region;
		closeRegionPicker();
	};

	const pickBrandImage = async () => {
		if (uploading.value) {
			return;
		}
		uploading.value = true;
		try {
			form.value.brandImage = await chooseAndUploadImage();
		} catch (error) {
			uni.showToast({
				title: error.message || '图片上传失败',
				icon: 'none'
			});
		} finally {
			uploading.value = false;
		}
	};

	const submit = async () => {
		if (submitting.value) {
			return;
		}
		submitting.value = true;
		try {
			await submitBrandCooperation(form.value);
			uni.showToast({ title: '提交成功', icon: 'success' });
			setTimeout(() => {
				uni.navigateBack();
			}, 500);
		} catch (error) {
			uni.showToast({
				title: error.message || '提交失败',
				icon: 'none'
			});
		} finally {
			submitting.value = false;
		}
	};
</script>

<style lang="scss">
	.brand-page {
		background: linear-gradient(180deg, #fff8f5 0%, #fff 320rpx);
	}

	.brand-body {
		padding-top: 16rpx;
		padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
	}

	.brand-hero {
		background: linear-gradient(135deg, #fff8f0 0%, #fff 48%, #fff8f6 100%);
	}

	.brand-hero__title {
		font-size: 34rpx;
		font-weight: 800;
		color: #332626;
	}

	.brand-hero__desc {
		margin-top: 14rpx;
		font-size: 26rpx;
		line-height: 1.7;
		color: #8a7676;
	}

	.brand-form__list {
		display: flex;
		flex-direction: column;
		gap: 2rpx;
	}

	.brand-form__item {
		display: flex;
		align-items: center;
		gap: 18rpx;
		padding: 28rpx 0;
		border-bottom: 2rpx solid #f3eaea;
	}

	.brand-form__item--textarea,
	.brand-form__item--upload {
		align-items: flex-start;
	}

	.brand-form__picker {
		width: 100%;
		box-sizing: border-box;
	}

	.brand-form__item:last-child {
		border-bottom: 0;
		padding-bottom: 6rpx;
	}

	.brand-form__label {
		width: 150rpx;
		font-size: 30rpx;
		font-weight: 600;
		color: #3a2d2d;
		flex-shrink: 0;
	}

	.brand-form__value {
		flex: 1;
		font-size: 28rpx;
		line-height: 1.6;
		color: #8f7b7b;
		text-align: right;
	}

	.brand-form__value.is-filled {
		color: #4d3c3c;
	}

	.brand-form__input {
		flex: 1;
		height: 44rpx;
		font-size: 28rpx;
		line-height: 44rpx;
		color: #4d3c3c;
		text-align: right;
		background: transparent;
		padding: 0;
		margin: 0;
	}

	.brand-form__textarea {
		flex: 1;
		min-height: 120rpx;
		font-size: 28rpx;
		line-height: 1.7;
		color: #4d3c3c;
		text-align: right;
		background: transparent;
		padding: 0;
		margin: 0;
	}

	.brand-form__upload {
		flex: 1;
		height: 160rpx;
		border-radius: 24rpx;
		background: #fffaf6;
		border: 2rpx dashed #ecd8c6;
		overflow: hidden;
	}

	.brand-form__upload-img {
		width: 100%;
		height: 100%;
	}

	.brand-form__upload-empty {
		width: 100%;
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 12rpx;
		font-size: 24rpx;
		color: #c7a27a;
	}

	.brand-form__placeholder {
		color: #b9a7a7;
	}

	.region-sheet {
		padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
		background: linear-gradient(180deg, #fff9f6 0%, #fff 100%);
	}

	.region-sheet__head,
	.region-sheet__tabs {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.region-sheet__head {
		padding: 0 8rpx;
	}

	.region-sheet__title,
	.region-sheet__placeholder {
		width: 220rpx;
		text-align: center;
	}

	.region-sheet__tabs {
		gap: 12rpx;
		margin-top: 24rpx;
		padding-bottom: 18rpx;
		border-bottom: 2rpx solid #f2e8e8;
	}

	.region-sheet__tab {
		flex: 1;
		padding: 18rpx 12rpx;
		border-radius: 20rpx;
		background: #fff4f4;
		font-size: 26rpx;
		font-weight: 600;
		color: #8e7676;
		text-align: center;
	}

	.region-sheet__tab.is-active {
		background: #fff0f2;
		color: #c51635;
	}

	.region-sheet__panel {
		height: 620rpx;
		margin-top: 24rpx;
	}

	.region-sheet__option {
		padding: 24rpx 20rpx;
		border-radius: 20rpx;
		font-size: 28rpx;
		color: #564444;
	}

	.region-sheet__option.is-selected {
		background: #fff1f3;
		color: #c51635;
		font-weight: 700;
	}

	.region-sheet__confirm {
		margin-top: 24rpx;
	}
</style>
