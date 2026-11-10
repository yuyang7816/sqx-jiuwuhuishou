<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import CrudPage from '@/components/common/CrudPage.vue'
import { userAddressApi } from '@/api/modules/user-address'
import { formatUserLabel } from '@/utils/display'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const router = useRouter()
const crudRef = ref()
const filters = reactive({
  userName: '',
  phone: '',
  contactName: '',
  contactMobile: '',
  isDefault: '',
})

const columns = [
  { prop: 'user', label: '所属用户', minWidth: 170 },
  { prop: 'contactName', label: '联系人', width: 100 },
  { prop: 'contactMobile', label: '联系电话', width: 130 },
  { prop: 'addressText', label: '地址', minWidth: 240 },
  { prop: 'tag', label: '标签', width: 90 },
  { prop: 'isDefault', label: '默认', width: 80 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

function buildQuery() {
  return {
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
    contactName: filters.contactName || undefined,
    contactMobile: filters.contactMobile || undefined,
    isDefault: filters.isDefault === '' ? undefined : Number(filters.isDefault),
  }
}

function handleSearch(loadData) {
  loadData(buildQuery())
}

function handleReset(loadData) {
  Object.assign(filters, {
    userName: '',
    phone: '',
    contactName: '',
    contactMobile: '',
    isDefault: '',
  })
  loadData({})
}

function goUserDetail(row) {
  if (row.userId) {
    router.push(`/users/${row.userId}`)
  }
}
</script>

<template>
  <CrudPage
    ref="crudRef"
    title="用户地址"
    :api="userAddressApi"
    :columns="columns"
    :show-create="false"
    :show-edit="false"
    :show-delete="false"
  >
    <template #toolbar="{ loadData }">
      <el-form :inline="true" class="search-grid">
        <el-form-item label="用户名">
          <el-input v-model="filters.userName" clearable placeholder="用户名" />
        </el-form-item>
        <el-form-item label="用户手机">
          <el-input v-model="filters.phone" clearable placeholder="用户手机" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="filters.contactName" clearable placeholder="联系人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="filters.contactMobile" clearable placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="默认">
          <el-select v-model="filters.isDefault" clearable placeholder="全部" style="width: 110px">
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-space>
            <el-button type="primary" @click="handleSearch(loadData)">搜索</el-button>
            <el-button @click="handleReset(loadData)">重置</el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </template>
    <template #column-user="{ row }">{{ formatUserLabel(row) }}</template>
    <template #column-isDefault="{ row }">
      <el-tag :type="row.isDefault === 1 ? 'success' : 'info'" size="small">
        {{ row.isDefault === 1 ? '是' : '否' }}
      </el-tag>
    </template>
    <template #actions="{ row }">
      <el-button v-if="isAuth(PERMS.userList)" link type="primary" @click="goUserDetail(row)">用户详情</el-button>
    </template>
  </CrudPage>
</template>
