import { apiGetNoAuth } from './http.js';

export const fetchAppointmentOptions = () => apiGetNoAuth('/app/order/appointment/options');
