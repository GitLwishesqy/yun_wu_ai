import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 2666,
    proxy: {
      '/api': {
        target: 'http://localhost:2667',
        changeOrigin: true,
      }
    }
  }
})
