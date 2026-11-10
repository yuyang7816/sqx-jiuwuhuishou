const pad = (value) => `${value}`.padStart(2, '0');

export const formatDateTime = (date) => {
	const d = date instanceof Date ? date : new Date(date);
	return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
};

/** 将后端预约时间统一解析为 Date（兼容字符串 / 时间戳 / 数组） */
const parseDateValue = (value) => {
	if (!value && value !== 0) {
		return null;
	}
	if (value instanceof Date) {
		return Number.isNaN(value.getTime()) ? null : value;
	}
	if (Array.isArray(value) && value.length >= 5) {
		const [year, month, day, hour = 0, minute = 0, second = 0] = value.map(Number);
		const date = new Date(year, month - 1, day, hour, minute, second);
		return Number.isNaN(date.getTime()) ? null : date;
	}
	if (typeof value === 'number') {
		const date = new Date(value);
		return Number.isNaN(date.getTime()) ? null : date;
	}
	const text = `${value}`.trim();
	if (!text) {
		return null;
	}
	const normalized = text.replace('T', ' ').replace(/-/g, '/');
	const date = new Date(normalized);
	if (!Number.isNaN(date.getTime())) {
		return date;
	}
	const fallback = new Date(text);
	return Number.isNaN(fallback.getTime()) ? null : fallback;
};

export const formatDisplayTime = (value) => {
	const date = parseDateValue(value);
	if (!date) {
		if (!value) {
			return '';
		}
		return `${value}`.replace('T', ' ').slice(0, 16);
	}
	return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

/** 订单「上门时间」展示：M月d日 HH:mm - HH:mm（有结束时间时展示时段） */
export const formatAppointmentDisplay = (startValue, endValue) => {
	const start = parseDateValue(startValue);
	if (!start) {
		const text = formatDisplayTime(startValue);
		if (!text) {
			return '';
		}
		// 已是时段文案则直接返回
		if (/\d{1,2}:\d{2}\s*[-–—~至到]\s*\d{1,2}:\d{2}/.test(text)) {
			return text;
		}
		return text;
	}
	const datePart = `${start.getMonth() + 1}月${start.getDate()}日`;
	const startHm = `${pad(start.getHours())}:${pad(start.getMinutes())}`;
	const end = parseDateValue(endValue);
	if (end) {
		const endHm = `${pad(end.getHours())}:${pad(end.getMinutes())}`;
		return `${datePart} ${startHm} - ${endHm}`;
	}
	return `${datePart} ${startHm}`;
};

export const formatFullDateTime = (value) => {
	const date = parseDateValue(value);
	if (!date) {
		if (!value) {
			return '';
		}
		return `${value}`.replace('T', ' ').slice(0, 19);
	}
	return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

const dateLabelOffsetMap = {
	'今日': 0,
	'明日': 1,
	'后日': 2
};

const resolveDateLabelOffset = (dateLabel) => {
	if (Object.prototype.hasOwnProperty.call(dateLabelOffsetMap, dateLabel)) {
		return dateLabelOffsetMap[dateLabel];
	}
	const match = `${dateLabel || ''}`.match(/^第(\d+)天$/);
	if (match) {
		return Math.max(0, Number(match[1]) - 1);
	}
	return 0;
};

/** 解析时段文案，如「14:00 - 16:00」「14:00-16:00」 */
export const parseAppointmentSlot = (slot = '') => {
	const text = `${slot || ''}`.trim();
	const matched = text.match(/(\d{1,2}:\d{2})\s*[-–—~至到]\s*(\d{1,2}:\d{2})/);
	if (matched) {
		return { start: matched[1], end: matched[2] };
	}
	const single = text.match(/(\d{1,2}:\d{2})/);
	return { start: single ? single[1] : '10:00', end: '' };
};

export const buildAppointmentTime = (dateLabel, slot, dateKey) => {
	const { start } = parseAppointmentSlot(slot);
	const [hour = '10', minute = '00'] = start.split(':');
	let target;
	if (dateKey && /^\d{4}-\d{2}-\d{2}$/.test(dateKey)) {
		const [year, month, day] = dateKey.split('-').map(Number);
		target = new Date(year, month - 1, day);
	} else {
		target = new Date();
		target.setDate(target.getDate() + resolveDateLabelOffset(dateLabel));
	}
	target.setHours(Number(hour), Number(minute), 0, 0);
	return formatDateTime(target);
};

export const formatMoney = (amount) => {
	if (amount === null || amount === undefined || amount === '') {
		return '¥0.00';
	}
	const num = Number(amount);
	if (Number.isNaN(num)) {
		return `${amount}`;
	}
	return `¥${num.toFixed(2)}`;
};
