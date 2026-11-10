// 本地后端（9135）
// const ROOTPATH = "http://127.0.0.1:9135/sqx_fast";
// const ROOTPATH3 = "ws://127.0.0.1:9135/sqx_fast/"; //聊天

// 线上
const ROOTPATH = "https://jwhs.xianmxkj.com/sqx_fast";
const ROOTPATH3 = "wss://jwhs.xianmxkj.com/wss/chatSocket/";//聊天

// 局域网真机调试可改成本机 IP，例如：
// const ROOTPATH = "http://192.168.0.128:9135/sqx_fast";
// const ROOTPATH3 = "ws://192.168.0.128:9135/sqx_fast/";

const config = {
	APIHOST: ROOTPATH,
	WSHOST1: ROOTPATH3,
	// 用户端 H5 线上地址（分享链接统一走线上，勿用本地调试域名）
	H5_HOST: 'https://jwhs.xianmxkj.com',
	// true: 使用本地 Mock；false: 调用 /app/** 后端接口
	USE_MOCK: false
};
export default config;
