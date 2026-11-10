<script setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CrudPage from '@/components/common/CrudPage.vue'
import RichTextEditor from '@/components/common/RichTextEditor.vue'
import { helpArticleApi } from '@/api/modules/help-article'
import { helpCategoryApi } from '@/api/modules/help-category'
import { PERMS } from '@/constants/permissions'

const route = useRoute()
const router = useRouter()

const TAB_CATEGORY = 'categories'
const TAB_ARTICLE = 'articles'
const validTabs = new Set([TAB_CATEGORY, TAB_ARTICLE])

const activeTab = ref(
  validTabs.has(route.query.tab) ? route.query.tab : TAB_CATEGORY
)

const categoryOptions = ref([])
const filters = reactive({ title: '', categoryId: '' })
const articleReady = ref(false)
const articleCrudRef = ref()

const categoryColumns = [
  { prop: 'name', label: '分类名称', minWidth: 160 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

const articleColumns = [
  { prop: 'title', label: '标题', minWidth: 160 },
  { prop: 'categoryId', label: '分类', width: 120 },
  { prop: 'content', label: '内容', minWidth: 280 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'createTime', label: '创建时间', minWidth: 170 },
]

const articleFormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [
    {
      required: true,
      trigger: 'blur',
      validator: (_rule, value, callback) => {
        const text = String(value || '')
          .replace(/<[^>]*>/g, '')
          .replace(/&nbsp;/gi, ' ')
          .trim()
        if (!text) {
          callback(new Error('请输入内容'))
          return
        }
        callback()
      },
    },
  ],
}

const categoryNameMap = computed(() => {
  const map = {}
  for (const item of categoryOptions.value) {
    map[item.id] = item.name
  }
  return map
})

const articleCreateDefaults = computed(() => ({
  sort: 1,
  categoryId: filters.categoryId || categoryOptions.value[0]?.id || undefined,
}))

async function loadCategoryOptions() {
  const data = await helpCategoryApi.page({ page: 1, limit: 200 })
  categoryOptions.value = data.list || []
}

function applyDefaultCategoryFilter() {
  if (!categoryOptions.value.length) {
    filters.categoryId = ''
    return
  }
  const exists = categoryOptions.value.some((item) => item.id === filters.categoryId)
  if (!filters.categoryId || !exists) {
    filters.categoryId = categoryOptions.value[0].id
  }
}

function buildArticleQuery() {
  return {
    title: filters.title || undefined,
    categoryId: filters.categoryId || undefined,
  }
}

function resetArticleFilters() {
  filters.title = ''
  filters.categoryId = categoryOptions.value[0]?.id || ''
}

function resolveCategoryName(row) {
  return categoryNameMap.value[row.categoryId] || row.categoryName || '—'
}

const articleQuery = computed(() => buildArticleQuery())

async function prepareArticleTab() {
  await loadCategoryOptions()
  applyDefaultCategoryFilter()
  articleReady.value = true
  await nextTick()
  articleCrudRef.value?.loadData(buildArticleQuery())
}

watch(activeTab, (tab) => {
  const nextQuery = { ...route.query, tab }
  if (route.query.tab !== tab) {
    router.replace({ query: nextQuery })
  }
  if (tab === TAB_ARTICLE) {
    prepareArticleTab()
  }
})

watch(
  () => route.query.tab,
  (tab) => {
    if (validTabs.has(tab) && activeTab.value !== tab) {
      activeTab.value = tab
    }
  }
)

onMounted(async () => {
  await loadCategoryOptions()
  if (activeTab.value === TAB_ARTICLE) {
    await prepareArticleTab()
  }
})
</script>

<template>
  <el-card shadow="never" class="page-card">
    <template #header>
      <div class="card-header">
        <div>
          <strong>帮助中心</strong>
          <div class="desc">维护帮助分类与帮助文章，用户端帮助中心同步读取。</div>
        </div>
      </div>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="帮助分类" :name="TAB_CATEGORY" lazy>
        <CrudPage
          title="帮助分类"
          :api="helpCategoryApi"
          :columns="categoryColumns"
          :create-defaults="{ sort: 1, clientType: 1 }"
          :create-perm="PERMS.helpAdd"
          :edit-perm="PERMS.helpUpdate"
          :delete-perm="PERMS.helpDelete"
        >
          <template #form="{ form }">
            <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
            <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
          </template>
        </CrudPage>
      </el-tab-pane>

      <el-tab-pane label="帮助文章" :name="TAB_ARTICLE">
        <CrudPage
          v-if="articleReady"
          ref="articleCrudRef"
          title="帮助文章"
          :api="helpArticleApi"
          :columns="articleColumns"
          :create-defaults="articleCreateDefaults"
          :default-query="articleQuery"
          :form-rules="articleFormRules"
          dialog-width="760px"
          :create-perm="PERMS.helpAdd"
          :edit-perm="PERMS.helpUpdate"
          :delete-perm="PERMS.helpDelete"
        >
          <template #toolbar="{ loadData }">
            <el-form inline>
              <el-form-item label="标题"><el-input v-model="filters.title" clearable /></el-form-item>
              <el-form-item label="分类">
                <el-select v-model="filters.categoryId" style="width: 160px">
                  <el-option
                    v-for="item in categoryOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadData(buildArticleQuery())">查询</el-button>
                <el-button @click="resetArticleFilters(); loadData(buildArticleQuery())">重置</el-button>
              </el-form-item>
            </el-form>
          </template>
          <template #column-categoryId="{ row }">
            {{ resolveCategoryName(row) }}
          </template>
          <template #column-content="{ row }">
            <div v-if="row.content" class="article-content-html" v-html="row.content" />
            <span v-else>—</span>
          </template>
          <template #form="{ form, editingId }">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" maxlength="128" />
            </el-form-item>
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" style="width: 100%">
                <el-option
                  v-for="item in categoryOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="内容" prop="content">
              <RichTextEditor
                :key="editingId || 'create'"
                v-model="form.content"
                placeholder="请输入帮助文章内容"
              />
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>
          </template>
        </CrudPage>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}
.desc {
  margin-top: 4px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.article-content-html {
  max-height: 88px;
  overflow: hidden;
  font-size: 13px;
  line-height: 1.5;
  color: var(--el-text-color-regular);
  word-break: break-word;
}
.article-content-html :deep(p) {
  margin: 0 0 4px;
}
.article-content-html :deep(p:last-child) {
  margin-bottom: 0;
}
.article-content-html :deep(img) {
  max-width: 100%;
  height: auto;
  vertical-align: middle;
}
.article-content-html :deep(ul),
.article-content-html :deep(ol) {
  margin: 0;
  padding-left: 1.2em;
}
</style>
