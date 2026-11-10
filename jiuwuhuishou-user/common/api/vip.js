import { apiPostJson } from './http.js';
import { post } from '../httpRequest.js';
import { assertSuccess } from './http.js';

export const fetchVipPlanList = () => apiPostJson('/app/vipPlan/list');

export const fetchVipRightsList = () => apiPostJson('/app/vipRights/list');

export const createVipPayOrder = async (payload) => assertSuccess(await post('/app/base/pay/pay', payload));
