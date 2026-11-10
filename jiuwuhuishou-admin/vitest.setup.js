import { vi } from 'vitest'

vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
  },
}))

vi.mock('@/api/http', () => ({
  get: vi.fn(),
  post: vi.fn(async () => ({})),
  resolveBusinessResponse: (result) => Promise.resolve(result?.data ?? result),
  default: {},
}))
