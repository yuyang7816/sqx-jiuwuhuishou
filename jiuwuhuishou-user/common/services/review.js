import config from '../config.js';
import * as reviewApi from '../api/review.js';
import { adaptReviewFromApi } from '../adapters/settlement.js';
import { reviewMockOrder } from './order.js';

export const loadReviewPageData = async (orderId) => {
	if (config.USE_MOCK) {
		return {
			reviewed: false,
			score: 5,
			complaintType: '价格争议',
			content: ''
		};
	}
	const data = await reviewApi.fetchReviewInfo({ orderId });
	return adaptReviewFromApi(data);
};

export const submitOrderReview = async ({ orderId, score, complaintType, content }) => {
	if (config.USE_MOCK) {
		reviewMockOrder(orderId);
		return { reviewed: true };
	}
	const data = await reviewApi.saveReview({ orderId, score, complaintType, content });
	return adaptReviewFromApi(data);
};
