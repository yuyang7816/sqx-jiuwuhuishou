import {
	get,
	getT,
	post,
	postT,
	postJson
} from './httpRequest.js';
const httpPlugin = {
	install(app) {
		app.config.globalProperties.$http = {
			get,
			post,
			postT,
			postJson,
			getT
		};
	}
};
export default httpPlugin;