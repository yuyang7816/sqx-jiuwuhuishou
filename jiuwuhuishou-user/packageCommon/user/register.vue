<template>
	<view class="app-page auth-page">
		<top-nav title="手机号注册" title-color="#1f1f1f"></top-nav>
		<scroll-view scroll-y class="page-body compact auth-body">
			<view class="auth-brand-card auth-brand-card--plain">
				<view class="auth-brand-badge">旧物回收</view>
				<view class="auth-title">创建新账号</view>
				<view class="auth-desc">使用手机号即可完成注册，马上开始体验回收服务</view>
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
					<view class="auth-field__label">验证码</view>
					<view class="auth-field__verify-row">
						<input v-model="form.code" class="auth-field__input auth-field__input--verify" type="number" maxlength="6" placeholder="请输入验证码" placeholder-class="auth-field__placeholder" />
						<view class="auth-verify-btn" @click="getCode">{{ codeText }}</view>
					</view>
				</view>

				<view class="auth-field">
					<view class="auth-field__label">密码</view>
					<view class="auth-field__input-row">
						<input v-model="form.password" class="auth-field__input auth-field__input--password" :password="passwordType" maxlength="20" placeholder="请输入密码" placeholder-class="auth-field__placeholder" />
						<u-icon :name="passwordType ? 'eye-off' : 'eye'" size="18" color="#6f6a6a" @click="passwordType = !passwordType"></u-icon>
					</view>
				</view>

				<view class="auth-field auth-field--danger">
					<view class="auth-field__label">确认密码</view>
					<view class="auth-field__input-row">
						<input v-model="form.confirmPassword" class="auth-field__input auth-field__input--password" :password="confirmPasswordType" maxlength="20" placeholder="请再次输入密码" placeholder-class="auth-field__placeholder" />
						<u-icon :name="confirmPasswordType ? 'eye-off' : 'eye'" size="18" color="#6f6a6a" @click="confirmPasswordType = !confirmPasswordType"></u-icon>
					</view>
				</view>

				<view class="auth-submit" :class="{ 'is-disabled': !agreed }" @click="submitRegister">注册并登录</view>

				<view class="auth-agree" @click="agreed = !agreed">
					<u-icon :name="agreed ? 'checkmark-circle-fill' : 'checkmark-circle'" size="16" :color="agreed ? '#c8102e' : '#c7b6b6'"></u-icon>
					<text class="auth-agree__text">
						我已阅读并同意
						<text class="auth-agree__link" @click.stop="go('/packageCommon/user/agreement?type=service')">用户协议</text>
						和
						<text class="auth-agree__link" @click.stop="go('/packageCommon/user/agreement?type=privacy')">隐私协议</text>
					</text>
				</view>

				<view class="auth-links">
					<text @click="go('/packageCommon/user/forgot-password')">忘记密码?</text>
					<text @click="goLogin">已有账号？去登录</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import { register, sendVerifyCode } from '@/common/services/auth.js';
	import { captureInvitationCode, getInvitationCode } from '@/common/services/inviteCode.js';

	const passwordType = ref(true);
	const confirmPasswordType = ref(true);
	const agreed = ref(false);
	const codeText = ref('获取验证码');
	const sendingCode = ref(false);
	const invitationCode = ref('');
	const form = ref({
		mobile: '',
		code: '',
		password: '',
		confirmPassword: ''
	});

	const go = (url) => uni.navigateTo({ url });
	const goLogin = () => uni.redirectTo({ url: '/packageCommon/user/login' });

	const ensureAgreed = () => {
		if (agreed.value) {
			return true;
		}
		uni.showToast({ title: '请先阅读并同意用户协议和隐私协议', icon: 'none' });
		return false;
	};

	onLoad((options) => {
		captureInvitationCode(options || {});
		invitationCode.value = getInvitationCode();
	});

	const getCode = async () => {
		if (sendingCode.value) {
			return;
		}
		if (!/^1\d{10}$/.test(form.value.mobile)) {
			uni.showToast({ title: '请输入正确手机号', icon: 'none' });
			return;
		}
		try {
			sendingCode.value = true;
			await sendVerifyCode(form.value.mobile, 'register');
			uni.showToast({ title: '验证码已发送', icon: 'success' });
			codeText.value = '60s后重试';
		} catch (error) {
			uni.showToast({ title: error.message || '发送失败', icon: 'none' });
		} finally {
			sendingCode.value = false;
		}
	};

	const submitRegister = async () => {
		if (!ensureAgreed()) {
			return;
		}
		if (!/^1\d{10}$/.test(form.value.mobile)) {
			uni.showToast({ title: '请输入正确手机号', icon: 'none' });
			return;
		}
		if (!form.value.code) {
			uni.showToast({ title: '请输入验证码', icon: 'none' });
			return;
		}
		if (!form.value.password) {
			uni.showToast({ title: '请输入密码', icon: 'none' });
			return;
		}
		if (form.value.password !== form.value.confirmPassword) {
			uni.showToast({ title: '两次输入密码不一致', icon: 'none' });
			return;
		}
		try {
			await register({
				mobile: form.value.mobile,
				code: form.value.code,
				password: form.value.password,
				superiorInvitationCode: invitationCode.value
			});
			uni.showToast({ title: '注册成功', icon: 'success' });
			setTimeout(() => {
				uni.switchTab({ url: '/pages/index/index' });
			}, 500);
		} catch (error) {
			uni.showToast({ title: error.message || '注册失败', icon: 'none' });
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
		border-radius: 20rpx;
		background: #fff7ed;
		color: #b45309;
		font-size: 24rpx;
		line-height: 1.6;
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

	.auth-field--danger {
		border-color: #ff7171;
		box-shadow: inset 0 0 0 2rpx rgba(255, 113, 113, 0.06);
	}

	.auth-field__label {
		font-size: 24rpx;
		color: #9a816f;
	}

	.auth-field__input-row,
	.auth-field__verify-row {
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

	.auth-field__input--password,
	.auth-field__input--verify {
		flex: 1;
	}

	.auth-field__placeholder {
		color: #b6bcc7;
		font-size: 30rpx;
	}

	.auth-verify-btn {
		margin-top: 10rpx;
		min-width: 152rpx;
		height: 62rpx;
		padding: 0 18rpx;
		border-radius: 999rpx;
		border: 2rpx solid #d8ab67;
		background: #fff;
		color: #d3a15d;
		font-size: 24rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		box-sizing: border-box;
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

	.auth-submit.is-disabled {
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

	.auth-links {
		margin-top: 24rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 28rpx;
		color: #c9a067;
	}
</style>
