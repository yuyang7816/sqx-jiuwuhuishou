<template>
	<view class="app-page invite-page">
		<top-nav title="推荐收益" title-color="#1f1f1f" :transparent="true"></top-nav>

		<scroll-view scroll-y class="page-body compact invite-body">
			<view class="invite-hero">
				<view class="invite-hero__gift-wrap">
					<view class="invite-hero__gift-glow"></view>
					<view class="invite-hero__gift-box">
						<view class="invite-hero__gift-lid"></view>
						<view class="invite-hero__gift-ribbon invite-hero__gift-ribbon--v"></view>
						<view class="invite-hero__gift-ribbon invite-hero__gift-ribbon--h"></view>
						<view class="invite-hero__gift-bow invite-hero__gift-bow--left"></view>
						<view class="invite-hero__gift-bow invite-hero__gift-bow--right"></view>
					</view>
					<view v-for="n in 8" :key="n" class="invite-hero__coin" :class="`coin-${n}`"></view>
				</view>

				<view class="invite-hero__title">邀请好友 共享返佣</view>
				<view class="invite-hero__desc">最高可得订单金额 15% 佣金奖励</view>

				<view class="invite-actions">
					<button class="invite-actions__item invite-share-btn" open-type="share" hover-class="none" @tap="handleShareTap">
						<u-icon name="account" size="20" color="#cf1432"></u-icon>
						<text>推荐好友</text>
					</button>
					<view class="invite-actions__item" @click="openPosterPopup">
						<u-icon name="grid" size="20" color="#cf1432"></u-icon>
						<text>生成海报</text>
					</view>
					<view class="invite-actions__item" @click="openRulesPopup">
						<u-icon name="bookmark" size="20" color="#cf1432"></u-icon>
						<text>推荐规则</text>
					</view>
				</view>
			</view>

			<view class="invite-stats-card" @click="go('/packageCommon/user/referral')">
				<view class="invite-stats-card__head row-between">
					<view class="invite-stats-card__title-wrap">
						<view class="invite-stats-card__bar"></view>
						<text class="invite-stats-card__title">数据统计</text>
					</view>
					<text class="invite-stats-card__time">{{ statsCutoffText }}</text>
				</view>

				<view class="invite-stats-card__grid">
					<view v-for="item in stats" :key="item.label" class="invite-stats-card__item">
						<view class="invite-stats-card__label">{{ item.label }}</view>
						<view class="invite-stats-card__value" :class="item.className">{{ item.value }}</view>
					</view>
				</view>
			</view>

			<view class="invite-tabs">
				<view class="invite-tabs__item" :class="{ 'is-active': activeTab === 'rank' }" @click="activeTab = 'rank'">推荐榜单</view>
				<view class="invite-tabs__item" :class="{ 'is-active': activeTab === 'records' }" @click="activeTab = 'records'">邀请记录</view>
				<view class="invite-tabs__item" :class="{ 'is-active': activeTab === 'commission' }" @click="activeTab = 'commission'">佣金明细</view>
			</view>

			<view v-if="activeTab === 'rank' && !loading && podium.length" class="invite-top3">
				<view
					v-for="item in podium"
					:key="item.rank"
					class="invite-top3__user"
					:class="item.podiumClass"
				>
					<image v-if="item.avatar" class="invite-top3__avatar" :class="item.rank === 1 ? 'is-first' : item.rank === 3 ? 'is-third' : ''" :src="item.avatar" mode="aspectFill"></image>
					<view v-else class="invite-top3__avatar" :class="item.rank === 1 ? 'is-first' : item.rank === 3 ? 'is-third' : ''"></view>
					<view class="invite-top3__medal" :class="item.medalClass">{{ item.rank }}</view>
					<view class="invite-top3__name">{{ item.name }}</view>
					<view class="invite-top3__amount" :class="item.amountClass">{{ item.amount }}</view>
				</view>
			</view>

			<view v-if="activeTab === 'rank' && !loading" class="invite-rank-list">
				<view v-for="item in rankList" :key="item.id" class="invite-rank-item">
					<view class="invite-rank-item__left">
						<text class="invite-rank-item__rank">{{ item.rankText }}</text>
						<image v-if="item.avatar" class="invite-rank-item__avatar invite-rank-item__avatar--image" :src="item.avatar" mode="aspectFill"></image>
						<view v-else class="invite-rank-item__avatar"></view>
						<view class="invite-rank-item__info">
							<view class="invite-rank-item__name">{{ item.name }}</view>
							<view v-if="item.count" class="invite-rank-item__desc">推荐 {{ item.count }} 人</view>
						</view>
					</view>
					<text class="invite-rank-item__amount">{{ item.amount }}</text>
				</view>
				<view v-if="podium.length === 0 && rankList.length === 0" class="invite-empty">暂无推荐榜单数据</view>
			</view>

			<view v-if="activeTab === 'records' && !loading" class="invite-switch-list">
				<view v-for="item in inviteRecords" :key="item.id" class="invite-switch-list__item">
					<view class="invite-switch-list__left">
						<image v-if="item.avatar" class="invite-switch-list__avatar invite-switch-list__avatar--image" :src="item.avatar" mode="aspectFill"></image>
						<view v-else class="invite-switch-list__avatar"></view>
						<view class="invite-switch-list__info">
							<view class="invite-switch-list__name-row">
								<text class="invite-switch-list__name">{{ item.name }}</text>
								<view class="invite-switch-list__tag" :class="item.tagClass">{{ item.tag }}</view>
							</view>
							<view class="invite-switch-list__desc">{{ item.desc }}</view>
							<view class="invite-switch-list__time">{{ item.time }}</view>
						</view>
					</view>
					<view class="invite-switch-list__right">
						<view class="invite-switch-list__status">{{ item.status }}</view>
						<view class="invite-switch-list__amount">{{ item.reward }}</view>
					</view>
				</view>
				<view v-if="inviteRecords.length === 0" class="invite-empty">暂无邀请记录</view>
			</view>

			<view v-if="activeTab === 'commission' && !loading" class="invite-switch-list">
				<view v-for="item in commissionLogs" :key="item.id" class="invite-switch-list__item invite-switch-list__item--commission">
					<view class="invite-switch-list__left">
						<view class="invite-switch-list__icon" :class="item.iconClass">
							<u-icon :name="item.icon" size="18" :color="item.iconColor"></u-icon>
						</view>
						<view class="invite-switch-list__info">
							<view class="invite-switch-list__name-row">
								<text class="invite-switch-list__name">{{ item.title }}</text>
								<view class="invite-switch-list__tag invite-switch-list__tag--gold">{{ item.tag }}</view>
							</view>
							<view class="invite-switch-list__desc">{{ item.desc }}</view>
							<view class="invite-switch-list__time">{{ item.time }}</view>
						</view>
					</view>
					<view class="invite-switch-list__right">
						<view class="invite-switch-list__amount">{{ item.amount }}</view>
						<view class="invite-switch-list__status">{{ item.settle }}</view>
					</view>
				</view>
				<view v-if="commissionLogs.length === 0" class="invite-empty">暂无佣金明细</view>
			</view>

			<view v-if="loading" class="invite-empty">加载中...</view>
		</scroll-view>

		<floating-service></floating-service>

		<view class="footer-bar invite-footer">
			<button class="btn-ghost invite-footer__ghost invite-share-btn" open-type="share" hover-class="none" @tap="handleShareTap">
				<u-icon name="share-square" size="18" color="#cf1432"></u-icon>
				<text>分享好友</text>
			</button>
			<view class="btn-main invite-footer__main" @click="openPosterPopup">
				<u-icon name="grid" size="18" color="#ffffff"></u-icon>
				<text>生成推广海报</text>
			</view>
		</view>

		<u-popup :show="showPoster" mode="center" :round="28" @close="closePosterPopup">
			<view class="poster-popup-wrap">
				<view class="poster-popup-close" @click="closePosterPopup">
					<u-icon name="close" size="18" color="#5d4a4a"></u-icon>
				</view>

				<view v-if="posterLoading" class="poster-loading">海报生成中...</view>
				<template v-else-if="posterData">
					<view class="poster-card">
						<image
							class="poster-card__bg"
							:src="posterData.backgroundImageUrl"
							mode="aspectFill"
						></image>
						<view class="poster-card__mask"></view>
						<view class="poster-card__bottom">
							<view class="poster-card__user">
								<image
									v-if="posterData.avatar"
									class="poster-card__avatar poster-card__avatar--image"
									:src="posterData.avatar"
									mode="aspectFill"
								></image>
								<view v-else class="poster-card__avatar"></view>
								<view class="poster-card__user-info">
									<view class="poster-card__user-name">{{ posterData.userName }}</view>
									<view class="poster-card__user-desc">{{ posterData.description }}</view>
								</view>
							</view>

							<view class="poster-card__qrcode-box">
								<view class="poster-card__qrcode">
									<image
										v-if="posterData.qrCodeImage"
										class="poster-card__qrcode-image"
										:src="posterData.qrCodeImage"
										mode="aspectFit"
									></image>
									<u-qrcode
										v-else
										:val="posterQrValue"
										:size="52"
										:margin="0"
										:show="true"
										:loadMake="true"
									></u-qrcode>
								</view>
								<view class="poster-card__qrcode-text">长按识别小程序</view>
							</view>
						</view>
					</view>

					<view class="poster-popup-btn poster-popup-btn--main" :class="{ 'is-disabled': savingPoster }" @click="savePoster">
						<u-icon name="download" size="18" color="#ffffff"></u-icon>
						<text>{{ savingPoster ? '保存中...' : '保存图片到相册' }}</text>
					</view>
					<view class="poster-popup-btn poster-popup-btn--ghost" @click="closePosterPopup">取消</view>
				</template>
			</view>
		</u-popup>

		<!-- 保存确认弹框：必须盖在海报弹层上方（uni.showModal 会被 u-popup 挡住） -->
		<view v-if="showSaveConfirm" class="poster-save-confirm-mask">
			<view class="poster-save-confirm">
				<view class="poster-save-confirm__title">保存海报</view>
				<view class="poster-save-confirm__desc">即将打开海报原图，请长按图片选择「保存到相册」</view>
				<view class="poster-save-confirm__actions">
					<view class="poster-save-confirm__btn poster-save-confirm__btn--ghost" @click="onSaveConfirmCancel">取消</view>
					<view class="poster-save-confirm__btn" @click="onSaveConfirmOk">去保存</view>
				</view>
			</view>
		</view>

		<!-- 必须挂在页面根节点：微信旧版 canvas 在自定义组件(u-popup)内用页面实例无法定位 -->
		<canvas
			canvas-id="invitePosterCanvas"
			id="invitePosterCanvas"
			class="poster-hidden-canvas"
			:style="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
		></canvas>

		<u-popup :show="showRules" mode="center" :round="24" @close="showRules = false">
			<view class="rules-popup">
				<view class="rules-popup__nav row-between">
					<u-icon name="arrow-left" size="22" color="#2f2424" @click="showRules = false"></u-icon>
					<text class="rules-popup__title">{{ rulesTitle }}</text>
					<view class="rules-popup__nav-placeholder"></view>
				</view>

				<scroll-view scroll-y class="rules-popup__content">
					<view v-if="rulesLoading" class="rules-popup__loading">加载中...</view>
					<rich-text v-else-if="rulesHtml" class="rules-popup__html" :nodes="rulesHtml"></rich-text>
					<view v-else class="rules-popup__loading">暂无推荐规则</view>
				</scroll-view>

				<view class="rules-popup__footer">
					<view class="rules-popup__agree" @click="showRules = false">已阅读并同意</view>
					<view class="rules-popup__tip">
						<u-icon name="info-circle" size="14" color="#9f8f8f"></u-icon>
						<text class="rules-popup__tip-text">确认同意代表您已完全理解以上内容</text>
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { computed, getCurrentInstance, ref } from 'vue';
	import { onShareAppMessage, onShow, onUnload } from '@dcloudio/uni-app';
	import config from '@/common/config.js';
	import { resolveUploadedUrl } from '@/common/api/file.js';
	import { assertSuccess } from '@/common/api/http.js';
	import { ensureLoggedIn } from '@/common/services/auth.js';
	import { fetchInvitePoster, fetchInviteShareConfig, loadInvitePageData } from '@/common/services/invite.js';
	import { loadReferralRules } from '@/common/services/referralRules.js';
	import { uploadImage } from '@/common/services/upload.js';
	import {
		buildDefaultReferralRulesHtml
	} from '@/common/adapters/referralRules.js';

	const RULES_POPUP_TITLE = '推荐规则';

	const instance = getCurrentInstance();
	const showPoster = ref(false);
	const posterLoading = ref(false);
	const savingPoster = ref(false);
	const showSaveConfirm = ref(false);
	const saveConfirmImageUrl = ref('');
	let saveConfirmResolver = null;
	const posterData = ref(null);
	const canvasWidth = 620;
	const canvasHeight = 920;
	const showRules = ref(false);
	const rulesLoading = ref(false);
	const rulesTitle = ref(RULES_POPUP_TITLE);
	const rulesHtml = ref(buildDefaultReferralRulesHtml());
	const activeTab = ref('rank');
	const loading = ref(false);
	const preparingShare = ref(false);
	const shareConfig = ref({
		title: '邀请好友 共享返佣',
		path: '/pages/index/index',
		shareText: ''
	});
	const statsCutoffText = ref('统计截止至今日 08:00');
	const stats = ref([
		{ label: '已邀请(人)', value: '0', className: 'is-red' },
		{ label: '累计收益(元)', value: '0', className: 'is-gold' },
		{ label: '待入账(元)', value: '0', className: 'is-dark' }
	]);
	const podium = ref([]);
	const rankList = ref([]);
	const inviteRecords = ref([]);
	const commissionLogs = ref([]);

	const posterQrValue = computed(() => {
		const data = posterData.value;
		if (!data) {
			return '';
		}
		return data.invitationCode || data.invitePath || shareConfig.value.path;
	});

	const loadPageData = async () => {
		loading.value = true;
		try {
			const data = await loadInvitePageData();
			stats.value = data.stats;
			statsCutoffText.value = data.statsCutoffText;
			podium.value = data.podium;
			rankList.value = data.rankList;
			inviteRecords.value = data.inviteRecords;
			commissionLogs.value = data.commissionLogs;
		} catch (error) {
			stats.value = [
				{ label: '已邀请(人)', value: '0', className: 'is-red' },
				{ label: '累计收益(元)', value: '0', className: 'is-gold' },
				{ label: '待入账(元)', value: '0', className: 'is-dark' }
			];
			podium.value = [];
			rankList.value = [];
			inviteRecords.value = [];
			commissionLogs.value = [];
			uni.showToast({
				title: error.message || '加载推荐收益失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};

	const go = (url) => {
		uni.navigateTo({ url });
	};

	const refreshShareConfig = async () => {
		if (!ensureLoggedIn()) {
			return;
		}
		try {
			shareConfig.value = await fetchInviteShareConfig();
		} catch (error) {
			uni.showToast({
				title: error.message || '获取邀请信息失败',
				icon: 'none'
			});
		}
	};

	const handleShareTap = async () => {
		if (!ensureLoggedIn() || preparingShare.value) {
			return;
		}
		preparingShare.value = true;
		try {
			const config = await fetchInviteShareConfig();
			shareConfig.value = config;
			await new Promise((resolve, reject) => {
				uni.setClipboardData({
					data: config.shareText,
					success: resolve,
					fail: reject
				});
			});
			uni.showToast({
				title: '邀请链接已复制',
				icon: 'none'
			});
		} catch (error) {
			uni.showToast({
				title: error.message || '分享准备失败',
				icon: 'none'
			});
		} finally {
			preparingShare.value = false;
		}
	};

	onShareAppMessage(() => ({
		title: shareConfig.value.title,
		path: shareConfig.value.path
	}));

	const openPosterPopup = async () => {
		if (!ensureLoggedIn() || posterLoading.value) {
			return;
		}
		showPoster.value = true;
		posterLoading.value = true;
		posterData.value = null;
		try {
			posterData.value = await fetchInvitePoster();
			if (!posterData.value?.backgroundImageUrl) {
				throw new Error('暂未配置邀请背景图，请联系管理员在首页装修中上传');
			}
		} catch (error) {
			showPoster.value = false;
			uni.showToast({
				title: error.message || '生成海报失败',
				icon: 'none'
			});
		} finally {
			posterLoading.value = false;
		}
	};

	const getCanvasOwner = () => instance?.proxy || instance;

	const isH5Env = () => {
		try {
			const info = uni.getSystemInfoSync?.() || {};
			if (info.uniPlatform === 'web' || info.platform === 'web') {
				return true;
			}
		} catch (_) {
			/* ignore */
		}
		return typeof document !== 'undefined' && typeof window !== 'undefined';
	};

	const toErrorMessage = (error, fallback = '保存失败，请重试') => {
		const raw = `${error?.errMsg || error?.message || error || ''}`.trim();
		if (!raw) {
			return fallback;
		}
		if (/auth deny|authorize|privacy|permission/i.test(raw)) {
			return 'auth deny';
		}
		if (/downloadFile|download|proxy|fail/i.test(raw)) {
			return '背景图下载失败，请重试';
		}
		if (/canvas/i.test(raw)) {
			return '海报生成失败，请重试';
		}
		if (/getSetting|saveImageToPhotosAlbum|is not a function/i.test(raw)) {
			return fallback;
		}
		if (raw.length > 36) {
			return fallback;
		}
		return raw;
	};

	/** 外链图片经后端代理，避免 H5 CORS / 小程序 downloadFile 域名限制 */
	const toDownloadableUrl = (url = '') => {
		if (!url
			|| url.startsWith('data:')
			|| url.startsWith('blob:')
			|| url.startsWith('wxfile:')
			|| url.startsWith('file:')) {
			return url;
		}
		if (url.includes('/file/proxy')) {
			return url;
		}
		try {
			const apiOrigin = new URL(config.APIHOST).origin;
			const targetOrigin = new URL(url, config.APIHOST).origin;
			if (targetOrigin === apiOrigin) {
				return url;
			}
		} catch (_) {
			/* ignore */
		}
		return `${config.APIHOST}/file/proxy?url=${encodeURIComponent(url)}`;
	};

	const downloadImage = (url, { required = false } = {}) => new Promise((resolve, reject) => {
		const fail = (error) => {
			if (required) {
				reject(error instanceof Error ? error : new Error(toErrorMessage(error, '下载图片失败')));
				return;
			}
			resolve('');
		};
		if (!url) {
			resolve('');
			return;
		}
		// H5 / 部分端可直接用 dataURL 绘制；小程序需落盘为本地路径
		if (url.startsWith('data:image')) {
			if (isH5Env()) {
				resolve(url);
				return;
			}
			const fs = uni.getFileSystemManager?.();
			const userDataPath = (typeof wx !== 'undefined' && wx.env && wx.env.USER_DATA_PATH)
				|| (uni.env && uni.env.USER_DATA_PATH)
				|| '';
			if (!fs || !userDataPath) {
				fail(new Error('当前环境不支持保存海报'));
				return;
			}
			const base64 = url.replace(/^data:image\/\w+;base64,/, '');
			const filePath = `${userDataPath}/invite-qr-${Date.now()}.png`;
			fs.writeFile({
				filePath,
				data: base64,
				encoding: 'base64',
				success: () => resolve(filePath),
				fail
			});
			return;
		}

		const requestUrl = toDownloadableUrl(url);
		const tryDownload = (targetUrl, allowProxyFallback) => {
			uni.downloadFile({
				url: targetUrl,
				success: (res) => {
					if (res.statusCode === 200 && res.tempFilePath) {
						resolve(res.tempFilePath);
						return;
					}
					if (allowProxyFallback && targetUrl === url) {
						tryDownload(toDownloadableUrl(url), false);
						return;
					}
					fail(new Error('下载图片失败'));
				},
				fail: (error) => {
					if (allowProxyFallback && targetUrl === url) {
						tryDownload(toDownloadableUrl(url), false);
						return;
					}
					fail(error);
				}
			});
		};

		// 外链默认走代理；同域直连
		tryDownload(requestUrl, requestUrl === url);
	});

	const wrapText = (ctx, text, maxWidth, maxLines = 3) => {
		const chars = `${text || ''}`.split('');
		const lines = [];
		let line = '';
		chars.forEach((char) => {
			const next = line + char;
			if (ctx.measureText(next).width > maxWidth && line) {
				lines.push(line);
				line = char;
			} else {
				line = next;
			}
		});
		if (line) {
			lines.push(line);
		}
		if (lines.length <= maxLines) {
			return lines;
		}
		const clipped = lines.slice(0, maxLines);
		clipped[maxLines - 1] = `${clipped[maxLines - 1].slice(0, Math.max(0, clipped[maxLines - 1].length - 1))}…`;
		return clipped;
	};

	const withTimeout = (promise, ms, message) => new Promise((resolve, reject) => {
		let settled = false;
		const timer = setTimeout(() => {
			if (settled) {
				return;
			}
			settled = true;
			reject(new Error(message || '操作超时，请重试'));
		}, ms);
		Promise.resolve(promise).then(
			(value) => {
				if (settled) {
					return;
				}
				settled = true;
				clearTimeout(timer);
				resolve(value);
			},
			(error) => {
				if (settled) {
					return;
				}
				settled = true;
				clearTimeout(timer);
				reject(error);
			}
		);
	});

	const canvasToTempFile = () => new Promise((resolve, reject) => {
		let settled = false;
		const done = (fn, arg) => {
			if (settled) {
				return;
			}
			settled = true;
			clearTimeout(timer);
			fn(arg);
		};
		const timer = setTimeout(() => {
			done(reject, new Error('海报导出超时，请重试'));
		}, 8000);
		uni.canvasToTempFilePath({
			canvasId: 'invitePosterCanvas',
			width: canvasWidth,
			height: canvasHeight,
			destWidth: canvasWidth * 2,
			destHeight: canvasHeight * 2,
			fileType: 'png',
			quality: 1,
			success: (res) => {
				if (res?.tempFilePath) {
					done(resolve, res.tempFilePath);
					return;
				}
				done(reject, new Error('海报导出失败'));
			},
			fail: (error) => done(reject, error)
		}, getCanvasOwner());
	});

	const ensureAlbumAuth = () => new Promise((resolve, reject) => {
		if (isH5Env() || typeof uni.getSetting !== 'function') {
			resolve();
			return;
		}
		uni.getSetting({
			success: (setting) => {
				const albumAuth = setting.authSetting?.['scope.writePhotosAlbum'];
				// false = 曾拒绝；undefined = 未询问，交给 saveImageToPhotosAlbum 弹系统授权
				if (albumAuth === false) {
					reject({ errMsg: 'auth deny' });
					return;
				}
				resolve();
			},
			fail: () => resolve()
		});
	});

	const SAVE_LAYER_ID = 'invite-poster-save-layer';

	const isMobileBrowser = () => {
		try {
			const ua = (typeof navigator !== 'undefined' && navigator.userAgent) || '';
			return /Android|iPhone|iPad|iPod|Mobile|MicroMessenger|HarmonyOS/i.test(ua);
		} catch (_) {
			return false;
		}
	};

	const isWechatBrowser = () => {
		try {
			return /MicroMessenger/i.test((typeof navigator !== 'undefined' && navigator.userAgent) || '');
		} catch (_) {
			return false;
		}
	};

	const filePathToBlob = async (filePath) => {
		const res = await withTimeout(fetch(filePath), 15000, '读取海报超时');
		if (!res.ok) {
			throw new Error('读取海报失败');
		}
		return res.blob();
	};

	const blobToDataUrl = (blob) => new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = () => resolve(reader.result);
		reader.onerror = reject;
		reader.readAsDataURL(blob);
	});

	const toPosterDataUrl = async (filePath) => {
		if (`${filePath}`.startsWith('data:image')) {
			return filePath;
		}
		const blob = await filePathToBlob(filePath);
		return blobToDataUrl(blob);
	};

	const requestImageBlob = (url) => new Promise((resolve, reject) => {
		const requestUrl = toDownloadableUrl(url);
		uni.request({
			url: requestUrl,
			method: 'GET',
			responseType: 'arraybuffer',
			success: (res) => {
				if (res.statusCode !== 200 || !res.data) {
					reject(new Error('图片下载失败'));
					return;
				}
				const contentType = res.header?.['content-type']
					|| res.header?.['Content-Type']
					|| 'image/png';
				resolve(new Blob([res.data], { type: String(contentType).split(';')[0] || 'image/png' }));
			},
			fail: (error) => reject(error)
		});
	});

	const loadBrowserImage = async (url) => {
		if (!url || typeof document === 'undefined') {
			return null;
		}
		if (url.startsWith('data:')) {
			try {
				const img = await new Promise((resolve, reject) => {
					const image = new Image();
					image.onload = () => resolve(image);
					image.onerror = () => reject(new Error('图片解码失败'));
					image.src = url;
				});
				return { img, objectUrl: '' };
			} catch (_) {
				return null;
			}
		}

		let objectUrl = '';
		try {
			let blob;
			try {
				blob = await withTimeout(requestImageBlob(url), 15000, '图片下载超时');
			} catch (_) {
				const requestUrl = toDownloadableUrl(url);
				const res = await withTimeout(fetch(requestUrl), 15000, '图片下载超时');
				if (!res.ok) {
					throw new Error('图片下载失败');
				}
				blob = await res.blob();
			}
			objectUrl = URL.createObjectURL(blob);
			const img = await new Promise((resolve, reject) => {
				const image = new Image();
				image.onload = () => resolve(image);
				image.onerror = () => reject(new Error('图片解码失败'));
				image.src = objectUrl;
			});
			return { img, objectUrl };
		} catch (_) {
			if (objectUrl) {
				URL.revokeObjectURL(objectUrl);
			}
			return null;
		}
	};

	/** H5 用浏览器原生 canvas，避免 uni.canvas draw 回调不触发导致一直「正在生成」 */
	const composePosterImageH5 = async () => {
		const data = posterData.value;
		if (!data?.backgroundImageUrl) {
			throw new Error('海报背景图缺失');
		}
		if (typeof document === 'undefined') {
			throw new Error('当前环境不支持保存海报');
		}

		const scale = 2;
		const width = canvasWidth * scale;
		const height = canvasHeight * scale;
		const canvas = document.createElement('canvas');
		canvas.width = width;
		canvas.height = height;
		const ctx = canvas.getContext('2d');
		if (!ctx) {
			throw new Error('海报生成失败');
		}
		const sx = (n) => n * scale;

		const [bgAsset, avatarAsset, qrAsset] = await Promise.all([
			loadBrowserImage(data.backgroundImageUrl),
			loadBrowserImage(data.avatar),
			loadBrowserImage(data.qrCodeImage)
		]);
		if (!bgAsset?.img) {
			throw new Error('背景图下载失败，请检查图片域名配置');
		}

		ctx.fillStyle = '#ffffff';
		ctx.fillRect(0, 0, width, height);
		ctx.drawImage(bgAsset.img, 0, 0, width, height);

		const panelTop = sx(canvasHeight - 168);
		ctx.fillStyle = 'rgba(255,255,255,0.94)';
		ctx.fillRect(0, panelTop, width, sx(168));

		const avatarSize = sx(44);
		const avatarX = sx(24);
		const avatarY = panelTop + sx(28);
		if (avatarAsset?.img) {
			ctx.save();
			ctx.beginPath();
			ctx.arc(avatarX + avatarSize / 2, avatarY + avatarSize / 2, avatarSize / 2, 0, Math.PI * 2);
			ctx.closePath();
			ctx.clip();
			ctx.drawImage(avatarAsset.img, avatarX, avatarY, avatarSize, avatarSize);
			ctx.restore();
		} else {
			ctx.fillStyle = '#4f6376';
			ctx.beginPath();
			ctx.arc(avatarX + avatarSize / 2, avatarY + avatarSize / 2, avatarSize / 2, 0, Math.PI * 2);
			ctx.fill();
		}

		ctx.fillStyle = '#312525';
		ctx.font = `700 ${sx(15)}px sans-serif`;
		ctx.fillText(data.userName || '旧物回收用户', sx(80), panelTop + sx(46));

		ctx.fillStyle = '#5e4b4b';
		ctx.font = `${sx(11)}px sans-serif`;
		const lines = wrapText(
			{
				measureText: (text) => ({ width: ctx.measureText(text).width / scale })
			},
			data.description,
			300,
			3
		);
		lines.forEach((line, index) => {
			ctx.fillText(line, sx(80), panelTop + sx(70 + index * 16));
		});

		const qrSize = sx(96);
		const qrX = width - qrSize - sx(28);
		const qrY = panelTop + sx(20);
		ctx.fillStyle = '#ffffff';
		ctx.fillRect(qrX - sx(6), qrY - sx(6), qrSize + sx(12), qrSize + sx(12));
		if (qrAsset?.img) {
			ctx.drawImage(qrAsset.img, qrX, qrY, qrSize, qrSize);
		} else {
			ctx.fillStyle = '#f3d8dd';
			ctx.fillRect(qrX, qrY, qrSize, qrSize);
			ctx.fillStyle = '#cf1432';
			ctx.font = `${sx(10)}px sans-serif`;
			ctx.fillText(data.invitationCode || '邀请码', qrX + sx(18), qrY + qrSize / 2);
		}
		ctx.fillStyle = '#cf1432';
		ctx.font = `${sx(11)}px sans-serif`;
		ctx.fillText('长按识别小程序', qrX + sx(4), qrY + qrSize + sx(18));

		[bgAsset, avatarAsset, qrAsset].forEach((asset) => {
			if (asset?.objectUrl) {
				URL.revokeObjectURL(asset.objectUrl);
			}
		});

		const blob = await new Promise((resolve) => {
			canvas.toBlob((result) => resolve(result), 'image/png');
		});
		if (blob) {
			return URL.createObjectURL(blob);
		}
		return canvas.toDataURL('image/png');
	};

	/** 上传海报拿到 http(s) 地址：微信内 dataURL/blob 长按和 download 都常被禁 */
	const uploadPosterImage = async (filePath) => {
		if (config.USE_MOCK) {
			return toPosterDataUrl(filePath);
		}
		try {
			return await withTimeout(uploadImage(filePath), 20000, '上传超时，请重试');
		} catch (uploadError) {
			// 部分 H5 对 blob 路径 uploadFile 不稳定，再走 FormData
			const blob = await filePathToBlob(filePath);
			const fileName = `invite-poster-${Date.now()}.png`;
			const form = new FormData();
			if (typeof File !== 'undefined') {
				form.append('file', new File([blob], fileName, { type: blob.type || 'image/png' }));
			} else {
				form.append('file', blob, fileName);
			}
			const token = uni.getStorageSync('token');
			const controller = typeof AbortController !== 'undefined' ? new AbortController() : null;
			const timer = controller ? setTimeout(() => controller.abort(), 20000) : null;
			let res;
			try {
				res = await fetch(`${config.APIHOST}/file/upload`, {
					method: 'POST',
					headers: token ? { token: String(token) } : {},
					body: form,
					signal: controller?.signal
				});
			} catch (error) {
				if (error?.name === 'AbortError') {
					throw new Error('上传超时，请重试');
				}
				throw uploadError?.message ? uploadError : error;
			} finally {
				if (timer) {
					clearTimeout(timer);
				}
			}
			let json;
			try {
				json = await res.json();
			} catch (_) {
				throw new Error('上传响应解析失败');
			}
			const data = assertSuccess(json);
			const url = resolveUploadedUrl(data);
			if (!url) {
				throw new Error('上传成功但未返回图片地址');
			}
			if (/^https?:\/\//i.test(url)) {
				return url;
			}
			const relative = url.startsWith('/') ? url : `/${url}`;
			return `${config.APIHOST}${relative}`;
		}
	};

	const downloadPosterInBrowser = async (filePath) => {
		if (typeof document === 'undefined') {
			throw new Error('当前浏览器不支持下载');
		}
		const filename = `invite-poster-${Date.now()}.png`;
		// 微信会静默吞掉 a.download，只能打开原图让用户长按
		if (isWechatBrowser()) {
			window.location.href = filePath;
			return;
		}
		const trigger = (href, withDownload) => {
			const link = document.createElement('a');
			link.href = href;
			if (withDownload) {
				link.download = filename;
			}
			link.target = '_blank';
			link.rel = 'noopener';
			link.style.display = 'none';
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
		};

		if (/^https?:\/\//i.test(filePath) || `${filePath}`.startsWith('blob:') || `${filePath}`.startsWith('data:')) {
			trigger(filePath, true);
			return;
		}

		const blob = await filePathToBlob(filePath);
		const objectUrl = URL.createObjectURL(blob);
		trigger(objectUrl, true);
		setTimeout(() => URL.revokeObjectURL(objectUrl), 1500);
	};

	const closeSavePreview = () => {
		if (typeof document === 'undefined') {
			return;
		}
		const el = document.getElementById(SAVE_LAYER_ID);
		if (el) {
			el.remove();
		}
	};

	const closePosterPopup = () => {
		showPoster.value = false;
		closeSaveConfirm('cancel');
		closeSavePreview();
	};

	const closeSaveConfirm = (action = 'cancel') => {
		showSaveConfirm.value = false;
		const resolve = saveConfirmResolver;
		saveConfirmResolver = null;
		if (resolve) {
			resolve(action);
		}
	};

	const openSaveConfirmDialog = (imageUrl) => new Promise((resolve) => {
		saveConfirmImageUrl.value = imageUrl || '';
		saveConfirmResolver = resolve;
		showSaveConfirm.value = true;
	});

	const onSaveConfirmOk = () => {
		const imageUrl = saveConfirmImageUrl.value;
		closeSaveConfirm('confirm');
		if (imageUrl && typeof window !== 'undefined') {
			window.location.href = imageUrl;
		}
	};

	const onSaveConfirmCancel = () => {
		closeSaveConfirm('cancel');
	};

	/** 挂到 document.body，避开 uni-view 对长按菜单的拦截 */
	const openNativeSaveLayer = (imageUrl) => {
		if (typeof document === 'undefined') {
			throw new Error('当前环境不支持保存海报');
		}
		closeSavePreview();

		const mask = document.createElement('div');
		mask.id = SAVE_LAYER_ID;
		mask.style.cssText = 'position:fixed;left:0;top:0;right:0;bottom:0;z-index:2147483646;background:rgba(0,0,0,0.92);display:flex;flex-direction:column;align-items:center;justify-content:center;padding:24px 16px;box-sizing:border-box;';

		const tip = document.createElement('div');
		tip.textContent = '请长按下方图片，选择「保存到相册」';
		tip.style.cssText = 'color:#fff;font-size:15px;line-height:1.4;margin-bottom:16px;padding:8px 14px;background:rgba(255,255,255,0.14);border-radius:999px;text-align:center;';

		const imgWrap = document.createElement('div');
		imgWrap.style.cssText = 'flex:1;min-height:0;width:100%;display:flex;align-items:center;justify-content:center;';

		const img = document.createElement('img');
		img.src = imageUrl;
		img.alt = '邀请海报';
		img.style.cssText = 'max-width:90%;max-height:72vh;border-radius:8px;-webkit-touch-callout:default;-webkit-user-select:auto;user-select:auto;pointer-events:auto;';

		const actions = document.createElement('div');
		actions.style.cssText = 'width:100%;display:flex;gap:10px;margin-top:18px;';

		const makeBtn = (text, primary) => {
			const btn = document.createElement('button');
			btn.type = 'button';
			btn.textContent = text;
			btn.style.cssText = primary
				? 'flex:1;height:44px;border:0;border-radius:999px;background:linear-gradient(180deg,#cf1432,#b90d2a);color:#fff;font-size:15px;font-weight:700;'
				: 'flex:1;height:44px;border:1px solid rgba(255,255,255,0.28);border-radius:999px;background:rgba(255,255,255,0.12);color:#fff;font-size:15px;font-weight:700;';
			return btn;
		};

		const openBtn = makeBtn('打开原图', false);
		openBtn.addEventListener('click', () => {
			window.location.href = imageUrl;
		});

		const doneBtn = makeBtn('完成', true);
		doneBtn.addEventListener('click', () => {
			closeSavePreview();
		});

		imgWrap.appendChild(img);
		actions.appendChild(openBtn);
		actions.appendChild(doneBtn);
		mask.appendChild(tip);
		mask.appendChild(imgWrap);
		mask.appendChild(actions);
		document.body.appendChild(mask);
	};

	/** H5：上传后用 http 图，保存确认弹框盖在海报上方 */
	const savePosterInBrowser = async (filePath) => {
		if (isMobileBrowser()) {
			let imageUrl = '';
			try {
				imageUrl = await uploadPosterImage(filePath);
			} catch (error) {
				// 微信对 dataURL 长按/下载基本无效，上传失败应直接提示
				if (isWechatBrowser()) {
					throw new Error(error?.message || '海报上传失败，请检查网络后重试');
				}
				imageUrl = await toPosterDataUrl(filePath);
				console.warn('poster upload failed, fallback dataURL', error);
			}

			uni.hideLoading();
			// 自定义弹框叠在海报图片上方，避免被 u-popup 挡住
			await openSaveConfirmDialog(imageUrl);
			return 'preview';
		}

		try {
			const imageUrl = await uploadPosterImage(filePath);
			await downloadPosterInBrowser(imageUrl);
		} catch (_) {
			await downloadPosterInBrowser(await toPosterDataUrl(filePath));
		}
		return 'download';
	};

	const saveImageToAlbum = async (filePath) => {
		if (isH5Env() || typeof uni.saveImageToPhotosAlbum !== 'function') {
			return savePosterInBrowser(filePath);
		}
		await new Promise((resolve, reject) => {
			uni.saveImageToPhotosAlbum({
				filePath,
				success: resolve,
				fail: reject
			});
		});
		return 'album';
	};

	const composePosterImage = async () => {
		if (isH5Env()) {
			return withTimeout(composePosterImageH5(), 25000, '海报生成超时，请重试');
		}
		const data = posterData.value;
		if (!data?.backgroundImageUrl) {
			throw new Error('海报背景图缺失');
		}
		const [bgPath, avatarPath, qrPath] = await Promise.all([
			downloadImage(data.backgroundImageUrl, { required: true }),
			downloadImage(data.avatar),
			downloadImage(data.qrCodeImage)
		]);
		if (!bgPath) {
			throw new Error('背景图下载失败，请检查图片域名配置');
		}
		const ctx = uni.createCanvasContext('invitePosterCanvas', getCanvasOwner());
		ctx.setFillStyle('#ffffff');
		ctx.fillRect(0, 0, canvasWidth, canvasHeight);
		ctx.drawImage(bgPath, 0, 0, canvasWidth, canvasHeight);

		const panelTop = canvasHeight - 168;
		ctx.setFillStyle('rgba(255,255,255,0.94)');
		ctx.fillRect(0, panelTop, canvasWidth, 168);

		const avatarSize = 44;
		const avatarX = 24;
		const avatarY = panelTop + 28;
		if (avatarPath) {
			ctx.save();
			ctx.beginPath();
			ctx.arc(avatarX + avatarSize / 2, avatarY + avatarSize / 2, avatarSize / 2, 0, Math.PI * 2);
			ctx.clip();
			ctx.drawImage(avatarPath, avatarX, avatarY, avatarSize, avatarSize);
			ctx.restore();
		} else {
			ctx.setFillStyle('#4f6376');
			ctx.beginPath();
			ctx.arc(avatarX + avatarSize / 2, avatarY + avatarSize / 2, avatarSize / 2, 0, Math.PI * 2);
			ctx.fill();
		}

		ctx.setFillStyle('#312525');
		ctx.setFontSize(15);
		ctx.fillText(data.userName || '旧物回收用户', 80, panelTop + 46);

		ctx.setFillStyle('#5e4b4b');
		ctx.setFontSize(11);
		const lines = wrapText(ctx, data.description, 300, 3);
		lines.forEach((line, index) => {
			ctx.fillText(line, 80, panelTop + 70 + index * 16);
		});

		const qrSize = 96;
		const qrX = canvasWidth - qrSize - 28;
		const qrY = panelTop + 20;
		ctx.setFillStyle('#ffffff');
		ctx.fillRect(qrX - 6, qrY - 6, qrSize + 12, qrSize + 12);
		if (qrPath) {
			ctx.drawImage(qrPath, qrX, qrY, qrSize, qrSize);
		} else {
			ctx.setFillStyle('#f3d8dd');
			ctx.fillRect(qrX, qrY, qrSize, qrSize);
			ctx.setFillStyle('#cf1432');
			ctx.setFontSize(10);
			ctx.fillText(data.invitationCode || '邀请码', qrX + 18, qrY + qrSize / 2);
		}
		ctx.setFillStyle('#cf1432');
		ctx.setFontSize(11);
		ctx.fillText('长按识别小程序', qrX + 4, qrY + qrSize + 18);

		await new Promise((resolve) => {
			let finished = false;
			const done = () => {
				if (finished) {
					return;
				}
				finished = true;
				resolve();
			};
			ctx.draw(false, () => setTimeout(done, 300));
			// H5/部分端 draw 回调不触发，超时兜底
			setTimeout(done, 800);
		});
		return canvasToTempFile();
	};

	const savePoster = async () => {
		if (!posterData.value || savingPoster.value) {
			return;
		}
		savingPoster.value = true;
		uni.showLoading({ title: '正在生成...', mask: true });
		try {
			await ensureAlbumAuth();
			const filePath = await composePosterImage();
			uni.showLoading({ title: '正在处理...', mask: true });
			const saveMode = await saveImageToAlbum(filePath);
			// preview 模式保留海报弹层，确认框叠在海报上方
			if (saveMode !== 'preview') {
				showPoster.value = false;
			}
			if (saveMode === 'preview') {
				return;
			}
			uni.showToast({
				title: saveMode === 'download' ? '海报已开始下载' : '已保存到相册',
				icon: 'success'
			});
		} catch (error) {
			if (error?.name === 'AbortError') {
				return;
			}
			const message = toErrorMessage(error);
			if (message === 'auth deny') {
				uni.showModal({
					title: '需要相册权限',
					content: '请在设置中允许保存到相册后重试',
					confirmText: '去设置',
					success: (res) => {
						if (res.confirm && typeof uni.openSetting === 'function') {
							uni.openSetting({});
						}
					}
				});
			} else {
				uni.showToast({
					title: message,
					icon: 'none'
				});
			}
		} finally {
			uni.hideLoading();
			savingPoster.value = false;
		}
	};

	const openRulesPopup = async () => {
		showRules.value = true;
		rulesLoading.value = true;
		try {
			const data = await loadReferralRules();
			rulesTitle.value = RULES_POPUP_TITLE;
			rulesHtml.value = data.html || buildDefaultReferralRulesHtml();
		} catch (error) {
			rulesTitle.value = RULES_POPUP_TITLE;
			rulesHtml.value = buildDefaultReferralRulesHtml();
			uni.showToast({
				title: error.message || '加载推荐规则失败',
				icon: 'none'
			});
		} finally {
			rulesLoading.value = false;
		}
	};

	onShow(() => {
		loadPageData();
		refreshShareConfig();
	});

	onUnload(() => {
		closeSavePreview();
		closeSaveConfirm('cancel');
	});
</script>

<style lang="scss">
	.invite-page {
		background: linear-gradient(180deg, #eaa0df 0%, #ffa2d4 160rpx, #fff 480rpx);
	}

	.invite-body {
		padding-top: 8rpx;
		padding-bottom: calc(210rpx + env(safe-area-inset-bottom));
	}

	.invite-hero {
		padding: 8rpx 0 0;
		text-align: center;
	}

	.invite-hero__gift-wrap {
		position: relative;
		width: 270rpx;
		height: 244rpx;
		margin: 0 auto;
	}

	.invite-hero__gift-glow {
		position: absolute;
		left: 50%;
		top: 18rpx;
		transform: translateX(-50%);
		width: 240rpx;
		height: 220rpx;
		border-radius: 30rpx;
		background: radial-gradient(circle, rgba(255, 249, 238, 0.96) 0%, rgba(255, 226, 206, 0.92) 35%, rgba(255, 226, 206, 0) 78%);
	}

	.invite-hero__gift-box {
		position: absolute;
		left: 50%;
		top: 70rpx;
		transform: translateX(-50%);
		width: 120rpx;
		height: 116rpx;
		border-radius: 16rpx;
		background: linear-gradient(180deg, #f3c56d 0%, #c98e2e 100%);
		box-shadow: 0 18rpx 38rpx rgba(123, 69, 22, 0.2);
	}

	.invite-hero__gift-lid {
		position: absolute;
		left: -8rpx;
		top: -18rpx;
		width: 136rpx;
		height: 30rpx;
		border-radius: 12rpx;
		background: linear-gradient(180deg, #f4d07e 0%, #cf9334 100%);
	}

	.invite-hero__gift-ribbon {
		position: absolute;
		background: linear-gradient(180deg, #d71b32 0%, #ab0d21 100%);
	}

	.invite-hero__gift-ribbon--v {
		left: 50%;
		top: -18rpx;
		transform: translateX(-50%);
		width: 22rpx;
		height: 134rpx;
	}

	.invite-hero__gift-ribbon--h {
		left: 0;
		top: 40rpx;
		width: 120rpx;
		height: 22rpx;
	}

	.invite-hero__gift-bow {
		position: absolute;
		top: -36rpx;
		width: 40rpx;
		height: 32rpx;
		background: linear-gradient(180deg, #d71b32 0%, #b60f24 100%);
		border-radius: 50% 50% 10rpx 10rpx;
	}

	.invite-hero__gift-bow--left {
		left: 26rpx;
		transform: rotate(-26deg);
	}

	.invite-hero__gift-bow--right {
		right: 26rpx;
		transform: rotate(26deg);
	}

	.invite-hero__coin {
		position: absolute;
		width: 18rpx;
		height: 18rpx;
		border-radius: 50%;
		background: linear-gradient(180deg, #ffd86f 0%, #d79e18 100%);
		box-shadow: 0 6rpx 16rpx rgba(191, 135, 31, 0.22);
	}

	.coin-1 { left: 34rpx; top: 52rpx; }
	.coin-2 { left: 64rpx; top: 24rpx; }
	.coin-3 { left: 104rpx; top: 16rpx; }
	.coin-4 { left: 146rpx; top: 22rpx; }
	.coin-5 { right: 58rpx; top: 40rpx; }
	.coin-6 { right: 26rpx; top: 78rpx; }
	.coin-7 { left: 58rpx; bottom: 34rpx; }
	.coin-8 { right: 36rpx; bottom: 28rpx; }

	.invite-hero__title {
		margin-top: 10rpx;
		font-size: 50rpx;
		font-weight: 800;
		color: #eb4646;
		line-height: 1.2;
	}

	.invite-hero__desc {
		margin-top: 12rpx;
		font-size: 24rpx;
		font-weight: 500;
		color: rgba(255, 255, 255, 0.78);
	}

	.invite-actions {
		display: grid;
		grid-template-columns: repeat(3, minmax(0, 1fr));
		gap: 16rpx;
		margin-top: 34rpx;
	}

	.invite-actions__item {
		height: 108rpx;
		border-radius: 24rpx;
		background: #fff;
		box-shadow: 0 14rpx 32rpx rgba(175, 116, 147, 0.12);
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 12rpx;
		font-size: 24rpx;
		font-weight: 700;
		color: #2f2525;
	}

	.invite-actions__item text {
		font-size: 24rpx;
		font-weight: 700;
		color: #2f2525;
		line-height: 1.2;
	}

	.invite-share-btn {
		margin: 0;
		padding: 0;
		border: 0;
		width: 100%;
		background: #fff;
		background-color: #fff;
		line-height: inherit;
		font-size: inherit;
		font-weight: inherit;
		color: inherit;
		box-sizing: border-box;
	}

	.invite-share-btn::after {
		border: none;
	}

	.invite-footer .invite-share-btn {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
		height: 88rpx;
		font-size: 28rpx;
	}

	.invite-stats-card {
		margin-top: -6rpx;
		background: #fff;
		border-radius: 34rpx 34rpx 28rpx 28rpx;
		padding: 26rpx 26rpx 24rpx;
		box-shadow: 0 18rpx 38rpx rgba(138, 100, 100, 0.08);
	}

	.invite-stats-card__head {
		align-items: center;
	}

	.invite-stats-card__title-wrap {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.invite-stats-card__bar {
		width: 6rpx;
		height: 28rpx;
		border-radius: 999rpx;
		background: #cf1432;
	}

	.invite-stats-card__title {
		font-size: 34rpx;
		font-weight: 800;
		color: #2d2424;
	}

	.invite-stats-card__time {
		font-size: 24rpx;
		color: #7d6969;
	}

	.invite-stats-card__grid {
		display: grid;
		grid-template-columns: repeat(3, minmax(0, 1fr));
		margin-top: 24rpx;
	}

	.invite-stats-card__item {
		text-align: center;
		padding: 0 10rpx;
		position: relative;
	}

	.invite-stats-card__item:not(:last-child)::after {
		content: '';
		position: absolute;
		right: 0;
		top: 8rpx;
		bottom: 8rpx;
		width: 2rpx;
		background: #f1e6e6;
	}

	.invite-stats-card__label {
		font-size: 24rpx;
		line-height: 1.4;
		color: #7b6969;
	}

	.invite-stats-card__value {
		margin-top: 14rpx;
		font-size: 38rpx;
		font-weight: 800;
		line-height: 1;
	}

	.invite-stats-card__value.is-red { color: #cf1432; }
	.invite-stats-card__value.is-gold { color: #d59b13; }
	.invite-stats-card__value.is-dark { color: #3e2f2f; }

	.invite-tabs {
		display: grid;
		grid-template-columns: repeat(3, minmax(0, 1fr));
		margin-top: 28rpx;
		padding: 0 8rpx;
	}

	.invite-tabs__item {
		height: 64rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		color: #4c3d3d;
		position: relative;
	}

	.invite-tabs__item.is-active {
		font-weight: 800;
		color: #cf1432;
	}

	.invite-tabs__item.is-active::after {
		content: '';
		position: absolute;
		left: 50%;
		bottom: 8rpx;
		transform: translateX(-50%);
		width: 62rpx;
		height: 6rpx;
		border-radius: 999rpx;
		background: #cf1432;
	}

	.invite-top3 {
		display: flex;
		align-items: flex-end;
		justify-content: space-between;
		margin-top: 18rpx;
		padding: 0 20rpx;
	}

	.invite-top3__user {
		position: relative;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.invite-top3__user--second,
	.invite-top3__user--third {
		padding-bottom: 10rpx;
	}

	.invite-top3__avatar {
		width: 92rpx;
		height: 92rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #2a2830 0%, #5d6676 100%);
		border: 5rpx solid #fff;
		box-shadow: 0 12rpx 24rpx rgba(92, 71, 71, 0.12);
	}

	.invite-top3__avatar.is-first {
		width: 118rpx;
		height: 118rpx;
		border: 6rpx solid #ffd046;
	}

	.invite-top3__avatar.is-third {
		background: linear-gradient(135deg, #222027 0%, #534648 100%);
	}

	.invite-top3__medal {
		position: absolute;
		right: 2rpx;
		top: -4rpx;
		width: 28rpx;
		height: 28rpx;
		border-radius: 50%;
		background: #b7bcc8;
		color: #fff;
		font-size: 18rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.invite-top3__medal.is-first {
		background: #ffbf1f;
		width: 34rpx;
		height: 34rpx;
	}

	.invite-top3__medal.is-third {
		background: #f1a957;
	}

	.invite-top3__name {
		margin-top: 16rpx;
		font-size: 28rpx;
		font-weight: 700;
		color: #332828;
	}

	.invite-top3__amount {
		margin-top: 8rpx;
		font-size: 30rpx;
		font-weight: 800;
		color: #cf1432;
	}

	.invite-top3__amount.is-first {
		font-size: 38rpx;
	}

	.invite-rank-list {
		margin-top: 24rpx;
		background: #fff;
		border-radius: 26rpx;
		box-shadow: 0 14rpx 32rpx rgba(111, 79, 79, 0.08);
		overflow: hidden;
	}

	.invite-rank-item {
		height: 112rpx;
		padding: 0 24rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		box-sizing: border-box;
		border-bottom: 2rpx solid #f4ebeb;
	}

	.invite-rank-item:last-child {
		border-bottom: 0;
	}

	.invite-rank-item__left {
		display: flex;
		align-items: center;
		gap: 16rpx;
	}

	.invite-rank-item__rank {
		width: 40rpx;
		font-size: 24rpx;
		font-style: italic;
		color: #8a7575;
	}

	.invite-rank-item__avatar {
		width: 56rpx;
		height: 56rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #2e2c33 0%, #676f7f 100%);
	}

	.invite-rank-item__avatar--image,
	.invite-switch-list__avatar--image {
		background: #f4ebeb;
	}

	.invite-top3__avatar {
		object-fit: cover;
	}

	.invite-rank-item__name {
		font-size: 28rpx;
		font-weight: 700;
		color: #342929;
	}

	.invite-rank-item__desc {
		margin-top: 8rpx;
		font-size: 22rpx;
		color: #7f6c6c;
	}

	.invite-rank-item__amount {
		font-size: 28rpx;
		font-weight: 800;
		color: #cf1432;
	}

	.invite-switch-list {
		margin-top: 24rpx;
		background: #fff;
		border-radius: 26rpx;
		box-shadow: 0 14rpx 32rpx rgba(111, 79, 79, 0.08);
		overflow: hidden;
	}

	.invite-switch-list__item {
		padding: 24rpx;
		display: flex;
		align-items: flex-start;
		justify-content: space-between;
		gap: 16rpx;
		border-bottom: 2rpx solid #f4ebeb;
	}

	.invite-switch-list__item:last-child {
		border-bottom: 0;
	}

	.invite-switch-list__left {
		display: flex;
		align-items: flex-start;
		gap: 16rpx;
		flex: 1;
	}

	.invite-switch-list__avatar,
	.invite-switch-list__icon {
		width: 64rpx;
		height: 64rpx;
		flex-shrink: 0;
	}

	.invite-switch-list__avatar {
		border-radius: 50%;
		background: linear-gradient(135deg, #2e2c33 0%, #676f7f 100%);
	}

	.invite-switch-list__icon {
		border-radius: 20rpx;
		background: #fff3f4;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.invite-switch-list__icon.is-gold {
		background: #fff6dc;
	}

	.invite-switch-list__icon.is-violet {
		background: #f4efff;
	}

	.invite-switch-list__info {
		flex: 1;
		min-width: 0;
	}

	.invite-switch-list__name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
		flex-wrap: wrap;
	}

	.invite-switch-list__name {
		font-size: 28rpx;
		font-weight: 700;
		color: #342929;
	}

	.invite-switch-list__tag {
		padding: 6rpx 14rpx;
		border-radius: 999rpx;
		font-size: 20rpx;
		font-weight: 700;
	}

	.invite-switch-list__tag.is-red {
		background: #fff1f3;
		color: #cf1432;
	}

	.invite-switch-list__tag.is-green {
		background: #eef9f1;
		color: #1d9b55;
	}

	.invite-switch-list__tag.is-gold,
	.invite-switch-list__tag--gold {
		background: #fff6dc;
		color: #8a6400;
	}

	.invite-switch-list__desc {
		margin-top: 8rpx;
		font-size: 24rpx;
		line-height: 1.5;
		color: #7f6c6c;
	}

	.invite-switch-list__time {
		margin-top: 10rpx;
		font-size: 22rpx;
		color: #b08f8f;
	}

	.invite-switch-list__right {
		min-width: 154rpx;
		text-align: right;
	}

	.invite-switch-list__status {
		font-size: 22rpx;
		line-height: 1.5;
		color: #8a7575;
	}

	.invite-switch-list__amount {
		margin-top: 10rpx;
		font-size: 28rpx;
		font-weight: 800;
		color: #cf1432;
	}

	.invite-empty {
		padding: 48rpx 24rpx;
		text-align: center;
		font-size: 26rpx;
		color: #9a8888;
	}

	.invite-footer {
		background: rgba(255, 255, 255, 0.98);
		gap: 16rpx;
	}

	.invite-footer__ghost,
	.invite-footer__main {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
		height: 88rpx;
		font-size: 28rpx;
	}

	.invite-footer__ghost {
		flex: 1;
		border: 2rpx solid #cf1432;
		background: #fff;
		color: #cf1432;
	}

	.invite-footer__main {
		flex: 1.2;
	}

	.poster-popup-wrap {
		width: 660rpx;
		position: relative;
		padding: 28rpx 20rpx 20rpx;
		box-sizing: border-box;
	}

	.poster-popup-close {
		position: absolute;
		right: 12rpx;
		top: 16rpx;
		z-index: 2;
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: #fff;
		box-shadow: 0 12rpx 24rpx rgba(111, 79, 79, 0.12);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.poster-loading {
		min-height: 520rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		color: #9a8888;
	}

	.poster-card {
		position: relative;
		height: 920rpx;
		background: #fff;
		border-radius: 30rpx;
		overflow: hidden;
		box-shadow: 0 18rpx 40rpx rgba(111, 79, 79, 0.12);
	}

	.poster-card__bg {
		position: absolute;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		width: 100%;
		height: 100%;
	}

	.poster-card__mask {
		position: absolute;
		left: 0;
		right: 0;
		bottom: 0;
		height: 280rpx;
		background: linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 0.92) 42%, #fff 100%);
	}

	.poster-card__bottom {
		position: absolute;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 2;
		display: flex;
		justify-content: space-between;
		gap: 18rpx;
		padding: 20rpx 24rpx 28rpx;
	}

	.poster-card__user {
		display: flex;
		gap: 14rpx;
		flex: 1;
		min-width: 0;
	}

	.poster-card__avatar {
		width: 52rpx;
		height: 52rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #1f1f1f 0%, #4f6376 100%);
		flex-shrink: 0;
	}

	.poster-card__avatar--image {
		background: #f4ebeb;
	}

	.poster-card__user-info {
		flex: 1;
		min-width: 0;
	}

	.poster-card__user-name {
		font-size: 26rpx;
		font-weight: 700;
		color: #312525;
	}

	.poster-card__user-desc {
		margin-top: 8rpx;
		font-size: 20rpx;
		line-height: 1.6;
		color: #5e4b4b;
	}

	.poster-card__qrcode-box {
		width: 156rpx;
		text-align: center;
		flex-shrink: 0;
	}

	.poster-card__qrcode {
		width: 124rpx;
		height: 124rpx;
		border-radius: 18rpx;
		background: #fff;
		border: 4rpx solid #f3d8dd;
		padding: 10rpx;
		box-sizing: border-box;
		margin: 0 auto;
		display: flex;
		align-items: center;
		justify-content: center;
		overflow: hidden;
	}

	.poster-card__qrcode-image {
		width: 100%;
		height: 100%;
	}

	.poster-card__qrcode-text {
		margin-top: 10rpx;
		font-size: 20rpx;
		font-weight: 700;
		color: #cf1432;
	}

	.poster-hidden-canvas {
		position: fixed;
		left: 0;
		top: 0;
		width: 620px;
		height: 920px;
		transform: translate3d(-200%, -200%, 0);
		pointer-events: none;
		opacity: 0;
		z-index: -1;
	}

	.poster-popup-btn {
		height: 82rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
		font-size: 32rpx;
		font-weight: 700;
	}

	.poster-popup-btn--main {
		margin-top: 24rpx;
		background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%);
		color: #fff;
		box-shadow: 0 12rpx 24rpx rgba(207, 20, 50, 0.18);
	}

	.poster-popup-btn--main.is-disabled {
		opacity: 0.7;
	}

	.poster-popup-btn--ghost {
		margin-top: 18rpx;
		background: rgba(255,255,255,0.12);
		border: 2rpx solid rgba(255,255,255,0.28);
		color: rgba(255,255,255,0.8);
	}

	.poster-save-confirm-mask {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		z-index: 20000;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 48rpx;
		box-sizing: border-box;
		background: rgba(0, 0, 0, 0.45);
	}

	.poster-save-confirm {
		width: 560rpx;
		padding: 40rpx 32rpx 28rpx;
		border-radius: 24rpx;
		background: #fff;
		box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.18);
	}

	.poster-save-confirm__title {
		text-align: center;
		font-size: 34rpx;
		font-weight: 700;
		color: #2f2424;
	}

	.poster-save-confirm__desc {
		margin-top: 24rpx;
		text-align: center;
		font-size: 28rpx;
		line-height: 1.6;
		color: #666;
	}

	.poster-save-confirm__actions {
		margin-top: 36rpx;
		display: flex;
		gap: 20rpx;
	}

	.poster-save-confirm__btn {
		flex: 1;
		height: 80rpx;
		border-radius: 999rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%);
		color: #fff;
		font-size: 30rpx;
		font-weight: 700;
	}

	.poster-save-confirm__btn--ghost {
		background: #f5f5f5;
		color: #666;
		font-weight: 600;
	}

	.rules-popup {
		width: 680rpx;
		height: 960rpx;
		max-height: 82vh;
		background: #fff;
		border-radius: 28rpx;
		overflow: hidden;
		display: flex;
		flex-direction: column;
		box-sizing: border-box;
	}

	.rules-popup__nav {
		flex-shrink: 0;
		height: 92rpx;
		padding: 0 24rpx;
		border-bottom: 2rpx solid #f1e7e7;
		box-sizing: border-box;
	}

	.rules-popup__nav-placeholder {
		width: 44rpx;
		height: 44rpx;
	}

	.rules-popup__title {
		font-size: 34rpx;
		font-weight: 700;
		color: #2f2424;
	}

	.rules-popup__content {
		flex: 1;
		height: 0;
		min-height: 0;
		padding: 24rpx 28rpx 12rpx;
		box-sizing: border-box;
	}

	.rules-popup__loading {
		padding: 80rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #9a8888;
	}

	.rules-popup__html {
		display: block;
		font-size: 28rpx;
		line-height: 1.8;
		color: #3f3131;
		word-break: break-word;
	}

	.rules-popup__footer {
		flex-shrink: 0;
		padding: 18rpx 28rpx 32rpx;
		border-top: 2rpx solid #f1e7e7;
		box-sizing: border-box;
	}

	.rules-popup__agree {
		height: 84rpx;
		border-radius: 18rpx;
		background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%);
		box-shadow: 0 12rpx 24rpx rgba(207, 20, 50, 0.18);
		color: #fff;
		font-size: 32rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.rules-popup__tip {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 8rpx;
		margin-top: 16rpx;
		padding-bottom: 4rpx;
	}

	.rules-popup__tip-text {
		font-size: 12px;
		line-height: 1.5;
		color: #999;
	}
</style>
