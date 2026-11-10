<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppDialog from '@/components/common/AppDialog.vue'
import BasePagination from '@/components/common/BasePagination.vue'
import { deleteAppUserApi, getAppUserInfo, getAppUserPage, updateAppUserApi } from '@/api/modules/app-user'
import { appUserTypeMap, appUserTypeOptions, userPlatformMap, userPlatformOptions } from '@/utils/display'
import { validateForm } from '@/utils/form'
import { confirmAction, showError, showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const router = useRouter()
const loading = ref(false)
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const formRef = ref()
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const editingId = ref(null)

const typeDialogVisible = ref(false)
const typeDialogLoading = ref(false)
const typeForm = reactive({
  userId: null,
  userName: '',
  userType: 1,
})

const filters = reactive({
  username: '',
  phone: '',
  status: '',
  userType: '',
  platform: '',
  invitationCode: '',
  superiorInvitationCode: '',
})

const form = reactive({
  userId: null,
  userName: '',
  phone: '',
  status: 1,
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
}

function buildQuery() {
  return {
    page: pageNum.value,
    limit: pageSize.value,
    username: filters.username || undefined,
    phone: filters.phone || undefined,
    status: filters.status === '' ? undefined : Number(filters.status),
    userType: filters.userType === '' ? undefined : Number(filters.userType),
    platform: filters.platform || undefined,
    invitationCode: filters.invitationCode || undefined,
    superiorInvitationCode: filters.superiorInvitationCode || undefined,
  }
}

async function loadData() {
  loading.value = true
  try {
    const data = await getAppUserPage(buildQuery())
    tableData.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleReset() {
  Object.assign(filters, {
    username: '',
    phone: '',
    status: '',
    userType: '',
    platform: '',
    invitationCode: '',
    superiorInvitationCode: '',
  })
  pageNum.value = 1
  loadData()
}

function formatUserType(userType) {
  return appUserTypeMap[userType]?.label || '—'
}

function formatPlatform(platform) {
  if (!platform) return '—'
  return userPlatformMap[platform]?.label || platform
}

function openDetail(row) {
  router.push(`/users/${row.userId}`)
}

function openTypeEdit(row) {
  Object.assign(typeForm, {
    userId: row.userId,
    userName: row.userName || row.phone || '',
    userType: Number(row.userType) === 2 ? 1 : Number(row.userType ?? 1),
  })
  typeDialogVisible.value = true
}

async function handleTypeSubmit() {
  typeDialogLoading.value = true
  try {
    await updateAppUserApi({
      userId: typeForm.userId,
      userType: typeForm.userType,
    })
    showSuccess('用户类型已更新')
    typeDialogVisible.value = false
    loadData()
  } finally {
    typeDialogLoading.value = false
  }
}

async function openEdit(row) {
  editingId.value = row.userId
  dialogVisible.value = true
  dialogLoading.value = true
  try {
    const data = await getAppUserInfo(row.userId)
    Object.assign(form, {
      userId: data.userId,
      userName: data.userName,
      phone: data.phone,
      status: data.status,
    })
  } catch (error) {
    showError(error?.msg || error?.message || '加载用户失败')
    dialogVisible.value = false
  } finally {
    dialogLoading.value = false
  }
}

async function handleSubmit() {
  const valid = await validateForm(formRef)
  if (!valid) return
  dialogLoading.value = true
  try {
    await updateAppUserApi({
      userId: form.userId,
      userName: form.userName,
      phone: form.phone,
      status: form.status,
    })
    showSuccess('用户已更新')
    dialogVisible.value = false
    loadData()
  } finally {
    dialogLoading.value = false
  }
}

async function handleDelete(row) {
  await confirmAction(`确认删除用户“${row.userName || row.phone}”吗？`)
  await deleteAppUserApi({ userId: row.userId })
  showSuccess('用户已删除')
  loadData()
}

onMounted(loadData)
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <strong>用户中心</strong>
      </div>
    </template>
    <el-form :inline="true" class="search-grid">
      <el-form-item label="用户名">
        <el-input v-model="filters.username" clearable placeholder="用户名" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="filters.phone" clearable placeholder="手机号" />
      </el-form-item>
      <el-form-item label="用户类型">
        <el-select v-model="filters.userType" clearable placeholder="全部" style="width: 120px">
          <el-option
            v-for="item in appUserTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="注册渠道">
        <el-select v-model="filters.platform" clearable placeholder="全部" style="width: 120px">
          <el-option
            v-for="item in userPlatformOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="邀请码">
        <el-input v-model="filters.invitationCode" clearable placeholder="邀请码" />
      </el-form-item>
      <el-form-item label="邀请人邀请码">
        <el-input v-model="filters.superiorInvitationCode" clearable placeholder="邀请人邀请码" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.status" clearable placeholder="全部" style="width: 100px">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-space>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-space>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column label="编号" width="70" align="center">
        <template #default="{ $index }">{{ (pageNum - 1) * pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="userName" label="用户名" min-width="120" />
      <el-table-column prop="phone" label="手机号" min-width="130" />
      <el-table-column prop="userType" label="用户类型" min-width="160">
        <template #default="{ row }">
          <div class="user-type-cell">
            <el-tag :type="appUserTypeMap[row.userType]?.type || 'info'">{{ formatUserType(row.userType) }}</el-tag>
            <el-button
              v-if="isAuth(PERMS.userUpdate)"
              link
              type="primary"
              @click="openTypeEdit(row)"
            >修改</el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="platform" label="注册渠道" min-width="110">
        <template #default="{ row }">
          <el-tag v-if="row.platform" :type="userPlatformMap[row.platform]?.type || 'info'" size="small">
            {{ formatPlatform(row.platform) }}
          </el-tag>
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column prop="invitationCode" label="邀请码" min-width="120">
        <template #default="{ row }">{{ row.invitationCode || '—' }}</template>
      </el-table-column>
      <el-table-column prop="superiorInvitationCode" label="邀请人邀请码" min-width="130">
        <template #default="{ row }">{{ row.superiorInvitationCode || '—' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <el-table-column label="操作" min-width="210" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-if="isAuth(PERMS.userList)" link type="primary" @click="openDetail(row)">用户详情</el-button>
            <el-button v-if="isAuth(PERMS.userUpdate)" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="isAuth(PERMS.userDelete)" link type="danger" @click="handleDelete(row)">删除</el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>
    <BasePagination
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @update:current-page="pageNum = $event; loadData()"
      @update:page-size="pageSize = $event; pageNum = 1; loadData()"
    />
    <AppDialog v-model="dialogVisible" title="编辑用户" :loading="dialogLoading" @confirm="handleSubmit">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
    </AppDialog>
    <AppDialog v-model="typeDialogVisible" title="修改用户类型" :loading="typeDialogLoading" @confirm="handleTypeSubmit">
      <el-form :model="typeForm" label-width="96px">
        <el-form-item label="用户">
          <span>{{ typeForm.userName || typeForm.userId }}</span>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="typeForm.userType" style="width: 100%">
            <el-option
              v-for="item in appUserTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </AppDialog>
  </el-card>
</template>

<style scoped>
.user-type-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
