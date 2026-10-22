import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  base:"/desconecta/",
  plugins: [react()],
  test: {
    globals: true,                 // permite usar describe/it/expect sem importar
    environment: 'jsdom',          // necessário para testes React
    setupFiles: './setupTests.js', // setup global (jest-dom)
    coverage: {
      provider: 'istanbul',               // provider de cobertura
      reporter: ['text', 'lcov', 'html'], // relatórios de cobertura
      all: true,                          // inclui arquivos não testados
      include: ['src/**/*.{js,jsx,ts,tsx}'], // quais arquivos cobrir
    },
  },
})

