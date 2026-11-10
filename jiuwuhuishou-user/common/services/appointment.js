import config from '../config.js';
import * as appointmentApi from '../api/appointment.js';
import {
	adaptAppointmentOptions,
	buildLocalAppointmentOptions,
	pickDefaultAppointment
} from '../adapters/appointment.js';

export const loadAppointmentOptions = async () => {
	if (config.USE_MOCK) {
		return buildLocalAppointmentOptions();
	}
	try {
		const list = await appointmentApi.fetchAppointmentOptions();
		const adapted = adaptAppointmentOptions(list);
		if (adapted.length) {
			return adapted;
		}
	} catch (error) {
		// 接口不可用时回退本地计算
	}
	return buildLocalAppointmentOptions();
};

export { pickDefaultAppointment };
