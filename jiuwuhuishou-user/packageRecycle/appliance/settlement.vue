<template>
	<view class="app-page appliance-settlement-page">
		<top-nav title="估价结算" :show-more="false"></top-nav>
		<scroll-view scroll-y class="page-body compact appliance-settlement-body">
			<view class="card" style="text-align: center;">
				<view class="muted">预计回收总额</view>
				<view v-if="estimateLoading" class="price-main appliance-estimate-loading" style="margin-top: 16rpx;">预估中...</view>
				<view v-else class="price-main" style="margin-top: 16rpx;">{{ estimateRangeText }}</view>
				<view class="list-divider"></view>
				<view class="row-between appliance-product-row">
					<view class="appliance-product-info">
						<view class="appliance-product-info__title">{{ applianceTitle }}</view>
						<view class="muted appliance-product-info__desc">{{ applianceDesc }}</view>
						<view class="appliance-tags">
							<text v-for="tag in specTags" :key="tag" class="appliance-tags__item">{{ tag }}</text>
						</view>
					</view>
					<text class="brand-text appliance-product-edit" @click="goModifySpec">修改参数</text>
				</view>
			</view>

			<view class="section-title"><text>选择回收方式</text></view>
			<view class="grid-two settlement-mode-grid">
				<view class="card settlement-mode-card" :style="mode === 'visit' ? activeModeStyle : ''" @click="mode = 'visit'">
					<view class="row-between">
						<view class="dot-badge" :class="{ 'is-muted': mode !== 'visit' }">
							<u-icon name="home" size="22" :color="mode === 'visit' ? '#c8102e' : '#6c5757'"></u-icon>
						</view>
						<u-icon v-if="mode === 'visit'" name="checkmark-circle-fill" size="22" color="#c8102e"></u-icon>
					</view>
					<view class="settlement-mode-card__title">上门回收</view>
					<view class="muted settlement-mode-card__desc">专业人员 24h 内上门</view>
				</view>
				<view class="card settlement-mode-card" :style="mode === 'mail' ? activeModeStyle : ''" @click="mode = 'mail'">
					<view class="row-between">
						<view class="dot-badge" :class="{ 'is-muted': mode !== 'mail' }">
							<u-icon name="car" size="22" :color="mode === 'mail' ? '#c8102e' : '#6c5757'"></u-icon>
						</view>
						<u-icon v-if="mode === 'mail'" name="checkmark-circle-fill" size="22" color="#c8102e"></u-icon>
					</view>
					<view class="settlement-mode-card__title">顺丰寄件</view>
					<view class="muted settlement-mode-card__desc">顺丰包邮 极速结算</view>
				</view>
			</view>

			<view v-if="mode === 'mail'" class="card platform-mail-card" style="margin-top: 24rpx;">
				<view class="platform-mail-card__title">平台收件信息</view>
				<view class="platform-mail-card__row">
					<text class="platform-mail-card__label">收件人</text>
					<text class="platform-mail-card__value">{{ platformMail.receiver }}</text>
				</view>
				<view class="platform-mail-card__row">
					<text class="platform-mail-card__label">联系电话</text>
					<text class="platform-mail-card__value">{{ platformMail.phone }}</text>
				</view>
				<view class="platform-mail-card__row">
					<text class="platform-mail-card__label">收件地址</text>
					<text class="platform-mail-card__value">{{ platformMail.address }}</text>
				</view>
			</view>

			<view class="card" style="margin-top: 24rpx;">
				<view v-if="mode === 'visit'" class="cell-list">
					<view class="cell-item" @click="openCityPicker">
						<text class="label">服务城市</text>
						<view class="row-between"><text class="value" :class="{ 'is-placeholder': !hasServiceCity }">{{ cityDisplay }}</text><u-icon name="arrow-right" size="18" color="#a68f8f"></u-icon></view>
					</view>
					<view class="cell-item" @click="showAddress = true">
						<text class="label">{{ addressLabel }}</text>
						<view class="row-between cell-item__address">
							<view class="address-value">
								<template v-if="selectedAddress?.id">
									<text class="value value--address">{{ selectedAddress.address }}</text>
									<text v-if="selectedAddress.phone" class="address-value__phone">{{ selectedAddress.phone }}</text>
								</template>
								<text v-else class="value value--address is-placeholder">请选择{{ addressLabel }}</text>
							</view>
							<u-icon name="edit-pen" size="18" color="#7a6161"></u-icon>
						</view>
					</view>
					<view class="cell-item" @click="openTimePicker">
						<text class="label">预约时间</text>
						<view class="row-between"><text class="value">{{ appointmentDisplay }}</text><u-icon name="arrow-right" size="18" color="#a68f8f"></u-icon></view>
					</view>
				</view>

				<view class="service-check-row" @click="go('/packageRecycle/common/service-check')">
					<view>
						<view style="font-size: 30rpx; font-weight: 700;">下单前规则校验</view>
						<view class="muted" style="margin-top: 8rpx;">{{ serviceHint }}</view>
					</view>
					<view class="brand-pill">查看规则</view>
				</view>

				<view class="check-row" @click="agreed = !agreed">
					<u-icon :name="agreed ? 'checkmark-circle-fill' : 'checkmark-circle'" size="18" :color="agreed ? '#cf1432' : '#c7b6b6'"></u-icon>
					<text>我已知晓服务范围、价格规则和大件上门注意事项</text>
				</view>
			</view>
		</scroll-view>
		<view class="footer-bar appliance-settlement-footer">
			<view class="btn-main appliance-settlement-footer__btn" @click="submitOrder">确认预约</view>
		</view>

		<u-popup :show="showCity" mode="bottom" @close="showCity = false">
			<view class="sheet-body address-sheet">
				<view class="sheet-handle"></view>
				<view class="address-sheet__head">
					<u-icon name="close" size="22" color="#5f4a4a" @click="showCity = false"></u-icon>
					<view class="sheet-title address-sheet__title">选择服务城市</view>
					<view class="address-sheet__placeholder"></view>
				</view>
				<view class="address-sheet__tabs">
					<view class="address-sheet__tab" :class="{ 'is-active': cityStep >= 1 }" @click="cityStep = 1">{{ tempProvinceName || '请选择' }}</view>
					<view class="address-sheet__tab" :class="{ 'is-active': cityStep >= 2, 'is-warn': cityStep >= 2 && !tempCityName }" @click="tempProvinceCode && (cityStep = 2)">{{ tempCityName || '请选择' }}</view>
					<view class="address-sheet__tab" :class="{ 'is-active': cityStep === 3, 'is-warn': cityStep === 3 && !tempDistrictName }" @click="tempCityCode && (cityStep = 3)">{{ tempDistrictName || '请选择' }}</view>
				</view>
				<scroll-view scroll-y class="address-sheet__panel">
					<view class="address-sheet__areas">
						<view
							v-for="item in currentCityRegionOptions"
							:key="item.code"
							class="address-sheet__area"
							:class="{ 'is-selected': isCityRegionOptionSelected(item) }"
							@click="selectCityRegionOption(item)"
						>
							{{ item.name }}
						</view>
					</view>
				</scroll-view>
				<view class="btn-main address-sheet__confirm" @click="confirmCity">确认选择</view>
			</view>
		</u-popup>

		<u-popup :show="showAddress" mode="bottom" @close="showAddress = false">
			<view class="sheet-body address-sheet">
				<view class="sheet-handle"></view>
				<view class="address-sheet__head">
					<u-icon name="close" size="22" color="#5f4a4a" @click="showAddress = false"></u-icon>
					<view class="sheet-title address-sheet__title">选择取件地址</view>
					<view class="address-sheet__placeholder"></view>
				</view>
				<view class="address-sheet__section" style="margin-top: 22rpx;">
					<view class="address-sheet__section-head">
						<u-icon name="star" size="18" color="#8a6400"></u-icon>
						<text>常用地址</text>
					</view>
					<view class="address-sheet__list">
						<view v-for="addr in addresses" :key="addr.id" class="address-sheet__card" @click="pickAddress(addr)">
							<view class="address-sheet__card-top">
								<view class="address-sheet__card-left">
									<view class="address-sheet__card-icon" :class="addr.default ? 'is-default' : ''">
										<u-icon :name="addr.icon" size="18" :color="addr.default ? '#cf1432' : '#6a5656'"></u-icon>
									</view>
									<view class="address-sheet__card-info">
										<view class="address-sheet__card-name-row">
											<text class="address-sheet__card-name">{{ addr.name }}</text>
											<view v-if="addr.default" class="address-sheet__card-tag">默认</view>
										</view>
										<view class="address-sheet__card-address">{{ addr.address }}</view>
										<view class="address-sheet__card-user">{{ addr.user }}</view>
									</view>
								</view>
								<view class="address-sheet__edit" @click.stop="editAddress(addr)">
									<u-icon name="edit-pen" size="16" color="#cf1432"></u-icon>
									<text>编辑</text>
								</view>
							</view>
						</view>
					</view>
					<view class="address-sheet__add" @click="createAddress">
						<u-icon name="plus-circle" size="18" color="#5a4545"></u-icon>
						<text>新增收货地址</text>
					</view>
				</view>
				<view class="btn-main address-sheet__confirm" @click="showAddress = false">确认选择</view>
			</view>
		</u-popup>

		<u-popup :show="showTime" mode="bottom" @close="showTime = false">
			<view class="sheet-body time-sheet">
				<view class="sheet-handle"></view>
				<view class="time-sheet__head">
					<view>
						<view class="sheet-title time-sheet__title">选择上门时间</view>
						<view class="time-sheet__desc">请在预约时间段内保持电话畅通，小哥将准时上门</view>
					</view>
					<u-icon name="close" size="22" color="#5f4a4a" @click="showTime = false"></u-icon>
				</view>
				<view class="time-sheet__content">
					<view class="time-sheet__dates">
						<view v-for="date in dateList" :key="date.label" class="time-sheet__date" :class="{ 'is-active': selectedDate === date.label }" @click="changeAppointmentDate(date.label)">
							<view class="time-sheet__date-label">{{ date.label }}</view>
							<view class="time-sheet__date-day">{{ date.day }}</view>
						</view>
					</view>
					<view class="time-sheet__slots">
						<view v-if="morningSlots.length" class="time-sheet__group">
							<view class="time-sheet__group-title">上午</view>
							<view v-for="slot in morningSlots" :key="slot.time" class="time-sheet__slot" :class="{ 'is-active': selectedSlot === slot.time, 'is-disabled': slot.disabled || slot.full }" @click="selectSlot(slot)">
								<view class="row-between">
									<text class="time-sheet__slot-time">{{ slot.time }}</text>
									<view class="time-sheet__slot-right">
										<text class="time-sheet__slot-state" :class="slot.disabled || slot.full ? 'is-full' : 'is-free'">{{ slot.disabled ? (slot.disabledReason === 'expired' ? '已过' : '约满') : slot.full ? '约满' : '有空' }}</text>
										<u-icon v-if="selectedSlot === slot.time" name="checkmark-circle-fill" size="22" color="#cf1432"></u-icon>
									</view>
								</view>
							</view>
						</view>
						<view v-if="afternoonSlots.length" class="time-sheet__group" :style="morningSlots.length ? 'margin-top: 20rpx;' : ''">
							<view class="time-sheet__group-title">下午</view>
							<view v-for="slot in afternoonSlots" :key="slot.time" class="time-sheet__slot" :class="{ 'is-active': selectedSlot === slot.time, 'is-disabled': slot.disabled || slot.full }" @click="selectSlot(slot)">
								<view class="row-between">
									<text class="time-sheet__slot-time">{{ slot.time }}</text>
									<view class="time-sheet__slot-right">
										<text class="time-sheet__slot-state" :class="slot.disabled || slot.full ? 'is-full' : 'is-free'">{{ slot.disabled ? (slot.disabledReason === 'expired' ? '已过' : '约满') : slot.full ? '约满' : '有空' }}</text>
										<u-icon v-if="selectedSlot === slot.time" name="checkmark-circle-fill" size="22" color="#cf1432"></u-icon>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
				<view class="btn-main time-sheet__confirm" @click="showTime = false">确定预约时间</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { getDefaultAddress, listBookingAddresses } from '@/common/services/address.js';
	import { createApplianceOrder } from '@/common/services/order.js';
	import { fetchApplianceEstimatePrice, loadApplianceSpecPage, normalizeSpecFromPage } from '@/common/services/applianceSpec.js';
	import { loadAppointmentOptions, pickDefaultAppointment } from '@/common/services/appointment.js';
	import { DEFAULT_PLATFORM_MAIL, loadPlatformMailReceiver } from '@/common/services/platformMail.js';
	import { getRegionStepOptions, regionOptions, resolveRegionSelection } from '@/common/utils/region.js';
	import { buildSpecSummary, buildSpecTagList, createDefaultSpec, encodeSpecToQuery, formatCurrency, parseSpecFromQuery } from './spec-data';
	import { clearApplianceSettlementDraft, loadApplianceSettlementDraft } from './appliance-draft.js';

	const parseSlotHour = (time) => {
		const start = `${time || ''}`.split('-')[0].trim();
		const [hour = '0'] = start.split(':');
		return Number(hour);
	};

	const mode = ref('visit');
	const platformMail = ref({ ...DEFAULT_PLATFORM_MAIL });
	const showCity = ref(false);
	const showAddress = ref(false);
	const showTime = ref(false);
	const agreed = ref(true);
	const selectedSpec = ref(createDefaultSpec('冰箱'));
	const cityStep = ref(1);
	const selectedProvince = ref('');
	const selectedCity = ref('');
	const selectedDistrict = ref('');
	const selectedRegion = ref([]);
	const tempProvinceCode = ref('');
	const tempCityCode = ref('');
	const tempDistrictCode = ref('');
	const selectedDate = ref('');
	const selectedSlot = ref('');
	const appointmentOptions = ref([]);
	const appointmentLoading = ref(false);
	const addresses = ref([]);
	const selectedAddress = ref(null);
	const dateList = computed(() => appointmentOptions.value.map(({ label, day }) => ({ label, day })));
	const currentDateOption = computed(() => appointmentOptions.value.find((item) => item.label === selectedDate.value) || appointmentOptions.value[0] || null);
	const currentSlots = computed(() => currentDateOption.value?.slots || []);
	const morningSlots = computed(() => currentSlots.value.filter((item) => parseSlotHour(item.time) < 12));
	const afternoonSlots = computed(() => currentSlots.value.filter((item) => parseSlotHour(item.time) >= 12));
	const selectedDateDay = computed(() => currentDateOption.value?.day || dateList.value.find((item) => item.label === selectedDate.value)?.day || '');
	const appointmentDisplay = computed(() => {
		if (!selectedSlot.value) {
			return '请选择预约时间';
		}
		return `${selectedDateDay.value} ${selectedSlot.value}`;
	});

	const activeModeStyle = 'border: 2rpx solid #c8102e; background: #fff6f7;';
	const specPage = ref({ config: { reward: 0 } });
	const estimateLoading = ref(true);
	const estimateData = ref({
		estimate: 0,
		reward: 0,
		low: 0,
		high: 0
	});
	const estimateRangeText = computed(() => {
		const low = Number(estimateData.value.low || 0);
		const high = Number(estimateData.value.high || 0);
		if (low === high) {
			return formatCurrency(low);
		}
		return `${formatCurrency(low)} - ${formatCurrency(high)}`;
	});
	const applianceTitle = computed(() => `${selectedSpec.value.brand} ${selectedSpec.value.model}`.trim());
	const applianceDesc = computed(() => buildSpecSummary(selectedSpec.value));
	const specTags = computed(() => buildSpecTagList(selectedSpec.value).slice(0, 5));
	const addressLabel = computed(() => (mode.value === 'mail' ? '上门地址' : '回收地址'));
	const hasServiceCity = computed(() => Boolean(selectedProvince.value && selectedCity.value));
	const cityDisplay = computed(() => {
		if (!hasServiceCity.value) {
			return '请选择';
		}
		return [selectedProvince.value, selectedCity.value, selectedDistrict.value].filter(Boolean).join(' · ');
	});
	const serviceHint = computed(() => {
		if (mode.value === 'mail') {
			return '请按上方平台收件信息寄出；顺丰包邮，寄出后可在订单中查看物流。';
		}
		if (!hasServiceCity.value) {
			return '请先选择服务城市；若楼层无电梯或需拆装，请提前备注。';
		}
		return `已支持 ${cityDisplay.value} 上门；若楼层无电梯或需拆装，请提前备注。`;
	});

	const currentRegionProvince = computed(() => regionOptions.find((item) => item.code === tempProvinceCode.value) || null);
	const currentRegionCity = computed(() => currentRegionProvince.value?.cities?.find((item) => item.code === tempCityCode.value) || null);
	const currentRegionDistrict = computed(() => currentRegionCity.value?.districts?.find((item) => item.code === tempDistrictCode.value) || null);
	const tempProvinceName = computed(() => currentRegionProvince.value?.name || '');
	const tempCityName = computed(() => currentRegionCity.value?.name || '');
	const tempDistrictName = computed(() => currentRegionDistrict.value?.name || '');
	const currentCityRegionOptions = computed(() =>
		getRegionStepOptions({
			regionOptionsList: regionOptions,
			regionStep: cityStep.value,
			provinceCode: tempProvinceCode.value,
			cityCode: tempCityCode.value
		})
	);

	const clearServiceCity = () => {
		selectedProvince.value = '';
		selectedCity.value = '';
		selectedDistrict.value = '';
		selectedRegion.value = [];
	};

	const applyServiceCity = (regionNames = []) => {
		const selection = resolveRegionSelection(regionOptions, regionNames, { fallback: false });
		selectedProvince.value = selection.province?.name || '';
		selectedCity.value = selection.city?.name || '';
		selectedDistrict.value = selection.district?.name || '';
		selectedRegion.value = selection.region;
		tempProvinceCode.value = selection.provinceCode;
		tempCityCode.value = selection.cityCode;
		tempDistrictCode.value = selection.districtCode;
	};

	const syncCityPickerState = (regionNames = selectedRegion.value) => {
		const selection = resolveRegionSelection(regionOptions, regionNames, { fallback: false });
		tempProvinceCode.value = selection.provinceCode;
		tempCityCode.value = selection.cityCode;
		tempDistrictCode.value = selection.districtCode;
		if (selection.provinceCode && selection.cityCode) {
			cityStep.value = 3;
		} else if (selection.provinceCode) {
			cityStep.value = 2;
		} else {
			cityStep.value = 1;
		}
	};

	const isCityRegionOptionSelected = (item) => {
		if (cityStep.value === 1) {
			return tempProvinceCode.value === item.code;
		}
		if (cityStep.value === 2) {
			return tempCityCode.value === item.code;
		}
		return tempDistrictCode.value === item.code;
	};

	const selectCityRegionOption = (item) => {
		if (cityStep.value === 1) {
			tempProvinceCode.value = item.code;
			tempCityCode.value = '';
			tempDistrictCode.value = '';
			cityStep.value = 2;
			return;
		}
		if (cityStep.value === 2) {
			tempCityCode.value = item.code;
			tempDistrictCode.value = '';
			cityStep.value = 3;
			return;
		}
		tempDistrictCode.value = item.code;
	};

	const openCityPicker = () => {
		syncCityPickerState(selectedRegion.value);
		showCity.value = true;
	};

	const syncSelectedSlotForDate = () => {
		const current = currentSlots.value.find((item) => item.time === selectedSlot.value);
		if (current && !current.disabled && !current.full) {
			return;
		}
		const firstAvailable = currentSlots.value.find((item) => !item.disabled && !item.full);
		selectedSlot.value = firstAvailable?.time || '';
	};

	const applyDefaultAppointment = () => {
		const picked = pickDefaultAppointment(appointmentOptions.value);
		selectedDate.value = picked.dateLabel;
		selectedSlot.value = picked.slot;
	};

	const refreshAppointmentOptions = async () => {
		appointmentLoading.value = true;
		try {
			appointmentOptions.value = await loadAppointmentOptions();
			if (!selectedDate.value) {
				applyDefaultAppointment();
				return;
			}
			syncSelectedSlotForDate();
		} catch (error) {
			uni.showToast({ title: error.message || '加载预约时间失败', icon: 'none' });
		} finally {
			appointmentLoading.value = false;
		}
	};

	const openTimePicker = async () => {
		await refreshAppointmentOptions();
		if (!appointmentOptions.value.length) {
			uni.showToast({ title: '暂无可预约时段', icon: 'none' });
			return;
		}
		showTime.value = true;
	};

	const changeAppointmentDate = (label) => {
		selectedDate.value = label;
		syncSelectedSlotForDate();
	};

	const refreshEstimate = async () => {
		estimateLoading.value = true;
		try {
			estimateData.value = await fetchApplianceEstimatePrice(selectedSpec.value, specPage.value);
		} finally {
			estimateLoading.value = false;
		}
	};

	const loadSettlementSpec = async (options) => {
		const draft = loadApplianceSettlementDraft();
		const categoryName = decodeURIComponent(options?.category || draft?.spec?.category || '冰箱');
		const rawSpec = draft?.spec || (options?.spec ? parseSpecFromQuery(options.spec, categoryName) : createDefaultSpec(categoryName));
		estimateLoading.value = true;
		try {
			specPage.value = await loadApplianceSpecPage(rawSpec.category || categoryName);
			selectedSpec.value = normalizeSpecFromPage(rawSpec, specPage.value, categoryName);
		} catch (error) {
			selectedSpec.value = rawSpec;
		}
		if (draft?.estimate) {
			estimateData.value = { ...draft.estimate };
		}
		await refreshEstimate();
	};

	const refreshPlatformMail = async () => {
		try {
			platformMail.value = await loadPlatformMailReceiver();
		} catch (error) {
			platformMail.value = { ...DEFAULT_PLATFORM_MAIL };
		}
	};

	onLoad((options) => {
		loadSettlementSpec(options);
		refreshAppointmentOptions();
		refreshPlatformMail();
	});

	const syncAddresses = async () => {
		try {
			addresses.value = await listBookingAddresses();
			const currentId = selectedAddress.value?.id;
			selectedAddress.value = (currentId && addresses.value.find((item) => `${item.id}` === `${currentId}`))
				|| addresses.value.find((item) => item.default)
				|| addresses.value[0]
				|| (await getDefaultAddress());
			if (selectedAddress.value?.region?.length) {
				applyServiceCity(selectedAddress.value.region);
			} else {
				clearServiceCity();
			}
		} catch (error) {
			uni.showToast({ title: error.message || '加载地址失败', icon: 'none' });
		}
	};

	onShow(() => {
		if (!ensureLoggedIn()) {
			return;
		}
		syncAddresses();
	});

	const selectSlot = (slot) => {
		if (slot.disabled || slot.full) return;
		selectedSlot.value = slot.time;
	};

	const pickAddress = (addr) => {
		selectedAddress.value = addr;
		if (addr.region?.length) {
			applyServiceCity(addr.region);
		}
		showAddress.value = false;
	};

	const editAddress = (addr) => uni.navigateTo({ url: `/packageCommon/user/address-form?id=${addr.id}&mode=edit` });
	const createAddress = () => uni.navigateTo({ url: '/packageCommon/user/address-form?mode=create' });
	const confirmCity = () => {
		if (!tempProvinceCode.value || !tempCityCode.value || !tempDistrictCode.value) {
			uni.showToast({ title: '请完整选择省市区', icon: 'none' });
			return;
		}
		selectedProvince.value = tempProvinceName.value;
		selectedCity.value = tempCityName.value;
		selectedDistrict.value = tempDistrictName.value;
		selectedRegion.value = [tempProvinceName.value, tempCityName.value, tempDistrictName.value].filter(Boolean);
		showCity.value = false;
		cityStep.value = 1;
	};
	const goModifySpec = () => {
		uni.redirectTo({
			url: `/packageRecycle/appliance/model-select?spec=${encodeSpecToQuery(selectedSpec.value)}&category=${encodeURIComponent(selectedSpec.value.category || '冰箱')}`
		});
	};
	const go = (url) => uni.navigateTo({ url });

	const submitOrder = async () => {
		if (estimateLoading.value) {
			uni.showToast({ title: '预估价格加载中，请稍候', icon: 'none' });
			return;
		}
		if (!selectedSpec.value?.modelId) {
			uni.showToast({ title: '商品型号无效，请返回修改参数', icon: 'none' });
			return;
		}
		if (!agreed.value) {
			uni.showToast({ title: '请先确认回收规则', icon: 'none' });
			return;
		}

		const isMail = mode.value === 'mail';
		if (!isMail) {
			if (!hasServiceCity.value) {
				uni.showToast({ title: '请选择服务城市', icon: 'none' });
				return;
			}
			if (!selectedAddress.value?.id) {
				uni.showToast({ title: `请先添加并选择${addressLabel.value}`, icon: 'none' });
				return;
			}
			if (!selectedSlot.value) {
				uni.showToast({ title: '请选择可预约时间', icon: 'none' });
				return;
			}
		} else {
			// 顺丰寄件不展示地址/预约，自动回填默认值供接口落库与下单
			if (!selectedAddress.value?.id) {
				const fallbackAddress = addresses.value.find((item) => item.default) || addresses.value[0] || await getDefaultAddress();
				if (fallbackAddress?.id) {
					selectedAddress.value = fallbackAddress;
				}
			}
			if (!selectedAddress.value?.id) {
				uni.showToast({ title: '请先添加收货地址后再寄件', icon: 'none' });
				return;
			}
			if (!selectedSlot.value) {
				if (!appointmentOptions.value.length) {
					try {
						appointmentOptions.value = await loadAppointmentOptions();
					} catch (error) {
						uni.showToast({ title: error.message || '加载预约时间失败', icon: 'none' });
						return;
					}
				}
				const picked = pickDefaultAppointment(appointmentOptions.value);
				selectedDate.value = picked.dateLabel || '';
				selectedSlot.value = picked.slot || '';
			}
			if (!selectedSlot.value) {
				uni.showToast({ title: '暂无可预约时段，请稍后再试', icon: 'none' });
				return;
			}
		}

		try {
			const order = await createApplianceOrder({
				addressId: selectedAddress.value.id,
				dateLabel: selectedDate.value,
				dateKey: currentDateOption.value?.dateKey,
				slot: selectedSlot.value,
				modelId: selectedSpec.value.modelId,
				estimateAmount: estimateData.value.estimate,
				estimateMinAmount: estimateData.value.low,
				estimateMaxAmount: estimateData.value.high,
				remark: applianceDesc.value,
				fulfillType: mode.value
			});
			clearApplianceSettlementDraft();
			uni.navigateTo({ url: `/packageCommon/order/success?id=${order.id}` });
		} catch (error) {
			uni.showToast({ title: error.message || '下单失败', icon: 'none' });
		}
	};
</script>



<style lang="scss">
	.appliance-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 10rpx;
		margin-top: 14rpx;
	}

	.appliance-tags__item {
		padding: 8rpx 14rpx;
		border-radius: 999rpx;
		background: #fff5f6;
		font-size: 20rpx;
		color: #a4525e;
	}

	.appliance-product-row {
		align-items: flex-start;
		gap: 16rpx;
	}

	.appliance-product-info {
		flex: 1;
		min-width: 0;
		text-align: left;
	}

	.appliance-product-info__title {
		font-size: 34rpx;
		font-weight: 700;
		color: #2f2424;
		line-height: 1.35;
	}

	.appliance-product-info__desc {
		margin-top: 8rpx;
	}

	.appliance-product-edit {
		flex-shrink: 0;
		padding-top: 4rpx;
	}

	.appliance-settlement-page {
		background: linear-gradient(180deg, #fff8f3 0%, #fff 320rpx);
	}

	.appliance-settlement-body {
		padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
	}

	.list-divider {
		height: 2rpx;
		margin: 24rpx 0;
		background: #f1e5e5;
	}

	.settlement-mode-grid {
		margin-top: 4rpx;
	}

	.settlement-mode-card {
		padding: 28rpx 24rpx;
		border-radius: 30rpx;
		box-shadow: 0 16rpx 36rpx rgba(200, 16, 46, 0.08);
		box-sizing: border-box;
		min-height: 220rpx;
	}

	.settlement-mode-card .dot-badge.is-muted {
		background: linear-gradient(180deg, #f0eaea 0%, #f7f3f3 100%);
		box-shadow: none;
	}

	.settlement-mode-card__title {
		margin-top: 22rpx;
		font-size: 34rpx;
		font-weight: 800;
		color: #2f2424;
	}

	.settlement-mode-card__desc {
		margin-top: 10rpx;
		font-size: 24rpx;
		line-height: 1.5;
	}

	.cell-list {
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.cell-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 18rpx;
		padding: 22rpx 0;
		border-bottom: 2rpx solid #f3e7e7;
	}

	.cell-item:last-child {
		border-bottom: 0;
	}

	.label {
		font-size: 28rpx;
		color: #5f4a4a;
	}

	.value {
		font-size: 28rpx;
		font-weight: 700;
		color: #2f2424;
	}

	.value.is-placeholder {
		font-weight: 500;
		color: #a48f8f;
	}

	.cell-item__address {
		flex: 1;
		justify-content: flex-end;
		gap: 12rpx;
		min-width: 0;
	}

	.address-value {
		display: flex;
		flex-direction: column;
		align-items: flex-end;
		gap: 6rpx;
		min-width: 0;
		max-width: 420rpx;
	}

	.value--address {
		max-width: 100%;
		text-align: right;
		line-height: 1.4;
		word-break: break-all;
	}

	.address-value__phone {
		font-size: 24rpx;
		color: #857272;
		line-height: 1.3;
	}

	.service-check-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16rpx;
		padding: 24rpx;
		margin-top: 18rpx;
		border-radius: 24rpx;
		background: linear-gradient(135deg, #fff5f6 0%, #fffaf1 100%);
	}

	.check-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
		margin-top: 22rpx;
		font-size: 24rpx;
		color: #6f5b5b;
	}

	.platform-mail-card__title {
		font-size: 30rpx;
		font-weight: 700;
		color: #332929;
		margin-bottom: 18rpx;
	}

	.platform-mail-card__row {
		display: flex;
		align-items: flex-start;
		justify-content: space-between;
		gap: 24rpx;
	}

	.platform-mail-card__row + .platform-mail-card__row {
		margin-top: 14rpx;
	}

	.platform-mail-card__label {
		flex-shrink: 0;
		font-size: 26rpx;
		color: #857272;
	}

	.platform-mail-card__value {
		flex: 1;
		text-align: right;
		font-size: 26rpx;
		color: #332929;
		line-height: 1.5;
	}

	.appliance-estimate-loading {
		font-size: 40rpx;
		font-weight: 700;
		color: #a68f8f;
	}

	.appliance-settlement-footer {
		align-items: center;
	}

	.appliance-settlement-footer__btn {
		flex: 1;
		width: 100%;
		min-width: 0;
	}
</style>
