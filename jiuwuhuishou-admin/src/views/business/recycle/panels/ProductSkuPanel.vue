<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import {
  deleteProductBrand,
  deleteProductModel,
  deleteProductSpec,
  getProductBrandInfo,
  getProductModelInfo,
  getProductSpecInfo,
  loadProductSpecTemplate,
  pageProductBrand,
  pageProductModel,
  pageProductSpec,
  productCategoryApi,
  saveProductBrand,
  saveProductModel,
  saveProductSpec,
  updateProductBrand,
  updateProductModel,
  updateProductSpec,
} from '@/api/modules/product-category'
import { recycleCategoryApi } from '@/api/modules/recycle-category'
import { validateForm } from '@/utils/form'
import { confirmAction, showError, showSuccess } from '@/utils/modal'
import { resolveFileUrl } from '@/utils/display'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const props = defineProps({
  recycleCode: { type: String, default: '' },
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const recycleOptions = ref([])
const lockedRecycleCategoryId = ref(null)

const filters = reactive({ recycleCategoryId: '', name: '' })
const recycleTypeLocked = computed(() => Boolean(props.recycleCode))
/** 家电用估值区间；数码/奢侈品用单个预估价 */
const usePriceRange = computed(() => {
  if (props.recycleCode === 'appliance') {
    return true
  }
  if (props.recycleCode === 'digital' || props.recycleCode === 'luxury') {
    return false
  }
  const recycleId = currentCategory.value?.recycleCategoryId
  const matched = recycleOptions.value.find((item) => `${item.id}` === `${recycleId}`)
  return matched?.code === 'appliance'
})
const modelRules = computed(() => {
  const rules = {
    name: [{ required: true, message: '请输入型号名称', trigger: 'blur' }],
  }
  if (usePriceRange.value) {
    rules.minPrice = [{ required: true, message: '请输入最低估值', trigger: 'change' }]
    rules.maxPrice = [{ required: true, message: '请输入最高估值', trigger: 'change' }]
  } else {
    rules.basePrice = [{ required: true, message: '请输入预估价', trigger: 'change' }]
  }
  return rules
})

const categoryDialogVisible = ref(false)
const categoryDialogLoading = ref(false)
const categoryFormRef = ref()
const categoryForm = reactive({ id: null, recycleCategoryId: null, name: '', image: '', sort: 0, state: 1 })
const categoryRules = {
  recycleCategoryId: [{ required: true, message: '请选择回收类型', trigger: 'change' }],
  name: [{ required: true, message: '请输入品类名称', trigger: 'blur' }],
}

const brandDialogVisible = ref(false)
const brandLoading = ref(false)
const brandTableData = ref([])
const brandTotal = ref(0)
const brandPageNum = ref(1)
const brandPageSize = ref(10)
const currentCategory = ref(null)

const brandFormDialogVisible = ref(false)
const brandFormLoading = ref(false)
const brandFormRef = ref()
const brandForm = reactive({ id: null, categoryId: null, name: '', image: '', sort: 0, state: 1 })
const brandRules = {
  name: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }],
}

const modelDialogVisible = ref(false)
const modelLoading = ref(false)
const modelTableData = ref([])
const modelTotal = ref(0)
const modelPageNum = ref(1)
const modelPageSize = ref(10)
const currentBrand = ref(null)

const modelFormDialogVisible = ref(false)
const modelFormLoading = ref(false)
const modelFormRef = ref()
const modelForm = reactive({
  id: null,
  brandId: null,
  name: '',
  image: '',
  basePrice: 0,
  minPrice: 0,
  maxPrice: 0,
  specJson: '',
  sort: 0,
  state: 1,
})

const specDialogVisible = ref(false)
const specLoading = ref(false)
const specTableData = ref([])
const specTotal = ref(0)
const specPageNum = ref(1)
const specPageSize = ref(10)
const currentModel = ref(null)

const specFormDialogVisible = ref(false)
const specFormLoading = ref(false)
const specFormRef = ref()
const specForm = reactive({
  id: null,
  modelId: null,
  attributeName: '',
  multiSelect: 0,
  sort: 0,
  attributes: [],
})
const specRules = {
  attributeName: [{ required: true, message: '请输入规格名称', trigger: 'blur' }],
}
const templateSections = ref([])
const templateConfigKey = ref('')

async function loadRecycleOptions() {
  const data = await recycleCategoryApi.page({ page: 1, limit: 200, state: 1 })
  recycleOptions.value = data.list || []
  if (props.recycleCode) {
    const matched = recycleOptions.value.find((item) => item.code === props.recycleCode)
    lockedRecycleCategoryId.value = matched?.id || null
    filters.recycleCategoryId = lockedRecycleCategoryId.value || ''
  }
}

function buildCategoryQuery() {
  return {
    recycleCategoryId: filters.recycleCategoryId || undefined,
    name: filters.name || undefined,
  }
}

async function loadData() {
  loading.value = true
  try {
    const data = await productCategoryApi.page({
      page: pageNum.value,
      limit: pageSize.value,
      ...buildCategoryQuery(),
    })
    tableData.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleReset() {
  Object.assign(filters, {
    recycleCategoryId: recycleTypeLocked.value ? (lockedRecycleCategoryId.value || '') : '',
    name: '',
  })
  pageNum.value = 1
  loadData()
}

function openCreateCategory() {
  Object.assign(categoryForm, {
    id: null,
    recycleCategoryId: lockedRecycleCategoryId.value || null,
    name: '',
    image: '',
    sort: 0,
    state: 1,
  })
  categoryDialogVisible.value = true
}

async function openEditCategory(row) {
  const detail = await productCategoryApi.info(row.id)
  Object.assign(categoryForm, detail)
  categoryDialogVisible.value = true
}

async function submitCategory() {
  const valid = await validateForm(categoryFormRef)
  if (!valid) return
  categoryDialogLoading.value = true
  try {
    if (categoryForm.id) {
      await productCategoryApi.update({ ...categoryForm })
      showSuccess('更新成功')
    } else {
      await productCategoryApi.save({ ...categoryForm })
      showSuccess('创建成功')
    }
    categoryDialogVisible.value = false
    await loadData()
  } finally {
    categoryDialogLoading.value = false
  }
}

async function removeCategory(row) {
  await confirmAction(`确认删除品类「${row.name}」吗？`)
  await productCategoryApi.delete({ id: row.id })
  showSuccess('删除成功')
  await loadData()
}

function openBrandManage(row) {
  currentCategory.value = row
  brandPageNum.value = 1
  brandDialogVisible.value = true
  loadBrandData()
}

async function loadBrandData() {
  if (!currentCategory.value) return
  brandLoading.value = true
  try {
    const data = await pageProductBrand({
      page: brandPageNum.value,
      limit: brandPageSize.value,
      categoryId: currentCategory.value.id,
    })
    brandTableData.value = data.list
    brandTotal.value = data.total
  } finally {
    brandLoading.value = false
  }
}

function openCreateBrand() {
  Object.assign(brandForm, { id: null, categoryId: currentCategory.value?.id, name: '', image: '', sort: 0, state: 1 })
  brandFormDialogVisible.value = true
}

async function openEditBrand(row) {
  const detail = await getProductBrandInfo(row.id)
  Object.assign(brandForm, detail)
  brandFormDialogVisible.value = true
}

async function submitBrand() {
  const valid = await validateForm(brandFormRef)
  if (!valid) return
  brandFormLoading.value = true
  try {
    if (brandForm.id) {
      await updateProductBrand({ ...brandForm })
      showSuccess('品牌更新成功')
    } else {
      await saveProductBrand({ ...brandForm })
      showSuccess('品牌创建成功')
    }
    brandFormDialogVisible.value = false
    await loadBrandData()
  } finally {
    brandFormLoading.value = false
  }
}

async function removeBrand(row) {
  await confirmAction(`确认删除品牌「${row.name}」吗？`)
  await deleteProductBrand({ id: row.id })
  showSuccess('删除成功')
  await loadBrandData()
}

function openModelManage(row) {
  currentBrand.value = row
  modelPageNum.value = 1
  modelDialogVisible.value = true
  loadModelData()
}

async function loadModelData() {
  if (!currentBrand.value) return
  modelLoading.value = true
  try {
    const data = await pageProductModel({
      page: modelPageNum.value,
      limit: modelPageSize.value,
      brandId: currentBrand.value.id,
    })
    modelTableData.value = data.list
    modelTotal.value = data.total
  } finally {
    modelLoading.value = false
  }
}

function openCreateModel() {
  Object.assign(modelForm, {
    id: null,
    brandId: currentBrand.value?.id,
    name: '',
    image: '',
    basePrice: 0,
    minPrice: 0,
    maxPrice: 0,
    specJson: '',
    sort: 0,
    state: 1,
  })
  modelFormDialogVisible.value = true
}

async function openEditModel(row) {
  const detail = await getProductModelInfo(row.id)
  const basePrice = Number(detail.basePrice ?? 0)
  Object.assign(modelForm, {
    ...detail,
    basePrice,
    minPrice: Number(detail.minPrice ?? basePrice),
    maxPrice: Number(detail.maxPrice ?? basePrice),
  })
  modelFormDialogVisible.value = true
}

async function submitModel() {
  const valid = await validateForm(modelFormRef)
  if (!valid) return
  let payload
  if (usePriceRange.value) {
    const minPrice = Number(modelForm.minPrice ?? 0)
    const maxPrice = Number(modelForm.maxPrice ?? 0)
    if (maxPrice < minPrice) {
      showError('最高估值不能低于最低估值')
      return
    }
    payload = {
      ...modelForm,
      minPrice,
      maxPrice,
      basePrice: Number(((minPrice + maxPrice) / 2).toFixed(2)),
    }
  } else {
    const basePrice = Number(modelForm.basePrice ?? 0)
    payload = {
      ...modelForm,
      basePrice,
      minPrice: basePrice,
      maxPrice: basePrice,
    }
  }
  modelFormLoading.value = true
  try {
    if (modelForm.id) {
      await updateProductModel(payload)
      showSuccess('型号更新成功')
    } else {
      await saveProductModel(payload)
      showSuccess('型号创建成功')
    }
    modelFormDialogVisible.value = false
    await loadModelData()
  } finally {
    modelFormLoading.value = false
  }
}

async function removeModel(row) {
  await confirmAction(`确认删除型号「${row.name}」吗？`)
  await deleteProductModel({ id: row.id })
  showSuccess('删除成功')
  await loadModelData()
}

function openSpecManage(row) {
  currentModel.value = row
  specPageNum.value = 1
  specDialogVisible.value = true
  loadSpecData()
}

async function loadSpecData() {
  if (!currentModel.value) return
  specLoading.value = true
  try {
    const data = await pageProductSpec({
      page: specPageNum.value,
      limit: specPageSize.value,
      modelId: currentModel.value.id,
    })
    specTableData.value = data.list
    specTotal.value = data.total
  } finally {
    specLoading.value = false
  }
}

function createAttributeRow() {
  return { id: '', attName: '', attPrice: '0', img: '' }
}

async function openCreateSpec() {
  Object.assign(specForm, {
    id: null,
    modelId: currentModel.value?.id,
    attributeName: '',
    multiSelect: 0,
    sort: 0,
    attributes: [createAttributeRow()],
  })
  templateSections.value = []
  templateConfigKey.value = ''
  if (currentModel.value?.id) {
    try {
      const template = await loadProductSpecTemplate(currentModel.value.id)
      templateConfigKey.value = template?.configKey || ''
      templateSections.value = template?.sections || []
    } catch (_) {
      templateSections.value = []
    }
  }
  specFormDialogVisible.value = true
}

async function openEditSpec(row) {
  const detail = await getProductSpecInfo(row.id)
  Object.assign(specForm, {
    ...detail,
    attributes: (detail.attributes || []).length ? detail.attributes : [createAttributeRow()],
  })
  templateSections.value = []
  templateConfigKey.value = ''
  if (currentModel.value?.id) {
    try {
      const template = await loadProductSpecTemplate(currentModel.value.id)
      templateConfigKey.value = template?.configKey || ''
      templateSections.value = template?.sections || []
    } catch (_) {
      templateSections.value = []
    }
  }
  specFormDialogVisible.value = true
}

function applyTemplateSection(section) {
  if (!section) return
  specForm.attributeName = section.label || section.key || ''
  specForm.attributes = (section.options || []).map((name) => ({
    id: '',
    attName: name,
    attPrice: '0',
    img: '',
  }))
  if (!specForm.attributes.length) {
    specForm.attributes = [createAttributeRow()]
  }
}

function addAttributeRow() {
  specForm.attributes.push(createAttributeRow())
}

function removeAttributeRow(index) {
  specForm.attributes.splice(index, 1)
}

async function submitSpec() {
  const valid = await validateForm(specFormRef)
  if (!valid) return
  specFormLoading.value = true
  try {
    const payload = {
      ...specForm,
      attributes: specForm.attributes.filter((item) => item.attName),
    }
    if (specForm.id) {
      await updateProductSpec(payload)
      showSuccess('规格更新成功')
    } else {
      await saveProductSpec(payload)
      showSuccess('规格创建成功')
    }
    specFormDialogVisible.value = false
    await loadSpecData()
  } finally {
    specFormLoading.value = false
  }
}

async function removeSpec(row) {
  await confirmAction(`确认删除规格「${row.attributeName}」吗？`)
  await deleteProductSpec({ id: row.id })
  showSuccess('删除成功')
  await loadSpecData()
}

onMounted(async () => {
  await loadRecycleOptions()
  await loadData()
})
</script>

<template>
  <div>
    <div class="card-header">
      <el-button v-if="isAuth(PERMS.productCategoryAdd)" type="primary" @click="openCreateCategory">新增品类</el-button>
    </div>

    <el-form inline @submit.prevent="handleSearch">
      <el-form-item v-if="!recycleTypeLocked" label="回收类型">
        <el-select v-model="filters.recycleCategoryId" clearable placeholder="全部" style="width: 160px">
          <el-option v-for="item in recycleOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="品类名称"><el-input v-model="filters.name" clearable placeholder="品类名称" /></el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column label="回收类型" min-width="140">
        <template #default="{ row }">{{ row.recycleCategoryName || '—' }}</template>
      </el-table-column>
      <el-table-column prop="name" label="品类名称" min-width="140" />
      <el-table-column label="图片" width="90" align="center">
        <template #default="{ row }">
          <el-image v-if="row.image" :src="resolveFileUrl(row.image)" style="width: 40px; height: 40px" fit="cover" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <el-table-column label="操作" min-width="220" fixed="right">
        <template #default="{ row }">
          <el-space wrap>
            <el-button link type="primary" @click="openBrandManage(row)">品牌管理</el-button>
            <el-button v-if="isAuth(PERMS.productCategoryUpdate)" link type="primary" @click="openEditCategory(row)">编辑</el-button>
            <el-button v-if="isAuth(PERMS.productCategoryDelete)" link type="danger" @click="removeCategory(row)">删除</el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>

    <BasePagination
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @update:current-page="pageNum = $event; loadData()"
      @update:page-size="pageSize = $event; pageNum = 1; loadData()"
    />

    <AppDialog v-model="categoryDialogVisible" :title="categoryForm.id ? '编辑品类' : '新增品类'" :loading="categoryDialogLoading" width="560px" @confirm="submitCategory">
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="96px">
        <el-form-item label="回收类型" prop="recycleCategoryId">
          <el-select
            v-model="categoryForm.recycleCategoryId"
            placeholder="请选择回收类型"
            style="width: 100%"
            :disabled="recycleTypeLocked"
          >
            <el-option v-for="item in recycleOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="品类名称" prop="name"><el-input v-model="categoryForm.name" placeholder="如：空调、华为手机、LV" /></el-form-item>
        <el-form-item label="图片"><ImageUpload v-model="categoryForm.image" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="categoryForm.sort" :min="0" /></el-form-item>
      </el-form>
    </AppDialog>

    <el-dialog v-model="brandDialogVisible" :title="`品牌管理 - ${currentCategory?.name || ''}`" width="920px" destroy-on-close>
      <div class="dialog-toolbar">
        <el-button v-if="isAuth(PERMS.productCategoryAdd)" type="primary" @click="openCreateBrand">添加品牌</el-button>
        <el-button @click="loadBrandData">刷新</el-button>
      </div>
      <el-table v-loading="brandLoading" :data="brandTableData" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ (brandPageNum - 1) * brandPageSize + $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="name" label="品牌名称" min-width="140" />
        <el-table-column label="图片" width="90" align="center">
          <template #default="{ row }">
            <el-image v-if="row.image" :src="resolveFileUrl(row.image)" style="width: 40px; height: 40px" fit="cover" />
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="openModelManage(row)">型号管理</el-button>
              <el-button v-if="isAuth(PERMS.productCategoryUpdate)" link type="primary" @click="openEditBrand(row)">修改</el-button>
              <el-button v-if="isAuth(PERMS.productCategoryDelete)" link type="danger" @click="removeBrand(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
      <BasePagination
        :current-page="brandPageNum"
        :page-size="brandPageSize"
        :total="brandTotal"
        @update:current-page="brandPageNum = $event; loadBrandData()"
        @update:page-size="brandPageSize = $event; brandPageNum = 1; loadBrandData()"
      />
    </el-dialog>

    <AppDialog v-model="brandFormDialogVisible" :title="brandForm.id ? '修改品牌' : '添加品牌'" :loading="brandFormLoading" width="520px" @confirm="submitBrand">
      <el-form ref="brandFormRef" :model="brandForm" :rules="brandRules" label-width="96px">
        <el-form-item label="品牌名称" prop="name"><el-input v-model="brandForm.name" /></el-form-item>
        <el-form-item label="图片"><ImageUpload v-model="brandForm.image" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="brandForm.sort" :min="0" /></el-form-item>
      </el-form>
    </AppDialog>

    <el-dialog v-model="modelDialogVisible" :title="`型号管理 - ${currentBrand?.name || ''}`" width="980px" destroy-on-close append-to-body>
      <div class="dialog-toolbar">
        <el-button v-if="isAuth(PERMS.productCategoryAdd)" type="primary" @click="openCreateModel">添加型号</el-button>
        <el-button @click="loadModelData">刷新</el-button>
      </div>
      <el-table v-loading="modelLoading" :data="modelTableData" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ (modelPageNum - 1) * modelPageSize + $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="name" label="型号名称" min-width="160" />
        <el-table-column v-if="usePriceRange" label="估值区间" width="180">
          <template #default="{ row }">
            {{ Number(row.minPrice ?? row.basePrice ?? 0).toFixed(2) }}
            ~
            {{ Number(row.maxPrice ?? row.basePrice ?? 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column v-else prop="basePrice" label="预估价" width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" min-width="240" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="openSpecManage(row)">规格估值</el-button>
              <el-button v-if="isAuth(PERMS.productCategoryUpdate)" link type="primary" @click="openEditModel(row)">修改</el-button>
              <el-button v-if="isAuth(PERMS.productCategoryDelete)" link type="danger" @click="removeModel(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
      <BasePagination
        :current-page="modelPageNum"
        :page-size="modelPageSize"
        :total="modelTotal"
        @update:current-page="modelPageNum = $event; loadModelData()"
        @update:page-size="modelPageSize = $event; modelPageNum = 1; loadModelData()"
      />
    </el-dialog>

    <AppDialog v-model="modelFormDialogVisible" :title="modelForm.id ? '修改型号' : '添加型号'" :loading="modelFormLoading" width="560px" append-to-body @confirm="submitModel">
      <el-form ref="modelFormRef" :model="modelForm" :rules="modelRules" label-width="96px">
        <el-form-item label="型号名称" prop="name"><el-input v-model="modelForm.name" /></el-form-item>
        <template v-if="usePriceRange">
          <el-form-item label="最低估值" prop="minPrice">
            <el-input-number v-model="modelForm.minPrice" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="最高估值" prop="maxPrice">
            <el-input-number v-model="modelForm.maxPrice" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </template>
        <el-form-item v-else label="预估价" prop="basePrice">
          <el-input-number v-model="modelForm.basePrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="图片"><ImageUpload v-model="modelForm.image" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="modelForm.sort" :min="0" /></el-form-item>
      </el-form>
    </AppDialog>

    <el-dialog v-model="specDialogVisible" :title="`规格估值 - ${currentModel?.name || ''}`" width="980px" destroy-on-close append-to-body>
      <div class="dialog-toolbar">
        <el-button v-if="isAuth(PERMS.productCategoryAdd)" type="primary" @click="openCreateSpec">添加</el-button>
        <el-button @click="loadSpecData">刷新</el-button>
      </div>
      <el-table v-loading="specLoading" :data="specTableData" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ (specPageNum - 1) * specPageSize + $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="attributeName" label="规格名称" min-width="120" />
        <el-table-column label="规格属性值" min-width="280">
          <template #default="{ row }">
            <div v-if="row.attributes?.length" class="attr-list">
              <div v-for="item in row.attributes" :key="item.id || item.attName" class="attr-item">
                <span>{{ item.attName }}</span>
                <span class="attr-price">估值 {{ item.attPrice || '0' }}</span>
              </div>
            </div>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="是否多选" width="90" align="center">
          <template #default="{ row }">{{ row.multiSelect === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-space>
              <el-button v-if="isAuth(PERMS.productCategoryUpdate)" link type="primary" @click="openEditSpec(row)">修改</el-button>
              <el-button v-if="isAuth(PERMS.productCategoryDelete)" link type="danger" @click="removeSpec(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
      <BasePagination
        :current-page="specPageNum"
        :page-size="specPageSize"
        :total="specTotal"
        @update:current-page="specPageNum = $event; loadSpecData()"
        @update:page-size="specPageSize = $event; specPageNum = 1; loadSpecData()"
      />
    </el-dialog>

    <AppDialog v-model="specFormDialogVisible" :title="specForm.id ? '修改规格估值' : '添加规格估值'" :loading="specFormLoading" width="780px" append-to-body @confirm="submitSpec">
      <el-form ref="specFormRef" :model="specForm" :rules="specRules" label-width="96px">
        <el-form-item v-if="templateSections.length" label="文案模板">
          <div class="template-box">
            <div class="template-tip">参照文案配置{{ templateConfigKey ? `（${templateConfigKey}）` : '' }}导入选项名称，估值需按型号单独填写</div>
            <el-space wrap>
              <el-button
                v-for="section in templateSections"
                :key="section.key || section.label"
                size="small"
                @click="applyTemplateSection(section)"
              >
                {{ section.label || section.key }}
              </el-button>
            </el-space>
          </div>
        </el-form-item>
        <el-form-item label="规格名称" prop="attributeName"><el-input v-model="specForm.attributeName" placeholder="如：机身内存ROM / 成色" /></el-form-item>
        <el-form-item label="是否多选">
          <el-radio-group v-model="specForm.multiSelect">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="属性值">
          <div class="attribute-editor">
            <div v-for="(item, index) in specForm.attributes" :key="index" class="attribute-row">
              <el-input v-model="item.attName" placeholder="属性值，如 128G" />
              <el-input v-model="item.attPrice" placeholder="估值" style="width: 120px" />
              <el-button link type="danger" @click="removeAttributeRow(index)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addAttributeRow">+ 添加属性值</el-button>
          </div>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="specForm.sort" :min="0" /></el-form-item>
      </el-form>
    </AppDialog>
  </div>
</template>

<style scoped>
.card-header,
.dialog-toolbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  margin-bottom: 12px;
}

.dialog-toolbar {
  margin-bottom: 12px;
}

.attr-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.attr-item {
  display: flex;
  gap: 8px;
  font-size: 13px;
}

.attr-price {
  color: var(--el-color-primary);
}

.attribute-editor {
  width: 100%;
}

.attribute-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.template-box {
  width: 100%;
}

.template-tip {
  margin-bottom: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
