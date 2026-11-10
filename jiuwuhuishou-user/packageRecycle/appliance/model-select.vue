<template>
	<view class="app-page appliance-spec-page">
		<top-nav title="回收商品参数" title-color="#1f1f1f" :show-more="false"></top-nav>
		<scroll-view scroll-y class="page-body compact appliance-spec-body">
			<view class="spec-hero-card">
				<view class="spec-hero-card__label">{{ currentConfig.cardLabel }}</view>
				<view class="spec-hero-card__title">{{ currentConfig.title }}</view>
				<view class="spec-hero-card__desc">{{ currentConfig.subtitle }}</view>
				<view class="spec-hero-card__icon">
					<u-icon :name="currentConfig.icon" size="30" color="#cf1432"></u-icon>
				</view>
			</view>

			<view class="spec-tabs">
				<view
					v-for="category in applianceCategories"
					:key="category"
					class="spec-tabs__item"
					:class="{ 'is-active': selectedCategory === category, 'is-disabled': categoryLoading }"
					@click="changeCategory(category)"
				>
					{{ category }}
				</view>
			</view>

			<view v-if="categoryLoading" class="spec-section-loading">加载中...</view>

			<template v-else>
				<view class="spec-section-card">
					<view class="spec-section-card__title">品牌</view>
					<view class="spec-chip-list">
						<view
							v-for="brand in brandOptions"
							:key="brand"
							class="spec-chip"
							:class="{ 'is-active': spec.brand === brand, 'is-disabled': specLoading }"
							@click="selectBrand(brand)"
						>
							{{ brand }}
						</view>
					</view>
				</view>

				<view v-if="specLoading" class="spec-section-loading">加载中...</view>

				<template v-else>
					<view class="spec-section-card">
						<view class="spec-section-card__head row-between">
							<view class="spec-section-card__title">型号</view>
							<view class="spec-section-card__hint">{{ modelOptions.length }} 款可选</view>
						</view>
						<view class="spec-model-list">
							<view
								v-for="model in modelOptions"
								:key="model.id || model.name"
								class="spec-model-item"
								:class="{ 'is-active': spec.model === model.name }"
								@click="selectModel(model)"
							>
								<view class="spec-model-item__name">{{ model.name }}</view>
								<u-icon :name="spec.model === model.name ? 'checkmark-circle-fill' : 'arrow-right'" size="18" :color="spec.model === model.name ? '#cf1432' : '#c7b7b7'"></u-icon>
							</view>
						</view>
					</view>

					<template v-if="hasModelSpecs">
						<view v-for="field in modelFields" :key="field.key" class="spec-section-card">
							<view class="spec-section-card__head row-between">
								<view class="spec-section-card__title">{{ field.label }}</view>
								<view v-if="isMultiSelectField(field)" class="spec-section-card__hint">可多选</view>
							</view>
							<view class="spec-chip-list">
								<view
									v-for="option in field.options"
									:key="option"
									class="spec-chip"
									:class="{ 'is-active': isDetailOptionSelected(spec.details[field.key], option) }"
									@click="updateDetail(field, option)"
								>
									{{ option }}
								</view>
							</view>
						</view>

						<view v-if="modelAssessOptions.damageOptions.length" class="spec-section-card">
							<view class="spec-section-card__title">损伤情况</view>
							<view class="spec-chip-list">
								<view
									v-for="option in modelAssessOptions.damageOptions"
									:key="option"
									class="spec-chip"
									:class="{ 'is-active': spec.damage === option }"
									@click="selectDamage(option)"
								>
									{{ option }}
								</view>
							</view>
						</view>

						<view
							v-if="modelAssessOptions.conditionOptions.length || modelAssessOptions.ageOptions.length"
							class="spec-stack-group"
						>
							<view v-if="modelAssessOptions.conditionOptions.length" class="spec-section-card">
								<view class="spec-section-card__title">新旧程度</view>
								<view class="spec-chip-list spec-chip-list--column">
									<view
										v-for="option in modelAssessOptions.conditionOptions"
										:key="option"
										class="spec-chip spec-chip--full"
										:class="{ 'is-active': spec.condition === option }"
										@click="selectCondition(option)"
									>
										{{ option }}
									</view>
								</view>
							</view>
							<view v-if="modelAssessOptions.ageOptions.length" class="spec-section-card">
								<view class="spec-section-card__title">使用年限</view>
								<view class="spec-chip-list spec-chip-list--column">
									<view
										v-for="option in modelAssessOptions.ageOptions"
										:key="option"
										class="spec-chip spec-chip--full"
										:class="{ 'is-active': spec.age === option }"
										@click="selectAge(option)"
									>
										{{ option }}
									</view>
								</view>
							</view>
						</view>
					</template>

					<view class="spec-preview-card">
						<view class="spec-preview-card__title">当前预估</view>
						<view class="spec-preview-card__amount">{{ estimateRangeText }}</view>
						<view class="spec-preview-card__desc">{{ estimateDesc }}</view>
						<view class="spec-preview-card__tags">
							<text v-for="tag in previewTags" :key="tag" class="spec-preview-card__tag">{{ tag }}</text>
						</view>
					</view>
				</template>
			</template>
		</scroll-view>

		<view class="footer-bar appliance-spec-footer">
			<view class="btn-main appliance-spec-footer__btn" @click="submitSpec">下一步</view>
		</view>
	</view>
</template>

<script setup>
	import { computed, reactive, ref, watch } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import {
		fetchApplianceEstimatePrice,
		loadApplianceSpecPage,
		normalizeSpecFromPage
	} from '@/common/services/applianceSpec.js';
	import {
		flattenDetailValues,
		hasModelBoundSpecs,
		isDetailOptionSelected,
		isMultiSelectField,
		resolveDetailValue,
		resolveModelAssessOptions,
		resolveModelFields,
		toggleDetailOption
	} from '@/common/adapters/applianceSpec.js';
	import {
		encodeSpecToQuery,
		formatCurrency,
		parseSpecFromQuery
	} from './spec-data';
	import { saveApplianceSettlementDraft } from './appliance-draft.js';

	const specPage = ref({
		categories: [],
		config: {
			title: '',
			subtitle: '',
			icon: 'home',
			cardLabel: '',
			brands: [],
			fields: [],
			damageOptions: [],
			conditionOptions: [],
			ageOptions: [],
			reward: 0
		}
	});
	const estimateData = ref({
		estimate: 0,
		reward: 0,
		low: 0,
		high: 0
	});
	const pendingSpec = ref(null);
	const categoryLoading = ref(true);
	const specLoading = ref(false);
	const suppressEstimateWatch = ref(false);
	const spec = reactive({
		categoryId: null,
		category: '冰箱',
		brand: '',
		model: '',
		modelId: null,
		minPrice: 0,
		maxPrice: 0,
		damage: '',
		condition: '',
		age: '',
		details: {}
	});

	const applianceCategories = computed(() => specPage.value.categories);
	const selectedCategory = computed(() => spec.category);
	const currentConfig = computed(() => specPage.value.config || {});
	const brandOptions = computed(() => (currentConfig.value.brands || []).map((item) => item.name));
	const modelOptions = computed(() => {
		const brand = (currentConfig.value.brands || []).find((item) => item.name === spec.brand);
		return brand?.models || [];
	});
	const selectedModel = computed(() =>
		modelOptions.value.find((item) => item.id != null && spec.modelId != null && `${item.id}` === `${spec.modelId}`) ||
		modelOptions.value.find((item) => item.name === spec.model) ||
		null
	);
	/** 仅展示当前型号在管理端绑定的规格；未配置则不展示规格与损伤/新旧/年限 */
	const modelFields = computed(() => resolveModelFields(selectedModel.value));
	const modelAssessOptions = computed(() => resolveModelAssessOptions(selectedModel.value));
	const hasModelSpecs = computed(() => hasModelBoundSpecs(selectedModel.value));
	const estimateRangeText = computed(() => `${formatCurrency(estimateData.value.low)} - ${formatCurrency(estimateData.value.high)}`);
	const estimateDesc = computed(() => '实际成交价以师傅上门复核为准。');
	const previewTags = computed(() => {
		const detailTags = hasModelSpecs.value ? flattenDetailValues(spec.details) : [];
		const tags = hasModelSpecs.value
			? [spec.brand, spec.model, ...detailTags, spec.condition, spec.damage, spec.age]
			: [spec.brand, spec.model];
		return tags.filter(Boolean).slice(0, 6);
	});

	const applyAssessDefaults = (model) => {
		const assess = resolveModelAssessOptions(model);
		spec.damage = assess.damageOptions[0] || '';
		spec.condition = assess.conditionOptions[1] || assess.conditionOptions[0] || '';
		spec.age = assess.ageOptions[1] || assess.ageOptions[0] || '';
	};

	const clearAssessFields = () => {
		spec.damage = '';
		spec.condition = '';
		spec.age = '';
	};

	const replaceSpec = (nextSpec) => {
		Object.assign(spec, nextSpec, {
			details: {
				...nextSpec.details
			}
		});
	};

	const refreshEstimate = async () => {
		estimateData.value = await fetchApplianceEstimatePrice(spec, specPage.value);
	};

	const loadCategoryPage = async (categoryName, rawSpec = null) => {
		categoryLoading.value = true;
		specLoading.value = false;
		suppressEstimateWatch.value = true;
		try {
			specPage.value = await loadApplianceSpecPage(categoryName);
			const normalized = rawSpec
				? normalizeSpecFromPage(rawSpec, specPage.value, categoryName)
				: normalizeSpecFromPage(spec, specPage.value, categoryName);
			replaceSpec(normalized);
			await refreshEstimate();
		} catch (error) {
			uni.showToast({ title: error.message || '估价参数加载失败', icon: 'none' });
		} finally {
			suppressEstimateWatch.value = false;
			categoryLoading.value = false;
		}
	};

	const selectBrand = async (brand) => {
		if (!brand || brand === spec.brand || categoryLoading.value || specLoading.value) {
			return;
		}
		specLoading.value = true;
		suppressEstimateWatch.value = true;
		try {
			spec.brand = brand;
			spec.model = '';
			spec.modelId = null;
			spec.details = {};
			const firstModel = modelOptions.value[0];
			if (firstModel) {
				selectModel(firstModel);
			} else {
				clearAssessFields();
				estimateData.value = {
					estimate: 0,
					reward: 0,
					low: 0,
					high: 0
				};
			}
			if (spec.modelId) {
				await refreshEstimate();
			}
		} finally {
			suppressEstimateWatch.value = false;
			specLoading.value = false;
		}
	};

	const selectModel = (model) => {
		spec.model = model.name;
		spec.modelId = model.id;
		spec.minPrice = Number(model.minPrice ?? model.basePrice ?? 0);
		spec.maxPrice = Number(model.maxPrice ?? model.basePrice ?? 0);
		const fields = resolveModelFields(model);
		const details = {};
		fields.forEach((field) => {
			details[field.key] = resolveDetailValue(field, spec.details?.[field.key], model.specs?.[field.key]);
		});
		spec.details = details;
		if (!hasModelBoundSpecs(model)) {
			clearAssessFields();
		} else if (!spec.damage && !spec.condition && !spec.age) {
			applyAssessDefaults(model);
		} else {
			const assess = resolveModelAssessOptions(model);
			if (!assess.damageOptions.includes(spec.damage)) {
				spec.damage = assess.damageOptions[0] || '';
			}
			if (!assess.conditionOptions.includes(spec.condition)) {
				spec.condition = assess.conditionOptions[1] || assess.conditionOptions[0] || '';
			}
			if (!assess.ageOptions.includes(spec.age)) {
				spec.age = assess.ageOptions[1] || assess.ageOptions[0] || '';
			}
		}
	};

	onLoad((options) => {
		const categoryName = decodeURIComponent(options?.category || '冰箱');
		pendingSpec.value = options?.spec ? parseSpecFromQuery(options.spec, categoryName) : null;
		loadCategoryPage(categoryName, pendingSpec.value);
	});

	watch(
		() => spec.modelId,
		() => {
			if (suppressEstimateWatch.value || categoryLoading.value || specLoading.value) {
				return;
			}
			if (spec.modelId) {
				refreshEstimate();
			}
		}
	);

	const changeCategory = async (category) => {
		if (category === spec.category || categoryLoading.value || specLoading.value) {
			return;
		}
		await loadCategoryPage(category);
	};

	const updateDetail = (field, option) => {
		if (!field?.key) {
			return;
		}
		const nextValue = toggleDetailOption(field, spec.details?.[field.key], option);
		if (isDetailOptionSelected(spec.details?.[field.key], option) && !isMultiSelectField(field)) {
			return;
		}
		spec.details = {
			...spec.details,
			[field.key]: nextValue
		};
		if (!suppressEstimateWatch.value && spec.modelId) {
			refreshEstimate();
		}
	};

	const selectDamage = (option) => {
		if (spec.damage === option) {
			return;
		}
		spec.damage = option;
		if (!suppressEstimateWatch.value && spec.modelId) {
			refreshEstimate();
		}
	};

	const selectCondition = (option) => {
		if (spec.condition === option) {
			return;
		}
		spec.condition = option;
		if (!suppressEstimateWatch.value && spec.modelId) {
			refreshEstimate();
		}
	};

	const selectAge = (option) => {
		if (spec.age === option) {
			return;
		}
		spec.age = option;
		if (!suppressEstimateWatch.value && spec.modelId) {
			refreshEstimate();
		}
	};

	const submitSpec = () => {
		if (!spec.modelId) {
			uni.showToast({ title: '请选择商品型号', icon: 'none' });
			return;
		}
		if (!estimateData.value.estimate) {
			uni.showToast({ title: '预估价格加载中，请稍候', icon: 'none' });
			return;
		}
		const savedSpec = {
			...spec,
			categoryId: spec.categoryId,
			modelId: spec.modelId,
			details: { ...spec.details }
		};
		const savedEstimate = { ...estimateData.value };
		saveApplianceSettlementDraft({
			spec: savedSpec,
			estimate: savedEstimate
		});
		const query = encodeSpecToQuery(savedSpec);
		uni.redirectTo({
			url: `/packageRecycle/appliance/settlement?spec=${query}&category=${encodeURIComponent(spec.category || '冰箱')}`
		});
	};
</script>

<style lang="scss">
	.appliance-spec-page {
		background: linear-gradient(180deg, #fff8f3 0%, #fff 320rpx);
	}

	.appliance-spec-body {
		padding-top: 18rpx;
		padding-bottom: calc(190rpx + env(safe-area-inset-bottom));
	}

	.spec-hero-card,
	.spec-section-card,
	.spec-preview-card {
		background: #fff;
		border-radius: 28rpx;
		box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.07);
	}

	.spec-hero-card {
		position: relative;
		padding: 30rpx 28rpx;
		overflow: hidden;
		background: linear-gradient(135deg, #fff7f2 0%, #fff1ef 48%, #fff8dd 100%);
	}

	.spec-hero-card__label {
		display: inline-flex;
		align-items: center;
		height: 44rpx;
		padding: 0 18rpx;
		border-radius: 999rpx;
		background: rgba(255, 255, 255, 0.68);
		font-size: 22rpx;
		font-weight: 700;
		color: #b97900;
	}

	.spec-hero-card__title {
		margin-top: 18rpx;
		font-size: 42rpx;
		font-weight: 800;
		color: #2f2424;
	}

	.spec-hero-card__desc {
		margin-top: 12rpx;
		max-width: 470rpx;
		font-size: 24rpx;
		line-height: 1.7;
		color: #7d6969;
	}

	.spec-hero-card__icon {
		position: absolute;
		right: 28rpx;
		top: 32rpx;
		width: 92rpx;
		height: 92rpx;
		border-radius: 28rpx;
		background: rgba(255, 255, 255, 0.72);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.spec-tabs {
		display: flex;
		gap: 16rpx;
		margin-top: 24rpx;
		overflow-x: auto;
		white-space: nowrap;
	}

	.spec-tabs__item {
		min-width: 120rpx;
		height: 62rpx;
		padding: 0 24rpx;
		border-radius: 999rpx;
		background: #f3ecec;
		font-size: 26rpx;
		color: #6f5b5b;
		display: flex;
		align-items: center;
		justify-content: center;
		box-sizing: border-box;
		flex-shrink: 0;
	}

	.spec-tabs__item.is-active {
		background: linear-gradient(180deg, #cf1432 0%, #b90d2a 100%);
		color: #fff;
		box-shadow: 0 12rpx 24rpx rgba(207, 20, 50, 0.18);
	}

	.spec-tabs__item.is-disabled,
	.spec-chip.is-disabled {
		opacity: 0.55;
		pointer-events: none;
	}

	.spec-section-loading {
		margin-top: 22rpx;
		padding: 80rpx 24rpx;
		border-radius: 28rpx;
		background: #fff;
		box-shadow: 0 14rpx 34rpx rgba(111, 79, 79, 0.07);
		text-align: center;
		font-size: 26rpx;
		color: #948383;
	}

	.spec-section-card {
		margin-top: 22rpx;
		padding: 26rpx 24rpx;
	}

	.spec-section-card__head {
		align-items: baseline;
	}

	.spec-section-card__title {
		font-size: 30rpx;
		font-weight: 800;
		color: #2f2424;
	}

	.spec-section-card__hint {
		font-size: 22rpx;
		color: #a48f8f;
	}

	.spec-chip-list {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
		margin-top: 20rpx;
	}

	.spec-chip-list--column {
		flex-direction: column;
		flex-wrap: nowrap;
	}

	.spec-chip {
		min-width: 140rpx;
		padding: 0 24rpx;
		height: 68rpx;
		border-radius: 22rpx;
		background: #f8f3f3;
		border: 2rpx solid #f1e4e4;
		font-size: 24rpx;
		color: #604c4c;
		display: flex;
		align-items: center;
		justify-content: center;
		box-sizing: border-box;
	}

	.spec-chip.is-active {
		background: #fff3f5;
		border-color: #cf1432;
		color: #cf1432;
		font-weight: 700;
	}

	.spec-chip--full {
		width: 100%;
	}

	.spec-model-list {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
		margin-top: 20rpx;
	}

	.spec-model-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16rpx;
		padding: 24rpx 22rpx;
		border-radius: 24rpx;
		background: #fbf7f7;
		border: 2rpx solid transparent;
	}

	.spec-model-item.is-active {
		background: #fff5f7;
		border-color: #f4c8cf;
	}

	.spec-model-item__name {
		font-size: 28rpx;
		font-weight: 700;
		color: #2f2424;
	}

	.spec-stack-group {
		display: flex;
		flex-direction: column;
		margin-top: 22rpx;
	}

	.spec-stack-group .spec-section-card {
		margin-top: 0;
	}

	.spec-stack-group .spec-section-card + .spec-section-card {
		margin-top: 22rpx;
	}

	.spec-preview-card {
		margin-top: 24rpx;
		padding: 28rpx 24rpx;
		background: linear-gradient(135deg, #fff7ef 0%, #fff2f5 100%);
	}

	.spec-preview-card__title {
		font-size: 26rpx;
		color: #7c6767;
	}

	.spec-preview-card__amount {
		margin-top: 14rpx;
		font-size: 42rpx;
		font-weight: 800;
		color: #cf1432;
	}

	.spec-preview-card__desc {
		margin-top: 10rpx;
		font-size: 24rpx;
		line-height: 1.7;
		color: #7c6767;
	}

	.spec-preview-card__tags {
		display: flex;
		flex-wrap: wrap;
		gap: 12rpx;
		margin-top: 18rpx;
	}

	.spec-preview-card__tag {
		padding: 10rpx 16rpx;
		border-radius: 999rpx;
		background: rgba(255, 255, 255, 0.82);
		font-size: 22rpx;
		color: #725d5d;
	}

	.appliance-spec-footer {
		align-items: center;
		justify-content: stretch;
	}

	.appliance-spec-footer__btn {
		flex: 1;
		width: 100%;
		min-width: 0;
	}
</style>
