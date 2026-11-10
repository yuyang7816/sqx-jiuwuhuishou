<script setup>
import { nextTick, ref, watch } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '请输入内容' },
  minHeight: { type: String, default: '220px' },
})

const emit = defineEmits(['update:modelValue', 'blur'])

const editorRef = ref()
const syncing = ref(false)

const toolbar = [
  ['bold', 'italic', 'underline', 'strike'],
  [{ header: [1, 2, 3, false] }],
  [{ list: 'ordered' }, { list: 'bullet' }],
  [{ color: [] }, { background: [] }],
  ['link'],
  ['clean'],
]

function normalizeHtml(html = '') {
  return String(html || '')
    .replace(/<p><br><\/p>/gi, '')
    .trim()
}

function handleUpdate(content) {
  if (syncing.value) return
  emit('update:modelValue', content ?? '')
}

function syncFromModel(html) {
  const editor = editorRef.value
  if (!editor?.setHTML) return
  const next = html || ''
  const current = editor.getHTML?.() || ''
  if (normalizeHtml(current) === normalizeHtml(next)) return
  syncing.value = true
  editor.setHTML(next)
  nextTick(() => {
    syncing.value = false
  })
}

watch(
  () => props.modelValue,
  (value) => {
    syncFromModel(value)
  }
)

function handleReady() {
  syncFromModel(props.modelValue)
}
</script>

<template>
  <div class="rich-text-editor" :style="{ '--editor-min-height': minHeight }">
    <QuillEditor
      ref="editorRef"
      :content="modelValue"
      content-type="html"
      theme="snow"
      :toolbar="toolbar"
      :placeholder="placeholder"
      @update:content="handleUpdate"
      @ready="handleReady"
      @blur="emit('blur')"
    />
  </div>
</template>

<style scoped>
.rich-text-editor {
  width: 100%;
  background: #fff;
  border-radius: 4px;
}

.rich-text-editor :deep(.ql-toolbar.ql-snow) {
  border-radius: 4px 4px 0 0;
  border-color: var(--el-border-color);
}

.rich-text-editor :deep(.ql-container.ql-snow) {
  border-radius: 0 0 4px 4px;
  border-color: var(--el-border-color);
  min-height: var(--editor-min-height);
  font-size: 14px;
}

.rich-text-editor :deep(.ql-editor) {
  min-height: var(--editor-min-height);
  line-height: 1.6;
}

.rich-text-editor :deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
</style>
