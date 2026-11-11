import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { llmProxyPlugin } from './llm-proxy.js'

export default defineConfig(({ mode }) => {
  // 把 .env / .env.local 注入 process.env，供 llm-proxy 读取
  const env = loadEnv(mode, process.cwd(), '')
  for (const [k, v] of Object.entries(env)) {
    if (process.env[k] == null) process.env[k] = v
  }

  return {
    plugins: [vue(), llmProxyPlugin()],
    server: {
      port: 5190,
      host: '127.0.0.1',
      proxy: {
        '/proxy-api': {
          target: 'http://127.0.0.1:48181',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/proxy-api/, ''),
        },
      },
    },
  }
})
