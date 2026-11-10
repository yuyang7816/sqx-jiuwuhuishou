<script setup>
import UserSelect from '@/components/common/UserSelect.vue'
import { getAppUserPage } from '@/api/modules/app-user'
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: [Number, String, null], default: null },
  placeholder: { type: String, default: '搜索取件员工' },
  disabled: { type: Boolean, default: false },
  clearable: { type: Boolean, default: true },
})

const emit = defineEmits(['update:modelValue', 'change'])

const loading = ref(false)
const options = ref([])

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
    const query = { page: 1, limit: 20, userType: 3 }
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

watch(() => props.modelValue, () => searchUsers(''), { immediate: true })
</script>

<template>
  <el-select
    :model-value="modelValue"
    filterable
    remote
    reserve-keyword
    :placeholder="placeholder"
    :remote-method="searchUsers"
    :loading="loading"
    :disabled="disabled"
    :clearable="clearable"
    style="width: 100%"
    @update:model-value="emit('update:modelValue', $event); emit('change', $event)"
  >
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>
