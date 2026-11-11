import Vue from 'vue'
import Router from 'vue-router'
/* Layout */
import Layout from '@/layout'

Vue.use(Router)

/**
 * Note: 路由配置项
 *
 * hidden: true                   // 【重要】当设置 true 的时候该路由不会再侧边栏出现 如 401，login 等页面，或者如一些编辑页面 /edit/1
 * alwaysShow: true               // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * path: '/login',                // 【重要】访问的 URL 路径
 * component: Layout,             // 【重要】对应的组件；也可以是 (resolve) => require(['@/views/login'], resolve),
 * redirect: noRedirect           // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'             // 【重要】设定路由的名字，一定要填写不然使用 <keep-alive> 时会出现各种问题
 * meta : {
    noCache: true                // 【重要】如果设置为 true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'               // 【重要】设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'             // 【重要】设置该路由的图标，对应路径 src/assets/icons/svg
    breadcrumb: false            // 如果设置为 false，则不会在 breadcrumb 面包屑中显示
    activeMenu: '/system/user'   // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: (resolve) => require(['@/views/redirect'], resolve)
      }
    ]
  },
  {
    path: '/login',
    component: (resolve) => require(['@/views/login'], resolve),
    hidden: true
  },
  {
    path: '/daping1',
    component: (resolve) => require(['@/views/daping1'], resolve),
    hidden: true
  },
  {
    path: '/daping2',
    component: (resolve) => require(['@/views/daping2'], resolve),
    hidden: true
  },
  {
    path: '/daping3',
    component: (resolve) => require(['@/views/daping3'], resolve),
    hidden: true
  },
  {
    path: '/daping4',
    component: (resolve) => require(['@/views/daping4'], resolve),
    hidden: true
  },
  {
    path: '/index_daping1',
    component: (resolve) => require(['@/views/index_daping1'], resolve),
    hidden: true
  },
  {
    path: '/index_daping1_son',
    component: (resolve) => require(['@/views/index_daping1_son_page'], resolve),
    hidden: true
  },
  {
    path: '/water_quality',
    component: (resolve) => require(['@/views/waterQuality'], resolve),
    hidden: true
  },
  {
    path: '/mission_planning',
    component: (resolve) => require(['@/views/MissionPlanning'], resolve),
    name: 'MissionPlanning',
    hidden: true,
    meta: { title: '任务与计划' }
  },
  // 旧版任务计划录入（localStorage 人工录入）
  {
    path: '/task_plan_manual',
    component: (resolve) => require(['@/views/keyshow/taskPlan'], resolve),
    name: 'TaskPlanManual',
    hidden: true,
    meta: { title: '任务计划录入' }
  },
  // 兼容旧入口：默认进新版协同计划
  {
    path: '/task_plan',
    redirect: to => ({ path: '/mission_planning', query: to.query }),
    hidden: true
  },
  {
    path: '/stock_distribution',
    component: (resolve) => require(['@/views/keyshow/stockDistribution'], resolve),
    name: 'StockDistributionPage',
    hidden: true,
    meta: { title: '存量分布' }
  },
  {
    path: '/alert_monitor',
    component: (resolve) => require(['@/views/alertMonitor'], resolve),
    name: 'AlertMonitor',
    hidden: true,
    meta: { title: 'AI 报警监控' }
  },
  {
    path: '/fish-disease',
    component: (resolve) => require(['@/views/iot/fishDisease'], resolve),
    hidden: true,
    meta: { title: '鱼病诊断' }
  },
  // 旧菜单绝对 path 兼容：阈值已迁到「综合模块」下
  {
    path: '/threshold-settings',
    redirect: '/experimental/threshold-settings',
    hidden: true
  },
  {
    path: '/device/zk-device-video',
    component: (resolve) => require(['@/views/fish/zkDevice/video'], resolve),
    hidden: true,
    meta: { title: '视频监控' }
  },
  {
    path: '/yangzhi/zk-device-video',
    redirect: '/device/zk-device-video',
    hidden: true
  },
  {
    path: '/device_monitor',
    component: (resolve) => require(['@/views/deviceMonitor'], resolve),
    hidden: true,
    meta: { title: '设备管理' }
  },
  {
    path: '/oa_score',
    component: (resolve) => require(['@/views/keyshow/oaScore'], resolve),
    hidden: true,
    meta: { title: '知识库评分' }
  },
  {
    path: '/knowledge/oa-score',
    component: (resolve) => require(['@/views/keyshow/oaScore'], resolve),
    hidden: true,
    meta: { title: '知识库·OA评分' }
  },
  // 123：知识树管理 CRUD（与 system_menu 5079 对齐；开发服静态可直达，不对普通用户开菜单）
  {
    path: '/knowledge/zk-konwledge-tree',
    component: Layout,
    hidden: true,
    meta: { title: '知识树管理', roles: ['admin', 'super_admin', 'ke_admin', 'kede_group_leader', 'expert'] },
    children: [
      {
        path: '',
        component: (resolve) => require(['@/views/fish/zkKonwledgeTree/index'], resolve),
        name: 'ZkKonwledgeTreeManage',
        meta: {
          title: 'RAG知识树',
          noCache: true,
          roles: ['admin', 'super_admin', 'ke_admin', 'kede_group_leader', 'expert']
        }
      }
    ]
  },
  // 鱼病壳内入口：/#/fish-disease?tab=knowledgeTree
  {
    path: '/fish-disease/knowledge-tree',
    redirect: '/fish-disease?tab=knowledgeTree',
    hidden: true
  },
  {
    path: '/fish-disease/knowledge-manage',
    redirect: '/fish-disease?tab=knowledgeManage',
    hidden: true
  },
  {
    path: '/experimental_system',
    component: (resolve) => require(['@/views/iot/experimentData'], resolve),
    hidden: true,
    meta: { title: '实验系统' }
  },
  {
    path: '/index_daping2',
    component: (resolve) => require(['@/views/index_daping2'], resolve),
    hidden: true
  },
  {
    path: '/index_daping3',
    component: (resolve) => require(['@/views/index_daping3'], resolve),
    hidden: true
  },
  {
    path: '/index_daping4',
    component: (resolve) => require(['@/views/index_daping4'], resolve),
    hidden: true
  },
  {
    path: '/sso',
    component: (resolve) => require(['@/views/sso'], resolve),
    hidden: true
  },
  {
    path: '/social-login',
    component: (resolve) => require(['@/views/socialLogin'], resolve),
    hidden: true
  },
  {
    path: '/404',
    component: (resolve) => require(['@/views/error/404'], resolve),
    hidden: true
  },
  {
    path: '/401',
    component: (resolve) => require(['@/views/error/401'], resolve),
    hidden: true
  },

  {
    path: '/keyshow',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'stockDistribution',
        component: (resolve) => require(['@/views/keyshow/stockDistribution'], resolve),
        name: 'StockDistribution',
        meta: { title: '存量分布', icon: 'chart' }
      },
      {
        path: 'taskPlan',
        component: (resolve) => require(['@/views/keyshow/taskPlan'], resolve),
        name: 'TaskPlanEntry',
        meta: { title: '任务计划录入', icon: 'list' }
      },
      {
        path: 'oaScore',
        component: (resolve) => require(['@/views/keyshow/oaScore'], resolve),
        name: 'OaScore',
        meta: { title: '知识库评分', icon: 'education' }
      },
      {
        path: 'knowledgeOaScore',
        component: (resolve) => require(['@/views/keyshow/oaScore'], resolve),
        name: 'KnowledgeOaScore',
        meta: { title: '知识库·OA评分', icon: 'education' }
      },
      {
        path: 'iframePage',
        component: (resolve) => require(['@/views/iframeSection'], resolve),
        name: 'iframe',
        meta: { title: 'iframe', icon: 'video-camera', iframe_prop: 'item1' }
      },
      {
        path: 'iframePage2',
        component: (resolve) => require(['@/views/iframeSection'], resolve),
        name: 'iframe2',
        meta: { title: 'iframe2', icon: 'video-camera', iframe_prop: 'item2' }
      },
      {
        path: 'iframePage3',
        component: (resolve) => require(['@/views/iframeSection'], resolve),
        name: 'iframe3',
        meta: { title: 'iframe3', icon: 'video-camera', iframe_prop: 'item3' }
      },
      {
        // 旧 healthStatus 入口统一进鱼类健康状态页
        path: 'healthStatus',
        redirect: { path: '/keyshow/fishHealthStatus' }
      },
      {
        // 鱼类健康状态独立路由（页签标题勿再用「死亡量存活率」）
        path: 'fishHealthStatus',
        component: (resolve) => require(['@/views/fish/zkDeathSurvivalRate/system'], resolve),
        name: '鱼类健康状态',
        meta: { title: '鱼类健康状态', icon: 'video-camera', defaultActiveTab: 'health' }
      },
      {
        path: 'biologicalAssetValuation',
        component: (resolve) => require(['@/views/fish/zkBiologicalAsset/system'], resolve),
        name: '生物资产评估',
        meta: { title: '生物资产评估', icon: 'video-camera' }
      },
      {
        // 兼容旧入口：健康度评估 → 鱼类健康状态（勿跳鱼病诊断）
        path: 'fishHealthValuation',
        redirect: { path: '/keyshow/fishHealthStatus' }
      },
      {
        path: 'woorkplan',
        component: (resolve) => require(['@/views/fish/zkWorkPlan/index'], resolve),
        name: '工作计划与目标',
        meta: { title: '工作计划与目标', icon: 'video-camera' }
      },
      {
        path: 'precisionfeeding',
        component: (resolve) => require(['@/views/fish/zkPrecisionFeeding/system'], resolve),
        name: '精准投喂',
        meta: { title: '精准投喂', icon: 'video-camera' }
      },
      {
        path: 'inspectionreport',
        component: (resolve) => require(['@/views/fish/zkInspectionReport/index'], resolve),
        name: '智能巡检报告',
        meta: { title: '智能巡检报告', icon: 'video-camera' }
      },
      {
        path: 'zkDeathSurvivalRate',
        component: (resolve) => require(['@/views/fish/zkDeathSurvivalRate/system'], resolve),
        name: '死亡量存活率',
        meta: { title: '死亡量存活率', icon: 'video-camera', defaultActiveTab: 'mortality' }
      },
      {
        path: 'zkGrowthModel',
        component: (resolve) => require(['@/views/fish/zkgrowdailyrecord/system'], resolve),
        name: '鱼生长大模型',
        meta: { title: '鱼生长大模型', icon: 'video-camera' }
      },
      // {
      //   path: 'pondwarn',
      //   component: (resolve) => require(['@/views/fish/zkPondWarn/index'], resolve),
      //   name: '设备告警',
      //   meta: { title: '设备告警', icon: 'video-camera' }
      // },
      {
        path: 'overallCost',
        component: (resolve) => require(['@/views/keyshow/overallCost'], resolve),
        name: '全场成本',
        meta: { title: '全场成本', icon: 'video-camera' }
      },
      {
        path: 'zkDevice',
        component: (resolve) => require(['@/views/fish/zkDevice/index'], resolve),
        name: '设备管理',
        meta: { title: '设备管理', icon: 'video-camera' }
      },
      {
        // 水质情况页空数据引导入口；数据表 zk_device_record
        path: 'waterQualityManage',
        component: (resolve) => require(['@/views/fish/zkDeviceRecord/index'], resolve),
        name: '水质数据管理',
        meta: { title: '水质数据管理', icon: 'video-camera' }
      },
      {
        path: 'zkDeviceAlarm',
        component: (resolve) => require(['@/views/fish/zkDeviceAlarm/index'], resolve),
        name: '设备告警',
        meta: { title: '设备告警', icon: 'video-camera' },
        children: [
          {
            path: 'alarmHolder',
            component: (resolve) => require(['@/views/fish/zkDeviceAlarm/alarmHolder'], resolve),
            name: '告警处理',
            meta: {
              title: '告警处理',
              activeMenu: '/fish/zkDeviceAlarm',
              hidden: true
            }
          }
        ]
      },
      {
        path: 'IntelligentDiagnosis',
        component: (resolve) => require(['@/views/fish/zkIntelligentDiagnosis/index'], resolve),
        name: '智能诊断',
        meta: { title: '智能诊断', icon: 'video-camera' }
      },
      {
        path: 'InspectionList',
        component: (resolve) => require(['@/views/keyshow/InspectionList'], resolve),
        name: '巡检列表',
        meta: { title: '巡检列表', icon: 'video-camera' }
      },
      {
        path: 'zkFcrModel',
        component: (resolve) => require(['@/views/fish/zkFcrModel/system'], resolve),
        name: '饵料系数',
        meta: { title: '饵料系数', icon: 'video-camera' }
      },
      {
        path: 'ezvizShow',
        component: (resolve) => require(['@/views/keyshow/ezvizShow'], resolve),
        name: '萤石监控',
        meta: { title: '实时监控', icon: 'video-camera' }
      },
      {
        path: 'zk-device-video',
        component: (resolve) => require(['@/views/fish/zkDevice/video'], resolve),
        name: 'ZkDeviceVideoPage',
        meta: { title: '视频监控', icon: 'video-camera' },
        hidden: true
      },
      {
        path: 'ezdetect',
        component: (resolve) => require(['@/components/EzvizPlayer/EzDetect'], resolve),
        name: '目标检测',
        meta: { title: '目标检测', icon: 'video-camera' },
        hidden: true
      },
      {
        path: 'workshopCNC',
        component: (resolve) => require(['@/views/keyshow/workshopCNC'], resolve),
        name: '车间数控',
        meta: { title: '车间数控', icon: 'video-camera', appMainOverflow: 'hidden', searchable: true },
        hidden: true
      },

    ]
  },
  {
    path: '/fish/zkPaperQuestion/exe-exam',
    component: (resolve) => require(['@/views/fish/zkPaperQuestion/exe-exam'], resolve),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'index',
    children: [{
      path: 'index',
      component: (resolve) => require(['@/views/index'], resolve),
      name: '首页',
      meta: { title: '首页', icon: 'dashboard', affix: true, appMainOverflow: 'hidden' }
    }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [{
      path: 'profile',
      component: (resolve) => require(['@/views/system/user/profile/index'], resolve),
      name: 'Profile',
      meta: { title: '个人中心', icon: 'user' }
    }, {
      path: 'notify-message',
      component: (resolve) => require(['@/views/system/notify/my/index'], resolve),
      name: 'MyNotifyMessage',
      meta: { title: '我的站内信', icon: 'message' },
    }]
  },
  {
    path: '/dict',
    component: Layout,
    hidden: true,
    children: [{
      path: 'type/data/:dictId(\\d+)',
      component: (resolve) => require(['@/views/system/dict/data'], resolve),
      name: 'SystemDictData',
      meta: { title: '字典数据', icon: '', activeMenu: '/system/dict' }
    }
    ]
  },
  {
    path: '/job',
    component: Layout,
    hidden: true,
    children: [{
      path: 'log',
      component: (resolve) => require(['@/views/infra/job/log'], resolve),
      name: 'InfraJobLog',
      meta: { title: '调度日志', activeMenu: '/infra/job' }
    }
    ]
  }, {
    path: '/codegen',
    component: Layout,
    hidden: true,
    children: [{
      path: 'edit/:tableId(\\d+)',
      component: (resolve) => require(['@/views/infra/codegen/editTable'], resolve),
      name: 'InfraCodegenEditTable',
      meta: { title: '修改生成配置', activeMenu: '/infra/codegen' }
    }
    ]
  },
  {
    path: '/bpm',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [{
      path: 'oa/leave/create',
      component: (resolve) => require(['@/views/bpm/oa/leave/create'], resolve),
      name: 'BpmOALeaveCreate',
      meta: { title: '发起 OA 请假', icon: 'form', activeMenu: '/bpm/oa/leave' }
    }, {
      path: 'oa/leave/detail',
      component: (resolve) => require(['@/views/bpm/oa/leave/detail'], resolve),
      name: 'BpmOALeaveDetail',
      meta: { title: '查看 OA 请假', icon: 'view', activeMenu: '/bpm/oa/leave' }
    }, {
      path: 'manager/form/edit',
      component: (resolve) => require(['@/views/bpm/form/formEditor'], resolve),
      name: 'BpmFormEditor',
      meta: { title: '流程表单-编辑', activeMenu: '/bpm/manager/form' }
    }, {
      path: 'manager/definition',
      component: (resolve) => require(['@/views/bpm/definition/index'], resolve),
      name: 'BpmProcessDefinition',
      meta: { title: '流程定义', activeMenu: '/bpm/manager/model' }
    }, {
      path: 'manager/model/design',
      component: (resolve) => require(['@/views/bpm/model/modelEditor'], resolve),
      name: 'BpmModelEditor',
      meta: { title: '设计流程', activeMenu: '/bpm/manager/model' }
    }, {
      path: 'process-instance/create',
      component: (resolve) => require(['@/views/bpm/processInstance/create/index'], resolve),
      name: 'BpmProcessInstanceCreate',
      meta: { title: '发起流程', activeMenu: '/bpm/task/my' }
    }, {
      path: 'process-instance/detail',
      component: (resolve) => require(['@/views/bpm/processInstance/detail'], resolve),
      name: 'BpmProcessInstanceDetail',
      meta: { title: '流程详情', activeMenu: '/bpm/task/my' }
    }
    ]
  },
  {
    path: '/property',
    component: Layout,
    hidden: true,
    children: [{
      path: 'value/:propertyId(\\d+)',
      component: (resolve) => require(['@/views/mall/product/property/value'], resolve),
      name: 'ProductPropertyValue',
      meta: { title: '商品属性值', icon: '', activeMenu: '/product/property' }
    }
    ]
  },
  {
    path: '/spu',
    component: Layout,
    hidden: true,
    children: [{
      path: 'edit/:spuId(\\d+)',
      component: (resolve) => require(['@/views/mall/product/spu/save'], resolve),
      name: 'ProductSpuUpdate',
      meta: { title: '修改商品', activeMenu: '/product/spu' }
    },
    {
      path: 'add',
      component: (resolve) => require(['@/views/mall/product/spu/save'], resolve),
      name: 'ProductSpuCreate',
      meta: { title: '添加商品', activeMenu: '/product/spu' }
    }
    ]
  },
  {
    path: '/trade/order',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'detail',
        name: 'TradeOrderDetail',
        hidden: true,
        meta: { title: '订单详情' },
        component: (resolve) => require(['@/views/mall/trade/order/detail'], resolve)
      }
    ]
  },
  {
    path: '/erp/purchase/order_detail',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'detail',
        name: 'PurchaseOrder',
        hidden: true,
        meta: { title: '订单详情' },
        component: (resolve) => require(['@/views/erp/purchase/order/detail'], resolve)
      }
    ]
  },
  {
    path: '/pay',
    component: Layout,
    hidden: true,
    children: [{
      path: 'cashier',
      name: 'PayCashier',
      hidden: true,
      meta: {
        title: '收银台',
        noCache: true
      },
      component: (resolve) => require(['@/views/pay/cashier'], resolve)
    }]
  },
  // IoT设备管理
  {
    path: '/iot',
    component: Layout,
    hidden: true,
    meta: {
      title: 'IoT设备管理',
      icon: 'iot'
    },
    children: [
      {
        path: 'fishDisease',
        component: (resolve) => require(['@/views/iot/fishDisease'], resolve),
        name: 'FishDiseasePage',
        hidden: true,
        meta: {
          title: '鱼病管理系统',
          icon: 'guide',
          appMainOverflow: 'hidden'
        }
      },
      {
        path: 'flavorOptimize',
        component: (resolve) => require(['@/views/iot/flavorOptimize'], resolve),
        name: 'FlavorOptimizePage',
        hidden: true,
        meta: {
          title: '风味优化管理',
          icon: 'guide',
          appMainOverflow: 'hidden'
        }
      },
      {
        path: 'thresholdSettings',
        component: (resolve) => require(['@/views/iot/thresholdSettings'], resolve),
        name: 'ThresholdSettingsPage',
        hidden: true,
        meta: {
          title: '阈值设置',
          icon: 'guide',
          appMainOverflow: 'hidden'
        }
      },
      {
        path: 'deviceCardShow',
        component: (resolve) => require(['@/views/iot/deviceCardShow'], resolve),
        name: 'IoT设备卡片展示',
        hidden: true,
        meta: {
          title: '设备卡片展示',
          icon: 'device'
        }
      },
      {
        path: 'systemShow',
        component: (resolve) => require(['@/views/iot/systemShow'], resolve),
        name: '系统展示',
        hidden: true,
        meta: {
          title: '系统展示',
          icon: 'system'
        }
      },
      {
        path: 'deviceDetail/:id',
        component: (resolve) => require(['@/views/iot/deviceDetail'], resolve),
        name: 'IoT设备详情',
        hidden: true,
        meta: {
          title: '设备详情',
          activeMenu: '/iot/deviceCardShow',
          hidden: true
        }
      },
      {
        path: 'rullSetting',
        component: (resolve) => require(['@/views/iot/rullSetting'], resolve),
        name: 'IoT设备阈值设置',
        hidden: true,
        meta: {
          title: '阈值规则设置',
          activeMenu: '/iot/deviceCardShow',
          hidden: true
        }
      }
    ]
  },
]

// 防止连续点击多次路由报错
let routerPush = Router.prototype.push;
Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(err => err)
}

export default new Router({
  base: process.env.VUE_APP_APP_NAME ? process.env.VUE_APP_APP_NAME : "/",
  // mode: 'history', // 去掉url中的#
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
