<template>
	<view class="app-page clothes-page">
		<top-nav :title="pageTitle"></top-nav>
		<scroll-view scroll-y class="page-body compact clothes-body">
			<view class="card hero-soft">
				<view class="row-between" style="align-items: flex-start;">
					<view>
						<view class="brand-text" style="font-size: 34rpx; font-weight: 700;">回收单价</view>
						<view style="margin-top: 14rpx; font-size: 58rpx; color: #c8102e; font-weight: 800;">
							<text>{{ unitPrice }}</text>
							<text style="font-size: 28rpx; color: #7a6161;">{{ unitSuffix }}</text>
						</view>
						<view class="muted" style="margin-top: 12rpx;">{{ priceDesc }}</view>
					</view>
					<view style="width: 200rpx;"><mock-art :label="heroLabel" :desc="heroDesc" theme="sand"></mock-art></view>
				</view>
				<view class="flow-row" style="margin-top: 24rpx;">
					<view v-for="item in flow" :key="item.txt" class="flow-item">
						<view class="num">{{ item.step }}</view>
						<view class="txt">{{ item.txt }}</view>
					</view>
				</view>
			</view>

			<view class="section-title"><text>可回收品类</text><text class="hint" @click="go('/packageRecycle/common/service-check')">服务规则</text></view>
			<scroll-view scroll-x class="tab-pills">
				<view style="display:flex; gap:20rpx;">
					<view v-for="item in types" :key="item.id || item.name" class="service-card" style="width: 180rpx; text-align: center; flex-shrink: 0;">
						<view class="dot-badge" style="margin: 0 auto;"><u-icon :name="item.icon || 'integral'" size="20" color="#c8102e"></u-icon></view>
						<view class="title" style="font-size: 28rpx; margin-top: 16rpx;">{{ item.name }}</view>
					</view>
				</view>
			</scroll-view>

			<view class="card" style="margin-top: 28rpx;">
				<view style="font-size: 34rpx; font-weight: 700;">预约信息</view>
				<view class="feature-list" style="margin-top: 24rpx;">
					<view class="ghost-panel" @click="showAddress = true">
						<view class="row-between"><text style="font-size: 30rpx; font-weight: 700;">{{ selectedAddressTitle }}</text><u-icon name="arrow-right" size="18" color="#8f7575"></u-icon></view>
						<view class="muted" style="margin-top: 10rpx;">{{ selectedAddressDesc }}</view>
					</view>
					<view class="ghost-panel" @click="openTimePicker">
						<view class="row-between"><text style="font-size: 30rpx; font-weight: 700;">预约时间</text><text class="value" :style="selectedSlot ? 'color:#c8102e;font-weight:700;' : ''">{{ selectedSlot ? `${selectedDateDay} ${selectedSlot}` : '请选择 >' }}</text></view>
					</view>
					<view class="service-range-card" @click="go('/packageRecycle/common/service-check')">
						<view>
							<view style="font-size: 30rpx; font-weight: 700;">服务范围校验</view>
							<view class="muted" style="margin-top: 8rpx;">{{ serviceRangeText }}</view>
						</view>
						<view class="brand-pill">{{ serviceRangeTag }}</view>
					</view>
				</view>

				<view style="margin-top: 32rpx; font-size: 34rpx; font-weight: 700;">预估重量</view>
				<view class="grid-two" style="margin-top: 20rpx; grid-template-columns: repeat(3,minmax(0,1fr));">
					<view v-for="item in weights" :key="item.title" class="ghost-panel" :style="weight === item.title ? 'border-color:#c8102e;background:#fff6f7;' : ''" @click="weight = item.title">
						<view style="font-size: 28rpx; font-weight: 700; color: #c8102e;">{{ item.title }}</view>
						<view class="muted" style="margin-top: 8rpx;">{{ item.desc }}</view>
						<view v-if="item.estimateAmount" style="margin-top: 10rpx; font-size: 24rpx; color: #c8102e; font-weight: 700;">约 {{ formatEstimatePrice(item.estimateAmount) }}</view>
					</view>
				</view>

				<view class="estimate-preview-card">
					<view class="muted">当前预估回收价</view>
					<view class="estimate-preview-card__price">{{ estimatePriceText }}</view>
					<view class="muted" style="margin-top: 8rpx;">按所选重量区间预估，最终以实际上门称重结算为准</view>
				</view>

				<view class="check-row" style="margin-top: 28rpx;" @click="agreed = !agreed">
					<u-icon :name="agreed ? 'checkmark-circle-fill' : 'checkmark-circle'" size="18" :color="agreed ? '#cf1432' : '#c7b6b6'"></u-icon>
					<text>{{ agreementText }}</text>
				</view>
			</view>

			<view class="faq-item" style="margin-top: 28rpx;" @click="noticeOpen = !noticeOpen">
				<view class="faq-question"><text>下单须知</text><u-icon :name="noticeOpen ? 'arrow-up' : 'arrow-down'" size="16" color="#7a6161"></u-icon></view>
				<view v-if="noticeOpen" class="faq-answer">{{ orderNotice }}</view>
			</view>

			<view class="card" style="margin-top: 28rpx; padding: 0; overflow: hidden;">
				<view class="row-between" style="padding: 28rpx; align-items: stretch;">
					<view style="flex: 1; padding-right: 18rpx;">
						<view style="font-size: 34rpx; font-weight: 800;">{{ scienceTitle }}</view>
						<view class="muted" style="margin-top: 12rpx;">{{ scienceDesc }}</view>
					</view>
					<view style="width: 230rpx;"><mock-art :label="scienceArtLabel" :desc="scienceArtDesc" theme="sand"></mock-art></view>
				</view>
			</view>
		</scroll-view>

		<floating-service></floating-service>
		<view class="footer-bar clothes-footer">
			<view class="clothes-footer__price">
				<view class="muted">预估回收价</view>
				<view class="clothes-footer__amount">{{ estimatePriceText }}</view>
			</view>
			<view class="btn-main clothes-footer__btn" @click="submitBooking">马上预约回收</view>
		</view>

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
	import { computed, ref, watch } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { listBookingAddresses } from '@/common/services/address.js';
	import { loadClothesBookingPageData, resolveBookingScene } from '@/common/services/clothes.js';
	import { formatEstimatePrice, formatServiceRangeDesc } from '@/common/adapters/clothes.js';
	import { createClothesOrder } from '@/common/services/order.js';
	import { loadAppointmentOptions, pickDefaultAppointment } from '@/common/services/appointment.js';
	import { checkServiceAreaByAddress } from '@/common/services/serviceCheck.js';
	import { getRegionStepOptions, regionOptions, resolveRegionSelection } from '@/common/utils/region.js';

	const parseSlotHour = (time) => {
		const start = `${time || ''}`.split('-')[0].trim();
		const [hour = '0'] = start.split(':');
		return Number(hour);
	};

	const flow = [{ step: 1, txt: '在线预约' }, { step: 2, txt: '免费上门' }, { step: 3, txt: '称重结算' }, { step: 4, txt: '环保返积分' }];
	const bookingScene = ref('clothes');
	const pageTitle = ref('旧衣服回收');
	const heroLabel = ref('旧衣叠放');
	const heroDesc = ref('公益捐赠 / 资源再生');
	const scienceArtLabel = ref('衣物新生');
	const scienceArtDesc = ref('循环利用');
	const unitPrice = ref('0.8');
	const unitSuffix = ref('/kg 起');
	const priceDesc = ref('支持上门回收、称重结算、环保积分返还');
	const types = ref([
		{ id: 1, name: '上衣/裤子', icon: 'integral' },
		{ id: 2, name: '床单/毛毯', icon: 'integral' },
		{ id: 3, name: '鞋帽/包包', icon: 'integral' },
		{ id: 4, name: '儿童旧衣', icon: 'integral' }
	]);
	const weights = ref([
		{ title: '5-20kg', desc: '日常清理', estimateAmount: 18.5 },
		{ title: '20-50kg', desc: '换季储物', estimateAmount: 36 },
		{ title: '>50kg', desc: '搬家回收', estimateAmount: 58 }
	]);
	const serviceRangeDescTemplate = ref('当前选择区域：{{area}}，支持免费上门；超区将提示改约或邮寄。');
	const serviceRangeTag = ref('已覆盖');
	const orderNotice = ref('请提前打包整理好旧衣物，避免混入潮湿、发霉或不可回收品。若回收员到场后发现不符合回收标准，订单会进入异常处理。');
	const scienceTitle = ref('旧衣去向科普');
	const scienceDesc = ref('每一件旧衣，都能成为新的资源，或温暖远方的他。');
	const agreementText = ref('我已知晓最低 5kg 起约、不可回收项和实际称重结算规则');
	const noticeOpen = ref(false);
	const agreed = ref(true);
	const weight = ref('5-20kg');
	const showAddress = ref(false);
	const showTime = ref(false);
	const selectedRegion = ref(['上海市', '上海市', '浦东新区']);
	const selectedArea = ref('浦东新区');
	const regionStep = ref(1);
	const tempProvinceCode = ref('');
	const tempCityCode = ref('');
	const tempDistrictCode = ref('');
	const selectedAddress = ref({ id: 1, title: '公司', desc: '上海市浦东新区张江路365号纳贤中心', user: '张经理 138****0987' });
	const appointmentOptions = ref([]);
	const selectedDate = ref('');
	const selectedSlot = ref('');

	const serviceRangeText = computed(() => formatServiceRangeDesc(serviceRangeDescTemplate.value, selectedArea.value));
	const selectedWeightOption = computed(() => weights.value.find((item) => item.title === weight.value) || null);
	const estimatePriceText = computed(() => formatEstimatePrice(selectedWeightOption.value?.estimateAmount || 0));

	const currentProvince = computed(() => regionOptions.find((item) => item.code === tempProvinceCode.value) || null);
	const currentCity = computed(() => currentProvince.value?.cities?.find((item) => item.code === tempCityCode.value) || null);
	const currentDistrict = computed(() => currentCity.value?.districts?.find((item) => item.code === tempDistrictCode.value) || null);
	const tempProvinceName = computed(() => currentProvince.value?.name || '');
	const tempCityName = computed(() => currentCity.value?.name || '');
	const tempDistrictName = computed(() => currentDistrict.value?.name || '');
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
		selectedArea.value = selection.district?.name || selection.city?.name || selection.province?.name || '';
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
		selectedArea.value = item.name;
	};

	const addresses = ref([]);
	const dateList = computed(() => appointmentOptions.value.map(({ label, day }) => ({ label, day })));
	const currentDateOption = computed(() => appointmentOptions.value.find((item) => item.label === selectedDate.value) || appointmentOptions.value[0] || null);
	const currentSlots = computed(() => currentDateOption.value?.slots || []);
	const morningSlots = computed(() => currentSlots.value.filter((item) => parseSlotHour(item.time) < 12));
	const afternoonSlots = computed(() => currentSlots.value.filter((item) => parseSlotHour(item.time) >= 12));

	const selectedAddressTitle = computed(() => selectedAddress.value.title || '请选择取件地址');
	const selectedAddressDesc = computed(() => {
		if (!selectedAddress.value.id) {
			return '请选择取件地址';
		}
		return `${selectedAddress.value.desc} · ${selectedAddress.value.user}`;
	});
	const selectedDateDay = computed(() => currentDateOption.value?.day || dateList.value.find((item) => item.label === selectedDate.value)?.day || '');

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

	const syncServiceRangeCheck = async (addressItem) => {
		if (!addressItem) {
			serviceRangeTag.value = '待确认';
			return;
		}
		try {
			const status = await checkServiceAreaByAddress(addressItem);
			serviceRangeTag.value = status.tag;
			if (!status.inService) {
				serviceRangeDescTemplate.value = `当前选择区域：{{area}}，${status.desc}`;
				return;
			}
			if (!serviceRangeDescTemplate.value.includes('{{area}}')) {
				serviceRangeDescTemplate.value = '当前选择区域：{{area}}，支持免费上门；超区将提示改约或邮寄。';
			}
		} catch (error) {
			serviceRangeTag.value = '待确认';
		}
	};

	const syncAddresses = async () => {
		try {
			addresses.value = await listBookingAddresses();
			const defaultAddress = addresses.value.find((item) => item.id === selectedAddress.value.id) || addresses.value.find((item) => item.default) || addresses.value[0];
			if (defaultAddress) {
				selectedAddress.value = { id: defaultAddress.id, title: defaultAddress.name, desc: defaultAddress.address, user: defaultAddress.user };
				if (defaultAddress.region?.length) {
					syncRegionState(defaultAddress.region);
				}
				await syncServiceRangeCheck(defaultAddress);
			}
		} catch (error) {
			uni.showToast({ title: error.message || '加载地址失败', icon: 'none' });
		}
	};

	watch(showAddress, (visible) => {
		if (visible) {
			syncRegionState(selectedRegion.value);
			regionStep.value = 1;
		}
	});

	watch(currentSlots, () => {
		syncSelectedSlotForDate();
	}, { immediate: true });

	const syncPageData = async () => {
		try {
			const data = await loadClothesBookingPageData(bookingScene.value);
			bookingScene.value = data.scene || bookingScene.value;
			pageTitle.value = data.pageTitle;
			heroLabel.value = data.heroLabel;
			heroDesc.value = data.heroDesc;
			scienceArtLabel.value = data.scienceArtLabel;
			scienceArtDesc.value = data.scienceArtDesc;
			unitPrice.value = data.unitPrice;
			unitSuffix.value = data.unitSuffix;
			priceDesc.value = data.priceDesc;
			types.value = data.types;
			weights.value = data.weights;
			serviceRangeDescTemplate.value = data.serviceRangeDesc;
			serviceRangeTag.value = data.serviceRangeTag;
			orderNotice.value = data.orderNotice;
			scienceTitle.value = data.scienceTitle;
			scienceDesc.value = data.scienceDesc;
			agreementText.value = data.agreementText;
			if (!weights.value.some((item) => item.title === weight.value)) {
				weight.value = weights.value[0]?.title || '';
			}
		} catch (error) {
			console.error('loadClothesBookingPageData failed', error);
		}
	};

	onLoad((options = {}) => {
		bookingScene.value = resolveBookingScene(options.scene);
	});

	const syncBookingScene = () => {
		const pages = getCurrentPages();
		const current = pages[pages.length - 1];
		const options = current?.$page?.options || current?.options || {};
		bookingScene.value = resolveBookingScene(options.scene || bookingScene.value);
	};

	onShow(() => {
		if (!ensureLoggedIn()) {
			return;
		}
		syncBookingScene();
		syncRegionState();
		syncAddresses();
		syncPageData();
		refreshAppointmentOptions();
	});

	const pickAddress = async (addr) => {
		selectedAddress.value = { id: addr.id, title: addr.name, desc: addr.address, user: addr.user };
		if (addr.region?.length) {
			syncRegionState(addr.region);
		}
		await syncServiceRangeCheck(addr);
		showAddress.value = false;
	};

	const editAddress = (addr) => {
		uni.navigateTo({ url: `/packageCommon/user/address-form?id=${addr.id}&mode=edit` });
	};

	const createAddress = () => {
		uni.navigateTo({ url: '/packageCommon/user/address-form?mode=create' });
	};

	const selectSlot = (slot) => {
		if (slot.disabled || slot.full) {
			return;
		}
		selectedSlot.value = slot.time;
	};

	const submitBooking = async () => {
		if (!selectedAddress.value.id) {
			uni.showToast({ title: '请先选择取件地址', icon: 'none' });
			return;
		}
		if (!selectedSlot.value) {
			uni.showToast({ title: '请先选择预约时间', icon: 'none' });
			return;
		}
		if (!weight.value) {
			uni.showToast({ title: '请先选择预估重量', icon: 'none' });
			return;
		}
		if (!agreed.value) {
			uni.showToast({ title: '请先确认回收规则', icon: 'none' });
			return;
		}
		const estimateAmount = Number(selectedWeightOption.value?.estimateAmount || 0);
		try {
			const order = await createClothesOrder({
				addressId: selectedAddress.value.id,
				dateLabel: selectedDate.value,
				dateKey: currentDateOption.value?.dateKey,
				slot: selectedSlot.value,
				weight: weight.value,
				estimateAmount,
				categoryName: bookingScene.value === 'book' ? '书籍回收' : '旧衣回收'
			});
			uni.navigateTo({ url: `/packageCommon/order/success?id=${order.id}` });
		} catch (error) {
			uni.showToast({ title: error.message || '下单失败', icon: 'none' });
		}
	};

	const go = (url) => uni.navigateTo({ url });
</script>

<style lang="scss">
	.clothes-page {
		background: linear-gradient(180deg, #fff9ef 0%, #fff 320rpx);
	}

	.clothes-body {
		padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
	}

	.estimate-preview-card {
		margin-top: 24rpx;
		padding: 24rpx;
		border-radius: 24rpx;
		background: #fff6f7;
		border: 2rpx solid #f6d9dc;
	}

	.estimate-preview-card__price {
		margin-top: 10rpx;
		font-size: 44rpx;
		font-weight: 800;
		color: #c8102e;
	}

	.clothes-footer {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 20rpx;
	}

	.clothes-footer__price {
		flex: 1;
		min-width: 0;
	}

	.clothes-footer__amount {
		margin-top: 6rpx;
		font-size: 40rpx;
		font-weight: 800;
		color: #c8102e;
	}

	.clothes-footer__btn {
		flex-shrink: 0;
		min-width: 280rpx;
	}

	.ghost-panel {
		padding: 24rpx;
		border-radius: 24rpx;
		background: #fff8f8;
		border: 2rpx solid #f3dfdf;
	}

	.service-range-card {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16rpx;
		padding: 24rpx;
		border-radius: 24rpx;
		background: linear-gradient(135deg, #fff5f6 0%, #fffaf1 100%);
	}

	.check-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
		font-size: 24rpx;
		color: #6f5b5b;
		line-height: 1.6;
	}

	.address-sheet {
		padding-top: 14rpx;
	}

	.address-sheet__head {
		display: grid;
		grid-template-columns: 48rpx 1fr 48rpx;
		align-items: center;
	}

	.address-sheet__title {
		text-align: center;
		margin-bottom: 0;
	}

	.address-sheet__placeholder {
		width: 48rpx;
		height: 48rpx;
	}

	.address-sheet__tabs {
		display: grid;
		grid-template-columns: repeat(3, minmax(0, 1fr));
		margin-top: 20rpx;
		border-bottom: 2rpx solid #f3dfdf;
	}

	.address-sheet__tab {
		height: 68rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		color: #6b5858;
		position: relative;
	}

	.address-sheet__tab.is-active {
		color: #cf1432;
		font-weight: 700;
	}

	.address-sheet__tab.is-active::after {
		content: '';
		position: absolute;
		left: 50%;
		bottom: 0;
		transform: translateX(-50%);
		width: 56rpx;
		height: 6rpx;
		border-radius: 999rpx;
		background: #cf1432;
	}

	.address-sheet__tab.is-warn {
		color: #cf1432;
	}

	.address-sheet__panel {
		max-height: 420rpx;
		margin-top: 22rpx;
	}

	.address-sheet__areas {
		display: grid;
		grid-template-columns: repeat(3, minmax(0, 1fr));
		gap: 16rpx;
	}

	.address-sheet__area {
		height: 72rpx;
		border-radius: 18rpx;
		background: #f7f3f3;
		color: #493939;
		font-size: 26rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.address-sheet__area.is-selected {
		background: linear-gradient(180deg, #e63a4b 0%, #cf1432 100%);
		color: #fff;
	}

	.address-sheet__section {
		margin-top: 24rpx;
	}

	.address-sheet__section-head {
		display: flex;
		align-items: center;
		gap: 10rpx;
		font-size: 26rpx;
		font-weight: 700;
	}

	.address-sheet__list {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
		margin-top: 18rpx;
	}

	.address-sheet__card {
		padding: 24rpx;
		border-radius: 24rpx;
		background: #fff8f8;
	}

	.address-sheet__card-top,
	.address-sheet__card-left,
	.address-sheet__card-name-row,
	.address-sheet__edit,
	.time-sheet__head,
	.time-sheet__slot-right {
		display: flex;
		align-items: center;
	}

	.address-sheet__card-top,
	.address-sheet__card-name-row {
		justify-content: space-between;
	}

	.address-sheet__card-left {
		gap: 16rpx;
		flex: 1;
		align-items: flex-start;
	}

	.address-sheet__card-icon {
		width: 64rpx;
		height: 64rpx;
		border-radius: 18rpx;
		background: #f1eaea;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.address-sheet__card-icon.is-default {
		background: #ffe8eb;
	}

	.address-sheet__card-name {
		font-size: 30rpx;
		font-weight: 700;
	}

	.address-sheet__card-tag {
		padding: 6rpx 12rpx;
		border-radius: 999rpx;
		background: #fff1f3;
		color: #cf1432;
		font-size: 20rpx;
	}

	.address-sheet__card-address,
	.address-sheet__card-user,
	.address-sheet__edit,
	.time-sheet__desc,
	.time-sheet__slot-state {
		font-size: 24rpx;
		color: #7c6868;
	}

	.address-sheet__card-user,
	.address-sheet__card-address {
		margin-top: 8rpx;
	}

	.address-sheet__edit {
		gap: 8rpx;
		color: #cf1432;
	}

	.address-sheet__add {
		height: 84rpx;
		margin-top: 16rpx;
		border-radius: 22rpx;
		background: #f7f3f3;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
		font-size: 26rpx;
	}

	.address-sheet__confirm,
	.time-sheet__confirm {
		margin-top: 28rpx;
	}

	.time-sheet__head {
		justify-content: space-between;
	}

	.time-sheet__dates {
		display: grid;
		grid-template-columns: repeat(5, minmax(0, 1fr));
		gap: 14rpx;
		margin-top: 22rpx;
	}

	.time-sheet__date {
		padding: 18rpx 8rpx;
		text-align: center;
		border-radius: 20rpx;
		background: #f7f3f3;
	}

	.time-sheet__date.is-active {
		background: #fff1f3;
		color: #cf1432;
	}

	.time-sheet__date-label {
		font-size: 24rpx;
		font-weight: 700;
	}

	.time-sheet__date-day {
		margin-top: 8rpx;
		font-size: 22rpx;
	}

	.time-sheet__slots {
		margin-top: 24rpx;
	}

	.time-sheet__group-title {
		font-size: 24rpx;
		font-weight: 700;
		color: #6d5858;
		margin-bottom: 12rpx;
	}

	.time-sheet__slot {
		padding: 24rpx;
		border-radius: 22rpx;
		background: #fff8f8;
		border: 2rpx solid transparent;
	}

	.time-sheet__slot + .time-sheet__slot {
		margin-top: 14rpx;
	}

	.time-sheet__slot.is-active {
		border-color: #cf1432;
		background: #fff4f6;
	}

	.time-sheet__slot.is-disabled {
		opacity: 0.72;
	}

	.time-sheet__slot-time {
		font-size: 28rpx;
		font-weight: 700;
	}

	.time-sheet__slot-right {
		gap: 12rpx;
	}

	.time-sheet__slot-state.is-full {
		color: #c6a13b;
	}

	.time-sheet__slot-state.is-free {
		color: #31a566;
	}

	.faq-item {
		padding: 26rpx 28rpx;
		border-radius: 26rpx;
		background: #fff;
		box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.07);
	}

	.faq-question {
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 30rpx;
		font-weight: 700;
	}

	.faq-answer {
		margin-top: 14rpx;
		font-size: 24rpx;
		line-height: 1.7;
		color: #7a6161;
	}
</style>
