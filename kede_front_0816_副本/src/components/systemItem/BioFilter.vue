<template>
  <div
    class="bio-filter-container"
    :style="positionStyle"
  >
    <div class="bio-filter-wrapper">
      <!-- 生物池 -->
      <div class="bio-frame">
        <!-- 运行指示灯 -->
        <div
          :class="statusClass"
          class="status-light"
        ></div>

        <!-- 4个圆形排列 -->
        <div class="circle-container">
          <!-- 圆形1：细菌群落 -->
          <div class="circle-item bacteria">
            <div class="inner-circle"></div>
          </div>
          
          <!-- 圆形2：氧气泡 -->
          <div class="circle-item oxygen">
            <div class="inner-circle pulse"></div>
          </div>
          
          <!-- 圆形3：矿物质 -->
          <div class="circle-item mineral">
            <div class="inner-circle"></div>
          </div>
          
          <!-- 圆形4：营养物质 -->
          <div class="circle-item nutrient">
            <div class="inner-circle bounce"></div>
          </div>
        </div>

        <!-- 标签 -->
        <span class="bio-label">生物池</span>

        <!-- 活性指示 -->
        <div class="activity-indicator">
          <div class="activity-dots">
            <div
              v-for="i in 5"
              :key="i"
              class="activity-dot"
              :class="i * 20 <= data.bacteriaActivity ? 'active' : 'inactive'"
            ></div>
          </div>
        </div>
      </div>

      <!-- 数据面板 -->
      <div class="data-panel">
        <div class="panel-content">
          <div class="data-item">
            <span class="data-label">状态</span>
            <span :class="statusTextClass" class="data-value">{{ statusText }}</span>
          </div>
          <div class="data-item">
            <span class="data-label">温度</span>
            <span class="data-value temp">{{ data.temperature.toFixed(1) }}°C</span>
          </div>
          <div class="data-item">
            <span class="data-label">效率</span>
            <span class="data-value efficiency">{{ data.efficiency.toFixed(1) }}%</span>
          </div>
          <div class="data-item">
            <span class="data-label">细菌活性</span>
            <span class="data-value activity">{{ data.bacteriaActivity.toFixed(1) }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BioFilter',
  props: {
    data: {
      type: Object,
      required: true,
    },
    position: {
      type: Object,
      required: true,
    },
  },
  computed: {
    positionStyle() {
      return {
        left: `${this.position.x}px`,
        top: `${this.position.y}px`,
      }
    },
    statusClass() {
      const classes = {
        running: 'status-light-running',
        stopped: 'status-light-stopped',
        error: 'status-light-error',
      }
      return classes[this.data.status] || classes.stopped
    },
    statusTextClass() {
      const classes = {
        running: 'status-text-running',
        stopped: 'status-text-stopped',
        error: 'status-text-error',
      }
      return classes[this.data.status] || classes.stopped
    },
    statusText() {
      const texts = {
        running: '运行中',
        stopped: '已停止',
        error: '故障',
      }
      return texts[this.data.status] || texts.stopped
    },
  },
}
</script>

<style scoped>
.bio-filter-container {
  position: absolute;
  width: 192px;
  height: 208px;
}

.bio-filter-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.bio-frame {
  width: 192px;
  height: 208px;
  background: linear-gradient(135deg, rgba(4, 120, 87, 0.6) 0%, rgba(5, 150, 105, 0.4) 100%);
  border: 2px solid rgba(16, 185, 129, 0.5);
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(4, 120, 87, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.status-light {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid #0f172a;
  z-index: 2;
}

.status-light-running {
  background: #10b981;
  animation: pulse 1.5s infinite;
}

.status-light-stopped {
  background: #64748b;
}

.status-light-error {
  background: #ef4444;
  animation: pulse 1.5s infinite;
}

.circle-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 12px;
}

.circle-item {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bacteria {
  background: linear-gradient(135deg, #10b981, #059669);
}

.oxygen {
  background: linear-gradient(135deg, #06b6d4, #0891b2);
}

.mineral {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.nutrient {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.inner-circle {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.bacteria .inner-circle {
  background: #6ee7b7;
}

.oxygen .inner-circle {
  background: #67e8f9;
}

.mineral .inner-circle {
  background: #fde68a;
}

.nutrient .inner-circle {
  background: #93c5fd;
}

.pulse {
  animation: pulse-dot 2s infinite;
}

.bounce {
  animation: bounce 1.5s infinite;
}

.bio-label {
  font-size: 14px;
  color: #a7f3d0;
  margin: 8px 0;
}

.activity-indicator {
  position: absolute;
  bottom: 16px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
}

.activity-dots {
  display: flex;
  align-items: center;
  gap: 4px;
}

.activity-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
}

.activity-dot.active {
  background: #34d399;
}

.activity-dot.inactive {
  background: #134e4a;
}

.data-panel {
  position: absolute;
  top: -200px;
  left: 50%;
  transform: translateX(-50%);
  width: 176px;
  background: rgba(15, 23, 42, 0.9);
  border: 1px solid #475569;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  z-index: 10;
}

.bio-filter-wrapper:hover .data-panel {
  opacity: 1;
}

.panel-content {
  padding: 12px;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid #334155;
}

.data-item:last-child {
  border-bottom: none;
}

.data-label {
  font-size: 12px;
  color: #94a3b8;
}

.data-value {
  font-size: 12px;
  font-weight: 500;
}

.status-text-running {
  color: #34d399;
}

.status-text-stopped {
  color: #94a3b8;
}

.status-text-error {
  color: #f87171;
}

.data-value.temp {
  color: #fbbf24;
}

.data-value.efficiency {
  color: #34d399;
}

.data-value.activity {
  color: #22d3ee;
}

@keyframes pulse-dot {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
  100% {
    opacity: 1;
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}
</style>