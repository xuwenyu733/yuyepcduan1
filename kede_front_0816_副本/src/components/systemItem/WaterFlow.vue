<template>
  <svg
    class="absolute overflow-visible pointer-events-none"
    :style="svgStyle"
    :viewBox="viewBox"
  >
    <defs>
      <linearGradient :id="gradientId" x1="0%" y1="0%" x2="100%" y2="0%">
        <stop offset="0%" :style="`stop-color:${colors.primary};stop-opacity:0.3`" />
        <stop offset="50%" :style="`stop-color:${colors.accent};stop-opacity:0.6`" />
        <stop offset="100%" :style="`stop-color:${colors.primary};stop-opacity:0.3`" />
      </linearGradient>

      <!-- 定义流动路径 -->
      <path
        :id="pathId"
        :d="`M ${from.x} ${from.y} L ${to.x} ${to.y}`"
        fill="none"
      />
    </defs>

    <!-- 静态管道 -->
    <use 
      :href="`#${pathId}`" 
      :stroke="colors.pipe" 
      :stroke-width="strokeWidth" 
      :opacity="opacity" 
    />

    <!-- 水流动画 -->
    <use
      :href="`#${pathId}`"
      :stroke="`url(#${gradientId})`"
      :stroke-width="strokeWidth"
      :stroke-dasharray="dashArray"
      v-if="showAnimation"
    >
      <animate
        attributeName="stroke-dashoffset"
        :from="dashOffsetStart"
        :to="dashOffsetEnd"
        :dur="animationDuration"
        repeatCount="indefinite"
      />
    </use>

    <!-- 流动方向箭头 -->
    <path
      :d="arrowPath"
      :fill="colors.arrow"
      :opacity="arrowOpacity"
      v-if="showArrow"
    >
      <animateMotion
        :dur="arrowAnimationDuration"
        repeatCount="indefinite"
        rotate="auto"
      >
        <mpath :href="`#${pathId}`" />
      </animateMotion>
    </path>
  </svg>
</template>

<script>
/**
 * WaterFlow - 水流管道组件
 * 改进版本：使用 animateMotion 实现更简洁的箭头动画
 */

export default {
  name: 'WaterFlow',
  
  props: {
    /** 起点坐标 */
    from: {
      type: Object,
      required: true,
      validator: (value) => {
        return typeof value.x === 'number' && typeof value.y === 'number'
      }
    },
    /** 终点坐标 */
    to: {
      type: Object,
      required: true,
      validator: (value) => {
        return typeof value.x === 'number' && typeof value.y === 'number'
      }
    },
    /** 线条宽度 */
    strokeWidth: {
      type: Number,
      default: 1.5
    },
    /** 线条透明度 */
    opacity: {
      type: Number,
      default: 0.4
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
      default: 'normal', // 'slow', 'normal', 'fast'
      validator: (value) => ['slow', 'normal', 'fast'].includes(value)
    },
    /** 管道颜色 */
    color: {
      type: String,
      default: '#3b82f6'
    }
  },

  computed: {
    /** SVG样式 - 简化计算 */
    svgStyle() {
      const minX = Math.min(this.from.x, this.to.x)
      const minY = Math.min(this.from.y, this.to.y)
      const maxX = Math.max(this.from.x, this.to.x)
      const maxY = Math.max(this.from.y, this.to.y)
      
      const width = maxX - minX
      const height = maxY - minY

      return {
        left: `${minX}px`,
        top: `${minY}px`,
        width: `${width}px`,
        height: `${height}px`
      }
    },

    /** 视图框 */
    viewBox() {
      const minX = Math.min(this.from.x, this.to.x)
      const minY = Math.min(this.from.y, this.to.y)
      const maxX = Math.max(this.from.x, this.to.x)
      const maxY = Math.max(this.from.y, this.to.y)
      
      const width = maxX - minX
      const height = maxY - minY
      
      return `${minX} ${minY} ${width} ${height}`
    },

    /** 动画配置 */
    animationDuration() {
      const durations = {
        slow: '2s',
        normal: '1s',
        fast: '0.5s'
      }
      return durations[this.animationSpeed]
    },

    arrowAnimationDuration() {
      return this.animationDuration === '1s' ? '2s' : 
             this.animationDuration === '2s' ? '4s' : '1s'
    },

    /** 渐变ID - 使用组件唯一标识 */
    gradientId() {
      return `gradient-${this._uid}`
    },

    /** 路径ID - 使用组件唯一标识 */
    pathId() {
      return `path-${this._uid}`
    },

    /** 颜色配置 */
    colors() {
      // 根据传入的颜色或默认颜色生成配色方案
      const baseColor = this.color
      return {
        primary: baseColor,
        accent: this.lightenColor(baseColor, 30),
        pipe: this.darkenColor(baseColor, 20),
        arrow: baseColor
      }
    },

    /** 线条配置 */
    dashArray() {
      return '8 8'
    },

    dashOffsetStart() {
      return '0'
    },

    dashOffsetEnd() {
      return '16'
    },

    arrowOpacity() {
      return 0.5
    },

    /** 箭头路径定义 */
    arrowPath() {
      // 箭头形状：指向右（+x 方向），长度约 4px
      return 'M0,-1.5 L-3,0 L0,1.5 Z'
    }
  },

  /** 颜色处理方法 */
  methods: {
    /** 将十六进制颜色变亮 */
    lightenColor(hex, percent) {
      const num = parseInt(hex.replace('#', ''), 16)
      const amt = Math.round(2.55 * percent)
      const R = (num >> 16) + amt
      const G = (num >> 8 & 0x00FF) + amt
      const B = (num & 0x0000FF) + amt
      
      return '#' + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
        (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
        (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1)
    },
    
    /** 将十六进制颜色变暗 */
    darkenColor(hex, percent) {
      const num = parseInt(hex.replace('#', ''), 16)
      const amt = Math.round(2.55 * percent)
      const R = (num >> 16) - amt
      const G = (num >> 8 & 0x00FF) - amt
      const B = (num & 0x0000FF) - amt
      
      return '#' + (0x1000000 + (R > 0 ? R : 0) * 0x10000 +
        (G > 0 ? G : 0) * 0x100 +
        (B > 0 ? B : 0)).toString(16).slice(1)
    }
  },

  /** 颜色处理方法 */
  methods: {
    /** 将十六进制颜色变亮 */
    lightenColor(hex, percent) {
      const num = parseInt(hex.replace('#', ''), 16)
      const amt = Math.round(2.55 * percent)
      const R = (num >> 16) + amt
      const G = (num >> 8 & 0x00FF) + amt
      const B = (num & 0x0000FF) + amt
      
      return '#' + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
        (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
        (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1)
    },
    
    /** 将十六进制颜色变暗 */
    darkenColor(hex, percent) {
      const num = parseInt(hex.replace('#', ''), 16)
      const amt = Math.round(2.55 * percent)
      const R = (num >> 16) - amt
      const G = (num >> 8 & 0x00FF) - amt
      const B = (num & 0x0000FF) - amt
      
      return '#' + (0x1000000 + (R > 0 ? R : 0) * 0x10000 +
        (G > 0 ? G : 0) * 0x100 +
        (B > 0 ? B : 0)).toString(16).slice(1)
    }
  },

  /** 生命周期钩子 */
  mounted() {
    // 组件挂载后的初始化操作
    if (process.env.NODE_ENV === 'development') {
      console.log(`WaterFlow mounted: ${this.from.x},${this.from.y} → ${this.to.x},${this.to.y}`)
    }
  },

  beforeDestroy() {
    // 清理工作
    if (process.env.NODE_ENV === 'development') {
      console.log('WaterFlow destroyed')
    }
  }
}
</script>

<style scoped>
/* 组件特定样式 */
use {
  transition: stroke-width 0.2s ease;
}

use:hover {
  stroke-width: calc(v-bind(strokeWidth) + 0.5);
}

path {
  transition: opacity 0.2s ease;
}

path:hover {
  opacity: 0.8;
}
</style>