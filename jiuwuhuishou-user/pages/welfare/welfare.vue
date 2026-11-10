<template>
	<view class="app-page">
		<top-nav title="福利中心" :showBack="false"></top-nav>
		<scroll-view scroll-y class="page-body compact">
			<view class="card hero-blue" style="overflow:hidden; position:relative;">
				<view style="position:absolute; right:-40rpx; top:-30rpx; width:180rpx; height:180rpx; border-radius:50%; background:rgba(255,255,255,.18);"></view>
				<view style="position:absolute; left:160rpx; bottom:-60rpx; width:240rpx; height:140rpx; border-radius:50%; background:rgba(255,255,255,.14);"></view>
				<view style="font-size: 26rpx; color: #5f4949;">我的积分</view>
				<view style="margin-top: 18rpx; color: #c8102e; font-size: 42rpx; font-weight: 800;">{{ coinBalanceText }}</view>
				<view class="muted" style="margin-top: 10rpx; font-size: 24rpx;">已连续签到 {{ summary.continuousSignDays }} 天</view>
				<view class="row-between" style="margin-top: 40rpx;">
					<view class="btn-ghost" style="max-width: 240rpx; height: 72rpx; background: rgba(255,255,255,0.56); font-size: 16px;" @click="routeTo('/packageCommon/user/coin-detail')">累计到账金额</view>
					<view class="withdraw-entry" style="background: linear-gradient(180deg, #ffd11f 0%, #ffbe0f 100%);" @click="openWithdrawPopup">提现</view>
				</view>
			</view>

			<view class="card" style="margin-top: 24rpx;">
				<view class="row-between">
					<view>
						<view style="font-size: 34rpx; font-weight: 700;">连续奖励</view>
						<view class="muted" style="margin-top: 8rpx;">已连续签到 {{ summary.continuousSignDays }} 天</view>
					</view>
					<u-switch
						v-model="continuousSignEnabled"
						activeColor="#c8102e"
						:loading="updatingContinuousSign"
						asyncChange
						@change="handleContinuousSignChange"
					></u-switch>
				</view>
				<view class="row-between" style="margin-top: 26rpx; gap: 12rpx;">
					<view v-for="day in signDays" :key="day.day" style="flex: 1; text-align: center;">
						<view :style="dayStyle(day)">{{ day.label }}</view>
						<view class="muted" style="margin-top: 10rpx; font-size: 22rpx;">{{ day.day }}</view>
					</view>
				</view>
				<view class="btn-gold" style="margin-top: 28rpx;" @click="handleSign">{{ summary.signedToday ? '今日已签到' : '立即签到领金币' }}</view>
			</view>

			<view class="section-title"><text>福利任务</text></view>
			<view class="feature-list">
				<view class="feature-row" v-for="task in tasks" :key="task.id || task.title">
					<view class="row-between" style="flex: 1; justify-content: flex-start;">
						<view class="dot-badge">
							<image
								v-if="task.iconIsImage"
								:src="task.icon"
								mode="aspectFit"
								class="task-icon-img"
							></image>
							<u-icon v-else :name="task.icon || 'gift'" size="22" color="#c8102e"></u-icon>
						</view>
						<view>
							<view style="font-size: 16px; font-weight: 700;">{{ task.title }}</view>
							<view class="muted" style="margin-top: 8rpx;">{{ task.desc }}</view>
						</view>
					</view>
					<view class="btn-main" style="flex: none; width: 168rpx; height: 72rpx; font-size: 28rpx;" @click.stop="task.action">{{ task.btnText }}</view>
				</view>
			</view>

			<view class="card" style="margin-top: 28rpx; padding: 0; overflow: hidden; border-radius: 36rpx;">
				<mock-art label="坚持回收，点石成金" desc="每一份小努力，都在让地球更美好" theme="mint"></mock-art>
			</view>
		</scroll-view>
		<floating-service></floating-service>

		<u-popup :show="showSignPopup" mode="center" :round="28" @close="showSignPopup = false">
			<view class="card" style="width: 620rpx; text-align: center; padding-top: 46rpx;">
				<view class="dot-badge" style="margin: 0 auto; width: 160rpx; height: 160rpx; background: linear-gradient(180deg, #ffd11f 0%, #ffbe0f 100%);">
					<u-icon name="coupon-fill" size="54" color="#8a6400"></u-icon>
				</view>
				<view style="margin-top: 30rpx; font-size: 44rpx; font-weight: 800;">签到成功</view>
				<view style="margin-top: 12rpx; font-size: 40rpx; color: #c8102e;">获得 +{{ signResult.coinReward }} 金币</view>
				<view class="ghost-panel" style="margin-top: 28rpx; text-align: left;">
					<view class="row-between"><text style="font-size: 32rpx; font-weight: 700;">连续签到进度</text><text class="brand-text" style="font-size: 36rpx; font-weight: 800;">{{ signResult.continuousSignDays }} / {{ signRule.totalDays }} 天</text></view>
					<u-line-progress :percentage="signProgress" activeColor="#c8102e" inactiveColor="#ece7e7" height="14" style="margin-top: 22rpx;"></u-line-progress>
					<view class="row-between" style="margin-top: 18rpx;"><text v-for="day in signDays.slice(0, 5)" :key="day.day" class="muted">{{ day.day }}</text></view>
				</view>
				<view class="muted" style="margin: 28rpx 0 34rpx;">{{ signRemainText }}</view>
				<view class="btn-gold" @click="showSignPopup = false">我知道了</view>
			</view>
		</u-popup>

		<u-popup :show="showWithdrawPopup" mode="center" :round="28" @close="closeWithdrawPopup">
			<view class="withdraw-popup">
				<view class="withdraw-popup__title">确定提现到钱包？</view>
				<view class="withdraw-popup__desc">最低提现：{{ withdrawMinAmount }} 金币</view>
				<view class="withdraw-popup__desc">兑换比例：1 积分 = {{ coinWalletRateText }} 元</view>
				<view v-if="withdrawPreviewText" class="withdraw-popup__desc">预计到账：{{ withdrawPreviewText }}</view>
				<input v-model="withdrawAmount" class="withdraw-popup__input" type="number" placeholder="请输入提现积分" placeholder-class="withdraw-popup__placeholder" />
				<view class="withdraw-popup__btn withdraw-popup__btn--main" @click="confirmWithdraw">{{ withdrawing ? '提交中...' : '确认' }}</view>
				<view class="withdraw-popup__btn withdraw-popup__btn--ghost" @click="closeWithdrawPopup">取消</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import {
		buildSignDayNodes,
		calcWalletCreditFromCoins,
		formatCoinBalance,
		loadWelfarePageData,
		refreshWelfareSummary,
		signWelfare,
		updateContinuousSignEnabled,
		withdrawWelfare
	} from '@/common/services/welfare.js';

	const continuousSignEnabled = ref(true);
	const showSignPopup = ref(false);
	const showWithdrawPopup = ref(false);
	const withdrawAmount = ref('');
	const loading = ref(false);
	const signing = ref(false);
	const withdrawing = ref(false);
	const updatingContinuousSign = ref(false);

	const summary = ref({
		coinBalance: 0,
		continuousSignDays: 0,
		continuousSignEnabled: true,
		signedToday: false
	});
	const signRule = ref({
		totalDays: 7,
		dailyCoin: 5
	});
	const signDays = ref([]);
	const tasks = ref([]);
	const withdrawMinAmount = ref(10);
	const coinWalletRate = ref(1);
	const signResult = ref({
		coinReward: 0,
		continuousSignDays: 0,
		coinBalance: 0
	});

	const coinBalanceText = computed(() => formatCoinBalance(summary.value.coinBalance));
	const coinWalletRateText = computed(() => {
		const rate = Number(coinWalletRate.value);
		if (Number.isNaN(rate)) {
			return '1';
		}
		return `${rate}`.replace(/\.0+$/, '').replace(/(\.\d*?)0+$/, '$1');
	});
	const withdrawPreviewText = computed(() => {
		const amount = Number(withdrawAmount.value);
		if (!withdrawAmount.value || Number.isNaN(amount) || amount <= 0) {
			return '';
		}
		const credit = calcWalletCreditFromCoins(amount, coinWalletRate.value);
		return `￥${credit.toFixed(2)}`;
	});
	const signProgress = computed(() => {
		const total = signRule.value.totalDays || 1;
		return Math.min(100, Math.round((signResult.value.continuousSignDays / total) * 100));
	});
	const signRemainText = computed(() => {
		const remain = Math.max(0, signRule.value.totalDays - signResult.value.continuousSignDays);
		return remain > 0 ? `再签到 ${remain} 天即可领取大礼包` : '已完成本轮连续签到';
	});

	const routeTo = (url) => uni.navigateTo({ url });

	const refreshSignDays = () => {
		signDays.value = buildSignDayNodes({
			continuousSignDays: summary.value.continuousSignDays,
			signedToday: summary.value.signedToday,
			totalDays: signRule.value.totalDays
		});
	};

	const loadPageData = async () => {
		loading.value = true;
		try {
			const data = await loadWelfarePageData();
			summary.value = data.summary;
			continuousSignEnabled.value = data.summary.continuousSignEnabled;
			signRule.value = data.signRule;
			signDays.value = data.signDays;
			tasks.value = data.tasks;
			withdrawMinAmount.value = data.withdrawMinAmount;
			coinWalletRate.value = data.coinWalletRate;
		} catch (error) {
			uni.showToast({
				title: error.message || '加载福利中心失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	const applyContinuousSignSummary = (nextSummary) => {
		summary.value = nextSummary;
		continuousSignEnabled.value = nextSummary.continuousSignEnabled;
		refreshSignDays();
	};

	const submitContinuousSignChange = async (enabled) => {
		if (updatingContinuousSign.value) {
			return;
		}
		updatingContinuousSign.value = true;
		try {
			const nextSummary = await updateContinuousSignEnabled(enabled);
			applyContinuousSignSummary(nextSummary);
			uni.showToast({
				title: enabled ? '已开启连续签到' : '已关闭连续签到',
				icon: 'none'
			});
		} catch (error) {
			uni.showToast({
				title: error.message || '设置失败',
				icon: 'none'
			});
		} finally {
			updatingContinuousSign.value = false;
		}
	};

	const handleContinuousSignChange = (enabled) => {
		if (updatingContinuousSign.value) {
			return;
		}
		if (enabled) {
			submitContinuousSignChange(true);
			return;
		}
		uni.showModal({
			title: '提示',
			content: '关闭后，将不再享受连续签到的福利！',
			confirmText: '确认',
			cancelText: '取消',
			success: (res) => {
				if (res.confirm) {
					submitContinuousSignChange(false);
				}
			}
		});
	};

	const handleSign = async () => {
		if (summary.value.signedToday || signing.value) {
			if (summary.value.signedToday) {
				uni.showToast({ title: '今日已签到', icon: 'none' });
			}
			return;
		}
		signing.value = true;
		try {
			const result = await signWelfare();
			signResult.value = result;
			summary.value = {
				...summary.value,
				coinBalance: result.coinBalance,
				continuousSignDays: result.continuousSignDays,
				signedToday: true
			};
			refreshSignDays();
			showSignPopup.value = true;
		} catch (error) {
			uni.showToast({
				title: error.message || '签到失败',
				icon: 'none'
			});
		} finally {
			signing.value = false;
		}
	};

	const openWithdrawPopup = () => {
		showWithdrawPopup.value = true;
	};

	const closeWithdrawPopup = () => {
		showWithdrawPopup.value = false;
		withdrawAmount.value = '';
	};

	const confirmWithdraw = async () => {
		if (withdrawing.value) {
			return;
		}
		const amount = Number(withdrawAmount.value);
		if (!withdrawAmount.value) {
			uni.showToast({
				title: '请输入提现金额',
				icon: 'none'
			});
			return;
		}
		if (Number.isNaN(amount) || amount < withdrawMinAmount.value) {
			uni.showToast({
				title: `提现金额不能低于${withdrawMinAmount.value}金币`,
				icon: 'none'
			});
			return;
		}
		if (!Number.isInteger(amount)) {
			uni.showToast({
				title: '提现金币必须为整数',
				icon: 'none'
			});
			return;
		}
		if (amount > summary.value.coinBalance) {
			uni.showToast({
				title: '金币余额不足',
				icon: 'none'
			});
			return;
		}
		withdrawing.value = true;
		try {
			await withdrawWelfare(amount);
			const refreshed = await refreshWelfareSummary();
			summary.value = refreshed.summary;
			signDays.value = refreshed.signDays;
			showWithdrawPopup.value = false;
			withdrawAmount.value = '';
			uni.showToast({
				title: '提现申请已提交',
				icon: 'success'
			});
		} catch (error) {
			uni.showToast({
				title: error.message || '提现失败',
				icon: 'none'
			});
		} finally {
			withdrawing.value = false;
		}
	};

	onShow(() => {
		if (!ensureLoggedIn()) {
			return;
		}
		loadPageData();
	});

	const dayStyle = (day) => {
		if (day.reward) return 'width: 72rpx; height: 72rpx; border-radius: 50%; margin: 0 auto; background: linear-gradient(180deg,#ffd11f 0%,#ffbe0f 100%); color: #8a6400; display:flex; align-items:center; justify-content:center; font-weight:800;';
		if (day.current) return 'width: 72rpx; height: 72rpx; border-radius: 50%; margin: 0 auto; background: #fff3cc; color: #8a6400; border: 2rpx dashed #8a6400; display:flex; align-items:center; justify-content:center; font-weight:800;';
		if (day.done) return 'width: 72rpx; height: 72rpx; border-radius: 50%; margin: 0 auto; background: #c8102e; color: #fff; display:flex; align-items:center; justify-content:center; font-weight:800;';
		return 'width: 72rpx; height: 72rpx; border-radius: 50%; margin: 0 auto; background: #f1eded; color: #b3a4a4; display:flex; align-items:center; justify-content:center; font-weight:800;';
	};
</script>

<style lang="scss">
	.task-icon-img {
		width: 44rpx;
		height: 44rpx;
		display: block;
	}

	.withdraw-entry {
		min-width: 132rpx;
		height: 72rpx;
		padding: 0 28rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		font-weight: 700;
		color: #8a6400;
		box-sizing: border-box;
		cursor: pointer;
	}

	.withdraw-popup {
		width: 620rpx;
		padding: 40rpx 32rpx 30rpx;
		background: #fff;
		border-radius: 28rpx;
		box-sizing: border-box;
	}

	.withdraw-popup__title {
		font-size: 36rpx;
		font-weight: 800;
		text-align: center;
		color: #2f2424;
	}

	.withdraw-popup__desc {
		margin-top: 16rpx;
		font-size: 26rpx;
		text-align: center;
		color: #7a6161;
	}

	.withdraw-popup__input {
		width: 100%;
		height: 96rpx;
		margin-top: 28rpx;
		padding: 0 24rpx;
		border-radius: 24rpx;
		background: #fff7f7;
		border: 2rpx solid #f1dddd;
		box-sizing: border-box;
		font-size: 30rpx;
		color: #2f2424;
	}

	.withdraw-popup__placeholder {
		color: #b8a8a8;
	}

	.withdraw-popup__btn {
		height: 88rpx;
		margin-top: 20rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 30rpx;
		font-weight: 700;
	}

	.withdraw-popup__btn--main {
		background: linear-gradient(135deg, #d01231 0%, #b80826 100%);
		color: #fff;
		box-shadow: 0 16rpx 36rpx rgba(200, 16, 46, 0.22);
	}

	.withdraw-popup__btn--ghost {
		background: #f7f2f2;
		color: #5c4a4a;
	}
</style>
