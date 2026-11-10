<script setup>
import { computed, ref } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import { useCrudPage } from '@/composables/useCrudPage'
import { validateForm } from '@/utils/form'
import { isAuth } from '@/utils/tool'

const props = defineProps({
  title: { type: String, required: true },
  api: { type: Object, required: true },
  columns: { type: Array, required: true },
  idKey: { type: String, default: 'id' },
  createDefaults: { type: Object, default: () => ({}) },
  defaultQuery: { type: Object, default: () => ({}) },
  formRules: { type: Object, default: () => ({}) },
  dialogWidth: { type: String, default: '620px' },
  showIndex: { type: Boolean, default: true },
  showCreate: { type: Boolean, default: true },
  showEdit: { type: Boolean, default: true },
  showDelete: { type: Boolean, default: true },
  /** 与 sys_menu 按钮权限 perms 对齐；不传则不校验 */
  createPerm: { type: String, default: '' },
  editPerm: { type: String, default: '' },
  deletePerm: { type: String, default: '' },
})

const formRef = ref()
const {
  loading,
  dialogVisible,
  dialogLoading,
  tableData,
  total,
  pageNum,
  pageSize,
  editingId,
  form,
  loadData,
  openCreate,
  openEdit,
  submitForm,
  removeRow,
} = useCrudPage(props.api, {
  idKey: props.idKey,
  defaultQuery: () => props.defaultQuery,
})

const canCreate = computed(() => props.showCreate && (!props.createPerm || isAuth(props.createPerm)))
const canEdit = computed(() => props.showEdit && (!props.editPerm || isAuth(props.editPerm)))
const canDelete = computed(() => props.showDelete && (!props.deletePerm || isAuth(props.deletePerm)))

function handleCreate() {
  openCreate({ ...props.createDefaults })
}

async function handleSubmit() {
  const valid = await validateForm(formRef)
  if (!valid) return
  await submitForm()
}

defineExpose({ loadData, pageNum, pageSize, total, tableData })
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <strong>{{ title }}</strong>
        <el-button v-if="canCreate" type="primary" @click="handleCreate">新增</el-button>
      </div>
    </template>
    <slot name="toolbar" :load-data="loadData" />
    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column v-if="showIndex" label="编号" width="70" align="center">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column
        v-for="column in columns"
        :key="column.prop || column.label"
        v-bind="column"
      >
        <template v-if="$slots[`column-${column.prop}`]" #default="scope">
          <slot :name="`column-${column.prop}`" v-bind="scope" />
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="150" fixed="right">
        <template #default="{ row }">
          <el-space>
            <slot
              name="actions"
              :row="row"
              :open-edit="() => openEdit(row)"
              :remove-row="() => removeRow(row)"
              :can-edit="canEdit"
              :can-delete="canDelete"
            />
            <el-button v-if="canEdit" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="canDelete" link type="danger" @click="removeRow(row)">删除</el-button>
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
    <AppDialog
      v-model="dialogVisible"
      :title="editingId ? `编辑${title}` : `新增${title}`"
      :width="dialogWidth"
      :loading="dialogLoading"
      @confirm="handleSubmit"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <slot name="form" :form="form" :editing-id="editingId" />
      </el-form>
    </AppDialog>
  </el-card>
</template>
