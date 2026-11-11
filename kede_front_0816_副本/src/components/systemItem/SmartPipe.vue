<template>
  <svg
    class="smart-pipe"
    :style="svgStyle"
    :viewBox="viewBox"
  >
    <defs>
      <!-- 水流渐变定义 -->
      <linearGradient :id="gradientId" x1="0%" y1="0%" x2="100%" y2="0%">
        <stop offset="0%" :style="`stop-color:${colors.primary};stop-opacity:0.8`" />
        <stop offset="50%" :style="`stop-color:${colors.accent};stop-opacity:0.6`" />
        <stop offset="100%" :style="`stop-color:${colors.primary};stop-opacity:0.8`" />
      </linearGradient>

      <!-- 箭头定义 -->
      <marker
        :id="arrowMarkerId"
        markerWidth="8"
        markerHeight="6" 
        refX="6" 
        refY="3"
        orient="auto"
      >
        <polygon
          points="0 0, 8 3, 0 6"
          :fill="colors.arrow"
        />
      </marker>
    </defs>

    <!-- 静态管道主体 -->
    <path 
      :d="pathData"
      :stroke="colors.pipe" 
      :stroke-width="strokeWidth" 
      stroke-linecap="round"
      stroke-linejoin="round"
      :marker-end="showArrow ? `url(#${arrowMarkerId})` : null"
      fill="none"
    />

    <!-- 水流动画层 -->
    <path
      v-if="showAnimation"
      :d="pathData"
      :stroke="`url(#${gradientId})`"
      :stroke-width="strokeWidth"
      :stroke-dasharray="dashArray"
      stroke-linecap="round"
      stroke-linejoin="round"
      class="water-flow"
    >
      <animate
        attributeName="stroke-dashoffset"
        :from="dashOffsetStart"
        :to="dashOffsetEnd"
        :dur="animationDuration"
        repeatCount="indefinite"
      />
    </path>
  </svg>
</template>

<script>
/**
 * SmartPipe - 智能管道组件
 * 支持多连接点、贝塞尔曲线和自动路径计算
 */

export default {
  name: 'SmartPipe',
  
  props: {
    /** 连接点数组 */
    points: {
      type: Array,
      required: true,
      validator: (points) => {
        return points.length >= 2 && points.every(p => 
          typeof p.x === 'number' && typeof p.y === 'number'
        )
      }
    },
    /** 线条宽度 */
    strokeWidth: {
      type: Number,
      default: 3
    },
    /** 是否显示动画 */
    showAnimation: {
      type: Boolean,
      default: true
    },
    /** 是否显示箭头 */
    showArrow: {
      type: Boolean,
      default: true
    },
    /** 动画速度 */
    animationSpeed: {
      type: String,
      default: 'normal',
      validator: (value) => ['slow', 'normal', 'fast'].includes(value)
    },
    /** 管道颜色 */
    color: {
      type: String,
      default: '#3b82f6'
    }
  },

  computed: {
    /** SVG样式 */
    svgStyle() {
      const bounds = this.getBounds()
      const padding = 20
      return {
        left: `${bounds.minX - padding}px`,
        top: `${bounds.minY - padding}px`,
        width: `${bounds.width + padding * 2}px`,
        height: `${bounds.height + padding * 2}px`,
        position: 'absolute'
      }
    },

    /** 视图框 */
    viewBox() {
      const bounds = this.getBounds()
      const padding = 20
      return `${bounds.minX - padding} ${bounds.minY - padding} ${bounds.width + padding * 2} ${bounds.height + padding * 2}`
    },

    /** 路径数据 */
    pathData() {
      if (this.points.length < 2) return ''
      
      // 使用折线路径
      let d = `M ${this.points[0].x} ${this.points[0].y}`
      for (let i = 1; i < this.points.length; i++) {
        d += ` L ${this.points[i].x} ${this.points[i].y}`
      }
      return d
    },

    /** 动画配置 */
    animationDuration() {
      const durations = {
        slow: '3s',
        normal: '2s',
        fast: '1s'
      }
      return durations[this.animationSpeed]
    },

    /** 渐变ID */
    gradientId() {
      return `gradient-${this._uid}`
    },

    /** 箭头标记ID */
    arrowMarkerId() {
      return `arrow-${this._uid}`
    },

    /** 颜色配置 */
    colors() {
      const baseColor = this.color
      return {
        primary: baseColor,
        accent: this.lightenColor(baseColor, 40),
        pipe: this.darkenColor(baseColor, 30),
        arrow: baseColor
      }
    },

    /** 虚线配置 */
    dashArray() {
      return '10 5'
    },

    dashOffsetStart() {
      return '0'
    },

    dashOffsetEnd() {
      return '15'
    }
  },

  methods: {
    /** 获取边界框 */
    getBounds() {
      if (this.points.length === 0) {
        return { minX: 0, minY: 0, maxX: 0, maxY: 0, width: 0, height: 0 }
      }
      
      const xs = this.points.map(p => p.x)
      const ys = this.points.map(p => p.y)
      
      const minX = Math.min(...xs)
      const maxX = Math.max(...xs)
      const minY = Math.min(...ys)
      const maxY = Math.max(...ys)
      
      return {
        minX,
        minY,
        maxX,
        maxY,
        width: maxX - minX,
        height: maxY - minY
      }
    },

    /** 将十六进制颜色变亮 */
    lightenColor(hex, percent) {
      const num = parseInt(hex.replace('#', ''), 16)
      const amt = Math.round(2.55 * percent)
      const R = Math.min((num >> 16) + amt, 255)
      const G = Math.min((num >> 8 & 0x00FF) + amt, 255)
      const B = Math.min((num & 0x0000FF) + amt, 255)
      
      return '#' + ((1 << 24) + (R << 16) + (G << 8) + B).toString(16).slice(1)
    },
    
    /** 将十六进制颜色变暗 */
    darkenColor(hex, percent) {
      const num = parseInt(hex.replace('#', ''), 16)
      const amt = Math.round(2.55 * percent)
      const R = Math.max((num >> 16) - amt, 0)
      const G = Math.max((num >> 8 & 0x00FF) - amt, 0)
      const B = Math.max((num & 0x0000FF) - amt, 0)
      
      return '#' + ((1 << 24) + (R << 16) + (G << 8) + B).toString(16).slice(1)
    }
  }
}
</script>

<style scoped>
.smart-pipe {
  pointer-events: none;
  z-index: 5;
}

.water-flow {
  opacity: 0.8;
  filter: drop-shadow(0 0 2px rgba(255, 255, 255, 0.3));
}
</style>