<template>
  <el-container>
    <el-header class="header-container">
      <div class="header-buttons">
        <el-button @click="goToDaPing4" class="header-btn btn-blue" type="primary">
          <i class="el-icon-box"></i>
          <span>存量分布</span>
        </el-button>
        <el-button @click="goToInspectionList" class="header-btn btn-purple" type="primary">
          <i class="el-icon-view"></i>
          <span>智能巡检系统</span>
        </el-button>
        <el-button @click="goToRespectionReport" class="header-btn btn-pink" type="primary">
          <i class="el-icon-document"></i>
          <span>智能分析报告</span>
        </el-button>
        <el-button @click="goToIntelligentDiagnosis" class="header-btn btn-red" type="primary">
          <i class="el-icon-medicine-box"></i>
          <span>鱼病诊断</span>
        </el-button>
        <el-button class="header-btn btn-green" type="primary" @click.stop="toExpertMeeting">
          <i class="el-icon-truck"></i>
          <span>远程专家与智能会议系统</span>
        </el-button>
      </div>
    </el-header>
    <el-container>
      <el-main>
        <el-row class="monitoring-row">
          <el-col :span="12" class="list-col">
            <div id="a1" class="alert">
              <div style="display:flex; justify-content:space-between; align-items:center; position:relative;">
                <h1 @click="goToZkDevice" @dblclick="showTestButton = !showTestButton">系统设备监测</h1>
                <el-button 
                  v-show="showTestButton"
                  type="danger" 
                  size="small" 
                  @click="testAlarmDialog"
                  class="test-alarm-button"
                  @blur="showTestButton = false"
                >测试告警</el-button>
              </div>
              <div class="scroll-list">
                <div class="list-header">
                  <span>车间</span>
                  <span>系统</span>
                  <span>设备</span>
                  <span>故障</span>
                  <span>日期</span>
                  <span>责任人</span>
                </div>
                <div class="list-body" ref="listBody1">
                  <div ref="scrollContent1" class="scroll-content">
                    <div class="list-row" v-for="item in machineDeviceAlarmList" @click="goToZkDevice(item)"
                      :key="item.id" :class="{'alert-red': item.type === 'error', 'alert-yellow': item.type === 'warning', 'alert-green': item.type === 'info'}">
                      <span>{{ item.workshopName }}</span>
                      <span>{{ item.pondSystemName }}</span>
                      <span>{{ item.warnDevice }}</span>
                      <span>{{ item.warnContent }}</span>
                      <span>{{ parseTime(item.createTime) }}</span>
                      <span>
                        <span v-if="item.userName">{{ item.userName }}</span>
                        <span v-else="item.userId">
                          <userName :user-id="item.userId"></userName>
                        </span>
                        <!-- <userName :user-id="item.userId"></userName> -->
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12" class="list-col">
            <div id="a2" class="alert">
              <h1 @click="goToDeviceAlarm">鱼塘水质监测</h1>
              <div class="scroll-list">
                <div class="list-header">
                  <span>车间</span>
                  <span>系统</span>
                  <span>传感器</span>
                  <span>故障</span>
                  <span>日期</span>
                  <span>责任人</span>
                </div>
                <div class="list-body" ref="listBody2">
                  <div ref="scrollContent2" class="scroll-content">
                    <!-- deviceList.sensorList -->
                    <div class="list-row" v-for="item in waterDeviceAlarmList" @click="goToDeviceAlarm(item)"
                      :key="item.id" :class="{'alert-red': item.type === 'error', 'alert-yellow': item.type === 'warning', 'alert-green': item.type === 'info'}">
                      <span>{{ item.workshopName }}</span>
                      <span>{{ item.pondSystemName }}</span>
                      <span>{{ item.warnDevice }}</span>
                      <span>{{ item.warnContent }}</span>
                      <span>{{ parseTime(item.createTime) }}</span>
                      <span>
                        <span v-if="item.userName">{{ item.userName }}</span>
                        <span v-else="item.userId">
                          <userName :user-id="item.userId"></userName>
                        </span>
                        <!-- <userName :user-id="item.userId"></userName> -->
                      </span>
                    </div>
                  </div>
                </div>
              </div>            
            </div>
          </el-col>
        </el-row>
        <el-row class="row-moudle">
          <el-col :span="8" class="col-moudle">
            <div id="m1" class="moudle" @click="goToBioAsset" style="cursor:pointer;">
              <h1 style="color:#409EFF;">生物资产评估</h1>
              <barChart :chartData="barChartData1" :chartOptions="barChartOptions1" />
            </div>
          </el-col>
          <el-col :span="8" class="col-moudle">
            <div id="m2" class="moudle" @click="goToHealthStatus" style="cursor:pointer;">
              <h1 style="color:#409EFF;">健康状态评估</h1>
              <barChart :chartData="barChartData2" :chartOptions="barChartOptions2" />
            </div>
          </el-col>
          <el-col :span="8" class="col-moudle">
            <div id="m3" class="moudle" @click="goToDeathSurvivalRate" style="cursor:pointer;">
              <h1 style="color:#409EFF;">死亡量存活率</h1>
              <barChart :chartData="barChartData3" :chartOptions="barChartOptions3" />
            </div>
          </el-col>
        </el-row>
        <el-row class="row-moudle" style="padding-top: 30px;">
          <el-col :span="8" class="col-moudle">
            <div id="m4" class="moudle" @click="goToPrecisionFeeding" style="cursor:pointer;">
              <h1 style="color:#409EFF;">精准投喂系统</h1>
              <barChart :chartData="barChartData4" :chartOptions="barChartOptions4" />
            </div>
          </el-col>
          <el-col :span="8" class="col-moudle">
            <div id="m5" class="moudle" @click="goToGrowthModel" style="cursor:pointer;">
              <h1 style="color:#409EFF;">鱼生长大模型</h1>
              <lineChart :chartData="lineChartData1" :chartOptions="lineChartOptions1" />
            </div>
          </el-col>
          <el-col :span="8" class="col-moudle">
            <div id="m6" class="moudle" @click="goToFeedConversionRate" style="cursor:pointer;">
              <h1 style="color:#409EFF;">饵料系数</h1>
              <barChart :chartData="barChartData6" :chartOptions="barChartOptions6" />
            </div>
          </el-col>
        </el-row>
        <el-row>
          <div class="video-title">
            <router-link to="/device/zk-device-video">
              <i class="el-icon-video-camera"></i>
              <h1>视频监控</h1>
            </router-link>
          </div>
          <el-col :span="8" class="col-monitor">
            <div id="video1" class="video">
              <div class="video-wrapper">
                <video 
                  style="width: 100%;height: 100%;"
                  src="https://kede-public1.oss-cn-beijing.aliyuncs.com/server/afb20129d31c4ef8afe53e12d1d67bb4_fishfamer3.mp4" type="video/mp4" controls muted autoplay loop>
                  <p>你的浏览器不支持 HTML5 视频，请下载<a href="https://kede-public1.oss-cn-beijing.aliyuncs.com/server/afb20129d31c4ef8afe53e12d1d67bb4_fishfamer3.mp4">视频文件</a></p>
                </video>
                <div class="live-badge">• LIVE</div>
              </div>
              <div class="video-info">
                <div class="video-info-main">
                  <h2 class="video-name">池塘-1 摄像头</h2>
                  <p class="video-subtitle">一号养殖池</p>
                </div>
                <div class="video-status">
                  <span class="status-dot"></span>
                  <span class="status-text">在线</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="8" class="col-monitor">
            <div id="video2" class="video">
              <div class="video-wrapper">
                <video 
                  style="width: 100%;height: 100%;"
                  src="https://kede-public1.oss-cn-beijing.aliyuncs.com/server/7a51e8115b2b498fabca8389b96934c9_fishfamer2.mp4" type="video/mp4" controls muted autoplay loop>
                  <p>你的浏览器不支持 HTML5 视频，请下载<a href="https://kede-public1.oss-cn-beijing.aliyuncs.com/server/7a51e8115b2b498fabca8389b96934c9_fishfamer2.mp4">视频文件</a></p>
                </video>
                <div class="live-badge">• LIVE</div>
              </div>
              <div class="video-info">
                <div class="video-info-main">
                  <h2 class="video-name">池塘-2 摄像头</h2>
                  <p class="video-subtitle">二号养殖池</p>
                </div>
                <div class="video-status">
                  <span class="status-dot"></span>
                  <span class="status-text">在线</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="8" class="col-monitor">
            <div id="video3" class="video">
              <div class="video-wrapper">
                <video 
                  style="width: 100%;height: 100%;"
                  src="https://kede-public1.oss-cn-beijing.aliyuncs.com/server/0c7f55c78ea64672b083eba3d0e304f2_fishfamer1.mp4" type="video/mp4" controls muted autoplay loop>
                  <p>你的浏览器不支持 HTML5 视频，请下载<a href="https://kede-public1.oss-cn-beijing.aliyuncs.com/server/0c7f55c78ea64672b083eba3d0e304f2_fishfamer1.mp4">视频文件</a></p>
                </video>
                <div class="live-badge">• LIVE</div>
              </div>
              <div class="video-info">
                <div class="video-info-main">
                  <h2 class="video-name">池塘-3 摄像头</h2>
                  <p class="video-subtitle">三号养殖池</p>
                </div>
                <div class="video-status">
                  <span class="status-dot"></span>
                  <span class="status-text">在线</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-main>
      <el-aside width="550px">
        <el-row >
          <div class="plan-box">
            <h1 class="section-title" @click="goToWorkPlan">
              <i class="el-icon-aim"></i>
              <span>目标与计划</span>
            </h1>
            <div class="plan-row plan-day" @click="goToWorkPlan">
              <div class="plan-label">
                <i class="el-icon-calendar"></i>
                <span>日计划</span>
              </div>
              <div class="plan-content">
                <el-input type="textarea" :value="planText.daylyPlan" readonly resize="none" rows="2" class="plan-textarea" />
              </div>
            </div>
            <div class="plan-row plan-week" @click="goToWorkPlan">
              <div class="plan-label">
                <i class="el-icon-flag"></i>
                <span>周计划</span>
              </div>
              <div class="plan-content">
                <el-input type="textarea" :value="planText.weeklyPlan" readonly resize="none" rows="2" class="plan-textarea" />
              </div>
            </div>
            <div class="plan-row plan-month" @click="goToWorkPlan">
              <div class="plan-label">
                <i class="el-icon-date"></i>
                <span>月度</span>
              </div>
              <div class="plan-content">
                <el-input type="textarea" :value="planText.monthlyGoal" readonly resize="none" rows="2" class="plan-textarea" />
              </div>
            </div>
            <div class="plan-row plan-quarter" @click="goToWorkPlan">
              <div class="plan-label">
                <i class="el-icon-user"></i>
                <span>季度</span>
              </div>
              <div class="plan-content">
                <el-input type="textarea" :value="planText.quarterlyGoal" readonly resize="none" rows="2" class="plan-textarea" />
              </div>
            </div>
            <div class="plan-row plan-year" @click="goToWorkPlan">
              <div class="plan-label">
                <i class="el-icon-aim"></i>
                <span>年度</span>
              </div>
              <div class="plan-content">
                <el-input type="textarea" :value="planText.annualGoal" readonly resize="none" rows="2" class="plan-textarea" />
              </div>
            </div>
          </div>
        </el-row>
        <div><h1 @click="goToOverallCost" class="cost-section-title">
          <i class="el-icon-time"></i>
          <span>全场成本</span>
        </h1></div>
        <el-row class="cost-row" width="100%">
          <el-col :span="24">     
            <pieChart ref="pieChartComponent" :chartData="pieChartData1" :chartOptions="pieChartOptions1" />    
          </el-col> 
        </el-row>
      </el-aside>      
    </el-container>
    
    <!-- 设备告警弹窗 - 增强版 -->
    <el-dialog
      :title="'设备告警通知 - ' + (currentAlarm && currentAlarm.type ? 
        (currentAlarm.type === 'error' ? '严重' : currentAlarm.type === 'warning' ? '警告' : '提示') : '普通')"
      :visible.sync="alarmDialogVisible"
      width="500px"
      center
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="closeAlarmDialog"
      class="compact-alarm-dialog"
    >
      <div class="alarm-message-enhanced compact-layout">
        <!-- 告警级别指示器 -->
        <div class="alarm-level-indicator" :class="'level-' + (currentAlarm?.type || 'info')">
          <i class="el-icon-warning"></i>
          <span class="level-text">{{ currentAlarm && currentAlarm.type ? 
            (currentAlarm.type === 'error' ? '严重' : currentAlarm.type === 'warning' ? '警告' : '提示') : '普通' }}</span>
        </div>
        
        <!-- 主要告警信息 - 紧凑布局 -->
        <div class="alarm-main-info">
          <div class="alarm-item primary">
            <strong>设备:</strong> {{ currentAlarm?.device || currentAlarm?.sensor || '未知设备' }}
          </div>
          <div class="alarm-item">
            <strong>告警内容:</strong> {{ currentAlarm?.fault || '未知告警内容' }}
          </div>
        </div>
        
        <!-- 详细信息网格布局 -->
        <div class="alarm-details-grid">
          <div class="detail-item">
            <span class="detail-label">位置:</span>
            <span class="detail-value">{{ currentAlarm?.workshop || '未知车间' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">系统:</span>
            <span class="detail-value">{{ currentAlarm?.system || '未知系统' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">时间:</span>
            <span class="detail-value">{{ currentAlarm?.date || new Date().toLocaleString('zh-CN') }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">责任人:</span>
            <span class="detail-value">{{ currentAlarm?.responsible || '系统' }}</span>
          </div>
        </div>
        
        <!-- 简化的原始数据查看 -->
        <div v-if="currentAlarm?.originalData" class="original-data-toggle">
          <el-button 
            size="mini" 
            type="text" 
            @click="showOriginalData = !showOriginalData"
            class="original-data-btn"
          >
            {{ showOriginalData ? '隐藏详情' : '查看详情' }}
          </el-button>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer compact-footer">
        <el-button size="small" @click="closeAlarmDialog">忽略</el-button>
        <el-button size="small" type="primary" @click="handleAlarm(currentAlarm)">处理告警</el-button>
      </div>
    </el-dialog>

    <!-- 告警弹窗 - 已替换为增强版 -->
    <!-- <el-dialog v-model="alarmDialogVisible" title="设备告警" width="520px" @close="onAlarmDialogClose">
      <div v-if="currentAlarm">
        <p><strong>设备:</strong> {{ currentAlarm.device || currentAlarm.sensor || currentAlarm.name || '-' }}</p>
        <p><strong>故障:</strong> {{ currentAlarm.fault || currentAlarm.message || currentAlarm.msg || '-' }}</p>
        <p><strong>位置:</strong> {{ currentAlarm.workshop || currentAlarm.location || '-' }}</p>
        <p><strong>时间:</strong> {{ currentAlarm.date || currentAlarm.time || currentAlarm.createTime || '-' }}</p>
        <el-collapse>
          <el-collapse-item title="原始数据" name="1">
            <pre style="white-space:pre-wrap">{{ JSON.stringify(currentAlarm.original || currentAlarm, null, 2) }}</pre>
          </el-collapse-item>
        </el-collapse>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="alarmDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="acknowledgeAlarm">确认处理</el-button>
      </span>
    </el-dialog> -->
  </el-container>
</template>

<script>
import {getUserById} from "@/api/system/user";
// import {getTaskData} from "@/api/workflow/task";
import {checkRole} from "@/utils/permission";
// import {getBackStdData, getSellStdData} from "@/api/fish/orderProduct";
// import {getMyPerformance} from "@/api/fish/performance";
// Add missing import
import { createZkBiologyCapital, updateZkBiologyCapital, deleteZkBiologyCapital, getZkBiologyCapital, getZkBiologyCapitalPage, exportZkBiologyCapitalExcel } from "@/api/fish/zkBiologyCapital";
// import {getZkBiologyCapitalPage} from "@/api/fish/zkBiologyCapital";
import {mapGetters} from "vuex";
import barChart from "@/views/keyshow/showBarChart";
import lineChart from "@/views/keyshow/showLineChart";
import pieChart from "@/views/keyshow/showPieChart";
import resize from './mixins/resize'
import { createZkDeviceAlarm, updateZkDeviceAlarm, deleteZkDeviceAlarm, getZkDeviceAlarm, getZkDeviceAlarmPage, exportZkDeviceAlarmExcel } from "@/api/fish/zkDeviceAlarm";
import { createZkWorkPlan, updateZkWorkPlan, deleteZkWorkPlan, getZkWorkPlan, getZkWorkPlanPage, exportZkWorkPlanExcel } from "@/api/fish/zkWorkPlan";
import { getZkBiologyCapitalMainHome } from "@/api/fish/zkBiologyCapital";

export default {
  name: "mainhome",
  components: {
    barChart,
    lineChart,
    pieChart,
  },
  data(){
      return {
        waterDeviceAlarmList: [], // 水质监测告警
        machineDeviceAlarmList: [], // 水质监测告警
        showTestButton: false,
        deptName: '',
        planText: {
          dayPlan: '日例会,巡检,投喂,分池',
          weekPlan: '周四重要接待,日常工作',
          monthPlan: '饵料系数,死亡率,能耗,易耗品',
          quarterPlan: '生产目标,经营目标,成本目标',
          yearPlan: '生产目标,经营目标,成本目标'
        },
        // 设备告警相关
        alarmDialogVisible: false,
        alarmMessage: '',
        currentAlarm: null,
        showOriginalData: false,
        // WebSocket状态监控
        webSocketStatus: {
          connected: false,
          lastMessageTime: null,
          messageCount: 0,
          errorCount: 0
        },
      showSell: 0,
      showBack: 0,
      todoCount: 0,
      confirmCount: 0,
      purchaseCount: 0,
      outCount: 0,
      drawCount: 0,
      sendCount: 0,
      recycleCount: 0,
      monthPerformanceSellAmount: 0,
      monthPerformanceOrderCount: 0,
      weekPerformanceSellAmount: 0,
      weekPerformanceOrderCount: 0,
      deviceList: {
        equipmentlist: [
            { id: 1, workshop: '一车间', system: '供水', device: '水泵A', fault: '漏水', type: 'error', date: '2025-07-15', responsible: '张三' },
            { id: 2, workshop: '二车间', system: '通风', device: '风机B', fault: '不转', type: 'warning', date: '2025-07-15', responsible: '李四' },
            { id: 3, workshop: '三车间', system: '加热', device: '加热器C', fault: '温度异常', type: 'info', date: '2025-07-14', responsible: '王五' },
            { id: 4, workshop: '一车间', system: '供水', device: '水泵A', fault: '漏水', type: 'error', date: '2025-07-15', responsible: '赵四' },
            { id: 5, workshop: '三车间', system: '加热', device: '加热器C', fault: '温度异常', type: 'info', date: '2025-07-14', responsible: '李丁' },
            { id: 6, workshop: '二车间', system: '通风', device: '风机B', fault: '不转', type: 'warning', date: '2025-07-15', responsible: '钱塘' },
            { id: 7, workshop: '三车间', system: '加热', device: '加热器C', fault: '温度异常', type: 'info', date: '2025-07-14', responsible: '孙新' },
          ],
        sensorList: [
          { id: 1, workshop: '一车间', system: '供水', sensor: '水泵A', fault: '漏水', type: 'error', date: '2025-07-15', responsible: '张三' },
          { id: 2, workshop: '二车间', system: '通风', sensor: '风机B', fault: '不转', type: 'warning', date: '2025-07-15', responsible: '李四' },
          { id: 3, workshop: '三车间', system: '加热', sensor: '加热器C', fault: '温度异常', type: 'info', date: '2025-07-14', responsible: '王五' },
          { id: 4, workshop: '一车间', system: '供水', sensor: '水泵A', fault: '漏水', type: 'error', date: '2025-07-15', responsible: '赵四' },
          { id: 5, workshop: '三车间', system: '加热', sensor: '加热器C', fault: '温度异常', type: 'info', date: '2025-07-14', responsible: '李丁' },
          { id: 6, workshop: '二车间', system: '通风', sensor: '风机B', fault: '不转', type: 'warning', date: '2025-07-15', responsible: '钱塘' },
          { id: 7, workshop: '三车间', system: '加热', sensor: '加热器C', fault: '温度异常', type: 'info', date: '2025-07-14', responsible: '孙新' }
        ]
      },
      barChartData1: {
        xAxis: ['1', '2', '3', '4'],
        series: [25, 14, 29, 24]
      },
      barChartOptions1: {
        color: [{
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 255, 255, 1)' },
            { offset: 0.3, color: 'rgba(64, 224, 255, 0.95)' },
            { offset: 0.6, color: 'rgba(64, 158, 255, 0.85)' },
            { offset: 1, color: 'rgba(30, 100, 200, 0.7)' }
          ]
        }],
        title: { 
          text: '资产估值 (元)', 
          left: 'center', 
          top: 10, 
          textStyle: { 
            fontSize: 14,
            color: '#ffffff',
            fontWeight: 'bold'
          } 
        },
        xAxis: { 
          type: 'category',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12,
            interval: 0,
            rotate: 30
          }
        },
        yAxis: { 
          type: 'value',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.15)',
              type: 'dashed',
              width: 1
            }
          }
        },
        grid: { 
          left: 20, 
          right: 20, 
          bottom: 30, 
          top: 40,
          containLabel: true
        },
        tooltip: { 
          trigger: 'axis',
          backgroundColor: 'rgba(10, 21, 32, 0.95)',
          borderColor: 'rgba(0, 255, 255, 0.5)',
          borderWidth: 1,
          textStyle: {
            color: '#ffffff'
          },
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(0, 255, 255, 0.2)'
            }
          }
        },
        series: [{
          type: 'bar',
          barWidth: '35%',
          itemStyle: {
            borderRadius: [6, 6, 0, 0],
            shadowBlur: 15,
            shadowColor: 'rgba(0, 255, 255, 0.6)',
            shadowOffsetY: -2
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 25,
              shadowColor: 'rgba(0, 255, 255, 0.9)',
              shadowOffsetY: -4
            }
          }
        }]
      },
      barChartData2: {
        xAxis: ['批次1', '批次2', '批次3', '批次4', '批次5'],
        series: [80, 92, 90, 95, 88]
      },
      barChartOptions2: {
        color: [{
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 255, 255, 1)' },
            { offset: 0.3, color: 'rgba(64, 224, 255, 0.95)' },
            { offset: 0.6, color: 'rgba(64, 158, 255, 0.85)' },
            { offset: 1, color: 'rgba(30, 100, 200, 0.7)' }
          ]
        }],
        title: { 
          text: '健康评分', 
          left: 'center', 
          top: 10, 
          textStyle: { 
            fontSize: 14,
            color: '#ffffff',
            fontWeight: 'bold'
          } 
        },
        xAxis: { 
          type: 'category',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12,
            interval: 0,
            rotate: 30
          }
        },
        yAxis: { 
          type: 'value',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.15)',
              type: 'dashed',
              width: 1
            }
          }
        },
        grid: { 
          left: 20, 
          right: 20, 
          bottom: 30, 
          top: 40,
          containLabel: true
        },
        tooltip: { 
          trigger: 'axis',
          backgroundColor: 'rgba(10, 21, 32, 0.95)',
          borderColor: 'rgba(0, 255, 255, 0.5)',
          borderWidth: 1,
          textStyle: {
            color: '#ffffff'
          },
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(0, 255, 255, 0.2)'
            }
          }
        },
        series: [{
          type: 'bar',
          barWidth: '35%',
          itemStyle: {
            borderRadius: [6, 6, 0, 0],
            shadowBlur: 15,
            shadowColor: 'rgba(0, 255, 255, 0.6)',
            shadowOffsetY: -2
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 25,
              shadowColor: 'rgba(0, 255, 255, 0.9)',
              shadowOffsetY: -4
            }
          }
        }]
      },
      barChartData3: {
        xAxis: ['批次1', '批次2', '批次3', '批次4', '批次5'],
        series: [70, 80, 95, 90, 92]
      },
      barChartOptions3: {
        color: [{
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 255, 255, 1)' },
            { offset: 0.3, color: 'rgba(64, 224, 255, 0.95)' },
            { offset: 0.6, color: 'rgba(64, 158, 255, 0.85)' },
            { offset: 1, color: 'rgba(30, 100, 200, 0.7)' }
          ]
        }],
        title: { 
          text: '存活率百分比', 
          left: 'center', 
          top: 10, 
          textStyle: { 
            fontSize: 14,
            color: '#ffffff',
            fontWeight: 'bold'
          } 
        },
        xAxis: { 
          type: 'category',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12,
            interval: 0,
            rotate: 30
          }
        },
        yAxis: { 
          type: 'value',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.15)',
              type: 'dashed',
              width: 1
            }
          }
        },
        grid: { 
          left: 20, 
          right: 20, 
          bottom: 30, 
          top: 40,
          containLabel: true
        },
        tooltip: { 
          trigger: 'axis',
          backgroundColor: 'rgba(10, 21, 32, 0.95)',
          borderColor: 'rgba(0, 255, 255, 0.5)',
          borderWidth: 1,
          textStyle: {
            color: '#ffffff'
          },
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(0, 255, 255, 0.2)'
            }
          }
        },
        series: [{
          type: 'bar',
          barWidth: '35%',
          itemStyle: {
            borderRadius: [6, 6, 0, 0],
            shadowBlur: 15,
            shadowColor: 'rgba(0, 255, 255, 0.6)',
            shadowOffsetY: -2
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 25,
              shadowColor: 'rgba(0, 255, 255, 0.9)',
              shadowOffsetY: -4
            }
          }
        }]
      },
      barChartData4: {
        xAxis: ['当日', '平均', '总合'],
        series: [500, 480, 1200]
      },
      barChartOptions4: {
        color: [{
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 255, 255, 1)' },
            { offset: 0.3, color: 'rgba(64, 224, 255, 0.95)' },
            { offset: 0.6, color: 'rgba(64, 158, 255, 0.85)' },
            { offset: 1, color: 'rgba(30, 100, 200, 0.7)' }
          ]
        }],
        title: { 
          text: '投喂量', 
          left: 'center', 
          top: 10, 
          textStyle: { 
            fontSize: 14,
            color: '#ffffff',
            fontWeight: 'bold'
          } 
        },
        xAxis: { 
          type: 'category',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12,
            interval: 0,
            rotate: 30
          }
        },
        yAxis: { 
          type: 'value',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.15)',
              type: 'dashed',
              width: 1
            }
          }
        },
        grid: { 
          left: 20, 
          right: 20, 
          bottom: 30, 
          top: 40,
          containLabel: true
        },
        tooltip: { 
          trigger: 'axis',
          backgroundColor: 'rgba(10, 21, 32, 0.95)',
          borderColor: 'rgba(0, 255, 255, 0.5)',
          borderWidth: 1,
          textStyle: {
            color: '#ffffff'
          },
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(0, 255, 255, 0.2)'
            }
          }
        },
        series: [{
          type: 'bar',
          barWidth: '35%',
          itemStyle: {
            borderRadius: [6, 6, 0, 0],
            shadowBlur: 15,
            shadowColor: 'rgba(0, 255, 255, 0.6)',
            shadowOffsetY: -2
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 25,
              shadowColor: 'rgba(0, 255, 255, 0.9)',
              shadowOffsetY: -4
            }
          }
        }]
      },

      // 新增 lineChartData1 和 lineChartOptions1
      lineChartData1: {
        xAxis: Array.from({length: 15}, (_, i) => {
          const d = new Date();
          d.setDate(d.getDate() - 14 + i);
          return `${d.getMonth() + 1}-${d.getDate()}`;
        }),
        expectedData: Array.from({length: 15}, (_, i) => 200 + i * 5),
        // actualData: Array.from({length: 15}, (_, i) => 200 + i * 5)
      },
      lineChartOptions1: {
        backgroundColor: 'transparent',
        title: { 
          text: '', // 15天生长变化 
          left: 'center', 
          top: 10, 
          textStyle: { 
            fontSize: 14,
            color: '#ffffff',
            fontWeight: 'bold'
          } 
        },
        xAxis: { 
          name: '日', 
          type: 'category',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12,
            interval: 0,
            rotate: 30
          },
          nameTextStyle: {
            color: 'rgba(255, 255, 255, 0.9)'
          }
        },
        yAxis: { 
          name: 'g', // 重量(g) 
          type: 'value',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12
          },
          nameTextStyle: {
            color: 'rgba(255, 255, 255, 0.9)'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.15)',
              type: 'dashed',
              width: 1
            }
          }
        },
        grid: { 
          left: 20, 
          right: 20, 
          bottom: 30, 
          top: 40,
          containLabel: true
        },
        // tooltip: { 
        //   trigger: 'axis',
        //   backgroundColor: 'rgba(10, 21, 32, 0)',
        //   borderColor: 'rgba(0, 255, 255, 0)',
        //   borderWidth: 1,
        //   textStyle: {
        //     color: 'rgba(0, 255, 255, 0)'
        //   },
        //   axisPointer: {
        //     type: 'cross',
        //     crossStyle: {
        //       color: 'rgba(0, 255, 255, 0)'
        //     }
        //   }
        // },
        // legend: { 
        //   data: ['实际'], // '期望', 
        //   textStyle: {
        //     color: 'rgba(0, 255, 255, 0)'
        //   },
        //   top: 10
        // }
      },
      barChartData6: {
        xAxis: ['批次1', '批次2', '批次3', '批次4', '批次5'],
        series: [1.2, 1.1, 1.3, 1.1, 1.2]
      },
      barChartOptions6: {
        color: [{
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 255, 255, 1)' },
            { offset: 0.3, color: 'rgba(64, 224, 255, 0.95)' },
            { offset: 0.6, color: 'rgba(64, 158, 255, 0.85)' },
            { offset: 1, color: 'rgba(30, 100, 200, 0.7)' }
          ]
        }],
        title: { 
          text: '各批次', 
          left: 'center', 
          top: 10, 
          textStyle: { 
            fontSize: 14,
            color: '#ffffff',
            fontWeight: 'bold'
          } 
        },
        xAxis: { 
          type: 'category',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12,
            interval: 0,
            rotate: 30
          }
        },
        yAxis: { 
          type: 'value',
          axisLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.4)'
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.9)',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(0, 255, 255, 0.15)',
              type: 'dashed',
              width: 1
            }
          }
        },
        grid: { 
          left: 20, 
          right: 20, 
          bottom: 30, 
          top: 40,
          containLabel: true
        },
        tooltip: { 
          trigger: 'axis',
          backgroundColor: 'rgba(10, 21, 32, 0.95)',
          borderColor: 'rgba(0, 255, 255, 0.5)',
          borderWidth: 1,
          textStyle: {
            color: '#ffffff'
          },
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(0, 255, 255, 0.2)'
            }
          }
        },
        series: [{
          type: 'bar',
          barWidth: '35%',
          itemStyle: {
            borderRadius: [6, 6, 0, 0],
            shadowBlur: 15,
            shadowColor: 'rgba(0, 255, 255, 0.6)',
            shadowOffsetY: -2
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 25,
              shadowColor: 'rgba(0, 255, 255, 0.9)',
              shadowOffsetY: -4
            }
          }
        }]
      },

      // 新增 pieChartData1 和 pieChartOptions1
      pieChartData1: {
        legend: ['饲料成本', '鱼苗鱼卵', '动保成本', '设备成本', '能源成本', '额外成本'],
        seriesName: '成本组成',
        data: [
          { value: 11, name: '饲料成本' },
          { value: 23, name: '鱼苗鱼卵' },
          { value: 15, name: '动保成本' },
          { value: 12, name: '设备成本' },
          { value: 25, name: '能源成本' },
          { value: 30, name: '额外成本' }
        ]
      },
      pieChartOptions1: {
        title: { 
          text: '成本组成', 
          left: 'center', 
          top: 10, 
          textStyle: { 
            fontSize: 14,
            color: '#ffffff'
          } 
        },
        legend: { 
          left: 'center', 
          bottom: 40,
          orient: 'horizontal',
          itemWidth: 14,
          itemHeight: 14,
          itemGap: 24,
          width: '85%',
          align: 'left',
          data: ['饲料成本', '鱼苗鱼卵', '动保成本', '设备成本', '能源成本', '额外成本'],
          textStyle: {
            color: '#ffffff',
            fontSize: 13,
            rich: {
              a: {
                padding: [0, 8, 0, 8]
              }
            }
          },
          formatter: function(name) {
            return '{a|' + name + '}';
          }
        },
        tooltip: { 
          trigger: 'item', 
          formatter: '{a} <br/>{b} : {c} ({d}%)',
          backgroundColor: 'rgba(10, 21, 32, 0.95)',
          borderColor: 'rgba(64, 158, 255, 0.5)',
          borderWidth: 1,
          textStyle: {
            color: '#ffffff'
          }
        },
        // 添加动画效果
        animationDuration: 1500,
        animationEasingUpdate: 'quinticInOut',
        // 设置颜色主题
        color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#C0C4CC']
      }

    }
  },
  computed: {
    ...mapGetters([
      'userId',
      'avatar',
      'nickname',
    ]),
  },
  created() {
    this.getWaterDeviceAlarmList();
    this.getMachineDeviceAlarmList();
    this.getData();
    this.getUser();
    this.getOverallCostData(); // 获取成本数据
    this.fetchMainHomeData();
    this.getZkWorkPlanList();
  },
  mounted() {
    this.startScroll();
    // 获取饼图组件引用
    this.pieChartComponent = this.$refs.pieChartComponent;
    console.log('饼图组件引用:', this.pieChartComponent);
    // 初始化WebSocket连接
    this.initWebSocket();
    // 监听WebSocket消息
    if (this.$socketPublic) {
      this.$nextTick(() => {
        // 监听WebSocket消息状态变化
        this.$watch(
          () => this.$socketPublic.state.msg,
          (newMsg) => {
            if (newMsg) {
              console.log('【WebSocket】监听到新消息:', typeof newMsg);
              this.handleDeviceAlarmMsg(newMsg);
            }
          },
          { deep: true }
        );
        
        // 添加额外的监听，确保能捕获所有可能的告警消息
        this.$watch(
          () => this.$socketPublic.state.productMsg,
          (newMsg) => {
            if (newMsg && newMsg.data) {
              console.log('【WebSocket】通过productMsg监听到新消息');
              this.handleDeviceAlarmMsg(newMsg.data);
            }
          },
          { deep: true }
        );
      });
    } else {
      console.error('【WebSocket】$socketPublic未找到，无法监听告警消息');
      // 为了调试，添加一个模拟告警的定时器
      setTimeout(() => {
        console.log('【调试】模拟WebSocket告警消息');
        this.testAlarmDialog();
      }, 5000);
    }
  },
  beforeDestroy() {
    this.stopScroll();
    // 不需要手动移除Vue watch监听，Vue会自动清理
  },
  methods: {
    fetchMainHomeData() {
      getZkBiologyCapitalMainHome().then(response => {
        const data = response.data;
        if (data) {
          // 1. 生物资产评估 (barChartData1)
          if (data.biologyCapitals && data.biologyCapitals.length > 0) {
            // 过滤掉 totalMount 为 0 的数据
            const validBiologyCapitals = data.biologyCapitals.filter(item => item.totalMount > 0);
            if (validBiologyCapitals.length > 0) {
              this.barChartData1 = {
                xAxis: validBiologyCapitals.map(item => item.pondDetailNum),
                series: validBiologyCapitals.map(item => item.totalMount)
              };
            }
          }

          // 2. 健康状态评估 (barChartData2) - 暂无SQL，保持默认

          // 3. 死亡量存活率 (barChartData3)
          if (data.survivalRates && data.survivalRates.length > 0) {
            this.barChartData3 = {
              xAxis: data.survivalRates.map(item => item.pondDetailNum),
              series: data.survivalRates.map(item => (item.survivalRate * 100).toFixed(2))
            };
          }

          // 4. 精准投喂系统 (barChartData4)
          if (data.precisionFeedings && data.precisionFeedings.length > 0) {
            this.barChartData4 = {
              xAxis: data.precisionFeedings.map(item => `${item.month}-${item.day}`),
              series: data.precisionFeedings.map(item => item.totalFeedKg)
            };
          }

          // 5. 鱼生长大模型 (lineChartData1)
          if (data.fishGrowths && data.fishGrowths.length > 0) {
            let v1 = data.fishGrowths.map(item => item.avgFishSpec);
            if(v1) v1 = v1.sort((a, b) => a-b);
            this.lineChartData1 = {
              xAxis: data.fishGrowths.map(item => item.logDay),
              actualData: v1,
              // expectedData: v1 // 期望数据暂无，可以先与实际数据保持一致
            };
          }

          // 6. 饵料系数 (barChartData6)
          if (data.baitCoefficients && data.baitCoefficients.length > 0) {
            this.barChartData6 = {
              xAxis: data.baitCoefficients.map(item => item.pondDetailNum),
              series: data.baitCoefficients.map(item => item.avgBaitCoefficient)
            };
          }
        }
      });
    },
    /** 查询水质警告列表 */
    getWaterDeviceAlarmList() {
      this.loading = true;
      // 执行查询
      let queryParams = {
        pageNo: 1,
        pageSize: 500,
        warnDeviceType: 1,
      }
      getZkDeviceAlarmPage(queryParams).then(response => {
        this.waterDeviceAlarmList = response.data.list;
        // console.log(`=================>deviceAlarmList:${JSON.stringify(this.deviceAlarmList)}`);
        this.loading = false;
      });
    },
    /** 查询设备警告列表 */
    getMachineDeviceAlarmList() {
      this.loading = true;
      // 执行查询
      let queryParams = {
        pageNo: 1,
        pageSize: 500,
        warnDeviceType: 0,
      }
      getZkDeviceAlarmPage(queryParams).then(response => {
        this.machineDeviceAlarmList = response.data.list;
        // console.log(`=================>deviceAlarmList:${JSON.stringify(this.deviceAlarmList)}`);
        this.loading = false;
      });
    },
    // 初始化WebSocket连接
    initWebSocket() {
      console.log('==========================================');
      console.log('【WebSocket初始化】开始初始化WebSocket连接');
      console.log('初始化时间:', new Date().toLocaleString('zh-CN'));
      
      if (this.$socketPublic) {
        console.log('【WebSocket初始化】$socketPublic对象可用');
        
        // 检查是否已经存在连接
        if (this.$socketPublic.state.ws && this.$socketPublic.state.ws.readyState === WebSocket.OPEN) {
          console.log('【WebSocket初始化】WebSocket连接已存在且处于打开状态，复用现有连接');
        } else {
          console.log('【WebSocket初始化】发送初始化命令');
          this.$socketPublic.dispatch('webSocketInit');
        }
        
        // 启动WebSocket状态监控
        this.startWebSocketMonitoring();
        
        // 添加全局错误处理
        window.addEventListener('error', this.handleGlobalError);
        
        console.log('【WebSocket初始化】初始化完成');
      } else {
        console.error('【WebSocket初始化失败】$socketPublic未找到，无法初始化连接');
        console.error('检查socketVuex.js是否正确导入到main.js并挂载到Vue.prototype');
        // 添加备用的模拟告警机制
        this.setupBackupAlarmSimulation();
      }
      console.log('==========================================');
    },
    
    // 启动WebSocket状态监控
    startWebSocketMonitoring() {
      console.log('【WebSocket监控】启动状态监控');
      
      // 立即检查初始状态
      this.checkWebSocketStatus();
      
      // 定期检查WebSocket状态
      this.wsStatusInterval = setInterval(() => {
        this.checkWebSocketStatus();
      }, 5000);
      
      console.log('【WebSocket监控】状态检查间隔设置为5秒');
    },
    
    // 检查WebSocket状态
    checkWebSocketStatus() {
      if (this.$socketPublic) {
        const ws = this.$socketPublic.state.ws;
        if (ws) {
          const isConnected = ws.readyState === WebSocket.OPEN;
          const readyStateText = this.getReadyStateText(ws.readyState);
          
          this.webSocketStatus.connected = isConnected;
          
          // 增强的状态日志
          // console.log('==========================================');
          // console.log('【WebSocket状态检查】', new Date().toLocaleString('zh-CN'));
          // console.log('连接状态:', {
          //   readyState: ws.readyState,
          //   readyStateText: readyStateText,
          //   connected: isConnected,
          //   messageCount: this.webSocketStatus.messageCount,
          //   errorCount: this.webSocketStatus.errorCount,
          //   lastMessageTime: this.webSocketStatus.lastMessageTime
          // });
          
          // 状态异常处理
          if (ws.readyState === WebSocket.CLOSED) {
            console.warn('【WebSocket警告】连接已关闭，尝试重新连接');
            this.$socketPublic.dispatch('webSocketInit');
          } else if (ws.readyState === WebSocket.CLOSING) {
            console.warn('【WebSocket警告】连接正在关闭');
          }
          // console.log('==========================================');
        } else {
          console.warn('【WebSocket警告】WebSocket实例未初始化');
        }
      } else {
        console.error('【WebSocket错误】$socketPublic不可用');
      }
    },
    
    // 获取WebSocket连接状态文本
    getReadyStateText(readyState) {
      const states = {
        0: 'CONNECTING - 连接中',
        1: 'OPEN - 已连接',
        2: 'CLOSING - 关闭中',
        3: 'CLOSED - 已关闭'
      };
      return states[readyState] || `UNKNOWN - ${readyState}`;
    },
    
    // 全局错误处理
    handleGlobalError(error) {
      console.error('【全局错误】WebSocket相关错误:', error);
      this.webSocketStatus.errorCount++;
    },
    
    // 设置备用的模拟告警机制
    setupBackupAlarmSimulation() {
      console.log('【备用机制】设置模拟告警机制，每30秒触发一次测试告警');
      
      // 立即触发一次测试告警
      setTimeout(() => {
        console.log('【备用机制】首次测试告警触发');
        this.testAlarmDialog();
      }, 3000);
      
      // 定期触发测试告警
      this.simulationInterval = setInterval(() => {
        console.log('【备用机制】定期测试告警触发');
        this.testAlarmDialog();
      }, 30000);
    },
    
    // 清理WebSocket相关资源
    cleanupWebSocketResources() {
      if (this.wsStatusInterval) {
        clearInterval(this.wsStatusInterval);
        this.wsStatusInterval = null;
      }
      if (this.simulationInterval) {
        clearInterval(this.simulationInterval);
        this.simulationInterval = null;
      }
    },
    
    // 处理设备告警消息
    handleDeviceAlarmMsg(msg) {
      // 更新WebSocket状态统计
      this.webSocketStatus.messageCount++;
      this.webSocketStatus.lastMessageTime = new Date().toLocaleString('zh-CN');
      this.webSocketStatus.connected = true;
      
      // 消息接收标记，添加时间戳和毫秒级精度
      const now = new Date();
      const receiveTime = this.webSocketStatus.lastMessageTime;
      const timestamp = now.getTime();
      
      // 增强的消息接收日志
      // console.log('==========================================');
      // console.log('【消息处理中心】收到WebSocket消息');
      // console.log('接收时间:', receiveTime);
      // console.log('时间戳:', timestamp);
      // console.log('消息编号:', this.webSocketStatus.messageCount);
      // console.log('==========================================');
      
      // 详细的原始消息分析
      // console.log('【原始消息分析】');
      // console.log('数据类型:', typeof msg);
      // console.log('是否为空:', msg === null || msg === undefined);
      
      if (typeof msg === 'string') {
        // console.log('字符串长度:', msg.length);
        // 打印前200个字符作为预览
        // console.log('内容预览:', msg.length > 200 ? 
        //            msg.substring(0, 200) + '...' : msg);
        
        // 检测消息类型特征
        if (msg === 'heartCheck') {
          // console.log('【消息类型】心跳消息');
          return; // 心跳消息不做进一步处理
        }
        if (msg.startsWith('websocket：')) {
          // console.log('【消息类型】连接状态消息');
          return; // 连接状态消息不做进一步处理
        }
        
        // 重点：检查是否是以'device-alarm'开头的Java服务端消息
        if (msg.startsWith('device-alarm')) {
          // console.log('【Java服务端消息】检测到以device-alarm开头的消息');
          // 这种格式可能是Java服务端直接发送的特殊格式消息
        }
        
        // 检查是否为JSON格式的字符串
        try {
          const parsed = JSON.parse(msg);
          // console.log('【消息类型】JSON字符串');
          // console.log('JSON解析成功，包含字段:', Object.keys(parsed).join(', '));
        } catch (e) {
          console.log('【消息类型】普通文本消息（非JSON）');
        }
      } else if (msg && typeof msg === 'object') {
        // console.log('对象属性列表:', Object.keys(msg).join(', '));
        // console.log('对象层级深度:', this.getObjectDepth(msg));
        
        // 重点：检查是否是Java服务端发送的device-alarm类型消息
        const isJavaAlarmMessage = msg.type === 'device-alarm' || 
                                 msg.alarmType === 'device' ||
                                 msg.messageType === 'device-alarm' ||
                                 msg.msgType === 'device-alarm';
        // console.log('是否为Java服务端告警消息:', isJavaAlarmMessage);
        
        // 检查是否包含告警相关字段
        const hasAlarmFields = ['type', 'alarmType', 'fault', 'error', 'warning', 'alert']
          .some(field => msg.hasOwnProperty(field));
        // console.log('是否包含告警相关字段:', hasAlarmFields);
        
        // 打印对象结构概览
        const objOverview = JSON.stringify(msg, null, 2);
        // console.log('对象内容概览:', objOverview.length > 500 ? 
        //            objOverview.substring(0, 500) + '...' : objOverview);
      }
      
      // 存储原始消息到本地，便于调试
      try {
        const msgKey = `ws_msg_${timestamp}_${this.webSocketStatus.messageCount}`;
        sessionStorage.setItem(msgKey, typeof msg === 'string' ? msg : JSON.stringify(msg));
        // console.log('【消息存储】原始消息已保存到sessionStorage，键名:', msgKey);
      } catch (storageError) {
        console.warn('【消息存储】无法保存消息到sessionStorage:', storageError.message);
      }
      
      try {
        // 初始化变量
        let parsedMsg = null;
        let isAlarmMessage = false;
        
        // 消息处理前置检查
        // console.log('【消息处理】开始消息解析和处理');
        
        // 尝试将消息解析为JSON对象
        if (typeof msg === 'string') {
          // 连接状态和心跳消息已在前面处理，这里进行其他字符串处理
          try {
            // 重点：特殊处理以device-alarm开头的消息格式
            if (msg.startsWith('device-alarm')) {
              // console.log('【特殊格式处理】处理device-alarm开头的消息');
              // 提取实际的JSON部分（假设格式为 "device-alarm{...}"）
              const jsonStartIndex = msg.indexOf('{');
              if (jsonStartIndex !== -1) {
                const jsonPart = msg.substring(jsonStartIndex);
                parsedMsg = JSON.parse(jsonPart);
                // 显式设置类型标识为device-alarm
                parsedMsg.type = 'device-alarm';
                // console.log('【特殊格式处理】成功提取并解析device-alarm消息');
              } else {
                throw new Error('无法找到JSON开始位置');
              }
            } else {
              // 常规JSON解析
              parsedMsg = JSON.parse(msg);
              // console.log('【消息解析】成功将字符串解析为JSON对象');
            }
          } catch (parseError) {
            // 非JSON格式的告警消息也尝试处理
            // console.log('【消息解析】无法解析为JSON，创建文本消息对象');
            parsedMsg = { 
              rawMessage: msg, 
              type: 'text_message',
              receivedAt: timestamp
            };
            
            // 尝试从文本中提取可能的告警信息
            if (msg.toLowerCase().includes('error') || msg.toLowerCase().includes('错误')) {
              parsedMsg.type = 'error';
            } else if (msg.toLowerCase().includes('warning') || msg.toLowerCase().includes('警告')) {
              parsedMsg.type = 'warning';
            }
          }
        } else if (msg && typeof msg === 'object') {
          // 如果已经是对象，直接使用并确保有时间戳
          console.log('【消息处理】使用原始对象格式');
          parsedMsg = { ...msg, receivedAt: timestamp };
        } else {
          console.log('【消息处理】未知格式，跳过处理');
          return;
        }
        
        // 告警消息检测与识别
        if (parsedMsg) {
          // console.log('==========================================');
          // console.log('【告警检测引擎】开始分析消息内容');
          
          // 创建详细的检测报告对象
          const detectionReport = {
            initialType: parsedMsg.type || 'unknown',
            hasAlarmFlag: false,
            hasAlarmContent: false,
            hasDeviceInfo: false,
            hasAlarmLevel: false,
            hasNestedStructure: false,
            isAlarmMessage: false,
            isJavaAlarmMessage: false,
            detectionSteps: []
          };
          
          // 步骤0: 快速识别Java服务端告警消息 - 最高优先级
          if (parsedMsg.type === 'device-alarm' || 
              parsedMsg.alarmType === 'device' ||
              parsedMsg.messageType === 'device-alarm' ||
              parsedMsg.msgType === 'device-alarm') {
            // console.log('【告警检测】步骤0: 快速识别到Java服务端告警消息');
            detectionReport.isJavaAlarmMessage = true;
            detectionReport.isAlarmMessage = true;
            detectionReport.hasAlarmFlag = true;
            detectionReport.detectionSteps.push('快速识别Java服务端device-alarm消息');
          } else {
            // 步骤1: 检查是否为明确的告警类型
            const explicitAlarmTypes = ['device-alarm', 'alarm', 'warning', 'error', 'alert'];
            if (parsedMsg.type && explicitAlarmTypes.includes(parsedMsg.type)) {
              detectionReport.hasAlarmFlag = true;
              detectionReport.detectionSteps.push('找到明确的告警类型标识');
              // console.log('【告警检测】步骤1: 找到明确的告警类型:', parsedMsg.type);
            }
            
            // 步骤2: 检查告警标识字段
            const hasAlarmFlag = parsedMsg.type === 'device-alarm' || 
                               parsedMsg.alarmType === 'device' ||
                               parsedMsg.msgType === 'alarm' ||
                               parsedMsg.messageType === 'alarm' ||
                               (typeof parsedMsg === 'object' && 'alarm' in parsedMsg) ||
                               parsedMsg.isAlarm === true ||
                               parsedMsg.warning === true ||
                               parsedMsg.alert === true;
            detectionReport.hasAlarmFlag = detectionReport.hasAlarmFlag || hasAlarmFlag;
            
            // 步骤3: 检查告警内容相关字段
            const contentFields = ['warnContent', 'alarmContent', 'warnName', 'rawMessage', 'content', 
                                 'fault', 'errorMessage', 'message', 'warningInfo', 'alertInfo', 
                                 'faultMessage', 'error', 'warnDesc'];
            const hasAlarmContent = contentFields.some(field => parsedMsg[field]);
            detectionReport.hasAlarmContent = hasAlarmContent;
            
            if (hasAlarmContent) {
              const foundFields = contentFields.filter(field => parsedMsg[field]);
              detectionReport.detectionSteps.push(`找到告警内容字段: ${foundFields.join(', ')}`);
              // console.log('【告警检测】步骤3: 找到告警内容字段:', foundFields);
            }
            
            // 步骤4: 检查设备标识相关字段
            const deviceFields = ['deviceId', 'deviceName', 'device', 'warnDevice', 'equipmentCode', 
                                 'equipmentName', 'sensorId', 'sensorName', 'deviceCode', 'assetCode', 
                                 'machineId', 'machineName'];
            const hasDeviceInfo = deviceFields.some(field => parsedMsg[field]);
            detectionReport.hasDeviceInfo = hasDeviceInfo;
            
            if (hasDeviceInfo) {
              const foundFields = deviceFields.filter(field => parsedMsg[field]);
              detectionReport.detectionSteps.push(`找到设备信息字段: ${foundFields.join(', ')}`);
              // console.log('【告警检测】步骤4: 找到设备信息字段:', foundFields);
            }
            
            // 步骤5: 检查告警级别字段
            const levelFields = ['warnType', 'alarmLevel', 'level', 'priority', 'severity', 
                               'warnLevel', 'alertLevel', 'importance'];
            const hasAlarmLevel = levelFields.some(field => parsedMsg[field] !== undefined);
            detectionReport.hasAlarmLevel = hasAlarmLevel;
            
            if (hasAlarmLevel) {
              const foundFields = levelFields.filter(field => parsedMsg[field] !== undefined);
              detectionReport.detectionSteps.push(`找到告警级别字段: ${foundFields.join(', ')}`);
              // console.log('【告警检测】步骤5: 找到告警级别字段:', foundFields);
            }
            
            // 步骤6: 检查嵌套结构
            if (!detectionReport.hasAlarmFlag && typeof parsedMsg === 'object') {
              const nestedAlarmKeys = ['data', 'result', 'alarmInfo', 'warningInfo', 'alertInfo'];
              for (const key of nestedAlarmKeys) {
                if (parsedMsg[key] && typeof parsedMsg[key] === 'object') {
                  const nestedHasContent = contentFields.some(field => parsedMsg[key][field]);
                  const nestedHasDevice = deviceFields.some(field => parsedMsg[key][field]);
                  
                  if (nestedHasContent || nestedHasDevice) {
                    detectionReport.hasNestedStructure = true;
                    detectionReport.detectionSteps.push(`发现嵌套结构: ${key}`);
                    // console.log('【告警检测】步骤6: 发现嵌套结构:', key);
                    
                    // 提取嵌套数据进行进一步分析
                    const nestedData = parsedMsg[key];
                    const nestedHasAlarmFlag = nestedData.type === 'device-alarm' || 
                                            nestedData.alarmType === 'device' ||
                                            nestedData.isAlarm === true;
                    
                    if (nestedHasAlarmFlag || (nestedHasContent && nestedHasDevice)) {
                      // console.log('【告警检测】步骤6: 嵌套结构包含告警信息，提取数据');
                      parsedMsg = nestedData;
                      detectionReport.isAlarmMessage = true;
                      break;
                    }
                  }
                }
              }
            }
            
            // 步骤7: 综合判断
            if (!detectionReport.isAlarmMessage) {
              // 多条件组合判断，降低漏报率
              detectionReport.isAlarmMessage = 
                detectionReport.hasAlarmFlag ||
                (detectionReport.hasAlarmContent && detectionReport.hasDeviceInfo) ||
                (detectionReport.hasAlarmContent && detectionReport.hasAlarmLevel) ||
                (typeof parsedMsg.rawMessage === 'string' && 
                 (parsedMsg.rawMessage.toLowerCase().includes('error') || 
                  parsedMsg.rawMessage.toLowerCase().includes('warning') ||
                  parsedMsg.rawMessage.toLowerCase().includes('告警') ||
                  parsedMsg.rawMessage.toLowerCase().includes('警告')));
            }
          }
          
          isAlarmMessage = detectionReport.isAlarmMessage;
          
          // 输出详细检测报告
          // console.log('【告警检测】详细检测报告:');
          // console.log('  初始消息类型:', detectionReport.initialType);
          // console.log('  告警标识字段:', detectionReport.hasAlarmFlag);
          // console.log('  告警内容字段:', detectionReport.hasAlarmContent);
          // console.log('  设备信息字段:', detectionReport.hasDeviceInfo);
          // console.log('  告警级别字段:', detectionReport.hasAlarmLevel);
          // console.log('  嵌套结构检测:', detectionReport.hasNestedStructure);
          // console.log('  是否为Java服务端告警:', detectionReport.isJavaAlarmMessage);
          // console.log('  检测步骤:', detectionReport.detectionSteps);
          // console.log('  最终判定结果:', isAlarmMessage ? '是告警消息' : '非告警消息');
          // console.log('==========================================');
        }
        
        if (isAlarmMessage) {
          // 告警消息处理流程
          // console.log('==========================================');
          // console.log('【告警处理流程】开始处理Java后端告警消息');
          // console.log('告警时间:', new Date().toLocaleString('zh-CN'));
          
          // 统一告警格式
          // console.log('【告警处理】调用normalizeAlarmData标准化告警数据');
          const alarmData = this.normalizeAlarmData(parsedMsg);
          
          // 增强告警数据，添加元信息
          alarmData.originalData = parsedMsg;
          alarmData.receiveTime = receiveTime;
          alarmData.timestamp = timestamp;
          alarmData.messageId = this.webSocketStatus.messageCount;
          alarmData.source = 'java-backend';
          
          // 存储当前告警
          this.currentAlarm = alarmData;
          
          // 记录告警历史
          this.recordAlarmHistory(alarmData);
          
          // 显示告警弹窗
          // console.log('【告警弹窗】设置alarmDialogVisible为true');
          this.alarmDialogVisible = true;
          
          // 详细的告警信息日志
          // console.log('【告警详情】');
          // console.log('  告警级别:', alarmData.type);
          // console.log('  告警来源:', alarmData.source);
          // console.log('  告警设备:', alarmData.device);
          // console.log('  告警内容:', alarmData.fault);
          // console.log('  告警时间:', alarmData.date);
          // console.log('==========================================');
          
          // 为确保弹窗能够正确显示，添加一个短暂的延迟后再次确认状态
          
          // 为确保弹窗能够正确显示，添加一个短暂的延迟后再次确认状态
          setTimeout(() => {
            // console.log('【状态确认】500ms后弹窗状态检查:');
            // console.log('  - alarmDialogVisible:', this.alarmDialogVisible);
            // console.log('  - currentAlarm:', this.currentAlarm ? '已设置' : '未设置');
            
            // 如果因为某种原因弹窗状态未正确设置，再次尝试
            if (!this.alarmDialogVisible && this.currentAlarm) {
              console.log('【状态修复】重新设置弹窗可见状态');
              this.alarmDialogVisible = true;
            }
          }, 500);
          
          // 结构化日志输出 - 增强版
          // console.log('==========================================');
          // console.log('【告警处理】已成功处理Java后端告警消息');
          // console.log('==========================================');
          // console.log('告警信息详情:');
          // console.log('┌─────────────────┬─────────────────────────────┐');
          // console.log('│ 告警级别        │', alarmData.type.padEnd(25), '│');
          // console.log('├─────────────────┼─────────────────────────────┤');
          // console.log('│ 告警来源        │', (alarmData.source || 'Java后端').padEnd(25), '│');
          // console.log('├─────────────────┼─────────────────────────────┤');
          // console.log('│ 车间            │', alarmData.workshop.padEnd(25), '│');
          // console.log('├─────────────────┼─────────────────────────────┤');
          // console.log('│ 系统            │', alarmData.system.padEnd(25), '│');
          // console.log('├─────────────────┼─────────────────────────────┤');
          // console.log('│ 设备            │', alarmData.device.padEnd(25), '│');
          // console.log('├─────────────────┼─────────────────────────────┤');
          // console.log('│ 告警内容        │', (alarmData.fault.length > 25 ? 
          //               alarmData.fault.substring(0, 25) + '...' : 
          //               alarmData.fault).padEnd(25), '│');
          // console.log('├─────────────────┼─────────────────────────────┤');
          // console.log('│ 告警时间        │', alarmData.date.padEnd(25), '│');
          // console.log('├─────────────────┼─────────────────────────────┤');
          // console.log('│ 责任人          │', alarmData.responsible.padEnd(25), '│');
          // console.log('└─────────────────┴─────────────────────────────┘');
          // console.log('==========================================');
          // console.log('【原始告警数据】');
          // console.log('原始告警对象:', parsedMsg);
          
        } else {
          // console.log('==========================================');
          // console.log('【告警处理】消息不符合告警格式，忽略处理');
          // console.log('当前消息类型:', parsedMsg?.type || '未知');
          // console.log('==========================================');
        }
      } catch (error) {
          // 增强的错误处理
          this.webSocketStatus.errorCount++;
          const errorId = `error_${timestamp}_${this.webSocketStatus.errorCount}`;
          
          console.error('==========================================');
          console.error('【告警处理异常】解析或处理Java后端告警消息失败!');
          console.error('错误ID:', errorId);
          console.error('错误信息:', error.message);
          console.error('错误类型:', error.name);
          console.error('错误堆栈:', error.stack);
          console.error('==========================================');
          
          // 详细的故障排查信息
          console.error('【故障排查】完整信息:');
          console.error('  消息类型:', typeof msg);
          console.error('  消息内容:', msg);
          console.error('  消息长度:', typeof msg === 'string' ? msg.length : 'N/A');
          console.error('  接收时间:', receiveTime);
          console.error('  消息编号:', this.webSocketStatus.messageCount);
          console.error('==========================================');
          
          // 存储错误信息便于后续分析
          try {
            const errorInfo = {
              errorId,
              timestamp,
              errorName: error.name,
              errorMessage: error.message,
              stack: error.stack,
              messageType: typeof msg,
              messageContent: typeof msg === 'string' ? msg : JSON.stringify(msg),
              messageNumber: this.webSocketStatus.messageCount
            };
            localStorage.setItem(`ws_error_${errorId}`, JSON.stringify(errorInfo));
            // console.error('【错误存储】错误信息已保存到localStorage');
          } catch (storageError) {
            console.error('【错误存储】无法保存错误信息:', storageError.message);
          }
          
          // 为了调试，即使处理失败也尝试显示一个测试告警
          if (!this.alarmDialogVisible) {
            setTimeout(() => {
              console.log('【调试】由于处理错误，显示测试告警');
              this.testAlarmDialog();
            }, 1000);
          }
        }
    },
    
    // 记录告警历史
    recordAlarmHistory(alarmData) {
      try {
        // 获取现有告警历史
        let history = JSON.parse(localStorage.getItem('ws_alarm_history') || '[]');
        
        // 添加新告警
        history.push({
          id: alarmData.messageId,
          timestamp: alarmData.timestamp,
          type: alarmData.type,
          device: alarmData.device,
          fault: alarmData.fault,
          date: alarmData.date
        });
        
        // 只保留最近100条告警历史
        if (history.length > 100) {
          history = history.slice(-100);
        }
        
        // 保存到localStorage
        localStorage.setItem('ws_alarm_history', JSON.stringify(history));
        console.log('【告警历史】已记录告警历史，当前历史记录数:', history.length);
      } catch (error) {
        console.warn('【告警历史】无法记录告警历史:', error.message);
      }
    },
    
    // 获取对象深度
    getObjectDepth(obj) {
      if (typeof obj !== 'object' || obj === null) {
        return 0;
      }
      
      let maxDepth = 0;
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
          const depth = this.getObjectDepth(obj[key]) + 1;
          maxDepth = Math.max(maxDepth, depth);
        }
      }
      return maxDepth;
    },
    
    // 统一告警数据格式
    normalizeAlarmData(data) {
      try {
        console.log('【数据标准化】开始处理告警数据...');
        
        // 增强错误处理和数据验证
        if (!data) {
          console.error('【数据标准化】告警数据为空:', data);
          return this.createDefaultAlarm('告警数据为空');
        }
        
        // 处理字符串类型的原始消息（可能是JSON字符串）
        if (typeof data === 'string') {
          console.log('【数据标准化】尝试解析字符串类型的告警数据:', data);
          try {
            // 重点：特殊处理以device-alarm开头的Java服务端消息格式
            if (data.startsWith('device-alarm')) {
              console.log('【数据标准化】检测到Java服务端device-alarm消息格式');
              // 提取实际的JSON部分（假设格式为 "device-alarm{...}"）
              const jsonStartIndex = data.indexOf('{');
              if (jsonStartIndex !== -1) {
                const jsonPart = data.substring(jsonStartIndex);
                const parsedData = JSON.parse(jsonPart);
                // 显式设置类型标识为device-alarm
                parsedData.type = 'device-alarm';
                parsedData.source = 'java-backend';
                console.log('【数据标准化】成功提取并解析device-alarm消息');
                return this.normalizeAlarmData(parsedData); // 递归处理解析后的对象
              }
            }
            
            // 尝试解析为JSON对象
            const parsedData = JSON.parse(data);
            console.log('【数据标准化】成功解析JSON字符串为对象');
            return this.normalizeAlarmData(parsedData); // 递归处理解析后的对象
          } catch (parseError) {
            console.warn('【数据标准化】无法解析为JSON，作为原始文本处理');
            // 作为原始文本处理
            return this.createTextAlarm(data);
          }
        }
        
        // 确保数据是对象类型
        if (typeof data !== 'object') {
          console.error('【数据标准化】告警数据类型无效，期望对象或字符串，实际为:', typeof data);
          return this.createDefaultAlarm(`无效的告警数据类型: ${typeof data}`);
        }
        
        // 处理包含rawMessage的原始消息
        if (data.rawMessage) {
          console.log('【数据标准化】处理包含rawMessage的告警数据');
          return this.createTextAlarm(data.rawMessage, data.type);
        }
        
        // 重点：识别Java服务端device-alarm类型消息
        const isJavaAlarmMessage = data.type === 'device-alarm' || 
                                  data.alarmType === 'device' ||
                                  data.messageType === 'device-alarm' ||
                                  data.msgType === 'device-alarm';
        
        if (isJavaAlarmMessage) {
          console.log('【数据标准化】识别到Java服务端device-alarm告警消息');
          // 为Java服务端告警消息设置明确标识
          data.source = 'java-backend';
          data.messageType = 'device-alarm';
        }
        
        // 增强的Java后端消息格式适配，支持更多字段名变化
        const result = {
          // 告警级别映射：增强支持多种可能的字段名和值范围
          type: isJavaAlarmMessage ? 'device-alarm' : this.normalizeAlarmType(data),
          // 告警ID：增强支持Java服务端的各种ID格式
          messageId: this.safeString(data.messageId) || 
                     this.safeString(data.id) || 
                     this.safeString(data.alarmId) || 
                     this.safeString(data.warningId) || 
                     `alarm_${Date.now()}`,
          // 车间信息：增加更多可能的字段名
          workshop: this.safeString(data.workshop) || 
                    this.safeString(data.workshopName) || 
                    this.safeString(data.factoryName) || 
                    this.safeString(data.factory) || 
                    '未知车间',
          // 系统信息：增加更多可能的字段名
          system: this.safeString(data.system) || 
                  this.safeString(data.pondSystemName) || 
                  this.safeString(data.subSystem) || 
                  this.safeString(data.systemName) || 
                  this.safeString(data.subSystemName) || 
                  '未知系统',
          // 设备信息：增加更多可能的字段名，特别支持Java服务端格式
          device: this.safeString(data.device) || 
                  this.safeString(data.deviceName) || 
                  this.safeString(data.deviceId) || 
                  this.safeString(data.warnDevice) || 
                  this.safeString(data.equipmentCode) || 
                  this.safeString(data.equipmentName) || 
                  this.safeString(data.machineCode) || 
                  // Java服务端特定字段
                  this.safeString(data.deviceCode) || 
                  this.safeString(data.machineName) || 
                  this.safeString(data.assetName) || 
                  '未知设备',
          // 告警内容：增加更多可能的字段名，特别支持Java服务端格式
          fault: this.safeString(data.fault) || 
                 this.safeString(data.warnContent) || 
                 this.safeString(data.warnName) || 
                 this.safeString(data.alarmContent) || 
                 this.safeString(data.description) || 
                 this.safeString(data.message) || 
                 // Java服务端特定字段
                 this.safeString(data.detail) || 
                 this.safeString(data.info) || 
                 this.safeString(data.content) || 
                 this.safeString(data.errorMessage) || 
                 this.safeString(data.warningMessage) || 
                 this.safeString(data.alertMessage) || 
                 '未知告警内容',
          // 时间信息：增加更多可能的字段名并改进时间格式化
          date: this.normalizeAlarmTime(data),
          // 责任人信息：增加更多可能的字段名
          responsible: this.safeString(data.responsible) || 
                       this.safeString(data.userName) || 
                       this.safeString(data.alarmPerson) || 
                       this.safeString(data.operator) || 
                       this.safeString(data.handler) || 
                       '系统',
          // 告警级别：专门为Java服务端告警添加
          level: this.safeString(data.level) || 
                 this.safeString(data.severity) || 
                 this.safeString(data.priority) || 
                 this.safeString(data.alarmLevel) || 
                 '未知',
          // 保留原始数据用于深度分析
          originalData: JSON.parse(JSON.stringify(data)), // 深拷贝，避免修改原始数据
          // 标记来源
          source: data.source || 'java-backend',
          // 标记是否为Java服务端告警
          isJavaAlarm: isJavaAlarmMessage,
          // 添加时间戳便于排序和追踪
          timestamp: Date.now()
        };
        
        // 增强日志：结构化打印标准化后的告警数据
        this.logAlarmInfo(result);
        
        return result;
      } catch (error) {
        console.error('【数据标准化】处理告警数据时发生错误:', error.stack || error);
        // 创建默认告警对象，确保系统可以正常处理
        return this.createDefaultAlarm('告警数据处理异常');
      }
    },
    
    // 安全获取字符串值，防止undefined/null导致的错误
    safeString(value) {
      if (value === null || value === undefined) return '';
      return String(value).trim() || '';
    },
    
    // 标准化告警级别
    normalizeAlarmType(data) {
      // 直接使用已有的type字段
      if (data.type && ['error', 'warning', 'info'].includes(data.type)) {
        return data.type;
      }
      
      // 处理数字类型的告警级别
      const levelFields = ['alarmLevel', 'warnType', 'level', 'priority'];
      for (const field of levelFields) {
        if (typeof data[field] === 'number') {
          if (data[field] === 1 || data[field] >= 4) return 'error'; // 最高级别或严重级别
          if (data[field] === 2) return 'warning'; // 警告级别
          if (data[field] === 3) return 'info'; // 提示级别
        }
      }
      
      // 处理字符串类型的告警级别
      const levelStrFields = ['alarmLevel', 'warnType', 'level', 'priority'];
      for (const field of levelStrFields) {
        if (typeof data[field] === 'string') {
          const levelStr = data[field].toLowerCase();
          if (levelStr.includes('error') || levelStr.includes('严重') || levelStr.includes('high')) {
            return 'error';
          } else if (levelStr.includes('warn') || levelStr.includes('warning') || levelStr.includes('警告')) {
            return 'warning';
          }
        }
      }
      
      // 默认级别
      return 'warning';
    },
    
    // 标准化告警时间
    normalizeAlarmTime(data) {
      const timeFields = ['date', 'createTime', 'alarmTime', 'timestamp', 'time', 'occurTime'];
      
      for (const field of timeFields) {
        if (data[field]) {
          try {
            let date;
            // 处理时间戳
            if (typeof data[field] === 'number' && data[field] > 1000000000) {
              date = new Date(data[field]);
            } else {
              date = new Date(data[field]);
            }
            
            // 验证是否是有效的日期
            if (!isNaN(date.getTime())) {
              // 格式化为更详细的中文时间格式
              return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
              });
            }
          } catch (e) {
            console.warn(`【时间标准化】解析时间字段 ${field} 时出错:`, e);
          }
        }
      }
      
      // 返回当前时间作为默认值
      return new Date().toLocaleString('zh-CN');
    },
    
    // 创建基于文本的告警对象
    createTextAlarm(message, type = 'warning') {
      // 尝试从文本中提取一些基本信息（简单的正则匹配）
      let workshop = '未知车间';
      let system = '未知系统';
      let device = '未知设备';
      
      // 简单的模式匹配（可根据实际情况扩展）
      const workshopMatch = message.match(/[车间场]\s*[:：]\s*([^\n]+)/);
      if (workshopMatch) workshop = workshopMatch[1];
      
      const systemMatch = message.match(/[系统]\s*[:：]\s*([^\n]+)/);
      if (systemMatch) system = systemMatch[1];
      
      const deviceMatch = message.match(/[设备机器]\s*[:：]\s*([^\n]+)/);
      if (deviceMatch) device = deviceMatch[1];
      
      return {
        type: ['error', 'warning', 'info'].includes(type) ? type : 'warning',
        workshop,
        system,
        device,
        fault: message || '未知告警内容',
        date: new Date().toLocaleString('zh-CN'),
        responsible: '系统',
        source: 'text-message',
        originalData: { rawMessage: message }
      };
    },
    
    // 创建默认告警对象
    createDefaultAlarm(message) {
      return {
        type: 'warning',
        workshop: '未知车间',
        system: '未知系统',
        device: '未知设备',
        fault: message || '未知告警内容',
        date: new Date().toLocaleString('zh-CN'),
        responsible: '系统',
        source: 'default',
        originalData: null
      };
    },
    
    // 结构化日志记录告警信息
    logAlarmInfo(alarm) {
      try {
        console.log('==========================================');
        console.log('【数据标准化】告警数据标准化完成');
        console.log('标准化结果概览:');
        
        // 重点：添加对Java服务端告警消息的特殊日志处理
        if (alarm.isJavaAlarm || alarm.type === 'device-alarm') {
          console.log('【重要】检测到Java服务端告警消息!');
          console.log('- 消息类型:', alarm.type);
          console.log('- 告警来源:', alarm.source);
          if (alarm.messageId) console.log('- 告警ID:', alarm.messageId);
          if (alarm.level) console.log('- 告警级别:', alarm.level);
          console.log('- 设备信息:', alarm.device);
          console.log('- 告警内容:', alarm.fault);
          console.log('- 告警时间:', alarm.date);
          if (alarm.timestamp) console.log('- 时间戳:', alarm.timestamp);
          
          // 添加Java服务端告警专用标记
          console.log('------------------------------------------');
          console.log('【Java服务端告警专用标记】');
          console.log('device-alarm消息已成功解析和标准化!');
          console.log('------------------------------------------');
        } else {
          // 常规告警消息日志
          console.log('- 告警级别:', alarm.type);
          console.log('- 告警来源:', alarm.source);
          console.log('- 告警车间:', alarm.workshop);
          console.log('- 告警系统:', alarm.system);
          console.log('- 告警设备:', alarm.device);
          console.log('- 内容摘要:', alarm.fault?.length > 30 ? 
                    alarm.fault.substring(0, 30) + '...' : alarm.fault);
          console.log('- 告警时间:', alarm.date);
          console.log('- 责任人:', alarm.responsible);
        }
        
        // 添加结构化的JSON格式输出，便于后续处理和分析
        const structuredOutput = {
          type: alarm.type,
          source: alarm.source,
          isJavaAlarm: alarm.isJavaAlarm || false,
          messageId: alarm.messageId,
          device: alarm.device,
          contentLength: alarm.fault?.length || 0,
          timestamp: alarm.timestamp || Date.now()
        };
        console.log('【结构化告警数据】:', JSON.stringify(structuredOutput, null, 2));
        
        console.log('==========================================');
      } catch (e) {
        console.error('【日志记录】记录告警信息时出错:', e);
      }
    },
    // 格式化告警消息 (保留以支持向后兼容)
    formatAlarmMessage(alarm) {
      const { workshop, system, device, fault, type, date, responsible } = alarm;
      let message = '【设备告警通知】\n';
      
      if (workshop) message += `车间: ${workshop}\n`;
      if (system) message += `系统: ${system}\n`;
      if (device) message += `设备/传感器: ${device}\n`;
      if (fault) message += `告警内容: ${fault}\n`;
      if (type) message += `告警级别: ${type === 'error' ? '严重' : type === 'warning' ? '警告' : '提示'}\n`;
      if (date) message += `告警时间: ${date}\n`;
      if (responsible) message += `责任人: ${responsible}\n`;
      
      return message;
    },
    
    // 关闭告警弹窗
    closeAlarmDialog() {
      this.alarmDialogVisible = false;
      this.alarmMessage = '';
      this.currentAlarm = null;
      this.showOriginalData = false;
      console.log('【告警弹窗】弹窗已关闭');
    },
    
    // 测试告警弹窗显示（用于调试）
    testAlarmDialog() {
      console.log('【测试】触发测试告警弹窗');
      const testAlarm = {
        type: 'error',
        source: '测试告警',
        workshop: '测试车间',
        system: '测试系统',
        device: '测试设备',
        fault: '测试告警内容：设备运行异常，请及时处理！',
        date: new Date().toLocaleString('zh-CN'),
        responsible: '测试用户',
        originalData: {testFlag: true, timestamp: Date.now()}
      };
      this.currentAlarm = testAlarm;
      this.alarmDialogVisible = true;
      console.log('【测试】测试告警弹窗已触发');
    },
    
    // 处理告警
    handleAlarm(alarm) {
      console.log('【告警处理】用户点击处理告警:', alarm);
      // 可以在这里添加告警处理逻辑，比如跳转到相关详情页面
      if (alarm) {
        // 根据告警类型跳转到不同页面
        if (alarm.type === 'device-alarm' || alarm.alarmType === 'device') {
          this.goToDeviceAlarm();
        } else {
          // 其他类型的告警处理逻辑
          this.$message.info('告警处理功能待实现');
        }
      }
      // 关闭当前弹窗
      this.closeAlarmDialog();
    },
    toExpertMeeting() {
      this.$router.push({
          path: '/knowledge/zk-meeting-expert',
          // path: '/fish/zk-paper-question',
          params: {},
          query: {  }, // paperId: item.id
      });
    },
    goToBioAsset() {
      this.$router.push({ path: '/keyshow/biologicalAssetValuation' });
    },
    goToHealthStatus() {
      // 首页健康度 → 鱼类健康状态独立路由，勿跳死亡量存活率/鱼病诊断
      this.$router.push({ path: '/keyshow/fishHealthStatus' });
    },
    // goToPondWarn() {
    //   this.$router.push({ path: '/keyshow/pondwarn' });
    // },
    goToDeviceAlarm(item) {
      if(item && item.metricId) {
        console.log(`goToZkDevice======================>${JSON.stringify(item)}`);
        this.$router.push({
            path: `/iot/deviceDetail/${item.deviceId}`,
            // path: '/pl/zk-paper-question',   =868898083070563&=TSs_TSs_1&=c1-s1-1
            params: {},
            query: { deviceSn: item.deviceId,metricId: item.metricId, poolId: item.pondDetailNum },
        });
        return;
      }
      this.$router.push({ path: '/keyshow/zkDeviceAlarm' });
    }, 
    goToWorkPlan() {
      this.$router.push({ path: '/keyshow/woorkplan' });
    },
    goToPrecisionFeeding() {
      this.$router.push({ path: '/keyshow/precisionfeeding' });
    },
    goToRespectionReport() {
      this.$router.push({ path: '/keyshow/inspectionreport' });
    },
    goToDeathSurvivalRate() {
      this.$router.push({ path: '/keyshow/zkDeathSurvivalRate', query: { activeTab: 'mortality' }, });
    },
    goToGrowthModel() {
      this.$router.push({ path: '/keyshow/zkGrowthModel' });
    },
    goToOverallCost() {
      this.$router.push({ path: '/keyshow/overallCost' });
    },
    goToZkDevice(item) {
      if(item && item.metricId) {
        console.log(`goToZkDevice======================>${JSON.stringify(item)}`);
        this.$router.push({
            path: `/iot/deviceDetail/${item.deviceId}`,
            // path: '/pl/zk-paper-question',   =868898083070563&=TSs_TSs_1&=c1-s1-1
            params: {},
            query: { deviceSn: item.deviceId,metricId: item.metricId, poolId: item.pondDetailNum },
        });
        return;
      }
      this.$router.push({ path: '/iot/systemShow' });
    },
    goToIntelligentDiagnosis() {
      this.$router.push({ path: '/fish-disease', query: { from: '/keyshow/mainhome' } });
    },
    goToFeedConversionRate() {
      this.$router.push({ path: '/keyshow/zkFcrModel' });

    },
    goToInspectionList() {
      this.$router.push({ path: '/keyshow/InspectionList' });
    },
    goToDaPing4() {
      this.$router.push({ path: '/stock_distribution' });
    },
    getUser(){
      getUserById(this.userId).then(response => {
        // console.log(response)
        this.deptName = response.data.dept.name;
      })
    },
    getData(){
      // getTaskData().then(response => {
      //   this.todoCount = response.data.todoCount
      // })
      // getSellStdData().then(response => {
      //   this.confirmCount = response.data.confirmCount
      //   this.purchaseCount = response.data.purchaseCount
      //   this.outCount = response.data.outCount
      //   this.drawCount = response.data.drawCount
      //   this.sendCount = response.data.sendCount
      //   this.recycleCount = response.data.recycleCount
      // })
      // getMyPerformance().then(response => {
      //   this.weekPerformanceSellAmount = response.data.weekPerformance.sellAmount;
      //   this.weekPerformanceOrderCount = response.data.weekPerformance.orderCount;
      //   this.monthPerformanceSellAmount = response.data.monthPerformance.sellAmount;
      //   this.monthPerformanceOrderCount = response.data.monthPerformance.orderCount;
      // })
    },
    startScroll() {
      this.scrollInterval1 = setInterval(() => {
        this.scrollUp('1');
      }, 2000);
      this.scrollInterval2 = setInterval(() => {
        this.scrollUp('2');
      }, 2000);
    },
    stopScroll() {
      clearInterval(this.scrollInterval1);
      clearInterval(this.scrollInterval2);
    },
    scrollUp(suffix) {
      const listBody = this.$refs[`listBody${suffix}`];
      const scrollContent = this.$refs[`scrollContent${suffix}`];
      if (!listBody || !scrollContent) return;

      const rowHeight = scrollContent.querySelector('.list-row')?.offsetHeight || 30;
      const maxScroll = scrollContent.offsetHeight - listBody.offsetHeight;
      const currentTop = parseInt(scrollContent.style.top || 0);

      if (Math.abs(currentTop) >= maxScroll) {
        scrollContent.style.top = '0';
      } else {
        scrollContent.style.top = `${currentTop - rowHeight}px`;
      }
    },
    getOverallCostData() {
      const params = {
        pageNo: 1,
        pageSize: 10,
        // 按创建时间降序排序
        orderByColumn: 'createTime',
        isAsc: 'desc'
      };

      console.log('请求成本数据参数:', params);
      getZkBiologyCapitalPage(params).then(response => {
        console.log('成本数据响应:', response);
        // 检查响应结构 - 注意API返回的code是0而不是200
        if (response && response.code === 0 && response.data && response.data.list && response.data.list.length > 0) {
          // 取最近的一条记录
          const latestData = response.data.list[response.data.list.length-1]; // .filter(item => item.feedCost > 0)[0];
          console.log('最近的成本数据结构:', latestData);
  
          // 检查成本字段是否存在且不为0
          const feedCost = latestData.feedCost || latestData.feed_cost || 0;
          const biologyCost = latestData.biologyCost || latestData.biology_cost || 0;
          const medicineCost = latestData.medicineCost || latestData.medicine_cost || 0;
          const deviceCost = latestData.deviceCost || latestData.device_cost || 0;
          const energyCost = latestData.energyCost || latestData.energy_cost || 0;
          const extraCost = latestData.extraCost || latestData.extra_cost || 0;
  
          console.log('解析后的成本数据:', {
            feedCost,
            biologyCost,
            medicineCost,
            deviceCost,
            energyCost,
            extraCost
          });
  
          const hasValidData = [feedCost, biologyCost, medicineCost, deviceCost, energyCost, extraCost].some(cost => cost > 0);
          console.log('是否有有效数据:', hasValidData);
          if (hasValidData) {
            // 更新饼图数据 - 直接修改数组元素而不是替换整个数组
            this.pieChartData1.data[0].value = feedCost;
            this.pieChartData1.data[1].value = biologyCost;
            this.pieChartData1.data[2].value = medicineCost;
            this.pieChartData1.data[3].value = deviceCost;
            this.pieChartData1.data[4].value = energyCost;
            this.pieChartData1.data[5].value = extraCost;
             
            console.log('更新后的饼图数据:', this.pieChartData1.data);
            // 更新图例
            this.pieChartData1.legend = ['饲料成本', '鱼苗鱼卵', '动保成本', '设备成本', '能源成本', '额外成本'];
            this.pieChartOptions1.legend.data = this.pieChartData1.legend;
          } else {
            console.warn('成本数据全为0或不存在:', latestData);
            // 直接修改数组元素
            this.pieChartData1.data = [
              { value: 1, name: '成本数据全为0' }
            ];
            this.pieChartData1.legend = ['成本数据全为0'];
            this.pieChartOptions1.legend.data = ['成本数据全为0'];
          }
  
          // 触发视图更新
          this.$forceUpdate();
          console.log('视图已强制更新');
          // 手动触发图表重新渲染
          this.$nextTick(() => {
            // 优先使用已初始化的组件引用
            if (this.pieChartComponent && this.pieChartComponent.initChart) {
              console.log('使用mounted中初始化的组件引用调用图表初始化方法');
              this.pieChartComponent.initChart();
            } else {
              // 如果组件引用不存在或initChart方法未定义，尝试通过$refs重新获取
              console.warn('饼图组件引用不存在或initChart方法未定义，尝试通过$refs重新获取');
              if (this.$refs.pieChartComponent) {
                console.log('通过$refs获取到组件引用');
                this.pieChartComponent = this.$refs.pieChartComponent;
                if (this.pieChartComponent.initChart) {
                  console.log('调用图表初始化方法');
                  this.pieChartComponent.initChart();
                } else {
                  console.error('通过$refs获取的组件没有initChart方法');
                }
              } else {
                console.error('未找到饼图组件引用，请检查模板中是否添加了ref="pieChartComponent"');
              }
            }
          });
        } else {
          console.warn('没有找到成本数据或响应格式不正确:', response);
          // 如果没有数据，显示空状态
          this.pieChartData1.data = [
            { value: 1, name: '暂无数据' }
          ];
          this.pieChartData1.legend = ['暂无数据'];
          this.pieChartOptions1.legend.data = ['暂无数据'];
          this.$forceUpdate();
        }
      }).catch(error => {
        console.error('获取成本数据失败:', error);
        // 错误处理
        this.pieChartData1.data = [
          { value: 1, name: '数据加载失败' }
        ];
        this.pieChartData1.legend = ['数据加载失败'];
        this.pieChartOptions1.legend.data = ['数据加载失败'];
        this.$forceUpdate();
      });
    },
    /** 查询列表 */
    getZkWorkPlanList() {
      this.loading = true;
      // 执行查询
      getZkWorkPlanPage(this.queryParams).then(response => {
        let list = response.data.list;
        this.planText = list[0];
        this.loading = false;
      });
    },
  }
}
</script>

<style scoped>
  /* 全局黑蓝色主题强制应用 */
  /* 使用深度选择器确保样式能够穿透到 Element UI 组件 */
  ::v-deep .el-container {
    background-color: #0a1520 !important;
    min-height: 100vh;
  }
  
  /* 确保嵌套的容器也是深色 */
  ::v-deep .el-container .el-container {
    background-color: #0a1520 !important;
  }
  
  /* 覆盖所有可能的白色背景容器 */
  ::v-deep .dashboard-editor-container {
    background-color: #0a1520 !important;
    background: #0a1520 !important;
  }
  
  ::v-deep [class*="dashboard"],
  ::v-deep [class*="editor-container"],
  ::v-deep [class*="dashboard-editor"] {
    background-color: #0a1520 !important;
    background: #0a1520 !important;
  }
  
  /* 覆盖内联样式中的浅色背景 #f0f2f5 */
  ::v-deep [style*="background-color: #f0f2f5"],
  ::v-deep [style*="background-color:#f0f2f5"],
  ::v-deep [style*="background: #f0f2f5"],
  ::v-deep [style*="background:#f0f2f5"] {
    background-color: #0a1520 !important;
    background: #0a1520 !important;
  }
  
  /* 确保页面根元素也是深色 */
  ::v-deep section,
  ::v-deep div[class*="container"]:not([class*="el-"]),
  ::v-deep div[class*="wrapper"]:not([class*="el-"]) {
    background-color: transparent !important;
  }
  
  .header-container {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 60px !important;
    background-color: #0a1520 !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }

  .header-buttons {
    display: flex;
    width: 100%;
    max-width: 1200px;
    justify-content: space-around;
    gap: 12px;
  }

  /* 头部按钮基础样式 - 现代化设计 */
  .header-btn {
    font-size: 16px;
    padding: 12px 20px;
    min-width: 180px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    gap: 10px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    color: #ffffff !important;
    border-radius: 12px !important;
    position: relative;
    overflow: hidden;
    backdrop-filter: blur(10px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.2) !important;
  }

  /* 按钮图标样式 */
  .header-btn i {
    font-size: 20px;
    color: #ffffff !important;
    flex-shrink: 0;
    transition: all 0.3s ease;
  }

  /* 按钮文字样式 */
  .header-btn span {
    color: #ffffff !important;
    font-weight: 500;
    white-space: nowrap;
  }

  /* 蓝色渐变按钮 - 存量分布 */
  .header-btn.btn-blue {
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.3) 0%, rgba(64, 158, 255, 0.15) 100%) !important;
    border-color: rgba(64, 158, 255, 0.4) !important;
  }

  /* 紫色渐变按钮 - 智能巡检系统 */
  .header-btn.btn-purple {
    background: linear-gradient(135deg, rgba(147, 51, 234, 0.3) 0%, rgba(147, 51, 234, 0.15) 100%) !important;
    border-color: rgba(147, 51, 234, 0.4) !important;
  }

  /* 粉色渐变按钮 - 智能分析报告 */
  .header-btn.btn-pink {
    background: linear-gradient(135deg, rgba(236, 72, 153, 0.3) 0%, rgba(236, 72, 153, 0.15) 100%) !important;
    border-color: rgba(236, 72, 153, 0.4) !important;
  }

  /* 红色渐变按钮 - 鱼病智能化诊断模型 */
  .header-btn.btn-red {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.3) 0%, rgba(239, 68, 68, 0.15) 100%) !important;
    border-color: rgba(239, 68, 68, 0.4) !important;
  }

  /* 绿色渐变按钮 - 远程专家与智能会议系统 */
  .header-btn.btn-green {
    background: linear-gradient(135deg, rgba(34, 197, 94, 0.3) 0%, rgba(34, 197, 94, 0.15) 100%) !important;
    border-color: rgba(34, 197, 94, 0.4) !important;
  }

  /* 按钮内部光晕效果 */
  .header-btn::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(255, 255, 255, 0.1) 50%, 
      transparent 100%);
    transition: left 0.5s ease;
  }

  /* 悬浮状态 - 增强的视觉效果 */
  .header-btn:hover {
    color: #ffffff !important;
    transform: translateY(-3px) scale(1.02);
    z-index: 10;
  }

  /* 悬浮时图标动画 */
  .header-btn:hover i {
    transform: scale(1.1);
  }

  /* 蓝色按钮悬浮效果 */
  .header-btn.btn-blue:hover {
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.4) 0%, rgba(64, 158, 255, 0.25) 100%) !important;
    border-color: rgba(64, 158, 255, 0.7) !important;
    box-shadow: 0 8px 24px rgba(64, 158, 255, 0.5),
                0 0 0 1px rgba(64, 158, 255, 0.4) inset;
  }

  /* 紫色按钮悬浮效果 */
  .header-btn.btn-purple:hover {
    background: linear-gradient(135deg, rgba(147, 51, 234, 0.4) 0%, rgba(147, 51, 234, 0.25) 100%) !important;
    border-color: rgba(147, 51, 234, 0.7) !important;
    box-shadow: 0 8px 24px rgba(147, 51, 234, 0.5),
                0 0 0 1px rgba(147, 51, 234, 0.4) inset;
  }

  /* 粉色按钮悬浮效果 */
  .header-btn.btn-pink:hover {
    background: linear-gradient(135deg, rgba(236, 72, 153, 0.4) 0%, rgba(236, 72, 153, 0.25) 100%) !important;
    border-color: rgba(236, 72, 153, 0.7) !important;
    box-shadow: 0 8px 24px rgba(236, 72, 153, 0.5),
                0 0 0 1px rgba(236, 72, 153, 0.4) inset;
  }

  /* 红色按钮悬浮效果 */
  .header-btn.btn-red:hover {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.4) 0%, rgba(239, 68, 68, 0.25) 100%) !important;
    border-color: rgba(239, 68, 68, 0.7) !important;
    box-shadow: 0 8px 24px rgba(239, 68, 68, 0.5),
                0 0 0 1px rgba(239, 68, 68, 0.4) inset;
  }

  /* 绿色按钮悬浮效果 */
  .header-btn.btn-green:hover {
    background: linear-gradient(135deg, rgba(34, 197, 94, 0.4) 0%, rgba(34, 197, 94, 0.25) 100%) !important;
    border-color: rgba(34, 197, 94, 0.7) !important;
    box-shadow: 0 8px 24px rgba(34, 197, 94, 0.5),
                0 0 0 1px rgba(34, 197, 94, 0.4) inset;
  }

  /* 悬浮时的光晕动画 */
  .header-btn:hover::before {
    left: 100%;
  }

  /* 悬浮时的外发光效果 - 蓝色 */
  .header-btn.btn-blue:hover::after {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(135deg, 
      rgba(64, 158, 255, 0.6) 0%, 
      rgba(64, 158, 255, 0.3) 50%,
      rgba(64, 158, 255, 0.6) 100%);
    border-radius: 12px;
    z-index: -1;
    opacity: 0.6;
    animation: border-glow-blue 2s ease-in-out infinite;
  }

  /* 悬浮时的外发光效果 - 紫色 */
  .header-btn.btn-purple:hover::after {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(135deg, 
      rgba(147, 51, 234, 0.6) 0%, 
      rgba(147, 51, 234, 0.3) 50%,
      rgba(147, 51, 234, 0.6) 100%);
    border-radius: 12px;
    z-index: -1;
    opacity: 0.6;
    animation: border-glow-purple 2s ease-in-out infinite;
  }

  /* 悬浮时的外发光效果 - 粉色 */
  .header-btn.btn-pink:hover::after {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(135deg, 
      rgba(236, 72, 153, 0.6) 0%, 
      rgba(236, 72, 153, 0.3) 50%,
      rgba(236, 72, 153, 0.6) 100%);
    border-radius: 12px;
    z-index: -1;
    opacity: 0.6;
    animation: border-glow-pink 2s ease-in-out infinite;
  }

  /* 悬浮时的外发光效果 - 红色 */
  .header-btn.btn-red:hover::after {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(135deg, 
      rgba(239, 68, 68, 0.6) 0%, 
      rgba(239, 68, 68, 0.3) 50%,
      rgba(239, 68, 68, 0.6) 100%);
    border-radius: 12px;
    z-index: -1;
    opacity: 0.6;
    animation: border-glow-red 2s ease-in-out infinite;
  }

  /* 悬浮时的外发光效果 - 绿色 */
  .header-btn.btn-green:hover::after {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(135deg, 
      rgba(34, 197, 94, 0.6) 0%, 
      rgba(34, 197, 94, 0.3) 50%,
      rgba(34, 197, 94, 0.6) 100%);
    border-radius: 12px;
    z-index: -1;
    opacity: 0.6;
    animation: border-glow-green 2s ease-in-out infinite;
  }

  /* 选中/激活状态 - 强烈的发光效果 */
  .header-btn:active,
  .header-btn.is-active,
  .header-btn.active,
  .header-btn:focus {
    color: #ffffff !important;
    transform: translateY(-1px) scale(1.01);
  }

  /* 选中时图标动画 */
  .header-btn:active i,
  .header-btn.is-active i,
  .header-btn.active i {
    transform: scale(1.05);
  }

  /* 蓝色按钮选中效果 */
  .header-btn.btn-blue:active,
  .header-btn.btn-blue.is-active,
  .header-btn.btn-blue.active,
  .header-btn.btn-blue:focus {
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.5) 0%, rgba(64, 158, 255, 0.35) 100%) !important;
    border-color: rgba(64, 158, 255, 0.9) !important;
    box-shadow: 0 0 30px rgba(64, 158, 255, 0.7),
                0 8px 20px rgba(64, 158, 255, 0.5),
                0 0 0 2px rgba(64, 158, 255, 0.6) inset;
  }

  /* 紫色按钮选中效果 */
  .header-btn.btn-purple:active,
  .header-btn.btn-purple.is-active,
  .header-btn.btn-purple.active,
  .header-btn.btn-purple:focus {
    background: linear-gradient(135deg, rgba(147, 51, 234, 0.5) 0%, rgba(147, 51, 234, 0.35) 100%) !important;
    border-color: rgba(147, 51, 234, 0.9) !important;
    box-shadow: 0 0 30px rgba(147, 51, 234, 0.7),
                0 8px 20px rgba(147, 51, 234, 0.5),
                0 0 0 2px rgba(147, 51, 234, 0.6) inset;
  }

  /* 粉色按钮选中效果 */
  .header-btn.btn-pink:active,
  .header-btn.btn-pink.is-active,
  .header-btn.btn-pink.active,
  .header-btn.btn-pink:focus {
    background: linear-gradient(135deg, rgba(236, 72, 153, 0.5) 0%, rgba(236, 72, 153, 0.35) 100%) !important;
    border-color: rgba(236, 72, 153, 0.9) !important;
    box-shadow: 0 0 30px rgba(236, 72, 153, 0.7),
                0 8px 20px rgba(236, 72, 153, 0.5),
                0 0 0 2px rgba(236, 72, 153, 0.6) inset;
  }

  /* 红色按钮选中效果 */
  .header-btn.btn-red:active,
  .header-btn.btn-red.is-active,
  .header-btn.btn-red.active,
  .header-btn.btn-red:focus {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.5) 0%, rgba(239, 68, 68, 0.35) 100%) !important;
    border-color: rgba(239, 68, 68, 0.9) !important;
    box-shadow: 0 0 30px rgba(239, 68, 68, 0.7),
                0 8px 20px rgba(239, 68, 68, 0.5),
                0 0 0 2px rgba(239, 68, 68, 0.6) inset;
  }

  /* 绿色按钮选中效果 */
  .header-btn.btn-green:active,
  .header-btn.btn-green.is-active,
  .header-btn.btn-green.active,
  .header-btn.btn-green:focus {
    background: linear-gradient(135deg, rgba(34, 197, 94, 0.5) 0%, rgba(34, 197, 94, 0.35) 100%) !important;
    border-color: rgba(34, 197, 94, 0.9) !important;
    box-shadow: 0 0 30px rgba(34, 197, 94, 0.7),
                0 8px 20px rgba(34, 197, 94, 0.5),
                0 0 0 2px rgba(34, 197, 94, 0.6) inset;
  }

  /* 选中状态的底部发光条 - 蓝色 */
  .header-btn.btn-blue:active::after,
  .header-btn.btn-blue.is-active::after,
  .header-btn.btn-blue.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(64, 158, 255, 0.9) 20%, 
      rgba(64, 158, 255, 1) 50%, 
      rgba(64, 158, 255, 0.9) 80%, 
      transparent 100%);
    box-shadow: 0 0 15px rgba(64, 158, 255, 1),
                0 0 30px rgba(64, 158, 255, 0.6);
    animation: glow-pulse-blue 2s ease-in-out infinite;
    border-radius: 0 0 12px 12px;
  }

  /* 选中状态的底部发光条 - 紫色 */
  .header-btn.btn-purple:active::after,
  .header-btn.btn-purple.is-active::after,
  .header-btn.btn-purple.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(147, 51, 234, 0.9) 20%, 
      rgba(147, 51, 234, 1) 50%, 
      rgba(147, 51, 234, 0.9) 80%, 
      transparent 100%);
    box-shadow: 0 0 15px rgba(147, 51, 234, 1),
                0 0 30px rgba(147, 51, 234, 0.6);
    animation: glow-pulse-purple 2s ease-in-out infinite;
    border-radius: 0 0 12px 12px;
  }

  /* 选中状态的底部发光条 - 粉色 */
  .header-btn.btn-pink:active::after,
  .header-btn.btn-pink.is-active::after,
  .header-btn.btn-pink.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(236, 72, 153, 0.9) 20%, 
      rgba(236, 72, 153, 1) 50%, 
      rgba(236, 72, 153, 0.9) 80%, 
      transparent 100%);
    box-shadow: 0 0 15px rgba(236, 72, 153, 1),
                0 0 30px rgba(236, 72, 153, 0.6);
    animation: glow-pulse-pink 2s ease-in-out infinite;
    border-radius: 0 0 12px 12px;
  }

  /* 选中状态的底部发光条 - 红色 */
  .header-btn.btn-red:active::after,
  .header-btn.btn-red.is-active::after,
  .header-btn.btn-red.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(239, 68, 68, 0.9) 20%, 
      rgba(239, 68, 68, 1) 50%, 
      rgba(239, 68, 68, 0.9) 80%, 
      transparent 100%);
    box-shadow: 0 0 15px rgba(239, 68, 68, 1),
                0 0 30px rgba(239, 68, 68, 0.6);
    animation: glow-pulse-red 2s ease-in-out infinite;
    border-radius: 0 0 12px 12px;
  }

  /* 选中状态的底部发光条 - 绿色 */
  .header-btn.btn-green:active::after,
  .header-btn.btn-green.is-active::after,
  .header-btn.btn-green.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(34, 197, 94, 0.9) 20%, 
      rgba(34, 197, 94, 1) 50%, 
      rgba(34, 197, 94, 0.9) 80%, 
      transparent 100%);
    box-shadow: 0 0 15px rgba(34, 197, 94, 1),
                0 0 30px rgba(34, 197, 94, 0.6);
    animation: glow-pulse-green 2s ease-in-out infinite;
    border-radius: 0 0 12px 12px;
  }

  /* 边框发光动画 - 蓝色 */
  @keyframes border-glow-blue {
    0%, 100% {
      opacity: 0.4;
      filter: blur(4px);
    }
    50% {
      opacity: 0.8;
      filter: blur(6px);
    }
  }

  /* 边框发光动画 - 紫色 */
  @keyframes border-glow-purple {
    0%, 100% {
      opacity: 0.4;
      filter: blur(4px);
    }
    50% {
      opacity: 0.8;
      filter: blur(6px);
    }
  }

  /* 边框发光动画 - 粉色 */
  @keyframes border-glow-pink {
    0%, 100% {
      opacity: 0.4;
      filter: blur(4px);
    }
    50% {
      opacity: 0.8;
      filter: blur(6px);
    }
  }

  /* 边框发光动画 - 红色 */
  @keyframes border-glow-red {
    0%, 100% {
      opacity: 0.4;
      filter: blur(4px);
    }
    50% {
      opacity: 0.8;
      filter: blur(6px);
    }
  }

  /* 边框发光动画 - 绿色 */
  @keyframes border-glow-green {
    0%, 100% {
      opacity: 0.4;
      filter: blur(4px);
    }
    50% {
      opacity: 0.8;
      filter: blur(6px);
    }
  }

  /* 底部发光脉冲动画 - 蓝色 */
  @keyframes glow-pulse-blue {
    0%, 100% {
      opacity: 0.9;
      box-shadow: 0 0 15px rgba(64, 158, 255, 1),
                  0 0 30px rgba(64, 158, 255, 0.6);
    }
    50% {
      opacity: 1;
      box-shadow: 0 0 20px rgba(64, 158, 255, 1),
                  0 0 40px rgba(64, 158, 255, 0.8);
    }
  }

  /* 底部发光脉冲动画 - 紫色 */
  @keyframes glow-pulse-purple {
    0%, 100% {
      opacity: 0.9;
      box-shadow: 0 0 15px rgba(147, 51, 234, 1),
                  0 0 30px rgba(147, 51, 234, 0.6);
    }
    50% {
      opacity: 1;
      box-shadow: 0 0 20px rgba(147, 51, 234, 1),
                  0 0 40px rgba(147, 51, 234, 0.8);
    }
  }

  /* 底部发光脉冲动画 - 粉色 */
  @keyframes glow-pulse-pink {
    0%, 100% {
      opacity: 0.9;
      box-shadow: 0 0 15px rgba(236, 72, 153, 1),
                  0 0 30px rgba(236, 72, 153, 0.6);
    }
    50% {
      opacity: 1;
      box-shadow: 0 0 20px rgba(236, 72, 153, 1),
                  0 0 40px rgba(236, 72, 153, 0.8);
    }
  }

  /* 底部发光脉冲动画 - 红色 */
  @keyframes glow-pulse-red {
    0%, 100% {
      opacity: 0.9;
      box-shadow: 0 0 15px rgba(239, 68, 68, 1),
                  0 0 30px rgba(239, 68, 68, 0.6);
    }
    50% {
      opacity: 1;
      box-shadow: 0 0 20px rgba(239, 68, 68, 1),
                  0 0 40px rgba(239, 68, 68, 0.8);
    }
  }

  /* 底部发光脉冲动画 - 绿色 */
  @keyframes glow-pulse-green {
    0%, 100% {
      opacity: 0.9;
      box-shadow: 0 0 15px rgba(34, 197, 94, 1),
                  0 0 30px rgba(34, 197, 94, 0.6);
    }
    50% {
      opacity: 1;
      box-shadow: 0 0 20px rgba(34, 197, 94, 1),
                  0 0 40px rgba(34, 197, 94, 0.8);
    }
  }

  /* Element UI 按钮样式覆盖 - 确保文字和图标为白色 */
  ::v-deep .header-btn.el-button--primary {
    color: #ffffff !important;
  }

  ::v-deep .header-btn.el-button--primary span {
    color: #ffffff !important;
  }

  ::v-deep .header-btn.el-button--primary i {
    color: #ffffff !important;
  }

  ::v-deep .header-btn.el-button--primary:hover {
    color: #ffffff !important;
  }

  ::v-deep .header-btn.el-button--primary:hover span {
    color: #ffffff !important;
  }

  ::v-deep .header-btn.el-button--primary:hover i {
    color: #ffffff !important;
  }

  ::v-deep .header-btn.el-button--primary:active,
  ::v-deep .header-btn.el-button--primary:focus {
    color: #ffffff !important;
  }

  ::v-deep .header-btn.el-button--primary:active span,
  ::v-deep .header-btn.el-button--primary:focus span {
    color: #ffffff !important;
  }

  ::v-deep .header-btn.el-button--primary:active i,
  ::v-deep .header-btn.el-button--primary:focus i {
    color: #ffffff !important;
  }

  /* 区块标题样式 - 与系统设备监测等标题保持一致 */
  .section-title {
    cursor: pointer;
    color: #ffffff !important;
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid transparent;
    background-image: linear-gradient(90deg,
      transparent 0%,
      rgba(64, 158, 255, 0.3) 20%,
      rgba(64, 158, 255, 0.6) 50%,
      rgba(64, 158, 255, 0.3) 80%,
      transparent 100%
    );
    background-clip: padding-box;
    border-image: linear-gradient(90deg,
      transparent 0%,
      rgba(64, 158, 255, 0.5) 20%,
      rgba(64, 158, 255, 0.8) 50%,
      rgba(64, 158, 255, 0.5) 80%,
      transparent 100%
    ) 1;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    display: flex;
    align-items: center;
    gap: 10px;
    text-align: left;
  }

  .section-title i {
    font-size: 24px;
    color: #409EFF !important;
    transition: all 0.3s ease;
    filter: drop-shadow(0 0 6px rgba(64, 158, 255, 0.6));
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .section-title span {
    color: #ffffff !important;
    font-weight: 700;
    text-shadow: 0 0 8px rgba(64, 158, 255, 0.4);
  }

  .section-title:hover {
    color: #66B1FF !important;
    transform: scale(1.02);
    text-shadow: 0 0 15px rgba(64, 158, 255, 0.7);
  }

  .section-title:hover i {
    transform: scale(1.15);
    color: #66B1FF !important;
    filter: drop-shadow(0 0 12px rgba(64, 158, 255, 0.9));
  }

  .section-title:hover span {
    color: #66B1FF !important;
    text-shadow: 0 0 15px rgba(64, 158, 255, 0.7);
  }

  .section-title:active {
    color: #85C1FF !important;
    transform: scale(0.98);
  }

  /* 滚动列表样式 */
  .scroll-list {
    width: 100%;
    height: 100%;
    min-height: 220px;
    overflow-y: auto;
    background: #0f1a26 !important;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
    padding: 16px;
    flex: 1;
  }
  .list-header {
    display: flex;
    font-weight: bold;
    border-bottom: 2px solid #409EFF;
    padding-bottom: 8px;
    margin-bottom: 12px;
    gap: 8px;
    color: #ffffff !important;
  }
  .list-header span,
  .list-row span {
    flex: 1;
    text-align: center;
    padding: 6px 0;
  }
  .list-body {
    height: 220px;
    overflow: hidden;
    position: relative;
  }
  .scroll-content {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    transition: top 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  }
  .list-row {
    display: flex;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding: 12px 0;
    margin-bottom: 6px;
    gap: 8px;
    line-height: 1.5;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    color: #ffffff !important;
    cursor: pointer;
    border-radius: 4px;
    margin-left: 0;
    margin-right: 0;
  }
  .list-row:hover {
    background: linear-gradient(90deg, 
      rgba(64, 158, 255, 0.2) 0%, 
      rgba(64, 158, 255, 0.1) 100%);
    transform: translateX(6px);
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
    border-bottom-color: rgba(64, 158, 255, 0.3);
  }
  .list-row:active {
    background: linear-gradient(90deg, 
      rgba(64, 158, 255, 0.25) 0%, 
      rgba(64, 158, 255, 0.15) 100%);
    transform: translateX(3px);
  }

  /* 监测区域样式 */
  .monitoring-row {
    margin-bottom: 20px;
  }

  .monitoring-row .list-col {
    padding: 0 10px;
  }

  .monitoring-row .list-col:first-child {
    padding-left: 0;
  }

  .monitoring-row .list-col:last-child {
    padding-right: 0;
  }

  /* 模块样式 - 第一行 */
  .row-moudle:first-of-type {
    height: 280px;
    margin-top: 20px;
    margin-bottom: 24px;
  }

  /* 模块样式 - 第二行 */
  .row-moudle:last-of-type {
    height: 280px;
    margin-bottom: 20px;
  }

  .row-moudle .el-col {
    height: 100%;
    padding: 0 12px;
  }
  
  /* 确保第一列和最后一列的间距 */
  .row-moudle .el-col:first-child {
    padding-left: 0;
    padding-right: 12px;
  }
  
  .row-moudle .el-col:last-child {
    padding-right: 0;
    padding-left: 12px;
  }

  .moudle {
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
    padding: 16px;
    box-sizing: border-box;
    /* 绚丽的深蓝灰色渐变背景 */
    background: linear-gradient(135deg, 
      rgba(10, 21, 32, 0.95) 0%, 
      rgba(15, 26, 38, 0.98) 50%,
      rgba(20, 35, 50, 0.95) 100%) !important;
    border-radius: 12px;
    /* 增强的阴影效果，营造立体感 */
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.6),
      0 0 0 1px rgba(64, 158, 255, 0.15) inset,
      0 2px 8px rgba(64, 158, 255, 0.1);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    /* 绚丽的浅蓝色边框 */
    border: 2px solid rgba(64, 158, 255, 0.25);
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  
  /* 卡片内部光晕效果 */
  .moudle::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(
      circle,
      rgba(64, 158, 255, 0.15) 0%,
      rgba(64, 158, 255, 0.05) 40%,
      transparent 70%
    );
    opacity: 0;
    transition: opacity 0.4s ease;
    pointer-events: none;
  }
  
  /* 卡片底部发光条 */
  .moudle::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg,
      transparent 0%,
      rgba(64, 158, 255, 0.5) 20%,
      rgba(64, 158, 255, 0.8) 50%,
      rgba(64, 158, 255, 0.5) 80%,
      transparent 100%
    );
    opacity: 0.6;
    border-radius: 0 0 12px 12px;
    transition: opacity 0.4s ease;
  }
  
  .moudle:hover {
    transform: translateY(-8px) scale(1.02);
    /* 增强的悬浮阴影和发光效果 */
    box-shadow: 
      0 16px 48px rgba(64, 158, 255, 0.4),
      0 0 0 2px rgba(64, 158, 255, 0.3) inset,
      0 4px 16px rgba(64, 158, 255, 0.3),
      0 0 40px rgba(64, 158, 255, 0.2);
    /* 更亮的边框 */
    border-color: rgba(64, 158, 255, 0.5);
    /* 更亮的背景渐变 */
    background: linear-gradient(135deg, 
      rgba(15, 26, 38, 0.98) 0%, 
      rgba(20, 35, 50, 1) 50%,
      rgba(25, 40, 58, 0.98) 100%) !important;
  }
  
  /* 悬浮时显示内部光晕 */
  .moudle:hover::before {
    opacity: 1;
  }
  
  /* 悬浮时增强底部发光条 */
  .moudle:hover::after {
    opacity: 1;
    height: 4px;
    box-shadow: 0 0 20px rgba(64, 158, 255, 0.8);
  }
  
  .moudle:active {
    transform: translateY(-4px) scale(1.01);
    box-shadow: 
      0 12px 36px rgba(64, 158, 255, 0.35),
      0 0 0 2px rgba(64, 158, 255, 0.4) inset;
  }

  .moudle h1 {
    margin-bottom: 12px;
    text-align: center;
    font-size: 16px;
    color: #ffffff !important;
    font-weight: 700;
    padding: 8px 0;
    /* 绚丽的标题底部边框 */
    border-bottom: 2px solid transparent;
    background-image: linear-gradient(90deg,
      transparent 0%,
      rgba(64, 158, 255, 0.3) 20%,
      rgba(64, 158, 255, 0.6) 50%,
      rgba(64, 158, 255, 0.3) 80%,
      transparent 100%
    );
    background-clip: padding-box;
    border-image: linear-gradient(90deg,
      transparent 0%,
      rgba(64, 158, 255, 0.5) 20%,
      rgba(64, 158, 255, 0.8) 50%,
      rgba(64, 158, 255, 0.5) 80%,
      transparent 100%
    ) 1;
    flex-shrink: 0;
    position: relative;
    text-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
    transition: all 0.3s ease;
  }
  
  /* 标题悬浮效果 */
  .moudle:hover h1 {
    color: #66B1FF !important;
    text-shadow: 0 0 15px rgba(64, 158, 255, 0.8),
                 0 0 30px rgba(64, 158, 255, 0.4);
    transform: scale(1.05);
  }

  .moudle >>> .bar-chart,
  .moudle >>> .line-chart,
  .moudle >>> .pie-chart {
    width: 100%;
    height: 100%;
    background: transparent !important;
  }
  
  /* 图表容器增强样式 */
  .moudle >>> .chart {
    background: transparent !important;
  }

  /* 容器样式 - 使用深度选择器确保样式生效 */
  ::v-deep .el-header {
    background-color: #0a1520 !important;
    color: #ffffff !important;
    text-align: center;
    line-height: 60px !important;
    height: 60px !important;
    min-height: 60px !important;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    position: sticky;
    top: 0;
    z-index: 10;
  }

  ::v-deep .el-aside {
    background-color: #0a1520 !important;
    color: #ffffff !important;
    text-align: center;
    line-height: normal;
    padding: 24px;
    border-left: 1px solid rgba(255, 255, 255, 0.1);
  }

  ::v-deep .el-main {
    background-color: #0a1520 !important;
    color: #ffffff !important;
    text-align: center;
    line-height: 1.5;
    padding: 20px;
  }

  body > .el-container {
    margin-bottom: 0;
    background-color: #0a1520 !important;
  }
  
  /* 覆盖所有可能的白色背景容器（已在上面定义，这里删除重复） */

  /* 紧凑告警弹窗样式 */
  .compact-alarm-dialog .el-dialog__body {
    padding: 15px 20px;
    max-height: 300px;
    overflow-y: auto;
  }

  .compact-alarm-dialog .el-dialog__header {
    padding: 10px 20px;
    background-color: #0f1a26 !important;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
    color: #ffffff !important;
  }

  .compact-alarm-dialog .el-dialog__headerbtn {
    top: 10px;
  }

  .alarm-message-enhanced.compact-layout {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  /* 告警级别指示器 */
  .alarm-level-indicator {
    display: inline-flex;
    align-items: center;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 14px;
    margin-bottom: 8px;
  }

  .alarm-level-indicator i {
    margin-right: 6px;
  }

  .alarm-level-indicator.level-error {
    background-color: rgba(245, 108, 108, 0.2) !important;
    color: #F56C6C !important;
    border: 1px solid rgba(245, 108, 108, 0.3) !important;
  }

  .alarm-level-indicator.level-warning {
    background-color: rgba(230, 162, 60, 0.2) !important;
    color: #E6A23C !important;
    border: 1px solid rgba(230, 162, 60, 0.3) !important;
  }

  .alarm-level-indicator.level-info {
    background-color: rgba(64, 158, 255, 0.2) !important;
    color: #409EFF !important;
    border: 1px solid rgba(64, 158, 255, 0.3) !important;
  }

  /* 主要告警信息 */
  .alarm-main-info {
    margin-bottom: 10px;
  }

  .alarm-item {
    padding: 8px;
    background-color: rgba(255, 255, 255, 0.05) !important;
    border-radius: 4px;
    margin-bottom: 6px;
    font-size: 14px;
    color: #ffffff !important;
  }

  .alarm-item.primary {
    background-color: rgba(64, 158, 255, 0.1) !important;
    border-left: 3px solid #409EFF !important;
    font-weight: 500;
    color: #ffffff !important;
  }

  /* 详细信息网格布局 */
  .alarm-details-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    margin-bottom: 10px;
  }

  .detail-item {
    display: flex;
    flex-direction: column;
    font-size: 13px;
  }

  .detail-label {
    color: rgba(255, 255, 255, 0.6) !important;
    font-size: 12px;
    margin-bottom: 2px;
  }

  .detail-value {
    color: #ffffff !important;
  }

  /* 原始数据切换按钮 */
  .original-data-toggle {
    text-align: right;
  }

  .original-data-btn {
    font-size: 12px;
    color: #409eff;
    padding: 0;
    height: auto;
    line-height: normal;
  }

  /* 紧凑底部按钮布局 */
  .dialog-footer.compact-footer {
    padding: 10px 20px;
    text-align: right;
    border-top: 1px solid rgba(255, 255, 255, 0.1) !important;
    background-color: #0f1a26 !important;
  }

  .dialog-footer.compact-footer .el-button {
    margin-left: 8px;
  }

  /* 响应式调整 */
  @media (max-width: 540px) {
    .alarm-details-grid {
      grid-template-columns: 1fr;
    }
  }


  /* 列样式 */
  .el-col {
    min-height: 300px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    background-color: transparent;
  }

  .col-monitor {
    height: 280px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: transparent;
  }

  /* 导航按钮样式 */
  .el-header .el-button {
    margin: 0 15px;
    height: 36px;
    line-height: 36px;
    padding: 0 16px;
    font-size: 14px;
    background-color: rgba(64, 158, 255, 0.2) !important;
    background: rgba(64, 158, 255, 0.2) !important;
    color: #409EFF !important;
    border-color: rgba(64, 158, 255, 0.5) !important;
    transition: var(--transition);
  }
  .el-header .el-button:hover {
    background-color: rgba(64, 158, 255, 0.3) !important;
    background: rgba(64, 158, 255, 0.3) !important;
    color: #66B1FF !important;
    transform: translateY(-2px);
  }

  /* 侧边栏样式 */
  .el-aside .el-row {
    height: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
    gap: 20px;
  }

  .col-moudle {
    min-height: 280px;
    height: 100%;
    display: flex;
    align-items: stretch;
    justify-content: center;
    background-color: transparent;
    padding: 0 12px;
  }

  .row-moudle .col-moudle:first-child {
    padding-left: 0;
    padding-right: 12px;
  }

  .row-moudle .col-moudle:last-child {
    padding-right: 0;
    padding-left: 12px;
  }

  /* 测试告警按钮样式 */
  .test-alarm-button {
    position: absolute;
    right: 0;
    z-index: 10;
    transition: all 0.3s ease;
  }
  
  .test-alarm-button:focus {
    outline: none;
    box-shadow: 0 0 0 2px rgba(235, 87, 87, 0.2);
  }
  
  /* 警告区域样式 */
  .list-col {
    display: block !important;
    height: auto !important;
    min-height: auto !important;
    padding: 0 10px;
  }

  .list-col:first-child {
    padding-left: 0;
  }

  .list-col:last-child {
    padding-right: 0;
  }
  .alert {
    background-color: #0f1a26 !important;
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    width: 100%;
    height: 100%;
    padding: 16px;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: flex-start;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
  }
  .alert h1 {
    margin-bottom: 15px;
    font-size: 18px;
    color: #ffffff !important;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    padding: 4px 0;
  }
  .alert h1:hover {
    color: #409EFF !important;
    transform: translateX(4px);
    text-shadow: 0 0 10px rgba(64, 158, 255, 0.6);
  }
  .alert h1:active {
    color: #66B1FF !important;
    transform: translateX(2px);
  }

  /* 状态颜色样式 */
  .list-row.alert-red {
    color: var(--danger-color);
    background-color: rgba(245, 108, 108, 0.15);
  }
  .list-row.alert-yellow {
    color: var(--warning-color);
    background-color: rgba(230, 162, 60, 0.15);
  }
  .list-row.alert-green {
    color: var(--success-color);
    background-color: rgba(103, 194, 58, 0.15);
  }

  /* 滚动条样式 */
  .list-body::-webkit-scrollbar {
    width: 6px;
  }
  .list-body::-webkit-scrollbar-thumb {
    background-color: #409EFF;
    border-radius: 3px;
  }
  .list-body::-webkit-scrollbar-track {
    background-color: rgba(255, 255, 255, 0.05);
  }
  
  /* 确保整个页面背景是深色 - 已在顶部定义 */

  /* 计划区域样式 */
  .plan-box {
    width: 100%;
    height: auto;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    gap: 0;
    background: linear-gradient(135deg, 
      rgba(10, 21, 32, 0.95) 0%, 
      rgba(15, 26, 38, 0.98) 100%) !important;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5),
                0 0 0 1px rgba(255, 255, 255, 0.05) inset;
    border: 1px solid rgba(255, 255, 255, 0.08);
    margin-bottom: 24px;
  }
  .plan-row {
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    justify-content: flex-start;
    width: 100%;
    min-height: 70px;
    background: linear-gradient(135deg, 
      rgba(15, 26, 38, 0.95) 0%, 
      rgba(20, 35, 50, 0.98) 100%);
    border-radius: 10px;
    padding: 16px 20px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: 2px solid transparent;
    cursor: pointer;
    position: relative;
    margin-bottom: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  }
  
  /* 日计划 - 浅蓝色 */
  .plan-row.plan-day {
    border-color: rgba(100, 200, 255, 0.4);
    box-shadow: 0 0 20px rgba(100, 200, 255, 0.3),
                0 4px 16px rgba(0, 0, 0, 0.3);
  }
  .plan-row.plan-day .plan-label,
  .plan-row.plan-day .plan-label i,
  .plan-row.plan-day .plan-label span {
    color: #64C8FF !important;
  }
  .plan-row.plan-day:hover {
    border-color: rgba(100, 200, 255, 0.7);
    box-shadow: 0 0 30px rgba(100, 200, 255, 0.5),
                0 6px 20px rgba(0, 0, 0, 0.4);
    transform: translateX(4px);
  }
  
  /* 周计划 - 蓝色 */
  .plan-row.plan-week {
    border-color: rgba(64, 158, 255, 0.4);
    box-shadow: 0 0 20px rgba(64, 158, 255, 0.3),
                0 4px 16px rgba(0, 0, 0, 0.3);
  }
  .plan-row.plan-week .plan-label,
  .plan-row.plan-week .plan-label i,
  .plan-row.plan-week .plan-label span {
    color: #409EFF !important;
  }
  .plan-row.plan-week:hover {
    border-color: rgba(64, 158, 255, 0.7);
    box-shadow: 0 0 30px rgba(64, 158, 255, 0.5),
                0 6px 20px rgba(0, 0, 0, 0.4);
    transform: translateX(4px);
  }
  
  /* 月度 - 紫色 */
  .plan-row.plan-month {
    border-color: rgba(147, 51, 234, 0.4);
    box-shadow: 0 0 20px rgba(147, 51, 234, 0.3),
                0 4px 16px rgba(0, 0, 0, 0.3);
  }
  .plan-row.plan-month .plan-label,
  .plan-row.plan-month .plan-label i,
  .plan-row.plan-month .plan-label span {
    color: #9333EA !important;
  }
  .plan-row.plan-month:hover {
    border-color: rgba(147, 51, 234, 0.7);
    box-shadow: 0 0 30px rgba(147, 51, 234, 0.5),
                0 6px 20px rgba(0, 0, 0, 0.4);
    transform: translateX(4px);
  }
  
  /* 季度 - 绿色 */
  .plan-row.plan-quarter {
    border-color: rgba(34, 197, 94, 0.4);
    box-shadow: 0 0 20px rgba(34, 197, 94, 0.3),
                0 4px 16px rgba(0, 0, 0, 0.3);
  }
  .plan-row.plan-quarter .plan-label,
  .plan-row.plan-quarter .plan-label i,
  .plan-row.plan-quarter .plan-label span {
    color: #22C55E !important;
  }
  .plan-row.plan-quarter:hover {
    border-color: rgba(34, 197, 94, 0.7);
    box-shadow: 0 0 30px rgba(34, 197, 94, 0.5),
                0 6px 20px rgba(0, 0, 0, 0.4);
    transform: translateX(4px);
  }
  
  /* 年度 - 橙色 */
  .plan-row.plan-year {
    border-color: rgba(249, 115, 22, 0.4);
    box-shadow: 0 0 20px rgba(249, 115, 22, 0.3),
                0 4px 16px rgba(0, 0, 0, 0.3);
  }
  .plan-row.plan-year .plan-label,
  .plan-row.plan-year .plan-label i,
  .plan-row.plan-year .plan-label span {
    color: #F97316 !important;
  }
  .plan-row.plan-year:hover {
    border-color: rgba(249, 115, 22, 0.7);
    box-shadow: 0 0 30px rgba(249, 115, 22, 0.5),
                0 6px 20px rgba(0, 0, 0, 0.4);
    transform: translateX(4px);
  }
  
  .plan-row .plan-label {
    flex: 0 0 100px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    gap: 12px;
    font-weight: 700;
    font-size: 16px;
    margin-right: 20px;
    padding-left: 4px;
  }
  .plan-row .plan-label i {
    font-size: 22px;
    transition: all 0.3s ease;
    flex-shrink: 0;
    filter: drop-shadow(0 0 4px currentColor);
  }
  .plan-row:hover .plan-label i {
    transform: scale(1.15);
    filter: drop-shadow(0 0 8px currentColor);
  }
  .plan-row .plan-label span {
    font-weight: 700;
    font-size: 16px;
  }
  .plan-row .plan-content {
    flex: 1 1 auto;
    display: flex;
    align-items: center;
    font-size: 16px;
    color: #ffffff !important;
    text-align: left;
    word-break: break-all;
    white-space: pre-line;
    overflow-wrap: break-word;
    line-height: 1.8;
  }
  .plan-textarea {
    width: 100%;
    background: transparent;
    border: none;
    color: #ffffff !important;
    font-size: 14px;
    resize: none;
    pointer-events: none;
  }
  .plan-textarea ::v-deep .el-textarea__inner {
    background: transparent;
    border: none;
    color: #ffffff !important;
  }

  /* 成本容器样式 */
  .cost-container {
    width: 100%;
    background: linear-gradient(135deg, 
      rgba(10, 21, 32, 0.95) 0%, 
      rgba(15, 26, 38, 0.98) 100%);
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5),
                0 0 0 1px rgba(255, 255, 255, 0.05) inset;
    border: 1px solid rgba(255, 255, 255, 0.08);
  }
  
  /* 成本区域标题样式 */
  .cost-section-title {
    cursor: pointer;
    color: #ffffff !important;
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid transparent;
    background-image: linear-gradient(90deg,
      transparent 0%,
      rgba(100, 200, 255, 0.3) 20%,
      rgba(100, 200, 255, 0.6) 50%,
      rgba(100, 200, 255, 0.3) 80%,
      transparent 100%
    );
    background-clip: padding-box;
    border-image: linear-gradient(90deg,
      transparent 0%,
      rgba(100, 200, 255, 0.5) 20%,
      rgba(100, 200, 255, 0.8) 50%,
      rgba(100, 200, 255, 0.5) 80%,
      transparent 100%
    ) 1;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    display: flex;
    align-items: center;
    gap: 10px;
    text-align: left;
  }

  .cost-section-title i {
    font-size: 24px;
    color: #64C8FF !important;
    transition: all 0.3s ease;
    filter: drop-shadow(0 0 6px rgba(100, 200, 255, 0.6));
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .cost-section-title span {
    color: #ffffff !important;
    font-weight: 700;
    text-shadow: 0 0 8px rgba(100, 200, 255, 0.4);
  }

  .cost-section-title:hover {
    color: #90D5FF !important;
    transform: scale(1.02);
    text-shadow: 0 0 15px rgba(100, 200, 255, 0.7);
  }

  .cost-section-title:hover i {
    transform: scale(1.15);
    color: #90D5FF !important;
    filter: drop-shadow(0 0 12px rgba(100, 200, 255, 0.9));
  }

  .cost-section-title:hover span {
    color: #90D5FF !important;
    text-shadow: 0 0 15px rgba(100, 200, 255, 0.7);
  }

  .cost-section-title:active {
    color: #B0E5FF !important;
    transform: scale(0.98);
  }
  
  /* 饼图容器样式 */
  .cost-row {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px 0;
    width: 100%;
    background-color: transparent;
  }

  .cost-row .el-col {
    min-height: auto;
    height: auto;
    width: 100%;
    background-color: transparent;
  }

  /* 确保饼图组件占满容器 */
  .cost-row .pie-chart {
    width: 100%;
    height: 360px;
  }

  /* 视频监控样式 */
  .video {
    width: 100%;
    height: 100%;
    background-color: rgba(15, 26, 38, 0.95);
    border-radius: 12px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    align-items: stretch;
    justify-content: flex-start;
    position: relative;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4),
                0 0 0 1px rgba(255, 255, 255, 0.05) inset;
    border: 1px solid rgba(255, 255, 255, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  .video:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.5),
                0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  }
  
  .video-wrapper {
    position: relative;
    width: 100%;
    flex: 1;
    overflow: hidden;
    background-color: #000;
  }
  
  .video-wrapper video {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
  
  /* LIVE 标签 */
  .live-badge {
    position: absolute;
    top: 12px;
    left: 12px;
    background-color: #F56C6C;
    color: #ffffff;
    font-size: 12px;
    font-weight: 700;
    padding: 4px 10px;
    border-radius: 4px;
    z-index: 10;
    box-shadow: 0 2px 8px rgba(245, 108, 108, 0.5);
    letter-spacing: 0.5px;
  }
  
  /* 视频信息区域 */
  .video-info {
    padding: 14px 16px;
    background: linear-gradient(135deg, 
      rgba(15, 26, 38, 0.98) 0%, 
      rgba(20, 35, 50, 0.95) 100%);
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
  }
  
  .video-info-main {
    flex: 1;
  }
  
  .video-name {
    color: #ffffff !important;
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 4px 0;
    line-height: 1.4;
  }
  
  .video-subtitle {
    color: rgba(255, 255, 255, 0.7) !important;
    font-size: 13px;
    margin: 0;
    line-height: 1.4;
  }
  
  /* 在线状态 */
  .video-status {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
  }
  
  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: #67C23A;
    box-shadow: 0 0 8px rgba(103, 194, 58, 0.6);
    animation: status-pulse 2s ease-in-out infinite;
  }
  
  @keyframes status-pulse {
    0%, 100% {
      opacity: 1;
      transform: scale(1);
    }
    50% {
      opacity: 0.8;
      transform: scale(1.1);
    }
  }
  
  .status-text {
    color: #67C23A !important;
    font-size: 13px;
    font-weight: 500;
  }

  /* 视频标题区域 */
  .video-title {
    margin: 15px 0 16px 20px;
    text-align: left;
  }
  
  .video-title a {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    color: #ffffff !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    text-decoration: none;
  }
  
  .video-title i {
    font-size: 22px;
    color: #90EE90 !important;
    filter: drop-shadow(0 0 6px rgba(144, 238, 144, 0.6));
    transition: all 0.3s ease;
  }
  
  .video-title h1 {
    font-size: 20px;
    color: #ffffff !important;
    font-weight: 700;
    margin: 0;
    display: inline-block;
    text-shadow: 0 0 8px rgba(144, 238, 144, 0.3);
  }
  
  .video-title a:hover {
    transform: translateX(4px);
  }
  
  .video-title a:hover i {
    color: #98FB98 !important;
    filter: drop-shadow(0 0 10px rgba(144, 238, 144, 0.9));
    transform: scale(1.1);
  }
  
  .video-title a:hover h1 {
    color: #90EE90 !important;
    text-shadow: 0 0 12px rgba(144, 238, 144, 0.6);
  }
  
  .video-title a:active {
    transform: translateX(2px);
  }

  /* 响应式调整 */
  @media screen and (max-width: 1200px) {
    .el-header .el-button {
      margin: 0 10px;
      padding: 0 12px;
      font-size: 12px;
    }
    .col-moudle {
      min-height: 220px;
    }
  }

  @media screen and (max-width: 992px) {
    ::v-deep .el-aside {
      width: 100% !important;
      border-left: none;
      border-top: 1px solid rgba(255, 255, 255, 0.1);
    }
    .el-row:nth-of-type(2) {
      height: auto;
    }
  }
  /* 告警弹窗样式 */
  .dialog-footer {
    text-align: center;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    padding-top: 15px;
  }
  
  /* 告警消息样式 */
  .alarm-message {
    font-size: 14px;
    line-height: 1.6;
    white-space: pre-line;
  }
  
  /* 增强版告警消息样式 */
  .alarm-message-enhanced {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  }
  
  .mb-20 {
    margin-bottom: 20px;
  }
  
  .mt-10 {
    margin-top: 10px;
  }
  
  .alarm-info-row {
    margin-bottom: 10px;
    line-height: 24px;
    display: flex;
    align-items: center;
  }
  
  .alarm-info-label {
    font-weight: 500;
    color: rgba(255, 255, 255, 0.6) !important;
    text-align: right;
    padding-right: 10px;
    min-width: 80px;
  }
  
  .alarm-info-value {
    color: #ffffff !important;
    word-break: break-all;
    flex: 1;
  }
  
  .alarm-fault-content {
    color: #F56C6C !important;
    font-weight: 500;
    padding: 10px 12px;
    background-color: rgba(245, 108, 108, 0.2) !important;
    border-radius: 4px;
    border-left: 4px solid #F56C6C !important;
    line-height: 1.5;
    word-break: break-all;
    min-height: 40px;
    margin-bottom: 15px;
  }
  
  .original-data-section {
    margin-top: 15px;
    border-top: 1px solid rgba(255, 255, 255, 0.1) !important;
    padding-top: 15px;
  }
  
  .original-data {
    background-color: rgba(255, 255, 255, 0.05) !important;
    border: 1px solid rgba(255, 255, 255, 0.1) !important;
    border-radius: 4px;
    padding: 10px;
    margin-top: 10px;
    font-family: 'Courier New', Courier, monospace;
    font-size: 12px;
    max-height: 200px;
    overflow: auto;
    white-space: pre-wrap;
    word-break: break-all;
    line-height: 1.4;
    color: #ffffff !important;
  }
  
  .el-alert {
    margin-bottom: 20px;
  }
</style>

