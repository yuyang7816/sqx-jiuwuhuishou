<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import CrudPage from '@/components/common/CrudPage.vue'
import { referralUserCommissionApi } from '@/api/modules/referral-user-commission'
import { formatUserLabel } from '@/utils/display'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const router = useRouter()
const crudRef = ref()
const filters = reactive({ userName: '', phone: '' })

const columns = [
  { prop: 'id', label: '编号', width: 90, align: 'center' },
  { prop: 'rank', label: '佣金排名', width: 100, align: 'center' },
  { prop: 'user', label: '用户', minWidth: 180 },
  { prop: 'commission', label: '佣金余额', minWidth: 120 },
]

function buildQuery() {
  return {
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
  }
}

function handleSearch(loadData) {
  loadData(buildQuery())
}

function handleReset(loadData) {
  Object.assign(filters, { userName: '', phone: '' })
  loadData({})
}

function openUserDetail(row) {
  if (!row?.userId) return
  router.push(`/users/${row.userId}`)
}

/** 当前页内全局排名（列表已按佣金降序） */
function resolveRank(index) {
  const page = crudRef.value?.pageNum || 1
  const size = crudRef.value?.pageSize || 10
  return (page - 1) * size + index + 1
}
</script>

<template>
  <CrudPage
    ref="crudRef"
    title="用户佣金汇总"
    :api="referralUserCommissionApi"
    :columns="columns"
    :show-index="false"
    :show-create="false"
    :show-edit="false"
    :show-delete="false"
  >
    <template #toolbar="{ loadData }">
      <el-form :inline="true" class="search-grid">
        <el-form-item label="用户名">
          <el-input v-model="filters.userName" clearable placeholder="用户名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="filters.phone" clearable placeholder="手机号" />
        </el-form-item>
        <el-form-item>
          <el-space>
            <el-button type="primary" @click="handleSearch(loadData)">搜索</el-button>
            <el-button @click="handleReset(loadData)">重置</el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </template>
    <template #column-rank="{ $index }">
      <el-tag size="small" type="warning">第 {{ resolveRank($index) }} 名</el-tag>
    </template>
    <template #column-user="{ row }">{{ formatUserLabel(row) }}</template>
    <template #column-commission="{ row }">
      <span class="commission-amount">{{ row.commission ?? 0 }}</span>
    </template>
    <template #actions="{ row }">
      <el-button v-if="isAuth(PERMS.userList)" link type="primary" @click="openUserDetail(row)">用户详情</el-button>
    </template>
  </CrudPage>
</template>

<style scoped>
.commission-amount {
  font-weight: 600;
  color: var(--el-color-warning);
}
</style>
