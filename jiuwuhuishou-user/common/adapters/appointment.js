const pad = (value) => `${value}`.padStart(2, '0');

const DEFAULT_SLOT_TEMPLATES = [
	{ time: '10:00 - 12:00', full: false },
	{ time: '14:00 - 16:00', full: false },
	{ time: '18:00 - 20:00', full: false }
];

const WEEKDAY_LABELS = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

const resolveDateLabel = (offset, date) => {
	if (offset === 0) return '今日';
	if (offset === 1) return '明日';
	if (offset === 2) return '后日';
	return WEEKDAY_LABELS[date.getDay()];
};

const parseSlotStart = (date, slotTime) => {
	const startText = `${slotTime || ''}`.split('-')[0].trim();
	const [hour = '10', minute = '00'] = startText.split(':');
	const target = new Date(date);
	target.setHours(Number(hour), Number(minute), 0, 0);
	return target;
};

const buildLocalAppointmentOptions = (days = 5, slotTemplates = DEFAULT_SLOT_TEMPLATES) => {
	const now = new Date();
	const result = [];
	for (let offset = 0; offset < days; offset += 1) {
		const date = new Date();
		date.setHours(0, 0, 0, 0);
		date.setDate(date.getDate() + offset);
		const slots = slotTemplates.map((template) => {
			const slotStart = parseSlotStart(date, template.time);
			const expired = slotStart.getTime() <= now.getTime();
			const full = !expired && !!template.full;
			const disabled = expired || full;
			return {
				time: template.time,
				full,
				disabled,
				disabledReason: expired ? 'expired' : full ? 'full' : '',
				tip: expired ? '该时段已过' : full ? '该时段已满' : '履约侧已预留运力'
			};
		});
		result.push({
			label: resolveDateLabel(offset, date),
			day: `${date.getMonth() + 1}月${date.getDate()}日`,
			dateKey: `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`,
			slots
		});
	}
	return result;
};

export const adaptAppointmentOptions = (list = []) => (list || []).map((item) => ({
	label: item.label,
	day: item.day,
	dateKey: item.dateKey,
	slots: (item.slots || []).map((slot) => ({
		time: slot.time,
		full: !!slot.full,
		disabled: !!slot.disabled,
		disabledReason: slot.disabledReason || '',
		tip: slot.tip || (slot.full ? '该时段已满' : '履约侧已预留运力')
	}))
}));

export const pickDefaultAppointment = (dateOptions = []) => {
	for (const date of dateOptions) {
		const slot = (date.slots || []).find((item) => !item.disabled && !item.full);
		if (slot) {
			return {
				dateLabel: date.label,
				slot: slot.time
			};
		}
	}
	return {
		dateLabel: dateOptions[0]?.label || '今日',
		slot: ''
	};
};

export { buildLocalAppointmentOptions, DEFAULT_SLOT_TEMPLATES };
