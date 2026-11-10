<script setup>
import { computed, reactive, ref, watch } from 'vue'
import BasePagination from '@/components/common/BasePagination.vue'
import { useCrudPage } from '@/composables/useCrudPage'
import { messagesApi } from '@/api/modules/messages'
import { messageTypeMap } from '@/utils/display'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

/** 消息类型 Tab：请求同一分页接口，按 type 区分 */
const MODULES = [
  { key: '1', type: 1, label: '系统公告' },
  { key: '2', type: 2, label: '用户消息' },
  { key: '3', type: 3, label: '订单消息' },
]

const activeTab = ref('1')
const defaultQuery = reactive({ type: 1 })

const filters = reactive({
  title: '',
  userName: '',
  phone: '',
  dateRange: [],
})

const {
  loading,
  tableData,
  total,
  pageNum,
  pageSize,
  loadData,
  removeRow,
} = useCrudPage(messagesApi, { defaultQuery })

const currentModule = computed(
  () => MODULES.find((item) => item.key === activeTab.value) || MODULES[0],
)

const isSystemNotice = computed(() => currentModule.value.type === 1)
const isOrderMessage = computed(() => currentModule.value.type === 3)

function emptyFilters() {
  Object.assign(filters, {
    title: '',
    userName: '',
    phone: '',
    dateRange: [],
  })
}

function buildQuery() {
  const [startTime, endTime] = filters.dateRange || []
  return {
    type: currentModule.value.type,
    title: filters.title || undefined,
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
    startTime: startTime || undefined,
    endTime: endTime || undefined,
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData(buildQuery())
}

function handleReset() {
  emptyFilters()
  pageNum.value = 1
  loadData({ type: currentModule.value.type })
}

watch(activeTab, () => {
  emptyFilters()
  defaultQuery.type = currentModule.value.type
  pageNum.value = 1
  // 切换模块：清空筛选条件后按新 type 重新请求
  loadData({ type: currentModule.value.type })
})
</script>

<template>
  <el-card shadow="never" class="page-card messages-page">
    <template #header>
      <div class="card-header">
        <strong>消息管理</strong>
      </div>
    </template>

    <el-tabs v-model="activeTab" class="message-tabs">
      <el-tab-pane
        v-for="mod in MODULES"
        :key="mod.key"
        :label="mod.label"
        :name="mod.key"
      />
    </el-tabs>

    <el-form :inline="true" class="search-grid" @submit.prevent>
      <el-form-item label="标题">
        <el-input v-model="filters.title" clearable placeholder="标题" />
      </el-form-item>
      <el-form-item v-if="!isSystemNotice" label="用户名">
        <el-input v-model="filters.userName" clearable placeholder="用户名" />
      </el-form-item>
      <el-form-item v-if="!isSystemNotice" label="手机号">
        <el-input v-model="filters.phone" clearable placeholder="手机号" />
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="filters.dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 360px"
        />
      </el-form-item>
      <el-form-item>
        <el-space>
          <el-button v-if="isAuth(PERMS.messageList)" type="primary" @click="handleSearch">搜索</el-button>
          <el-button v-if="isAuth(PERMS.messageList)" @click="handleReset">重置</el-button>
        </el-space>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="id" label="编号" width="90" align="center" />
      <el-table-column label="用户名" min-width="120" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ isSystemNotice ? '全体用户' : (row.userName || '—') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" width="130">
        <template #default="{ row }">
          <span>{{ row.phone || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" min-width="180" show-overflow-tooltip />
      <el-table-column v-if="isOrderMessage" label="关联订单" width="110" align="center">
        <template #default="{ row }">
          <span>{{ row.dataId || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" width="110">
        <template #default="{ row }">
          <el-tag :type="messageTypeMap[row.type]?.type || 'info'">
            {{ messageTypeMap[row.type]?.label || currentModule.label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" @click="removeRow(row)">删除</el-button>
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
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.message-tabs {
  margin-bottom: 12px;
}

.message-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.search-grid {
  margin-bottom: 12px;
}
</style>
