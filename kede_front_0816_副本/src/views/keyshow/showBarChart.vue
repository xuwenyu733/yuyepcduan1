<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

const animationDuration = 6000

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    chartData: {
      type: Object,
      default: () => ({})
    },
    chartOptions: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        if (this.chart) {
          this.initChart()
        }
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      // 默认option
      const defaultOption = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: this.chartData.xAxis || ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          axisTick: { alignWithLabel: true }
        }],
        yAxis: [{
          type: 'value',
          axisTick: { show: false }
        }],
        series: [
          {
            name: '数据',
            type: 'bar',
            barWidth: '60%',
            data: this.chartData.series || [0,0,0,0,0]
          }
        ]
      }
      // 合并外部传入的 chartOptions
      const option = Object.assign({}, defaultOption, this.chartOptions)
      // 如果外部传入了series则覆盖默认series，但需要确保data来自chartData
      if (this.chartOptions && this.chartOptions.series) {
        option.series = this.chartOptions.series.map((s, index) => {
          // 如果series中没有data，则使用chartData.series
          if (!s.data && this.chartData.series) {
            return {
              ...s,
              data: Array.isArray(this.chartData.series) ? this.chartData.series : [this.chartData.series]
            }
          }
          return s
        })
      } else {
        // 如果没有传入series，使用默认series但更新data
        if (option.series && option.series[0]) {
          option.series[0].data = this.chartData.series || [0,0,0,0,0]
        }
      }
      // 确保xAxis的data来自chartData
      if (this.chartData.xAxis && option.xAxis) {
        if (Array.isArray(option.xAxis)) {
          option.xAxis[0].data = this.chartData.xAxis
        } else {
          option.xAxis.data = this.chartData.xAxis
        }
      }
      this.chart.setOption(option)
    }
  }
}
</script>
