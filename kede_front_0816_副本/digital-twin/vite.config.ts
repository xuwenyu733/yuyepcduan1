import { defineConfig } from 'vite'

// 嵌入主站 public/digital-twin，点击即开静态页，避免二次编译延迟
export default defineConfig({
  base: '/digital-twin/',
  build: {
    outDir: '../public/digital-twin',
    emptyOutDir: true,
    // 略提高 chunk 内联阈值，减少首屏请求数
    assetsInlineLimit: 4096,
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8787',
        changeOrigin: true,
      },
    },
  },
})
