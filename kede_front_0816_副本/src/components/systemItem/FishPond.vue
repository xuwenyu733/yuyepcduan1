<template>
  <div
    class="fish-pond-container"
    :style="positionStyle"
  >
    <div class="fish-pond-wrapper">
      <!-- 养鱼池圆形 -->
      <div class="fish-pond-circle">
        <!-- 水波动画 -->
        <div class="water-animation"></div>

        <!-- 鱼图标 -->
        <svg class="fish-icon" fill="currentColor" viewBox="0 0 24 24">
          <path d="M12 20l-7-4v-9l7 4 7-4v9l-7 4zm0-12.5l-5 2.5v5.5l5 2.5 5-2.5v-5.5l-5-2.5z"/>
        </svg>

        <!-- 名称 -->
        <div class="pond-name">
          {{ data.name }}
        </div>
      </div>

      <!-- 数据面板 -->
      <div class="data-panel">
        <div class="panel-content">
          <div class="data-item">
            <span class="data-label">水温</span>
            <span class="data-value temp">{{ data.temperature.toFixed(1) }}°C</span>
          </div>
          <div class="data-item">
            <span class="data-label">pH值</span>
            <span class="data-value ph">{{ data.ph.toFixed(1) }}</span>
          </div>
          <div class="data-item">
            <span class="data-label">溶解氧</span>
            <span class="data-value oxygen">{{ data.dissolvedOxygen.toFixed(1) }} mg/L</span>
          </div>
          <div class="data-item">
            <span class="data-label">水位</span>
            <span class="data-value level">{{ data.waterLevel.toFixed(0) }}%</span>
          </div>
          <div class="data-item">
            <span class="data-label">鱼数</span>
            <span class="data-value fish-count">{{ data.fishCount }} 条</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FishPond',
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
  },
}
</script>

<style scoped>
.fish-pond-container {
  position: absolute;
  width: 192px;
  height: 192px;
}

.fish-pond-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.fish-pond-circle {
  width: 192px;
  height: 192px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(30, 64, 175, 0.6) 0%, rgba(30, 58, 138, 0.4) 100%);
  border: 4px solid rgba(59, 130, 246, 0.5);
  box-shadow: 0 10px 25px rgba(30, 64, 175, 0.3), 0 0 20px rgba(59, 130, 246, 0.2) inset;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.water-animation {
  position: absolute;
  inset: 0;
  opacity: 0.3;
  background: linear-gradient(to top, rgba(59, 130, 246, 0.2) 0%, transparent 100%);
  animation: pulse 2s infinite;
}

.fish-icon {
  width: 80px;
  height: 80px;
  color: rgba(96, 165, 250, 0.4);
}

.pond-name {
  position: absolute;
  top: 20px;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 14px;
  color: #dbeafe;
  padding: 4px 12px;
  background: rgba(30, 64, 175, 0.6);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 20px;
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

.fish-pond-wrapper:hover .data-panel {
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

.data-value.temp {
  color: #60a5fa;
}

.data-value.ph {
  color: #34d399;
}

.data-value.oxygen {
  color: #22d3ee;
}

.data-value.level {
  color: #a78bfa;
}

.data-value.fish-count {
  color: #34d399;
}

@keyframes pulse {
  0% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 0.3;
  }
}
</style>