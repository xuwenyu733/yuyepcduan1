<template>
  <div
    class="water-pump-container"
    :style="positionStyle"
  >
    <div class="water-pump-wrapper">
      <!-- 水泵外框 -->
      <div class="pump-frame">
        <!-- 运行指示灯 -->
        <div
          :class="statusClass"
          class="status-light"
        ></div>

        <!-- 水泵图标 -->
        <svg class="pump-icon" :class="{ 'spinning': isRunning }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>

        <!-- 标签 -->
        <span class="pump-label">{{ label }}</span>
      </div>

      <!-- 数据面板 -->
      <div class="data-panel">
        <div class="panel-content">
          <div class="data-item">
            <span class="data-label">状态</span>
            <span :class="statusTextClass" class="data-value">{{ statusText }}</span>
          </div>
          <div class="data-item">
            <span class="data-label">流量</span>
            <span class="data-value flow">{{ data.flowRate.toFixed(1) }} L/min</span>
          </div>
          <div class="data-item">
            <span class="data-label">功率</span>
            <span class="data-value power">{{ data.power.toFixed(1) }} kW</span>
          </div>
          <div class="data-item">
            <span class="data-label">压力</span>
            <span class="data-value pressure">{{ data.pressure.toFixed(2) }} MPa</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WaterPump',
  props: {
    data: {
      type: Object,
      required: true,
    },
    position: {
      type: Object,
      required: true,
    },
    label: {
      type: String,
      default: '水泵',
    },
  },
  computed: {
    positionStyle() {
      return {
        left: `${this.position.x}px`,
        top: `${this.position.y}px`,
      }
    },
    isRunning() {
      return this.data.status === 'running'
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
.water-pump-container {
  position: absolute;
  width: 64px;
  height: 80px;
}

.water-pump-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.pump-frame {
  width: 64px;
  height: 80px;
  background: linear-gradient(135deg, rgba(79, 70, 229, 0.6) 0%, rgba(99, 102, 241, 0.4) 100%);
  border: 2px solid rgba(99, 102, 241, 0.5);
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(79, 70, 229, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.status-light {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid #0f172a;
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

.pump-icon {
  width: 32px;
  height: 32px;
  color: #a5b4fc;
}

.spinning {
  animation: spin 2s linear infinite;
}

.pump-label {
  font-size: 12px;
  color: #c7d2fe;
  margin-top: 6px;
  text-align: center;
}

.data-panel {
  position: absolute;
  top: 0;
  left: 80px;
  width: 160px;
  background: rgba(15, 23, 42, 0.9);
  border: 1px solid #475569;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  z-index: 10;
}

.water-pump-wrapper:hover .data-panel {
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

.data-value.flow {
  color: #60a5fa;
}

.data-value.power {
  color: #fbbf24;
}

.data-value.pressure {
  color: #22d3ee;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
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