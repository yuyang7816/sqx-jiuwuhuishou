import { defineConfig, devices } from '@playwright/test'

const baseURL = process.env.PLAYWRIGHT_BASE_URL || 'http://127.0.0.1:5173'
const useSystemEdge = !process.env.CI && process.env.PLAYWRIGHT_CHANNEL !== 'chromium'

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: Boolean(process.env.CI),
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: process.env.CI ? [['list'], ['html', { open: 'never' }]] : 'list',
  timeout: 60_000,
  expect: {
    timeout: 10_000,
  },
  use: {
    baseURL,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    // 本地常缺 ffmpeg；需要失败录像时先 `npx playwright install ffmpeg`
    video: process.env.CI || process.env.PLAYWRIGHT_VIDEO === '1' ? 'retain-on-failure' : 'off',
    ...(useSystemEdge ? { channel: 'msedge' } : {}),
  },
  projects: [
    {
      name: useSystemEdge ? 'msedge' : 'chromium',
      use: { ...devices[useSystemEdge ? 'Desktop Edge' : 'Desktop Chrome'] },
    },
  ],
  webServer: {
    command: 'npm run dev -- --host 127.0.0.1 --port 5173',
    url: baseURL,
    reuseExistingServer: !process.env.CI,
    timeout: 120_000,
  },
})
