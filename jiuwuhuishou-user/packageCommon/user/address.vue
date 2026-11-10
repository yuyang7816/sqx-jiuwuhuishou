<template>
	<view class="app-page address-page">
		<top-nav title="常用地址管理"></top-nav>
		<scroll-view scroll-y class="page-body compact address-body">
			<view class="address-list">
				<view v-for="item in addresses" :key="item.id" class="address-card">
					<view class="address-card__top">
						<view class="address-card__icon" :class="item.default ? 'is-default' : ''">
							<u-icon :name="item.icon" size="22" :color="item.default ? '#d91f37' : '#7c5f5f'"></u-icon>
						</view>
						<view class="address-card__info">
							<view class="address-card__name-row">
								<text class="address-card__name">{{ item.name }}</text>
								<text class="address-card__phone">{{ item.phone }}</text>
								<view v-if="item.default" class="address-card__tag">默认</view>
							</view>
							<text class="address-card__detail">{{ item.address }}</text>
						</view>
					</view>

					<view class="address-card__line"></view>

					<view class="address-card__actions">
						<view v-if="!item.default" class="address-card__action" @click="setDefault(item)">
							<u-icon name="star" size="15" color="#6c5151"></u-icon>
							<text>设为默认</text>
						</view>
						<view class="address-card__action" @click="goForm(item)">
							<u-icon name="edit-pen" size="15" color="#6c5151"></u-icon>
							<text>编辑</text>
						</view>
						<view class="address-card__action" @click="askDelete(item)">
							<u-icon name="trash" size="15" color="#6c5151"></u-icon>
							<text>删除</text>
						</view>
					</view>
				</view>
			</view>

			<view class="address-limit">
				<view class="address-limit__line"></view>
				<text class="address-limit__text">最多可添加 10 个地址</text>
				<view class="address-limit__line"></view>
			</view>
		</scroll-view>

		<floating-service></floating-service>

		<view class="footer-bar one address-footer">
			<view class="btn-main address-footer__btn" @click="goForm()">+ 新增地址</view>
		</view>

		<u-popup :show="showDelete" mode="center" :round="24" @close="showDelete = false">
			<view class="card" style="width: 620rpx; text-align: center;">
				<view class="dot-badge" style="margin: 10rpx auto 0; width: 108rpx; height: 108rpx;">
					<u-icon name="trash" size="34" color="#d91f37"></u-icon>
				</view>
				<view style="margin-top: 24rpx; font-size: 40rpx; font-weight: 800;">确认删除地址？</view>
				<view class="muted" style="margin-top: 14rpx;">删除后将无法恢复，请确认是否继续。</view>
				<view class="row-between" style="margin-top: 30rpx; gap: 18rpx;">
					<view class="btn-ghost" @click="showDelete = false">取消</view>
					<view class="btn-main" @click="removeAddress">确认删除</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { listAddresses, removeAddress as deleteAddress, setAddressAsDefault, formatAddressCard } from '@/common/services/address.js';

	const addresses = ref([]);

	const syncAddresses = async () => {
		try {
			const list = await listAddresses();
			addresses.value = list.map((item) => {
				const card = formatAddressCard(item);
				return {
					...item,
					phone: card.phoneText,
					address: card.detail
				};
			});
		} catch (error) {
			uni.showToast({ title: error.message || '加载地址失败', icon: 'none' });
		}
	};

	onShow(() => {
		syncAddresses();
	});

	const showDelete = ref(false);
	const deletingId = ref(null);

	const goForm = (item) => {
		const query = item ? `?id=${item.id}&mode=edit` : '?mode=create';
		uni.navigateTo({
			url: `/packageCommon/user/address-form${query}`
		});
	};

	const askDelete = (item) => {
		deletingId.value = item.id;
		showDelete.value = true;
	};

	const setDefault = async (item) => {
		try {
			await setAddressAsDefault(item.id);
			await syncAddresses();
			uni.showToast({ title: '已设为默认地址', icon: 'success' });
		} catch (error) {
			uni.showToast({ title: error.message || '设置失败', icon: 'none' });
		}
	};

	const removeAddress = async () => {
		try {
			await deleteAddress(deletingId.value);
			await syncAddresses();
			showDelete.value = false;
			deletingId.value = null;
			uni.showToast({ title: '删除成功', icon: 'success' });
		} catch (error) {
			uni.showToast({ title: error.message || '删除失败', icon: 'none' });
		}
	};
</script>

<style lang="scss">
	.address-page {
		background: linear-gradient(180deg, #fff8f5 0%, #ffffff 320rpx);
	}

	.address-body {
		padding-top: 14rpx;
		padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
	}

	.address-list {
		display: flex;
		flex-direction: column;
		gap: 22rpx;
	}

	.address-card {
		background: #fff;
		border-radius: 28rpx;
		padding: 24rpx 26rpx 22rpx;
		box-shadow: 0 14rpx 34rpx rgba(109, 75, 75, 0.08);
	}

	.address-card__top {
		display: flex;
		align-items: flex-start;
		gap: 18rpx;
	}

	.address-card__icon {
		width: 70rpx;
		height: 70rpx;
		border-radius: 50%;
		background: #f5eeee;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.address-card__icon.is-default {
		background: #fff0f1;
	}

	.address-card__info {
		flex: 1;
		min-width: 0;
	}

	.address-card__name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
		flex-wrap: wrap;
	}

	.address-card__name {
		font-size: 40rpx;
		font-weight: 800;
		color: #2f2525;
		line-height: 1.1;
	}

	.address-card__phone {
		font-size: 34rpx;
		font-weight: 500;
		color: #5a4343;
	}

	.address-card__tag {
		padding: 6rpx 14rpx;
		border-radius: 12rpx;
		background: #ef3546;
		color: #fff;
		font-size: 22rpx;
		line-height: 1;
	}

	.address-card__detail {
		display: block;
		margin-top: 12rpx;
		font-size: 28rpx;
		line-height: 1.65;
		color: #5b4747;
	}

	.address-card__line {
		height: 2rpx;
		background: #f1e7e7;
		margin: 20rpx 0 16rpx 88rpx;
	}

	.address-card__actions {
		display: flex;
		align-items: center;
		gap: 38rpx;
		padding-left: 88rpx;
	}

	.address-card__action {
		display: inline-flex;
		align-items: center;
		gap: 8rpx;
		font-size: 24rpx;
		color: #6c5151;
		line-height: 1;
	}

	.address-limit {
		margin-top: 78rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 18rpx;
	}

	.address-limit__line {
		width: 88rpx;
		height: 2rpx;
		background: #e7dada;
	}

	.address-limit__text {
		font-size: 24rpx;
		color: #b2a1a1;
	}

	.address-footer {
		background: transparent;
		box-shadow: none;
		backdrop-filter: none;
	}

	.address-footer__btn {
		height: 90rpx;
		border-radius: 999rpx;
		background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%);
		font-size: 34rpx;
		letter-spacing: 1rpx;
	}
</style>
