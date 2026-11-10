<script setup>
import { ElMessage } from 'element-plus'
import CrudPage from '@/components/common/CrudPage.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import { recycleRuleApi } from '@/api/modules/recycle-rule'
import { PERMS } from '@/constants/permissions'
import { enableStateMap, recycleRuleTypeMap, tagMeta } from '@/utils/display'
import {
  defaultContentByType,
  formatRuleContentPreview,
  isJsonRuleType,
  looksLikeJson,
  parseJsonSafe,
  parsePricingContent,
  parseServiceContent,
  serializePricingContent,
  serializeServiceContent,
} from '@/utils/recycleRule'

const ruleTypeOptions = Object.entries(recycleRuleTypeMap).map(([value, meta]) => ({
  value,
  label: meta.label,
}))

const columns = [
  { prop: 'title', label: '标题', minWidth: 140 },
  { prop: 'ruleType', label: '类型', width: 140 },
  { prop: 'content', label: '内容', minWidth: 280 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'state', label: '状态', width: 90 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  ruleType: [{ required: true, message: '请选择类型', trigger: 'change' }],
}

function contentMode(form) {
  if (form.ruleType === 'appliance_more_pricing') return 'pricing'
  if (form.ruleType === 'appliance_more_service') return 'service'
  if (looksLikeJson(form.content)) return 'json'
  return 'text'
}

function onRuleTypeChange(form, ruleType) {
  const nextType = ruleType || form.ruleType
  if (isJsonRuleType(nextType)) {
    const matchedPricing = nextType === 'appliance_more_pricing' && parseJsonSafe(form.content)?.items
    const matchedService = nextType === 'appliance_more_service'
      && (parseJsonSafe(form.content)?.desc != null || Array.isArray(parseJsonSafe(form.content)?.lines))
    if (matchedPricing) {
      form.content = serializePricingContent(parsePricingContent(form.content))
      return
    }
    if (matchedService) {
      form.content = serializeServiceContent(parseServiceContent(form.content))
      return
    }
    form.content = defaultContentByType(nextType)
    return
  }
  if (looksLikeJson(form.content)) {
    form.content = ''
  }
}

function patchPricing(form, patch) {
  form.content = serializePricingContent({
    ...parsePricingContent(form.content),
    ...patch,
  })
}

function patchPricingItem(form, index, patch) {
  const meta = parsePricingContent(form.content)
  meta.items[index] = { ...meta.items[index], ...patch }
  form.content = serializePricingContent(meta)
}

function addPricingItem(form) {
  const meta = parsePricingContent(form.content)
  meta.items.push({ name: '', price: '' })
  form.content = serializePricingContent(meta)
}

function removePricingItem(form, index) {
  const meta = parsePricingContent(form.content)
  meta.items.splice(index, 1)
  if (!meta.items.length) meta.items.push({ name: '', price: '' })
  form.content = serializePricingContent(meta)
}

function patchService(form, patch) {
  form.content = serializeServiceContent({
    ...parseServiceContent(form.content),
    ...patch,
  })
}

function patchServiceLine(form, index, value) {
  const meta = parseServiceContent(form.content)
  meta.lines[index] = value
  form.content = serializeServiceContent(meta)
}

function addServiceLine(form) {
  const meta = parseServiceContent(form.content)
  meta.lines.push('')
  form.content = serializeServiceContent(meta)
}

function removeServiceLine(form, index) {
  const meta = parseServiceContent(form.content)
  meta.lines.splice(index, 1)
  if (!meta.lines.length) meta.lines.push('')
  form.content = serializeServiceContent(meta)
}

function patchServiceTag(form, index, patch) {
  const meta = parseServiceContent(form.content)
  meta.tags[index] = { ...meta.tags[index], ...patch }
  form.content = serializeServiceContent(meta)
}

function addServiceTag(form) {
  const meta = parseServiceContent(form.content)
  meta.tags.push({ name: '', price: '' })
  form.content = serializeServiceContent(meta)
}

function removeServiceTag(form, index) {
  const meta = parseServiceContent(form.content)
  meta.tags.splice(index, 1)
  form.content = serializeServiceContent(meta)
}

function prettyJson(form) {
  try {
    form.content = JSON.stringify(JSON.parse(form.content || '{}'), null, 2)
    ElMessage.success('已格式化 JSON')
  } catch {
    ElMessage.error('当前内容不是合法 JSON')
  }
}

function compactJson(form) {
  try {
    form.content = JSON.stringify(JSON.parse(form.content || '{}'))
    ElMessage.success('已压缩 JSON')
  } catch {
    ElMessage.error('当前内容不是合法 JSON')
  }
}
</script>

<template>
  <CrudPage
    title="回收规则"
    :api="recycleRuleApi"
    :columns="columns"
    :form-rules="formRules"
    dialog-width="760px"
    :create-defaults="{ sort: 1, state: 1, ruleType: 'general', content: '' }"
    :create-perm="PERMS.recycleRuleAdd"
    :edit-perm="PERMS.recycleRuleUpdate"
    :delete-perm="PERMS.recycleRuleDelete"
  >
    <template #column-ruleType="{ row }">
      <el-tag :type="tagMeta(recycleRuleTypeMap, row.ruleType).type">
        {{ tagMeta(recycleRuleTypeMap, row.ruleType).label }}
      </el-tag>
    </template>

    <template #column-content="{ row }">
      <div class="content-cell">
        <pre>{{ formatRuleContentPreview(row) }}</pre>
      </div>
    </template>

    <template #column-state="{ row }">
      <el-tag :type="tagMeta(enableStateMap, row.state).type">{{ tagMeta(enableStateMap, row.state).label }}</el-tag>
    </template>

    <template #form="{ form }">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" maxlength="100" placeholder="请输入标题" />
      </el-form-item>

      <el-form-item label="类型" prop="ruleType">
        <el-select
          v-model="form.ruleType"
          style="width: 100%"
          placeholder="请选择类型"
          @change="(value) => onRuleTypeChange(form, value)"
        >
          <el-option
            v-for="item in ruleTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <!-- 家电价格说明 JSON -->
      <template v-if="contentMode(form) === 'pricing'">
        <el-form-item label="图标">
          <ImageUpload
            :model-value="parsePricingContent(form.content).imageUrl"
            tip="支持 JPG/PNG，不超过 5MB"
            @update:model-value="(value) => patchPricing(form, { imageUrl: value })"
          />
        </el-form-item>
        <el-form-item label="起步价">
          <el-input
            :model-value="parsePricingContent(form.content).top"
            placeholder="如 ¥80起"
            @update:model-value="(value) => patchPricing(form, { top: value })"
          />
        </el-form-item>
        <el-form-item label="价格明细">
          <div class="structured-list">
            <div
              v-for="(item, index) in parsePricingContent(form.content).items"
              :key="`pricing-${index}`"
              class="structured-row"
            >
              <el-input
                :model-value="item.name"
                placeholder="名称"
                @update:model-value="(value) => patchPricingItem(form, index, { name: value })"
              />
              <el-input
                :model-value="item.price"
                placeholder="价格"
                @update:model-value="(value) => patchPricingItem(form, index, { price: value })"
              />
              <el-button link type="danger" @click="removePricingItem(form, index)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addPricingItem(form)">+ 添加明细</el-button>
          </div>
        </el-form-item>
      </template>

      <!-- 家电服务费用 JSON -->
      <template v-else-if="contentMode(form) === 'service'">
        <el-form-item label="图标">
          <ImageUpload
            :model-value="parseServiceContent(form.content).imageUrl"
            tip="支持 JPG/PNG，不超过 5MB"
            @update:model-value="(value) => patchService(form, { imageUrl: value })"
          />
        </el-form-item>
        <el-form-item label="主题色">
          <el-select
            :model-value="parseServiceContent(form.content).theme"
            style="width: 100%"
            @update:model-value="(value) => patchService(form, { theme: value })"
          >
            <el-option label="金色 gold" value="gold" />
            <el-option label="粉色 pink" value="pink" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input
            :model-value="parseServiceContent(form.content).desc"
            type="textarea"
            :rows="2"
            placeholder="服务费用说明"
            @update:model-value="(value) => patchService(form, { desc: value })"
          />
        </el-form-item>
        <el-form-item label="费用条目">
          <div class="structured-list">
            <div
              v-for="(line, index) in parseServiceContent(form.content).lines"
              :key="`line-${index}`"
              class="structured-row is-single"
            >
              <el-input
                :model-value="line"
                placeholder="如：10-15 公里加收 ¥30"
                @update:model-value="(value) => patchServiceLine(form, index, value)"
              />
              <el-button link type="danger" @click="removeServiceLine(form, index)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addServiceLine(form)">+ 添加条目</el-button>
          </div>
        </el-form-item>
        <el-form-item label="标签收费">
          <div class="structured-list">
            <div
              v-for="(item, index) in parseServiceContent(form.content).tags"
              :key="`tag-${index}`"
              class="structured-row"
            >
              <el-input
                :model-value="item.name"
                placeholder="名称"
                @update:model-value="(value) => patchServiceTag(form, index, { name: value })"
              />
              <el-input
                :model-value="item.price"
                placeholder="价格"
                @update:model-value="(value) => patchServiceTag(form, index, { price: value })"
              />
              <el-button link type="danger" @click="removeServiceTag(form, index)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addServiceTag(form)">+ 添加标签</el-button>
          </div>
        </el-form-item>
      </template>

      <!-- 其他 JSON：格式化编辑 -->
      <el-form-item v-else-if="contentMode(form) === 'json'" label="内容(JSON)">
        <div class="json-toolbar">
          <el-button size="small" @click="prettyJson(form)">格式化</el-button>
          <el-button size="small" @click="compactJson(form)">压缩</el-button>
        </div>
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="12"
          class="json-textarea"
          placeholder="请输入 JSON 内容"
        />
      </el-form-item>

      <!-- 普通文本 -->
      <el-form-item v-else label="内容">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          maxlength="2000"
          show-word-limit
          placeholder="请输入规则内容"
        />
      </el-form-item>

      <el-form-item label="排序">
        <el-input-number v-model="form.sort" :min="0" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.state" style="width: 100%">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
      </el-form-item>
    </template>
  </CrudPage>
</template>

<style scoped>
.content-cell {
  max-height: 160px;
  overflow: auto;
  color: #303133;
}

.content-cell pre {
  margin: 0;
  font-family: inherit;
  font-size: 12px;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
}

.structured-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.structured-row {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 8px;
  align-items: center;
}

.structured-row.is-single {
  grid-template-columns: 1fr auto;
}

.json-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.json-textarea :deep(textarea) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  line-height: 1.5;
}
</style>
