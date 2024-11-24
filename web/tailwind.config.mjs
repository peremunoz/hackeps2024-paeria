/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}'],
  theme: {
    extend: {
      colors: {
        primary: '#B50027',
        secondary: '#999999',
        terciary: '#eeeeee',
        black: '#0C0C20',
        white: '#F6FAF9',
      },
    },
  },
  plugins: [],
}
