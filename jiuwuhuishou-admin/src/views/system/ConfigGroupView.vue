<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import {
  getSystemConfigListApi,
  refreshSystemConfigApi,
  updateSystemConfigApi,
} from '@/api/modules/system-config'
import { showSuccess } from '@/utils/modal'
import { ElMessage } from 'element-plus'
import { isAuth } from '@/utils/tool'

/** 业务分组：按 paramKey 前缀/精确匹配（优先命中） */
const CONFIG_GROUPS = [
  {
    id: 'home',
    label: '首页运营',
    match: (key) => key.startsWith('home_invite_') || key.startsWith('app_push_remind_'),
  },
  {
    id: 'order',
    label: '订单履约',
    match: (key) => key.startsWith('order_') || key.startsWith('platform_mail_') || key.startsWith('sf_butler_') || key.startsWith('aliyun_logistics_'),
  },
  {
    id: 'clothes',
    label: '旧衣文案',
    match: (key) => key.startsWith('clothes_'),
  },
  {
    id: 'book',
    label: '书籍文案',
    match: (key) => key.startsWith('book_'),
  },
  {
    id: 'appliance',
    label: '家电页面',
    match: (key) => key.startsWith('appliance_'),
  },
  {
    id: 'digital',
    label: '数码/奢侈',
    match: (key) => key.startsWith('digital_'),
  },
  {
    id: 'welfare',
    label: '福利中心',
    match: (key) => key.startsWith('welfare_'),
  },
  {
    id: 'withdraw',
    label: '提现规则',
    match: (key) => key.startsWith('sys_withdraw_'),
  },
  {
    id: 'referral',
    label: '推荐分佣',
    match: (key) =>
      (key.startsWith('referral_') && key !== 'referral_rules_json')
      || key.startsWith('biz_referral_'),
  },
  {
    id: 'service',
    label: '客服/服务范围',
    match: (key) => key.startsWith('service_chat_') || key === 'service_check_json',
  },
  {
    id: 'legal',
    label: '协议配置',
    match: (key) =>
      key === 'user_service_agreement'
      || key === 'user_privacy_agreement'
      || key === 'user_about_us'
      || key === 'referral_rules_json'
      || (key.startsWith('user_') && key.endsWith('_agreement')),
  },
]

const loading = ref(false)
const allConfigs = ref([])
const activeTab = ref('home')
const keyword = ref('')
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const editing = reactive({
  id: null,
  paramKey: '',
  paramValue: '',
  remark: '',
  status: '1',
  paramType: '',
  paramTypeDesc: '',
  isJson: false,
})

function flattenConfigs(data) {
  if (!Array.isArray(data)) return []
  if (data.length && data[0]?.sysConfigEntityList) {
    return data.flatMap((g) => g.sysConfigEntityList || [])
  }
  return data
}

function resolveGroupId(paramKey = '') {
  const key = String(paramKey || '')
  const hit = CONFIG_GROUPS.find((g) => g.match(key))
  return hit?.id || 'other'
}

function looksLikeJson(value) {
  const text = String(value || '').trim()
  return (text.startsWith('{') && text.endsWith('}')) || (text.startsWith('[') && text.endsWith(']'))
}

function formatJsonMaybe(value) {
  try {
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch {
    return value
  }
}

const tabOptions = computed(() => {
  const counts = {}
  for (const item of allConfigs.value) {
    const gid = resolveGroupId(item.paramKey)
    counts[gid] = (counts[gid] || 0) + 1
  }
  const tabs = CONFIG_GROUPS.map((g) => ({
    id: g.id,
    label: `${g.label}（${counts[g.id] || 0}）`,
  }))
  tabs.push({ id: 'other', label: `其他（${counts.other || 0}）` })
  return tabs
})

const tableData = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  return allConfigs.value
    .filter((item) => resolveGroupId(item.paramKey) === activeTab.value)
    .filter((item) => {
      if (!kw) return true
      return (
        String(item.paramKey || '').toLowerCase().includes(kw)
        || String(item.remark || '').toLowerCase().includes(kw)
        || String(item.paramValue || '').toLowerCase().includes(kw)
      )
    })
    .sort((a, b) => String(a.paramKey || '').localeCompare(String(b.paramKey || '')))
})

async function loadData() {
  loading.value = true
  try {
    const data = await getSystemConfigListApi({})
    allConfigs.value = flattenConfigs(data)
    if (!tabOptions.value.some((t) => t.id === activeTab.value)) {
      activeTab.value = tabOptions.value[0]?.id || 'home'
    }
  } finally {
    loading.value = false
  }
}

function openEdit(row) {
  const isJson = looksLikeJson(row.paramValue)
  Object.assign(editing, {
    id: row.id,
    paramKey: row.paramKey,
    paramValue: isJson ? formatJsonMaybe(row.paramValue) : (row.paramValue || ''),
    remark: row.remark || '',
    status: row.status || '1',
    paramType: row.paramType || '',
    paramTypeDesc: row.paramTypeDesc || '',
    isJson,
  })
  dialogVisible.value = true
}

function prettyJson() {
  try {
    editing.paramValue = JSON.stringify(JSON.parse(editing.paramValue), null, 2)
    editing.isJson = true
    showSuccess('已格式化 JSON')
  } catch {
    ElMessage.error('当前内容不是合法 JSON')
  }
}

function compactJson() {
  try {
    editing.paramValue = JSON.stringify(JSON.parse(editing.paramValue))
    editing.isJson = true
    showSuccess('已压缩 JSON')
  } catch {
    ElMessage.error('当前内容不是合法 JSON')
  }
}

async function handleSubmit() {
  if (!editing.id) return
  let value = editing.paramValue
  if (editing.isJson || looksLikeJson(value)) {
    try {
      value = JSON.stringify(JSON.parse(value))
    } catch {
      ElMessage.error('请先修正 JSON 格式后再保存')
      return
    }
  }
  dialogLoading.value = true
  try {
    await updateSystemConfigApi({
      id: editing.id,
      paramKey: editing.paramKey,
      paramValue: value,
      status: editing.status,
      remark: editing.remark,
      paramType: editing.paramType,
      paramTypeDesc: editing.paramTypeDesc,
    })
    await refreshSystemConfigApi()
    showSuccess('配置已保存并刷新缓存')
    dialogVisible.value = false
    await loadData()
  } finally {
    dialogLoading.value = false
  }
}

async function handleRefresh() {
  await refreshSystemConfigApi()
  await loadData()
  showSuccess('配置缓存已刷新')
}

function previewValue(value) {
  const text = String(value ?? '')
  if (text.length <= 80) return text
  return `${text.slice(0, 80)}…`
}

onMounted(loadData)
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <div>
          <strong>配置分组</strong>
          <span class="sub">按业务键前缀归类，编辑后自动刷新缓存；完整平铺列表见「系统配置」</span>
        </div>
        <el-space>
          <el-input
            v-model="keyword"
            clearable
            placeholder="搜索键名/备注/内容"
            style="width: 220px"
          />
          <el-button @click="loadData">刷新列表</el-button>
          <el-button v-if="isAuth('sys:config:update')" type="primary" plain @click="handleRefresh">
            刷新缓存
          </el-button>
        </el-space>
      </div>
    </template>

    <el-tabs v-model="activeTab" class="group-tabs">
      <el-tab-pane v-for="tab in tabOptions" :key="tab.id" :label="tab.label" :name="tab.id" />
    </el-tabs>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="paramKey" label="配置键" min-width="220" show-overflow-tooltip />
      <el-table-column prop="remark" label="说明" min-width="180" show-overflow-tooltip />
      <el-table-column label="内容预览" min-width="260" show-overflow-tooltip>
        <template #default="{ row }">
          <el-tag v-if="looksLikeJson(row.paramValue)" size="small" type="warning" class="json-tag">JSON</el-tag>
          {{ previewValue(row.paramValue) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="isAuth('sys:config:update')"
            link
            type="primary"
            @click="openEdit(row)"
          >
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && !tableData.length" description="本组暂无配置或被搜索过滤" />
  </el-card>

  <AppDialog
    v-model="dialogVisible"
    title="编辑业务配置"
    :loading="dialogLoading"
    width="720px"
    @confirm="handleSubmit"
  >
    <el-form label-width="90px">
      <el-form-item label="配置键">
        <el-input :model-value="editing.paramKey" readonly />
      </el-form-item>
      <el-form-item label="说明">
        <el-input v-model="editing.remark" />
      </el-form-item>
      <el-form-item label="内容">
        <div class="editor-toolbar">
          <el-button size="small" @click="prettyJson">格式化 JSON</el-button>
          <el-button size="small" @click="compactJson">压缩 JSON</el-button>
        </div>
        <el-input
          v-model="editing.paramValue"
          type="textarea"
          :rows="editing.isJson || looksLikeJson(editing.paramValue) ? 14 : 8"
          placeholder="配置内容"
        />
      </el-form-item>
    </el-form>
  </AppDialog>
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

.group-tabs {
  margin-bottom: 12px;
}

.json-tag {
  margin-right: 6px;
}

.editor-toolbar {
  margin-bottom: 8px;
  display: flex;
  gap: 8px;
}
</style>
