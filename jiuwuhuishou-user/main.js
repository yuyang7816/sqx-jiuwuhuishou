import App from './App'
//引入uview plus
import uviewPlus from '@/uni_modules/uview-plus';
import httpPlugin from '/common/httpPlugin.js';
import {
	getWxAppid,
	getInvitation,
	getAppDownUrl,
	publicYuMing
} from '/common/queue.js';
// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	...App
})
app.$mount()
// #endif

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
export function createApp() {
	const app = createSSRApp(App)
	app.config.globalProperties.$getWxAppid = getWxAppid;
	app.config.globalProperties.$getInvitation = getInvitation;
	app.config.globalProperties.$getAppDownUrl = getAppDownUrl;
	app.config.globalProperties.$publicYuMing = publicYuMing;
	//使用uview plus
	app.use(uviewPlus)
	//挂载http请求
	app.use(httpPlugin)
	return {
		app
	}
}
// #endif