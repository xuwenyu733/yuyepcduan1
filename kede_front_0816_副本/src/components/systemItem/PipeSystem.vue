<template>
  <div class="pipe-system">
    <!-- 连接点标记 -->
    <ConnectionPoint
      v-for="(point, index) in connectionPointMarkers"
      :key="`point-${index}`"
      :position="point.position"
      :type="point.type"
      :show-label="true"
      :label="point.label"
      :label-position="point.labelPosition"
    />

    <!-- 供水管道系统 -->
    <div class="water-supply-pipes">
      <!-- 供水主管道 -->
      <SmartPipe
        v-for="(pipe, index) in supplyPipes"
        :key="`supply-${index}`"
        :points="pipe.points"
        :stroke-width="pipe.strokeWidth"
        :color="'#4ade80'"
        :show-arrow="true"
        :show-animation="true"
        animation-speed="fast"
      />
    </div>

    <!-- 回水管道系统 -->
    <div class="return-pipes">
      <!-- 回水主管道 -->
      <SmartPipe
        v-for="(pipe, index) in returnPipes"
        :key="`return-${index}`"
        :points="pipe.points"
        :stroke-width="pipe.strokeWidth"
        :color="'#60a5fa'"
        :show-arrow="true"
        :show-animation="true"
        animation-speed="fast"
      />
    </div>
  </div>
</template>

<script>
import SmartPipe from './SmartPipe.vue'
import ConnectionPoint from './ConnectionPoint.vue'

export default {
  name: 'PipeSystem',
  components: {
    SmartPipe,
    ConnectionPoint
  },

  data() {
    return {
      // 设备位置和尺寸配置（基于实际组件布局）
      deviceConfig: {
        fishPonds: [
          { id: 'fishPond1', x: 150, y: 250, width: 192, height: 192, radius: 96 },
          { id: 'fishPond2', x: 450, y: 250, width: 192, height: 192, radius: 96 },
          { id: 'fishPond3', x: 750, y: 250, width: 192, height: 192, radius: 96 }
        ],
        microFilter: { id: 'microFilter', x: 1050, y: 250, width: 192, height: 208 },
        bioFilter: { id: 'bioFilter', x: 1350, y: 250, width: 192, height: 208 },
        aerationSystem: { id: 'aerationSystem', x: 1650, y: 250, width: 192, height: 208 }
      }
    }
  },

  computed: {
    /** 计算所有设备的连接点 */
    connectionPoints() {
      const points = {}

      // 鱼池连接点（基于w-48 h-48的实际尺寸192x192px）
      this.deviceConfig.fishPonds.forEach((pond, index) => {
        points[`fishPond${index + 1}`] = {
          // 上方顶点 - 出水口
          outlet: {
            x: pond.x + pond.radius,
            y: pond.y
          },
          // 下方顶点 - 进水口
          inlet: {
            x: pond.x + pond.radius,
            y: pond.y + pond.radius * 2
          }
        }
      })

      // 微滤机连接点（基于w-48 h-52的实际尺寸192x208px）
      points.microFilter = {
        // 上边中间点 - 进水口
        inlet: {
          x: this.deviceConfig.microFilter.x + this.deviceConfig.microFilter.width / 2,
          y: this.deviceConfig.microFilter.y
        },
        // 下底中间点 - 出水口
        outlet: {
          x: this.deviceConfig.microFilter.x + this.deviceConfig.microFilter.width / 2,
          y: this.deviceConfig.microFilter.y + this.deviceConfig.microFilter.height
        }
      }

      // 生物池连接点（基于w-48 h-52的实际尺寸192x208px）
      points.bioFilter = {
        // 左侧边中间点 - 进水口
        inlet: {
          x: this.deviceConfig.bioFilter.x,
          y: this.deviceConfig.bioFilter.y + this.deviceConfig.bioFilter.height / 2
        },
        // 右侧边中间点 - 出水口
        outlet: {
          x: this.deviceConfig.bioFilter.x + this.deviceConfig.bioFilter.width,
          y: this.deviceConfig.bioFilter.y + this.deviceConfig.bioFilter.height / 2
        }
      }

      // 加氧设备连接点（基于w-48 h-52的实际尺寸192x208px）
      points.aerationSystem = {
        // 左侧边中间点 - 进水口
        inlet: {
          x: this.deviceConfig.aerationSystem.x,
          y: this.deviceConfig.aerationSystem.y + this.deviceConfig.aerationSystem.height / 2
        },
        // 下边中间点 - 出水口
        outlet: {
          x: this.deviceConfig.aerationSystem.x + this.deviceConfig.aerationSystem.width / 2,
          y: this.deviceConfig.aerationSystem.y + this.deviceConfig.aerationSystem.height
        }
      }

      return points
    },

    /** 连接点标记数组 */
    connectionPointMarkers() {
      const points = this.connectionPoints
      const markers = []

      // 鱼池连接点
      this.deviceConfig.fishPonds.forEach((pond, index) => {
        markers.push({
          position: points[`fishPond${index + 1}`].outlet,
          type: 'outlet',
          label: '',
          labelPosition: 'top',
          showLabel: false
        })
        markers.push({
          position: points[`fishPond${index + 1}`].inlet,
          type: 'inlet',
          label: '',
          labelPosition: 'bottom',
          showLabel: false
        })
      })

      // 微滤机连接点
      markers.push({
        position: points.microFilter.inlet,
        type: 'inlet',
        label: '',
        labelPosition: 'top',
        showLabel: false
      })
      markers.push({
        position: points.microFilter.outlet,
        type: 'outlet',
        label: '',
        labelPosition: 'bottom',
        showLabel: false
      })

      // 生物池连接点
      markers.push({
        position: points.bioFilter.inlet,
        type: 'inlet',
        label: '',
        labelPosition: 'left',
        showLabel: false
      })
      markers.push({
        position: points.bioFilter.outlet,
        type: 'outlet',
        label: '',
        labelPosition: 'right',
        showLabel: false
      })

      // 加氧设备连接点
      markers.push({
        position: points.aerationSystem.inlet,
        type: 'inlet',
        label: '',
        labelPosition: 'left',
        showLabel: false
      })
      markers.push({
        position: points.aerationSystem.outlet,
        type: 'outlet',
        label: '',
        labelPosition: 'bottom',
        showLabel: false
      })

      return markers
    },

    /** 供水管道系统 */
    supplyPipes() {
      const points = this.connectionPoints
      const pipes = []

      // 供水主管道Y坐标（组件下方400px处）
      const mainPipeY = 500

      // 1. 加氧设备出水口 → 供水主管道（竖直）
      pipes.push({
        points: [
          points.aerationSystem.outlet,
          { x: points.aerationSystem.outlet.x, y: mainPipeY }
        ],
        strokeWidth: 3.5
      })

      // 2. 供水主管道（水平，最下方）
      pipes.push({
        points: [
          { x: points.aerationSystem.outlet.x, y: mainPipeY },
          { x: points.fishPond1.inlet.x, y: mainPipeY }
        ],
        strokeWidth: 4
      })

      // 3. 供水主管道 → 鱼池1进水口（竖直）
      pipes.push({
        points: [
          { x: points.fishPond1.inlet.x, y: mainPipeY },
          points.fishPond1.inlet
        ],
        strokeWidth: 3.5
      })

      // 4. 供水主管道 → 鱼池2进水口（竖直）
      pipes.push({
        points: [
          { x: points.fishPond2.inlet.x, y: mainPipeY },
          points.fishPond2.inlet
        ],
        strokeWidth: 3.5
      })

      // 5. 供水主管道 → 鱼池3进水口（竖直）
      pipes.push({
        points: [
          { x: points.fishPond3.inlet.x, y: mainPipeY },
          points.fishPond3.inlet
        ],
        strokeWidth: 3.5
      })

      return pipes
    },

    /** 回水管道系统 */
    returnPipes() {
      const points = this.connectionPoints
      const pipes = []

      // 回水主管道Y坐标（组件上方100px处）
      const mainPipeY = 100

      // 1. 鱼池1出水口 → 回水主管道（竖直）
      pipes.push({
        points: [
          points.fishPond1.outlet,
          { x: points.fishPond1.outlet.x, y: mainPipeY }
        ],
        strokeWidth: 3.5
      })

      // 2. 鱼池2出水口 → 回水主管道（竖直）
      pipes.push({
        points: [
          points.fishPond2.outlet,
          { x: points.fishPond2.outlet.x, y: mainPipeY }
        ],
        strokeWidth: 3.5
      })

      // 3. 鱼池3出水口 → 回水主管道（竖直）
      pipes.push({
        points: [
          points.fishPond3.outlet,
          { x: points.fishPond3.outlet.x, y: mainPipeY }
        ],
        strokeWidth: 3.5
      })

      // 4. 回水主管道（水平，组件上方）
      pipes.push({
        points: [
          { x: points.fishPond1.outlet.x, y: mainPipeY },
          { x: points.microFilter.inlet.x, y: mainPipeY }
        ],
        strokeWidth: 4
      })

      // 5. 回水主管道 → 微滤机进水口（竖直）
      pipes.push({
        points: [
          { x: points.microFilter.inlet.x, y: mainPipeY },
          points.microFilter.inlet
        ],
        strokeWidth: 3.5
      })

      // 6. 微滤机出水口 → 生物池进水口（水平 + 竖直）
      pipes.push({
        points: [
          points.microFilter.outlet,
          { x: points.microFilter.outlet.x + 100, y: points.microFilter.outlet.y },
          { x: points.microFilter.outlet.x + 100, y: points.bioFilter.inlet.y },
          points.bioFilter.inlet
        ],
        strokeWidth: 3.5
      })

      // 7. 生物池出水口 → 脱气床氧锥进水口（水平 + 竖直）
      pipes.push({
        points: [
          points.bioFilter.outlet,
          { x: points.bioFilter.outlet.x + 100, y: points.bioFilter.outlet.y },
          { x: points.bioFilter.outlet.x + 100, y: points.aerationSystem.inlet.y },
          { x: points.aerationSystem.inlet.x, y: points.aerationSystem.inlet.y }
        ],
        strokeWidth: 3.5
      })

      return pipes
    }
  }
}
</script>

<style scoped>
.pipe-system {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 5;
}

.water-supply-pipes {
  z-index: 6;
}

.return-pipes {
  z-index: 6;
}
</style>