<template>
  <div class="dashboard-wrapper" :class="{ 'embedded-mode': embedded }">
    <!-- AI助手占位 (React版本中有AIAssistant，这里如果Vue版本没有则忽略或留坑) -->

    <!-- 顶部导航栏 -->
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
          <!-- 与原有数字孪生页 /index_daping1_son 连接（演示入口） -->
          <button
            class="nav-btn btn-blue"
            @mouseenter="prefetchDigitalTwin"
            @focus="prefetchDigitalTwin"
            @click="toPage('/index_daping1_son')"
          >
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
              <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
              <line x1="12" y1="22.08" x2="12" y2="12" />
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
        </div>

        <div class="header-center">
          <div class="title-wrap">
            <div class="logo">
              <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                   stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
              </svg>
            </div>
            <h1>系统总览</h1>
            <p>智慧渔业数字化监控平台</p>
          </div>
        </div>

        <div class="header-right">
          <div class="header-filter" :class="{ 'is-loading': siteOptionsLoading }">
            <div class="filter-item">
              <span class="label">车间：</span>
              <select
                v-model="searchWorkshop"
                class="select-input"
                :disabled="siteOptionsLoading && !workshopOptions.length"
                @focus="ensureSiteOptionsLoaded"
              >
                <option value="">{{ siteOptionsLoading && !workshopOptions.length ? '加载中…' : '请选择' }}</option>
                <option v-for="item in workshopOptions" :key="'ws-' + item.value" :value="item.value">{{ item.label }}</option>
              </select>
            </div>
            <div class="filter-item">
              <span class="label">系统：</span>
              <select
                v-model="searchSystem"
                class="select-input"
                :disabled="siteOptionsLoading && !systemOptions.length"
                @focus="ensureSiteOptionsLoaded"
              >
                <option value="">{{ siteOptionsLoading && !systemOptions.length ? '加载中…' : '请选择' }}</option>
                <option
                  v-for="item in filteredSystemOptions"
                  :key="'sys-' + (item.workshopValue || '') + '::' + item.value"
                  :value="item.value"
                >{{ item.label }}</option>
              </select>
            </div>
            <button class="confirm-btn" @click="goWorkshopCNC">确定</button>
          </div>

          <div class="quick-btns">
            <button class="q-btn btn-emerald" @click="toPage('/stock_distribution')"><svg class="icon" viewBox="0 0 24 24"
                                                                                          fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            </svg><span>存量分布</span></button>
            <button class="q-btn btn-orange" @click="toPage('/keyshow/InspectionList')"><svg class="icon"
                                                                                             viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                                             stroke-linejoin="round">
              <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" />
              <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" />
            </svg><span>智能巡检</span></button>
            <button class="q-btn btn-orange" @click="toPage('/mission_planning')"><span>任务与计划</span></button>
            <button class="q-btn btn-cyan" @click="toPage('/keyshow/inspectionreport')"><svg class="icon"
                                                                                             viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                                             stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
              <polyline points="10 9 9 9 8 9" />
            </svg><span>分析报告</span></button>
            <button class="q-btn btn-pink" @click="goFishDisease()"><svg class="icon"
                                                                         viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                         stroke-linejoin="round">
              <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
            </svg><span>鱼病诊断</span></button>
            <button class="q-btn btn-indigo" @click="toPage('/knowledge/zk-meeting-expert')"><svg class="icon"
                                                                                                  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                                                  stroke-linejoin="round">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
              <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
              <path d="M16 3.13a4 4 0 0 1 0 7.75" />
            </svg><span>专家会议</span></button>
            <!-- Excel：综合模块目录删除试验系统入口 -->
          </div>
        </div>
      </div>
    </header>

    <div
      v-if="degradedSources.length"
      class="data-degrade-banner"
      :title="'失败模块：' + degradedSources.join('、')"
    >
      部分数据源未刷新，页面含默认/缓存展示（{{ degradedSources.join('、') }}）
    </div>

    <main class="main-content">
      <div class="content-grid">
        <!-- 左侧列 -->
        <div class="left-column">
          <!-- 渔况智览 - 2排3列 -->
          <div class="stats-grid">
            <!-- 1. 生物资产总值 -->
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
                  <p class="val">{{ formatWanYuan(overview.totalWorth) }}<span class="unit">万元</span></p>
                </div>
                <div class="sub-list">
                  <div v-for="(item, index) in overview.biologicalAssets" :key="item.fishType || index" class="sub-item">
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
                <div v-if="healthListLoading && !overview.healthList.length" class="health-loading">健康数据加载中…</div>
                <div v-else-if="!overview.healthList.length" class="health-loading">暂无健康评分数据</div>
                <div v-for="(item, index) in overview.healthList" :key="item.pondId || index"
                  :class="['health-item', getHealthItemClass(item.healthStatus)]"
                  style="cursor:pointer"
                  @click.stop="goFishHealthForPond(item)">
                  <span>{{ plainText(item.pondName) || siteDisplayName(item.pondName) }}</span>
                  <span :class="['status', getHealthStatusClass(item.healthStatus)]">{{ plainText(item.healthStatus) }}</span>
                </div>
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
                <div class="mortality-metrics">
                  <div class="sub-item"><span class="label">今日死亡率</span><span class="val text-red">{{ formatPercent(overview.mortalityStats && overview.mortalityStats.dayDeadPer) }}</span></div>
                  <div class="sub-item"><span class="label">今日死亡量</span><span class="val text-red">{{ formatCount(overview.mortalityStats && overview.mortalityStats.dayDead) }}尾</span></div>
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
                  <p class="val">{{ formatGrowthLatest() }} <span class="unit">kg</span></p>
                </div>
                <div ref="growthChart" class="mini-chart"></div>
                <div class="grid-2 mt-1">
                  <div class="sub-item"><span class="label">生物总量</span><span class="val text-emerald">{{ formatKg(overview.precisionFeeding && overview.precisionFeeding.totalWeight) }}</span></div>
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

          <!-- 水质情况：整卡可进下级页；时段/阈值按钮 stop 避免误跳 -->
          <div class="card water-card card-clickable" @click="goWaterQuality">
            <div class="card-header-between">
              <div class="header-left">
                <svg class="icon text-blue" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
                </svg>
                <h2 class="title">水质情况</h2>
                <span class="card-enter-hint">详情 →</span>
              </div>
              <div class="header-right" @click.stop>
                <button v-for="range in ['24h', '7d', '30d']" :key="range" @click="waterQualityTimeRange = range"
                  :class="['range-btn', waterQualityTimeRange === range ? 'active' : '']">
                  {{ range }}
                </button>
                <button class="range-btn threshold-btn" @click="goThresholdSettings" title="水质阈值配置">
                  <svg class="icon-xs" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
                  </svg>
                  <span>阈值</span>
                </button>
              </div>
            </div>

            <div class="card-body grid-2">
              <div class="realtime-section">
                <div v-if="waterQualityLoading && !waterQualityRows.length" class="wq-card-loading">
                  <i class="wq-spinner"></i>
                  <span>水质数据加载中…</span>
                </div>
                <template v-else>
                <div class="threshold-compliance" v-if="waterThresholdConfig">
                  <span class="compliance-label">阈值符合率</span>
                  <span :class="['compliance-pct', waterQualityCompliance.pct >= 80 ? 'text-emerald' : waterQualityCompliance.pct >= 50 ? 'text-yellow' : 'text-red']">
                    {{ waterQualityCompliance.normal }}/{{ waterQualityCompliance.total }} ({{ waterQualityCompliance.pct }}%)
                  </span>
                </div>
                <h3 class="section-title">实时数值<span class="pool-hint" v-if="waterQualityPoolName"> · {{ waterQualityPoolDisplay }}</span></h3>
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
                      <tr v-for="(item, i) in waterQualityRows" :key="i">
                        <td>{{ item.name }}</td>
                        <td>
                          <span class="val text-cyan">{{ item.value }}</span>
                          <span class="unit text-slate" v-if="item.unit">{{ item.unit }}</span>
                        </td>
                        <td class="text-slate">{{ item.thresholdRange }}</td>
                        <td>
                          <span :class="['status-tag', getWaterStatusClass(item.evalStatus)]">
                            {{ getWaterStatusLabel(item.evalStatus, item) }}
                          </span>
                        </td>
                      </tr>
                      <tr v-if="!waterQualityRows.length">
                        <td colspan="4" class="health-loading">暂无水质实时数据</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                </template>
              </div>

              <div class="trend-section">
                <h3 class="section-title">水质趋势</h3>
                <div v-if="waterQualityLoading && !waterQualityRows.length" class="wq-card-loading trend-loading">
                  <i class="wq-spinner"></i>
                  <span>趋势加载中…</span>
                </div>
                <div ref="waterTrendChart" class="trend-chart" :class="{ 'is-dimmed': waterQualityLoading && !waterQualityRows.length }"></div>
              </div>
            </div>
          </div>

          <!-- 设备状态：整卡进入设备监控下级页 -->
          <div class="card equipment-card card-clickable" @click="goDeviceMonitor">
            <div class="card-header">
              <svg class="icon text-orange" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="3" />
                <path
                  d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
              </svg>
              <h2 class="title">设备状态</h2>
              <span class="card-enter-hint">详情 →</span>
            </div>

            <div class="card-body grid-4">
              <div class="status-list-section">
                <h3 class="section-title text-orange">运行状态</h3>
                <div class="status-scroll scroll-y">
                  <div class="status-scroll-inner">
                    <div v-for="(item, i) in scopedEquipmentStatus" :key="i"
                      :class="['status-box', item.status === 'running' ? 'border-green' : (item.status === 'fault' ? 'border-red' : 'border-yellow')]">
                      <div class="box-header">
                        <p class="name">{{ item.name }}</p>
                        <span :class="['tag', item.status === 'running' ? 'tag-green' : (item.status === 'fault' ? 'tag-red' : 'tag-yellow')]">
                          {{ deviceStatusLabel(item.status) }}
                        </span>
                      </div>
                      <p class="workshop">{{ item.workshop }}</p>
                      <div class="params">
                        <div class="param"><svg class="icon text-yellow" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
                          </svg><span>{{ item.voltage }}V</span></div>
                        <div class="param"><svg class="icon text-blue" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M18 20V10" />
                            <path d="M12 20V4" />
                            <path d="M6 20v-6" />
                          </svg><span>{{ item.current }}A</span></div>
                        <div class="param"><svg class="icon text-cyan" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
                          </svg><span>{{ item.frequency }}Hz</span></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="failure-stats-section">
                <h3 class="section-title text-orange">故障统计（本月）</h3>
                <div ref="failureChart" class="failure-chart"></div>
              </div>

              <div class="energy-stats-section">
                <h3 class="section-title text-yellow">能耗统计及优化</h3>
                <div class="energy-body">
                  <div class="energy-kpi bg-yellow-dim border-yellow">
                    <div class="kpi-main">
                      <span class="label text-yellow">本月总能耗</span>
                      <span class="val">{{ totalEnergyCost }}<span class="unit">MWh</span></span>
                    </div>
                    <span class="trend text-yellow">{{ energyMoM }}</span>
                  </div>
                  <div class="energy-visual">
                    <div ref="energyChart" class="energy-chart"></div>
                    <div class="energy-list">
                      <div v-for="(item, i) in energyData" :key="i" class="energy-item" :title="item.name">
                        <div class="label-box">
                          <div class="dot" :style="{ backgroundColor: item.color }"></div>
                          <span class="label">{{ item.name }}</span>
                        </div>
                        <span class="val">{{ formatEnergyValue(item.value) }}<span class="unit">MWh</span></span>
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
                      <tr v-for="(item, i) in medicationRecords" :key="i">
                        <td class="symptom">{{ plainText(item.symptom) }}</td>
                        <td class="time">{{ item.time }}</td>
                        <td class="drug">{{ plainText(item.drug) }}</td>
                        <td class="pond">{{ siteDisplayName(item.pond) }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧列 -->
        <div class="right-column">
          <!-- 任务与计划：整卡可点进下级；与 AI 报警卡分离，互不影响 -->
          <div class="card task-card card-clickable" @click="toPage('/mission_planning')">
            <div class="card-header">
              <svg class="icon text-purple" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                stroke-linecap="round" stroke-linejoin="round">
                <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
                <rect x="8" y="2" width="8" height="4" rx="1" ry="1" />
              </svg>
              <h2 class="title">任务与计划</h2>
              <span style="margin-left:auto;font-size:12px;opacity:.65">管理 / 新建 →</span>
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
                  <!-- 时间轴：默认 6–21，有更早/更晚任务时扩展 -->
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

          <!-- AI报警监控：仅右上角「详情」跳转，卡片其它区域可筛选/浏览 -->
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
              <span
                class="count text-red alert-detail-link"
                role="button"
                title="查看 AI 报警监控详情"
                @click.stop="toPage('/alert_monitor')"
              >{{ filteredAlerts.length }}条 · 详情</span>
            </div>

            <div class="card-body flex-col">
              <div class="filter-btns">
                <button v-for="type in alertTypes" :key="type.key" @click.stop="alertFilter = type.key"
                  :class="['filter-btn', alertFilter === type.key ? 'active' : '']">
                  {{ type.label }}
                </button>
              </div>
              <div class="alert-list scroll-y">
                <div v-for="(alert, index) in filteredAlerts" :key="index" class="alert-item bg-red-dim border-red">
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
import * as echarts from 'echarts';
import { getOverview3, getOverviewSchedule } from '@/api/fish/daPing';
import { getWaterThresholdConfig, formatThresholdRange, evaluateWaterStatus, getStatusInfo } from './index_daping3_waterThreshold';
import { getZkUseMedicinePage } from '@/api/fish/zkUseMedicine';
import { getZkTaskPage } from '@/api/fish/zkTask';
import { prefetchDigitalTwin } from '@/utils/digitalTwin';
import { fetchMissionPlanOpenTasks, fetchMissionPlanTodayTimelineEvents, buildDashboardTaskBoard, syncOpenSystemAlarmsToMission, syncModuleAiAlarmsToMission } from '@/utils/alarmTaskBridge';
import { readDashboardSnapshot, writeDashboardSnapshot } from '@/utils/dashboardCache';
import { fetchOverviewMonitor } from '@/utils/dashboardLiveBridge';
import { loadOverviewAlerts } from '@/utils/zkAlarmShared';
import { deviceStatusLabel, TYPE_LEGEND, DEFAULT_ENERGY_BY_TYPE } from '@/utils/deviceDemoData';
import { loadSharedSiteOptions, peekSharedSiteOptions, pickWorkshopOptionItems, pickSystemOptionItems, isAlarmInUserSiteScope, SITE_OPTIONS_CHANGED_EVENT } from '@/utils/siteOptions';
import { USER_SCOPE_CHANGED_EVENT } from '@/utils/deptScope';
import { normalizeLakeName, formatWorkshop, formatSystem } from '@/utils/siteNaming';
import { fetchUnifiedHealthList } from '@/utils/healthPreviewBridge';
import { withTimeout } from '@/utils/siteDisplay';

export default {
  name: "IndexDaping3",
  props: {
    embedded: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      overview: {
        totalWorth: 0,
        biologicalAssets: [],
        healthList: [],
        mortalityStats: null,
        precisionFeeding: null,
        growthCurve: [],
        feedCoefficient: null
      },
      // 水质阈值配置（来自 BFF 或内置默认）
      waterThresholdConfig: null,
      // 能耗 KPI
      totalEnergyCost: '13.6',
      energyMoM: '环比 -',
      // 各模块数据加载状态
      dataLoaded: {
        overview: false,
        alerts: false,
        devices: false,
        medicine: false,
        tasks: false,
        waterInfo: false,
        cost: false
      },
      /** 加载失败并回退默认值的模块名，用于顶部可见提示 */
      degradedSources: [],
      /** 健康度评估：仅使用统一健康接口，加载中 */
      healthListLoading: false,
      /** 水质情况卡片加载中 */
      waterQualityLoading: false,
      alertFilter: 'all',
      waterQualityTimeRange: '24h',
      waterQualityPoolName: '',
      waterDemoHistory: [],
      searchWorkshop: '',
      searchSystem: '',
      workshopOptions: [],
      systemOptions: [],
      siteOptionsLoading: false,
      sharedSiteOptions: null,
      // 监控块由 load* 从库表填充；初始为空
      abnormalAlerts: [],
      waterQualityRealtime: [],
      waterQuality24h: [],
      waterQuality7d: [],
      waterQuality30d: [],
      // 水质趋势当前时间范围的数据（由 switchWaterTrend 填充）
      waterTrendRangeData: null,
      allEquipmentStatus: [],
      equipmentFailureData: [],
      energyData: [],
      energyNote: '',
      medicationRecords: [],
      growthCurveData: [],
      // Excel：任务与计划删除季度计划
      tasks: {
        day: { urgent: [], priority: [], normal: [] },
        week: { urgent: [], priority: [], normal: [] },
        month: { urgent: [], priority: [], normal: [] },
        year: { urgent: [], priority: [], normal: [] },
      },
      /* 原写死日程已落库 zk_overview_schedule_event，以下注释保留备查
      events: {
        fish: [
          { hour: 6, event: '晨检鱼群状态', completed: true },
          { hour: 9, event: '投喂第一餐', completed: true },
          { hour: 12, event: '投喂第二餐', completed: true },
          { hour: 15, event: '投喂第三餐', completed: false },
          { hour: 18, event: '投喂第四餐', completed: false },
        ],
        water: [
          { hour: 8, event: '水质检测', completed: true },
          { hour: 10, event: '调节pH值', completed: true },
          { hour: 14, event: '检测溶氧', completed: false },
          { hour: 20, event: '夜间水质监测', completed: false },
        ],
        equipment: [
          { hour: 7, event: '设备巡检', completed: true },
          { hour: 11, event: '更换过滤棉', completed: true },
          { hour: 16, event: '清洗微滤机', completed: false },
          { hour: 19, event: '设备维护', completed: false },
        ],
        human: [
          { hour: 8, event: '班前会', completed: true },
          { hour: 12, event: '中班交接', completed: true },
          { hour: 17, event: '工作总结', completed: false },
          { hour: 20, event: '晚班交接', completed: false },
        ],
      },
      */
      events: { fish: [], water: [], equipment: [], human: [] },
      // periodLabels: { day: '日计划', week: '周计划', month: '月计划', quarter: '季度', year: '年度' },
      periodLabels: { day: '日计划', week: '周计划', month: '月计划', year: '年度' },
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
      }
    };
  },
  computed: {
    filteredAlerts() {
      const base = this.alertFilter === 'all'
        ? this.abnormalAlerts
        : this.abnormalAlerts.filter(alert => alert.category === this.alertFilter);
      // dashboard 接口已按 deptId 过滤且无车间字段，不再二次白名单过滤以免与详情页数量不一致
      return base || [];
    },
    scopedEquipmentStatus() {
      // 设备列表接口已带 deptId；无车间标注的本部门设备放行，鱼池编码不做硬匹配
      return (this.allEquipmentStatus || []).filter((d) => isAlarmInUserSiteScope(d, this.sharedSiteOptions));
    },
    /** 水质实时数据行（含动态阈值和状态）
     * 注意：waterRealtime 是「水温/pH/溶氧」等指标行，不是鱼池行；
     * 部门范围已由监控接口 deptId 保证，禁止再按站点白名单把指标名当鱼池滤空。
     */
    waterQualityRows() {
      return (this.waterQualityRealtime || [])
        .map(item => ({
          ...item,
          thresholdRange: this.getThresholdRange(item),
          evalStatus: this.getEvalStatus(item)
        }));
    },
    /** 阈值符合率（正常/总数） */
    waterQualityCompliance() {
      const rows = this.waterQualityRows;
      if (!rows.length) return { normal: 0, total: 0, pct: 100 };
      const normal = rows.filter(r => r.evalStatus === 'normal').length;
      return { normal, total: rows.length, pct: Math.round(normal / rows.length * 100) };
    },
    filteredSystemOptions() {
      return pickSystemOptionItems(
        { systemOptions: this.systemOptions },
        this.searchWorkshop
      );
    },
    waterQualityPoolDisplay() {
      return normalizeLakeName(this.waterQualityPoolName) || this.waterQualityPoolName;
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
    /** 预处理节点 left，避免模板调用 methods 在 HMR 下丢失 */
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
    waterQualityTimeRange(newVal) {
      this.switchWaterTrend(newVal);
    },
    searchWorkshop() {
      if (!this.searchSystem) return;
      const valid = this.filteredSystemOptions.some((o) => o.value === this.searchSystem);
      if (!valid) this.searchSystem = '';
    }
  },
  mounted() {
    this._prevSidebarOpened = this.$store.state.app.sidebar.opened;
    this.$store.dispatch('app/closeSideBar', { withoutAnimation: false });
    // 筛选项优先：先同步灌缓存，再异步拉车间/系统（不拉 zk_pond）
    this.hydrateSiteOptionsFromCache();
    this.loadSiteOptions();
    this._onSiteOptionsChanged = () => {
      if (!this._isDestroyed) this.loadSiteOptions(true);
    };
    window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    this._onUserScopeChanged = () => {
      if (this._isDestroyed) return;
      // 换用户/部门：先清空界面，再强制重拉（绕过 30s 节流与内存缓存）
      this.resetDashboardScopedState();
      this.loadSiteOptions(true);
      this.loadAllDashboardData(true);
    };
    window.addEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    // 快速进出模块后：先用会话快照渲染（含短暂过期的 stale），再后台刷新，避免首页长时间空白
    const snap = readDashboardSnapshot({ allowStale: true });
    if (snap) {
      this.applyDashboardSnapshot(snap);
      this.$nextTick(() => {
        if (!this._isDestroyed) this.initCharts();
      });
    }
    this.loadAllDashboardData();
    this._dashTimer = setInterval(() => {
      if (!this._isDestroyed) this.loadAllDashboardData();
    }, 5000);
    window.addEventListener('resize', this.handleResize);
    setTimeout(() => this.handleResize(), 350);
    // 进入总览即预取 3D 静态资源，缩短进入原有数字孪生页后的打开延迟
    this.prefetchDigitalTwin();
  },
  activated() {
    // keep-alive 场景下再次进入首页：优先展示缓存，后台静默刷新
    this.hydrateSiteOptionsFromCache();
    this.loadSiteOptions(true);
    const snap = readDashboardSnapshot({ allowStale: true });
    if (snap) {
      this.applyDashboardSnapshot(snap);
      this.$nextTick(() => {
        if (!this._isDestroyed) this.initCharts();
      });
    }
    this.loadAllDashboardData();
  },
  beforeDestroy() {
    this.destroyEventTipEl();
    this.persistDashboardSnapshot();
    if (this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged);
    }
    if (this._onUserScopeChanged) {
      window.removeEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged);
    }
    if (this._dashTimer) {
      clearInterval(this._dashTimer);
      this._dashTimer = null;
    }
    if (this._chartRetryTimer) {
      clearTimeout(this._chartRetryTimer);
      this._chartRetryTimer = null;
    }
    if (this._prevSidebarOpened && !this.$store.state.app.sidebar.opened) {
      this.$store.dispatch('app/toggleSideBar');
    }
    window.removeEventListener('resize', this.handleResize);
    Object.values(this.charts).forEach(chart => {
      if (chart) chart.dispose();
    });
  },
  methods: {
    prefetchDigitalTwin,
    /** 解析事件小时：优先 hour，其次 timeLabel（如 01:42） */
    resolveEventHour(ev) {
      if (!ev) return NaN;
      const h = Number(ev.hour);
      if (Number.isFinite(h)) return h;
      const m = String(ev.timeLabel || '').trim().match(/^(\d{1,2}):(\d{2})/);
      if (m) return Number(m[1]) + Number(m[2]) / 60;
      return NaN;
    },
    /** 悬浮窗挂到 body，避开卡片 overflow / transform 裁切 */
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
    applyDashboardSnapshot(snap) {
      if (!snap || typeof snap !== 'object') return;
      if (snap.overview) {
        const { healthList, ...overviewRest } = snap.overview;
        this.overview = Object.assign({}, this.overview, overviewRest);
        // 快照里的 healthList 也必须是统一健康接口格式（含 overallRating），避免回显旧总览数据
        if (Array.isArray(healthList) && healthList.length && healthList.some((h) => h && h.overallRating != null)) {
          this.overview.healthList = healthList;
        }
      }
      if (snap.abnormalAlerts) this.abnormalAlerts = snap.abnormalAlerts;
      if (snap.allEquipmentStatus) this.allEquipmentStatus = snap.allEquipmentStatus;
      if (snap.equipmentFailureData) this.equipmentFailureData = snap.equipmentFailureData;
      if (snap.medicationRecords) this.medicationRecords = snap.medicationRecords;
      if (snap.taskBoard) this.taskBoard = snap.taskBoard;
      if (snap.waterQualityRealtime) this.waterQualityRealtime = snap.waterQualityRealtime;
      if (snap.waterQualityPoolName) this.waterQualityPoolName = snap.waterQualityPoolName;
      if (snap.energyData) this.energyData = snap.energyData;
      if (snap.growthCurveData) this.growthCurveData = snap.growthCurveData;
      if (snap.totalEnergyCost != null) this.totalEnergyCost = snap.totalEnergyCost;
      if (snap.dataLoaded) this.dataLoaded = Object.assign({}, this.dataLoaded, snap.dataLoaded);
    },
    persistDashboardSnapshot() {
      writeDashboardSnapshot({
        overview: this.overview,
        abnormalAlerts: this.abnormalAlerts,
        allEquipmentStatus: this.allEquipmentStatus,
        equipmentFailureData: this.equipmentFailureData,
        medicationRecords: this.medicationRecords,
        taskBoard: this.taskBoard,
        waterQualityRealtime: this.waterQualityRealtime,
        waterQualityPoolName: this.waterQualityPoolName,
        energyData: this.energyData,
        growthCurveData: this.growthCurveData,
        totalEnergyCost: this.totalEnergyCost,
        dataLoaded: this.dataLoaded
      });
    },
    async loadAllDashboardData(force = false) {
      const now = Date.now();
      if (!force && this._dashLoading) return;
      if (!force && this._lastDashLoad && now - this._lastDashLoad < 5000) return;
      // force 时打断节流：允许覆盖进行中的旧请求结果
      this._dashLoadToken = (this._dashLoadToken || 0) + 1;
      const token = this._dashLoadToken;
      this._dashLoading = true;
      this._lastDashLoad = now;
      this.degradedSources = [];
      try {
        await Promise.all([
          this.loadOverview3(force),
          this.loadWaterThreshold(),
          this.loadDashboardAlerts(),
          this.loadDeviceStatus(force),
          this.loadEquipmentFailures(force),
          this.loadMedicineRecords(),
          this.loadTasks(),
          this.loadWaterInfo(force),
          this.loadEnergyCost(force),
          this.loadScheduleEvents()
        ]);
        if (token !== this._dashLoadToken) return;
        this.persistDashboardSnapshot();
        this.$nextTick(() => {
          if (this._isDestroyed || token !== this._dashLoadToken) return;
          this.initCharts();
          requestAnimationFrame(() => {
            if (!this._isDestroyed) this.handleResize();
          });
        });
      } finally {
        if (token === this._dashLoadToken) this._dashLoading = false;
      }
    },
    /** 切换用户/部门时立即清空易串数据的模块，避免旧部门内容滞留 */
    resetDashboardScopedState() {
      this._lastDashLoad = 0;
      this.overview = Object.assign({}, this.overview, {
        healthList: [],
        growthCurve: [],
        feedCoefficient: null,
        totalWorth: 0,
        biologicalAssets: [],
        mortalityStats: null,
        precisionFeeding: null
      });
      this.growthCurveData = [];
      this.waterQualityRealtime = [];
      this.waterQuality24h = [];
      this.waterQuality7d = [];
      this.waterQuality30d = [];
      this.waterTrendRangeData = null;
      this.waterQualityPoolName = '';
      this.waterQualityLoading = true;
      this.allEquipmentStatus = [];
      this.equipmentFailureData = [];
      this.abnormalAlerts = [];
      this.medicationRecords = [];
      this.energyData = [];
      this.events = { fish: [], water: [], equipment: [], human: [] };
      this.searchWorkshop = '';
      this.searchSystem = '';
      this.dataLoaded = {
        overview: false,
        alerts: false,
        devices: false,
        medicine: false,
        tasks: false,
        waterInfo: false,
        cost: false
      };
    },
    markDegraded(name) {
      const n = String(name || '').trim();
      if (!n) return;
      if (this.degradedSources.indexOf(n) === -1) this.degradedSources.push(n);
    },
    /* ===== 1. 渔况智览6模块 ===== */
    async loadOverview3(force = false) {
      try {
        const res = await withTimeout(getOverview3(), 12000, null);
        if (res && res.data) {
          const { healthList, ...overviewRest } = res.data;
          this.overview = Object.assign({}, this.overview, overviewRest);
          this.growthCurveData = (res.data.growthCurve || []).map(item => ({
            month: this.formatChartDay(item.staticDay),
            weight: Number(item.fishGainCount) || 0
          }));
          this.dataLoaded.overview = true;
        } else if (force) {
          this.growthCurveData = [];
        }
        await this.syncOverviewHealthFromScores(force);
      } catch (error) {
        console.warn('[总览] 渔况智览数据加载失败，使用默认值', error.message || error);
        this.markDegraded('渔况智览');
        if (force) this.growthCurveData = [];
        await this.syncOverviewHealthFromScores(force).catch(() => {});
      }
    },
    /** 健康度评估唯一数据源：与鱼类健康状态页一致（评分+完整池名） */
    async syncOverviewHealthFromScores(force = false) {
      this.healthListLoading = true;
      try {
        const bundle = await withTimeout(fetchUnifiedHealthList({ force: !!force }), 15000, null);
        if (bundle && Array.isArray(bundle.overviewList)) {
          // 健康列表接口已按部门过滤；勿再用车间白名单二次过滤。
          // sharedSiteOptions 未就绪、或库内「车间1」等名称不在生产记录白名单时，会把整卡滤成「暂无健康评分数据」。
          const list = bundle.overviewList.filter(
            (row) => row && !row.pendingHealth && row.overallRating != null
          );
          this.overview.healthList = list;
        } else if (force) {
          this.overview.healthList = [];
        }
      } catch (e) {
        console.warn('[总览] 健康评分加载失败', e && e.message);
        this.markDegraded('健康度评估');
        if (force) this.overview.healthList = [];
      } finally {
        this.healthListLoading = false;
      }
    },
    /* ===== 2. 水质阈值配置 ===== */
    async loadWaterThreshold() {
      try {
        this.waterThresholdConfig = await getWaterThresholdConfig();
      } catch (e) {
        console.warn('[总览] 水质阈值加载失败', e);
        this.markDegraded('水质阈值');
      }
    },
    /* ===== 3. AI报警监控（与详情页同源：zk_device_alarm） ===== */
    async loadDashboardAlerts() {
      try {
        const { alerts } = await loadOverviewAlerts();
        this.abnormalAlerts = alerts || [];
        this.dataLoaded.alerts = true;
        if (this.abnormalAlerts.length) {
          syncModuleAiAlarmsToMission(this.abnormalAlerts, {
            prefix: 'DP3-AI-',
            source: 'daping3',
            category: 'equip'
          }).catch(e => console.warn('[总览] 报警同步任务', e));
          syncOpenSystemAlarmsToMission().catch(e => console.warn('[总览] 系统报警同步任务失败', e));
        }
      } catch (e) {
        console.warn('[总览] AI报警数据加载失败，使用默认值', e.message || e);
        this.abnormalAlerts = [];
        this.markDegraded('AI报警');
      }
    },
    /* ===== 4. 设备运行状态（zk_device） ===== */
    async loadDeviceStatus(force = false) {
      try {
        const mon = await fetchOverviewMonitor(!!force);
        const devices = (mon && mon.devices) || [];
        this.allEquipmentStatus = devices.map((d) => ({
          name: d.name,
          status: d.status,
          workshop: d.workshop,
          system: d.system || d.pondSystemName || '',
          pondName: d.pondName || '',
          pondDetailNum: d.pondDetailNum || '',
          voltage: d.voltage,
          current: d.current,
          frequency: d.frequency,
          currValue: d.currValue,
          unit: d.unit
        }));
        this.dataLoaded.devices = true;
      } catch (e) {
        console.warn('[总览] 设备数据加载失败', e.message || e);
        this.markDegraded('设备状态');
        if (force) this.allEquipmentStatus = [];
      }
    },
    /** 检测设备是否超出阈值 */
    _checkDeviceThreshold(d) {
      if (d.currValue == null || d.standardStart == null || d.standardEnd == null) return false;
      const v = Number(d.currValue), lo = Number(d.standardStart), hi = Number(d.standardEnd);
      return v < lo || v > hi;
    },
    /* ===== 4b. 设备故障统计（zk_device_alarm 近90天） ===== */
    async loadEquipmentFailures(force = false) {
      try {
        const mon = await fetchOverviewMonitor(!!force);
        this.equipmentFailureData = (mon && mon.faultMonth) || [];
        this.dataLoaded.devices = true;
        this.$nextTick(() => this.initFailureChart && this.initFailureChart());
      } catch (e) {
        console.warn('[总览] 设备故障数据加载失败', e.message || e);
        this.markDegraded('设备故障');
        if (force) this.equipmentFailureData = [];
      }
    },
    /* ===== 5. 用药记录 ===== */
    async loadMedicineRecords() {
      try {
        const res = await getZkUseMedicinePage({ pageNo: 1, pageSize: 12 });
        const list = (res && res.data && res.data.list) || [];
        if (list.length) {
          this.medicationRecords = list.map(m => ({
            symptom: this.plainText(m.controlTitle || m.controlContent || '未记录'),
            time: m.createTime ? this.formatDateTime(m.createTime) : '-',
            drug: this.plainText(m.medicineName || '-'),
            pond: this.siteDisplayName(m.pondName || '-')
          }));
          this.dataLoaded.medicine = true;
        }
      } catch (e) {
        console.warn('[总览] 用药记录加载失败，使用默认值', e.message || e);
        this.markDegraded('用药记录');
      }
    },
    /* ===== 6. 任务与计划（优先 wiidu 任务与计划，兼容旧 zk_task） ===== */
    async loadTasks() {
      try {
        const missionRows = await fetchMissionPlanOpenTasks(48)
        if (missionRows.length) {
          this.tasks = buildDashboardTaskBoard(missionRows)
          this.dataLoaded.tasks = true
          return
        }
        const res = await getZkTaskPage({ pageNo: 1, pageSize: 20 });
        const list = (res && res.data && res.data.list) || [];
        if (list.length) {
          const now = new Date();
          const dayEnd = new Date(now); dayEnd.setHours(23, 59, 59);

          const urgent = [], priority = [], normal = [];
          list.forEach(t => {
            const status = t.status;
            if (status === '2' && t.endTime && new Date(t.endTime) <= dayEnd) {
              urgent.push(t.taskName);
            } else if (status === '2') {
              priority.push(t.taskName);
            } else {
              normal.push(t.taskName);
            }
          });

          this.tasks = {
            day: { urgent: urgent.slice(0, 3), priority: priority.slice(0, 3), normal: normal.slice(0, 3) },
            week: { urgent: urgent.slice(0, 3), priority: priority.slice(1, 4), normal: normal.slice(1, 4) },
            month: { urgent: urgent.slice(0, 3), priority: priority.slice(0, 3), normal: normal.slice(2, 5) },
            year: { urgent: urgent.slice(0, 3), priority: priority.slice(0, 3), normal: normal.slice(0, 3) }
          };
          this.dataLoaded.tasks = true;
        }
      } catch (e) {
        console.warn('[总览] 任务数据加载失败，使用默认值', e.message || e);
        this.markDegraded('任务计划');
      }
    },
    /* ===== 7. 水质实时信息（zk_device_record） ===== */
    async loadScheduleEvents() {
      try {
        // 优先：任务与计划当天节点；即使全空也展示空节点图，不再回填日程模板
        const fromMission = await fetchMissionPlanTodayTimelineEvents(120)
        if (fromMission && typeof fromMission === 'object') {
          this.events = {
            fish: fromMission.fish || [],
            water: fromMission.water || [],
            equipment: fromMission.equipment || [],
            human: fromMission.human || []
          }
          return
        }
        // 回退：概览日程表模板（仅在任务与计划接口不可用时）
        const res = await getOverviewSchedule()
        const rows = (res && res.data) || []
        const map = { fish: [], water: [], equipment: [], human: [] }
        const catMap = { fish: 'fish', water: 'water', equip: 'equipment', equipment: 'equipment', device: 'equipment', person: 'human', human: 'human' }
        rows.forEach((r) => {
          const key = catMap[r.category] || r.category
          if (!map[key]) map[key] = []
          const hour = Number(r.hourOfDay) || 0
          map[key].push({
            hour,
            timeLabel: String(Math.floor(hour)).padStart(2, '0') + ':00',
            event: r.eventText,
            completed: !!r.completed
          })
        })
        Object.keys(map).forEach((k) => {
          map[k] = (map[k] || [])
            .slice()
            .sort((a, b) => a.hour - b.hour)
            .slice(0, 8)
        })
        this.events = map
      } catch (e) {
        console.warn('[总览] 日程事件加载失败', e && e.message)
        this.events = { fish: [], water: [], equipment: [], human: [] }
      }
    },
    async loadWaterInfo(force = false) {
      this.waterQualityLoading = !(this.waterQualityRealtime && this.waterQualityRealtime.length);
      try {
        const mon = await fetchOverviewMonitor(!!force);
        const rows = (mon && mon.waterRealtime) || [];
        const hasValue = rows.some(w => w && w.value != null && !Number.isNaN(Number(w.value)));
        if (hasValue) {
          this.waterQualityRealtime = rows;
          this.waterQuality24h = (mon.waterTrend && mon.waterTrend.length)
            ? mon.waterTrend
            : this.buildSyntheticWaterTrend(rows, 24);
          // 点数过少时回退：7d→24h 曲线 / 合成；避免只剩 1 个日点看起来像空白
          const t7 = (mon.waterTrend7d && mon.waterTrend7d.length) ? mon.waterTrend7d : [];
          const t30 = (mon.waterTrend30d && mon.waterTrend30d.length) ? mon.waterTrend30d : [];
          this.waterQuality7d = t7.length >= 2
            ? t7
            : (this.waterQuality24h.length >= 2
              ? this.waterQuality24h
              : this.buildSyntheticWaterTrend(rows, 7));
          this.waterQuality30d = t30.length >= 2
            ? t30
            : (t7.length >= 2
              ? t7
              : (this.waterQuality24h.length >= 2
                ? this.waterQuality24h
                : this.buildSyntheticWaterTrend(rows, 30)));
          this.dataLoaded.waterInfo = true;
          this.syncAbnormalWaterToMission();
          this.switchWaterTrend(this.waterQualityTimeRange);
          return;
        }
        // 无本部门实时值：清空旧部门水质，避免串显
        this.waterQualityRealtime = [];
        this.waterQuality24h = this.buildSyntheticWaterTrend([], 24);
        this.waterQuality7d = this.buildSyntheticWaterTrend([], 7);
        this.waterQuality30d = this.buildSyntheticWaterTrend([], 30);
        this.switchWaterTrend(this.waterQualityTimeRange);
        this.markDegraded('水质实时');
      } catch (e) {
        console.warn('[总览] 水质实时数据加载失败', e.message || e);
        this.waterQualityRealtime = [];
        this.waterQuality24h = this.buildSyntheticWaterTrend([], 24);
        this.waterQuality7d = this.buildSyntheticWaterTrend([], 7);
        this.waterQuality30d = this.buildSyntheticWaterTrend([], 30);
        this.switchWaterTrend(this.waterQualityTimeRange);
        this.markDegraded('水质实时');
      } finally {
        this.waterQualityLoading = false;
      }
    },
    /** 监控趋势为空时，用实时指标合成曲线，保证首页「水质趋势」可见 */
    buildSyntheticWaterTrend(realtimeRows, mode) {
      const pick = (key, fallback) => {
        const hit = (realtimeRows || []).find(r => r && (r.key === key || (r.name || '').includes(key === 'temp' ? '水温' : key === 'oxygen' ? '溶氧' : 'pH')))
        const v = hit && hit.value != null ? Number(hit.value) : fallback
        return Number.isFinite(v) ? v : fallback
      }
      const t0 = pick('temp', 25)
      const o0 = pick('oxygen', 7.2)
      const p0 = pick('ph', 7.4)
      const points = mode === 24 ? 12 : (mode === 7 ? 14 : 15)
      const list = []
      for (let i = 0; i < points; i++) {
        const wave = Math.sin(i / 2.2) * 0.4
        let time
        if (mode === 24) {
          time = String(Math.floor((i * 24) / points)).padStart(2, '0') + ':00'
        } else if (mode === 7) {
          const d = new Date(Date.now() - (points - 1 - i) * 12 * 3600000)
          time = (d.getMonth() + 1) + '/' + d.getDate() + ' ' + String(d.getHours()).padStart(2, '0') + 'h'
        } else {
          const d = new Date(Date.now() - (points - 1 - i) * 2 * 24 * 3600000)
          time = (d.getMonth() + 1) + '/' + d.getDate()
        }
        list.push({
          time,
          temp: Number((t0 + wave * 0.45).toFixed(2)),
          oxygen: Number((o0 + wave * 0.28).toFixed(2)),
          ph: Number((p0 + wave * 0.035).toFixed(2))
        })
      }
      return list
    },
    /** 水质异常/预警 → 任务与计划 */
    syncAbnormalWaterToMission() {
      const alarms = (this.waterQualityRealtime || []).map((item, idx) => {
        const status = this.getEvalStatus(item);
        if (status !== 'critical' && status !== 'warn') return null;
        const level = status === 'critical' ? 'urgent' : 'priority';
        return {
          id: 'WQ-' + (item._key || item.name || idx),
          title: '水质' + (item.name || '指标') + (status === 'critical' ? '异常' : '预警'),
          message: (item.name || '') + ' 当前 ' + item.value + (item.unit || '') + '（设定 ' + this.getThresholdRange(item) + '）',
          suggestion: '请核查传感器与现场工况并处置',
          priority: level,
          category: 'water',
          currentValue: String(item.value != null ? item.value : ''),
          thresholdValue: this.getThresholdRange(item),
          unit: item.unit || ''
        };
      }).filter(Boolean);
      if (!alarms.length) return;
      syncModuleAiAlarmsToMission(alarms, {
        prefix: 'DP3-WQ-',
        source: 'daping_water',
        category: 'water'
      }).catch(e => console.warn('[总览] 水质同步任务', e));
    },
    /* ===== 8. 能耗/费用统计（经营成本或净化成本明细） ===== */
    async loadEnergyCost(force = false) {
      try {
        const mon = await fetchOverviewMonitor(!!force);
        this.energyNote = (mon && mon.energyNote) || '';
        let list = (mon && mon.energy && mon.energy.length) ? mon.energy.slice() : [];
        // 接口超时/查询失败时用与设备监控页一致的兜底，避免「- MWh」空白
        if (!list.length) {
          list = TYPE_LEGEND.map(t => ({
            name: t.name,
            value: Number(DEFAULT_ENERGY_BY_TYPE[t.name]) || 0,
            color: t.color
          })).filter(d => d.value > 0);
          this.energyNote = this.energyNote || '展示最新电度汇总';
        }
        // 与设备监控页同口径，不做二次单位换算，避免数值观感突变
        this.energyData = list;
        const sum = list.reduce((s, d) => s + (Number(d.value) || 0), 0);
        this.totalEnergyCost = sum > 0 ? sum.toFixed(1) : '-';
        this.energyMoM = '环比 -';
        this.dataLoaded.cost = true;
        this.$nextTick(() => this.initEnergyChart && this.initEnergyChart());
      } catch (e) {
        console.warn('[总览] 能耗数据加载失败', e.message || e);
        this.energyData = TYPE_LEGEND.map(t => ({
          name: t.name,
          value: Number(DEFAULT_ENERGY_BY_TYPE[t.name]) || 0,
          color: t.color
        })).filter(d => d.value > 0);
        const sum = this.energyData.reduce((s, d) => s + (Number(d.value) || 0), 0);
        this.totalEnergyCost = sum.toFixed(1);
        this.energyMoM = '环比 -';
        this.dataLoaded.cost = true;
        this.$nextTick(() => this.initEnergyChart && this.initEnergyChart());
        this.markDegraded('能耗');
      }
    },
    /* ===== 辅助方法 ===== */
    /** 将后端 LocalDateTime 时间戳统一转为毫秒数 */
    _parseDateTimestamp(val) {
      if (val == null) return null;
      if (typeof val === 'number') return val;           // Jackson epoch millis
      if (Array.isArray(val)) return new Date(val[0], (val[1]||1)-1, val[2]||1, val[3]||0, val[4]||0, val[5]||0).getTime();
      if (typeof val === 'string') {
        const d = new Date(val);
        return isNaN(d.getTime()) ? null : d.getTime();
      }
      return null;
    },
    formatDateTime(val) {
      if (!val) return '-';
      const d = new Date(val);
      const pad = n => String(n).padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    },
    /*
     * ===== 水质阈值辅助方法 =====
     * 依赖 waterThresholdConfig（由 loadWaterThreshold 填充）
     */
    getThresholdRange(item) {
      if (item && item._fromWaterPage && item.range) return item.range;
      if (!this.waterThresholdConfig) return item.range || '-';
      const key = this.resolveThresholdKey(item);
      if (!key) return item.range || '-';
      return formatThresholdRange(this.waterThresholdConfig, key);
    },
    getEvalStatus(item) {
      if (item && item._fromWaterPage) return item.status || 'normal';
      if (!this.waterThresholdConfig) return item.status || 'normal';
      const key = this.resolveThresholdKey(item);
      if (!key) return item.status || 'normal';
      return evaluateWaterStatus(this.waterThresholdConfig, key, item.value);
    },
    /** 水质指标名 → 阈值配置 key 映射 */
    resolveThresholdKey(item) {
      const name = (item.name || '').trim();
      const map = {
        '水温': 'doTemp',
        'pH值': 'phPh',
        '溶解氧': 'doOxygen',
        '氨氮': 'nh3',
        '亚硝酸盐': 'no2',
        '硝酸盐': 'no3',
        'ORP': 'orp',
        '碱度': 'alk',
        '硬度': 'hard',
        '二氧化碳': 'co2',
        '总磷': 'tp',
        '总氮': 'tn',
        '浊度': 'tss'
      };
      return map[name] || null;
    },
    getWaterStatusClass(status) {
      return getStatusInfo(status).cls;
    },
    getWaterStatusLabel(status, item) {
      if (item && item.statusLabel) return item.statusLabel;
      return getStatusInfo(status).label;
    },
    deviceStatusLabel,
    /** 配套系统暂缓开发，保留展示不跳转 */
    goSupportSystem() {
      this.$message && this.$message.info('配套系统暂未开放');
    },
    /** 跳转阈值设置页面 */
    goThresholdSettings() {
      this.$router.push({
        path: '/iot/thresholdSettings',
        query: { from: this.$route.fullPath || '/index_daping3' }
      }).catch(() => {});
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
      const list = this.overview.growthCurve || [];
      if (!list.length) return '-';
      const latest = list[list.length - 1];
      return Number(latest.fishGainCount || 0).toFixed(2);
    },
    formatTodayGain() {
      const today = this.formatDateKey(new Date());
      const item = (this.overview.growthCurve || []).find(row => this.formatDateKey(row.staticDay) === today);
      if (!item) return '0kg';
      return Number(item.fishGainCount || 0).toFixed(2) + 'kg';
    },
    formatChartDay(day) {
      if (!day) return '';
      // Jackson 常把 LocalDate 序列化为 [y,m,d]
      if (Array.isArray(day) && day.length >= 3) {
        return `${String(day[1]).padStart(2, '0')}-${String(day[2]).padStart(2, '0')}`;
      }
      const text = String(day);
      const parts = text.split(/[-,/]/);
      if (parts.length >= 3) return `${String(parts[1]).padStart(2, '0')}-${String(parts[2]).padStart(2, '0')}`;
      return text;
    },
    formatDateKey(day) {
      if (!day) return '';
      if (Array.isArray(day) && day.length >= 3) {
        return `${day[0]}-${String(day[1]).padStart(2, '0')}-${String(day[2]).padStart(2, '0')}`;
      }
      return String(day).substring(0, 10);
    },
    getAssetColorClass(index) {
      const classes = ['text-cyan', 'text-purple', 'text-amber'];
      return classes[index % classes.length];
    },
    getHealthItemClass(status) {
      const s = this.plainText(status);
      if (s === '不健康') return 'bg-red-dim border-red';
      if (s === '亚健康') return 'bg-yellow-dim border-yellow';
      return 'bg-emerald-dim border-emerald';
    },
    getHealthStatusClass(status) {
      const s = this.plainText(status);
      if (s === '不健康') return 'text-red';
      if (s === '亚健康') return 'text-yellow';
      return 'text-emerald';
    },
    toPage(path) {
      this.$router.push(path).catch(() => { });
    },
    /** 去掉接口返回的富文本标签，避免页面露出 <p> */
    plainText(val) {
      if (val == null || val === '') return '';
      const div = document.createElement('div');
      div.innerHTML = String(val);
      return (div.textContent || div.innerText || '').replace(/\s+/g, ' ').trim();
    },
    siteDisplayName(val) {
      const raw = this.plainText(val) || String(val || '').trim();
      return normalizeLakeName(raw) || raw;
    },
    /** 能耗数值：去掉多余小数，避免窄列把单位挤没 */
    formatEnergyValue(val) {
      const n = Number(val);
      if (!Number.isFinite(n)) return val == null || val === '' ? '-' : val;
      if (Math.abs(n) >= 100) return n.toFixed(0);
      if (Math.abs(n) >= 10) return n.toFixed(1);
      return n.toFixed(2);
    },
    hydrateSiteOptionsFromCache() {
      const cached = peekSharedSiteOptions();
      if (!cached) return;
      const ws = pickWorkshopOptionItems(cached);
      const sys = cached.systemOptions || [];
      if (ws.length) this.workshopOptions = ws;
      if (sys.length) this.systemOptions = sys;
    },
    applySiteOptions(opts) {
      if (!opts) return;
      this.sharedSiteOptions = opts;
      const ws = pickWorkshopOptionItems(opts);
      const sys = opts.systemOptions || [];
      // 成对更新，避免异步交错导致「车间有、系统空」
      if (ws.length || sys.length) {
        this.workshopOptions = ws.length ? ws : this.workshopOptions;
        this.systemOptions = sys.length ? sys : this.systemOptions;
      }
    },
    async loadSiteOptions(force = false) {
      if (this._siteOptionsLoadingLock && !force) return;
      this.hydrateSiteOptionsFromCache();
      // 已有选项时不再显示「加载中」，避免 Chromium 下反复 focus 触发闪烁
      this.siteOptionsLoading = !this.workshopOptions.length && !this.systemOptions.length;
      this._siteOptionsLoadingLock = true;
      const applyPartial = (partial) => {
        if (this._isDestroyed) return;
        this.applySiteOptions(partial);
        if (this.workshopOptions.length) this.siteOptionsLoading = false;
      };
      try {
        // 总览筛选项只需车间+系统；车间走大屏轻量接口，先到先填
        const opts = await loadSharedSiteOptions({
          force: !!force,
          includePonds: false,
          onPartial: applyPartial
        });
        if (this._isDestroyed) return;
        this.applySiteOptions(opts);
        // 仍无车间：强制再拉一次（绕过可能的半空缓存）
        if (!this.workshopOptions.length) {
          const again = await loadSharedSiteOptions({
            force: true,
            includePonds: false,
            onPartial: applyPartial
          });
          if (this._isDestroyed) return;
          this.applySiteOptions(again);
        }
        if (!this.workshopOptions.length && !this.systemOptions.length) {
          this._siteOptionsFailAt = Date.now();
          this.$message.warning('车间/系统筛选项暂时为空，请稍后重试或检查存塘基础数据');
        } else {
          this._siteOptionsFailAt = 0;
        }
      } catch (e) {
        console.warn('[index_daping3] site options', e);
        this._siteOptionsFailAt = Date.now();
        this.hydrateSiteOptionsFromCache();
      } finally {
        this.siteOptionsLoading = false;
        this._siteOptionsLoadingLock = false;
      }
    },
    /** 下拉获焦时若仍为空则补拉，避免偶发空白；失败后冷却，防止 Chromium 持续加载 */
    ensureSiteOptionsLoaded() {
      if (this.siteOptionsLoading || this._siteOptionsLoadingLock) return;
      if (this.workshopOptions.length && this.systemOptions.length) return;
      if (this._siteOptionsFailAt && Date.now() - this._siteOptionsFailAt < 60000) return;
      this.loadSiteOptions();
    },
    /** 首页健康度评估 → 鱼类健康状态独立路由，禁止进鱼病诊断 */
    goHealthAssessment() {
      this.$router.push({
        path: '/keyshow/fishHealthStatus',
        query: { from: '/index_daping3' }
      }).catch(() => { });
    },
    goMortalityAssessment() {
      const query = { activeTab: 'mortality', from: '/index_daping3' };
      if (this.searchWorkshop) query.workshop = this.searchWorkshop;
      if (this.searchSystem) query.system = this.searchSystem;
      this.$router.push({
        path: '/keyshow/zkDeathSurvivalRate',
        query
      }).catch(() => { });
    },
    goFishDisease(extra = {}) {
      const query = { from: '/index_daping3', ...extra };
      this.$router.push({ path: '/fish-disease', query }).catch(() => { });
    },
    goFishHealthForPond(item) {
      const pondName = this.plainText((item && (item.pondName || item.pondDetailNum || item.pondCode || item.pondId)) || '')
        || this.siteDisplayName((item && (item.pondName || item.pondDetailNum || item.pondCode || item.pondId)) || '');
      this.$router.push({
        path: '/keyshow/fishHealthStatus',
        query: {
          from: '/index_daping3',
          pondName: pondName || undefined
        }
      }).catch(() => { });
    },
    goWaterQuality() {
      this.$router.push({ path: '/water_quality', query: { from: '/index_daping3' } }).catch(() => {});
    },
    goDeviceMonitor() {
      this.$router.push({ path: '/device_monitor', query: { from: '/index_daping3' } }).catch(() => {});
    },
    goWorkshopCNC() {
      if (!this.searchWorkshop && !this.searchSystem) {
        this.$message.warning('请先选择车间或系统');
        return;
      }
      // 只选系统也可进入；车间可选，不再强制
      // 进页前预取大屏分包，缩短白屏/卡顿
      import(/* webpackChunkName: "index-daping4" */ '@/views/index_daping4.vue').catch(() => {});
      import(/* webpackChunkName: "echarts" */ 'echarts').catch(() => {});
      const query = {};
      if (this.searchWorkshop) query.workshop = this.searchWorkshop;
      if (this.searchSystem) query.system = this.searchSystem;
      this.$router.push({
        path: '/keyshow/workshopCNC',
        query
      }).catch(() => { });
    },
    handleResize() {
      Object.values(this.charts).forEach(chart => {
        if (chart) chart.resize();
      });
    },
    /** 容器宽高为 0 时跳过 init，避免 ECharts 刷屏警告；稍后自动重试 */
    canInitChart(el) {
      if (!el) return false;
      const w = el.clientWidth || el.offsetWidth || 0;
      const h = el.clientHeight || el.offsetHeight || 0;
      return w > 0 && h > 0;
    },
    scheduleChartRetry() {
      if (this._isDestroyed || this._chartRetryTimer) return;
      this._chartRetryTimer = setTimeout(() => {
        this._chartRetryTimer = null;
        if (!this._isDestroyed) this.initCharts();
      }, 280);
    },
    initCharts() {
      if (this._isDestroyed) return;
      this.initGrowthChart();
      this.initWaterTrendChart();
      this.initFailureChart();
      this.initEnergyChart();
    },
    initGrowthChart() {
      if (this._isDestroyed || !this.$refs.growthChart) return;
      if (!this.canInitChart(this.$refs.growthChart)) {
        this.scheduleChartRetry();
        return;
      }
      if (this.charts.growth) {
        this.charts.growth.dispose();
      }
      try {
      const chart = echarts.init(this.$refs.growthChart);
      const option = {
        grid: { left: 0, right: 0, top: 5, bottom: 0 },
        xAxis: { type: 'category', show: false, data: this.growthCurveData.map(d => d.month) },
        yAxis: { type: 'value', show: false },
        series: [{
          data: this.growthCurveData.map(d => d.weight),
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
      } catch (e) { console.warn('growth chart init skipped', e); }
    },
    /** 切换水质趋势时间范围（24h/7d/30d 均来自库表聚合） */
    switchWaterTrend(range) {
      if (range === '7d') {
        this.waterTrendRangeData = (this.waterQuality7d && this.waterQuality7d.length)
          ? this.waterQuality7d : this.waterQuality24h;
      } else if (range === '30d') {
        this.waterTrendRangeData = (this.waterQuality30d && this.waterQuality30d.length)
          ? this.waterQuality30d : this.waterQuality24h;
      } else {
        this.waterTrendRangeData = this.waterQuality24h;
      }
      this.$nextTick(() => {
        if (!this._isDestroyed) this.initWaterTrendChart();
      });
    },
    initWaterTrendChart() {
      if (this._isDestroyed || !this.$refs.waterTrendChart) return;
      if (!this.canInitChart(this.$refs.waterTrendChart)) {
        this.scheduleChartRetry();
        return;
      }
      if (this.charts.waterTrend) this.charts.waterTrend.dispose();
      try {
      let data = this.waterTrendRangeData || this.waterQuality24h || [];
      if (!data.length) {
        data = this.buildSyntheticWaterTrend(this.waterQualityRealtime || [], 24);
        this.waterTrendRangeData = data;
      }
      const chart = echarts.init(this.$refs.waterTrendChart);
      const sparse = data.length > 0 && data.length <= 3;
      const option = {
        tooltip: { trigger: 'axis', backgroundColor: '#1e293b', borderColor: '#334155', textStyle: { color: '#fff', fontSize: 11 } },
        legend: { textStyle: { color: '#94a3b8', fontSize: 10 }, itemWidth: 8, itemHeight: 8, bottom: 0 },
        grid: { left: '3%', right: '3%', top: '6%', bottom: '18%', containLabel: true },
        xAxis: { type: 'category', data: data.map(d => d.time), axisLabel: { color: '#94a3b8', fontSize: 10 }, axisLine: { lineStyle: { color: '#334155' } } },
        yAxis: { type: 'value', scale: true, axisLabel: { color: '#94a3b8', fontSize: 10 }, splitLine: { lineStyle: { color: '#334155', type: 'dashed' } } },
        series: [
          { name: '水温(°C)', type: 'line', data: data.map(d => d.temp), itemStyle: { color: '#f59e0b' }, lineStyle: { color: '#f59e0b' }, smooth: true, showSymbol: sparse, symbolSize: 6, connectNulls: true },
          { name: '溶氧(mg/L)', type: 'line', data: data.map(d => d.oxygen), itemStyle: { color: '#3b82f6' }, lineStyle: { color: '#3b82f6' }, smooth: true, showSymbol: sparse, symbolSize: 6, connectNulls: true },
          { name: 'pH', type: 'line', data: data.map(d => d.ph), itemStyle: { color: '#10b981' }, lineStyle: { color: '#10b981' }, smooth: true, showSymbol: sparse, symbolSize: 6, connectNulls: true }
        ]
      };
      chart.setOption(option);
      this.charts.waterTrend = chart;
      } catch (e) { console.warn('waterTrend chart init skipped', e); }
    },
    initFailureChart() {
      if (this._isDestroyed || !this.$refs.failureChart) return;
      if (!this.canInitChart(this.$refs.failureChart)) {
        this.scheduleChartRetry();
        return;
      }
      if (this.charts.failure) this.charts.failure.dispose();
      try {
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
      } catch (e) { console.warn('failure chart init skipped', e); }
    },
    initEnergyChart() {
      if (this._isDestroyed || !this.$refs.energyChart) return;
      if (!this.canInitChart(this.$refs.energyChart)) {
        this.scheduleChartRetry();
        return;
      }
      if (this.charts.energy) {
        this.charts.energy.dispose();
      }
      try {
      const chart = echarts.init(this.$refs.energyChart);
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} MWh ({d}%)',
          backgroundColor: '#1e293b',
          borderColor: '#334155',
          textStyle: { color: '#fff', fontSize: 11 },
          confine: true
        },
        series: [{
          type: 'pie',
          radius: ['48%', '88%'],
          center: ['50%', '52%'],
          avoidLabelOverlap: true,
          label: { show: false },
          labelLine: { show: false },
          itemStyle: {
            borderRadius: 3,
            borderColor: 'rgba(10, 22, 40, 0.9)',
            borderWidth: 2
          },
          emphasis: {
            scale: true,
            scaleSize: 4,
            label: { show: false }
          },
          data: this.energyData.map(d => ({ value: d.value, name: d.name, itemStyle: { color: d.color } }))
        }]
      };
      chart.setOption(option, true);
      this.charts.energy = chart;
      this.$nextTick(() => {
        try { chart.resize(); } catch (err) { /* ignore */ }
      });
      } catch (e) { console.warn('energy chart init skipped', e); }
    }
  }
};
</script>

<style lang="scss" scoped>
@import './index_daping3.styles.scss';

.alert-detail-link {
  cursor: pointer;
  user-select: none;
  padding: 2px 6px;
  border-radius: 4px;
  transition: background-color 0.15s ease, color 0.15s ease;
}
.alert-detail-link:hover {
  background: rgba(255, 77, 79, 0.18);
  text-decoration: underline;
}
.health-loading {
  padding: 12px 8px;
  color: #9ca3af;
  font-size: 13px;
  text-align: center;
}
.wq-card-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  min-height: 120px;
  color: #94a3b8;
  font-size: 13px;
}
.wq-card-loading.trend-loading {
  position: absolute;
  inset: 28px 0 0;
  z-index: 2;
  background: rgba(15, 23, 42, 0.55);
  border-radius: 8px;
}
.trend-section {
  position: relative;
}
.trend-chart.is-dimmed {
  opacity: 0.25;
  pointer-events: none;
}
.wq-spinner {
  display: inline-block;
  width: 22px;
  height: 22px;
  border: 2px solid rgba(56, 189, 248, 0.25);
  border-top-color: #38bdf8;
  border-radius: 50%;
  animation: wq-spin 0.75s linear infinite;
}
@keyframes wq-spin {
  to { transform: rotate(360deg); }
}
</style>

<style lang="scss">
/* 挂到 body 的事件节点悬浮窗（不受 scoped / overflow / transform 影响） */
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
