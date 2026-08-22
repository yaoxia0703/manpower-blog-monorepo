import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import ja from 'element-plus/es/locale/lang/ja'
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
// Element Plus の組み込み表示を日本語に統一する
app.use(ElementPlus, { locale: ja })
// カスタムディレクティブを登録する
app.directive('permission', permissionDirective)

app.mount('#app')
