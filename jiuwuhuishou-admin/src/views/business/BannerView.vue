<script setup>
import { computed, reactive, ref, watch } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import { useCrudPage } from '@/composables/useCrudPage'
import { bannerApi, updateBannerStateApi } from '@/api/modules/banner'
import { bannerStateMap, resolveFileUrl } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { showError, showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

/** 按 classify 区分模块 */
const MODULES = [
  { key: '1', classify: 1, label: '首页轮播图', createText: '添加轮播图', nameLabel: '轮播图名称', requireImage: true },
  { key: '6', classify: 6, label: '首页公告消息', createText: '添加公告消息', nameLabel: '公告内容', requireImage: false, notice: true },
  { key: '4', classify: 4, label: '邀请背景图', createText: '添加邀请背景图', nameLabel: '背景图名称', requireImage: true },
  { key: '2', classify: 2, label: '首页底部配置', createText: '添加底部配置', nameLabel: '配置名称', requireImage: false },
  { key: '5', classify: 5, label: '首页分类', createText: '添加首页分类', nameLabel: '分类名称', requireImage: false },
  { key: '3', classify: 3, label: '其他配置分类', createText: '添加配置', nameLabel: '配置名称', requireImage: true },
]

const activeTab = ref('1')
const formRef = ref()
const togglingIds = ref(new Set())
const defaultQuery = reactive({ classify: 1 })

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
} = useCrudPage(bannerApi, { defaultQuery })

const currentModule = computed(
  () => MODULES.find((item) => item.key === activeTab.value) || MODULES[0],
)

const isNoticeModule = computed(() => !!currentModule.value.notice)

const formRules = computed(() => {
  const rules = {
    name: [{ required: true, message: `请输入${currentModule.value.nameLabel}`, trigger: 'blur' }],
  }
  // 首页底部配置/首页分类/公告消息允许无图；轮播/邀请背景/其他配置需上传图片
  if (currentModule.value.requireImage) {
    rules.imageUrl = [{ required: true, message: '请上传图片', trigger: 'change' }]
  }
  if (currentModule.value.classify === 5) {
    rules.url = [{ required: true, message: '请填写跳转链接', trigger: 'blur' }]
  }
  return rules
})

watch(activeTab, () => {
  const classify = currentModule.value.classify
  defaultQuery.classify = classify
  pageNum.value = 1
  loadData({ classify })
})

function handleCreate() {
  openCreate({
    classify: currentModule.value.classify,
    sort: 1,
    state: 1,
    name: '',
    imageUrl: '',
    url: '',
    description: '',
  })
}

async function handleSubmit() {
  const valid = await validateForm(formRef)
  if (!valid) return
  await submitForm({ classify: currentModule.value.classify })
}

async function handleToggleState(row, enabled) {
  const nextState = enabled ? 1 : 2
  if (row.state === nextState) return
  const prev = row.state
  row.state = nextState
  togglingIds.value = new Set([...togglingIds.value, row.id])
  try {
    await updateBannerStateApi({ id: row.id, state: nextState })
    showSuccess(enabled ? '已启用' : '已隐藏')
  } catch (error) {
    row.state = prev
    showError(error?.msg || error?.message || '状态更新失败')
  } finally {
    const next = new Set(togglingIds.value)
    next.delete(row.id)
    togglingIds.value = next
  }
}
</script>

<template>
  <el-card shadow="never" class="page-card banner-page">
    <el-tabs v-model="activeTab" class="banner-tabs">
      <el-tab-pane
        v-for="mod in MODULES"
        :key="mod.key"
        :label="mod.label"
        :name="mod.key"
      />
    </el-tabs>

    <div class="banner-toolbar">
      <el-button v-if="isAuth(PERMS.bannerAdd)" type="primary" @click="handleCreate">{{ currentModule.createText }}</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" border class="banner-table">
      <el-table-column prop="id" label="编号" width="80" align="center" />
      <el-table-column v-if="!isNoticeModule" label="图片" width="100" align="center">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="resolveFileUrl(row.imageUrl)"
            fit="cover"
            class="banner-thumb"
            :preview-src-list="[resolveFileUrl(row.imageUrl)]"
            preview-teleported
          />
          <span v-else class="text-muted">—</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="name"
        :label="currentModule.nameLabel"
        :min-width="isNoticeModule ? 280 : 140"
        show-overflow-tooltip
      />
      <el-table-column prop="url" label="链接" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ row.url || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <span :class="row.state === 1 ? 'status-on' : 'status-off'">
            {{ bannerStateMap[row.state]?.label || (row.state === 1 ? '显示' : '隐藏') }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="是否启用" width="110" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="row.state === 1"
            :loading="togglingIds.has(row.id)"
            @change="(val) => handleToggleState(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-space>
            <el-button v-if="isAuth(PERMS.bannerUpdate)" type="primary" size="small" @click="openEdit(row)">修改</el-button>
            <el-button v-if="isAuth(PERMS.bannerDelete)" type="danger" size="small" @click="removeRow(row)">删除</el-button>
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
      :title="editingId ? `编辑${currentModule.label}` : currentModule.createText"
      :loading="dialogLoading"
      width="560px"
      @confirm="handleSubmit"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item :label="currentModule.nameLabel" prop="name">
          <el-input
            v-model="form.name"
            :type="isNoticeModule ? 'textarea' : 'text'"
            :rows="isNoticeModule ? 3 : undefined"
            :placeholder="isNoticeModule
              ? '如：王** 刚刚回收旧衣获得 15.5 元'
              : `请输入${currentModule.nameLabel}`"
            :maxlength="isNoticeModule ? 200 : undefined"
            :show-word-limit="isNoticeModule"
          />
        </el-form-item>
        <el-form-item v-if="!isNoticeModule" label="图片" prop="imageUrl">
          <ImageUpload v-model="form.imageUrl" tip="支持 JPG/PNG，不超过 5MB" />
        </el-form-item>
        <el-form-item label="跳转链接" :prop="currentModule.classify === 5 ? 'url' : undefined">
          <el-input
            v-model="form.url"
            :placeholder="isNoticeModule
              ? '可选，点击公告后跳转，如 /packageCommon/order/list'
              : '如 /packageRecycle/appliance/home'"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item v-if="!isNoticeModule" :label="currentModule.classify === 5 ? '图标兜底' : '描述'">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            :placeholder="currentModule.classify === 5
              ? '无图片时生效：图标名|样式，如 home|is-red'
              : (currentModule.classify === 2 ? '可选：说明|角标|图标名，如 连续签到领好礼|+5|calendar' : '可选描述')"
          />
        </el-form-item>
      </el-form>
    </AppDialog>
  </el-card>
</template>

<style scoped>
.banner-page {
  min-height: 100%;
}

.banner-tabs {
  margin-bottom: 0;
}

.banner-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.banner-tabs :deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

.banner-toolbar {
  display: flex;
  justify-content: flex-end;
  margin: 16px 0;
}

.banner-thumb {
  width: 56px;
  height: 56px;
  border-radius: 6px;
}

.status-on {
  color: var(--el-color-success);
}

.status-off {
  color: var(--el-text-color-secondary);
}

.text-muted {
  color: var(--el-text-color-placeholder);
}
</style>
