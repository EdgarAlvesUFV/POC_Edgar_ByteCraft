import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            // Redireciona requisições que começam com /api para o backend
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            }
        }
    }
})