<template>
  <div
    class="micro-filter-container"
    :style="positionStyle"
  >
    <div class="micro-filter-wrapper">
      <!-- 微滤机大框 -->
      <div class="filter-frame">
        <!-- 运行指示灯 -->
        <div
          :class="statusClass"
          class="status-light"
        ></div>

        <!-- 微滤机内容（位于顶部） -->
        <div class="filter-header">
          <!-- 微滤机图标 -->
          <svg class="filter-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>

          <!-- 标签 -->
          <span class="filter-label">微滤机</span>
        </div>

        <!-- 效率指示 -->
        <div class="efficiency-indicator">
          <div class="efficiency-bar">
            <div
              class="efficiency-progress"
              :style="{ width: data.efficiency + '%' }"
            ></div>
          </div>
        </div>

        <!-- 循环泵（位于底部） -->
        <div class="pump-container">
          <!-- 循环泵 #1 -->
          <div class="mini-pump">
            <!-- 运行指示灯 -->
            <div
              :class="statusClass"
              class="mini-status-light"
            ></div>

            <!-- 水泵图标 -->
            <svg class="mini-pump-icon" :class="{ 'spinning': isRunning }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>

            <!-- 标签 -->
            <span class="mini-pump-label">循环泵 #1</span>
            
            <!-- 流量指示 -->
            <div class="mini-efficiency-bar">
              <div class="mini-efficiency-progress" style="width: 70%"></div>
            </div>
          </div>
          
          <!-- 循环泵 #2 -->
          <div class="mini-pump">
            <!-- 运行指示灯 -->
            <div
              :class="statusClass"
              class="mini-status-light"
            ></div>

            <!-- 水泵图标 -->
            <svg class="mini-pump-icon" :class="{ 'spinning': isRunning }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>

            <!-- 标签 -->
            <span class="mini-pump-label">循环泵 #2</span>
            
            <!-- 流量指示 -->
            <div class="mini-efficiency-bar">
              <div class="mini-efficiency-progress" style="width: 85%"></div>
            </div>
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
            <span class="data-label">流量</span>
            <span class="data-value flow">{{ data.flowRate.toFixed(1) }} L/min</span>
          </div>
          <div class="data-item">
            <span class="data-label">压力</span>
            <span class="data-value pressure">{{ data.pressure.toFixed(2) }} MPa</span>
          </div>
          <div class="data-item">
            <span class="data-label">效率</span>
            <span class="data-value efficiency">{{ data.efficiency.toFixed(1) }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MicroFilter',
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
}
</script>

<style scoped>
.micro-filter-container {
  position: absolute;
  width: 192px;
  height: 208px;
}

.micro-filter-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.filter-frame {
  width: 192px;
  height: 208px;
  background: linear-gradient(135deg, rgba(6, 78, 129, 0.6) 0%, rgba(12, 122, 194, 0.4) 100%);
  border: 2px solid rgba(56, 189, 248, 0.5);
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(6, 78, 129, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
  padding: 16px 0;
  box-sizing: border-box;
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

.filter-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.filter-icon {
  width: 48px;
  height: 48px;
  color: #7dd3fc;
}

.filter-label {
  font-size: 14px;
  color: #bae6fd;
  margin-top: 8px;
}

.efficiency-indicator {
  width: 100%;
  padding: 0 16px;
  box-sizing: border-box;
}

.efficiency-bar {
  height: 4px;
  background: rgba(56, 189, 248, 0.3);
  border-radius: 2px;
  overflow: hidden;
}

.efficiency-progress {
  height: 100%;
  background: linear-gradient(90deg, #0ea5e9, #0284c7);
  border-radius: 2px;
  transition: width 0.5s ease;
}

.pump-container {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 0 16px;
  box-sizing: border-box;
}

.mini-pump {
  width: 80px;
  height: 96px;
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

.mini-status-light {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid #0f172a;
}

.mini-pump-icon {
  width: 32px;
  height: 32px;
  color: #a5b4fc;
}

.spinning {
  animation: spin 2s linear infinite;
}

.mini-pump-label {
  font-size: 10px;
  color: #c7d2fe;
  margin: 6px 0;
  text-align: center;
}

.mini-efficiency-bar {
  position: absolute;
  bottom: 8px;
  left: 4px;
  right: 4px;
  height: 4px;
  background: rgba(99, 102, 241, 0.3);
  border-radius: 2px;
  overflow: hidden;
}

.mini-efficiency-progress {
  height: 100%;
  background: linear-gradient(90deg, #818cf8, #6366f1);
  border-radius: 2px;
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

.micro-filter-wrapper:hover .data-panel {
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

.data-value.pressure {
  color: #fbbf24;
}

.data-value.efficiency {
  color: #34d399;
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