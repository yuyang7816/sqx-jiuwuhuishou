<script setup>
import { reactive, ref } from 'vue'
import CrudPage from '@/components/common/CrudPage.vue'
import AppDialog from '@/components/common/AppDialog.vue'
import { brandCooperationApi } from '@/api/modules/brand-cooperation'
import { brandCooperationStatusMap, resolveFileUrl, tagMeta } from '@/utils/display'
import { showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const crudRef = ref()
const statusDialogVisible = ref(false)
const statusLoading = ref(false)
const statusForm = reactive({ id: null, status: 1, remark: '' })

const filters = reactive({ brandName: '', mobile: '', status: '' })

const columns = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'brandName', label: '品牌名', minWidth: 120 },
  { prop: 'brandImage', label: '品牌图片', width: 100 },
  { prop: 'contactName', label: '联系人', width: 100 },
  { prop: 'mobile', label: '手机', width: 120 },
  { prop: 'province', label: '省', width: 100 },
  { prop: 'city', label: '市', width: 100 },
  { prop: 'district', label: '区', width: 100 },
  { prop: 'address', label: '详细地址', minWidth: 180 },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'createTime', label: '提交时间', minWidth: 170 },
]

function buildQuery() {
  return {
    brandName: filters.brandName || undefined,
    mobile: filters.mobile || undefined,
    status: filters.status === '' ? undefined : Number(filters.status),
  }
}

function openStatus(row) {
  Object.assign(statusForm, { id: row.id, status: row.status ?? 0, remark: row.remark || '' })
  statusDialogVisible.value = true
}

async function submitStatus() {
  statusLoading.value = true
  try {
    await brandCooperationApi.updateStatus({ ...statusForm })
    showSuccess('状态已更新')
    statusDialogVisible.value = false
    crudRef.value?.loadData(buildQuery())
  } finally {
    statusLoading.value = false
  }
}
</script>

<template>
  <CrudPage ref="crudRef" title="品牌合作" :api="brandCooperationApi" :columns="columns" :show-create="false" :show-edit="false" :show-delete="false" :show-index="false">
    <template #toolbar="{ loadData }">
      <el-form inline>
        <el-form-item label="品牌名"><el-input v-model="filters.brandName" clearable /></el-form-item>
        <el-form-item label="手机"><el-input v-model="filters.mobile" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable style="width: 120px">
            <el-option label="待跟进" :value="0" />
            <el-option label="已联系" :value="1" />
            <el-option label="已合作" :value="2" />
            <el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData(buildQuery())">查询</el-button>
          <el-button @click="Object.assign(filters, { brandName: '', mobile: '', status: '' }); loadData({})">重置</el-button>
        </el-form-item>
      </el-form>
    </template>
    <template #column-brandImage="{ row }">
      <el-image
        v-if="row.brandImage"
        :src="resolveFileUrl(row.brandImage)"
        :preview-src-list="[resolveFileUrl(row.brandImage)]"
        preview-teleported
        fit="cover"
        style="width: 48px; height: 48px; border-radius: 6px"
      />
      <span v-else>—</span>
    </template>
    <template #column-status="{ row }">
      <el-tag :type="tagMeta(brandCooperationStatusMap, row.status).type">{{ tagMeta(brandCooperationStatusMap, row.status).label }}</el-tag>
    </template>
    <template #column-address="{ row }">
      <span class="cell-ellipsis" :title="row.address || ''">{{ row.address || '—' }}</span>
    </template>
    <template #actions="{ row }">
      <el-button v-if="isAuth(PERMS.brandFollow)" link type="primary" @click="openStatus(row)">跟进</el-button>
    </template>
  </CrudPage>

  <AppDialog v-model="statusDialogVisible" title="跟进状态" :loading="statusLoading" @confirm="submitStatus">
    <el-form label-width="90px">
      <el-form-item label="状态">
        <el-select v-model="statusForm.status" style="width: 100%">
          <el-option label="待跟进" :value="0" />
          <el-option label="已联系" :value="1" />
          <el-option label="已合作" :value="2" />
          <el-option label="已拒绝" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注"><el-input v-model="statusForm.remark" type="textarea" /></el-form-item>
    </el-form>
  </AppDialog>
</template>

<style scoped>
.cell-ellipsis {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: bottom;
}
</style>
