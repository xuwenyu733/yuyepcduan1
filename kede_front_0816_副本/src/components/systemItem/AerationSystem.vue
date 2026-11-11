<template>
  <div
    class="aeration-system-container"
    :style="positionStyle"
  >
    <div class="aeration-system-wrapper">
      <!-- 加氧曝气系统 -->
      <div class="aeration-frame">
        <!-- 运行指示灯 -->
        <div
          :class="statusClass"
          class="status-light"
        ></div>

        <!-- 气泡动画 -->
        <div v-if="isRunning" class="bubble-container">
          <div
            v-for="i in 3"
            :key="i"
            class="bubble"
            :style="bubbleStyle(i)"
          ></div>
        </div>

        <!-- 竖直排列的三个元素：两个长方形框 + 氧锥 -->
        <div class="aeration-content">
          <!-- 第一个长方形框 - 脱气床 -->
          <div class="module-box">
            <span class="module-label">脱气床</span>
          </div>
          
          <!-- 第二个长方形框 - 脱气床 -->
          <div class="module-box">
            <span class="module-label">脱气床</span>
          </div>

          <!-- 氧锥 -->
          <div class="oxygen-cone">
            <span class="cone-label">氧锥</span>
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
            <span class="data-label">氧气水平</span>
            <span class="data-value oxygen">{{ data.oxygenLevel.toFixed(1) }}%</span>
          </div>
          <div class="data-item">
            <span class="data-label">空气流量</span>
            <span class="data-value airflow">{{ data.airFlowRate.toFixed(1) }} L/min</span>
          </div>
          <div class="data-item">
            <span class="data-label">功率</span>
            <span class="data-value power">{{ data.power.toFixed(1) }} kW</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AerationSystem',
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
  methods: {
    bubbleStyle(index) {
      const leftPositions = ['25%', '50%', '75%']
      return {
        left: leftPositions[index - 1],
        animationDelay: `${index * 0.3}s`,
      }
    },
  },
}
</script>

<style scoped>
.aeration-system-container {
  position: absolute;
  width: 192px;
  height: 208px;
}

.aeration-system-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.aeration-frame {
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

.bubble-container {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bubble {
  position: absolute;
  bottom: 0;
  width: 8px;
  height: 8px;
  background: rgba(16, 185, 129, 0.4);
  border-radius: 50%;
  animation: bubble-rise 3s infinite;
}

@keyframes bubble-rise {
  0% {
    bottom: 0;
    transform: translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    bottom: 100%;
    transform: translateX(20px);
    opacity: 0;
  }
}

.aeration-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  z-index: 1;
}

.module-box {
  width: 96px;
  height: 32px;
  background: rgba(16, 185, 129, 0.4);
  border: 1px solid rgba(16, 185, 129, 0.6);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.module-label {
  font-size: 12px;
  font-weight: 500;
  color: #a7f3d0;
}

.oxygen-cone {
  width: 40px;
  height: 40px;
  background: rgba(16, 185, 129, 0.7);
  border: 1px solid rgba(16, 185, 129, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  transform: rotate(45deg);
}

.cone-label {
  font-size: 12px;
  font-weight: 700;
  color: #a7f3d0;
  transform: rotate(-45deg);
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

.aeration-system-wrapper:hover .data-panel {
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

.data-value.oxygen {
  color: #34d399;
}

.data-value.airflow {
  color: #60a5fa;
}

.data-value.power {
  color: #fbbf24;
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