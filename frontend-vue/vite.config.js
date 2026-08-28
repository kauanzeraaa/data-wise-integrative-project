import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
  ],
  server:{
    open: true,
    port: 3000
  }

  
})

module.exports = {
  theme: {
    colors: {
      // Configure your color palette here
        pallete: {
        lightYellow: '#fdf2c5',
        midYellow: '#efe8b2',
        lightGreen: '#c6d1a6',
        midGreen: '#82bfa0',
        brown: '#7a6f5d',
        gray: '#333333',
        chumbo: '#212121',
      }
    }
  }
}
