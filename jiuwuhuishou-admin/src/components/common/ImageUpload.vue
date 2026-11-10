<script setup>
import { computed, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { uploadFileApi } from '@/api/modules/system-config'
import { resolveFileUrl } from '@/utils/display'
import { showError } from '@/utils/modal'

const props = defineProps({
  modelValue: { type: String, default: '' },
  limit: { type: Number, default: 1 },
  accept: { type: String, default: 'image/*' },
  tip: { type: String, default: '点击上传图片' },
})

const emit = defineEmits(['update:modelValue'])

const uploading = ref(false)

const fileList = computed(() => {
  if (!props.modelValue) return []
  return props.modelValue
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
    .map((url, index) => ({ name: `image-${index + 1}`, url: resolveFileUrl(url), rawUrl: url }))
})

async function handleUpload(options) {
  uploading.value = true
  try {
    const result = await uploadFileApi(options.file)
    const path = result?.path || result?.relativePath || ''
    if (!path) {
      throw new Error('上传成功但未返回文件路径')
    }
    if (props.limit <= 1) {
      emit('update:modelValue', path)
    } else {
      const current = props.modelValue ? `${props.modelValue},${path}` : path
      emit('update:modelValue', current)
    }
    options.onSuccess?.(result)
  } catch (error) {
    showError(error?.msg || error?.message || '图片上传失败')
    options.onError?.(error)
  } finally {
    uploading.value = false
  }
}

function handleRemove(file) {
  const target = file.rawUrl || file.url
  const next = fileList.value
    .filter((item) => item.rawUrl !== target && item.url !== target)
    .map((item) => item.rawUrl)
    .join(',')
  emit('update:modelValue', next)
}

function beforeUpload(file) {
  if (!file.type?.startsWith('image/')) {
    showError('只能上传图片文件')
    return false
  }
  if (file.size / 1024 / 1024 > 5) {
    showError('图片大小不能超过 5MB')
    return false
  }
  return true
}
</script>

<template>
  <div class="image-upload">
    <el-upload
      list-type="picture-card"
      :accept="accept"
      :file-list="fileList"
      :limit="limit"
      :http-request="handleUpload"
      :before-upload="beforeUpload"
      :disabled="uploading"
      :on-remove="handleRemove"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>
    <div v-if="tip" class="image-upload__tip">{{ tip }}</div>
  </div>
</template>

<style scoped>
.image-upload__tip {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
