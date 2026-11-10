<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import RichTextEditor from '@/components/common/RichTextEditor.vue'
import {
  getSystemConfigListApi,
  refreshSystemConfigApi,
  saveSystemConfigApi,
  updateSystemConfigApi,
} from '@/api/modules/system-config'
import {
  isAgreementRichTextRow,
  parseAgreementValue,
  serializeAgreementValue,
} from '@/utils/agreementConfig'
import { validateForm } from '@/utils/form'
import { showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'

const AGREEMENT_TYPE = 'agreement'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const formRef = ref()
const groups = ref([])
const activeTab = ref('')
const tableData = ref([])
const editingId = ref(null)

const form = reactive({
  id: null,
  paramKey: '',
  paramValue: '',
  status: '1',
  remark: '',
  paramType: '',
  paramTypeDesc: '',
  agreementTitle: '',
  agreementHtml: '',
})

function getActiveGroup() {
  return groups.value.find((item) => item.paramType === activeTab.value)
}

function isAgreementTab() {
  return activeTab.value === AGREEMENT_TYPE
    || getActiveGroup()?.paramTypeDesc === '协议配置'
}

const isAgreementEdit = computed(() => isAgreementRichTextRow(form, isAgreementTab()))

const formRules = computed(() => {
  if (isAgreementEdit.value) {
    return {
      agreementTitle: [{ required: true, message: '请输入协议标题', trigger: 'blur' }],
      agreementHtml: [{ required: true, message: '请输入协议内容', trigger: 'blur' }],
    }
  }
  if (editingId.value) {
    return {
      paramValue: [{ required: true, message: '请输入内容', trigger: 'blur' }],
    }
  }
  return {
    remark: [{ required: true, message: '请输入类型', trigger: 'blur' }],
    paramValue: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  }
})

function syncTableData() {
  tableData.value = groups.value.find((item) => item.paramType === activeTab.value)?.list || []
}

function previewCell(row) {
  if (!isAgreementRichTextRow(row, isAgreementTab())) {
    return row.paramValue
  }
  const parsed = parseAgreementValue(row.paramValue, row.remark || '协议')
  const plain = String(parsed.html || '')
    .replace(/<[^>]+>/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
  return plain || parsed.title
}

async function loadData() {
  loading.value = true
  try {
    const data = await getSystemConfigListApi()
    groups.value = (data || []).map((item) => ({
      paramType: item.paramType,
      paramTypeDesc: item.paramTypeDesc || item.paramType,
      list: item.sysConfigEntityList || [],
    }))
    // 首次进入默认展示「其他配置」
    const defaultGroup = groups.value.find(
      (g) => g.paramType === 'default' || g.paramTypeDesc === '其他配置',
    )
    if (!activeTab.value && defaultGroup) {
      activeTab.value = defaultGroup.paramType
    } else if (!activeTab.value && groups.value.length) {
      activeTab.value = groups.value[0].paramType
    } else if (!groups.value.some((item) => item.paramType === activeTab.value)) {
      activeTab.value = defaultGroup?.paramType || groups.value[0]?.paramType || ''
    }
    syncTableData()
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  syncTableData()
}

function openCreate() {
  const activeGroup = getActiveGroup()
  editingId.value = null
  Object.assign(form, {
    id: null,
    paramKey: '',
    paramValue: '',
    status: '1',
    remark: '',
    paramType: activeTab.value,
    paramTypeDesc: activeGroup?.paramTypeDesc || '',
    agreementTitle: '',
    agreementHtml: '',
  })
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  const base = {
    id: row.id,
    paramKey: row.paramKey,
    paramValue: row.paramValue,
    status: row.status || '1',
    remark: row.remark || '',
    paramType: row.paramType || activeTab.value,
    paramTypeDesc: row.paramTypeDesc || getActiveGroup()?.paramTypeDesc || '',
    agreementTitle: '',
    agreementHtml: '',
  }
  if (isAgreementRichTextRow(base, isAgreementTab())) {
    const parsed = parseAgreementValue(row.paramValue, row.remark || '协议')
    base.agreementTitle = parsed.title
    base.agreementHtml = parsed.html
  }
  Object.assign(form, base)
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await validateForm(formRef)
  if (!valid) return
  dialogLoading.value = true
  try {
    if (editingId.value) {
      let paramValue = form.paramValue
      let remark = form.remark
      if (isAgreementEdit.value) {
        paramValue = serializeAgreementValue(form.agreementTitle, form.agreementHtml)
        remark = form.agreementTitle || form.remark
      }
      await updateSystemConfigApi({
        id: form.id,
        paramKey: form.paramKey,
        paramValue,
        status: form.status,
        remark,
        paramType: form.paramType || AGREEMENT_TYPE,
        paramTypeDesc: form.paramTypeDesc || '协议配置',
      })
      await refreshSystemConfigApi()
      showSuccess('配置已更新并刷新缓存')
    } else {
      await saveSystemConfigApi({
        paramKey: `cfg_${Date.now()}`,
        paramValue: form.paramValue,
        status: '1',
        remark: form.remark,
        paramType: form.paramType,
        paramTypeDesc: form.paramTypeDesc,
      })
      showSuccess('配置已新增')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    dialogLoading.value = false
  }
}

async function handleRefresh() {
  await refreshSystemConfigApi()
  showSuccess('配置缓存已刷新')
}

onMounted(loadData)
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <div>
          <strong>系统配置</strong>
          <span v-if="isAgreementTab()" class="sub">
            协议配置：用户协议 / 隐私协议 / 关于我们 / 分佣规则，编辑后用户端实时读取
          </span>
        </div>
        <el-space>
          <el-button
            v-if="isAuth('sys:config:save') && groups.length && !isAgreementTab()"
            type="primary"
            @click="openCreate"
          >
            新增
          </el-button>
          <el-button v-if="isAuth('sys:config:update')" @click="handleRefresh">刷新缓存</el-button>
        </el-space>
      </div>
    </template>

    <el-tabs v-if="groups.length" v-model="activeTab" class="config-tabs" @tab-change="handleTabChange">
      <el-tab-pane
        v-for="group in groups"
        :key="group.paramType"
        :label="group.paramTypeDesc"
        :name="group.paramType"
      />
    </el-tabs>

    <el-table v-if="groups.length" v-loading="loading" :data="tableData" border class="config-table">
      <el-table-column prop="id" label="编号" width="90" align="center" />
      <el-table-column
        v-if="isAgreementTab()"
        prop="paramKey"
        label="配置键"
        min-width="200"
        show-overflow-tooltip
      />
      <el-table-column prop="remark" label="类型" min-width="180" show-overflow-tooltip />
      <el-table-column label="内容" min-width="360" show-overflow-tooltip>
        <template #default="{ row }">
          <el-tag v-if="isAgreementRichTextRow(row, isAgreementTab())" size="small" type="success" class="rich-tag">
            富文本
          </el-tag>
          {{ previewCell(row) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button v-if="isAuth('sys:config:update')" link type="primary" @click="openEdit(row)">
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-else-if="!loading" description="暂无配置数据" />

    <AppDialog
      v-model="dialogVisible"
      :title="editingId ? (isAgreementEdit ? '编辑协议' : '编辑配置') : '新增配置'"
      :loading="dialogLoading"
      :width="isAgreementEdit ? '860px' : '640px'"
      @confirm="handleSubmit"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <template v-if="isAgreementEdit">
          <el-form-item label="配置键">
            <el-input :model-value="form.paramKey" readonly />
          </el-form-item>
          <el-form-item label="协议标题" prop="agreementTitle">
            <el-input v-model="form.agreementTitle" maxlength="64" placeholder="如：用户服务协议" />
          </el-form-item>
          <el-form-item label="协议内容" prop="agreementHtml">
            <RichTextEditor
              :key="`agreement-${form.id || 'new'}`"
              v-model="form.agreementHtml"
              min-height="320px"
              placeholder="请输入协议正文，支持标题、列表、加粗等"
            />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item v-if="!editingId" label="所属模块">
            <el-input :model-value="form.paramTypeDesc" readonly />
          </el-form-item>
          <el-form-item label="类型" prop="remark">
            <el-input v-model="form.remark" :readonly="Boolean(editingId)" placeholder="请输入类型说明" />
          </el-form-item>
          <el-form-item label="内容" prop="paramValue">
            <el-input v-model="form.paramValue" type="textarea" :rows="6" placeholder="请输入配置内容" />
          </el-form-item>
        </template>
      </el-form>
    </AppDialog>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.sub {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  font-weight: 400;
}

.config-tabs {
  margin-bottom: 16px;
}

.config-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.config-table {
  margin-top: 0;
}

.rich-tag {
  margin-right: 6px;
}
</style>
