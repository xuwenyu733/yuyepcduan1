<template>
  <!-- SVG 图标组件 -->
  <svg 
    class="svg-icon"
    :class="computedClasses"
    :style="computedStyles"
    :width="size"
    :height="size"
    :viewBox="viewBox"
    aria-hidden="true"
    v-on="$listeners"
  >
    <use :xlink:href="iconPath" />
  </svg>
</template>

<script>
/**
 * SvgIcon - SVG图标组件
 * 支持从外部SVG文件加载图标，支持自定义尺寸和颜色
 */

export default {
  name: 'SvgIcon',
  
  props: {
    /** 图标名称（对应svg文件名） */
    name: {
      type: String,
      required: true
    },
    /** 图标尺寸 */
    size: {
      type: [String, Number],
      default: '1em'
    },
    /** 图标颜色 */
    color: {
      type: String,
      default: 'currentColor'
    },
    /** 自定义类名 */
    customClass: {
      type: String,
      default: ''
    },
    /** 是否禁用 */
    disabled: {
      type: Boolean,
      default: false
    },
    /** 是否旋转（加载状态） */
    spin: {
      type: Boolean,
      default: false
    },
    /** 视图框 */
    viewBox: {
      type: String,
      default: '0 0 24 24'
    }
  },
  
  computed: {
    /** 图标路径 */
    iconPath() {
      return `#icon-${this.name}`
    },
    
    /** 计算样式 */
    computedStyles() {
      return {
        color: this.color,
        fontSize: typeof this.size === 'number' ? `${this.size}px` : this.size
      }
    },
    
    /** 计算类名 */
    computedClasses() {
      return [
        this.customClass,
        {
          'svg-icon--disabled': this.disabled,
          'svg-icon--spin': this.spin
        }
      ]
    }
  }
}
</script>

<style scoped>
.svg-icon {
  display: inline-block;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
  transition: all 0.2s ease-in-out;
}

.svg-icon--disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.svg-icon--spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 悬停效果 */
.svg-icon:hover:not(.svg-icon--disabled) {
  opacity: 0.8;
  transform: scale(1.1);
}

/* 聚焦效果 */
.svg-icon:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}
</style>