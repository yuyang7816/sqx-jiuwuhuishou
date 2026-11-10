<script setup>
	import {
		getCurrentInstance
	} from 'vue';
	const {
		proxy
	} = getCurrentInstance();
	import {
		onLaunch,
		onShow,
		onHide
	} from '@dcloudio/uni-app';
	import { captureInvitationCode } from '@/common/services/inviteCode.js';

	onLaunch((options) => {
		captureInvitationCode(options?.query || {});
		// //企业微信客服链接
		// proxy.$http.get('/app/common/type/274').then(res => {
		// 	if (res.code == 0 && res.data.value) {
		// 		uni.setStorageSync('kefu', res.data.value)
		// 	}
		// })
		// //企业微信客服appid
		// proxy.$http.get('/app/common/type/275').then(res => {
		// 	if (res.code == 0 && res.data.value) {
		// 		uni.setStorageSync('kefuAppId', res.data.value)
		// 	}
		// })
		// //客服类型  328
		// proxy.$http.get('/app/common/type/328').then(res => {
		// 	if (res.code == 0 && res.data.value) {
		// 		uni.setStorageSync('SelKeFu', res.data.value)
		// 	}
		// })
		// //客服电话  329
		// proxy.$http.get('/app/common/type/329').then(res => {
		// 	if (res.code == 0 && res.data.value) {
		// 		uni.setStorageSync('kefuPhone', res.data.value)
		// 	}
		// })
	});
	onShow(() => {
		console.log('onShow')
	});
	onHide(() => {
		console.log('onHide')
	});
</script>

<style lang="scss">
	/*每个页面公共css */
	//uview-plus公共样式
	@import "@/uni_modules/uview-plus/index.scss";
	//自定义公共样式
	@import "@/static/css/main.css";
	@import "@/static/css/theme.scss";

	page {
		background-color: #fffaf7;
	}
</style>
