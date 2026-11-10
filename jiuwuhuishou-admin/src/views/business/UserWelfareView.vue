<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import CrudPage from '@/components/common/CrudPage.vue'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import {
  getUserSignRecordPageApi,
  updateUserWelfareCoinApi,
  userWelfareApi,
} from '@/api/modules/user-welfare'
import { formatUserLabel } from '@/utils/display'
import { calcListIndex, isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'
import { showSuccess } from '@/utils/modal'

const router = useRouter()
const crudRef = ref()
const filters = reactive({ userName: '', phone: '' })

const coinDialogVisible = ref(false)
const coinLoading = ref(false)
const coinForm = reactive({ userId: null, amount: 0, operator: 'incr' })

const signDrawerVisible = ref(false)
const signLoading = ref(false)
const signUserLabel = ref('')
const signUserId = ref(null)
const signList = ref([])
const signTotal = ref(0)
const signPage = ref(1)
const signPageSize = ref(10)

const columns = [
  { prop: 'user', label: '用户', minWidth: 180 },
  { prop: 'coinBalance', label: '积分余额', width: 110 },
  { prop: 'continuousSignDays', label: '连续签到', width: 100 },
  { prop: 'lastSignDate', label: '最近签到', width: 120 },
  { prop: 'updateTime', label: '更新时间', minWidth: 170 },
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

function openCoin(row) {
  Object.assign(coinForm, { userId: row.userId, amount: 0, operator: 'incr' })
  coinDialogVisible.value = true
}

async function submitCoin() {
  coinLoading.value = true
  try {
    await updateUserWelfareCoinApi({ ...coinForm })
    showSuccess('积分已调整')
    coinDialogVisible.value = false
    crudRef.value?.loadData(buildQuery())
  } finally {
    coinLoading.value = false
  }
}

async function openSignRecords(row) {
  signUserId.value = row.userId
  signUserLabel.value = formatUserLabel(row)
  signPage.value = 1
  signDrawerVisible.value = true
  await loadSignRecords()
}

async function loadSignRecords() {
  if (!signUserId.value) return
  signLoading.value = true
  try {
    const data = await getUserSignRecordPageApi({
      userId: signUserId.value,
      page: signPage.value,
      limit: signPageSize.value,
    })
    signList.value = data.list || []
    signTotal.value = data.total || 0
  } finally {
    signLoading.value = false
  }
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
    title="用户福利"
    :api="userWelfareApi"
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
    <template #column-user="{ row }">{{ formatUserLabel(row) }}</template>
    <template #actions="{ row }">
      <el-button v-if="isAuth(PERMS.userWelfareList)" link type="primary" @click="openSignRecords(row)">签到记录</el-button>
      <el-button v-if="isAuth(PERMS.userUpdateCoin)" link type="warning" @click="openCoin(row)">调积分</el-button>
      <el-button v-if="isAuth(PERMS.userList)" link @click="goUserDetail(row)">用户详情</el-button>
    </template>
  </CrudPage>

  <AppDialog v-model="coinDialogVisible" title="调整积分" :loading="coinLoading" @confirm="submitCoin">
    <el-form :model="coinForm" label-width="110px">
      <el-form-item label="积分数">
        <el-input-number v-model="coinForm.amount" :min="0" :precision="0" style="width: 100%" />
      </el-form-item>
      <el-form-item label="方向">
        <el-select v-model="coinForm.operator" style="width: 100%">
          <el-option label="增加" value="incr" />
          <el-option label="减少" value="decr" />
        </el-select>
      </el-form-item>
    </el-form>
  </AppDialog>

  <el-drawer v-model="signDrawerVisible" :title="`签到记录 · ${signUserLabel}`" size="520px">
    <el-table v-loading="signLoading" :data="signList" border>
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ calcListIndex(signPage, signPageSize, $index) }}</template>
      </el-table-column>
      <el-table-column prop="signDate" label="签到日期" min-width="120" />
      <el-table-column prop="coinReward" label="积分奖励" width="100">
        <template #default="{ row }">+ {{ row.coinReward || 0 }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" min-width="160" />
    </el-table>
    <BasePagination
      :current-page="signPage"
      :page-size="signPageSize"
      :total="signTotal"
      @update:current-page="signPage = $event; loadSignRecords()"
      @update:page-size="signPageSize = $event; signPage = 1; loadSignRecords()"
    />
  </el-drawer>
</template>
