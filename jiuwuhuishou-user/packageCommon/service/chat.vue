<template>
	<view class="app-page chat-page">
		<view class="chat-nav" :style="chatNavStyle">
			<view class="chat-nav__status" :style="{ height: `${statusBarHeight}px` }"></view>
			<view class="chat-nav__row">
				<view class="chat-nav__side" @click="goBack">
					<u-icon name="arrow-left" size="24" color="#1f1f1f"></u-icon>
				</view>
				<view class="chat-nav__title">旧物回收官方客服</view>
				<view class="chat-nav__side chat-nav__side--right"></view>
			</view>
		</view>
		<scroll-view
			scroll-y
			class="page-body compact chat-body"
			:scroll-into-view="scrollIntoView"
			:scroll-top="scrollTop"
			scroll-with-animation
			:enable-back-to-top="false"
		>
			<view class="chat-scroll-inner">
			<view class="row-between chat-welcome" style="align-items:flex-start; justify-content:flex-start;">
				<view class="dot-badge" style="flex:none;"><u-icon name="kefu-ermai" size="22" color="#c8102e"></u-icon></view>
				<view class="card" style="flex:1; margin-left: 16rpx;">
					<view style="font-size: 34rpx; line-height: 1.8;">{{ pageConfig.welcomeTitle }}</view>
					<view class="muted" style="margin-top: 16rpx;">{{ pageConfig.welcomeDesc }}</view>
					<view v-if="pageConfig.quickQuestions.length" class="list-divider"></view>
					<view v-if="pageConfig.quickQuestions.length" class="feature-list">
						<view
							v-for="q in pageConfig.quickQuestions"
							:key="q"
							class="ghost-panel"
							@click="sendQuickText(q)"
						>{{ q }}</view>
					</view>
				</view>
			</view>

			<view
				v-for="item in messages"
				:id="`msg-${item.id}`"
				:key="item.id"
				class="chat-message"
				:class="item.isUser ? 'is-user' : 'is-service'"
			>
				<view v-if="!item.isUser" class="dot-badge chat-message__avatar"><u-icon name="kefu-ermai" size="22" color="#c8102e"></u-icon></view>
				<view v-if="item.isUser" class="chat-message__bubble chat-message__bubble--user">
					<image
						v-if="item.messageType === 'image'"
						:src="item.content"
						mode="widthFix"
						class="chat-message__image"
						@click="previewImage(item.content)"
						@load="handleMessageImageLoad"
					></image>
					<view v-else>{{ item.content }}</view>
				</view>
				<view v-else class="chat-message__bubble chat-message__bubble--service">
					<image
						v-if="item.messageType === 'image'"
						:src="item.content"
						mode="widthFix"
						class="chat-message__image"
						@click="previewImage(item.content)"
						@load="handleMessageImageLoad"
					></image>
					<view v-else>{{ item.content }}</view>
				</view>
				<view v-if="item.isUser" class="chat-message__avatar chat-message__avatar--user">
					<image v-if="userAvatar" :src="userAvatar" mode="aspectFill" class="chat-message__avatar-img"></image>
					<view v-else class="chat-message__avatar-fallback"></view>
				</view>
			</view>
			<view v-if="transferHint" id="chat-transfer-hint" class="chat-transfer-hint">{{ transferHint }}</view>
			<view id="chat-bottom-anchor" class="chat-bottom-anchor"></view>
			</view>
		</scroll-view>

		<view class="footer-bar chat-footer" style="display:block; padding-top: 14rpx;">
			<scroll-view v-if="pageConfig.shortcuts.length" scroll-x class="tab-pills" style="margin-bottom: 14rpx;">
				<view style="display:flex; gap:12rpx;">
					<view
						v-for="item in pageConfig.shortcuts"
						:key="item"
						class="tab-pill"
						style="padding: 14rpx 24rpx;"
						@click="sendQuickText(item)"
					>{{ item }}</view>
				</view>
			</scroll-view>
			<view class="row-between" style="gap:16rpx;">
				<view class="ghost-panel chat-plus-btn" @click="pickAlbumImage">
					<u-icon name="plus-circle" size="26" color="#7a6161"></u-icon>
				</view>
				<input
					v-model="inputText"
					class="ghost-panel chat-input"
					confirm-type="send"
					placeholder="输入您想咨询的内容..."
					placeholder-class="chat-input__placeholder"
					@confirm="sendTextMessage"
				/>
				<view class="btn-main chat-send-btn" @click="sendTextMessage">
					<u-icon name="play-right-fill" size="24" color="#fff"></u-icon>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { getCurrentInstance, nextTick, ref } from 'vue';
	import { onHide, onLoad, onReady, onShow, onUnload } from '@dcloudio/uni-app';
	import { ensureLoggedIn, fetchProfile } from '@/common/services/auth.js';
	import { loadChatPageConfig, loadChatPollInterval, loadServiceMessages, askQuickQuestion, sendServiceMessage } from '@/common/services/service.js';
	import { chooseAlbumAndUploadImage } from '@/common/services/upload.js';

	const { proxy } = getCurrentInstance();
	const statusBarHeight = uni.getSystemInfoSync().statusBarHeight || 20;
	const chatNavStyle = {
		paddingTop: '0px'
	};

	const pageConfig = ref({
		welcomeTitle: '',
		welcomeDesc: '',
		quickQuestions: [],
		shortcuts: []
	});
	const messages = ref([]);
	const inputText = ref('');
	const userAvatar = ref('');
	const scrollIntoView = ref('');
	const scrollTop = ref(0);
	const scrollSeed = ref(0);
	const sending = ref(false);
	const uploading = ref(false);
	const transferHint = ref('');
	const pageActive = ref(false);
	const lastMessageId = ref(0);
	let pollTimer = null;
	let pollInterval = 3000;

	const getBottomAnchorId = () => {
		if (transferHint.value) {
			return 'chat-transfer-hint';
		}
		if (messages.value.length) {
			return `msg-${messages.value[messages.value.length - 1].id}`;
		}
		return 'chat-bottom-anchor';
	};

	const measureScrollHeight = () => new Promise((resolve) => {
		const query = proxy ? uni.createSelectorQuery().in(proxy) : uni.createSelectorQuery();
		query.select('.chat-scroll-inner').boundingClientRect((rect) => {
			resolve(rect?.height || 0);
		}).exec();
	});

	const scrollToBottom = async (force = false) => {
		if (!force && !pageActive.value) {
			return;
		}
		await nextTick();
		scrollIntoView.value = '';
		await nextTick();
		scrollIntoView.value = getBottomAnchorId();
		const contentHeight = await measureScrollHeight();
		scrollSeed.value += 1;
		if (contentHeight > 0) {
			scrollTop.value = contentHeight + scrollSeed.value;
		} else {
			scrollTop.value = 999999 + scrollSeed.value;
		}
	};

	const scrollToBottomOnEnter = async () => {
		const delays = [0, 120, 320, 640];
		for (const delay of delays) {
			if (delay > 0) {
				await new Promise((resolve) => setTimeout(resolve, delay));
			}
			await scrollToBottom(true);
		}
	};

	const updateLastMessageId = (records = []) => {
		lastMessageId.value = records.length ? records[records.length - 1].id : 0;
	};

	/** 转人工后若已有人工客服回复，则隐藏「正在转接人工」提示 */
	const clearTransferHintIfAgentReplied = (records = []) => {
		if (!transferHint.value) {
			return;
		}
		let lastTransferIndex = -1;
		for (let i = records.length - 1; i >= 0; i -= 1) {
			const item = records[i];
			if (item?.isUser && String(item.content || '').trim() === '转人工') {
				lastTransferIndex = i;
				break;
			}
		}
		if (lastTransferIndex < 0) {
			return;
		}
		const hasAgentReply = records.slice(lastTransferIndex + 1).some((item) => item && !item.isUser);
		if (hasAgentReply) {
			transferHint.value = '';
		}
	};

	const syncMessages = async ({ shouldScroll = false, alwaysScroll = false } = {}) => {
		const messageData = await loadServiceMessages();
		const records = messageData.records || [];
		const latestId = records.length ? records[records.length - 1].id : 0;
		const hasNewMessage = latestId !== lastMessageId.value || records.length !== messages.value.length;
		messages.value = records;
		updateLastMessageId(records);
		clearTransferHintIfAgentReplied(records);
		if (shouldScroll && (alwaysScroll || hasNewMessage)) {
			await scrollToBottom(true);
		}
	};

	const startPolling = () => {
		stopPolling();
		pollTimer = setInterval(async () => {
			if (!pageActive.value) {
				return;
			}
			try {
				await syncMessages({ shouldScroll: true });
			} catch (error) {
				// 轮询失败时静默处理，避免频繁打扰用户
			}
		}, pollInterval);
	};

	const stopPolling = () => {
		if (pollTimer) {
			clearInterval(pollTimer);
			pollTimer = null;
		}
	};

	const loadPageData = async () => {
		const [configData, messageData, profile, interval] = await Promise.all([
			loadChatPageConfig(),
			loadServiceMessages(),
			fetchProfile().catch(() => null),
			loadChatPollInterval()
		]);
		pageConfig.value = configData;
		messages.value = messageData.records || [];
		updateLastMessageId(messages.value);
		userAvatar.value = profile?.avatar || '';
		pollInterval = interval;
		await scrollToBottomOnEnter();
	};

	onLoad(async () => {
		if (!ensureLoggedIn()) {
			return;
		}
		pageActive.value = true;
		try {
			await loadPageData();
		} catch (error) {
			uni.showToast({ title: error?.message || '加载客服信息失败', icon: 'none' });
		}
	});

	onReady(async () => {
		await scrollToBottomOnEnter();
	});

	onShow(async () => {
		if (!ensureLoggedIn()) {
			return;
		}
		pageActive.value = true;
		try {
			await syncMessages({ shouldScroll: true, alwaysScroll: true });
			await scrollToBottomOnEnter();
		} catch (error) {
			// 页面返回时静默刷新，避免重复弹错
		}
		startPolling();
	});

	onHide(() => {
		pageActive.value = false;
		stopPolling();
	});

	onUnload(() => {
		pageActive.value = false;
		stopPolling();
	});

	const appendMessages = async (items = []) => {
		const validItems = items.filter(Boolean);
		if (!validItems.length) {
			return;
		}
		messages.value = [...messages.value, ...validItems];
		updateLastMessageId(messages.value);
		await scrollToBottom(true);
	};

	const sendQuickText = async (text) => {
		if (sending.value) {
			return;
		}
		sending.value = true;
		try {
			const result = await askQuickQuestion(text);
			await appendMessages([result.userMessage]);
			if (result.transferring) {
				transferHint.value = result.transferHint;
				await scrollToBottom(true);
				return;
			}
			transferHint.value = '';
			await appendMessages([result.replyMessage]);
			await scrollToBottom(true);
		} catch (error) {
			uni.showToast({ title: error?.message || '发送失败', icon: 'none' });
		} finally {
			sending.value = false;
		}
	};

	const sendTextMessage = async () => {
		const text = inputText.value.trim();
		if (!text || sending.value) {
			return;
		}
		sending.value = true;
		try {
			const message = await sendServiceMessage({ content: text, messageType: 'text' });
			inputText.value = '';
			transferHint.value = '';
			await appendMessages([message]);
		} catch (error) {
			uni.showToast({ title: error?.message || '发送失败', icon: 'none' });
		} finally {
			sending.value = false;
		}
	};

	const pickAlbumImage = async () => {
		if (uploading.value || sending.value) {
			return;
		}
		uploading.value = true;
		try {
			const url = await chooseAlbumAndUploadImage();
			const message = await sendServiceMessage({ content: url, messageType: 'image' });
			transferHint.value = '';
			await appendMessages([message]);
		} catch (error) {
			if (error?.errMsg && error.errMsg.includes('cancel')) {
				return;
			}
			uni.showToast({ title: error?.message || '图片发送失败', icon: 'none' });
		} finally {
			uploading.value = false;
		}
	};

	const previewImage = (url) => {
		if (!url) {
			return;
		}
		uni.previewImage({ urls: [url], current: url });
	};

	const handleMessageImageLoad = () => {
		scrollToBottom(true);
	};

	const goBack = () => {
		uni.navigateBack({
			fail() {
				uni.switchTab({
					url: '/pages/index/index'
				});
			}
		});
	};
</script>

<style lang="scss">
	.chat-nav {
		position: sticky;
		top: 0;
		left: 0;
		right: 0;
		z-index: 10;
		background: rgba(255, 255, 255, 0.96);
	}

	.chat-nav__row {
		height: 44px;
		padding: 0 24rpx;
		display: flex;
		align-items: center;
	}

	.chat-nav__side {
		width: 132rpx;
		display: flex;
		align-items: center;
	}

	.chat-nav__side--right {
		justify-content: flex-end;
	}

	.chat-nav__title {
		flex: 1;
		text-align: center;
		font-size: 34rpx;
		font-weight: 700;
		color: #1f1f1f;
	}

	.chat-page {
		height: 100vh;
		display: flex;
		flex-direction: column;
		overflow: hidden;
	}

	.chat-body {
		flex: 1;
		height: 0;
		padding-bottom: 0;
		box-sizing: border-box;
	}

	.chat-scroll-inner {
		padding-bottom: 32rpx;
	}

	.chat-footer {
		position: relative;
		left: auto;
		right: auto;
		bottom: auto;
		flex-shrink: 0;
		z-index: 1;
	}

	.chat-welcome {
		margin-top: 26rpx;
	}

	.chat-message {
		display: flex;
		align-items: flex-start;
		margin-top: 24rpx;
	}

	.chat-message.is-user {
		justify-content: flex-end;
	}

	.chat-message.is-service {
		justify-content: flex-start;
	}

	.chat-message__avatar {
		flex: none;
	}

	.chat-message__avatar--user {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		margin-left: 14rpx;
		overflow: hidden;
		background: linear-gradient(135deg, #ffd3c7, #f3b8ab);
	}

	.chat-message__avatar-img,
	.chat-message__avatar-fallback {
		width: 100%;
		height: 100%;
	}

	.chat-message__bubble {
		max-width: 520rpx;
		border-radius: 28rpx;
		padding: 26rpx;
		box-sizing: border-box;
		font-size: 34rpx;
		line-height: 1.7;
		word-break: break-word;
	}

	.chat-message__bubble--user {
		background: linear-gradient(135deg, #cf1432, #e63a4d);
		color: #fff;
	}

	.chat-message__bubble--service {
		background: #fff;
		color: #2f2424;
		box-shadow: 0 10rpx 24rpx rgba(111, 79, 79, 0.06);
		margin-left: 16rpx;
	}

	.chat-message__image {
		width: 320rpx;
		max-width: 100%;
		border-radius: 16rpx;
		display: block;
	}

	.chat-plus-btn {
		flex: none;
		width: 80rpx;
		text-align: center;
	}

	.chat-input {
		flex: 1;
		border-radius: 999rpx;
		height: 80rpx;
		line-height: 80rpx;
		padding: 0 28rpx;
		font-size: 28rpx;
		color: #4a3535;
	}

	.chat-input__placeholder {
		color: #b7abab;
	}

	.chat-send-btn {
		flex: none;
		width: 96rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.chat-transfer-hint {
		margin-top: 18rpx;
		text-align: center;
		font-size: 22rpx;
		line-height: 1.6;
		color: #b19f9f;
	}

	.chat-bottom-anchor {
		height: 1px;
	}
</style>
