import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import BasePagination from '@/components/common/BasePagination.vue'

describe('BasePagination', () => {
  it('emits update:currentPage', async () => {
    const wrapper = mount(BasePagination, {
      props: { currentPage: 1, pageSize: 10, total: 100 },
      global: {
        stubs: {
          'el-pagination': {
            name: 'ElPagination',
            template: '<button class="next" @click="$emit(\'update:current-page\', 2)" />',
            emits: ['update:current-page', 'update:page-size'],
          },
        },
      },
    })
    await wrapper.find('.next').trigger('click')
    expect(wrapper.emitted('update:currentPage')?.[0]).toEqual([2])
  })
})
