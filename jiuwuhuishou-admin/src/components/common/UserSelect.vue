<script setup>
import { ref, watch } from 'vue'
import { getAppUserInfo, getAppUserPage } from '@/api/modules/app-user'

const props = defineProps({
  modelValue: { type: [Number, String, null], default: null },
  placeholder: { type: String, default: '搜索用户名或手机号' },
  disabled: { type: Boolean, default: false },
  clearable: { type: Boolean, default: true },
})

const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const options = ref([])
const selectedLabel = ref('')

function formatOption(user) {
  const name = user.userName || '未命名'
  const phone = user.phone || ''
  return {
    value: user.userId,
    label: phone ? `${name}（${phone}）` : name,
    user,
  }
}

async function searchUsers(keyword = '') {
  loading.value = true
  try {
    const query = { page: 1, limit: 20 }
    const text = String(keyword || '').trim()
    if (/^\d{3,}$/.test(text)) {
      query.phone = text
    } else if (text) {
      query.username = text
    }
    const data = await getAppUserPage(query)
    options.value = (data.list || []).map(formatOption)
  } finally {
    loading.value = false
  }
}

async function loadSelectedUser(userId) {
  if (!userId) {
    selectedLabel.value = ''
    return
  }
  const exists = options.value.find((item) => item.value === userId)
  if (exists) {
    selectedLabel.value = exists.label
    return
  }
  loading.value = true
  try {
    const user = await getAppUserInfo(userId)
    if (user) {
      const option = formatOption(user)
      options.value = [option, ...options.value.filter((item) => item.value !== userId)]
      selectedLabel.value = option.label
    }
  } finally {
    loading.value = false
  }
}

function handleChange(value) {
  emit('update:modelValue', value ?? null)
  const option = options.value.find((item) => item.value === value)
  emit('change', option?.user || null)
}

watch(
  () => props.modelValue,
  (value) => {
    loadSelectedUser(value)
  },
  { immediate: true },
)
</script>

<template>
  <el-select
    :model-value="modelValue"
    filterable
    remote
    reserve-keyword
    :remote-method="searchUsers"
    :loading="loading"
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    style="width: 100%"
    @focus="searchUsers('')"
    @change="handleChange"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>
