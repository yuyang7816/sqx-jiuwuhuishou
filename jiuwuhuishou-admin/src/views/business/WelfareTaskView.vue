<script setup>
import CrudPage from '@/components/common/CrudPage.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import { welfareTaskApi } from '@/api/modules/welfare-task'
import { PERMS } from '@/constants/permissions'
import { enableStateMap, resolveFileUrl, tagMeta } from '@/utils/display'

const columns = [
  { prop: 'title', label: '标题', minWidth: 140 },
  { prop: 'description', label: '描述', minWidth: 180 },
  { prop: 'icon', label: '图标', width: 90 },
  { prop: 'btnText', label: '按钮文案', width: 100 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'state', label: '状态', width: 90 },
]
</script>

<template>
  <CrudPage
    title="福利任务"
    :api="welfareTaskApi"
    :columns="columns"
    :create-defaults="{ sort: 1, state: 1, btnText: '去完成', icon: '' }"
    :create-perm="PERMS.welfareAdd"
    :edit-perm="PERMS.welfareUpdate"
    :delete-perm="PERMS.welfareDelete"
  >
    <template #column-icon="{ row }">
      <el-image
        v-if="row.icon"
        :src="resolveFileUrl(row.icon)"
        style="width: 36px; height: 36px"
        fit="cover"
        :preview-src-list="[resolveFileUrl(row.icon)]"
        preview-teleported
      />
      <span v-else>—</span>
    </template>
    <template #column-state="{ row }">
      <el-tag :type="tagMeta(enableStateMap, row.state).type">{{ tagMeta(enableStateMap, row.state).label }}</el-tag>
    </template>
    <template #form="{ form }">
      <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      <el-form-item label="图标">
        <ImageUpload v-model="form.icon" tip="支持 JPG/PNG，不超过 5MB" />
      </el-form-item>
      <el-form-item label="跳转链接"><el-input v-model="form.url" /></el-form-item>
      <el-form-item label="按钮文案"><el-input v-model="form.btnText" /></el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.state" style="width: 100%">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
      </el-form-item>
    </template>
  </CrudPage>
</template>
