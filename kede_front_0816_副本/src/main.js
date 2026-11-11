import Vue from 'vue'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import App from './App'
import store from './store'
import router from './router'
import directive from './directive' // directive
import plugins from './plugins' // plugins

import './assets/icons' // icon
import './permission' // permission control
import './tongji' // 百度统计
import { getDicts } from "@/api/system/dict/data";
import { getConfigKey } from "@/api/infra/config";
import { parseTime, resetForm, handleTree, addBeginAndEndTime, divide } from "@/utils/ruoyi";
import { isEmpty } from "@/utils";
import Pagination from "@/components/Pagination";
// 自定义表格工具扩展
import RightToolbar from "@/components/RightToolbar"
// 代码高亮插件
// import hljs from 'highlight.js'
// import 'highlight.js/styles/github-gist.css'
import { DICT_TYPE, getDictDataLabel, getDictDatas, getDictDatas2 } from "@/utils/dict";
import my_common from "@/utils/common";
import socketPublic from '@/utils/socketVuex.js'
import $ from 'jquery'
import userName from "@/components/userName";
import deptName from "@/components/deptName";

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.getDictDatas = getDictDatas
Vue.prototype.getDictDatas2 = getDictDatas2
Vue.prototype.getDictDataLabel = getDictDataLabel
Vue.prototype.DICT_TYPE = DICT_TYPE
Vue.prototype.handleTree = handleTree
Vue.prototype.addBeginAndEndTime = addBeginAndEndTime
Vue.prototype.divide = divide
Vue.prototype.isEmpty = isEmpty
Vue.prototype.my_common = my_common
Vue.prototype.$socketPublic = socketPublic
Vue.prototype.jq = $
window.$ = $
window.jQuery = $
Vue.component('userName', userName)
Vue.component('deptName', deptName)

// 全局组件挂载
Vue.component('DictTag', DictTag)
Vue.component('DocAlert', DocAlert)
Vue.component('Pagination', Pagination)
Vue.component('RightToolbar', RightToolbar)
// Element UI 无 ElText；占位页从 Vue3 模板拷贝了 <el-text>，注册兼容组件消除告警
Vue.component('ElText', {
  name: 'ElText',
  functional: true,
  render(h, ctx) {
    return h('span', ctx.data, ctx.children)
  }
})
// 字典标签组件
import DictTag from '@/components/DictTag'
import DocAlert from '@/components/DocAlert'
// 头部标签插件
import VueMeta from 'vue-meta'

Vue.use(directive)
Vue.use(plugins)
Vue.use(VueMeta)
// Vue.use(hljs.vuePlugin);

// bpmnProcessDesigner 需要引入
import MyPD from "@/components/bpmnProcessDesigner/package/index.js";

Vue.use(MyPD);
import "@/components/bpmnProcessDesigner/package/theme/index.scss";
import "bpmn-js/dist/assets/diagram-js.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css";

// Form Generator 组件需要使用到 tinymce
import Tinymce from '@/components/tinymce/index.vue'

Vue.component('tinymce', Tinymce)
import '@/assets/icons'
import request from "@/utils/request" // 实现 form generator 使用自己定义的 axios request 对象
// console.log(request)
Vue.prototype.$axios = request
import '@/styles/index.scss'

// 默认点击背景不关闭弹窗
import ElementUI from 'element-ui'

ElementUI.Dialog.props.closeOnClickModal.default = false

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
  size: localStorage.getItem("size") || "medium", // set element-ui default size
});

Vue.config.productionTip = false

// 预加载所有组件
// router.beforeEach((to, from, next) => {
//   to.matched.forEach(record => {
//     const components = record.components || { default: record.component };
//     for (const name in components) {
//       if (components[name]) {
//         import(`./components/${name}.vue`); // 注意这里实际上并不会阻止路由前进，但会触发组件加载
//       }
//     }
//   });
//   next();
// });

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
