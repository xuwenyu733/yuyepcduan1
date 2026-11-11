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
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  // 添加对chartData和chartOptions的监听
  watch: {
    chartData: {
      deep: true,
      handler() {
        console.log('图表数据发生变化，重新初始化图表');
        this.initChart();
      }
    },
    chartOptions: {
      deep: true,
      handler() {
        console.log('图表配置发生变化，重新初始化图表');
        this.initChart();
      }
    }
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
      const defaultOption = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: (this.chartData.legend || ['Industries', 'Technology', 'Forex', 'Gold', 'Forecasts'])
        },
        series: [
          {
            name: (this.chartData.seriesName || 'WEEKLY WRITE ARTICLES'),
            type: 'pie',
            roseType: 'radius',
            radius: [15, 75],
            center: ['50%', '30%'],
            data: this.chartData.data || [
              { value: 320, name: 'Industries' },
              { value: 240, name: 'Technology' },
              { value: 149, name: 'Forex' },
              { value: 100, name: 'Gold' },
              { value: 59, name: 'Forecasts' }
            ],
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      }
      const option = Object.assign({}, defaultOption, this.chartOptions)
      if (this.chartOptions && this.chartOptions.series) {
        option.series = this.chartOptions.series
      }
      this.chart.setOption(option)
    }
  }
}
</script>
