<script setup>
import { reactive, ref, watch } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import { enableStateMap, resolveFileUrl, tagMeta } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { confirmAction, showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const props = defineProps({
  api: { type: Object, required: true },
  label: { type: String, default: '分类' },
  hint: { type: String, default: '' },
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
const form = reactive({ id: null, name: '', icon: '', sort: 0, state: 1 })
const rules = { name: [{ required: true, message: `请输入${props.label}名称`, trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const data = await props.api.page({
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

watch(() => props.api, () => {
  pageNum.value = 1
  loadData()
})

function openCreate() {
  Object.assign(form, { id: null, name: '', icon: '', sort: 0, state: 1 })
  dialogVisible.value = true
}

async function openEdit(row) {
  const detail = await props.api.info(row.id)
  Object.assign(form, detail)
  dialogVisible.value = true
}

async function submit() {
  const valid = await validateForm(formRef)
  if (!valid) return
  dialogLoading.value = true
  try {
    if (form.id) {
      await props.api.update({ ...form })
      showSuccess('已更新')
    } else {
      await props.api.save({ ...form })
      showSuccess('已创建')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    dialogLoading.value = false
  }
}

async function removeRow(row) {
  await confirmAction(`确认删除「${row.name}」吗？`)
  await props.api.delete({ id: row.id })
  showSuccess('已删除')
  await loadData()
}

loadData()
</script>

<template>
  <div>
    <el-alert v-if="hint" class="hint" type="info" :closable="false" :title="hint" show-icon />
    <div class="toolbar">
      <el-form inline @submit.prevent="pageNum = 1; loadData()">
        <el-form-item :label="label"><el-input v-model="filters.name" clearable /></el-form-item>
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
      <el-button v-if="isAuth(PERMS.productCategoryAdd)" type="primary" @click="openCreate">新增{{ label }}</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="name" :label="`${label}名称`" min-width="140" />
      <el-table-column label="图标" width="80" align="center">
        <template #default="{ row }">
          <el-image v-if="row.icon" :src="resolveFileUrl(row.icon)" style="width: 36px; height: 36px" fit="cover" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="tagMeta(enableStateMap, row.state).type">{{ tagMeta(enableStateMap, row.state).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-if="isAuth(PERMS.productCategoryUpdate)" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="isAuth(PERMS.productCategoryDelete)" link type="danger" @click="removeRow(row)">删除</el-button>
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

    <AppDialog v-model="dialogVisible" :title="form.id ? `编辑${label}` : `新增${label}`" :loading="dialogLoading" width="520px" @confirm="submit">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item :label="`${label}名称`" prop="name"><el-input v-model="form.name" /></el-form-item>
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
  </div>
</template>

<style scoped>
.hint { margin-bottom: 12px; }
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
