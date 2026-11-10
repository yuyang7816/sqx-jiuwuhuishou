<template>
	<view class="app-page digital-settlement-page">
		<top-nav title="旧机估价结算" :show-more="false"></top-nav>

		<scroll-view scroll-y class="page-body compact digital-settlement-body">
			<view class="digital-price-card">
				<view class="digital-price-card__top row-between" style="align-items:flex-start;">
					<view>
						<view class="digital-price-card__model">{{ currentModel }}</view>
						<view class="digital-price-card__label">预计最高得</view>
					</view>
					<text class="digital-price-card__currency">￥</text>
				</view>
				<view class="digital-price-card__amount">{{ currentPrice.replace('￥', '¥') }}</view>
				<view class="digital-price-card__tags">
					<view class="digital-price-card__tag row-between" style="justify-content:flex-start;"><u-icon name="checkmark-circle-fill" size="14" color="#ff4e5f"></u-icon><text>官方质检</text></view>
					<view class="digital-price-card__tag row-between" style="justify-content:flex-start;"><u-icon name="car" size="14" color="#ff4e5f"></u-icon><text>顺丰包邮</text></view>
					<view class="digital-price-card__tag is-gold">7天保价</view>
				</view>
			</view>

			<view class="digital-card">
				<view class="digital-card__title">回收流程</view>
				<view class="digital-flow">
					<view v-for="(item, index) in flow" :key="item.name" class="digital-flow__item" :class="{ 'is-active': item.active }">
						<view class="digital-flow__icon-wrap">
							<view class="digital-flow__line" v-if="index !== flow.length - 1"></view>
							<view class="digital-flow__icon"><u-icon :name="item.icon" size="16" :color="item.active ? '#ffffff' : '#8e7f7f'"></u-icon></view>
						</view>
						<view class="digital-flow__text">{{ item.name }}</view>
					</view>
				</view>
			</view>

			<view class="digital-card">
				<view class="digital-card__title">取件信息</view>
				<view class="digital-info-item row-between" @click="showAddress = true">
					<view class="digital-info-item__left">
						<u-icon name="map" size="18" color="#8d7777"></u-icon>
						<view>
							<view class="digital-info-item__main">{{ addressDisplay.main }}</view>
							<view class="digital-info-item__sub">{{ addressDisplay.sub }}</view>
						</view>
					</view>
					<u-icon name="arrow-right" size="16" color="#c4b7b7"></u-icon>
				</view>
				<view class="digital-info-divider"></view>
				<view class="digital-info-item row-between" @click="openTimePicker">
					<view class="digital-info-item__left">
						<u-icon name="clock" size="18" color="#8d7777"></u-icon>
						<view>
							<view class="digital-info-item__label">预约取件时间</view>
							<view class="digital-info-item__time">{{ appointmentDisplay }}</view>
						</view>
					</view>
					<u-icon name="arrow-right" size="16" color="#c4b7b7"></u-icon>
				</view>
			</view>

			<view class="digital-card digital-mail-card">
				<view class="digital-card__title">平台收件信息</view>
				<view class="digital-mail-card__row">
					<text class="digital-mail-card__label">收件人</text>
					<text class="digital-mail-card__value">{{ platformMail.receiver }}</text>
				</view>
				<view class="digital-mail-card__row">
					<text class="digital-mail-card__label">联系电话</text>
					<text class="digital-mail-card__value">{{ platformMail.phone }}</text>
				</view>
				<view class="digital-mail-card__row">
					<text class="digital-mail-card__label">收件地址</text>
					<text class="digital-mail-card__value">{{ platformMail.address }}</text>
				</view>
			</view>

			<view class="digital-card digital-service-card">
				<view class="digital-service-row" @click="privacyService = !privacyService">
					<text class="digital-service-row__label">隐私清除服务</text>
					<view class="digital-service-row__right">
						<text class="digital-service-row__value" :class="{ 'is-active': privacyService }">{{ privacyService ? '军工级擦除' : '未选择' }}</text>
						<u-icon :name="privacyService ? 'checkmark-circle-fill' : 'checkmark-circle'" size="22" :color="privacyService ? '#ff4e5f' : '#c4b7b7'"></u-icon>
					</view>
				</view>
				<view class="digital-info-divider"></view>
				<view class="digital-service-row" @click="go('/packageRecycle/common/service-check')">
					<text class="digital-service-row__label">规则与质检说明</text>
					<view class="digital-service-row__right"><text class="digital-service-row__value">查看</text><u-icon name="arrow-right" size="16" color="#c4b7b7"></u-icon></view>
				</view>
			</view>

			<view class="digital-carbon-card">
				<view class="digital-carbon-card__icon">
					<image class="digital-carbon-card__logo" src="/static/home-logo.png" mode="aspectFit"></image>
				</view>
				<view class="digital-carbon-card__info">
					<view class="digital-carbon-card__title">此次回收将减少碳排放约 25.4kg</view>
					<view class="digital-carbon-card__desc">相当于为地球种植了 2 棵成年樟树</view>
				</view>
			</view>
		</scroll-view>

		<floating-service></floating-service>

		<u-popup :show="showAddress" mode="bottom" @close="showAddress = false">
			<view class="sheet-body address-sheet">
				<view class="sheet-handle"></view>
				<view class="address-sheet__head">
					<u-icon name="close" size="22" color="#5f4a4a" @click="showAddress = false"></u-icon>
					<view class="sheet-title address-sheet__title">选择取件地址</view>
					<view class="address-sheet__placeholder"></view>
				</view>
				<view class="address-sheet__tabs">
					<view class="address-sheet__tab" :class="{ 'is-active': regionStep >= 1 }" @click="regionStep = 1">{{ tempProvinceName || '请选择' }}</view>
					<view class="address-sheet__tab" :class="{ 'is-active': regionStep >= 2, 'is-warn': regionStep >= 2 && !tempCityName }" @click="tempProvinceCode && (regionStep = 2)">{{ tempCityName || '请选择' }}</view>
					<view class="address-sheet__tab" :class="{ 'is-active': regionStep === 3, 'is-warn': regionStep === 3 && !tempDistrictName }" @click="tempCityCode && (regionStep = 3)">{{ tempDistrictName || '请选择' }}</view>
				</view>
				<scroll-view scroll-y class="address-sheet__panel">
					<view class="address-sheet__areas">
						<view
							v-for="item in currentRegionOptions"
							:key="item.code"
							class="address-sheet__area"
							:class="{ 'is-selected': isRegionOptionSelected(item) }"
							@click="selectRegionOption(item)"
						>
							{{ item.name }}
						</view>
					</view>
				</scroll-view>
				<view class="address-sheet__section">
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
						<view class="sheet-title time-sheet__title">选择取件时间</view>
						<view class="time-sheet__desc">请在预约时间段内保持电话畅通，顺丰小哥将上门取件</view>
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

		<view class="digital-bottom-bar">
			<view class="digital-bottom-bar__top row-between">
				<view>
					<view class="digital-bottom-bar__label">最终预估总价</view>
					<view class="digital-bottom-bar__amount">{{ `${currentPrice.replace('￥', '¥')} 起` }}</view>
				</view>
				<view class="digital-bottom-bar__btn" @click="submitOrder"><u-icon name="calendar" size="18" color="#ffffff"></u-icon><text>预约免费寄件</text></view>
			</view>
			<view class="digital-bottom-bar__agree row-between" style="justify-content:flex-start;" @click="agreed = !agreed">
				<u-icon :name="agreed ? 'checkmark-circle-fill' : 'checkmark-circle'" size="14" :color="agreed ? '#cf1432' : '#bba8a8'"></u-icon>
				<text class="digital-bottom-bar__agree-text">
					同意
					<text class="digital-bottom-bar__link" @click.stop="openAgreement('service')">《用户回收协议》</text>
					及
					<text class="digital-bottom-bar__link" @click.stop="openAgreement('privacy')">《隐私保障政策》</text>
					，接受以质检结果结算
				</text>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref, watch } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { listBookingAddresses } from '@/common/services/address.js';
	import { createDigitalOrder } from '@/common/services/order.js';
	import { loadAppointmentOptions, pickDefaultAppointment } from '@/common/services/appointment.js';
	import { DEFAULT_PLATFORM_MAIL, loadPlatformMailReceiver } from '@/common/services/platformMail.js';
	import { getRegionStepOptions, regionOptions, resolveRegionSelection } from '@/common/utils/region.js';

	const parseSlotHour = (time) => {
		const start = `${time || ''}`.split('-')[0].trim();
		const [hour = '0'] = start.split(':');
		return Number(hour);
	};

	const agreed = ref(true);
	const privacyService = ref(true);
	const showAddress = ref(false);
	const showTime = ref(false);
	const selectedDate = ref('');
	const selectedSlot = ref('');
	const currentModel = ref('iPhone 15 Pro Max 256G');
	const currentPrice = ref('￥5,480');
	const modelId = ref(1);
	const bookingScene = ref('digital');
	const addresses = ref([]);
	const selectedAddress = ref(null);
	const selectedRegion = ref(['上海市', '上海市', '浦东新区']);
	const regionStep = ref(1);
	const tempProvinceCode = ref('');
	const tempCityCode = ref('');
	const tempDistrictCode = ref('');
	const appointmentOptions = ref([]);
	const platformMail = ref({ ...DEFAULT_PLATFORM_MAIL });
	const flow = [
		{ name: '预约寄件', icon: 'calendar', active: true },
		{ name: '顺丰取件', icon: 'car' },
		{ name: '专业质检', icon: 'search' },
		{ name: '极速打款', icon: 'coupon' }
	];
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
	const addressDisplay = computed(() => {
		if (!selectedAddress.value?.id) {
			return { main: '请添加取件地址', sub: '点击选择取件地址' };
		}
		return {
			main: selectedAddress.value.user?.replace(/\s(\d{3})\d{4}(\d{4})/, ' $1****$2') || selectedAddress.value.name || '联系人',
			sub: selectedAddress.value.address
		};
	});

	const currentRegionProvince = computed(() => regionOptions.find((item) => item.code === tempProvinceCode.value) || null);
	const currentRegionCity = computed(() => currentRegionProvince.value?.cities?.find((item) => item.code === tempCityCode.value) || null);
	const currentRegionDistrict = computed(() => currentRegionCity.value?.districts?.find((item) => item.code === tempDistrictCode.value) || null);
	const tempProvinceName = computed(() => currentRegionProvince.value?.name || '');
	const tempCityName = computed(() => currentRegionCity.value?.name || '');
	const tempDistrictName = computed(() => currentRegionDistrict.value?.name || '');
	const currentRegionOptions = computed(() =>
		getRegionStepOptions({
			regionOptionsList: regionOptions,
			regionStep: regionStep.value,
			provinceCode: tempProvinceCode.value,
			cityCode: tempCityCode.value
		})
	);

	const syncRegionState = (regionNames = selectedRegion.value) => {
		const selection = resolveRegionSelection(regionOptions, regionNames);
		tempProvinceCode.value = selection.provinceCode;
		tempCityCode.value = selection.cityCode;
		tempDistrictCode.value = selection.districtCode;
		selectedRegion.value = selection.region;
	};

	const isRegionOptionSelected = (item) => {
		if (regionStep.value === 1) {
			return tempProvinceCode.value === item.code;
		}
		if (regionStep.value === 2) {
			return tempCityCode.value === item.code;
		}
		return tempDistrictCode.value === item.code;
	};

	const selectRegionOption = (item) => {
		if (regionStep.value === 1) {
			tempProvinceCode.value = item.code;
			tempCityCode.value = item.cities?.[0]?.code || '';
			tempDistrictCode.value = item.cities?.[0]?.districts?.[0]?.code || '';
			regionStep.value = 2;
			return;
		}
		if (regionStep.value === 2) {
			tempCityCode.value = item.code;
			tempDistrictCode.value = item.districts?.[0]?.code || '';
			regionStep.value = 3;
			return;
		}
		tempDistrictCode.value = item.code;
		selectedRegion.value = [tempProvinceName.value, tempCityName.value, tempDistrictName.value].filter(Boolean);
	};

	onLoad((options) => {
		bookingScene.value = options.scene === 'luxury' ? 'luxury' : 'digital';
		if (options.model) {
			currentModel.value = decodeURIComponent(options.model);
		}
		if (options.price) {
			currentPrice.value = decodeURIComponent(options.price);
		}
		if (options.modelId) {
			modelId.value = Number(options.modelId) || 1;
		}
		refreshAppointmentOptions();
		refreshPlatformMail();
	});

	const refreshPlatformMail = async () => {
		try {
			platformMail.value = await loadPlatformMailReceiver();
		} catch (error) {
			platformMail.value = { ...DEFAULT_PLATFORM_MAIL };
		}
	};

	const syncAddresses = async () => {
		try {
			addresses.value = await listBookingAddresses();
			const currentId = selectedAddress.value?.id;
			selectedAddress.value = (currentId && addresses.value.find((item) => `${item.id}` === `${currentId}`))
				|| addresses.value.find((item) => item.default)
				|| addresses.value[0]
				|| null;
			if (selectedAddress.value?.region?.length) {
				syncRegionState(selectedAddress.value.region);
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

	watch(showAddress, (visible) => {
		if (visible) {
			syncRegionState(selectedAddress.value?.region?.length ? selectedAddress.value.region : selectedRegion.value);
			regionStep.value = 1;
		}
	});

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
		try {
			appointmentOptions.value = await loadAppointmentOptions();
			if (!selectedDate.value) {
				applyDefaultAppointment();
				return;
			}
			syncSelectedSlotForDate();
		} catch (error) {
			uni.showToast({ title: error.message || '加载预约时间失败', icon: 'none' });
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

	const parseEstimateAmount = () => {
		const raw = `${currentPrice.value}`.replace(/[￥¥,\s]/g, '');
		return Number(raw) || 0;
	};

	const selectSlot = (slot) => {
		if (slot.disabled || slot.full) {
			return;
		}
		selectedSlot.value = slot.time;
	};

	const pickAddress = (addr) => {
		selectedAddress.value = addr;
		if (addr.region?.length) {
			syncRegionState(addr.region);
		}
		showAddress.value = false;
	};

	const editAddress = (addr) => uni.navigateTo({ url: `/packageCommon/user/address-form?id=${addr.id}&mode=edit` });
	const createAddress = () => uni.navigateTo({ url: '/packageCommon/user/address-form?mode=create' });
	const go = (url) => uni.navigateTo({ url });
	const openAgreement = (type) => {
		uni.navigateTo({ url: `/packageCommon/user/agreement?type=${type}` });
	};
	const submitOrder = async () => {
		if (!agreed.value) {
			uni.showToast({ title: '请先同意回收协议', icon: 'none' });
			return;
		}
		if (!selectedAddress.value?.id) {
			uni.showToast({ title: '请先选择取件地址', icon: 'none' });
			return;
		}
		if (!selectedSlot.value) {
			uni.showToast({ title: '请选择可预约时间', icon: 'none' });
			return;
		}
		try {
			const order = await createDigitalOrder({
				addressId: selectedAddress.value.id,
				dateLabel: selectedDate.value,
				dateKey: currentDateOption.value?.dateKey,
				slot: selectedSlot.value,
				modelId: modelId.value,
				estimateAmount: parseEstimateAmount(),
				remark: privacyService.value ? '需要隐私清除服务' : '',
				scene: bookingScene.value
			});
			uni.navigateTo({ url: `/packageCommon/order/success?id=${order.id}` });
		} catch (error) {
			uni.showToast({ title: error.message || '下单失败', icon: 'none' });
		}
	};
</script>

<style lang="scss">
	.digital-settlement-page {
		background: linear-gradient(180deg, #fff8f6 0%, #fff 340rpx);
	}

	.digital-settlement-body {
		padding-top: 14rpx;
		padding-bottom: calc(230rpx + env(safe-area-inset-bottom));
	}

	.digital-price-card,
	.digital-card,
	.digital-carbon-card {
		background: #fff;
		border-radius: 26rpx;
		box-shadow: 0 14rpx 32rpx rgba(111, 79, 79, 0.06);
	}

	.digital-price-card {
		padding: 28rpx 26rpx 24rpx;
	}

	.digital-price-card__model { font-size: 28rpx; color: #5d4949; }
	.digital-price-card__label { margin-top: 10rpx; font-size: 24rpx; color: #8a7878; }
	.digital-price-card__currency { font-size: 60rpx; line-height: 1; color: #d7c79f; }
	.digital-price-card__amount { margin-top: 20rpx; font-size: 74rpx; font-weight: 800; line-height: 1; color: #1f1f1f; }
	.digital-price-card__tags { display: flex; align-items: center; gap: 18rpx; margin-top: 24rpx; flex-wrap: wrap; }
	.digital-price-card__tag { display: inline-flex; align-items: center; gap: 8rpx; font-size: 22rpx; color: #685757; }
	.digital-price-card__tag.is-gold { height: 42rpx; padding: 0 16rpx; border-radius: 999rpx; background: #fff4d4; color: #8a6400; }
	.digital-card { margin-top: 22rpx; padding: 24rpx; }
	.digital-card__title { font-size: 32rpx; font-weight: 700; color: #332929; }
	.digital-flow { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 6rpx; margin-top: 22rpx; }
	.digital-flow__item { text-align: center; }
	.digital-flow__icon-wrap { position: relative; height: 46rpx; margin-bottom: 14rpx; }
	.digital-flow__line { position: absolute; left: calc(50% + 20rpx); right: -18rpx; top: 22rpx; height: 2rpx; background: #e7dada; }
	.digital-flow__icon { position: absolute; left: 50%; top: 0; transform: translateX(-50%); width: 42rpx; height: 42rpx; border-radius: 50%; background: #f0ebeb; display: flex; align-items: center; justify-content: center; z-index: 2; }
	.digital-flow__item.is-active .digital-flow__icon { background: #cf1432; }
	.digital-flow__text { font-size: 22rpx; color: #6f5f5f; }
	.digital-info-item { padding: 22rpx 0; }
	.digital-info-item__left { display: flex; align-items: flex-start; gap: 16rpx; flex: 1; }
	.digital-info-item__main,
	.digital-info-item__time,
	.digital-service-row__label { font-size: 28rpx; font-weight: 700; color: #2f2424; }
	.digital-info-item__sub,
	.digital-info-item__label,
	.digital-service-row__value { margin-top: 8rpx; font-size: 24rpx; color: #7a6868; }
	.digital-info-divider { height: 2rpx; background: #f2e6e6; }
	.digital-service-row { display: flex; align-items: center; justify-content: space-between; gap: 16rpx; padding: 8px 0; }
	.digital-service-row__right { display: flex; align-items: center; gap: 10rpx; }
	.digital-service-row__value.is-active { color: #ff4e5f; font-weight: 700; }
	.digital-mail-card { margin-top: 22rpx; }
	.digital-mail-card__row {
		display: flex;
		align-items: flex-start;
		gap: 20rpx;
		margin-top: 18rpx;
	}
	.digital-mail-card__label {
		width: 140rpx;
		flex-shrink: 0;
		font-size: 26rpx;
		color: #7a6868;
	}
	.digital-mail-card__value {
		flex: 1;
		font-size: 26rpx;
		font-weight: 600;
		color: #2f2424;
		line-height: 1.5;
	}
	.digital-carbon-card { margin-top: 22rpx; padding: 24rpx; display: flex; align-items: center; gap: 18rpx; background: linear-gradient(135deg, #d01d36 0%, #f15f6f 100%); color: #fff; }
	.digital-carbon-card__icon { width: 76rpx; height: 76rpx; border-radius: 50%; background: rgba(255,255,255,0.92); display: flex; align-items: center; justify-content: center; flex-shrink: 0; overflow: hidden; }
	.digital-carbon-card__logo { width: 56rpx; height: 56rpx; }
	.digital-carbon-card__title { font-size: 30rpx; font-weight: 700; }
	.digital-carbon-card__desc { margin-top: 8rpx; font-size: 24rpx; opacity: 0.9; }
	.digital-bottom-bar { position: fixed; left: 0; right: 0; bottom: 0; padding: 20rpx 30rpx calc(20rpx + env(safe-area-inset-bottom)); background: rgba(255,255,255,0.96); box-shadow: 0 -8rpx 30rpx rgba(89,49,49,0.06); backdrop-filter: blur(14rpx); }
	.digital-bottom-bar__label { font-size: 22rpx; color: #7b6969; }
	.digital-bottom-bar__amount { margin-top: 8rpx; font-size: 42rpx; font-weight: 800; color: #cf1432; }
	.digital-bottom-bar__btn { min-width: 260rpx; height: 92rpx; border-radius: 999rpx; background: linear-gradient(135deg, #d01231 0%, #b80826 100%); color: #fff; display: flex; align-items: center; justify-content: center; gap: 10rpx; font-size: 30rpx; font-weight: 700; box-shadow: 0 16rpx 36rpx rgba(200,16,46,0.25); }
	.digital-bottom-bar__agree { margin-top: 16rpx; gap: 10rpx; font-size: 22rpx; color: #6e5d5d; align-items: flex-start; }
	.digital-bottom-bar__agree-text { flex: 1; line-height: 1.5; }
	.digital-bottom-bar__link { color: #cf1432; font-weight: 700; }
</style>
