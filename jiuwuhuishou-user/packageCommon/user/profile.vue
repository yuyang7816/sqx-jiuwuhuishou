<template>
	<view class="app-page profile-page">
		<top-nav title="用户信息"></top-nav>
		<scroll-view scroll-y class="page-body compact profile-body">
			<view class="card profile-hero">
				<view class="profile-hero__glow"></view>
				<view class="profile-avatar-wrap" @click="pickAvatar">
					<view class="profile-avatar">
						<image
							v-if="form.avatar"
							:key="avatarDisplayKey"
							:src="form.avatar"
							mode="aspectFill"
							class="profile-avatar__img"
						></image>
						<template v-else>
							<view class="profile-avatar__face"></view>
							<view class="profile-avatar__eye profile-avatar__eye--left"></view>
							<view class="profile-avatar__eye profile-avatar__eye--right"></view>
							<view class="profile-avatar__body"></view>
						</template>
					</view>
					<view class="profile-avatar__edit"><u-icon name="camera-fill" size="14" color="#ffffff"></u-icon></view>
				</view>
				<view class="profile-hero__title">点击头像可更换</view>
				<view class="muted" style="margin-top: 10rpx; text-align: center;">支持修改头像、昵称、性别等基础信息</view>
			</view>

			<view class="card" style="margin-top: 24rpx;">
				<view class="profile-form__list">
					<view class="profile-form__item">
						<view class="profile-form__label">昵称</view>
						<input v-model="form.nickname" class="profile-form__input" type="text" maxlength="16" placeholder="请输入昵称" placeholder-class="profile-form__placeholder" />
					</view>

					<view class="profile-form__item">
						<view class="profile-form__label">手机号</view>
						<input v-model="form.mobile" class="profile-form__input profile-form__input--readonly" type="number" maxlength="11" disabled placeholder="登录手机号" placeholder-class="profile-form__placeholder" />
					</view>

					<view class="profile-form__item profile-form__item--readonly">
						<view class="profile-form__label">邀请码</view>
						<view class="profile-form__value is-filled">{{ form.invitationCode || '暂无' }}</view>
					</view>

					<picker :range="genderOptions" @change="handleGenderChange">
						<view class="profile-form__item profile-form__picker">
							<view class="profile-form__label">性别</view>
							<view class="profile-form__value" :class="{ 'is-filled': form.gender }">{{ form.gender || '请选择性别' }}</view>
							<u-icon name="arrow-right" size="16" color="#b49d9d"></u-icon>
						</view>
					</picker>
				</view>
			</view>
		</scroll-view>

		<view class="footer-bar one" style="background: transparent; box-shadow: none; backdrop-filter: none;">
			<view class="btn-main" @click="submit">保存修改</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { ensureLoggedIn, fetchProfile, saveProfile } from '@/common/services/auth.js';
	import { chooseAndUploadImage } from '@/common/services/upload.js';

	const genderOptions = ['男', '女', '保密'];
	const form = ref({
		userId: '',
		nickname: '',
		mobile: '',
		avatar: '',
		gender: '保密',
		invitationCode: ''
	});
	// 选图/上传期间会触发多次 onShow；未保存前不要用服务端旧头像覆盖本地回显
	let suppressProfileReloadUntil = 0;
	let localAvatarPending = false;
	const avatarVersion = ref(0);
	const avatarDisplayKey = computed(() => `${form.value.avatar || ''}-${avatarVersion.value}`);

	const suppressProfileReload = (ms = 2500) => {
		suppressProfileReloadUntil = Date.now() + ms;
	};

	const setAvatarPreview = (url = '') => {
		const next = `${url || ''}`.trim();
		if (!next) {
			return;
		}
		form.value = {
			...form.value,
			avatar: next
		};
		avatarVersion.value += 1;
		localAvatarPending = true;
	};

	onShow(async () => {
		if (!ensureLoggedIn()) {
			return;
		}
		if (Date.now() < suppressProfileReloadUntil) {
			return;
		}
		try {
			const profile = await fetchProfile();
			form.value = {
				userId: profile.userId || '',
				nickname: profile.nickname || '',
				mobile: profile.mobile || '',
				avatar: localAvatarPending && form.value.avatar
					? form.value.avatar
					: (profile.avatar || ''),
				gender: profile.gender || '保密',
				invitationCode: profile.invitationCode || ''
			};
		} catch (error) {
			uni.showToast({ title: error.message || '加载资料失败', icon: 'none' });
		}
	});

	const pickAvatar = async () => {
		suppressProfileReload();
		try {
			const url = await chooseAndUploadImage({
				onPicked: (tempPath) => {
					// 先用本地临时图立即回显，避免上传期间空白
					setAvatarPreview(tempPath);
					suppressProfileReload();
				}
			});
			setAvatarPreview(url);
			suppressProfileReload();
		} catch (error) {
			if (error?.errMsg?.includes('cancel')) {
				return;
			}
			uni.showToast({ title: error.message || '头像上传失败', icon: 'none' });
		}
	};

	const handleGenderChange = (event) => {
		form.value.gender = genderOptions[event.detail.value] || '';
	};

	const submit = async () => {
		if (!form.value.nickname.trim()) {
			uni.showToast({ title: '请输入昵称', icon: 'none' });
			return;
		}
		try {
			await saveProfile(form.value);
			localAvatarPending = false;
			uni.showToast({ title: '保存成功', icon: 'success' });
			setTimeout(() => {
				uni.navigateBack();
			}, 500);
		} catch (error) {
			uni.showToast({ title: error.message || '保存失败', icon: 'none' });
		}
	};
</script>

<style lang="scss">
	.profile-page {
		background: linear-gradient(180deg, #fff7f5 0%, #fff 340rpx);
	}

	.profile-body {
		padding-top: 14rpx;
		padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
	}

	.profile-hero {
		position: relative;
		text-align: center;
		overflow: hidden;
		background: linear-gradient(135deg, #fff6f4 0%, #fff 45%, #fff8ee 100%);
	}

	.profile-hero__glow {
		position: absolute;
		left: 50%;
		top: -40rpx;
		transform: translateX(-50%);
		width: 320rpx;
		height: 200rpx;
		border-radius: 50%;
		background: radial-gradient(circle, rgba(255, 231, 224, 0.88) 0%, rgba(255, 231, 224, 0.18) 62%, rgba(255, 231, 224, 0) 80%);
	}

	.profile-avatar-wrap {
		position: relative;
		z-index: 2;
		width: 148rpx;
		height: 148rpx;
		margin: 4rpx auto 0;
	}

	.profile-avatar {
		width: 148rpx;
		height: 148rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #ffe1d8 0%, #ffcfc8 100%);
		border: 8rpx solid #fff;
		box-shadow: 0 16rpx 32rpx rgba(123, 79, 79, 0.12);
		position: relative;
		overflow: hidden;
	}

	.profile-avatar__img {
		width: 100%;
		height: 100%;
	}

	.profile-avatar__face {
		position: absolute;
		left: 40rpx;
		top: 28rpx;
		width: 66rpx;
		height: 58rpx;
		border-radius: 22rpx;
		background: linear-gradient(180deg, #ffb69e 0%, #f59a80 100%);
		border: 4rpx solid #fff2ed;
	}

	.profile-avatar__eye {
		position: absolute;
		top: 54rpx;
		width: 8rpx;
		height: 8rpx;
		border-radius: 50%;
		background: #4a2f2f;
	}

	.profile-avatar__eye--left {
		left: 60rpx;
	}

	.profile-avatar__eye--right {
		left: 82rpx;
	}

	.profile-avatar__body {
		position: absolute;
		left: 52rpx;
		bottom: 18rpx;
		width: 44rpx;
		height: 38rpx;
		border-radius: 20rpx 20rpx 12rpx 12rpx;
		background: linear-gradient(180deg, #f9a684 0%, #ef7e63 100%);
	}

	.profile-avatar__edit {
		position: absolute;
		right: 2rpx;
		bottom: 6rpx;
		width: 42rpx;
		height: 42rpx;
		border-radius: 50%;
		background: linear-gradient(180deg, #d01231 0%, #b80826 100%);
		box-shadow: 0 8rpx 18rpx rgba(188, 14, 43, 0.22);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.profile-hero__title {
		position: relative;
		z-index: 2;
		margin-top: 24rpx;
		font-size: 36rpx;
		font-weight: 800;
		color: #322727;
	}

	.profile-form__list {
		display: flex;
		flex-direction: column;
		gap: 2rpx;
	}

	.profile-form__item {
		display: flex;
		align-items: center;
		gap: 18rpx;
		padding: 28rpx 0;
		border-bottom: 2rpx solid #f3eaea;
	}

	.profile-form__item--readonly {
		justify-content: space-between;
	}

	.profile-form__input--readonly {
		color: #8f7b7b;
	}

	.profile-form__item:last-child {
		border-bottom: 0;
		padding-bottom: 6rpx;
	}

	.profile-form__picker {
		width: 100%;
		box-sizing: border-box;
	}

	.profile-form__label {
		width: 150rpx;
		font-size: 30rpx;
		font-weight: 600;
		color: #3a2d2d;
		flex-shrink: 0;
	}

	.profile-form__value {
		flex: 1;
		font-size: 28rpx;
		line-height: 1.6;
		color: #8f7b7b;
		text-align: right;
	}

	.profile-form__value.is-filled {
		color: #4d3c3c;
	}

	.profile-form__input {
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

	.profile-form__textarea {
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

	.profile-form__placeholder {
		color: #b9a7a7;
	}
</style>
