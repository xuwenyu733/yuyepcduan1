<template>
  <div
    class="connection-point-container"
    :style="positionStyle"
  >
    <!-- 连接点标记 -->
    <div
      class="connection-point"
      :class="pointClass"
    >
      <!-- 中心小圆点 -->
      <div class="inner-point" :class="innerClass"></div>
    </div>
    
    <!-- 标签 -->
    <div
      v-if="showLabel"
      class="point-label"
      :class="[labelClass, labelPositionClass]"
    >
      {{ label }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConnectionPoint',
  props: {
    position: {
      type: Object,
      required: true,
    },
    type: {
      type: String,
      default: 'inlet', // 'inlet' 或 'outlet'
      validator: (value) => ['inlet', 'outlet'].includes(value)
    },
    color: {
      type: String,
      default: '#60a5fa' // 蓝色
    },
    showLabel: {
      type: Boolean,
      default: false
    },
    label: {
      type: String,
      default: ''
    },
    labelPosition: {
      type: String,
      default: 'right', // 'top', 'right', 'bottom', 'left'
      validator: (value) => ['top', 'right', 'bottom', 'left'].includes(value)
    }
  },
  computed: {
    positionStyle() {
      return {
        left: `${this.position.x - 8}px`, // 偏移以居中
        top: `${this.position.y - 8}px`,
      }
    },
    pointClass() {
      return {
        'inlet': 'point-inlet',
        'outlet': 'point-outlet'
      }[this.type]
    },
    innerClass() {
      return {
        'inlet': 'inner-inlet',
        'outlet': 'inner-outlet'
      }[this.type]
    },
    labelClass() {
      return {
        'inlet': 'label-inlet',
        'outlet': 'label-outlet'
      }[this.type]
    },
    labelPositionClass() {
      return `label-${this.labelPosition}`
    }
  }
}
</script>

<style scoped>
.connection-point-container {
  position: absolute;
  width: 16px;
  height: 16px;
  pointer-events: none;
}

.connection-point {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 4px rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.connection-point:hover {
  transform: scale(1.2);
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

.point-inlet {
  background: rgba(16, 185, 129, 0.2);
}

.point-outlet {
  background: rgba(59, 130, 246, 0.2);
}

.inner-point {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.inner-inlet {
  background: #10b981;
}

.inner-outlet {
  background: #3b82f6;
}

.point-label {
  position: absolute;
  font-size: 12px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 12px;
  white-space: nowrap;
}

.label-inlet {
  background: rgba(16, 185, 129, 0.6);
  color: #ecfdf5;
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.label-outlet {
  background: rgba(59, 130, 246, 0.6);
  color: #dbeafe;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.label-top {
  top: -32px;
  left: 50%;
  transform: translateX(-50%);
}

.label-right {
  top: 50%;
  left: 24px;
  transform: translateY(-50%);
}

.label-bottom {
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
}

.label-left {
  top: 50%;
  left: -24px;
  transform: translateY(-50%);
}
</style>