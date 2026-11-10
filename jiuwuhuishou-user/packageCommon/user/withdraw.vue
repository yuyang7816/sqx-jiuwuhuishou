<template>
	<view class="app-page withdraw-page">
		<top-nav title="提现"></top-nav>
		<scroll-view scroll-y class="page-body compact withdraw-body">
			<view class="withdraw-hero">
				<view class="withdraw-hero__label">钱包余额（元）</view>
				<view class="withdraw-hero__amount">{{ pageData.availableBalanceText }}</view>
				<view class="withdraw-hero__desc">{{ pageData.heroDesc }}</view>
			</view>

			<view class="withdraw-card">
				<view class="withdraw-card__title">提现方式</view>
				<view class="withdraw-type-list">
					<view class="withdraw-type-item" :class="{ 'is-active': withdrawType === 'wechat' }" @click="withdrawType = 'wechat'">
						<view class="withdraw-type-item__icon is-wechat">
							<u-icon name="weixin-fill" size="22" color="#00b96b"></u-icon>
						</view>
						<view class="withdraw-type-item__info">
							<view class="withdraw-type-item__title">微信提现</view>
							<view class="withdraw-type-item__desc">到账快，需上传微信收款码</view>
						</view>
						<u-icon :name="withdrawType === 'wechat' ? 'checkmark-circle-fill' : 'checkmark-circle'" size="20" :color="withdrawType === 'wechat' ? '#cf1432' : '#c8b8b8'"></u-icon>
					</view>
					<view class="withdraw-type-item" :class="{ 'is-active': withdrawType === 'alipay' }" @click="withdrawType = 'alipay'">
						<view class="withdraw-type-item__icon is-alipay">
							<u-icon name="rmb-circle" size="22" color="#1677ff"></u-icon>
						</view>
						<view class="withdraw-type-item__info">
							<view class="withdraw-type-item__title">支付宝提现</view>
							<view class="withdraw-type-item__desc">支持支付宝账号或绑定手机号收款</view>
						</view>
						<u-icon :name="withdrawType === 'alipay' ? 'checkmark-circle-fill' : 'checkmark-circle'" size="20" :color="withdrawType === 'alipay' ? '#cf1432' : '#c8b8b8'"></u-icon>
					</view>
				</view>
			</view>

			<view class="withdraw-card" style="margin-top: 22rpx;">
				<view class="withdraw-card__title">提现金额</view>
				<view class="withdraw-amount-row">
					<text class="withdraw-amount-row__symbol">￥</text>
					<input v-model="form.amount" class="withdraw-amount-row__input" type="number" placeholder="请输入提现金额" placeholder-class="withdraw-form__placeholder" />
				</view>
				<view class="withdraw-quick-actions">
					<view
						v-for="item in quickAmountOptions"
						:key="item.key"
						class="withdraw-quick-actions__item"
						@click="fillAmount(item)"
					>{{ item.label }}</view>
				</view>
				<view class="withdraw-card__tips">{{ pageData.tipsPrefix }}{{ withdrawType === 'wechat' ? '微信零钱' : '支付宝' }}{{ pageData.tipsSuffix }}</view>
				<view class="withdraw-submit" @click="submitWithdraw">{{ submitting ? '提交中...' : '确认提现' }}</view>
				<view class="withdraw-mini-actions">
					<view class="withdraw-mini-actions__item" @click="showAccountPopup = true">提现账号</view>
					<view class="withdraw-mini-actions__item" @click="showQrPopup = true">微信收款码</view>
					<view class="withdraw-mini-actions__item" @click="goRecords">提现记录</view>
				</view>
			</view>
		</scroll-view>

		<u-popup :show="showAccountPopup" mode="center" :round="28" @close="showAccountPopup = false">
			<view class="withdraw-popup-card">
				<view class="withdraw-popup-card__title">提现账号</view>
				<view class="withdraw-form__item">
					<view class="withdraw-form__label">支付宝名称</view>
					<input v-model="alipayAccount.accountName" class="withdraw-form__input" type="text" maxlength="20" placeholder="请输入支付宝名称" placeholder-class="withdraw-form__placeholder" />
				</view>
				<view class="withdraw-form__item withdraw-form__item--last">
					<view class="withdraw-form__label">支付宝账号</view>
					<input v-model="alipayAccount.accountNumber" class="withdraw-form__input" type="text" maxlength="30" placeholder="请输入支付宝账号" placeholder-class="withdraw-form__placeholder" />
				</view>
				<view class="withdraw-popup-card__actions">
					<view class="withdraw-popup-card__btn withdraw-popup-card__btn--ghost" @click="showAccountPopup = false">取消</view>
					<view class="withdraw-popup-card__btn withdraw-popup-card__btn--main" @click="saveAlipayAccount">{{ savingAccount ? '保存中...' : '保存' }}</view>
				</view>
			</view>
		</u-popup>

		<u-popup :show="showQrPopup" mode="center" :round="28" @close="showQrPopup = false">
			<view class="withdraw-popup-card withdraw-popup-card--qr">
				<view class="withdraw-popup-card__title">微信收款码</view>
				<view class="withdraw-qr-row">
					<view class="withdraw-qr-box" @click="pickQrImage">
						<image v-if="wechatAccount.qrImage" :src="wechatAccount.qrImage" mode="aspectFill" class="withdraw-qr-box__img"></image>
						<view v-else class="withdraw-qr-box__empty">
							<u-icon name="plus" size="24" color="#c6a48a"></u-icon>
							<text>上传收款码</text>
						</view>
					</view>
					<view class="withdraw-qr-info">
						<view class="withdraw-qr-info__title">用于微信提现审核</view>
						<view class="withdraw-qr-info__desc">请上传清晰可识别的微信收款二维码，便于财务核验。</view>
						<view class="withdraw-qr-info__btn" @click="pickQrImage">{{ wechatAccount.qrImage ? '重新上传' : '立即上传' }}</view>
					</view>
				</view>
				<view class="withdraw-popup-card__actions">
					<view class="withdraw-popup-card__btn withdraw-popup-card__btn--ghost" @click="showQrPopup = false">取消</view>
					<view class="withdraw-popup-card__btn withdraw-popup-card__btn--main" @click="saveWechatQr">{{ savingQr ? '保存中...' : '保存' }}</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { chooseAndUploadImage } from '@/common/services/upload.js';
	import { DEFAULT_ALIPAY_ACCOUNT, DEFAULT_WECHAT_ACCOUNT, buildWithdrawQuickOptions } from '@/common/adapters/withdraw.js';
	import {
		DEFAULT_PAGE_DATA,
		loadWithdrawAccounts,
		loadWithdrawPageData,
		saveAlipayWithdrawAccount,
		saveWechatWithdrawAccount,
		submitWithdrawRequest
	} from '@/common/services/withdraw.js';

	const form = ref({
		amount: ''
	});
	const withdrawType = ref('wechat');
	const showAccountPopup = ref(false);
	const showQrPopup = ref(false);
	const loading = ref(false);
	const savingAccount = ref(false);
	const savingQr = ref(false);
	const submitting = ref(false);
	const pageData = ref(DEFAULT_PAGE_DATA);
	const alipayAccount = ref({ ...DEFAULT_ALIPAY_ACCOUNT });
	const wechatAccount = ref({ ...DEFAULT_WECHAT_ACCOUNT });

	const quickAmountOptions = computed(() => buildWithdrawQuickOptions(
		pageData.value.quickAmounts,
		pageData.value.availableBalance
	));

	const loadPageData = async () => {
		if (!ensureLoggedIn()) {
			pageData.value = DEFAULT_PAGE_DATA;
			alipayAccount.value = { ...DEFAULT_ALIPAY_ACCOUNT };
			wechatAccount.value = { ...DEFAULT_WECHAT_ACCOUNT };
			return;
		}
		loading.value = true;
		try {
			const [withdrawData, accountData] = await Promise.all([
				loadWithdrawPageData(),
				loadWithdrawAccounts()
			]);
			pageData.value = withdrawData;
			alipayAccount.value = accountData.alipayAccount;
			wechatAccount.value = accountData.wechatAccount;
		} catch (error) {
			pageData.value = DEFAULT_PAGE_DATA;
			alipayAccount.value = { ...DEFAULT_ALIPAY_ACCOUNT };
			wechatAccount.value = { ...DEFAULT_WECHAT_ACCOUNT };
			uni.showToast({
				title: error.message || '加载提现信息失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	const fillAmount = (item) => {
		form.value.amount = item.value;
	};

	const goRecords = () => {
		uni.navigateTo({ url: '/packageCommon/user/withdraw-records' });
	};

	const pickQrImage = async () => {
		if (savingQr.value) {
			return;
		}
		try {
			const url = await chooseAndUploadImage();
			wechatAccount.value.qrImage = url;
		} catch (error) {
			if (error?.errMsg && error.errMsg.includes('cancel')) {
				return;
			}
			uni.showToast({
				title: error.message || '上传收款码失败',
				icon: 'none'
			});
		}
	};

	const saveAlipayAccount = async () => {
		if (savingAccount.value) {
			return;
		}
		savingAccount.value = true;
		try {
			alipayAccount.value = await saveAlipayWithdrawAccount(alipayAccount.value);
			showAccountPopup.value = false;
			uni.showToast({ title: '保存成功', icon: 'success' });
		} catch (error) {
			uni.showToast({
				title: error.message || '保存失败',
				icon: 'none'
			});
		} finally {
			savingAccount.value = false;
		}
	};

	const saveWechatQr = async () => {
		if (savingQr.value) {
			return;
		}
		savingQr.value = true;
		try {
			wechatAccount.value = await saveWechatWithdrawAccount(wechatAccount.value);
			showQrPopup.value = false;
			uni.showToast({ title: '保存成功', icon: 'success' });
		} catch (error) {
			uni.showToast({
				title: error.message || '保存失败',
				icon: 'none'
			});
		} finally {
			savingQr.value = false;
		}
	};

	const submitWithdraw = async () => {
		if (submitting.value) {
			return;
		}
		const amount = Number(form.value.amount);
		if (withdrawType.value === 'alipay') {
			if (!alipayAccount.value.accountName.trim()) {
				uni.showToast({ title: '请先设置支付宝名称', icon: 'none' });
				return;
			}
			if (!alipayAccount.value.accountNumber.trim()) {
				uni.showToast({ title: '请先设置支付宝账号', icon: 'none' });
				return;
			}
		}
		if (withdrawType.value === 'wechat' && !wechatAccount.value.qrImage) {
			uni.showToast({ title: '请先上传微信收款码', icon: 'none' });
			return;
		}
		if (!form.value.amount) {
			uni.showToast({ title: '请输入提现金额', icon: 'none' });
			return;
		}
		if (Number.isNaN(amount) || amount < pageData.value.minAmount) {
			uni.showToast({ title: `提现金额不能低于${pageData.value.minAmount}`, icon: 'none' });
			return;
		}
		if (amount > pageData.value.availableBalance) {
			uni.showToast({ title: '提现金额不能超过钱包余额', icon: 'none' });
			return;
		}

		submitting.value = true;
		try {
			await submitWithdrawRequest({
				amount,
				withdrawType: withdrawType.value
			});
			uni.showToast({ title: '提现申请已提交', icon: 'success' });
			setTimeout(() => {
				uni.navigateBack();
			}, 500);
		} catch (error) {
			uni.showToast({
				title: error?.message || '提现申请失败',
				icon: 'none'
			});
		} finally {
			submitting.value = false;
		}
	};

	onShow(() => {
		loadPageData();
	});
</script>

<style lang="scss">
	.withdraw-page { background: linear-gradient(180deg, #fff8f5 0%, #fff 340rpx); }
	.withdraw-body { padding-top: 14rpx; padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
	.withdraw-hero,
	.withdraw-card { background: #fff; border-radius: 28rpx; box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06); }
	.withdraw-hero { padding: 28rpx; background: linear-gradient(135deg, #ffe39f 0%, #ffc71a 100%); box-shadow: 0 20rpx 42rpx rgba(255, 191, 31, 0.22); }
	.withdraw-hero__label { font-size: 24rpx; color: #6c4f00; }
	.withdraw-hero__amount { margin-top: 16rpx; font-size: 62rpx; font-weight: 800; color: #352600; line-height: 1; }
	.withdraw-hero__desc { margin-top: 16rpx; font-size: 24rpx; line-height: 1.7; color: rgba(80, 58, 0, 0.8); }
	.withdraw-card { margin-top: 22rpx; padding: 26rpx 24rpx; }
	.withdraw-card__title { font-size: 32rpx; font-weight: 700; color: #302525; }
	.withdraw-card__tips { margin-top: 18rpx; font-size: 24rpx; line-height: 1.7; color: #8a7676; }
	.withdraw-type-list { display: flex; flex-direction: column; gap: 16rpx; margin-top: 20rpx; }
	.withdraw-type-item { display: flex; align-items: center; gap: 16rpx; padding: 22rpx 20rpx; border-radius: 24rpx; background: #fff8f8; border: 2rpx solid transparent; }
	.withdraw-type-item.is-active { background: #fff1f3; border-color: #cf1432; }
	.withdraw-type-item__icon { width: 64rpx; height: 64rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
	.withdraw-type-item__icon.is-wechat { background: #e4f8ee; }
	.withdraw-type-item__icon.is-alipay { background: #e9f2ff; }
	.withdraw-type-item__info { flex: 1; }
	.withdraw-type-item__title { font-size: 30rpx; font-weight: 700; color: #302525; }
	.withdraw-type-item__desc { margin-top: 8rpx; font-size: 22rpx; color: #8a7777; }
	.withdraw-form__item { display: flex; align-items: center; gap: 18rpx; min-height: 96rpx; border-bottom: 2rpx solid #f3eaea; }
	.withdraw-form__item--last { border-bottom: 0; }
	.withdraw-form__label { width: 160rpx; font-size: 28rpx; font-weight: 700; color: #322626; flex-shrink: 0; }
	.withdraw-form__input { flex: 1; height: 44rpx; font-size: 28rpx; line-height: 44rpx; color: #4e3d3d; text-align: right; background: transparent; padding: 0; margin: 0; }
	.withdraw-form__placeholder { color: #c0aeae; font-size: 28rpx; }
	.withdraw-qr-row { display: flex; gap: 18rpx; margin-top: 22rpx; align-items: stretch; }
	.withdraw-qr-box { width: 210rpx; height: 210rpx; border-radius: 24rpx; background: #fff8f8; border: 2rpx dashed #edd7d7; overflow: hidden; flex-shrink: 0; }
	.withdraw-qr-box__img { width: 100%; height: 100%; }
	.withdraw-qr-box__empty { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12rpx; font-size: 24rpx; color: #c6a48a; }
	.withdraw-qr-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
	.withdraw-qr-info__title { font-size: 28rpx; font-weight: 700; color: #312525; }
	.withdraw-qr-info__desc { margin-top: 12rpx; font-size: 24rpx; line-height: 1.7; color: #8a7777; }
	.withdraw-qr-info__btn { margin-top: 18rpx; width: 180rpx; height: 60rpx; border-radius: 999rpx; background: #fff1f3; color: #cf1432; font-size: 24rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; }
	.withdraw-amount-row { display: flex; align-items: center; gap: 14rpx; margin-top: 20rpx; padding-bottom: 18rpx; border-bottom: 2rpx solid #f3eaea; }
	.withdraw-amount-row__symbol { font-size: 42rpx; font-weight: 800; color: #2f2424; }
	.withdraw-amount-row__input { flex: 1; height: 52rpx; font-size: 38rpx; line-height: 52rpx; color: #2f2424; background: transparent; padding: 0; margin: 0; }
	.withdraw-quick-actions { display: flex; flex-wrap: wrap; gap: 14rpx; margin-top: 20rpx; }
	.withdraw-quick-actions__item { min-width: 126rpx; height: 52rpx; padding: 0 18rpx; border-radius: 999rpx; background: #f4efef; color: #6d5c5c; font-size: 24rpx; display: flex; align-items: center; justify-content: center; box-sizing: border-box; }
	.withdraw-submit { height: 92rpx; margin-top: 28rpx; border-radius: 999rpx; background: linear-gradient(135deg, #d01231 0%, #b80826 100%); color: #fff; font-size: 32rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; box-shadow: 0 16rpx 36rpx rgba(200,16,46,0.25); }
	.withdraw-mini-actions { display: flex; gap: 16rpx; margin-top: 18rpx; }
	.withdraw-mini-actions__item { flex: 1; height: 60rpx; border-radius: 999rpx; background: #fff1f3; color: #cf1432; font-size: 24rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; }
	.withdraw-popup-card { width: 650rpx; padding: 34rpx 28rpx 28rpx; background: #fff; border-radius: 28rpx; box-sizing: border-box; }
	.withdraw-popup-card--qr { width: 680rpx; }
	.withdraw-popup-card__title { font-size: 36rpx; font-weight: 800; text-align: center; color: #2f2424; }
	.withdraw-popup-card__actions { display: flex; gap: 16rpx; margin-top: 28rpx; }
	.withdraw-popup-card__btn { flex: 1; height: 80rpx; border-radius: 999rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 700; }
	.withdraw-popup-card__btn--ghost { background: #f6f0f0; color: #6c5b5b; }
	.withdraw-popup-card__btn--main { background: linear-gradient(135deg, #d01231 0%, #b80826 100%); color: #fff; }
</style>
