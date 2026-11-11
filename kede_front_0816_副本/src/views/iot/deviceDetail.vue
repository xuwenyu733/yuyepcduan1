<template>
  <div class="iot-device-detail-container" v-loading="loading">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><i class="el-icon-location"></i> 首页</el-breadcrumb-item>
        <el-breadcrumb-item>物联网管理</el-breadcrumb-item>
        <el-breadcrumb-item>设备卡片展示</el-breadcrumb-item>
        <el-breadcrumb-item>设备详情</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="header-right">
        <div v-if="deviceInfo.deviceName" class="device-name-title">
          {{ deviceInfo.deviceName }}
        </div>
        <el-button @click="goBack">
          <i class="el-icon-arrow-left"></i> 返回
        </el-button>
        <el-button type="primary" @click="refreshData">
          <i class="el-icon-refresh"></i> 刷新数据
        </el-button>
      </div>
    </div>
    
    <div class="device-info-panel">
      <el-card class="device-basic-info">
        <div slot="header" class="card-header">
          <span>设备基本信息</span>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">设备名称：</span>
            <span class="info-value">{{ deviceInfo.deviceName || '未知设备' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">设备ID：</span>
            <span class="info-value">{{ deviceInfo.deviceCode }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">指标ID：</span>
            <span class="info-value">{{ deviceInfo.metricId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">车间：</span>
            <span class="info-value">{{ deviceInfo.workshop }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">系统：</span>
            <span class="info-value">{{ deviceInfo.system }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">池塘标号：</span>
            <span class="info-value">{{ deviceInfo.pondNo }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">运行状态：</span>
            <el-tag :type="getStatusTagType(deviceInfo.status)">
              {{ getStatusText(deviceInfo.status) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="info-label">设备类型：</span>
            <span class="info-value">{{ deviceInfo.deviceType }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">最近更新：</span>
            <span class="info-value">{{ parseTime(deviceInfo.lastUpdateTime) }}</span>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="chart-container">
      <el-card class="chart-card">
        <div slot="header" class="card-header">
          <span>设备数据记录</span>
          <div class="chart-controls">
            <el-select v-model="timeRange" placeholder="选择时间范围" @change="handleTimeRangeChange" size="small">
              <el-option label="最近24小时" value="24h"></el-option>
              <el-option label="最近7天" value="7d"></el-option>
              <el-option label="最近30天" value="30d"></el-option>
              <el-option label="自定义" value="custom"></el-option>
            </el-select>
            <el-date-picker
              v-if="timeRange === 'custom'"
              v-model="customDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleCustomDateChange"
              size="small"
            ></el-date-picker>
          </div>
        </div>
        <div class="chart-wrapper">
          <div id="dataChart" ref="dataChart" class="chart"></div>
        </div>
        <!-- 分页拖动条 -->
        <div v-if="paginationVisible" class="pagination-slider" style="padding: 0 20px 20px;">
          <div class="pagination-info" style="margin-bottom: 10px;">
            <span>共 {{ totalRecords }} 条数据，每页显示 {{ pageSize }} 条，当前第 {{ currentPage }}/{{ totalPages }} 页</span>
          </div>
          <el-slider
            v-model="currentPage"
            :min="1"
            :max="totalPages"
            :step="1"
            :show-stops="true"
            @input="handlePageChange"
            style="width: 100%;"
          ></el-slider>
        </div>
      </el-card>
    </div>
    <div style="width: 1000px;height: 200px;"></div>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi';
import * as echarts from 'echarts';
import { getZkDevicePage } from "@/api/fish/zkDevice";
import { getZkDeviceRecordPage } from "@/api/fish/zkDeviceRecord";
import { throttle } from '@/utils';

export default {
  name: 'DeviceDetail',
  data() {
      return {
        deviceId: '',
        metricId: '',
        recordId: null,
        deviceInfo: {
          deviceCode: '',
          deviceName: '',
          metricId: '',
          workshop: '',
          system: '',
          pondNo: '',
          status: 'normal',
          deviceType: '',
          lastUpdateTime: ''
        },
        dataRecords: [],
        chartInstance: null,
        timeRange: '24h',
        customDateRange: [],
        loading: false,
        // 分页相关属性
        totalRecords: 0,
        currentPage: 1,
        pageSize: 500,
        totalPages: 1,
        paginationVisible: false,
        pageChangeTimeout: null
      };
    },
  created() {
    // 从URL获取设备ID、指标ID和记录ID
    this.deviceId = this.$route.params.id || this.$route.query.deviceId || this.$route.query.deviceCode || '';
    this.metricId = this.$route.query.metricId || '';
    this.recordId = this.$route.query.recordId || null;
    
    console.log('从路由获取的参数 - deviceId:', this.deviceId, 'metricId:', this.metricId, 'recordId:', this.recordId);
    
    // 如果有recordId，优先使用recordId作为查询唯一键
    if (this.recordId) {
      console.log('使用recordId作为查询唯一键:', this.recordId);
    }
    
    // 先获取设备基本信息，再获取数据记录
    this.getDeviceInfo().then(() => {
      this.getDeviceDataRecords();
    }).catch(() => {
      // 即使设备信息获取失败，也尝试获取数据记录
      this.getDeviceDataRecords();
    });
  },
  mounted() {
    this.initChart();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    if (this.chartInstance) {
      this.chartInstance.dispose();
    }
    // 清除定时器，避免内存泄漏
    if (this.pageChangeTimeout) {
      clearTimeout(this.pageChangeTimeout);
    }
  },
  
  watch: {
    // 监听路由变化，确保参数变化时重新加载数据
    '$route': {
      handler(to, from) {
        // 总是重新加载数据，不依赖参数比较
        console.log('路由发生变化，强制重新加载数据');
        console.log('原路由参数:', { id: from.params.id, deviceId: from.query.deviceId, metricId: from.query.metricId, recordId: from.query.recordId });
        console.log('新路由参数:', { id: to.params.id, deviceId: to.query.deviceId, metricId: to.query.metricId, recordId: to.query.recordId });
        
        // 重置数据，确保不使用旧数据
        this.deviceInfo = {
          deviceCode: '',
          deviceName: '',
          metricId: '',
          workshop: '',
          system: '',
          pondNo: '',
          status: 'normal',
          deviceType: '',
          lastUpdateTime: ''
        };
        this.dataRecords = [];
        // 重置分页状态
        this.currentPage = 1;
        this.totalRecords = 0;
        this.totalPages = 1;
        this.paginationVisible = false;
        
        // 更新参数并重新加载数据
        this.deviceId = to.params.id || to.query.deviceId || to.query.deviceCode || '';
        this.metricId = to.query.metricId || '';
        this.recordId = to.query.recordId || null;
        
        // 打印更新后的参数
        console.log('更新后的组件参数 - deviceId:', this.deviceId, 'metricId:', this.metricId, 'recordId:', this.recordId);
        
        // 重新获取数据
        this.getDeviceInfo().then(() => {
          console.log('设备信息获取成功，开始获取数据记录');
          this.getDeviceDataRecords();
        }).catch((error) => {
          console.error('获取设备信息失败，但仍尝试获取数据记录:', error);
          this.getDeviceDataRecords();
        });
      },
      immediate: false
    }
  },
  methods: {
    parseTime,
    
    // 返回上一页
    goBack() {
      this.$router.push('/iot/deviceCardShow');
    },
    
    /** 获取设备基本信息 */
    async getDeviceInfo() {
      // 验证必要参数
      if (!this.deviceId) {
        this.$message.warning('未指定设备ID');
        this.deviceInfo = {
          deviceCode: '',
          deviceName: '',
          metricId: this.metricId || '',
          workshop: '',
          system: '',
          pondNo: '',
          status: 'offline',
          deviceType: '',
          lastUpdateTime: ''
        };
        return Promise.reject(new Error('未指定设备ID'));
      }
      
      this.loading = true;
      try {
        // 使用与rullSetting.vue相同的查询参数结构
        const queryParams = {
          pageNo: 1,
          pageSize: 1,
          deviceSn: this.deviceId
        };
        
        // 如果有metricId，也作为查询条件
        if (this.metricId) {
          queryParams.deviceMetricId = this.metricId;
        }
        
        console.log('查询设备信息参数:', queryParams);
        const response = await getZkDevicePage(queryParams);
        
        if (!response.data || !response.data.list || response.data.list.length === 0) {
          console.error('获取设备信息失败: 设备数据为空');
          // 无数据保持空态（原 generateMockDeviceInfo 已废弃）
          this.deviceInfo = {
            deviceCode: this.deviceId || '',
            deviceName: '',
            metricId: this.metricId || '',
            workshop: '',
            system: '',
            pondNo: '',
            status: 'offline',
            deviceType: '',
            lastUpdateTime: ''
          };
          this.loading = false;
          return Promise.reject(new Error('设备数据为空'));
        }
        
        const deviceData = response.data.list[0];
        // 映射数据字段，与rullSetting.vue保持一致
        this.deviceInfo = {
          deviceCode: deviceData.deviceSn || '',
          deviceName: deviceData.deviceName || '',
          metricId: deviceData.deviceMetricId || '',
          workshop: deviceData.workshopId || '',
          system: this.getSystemNameByDeviceType(deviceData.deviceType || ''),
          pondNo: deviceData.pondDetailNum || '',
          status: this.mapStatus(deviceData.status),
          deviceType: deviceData.deviceType || '',
          lastUpdateTime: deviceData.createTime || ''
        };
        
        // 更新本地保存的metricId（如果从API返回中获取到）
        if (deviceData.deviceMetricId) {
          this.metricId = deviceData.deviceMetricId;
        }
        
        this.loading = false;
        return Promise.resolve();
      } catch (error) {
        console.error('获取设备信息失败:', error);
        this.$message.error('获取设备信息失败');
        this.loading = false;
        // 获取失败时保持空态，不回填模拟数据
        this.deviceInfo = {
          deviceCode: this.deviceId || '',
          deviceName: '',
          metricId: this.metricId || '',
          workshop: '',
          system: '',
          pondNo: '',
          status: 'offline',
          deviceType: '',
          lastUpdateTime: ''
        };
        return Promise.reject(error);
      }
    },
    
    /** 根据设备类型获取系统名称 */
    getSystemNameByDeviceType(deviceType) {
      const systemMap = {
        '温度传感器': '温度监控系统',
        '湿度传感器': '湿度监控系统',
        '压力传感器': '压力监控系统',
        '流量传感器': '流量监控系统',
        '液位传感器': '液位监控系统'
      };
      return systemMap[deviceType] || '未知监控系统';
    },
    
    // 映射状态值
    mapStatus(status) {
      switch (status) {
        case 1:
          return 'normal';
        case 0:
          return 'offline';
        case -1:
          return 'abnormal';
        default:
          return 'offline';
      }
    },
    
    /** 获取设备数据记录 */
    async getDeviceDataRecords() {
      // 使用zkDeviceRecord接口获取实际数据
      this.loading = true;
      
      // 使用设备唯一键作为查询条件
      const deviceId = this.deviceId || this.deviceInfo.deviceCode;
      const metricId = this.metricId || this.deviceInfo.metricId;
      
      console.log('准备获取设备数据记录，使用设备唯一键查询:', { deviceId, metricId, timeRange: this.timeRange });
      
      if (!deviceId) {
        console.warn('获取设备数据记录失败: 设备ID为空');
        this.$message.warning('获取设备数据记录失败: 设备ID为空');
        this.dataRecords = [];
        this.updateChart();
        this.loading = false;
        return;
      }
      
      if (!metricId) {
        console.warn('获取设备数据记录失败: 指标ID为空');
        this.$message.warning('获取设备数据记录失败: 指标ID为空');
        this.dataRecords = [];
        this.updateChart();
        this.loading = false;
        return;
      }
      
      // 准备查询参数，参考rullSetting.vue的查询方式
      const params = {
        pageNo: this.currentPage,
        pageSize: this.pageSize,
        deviceId: deviceId,
        metricId: metricId
      };
      
      // 增强日志，记录完整的查询参数
      console.log('API调用完整参数:', params);
      
      // 根据时间范围设置查询条件 - 格式化时间为后端可接受的格式
      // if (this.timeRange === '24h') {
      //   const endTime = new Date();
      //   const startTime = new Date();
      //   startTime.setHours(startTime.getHours() - 24);
      //   params.createTime = [this.formatDateTimeForAPI(startTime), this.formatDateTimeForAPI(endTime)];
      // } else if (this.timeRange === '7d') {
      //   const endTime = new Date();
      //   const startTime = new Date();
      //   startTime.setDate(startTime.getDate() - 7);
      //   params.createTime = [this.formatDateTimeForAPI(startTime), this.formatDateTimeForAPI(endTime)];
      // } else if (this.timeRange === '30d') {
      //   const endTime = new Date();
      //   const startTime = new Date();
      //   startTime.setDate(startTime.getDate() - 30);
      //   params.createTime = [this.formatDateTimeForAPI(startTime), this.formatDateTimeForAPI(endTime)];
      // } else if (this.timeRange === 'custom' && this.customDateRange.length > 0) {
      //   // 确保自定义时间范围也使用正确的格式
      //   params.createTime = [
      //     this.customDateRange[0] ? this.formatDateTimeForAPI(new Date(this.customDateRange[0])) : '',
      //     this.customDateRange[1] ? this.formatDateTimeForAPI(new Date(this.customDateRange[1])) : ''
      //   ];
      // }

      // 不设置时间限制条件，获取所有历史数据
      // 不再向API传递createTime参数，实现无时间限制的数据获取
      
      console.log('调用API参数:', params);
      
      try {
        console.log('调用API参数:', params);
        const response = await getZkDeviceRecordPage(params);
        console.log('API返回结果:', response);
        
        // 更新分页信息
        if (response.data && response.data.total) {
          this.totalRecords = response.data.total;
          this.totalPages = Math.ceil(this.totalRecords / this.pageSize);
          // 当总记录数超过当前页大小时，显示分页拖动条
          this.paginationVisible = this.totalRecords > this.pageSize;
        } else {
          this.totalRecords = 0;
          this.totalPages = 1;
          this.paginationVisible = false;
        }
        
        if (response.data && response.data.list && response.data.list.length > 0) {
          // 转换数据格式，严格使用recordTime作为横坐标，metricValue作为纵坐标
          this.dataRecords = response.data.list
            .map(record => ({
              timestamp: record.recordTime, // 严格使用recordTime
              value: parseFloat(record.metricValue) // 严格使用metricValue
            }))
            .filter(record => record.timestamp && !isNaN(record.value)) // 过滤无效数据
            .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp)); // 按时间排序
          
          console.log('处理后的数据记录数量:', this.dataRecords.length);
        } else {
          console.log('没有获取到数据，保持空态');
          this.dataRecords = [];
          this.totalRecords = 0;
          this.totalPages = 1;
          this.paginationVisible = false;
        }
        
        this.updateChart();
      } catch (error) {
        console.error('获取设备数据记录失败详情:', error);
        console.error('请求参数:', params);
        
        // 显示更详细的错误信息
        let errorMsg = '获取设备数据记录失败';
        if (error && error.message) {
          errorMsg += ': ' + error.message;
        } else if (error && error.toString) {
          errorMsg += ': ' + error.toString();
        }
        
        this.$message.error(errorMsg);
        this.dataRecords = [];
        this.updateChart();
      } finally {
        this.loading = false;
      }
    },

    /** 已废弃：不再使用模拟曲线 */
    generateMockDataRecords() {
      return [];
    },

    // 创建基于种子的确定性随机数生成器
    createDeterministicRandom(seed) {
      // 简单的线性同余生成器
      let state = seed % 2147483647;
      if (state <= 0) state += 2147483646;
      
      return function() {
        // 返回0到1之间的伪随机数
        state = state * 16807 % 2147483647;
        return (state - 1) / 2147483646;
      };
    },
    
    // 计算字符串的哈希值
    hashCode(str) {
      let hash = 0;
      if (str.length === 0) return hash;
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // 转换为32位整数
      }
      return hash;
    },
    
    // 根据时间范围获取时间间隔（毫秒）
    getTimeIntervalMs() {
      switch (this.timeRange) {
        case '24h':
          return 60 * 60 * 1000; // 1小时
        case '7d':
          return 60 * 60 * 1000; // 1小时
        case '30d':
          return 24 * 60 * 60 * 1000; // 1天
        default:
          return 60 * 60 * 1000; // 默认1小时
      }
    },
    
    // 初始化图表
    initChart() {
      const chartDom = document.getElementById('dataChart');
      if (!chartDom) return;
      
      this.chartInstance = echarts.init(chartDom);
      
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            const data = params[0];
            return `${data.name}<br/>${data.marker} ${data.seriesName}: ${data.value}`;
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: [],
          axisLabel: {
            formatter: this.formatXAxisLabel
          }
        },
        yAxis: {
          type: 'value',
          name: '数值',
          axisLabel: {
            formatter: '{value}'
          }
        },
        series: [
          {
            name: this.getMetricName(),
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            data: [],
            lineStyle: {
              color: '#1890ff',
              width: 3
            },
            itemStyle: {
              color: '#1890ff',
              borderColor: '#fff',
              borderWidth: 2
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(24, 144, 255, 0.3)'
                },
                {
                  offset: 1,
                  color: 'rgba(24, 144, 255, 0.05)'
                }
              ])
            },
            emphasis: {
              focus: 'series'
            }
          }
        ]
      };
      
      this.chartInstance.setOption(option);
    },
    
    // 更新图表数据
    updateChart() {
      if (!this.chartInstance) return;
      
      const xAxisData = this.dataRecords.map(record => {
        const date = new Date(record.timestamp);
        return this.formatDateTimeLabel(date);
      });
      
      const seriesData = this.dataRecords.map(record => record.value);
      
      this.chartInstance.setOption({
        xAxis: {
          data: xAxisData
        },
        series: [
          {
            name: this.getMetricName(),
            data: seriesData
          }
        ]
      });
    },
    
    // 格式化日期时间标签
    formatDateTimeLabel(date) {
      switch (this.timeRange) {
        case '24h':
          return `${date.getHours()}:00`;
        case '7d':
          return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:00`;
        case '30d':
          return `${date.getMonth() + 1}/${date.getDate()}`;
        default:
          return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:00`;
      }
    },
    
    // 格式化X轴标签
    formatXAxisLabel(value) {
      return value;
    },
    
    // 获取指标名称
    getMetricName() {
      // 根据设备类型返回相应的指标名称
      const typeMap = {
        '温度传感器': '温度 (°C)',
        '湿度传感器': '湿度 (%)',
        '压力传感器': '压力 (kPa)',
        '流量传感器': '流量 (L/min)',
        '液位传感器': '液位 (cm)'
      };
      
      return typeMap[this.deviceInfo.deviceType] || '测量值';
    },
    
    // 获取状态标签类型
    getStatusTagType(status) {
      switch (status) {
        case 'normal':
          return 'success';
        case 'abnormal':
          return 'danger';
        case 'offline':
          return 'warning';
        default:
          return 'default';
      }
    },
    
    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 'normal':
          return '正常';
        case 'abnormal':
          return '异常';
        case 'offline':
          return '离线';
        default:
          return '未知';
      }
    },
    
    // 处理时间范围变化
    handleTimeRangeChange() {
      this.getDeviceDataRecords();
    },
    
    // 处理自定义日期变化
    handleCustomDateChange() {
      if (this.customDateRange.length > 0) {
        this.getDeviceDataRecords();
      }
    },
    
    // 处理分页拖动条变化
    handlePageChange(newPage) {
      // 使用防抖处理，避免频繁请求
      if (this.pageChangeTimeout) {
        clearTimeout(this.pageChangeTimeout);
      }
      
      this.pageChangeTimeout = setTimeout(() => {
        this.currentPage = newPage;
        console.log('切换到页码:', newPage);
        this.getDeviceDataRecords();
      }, 300);
    },
    
    // 格式化日期时间为后端API可接受的格式
    formatDateTimeForAPI(date) {
      if (!date || isNaN(date.getTime())) {
        return '';
      }
      // Java LocalDateTime期望的格式: yyyy-MM-dd HH:mm:ss
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    
    /** 刷新数据 */
    async refreshData() {
      this.$message.loading('刷新中...', 0);
      // 重置分页状态
      this.currentPage = 1;
      this.totalRecords = 0;
      this.totalPages = 1;
      this.paginationVisible = false;
      
      try {
        await this.getDeviceInfo();
        await this.getDeviceDataRecords();
        this.$message.success('数据刷新成功');
      } catch (error) {
        console.error('刷新数据失败:', error);
        this.$message.error('部分数据刷新失败');
      } finally {
        setTimeout(() => {
          this.$message.closeAll();
        }, 1000);
      }
    },
    
    // 处理窗口大小变化
    handleResize() {
      if (this.chartInstance) {
        this.chartInstance.resize();
      }
    }
  }
};
</script>

<style scoped>
.iot-device-detail-container {
  padding: 20px;
  height: 100%;
  background-color: #0a1520;
}

.page-header .header-right {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
}

.device-name-title {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff !important;
  margin-right: auto;
}

.device-info-panel {
  margin-bottom: 20px;
}

.device-basic-info {
  border-radius: 8px;
  overflow: hidden;
}

/* 深度选择器修改 el-card 的背景色和边框 */
.device-basic-info ::v-deep .el-card,
.chart-card ::v-deep .el-card {
  background-color: #0f1a26 !important;
  border: 1px solid rgba(64, 158, 255, 0.5) !important;
  color: #ffffff !important;
}

.device-basic-info ::v-deep .el-card__header,
.chart-card ::v-deep .el-card__header {
  background-color: #0f1a26 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.device-basic-info ::v-deep .el-card__body,
.chart-card ::v-deep .el-card__body {
  background-color: #0f1a26 !important;
  color: #ffffff !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #0f1a26 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.card-header span {
  color: #ffffff !important;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  padding: 20px;
  background-color: #0f1a26 !important;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.info-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6) !important;
  width: 90px;
  flex-shrink: 0;
}

.info-value {
  font-size: 14px;
  color: #ffffff !important;
  flex: 1;
  word-break: break-all;
}

.chart-container {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 8px;
  overflow: hidden;
}

.chart-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chart-wrapper {
  padding: 20px;
  background-color: #0f1a26 !important;
}

.chart {
  width: 100%;
  height: 400px;
}

/* 修改表单元素的样式 */
.chart-controls ::v-deep .el-select .el-input__inner {
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.chart-controls ::v-deep .el-select .el-input__suffix {
  color: rgba(255, 255, 255, 0.6) !important;
}

.chart-controls ::v-deep .el-date-editor .el-input__inner {
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.chart-controls ::v-deep .el-date-editor .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.4) !important;
}

.chart-controls ::v-deep .el-date-editor .el-input__suffix {
  color: rgba(255, 255, 255, 0.6) !important;
}

/* 分页拖动条样式 */
.pagination-slider {
  background-color: #0f1a26 !important;
}

.pagination-info {
  color: rgba(255, 255, 255, 0.8) !important;
}

.pagination-slider ::v-deep .el-slider__runway {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.pagination-slider ::v-deep .el-slider__bar {
  background-color: rgba(64, 158, 255, 0.5) !important;
}

.pagination-slider ::v-deep .el-slider__button {
  border-color: rgba(64, 158, 255, 0.5) !important;
  background-color: #409EFF !important;
}

.pagination-slider ::v-deep .el-slider__stop {
  background-color: rgba(255, 255, 255, 0.3) !important;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-right {
    margin-top: 10px;
    display: flex;
    gap: 10px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-controls {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .chart {
    height: 300px;
  }
}
</style>