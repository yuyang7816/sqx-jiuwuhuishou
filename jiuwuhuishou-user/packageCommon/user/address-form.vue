<template>
	<view class="app-page address-form-page">
		<top-nav :title="isEdit ? '编辑地址' : '新增地址'"></top-nav>
		<scroll-view scroll-y class="page-body compact" style="padding-top: 16rpx; padding-bottom: calc(180rpx + env(safe-area-inset-bottom));">
			<view class="card">
				<view class="address-form__list">
					<view class="address-form__item">
						<view class="address-form__label">联系人</view>
						<input v-model="form.contact" class="address-form__input" type="text" maxlength="20" placeholder="请输入联系人姓名" placeholder-class="address-form__placeholder" />
					</view>

					<view class="address-form__item">
						<view class="address-form__label">手机号</view>
						<input v-model="form.mobile" class="address-form__input" type="number" maxlength="11" placeholder="请输入手机号" placeholder-class="address-form__placeholder" />
					</view>

					<view class="address-form__item address-form__picker" @click="openRegionPicker">
						<view class="address-form__label">所在区域</view>
						<view class="address-form__value" :class="{ 'is-filled': regionText }">{{ regionText || '请选择省市区' }}</view>
						<u-icon name="arrow-right" size="16" color="#b49d9d"></u-icon>
					</view>

					<view class="address-form__item address-form__item--textarea">
						<view class="address-form__label">详细地址</view>
						<textarea v-model="form.address" class="address-form__textarea" maxlength="120" placeholder="请输入街道、门牌号、楼层房间号等信息" placeholder-class="address-form__placeholder" auto-height />
					</view>

					<view class="address-form__item">
						<view class="address-form__label">地址标签</view>
						<input v-model="form.tag" class="address-form__input" type="text" maxlength="10" placeholder="如：家 / 公司 / 学校" placeholder-class="address-form__placeholder" />
					</view>
				</view>
			</view>

			<view class="card" style="margin-top: 24rpx;">
				<view class="row-between">
					<text style="font-size: 32rpx; font-weight: 700;">设为默认地址</text>
					<u-switch v-model="isDefault" activeColor="#cf1432"></u-switch>
				</view>
				<view class="muted" style="margin-top: 14rpx;">下单时优先使用该地址，减少重复填写。</view>
			</view>
		</scroll-view>

		<view class="footer-bar one" style="background: transparent; box-shadow: none; backdrop-filter: none;">
			<view class="btn-main" @click="submit">{{ isEdit ? '保存修改' : '确认新增' }}</view>
		</view>

		<u-popup :show="showRegionPicker" mode="bottom" @close="closeRegionPicker">
			<view class="sheet-body region-sheet">
				<view class="sheet-handle"></view>
				<view class="region-sheet__head">
					<u-icon name="close" size="22" color="#5f4a4a" @click="closeRegionPicker"></u-icon>
					<view class="sheet-title region-sheet__title">选择所在区域</view>
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
	import { onLoad } from '@dcloudio/uni-app';
	import { getAddressById, saveAddressInfo } from '@/common/services/address.js';
	import { regionOptions, resolveRegionSelection } from '@/common/utils/region.js';

	const mode = ref('create');
	const currentId = ref('');
	const isDefault = ref(true);
	const showRegionPicker = ref(false);
	const regionStep = ref(1);
	const tempProvinceCode = ref('');
	const tempCityCode = ref('');
	const tempDistrictCode = ref('');
	const form = ref({
		contact: '',
		mobile: '',
		region: [],
		address: '',
		tag: ''
	});

	onLoad(async (options) => {
		mode.value = options.mode || 'create';
		currentId.value = options.id || '';
		if (mode.value === 'edit') {
			try {
				const current = await getAddressById(options.id);
				if (!current) {
					return;
				}
				const fullMobile = current.contactMobile || current.mobile || current.phone || '';
				const region = current.region?.length ? current.region : [current.province, current.city, current.district].filter(Boolean);
				form.value = {
					contact: current.name,
					mobile: fullMobile,
					region,
					address: current.address,
					tag: current.label || current.tag || ''
				};
				isDefault.value = Boolean(current.default);
			} catch (error) {
				uni.showToast({ title: error.message || '加载地址失败', icon: 'none' });
			}
		}
	});

	const isEdit = computed(() => mode.value === 'edit');
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
		form.value.region = [tempProvinceName.value, tempCityName.value, tempDistrictName.value].filter(Boolean);
		closeRegionPicker();
	};

	const submit = async () => {
		if (!form.value.contact.trim()) {
			uni.showToast({ title: '请输入联系人', icon: 'none' });
			return;
		}
		if (!/^1\d{10}$/.test(form.value.mobile)) {
			uni.showToast({ title: '请输入正确手机号', icon: 'none' });
			return;
		}
		if (!form.value.region.length) {
			uni.showToast({ title: '请选择所在区域', icon: 'none' });
			return;
		}
		if (!form.value.address.trim()) {
			uni.showToast({ title: '请输入详细地址', icon: 'none' });
			return;
		}
		try {
			await saveAddressInfo({
				contact: form.value.contact,
				mobile: form.value.mobile,
				region: form.value.region,
				address: form.value.address,
				tag: form.value.tag,
				isDefault: isDefault.value
			}, currentId.value || undefined);
			uni.showToast({
				title: isEdit.value ? '修改成功' : '新增成功',
				icon: 'success'
			});
			setTimeout(() => {
				uni.navigateBack();
			}, 500);
		} catch (error) {
			uni.showToast({ title: error.message || '保存失败', icon: 'none' });
		}
	};
</script>

<style lang="scss">
	.address-form-page {
		background: linear-gradient(180deg, #fff8f5 0%, #fff 320rpx);
	}

	.address-form__list {
		display: flex;
		flex-direction: column;
		gap: 2rpx;
	}

	.address-form__item {
		display: flex;
		align-items: center;
		gap: 18rpx;
		padding: 28rpx 0;
		border-bottom: 2rpx solid #f3eaea;
	}

	.address-form__item--textarea {
		align-items: flex-start;
	}

	.address-form__picker {
		width: 100%;
		box-sizing: border-box;
	}

	.address-form__item:last-child {
		border-bottom: 0;
		padding-bottom: 6rpx;
	}

	.address-form__label {
		width: 150rpx;
		font-size: 30rpx;
		font-weight: 600;
		color: #3a2d2d;
		flex-shrink: 0;
	}

	.address-form__value {
		flex: 1;
		font-size: 28rpx;
		line-height: 1.6;
		color: #8f7b7b;
		text-align: right;
	}

	.address-form__value.is-filled {
		color: #4d3c3c;
	}

	.address-form__input {
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

	.address-form__textarea {
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

	.address-form__placeholder {
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
