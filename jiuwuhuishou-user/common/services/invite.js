import config from '../config.js';
import * as referralApi from '../api/referral.js';
import { fetchProfile } from './auth.js';
import { adaptReferralOverviewFromApi } from '../adapters/referral.js';
import {
	adaptInviteCommissionLogFromApi,
	adaptInviteRecordFromApi,
	adaptInviteStatsFromOverview,
	buildStatsCutoffText,
	splitRankBoard
} from '../adapters/invite.js';

import { resolveAssetUrl } from '../adapters/user.js';

const DEFAULT_OVERVIEW = {
	pendingCommission: 0,
	monthContributorCount: 0,
	totalCommission: 0,
	inviteCount: 0
};

const DEFAULT_PODIUM = [
	{
		rank: 2,
		name: '张*明',
		amount: '￥8,240',
		podiumClass: 'invite-top3__user--second',
		medalClass: '',
		amountClass: '',
		avatar: ''
	},
	{
		rank: 1,
		name: '王*美',
		amount: '￥12,500',
		podiumClass: 'invite-top3__user--first',
		medalClass: 'is-first',
		amountClass: 'is-first',
		avatar: ''
	},
	{
		rank: 3,
		name: '李*华',
		amount: '￥6,100',
		podiumClass: 'invite-top3__user--third',
		medalClass: 'is-third',
		amountClass: '',
		avatar: ''
	}
];

const DEFAULT_RANK_LIST = [
	{ id: 4, rankText: '04', name: '陈*强', count: 42, amount: '￥4,280', avatar: '' },
	{ id: 5, rankText: '05', name: '赵*兰', count: 38, amount: '￥3,950', avatar: '' }
];

const DEFAULT_INVITE_RECORDS = [
	{
		id: 1,
		name: '张*明',
		tag: '已注册',
		tagClass: 'is-red',
		desc: '已完成注册，等待首单完成后发放奖励',
		time: '今天 09:28',
		status: '跟进中',
		reward: '预计 +￥15',
		avatar: ''
	}
];

const DEFAULT_COMMISSION_LOGS = [
	{
		id: 1,
		title: '一级邀请奖励',
		tag: '待入账',
		desc: '好友完成首单回收',
		time: '今天 08:00',
		amount: '+￥38.00',
		settle: '预计 3 日内结算',
		icon: 'account',
		iconColor: '#cf1432',
		iconClass: 'is-red'
	}
];

const DEFAULT_SHARE_TITLE = '邀请好友 共享返佣';
const DEFAULT_SHARE_PAGE = '/pages/index/index';
const DEFAULT_H5_HOST = 'https://jwhs.xianmxkj.com';

export const getH5Host = () => {
	const configured = `${config.H5_HOST || ''}`.trim().replace(/\/+$/, '');
	return configured || DEFAULT_H5_HOST;
};

export const buildInviteSharePath = (invitationCode = '') => {
	const code = `${invitationCode || ''}`.trim();
	if (!code) {
		return DEFAULT_SHARE_PAGE;
	}
	return `${DEFAULT_SHARE_PAGE}?invitationCode=${encodeURIComponent(code)}`;
};

/** 生成可直接打开的线上 H5 邀请链接 */
export const buildInviteShareUrl = (invitationCode = '') => {
	const path = buildInviteSharePath(invitationCode);
	return `${getH5Host()}${path.startsWith('/') ? path : `/${path}`}`;
};

export const buildInviteShareText = (invitationCode = '', title = '') => {
	const code = `${invitationCode || ''}`.trim();
	const url = buildInviteShareUrl(code);
	const headline = `${title || ''}`.trim() || '【旧物回收】邀请你一起参与环保回收领奖励';
	if (!code) {
		return `${headline}\n${url}`;
	}
	return `${headline}\n我的邀请码：${code}\n${url}`;
};

export const fetchInviteShareConfig = async () => {
	if (config.USE_MOCK) {
		const invitationCode = 'DEMO01';
		const path = buildInviteSharePath(invitationCode);
		const shareUrl = buildInviteShareUrl(invitationCode);
		return {
			invitationCode,
			title: DEFAULT_SHARE_TITLE,
			path,
			shareUrl,
			shareText: buildInviteShareText(invitationCode, DEFAULT_SHARE_TITLE)
		};
	}
	const profile = await fetchProfile();
	const invitationCode = profile.invitationCode || '';
	const path = buildInviteSharePath(invitationCode);
	const shareUrl = buildInviteShareUrl(invitationCode);
	return {
		invitationCode,
		title: DEFAULT_SHARE_TITLE,
		path,
		shareUrl,
		shareText: buildInviteShareText(invitationCode, DEFAULT_SHARE_TITLE)
	};
};

const adaptInvitePosterFromApi = (data = {}) => ({
	backgroundImageUrl: resolveAssetUrl(data.backgroundImageUrl || ''),
	userName: data.userName || '旧物回收用户',
	avatar: resolveAssetUrl(data.avatar || ''),
	invitationCode: data.invitationCode || '',
	invitePath: data.invitePath || buildInviteSharePath(data.invitationCode || ''),
	qrCodeImage: data.qrCodeImage || '',
	title: data.title || DEFAULT_SHARE_TITLE,
	description: data.description || '我正在参与旧物回收环保行动，邀请你一起为地球减负，首单回收即领惊喜红包！'
});

export const fetchInvitePoster = async () => {
	if (config.USE_MOCK) {
		return adaptInvitePosterFromApi({
			backgroundImageUrl: '',
			userName: '小绿同学',
			avatar: '',
			invitationCode: 'DEMO01',
			invitePath: buildInviteSharePath('DEMO01'),
			qrCodeImage: '',
			title: DEFAULT_SHARE_TITLE,
			description: '我正在参与旧物回收环保行动，邀请你一起为地球减负，首单回收即领惊喜红包！'
		});
	}
	const data = await referralApi.fetchInvitePoster();
	return adaptInvitePosterFromApi(data || {});
};

const fetchOverview = async () => {
	if (config.USE_MOCK) {
		return {
			pendingCommission: 420,
			monthContributorCount: 18,
			totalCommission: 3850.5,
			inviteCount: 128
		};
	}
	const [info, data] = await Promise.all([
		referralApi.fetchReferralCommissionInfo(),
		referralApi.fetchReferralData()
	]);
	return adaptReferralOverviewFromApi(info, data);
};

const fetchRankBoard = async () => {
	if (config.USE_MOCK) {
		return {
			podium: DEFAULT_PODIUM,
			rankList: DEFAULT_RANK_LIST
		};
	}
	const list = await referralApi.fetchReferralLeaderboard();
	return splitRankBoard(Array.isArray(list) ? list : []);
};

const fetchInviteRecords = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_INVITE_RECORDS;
	}
	const page = await referralApi.fetchReferralUserList();
	return (page?.records || []).map(adaptInviteRecordFromApi);
};

const fetchCommissionLogs = async () => {
	if (config.USE_MOCK) {
		return DEFAULT_COMMISSION_LOGS;
	}
	const page = await referralApi.fetchReferralCommissionPage({
		page: 1,
		limit: 100
	});
	return (page?.records || []).map(adaptInviteCommissionLogFromApi);
};

export const loadInvitePageData = async () => {
	const [overview, rankBoard, inviteRecords, commissionLogs] = await Promise.all([
		fetchOverview(),
		fetchRankBoard(),
		fetchInviteRecords(),
		fetchCommissionLogs()
	]);

	return {
		stats: adaptInviteStatsFromOverview(overview),
		statsCutoffText: buildStatsCutoffText(),
		podium: rankBoard.podium,
		rankList: rankBoard.rankList,
		inviteRecords,
		commissionLogs
	};
};

export { buildStatsCutoffText };
