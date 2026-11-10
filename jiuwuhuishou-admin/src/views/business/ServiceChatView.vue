<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import BasePagination from '@/components/common/BasePagination.vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import AppDialog from '@/components/common/AppDialog.vue'
import { serviceChatApi } from '@/api/modules/service-chat'
import {
  formatUserLabel,
  resolveFileUrl,
  serviceSessionStatusMap,
  tagMeta,
} from '@/utils/display'
import { showSuccess } from '@/utils/modal'
import { isAuth } from '@/utils/tool'
import { PERMS } from '@/constants/permissions'

const POLL_MS = 8000

const sessionLoading = ref(false)
const sessionList = ref([])
const sessionTotal = ref(0)
const sessionPage = ref(1)
const sessionPageSize = ref(20)
const filters = reactive({ userName: '', phone: '', status: 1, pendingReply: false })

const currentSession = ref(null)
const messageLoading = ref(false)
const messages = ref([])
const messageTotal = ref(0)
const draft = ref('')
const sending = ref(false)
const closing = ref(false)
const imageDialogVisible = ref(false)
const imageUrl = ref('')
const messagePanelRef = ref()
let pollTimer = null

const currentTitle = computed(() => {
  if (!currentSession.value) return '请选择会话'
  return formatUserLabel(currentSession.value)
})

function buildSessionQuery() {
  return {
    userName: filters.userName || undefined,
    phone: filters.phone || undefined,
    status: filters.status === '' || filters.status == null ? undefined : Number(filters.status),
    pendingReply: filters.pendingReply ? true : undefined,
  }
}

async function loadSessions() {
  sessionLoading.value = true
  try {
    const data = await serviceChatApi.pageSession({
      page: sessionPage.value,
      limit: sessionPageSize.value,
      ...buildSessionQuery(),
    })
    sessionList.value = data.list || []
    sessionTotal.value = data.total || 0
    if (currentSession.value) {
      const matched = sessionList.value.find((item) => item.id === currentSession.value.id)
      if (matched) currentSession.value = matched
    }
  } finally {
    sessionLoading.value = false
  }
}

function handleSearch() {
  sessionPage.value = 1
  loadSessions()
}

function handleReset() {
  Object.assign(filters, { userName: '', phone: '', status: 1, pendingReply: false })
  sessionPage.value = 1
  loadSessions()
}

async function selectSession(row) {
  currentSession.value = row
  await loadMessages(true)
  restartPoll()
}

async function loadMessages(scrollBottom = false) {
  if (!currentSession.value?.id) return
  messageLoading.value = true
  try {
    const data = await serviceChatApi.pageMessage({
      sessionId: currentSession.value.id,
      page: 1,
      limit: 200,
    })
    messages.value = data.list || []
    messageTotal.value = data.total || 0
    if (scrollBottom) {
      await nextTick()
      scrollToBottom()
    }
  } finally {
    messageLoading.value = false
  }
}

function scrollToBottom() {
  const el = messagePanelRef.value
  if (el) el.scrollTop = el.scrollHeight
}

async function sendText() {
  const content = draft.value.trim()
  if (!content || !currentSession.value?.id) return
  sending.value = true
  try {
    await serviceChatApi.saveMessage({
      sessionId: currentSession.value.id,
      messageType: 'text',
      content,
    })
    draft.value = ''
    showSuccess('已发送')
    await Promise.all([loadMessages(true), loadSessions()])
  } finally {
    sending.value = false
  }
}

function openImageDialog() {
  imageUrl.value = ''
  imageDialogVisible.value = true
}

async function sendImage() {
  if (!imageUrl.value || !currentSession.value?.id) return
  sending.value = true
  try {
    await serviceChatApi.saveMessage({
      sessionId: currentSession.value.id,
      messageType: 'image',
      content: imageUrl.value,
    })
    imageDialogVisible.value = false
    showSuccess('图片已发送')
    await Promise.all([loadMessages(true), loadSessions()])
  } finally {
    sending.value = false
  }
}

async function closeSession() {
  if (!currentSession.value?.id) return
  closing.value = true
  try {
    await serviceChatApi.closeSession(currentSession.value.id)
    showSuccess('会话已关闭')
    await loadSessions()
    const matched = sessionList.value.find((item) => item.id === currentSession.value.id)
    currentSession.value = matched || { ...currentSession.value, status: 2 }
  } finally {
    closing.value = false
  }
}

function restartPoll() {
  stopPoll()
  pollTimer = window.setInterval(async () => {
    if (!currentSession.value?.id) return
    await loadMessages(false)
    await loadSessions()
  }, POLL_MS)
}

function stopPoll() {
  if (pollTimer) {
    window.clearInterval(pollTimer)
    pollTimer = null
  }
}

watch(
  () => [sessionPage.value, sessionPageSize.value],
  () => loadSessions(),
)

onMounted(() => {
  loadSessions()
})

onBeforeUnmount(() => {
  stopPoll()
})
</script>

<template>
  <div class="service-chat-page">
    <aside class="session-pane">
      <div class="pane-header">
        <h3>客服会话</h3>
        <el-button link type="primary" @click="loadSessions">刷新</el-button>
      </div>
      <el-form class="session-filters" label-position="top" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="filters.userName" clearable placeholder="用户名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="filters.phone" clearable placeholder="手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部" style="width: 100%">
            <el-option label="进行中" :value="1" />
            <el-option label="已关闭" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="filters.pendingReply">仅看待回复</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div v-loading="sessionLoading" class="session-list">
        <div
          v-for="(item, index) in sessionList"
          :key="item.id"
          class="session-item"
          :class="{ active: currentSession?.id === item.id }"
          @click="selectSession(item)"
        >
          <div class="session-top">
            <span class="session-user">{{ index + 1 }}. {{ formatUserLabel(item) }}</span>
            <el-tag size="small" :type="tagMeta(serviceSessionStatusMap, item.status).type">
              {{ tagMeta(serviceSessionStatusMap, item.status).label }}
            </el-tag>
          </div>
          <div class="session-preview">
            <el-badge v-if="item.pendingReply" is-dot class="pending-dot">
              <span>{{ item.lastMessagePreview || '暂无消息' }}</span>
            </el-badge>
            <span v-else>{{ item.lastMessagePreview || '暂无消息' }}</span>
          </div>
          <div class="session-time">{{ item.lastMessageTime || item.createTime || '—' }}</div>
        </div>
        <el-empty v-if="!sessionLoading && !sessionList.length" description="暂无会话" :image-size="64" />
      </div>

      <BasePagination
        v-model:current-page="sessionPage"
        v-model:page-size="sessionPageSize"
        :total="sessionTotal"
        :page-sizes="[10, 20, 50]"
      />
    </aside>

    <section class="chat-pane">
      <div class="pane-header">
        <div>
          <h3>{{ currentTitle }}</h3>
          <p v-if="currentSession" class="sub">
            消息 {{ messageTotal }} 条 ·
            <el-tag size="small" :type="tagMeta(serviceSessionStatusMap, currentSession.status).type">
              {{ tagMeta(serviceSessionStatusMap, currentSession.status).label }}
            </el-tag>
          </p>
        </div>
        <div v-if="currentSession" class="header-actions">
          <el-button :loading="messageLoading" @click="loadMessages(true)">刷新消息</el-button>
          <el-button
            v-if="currentSession.status === 1"
            type="warning"
            plain
            :loading="closing"
            @click="closeSession"
          >
            关闭会话
          </el-button>
        </div>
      </div>

      <div v-if="!currentSession" class="chat-empty">
        <el-empty description="从左侧选择会话开始回复" />
      </div>

      <template v-else>
        <div ref="messagePanelRef" v-loading="messageLoading" class="message-panel">
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="message-row"
            :class="msg.senderType === 2 ? 'is-service' : 'is-user'"
          >
            <div class="bubble">
              <template v-if="msg.messageType === 'image'">
                <el-image
                  :src="resolveFileUrl(msg.content)"
                  fit="cover"
                  style="max-width: 220px; max-height: 220px"
                  :preview-src-list="[resolveFileUrl(msg.content)]"
                />
              </template>
              <template v-else>
                <div class="text">{{ msg.content }}</div>
              </template>
              <div class="meta">
                {{ msg.senderType === 2 ? '客服' : '用户' }} · {{ msg.createTime || '' }}
              </div>
            </div>
          </div>
          <el-empty v-if="!messageLoading && !messages.length" description="暂无消息" :image-size="64" />
        </div>

        <div class="composer">
          <el-input
            v-model="draft"
            type="textarea"
            :rows="3"
            placeholder="输入回复内容，Enter 发送（Shift+Enter 换行）"
            @keydown.enter.exact.prevent="sendText"
          />
          <div class="composer-actions">
            <el-button v-if="isAuth(PERMS.serviceChatSend)" :icon="Picture" @click="openImageDialog">发图片</el-button>
            <el-button v-if="isAuth(PERMS.serviceChatSend)" type="primary" :loading="sending" :disabled="!draft.trim()" @click="sendText">
              发送
            </el-button>
          </div>
        </div>
      </template>
    </section>
  </div>

  <AppDialog
    v-model="imageDialogVisible"
    title="发送图片"
    :loading="sending"
    @confirm="sendImage"
  >
    <ImageUpload v-model="imageUrl" tip="上传客服回复图片" />
  </AppDialog>
</template>

<style scoped>
.service-chat-page {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 12px;
  height: calc(100vh - 120px);
  min-height: 560px;
}

.session-pane,
.chat-pane {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pane-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.pane-header h3 {
  margin: 0;
  font-size: 16px;
}

.sub {
  margin: 4px 0 0;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.session-filters {
  padding: 12px 16px 0;
  border-bottom: 1px solid var(--el-border-color-extra-light);
}

.session-list {
  flex: 1;
  overflow: auto;
  padding: 8px;
}

.session-item {
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 6px;
}

.session-item:hover,
.session-item.active {
  background: var(--el-color-primary-light-9);
}

.session-top {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  align-items: center;
}

.session-user {
  font-weight: 600;
  font-size: 13px;
}

.session-preview {
  margin-top: 6px;
  color: var(--el-text-color-regular);
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-time {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.pending-dot {
  width: 100%;
}

.chat-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.message-panel {
  flex: 1;
  overflow: auto;
  padding: 16px;
  background: var(--el-fill-color-blank);
}

.message-row {
  display: flex;
  margin-bottom: 12px;
}

.message-row.is-user {
  justify-content: flex-start;
}

.message-row.is-service {
  justify-content: flex-end;
}

.bubble {
  max-width: 68%;
  padding: 10px 12px;
  border-radius: 10px;
  background: #fff;
  border: 1px solid var(--el-border-color-lighter);
}

.message-row.is-service .bubble {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
}

.bubble .text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.5;
}

.bubble .meta {
  margin-top: 6px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.composer {
  border-top: 1px solid var(--el-border-color-lighter);
  padding: 12px 16px 16px;
}

.composer-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.header-actions {
  display: flex;
  gap: 8px;
}
</style>
