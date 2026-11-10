<script setup>
import CrudPage from '@/components/common/CrudPage.vue'
import { serviceAreaApi } from '@/api/modules/service-area'
import { PERMS } from '@/constants/permissions'
import { enableStateMap, tagMeta } from '@/utils/display'

const columns = [
  { prop: 'province', label: '省', width: 100 },
  { prop: 'city', label: '市', width: 100 },
  { prop: 'district', label: '区', width: 100 },
  { prop: 'state', label: '状态', width: 90 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]
</script>

<template>
  <CrudPage
    title="服务区域"
    :api="serviceAreaApi"
    :columns="columns"
    :create-defaults="{ state: 1 }"
    :create-perm="PERMS.serviceAreaAdd"
    :edit-perm="PERMS.serviceAreaUpdate"
    :delete-perm="PERMS.serviceAreaDelete"
  >
    <template #column-state="{ row }">
      <el-tag :type="tagMeta(enableStateMap, row.state).type">{{ tagMeta(enableStateMap, row.state).label }}</el-tag>
    </template>
    <template #form="{ form }">
      <el-form-item label="省"><el-input v-model="form.province" /></el-form-item>
      <el-form-item label="市"><el-input v-model="form.city" /></el-form-item>
      <el-form-item label="区"><el-input v-model="form.district" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.state" style="width: 100%">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
      </el-form-item>
    </template>
  </CrudPage>
</template>
