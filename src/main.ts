import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from "./router";
import './index.css'  // <-- import TailwindCSS

createApp(App).use(router).mount('#app')