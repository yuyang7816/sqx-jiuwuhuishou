import config from '../config.js';
import * as helpApi from '../api/help.js';
import {
	adaptHelpArticleDetailFromApi,
	adaptHelpArticlePageFromApi,
	DEFAULT_HELP_ARTICLES,
	DEFAULT_HELP_DETAIL
} from '../adapters/help.js';

export const loadHelpArticles = async ({ page = 1, limit = 50 } = {}) => {
	if (config.USE_MOCK) {
		return {
			records: [...DEFAULT_HELP_ARTICLES],
			total: DEFAULT_HELP_ARTICLES.length
		};
	}
	try {
		const data = await helpApi.fetchHelpArticlePage({ page, limit });
		return adaptHelpArticlePageFromApi(data);
	} catch (error) {
		return {
			records: [...DEFAULT_HELP_ARTICLES],
			total: DEFAULT_HELP_ARTICLES.length
		};
	}
};

export const loadHelpArticleDetail = async (id) => {
	const articleId = Number(id);
	if (!articleId) {
		throw new Error('文章ID无效');
	}
	if (config.USE_MOCK) {
		return {
			...DEFAULT_HELP_DETAIL,
			id: articleId
		};
	}
	try {
		const data = await helpApi.fetchHelpArticleInfo({ id: articleId });
		return adaptHelpArticleDetailFromApi(data);
	} catch (error) {
		return {
			...DEFAULT_HELP_DETAIL,
			id: articleId
		};
	}
};
