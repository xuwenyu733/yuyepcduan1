<template>
  <div class="dashboard-wrapper">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-bar">
        <div class="header-left">
          <button class="nav-btn btn-blue" @click="toPage('/index_daping1_son')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="7" height="7" x="3" y="3" rx="1"></rect><rect width="7" height="7" x="14" y="3" rx="1"></rect><rect width="7" height="7" x="14" y="14" rx="1"></rect><rect width="7" height="7" x="3" y="14" rx="1"></rect></svg>
            <span>数字孪生</span>
          </button>
          <button class="nav-btn btn-purple" @click="toPage('/index_daping2')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
            <span>视频监控</span>
          </button>
          <button class="nav-btn btn-cyan" @click="toPage('/index')">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
            <span>数字化监控</span>
          </button>
        </div>

        <div class="header-center">
          <div class="title-wrap">
            <div class="logo">
              <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6.5 12c.94-3.46 4.94-6 8.5-6 3.56 0 6.06 2.54 7 6-.94 3.47-3.44 6-7 6s-7.56-2.53-8.5-6Z"></path><path d="M18 12v.01"></path><path d="M11.52 17c-2.08 1.56-4.96 1.83-7.52.5-2.56-1.33-4-4.5-4-7.5 0-3 1.44-6.17 4-7.5 2.56-1.33 5.44-1.06 7.52.5"></path></svg>
            </div>
            <h1>循环水AI智慧渔业养殖系统</h1>
          </div>
        </div>

        <div class="header-right">
          <div class="quick-btns">
            <button class="q-btn btn-emerald" @click="toPage('/stock_distribution')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <span>存量分布</span>
            </button>
            <button class="q-btn btn-orange" @click="toPage('/keyshow/InspectionList')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
              <span>智能巡检</span>
            </button>
            <button class="q-btn btn-orange" @click="toPage('/mission_planning')">
              <span>任务与计划</span>
            </button>
            <button class="q-btn btn-cyan-light" @click="toPage('/keyshow/inspectionreport')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
              <span>分析报告</span>
            </button>
            <button class="q-btn btn-pink" @click="goFishDisease()">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              <span>鱼病诊断</span>
            </button>
            <button class="q-btn btn-indigo" @click="toPage('/knowledge/zk-meeting-expert')">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              <span>专家会议</span>
            </button>
            <!-- Excel：综合目录去掉试验系统入口 -->
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧区域 -->
      <div class="panel-left">
        <!-- 企业介绍 -->
        <div class="card intro-card">
          <div class="card-header">
            <div class="icon-box blue-box">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6.5 12c.94-3.46 4.94-6 8.5-6 3.56 0 6.06 2.54 7 6-.94 3.47-3.44 6-7 6s-7.56-2.53-8.5-6Z"></path><path d="M18 12v.01"></path><path d="M11.52 17c-2.08 1.56-4.96 1.83-7.52.5-2.56-1.33-4-4.5-4-7.5 0-3 1.44-6.17 4-7.5 2.56-1.33 5.44-1.06 7.52.5"></path></svg>
            </div>
            <h2>企业介绍</h2>
          </div>
          <div class="card-body intro-body">
            <div class="intro-brand">
              <p class="intro-name">北京鲸鲲智慧渔业养殖有限公司</p>
              <p class="intro-slogan">专注陆基循环水智慧养殖，科技赋能现代渔业</p>
            </div>
            <div class="intro-metrics">
              <div
                v-for="item in companyMetrics"
                :key="item.label"
                class="metric-row"
                :style="{ '--accent': item.color }"
              >
                <div class="metric-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path :d="item.iconPath" />
                  </svg>
                </div>
                <div class="metric-content">
                  <span class="metric-label">{{ item.label }}</span>
                  <div class="metric-value-line">
                    <span class="metric-value" :class="{ 'is-text': !item.unit }">{{ item.value }}</span>
                    <span v-if="item.unit" class="metric-unit">{{ item.unit }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="intro-desc">
              <p>依托先进 RAS 循环水系统，模拟原生冷水环境，全程标准化管控。生产生食级大西洋鲑鱼，立足北京辐射华北市场。</p>
            </div>
          </div>
        </div>

        <!-- AI报警监控 -->
        <div class="card alert-card flex-1">
          <div class="card-header space-between">
            <div class="flex-align">
              <div class="icon-box red-box animate-pulse">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"></path><path d="M12 9v4"></path><path d="M12 17h.01"></path></svg>
              </div>
              <h2>AI报警监控</h2>
            </div>
            <span class="alert-count">{{ filteredAlerts.length }}条异常</span>
          </div>
          <div class="filter-btns">
            <button @click="setAlertFilter('all')" :class="['filter-btn', alertFilter === 'all' ? 'active' : '']">全部</button>
            <button @click="setAlertFilter('fish')" :class="['filter-btn', alertFilter === 'fish' ? 'active' : '']">鱼</button>
            <button @click="setAlertFilter('water')" :class="['filter-btn', alertFilter === 'water' ? 'active' : '']">水</button>
            <button @click="setAlertFilter('equipment')" :class="['filter-btn', alertFilter === 'equipment' ? 'active' : '']">设备</button>
            <button @click="setAlertFilter('human')" :class="['filter-btn', alertFilter === 'human' ? 'active' : '']">人</button>
          </div>
          <div class="scroll-container">
            <div class="animate-scroll" :style="{ animationDuration: alertScrollDuration }">
              <div v-for="(alert, index) in filteredAlerts" :key="index" class="alert-item">
                <svg v-if="alert.category === 'fish'" class="icon text-red" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6.5 12c.94-3.46 4.94-6 8.5-6 3.56 0 6.06 2.54 7 6-.94 3.47-3.44 6-7 6s-7.56-2.53-8.5-6Z"></path><path d="M18 12v.01"></path><path d="M11.52 17c-2.08 1.56-4.96 1.83-7.52.5-2.56-1.33-4-4.5-4-7.5 0-3 1.44-6.17 4-7.5 2.56-1.33 5.44-1.06 7.52.5"></path></svg>
                <svg v-else-if="alert.category === 'water'" class="icon text-red" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"></path></svg>
                <svg v-else-if="alert.category === 'equipment'" class="icon text-red" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m12 14 4-4"></path><path d="M3.34 19a10 10 0 1 1 17.32 0"></path></svg>
                <svg v-else-if="alert.category === 'human'" class="icon text-red" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M22 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>

                <div class="alert-content">
                  <p class="title">{{ alert.title }}</p>
                  <p class="msg">{{ alert.message }}</p>
                  <p class="sug">{{ alert.suggestion }} | {{ alert.time }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间区域 -->
      <div class="panel-center">
        <IndexDaping1Son />

        <!-- 企业宣传视频 -->
        <div class="card video-card h-64">
          <video
            src="https://www.kdaqua.cn/kede_file/1.mp4"
            controls
            autoplay
            muted
            loop
            class="video-player"
          >
            您的浏览器不支持播放该视频。
          </video>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="panel-right">
        <!-- 养殖品种与产能计划 -->
        <div class="card plan-card">
          <div class="card-header">
            <div class="icon-box teal-box">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6.5 12c.94-3.46 4.94-6 8.5-6 3.56 0 6.06 2.54 7 6-.94 3.47-3.44 6-7 6s-7.56-2.53-8.5-6Z"></path><path d="M18 12v.01"></path><path d="M11.52 17c-2.08 1.56-4.96 1.83-7.52.5-2.56-1.33-4-4.5-4-7.5 0-3 1.44-6.17 4-7.5 2.56-1.33 5.44-1.06 7.52.5"></path></svg>
            </div>
            <h2>养殖品种与产能计划</h2>
          </div>

          <div class="plan-section" @click="toPage(`/jidi/zk-breed-species`)">
            <h3>养殖品种</h3>
            <div class="species-list">
              <div v-for="(item, index) in speciesList" :key="item.id"
                :class="['species-item', getSpeciesStyle(index).itemClass]">
                <div class="s-head">
                  <span class="s-name">{{ item.speciesName }}</span>
                  <span :class="['s-tag', getSpeciesStyle(index).tagClass]">{{ item.qualityGrade }}</span>
                </div>
                <p class="s-desc">{{ item.scientificName }} | 适宜水温 {{ item.suitableTemp }} | 养殖周期 {{ item.breedingCycle }}</p>
              </div>
            </div>
          </div>

          <div class="plan-section" @click="toPage('/jidi/zk-annual-capacity-plan')">
            <h3>{{ capacityList.length > 0 ? capacityList[0].planYear : '2026' }}年度产能计划</h3>
            <div class="progress-list">
              <div v-for="(item, index) in capacityList" :key="item.id"
                :class="['progress-item', getCapacityStyle(index).itemClass]">
                <div class="p-head">
                  <span class="p-name">{{ item.speciesName }}</span>
                  <div class="p-val-box">
                    <p class="p-val">{{ item.currentCapacity }}吨</p>
                    <p class="p-target">目标: {{ item.targetCapacity }}吨</p>
                  </div>
                </div>
                <div class="p-bar-bg">
                  <div :class="['p-bar', getCapacityStyle(index).barClass]"
                    :style="{ width: Math.min(calculateProgress(item.currentCapacity, item.targetCapacity), 100) + '%' }">
                  </div>
                </div>
                <p class="p-sub">
                  进度: {{ calculateProgress(item.currentCapacity, item.targetCapacity) }}% •
                  <span v-if="Number(item.targetCapacity) > Number(item.currentCapacity)">
                    剩余: {{ (Number(item.targetCapacity) - Number(item.currentCapacity)).toFixed(0) }}吨
                  </span>
                  <span v-else>
                    超额: {{ (Number(item.currentCapacity) - Number(item.targetCapacity)).toFixed(0) }}吨
                  </span>
                </p>
              </div>

              <!-- 总计产能 -->
              <div class="progress-item total-progress" v-if="capacityList.length > 0">
                <div class="p-head">
                  <span class="p-name">总计产能</span>
                  <div class="p-val-box">
                    <p class="p-val large">{{ totalCurrentCapacity }}吨</p>
                    <p class="p-target">目标: {{ totalTargetCapacity }}吨</p>
                  </div>
                </div>
                <div class="p-bar-bg">
                  <div class="p-bar mix-bg" :style="{ width: Math.min(totalProgress, 100) + '%' }"></div>
                </div>
                <p class="p-sub">
                  总进度: {{ totalProgress }}% •
                  <span v-if="totalTargetCapacity > totalCurrentCapacity">
                    剩余: {{ (totalTargetCapacity - totalCurrentCapacity).toFixed(0) }}吨
                  </span>
                  <span v-else>
                    超额: {{ (totalCurrentCapacity - totalTargetCapacity).toFixed(0) }}吨
                  </span>
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- 成本统计 -->
        <div class="card cost-card">
          <div class="card-header">
            <div class="icon-box orange-box">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 7V4a1 1 0 0 0-1-1H5a2 2 0 0 0 0 4h15a1 1 0 0 1 1 1v4h-3a2 2 0 0 0 0 4h3a8 8 0 0 1-9 11.29"></path><path d="M22 12h-4v-4h4v4Z"></path></svg>
            </div>
            <h2>成本统计</h2>
          </div>

          <div class="cost-summary">
            <div class="c-head">
              <div class="c-label">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 20px; height: 20px; color: #fb923c;"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
                <span>本月总成本</span>
              </div>
              <div class="c-tag" v-if="false">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 16px; height: 16px; color: #60a5fa;"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline><polyline points="17 6 23 6 23 12"></polyline></svg>
                <span>-3.2%</span>
              </div>
            </div>
            <p class="c-val">¥{{ displayTotalCost }}<span>万</span></p>
          </div>

          <div class="chart-container">
            <div ref="costChart" class="cost-chart"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import IndexDaping1Son from '@/views/index_daping1_son.vue';
import * as ZkBreedSpeciesApi from '@/api/fish/zkbreedspecies';
import * as ZkAnnualCapacityPlanApi from '@/api/fish/zkannualcapacityplan';
import * as ZkDeviceAlarmApi from '@/api/fish/zkDeviceAlarm';
import { getZkBiologyCapitalPage } from "@/api/fish/zkBiologyCapital";

export default {
  name: "IndexDaping1",
  components: {
    IndexDaping1Son,
  },
  data() {
    return {
      alertFilter: 'all',
      companyMetrics: [
        {
          label: '占地面积',
          value: '50,000',
          unit: 'm²',
          color: '#06b6d4',
          iconPath: 'M3 21h18M5 21V7l7-4 7 4v14M9 21v-6h6v6',
        },
        {
          label: '养殖车间',
          value: '3',
          unit: '个',
          color: '#3b82f6',
          iconPath: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z',
        },
        {
          label: '年产能',
          value: '5,000+',
          unit: '吨',
          color: '#10b981',
          iconPath: 'M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6',
        },
        {
          label: '养殖品种',
          value: '大西洋鲑鱼',
          unit: '',
          color: '#a855f7',
          iconPath: 'M6.5 12c.94-3.46 4.94-6 8.5-6 3.56 0 6.06 2.54 7 6-.94 3.47-3.44 6-7 6s-7.56-2.53-8.5-6Z',
        },
      ],
      speciesList: [], // 养殖品种列表
      capacityList: [], // 年度产能计划列表
      abnormalAlerts: [], // 异常报警数据
      totalCost: 0, // 总成本

      // 成本数据
      costData: [
        { name: '饲料成本', value: 0, color: '#f97316' },
        { name: '鱼苗鱼卵', value: 0, color: '#3b82f6' },
        { name: '动保成本', value: 0, color: '#10b981' },
        { name: '设备成本', value: 0, color: '#a855f7' },
        { name: '能源成本', value: 0, color: '#06b6d4' },
        { name: '额外成本', value: 0, color: '#eab308' },
      ]
    };
  },
  computed: {
    filteredAlerts() {
      if (this.alertFilter === 'all') {
        return this.abnormalAlerts;
      }
      return this.abnormalAlerts.filter(alert => alert.category === this.alertFilter);
    },
    /** 报警列表滚动时长：每条约 6 秒，避免 100 条在 20 秒内刷完 */
    alertScrollDuration() {
      const n = (this.filteredAlerts && this.filteredAlerts.length) || 0;
      const seconds = Math.max(48, n * 6);
      return seconds + 's';
    },
    /** 计算总计当前产能 */
    totalCurrentCapacity() {
      return this.capacityList.reduce((sum, item) => sum + (Number(item.currentCapacity) || 0), 0);
    },
    /** 计算总计目标产能 */
    totalTargetCapacity() {
      return this.capacityList.reduce((sum, item) => sum + (Number(item.targetCapacity) || 0), 0);
    },
    /** 计算总计进度 */
    totalProgress() {
      if (this.totalTargetCapacity === 0) return 0;
      return (this.totalCurrentCapacity / this.totalTargetCapacity * 100).toFixed(1);
    },
    /** 元 → 万元展示 */
    displayTotalCost() {
      const yuan = Number(this.totalCost) || 0;
      return (yuan / 10000).toFixed(2);
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
    });
    this.getSpeciesList();
    this.getCapacityList();
    this.getAlarmList();
    this.getOverallCostData();
  },
  beforeDestroy() {
    if (this.myChart) {
      window.removeEventListener('resize', this.resizeChart);
      this.myChart.dispose();
    }
  },
  methods: {
    toPage(path) {
      this.$router.push(path).catch(() => {});
    },
    goFishDisease() {
      this.$router.push({ path: '/fish-disease', query: { from: '/index_daping1' } }).catch(() => {});
    },
    setAlertFilter(filter) {
      this.alertFilter = filter;
    },
    /** 获取养殖品种列表 */
    async getSpeciesList() {
      try {
        const res = await ZkBreedSpeciesApi.getZkBreedSpeciesPage({
          pageNo: 1,
          pageSize: 10
        });
        this.speciesList = res.data.list;
      } catch (error) {
        console.error('获取养殖品种列表失败:', error);
      }
    },
    /** 获取养殖品种样式 */
    getSpeciesStyle(index) {
      const schemes = [
        { itemClass: 'blue-species', tagClass: 'emerald-tag' },
        { itemClass: 'purple-species', tagClass: 'blue-tag' },
        { itemClass: 'amber-species', tagClass: 'yellow-tag' }
      ];
      return schemes[index % schemes.length];
    },
    /** 获取年度产能计划列表 */
    async getCapacityList() {
      try {
        const res = await ZkAnnualCapacityPlanApi.getZkAnnualCapacityPlanPage({
          pageNo: 1,
          pageSize: 10
        });
        this.capacityList = res.data.list;
      } catch (error) {
        console.error('获取年度产能计划失败:', error);
      }
    },
    /** 获取产能计划样式 */
    getCapacityStyle(index) {
      const schemes = [
        { itemClass: 'blue-progress', barClass: 'emerald-bg' },
        { itemClass: 'purple-progress', barClass: 'purple-bg' },
        { itemClass: 'amber-progress', barClass: 'amber-bg' }
      ];
      return schemes[index % schemes.length];
    },
    /** 计算进度百分比 */
    calculateProgress(current, target) {
      if (!target || target === 0) return 0;
      return (current / target * 100).toFixed(1);
    },
    /** 获取报警列表 */
    async getAlarmList() {
      try {
        const res = await ZkDeviceAlarmApi.getZkDeviceAlarmDashboardList();
        this.abnormalAlerts = res.data;
      } catch (error) {
        console.error('获取报警列表失败:', error);
      }
    },
    /** 获取成本统计数据：取有金额的最新记录，避免取到全 0 的旧行导致饼图空白 */
    async getOverallCostData() {
      const params = {
        pageNo: 1,
        pageSize: 50
      };

      try {
        const response = await getZkBiologyCapitalPage(params);
        const list = (response && response.code === 0 && response.data && response.data.list) || [];
        if (!list.length) {
          this.applyCostFallback();
          return;
        }
        const rowCost = (row) => {
          const feed = Number(row.feedCost || row.feed_cost || 0);
          const bio = Number(row.biologyCost || row.biology_cost || 0);
          const med = Number(row.medicineCost || row.medicine_cost || 0);
          const device = Number(row.deviceCost || row.device_cost || 0);
          const energy = Number(row.energyCost || row.energy_cost || 0);
          const extra = Number(row.extraCost || row.extra_cost || 0);
          return { feed, bio, med, device, energy, extra, sum: feed + bio + med + device + energy + extra };
        };
        // 列表按 id 倒序：优先最新且成本>0 的记录
        let picked = null;
        for (let i = 0; i < list.length; i++) {
          const c = rowCost(list[i]);
          if (c.sum > 0) {
            picked = c;
            break;
          }
        }
        if (!picked) {
          this.applyCostFallback();
          return;
        }
        this.costData[0].value = picked.feed;
        this.costData[1].value = picked.bio;
        this.costData[2].value = picked.med;
        this.costData[3].value = picked.device;
        this.costData[4].value = picked.energy;
        this.costData[5].value = picked.extra;
        this.totalCost = picked.sum;
        this.$nextTick(() => this.initChart());
      } catch (error) {
        console.error('获取成本数据失败:', error);
        this.applyCostFallback();
      }
    },
    /** 接口无有效成本时用演示数据，保证饼图可见 */
    applyCostFallback() {
      this.costData = [
        { name: '饲料成本', value: 128000, color: '#f97316' },
        { name: '鱼苗鱼卵', value: 46000, color: '#3b82f6' },
        { name: '动保成本', value: 22000, color: '#10b981' },
        { name: '设备成本', value: 35000, color: '#a855f7' },
        { name: '能源成本', value: 41000, color: '#06b6d4' },
        { name: '额外成本', value: 18000, color: '#eab308' }
      ];
      this.totalCost = this.costData.reduce((s, i) => s + i.value, 0);
      this.$nextTick(() => this.initChart());
    },
    resizeChart() {
      if (this.myChart) {
        this.myChart.resize();
      }
    },
    initChart() {
      const chartDom = this.$refs.costChart;
      if (!chartDom) return;

      this.$nextTick(() => {
        const chartDom = this.$refs.costChart;
        if (!chartDom) return;

        // 容器被挤成 0 高时延后重试，避免「标题在、图没有」
        if (chartDom.clientWidth < 8 || chartDom.clientHeight < 8) {
          setTimeout(() => this.initChart(), 200);
          return;
        }

        if (!this.myChart) {
          this.myChart = echarts.init(chartDom);
        }

        const seriesData = this.costData
          .filter((item) => Number(item.value) > 0)
          .map((item) => ({
            name: item.name,
            value: Number(item.value),
            itemStyle: { color: item.color }
          }));

        const option = {
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(15, 23, 42, 0.95)',
          borderColor: 'rgba(59, 130, 246, 0.3)',
          textStyle: { color: '#fff', fontSize: 14 },
          formatter: (params) => {
            const wan = (Number(params.value) / 10000).toFixed(2);
            return `${params.name}<br/>¥${wan}万 (${params.percent}%)`;
          }
        },
        legend: {
          show: false
        },
        series: [
          {
            name: '成本',
            type: 'pie',
            radius: ['38%', '72%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 2,
              borderColor: '#0f172a',
              borderWidth: 1
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: false
              }
            },
            labelLine: {
              show: false
            },
            data: seriesData.length
              ? seriesData
              : [{ name: '暂无数据', value: 1, itemStyle: { color: '#334155' } }]
          }
        ]
      };

        this.myChart.setOption(option, true);
        window.removeEventListener('resize', this.resizeChart);
        window.addEventListener('resize', this.resizeChart);
        this.$nextTick(() => {
          if (this.myChart) {
            this.myChart.resize();
          }
        });
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-wrapper {
  width: 100%;
  height: 100vh;
  background: linear-gradient(to bottom right, #020617, #172554, #0f172a);
  overflow: hidden; /* 恢复为 hidden 锁定一屏 */
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  color: white;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;

  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  svg {
    flex-shrink: 0;
  }

  .header {
    background: linear-gradient(to right, rgba(15, 23, 42, 0.8), rgba(30, 58, 138, 0.6), rgba(15, 23, 42, 0.8));
    backdrop-filter: blur(12px);
    border-bottom: 1px solid rgba(59, 130, 246, 0.3);
    padding: 0.65rem 1.25rem;
    flex-shrink: 0;

    .header-bar {
      display: grid;
      grid-template-columns: 1fr auto 1fr;
      align-items: center;
      gap: 1rem;
      min-width: 0;
    }

    .header-left {
      justify-self: start;
      display: flex;
      align-items: center;
      gap: 0.6rem;
      min-width: 0;

      .nav-btn {
        display: flex;
        align-items: center;
        gap: 0.45rem;
        padding: 0 0.9rem;
        height: 2.5rem;
        border-radius: 0.5rem;
        color: white;
        font-weight: 600;
        font-size: 0.94rem;
        cursor: pointer;
        border: none;
        transition: all 0.3s;
        white-space: nowrap;
        flex-shrink: 0;

        .icon { width: 0.9rem; height: 0.9rem; }

        &.btn-blue {
          background: linear-gradient(to right, #2563eb, #3b82f6);
          box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
          &:hover { filter: brightness(1.08); transform: translateY(-1px); }
        }
        &.btn-purple {
          background: linear-gradient(to right, #9333ea, #a855f7);
          box-shadow: 0 4px 12px rgba(168, 85, 247, 0.35);
          &:hover { filter: brightness(1.08); transform: translateY(-1px); }
        }
        &.btn-cyan {
          background: linear-gradient(to right, #0891b2, #06b6d4);
          box-shadow: 0 4px 12px rgba(6, 182, 212, 0.35);
          &:hover { filter: brightness(1.08); transform: translateY(-1px); }
        }
      }
    }

    .header-center {
      justify-self: center;
      display: flex;
      align-items: center;
      min-width: 0;

      .title-wrap {
        display: flex;
        align-items: center;
        gap: 0.75rem;

        .logo {
          position: static;
          width: 2.75rem;
          height: 2.75rem;
          background: linear-gradient(to bottom right, #3b82f6, #22d3ee);
          border-radius: 0.5rem;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 4px 15px rgba(59, 130, 246, 0.45);
          flex-shrink: 0;

          .icon-lg { width: 1.6rem; height: 1.6rem; color: white; }
        }

        h1 {
          font-size: 1.75rem;
          font-weight: 800;
          letter-spacing: 0.04em;
          line-height: 1.2;
          white-space: nowrap;
          margin: 0;
        }
      }
    }

    .header-right {
      justify-self: end;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      min-width: 0;

      .quick-btns {
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-end;
        gap: 0.6rem;

        .q-btn {
          height: 2.5rem;
          padding: 0 0.9rem;
          border-radius: 0.5rem;
          display: flex;
          align-items: center;
          gap: 0.45rem;
          color: white;
          font-size: 0.94rem;
          font-weight: 600;
          border: none;
          cursor: pointer;
          transition: all 0.3s;
          white-space: nowrap;
          flex-shrink: 0;

          .icon { width: 0.9rem; height: 0.9rem; }

          &.btn-emerald { background: linear-gradient(to right, #059669, #10b981); box-shadow: 0 4px 10px rgba(16, 185, 129, 0.25); }
          &.btn-orange { background: linear-gradient(to right, #ea580c, #f97316); box-shadow: 0 4px 10px rgba(249, 115, 22, 0.25); }
          &.btn-cyan-light { background: linear-gradient(to right, #0891b2, #06b6d4); box-shadow: 0 4px 10px rgba(6, 182, 212, 0.25); }
          &.btn-pink { background: linear-gradient(to right, #db2777, #ec4899); box-shadow: 0 4px 10px rgba(236, 72, 153, 0.25); }
          &.btn-indigo { background: linear-gradient(to right, #4f46e5, #6366f1); box-shadow: 0 4px 10px rgba(99, 102, 241, 0.25); }
          &.btn-teal { background: linear-gradient(to right, #0d9488, #14b8a6); box-shadow: 0 4px 10px rgba(20, 184, 166, 0.25); }

          &:hover {
            filter: brightness(1.08);
            transform: translateY(-1px);
          }
        }
      }
    }
  }

  .main-content {
    flex: 1;
    min-height: 0;
    display: flex;
    align-items: stretch;
    gap: 1.25rem;
    padding: 1rem 1.25rem 1.25rem;
    overflow: hidden;

    .card {
      background: linear-gradient(to bottom right, rgba(30, 41, 59, 0.6), rgba(15, 23, 42, 0.6));
      backdrop-filter: blur(4px);
      border-radius: 0.75rem;
      border: 1px solid rgba(59, 130, 246, 0.2);
      padding: 1rem 1.1rem;
      box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
      display: flex;
      flex-direction: column;
      min-height: 0;

      .card-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-bottom: 0.75rem;

        &.space-between { justify-content: space-between; }
        .flex-align { display: flex; align-items: center; gap: 0.5rem; }

        .icon-box {
          width: 2rem; height: 2rem;
          border-radius: 0.5rem;
          display: flex;
          align-items: center;
          justify-content: center;
          svg { width: 1.25rem; height: 1.25rem; color: white; }

          &.blue-box { background: linear-gradient(to bottom right, #3b82f6, #22d3ee); }
          &.red-box { background: linear-gradient(to bottom right, #ef4444, #f97316); }
          &.emerald-box { background: linear-gradient(to bottom right, #10b981, #2dd4bf); }
          &.teal-box { background: linear-gradient(to bottom right, #14b8a6, #34d399); }
          &.orange-box { background: linear-gradient(to bottom right, #f97316, #f87171); }
          &.animate-pulse { animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite; }
        }

        h2 { font-size: 1.5rem; font-weight: 700; color: white; }
      }
    }

    .flex-1 { flex: 1 1 0; min-height: 0; }
    .h-80 { flex: 0 0 31rem; min-height: 31rem; max-height: 31rem; }
    .h-64 { height: 20rem; }
    .h-72 { height: 22rem; }

    .panel-left {
      width: 28rem;
      display: flex;
      flex-direction: column;
      gap: 1rem;
      min-height: 0;

      .intro-card {
        flex: 0 0 auto;
        overflow: hidden;

        .card-header {
          flex-shrink: 0;
          margin-bottom: 0.5rem;
        }

        .intro-body {
          display: flex;
          flex-direction: column;
          gap: 12px;
        }

        .intro-brand {
          flex-shrink: 0;
          padding-bottom: 10px;
          border-bottom: 1px solid rgba(59, 130, 246, 0.25);

          .intro-name {
            font-size: 1.5rem;
            font-weight: 700;
            line-height: 1.35;
            color: #fff;
          }

          .intro-slogan {
            margin-top: 8px;
            font-size: 1.18rem;
            line-height: 1.45;
            color: #93c5fd;
          }
        }

        .intro-metrics {
          flex: 0 0 auto;
          display: flex;
          flex-direction: column;
          gap: 6px;

          .metric-row {
            flex: 0 0 auto;
            min-height: 38px;
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 5px 10px;
            border-radius: 8px;
            border: 1px solid rgba(59, 130, 246, 0.22);
            border-left: 4px solid var(--accent);
            background: linear-gradient(90deg, rgba(15, 28, 48, 0.85), rgba(30, 58, 138, 0.25));
          }

          .metric-icon {
            width: 34px;
            height: 34px;
            flex-shrink: 0;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: rgba(15, 23, 42, 0.55);
            border: 1px solid rgba(255, 255, 255, 0.08);
            color: var(--accent);
            box-shadow: inset 0 0 12px rgba(255, 255, 255, 0.04);

            svg {
              width: 17px;
              height: 17px;
            }
          }

          .metric-content {
            flex: 1;
            min-width: 0;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 10px;
          }

          .metric-label {
            font-size: 1.02rem;
            font-weight: 600;
            color: #bfdbfe;
            white-space: nowrap;
          }

          .metric-value-line {
            display: flex;
            align-items: baseline;
            justify-content: flex-end;
            gap: 4px;
            min-width: 0;
            text-align: right;
          }

          .metric-value {
            font-size: 1.3rem;
            font-weight: 800;
            line-height: 1.15;
            color: var(--accent);
            white-space: nowrap;

            &.is-text {
              font-size: 1.05rem;
            }
          }

          .metric-unit {
            font-size: 1rem;
            font-weight: 700;
            color: #6ee7b7;
          }
        }

        .intro-desc {
          flex-shrink: 0;
          padding: 12px 14px;
          border-radius: 8px;
          border: 1px solid rgba(59, 130, 246, 0.22);
          background: rgba(30, 58, 138, 0.22);

          p {
            font-size: 1.12rem;
            line-height: 1.75;
            color: #f1f5f9;
          }
        }
      }

      .alert-card {
        flex: 1 1 0;
        min-height: 0;
        background: linear-gradient(to bottom right, rgba(69, 10, 10, 0.4), rgba(67, 20, 7, 0.4));
        border: 1px solid rgba(239, 68, 68, 0.3);

        .card-header,
        .filter-btns {
          flex-shrink: 0;
        }

        .alert-count { color: #fca5a5; font-size: 1rem; font-weight: 600; }

        .filter-btns {
          display: flex;
          gap: 0.375rem;
          margin-bottom: 0.75rem;
          button {
            padding: 0.25rem 0.625rem;
            border-radius: 0.25rem;
            font-size: 1rem;
            font-weight: 600;
            border: none;
            cursor: pointer;
            transition: all 0.3s;
            &.active { background: #ef4444; color: white; }
            &:not(.active) { background: rgba(30, 41, 59, 0.5); color: #cbd5e1; &:hover { background: rgba(51, 65, 85, 0.5); } }
          }
        }

        .scroll-container {
          flex: 1;
          min-height: 0;
          overflow: hidden;
          position: relative;

          .animate-scroll {
            animation: scroll-up linear infinite;
            animation-duration: 120s;
            display: flex;
            flex-direction: column;
            gap: 0.5rem;

            .alert-item {
              background: rgba(127, 29, 29, 0.5);
              border: 2px solid #ef4444;
              border-radius: 0.5rem;
              padding: 0.75rem;
              display: flex;
              align-items: flex-start;
              gap: 0.75rem;

              .icon { width: 1.25rem; height: 1.25rem; margin-top: 0.125rem; }
              .text-red { color: #f87171; }

              .alert-content {
                flex: 1;
                .title { color: #fca5a5; font-weight: 700; font-size: 1.125rem; margin-bottom: 0.25rem; }
                .msg { color: white; font-size: 1rem; margin-bottom: 0.125rem; }
                .sug { color: #fecaca; font-size: 1rem; }
              }
            }
          }
        }
      }
    }

    .panel-center {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
      min-width: 0; /* 防止子元素撑破 flex 容器 */

      .video-card {
        padding: 0;
        overflow: hidden;
        .video-player {
          width: 100%;
          height: 100%;
          object-fit: cover;
          display: block;
        }
      }
    }

    .panel-right {
      width: 32rem;
      height: 100%;
      display: grid;
      grid-template-rows: minmax(0, 1fr) 17.5rem;
      gap: 1.5rem;
      flex-shrink: 0;
      min-height: 0;
      overflow: hidden;
      align-self: stretch;

      .plan-card {
        min-height: 0;
        overflow: auto;
        height: 100%;

        .plan-section {
          margin-bottom: 1.5rem;

          &:last-child {
            margin-bottom: 0;
          }
          h3 { font-size: 1rem; font-weight: 600; color: #93c5fd; margin-bottom: 0.5rem; }

          .species-list {
            display: flex; flex-direction: column; gap: 0.5rem;

            .species-item {
              border-radius: 0.5rem; padding: 0.75rem;
              border: 1px solid;

              .s-head {
                display: flex; align-items: center; justify-content: space-between; margin-bottom: 0.25rem;
                .s-name { color: white; font-weight: 700; }
                .s-tag {
                  padding: 0.125rem 0.5rem; border-radius: 9999px; border: 1px solid;
                  font-size: 1rem; font-weight: 600;
                }
              }
              .s-desc { font-size: 1rem; }

              &.blue-species {
                background: linear-gradient(to bottom right, rgba(30, 58, 138, 0.4), rgba(22, 78, 99, 0.4));
                border-color: rgba(59, 130, 246, 0.3);
                .emerald-tag { background: rgba(16, 185, 129, 0.2); border-color: #34d399; color: #6ee7b7; }
                .s-desc { color: #bfdbfe; }
              }
              &.purple-species {
                background: linear-gradient(to bottom right, rgba(88, 28, 135, 0.4), rgba(131, 24, 67, 0.4));
                border-color: rgba(168, 85, 247, 0.3);
                .blue-tag { background: rgba(59, 130, 246, 0.2); border-color: #60a5fa; color: #93c5fd; }
                .s-desc { color: #e9d5ff; }
              }
              &.amber-species {
                background: linear-gradient(to bottom right, rgba(120, 53, 15, 0.4), rgba(124, 45, 18, 0.4));
                border-color: rgba(245, 158, 11, 0.3);
                .yellow-tag { background: rgba(234, 179, 8, 0.2); border-color: #facc15; color: #fde047; }
                .s-desc { color: #fde68a; }
              }
            }
          }

          .progress-list {
            display: flex; flex-direction: column; gap: 0.5rem;

            .progress-item {
              border-radius: 0.5rem; padding: 0.75rem;
              border: 1px solid;

              .p-head {
                display: flex; align-items: center; justify-content: space-between; margin-bottom: 0.375rem;
                .p-name { color: white; font-weight: 600; font-size: 1.125rem; }
                .p-val-box {
                  text-align: right;
                  .p-val { color: #34d399; font-weight: 700; font-size: 1.125rem; &.large { font-size: 1.5rem; } }
                  .p-target { font-size: 1rem; }
                }
              }

              .p-bar-bg {
                height: 0.375rem; background: #334155; border-radius: 9999px; overflow: hidden;
                .p-bar { height: 100%; border-radius: 9999px; }
              }

              .p-sub { font-size: 1rem; margin-top: 0.25rem; }

              &.blue-progress {
                background: linear-gradient(to bottom right, rgba(30, 58, 138, 0.4), rgba(22, 78, 99, 0.4));
                border-color: rgba(59, 130, 246, 0.3);
                .p-target { color: #93c5fd; }
                .emerald-bg { background: linear-gradient(to right, #10b981, #4ade80); }
                .p-sub { color: #bfdbfe; }
              }
              &.purple-progress {
                background: linear-gradient(to bottom right, rgba(88, 28, 135, 0.4), rgba(131, 24, 67, 0.4));
                border-color: rgba(168, 85, 247, 0.3);
                .p-target { color: #d8b4fe; }
                .purple-bg { background: linear-gradient(to right, #a855f7, #f472b6); }
                .p-sub { color: #e9d5ff; }
              }
              &.amber-progress {
                background: linear-gradient(to bottom right, rgba(120, 53, 15, 0.4), rgba(124, 45, 18, 0.4));
                border-color: rgba(245, 158, 11, 0.3);
                .p-target { color: #fcd34d; }
                .amber-bg { background: linear-gradient(to right, #f59e0b, #fb923c); }
                .p-sub { color: #fde68a; }
              }
              &.total-progress {
                background: linear-gradient(to bottom right, rgba(15, 23, 42, 0.6), rgba(30, 58, 138, 0.6));
                border: 2px solid rgba(59, 130, 246, 0.5);
                margin-top: 0.75rem;
                .p-bar-bg { height: 0.5rem; }
                .mix-bg { background: linear-gradient(to right, #10b981, #2dd4bf, #4ade80); }
                .p-target { color: #93c5fd; }
                .p-sub { color: #bfdbfe; margin-top: 0.375rem; }
              }
            }
          }
        }
      }

      .cost-card {
        height: 100%;
        min-height: 0;
        overflow: hidden;
        display: flex;
        flex-direction: column;

        .card-header,
        .cost-summary {
          flex-shrink: 0;
        }

        .cost-summary {
          background: linear-gradient(to right, rgba(124, 45, 18, 0.4), rgba(127, 29, 29, 0.4), rgba(159, 18, 57, 0.4));
          border-radius: 0.75rem;
          padding: 0.65rem 1rem;
          border: 1px solid rgba(249, 115, 22, 0.4);
          margin-bottom: 0.5rem;

          .c-head {
            display: flex;
            align-items: center;
            justify-content: space-between;

            .c-label {
              display: flex;
              align-items: center;
              gap: 0.5rem;

              svg { width: 1rem; height: 1rem; color: #fb923c; }
              span { color: #fed7aa; font-size: 0.95rem; font-weight: 600; }
            }

            .c-tag {
              display: flex;
              align-items: center;
              gap: 0.25rem;
              padding: 0.125rem 0.5rem;
              background: rgba(59, 130, 246, 0.2);
              border-radius: 9999px;

              svg { width: 0.75rem; height: 0.75rem; color: #60a5fa; }
              span { color: #60a5fa; font-size: 1rem; font-weight: 700; }
            }
          }

          .c-val {
            color: white;
            font-size: 1.6rem;
            font-weight: 700;
            margin-top: 0.15rem;

            span { font-size: 1rem; margin-left: 0.25rem; }
          }
        }

        .chart-container {
          flex: 1 1 0;
          min-height: 9rem;
          position: relative;
          overflow: hidden;
        }

        .cost-chart {
          position: absolute;
          inset: 0;
          width: 100%;
          height: 100%;
        }
      }
    }
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: .5; }
  }

  @keyframes scroll-up {
    0% { transform: translateY(0); }
    100% { transform: translateY(-50%); }
  }

  /* 隐藏滚动条 */
  ::-webkit-scrollbar {
    width: 0px;
    background: transparent;
  }
}
</style>
