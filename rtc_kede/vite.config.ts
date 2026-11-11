import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { visualizer } from 'rollup-plugin-visualizer';
import basicSsl from '@vitejs/plugin-basic-ssl'
const path = require('path');

// https://vitejs.dev/config/
export default defineConfig({
  // Static Resource Base Path base: './' || '',
  base: process.env.NODE_ENV === 'production' ? './' : '/',
  server: {
    host: true,
    port: 9391,
    strictPort: true,
    // 允许渔业前端 iframe 嵌入（跨端口）
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
  },
  optimizeDeps: {
    include: ['@tencentcloud/roomkit-web-vue3', 'trtc-cloud-js-sdk']
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  build: {
    target: 'esnext',
    rollupOptions: {
      output: {
        // Custom Split Strategy
      },
    },
  },
  plugins: [
    vue({
      // Explanation: Solved the problem of introducing the generation of userSig files.
      // reactivityTransform: true,
    }),
    // basicSsl(),
    visualizer({
      // open: true,
    }),
  ],
});
