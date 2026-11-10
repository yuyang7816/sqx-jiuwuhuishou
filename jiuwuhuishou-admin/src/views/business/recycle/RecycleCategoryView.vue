<script setup>
import CrudPage from '@/components/common/CrudPage.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import { recycleCategoryApi } from '@/api/modules/recycle-category'
import { PERMS } from '@/constants/permissions'
import { enableStateMap, resolveFileUrl, tagMeta } from '@/utils/display'

const columns = [
  { prop: 'name', label: '名称', minWidth: 120 },
  { prop: 'code', label: '编码', width: 110 },
  { prop: 'icon', label: '图标', width: 90 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'state', label: '状态', width: 90 },
  { prop: 'description', label: '描述', minWidth: 160 },
]
</script>

<template>
  <CrudPage
    title="回收品类"
    :api="recycleCategoryApi"
    :columns="columns"
    :create-defaults="{ sort: 1, state: 1 }"
    :create-perm="PERMS.recycleCategoryAdd"
    :edit-perm="PERMS.recycleCategoryUpdate"
    :delete-perm="PERMS.recycleCategoryDelete"
  >
    <template #column-icon="{ row }">
      <el-image v-if="row.icon" :src="resolveFileUrl(row.icon)" style="width: 36px; height: 36px" fit="cover" />
      <span v-else>—</span>
    </template>
    <template #column-state="{ row }">
      <el-tag :type="tagMeta(enableStateMap, row.state).type">{{ tagMeta(enableStateMap, row.state).label }}</el-tag>
    </template>
    <template #form="{ form }">
      <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="编码"><el-input v-model="form.code" placeholder="clothes/appliance/digital" /></el-form-item>
      <el-form-item label="图标"><ImageUpload v-model="form.icon" /></el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.state" style="width: 100%">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
    </template>
  </CrudPage>
</template>
