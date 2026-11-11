<template>
  <div class="system-overview-container">
    <!-- 系统信息卡片 -->
    <div class="overview-card fish-ponds-card">
      <div class="card-header">
        <div class="header-text">
          <p class="card-title">系统信息</p>
          <p class="card-value">循环水系统参数</p>
        </div>
        <div class="header-icon">
          <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        </div>
      </div>
      <div class="card-footer">
        <p class="footer-text">水流速度: {{ flowSpeed }} L/min</p>
        <p class="footer-text">循环量: {{ circulationVolume }} L/h</p>
        <p class="footer-text">换水率: 3.5%/d</p>
        <p class="footer-text">停留时间: 2.5h</p>
      </div>
    </div>

    <!-- 设备信息卡片 -->
    <div class="overview-card pumps-card">
      <div class="card-header">
        <div class="header-text">
          <p class="card-title">设备信息</p>
          <p class="card-value">设备运行状态</p>
        </div>
        <div class="header-icon">
          <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
        </div>
      </div>
      <div class="card-footer">
        <p class="footer-text">循环泵: {{ runningPumps }}/{{ totalPumps }} 台运行</p>
        <p class="footer-text">微滤机: {{ microFilterStatus }}</p>
        <p class="footer-text">温度: {{ avgTemperature }}°C</p>
        <p class="footer-text">pH: {{ avgPh }}</p>
      </div>
    </div>

    <!-- 生物信息卡片 -->
    <div class="overview-card filter-card">
      <div class="card-header">
        <div class="header-text">
          <p class="card-title">生物信息</p>
          <p class="card-value">生物系统参数</p>
        </div>
        <div class="header-icon">
          <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>
        </div>
      </div>
      <div class="card-footer">
        <p class="footer-text">养殖密度: 30 kg/m³</p>
        <p class="footer-text">饵料系数: 1.2</p>
        <p class="footer-text">细菌活性: {{ bacteriaActivity }}%</p>
        <p class="footer-text">投喂率: 2.5%/d</p>
      </div>
    </div>

    <!-- 生产信息卡片 -->
    <div class="overview-card oxygen-card">
      <div class="card-header">
        <div class="header-text">
          <p class="card-title">生产信息</p>
          <p class="card-value">养殖生产数据</p>
        </div>
        <div class="header-icon">
          <svg class="icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
          </svg>
        </div>
      </div>
      <div class="card-footer">
        <p class="footer-text">鱼品种: 加州鲈</p>
        <p class="footer-text">规格: 50-60 g/尾</p>
        <p class="footer-text">数量: {{ totalFish }} 尾</p>
        <p class="footer-text">状态: 正常</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SystemOverview',
  props: {
    data: {
      type: Object,
      required: true,
    },
  },
  computed: {
    // 系统信息卡片相关计算
    flowSpeed() {
      // 平均水流速度
      const pumps = this.data.circulationPumps || []
      const total = pumps.reduce((sum, pump) => sum + pump.flowRate, 0)
      return pumps.length > 0 ? (total / pumps.length).toFixed(0) : '0'
    },
    circulationVolume() {
      // 总循环量
      const pumps = this.data.circulationPumps || []
      return pumps.reduce((sum, pump) => sum + pump.flowRate, 0)
    },
    
    // 设备信息卡片相关计算
    runningPumps() {
      return (this.data.circulationPumps || []).filter(pump => pump.status === 'running').length
    },
    totalPumps() {
      return (this.data.circulationPumps || []).length
    },
    microFilterStatus() {
      return this.data.microFilter.status
    },
    avgTemperature() {
      const total = this.data.fishPonds.reduce((sum, pond) => sum + pond.temperature, 0)
      return (total / this.data.fishPonds.length).toFixed(1)
    },
    avgPh() {
      const total = this.data.fishPonds.reduce((sum, pond) => sum + pond.ph, 0)
      return (total / this.data.fishPonds.length).toFixed(1)
    },
    
    // 生物信息卡片相关计算
    bacteriaActivity() {
      return this.data.bioFilter.bacteriaActivity.toFixed(1)
    },
    avgFilterEfficiency() {
      const avg = (this.data.microFilter.efficiency + this.data.bioFilter.efficiency) / 2
      return avg.toFixed(1)
    },
    
    // 生产信息卡片相关计算
    totalFish() {
      return this.data.fishPonds.reduce((sum, pond) => sum + pond.fishCount, 0)
    },
    fishPondCount() {
      return this.data.fishPonds.length
    },
  },
}
</script>

<style scoped>
.system-overview-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.overview-card {
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  min-height: 120px;
  display: flex;
  flex-direction: column;
}

.overview-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.fish-ponds-card {
  background: linear-gradient(135deg, rgba(30, 64, 175, 0.4) 0%, rgba(30, 58, 138, 0.2) 100%);
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.pumps-card {
  background: linear-gradient(135deg, rgba(79, 70, 229, 0.4) 0%, rgba(99, 102, 241, 0.2) 100%);
  border: 1px solid rgba(99, 102, 241, 0.3);
}

.filter-card {
  background: linear-gradient(135deg, rgba(6, 78, 129, 0.4) 0%, rgba(12, 122, 194, 0.2) 100%);
  border: 1px solid rgba(56, 189, 248, 0.3);
}

.oxygen-card {
  background: linear-gradient(135deg, rgba(4, 120, 87, 0.4) 0%, rgba(5, 150, 105, 0.2) 100%);
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-text {
  flex: 1;
  display: flex;
  align-items: center;
}

.card-title {
  font-size: 14px;
  color: #cbd5e1;
  margin: 0;
  font-weight: normal;
}

.card-value {
  font-size: 14px;
  font-weight: 600;
  margin: 0;
  margin-left: 8px;
  color: #ffffff;
}

.fish-ponds-card .card-value {
  color: #dbeafe;
}

.pumps-card .card-value {
  color: #c7d2fe;
}

.filter-card .card-value {
  color: #bae6fd;
}

.oxygen-card .card-value {
  color: #a7f3d0;
}

.header-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 12px;
}

.fish-ponds-card .header-icon {
  background: rgba(30, 64, 175, 0.3);
}

.pumps-card .header-icon {
  background: rgba(79, 70, 229, 0.3);
}

.filter-card .header-icon {
  background: rgba(6, 78, 129, 0.3);
}

.oxygen-card .header-icon {
  background: rgba(4, 120, 87, 0.3);
}

.icon {
  width: 24px;
  height: 24px;
}

.fish-ponds-card .icon {
  color: #93c5fd;
}

.pumps-card .icon {
  color: #a5b4fc;
}

.filter-card .icon {
  color: #7dd3fc;
}

.oxygen-card .icon {
  color: #6ee7b7;
}

.card-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4px 8px;
}

.footer-text {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
  line-height: 1.4;
}

@media (max-width: 768px) {
  .system-overview-container {
    grid-template-columns: 1fr;
  }
}
</style>