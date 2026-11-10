<script setup>
import { ref } from 'vue'
import CrudPage from '@/components/common/CrudPage.vue'
import { serviceQuickQuestionApi, serviceShortcutApi } from '@/api/modules/service-config'

const activeTab = ref('questions')

const questionColumns = [
  { prop: 'id', label: '编号', width: 90 },
  { prop: 'question', label: '问题', minWidth: 180 },
  { prop: 'content', label: '内容', minWidth: 280 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

const shortcutColumns = [
  { prop: 'id', label: '编号', width: 90 },
  { prop: 'content', label: '提示词', minWidth: 220 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

const questionFormRules = {
  question: [{ required: true, message: '请输入问题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

const shortcutFormRules = {
  content: [{ required: true, message: '请输入提示词', trigger: 'blur' }],
}
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <strong>客服配置</strong>
      </div>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="快捷问题配置" name="questions" lazy>
        <CrudPage
          title="快捷问题"
          :api="serviceQuickQuestionApi"
          :columns="questionColumns"
          :form-rules="questionFormRules"
          :create-defaults="{ sort: 1, showWelcome: 1, content: '' }"
        >
          <template #column-content="{ row }">
            <span class="content-preview">{{ row.content }}</span>
          </template>
          <template #form="{ form }">
            <el-form-item label="问题" required>
              <el-input v-model="form.question" maxlength="255" placeholder="如：如何预约上门回收？" />
            </el-form-item>
            <el-form-item label="内容" required>
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="5"
                maxlength="2000"
                show-word-limit
                placeholder="点击该问题后的自动回复内容"
              />
            </el-form-item>
            <el-form-item label="欢迎区展示">
              <el-radio-group v-model="form.showWelcome">
                <el-radio :label="1">展示</el-radio>
                <el-radio :label="0">仅自动回复</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>
          </template>
        </CrudPage>
      </el-tab-pane>

      <el-tab-pane label="快捷提示词配置" name="shortcuts" lazy>
        <CrudPage
          title="快捷提示词"
          :api="serviceShortcutApi"
          :columns="shortcutColumns"
          :form-rules="shortcutFormRules"
          :create-defaults="{ sort: 1, content: '' }"
        >
          <template #form="{ form }">
            <el-form-item label="提示词" required>
              <el-input v-model="form.content" maxlength="255" placeholder="如：价格查询、转人工" />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>
          </template>
        </CrudPage>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<style scoped>
.page-card {
  min-height: calc(100vh - 120px);
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.content-preview {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
