const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  devServer: {
    port: 3004,
    proxy: {
      '/api': {
        target: 'http://localhost:8004',
        changeOrigin: true
      }
    }
  }
})
