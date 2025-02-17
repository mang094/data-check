// import { createApp } from 'vue'
// import './style.css'
// import App from './App.vue'
//
// createApp(App).mount('#app')

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

const app = createApp(App)
app.use(ElementPlus)
app.mount('#app')


// # 后端启动（在backend目录下）
// mvn spring-boot:run

// # 前端启动（在frontend/catalog-viewer目录下）
// npm run dev
//# 安装依赖
// npm install
//
// # 启动开发服务器
// npm run dev