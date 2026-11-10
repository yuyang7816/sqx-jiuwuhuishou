<script setup>
import { reactive, ref, watch } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import { digitalBrandApi, digitalModelApi, digitalPricingApi } from '@/api/modules/digital-master'
import { enableStateMap, resolveFileUrl, tagMeta } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { confirmAction, showSuccess } from '@/utils/modal'

const props = defineProps({
  scene: { type: String, required: true },
  title: { type: String, default: '' },
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filters = reactive({ name: '', state: '' })

const dialogVisible = ref(false)
const dialogLoading = ref(false)
const formRef = ref()
const form = reactive({ id: null, name: '', logo: '', scene: props.scene, sort: 0, state: 1 })
const rules = { name: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }] }

const modelDialogVisible = ref(false)
const modelLoading = ref(false)
const modelTable = ref([])
const modelTotal = ref(0)
const modelPageNum = ref(1)
const modelPageSize = ref(10)
const currentBrand = ref(null)

const modelFormVisible = ref(false)
const modelFormLoading = ref(false)
const modelFormRef = ref()
const modelForm = reactive({ id: null, brandId: null, name: '', basePrice: null, specJson: '', state: 1 })
const modelRules = { name: [{ required: true, message: '请输入型号名称', trigger: 'blur' }] }

const pricingDialogVisible = ref(false)
const pricingLoading = ref(false)
const pricingTable = ref([])
const pricingTotal = ref(0)
const pricingPageNum = ref(1)
const pricingPageSize = ref(10)
const currentModel = ref(null)

const pricingFormVisible = ref(false)
const pricingFormLoading = ref(false)
const pricingFormRef = ref()
const pricingForm = reactive({ id: null, modelId: null, ruleKey: '', ruleValue: '', adjustRate: 1 })
const pricingRules = {
  ruleKey: [{ required: true, message: '请输入规则键', trigger: 'blur' }],
  ruleValue: [{ required: true, message: '请输入规则值', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const data = await digitalBrandApi.page({
      page: pageNum.value,
      limit: pageSize.value,
      scene: props.scene,
      name: filters.name || undefined,
      state: filters.state || undefined,
    })
    tableData.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

watch(() => props.scene, () => {
  pageNum.value = 1
  loadData()
})

function openCreate() {
  Object.assign(form, { id: null, name: '', logo: '', scene: props.scene, sort: 0, state: 1 })
  dialogVisible.value = true
}

async function openEdit(row) {
  const detail = await digitalBrandApi.info(row.id)
  Object.assign(form, detail)
  dialogVisible.value = true
}

async function submit() {
  const valid = await validateForm(formRef)
  if (!valid) return
  dialogLoading.value = true
  try {
    const payload = { ...form, scene: props.scene }
    if (form.id) {
      await digitalBrandApi.update(payload)
      showSuccess('品牌已更新')
    } else {
      await digitalBrandApi.save(payload)
      showSuccess('品牌已创建')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    dialogLoading.value = false
  }
}

async function removeRow(row) {
  await confirmAction(`确认删除品牌「${row.name}」吗？`)
  await digitalBrandApi.delete({ id: row.id })
  showSuccess('已删除')
  await loadData()
}

function openModels(row) {
  currentBrand.value = row
  modelPageNum.value = 1
  modelDialogVisible.value = true
  loadModels()
}

async function loadModels() {
  if (!currentBrand.value) return
  modelLoading.value = true
  try {
    const data = await digitalModelApi.page({
      page: modelPageNum.value,
      limit: modelPageSize.value,
      brandId: currentBrand.value.id,
    })
    modelTable.value = data.list
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
    basePrice: null,
    specJson: '',
    state: 1,
  })
  modelFormVisible.value = true
}

async function openEditModel(row) {
  const detail = await digitalModelApi.info(row.id)
  Object.assign(modelForm, detail)
  modelFormVisible.value = true
}

async function submitModel() {
  const valid = await validateForm(modelFormRef)
  if (!valid) return
  modelFormLoading.value = true
  try {
    if (modelForm.id) {
      await digitalModelApi.update({ ...modelForm })
      showSuccess('型号已更新')
    } else {
      await digitalModelApi.save({ ...modelForm })
      showSuccess('型号已创建')
    }
    modelFormVisible.value = false
    await loadModels()
  } finally {
    modelFormLoading.value = false
  }
}

async function removeModel(row) {
  await confirmAction(`确认删除型号「${row.name}」吗？关联估价规则会一并删除。`)
  await digitalModelApi.delete({ id: row.id })
  showSuccess('已删除')
  await loadModels()
}

function openPricing(row) {
  currentModel.value = row
  pricingPageNum.value = 1
  pricingDialogVisible.value = true
  loadPricing()
}

async function loadPricing() {
  if (!currentModel.value) return
  pricingLoading.value = true
  try {
    const data = await digitalPricingApi.page({
      page: pricingPageNum.value,
      limit: pricingPageSize.value,
      modelId: currentModel.value.id,
    })
    pricingTable.value = data.list
    pricingTotal.value = data.total
  } finally {
    pricingLoading.value = false
  }
}

function openCreatePricing() {
  Object.assign(pricingForm, {
    id: null,
    modelId: currentModel.value?.id,
    ruleKey: 'condition',
    ruleValue: '',
    adjustRate: 1,
  })
  pricingFormVisible.value = true
}

async function openEditPricing(row) {
  const detail = await digitalPricingApi.info(row.id)
  Object.assign(pricingForm, detail)
  pricingFormVisible.value = true
}

async function submitPricing() {
  const valid = await validateForm(pricingFormRef)
  if (!valid) return
  pricingFormLoading.value = true
  try {
    if (pricingForm.id) {
      await digitalPricingApi.update({ ...pricingForm })
      showSuccess('规则已更新')
    } else {
      await digitalPricingApi.save({ ...pricingForm })
      showSuccess('规则已创建')
    }
    pricingFormVisible.value = false
    await loadPricing()
  } finally {
    pricingFormLoading.value = false
  }
}

async function removePricing(row) {
  await confirmAction('确认删除该估价规则吗？')
  await digitalPricingApi.delete({ id: row.id })
  showSuccess('已删除')
  await loadPricing()
}

loadData()
</script>

<template>
  <div>
    <el-alert
      class="hint"
      type="info"
      :closable="false"
      :title="`对应用户端 scene=${scene}（${title || scene}）。品牌 → 型号 → 估价规则，与 /app/recycle/digital 一致。`"
      show-icon
    />
    <div class="toolbar">
      <el-form inline @submit.prevent="pageNum = 1; loadData()">
        <el-form-item label="品牌"><el-input v-model="filters.name" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.state" clearable placeholder="全部" style="width: 110px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="pageNum = 1; loadData()">查询</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="openCreate">新品牌</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="name" label="品牌" min-width="120" />
      <el-table-column label="Logo" width="80" align="center">
        <template #default="{ row }">
          <el-image v-if="row.logo" :src="resolveFileUrl(row.logo)" style="width: 36px; height: 36px" fit="cover" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="tagMeta(enableStateMap, row.state).type">{{ tagMeta(enableStateMap, row.state).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="200" fixed="right">
        <template #default="{ row }">
          <el-space wrap>
            <el-button link type="primary" @click="openModels(row)">型号</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="removeRow(row)">删除</el-button>
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

    <AppDialog v-model="dialogVisible" :title="form.id ? '编辑品牌' : '新品牌'" :loading="dialogLoading" width="520px" @confirm="submit">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="品牌名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="Logo"><ImageUpload v-model="form.logo" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.state" style="width: 100%">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
    </AppDialog>

    <el-dialog v-model="modelDialogVisible" :title="`型号 - ${currentBrand?.name || ''}`" width="960px" destroy-on-close>
      <div class="dialog-toolbar">
        <el-button type="primary" @click="openCreateModel">新型号</el-button>
        <el-button @click="loadModels">刷新</el-button>
      </div>
      <el-table v-loading="modelLoading" :data="modelTable" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ (modelPageNum - 1) * modelPageSize + $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="name" label="型号" min-width="140" />
        <el-table-column prop="basePrice" label="基础估价" width="110" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="tagMeta(enableStateMap, row.state).type">{{ tagMeta(enableStateMap, row.state).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="openPricing(row)">估价规则</el-button>
              <el-button link type="primary" @click="openEditModel(row)">编辑</el-button>
              <el-button link type="danger" @click="removeModel(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
      <BasePagination
        :current-page="modelPageNum"
        :page-size="modelPageSize"
        :total="modelTotal"
        @update:current-page="modelPageNum = $event; loadModels()"
        @update:page-size="modelPageSize = $event; modelPageNum = 1; loadModels()"
      />
    </el-dialog>

    <AppDialog v-model="modelFormVisible" :title="modelForm.id ? '编辑型号' : '新型号'" :loading="modelFormLoading" width="560px" append-to-body @confirm="submitModel">
      <el-form ref="modelFormRef" :model="modelForm" :rules="modelRules" label-width="96px">
        <el-form-item label="型号名称" prop="name"><el-input v-model="modelForm.name" /></el-form-item>
        <el-form-item label="基础估价">
          <el-input-number v-model="modelForm.basePrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="规格JSON">
          <el-input v-model="modelForm.specJson" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="modelForm.state" style="width: 100%">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
    </AppDialog>

    <el-dialog v-model="pricingDialogVisible" :title="`估价规则 - ${currentModel?.name || ''}`" width="860px" destroy-on-close append-to-body>
      <div class="dialog-toolbar">
        <el-button type="primary" @click="openCreatePricing">新规则</el-button>
        <el-button @click="loadPricing">刷新</el-button>
      </div>
      <el-table v-loading="pricingLoading" :data="pricingTable" border>
        <el-table-column label="编号" width="70" align="center">
          <template #default="{ $index }">{{ (pricingPageNum - 1) * pricingPageSize + $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="ruleKey" label="规则键" min-width="120" />
        <el-table-column prop="ruleValue" label="规则值" min-width="120" />
        <el-table-column prop="adjustRate" label="调整比例" width="110" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-space>
              <el-button link type="primary" @click="openEditPricing(row)">编辑</el-button>
              <el-button link type="danger" @click="removePricing(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
      <BasePagination
        :current-page="pricingPageNum"
        :page-size="pricingPageSize"
        :total="pricingTotal"
        @update:current-page="pricingPageNum = $event; loadPricing()"
        @update:page-size="pricingPageSize = $event; pricingPageNum = 1; loadPricing()"
      />
    </el-dialog>

    <AppDialog v-model="pricingFormVisible" :title="pricingForm.id ? '编辑规则' : '新规则'" :loading="pricingFormLoading" width="520px" append-to-body @confirm="submitPricing">
      <el-form ref="pricingFormRef" :model="pricingForm" :rules="pricingRules" label-width="96px">
        <el-form-item label="规则键" prop="ruleKey"><el-input v-model="pricingForm.ruleKey" placeholder="如 condition" /></el-form-item>
        <el-form-item label="规则值" prop="ruleValue"><el-input v-model="pricingForm.ruleValue" placeholder="如 九成新" /></el-form-item>
        <el-form-item label="调整比例">
          <el-input-number v-model="pricingForm.adjustRate" :min="0" :precision="4" :step="0.05" style="width: 100%" />
        </el-form-item>
      </el-form>
    </AppDialog>
  </div>
</template>

<style scoped>
.hint { margin-bottom: 12px; }
.toolbar, .dialog-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
