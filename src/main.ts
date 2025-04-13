import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
//全局权限校验
import '@/access'
import VueMasonry from 'vue-masonry-css'


const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)
app.use(VueMasonry)
app.mount('#app')
