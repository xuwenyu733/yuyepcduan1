<template>
  <div class="system-container">
    <PageLoadingMask
      :visible="pageLoading && !(pondList && pondList.length)"
      text="正在加载健康/死亡数据…"
    />
    <!-- 顶部标题栏 -->
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <div class="icon-wrapper">
            <i class="el-icon-water-cup"></i>
          </div>
          <div>
            <h1 class="title">{{ pageTitle }}</h1>
            <p class="subtitle">{{ pageSubtitle }}</p>
          </div>
        </div>
        <div class="header-right">
          <div class="last-update">最后更新: {{ lastUpdateTime }}</div>
          <el-button size="small" icon="el-icon-refresh" @click="handleRefresh">刷新数据</el-button>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- Excel：车间/系统/池号/品种/批次/规格 筛选；健康与死亡模块分离（路由 activeTab） -->
      <el-form :inline="true" size="small" class="dash-filter-bar" @submit.native.prevent>
        <el-form-item label="车间">
          <el-select v-model="filterWorkshop" placeholder="车间" clearable filterable style="width: 120px;" @change="onFilterWorkshopChange" @clear="onFilterWorkshopChange('')">
            <el-option v-for="(item, idx) in workshopOptions" :key="'w-'+idx+'-'+item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="系统">
          <el-select v-model="filterSystem" placeholder="系统" clearable filterable style="width: 120px;" @change="onFilterSystemChange" @clear="onFilterSystemChange('')">
            <el-option v-for="(item, idx) in systemOptions" :key="'s-'+idx+'-'+item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="池号">
          <el-select v-model="filterPond" placeholder="池号" clearable filterable style="width: 120px;" @change="onFilterPondChange" @clear="onFilterPondClear">
            <el-option v-for="(item, idx) in pondOptions" :key="'p-'+idx+'-'+item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种">
          <el-select v-model="filterSpecies" placeholder="品种" clearable filterable style="width: 120px;" @change="applyFilter">
            <el-option v-for="(item, idx) in speciesOptions" :key="'sp-'+idx+'-'+item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="批次">
          <el-select v-model="filterBatch" placeholder="批次" clearable filterable style="width: 150px;" @change="applyFilter">
            <el-option v-for="(item, idx) in batchOptions" :key="'b-'+idx+'-'+item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-select v-model="filterSpec" placeholder="规格" clearable filterable style="width: 110px;" @change="applyFilter">
            <el-option v-for="(item, idx) in specOptions" :key="'spec-'+idx+'-'+item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-refresh" @click="resetFilter">重置</el-button>
          <el-button type="text" size="mini" icon="el-icon-setting" @click="goThresholdPage">阈值管理</el-button>
        </el-form-item>
      </el-form>

      <el-tabs v-model="activeTab" type="card" class="custom-tabs" :class="{ 'health-only-tabs': isHealthOnlyPage }">
        <el-tab-pane label="健康状况评估" name="health">
          <!-- Excel：评分标准与健康模块同页展示，便于对照 -->
          <el-card class="box-card score-legend-card mb-4" shadow="never">
            <div slot="header" class="clearfix">
              <span>评分标准（外部环境 {{ healthWeights.env }} + 外观行为 {{ healthWeights.appearance }} + 内部肌体 {{ healthWeights.internal }}）</span>
              <div style="float:right;display:flex;gap:8px;align-items:center">
                <el-button
                  v-if="attentionHealthPools.length"
                  type="danger"
                  size="mini"
                  plain
                  icon="el-icon-bell"
                  :loading="pushingHealthAlarms"
                  @click="pushAttentionHealthAlarms"
                >推送关注池报警并生成任务({{ attentionHealthPools.length }})</el-button>
                <el-button type="text" style="padding:0" @click="$router.push('/alert_monitor?source=health&category=fish')">AI 报警 →</el-button>
                <el-button type="text" style="padding:0" @click="$router.push('/keyshow/zkDeathSurvivalRate?activeTab=mortality')">死亡量存活率 →</el-button>
              </div>
            </div>
            <el-row :gutter="12">
              <el-col :span="8">
                <div class="legend-block">
                  <div class="legend-title text-green">优秀 ≥ {{ healthGrade.excellentMin }}</div>
                  <p class="legend-desc">三项指标均稳定，无需干预</p>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="legend-block">
                  <div class="legend-title text-yellow">良好 {{ healthGrade.watchMin }}–{{ healthGrade.excellentMin - 1 }}</div>
                  <p class="legend-desc">个别参数偏弱，加强巡检与投喂观察</p>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="legend-block">
                  <div class="legend-title text-red">关注 &lt; {{ healthGrade.watchMin }}</div>
                  <p class="legend-desc">需联动水质/死亡预警并安排处置</p>
                </div>
              </el-col>
            </el-row>
          </el-card>
          <!-- 健康状态分布 + AI 健康分析（与设计图一致，等高布局） -->
          <el-row :gutter="20" class="mb-4 health-overview-row" :class="{ 'health-page-light': isHealthOnlyPage }">
            <el-col :span="12" class="health-overview-col">
              <el-card class="box-card health-overview-card">
                <div slot="header" class="clearfix health-overview-header">
                  <span>健康状态分布</span>
                </div>
                <div class="health-dist-panel">
                  <div class="health-dist-row health-dist-row--excellent">
                    <span class="health-dist-label">
                      <i class="health-dist-dot"></i>
                      优秀（≥{{ healthGrade.excellentMin }}分）
                    </span>
                    <span class="health-dist-value">{{ totalHealthy }}</span>
                  </div>
                  <div class="health-dist-row health-dist-row--watch">
                    <span class="health-dist-label">
                      <i class="health-dist-dot"></i>
                      良好（{{ healthGrade.watchMin }}–{{ healthGrade.excellentMin - 1 }}分）
                    </span>
                    <span class="health-dist-value">{{ totalSubHealthy }}</span>
                  </div>
                  <div class="health-dist-row health-dist-row--intervene">
                    <span class="health-dist-label">
                      <i class="health-dist-dot"></i>
                      关注（&lt;{{ healthGrade.watchMin }}分）
                    </span>
                    <span class="health-dist-value">{{ totalDisease }}</span>
                  </div>
                  <div class="health-dist-total">
                    <span class="health-dist-total-label">总鱼池数</span>
                    <span class="health-dist-total-value">{{ filteredFishPoolData.length }}</span>
                  </div>
                  <div v-if="pendingHealthCount" class="health-dist-pending">
                    其中 {{ pendingHealthCount }} 个待评估（与投喂主档对齐，尚无评分）
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12" class="health-overview-col">
              <el-card class="box-card health-overview-card">
                <div slot="header" class="clearfix health-overview-header">
                  <span>AI 健康分析</span>
                  <span v-if="lastUpdateTime" class="ai-health-time">{{ lastUpdateTime }} 分析完成</span>
                </div>
                <div class="ai-health-panel">
                  <div
                    v-for="(item, idx) in aiHealthInsights"
                    :key="'ai-' + idx"
                    class="ai-health-item"
                    :class="'ai-health-item--' + item.level"
                  >
                    <div class="ai-health-item-head">
                      <i :class="item.level === 'high' ? 'el-icon-warning' : item.level === 'medium' ? 'el-icon-info' : 'el-icon-success'"></i>
                      <span class="ai-health-item-title">{{ item.title }}</span>
                      <span class="ai-health-item-badge">{{ item.badge }}</span>
                    </div>
                    <div class="ai-health-item-detail">{{ item.detail }}</div>
                    <div v-if="item.hint" class="ai-health-item-hint">{{ item.hint }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <h2 class="section-title">各鱼池详细数据</h2>
          <p class="text-sm text-gray-600 mb-4" style="color: #9ca3af; margin-bottom: 1rem; font-size: 0.875rem;">
            数据来源：外部环境层（水质监测） | 外观行为层（手动录入） | 内部肌体层（打样检查） · 权重 {{ healthWeights.env }}/{{ healthWeights.appearance }}/{{ healthWeights.internal }}
          </p>
          <div class="pool-detail-grid">
            <div
              v-for="(pool, pIdx) in filteredFishPoolData"
              :key="'hp-'+(pool.pondId != null ? pool.pondId : pIdx)+'-'+pIdx"
              class="pool-detail-cell"
            >
              <el-card class="pool-card">
                <div slot="header" class="clearfix" style="display: flex; justify-content: space-between; align-items: center;">
                  <div style="display: flex; align-items: center; gap: 8px;">
                    <i class="el-icon-water-cup" style="color: #3b82f6; font-size: 1.2rem;"></i>
                    <span class="pool-name" style="font-size: 1.1rem;">{{ pool.displayLabel || pool.pondDetailNum || getPondName(pool.pondId) }}</span>
                  </div>
                  <el-tag size="small" type="danger" v-if="pool.numberOfExceptions > 0">
                    <i class="el-icon-warning-outline"></i> {{ pool.numberOfExceptions }}个异常
                  </el-tag>
                </div>

                <div class="pool-card-body">
                <!-- 综合评分：颜色按 ≥85 优秀 / 70–84 良好 / <70 关注 -->
                <div class="score-section" :style="scoreSectionStyle(pool.overallRating)">
                  <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                    <span style="color: #d1d5db;">综合健康评分
                      <span style="font-size: 0.75rem; margin-left: 6px;" :style="{ color: scoreGradeColor(pool.overallRating) }">{{ scoreGradeLabel(pool.overallRating) }}</span>
                    </span>
                    <span style="font-size: 2rem; font-weight: bold;" :style="{ color: scoreGradeColor(pool.overallRating) }">{{ formatScore(pool.overallRating) }}</span>
                  </div>
                  <el-progress :percentage="Math.min(100, Number(pool.overallRating) || 0)" :show-text="false" :color="scoreGradeColor(pool.overallRating)" style="margin-bottom: 15px;"></el-progress>
                  <div style="display: flex; justify-content: space-between; text-align: center; font-size: 0.875rem;">
                    <div>
                      <div style="color: #9ca3af; margin-bottom: 4px;">外部环境</div>
                      <div style="color: #f3f4f6; font-weight: bold;">{{ formatScore(pool.environmentalLayerRating) }}/{{ healthWeights.env }}</div>
                    </div>
                    <div>
                      <div style="color: #9ca3af; margin-bottom: 4px;">外观行为</div>
                      <div style="color: #f3f4f6; font-weight: bold;">{{ formatScore(pool.behavioralLayerRating) }}/{{ healthWeights.appearance }}</div>
                    </div>
                    <div>
                      <div style="color: #9ca3af; margin-bottom: 4px;">内部肌体</div>
                      <div style="color: #f3f4f6; font-weight: bold;">{{ formatScore(pool.bodyLayerRating) }}/{{ healthWeights.internal }}</div>
                    </div>
                  </div>
                </div>

                <!-- 外部环境层 -->
                <div class="layer-section" v-if="pool.environmentalLayerInformationObj">
                  <div class="layer-title" style="color: #60a5fa; font-weight: bold; margin-bottom: 12px; display: flex; align-items: center; gap: 8px;">
                    <i class="el-icon-odometer"></i> 外部环境层 ({{ healthWeights.env }}分) - 水质参数
                  </div>
                  <div class="metric-grid">
                    <div class="param-box">
                      <div class="param-label">溶氧 DO {{ itemScoreText(pool.environmentalLayerInformationObj, 'doOxygen', 4) }}</div>
                      <div class="param-value" :class="metricClass(pool.environmentalLayerInformationObj, 'doOxygen')">{{ formatMetric(pool.environmentalLayerInformationObj.doOxygen) }} mg/L</div>
                    </div>
                    <div class="param-box">
                      <div class="param-label">亚硝酸盐 {{ itemScoreText(pool.environmentalLayerInformationObj, 'nitrite', 4) }}</div>
                      <div class="param-value" :class="metricClass(pool.environmentalLayerInformationObj, 'nitrite')">{{ formatMetric(pool.environmentalLayerInformationObj.nitrite) }} mg/L</div>
                    </div>
                    <div class="param-box">
                      <div class="param-label">氨氮 {{ itemScoreText(pool.environmentalLayerInformationObj, 'ammoniaNitrogen', 3) }}</div>
                      <div class="param-value" :class="metricClass(pool.environmentalLayerInformationObj, 'ammoniaNitrogen')">{{ formatMetric(pool.environmentalLayerInformationObj.ammoniaNitrogen) }} mg/L</div>
                    </div>
                    <div class="param-box">
                      <div class="param-label">pH值 {{ itemScoreText(pool.environmentalLayerInformationObj, 'phPh', 3) }}</div>
                      <div class="param-value" :class="metricClass(pool.environmentalLayerInformationObj, 'phPh')">{{ formatMetric(pool.environmentalLayerInformationObj.phPh, 2) }}</div>
                    </div>
                    <div class="param-box">
                      <div class="param-label">
                        水温 {{ itemScoreText(pool.environmentalLayerInformationObj, 'doTemp', 3) }}
                        <span v-if="pool._speciesTempRange" class="temp-range-hint">适宜 {{ formatSpeciesTempHint(pool._speciesTempRange) }}</span>
                      </div>
                      <div class="param-value" :class="metricClass(pool.environmentalLayerInformationObj, 'doTemp')">{{ formatMetric(pool.environmentalLayerInformationObj.doTemp, 1) }} °C</div>
                    </div>
                    <div class="param-box">
                      <div class="param-label">硝酸盐 {{ itemScoreText(pool.environmentalLayerInformationObj, 'nitrate', 3) }}</div>
                      <div class="param-value" :class="metricClass(pool.environmentalLayerInformationObj, 'nitrate')">{{ formatMetric(pool.environmentalLayerInformationObj.nitrate) }} mg/L</div>
                    </div>
                  </div>
                </div>

                <el-divider v-if="pool.environmentalLayerInformationObj && (pool.behavioralLayerInformationObj || pool.bodyLayerInformationObj)"></el-divider>

                <!-- 外观行为层 -->
                <div class="layer-section" v-if="pool.behavioralLayerInformationObj">
                  <div class="layer-title" style="color: #a78bfa; font-weight: bold; margin-bottom: 12px; display: flex; align-items: center; gap: 8px;">
                    <i class="el-icon-view"></i> 外观行为层 ({{ healthWeights.appearance }}分)
                  </div>
                  <div class="metric-grid">
                    <div
                      class="status-box"
                      v-for="(row, bIdx) in behaviorRows(pool.behavioralLayerInformationObj)"
                      :key="(row && row.key) || ('brow-'+bIdx)"
                    >
                      <span class="status-label">{{ row.label }} {{ row.scoreText }}</span>
                      <el-tag size="mini" :type="row.ok ? 'success' : 'danger'">{{ row.value }}</el-tag>
                    </div>
                  </div>
                </div>

                <el-divider v-if="pool.behavioralLayerInformationObj && pool.bodyLayerInformationObj"></el-divider>

                <!-- 内部肌体层 -->
                <div class="layer-section" v-if="pool.bodyLayerInformationObj">
                  <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
                    <div class="layer-title" style="color: #fb923c; font-weight: bold; display: flex; align-items: center; gap: 8px;">
                      <i class="el-icon-first-aid-kit"></i> 内部肌体层 ({{ healthWeights.internal }}分)
                    </div>
                    <span style="color: #9ca3af; font-size: 0.875rem;">{{ formatDate(pool.createTime) }}</span>
                  </div>
                  <div v-if="pool.bodyLayerInformationObj.itemScores && pool.bodyLayerInformationObj.itemScores.placeholder" style="color:#facc15;font-size:12px;margin-bottom:8px;">
                    暂无打样数据，机体层按满分占位
                  </div>
                  <div class="list-section">
                    <div class="list-item">
                      <span>肝脏与胆囊 {{ itemScoreText(pool.bodyLayerInformationObj, 'liverAndGallbladder', 4) }}</span>
                      <span :class="statusTextClass(pool.bodyLayerInformationObj.liverAndGallbladder)">{{ pool.bodyLayerInformationObj.liverAndGallbladder || '—' }}</span>
                    </div>
                    <div class="list-item">
                      <span>鳃丝(镜检) {{ itemScoreText(pool.bodyLayerInformationObj, 'gillFilaments', 4) }}</span>
                      <span :class="statusTextClass(pool.bodyLayerInformationObj.gillFilaments)">{{ pool.bodyLayerInformationObj.gillFilaments || '—' }}</span>
                    </div>
                    <div class="list-item">
                      <span>肠道 {{ itemScoreText(pool.bodyLayerInformationObj, 'intestine', 4) }}</span>
                      <span :class="statusTextClass(pool.bodyLayerInformationObj.intestine)">{{ pool.bodyLayerInformationObj.intestine || '—' }}</span>
                    </div>
                    <div class="list-item">
                      <span>内脏结节 {{ itemScoreText(pool.bodyLayerInformationObj, 'visceralNodule', 4) }}</span>
                      <span :class="statusTextClass(pool.bodyLayerInformationObj.visceralNodule)">{{ pool.bodyLayerInformationObj.visceralNodule || '—' }}</span>
                    </div>
                    <div class="list-item">
                      <span>规格与饵料系数 {{ itemScoreText(pool.bodyLayerInformationObj, 'specificationAndBaitCoefficient', 4) }}</span>
                      <span :class="statusTextClass(pool.bodyLayerInformationObj.specificationAndBaitCoefficient)">{{ pool.bodyLayerInformationObj.specificationAndBaitCoefficient || '—' }}</span>
                    </div>
                  </div>
                </div>

                <!-- 异常汇总：限高滚动，避免单卡撑高整行留下大块空白 -->
                <div v-if="pool.exceptionMessage" class="exception-summary">
                  <div class="exception-summary-title">
                    <i class="el-icon-warning"></i> 异常汇总提示
                  </div>
                  <div class="exception-summary-body">
                    {{ plainText(pool.exceptionMessage) }}
                  </div>
                </div>
                </div>

              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane v-if="!isHealthOnlyPage" label="死亡量死亡率" name="mortality">
          <!-- 死亡统计概览（默认展示全部鱼池/网箱汇总，筛选仅缩小范围） -->
          <el-row :gutter="20" class="mb-4">
            <el-col :span="6">
              <el-card shadow="hover">
                <div class="stat-title">今日总死亡数</div>
                <div class="stat-value text-red">{{ filteredTodayDeaths }} <span class="unit">尾</span></div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <div class="stat-title">本周总死亡数</div>
                <div class="stat-value text-orange">{{ filteredWeekDeaths }} <span class="unit">尾</span></div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <div class="stat-title">累计死亡数（台账）</div>
                <div class="stat-value text-amber">{{ filteredTotalDeaths }} <span class="unit">尾</span></div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <div class="stat-title">平均存活率</div>
                <div class="stat-value">{{ filteredAvgSurvival }}% <span class="unit">当前范围</span></div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="mb-4">
            <el-col :span="12">
              <el-card>
                <div slot="header">7天死亡趋势</div>
                <div class="chart-container" ref="mortalityLineChart"></div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card>
                <div slot="header">各鱼池死亡对比</div>
                <div class="chart-container" ref="mortalityBarChart"></div>
              </el-card>
            </el-col>
          </el-row>

          <h2 class="section-title">各鱼池死亡详情</h2>
          <el-row :gutter="20">
            <el-col :span="8" v-for="(pool, pIdx) in filteredMortalityData" :key="'mp-'+(pool.pondId != null ? pool.pondId : pIdx)+'-'+pIdx" class="mb-4">
              <el-card class="pool-card" :class="{ 'warn-pool': (pool.dayDead || 0) >= deathWarnThreshold && deathWarnThreshold > 0 }">
                <div slot="header" class="clearfix">
                  <span class="pool-name">{{ plainText(pool.displayLabel || pool.pondName) }}</span>
                  <el-tag size="small" :type="(pool.dayDead || 0) >= deathWarnThreshold && deathWarnThreshold > 0 ? 'danger' : 'info'">
                    今日死亡: {{ pool.dayDead || 0 }}
                  </el-tag>
                </div>
                <div class="pool-stats">
                  <div class="stat-row">
                    <span>本周死亡（日志）</span>
                    <span>{{ pool.weekDead || 0 }} 尾</span>
                  </div>
                  <div class="stat-row">
                    <span>本月死亡（日志）</span>
                    <span>{{ pool.monthDead || 0 }} 尾</span>
                  </div>
                  <div class="stat-row">
                    <span>累计死亡（台账）</span>
                    <span>{{ pool.deathQuantity || 0 }} 尾</span>
                  </div>
                  <div v-if="pool.survivalRate != null && pool.survivalRate !== ''" class="stat-row">
                    <span>存活率</span>
                    <span>{{ pool.survivalRate }}%</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div style="width: 100%;height: 30px;"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getZkDeathSurvivalRateStatistics, getZkDeathSurvivalRatePage } from '@/api/fish/zkDeathSurvivalRate'
import { getHealthStatusDistribution, getLatestPondHealthList } from '@/api/fish/zkPondHealth'
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { getPrecisionFeedingPonds } from '@/api/fish/zkBiologyCapital'
import { getZkPondFishDetailPage } from '@/api/fish/zkPondFishDetail'
import { pushAlarmAndCreateTask, ROUTES } from '@/utils/alarmTaskBridge'
import { getThresholdModule, resolveHealthGrades, resolveHealthWeights } from '@/utils/thresholdConfig'
import {
  normalizeSpeciesName,
  speciesNamesEqual,
  listSyncedSpeciesOptions,
  collectSpeciesFilterOptions,
  applySpeciesWaterTempAndWeights,
  formatSpeciesTempHint
} from '@/utils/speciesTempBridge'
import { evaluateAssociationAndPush } from '@/utils/associationBridge'
import { formatWorkshop, formatSystem, parseWorkshopNum, parseSystemNum, normalizeLakeName, siteLabelsEqual } from '@/utils/siteNaming'
import { loadWorkshopRegistry, resolveWorkshopScope, textMatchesWorkshopScope } from '@/utils/siteScopeResolver'
import { buildSiteDisplayLabel, buildWorkshopOptionLabel, buildSystemOptionLabel, withTimeout } from '@/utils/siteDisplay'
import { html2Text } from '@/utils/index'
import { parseLayerInfo } from '@/utils/healthLayerDisplay'
import { getCachedPondList, ensurePondArchiveList, mergeMasterPondsWithHealth, dedupePondsById, collectWorkshopsFromPonds, workshopLabelForPond, resolvePondWorkshopKey } from '@/utils/healthPreviewBridge'
import { loadSharedSiteOptions, peekSharedSiteOptions, pickWorkshopOptionLabels, pickSystemOptionLabels, pickPondOptionLabels, rowMatchesSiteFilters, isRowInUserSiteScope, SITE_OPTIONS_CHANGED_EVENT } from '@/utils/siteOptions'
import { onProductionRecordChanged } from '@/utils/productionRecordBridge'
import { collectBatchFilterOptions, rowMatchesBatchFilter } from '@/utils/batchTokens'
import PageLoadingMask from '@/components/PageLoadingMask/index'

export default {
  name: 'ZkDeathSurvivalRateSystem',
  components: { PageLoadingMask },
  data() {
    return {
      activeTab: 'health',
      lastUpdateTime: '',
      fishPoolData: [],
      mortalityData: [],
      healthStatus: {
        excellentCount: 0,
        goodCount: 0,
        attentionCount: 0,
        totalCount: 0
      },
      healthGrade: { excellentMin: 85, watchMin: 70 },
      healthWeights: { env: 20, appearance: 60, internal: 20, sum: 100 },
      /** 水质阈值（品种水温），用于健康环境层水温重算 */
      waterThresholdConfig: null,
      pondList: [],
      siteOptions: peekSharedSiteOptions(),
      topStats: {
        dayDead: 0,
        weekDead: 0,
        monthDead: 0,
        avgDeadPerEvent: 0
      },
      trendStats: {},
      charts: {},
      filterWorkshop: '',
      filterSystem: '',
      filterPond: '',
      filterSpecies: '',
      filterBatch: '',
      filterSpec: '',
      deathWarnThreshold: 10,
      pushingHealthAlarms: false,
      pageLoading: false,
      workshopRegistry: [],
    }
  },
  computed: {
    pageTitle() {
      return this.isHealthOnlyPage ? '鱼类健康状态' : '死亡量存活率'
    },
    pageSubtitle() {
      return this.isHealthOnlyPage
        ? '实时监测各鱼池健康评分与分层指标'
        : '展示全部鱼池死亡量与存活趋势，可按车间/系统/池号筛选'
    },
    workshopOptions() {
      return pickWorkshopOptionLabels(this.siteOptions)
    },
    systemOptions() {
      return pickSystemOptionLabels(this.siteOptions, this.filterWorkshop)
    },
    pondOptions() {
      return pickPondOptionLabels(this.siteOptions, this.filterWorkshop, this.filterSystem)
    },
    speciesOptions() {
      // 与精准投喂 / 生长大模型 / 死亡记录同一套：只取鱼池主档实际品种并归一别名
      return collectSpeciesFilterOptions([
        ...(this.fishPoolData || []),
        ...(this.mortalityData || []),
        ...(this.pondList || [])
      ])
    },
    batchOptions() {
      return collectBatchFilterOptions([
        ...(this.enrichedHealthPools || []),
        ...(this.enrichedMortalityPools || []),
        ...(this.pondList || [])
      ])
    },
    specOptions() {
      const fromHealth = this.fishPoolData.map(p => p.fishSpec || p.spec).filter(Boolean)
      const fromDead = this.mortalityData.map(p => p.fishSpec || p.spec).filter(Boolean)
      return [...new Set([...fromHealth, ...fromDead])]
    },
    enrichedHealthPools() {
      return (this.fishPoolData || []).map(pool => {
        const pond = this.pondList.find(p => String(p.id) === String(pool.pondId)) || {}
        const siteRow = ((this.siteOptions && this.siteOptions.rows) || []).find(
          (r) => String(r.id) === String(pool.pondId || pond.id)
        ) || {}
        const merged = {
          ...pond,
          ...pool,
          workshopId: pool.workshopId || pond.workshopId || siteRow.workshopId,
          pondSystemId: pool.pondSystemId || pond.pondSystemId || siteRow.pondSystemId,
          workshopName: siteRow.workshopName || pool.workshopName || pond.workshopName || '',
          pondNum: pool.pondNum || pond.pondNum || '',
          pondSystemName: siteRow.pondSystemName || pool.pondSystemName || pool.pond_system_name || pond.pondSystemName || '',
          pondDetailNum: pool.pondDetailNum || pond.pondDetailNum || siteRow.pondDetailNum || '',
          pondName: pool.pondName || pond.pondName || siteRow.pondName || '',
          fishName: pool.fishName || pool.species || pool.fishTypes || '',
          batchNumber: pool.batchNumber || pond.batchNumber || siteRow.batchNumber || '',
          fishSpec: pool.fishSpec || pool.spec || '',
        }
        return {
          ...merged,
          displayLabel: buildSiteDisplayLabel(merged)
        }
      })
    },
    enrichedMortalityPools() {
      return (this.mortalityData || []).map(pool => {
        const pond = this.pondList.find(p => String(p.id) === String(pool.pondId)) || {}
        const siteRow = ((this.siteOptions && this.siteOptions.rows) || []).find(
          (r) => String(r.id) === String(pool.pondId || pond.id)
        ) || {}
        const merged = {
          ...pond,
          ...pool,
          workshopId: pool.workshopId || pond.workshopId || siteRow.workshopId,
          pondSystemId: pool.pondSystemId || pond.pondSystemId || siteRow.pondSystemId,
          workshopName: siteRow.workshopName || pool.workshopName || pond.workshopName || '',
          pondNum: pool.pondNum || pond.pondNum || '',
          pondSystemName: siteRow.pondSystemName || pool.pondSystemName || pool.pond_system_name || pond.pondSystemName || '',
          pondDetailNum: pool.pondDetailNum || pond.pondDetailNum || siteRow.pondDetailNum || '',
          pondName: pool.pondName || pond.pondName || pool.pondDetailNum || siteRow.pondName || '',
          fishName: pool.fishName || pool.species || pool.fishTypes || '',
          batchNumber: pool.batchNumber || pond.batchNumber || siteRow.batchNumber || '',
          fishSpec: pool.fishSpec || pool.spec || '',
          fishNum: pool.fishNum != null ? pool.fishNum : (pond.fishNum != null ? pond.fishNum : pond.fish_num),
        }
        return {
          ...merged,
          displayLabel: buildSiteDisplayLabel(merged)
        }
      })
    },
    filteredFishPoolData() {
      return this.enrichedHealthPools.filter(pool => this.matchPoolFilter(pool))
    },
    filteredMortalityData() {
      return this.enrichedMortalityPools.filter(pool => this.matchPoolFilter(pool))
    },
    totalHealthy() {
      const min = this.healthGrade.excellentMin
      return this.filteredFishPoolData.filter(p => !p._pendingHealth && (p.overallRating || 0) >= min).length
    },
    totalSubHealthy() {
      const { excellentMin, watchMin } = this.healthGrade
      return this.filteredFishPoolData.filter(p => {
        if (p._pendingHealth || p.overallRating == null) return false
        const s = p.overallRating || 0
        return s >= watchMin && s < excellentMin
      }).length
    },
    totalDisease() {
      return this.filteredFishPoolData.filter(p => !p._pendingHealth && p.overallRating != null && (p.overallRating || 0) < this.healthGrade.watchMin).length
    },
    attentionHealthPools() {
      const watch = this.healthGrade.watchMin
      return this.filteredFishPoolData.filter(p => !p._pendingHealth && (Number(p.overallRating) || 0) < watch)
    },
    pendingHealthCount() {
      return this.filteredFishPoolData.filter(p => p._pendingHealth).length
    },
    totalFish() {
      return this.filteredFishPoolData.length
    },
    avgHealthScore() {
      const list = this.filteredFishPoolData
      if (!list.length) return 0
      return (list.reduce((sum, pool) => sum + (pool.overallRating || 0), 0) / list.length).toFixed(1)
    },
    filteredTodayDeaths() {
      return this.filteredMortalityData.reduce((s, p) => s + (Number(p.dayDead) || 0), 0)
    },
    filteredWeekDeaths() {
      return this.filteredMortalityData.reduce((s, p) => s + (Number(p.weekDead) || 0), 0)
    },
    filteredMonthDeaths() {
      return this.filteredMortalityData.reduce((s, p) => s + (Number(p.monthDead) || 0), 0)
    },
    filteredTotalDeaths() {
      return this.filteredMortalityData.reduce((s, p) => s + (Number(p.deathQuantity) || 0), 0)
    },
    filteredAvgSurvival() {
      const list = this.filteredMortalityData.filter(p => p.survivalRate != null && p.survivalRate !== '')
      if (!list.length) return '—'
      const sum = list.reduce((s, p) => s + (Number(p.survivalRate) || 0), 0)
      return (sum / list.length).toFixed(1)
    },
    filteredAvgMortality() {
      const list = this.filteredMortalityData
      if (!list.length) return '0.00'
      // PoolStats 无死亡率字段：用今日死亡 / (存塘+今日死亡) 估算筛选范围平均死亡率
      const rates = list.map((p) => {
        const dead = Number(p.dayDead) || 0
        const fish = Number(p.fishNum != null ? p.fishNum : p.fish_num) || 0
        const denom = fish + dead
        if (denom <= 0) return null
        return (dead / denom) * 100
      }).filter((r) => r != null)
      if (!rates.length) {
        const top = Number(this.topStats && this.topStats.avgDeadPerEvent)
        if (Number.isFinite(top)) return (top <= 1 ? top * 100 : top).toFixed(2)
        return '0.00'
      }
      return (rates.reduce((s, r) => s + r, 0) / rates.length).toFixed(2)
    },
    totalTodayDeaths() {
      return this.topStats.dayDead || 0;
    },
    totalWeekDeaths() {
      return this.topStats.weekDead || 0;
    },
    totalMonthDeaths() {
      return this.topStats.monthDead || 0;
    },
    avgTodayMortalityRate() {
      return this.topStats.avgDeadPerEvent ? this.topStats.avgDeadPerEvent.toFixed(2) : 0;
    },
    /** 独立健康路由：仅展示健康页，不露出死亡 Tab */
    isHealthOnlyPage() {
      const path = (this.$route && this.$route.path) || '';
      return path.indexOf('fishHealthStatus') !== -1;
    },
    aiHealthInsights() {
      const list = (this.filteredFishPoolData || []).filter(p => !p._pendingHealth)
      const pending = (this.filteredFishPoolData || []).filter(p => p._pendingHealth)
      const { watchMin } = this.healthGrade
      const insights = []
      const poolLabel = (p) => p.displayLabel || p.pondDetailNum || p.pondName || this.getPondName(p.pondId)

      const intervene = list.filter(p => (Number(p.overallRating) || 0) < watchMin)
      if (intervene.length) {
        insights.push({
          level: 'high',
          badge: '高风险',
          title: '发现需要立即介入的鱼池',
          detail: intervene.map(poolLabel).join(', '),
          hint: '外部环境层异常可能需要调整水质参数，请优先复核。'
        })
      }

      const envWeight = this.healthWeights.env || 20
      const envBad = list.filter(p => (Number(p.environmentalLayerRating) || 0) < envWeight * 0.65)
      if (envBad.length) {
        insights.push({
          level: 'high',
          badge: '高风险',
          title: '外部环境层存在异常',
          detail: envBad.map(poolLabel).join(', '),
          hint: '复核水温、溶氧、氨氮等指标。'
        })
      }

      const appWeight = this.healthWeights.appearance || 60
      const behBad = list.filter(p => (Number(p.behavioralLayerRating) || 0) < appWeight * 0.65)
      if (behBad.length) {
        insights.push({
          level: 'medium',
          badge: '中风险',
          title: '外观行为层异常信号',
          detail: behBad.map(poolLabel).join(', '),
          hint: '摄食、游泳等行为指标偏弱，建议加强巡检。'
        })
      }

      if (pending.length) {
        insights.push({
          level: 'medium',
          badge: '待评估',
          title: '部分鱼池尚无健康评分',
          detail: pending.map(poolLabel).join(', '),
          hint: '已与精准投喂主档对齐，可在健康录入后自动出分。'
        })
      }

      if (!insights.length) {
        insights.push({
          level: 'normal',
          badge: '正常',
          title: '当前筛选范围内鱼池健康整体稳定',
          detail: '共 ' + (this.filteredFishPoolData || []).length + ' 个鱼池',
          hint: '保持例行监测与投喂观察即可。'
        })
      }
      return insights
    }
  },
  created() {
    const query = this.$route.query || {};
    const metaTab = this.$route.meta && this.$route.meta.defaultActiveTab;
    if (this.isHealthOnlyPage) {
      this.activeTab = 'health';
    } else {
      const activeTab = query.activeTab || metaTab;
      if (activeTab && ['health', 'mortality'].includes(activeTab)) this.activeTab = activeTab;
    }
    // 从路由读取车间/系统/池号筛选（大屏跳转带入）
    this.bootstrapRouteFilters()
    loadSharedSiteOptions({ force: true, includePonds: true })
      .then((opts) => { this.siteOptions = opts })
      .catch((e) => console.warn('[zkDeathSurvivalRate] site options', e))
    this._onSiteOptionsChanged = () => {
      loadSharedSiteOptions({ force: true, includePonds: true })
        .then((opts) => { this.siteOptions = opts })
        .catch(() => {})
    }
    if (typeof window !== 'undefined') {
      window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged)
    }
    this._offProductionRecord = onProductionRecordChanged((detail) => {
      const t = detail && detail.type
      if (!t || t === 'death' || t === 'stocking' || t === 'split' || t === 'sale') {
        this.handleRefresh()
      }
    })
    this.handleRefresh();
  },
  mounted() {
    this.initCharts();
    window.addEventListener('resize', this.resizeCharts);
  },
  activated() {
    loadSharedSiteOptions({ force: true, includePonds: true })
      .then((opts) => { this.siteOptions = opts })
      .catch(() => {})
    this.handleRefresh()
  },
  beforeDestroy() {
    if (typeof this._offProductionRecord === 'function') {
      this._offProductionRecord()
      this._offProductionRecord = null
    }
    if (typeof window !== 'undefined' && this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged)
    }
    window.removeEventListener('resize', this.resizeCharts);
    Object.values(this.charts).forEach(chart => chart && chart.dispose());
  },
  watch: {
    activeTab() {
      this.$nextTick(() => {
        if (this.activeTab === 'mortality') {
          if (this.$refs.mortalityLineChart && !this.charts.mortalityLine) {
            this.charts.mortalityLine = echarts.init(this.$refs.mortalityLineChart)
          }
          if (this.$refs.mortalityBarChart && !this.charts.mortalityBar) {
            this.charts.mortalityBar = echarts.init(this.$refs.mortalityBarChart)
          }
        }
        this.resizeCharts();
        this.updateCharts();
      });
    },
    '$route.query.activeTab'(val) {
      if (this.isHealthOnlyPage) {
        this.activeTab = 'health';
        return;
      }
      if (val && ['health', 'mortality'].includes(val) && val !== this.activeTab) {
        this.activeTab = val;
      }
    },
    '$route.path'() {
      if (this.isHealthOnlyPage) this.activeTab = 'health';
    },
    '$route.query.pondName'(val) {
      this.filterPond = val ? String(val) : ''
      this.applyFilter()
    },
    '$route.query.workshop'() {
      this.bootstrapRouteFilters()
      this.applyFilter()
    },
    '$route.query.system'() {
      this.applyRouteSiteFilters()
      this.applyFilter()
    },
    '$route.fullPath'() {
      if (!this.isHealthOnlyPage) return;
      const pond = (this.$route.query && this.$route.query.pondName) || '';
      this.filterPond = pond ? String(pond) : '';
      this.applyFilter();
    }
  },
  activated() {
    const query = this.$route.query || {};
    if (this.isHealthOnlyPage) {
      this.activeTab = 'health';
    } else {
      const activeTab = query.activeTab;
      if (activeTab && ['health', 'mortality'].includes(activeTab)) {
        this.activeTab = activeTab;
      }
    }
    // 从路由同步筛选（无 pondName 时不默认全池）
    this.bootstrapRouteFilters()
    // 重新拉阈值权重，保证阈值管理页改动立刻生效
    this.handleRefresh();
  },
  methods: {
    async bootstrapRouteFilters() {
      try {
        this.workshopRegistry = await loadWorkshopRegistry()
      } catch (e) {
        this.workshopRegistry = []
      }
      this.applyRouteSiteFilters()
    },
    applyRouteSiteFilters() {
      const q = (this.$route && this.$route.query) || {}
      this.filterPond = (q.pondName != null && q.pondName !== '') ? String(q.pondName) : ''
      if (q.workshop != null && q.workshop !== '') {
        const scope = resolveWorkshopScope(q.workshop, null, this.workshopRegistry)
        this.filterWorkshop = scope.label || formatWorkshop(q.workshop)
      } else {
        this.filterWorkshop = ''
      }
      this.filterSystem = (q.system != null && q.system !== '') ? String(q.system) : ''
    },
    syncRouteQuery(patch) {
      const q = Object.assign({}, this.$route.query)
      Object.keys(patch).forEach((k) => {
        if (patch[k] === undefined || patch[k] === '') delete q[k]
        else q[k] = patch[k]
      })
      if (JSON.stringify(q) !== JSON.stringify(this.$route.query)) {
        this.$router.replace({ path: this.$route.path, query: q }).catch(() => {})
      }
    },
    onFilterWorkshopChange(val) {
      this.filterWorkshop = val || ''
      if (!val) {
        this.filterSystem = ''
        this.filterPond = ''
        this.syncRouteQuery({ workshop: undefined, system: undefined, pondName: undefined })
      } else if (this.filterSystem && this.systemOptions.length && !this.systemOptions.includes(this.filterSystem)) {
        this.filterSystem = ''
      }
      this.applyFilter()
    },
    onFilterSystemChange(val) {
      this.filterSystem = val || ''
      if (!val) {
        this.filterPond = ''
        this.syncRouteQuery({ system: undefined, pondName: undefined })
      }
      this.applyFilter()
    },
    onFilterPondChange(val) {
      this.filterPond = val || ''
      this.syncRouteQuery({ pondName: val || undefined })
      this.applyFilter()
    },
    onFilterPondClear() {
      this.filterPond = ''
      this.syncRouteQuery({ pondName: undefined })
      this.applyFilter()
    },
    plainText(val) {
      if (val == null || val === '') return '';
      return html2Text(String(val)).replace(/\s+/g, ' ').trim();
    },
    async pushAttentionHealthAlarms(opts) {
      const silent = !!(opts && opts.silent)
      const list = this.attentionHealthPools || []
      if (!list.length) {
        if (!silent) this.$message.info('当前无关注级鱼池')
        return
      }
      this.pushingHealthAlarms = true
      let ok = 0
      let skipped = 0
      let fail = 0
      try {
        for (const pool of list) {
          const pondNo = pool.pondDetailNum || pool.pondName || this.getPondName(pool.pondId) || ''
          const score = Number(pool.overallRating || 0).toFixed(1)
          const title = `健康度关注 ${pondNo} 评分=${score}`
          const res = await pushAlarmAndCreateTask({
            source: 'health',
            category: 'fish',
            pondDetailNum: pondNo,
            pondName: pool.pondName || pondNo,
            workshopName: pool.workshopName || '',
            pondSystemName: pool.pondNum || '',
            title,
            content: `综合评分 ${score}（关注 <${this.healthGrade.watchMin}）；异常项 ${pool.numberOfExceptions || 0} 个`,
            taskTitle: `巡检核查：${pondNo} 健康度关注(评分 ${score})`,
            level: 'urgent',
            period: 'day',
          })
          if (res.alarm && res.alarm.skipped) skipped += 1
          else if (res.alarm && res.alarm.ok) ok += 1
          else fail += 1
          await evaluateAssociationAndPush({
            category: 'fish',
            pondDetailNum: pondNo,
            pondName: pool.pondName || pondNo,
            workshopName: pool.workshopName || '',
            pondSystemName: pool.pondSystemName || pool.pondNum || '',
            healthScore: Number(pool.overallRating || 0),
            healthWatchMin: this.healthGrade.watchMin,
            title: `健康度关注 ${pondNo} 评分=${score}`
          })
        }
        if (silent) {
          // 静默模式仅压制成功提示；有失败必须可见
          if (fail > 0) {
            this.$message.warning(`健康度报警同步：成功 ${ok}，去重 ${skipped}，失败 ${fail}`)
          }
          return
        }
        const tip = `已处理 ${list.length} 项：新推送 ${ok}，去重跳过 ${skipped}，失败 ${fail}`
        if (fail && !ok) this.$message.error(tip)
        else this.$message.success(tip)
        this.$confirm('是否前往 AI 报警监控查看？', '推送完成', {
          confirmButtonText: '去报警监控',
          cancelButtonText: '去任务计划',
          distinguishCancelAndClose: true,
          type: 'success',
        }).then(() => {
          this.$router.push(`${ROUTES.ALERT}?source=health&category=fish`)
        }).catch(action => {
          if (action === 'cancel') this.$router.push(`${ROUTES.TASK}?from=health`)
        })
      } finally {
        this.pushingHealthAlarms = false
      }
    },
    matchPoolFilter(pool) {
      // 有今日/近周死亡数据时不因站点白名单硬滤掉，保证生产记录能回显到看板
      const hasDead =
        (Number(pool && pool.dayDead) || 0) > 0 ||
        (Number(pool && pool.weekDead) || 0) > 0 ||
        (Number(pool && pool.monthDead) || 0) > 0
      if (!hasDead && !isRowInUserSiteScope(pool, this.siteOptions)) return false
      if (!rowMatchesSiteFilters(pool, {
        workshop: this.filterWorkshop,
        system: this.filterSystem,
        pond: this.filterPond
      }, this.siteOptions)) {
        return false
      }
      if (this.filterSpecies) {
        const sp = pool.fishName || pool.species || pool.fishTypes || pool.fishTypeName || ''
        if (!speciesNamesEqual(sp, this.filterSpecies)) return false
      }
      if (this.filterBatch) {
        if (!rowMatchesBatchFilter(pool, this.filterBatch)) return false
      }
      if (this.filterSpec) {
        const spec = pool.fishSpec || pool.spec || ''
        if (String(spec) !== String(this.filterSpec)) return false
      }
      return true
    },
    applyFilter() {
      this.$nextTick(() => {
        this.updateCharts()
      })
    },
    resetFilter() {
      this.filterWorkshop = ''
      this.filterSystem = ''
      this.filterPond = ''
      this.filterSpecies = ''
      this.filterBatch = ''
      this.filterSpec = ''
      const q = Object.assign({}, this.$route.query || {})
      delete q.pondName
      delete q.workshop
      delete q.system
      this.$router.replace({ path: this.$route.path, query: q }).catch(() => {})
      this.applyFilter()
    },
    goThresholdPage() {
      this.$router.push({ path: '/iot/thresholdSettings', query: { module: 'health' } }).catch(() => {})
    },
    async loadHealthThresholds() {
      try {
        const [health, water] = await Promise.all([
          withTimeout(getThresholdModule('health', false), 6000, null),
          withTimeout(getThresholdModule('water', false), 6000, null)
        ])
        if (health) {
          this.healthGrade = resolveHealthGrades(health)
          this.healthWeights = resolveHealthWeights(health)
        }
        if (water && Array.isArray(water.speciesTemps)) {
          this.waterThresholdConfig = water
        }
      } catch (e) {
        console.warn('[health] 阈值加载失败，使用默认分界', e && e.message)
      }
    },
    applyHealthThresholdWeights(list) {
      // 先按品种水温阈值重算环境层 doTemp，再套健康权重
      return applySpeciesWaterTempAndWeights(
        list,
        this.waterThresholdConfig,
        this.healthWeights || { env: 20, appearance: 60, internal: 20 }
      )
    },
    formatSpeciesTempHint,
    mergeMortalityWithBusinessRecords(poolStatsList, businessList) {
      const byPond = new Map()
      ;(poolStatsList || []).forEach((p) => {
        if (!p || p.pondId == null) return
        byPond.set(String(p.pondId), { ...p })
      })
      ;(businessList || []).forEach((rec) => {
        if (!rec || rec.pondId == null) return
        const key = String(rec.pondId)
        const prev = byPond.get(key) || {
          pondId: rec.pondId,
          pondName: rec.pondName,
          dayDead: 0,
          weekDead: 0,
          monthDead: 0
        }
        byPond.set(key, {
          ...prev,
          pondId: rec.pondId,
          pondName: rec.pondName || prev.pondName,
          workshopName: rec.workshopName || prev.workshopName,
          pondNum: rec.pondNum || prev.pondNum,
          pondSystemName: rec.pondSystemName || prev.pondSystemName,
          pondDetailNum: rec.pondDetailNum || prev.pondDetailNum,
          batchNumber: rec.batchNumber || prev.batchNumber,
          deathQuantity: rec.deathQuantity != null ? rec.deathQuantity : prev.deathQuantity,
          survivalRate: rec.survivalRate != null ? rec.survivalRate : prev.survivalRate
        })
      })
      return Array.from(byPond.values())
    },
    /** 用生产「死亡记录」当日明细补强 dayDead，避免统计接口滞后导致看板不回显 */
    mergeMortalityWithTodayDeathDetails(statsList, deathDetails) {
      const start = new Date()
      start.setHours(0, 0, 0, 0)
      const startTs = start.getTime()
      const todaySum = new Map()
      ;(deathDetails || []).forEach((rec) => {
        if (!rec || rec.pondId == null) return
        if (String(rec.addType) !== '-2') return
        const ts = Number(rec.createTime) || 0
        if (ts && ts < startTs) return
        const key = String(rec.pondId)
        const prev = todaySum.get(key) || { sum: 0, rec }
        prev.sum += Number(rec.fishNum) || 0
        prev.rec = rec
        todaySum.set(key, prev)
      })
      if (!todaySum.size) return statsList || []
      const byPond = new Map()
      ;(statsList || []).forEach((p) => {
        if (!p || p.pondId == null) return
        byPond.set(String(p.pondId), { ...p })
      })
      todaySum.forEach((info, key) => {
        const rec = info.rec
        const prev = byPond.get(key) || {
          pondId: rec.pondId,
          pondName: rec.pondName,
          dayDead: 0,
          weekDead: 0,
          monthDead: 0
        }
        byPond.set(key, {
          ...prev,
          pondId: rec.pondId,
          pondName: rec.pondName || prev.pondName,
          workshopName: rec.workshopName || prev.workshopName,
          pondNum: rec.pondNum || prev.pondNum,
          pondSystemName: rec.pondSystemName || prev.pondSystemName,
          pondDetailNum: rec.pondDetailNum || prev.pondDetailNum,
          batchNumber: rec.batchNumber || prev.batchNumber,
          dayDead: Math.max(Number(prev.dayDead) || 0, info.sum)
        })
      })
      return Array.from(byPond.values())
    },
    /** 171：与精准投喂/健康主档对齐，保证各鱼池卡片均展示 */
    mergeMortalityWithMasterPonds(masterPonds, statsList) {
      const merged = this.mergeMortalityWithBusinessRecords(statsList || [], [])
      const byId = new Map()
      merged.forEach((p) => {
        if (p && p.pondId != null) byId.set(String(p.pondId), p)
      })
      const master = dedupePondsById(masterPonds || [])
      if (!master.length) return merged
      const used = new Set()
      const list = master.map((pond) => {
        const prev = byId.get(String(pond.id)) || {}
        if (prev.pondId != null) used.add(String(pond.id))
        return {
          pondId: pond.id,
          pondName: pond.pondName || prev.pondName || pond.name,
          pondDetailNum: pond.pondDetailNum || prev.pondDetailNum,
          workshopName: pond.workshopName || prev.workshopName,
          pondSystemName: pond.pondSystemName || prev.pondSystemName,
          pondNum: pond.pondNum || prev.pondNum,
          batchNumber: pond.batchNumber || prev.batchNumber,
          dayDead: prev.dayDead != null ? prev.dayDead : 0,
          weekDead: prev.weekDead != null ? prev.weekDead : 0,
          monthDead: prev.monthDead != null ? prev.monthDead : 0,
          deathQuantity: prev.deathQuantity != null ? prev.deathQuantity : 0,
          survivalRate: prev.survivalRate != null ? prev.survivalRate : null,
          fishNum: pond.fishNum != null ? pond.fishNum : prev.fishNum
        }
      })
      // 主档对不上的统计池仍保留，避免有汇总无卡片
      merged.forEach((p) => {
        if (p && p.pondId != null && !used.has(String(p.pondId))) list.push(p)
      })
      return list
    },
    async handleRefresh() {
      if (this._refreshing) return
      this._refreshing = true
      this.pageLoading = true
      try {
        // 阈值与健康评分并行；zk-pond/page 不得阻塞首屏
        const cachedPonds = getCachedPondList()
        if (cachedPonds.length && !(this.pondList && this.pondList.length)) {
          this.pondList = cachedPonds
        }

        const healthOnly = this.isHealthOnlyPage
        const [, healthDistRes, healthListRes, mortalityRes, deathPageRes, feedingPondsRes, todayDeathRes] = await Promise.all([
          this.loadHealthThresholds(),
          withTimeout(getHealthStatusDistribution(), 12000, null).catch((e) => {
            console.error('[健康状态] 分布加载失败', e)
            this.$message && this.$message.warning('健康分布数据加载失败')
            return null
          }),
          withTimeout(getLatestPondHealthList(), 12000, null).catch((e) => {
            console.error('[健康状态] 池评分加载失败', e)
            this.$message && this.$message.warning('各池健康评分加载失败')
            return null
          }),
          healthOnly
            ? Promise.resolve(null)
            : withTimeout(getZkDeathSurvivalRateStatistics(), 12000, null).catch((e) => {
              console.error('[健康状态] 死亡率统计加载失败', e)
              this.$message && this.$message.warning('死亡率统计加载失败')
              return null
            }),
          healthOnly
            ? Promise.resolve(null)
            : withTimeout(getZkDeathSurvivalRatePage({ pageNo: 1, pageSize: 500 }), 12000, null).catch((e) => {
              console.error('[健康状态] 死亡台账加载失败', e)
              return null
            }),
          // 与精准投喂同一鱼池主档
          withTimeout(getPrecisionFeedingPonds(), 12000, null).catch((e) => {
            console.error('[健康状态] 投喂鱼池主档加载失败', e)
            return null
          }),
          healthOnly
            ? Promise.resolve(null)
            : withTimeout(getZkPondFishDetailPage({ pageNo: 1, pageSize: 200, addType: '-2' }), 12000, null).catch(() => null)
        ])

        const feedingPonds = dedupePondsById((feedingPondsRes && feedingPondsRes.data) || [])
        if (feedingPonds.length) {
          this.pondList = feedingPonds.map((p) => ({
            id: p.id,
            name: p.pondName || p.pondDetailNum || String(p.id),
            pondName: p.pondName,
            pondDetailNum: p.pondDetailNum,
            workshopId: p.workshopId,
            workshopName: p.workshopName,
            pondSystemName: p.pondSystemName || p.pondNum,
            pondNum: p.pondNum,
            fishName: p.fishTypeName,
            batchNumber: p.batchNumber,
            fishSpec: p.fishSpec
          }))
        }

        if (healthDistRes && healthDistRes.data) {
          this.healthStatus = healthDistRes.data
          if (healthDistRes.data.deathWarnDailyCount != null) {
            this.deathWarnThreshold = Number(healthDistRes.data.deathWarnDailyCount) || this.deathWarnThreshold
          }
        }

        if (!healthOnly) {
          const todayDeathList = (todayDeathRes && todayDeathRes.data && todayDeathRes.data.list) || []
          if (mortalityRes && mortalityRes.data) {
            this.topStats = mortalityRes.data.topStats || {}
            this.trendStats = mortalityRes.data.trendStats || {}
            const businessList = (deathPageRes && deathPageRes.data && deathPageRes.data.list) || []
            this.mortalityData = this.mergeMortalityWithMasterPonds(
              feedingPonds,
              this.mergeMortalityWithTodayDeathDetails(
                this.mergeMortalityWithBusinessRecords(
                  mortalityRes.data.poolStatsList || [],
                  businessList
                ),
                todayDeathList
              )
            )
            this.lastUpdateTime = new Date().toLocaleTimeString('zh-CN')
          } else {
            const businessList = (deathPageRes && deathPageRes.data && deathPageRes.data.list) || []
            if (businessList.length || todayDeathList.length) {
              this.mortalityData = this.mergeMortalityWithMasterPonds(
                feedingPonds,
                this.mergeMortalityWithTodayDeathDetails(
                  this.mergeMortalityWithBusinessRecords([], businessList),
                  todayDeathList
                )
              )
            } else if (feedingPonds.length) {
              this.mortalityData = this.mergeMortalityWithMasterPonds(feedingPonds, [])
            }
            this.lastUpdateTime = new Date().toLocaleTimeString('zh-CN')
          }
        } else {
          this.lastUpdateTime = new Date().toLocaleTimeString('zh-CN')
        }

        if (healthListRes && healthListRes.data) {
          const rawHealth = healthListRes.data
          const master = feedingPonds.length
            ? feedingPonds
            : (this.pondList || [])
          const merged = master.length
            ? mergeMasterPondsWithHealth(master, rawHealth)
            : rawHealth
          this.fishPoolData = this.applyHealthThresholdWeights(merged.map((item) => {
            if (item._pendingHealth) return item
            try {
              const envObj = parseLayerInfo(item.environmentalLayerInformation)
              if (envObj) item.environmentalLayerInformationObj = envObj
              const behObj = parseLayerInfo(item.behavioralLayerInformation)
              if (behObj) {
                item.behavioralLayerInformationObj = behObj
                if (behObj.deathWarnThreshold != null) {
                  this.deathWarnThreshold = Number(behObj.deathWarnThreshold) || this.deathWarnThreshold
                }
              }
              const bodyObj = parseLayerInfo(item.bodyLayerInformation)
              if (bodyObj) item.bodyLayerInformationObj = bodyObj
            } catch (e) {
              console.error('JSON 解析失败', e)
            }
            return item
          }))
        } else if (feedingPonds.length) {
          this.fishPoolData = mergeMasterPondsWithHealth(feedingPonds, [])
        }

        // 先结束 loading，再画图；鱼池档案后台补全筛选下拉与池名
        this.pageLoading = false
        this.$nextTick(() => {
          this.updateCharts()
          if (this.attentionHealthPools && this.attentionHealthPools.length) {
            this.pushAttentionHealthAlarms({ silent: true }).catch((err) => {
              console.error('[健康状态] 关注级报警同步失败', err)
            })
          }
        })

        this.loadPondArchiveInBackground()
      } finally {
        this.pageLoading = false
        this._refreshing = false
      }
    },
    async loadPondArchiveInBackground() {
      try {
        const list = await ensurePondArchiveList()
        if (this._isDestroyed) return
        if (!list || !list.length) return
        if (!(this.pondList && this.pondList.length)) {
          this.pondList = list
        } else {
          // 保留投喂主档集合，仅补全档案里的名称字段
          const byId = new Map(list.map((p) => [String(p.id), p]))
          this.pondList = this.pondList.map((p) => {
            const hit = byId.get(String(p.id))
            if (!hit) return p
            return Object.assign({}, p, {
              pondName: p.pondName || hit.pondName || hit.name,
              name: p.name || hit.pondName || hit.name,
              workshopId: p.workshopId != null ? p.workshopId : hit.workshopId,
              workshopName: p.workshopName || hit.workshopName,
              pondSystemName: p.pondSystemName || hit.pondSystemName || hit.pondNum,
              pondDetailNum: p.pondDetailNum || hit.pondDetailNum,
              batchNumber: p.batchNumber || hit.batchNumber || '',
              fishSpec: p.fishSpec != null ? p.fishSpec : hit.fishSpec
            })
          })
          // 投喂主档未带批次时，把档案批次回填到健康/死亡列表，供下拉与筛选使用
          const fillBatch = (rows) => (rows || []).map((row) => {
            if (!row || row.pondId == null) return row
            if (row.batchNumber) return row
            const hit = byId.get(String(row.pondId))
            if (!hit || !hit.batchNumber) return row
            return Object.assign({}, row, { batchNumber: hit.batchNumber })
          })
          this.fishPoolData = fillBatch(this.fishPoolData)
          this.mortalityData = fillBatch(this.mortalityData)
        }
        this.$nextTick(() => {
          if (!this._isDestroyed) {
            this.updateCharts()
          }
        })
      } catch (e) {
        console.error('[健康状态] 鱼池列表加载失败', e)
        if (!(this.pondList && this.pondList.length)) {
          this.$message && this.$message.warning('鱼池名称映射加载失败')
        }
      }
    },
    getPondName(pondId) {
      if (!pondId) return '未知鱼池';
      const pond = this.pondList.find(p => String(p.id) === String(pondId));
      return pond ? (pond.pondName || pond.name || pond.pondDetailNum) : `鱼池 ${pondId}`;
    },
    formatScore(val) {
      if (val == null || val === '') return '—'
      const n = Number(val)
      if (Number.isNaN(n)) return val
      return Number.isInteger(n) ? String(n) : n.toFixed(1)
    },
    /** 水质等数值去掉多余小数，避免把布局撑宽 */
    formatMetric(val, digits = 3) {
      if (val == null || val === '') return '—'
      const n = Number(val)
      if (Number.isNaN(n)) return val
      const d = Math.max(0, Number(digits) || 0)
      const fixed = n.toFixed(d)
      return fixed.replace(/\.?0+$/, '') || '0'
    },
    /** 与阈值「健康评分」及图例一致 */
    scoreGradeColor(val) {
      if (val == null || val === '') return '#94a3b8'
      const s = Number(val) || 0
      const { excellentMin, watchMin } = this.healthGrade
      if (s >= excellentMin) return '#4ade80'
      if (s >= watchMin) return '#facc15'
      return '#f87171'
    },
    scoreGradeLabel(val) {
      if (val == null || val === '') return '待评估'
      const s = Number(val) || 0
      const { excellentMin, watchMin } = this.healthGrade
      if (s >= excellentMin) return '优秀'
      if (s >= watchMin) return '良好'
      return '关注'
    },
    scoreSectionStyle(val) {
      const c = this.scoreGradeColor(val)
      // hex -> rgba 背景/描边
      const hex = c.replace('#', '')
      const r = parseInt(hex.slice(0, 2), 16)
      const g = parseInt(hex.slice(2, 4), 16)
      const b = parseInt(hex.slice(4, 6), 16)
      return {
        backgroundColor: `rgba(${r}, ${g}, ${b}, 0.08)`,
        border: `1px solid rgba(${r}, ${g}, ${b}, 0.35)`,
        borderRadius: '8px',
        padding: '15px',
        marginBottom: '20px'
      }
    },
    itemScoreText(infoObj, key, full) {
      if (!infoObj || !infoObj.itemScores) return ''
      const s = infoObj.itemScores[key]
      if (s == null || s === true) return ''
      return `(${s}/${full})`
    },
    metricClass(infoObj, key) {
      if (!infoObj || !infoObj.itemScores) return 'text-green'
      const s = infoObj.itemScores[key]
      const fullMap = { doOxygen: 4, nitrite: 4, ammoniaNitrogen: 3, phPh: 3, doTemp: 3, nitrate: 3 }
      const full = fullMap[key] || 0
      if (s == null) return 'text-green'
      return Number(s) >= full ? 'text-green' : 'text-red'
    },
    statusTextClass(val) {
      if (!val || val === '—' || val === '正常' || val === '无') return 'text-green'
      return 'text-red'
    },
    behaviorRows(info) {
      if (!info) return []
      const warn = info.deathWarnThreshold != null ? info.deathWarnThreshold : this.deathWarnThreshold
      const defs = [
        { key: 'foodIntakeAmount', label: '摄食量', full: 15, legacy: 'foodSituation' },
        { key: 'foodIntakeDegree', label: '摄食程度', full: 10, legacy: 'foodSituation' },
        { key: 'bodyShape', label: '体型', full: 5 },
        { key: 'bodyColor', label: '体色', full: 5, legacy: 'bodyColorMucus' },
        { key: 'injury', label: '损伤', full: 5 },
        { key: 'swimming', label: '游泳情况', full: 10, legacy: 'swimmingState' },
        { key: 'death', label: '死亡情况', full: 10, legacy: 'deadFishSituation' }
      ]
      return defs.map(d => {
        let value = info[d.key] || info[d.legacy] || '—'
        if (d.key === 'death' && info.todayDeathCount != null) {
          const n = Number(info.todayDeathCount) || 0
          value = n <= 0 ? '无' : `今日死亡${n}尾（阈值${warn}）`
        }
        const score = info.itemScores ? info.itemScores[d.key] : null
        const ok = value === '正常' || value === '无' || value === '强' || value === '良好'
          || (typeof value === 'string' && value.indexOf('今日死亡0') === 0)
          || (score != null && Number(score) >= d.full)
        return {
          key: d.key,
          label: d.label,
          value,
          ok: d.key === 'death' && info.todayDeathCount != null
            ? Number(info.todayDeathCount) < Number(warn)
            : ok,
          scoreText: score != null ? `(${score}/${d.full})` : ''
        }
      })
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
    },
    initCharts() {
      if (this.$refs.mortalityLineChart) this.charts.mortalityLine = echarts.init(this.$refs.mortalityLineChart);
      if (this.$refs.mortalityBarChart) this.charts.mortalityBar = echarts.init(this.$refs.mortalityBarChart);
      this.updateCharts();
    },
    resizeCharts() {
      Object.values(this.charts).forEach(chart => chart && chart.resize());
    },
    updateCharts() {
      if (this.activeTab === 'mortality') {
        this.updateMortalityCharts();
      }
    },
    updateMortalityCharts() {
      if (!this.charts.mortalityLine || !this.charts.mortalityBar) return;

      // Line chart
      const trendData = [
        { day: "7天前", deaths: this.trendStats.deathCountSevenDaysAgo || 0, rate: this.trendStats.mortalityRateSevenDaysAgo || 0 },
        { day: "6天前", deaths: this.trendStats.deathCountSixDaysAgo || 0, rate: this.trendStats.mortalityRateSixDaysAgo || 0 },
        { day: "5天前", deaths: this.trendStats.deathCountFiveDaysAgo || 0, rate: this.trendStats.mortalityRateFiveDaysAgo || 0 },
        { day: "4天前", deaths: this.trendStats.deathCountFourDaysAgo || 0, rate: this.trendStats.mortalityRateFourDaysAgo || 0 },
        { day: "3天前", deaths: this.trendStats.deathCountThreeDaysAgo || 0, rate: this.trendStats.mortalityRateThreeDaysAgo || 0 },
        { day: "2天前", deaths: this.trendStats.deathCountTwoDaysAgo || 0, rate: this.trendStats.mortalityRateTwoDaysAgo || 0 },
        { day: "昨日", deaths: this.trendStats.deathCountYesterday || 0, rate: this.trendStats.mortalityRateYesterday || 0 },
      ];

      this.charts.mortalityLine.setOption({
        tooltip: { trigger: 'axis' },
        legend: { bottom: 0, textStyle: { color: '#9ca3af' } },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
        xAxis: { type: 'category', data: trendData.map(d => d.day), axisLabel: { color: '#9ca3af' } },
        yAxis: [
          { type: 'value', name: '死亡数', axisLabel: { color: '#9ca3af' }, nameTextStyle: { color: '#9ca3af' }, splitLine: { lineStyle: { color: '#1f2937' } } },
          { type: 'value', name: '死亡率(%)', axisLabel: { color: '#9ca3af' }, nameTextStyle: { color: '#9ca3af' }, splitLine: { show: false } }
        ],
        series: [
          { name: '死亡数', type: 'line', data: trendData.map(d => d.deaths), itemStyle: { color: '#f87171' }, smooth: true },
          { name: '死亡率', type: 'line', yAxisIndex: 1, data: trendData.map(d => d.rate), itemStyle: { color: '#fb923c' }, smooth: true }
        ]
      });

      // Bar chart（跟随筛选）
      const barNames = this.filteredMortalityData.map(p => p.displayLabel || p.pondName);
      const todayData = this.filteredMortalityData.map(p => p.dayDead || 0);
      const weekData = this.filteredMortalityData.map(p => p.weekDead || 0);
      const totalData = this.filteredMortalityData.map(p => Number(p.deathQuantity) || 0);

      this.charts.mortalityBar.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { bottom: 0, textStyle: { color: '#9ca3af' } },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
        xAxis: { type: 'category', data: barNames, axisLabel: { color: '#9ca3af' } },
        yAxis: { type: 'value', axisLabel: { color: '#9ca3af' }, splitLine: { lineStyle: { color: '#1f2937' } } },
        series: [
          { name: '今日', type: 'bar', data: todayData, itemStyle: { color: '#f87171' } },
          { name: '本周', type: 'bar', data: weekData, itemStyle: { color: '#fb923c' } },
          { name: '累计', type: 'bar', data: totalData, itemStyle: { color: '#fbbf24' } }
        ]
      });
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.el-tabs__header){
  display: none;
}
.system-container {
  position: relative;
  min-height: 100vh;
  background: #0b0f19; /* 深色背景 */
  color: #e5e7eb;
}

.header {
  background-color: #111827;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.5);
  border-bottom: 1px solid #1f2937;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.icon-wrapper {
  background-color: #3b82f6;
  padding: 0.5rem;
  border-radius: 0.5rem;
  color: white;
  font-size: 1.5rem;
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #f3f4f6;
  margin: 0;
}

.subtitle {
  font-size: 0.875rem;
  color: #9ca3af;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.last-update {
  font-size: 0.875rem;
  color: #9ca3af;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.score-legend-card .legend-block {
  padding: 8px 10px;
  border-radius: 8px;
  background: rgba(31, 41, 55, 0.6);
  min-height: 72px;
}
.score-legend-card .legend-title {
  font-weight: 700;
  margin-bottom: 4px;
}
.score-legend-card .legend-desc {
  margin: 0;
  font-size: 12px;
  color: #9ca3af;
  line-height: 1.4;
}
.mb-4 {
  margin-bottom: 1rem;
}

/* 健康状态分布 + AI 健康分析（与设计图一致） */
.health-overview-row {
  display: flex;
  flex-wrap: wrap;
  align-items: stretch;
}
.health-overview-col {
  display: flex;
}
.health-overview-card {
  width: 100%;
  display: flex;
  flex-direction: column;
}
.health-overview-card ::v-deep .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px 20px 20px;
}
.health-overview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.ai-health-time {
  font-size: 12px;
  color: #9ca3af;
  font-weight: normal;
}
.health-dist-panel,
.ai-health-panel {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  max-height: 420px;
  overflow-x: hidden;
  overflow-y: auto;
}
.health-dist-panel {
  gap: 12px;
}
.health-dist-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-radius: 10px;
  min-height: 52px;
  box-sizing: border-box;
}
.health-dist-row--excellent {
  background: rgba(34, 197, 94, 0.14);
}
.health-dist-row--watch {
  background: rgba(234, 179, 8, 0.16);
}
.health-dist-row--intervene {
  background: rgba(239, 68, 68, 0.12);
}
.health-dist-label {
  display: inline-flex;
  align-items: center;
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}
.health-dist-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  margin-right: 8px;
  flex-shrink: 0;
}
.health-dist-row--excellent .health-dist-dot { background: #22c55e; }
.health-dist-row--watch .health-dist-dot { background: #eab308; }
.health-dist-row--intervene .health-dist-dot { background: #ef4444; }
.health-dist-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1;
}
.health-dist-row--excellent .health-dist-value { color: #16a34a; }
.health-dist-row--watch .health-dist-value { color: #ca8a04; }
.health-dist-row--intervene .health-dist-value { color: #dc2626; }
.health-dist-total {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 4px 0;
  border-top: 1px solid rgba(156, 163, 175, 0.3);
  min-height: 44px;
}
.health-dist-total-label {
  font-size: 14px;
  color: #6b7280;
}
.health-dist-total-value {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}
.health-dist-pending {
  margin-top: 8px;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.4;
}
.ai-health-panel {
  gap: 12px;
}
.ai-health-item {
  padding: 14px 16px;
  border-radius: 10px;
  border-left: 4px solid transparent;
}
.ai-health-item--high {
  background: rgba(239, 68, 68, 0.1);
  border-left-color: #ef4444;
}
.ai-health-item--medium {
  background: rgba(249, 115, 22, 0.1);
  border-left-color: #f97316;
}
.ai-health-item--normal {
  background: rgba(34, 197, 94, 0.1);
  border-left-color: #22c55e;
}
.ai-health-item-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.ai-health-item-head i {
  font-size: 16px;
}
.ai-health-item--high .ai-health-item-head i { color: #ef4444; }
.ai-health-item--medium .ai-health-item-head i { color: #f97316; }
.ai-health-item--normal .ai-health-item-head i { color: #22c55e; }
.ai-health-item-title {
  flex: 1;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}
.ai-health-item-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.06);
  color: #6b7280;
}
.ai-health-item--high .ai-health-item-badge {
  background: rgba(239, 68, 68, 0.15);
  color: #dc2626;
}
.ai-health-item--medium .ai-health-item-badge {
  background: rgba(249, 115, 22, 0.15);
  color: #ea580c;
}
.ai-health-item-detail {
  font-size: 13px;
  color: #374151;
  line-height: 1.5;
  word-break: break-all;
}
.ai-health-item-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
  line-height: 1.4;
}

/* 鱼类健康状态独立页：概览区文字白色（审核意见：把文字改成白色） */
.health-page-light.health-overview-row ::v-deep .health-overview-card.el-card {
  background: rgba(15, 23, 42, 0.72);
  border-color: rgba(148, 163, 184, 0.25);
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.2);
}
.health-page-light.health-overview-row ::v-deep .health-overview-card .el-card__header {
  background: transparent;
  border-bottom-color: rgba(148, 163, 184, 0.2);
  color: #ffffff;
  font-weight: 600;
}
.health-page-light .health-dist-label,
.health-page-light .health-dist-total-label {
  color: #ffffff;
}
.health-page-light .health-dist-total-value {
  color: #ffffff;
}
.health-page-light .ai-health-item-title {
  color: #ffffff;
}
.health-page-light .ai-health-item-detail {
  color: #f3f4f6;
}
.health-page-light .ai-health-item-hint {
  color: #e5e7eb;
}
.health-page-light .ai-health-time {
  color: #e5e7eb;
}

/* 鱼类健康状态独立页：概览区浅色卡片（贴近设计图） — 已由上方深色卡片+白字替代 */

/* 深色主题下概览区文字可读性 */
.health-overview-row:not(.health-page-light) .health-dist-label {
  color: #e5e7eb;
}
.health-overview-row:not(.health-page-light) .health-dist-total-label {
  color: #9ca3af;
}
.health-overview-row:not(.health-page-light) .health-dist-total-value {
  color: #f3f4f6;
}
.health-overview-row:not(.health-page-light) .ai-health-item-title {
  color: #f3f4f6;
}
.health-overview-row:not(.health-page-light) .ai-health-item-detail {
  color: #d1d5db;
}
.health-overview-row:not(.health-page-light) .ai-health-item-hint {
  color: #9ca3af;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.stat-item .label {
  font-size: 0.875rem;
  color: #9ca3af;
}

.stat-item .value {
  font-size: 1.25rem;
  font-weight: bold;
  color: #f3f4f6;
}

.divider {
  border-top: 1px solid #1f2937;
  margin: 1rem 0;
}

/* 调整深色模式下的颜色 */
.text-green { color: #4ade80 !important; }
.text-yellow { color: #facc15 !important; }
.text-red { color: #f87171 !important; }
.text-blue { color: #60a5fa !important; }
.text-orange { color: #fb923c !important; }
.text-amber { color: #fbbf24 !important; }

.chart-placeholder, .chart-container {
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
  color: #6b7280;
  border-radius: 4px;
}
.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #f3f4f6;
  margin: 1.5rem 0 1rem 0;
}

.pool-detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
  gap: 20px;
  align-items: stretch;
  margin-bottom: 1rem;
}

.pool-detail-cell {
  display: flex;
  min-width: 0;
}

.pool-card {
  width: 100%;
  min-height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pool-card ::v-deep .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.pool-card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.metric-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

/* 奇数个行为项时，最后一项铺满整行，避免右侧空格子 */
.metric-grid > .status-box:last-child:nth-child(odd) {
  grid-column: 1 / -1;
}

.exception-summary {
  margin-top: auto;
  background-color: rgba(248, 113, 113, 0.1);
  border: 1px solid rgba(248, 113, 113, 0.3);
  border-radius: 6px;
  padding: 12px;
  flex-shrink: 0;
}

.exception-summary-title {
  color: #f87171;
  font-weight: bold;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.exception-summary-body {
  color: #fca5a5;
  font-size: 0.875rem;
  line-height: 1.5;
  white-space: pre-line;
  max-height: 4.8em;
  overflow-y: auto;
}

.pool-name {
  font-weight: bold;
  color: #f3f4f6;
}

.param-box {
  background-color: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.param-label {
  color: #9ca3af;
  font-size: 0.875rem;
}
.temp-range-hint {
  margin-left: 6px;
  font-size: 11px;
  color: #64748b;
  font-weight: 400;
}

.param-value {
  font-weight: bold;
  font-size: 1.1rem;
  word-break: break-all;
}

.status-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  background-color: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 8px 12px;
  min-width: 0;
}

.status-label {
  color: #d1d5db;
  font-size: 0.875rem;
  min-width: 0;
}

.list-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background-color: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  color: #d1d5db;
  font-size: 0.875rem;
}

.list-item > span:first-child {
  flex: 1;
  min-width: 0;
}

.list-item > span:last-child {
  flex-shrink: 0;
  max-width: 45%;
  text-align: right;
}

.pool-stats .stat-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid #1f2937;
  color: #d1d5db;
}

.pool-stats .stat-row:last-child {
  border-bottom: none;
}

.stat-title {
  font-size: 0.875rem;
  font-weight: 500;
  color: #9ca3af;
  margin-bottom: 0.5rem;
}

.stat-value {
  font-size: 1.875rem;
  font-weight: bold;
  color: #f3f4f6;
}

.stat-value .unit {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: normal;
}

/* Element UI 深色主题覆盖 */
::v-deep .el-card {
  background-color: #111827;
  border: 1px solid #1f2937;
  color: #e5e7eb;
}

::v-deep .el-card__header {
  border-bottom: 1px solid #1f2937;
  color: #f3f4f6;
}

::v-deep .el-tabs__item {
  color: #9ca3af;
}

::v-deep .el-tabs__item.is-active {
  color: #3b82f6;
}

::v-deep .el-tabs--card > .el-tabs__header .el-tabs__item {
  border-bottom: 1px solid #1f2937;
  border-left: 1px solid #1f2937;
}

::v-deep .el-tabs--card > .el-tabs__header {
  border-bottom: 1px solid #1f2937;
}

::v-deep .el-tabs--card > .el-tabs__header .el-tabs__nav {
  border: 1px solid #1f2937;
  border-bottom: none;
  border-radius: 4px 4px 0 0;
}

::v-deep .el-button {
  background-color: #1f2937;
  border-color: #374151;
  color: #e5e7eb;
}

::v-deep .el-button:hover,
::v-deep .el-button:focus {
  background-color: #374151;
  border-color: #4b5563;
  color: #ffffff;
}

::v-deep .el-tag--success {
  background-color: rgba(74, 222, 128, 0.1);
  border-color: rgba(74, 222, 128, 0.2);
  color: #4ade80;
}

::v-deep .el-tag--warning {
  background-color: rgba(250, 204, 21, 0.1);
  border-color: rgba(250, 204, 21, 0.2);
  color: #facc15;
}

::v-deep .el-tag--danger {
  background-color: rgba(248, 113, 113, 0.1);
  border-color: rgba(248, 113, 113, 0.2);
  color: #f87171;
}
::v-deep .el-progress-bar__outer {
  background-color: #1f2937;
}

::v-deep .el-divider {
  background-color: #1f2937;
  margin: 16px 0;
}
</style>
