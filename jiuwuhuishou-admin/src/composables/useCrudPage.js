import { onMounted, reactive, ref } from 'vue'
import { confirmAction, showError, showSuccess } from '@/utils/modal'

export function useCrudPage(api, { defaultQuery = {}, idKey = 'id' } = {}) {
  const loading = ref(false)
  const dialogVisible = ref(false)
  const dialogLoading = ref(false)
  const tableData = ref([])
  const total = ref(0)
  const pageNum = ref(1)
  const pageSize = ref(10)
  const editingId = ref(null)
  const form = reactive({})
  const lastQuery = ref({})

  function resolveDefaultQuery() {
    if (typeof defaultQuery === 'function') {
      return defaultQuery() || {}
    }
    return defaultQuery || {}
  }

  async function loadData(extra) {
    loading.value = true
    try {
      // 显式传入 extra（含 {}）时替换查询条件；无参调用（翻页）保留上次条件
      if (arguments.length > 0) {
        lastQuery.value = extra && typeof extra === 'object' ? { ...extra } : {}
        pageNum.value = 1
      }
      const data = await api.page({
        page: pageNum.value,
        limit: pageSize.value,
        ...lastQuery.value,
        ...resolveDefaultQuery(),
      })
      tableData.value = data.list
      total.value = data.total
    } finally {
      loading.value = false
    }
  }

  function openCreate(initial = {}) {
    editingId.value = null
    Object.keys(form).forEach((key) => delete form[key])
    Object.assign(form, initial)
    dialogVisible.value = true
  }

  async function openEdit(row) {
    editingId.value = row[idKey]
    dialogLoading.value = true
    dialogVisible.value = true
    try {
      const data = await api.info(row[idKey])
      Object.keys(form).forEach((key) => delete form[key])
      Object.assign(form, data || row)
    } catch (error) {
      showError(error?.msg || error?.message || '加载详情失败')
      dialogVisible.value = false
    } finally {
      dialogLoading.value = false
    }
  }

  async function submitForm(extra = {}) {
    dialogLoading.value = true
    try {
      const payload = { ...form, ...extra }
      if (editingId.value) {
        await api.update({ ...payload, [idKey]: editingId.value })
        showSuccess('更新成功')
      } else {
        await api.save(payload)
        showSuccess('创建成功')
      }
      dialogVisible.value = false
      await loadData()
    } catch (error) {
      showError(error?.msg || error?.message || '保存失败')
    } finally {
      dialogLoading.value = false
    }
  }

  async function removeRow(row) {
    await confirmAction('确认删除该记录吗？')
    await api.delete({ [idKey]: row[idKey] })
    showSuccess('删除成功')
    await loadData()
  }

  onMounted(() => loadData())

  return {
    loading,
    dialogVisible,
    dialogLoading,
    tableData,
    total,
    pageNum,
    pageSize,
    editingId,
    form,
    loadData,
    openCreate,
    openEdit,
    submitForm,
    removeRow,
  }
}
