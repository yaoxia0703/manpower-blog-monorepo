import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import { permissionDirective } from '@/directives/permission'
import * as Icons from '@element-plus/icons-vue'
// ルーターをインポートする
const app = createApp(App)
const pinia = createPinia()
for (const [key, component] of Object.entries(Icons)) {
  app.component(key, component)
}
// Pinia を使う
app.use(pinia)
// router guard 内の store より先に Pinia を有効化する
app.use(router)
// Element Plus を使う
app.use(ElementPlus)
// カスタムディレクティブを登録する
app.directive('permission', permissionDirective)

app.mount('#app')
