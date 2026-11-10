import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import CrudPage from '@/components/common/CrudPage.vue'

vi.mock('@/utils/modal', () => ({
  confirmAction: vi.fn(async () => true),
  showError: vi.fn(),
  showSuccess: vi.fn(),
}))

vi.mock('@/utils/form', () => ({
  validateForm: vi.fn(async () => true),
}))

function createApi(overrides = {}) {
  return {
    page: vi.fn(async () => ({ list: [{ id: 1, name: 'demo' }], total: 1 })),
    info: vi.fn(async () => ({ id: 1, name: 'demo' })),
    save: vi.fn(async () => ({})),
    update: vi.fn(async () => ({})),
    delete: vi.fn(async () => ({})),
    ...overrides,
  }
}

const elementStubs = {
  'el-card': {
    template: '<div class="el-card"><header><slot name="header" /></header><slot /></div>',
  },
  'el-button': {
    template: '<button v-bind="$attrs" @click="$emit(\'click\')"><slot /></button>',
  },
  'el-table': {
    template: '<div class="el-table"><slot /><div v-for="(row, $index) in data" :key="$index"><slot name="default" :row="row" :$index="$index" /></div></div>',
    props: ['data'],
  },
  'el-table-column': {
    template: '<div class="el-table-column"><slot :row="{}" :$index="0" /></div>',
  },
  'el-space': { template: '<div class="el-space"><slot /></div>' },
  'el-form': {
    template: '<form class="el-form"><slot /></form>',
  },
  'el-dialog': {
    template:
      '<div class="el-dialog" v-if="modelValue"><div class="dialog-title">{{ title }}</div><slot /><slot name="footer" /></div>',
    props: ['modelValue', 'title'],
  },
  BasePagination: {
    name: 'BasePagination',
    template:
      '<div class="base-pagination"><button class="page-2" @click="$emit(\'update:currentPage\', 2)" /><button class="size-20" @click="$emit(\'update:pageSize\', 20)" /></div>',
    props: ['currentPage', 'pageSize', 'total'],
    emits: ['update:currentPage', 'update:pageSize'],
  },
}

async function mountCrudPage(api = createApi()) {
  const wrapper = mount(CrudPage, {
    props: {
      title: '演示',
      api,
      columns: [{ prop: 'name', label: '名称' }],
    },
    global: { stubs: elementStubs },
  })
  await flushPromises()
  return wrapper
}

describe('CrudPage', () => {
  it('renders title and loads table on mount', async () => {
    const api = createApi()
    const wrapper = await mountCrudPage(api)
    expect(wrapper.text()).toContain('演示')
    expect(api.page).toHaveBeenCalledWith({ page: 1, limit: 10 })
  })

  it('opens create dialog when clicking 新增', async () => {
    const wrapper = await mountCrudPage()
    await wrapper.find('button').trigger('click')
    await flushPromises()
    expect(wrapper.text()).toContain('新增演示')
  })

  it('reloads when pagination page changes', async () => {
    const api = createApi()
    const wrapper = await mountCrudPage(api)
    api.page.mockClear()
    await wrapper.find('.page-2').trigger('click')
    await flushPromises()
    expect(api.page).toHaveBeenCalledWith({ page: 2, limit: 10 })
  })

  it('exposes loadData for toolbar search', async () => {
    const api = createApi()
    const wrapper = await mountCrudPage(api)
    api.page.mockClear()
    await wrapper.vm.loadData({ keyword: 'kw' })
    expect(api.page).toHaveBeenCalledWith({ page: 1, limit: 10, keyword: 'kw' })
  })
})
