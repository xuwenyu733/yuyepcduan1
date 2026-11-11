<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

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
    autoResize: {
      type: Boolean,
      default: true
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
        this.setOptions(val)
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
      this.setOptions(this.chartData, this.chartOptions)
    },
    setOptions(chartData = {}, chartOptions = {}) {
      const defaultOption = {
        backgroundColor: 'transparent',
        xAxis: {
          data: chartData.xAxis || ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          boundaryGap: false,
          axisTick: { show: false }
        },
        grid: {
          left: 20,
          right: 20,
          bottom: 20,
          top: 40,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          padding: [5, 10]
        },
        yAxis: { axisTick: { show: false } },
        legend: { data: ['生长'] }, // 'expected', 
        series: [
          // {
          //   name: 'expected',
          //   itemStyle: { normal: { color: '#FF005A', lineStyle: { color: '#FF005A', width: 2 } } },
          //   smooth: true,
          //   type: 'line',
          //   data: chartData.expectedData || [],
          //   animationDuration: 2800,
          //   animationEasing: 'cubicInOut'
          // },
          {
            name: '生长曲线',
            smooth: true,
            type: 'line',
            itemStyle: { normal: { color: '#3888fa', lineStyle: { color: '#3888fa', width: 2 }, areaStyle: { color: 'rgba(0, 255, 255, 0.1)' } } },
            data: chartData.actualData || [],
            animationDuration: 2800,
            animationEasing: 'quadraticOut'
          }
        ]
      }
      const option = Object.assign({}, defaultOption, chartOptions)
      if (chartOptions && chartOptions.series) {
        option.series = chartOptions.series
      }
      // 确保xAxis的data来自chartData
      if (chartData.xAxis && option.xAxis) {
        if (Array.isArray(option.xAxis)) {
          option.xAxis[0].data = chartData.xAxis
        } else {
          option.xAxis.data = chartData.xAxis
        }
      }
      
      // 确保series的data来自chartData
      if (option.series && option.series.length > 0) {
        if (chartData.expectedData && option.series[0]) {
          option.series[0].data = chartData.expectedData
        }
        if (chartData.actualData && option.series[1]) {
          option.series[1].data = chartData.actualData
        }
      }
      
      this.chart.setOption(option)
    }
  }
}
</script>
