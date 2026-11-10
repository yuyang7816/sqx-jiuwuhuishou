<script setup>
import { reactive, ref } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import { applianceCategoryApi, applianceModelApi } from '@/api/modules/appliance-master'
import { enableStateMap, resolveFileUrl, tagMeta } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { confirmAction, showSuccess } from '@/utils/modal'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filters = reactive({ name: '', state: '' })

const dialogVisible = ref(false)
const dialogLoading = ref(false)
const formRef = ref()
const form = reactive({ id: null, name: '', icon: '', displayTitle: '', sort: 0, state: 1 })
const rules = { name: [{ required: true, message: '请输入品类名称', trigger: 'blur' }] }

const modelDialogVisible = ref(false)
const modelLoading = ref(false)
const modelTable = ref([])
const modelTotal = ref(0)
const modelPageNum = ref(1)
const modelPageSize = ref(10)
const currentCategory = ref(null)

const modelFormVisible = ref(false)
const modelFormLoading = ref(false)
const modelFormRef = ref()
const modelForm = reactive({ id: null, categoryId: null, name: '', basePrice: null, specJson: '', state: 1 })
const modelRules = { name: [{ required: true, message: '请输入型号名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const data = await applianceCategoryApi.page({
      page: pageNum.value,
      limit: pageSize.value,
      name: filters.name || undefined,
      state: filters.state || undefined,
    })
    tableData.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  Object.assign(form, { id: null, name: '', icon: '', displayTitle: '', sort: 0, state: 1 })
  dialogVisible.value = true
}

async function openEdit(row) {
  const detail = await applianceCategoryApi.info(row.id)
  Object.assign(form, detail)
  dialogVisible.value = true
}

async function submit() {
  const valid = await validateForm(formRef)
  if (!valid) return
  dialogLoading.value = true
  try {
    if (form.id) {
      await applianceCategoryApi.update({ ...form })
      showSuccess('品类已更新')
    } else {
      await applianceCategoryApi.save({ ...form })
      showSuccess('品类已创建')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    dialogLoading.value = false
  }
}

async function removeRow(row) {
  await confirmAction(`确认删除家电品类「${row.name}」吗？`)
  await applianceCategoryApi.delete({ id: row.id })
  showSuccess('已删除')
  await loadData()
}

function openModels(row) {
  currentCategory.value = row
  modelPageNum.value = 1
  modelDialogVisible.value = true
  loadModels()
}

async function loadModels() {
  if (!currentCategory.value) return
  modelLoading.value = true
  try {
    const data = await applianceModelApi.page({
      page: modelPageNum.value,
      limit: modelPageSize.value,
      categoryId: currentCategory.value.id,
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
    categoryId: currentCategory.value?.id,
    name: '',
    basePrice: null,
    specJson: '',
    state: 1,
  })
  modelFormVisible.value = true
}

async function openEditModel(row) {
  const detail = await applianceModelApi.info(row.id)
  Object.assign(modelForm, detail)
  modelFormVisible.value = true
}

async function submitModel() {
  const valid = await validateForm(modelFormRef)
  if (!valid) return
  modelFormLoading.value = true
  try {
    if (modelForm.id) {
      await applianceModelApi.update({ ...modelForm })
      showSuccess('型号已更新')
    } else {
      await applianceModelApi.save({ ...modelForm })
      showSuccess('型号已创建')
    }
    modelFormVisible.value = false
    await loadModels()
  } finally {
    modelFormLoading.value = false
  }
}

async function removeModel(row) {
  await confirmAction(`确认删除型号「${row.name}」吗？`)
  await applianceModelApi.delete({ id: row.id })
  showSuccess('已删除')
  await loadModels()
}

loadData()
</script>

<template>
  <div>
    <div class="toolbar">
      <el-form inline @submit.prevent="pageNum = 1; loadData()">
        <el-form-item label="品类"><el-input v-model="filters.name" clearable placeholder="名称" /></el-form-item>
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
      <el-button type="primary" @click="openCreate">新品类</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="name" label="品类名称" min-width="120" />
      <el-table-column label="图标" width="80" align="center">
        <template #default="{ row }">
          <el-image v-if="row.icon" :src="resolveFileUrl(row.icon)" style="width: 36px; height: 36px" fit="cover" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column prop="displayTitle" label="展示标题" min-width="120" />
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

    <AppDialog v-model="dialogVisible" :title="form.id ? '编辑家电品类' : '新建家电品类'" :loading="dialogLoading" width="560px" @confirm="submit">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="展示标题"><el-input v-model="form.displayTitle" placeholder="价格卡片标题" /></el-form-item>
        <el-form-item label="图标"><ImageUpload v-model="form.icon" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.state" style="width: 100%">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
    </AppDialog>

    <el-dialog v-model="modelDialogVisible" :title="`型号管理 - ${currentCategory?.name || ''}`" width="920px" destroy-on-close>
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
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-space>
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
          <el-input v-model="modelForm.specJson" type="textarea" :rows="3" placeholder='如 {"容量":"一级"}' />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="modelForm.state" style="width: 100%">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
    </AppDialog>
  </div>
</template>

<style scoped>
.toolbar, .dialog-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
