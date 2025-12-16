// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'), // @ 指向 src
    },
  },
  server: {
    proxy: {
      // ⭐ 所有 /api 开头的请求都转发到后端 Spring Boot
      '/api': {
        target: 'http://localhost:8080', // 如果后端不是 8080，在这里改端口
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
