<template>
	<view class="app-page change-password-page">
		<top-nav title="修改密码" title-color="#1f1f1f"></top-nav>
		<scroll-view scroll-y class="page-body compact change-password-body">
			<view class="change-password-card">
				<view class="change-password-form__item">
					<view class="change-password-form__label">当前密码</view>
					<view class="change-password-form__control">
						<input v-model="form.oldPassword" class="change-password-form__input" :password="oldPasswordType" maxlength="20" placeholder="请输入当前密码" placeholder-class="change-password-form__placeholder" />
						<u-icon :name="oldPasswordType ? 'eye-off' : 'eye'" size="18" color="#b9a6a6" @click="oldPasswordType = !oldPasswordType"></u-icon>
					</view>
				</view>

				<view class="change-password-form__item">
					<view class="change-password-form__label">新密码</view>
					<view class="change-password-form__control">
						<input v-model="form.newPassword" class="change-password-form__input" :password="newPasswordType" maxlength="20" placeholder="请输入新密码" placeholder-class="change-password-form__placeholder" />
						<u-icon :name="newPasswordType ? 'eye-off' : 'eye'" size="18" color="#b9a6a6" @click="newPasswordType = !newPasswordType"></u-icon>
					</view>
				</view>

				<view class="change-password-form__item change-password-form__item--last">
					<view class="change-password-form__label">确认密码</view>
					<view class="change-password-form__control">
						<input v-model="form.confirmPassword" class="change-password-form__input" :password="confirmPasswordType" maxlength="20" placeholder="请再次输入新密码" placeholder-class="change-password-form__placeholder" />
						<u-icon :name="confirmPasswordType ? 'eye-off' : 'eye'" size="18" color="#b9a6a6" @click="confirmPasswordType = !confirmPasswordType"></u-icon>
					</view>
				</view>
			</view>

			<view class="change-password-tips-card">
				<view class="change-password-tips-card__title">密码安全提醒</view>
				<view class="change-password-tips-card__desc">建议使用 6 位以上密码，并同时包含字母或数字组合，提升账户安全性。</view>
			</view>
		</scroll-view>

		<view class="change-password-footer">
			<view class="change-password-submit" @click="submitChangePassword">{{ submitting ? '提交中...' : '确认修改' }}</view>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { changePassword, ensureLoggedIn } from '@/common/services/auth.js';

	const oldPasswordType = ref(true);
	const newPasswordType = ref(true);
	const confirmPasswordType = ref(true);
	const submitting = ref(false);
	const form = ref({
		oldPassword: '',
		newPassword: '',
		confirmPassword: ''
	});

	const submitChangePassword = async () => {
		if (!ensureLoggedIn()) {
			return;
		}
		if (submitting.value) {
			return;
		}
		if (!form.value.oldPassword) {
			uni.showToast({ title: '请输入当前密码', icon: 'none' });
			return;
		}
		if (!form.value.newPassword) {
			uni.showToast({ title: '请输入新密码', icon: 'none' });
			return;
		}
		if (form.value.newPassword.length < 6) {
			uni.showToast({ title: '新密码不能少于6位', icon: 'none' });
			return;
		}
		if (form.value.newPassword !== form.value.confirmPassword) {
			uni.showToast({ title: '两次输入密码不一致', icon: 'none' });
			return;
		}
		submitting.value = true;
		try {
			await changePassword({
				oldPassword: form.value.oldPassword,
				newPassword: form.value.newPassword
			});
			uni.showToast({ title: '密码修改成功', icon: 'success' });
			setTimeout(() => {
				uni.navigateBack();
			}, 500);
		} catch (error) {
			uni.showToast({ title: error?.message || '修改失败', icon: 'none' });
		} finally {
			submitting.value = false;
		}
	};
</script>

<style lang="scss">
	.change-password-page {
		background: linear-gradient(180deg, #fff8f5 0%, #fff 280rpx);
	}

	.change-password-body {
		padding-top: 16rpx;
		padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
	}

	.change-password-card,
	.change-password-tips-card {
		background: #fff;
		border-radius: 28rpx;
		box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.06);
	}

	.change-password-card {
		padding: 0 28rpx;
	}

	.change-password-form__item {
		display: flex;
		align-items: center;
		gap: 24rpx;
		min-height: 108rpx;
		border-bottom: 2rpx solid #f2e8e8;
	}

	.change-password-form__item--last {
		border-bottom: 0;
	}

	.change-password-form__label {
		width: 150rpx;
		font-size: 32rpx;
		font-weight: 700;
		color: #2f2424;
		flex-shrink: 0;
	}

	.change-password-form__control {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		gap: 12rpx;
		min-width: 0;
	}

	.change-password-form__input {
		flex: 1;
		height: 46rpx;
		font-size: 28rpx;
		line-height: 46rpx;
		color: #4a3a3a;
		text-align: right;
		background: transparent;
		padding: 0;
		margin: 0;
	}

	.change-password-form__placeholder {
		color: #c2aeae;
		font-size: 28rpx;
	}

	.change-password-tips-card {
		margin-top: 24rpx;
		padding: 26rpx 28rpx 24rpx;
	}

	.change-password-tips-card__title {
		font-size: 32rpx;
		font-weight: 700;
		color: #2f2424;
	}

	.change-password-tips-card__desc {
		margin-top: 14rpx;
		font-size: 24rpx;
		line-height: 1.7;
		color: #8a7676;
	}

	.change-password-footer {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		padding: 20rpx 30rpx calc(20rpx + env(safe-area-inset-bottom));
		background: rgba(255, 255, 255, 0.96);
		box-shadow: 0 -8rpx 30rpx rgba(89, 49, 49, 0.06);
		backdrop-filter: blur(14rpx);
		box-sizing: border-box;
	}

	.change-password-submit {
		height: 92rpx;
		border-radius: 999rpx;
		background: linear-gradient(135deg, #d01231 0%, #b80826 100%);
		color: #fff;
		font-size: 34rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 16rpx 36rpx rgba(200, 16, 46, 0.25);
	}
</style>
