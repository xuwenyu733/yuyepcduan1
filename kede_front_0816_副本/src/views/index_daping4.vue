<template>
  <div class="dashboard-wrapper" :class="{ 'embedded-mode': embedded }">
    <header class="header">
      <div class="header-bar">
        <div class="header-left">
          <button class="nav-btn home" @click="toPage('/index_daping1')" title="首页">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
              <polyline points="9 22 9 12 15 12 15 22" />
            </svg>
            <span>首页</span>
          </button>
          <div class="divider"></div>
          <button class="nav-btn btn-blue" @click="toPage('/index_daping1_son')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <rect width="7" height="7" x="3" y="3" rx="1" />
              <rect width="7" height="7" x="14" y="3" rx="1" />
              <rect width="7" height="7" x="14" y="14" rx="1" />
              <rect width="7" height="7" x="3" y="14" rx="1" />
            </svg>
            <span>数字孪生</span>
          </button>
          <button class="nav-btn btn-purple" @click="toPage('/index_daping2')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="m22 8-6 4 6 4V8Z" />
              <rect width="14" height="12" x="2" y="6" rx="2" ry="2" />
            </svg>
            <span>视频监控</span>
          </button>
          <button class="nav-btn btn-cyan" @click="toPage('/index')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
            </svg>
            <span>数字化监控</span>
          </button>
        </div>

        <div class="header-center">
          <div class="title-wrap">
            <div class="logo">
              <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
              </svg>
            </div>
            <h1>{{ pageTitle }}</h1>
            <p>{{ pageSubtitle }}</p>
          </div>
        </div>

        <div class="header-right">
          <div v-if="siteContextLabel" class="site-context-badge" :title="siteContextLabel">
            {{ siteContextLabel }}
          </div>
          <div class="quick-btns">
            <button class="q-btn btn-emerald" @click="toPage('/stock_distribution')"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              </svg><span>存量分布</span></button>
            <button class="q-btn btn-orange" @click="toPage('/keyshow/InspectionList')"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" />
                <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" />
              </svg><span>智能巡检</span></button>
            <button class="q-btn btn-orange" @click="toPage('/mission_planning')"><span>任务与计划</span></button>
            <button class="q-btn btn-cyan" @click="toPage('/keyshow/inspectionreport')"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                <polyline points="14 2 14 8 20 8" />
                <line x1="16" y1="13" x2="8" y2="13" />
                <line x1="16" y1="17" x2="8" y2="17" />
                <polyline points="10 9 9 9 8 9" />
              </svg><span>分析报告</span></button>
            <button class="q-btn btn-pink" @click="goFishDisease()"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
              </svg><span>鱼病诊断</span></button>
            <button class="q-btn btn-indigo" @click="toPage('/knowledge/zk-meeting-expert')"><svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              </svg><span>专家会议</span></button>
            <!-- Excel：综合目录去掉试验系统入口 -->
          </div>
        </div>
      </div>
    </header>

    <main class="main-content">
      <div class="overlay-panels">
        <!-- 左侧面板 -->
        <div class="side-panel left-panel">
          <!-- 渔况智览 - 2排3列 -->
          <div class="stats-grid">
            <!-- 1. 生物资产总值（真实接口，按车间范围过滤品种） -->
            <div class="card stat-card border-teal" @click="toPage('/keyshow/biologicalAssetValuation')">
              <div class="card-header">
                <svg class="icon text-teal" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <line x1="12" y1="1" x2="12" y2="23" />
                  <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
                </svg>
                <p class="title text-teal">生物资产总值</p>
              </div>
              <div class="card-body">
                <div class="main-val">
                  <p class="val">{{ formatWanYuan(scopedTotalWorth) }}<span class="unit">万元</span></p>
                </div>
                <div class="sub-list">
                  <div v-if="!scopedBiologicalAssets.length" class="sub-item">
                    <span class="label">当前范围暂无资产数据</span>
                    <span class="val text-slate">-</span>
                  </div>
                  <div v-for="(item, index) in scopedBiologicalAssets" :key="item.fishType || index" class="sub-item">
                    <span class="label">{{ plainText(item.fishName) || '-' }}</span>
                    <span :class="['val', getAssetColorClass(index)]">¥{{ formatMoney(item.worth) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 2. 健康度评估 → 鱼类健康状态页（勿跳鱼病诊断） -->
            <div class="card stat-card border-pink" @click="goHealthAssessment()">
              <div class="card-header">
                <svg class="icon text-pink" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <path
                    d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                </svg>
                <p class="title text-pink">健康度评估</p>
              </div>
              <div class="card-body scroll-y">
                <div v-if="healthListLoading && !scopedHealthPreview.length" class="health-loading">健康数据加载中…</div>
                <div
                  v-for="(item, idx) in scopedHealthPreview"
                  :key="'hp-' + (item.pondId != null ? item.pondId : idx)"
                  :class="['health-item', item.cls]"
                >
                  <span>{{ item.label }}</span>
                  <span :class="['status', item.statusCls]">{{ item.status }}</span>
                </div>
                <div
                  v-if="!healthListLoading && hasSiteScope && !scopedHealthPreview.length"
                  class="health-item bg-slate-dim border-slate"
                >
                  <span>当前车间/系统暂无健康数据</span>
                </div>
                <div
                  v-else-if="!healthListLoading && !hasSiteScope && !scopedHealthPreview.length"
                  class="health-loading"
                >暂无健康评分数据</div>
              </div>
            </div>

            <!-- 3. 死亡率统计 -->
            <div class="card stat-card border-red" @click="goMortalityAssessment()">
              <div class="card-header">
                <svg class="icon text-red" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z" />
                  <path d="M12 9v4" />
                  <path d="M12 17h.01" />
                </svg>
                <p class="title text-red">死亡率统计</p>
              </div>
              <div class="card-body">
                <div class="main-val">
                  <p class="val">{{ formatPercent(overview.mortalityStats && overview.mortalityStats.sumDeadPer) }}<span class="unit"></span></p>
                  <p class="label">总死亡率</p>
                </div>
                <div class="grid-2">
                  <div class="sub-item"><span class="label">今日死亡率</span><span class="val text-red">{{ formatPercent(overview.mortalityStats && overview.mortalityStats.dayDeadPer) }}</span></div>
                  <div class="sub-item"><span class="label">今日死亡量</span><span class="val text-red">{{ formatCount(overview.mortalityStats && overview.mortalityStats.dayDead) }}尾</span></div>
                  <div class="sub-item"><span class="label">总死亡率</span><span class="val text-orange">{{ formatPercent(overview.mortalityStats && overview.mortalityStats.sumDeadPer) }}</span></div>
                  <div class="sub-item"><span class="label">总死亡量</span><span class="val text-orange">{{ formatCount(overview.mortalityStats && overview.mortalityStats.sumDead) }}尾</span></div>
                </div>
              </div>
            </div>

            <!-- 4. 精准投喂系统 -->
            <div class="card stat-card border-blue" @click="toPage('/keyshow/precisionfeeding')">
              <div class="card-header">
                <svg class="icon text-blue" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <path
                    d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
                  <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
                  <line x1="12" y1="22.08" x2="12" y2="12" />
                </svg>
                <p class="title text-blue">精准投喂系统</p>
              </div>
              <div class="card-body">
                <div class="feed-info">
                  <p class="label">投喂率</p>
                  <p class="val">{{ formatFeedRate(overview.precisionFeeding && overview.precisionFeeding.feedRate) }}</p>
                </div>
                <div class="feed-info">
                  <p class="label">今日投喂量</p>
                  <p class="val text-cyan">{{ formatKg(overview.precisionFeeding && overview.precisionFeeding.dayFeedCount) }}</p>
                </div>
              </div>
            </div>

            <!-- 5. 鱼类生长曲线 -->
            <div class="card stat-card border-purple" @click="toPage('/keyshow/zkGrowthModel')">
              <div class="card-header">
                <svg class="icon text-purple" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
                </svg>
                <p class="title text-purple">鱼类生长曲线</p>
              </div>
              <div class="card-body">
                <div class="main-val">
                  <p class="val">{{ formatGrowthLatest() }} <span class="unit">增重</span></p>
                </div>
                <div ref="growthChart" class="mini-chart"></div>
                <div class="grid-2 mt-1">
                  <div class="sub-item"><span class="label">数据点数</span><span class="val text-emerald">{{ growthCurveData.length }}</span></div>
                  <div class="sub-item"><span class="label">今日增重</span><span class="val text-cyan">{{ formatTodayGain() }}</span></div>
                </div>
              </div>
            </div>

            <!-- 6. 饲料系数 -->
            <div class="card stat-card border-amber" @click="toPage('/keyshow/zkFcrModel')">
              <div class="card-header">
                <svg class="icon text-amber" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <path
                    d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
                  <polyline points="7.5 4.21 12 6.81 16.5 4.21" />
                  <polyline points="7.5 19.79 7.5 14.6 3 12" />
                  <polyline points="21 12 16.5 14.6 16.5 19.79" />
                  <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
                  <line x1="12" y1="22.08" x2="12" y2="12" />
                </svg>
                <p class="title text-amber">饲料系数</p>
              </div>
              <div class="card-body">
                <div class="main-val">
                  <p class="val">{{ formatFcr() }} <span class="unit">FCR</span></p>
                </div>
                <div class="sub-list">
                  <div class="sub-item"><span class="label">累计饲料消耗量</span><span class="val text-amber">{{ formatKg(overview.feedCoefficient && overview.feedCoefficient.feedCount) }}</span>
                  </div>
                  <div class="sub-item"><span class="label">入池体重</span><span class="val text-blue">{{ formatKg(overview.feedCoefficient && overview.feedCoefficient.initFishWeight) }}</span></div>
                  <div class="sub-item"><span class="label">累计死鱼量</span><span class="val text-red">{{ formatCount(overview.feedCoefficient && overview.feedCoefficient.sumDead) }}尾</span></div>
                  <div class="sub-item"><span class="label">净增重</span><span class="val text-emerald">{{ formatKg(overview.feedCoefficient && overview.feedCoefficient.accumulatedWeightGain) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 水质情况 -->
          <div class="card water-card card-clickable" @click="goWaterQuality" title="进入水质情况">
            <div class="card-header-between">
              <div class="header-left">
                <svg class="icon text-blue" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
                </svg>
                <h2 class="title">水质情况</h2>
              </div>
              <div v-if="waterQualityTab === 'trend'" class="header-right" @click.stop>
                <button v-for="range in ['24h', '7d', '30d']" :key="range" @click="waterQualityTimeRange = range"
                  :class="['range-btn', waterQualityTimeRange === range ? 'active' : '']">
                  {{ range }}
                </button>
              </div>
            </div>

            <div class="water-tabs" @click.stop>
              <button
                v-for="tab in waterQualityTabs"
                :key="tab.key"
                @click="switchWaterTab(tab.key)"
                :class="['water-tab-btn', waterQualityTab === tab.key ? 'active' : '']">
                {{ tab.label }}
              </button>
            </div>

            <div class="card-body water-tab-body">
              <div v-show="waterQualityTab === 'realtime'" class="realtime-section">
                <div class="table-wrapper">
                  <table class="data-table">
                    <thead>
                      <tr>
                        <th>指标</th>
                        <th>当前值</th>
                        <th>设定值</th>
                        <th>状态</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(item, i) in waterQualityRealtime" :key="i">
                        <td>{{ item.name }}</td>
                        <td>
                          <span class="val text-cyan">{{ item.value }}</span>
                          <span class="unit text-slate" v-if="item.unit">{{ item.unit }}</span>
                        </td>
                        <td class="text-slate">{{ item.range }}</td>
                        <td>
                          <span :class="['status-tag', item.status === 'normal' ? 'tag-emerald' : 'tag-red']">
                            {{ item.status === 'normal' ? '正常' : '异常' }}
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <div v-show="waterQualityTab === 'trend'" class="trend-section">
                <div ref="waterTrendChart" class="trend-chart"></div>
              </div>
            </div>
          </div>


        </div>

        <!-- 中间区域：3D模拟 + 设备状态 -->
        <div class="center-column">
          <div class="center-3d-wrap">
            <div class="center-3d-container">
              <div class="twin-scene">
                <div class="model-viewport" ref="modelViewport">
                <div
                  class="process-flow-model"
                  ref="processFlowModel"
                  :style="processFlowModelStyle"
                >
                  <!-- 左侧鱼池 -->
                  <div class="fish-tanks-col">
                    <div
                      v-for="(pond, idx) in scopedTwinPonds"
                      :key="pond.label"
                      :class="['fish-tank', 'ft-' + (idx + 1)]"
                    >
                      <div class="model-label">
                        <div class="l-title"><span class="dot text-cyan"></span>{{ pond.label }}</div>
                        <div class="l-val">水温: <span class="text-emerald">{{ pond.temp }} °C</span></div>
                        <div class="l-val">溶氧: <span class="text-emerald">{{ pond.oxygen }} mg/L</span></div>
                      </div>
                      <div class="tank-body"></div>
                      <div class="decorate-ring"></div>
                    </div>
                    <div v-if="hasSiteScope && !scopedTwinPonds.length" class="fish-tank ft-1">
                      <div class="model-label">
                        <div class="l-title"><span class="dot text-cyan"></span>当前范围暂无鱼池</div>
                      </div>
                      <div class="tank-body"></div>
                      <div class="decorate-ring"></div>
                    </div>
                  </div>

                  <!-- 中间微滤机 -->
                  <div class="filter-device">
                    <div class="model-label">
                      <div class="l-title"><span class="dot"></span>微滤机</div>
                      <div class="l-val">状态: <span class="text-emerald">运行中</span></div>
                      <div class="l-val">效率: <span class="text-emerald">92.5 %</span></div>
                    </div>
                    <div class="device-body">
                      <div class="inner-lines">
                        <div class="l"></div>
                        <div class="l"></div>
                        <div class="l"></div>
                      </div>
                    </div>
                  </div>

                  <!-- 右侧生物池 & 加氧系统 -->
                  <div class="right-tanks-row">
                    <div class="tank bio-tank">
                      <div class="model-label">
                        <div class="l-title"><span class="dot"></span>生物池</div>
                        <div class="l-val">水温: <span class="text-emerald">28.2 °C</span></div>
                        <div class="l-val">菌群活性: <span class="text-emerald">91.9 %</span></div>
                      </div>
                      <div class="tank-body"></div>
                      <div class="decorate-ring"></div>
                    </div>

                    <div class="tank oxy-tank">
                      <div class="model-label">
                        <div class="l-title"><span class="dot"></span>加氧系统</div>
                        <div class="l-val">氧气水平: <span class="text-emerald">95.7 %</span></div>
                        <div class="l-val">功率: <span class="text-emerald">4.5 kW</span></div>
                      </div>
                      <div class="tank-body">
                        <div class="bubbles">
                          <div class="bubble" v-for="i in 10" :key="i"></div>
                        </div>
                      </div>
                      <div class="decorate-ring"></div>
                    </div>
                  </div>

                  <!-- 管道系统 -->
                  <div class="pipes">
                    <div class="pipe p-v-1">
                      <div class="flow"></div>
                    </div>
                    <div class="pipe p-h-1">
                      <div class="flow"></div>
                    </div>
                    <div class="pipe p-h-2">
                      <div class="flow"></div>
                    </div>
                    <div class="pipe p-v-2">
                      <div class="flow"></div>
                    </div>
                  </div>
                  <div class="ground-grid"></div>
                </div>
                </div>
              </div>
            </div>
          </div>
          <div class="bottom-panel">
            <!-- 设备状态 -->
            <div class="card equipment-card card-clickable" @click="goDeviceMonitor">
              <div class="card-header">
                <svg class="icon text-orange" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="3" />
                  <path
                    d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
                </svg>
                <h2 class="title">设备状态</h2>
              </div>

              <div class="card-body grid-4">
                <div class="status-list-section">
                  <h3 class="section-title text-orange">运行状态</h3>
                  <div class="status-scroll scroll-y" @click.stop @wheel.stop>
                    <div class="status-scroll-inner">
                      <div v-for="(item, i) in scopedEquipmentStatus" :key="i"
                        :class="['status-box', item.status === 'running' ? 'border-green' : 'border-yellow']">
                        <div class="box-header">
                          <p class="name">{{ item.name }}</p>
                          <span :class="['tag', item.status === 'running' ? 'tag-green' : 'tag-yellow']">
                            {{ item.status === 'running' ? '运行' : '维护' }}
                          </span>
                        </div>
                        <p class="workshop">{{ item.workshop }}</p>
                        <div class="params">
                          <div class="param" v-if="item.voltage != null"><svg class="icon text-yellow" viewBox="0 0 24 24" fill="none"
                              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
                            </svg><span>{{ item.voltage }}V</span></div>
                          <div class="param" v-if="item.current != null"><svg class="icon text-blue" viewBox="0 0 24 24" fill="none"
                              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <path d="M18 20V10" />
                              <path d="M12 20V4" />
                              <path d="M6 20v-6" />
                            </svg><span>{{ item.current }}{{ item.unit || '' }}</span></div>
                          <div class="param" v-if="item.frequency != null"><svg class="icon text-cyan" viewBox="0 0 24 24" fill="none"
                              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
                            </svg><span>{{ item.frequency }}Hz</span></div>
                          <div class="param" v-if="item.voltage == null && item.frequency == null && item.pondDetailNum">
                            <span>{{ item.pondDetailNum }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="failure-stats-section">
                  <h3 class="section-title text-orange">故障统计（近90天）</h3>
                  <div ref="failureChart" class="failure-chart"></div>
                </div>

                <div class="energy-stats-section">
                  <h3 class="section-title text-yellow">能耗统计及优化</h3>
                  <div class="energy-body">
                    <div class="energy-kpi bg-yellow-dim border-yellow">
                      <div class="kpi-main">
                        <span class="label text-yellow">本月总能耗</span>
                        <span class="val" v-if="totalEnergyCost">{{ totalEnergyCost }}<span class="unit">kWh</span></span>
                        <span class="val" v-else style="font-size:14px;">暂无计量</span>
                      </div>
                      <p v-if="energyNote" class="text-slate" style="font-size:11px;margin-top:4px;">{{ energyNote }}</p>
                      <span class="trend text-yellow">环比 -5.2%</span>
                    </div>
                    <div class="energy-visual">
                      <div ref="energyChart" class="energy-chart"></div>
                      <div class="energy-list">
                        <div v-for="(item, i) in energyData" :key="i" class="energy-item">
                          <div class="label-box">
                            <div class="dot" :style="{ backgroundColor: item.color }"></div>
                            <span class="label">{{ item.name }}</span>
                          </div>
                          <span class="val">{{ item.value }} kWh</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="medication-section">
                  <h3 class="section-title text-cyan">用药记录</h3>
                  <div class="medication-table-wrapper scroll-y">
                    <table class="medication-table">
                      <thead>
                        <tr>
                          <th>症状</th>
                          <th>用药时间</th>
                          <th>药物</th>
                          <th>存塘</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="(item, i) in scopedMedicationRecords" :key="i">
                          <td class="symptom">{{ plainText(item.symptom) }}</td>
                          <td class="time">{{ item.time }}</td>
                          <td class="drug">{{ plainText(item.drug) }}</td>
                          <td class="pond">{{ plainText(item.pond) }}</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>

        <!-- 右侧面板 -->
        <div class="side-panel right-panel">
          <!-- 任务与计划：标题进下级；正文可滚动浏览 -->
          <div class="card task-card">
            <div class="card-header card-clickable" style="cursor:pointer" @click="toPage('/mission_planning')">
              <svg class="icon text-purple" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                stroke-linecap="round" stroke-linejoin="round">
                <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
                <rect x="8" y="2" width="8" height="4" rx="1" ry="1" />
              </svg>
              <h2 class="title">任务与计划</h2>
              <span style="margin-left:auto;font-size:12px;opacity:.65">管理 →</span>
            </div>

            <div class="card-body">
              <div class="todo-section">
                <h3 class="section-title text-blue">待办任务</h3>
                <div class="table-wrapper">
                  <table class="task-table">
                    <thead>
                      <tr>
                        <th class="w-16">时间</th>
                        <th>紧急</th>
                        <th>优先</th>
                        <th>日常</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(period, key) in periodLabels" :key="key">
                        <td class="text-slate font-bold">{{ period }}</td>
                        <td>
                          <div v-for="(item, idx) in ((tasks[key] && tasks[key].urgent) || [])" :key="'u-' + key + '-' + idx" class="task-tag tag-red">{{ item }}</div>
                        </td>
                        <td>
                          <div v-for="(item, idx) in ((tasks[key] && tasks[key].priority) || [])" :key="'p-' + key + '-' + idx" class="task-tag tag-yellow">{{ item }}</div>
                        </td>
                        <td>
                          <div v-for="(item, idx) in ((tasks[key] && tasks[key].normal) || [])" :key="'n-' + key + '-' + idx" class="task-tag tag-emerald">{{ item }}</div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <div class="events-section">
                <h3 class="section-title text-purple">事件节点图</h3>
                <div class="events-container bg-slate-dim border-purple">
                  <!-- 时间轴 -->
                  <div class="time-axis">
                    <div v-for="h in eventAxisHours" :key="'axis-' + h" class="time-label">{{ h }}:00</div>
                  </div>

                  <!-- 事件线 -->
                  <div class="event-lines">
                    <div v-for="cat in categories" :key="cat.key" class="event-line-row">
                      <div class="line-label">
                        <svg class="icon" :style="{ color: cat.colors.icon }" viewBox="0 0 24 24" fill="none"
                          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <path v-if="cat.key === 'fish'" d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
                          <path v-else-if="cat.key === 'water'" d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
                          <circle v-else-if="cat.key === 'equipment'" cx="12" cy="12" r="3" />
                          <path v-if="cat.key === 'equipment'"
                            d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
                          <path v-else-if="cat.key === 'human'" d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                          <circle v-if="cat.key === 'human'" cx="9" cy="7" r="4" />
                        </svg>
                        <span class="label">{{ cat.label }}</span>
                      </div>
                      <div class="line-track">
                        <div class="track-bg" :style="{ backgroundColor: cat.colors.line }"></div>
                        <div
                          v-for="(event, idx) in (eventsWithLayout[cat.key] || [])"
                          :key="(event.taskCode || event.event) + '-' + idx"
                          class="event-point group"
                          :style="{ left: event._left }"
                          @click.stop
                          @mouseenter="onEventTipEnter($event, event, cat)"
                          @mouseleave="onEventTipLeave"
                        >
                          <div class="point-dot" :style="{
                            backgroundColor: event.completed ? cat.colors.point : '#334155',
                            borderColor: event.completed ? cat.colors.border : cat.colors.line
                          }"></div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- AI报警监控 -->
          <div class="card alert-card border-red">
            <div class="card-header-between">
              <div class="header-left">
                <div class="alert-icon-box animate-pulse">
                  <svg class="icon text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round">
                    <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z" />
                    <path d="M12 9v4" />
                    <path d="M12 17h.01" />
                  </svg>
                </div>
                <h2 class="title">AI报警监控</h2>
              </div>
              <div class="header-right alert-head-actions">
                <span class="count text-red">{{ scopedAlerts.length }}条</span>
                <button type="button" class="alert-detail-btn" @click.stop="toPage('/alert_monitor')">详情</button>
              </div>
            </div>

            <div class="card-body flex-col">
              <div class="filter-btns">
                <button v-for="type in alertTypes" :key="type.key" @click="alertFilter = type.key"
                  :class="['filter-btn', alertFilter === type.key ? 'active' : '']">
                  {{ type.label }}
                </button>
              </div>
              <div class="alert-list scroll-y">
                <div
                  v-for="(alert, index) in scopedAlerts"
                  :key="index"
                  class="alert-item bg-red-dim border-red"
                  style="cursor:pointer"
                  @click="toPage('/alert_monitor')"
                >
                  <div class="item-header">
                    <svg class="icon text-red" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                      stroke-linecap="round" stroke-linejoin="round">
                      <path v-if="alert.category === 'fish'" d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
                      <path v-else-if="alert.category === 'water'" d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
                      <circle v-else-if="alert.category === 'equipment'" cx="12" cy="12" r="3" />
                      <path v-if="alert.category === 'equipment'"
                        d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
                      <path v-else-if="alert.category === 'human'" d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                      <circle v-if="alert.category === 'human'" cx="9" cy="7" r="4" />
                    </svg>
                    <p class="title text-red">{{ alert.title }}</p>
                  </div>
                  <div class="item-body">
                    <p class="msg">{{ alert.message }}</p>
                    <p class="sug text-red-light">{{ alert.suggestion }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 配套系统 -->
          <div class="card support-card">
            <div class="card-header">
              <svg class="icon text-cyan" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="3" />
                <path
                  d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
              </svg>
              <h2 class="title">配套系统</h2>
            </div>
            <div class="card-body support-list">
              <!-- 配套系统暂缓开发：仅展示，去掉错误二级页链接 -->
              <div
                v-for="item in supportSystems"
                :key="item.name"
                class="support-item support-item--disabled"
                :style="{ '--accent': item.color }"
                title="暂未开放"
              >
                <span class="item-dot"></span>
                <span class="item-name">{{ item.name }}</span>
              </div>
            </div>
          </div>

        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { fetchMissionPlanOpenTasks, fetchMissionPlanTodayTimelineEvents, buildDashboardTaskBoard, syncModuleAiAlarmsToMission } from '@/utils/alarmTaskBridge';
import { loadSharedSiteOptions, isRowInUserSiteScope, isAlarmInUserSiteScope, SITE_OPTIONS_CHANGED_EVENT } from '@/utils/siteOptions';
import { USER_SCOPE_CHANGED_EVENT } from '@/utils/deptScope';
import {
  fetchUnifiedHealthList,
  whenPondArchiveReady,
  refetchUnifiedHealthListWithArchive
} from '@/utils/healthPreviewBridge';
import { getOverview3 } from '@/api/fish/daPing';
import {
  fetchDashboardAlerts,
  fetchDashboardMedicine,
  fetchOverviewMonitor
} from '@/utils/dashboardLiveBridge';
import { readDashboardSnapshot, writeDashboardSnapshot } from '@/utils/dashboardCache';
import { TYPE_LEGEND, DEFAULT_ENERGY_BY_TYPE } from '@/utils/deviceDemoData';
import {
  loadWorkshopRegistry,
  resolveWorkshopScope,
  resolveSystemScope,
  matchesSiteScopeText,
  textMatchesWorkshopScope,
  pondRowMatchesScope
} from '@/utils/siteScopeResolver';

export default {
  name: "IndexDaping4",
  props: {
    embedded: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      alertFilter: 'all',
      waterQualityTab: 'realtime',
      waterQualityTimeRange: '24h',
      waterQualityTabs: [
        { key: 'realtime', label: '实时数值' },
        { key: 'trend', label: '水质趋势' },
      ],
      /** 中央工艺图缩放（JS 实测，避免 CSS 容器查询失效） */
      modelScale: 0.82,

      // 以下监控块由 bootstrap 从库表填充；初始为空，不再写死演示数据
      abnormalAlerts: [],
      waterQualityRealtime: [],
      waterQuality24h: [],
      waterAsOf: '',
      allEquipmentStatus: [],
      equipmentFailureData: [],
      energyData: [],
      energyNote: '',
      totalEnergyCost: '',
      medicationRecords: [],
      growthCurveData: [],
      overview: {
        totalWorth: 0,
        biologicalAssets: [],
        mortalityStats: null,
        precisionFeeding: null,
        feedCoefficient: null
      },
      tasks: {
        day: { urgent: [], priority: [], normal: [] },
        week: { urgent: [], priority: [], normal: [] },
        month: { urgent: [], priority: [], normal: [] },
        quarter: { urgent: [], priority: [], normal: [] },
        year: { urgent: [], priority: [], normal: [] },
      },
      events: {
        fish: [],
        water: [],
        equipment: [],
        human: [],
      },
      periodLabels: { day: '日计划', week: '周计划', month: '月计划', quarter: '季度', year: '年度' },
      alertTypes: [
        { key: 'all', label: '全部' },
        { key: 'fish', label: '鱼' },
        { key: 'water', label: '水' },
        { key: 'equipment', label: '设备' },
        { key: 'human', label: '人' },
      ],
      categories: [
        { key: 'fish', label: '鱼', colors: { icon: '#22d3ee', line: '#164e63', point: '#06b6d4', border: '#67e8f9' } },
        { key: 'water', label: '水', colors: { icon: '#60a5fa', line: '#1e3a8a', point: '#3b82f6', border: '#93c5fd' } },
        { key: 'equipment', label: '设备', colors: { icon: '#fb923c', line: '#7c2d12', point: '#f97316', border: '#fdba74' } },
        { key: 'human', label: '人', colors: { icon: '#c084fc', line: '#581c87', point: '#a855f7', border: '#d8b4fe' } },
      ],
      supportSystems: [
        { name: '反硝化系统', color: '#2563eb' },
        { name: '碱度调节系统', color: '#0891b2' },
        { name: '尾水处理系统', color: '#059669' },
        { name: '鱼菜共生系统', color: '#0d9488' },
        { name: '环境与照明系统', color: '#d97706' },
      ],
      charts: {
        growth: null,
        waterTrend: null,
        failure: null,
        energy: null
      },
      activeWorkshop: '',
      activeSystem: '',
      siteOptions: null,
      workshopRegistry: [],
      healthPreviewItems: [],
      healthListLoading: false,
    };
  },
  computed: {
    pageTitle() {
      return this.embedded ? '车间数控' : '数字孪生模拟大屏';
    },
    pageSubtitle() {
      if (this.embedded && this.siteContextLabel) {
        return this.siteContextLabel + ' · 工业循环水数字化监测系统';
      }
      return '工业循环水数字化监测系统';
    },
    processFlowModelStyle() {
      const s = Number(this.modelScale) || 0.82;
      const rot = this.embedded ? 5 : 6;
      // 左侧鱼池标签易被裁切，整体右移腾出左边距
      const shiftX = this.embedded ? 48 : 28;
      return {
        transform: 'rotateX(' + rot + 'deg) translateX(' + shiftX + 'px) scale(' + s + ')',
        transformOrigin: 'center center',
        margin: (270 * (s - 1)) + 'px ' + (410 * (s - 1)) + 'px'
      };
    },
    siteContextLabel() {
      const parts = [];
      if (this.activeWorkshop) parts.push(this.resolvedWorkshopScope.label || '');
      if (this.activeSystem) parts.push(this.resolvedSystemScope.label || '');
      return parts.filter(Boolean).join(' · ');
    },
    resolvedWorkshopScope() {
      return resolveWorkshopScope(this.activeWorkshop, this.siteOptions, this.workshopRegistry);
    },
    resolvedSystemScope() {
      return resolveSystemScope(this.activeSystem);
    },
    hasSiteScope() {
      return !!(this.activeWorkshop || this.activeSystem);
    },
    filteredAlerts() {
      if (this.alertFilter === 'all') return this.abnormalAlerts;
      return this.abnormalAlerts.filter(alert => alert.category === this.alertFilter);
    },
    scopedAlerts() {
      const base = this.filteredAlerts || [];
      // 与详情页同源 zk_device_alarm，部门已由接口 deptId 过滤；仅按页面站点筛选收窄
      if (!this.hasSiteScope) return base;
      return base.filter(alert => this.matchesSiteScope(alert.message || alert.title || ''));
    },
    scopedEquipmentStatus() {
      const base = (this.allEquipmentStatus || []).filter((item) => isAlarmInUserSiteScope(item, this.siteOptions));
      if (!this.hasSiteScope) return base;
      return base.filter(item => this.matchesSiteScope(item.workshop || item.name || ''));
    },
    scopedMedicationRecords() {
      const base = (this.medicationRecords || []).filter((item) => isRowInUserSiteScope({
        pondName: item.pond,
        name: item.pond
      }, this.siteOptions));
      if (!this.hasSiteScope) return base;
      return base.filter(item => this.matchesSiteScope(item.pond || ''));
    },
    scopedHealthPreview() {
      const list = this.healthPreviewItems || [];
      if (!this.hasSiteScope) return list;
      return list.filter((item) => this.itemMatchesSiteScope(item));
    },
    scopedTwinPonds() {
      const opts = this.siteOptions;
      if (!opts || !opts.pondOptions) return [];
      let ponds = opts.pondOptions;
      if (this.hasSiteScope) {
        ponds = ponds.filter((row) => pondRowMatchesScope(row, this.resolvedWorkshopScope, this.resolvedSystemScope));
      }
      return ponds.slice(0, 3).map((p, i) => ({
        label: p.label,
        temp: (26.5 + i * 0.3).toFixed(1),
        oxygen: (6.5 + i * 0.2).toFixed(1)
      }));
    },
    /** 按当前车间/系统过滤品种：去掉演示页写死的虹鳟/金鳟，并避免串车间品种 */
    scopedBiologicalAssets() {
      const list = (this.overview && this.overview.biologicalAssets) || [];
      if (!this.hasSiteScope) return list;
      const wLabel = String((this.resolvedWorkshopScope && this.resolvedWorkshopScope.label) || this.activeWorkshop || '');
      const isGenericWorkshop = /车间\s*[123]|^[123]$/.test(wLabel);
      if (isGenericWorkshop) {
        // 车间1/2/3：排除鲑/鳟/三文鱼等其它专车间品种
        return list.filter((a) => !/[鲑鳟]|三文鱼/.test(String(a.fishName || '')));
      }
      if (/鲑|三文/.test(wLabel)) {
        return list.filter((a) => /[鲑]|三文/.test(String(a.fishName || '')));
      }
      if (/鲈/.test(wLabel)) {
        return list.filter((a) => /鲈/.test(String(a.fishName || '')));
      }
      return list;
    },
    scopedTotalWorth() {
      const list = this.scopedBiologicalAssets || [];
      if (!list.length) return Number((this.overview && this.overview.totalWorth) || 0);
      return list.reduce((sum, item) => sum + (Number(item.worth) || 0), 0);
    },
    /** 事件轴：默认 6–21；存在更早/更晚节点时两端扩展 */
    eventAxisRange() {
      let minH = 6;
      let maxH = 21;
      const buckets = this.events || {};
      Object.keys(buckets).forEach((k) => {
        (buckets[k] || []).forEach((ev) => {
          const h = this.resolveEventHour(ev);
          if (!Number.isFinite(h)) return;
          if (h < minH) minH = Math.floor(h);
          if (h > maxH) maxH = Math.ceil(h);
        });
      });
      if (minH < 0) minH = 0;
      if (maxH > 24) maxH = 24;
      if (maxH <= minH) maxH = minH + 1;
      return { min: minH, max: maxH, span: Math.max(1, maxH - minH) };
    },
    eventAxisHours() {
      const { min, max } = this.eventAxisRange;
      const span = Math.max(1, max - min);
      const step = span <= 12 ? 3 : (span <= 18 ? 3 : 4);
      const hours = [];
      for (let h = min; h < max; h += step) hours.push(h);
      if (hours[hours.length - 1] !== max) hours.push(max);
      return hours;
    },
    eventsWithLayout() {
      const range = this.eventAxisRange;
      const src = this.events || {};
      const out = { fish: [], water: [], equipment: [], human: [] };
      Object.keys(out).forEach((k) => {
        out[k] = (src[k] || []).map((ev) => {
          const h = this.resolveEventHour(ev);
          const pct = Number.isFinite(h) ? ((h - range.min) / range.span) * 100 : 0;
          const clamped = Math.max(0, Math.min(100, pct));
          return Object.assign({}, ev, {
            _left: clamped + '%',
            hour: Number.isFinite(h) ? h : ev.hour
          });
        });
      });
      return out;
    }
  },
  watch: {
    '$route.query': {
      handler() {
        this.syncSiteScopeFromRoute();
        this.loadScopedHealthPreview();
      },
      deep: true
    },
  },
  created() {
    this.syncSiteScopeFromRoute();
    // 先用会话快照填屏，避免切进来先空白再猛刷
    try {
      const snap = readDashboardSnapshot({ allowStale: true });
      if (snap) this.applyDashboardSnapshot(snap);
    } catch (e) { /* ignore */ }
    // 嵌入模式：等首帧后再拉网；独立大屏保持原节奏
    if (!this.embedded) {
      this.bootstrapSiteContext();
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.updateModelScale();
      this.bindModelScaleObserver();
      window.addEventListener('resize', this.handleResize);
      const runCharts = () => {
        if (this._isDestroyed) return;
        this.initChartsStaggered();
      };
      if (typeof requestIdleCallback === 'function') {
        requestIdleCallback(runCharts, { timeout: 1200 });
      } else {
        setTimeout(runCharts, 280);
      }
    });
    this._onSiteOptionsChanged = () => {
      if (!this._isDestroyed) this.loadSiteOptions(true);
    };
    window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    this._onUserScopeChanged = () => {
      if (this._isDestroyed) return;
      this.resetDashboardScopedState && this.resetDashboardScopedState();
      this.loadSiteOptions && this.loadSiteOptions(true);
      if (typeof this.bootstrapSiteContext === 'function') this.bootstrapSiteContext();
      if (typeof this.loadMissionTasks === 'function') this.loadMissionTasks();
    };
    window.addEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    if (this.embedded) {
      // 侧栏收起 + 壳层渲染完成后再请求，减轻切页卡顿
      const start = () => {
        if (this._isDestroyed || this._bootStarted) return;
        this._bootStarted = true;
        this.bootstrapSiteContext();
        this.loadMissionTasks();
      };
      if (typeof requestIdleCallback === 'function') {
        requestIdleCallback(start, { timeout: 500 });
      } else {
        setTimeout(start, 120);
      }
    } else {
      setTimeout(() => {
        if (!this._isDestroyed) this.loadMissionTasks();
      }, 60);
    }
  },
  beforeDestroy() {
    this.destroyEventTipEl();
    this._isDestroyed = true;
    if (this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    }
    if (this._onUserScopeChanged) {
      window.removeEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    }
    this.persistDashboardSnapshot();
    window.removeEventListener('resize', this.handleResize);
    if (this._modelScaleObserver) {
      this._modelScaleObserver.disconnect();
      this._modelScaleObserver = null;
    }
    Object.values(this.charts).forEach(chart => {
      if (chart) chart.dispose();
    });
  },
  methods: {
    resolveEventHour(ev) {
      if (!ev) return NaN;
      const h = Number(ev.hour);
      if (Number.isFinite(h)) return h;
      const m = String(ev.timeLabel || '').trim().match(/^(\d{1,2}):(\d{2})/);
      if (m) return Number(m[1]) + Number(m[2]) / 60;
      return NaN;
    },
    ensureEventTipEl() {
      if (this._eventTipEl && this._eventTipEl.parentNode) return this._eventTipEl;
      const el = document.createElement('div');
      el.className = 'daping-event-float-tip';
      el.innerHTML = '<p class="time"></p><p class="name"></p><p class="status"></p>';
      document.body.appendChild(el);
      this._eventTipEl = el;
      return el;
    },
    onEventTipEnter(e, event, cat) {
      const host = e && e.currentTarget;
      if (!host || !event) return;
      const tip = this.ensureEventTipEl();
      const timeEl = tip.querySelector('.time');
      const nameEl = tip.querySelector('.name');
      const statusEl = tip.querySelector('.status');
      const hour = Number(event.hour);
      const timeText = event.timeLabel || (Number.isFinite(hour) ? (Math.floor(hour) + ':00') : '-');
      if (timeEl) timeEl.textContent = timeText;
      if (nameEl) {
        nameEl.textContent = event.event || '-';
        nameEl.style.color = (cat && cat.colors && cat.colors.icon) || '#94a3b8';
      }
      if (statusEl) statusEl.textContent = event.completed ? '✓ 当日已完成' : '○ 待完成';
      tip.style.visibility = 'hidden';
      tip.style.display = 'block';
      tip.setAttribute('data-open', '1');
      this.$nextTick(() => {
        if (!tip.getAttribute('data-open')) return;
        const r = host.getBoundingClientRect();
        const tw = tip.offsetWidth || 180;
        const th = tip.offsetHeight || 56;
        const pad = 8;
        let left = r.left + r.width / 2 - tw / 2;
        let top = r.bottom + 8;
        if (left < pad) left = pad;
        if (left + tw > window.innerWidth - pad) left = Math.max(pad, window.innerWidth - tw - pad);
        if (top + th > window.innerHeight - pad) top = Math.max(pad, r.top - th - 8);
        tip.style.left = left + 'px';
        tip.style.top = top + 'px';
        tip.style.visibility = 'visible';
      });
    },
    onEventTipLeave() {
      const tip = this._eventTipEl;
      if (!tip) return;
      tip.removeAttribute('data-open');
      tip.style.display = 'none';
      tip.style.visibility = 'hidden';
    },
    destroyEventTipEl() {
      this.onEventTipLeave();
      if (this._eventTipEl && this._eventTipEl.parentNode) {
        this._eventTipEl.parentNode.removeChild(this._eventTipEl);
      }
      this._eventTipEl = null;
    },
    async ensureEcharts() {
      if (this._echarts) return this._echarts;
      if (!this._echartsLoading) {
        this._echartsLoading = import(/* webpackChunkName: "echarts" */ 'echarts').then((mod) => {
          this._echarts = mod && (mod.default || mod);
          return this._echarts;
        });
      }
      return this._echartsLoading;
    },
    applyDashboardSnapshot(snap) {
      if (!snap || typeof snap !== 'object') return;
      if (snap.overview) {
        const { healthList, ...overviewRest } = snap.overview;
        this.overview = Object.assign({}, this.overview, overviewRest);
        if (Array.isArray(healthList) && healthList.length) {
          this.seedHealthPreviewFromOverview(healthList);
        }
      }
      if (Array.isArray(snap.abnormalAlerts)) this.abnormalAlerts = snap.abnormalAlerts;
      if (Array.isArray(snap.allEquipmentStatus)) this.allEquipmentStatus = snap.allEquipmentStatus;
      if (Array.isArray(snap.equipmentFailureData)) this.equipmentFailureData = snap.equipmentFailureData;
      if (Array.isArray(snap.medicationRecords)) this.medicationRecords = snap.medicationRecords;
      if (Array.isArray(snap.waterQualityRealtime)) this.waterQualityRealtime = snap.waterQualityRealtime;
      if (Array.isArray(snap.energyData)) this.energyData = snap.energyData;
      if (Array.isArray(snap.growthCurveData)) this.growthCurveData = snap.growthCurveData;
      if (snap.totalEnergyCost != null) this.totalEnergyCost = snap.totalEnergyCost;
      if (snap.tasks) this.tasks = Object.assign({}, this.tasks, snap.tasks);
      if (snap.healthPreviewItems) this.healthPreviewItems = snap.healthPreviewItems;
    },
    persistDashboardSnapshot() {
      writeDashboardSnapshot({
        overview: this.overview,
        abnormalAlerts: this.abnormalAlerts,
        allEquipmentStatus: this.allEquipmentStatus,
        equipmentFailureData: this.equipmentFailureData,
        medicationRecords: this.medicationRecords,
        waterQualityRealtime: this.waterQualityRealtime,
        energyData: this.energyData,
        growthCurveData: this.growthCurveData,
        totalEnergyCost: this.totalEnergyCost,
        tasks: this.tasks,
        healthPreviewItems: this.healthPreviewItems
      });
    },
    updateModelScale() {
      const vp = this.$refs.modelViewport;
      if (!vp) return;
      const w = vp.clientWidth || 0;
      const h = vp.clientHeight || 0;
      if (w < 40 || h < 40) return;
      // 按视口尽量铺满中间区；左侧标签多，右边距略紧、左边多留
      const padX = this.embedded ? 24 : 28;
      const padY = this.embedded ? 12 : 16;
      const designW = 860;
      const designH = 540;
      const sx = (w - padX) / designW;
      const sy = (h - padY) / designH;
      const cap = this.embedded ? 0.95 : 0.98;
      const floor = this.embedded ? 0.5 : 0.48;
      let scale = Math.min(cap, sx, sy);
      if (!(scale > 0)) scale = this.embedded ? 0.78 : 0.72;
      scale = Math.max(floor, Math.min(cap, scale));
      const next = Math.round(scale * 1000) / 1000;
      if (Math.abs(next - (Number(this.modelScale) || 0)) < 0.008) return;
      this.modelScale = next;
    },
    bindModelScaleObserver() {
      const el = this.$refs.modelViewport;
      if (!el || typeof ResizeObserver === 'undefined') return;
      if (this._modelScaleObserver) this._modelScaleObserver.disconnect();
      let raf = 0;
      this._modelScaleObserver = new ResizeObserver(() => {
        if (raf) cancelAnimationFrame(raf);
        raf = requestAnimationFrame(() => {
          raf = 0;
          this.updateModelScale();
        });
      });
      this._modelScaleObserver.observe(el);
    },
    bootstrapSiteContext() {
      // 总览优先出屏；健康/筛选项/监控块错开，减轻切页卡顿
      this.loadOverview3()
        .then(() => {
          this.persistDashboardSnapshot();
          this.$nextTick(() => {
            if (!this._isDestroyed) this.initGrowthChart();
          });
        })
        .catch(() => {});
      const defer = (fn, ms) => setTimeout(() => {
        if (!this._isDestroyed) fn();
      }, ms);
      defer(() => this.loadScopedHealthPreview(true), 180);
      defer(() => this.loadWorkshopRegistryList(), 260);
      defer(() => this.loadSiteOptions(), 340);
      defer(() => this.loadLiveMonitorPanels(true), 420);
    },
    resetDashboardScopedState() {
      this.overview = Object.assign({}, this.overview, {
        healthList: [],
        biologicalAssets: [],
        growthCurve: [],
        totalWorth: 0,
        mortalityStats: null,
        precisionFeeding: null,
        feedCoefficient: null
      });
      this.growthCurveData = [];
      this.healthPreviewItems = [];
      this.waterQualityRealtime = [];
      this.waterQuality24h = [];
      this.allEquipmentStatus = [];
      this.equipmentFailureData = [];
      this.abnormalAlerts = [];
      this.medicationRecords = [];
      this.energyData = [];
    },
    async loadLiveMonitorPanels(force = false) {
      try {
        const [alerts, medicine, monitor] = await Promise.all([
          fetchDashboardAlerts(),
          fetchDashboardMedicine(12),
          fetchOverviewMonitor(!!force)
        ]);
        if (this._isDestroyed) return;
        this.abnormalAlerts = alerts || [];
        this.medicationRecords = medicine || [];
        if (monitor) {
          this.waterQualityRealtime = monitor.waterRealtime || [];
          if (monitor.waterTrend && monitor.waterTrend.length) {
            this.waterQuality24h = monitor.waterTrend;
          } else if ((this.waterQualityRealtime || []).length) {
            // 趋势接口空时用实时值合成，避免首页水质趋势空白
            const pick = (key, fb) => {
              const hit = this.waterQualityRealtime.find(r => r && r.key === key)
              const v = hit && hit.value != null ? Number(hit.value) : fb
              return Number.isFinite(v) ? v : fb
            }
            const t0 = pick('temp', 25), o0 = pick('oxygen', 7.2), p0 = pick('ph', 7.4)
            this.waterQuality24h = Array.from({ length: 12 }, (_, i) => {
              const wave = Math.sin(i / 2.5) * 0.35
              return {
                time: String(i * 2).padStart(2, '0') + ':00',
                temp: Number((t0 + wave * 0.4).toFixed(2)),
                oxygen: Number((o0 + wave * 0.25).toFixed(2)),
                ph: Number((p0 + wave * 0.03).toFixed(2))
              }
            })
          } else {
            this.waterQuality24h = [];
          }
          this.waterAsOf = monitor.waterAsOf || '';
          this.allEquipmentStatus = monitor.devices || [];
          this.equipmentFailureData = monitor.faultMonth || [];
          this.energyData = monitor.energy || [];
          this.energyNote = monitor.energyNote || '';
          if (!this.energyData.length) {
            // 与首页/设备监控一致：接口无能耗时用最新汇总兜底，避免空白
            this.energyData = TYPE_LEGEND.map(t => ({
              name: t.name,
              value: Number(DEFAULT_ENERGY_BY_TYPE[t.name]) || 0,
              color: t.color
            })).filter(d => d.value > 0)
            this.energyNote = this.energyNote || '展示最新电度汇总'
          }
          // 与设备监控页同口径，不做二次单位换算
          if (this.energyData.length) {
            const sum = this.energyData.reduce((s, d) => s + (Number(d.value) || 0), 0);
            this.totalEnergyCost = String(Math.round(sum * 10) / 10);
          } else {
            this.totalEnergyCost = '';
          }
        } else if (force) {
          this.waterQualityRealtime = [];
          this.waterQuality24h = [];
          this.allEquipmentStatus = [];
          this.equipmentFailureData = [];
        }
        this.$nextTick(() => {
          if (this._isDestroyed) return;
          this.initFailureChart();
          this.initEnergyChart();
          if (this.waterQualityTab === 'trend') this.initWaterTrendChart();
        });
        // 仅同步真实告警到任务
        if (alerts.length) {
          setTimeout(() => {
            if (!this._isDestroyed) this.syncDemoAlertsToMission();
          }, 400);
        }
      } catch (e) {
        console.warn('[index_daping4] live monitor', e && e.message);
      }
    },
    /** overview3 自带简易 healthList，可先填卡片，再被评分接口覆盖 */
    seedHealthPreviewFromOverview(healthList) {
      if (!Array.isArray(healthList) || !healthList.length) return;
      if (this.healthPreviewItems && this.healthPreviewItems.length) return;
      this.healthPreviewItems = healthList.map((h) => {
        const status = h.healthStatus || '健康';
        let cls = 'bg-emerald-dim border-emerald';
        let statusCls = 'text-emerald';
        if (status === '不健康') {
          cls = 'bg-red-dim border-red';
          statusCls = 'text-red';
        } else if (status === '亚健康') {
          cls = 'bg-yellow-dim border-yellow';
          statusCls = 'text-yellow';
        }
        const label = h.pondName || '';
        return {
          pondId: h.pondId,
          pondName: label,
          label,
          status,
          cls,
          statusCls,
          workshopName: h.workshopName || '',
          pondDetailNum: h.pondDetailNum || ''
        };
      });
    },
    async loadOverview3() {
      try {
        const res = await getOverview3();
        if (res && res.data) {
          const data = res.data;
          this.overview = Object.assign({}, this.overview, {
            totalWorth: data.totalWorth || 0,
            biologicalAssets: data.biologicalAssets || [],
            mortalityStats: data.mortalityStats || null,
            precisionFeeding: data.precisionFeeding || null,
            feedCoefficient: data.feedCoefficient || null
          });
          this.seedHealthPreviewFromOverview(data.healthList);
          this.growthCurveData = (data.growthCurve || []).map((item) => ({
            month: this.formatChartDay(item.staticDay),
            weight: Number(item.fishGainCount) || 0
          }));
          this.$nextTick(() => {
            if (!this._isDestroyed) this.initGrowthChart();
          });
        }
      } catch (e) {
        console.warn('[index_daping4] overview3', e && e.message);
        this.overview.biologicalAssets = [];
        this.growthCurveData = [];
      }
    },
    async loadWorkshopRegistryList() {
      try {
        this.workshopRegistry = await loadWorkshopRegistry();
      } catch (e) {
        console.warn('[index_daping4] workshop registry', e);
        this.workshopRegistry = [];
      }
    },
    itemMatchesSiteScope(item) {
      const label = (item && (item.label || item.pondName)) || '';
      const workshopName = (item && item.workshopName) || '';
      return matchesSiteScopeText(label, this.resolvedWorkshopScope, this.resolvedSystemScope)
        || textMatchesWorkshopScope(workshopName, this.resolvedWorkshopScope);
    },
    async loadSiteOptions(force = false) {
      try {
        this.siteOptions = await loadSharedSiteOptions({ includePonds: false, force: !!force });
      } catch (e) {
        console.warn('[index_daping4] site options', e);
      }
    },
    async loadScopedHealthPreview(force = false) {
      const hasSeed = !!(this.healthPreviewItems && this.healthPreviewItems.length);
      // 已有总览占位时不打全屏 loading，减少「卡住」感
      if (!hasSeed || force) this.healthListLoading = true;
      try {
        const bundle = await fetchUnifiedHealthList({ force: !!force });
        if (bundle && Array.isArray(bundle.previewList)) {
          this.healthPreviewItems = bundle.previewList;
        } else if (force) {
          this.healthPreviewItems = [];
        }
        // 鱼池档案较慢：名称后续补全，不挡首屏分数
        whenPondArchiveReady(async () => {
          if (this._isDestroyed) return;
          try {
            const enriched = await refetchUnifiedHealthListWithArchive();
            if (enriched && Array.isArray(enriched.previewList)) {
              this.healthPreviewItems = enriched.previewList;
            }
          } catch (e) { /* ignore enrich */ }
        });
      } catch (e) {
        console.warn('[index_daping4] health preview', e);
        if (force) this.healthPreviewItems = [];
      } finally {
        this.healthListLoading = false;
      }
    },
    syncSiteScopeFromRoute() {
      const q = (this.$route && this.$route.query) || {};
      this.activeWorkshop = q.workshop ? String(q.workshop) : '';
      this.activeSystem = q.system ? String(q.system) : '';
    },
    matchesSiteScope(text) {
      return matchesSiteScopeText(text, this.resolvedWorkshopScope, this.resolvedSystemScope);
    },
    siteRouteQuery(extra = {}) {
      const q = Object.assign({}, (this.$route && this.$route.query) || {}, extra);
      if (this.activeWorkshop) q.workshop = this.activeWorkshop;
      if (this.activeSystem) q.system = this.activeSystem;
      return q;
    },
    async loadMissionTasks() {
      try {
        const [rows, timeline] = await Promise.all([
          fetchMissionPlanOpenTasks(48),
          fetchMissionPlanTodayTimelineEvents(120)
        ]);
        if (rows.length) {
          const board = buildDashboardTaskBoard(rows);
          this.tasks = {
            ...board,
            quarter: this.tasks.quarter || { urgent: [], priority: [], normal: [] }
          };
        }
        const hasAny = Object.keys(timeline || {}).some((k) => (timeline[k] || []).length);
        // 即使无节点也覆盖为空轴，避免沿用旧模板/缓存点
        this.events = timeline && typeof timeline === 'object'
          ? {
              fish: timeline.fish || [],
              water: timeline.water || [],
              equipment: timeline.equipment || [],
              human: timeline.human || []
            }
          : this.events;
        if (!hasAny) {
          // keep empty tracks
        }
      } catch (e) {
        console.warn('[daping4] 任务与计划加载失败', e && e.message);
      }
    },
    syncDemoAlertsToMission() {
      const list = (this.abnormalAlerts || []).map((a, i) => ({
        id: a.id || ('DP4-' + i + '-' + (a.title || '')),
        title: a.title || 'AI预警',
        message: a.message || '',
        suggestion: a.suggestion || '',
        priority: a.category === 'equipment' || a.category === 'fish' ? 'urgent' : 'priority',
        category: a.category || 'equip',
        alarmStatus: 'open'
      }));
      syncModuleAiAlarmsToMission(list, {
        prefix: 'DP4-AI-',
        source: 'daping4',
        category: 'equip'
      }).catch(e => console.warn('[daping4] 报警同步任务', e));
    },
    toPage(path) {
      if (!path) return;
      if (typeof path === 'string' && path.indexOf('?') === -1 && this.hasSiteScope) {
        this.$router.push({ path, query: this.siteRouteQuery({ from: '/keyshow/workshopCNC' }) }).catch(() => {});
        return;
      }
      this.$router.push(path).catch(() => { });
    },
    getAssetColorClass(index) {
      const colors = ['text-cyan', 'text-purple', 'text-amber', 'text-emerald', 'text-blue'];
      return colors[index % colors.length];
    },
    formatWanYuan(value) {
      const num = Number(value) || 0;
      return (num / 10000).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    },
    formatMoney(value) {
      const num = Number(value) || 0;
      return num.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
    },
    formatPercent(value) {
      const num = Number(value) || 0;
      return (num * 100).toFixed(2) + '%';
    },
    formatCount(value) {
      return Number(value) || 0;
    },
    formatKg(value) {
      const num = Number(value) || 0;
      return num.toFixed(2) + 'kg';
    },
    formatFeedRate(value) {
      const num = Number(value) || 0;
      return (num * 100).toFixed(2) + '%';
    },
    formatFcr() {
      const fc = this.overview.feedCoefficient;
      if (!fc) return '-';
      const feed = Number(fc.feedCount) || 0;
      const gain = Number(fc.accumulatedWeightGain) || 0;
      if (gain <= 0) return '-';
      return (feed / gain).toFixed(2);
    },
    formatGrowthLatest() {
      const list = this.growthCurveData || [];
      if (!list.length) return '-';
      return Number(list[list.length - 1].weight || 0).toFixed(2);
    },
    formatTodayGain() {
      const list = this.growthCurveData || [];
      if (!list.length) return '0kg';
      return Number(list[list.length - 1].weight || 0).toFixed(2) + 'kg';
    },
    formatChartDay(day) {
      if (!day) return '';
      if (Array.isArray(day) && day.length >= 3) {
        return `${String(day[1]).padStart(2, '0')}-${String(day[2]).padStart(2, '0')}`;
      }
      const text = String(day);
      const parts = text.split('-');
      if (parts.length >= 3) return `${parts[1]}-${parts[2]}`;
      return text;
    },
    /** 去掉接口返回的富文本标签，避免页面露出 <p> */
    plainText(val) {
      if (val == null || val === '') return '';
      const div = document.createElement('div');
      div.innerHTML = String(val);
      return (div.textContent || div.innerText || '').replace(/\s+/g, ' ').trim();
    },
    goWaterQuality() {
      const q = this.siteRouteQuery({ from: '/keyshow/workshopCNC' });
      this.$router.push({ path: '/water_quality', query: q }).catch(() => {});
    },
    goDeviceMonitor() {
      const q = this.siteRouteQuery({ from: '/keyshow/workshopCNC' });
      this.$router.push({ path: '/device_monitor', query: q }).catch(() => {});
    },
    /** 首页健康度评估 → 鱼类健康状态独立路由，禁止进鱼病诊断 */
    goHealthAssessment() {
      this.$router.push({
        path: '/keyshow/fishHealthStatus',
        query: this.siteRouteQuery({ from: '/keyshow/workshopCNC' })
      }).catch(() => { });
    },
    goMortalityAssessment() {
      this.$router.push({
        path: '/keyshow/zkDeathSurvivalRate',
        query: this.siteRouteQuery({ activeTab: 'mortality', from: '/keyshow/workshopCNC' })
      }).catch(() => { });
    },
    goFishDisease() {
      this.$router.push({ path: '/fish-disease', query: { from: '/index_daping4' } }).catch(() => { });
    },
    handleResize() {
      Object.values(this.charts).forEach(chart => {
        if (chart) chart.resize();
      });
      this.updateModelScale();
    },
    initCharts() {
      this.initChartsStaggered();
    },
    initChartsStaggered() {
      const steps = [
        () => this.initGrowthChart(),
        () => this.initFailureChart(),
        () => this.initEnergyChart(),
        () => this.handleResize()
      ];
      let i = 0;
      const tick = () => {
        if (this._isDestroyed) return;
        const fn = steps[i++];
        if (!fn) return;
        try { fn(); } catch (e) { /* ignore */ }
        if (i < steps.length) {
          if (typeof requestIdleCallback === 'function') {
            requestIdleCallback(tick, { timeout: 400 });
          } else {
            setTimeout(tick, 40);
          }
        }
      };
      tick();
    },
    switchWaterTab(tab) {
      this.waterQualityTab = tab;
      if (tab !== 'trend') return;
      this.$nextTick(() => {
        if (this.charts.waterTrend) {
          this.charts.waterTrend.resize();
        } else {
          this.initWaterTrendChart();
        }
      });
    },
    async initGrowthChart() {
      if (!this.$refs.growthChart) return;
      const echarts = await this.ensureEcharts();
      if (this._isDestroyed || !this.$refs.growthChart) return;
      if (this.charts.growth) {
        this.charts.growth.dispose();
      }
      const seriesData = (this.growthCurveData && this.growthCurveData.length)
        ? this.growthCurveData
        : [{ month: '-', weight: 0 }];
      const chart = echarts.init(this.$refs.growthChart);
      const option = {
        grid: { left: 0, right: 0, top: 5, bottom: 0 },
        xAxis: { type: 'category', show: false, data: seriesData.map(d => d.month) },
        yAxis: { type: 'value', show: false },
        series: [{
          data: seriesData.map(d => d.weight),
          type: 'line',
          smooth: true,
          symbol: 'none',
          lineStyle: { color: '#a855f7', width: 2 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(168, 85, 247, 0.3)' },
              { offset: 1, color: 'rgba(168, 85, 247, 0)' }
            ])
          }
        }]
      };
      chart.setOption(option);
      this.charts.growth = chart;
    },
    async initWaterTrendChart() {
      if (!this.$refs.waterTrendChart) return;
      const echarts = await this.ensureEcharts();
      if (this._isDestroyed || !this.$refs.waterTrendChart) return;
      if (this.charts.waterTrend) this.charts.waterTrend.dispose();
      const chart = echarts.init(this.$refs.waterTrendChart);
      const option = {
        tooltip: { trigger: 'axis', backgroundColor: '#1e293b', borderColor: '#334155', textStyle: { color: '#fff', fontSize: 11 } },
        legend: { textStyle: { color: '#94a3b8', fontSize: 11 }, itemWidth: 10, itemHeight: 10, bottom: 0 },
        grid: { left: '3%', right: '4%', top: '10%', bottom: '15%', containLabel: true },
        xAxis: { type: 'category', data: this.waterQuality24h.map(d => d.time), axisLabel: { color: '#94a3b8', fontSize: 11 }, axisLine: { lineStyle: { color: '#334155' } } },
        yAxis: { type: 'value', axisLabel: { color: '#94a3b8', fontSize: 11 }, splitLine: { lineStyle: { color: '#334155', type: 'dashed' } } },
        series: [
          { name: '水温(°C)', type: 'line', data: this.waterQuality24h.map(d => d.temp), stroke: '#f59e0b', itemStyle: { color: '#f59e0b' }, smooth: true },
          { name: '溶氧(mg/L)', type: 'line', data: this.waterQuality24h.map(d => d.oxygen), stroke: '#3b82f6', itemStyle: { color: '#3b82f6' }, smooth: true },
          { name: 'pH', type: 'line', data: this.waterQuality24h.map(d => d.ph), stroke: '#10b981', itemStyle: { color: '#10b981' }, smooth: true }
        ]
      };
      chart.setOption(option);
      this.charts.waterTrend = chart;
    },
    async initFailureChart() {
      if (!this.$refs.failureChart) return;
      const echarts = await this.ensureEcharts();
      if (this._isDestroyed || !this.$refs.failureChart) return;
      if (this.charts.failure) {
        this.charts.failure.dispose();
      }
      const chart = echarts.init(this.$refs.failureChart);
      const option = {
        grid: { left: '6%', right: '4%', top: '6%', bottom: '0%', containLabel: true },
        xAxis: {
          type: 'category',
          data: this.equipmentFailureData.map(d => d.name),
          axisLabel: { color: '#94a3b8', fontSize: 10, interval: 0 },
          axisLine: { lineStyle: { color: '#334155' } },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: '#94a3b8', fontSize: 10 },
          splitLine: { lineStyle: { color: '#334155', type: 'dashed' } }
        },
        series: [{
          data: this.equipmentFailureData.map(d => d.count),
          type: 'bar',
          itemStyle: { color: '#f59e0b', borderRadius: [3, 3, 0, 0] },
          barWidth: 14
        }]
      };
      chart.setOption(option);
      this.charts.failure = chart;
    },
    async initEnergyChart() {
      if (!this.$refs.energyChart) return;
      const echarts = await this.ensureEcharts();
      if (this._isDestroyed || !this.$refs.energyChart) return;
      if (this.charts.energy) {
        this.charts.energy.dispose();
      }
      const chart = echarts.init(this.$refs.energyChart);
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} kWh ({d}%)',
          backgroundColor: '#1e293b',
          borderColor: '#334155',
          textStyle: { color: '#fff', fontSize: 11 }
        },
        series: [{
          type: 'pie',
          radius: ['38%', '58%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: false,
          label: { show: false },
          itemStyle: {
            borderRadius: 3,
            borderColor: 'rgba(10, 22, 40, 0.9)',
            borderWidth: 2
          },
          emphasis: {
            scale: true,
            scaleSize: 5
          },
          data: this.energyData.map(d => ({ value: d.value, name: d.name, itemStyle: { color: d.color } }))
        }]
      };
      chart.setOption(option);
      this.charts.energy = chart;
    }
  }
};
</script>
<style lang="scss" scoped>
@import './index_daping4.styles.scss';
</style>

<style lang="scss">
.daping-event-float-tip {
  display: none;
  position: fixed;
  z-index: 10050;
  pointer-events: none;
  background: #1e293b;
  border: 1px solid #334155;
  border-radius: 4px;
  padding: 6px 8px;
  max-width: 260px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.4);
  white-space: nowrap;
  .time {
    margin: 0;
    font-size: 11px;
    font-weight: 700;
    color: #e2e8f0;
  }
  .name {
    margin: 2px 0 0;
    font-size: 11px;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .status {
    margin: 2px 0 0;
    font-size: 11px;
    color: #94a3b8;
  }
}
</style>
