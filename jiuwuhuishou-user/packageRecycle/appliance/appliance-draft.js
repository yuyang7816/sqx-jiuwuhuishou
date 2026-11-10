const STORAGE_KEY = 'appliance_settlement_draft';

export const saveApplianceSettlementDraft = ({ spec, estimate }) => {
	uni.setStorageSync(STORAGE_KEY, {
		spec,
		estimate,
		savedAt: Date.now()
	});
};

export const loadApplianceSettlementDraft = () => {
	try {
		return uni.getStorageSync(STORAGE_KEY) || null;
	} catch (error) {
		return null;
	}
};

export const clearApplianceSettlementDraft = () => {
	try {
		uni.removeStorageSync(STORAGE_KEY);
	} catch (error) {
		// ignore
	}
};
