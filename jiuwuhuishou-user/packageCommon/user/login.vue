<template>
	<view class="app-page auth-page">
		<top-nav title="手机号登录" title-color="#1f1f1f" back-url="/pages/index/index"></top-nav>
		<scroll-view scroll-y class="page-body compact auth-body">
			<view class="auth-brand-card auth-brand-card--plain">
				<view class="auth-brand-badge">旧物回收</view>
				<view class="auth-title">欢迎回来</view>
				<view class="auth-desc">登录后可查看订单、钱包和推荐收益</view>
			</view>

			<view class="auth-panel">
				<view v-if="invitationCode" class="auth-invite-tip">
					已识别邀请码：{{ invitationCode }}
				</view>

				<view class="auth-field">
					<view class="auth-field__label">手机号</view>
					<input v-model="form.mobile" class="auth-field__input" type="number" maxlength="11" placeholder="请输入手机号" placeholder-class="auth-field__placeholder" />
				</view>

				<view class="auth-field">
					<view class="auth-field__label">密码</view>
					<view class="auth-field__input-row">
						<input v-model="form.password" class="auth-field__input auth-field__input--password" :password="passwordType" maxlength="20" placeholder="请输入密码" placeholder-class="auth-field__placeholder" />
						<u-icon :name="passwordType ? 'eye-off' : 'eye'" size="18" color="#6f6a6a" @click="passwordType = !passwordType"></u-icon>
					</view>
				</view>

				<view class="auth-submit" :class="{ 'is-disabled': !agreed }" @click="submitLogin">登录</view>

				<view class="auth-agree" @click="agreed = !agreed">
					<u-icon :name="agreed ? 'checkmark-circle-fill' : 'checkmark-circle'" size="16" :color="agreed ? '#c8102e' : '#c7b6b6'"></u-icon>
					<text class="auth-agree__text">
						我已阅读并同意
						<text class="auth-agree__link" @click.stop="go('/packageCommon/user/agreement?type=service')">用户协议</text>
						和
						<text class="auth-agree__link" @click.stop="go('/packageCommon/user/agreement?type=privacy')">隐私协议</text>
					</text>
				</view>

				<!-- #ifdef MP-WEIXIN -->
				<view class="auth-divider">
					<view class="auth-divider__line"></view>
					<text class="auth-divider__text">或</text>
					<view class="auth-divider__line"></view>
				</view>

				<view
					v-if="!needPhoneBind"
					class="auth-wx-login"
					:class="{ 'is-loading': wxLoading, 'is-disabled': !agreed }"
					@click="submitWeixinLogin"
				>
					<u-icon name="weixin-fill" size="20" color="#fff"></u-icon>
					<text>{{ wxLoading ? '登录中...' : '微信快捷登录' }}</text>
				</view>

				<button
					v-else
					class="auth-wx-login auth-wx-login--phone"
					:class="{ 'is-disabled': !agreed }"
					open-type="getPhoneNumber"
					:loading="wxLoading"
					:disabled="wxLoading || !agreed"
					@getphonenumber="bindWeixinPhone"
				>
					授权手机号完成登录
				</button>
				<!-- #endif -->

				<view class="auth-links">
					<text @click="go('/packageCommon/user/forgot-password')">忘记密码?</text>
					<text @click="go('/packageCommon/user/register')">还没有账号？去注册</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import { login, weixinBindPhoneLogin, weixinQuickLogin } from '@/common/services/auth.js';
	import { captureInvitationCode, getInvitationCode } from '@/common/services/inviteCode.js';

	const passwordType = ref(true);
	const agreed = ref(false);
	const wxLoading = ref(false);
	const needPhoneBind = ref(false);
	const pendingOpenId = ref('');
	const invitationCode = ref('');
	const form = ref({
		mobile: '',
		password: ''
	});

	const go = (url) => uni.navigateTo({ url });

	const ensureAgreed = () => {
		if (agreed.value) {
			return true;
		}
		uni.showToast({ title: '请先阅读并同意用户协议和隐私协议', icon: 'none' });
		return false;
	};

	const redirectAfterLogin = () => {
		setTimeout(() => {
			uni.switchTab({ url: '/pages/index/index' });
		}, 500);
	};

	onLoad((options) => {
		captureInvitationCode(options || {});
		invitationCode.value = getInvitationCode();
	});

	const submitLogin = async () => {
		if (!ensureAgreed()) {
			return;
		}
		if (!/^1\d{10}$/.test(form.value.mobile)) {
			uni.showToast({ title: '请输入正确手机号', icon: 'none' });
			return;
		}
		if (!form.value.password) {
			uni.showToast({ title: '请输入密码', icon: 'none' });
			return;
		}
		try {
			await login(form.value.mobile, form.value.password);
			uni.showToast({ title: '登录成功', icon: 'success' });
			redirectAfterLogin();
		} catch (error) {
			uni.showToast({ title: error.message || '登录失败', icon: 'none' });
		}
	};

	const submitWeixinLogin = async () => {
		if (!ensureAgreed() || wxLoading.value) {
			return;
		}
		wxLoading.value = true;
		try {
			const result = await weixinQuickLogin();
			if (result?.needPhoneBind) {
				pendingOpenId.value = result.openid || '';
				needPhoneBind.value = true;
				uni.showToast({ title: '请授权手机号完成登录', icon: 'none' });
				return;
			}
			uni.showToast({ title: '登录成功', icon: 'success' });
			redirectAfterLogin();
		} catch (error) {
			uni.showToast({ title: error.message || '微信登录失败', icon: 'none' });
		} finally {
			wxLoading.value = false;
		}
	};

	const bindWeixinPhone = async (event) => {
		if (!ensureAgreed() || wxLoading.value) {
			return;
		}
		if (event?.detail?.errMsg !== 'getPhoneNumber:ok') {
			uni.showToast({ title: '需要授权手机号才能登录', icon: 'none' });
			return;
		}
		wxLoading.value = true;
		try {
			await weixinBindPhoneLogin({
				openid: pendingOpenId.value,
				phoneCode: event.detail.code
			});
			uni.showToast({ title: '登录成功', icon: 'success' });
			redirectAfterLogin();
		} catch (error) {
			uni.showToast({ title: error.message || '微信登录失败', icon: 'none' });
		} finally {
			wxLoading.value = false;
		}
	};
</script>

<style lang="scss">
	.auth-page {
		background: linear-gradient(180deg, #fff8ef 0%, #fffef9 240rpx, #fff 100%);
	}

	.auth-body {
		padding-top: 12rpx;
		padding-bottom: calc(80rpx + env(safe-area-inset-bottom));
	}

	.auth-brand-card {
		text-align: center;
		padding: 28rpx 20rpx 18rpx;
	}

	.auth-brand-card--plain {
		padding-top: 36rpx;
	}

	.auth-brand-badge {
		width: 108rpx;
		height: 108rpx;
		margin: 0 auto;
		background: #c8102e;
		border-radius: 50%;
		color: #fff;
		font-size: 24rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 16rpx 30rpx rgba(200, 16, 46, 0.18);
	}

	.auth-title {
		margin-top: 34rpx;
		font-size: 56rpx;
		font-weight: 800;
		color: #5b4030;
	}

	.auth-desc {
		margin-top: 16rpx;
		font-size: 28rpx;
		line-height: 1.7;
		color: #9e8472;
	}

	.auth-panel {
		margin-top: 22rpx;
		background: #fff;
		border-radius: 34rpx;
		padding: 22rpx 22rpx 26rpx;
		box-shadow: 0 16rpx 36rpx rgba(111, 79, 79, 0.07);
	}

	.auth-invite-tip {
		margin-bottom: 20rpx;
		padding: 18rpx 20rpx;
		border-radius: 18rpx;
		background: #fff6f7;
		color: #c8102e;
		font-size: 24rpx;
		line-height: 1.5;
	}

	.auth-field {
		padding: 18rpx 16rpx;
		border: 2rpx solid #f1dfcb;
		border-radius: 24rpx;
		background: #fffdfb;
	}

	.auth-field + .auth-field {
		margin-top: 20rpx;
	}

	.auth-field__label {
		font-size: 24rpx;
		color: #9a816f;
	}

	.auth-field__input-row {
		display: flex;
		align-items: center;
		gap: 14rpx;
	}

	.auth-field__input {
		width: 100%;
		height: 62rpx;
		margin-top: 10rpx;
		padding: 0;
		background: transparent;
		font-size: 34rpx;
		color: #2d2d2d;
		box-sizing: border-box;
	}

	.auth-field__input--password {
		flex: 1;
	}

	.auth-field__placeholder {
		color: #b6bcc7;
		font-size: 30rpx;
	}

	.auth-submit {
		height: 92rpx;
		margin-top: 24rpx;
		border-radius: 999rpx;
		background: linear-gradient(135deg, #ddb16c 0%, #d5a562 100%);
		color: #fff;
		font-size: 34rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.auth-submit.is-disabled,
	.auth-wx-login.is-disabled {
		opacity: 0.45;
	}

	.auth-agree {
		margin-top: 22rpx;
		display: flex;
		align-items: flex-start;
		gap: 10rpx;
	}

	.auth-agree__text {
		flex: 1;
		font-size: 24rpx;
		line-height: 1.6;
		color: #9a8888;
	}

	.auth-agree__link {
		color: #c8102e;
	}

	.auth-divider {
		display: flex;
		align-items: center;
		gap: 18rpx;
		margin: 28rpx 0 22rpx;
	}

	.auth-divider__line {
		flex: 1;
		height: 2rpx;
		background: #f1dfcb;
	}

	.auth-divider__text {
		font-size: 24rpx;
		color: #b6a08d;
	}

	.auth-wx-login {
		height: 92rpx;
		border-radius: 999rpx;
		background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
		color: #fff;
		font-size: 32rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 12rpx;
	}

	.auth-wx-login.is-loading {
		opacity: 0.72;
	}

	.auth-wx-login--phone {
		width: 100%;
		padding: 0;
		margin: 0;
		border: none;
		line-height: 92rpx;
	}

	.auth-wx-login--phone::after {
		border: none;
	}

	.auth-links {
		margin-top: 24rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 28rpx;
		color: #c9a067;
	}
</style>
