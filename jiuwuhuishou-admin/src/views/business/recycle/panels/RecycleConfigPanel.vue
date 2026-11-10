<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getSystemConfigListApi,
  refreshSystemConfigApi,
  saveSystemConfigApi,
  updateSystemConfigApi,
} from '@/api/modules/system-config'
import { showSuccess } from '@/utils/modal'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const savingKey = ref('')
const allConfigs = ref([])

const SIMPLE_KEYS = [
  { key: 'clothes_weight_options', label: '旧衣重量选项' },
  { key: 'digital_hot_keywords_digital', label: '数码热搜词' },
  { key: 'digital_hot_keywords_luxury', label: '奢侈品热搜词' },
  { key: 'appliance_estimate_adjust_condition', label: '家电成色调价' },
]

const DIGITAL_DEFAULT_SECTIONS = [
  { key: 'storage', label: '容量版本', optionsText: '128G,256G,512G,1TB', layout: 'grid' },
  { key: 'color', label: '机身颜色', optionsText: '深空黑,银色,蓝色,紫色', layout: 'grid' },
  { key: 'damage', label: '损伤情况', optionsText: '无明显故障,轻微磕碰,屏幕细纹,电池损耗明显,无法开机', layout: 'grid' },
  { key: 'condition', label: '新旧程度', optionsText: '九成新,良好,正常老化,成色一般', layout: 'column' },
]

const LUXURY_DEFAULT_SECTIONS = [
  { key: 'series', label: '款式系列', optionsText: '经典款,热门款,限定款,常规款', layout: 'grid' },
  { key: 'material', label: '材质', optionsText: '牛皮,帆布,羊皮,拼接材质', layout: 'grid' },
  { key: 'damage', label: '损伤情况', optionsText: '无明显瑕疵,轻微磨损,五金划痕,边角磨损,需专业修复', layout: 'grid' },
  { key: 'condition', label: '新旧程度', optionsText: '成色优秀,成色良好,正常使用痕迹,成色一般', layout: 'column' },
]

const specEditors = reactive({
  digital_spec_digital: {
    key: 'digital_spec_digital',
    label: '数码精准估价选项',
    sections: cloneSections(DIGITAL_DEFAULT_SECTIONS),
    id: null,
  },
  digital_spec_luxury: {
    key: 'digital_spec_luxury',
    label: '奢侈品精准估价选项',
    sections: cloneSections(LUXURY_DEFAULT_SECTIONS),
    id: null,
  },
})

function cloneSections(list) {
  return list.map((item) => ({ ...item }))
}

function flattenConfigs(data) {
  if (!Array.isArray(data)) return []
  // list 接口返回分组；兼容扁平数组
  if (data.length && data[0]?.sysConfigEntityList) {
    return data.flatMap((g) => g.sysConfigEntityList || [])
  }
  return data
}

function findConfig(key) {
  return allConfigs.value.find((c) => c.paramKey === key)
}

function parseSectionsFromJson(raw, fallback) {
  try {
    const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw
    const sections = parsed?.sections
    if (!Array.isArray(sections) || !sections.length) return cloneSections(fallback)
    return sections.map((s) => ({
      key: s.key || '',
      label: s.label || '',
      layout: s.layout || 'grid',
      optionsText: Array.isArray(s.options) ? s.options.join(',') : String(s.options || ''),
    }))
  } catch {
    return cloneSections(fallback)
  }
}

function sectionsToJson(sections) {
  return JSON.stringify({
    sections: sections.map((s) => ({
      key: (s.key || '').trim(),
      label: (s.label || '').trim(),
      layout: s.layout || 'grid',
      options: String(s.optionsText || '')
        .split(/[,，、\n]/)
        .map((x) => x.trim())
        .filter(Boolean),
    })),
  })
}

const simpleRows = computed(() => SIMPLE_KEYS.map((item) => {
  const found = findConfig(item.key)
  return {
    ...item,
    id: found?.id ?? null,
    paramValue: found?.paramValue ?? '',
    exists: !!found,
  }
}))

function hydrateSpecEditors() {
  const digital = findConfig('digital_spec_digital')
  specEditors.digital_spec_digital.id = digital?.id ?? null
  specEditors.digital_spec_digital.sections = digital?.paramValue
    ? parseSectionsFromJson(digital.paramValue, DIGITAL_DEFAULT_SECTIONS)
    : cloneSections(DIGITAL_DEFAULT_SECTIONS)

  const luxury = findConfig('digital_spec_luxury')
  specEditors.digital_spec_luxury.id = luxury?.id ?? null
  specEditors.digital_spec_luxury.sections = luxury?.paramValue
    ? parseSectionsFromJson(luxury.paramValue, LUXURY_DEFAULT_SECTIONS)
    : cloneSections(LUXURY_DEFAULT_SECTIONS)
}

async function load() {
  loading.value = true
  try {
    const data = await getSystemConfigListApi({})
    allConfigs.value = flattenConfigs(data)
    hydrateSpecEditors()
  } finally {
    loading.value = false
  }
}

async function persistConfig({ id, key, value, remark }) {
  if (id) {
    await updateSystemConfigApi({
      id,
      paramKey: key,
      paramValue: value,
      status: '1',
      remark,
      paramType: 'recycle',
      paramTypeDesc: '回收配置',
    })
  } else {
    await saveSystemConfigApi({
      paramKey: key,
      paramValue: value,
      status: '1',
      remark,
      paramType: 'recycle',
      paramTypeDesc: '回收配置',
    })
  }
  await refreshSystemConfigApi()
}

async function saveSimple(row) {
  if (!row.exists && !row.paramValue) {
    ElMessage.warning('请先填写配置值，或到系统配置中新建该键')
    return
  }
  savingKey.value = row.key
  try {
    await persistConfig({
      id: row.id,
      key: row.key,
      value: row.paramValue,
      remark: row.label,
    })
    showSuccess(`${row.label} 已保存并刷新缓存`)
    await load()
  } finally {
    savingKey.value = ''
  }
}

function addSection(editorKey) {
  specEditors[editorKey].sections.push({
    key: '',
    label: '',
    optionsText: '',
    layout: 'grid',
  })
}

function removeSection(editorKey, index) {
  specEditors[editorKey].sections.splice(index, 1)
}

function resetSpec(editorKey, fallback) {
  specEditors[editorKey].sections = cloneSections(fallback)
}

async function saveSpec(editorKey) {
  const editor = specEditors[editorKey]
  const invalid = editor.sections.some((s) => !s.key?.trim() || !s.label?.trim() || !s.optionsText?.trim())
  if (invalid) {
    ElMessage.warning('请完整填写每个分区的键名、标题和选项')
    return
  }
  savingKey.value = editor.key
  try {
    const value = sectionsToJson(editor.sections)
    await persistConfig({
      id: editor.id,
      key: editor.key,
      value,
      remark: editor.label,
    })
    showSuccess(`${editor.label} 已保存；用户端规格页将按新选项展示`)
    await load()
  } finally {
    savingKey.value = ''
  }
}

onMounted(load)
</script>

<template>
  <div v-loading="loading">
    <div class="toolbar">
      <el-button @click="load">刷新</el-button>
      <el-button type="primary" plain @click="router.push('/system/configs')">打开系统配置</el-button>
    </div>

    <el-card
      v-for="(editor, editorKey) in specEditors"
      :key="editorKey"
      shadow="never"
      class="spec-card"
    >
      <template #header>
        <div class="spec-header">
          <strong>{{ editor.label }}</strong>
          <el-space>
            <el-button
              link
              type="primary"
              @click="resetSpec(editorKey, editorKey === 'digital_spec_digital' ? DIGITAL_DEFAULT_SECTIONS : LUXURY_DEFAULT_SECTIONS)"
            >
              恢复默认
            </el-button>
            <el-button link type="primary" @click="addSection(editorKey)">加分区</el-button>
            <el-button
              type="primary"
              :loading="savingKey === editor.key"
              @click="saveSpec(editorKey)"
            >
              保存
            </el-button>
          </el-space>
        </div>
      </template>

      <div
        v-for="(section, index) in editor.sections"
        :key="`${editorKey}-${index}`"
        class="section-row"
      >
        <el-input v-model="section.label" placeholder="标题，如 容量版本" style="width: 140px" />
        <el-input v-model="section.key" placeholder="键，如 storage" style="width: 120px" />
        <el-select v-model="section.layout" style="width: 110px">
          <el-option label="网格" value="grid" />
          <el-option label="列表" value="column" />
        </el-select>
        <el-input
          v-model="section.optionsText"
          placeholder="选项，逗号分隔：128G,256G,512G"
          class="options-input"
        />
        <el-button link type="danger" @click="removeSection(editorKey, index)">删除</el-button>
      </div>
    </el-card>

    <el-divider content-position="left">其它回收相关配置</el-divider>

    <el-table :data="simpleRows" border>
      <el-table-column prop="label" label="配置项" min-width="140" />
      <el-table-column prop="key" label="配置键" min-width="220" />
      <el-table-column label="值" min-width="280">
        <template #default="{ row }">
          <el-input v-model="row.paramValue" type="textarea" :rows="2" placeholder="JSON 或文本" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            :loading="savingKey === row.key"
            @click="saveSimple(row)"
          >
            {{ row.exists ? '保存' : '创建并保存' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.spec-card {
  margin-bottom: 16px;
}
.spec-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.section-row {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  align-items: center;
  flex-wrap: wrap;
}
.options-input {
  flex: 1;
  min-width: 220px;
}
</style>
