<template>
  <div class="system-show-container">
    <div class="system-content">
      <!-- 系统头部 -->
      <SystemHeader 
        :system-name="systemData.systemName"
        :last-update="systemData.lastUpdate"
        @refresh="loadSystemData"
      />
      
      <!-- 系统概览 -->
      <SystemOverview :data="systemData" />
      
      <!-- 系统主视图容器 -->
      <div class="system-main-view">
        <!-- 养鱼池 #1 -->
        <FishPond 
          :data="systemData.fishPonds[0]" 
          :position="{ x: 150, y: 250 }" 
        />
        
        <!-- 养鱼池 #2 -->
        <FishPond 
          :data="systemData.fishPonds[1]" 
          :position="{ x: 450, y: 250 }" 
        />
        
        <!-- 养鱼池 #3 -->
        <FishPond 
          :data="systemData.fishPonds[2]" 
          :position="{ x: 750, y: 250 }" 
        />
        
        <!-- 微滤机 -->
        <MicroFilter 
          :data="systemData.microFilter" 
          :position="{ x: 1050, y: 250 }" 
        />
        
        <!-- 生物池 -->
        <BioFilter 
          :data="systemData.bioFilter" 
          :position="{ x: 1350, y: 250 }" 
        />
        
        <!-- 加氧曝气系统 -->
        <AerationSystem 
          :data="systemData.aeration" 
          :position="{ x: 1650, y: 250 }" 
        />
        
        <!-- 管道系统 -->
        <PipeSystem />
      </div>
      <div style="width: 100%;height: 200px;"></div>
    </div>
  </div>
</template>

<script>
import SystemHeader from '@/components/systemItem/SystemHeader.vue'
import SystemOverview from '@/components/systemItem/SystemOverview.vue'
import FishPond from '@/components/systemItem/FishPond.vue'
import MicroFilter from '@/components/systemItem/MicroFilter.vue'
import BioFilter from '@/components/systemItem/BioFilter.vue'
import AerationSystem from '@/components/systemItem/AerationSystem.vue'
import WaterPump from '@/components/systemItem/WaterPump.vue'
import PipeSystem from '@/components/systemItem/PipeSystem.vue'

export default {
  name: "SystemShow",
  components: {
    SystemHeader,
    SystemOverview,
    FishPond,
    MicroFilter,
    BioFilter,
    AerationSystem,
    WaterPump,
    PipeSystem
  },
  data() {
    return {
      systemData: {
        systemName: "循环水系统",
        lastUpdate: new Date(),
        fishPonds: [
          {
            id: "pond1",
            name: "养鱼池 #1",
            temperature: 26.5,
            ph: 7.2,
            dissolvedOxygen: 6.8,
            waterLevel: 85,
            fishCount: 1200
          },
          {
            id: "pond2",
            name: "养鱼池 #2",
            temperature: 26.8,
            ph: 7.1,
            dissolvedOxygen: 6.5,
            waterLevel: 88,
            fishCount: 1150
          },
          {
            id: "pond3",
            name: "养鱼池 #3",
            temperature: 27.1,
            ph: 7.3,
            dissolvedOxygen: 6.9,
            waterLevel: 82,
            fishCount: 1250
          }
        ],
        microFilter: {
          id: "microFilter1",
          status: "running",
          flowRate: 1200,
          pressure: 0.3,
          efficiency: 92.5,
          circulationPumps: [
            {
              id: "pump1",
              status: "running",
              flowRate: 650,
              power: 2.1,
              pressure: 0.25
            },
            {
              id: "pump2",
              status: "running",
              flowRate: 550,
              power: 1.8,
              pressure: 0.22
            }
          ]
        },
        bioFilter: {
          id: "bioFilter1",
          status: "running",
          temperature: 28.2,
          efficiency: 88.3,
          bacteriaActivity: 91.2,
          circulationPump: {
            id: "pump3",
            status: "running",
            flowRate: 1200,
            power: 3.2,
            pressure: 0.28
          }
        },
        aeration: {
          id: "aeration1",
          status: "running",
          oxygenLevel: 95.7,
          airFlowRate: 800,
          power: 4.5,
          circulationPump: {
            id: "pump4",
            status: "running",
            flowRate: 1200,
            power: 3.0,
            pressure: 0.32
          }
        },
        circulationPumps: [
          {
            id: "pump1",
            status: "running",
            flowRate: 650,
            power: 2.1,
            pressure: 0.25
          },
          {
            id: "pump2",
            status: "running",
            flowRate: 550,
            power: 1.8,
            pressure: 0.22
          },
          {
            id: "pump3",
            status: "running",
            flowRate: 1200,
            power: 3.2,
            pressure: 0.28
          },
          {
            id: "pump4",
            status: "running",
            flowRate: 1200,
            power: 3.0,
            pressure: 0.32
          }
        ]
      }
    }
  },
  methods: {
    loadSystemData() {
      // 模拟加载数据
      this.systemData.lastUpdate = new Date()
      console.log('系统数据已刷新')
    }
  },
  mounted() {
    // 模拟定期更新数据
    setInterval(() => {
      this.loadSystemData()
    }, 30000) // 每30秒更新一次
  }
}
</script>

<style scoped>
.system-show-container {
  width: 100%;
  height: calc(100vh - 84px);
  background: linear-gradient(135deg, #1a2a3a 0%, #0d1b2a 100%);
  color: #e2e8f0;
  overflow: auto;
  position: relative;
}

.system-content {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

.system-main-view {
  position: relative;
  width: 100%;
  height: calc(100% - 200px);
  background: rgba(30, 41, 59, 0.7);
  border: 1px solid #334155;
  border-radius: 12px;
  margin-top: 20px;
  overflow: auto;
}

/* 添加基础的网格背景 */
.system-main-view::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(51, 65, 85, 0.3) 1px, transparent 1px),
    linear-gradient(90deg, rgba(51, 65, 85, 0.3) 1px, transparent 1px);
  background-size: 20px 20px;
  pointer-events: none;
  mask-image: linear-gradient(0deg, #1e293b, rgba(30, 41, 59, 0.6));
}
</style>