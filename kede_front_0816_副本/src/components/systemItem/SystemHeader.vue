<template>
  <div class="system-header-container">
    <div class="header-content">
      <div class="header-left">
        <h1 class="system-title">{{ systemName }}</h1>
        <div class="header-controls">
          <el-select style="width: 180px; margin-right: 16px;" v-model="queryParams.workshopId" placeholder="请选择车间" filterable clearable @change="handleWorkshopChange">
            <el-option v-for="(item, index) in workshopList" :key="item.workshopId+'__'+index" :label="item.workshopName" :value="item.workshopId"></el-option>
          </el-select>
          <el-select style="width: 180px;" v-model="queryParams.pondNum" placeholder="请选择系统" filterable clearable @change="handleSystemChange">
            <el-option v-for="(item, index) in systemList" :key="item.pondNum+'__'+index" :label="item.pondSystemName" :value="item.pondNum"></el-option>
          </el-select>
        </div>
        <!-- <p class="last-update">最后更新: {{ formattedLastUpdate }}</p> -->
      </div>
      <div class="header-right">
        <button class="refresh-button" @click="goToZkDevice">
          <svg class="refresh-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
          </svg>
          设备列表
        </button>
        <button class="refresh-button" @click="goToDeviceCardShow">
          <svg class="refresh-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
          </svg>
          设备卡片
        </button>
        <button class="refresh-button" @click="$emit('refresh')">
          <svg class="refresh-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          刷新数据
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { getZkPondPage } from "@/api/fish/zkPond";

export default {
  name: 'SystemHeader',
  props: {
    systemName: {
      type: String,
      default: '循环水系统'
    },
    lastUpdate: {
      type: Date,
      default: () => new Date()
    }
  },
  data() {
    return {
      // 车间和系统选择框数据
      pondList: [],
      workshopList: [],
      systemList: [],
      // 查询参数
      queryParams: {
        workshopId: null,
        pondNum: null
      }
    };
  },
  computed: {
    formattedLastUpdate() {
      return this.lastUpdate.toLocaleString('zh-CN')
    }
  },
  created() {
    this.getPondList();
  },
  methods: {
    // 获取池塘列表数据
    getPondList() {
      this.pondList = [];
      let params = {
        pageNo: 1,
        pageSize: 500
      };
      getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list);
        this.getWorkshopList();
      });
    },
    // 构建车间列表和系统列表
    getWorkshopList() {
      let shopTemp = new Set();
      let systemTemp = new Set();
      
      // 清空列表
      this.workshopList = [];
      this.systemList = [];
      
      this.pondList.forEach((item, index) => {
        // 构建车间列表
        if (!shopTemp.has(item.workshopId)) {
          shopTemp.add(item.workshopId);
          this.workshopList.push({
            workshopId: item.workshopId,
            workshopName: item.workshopName
          });
        }
        
        // 构建系统列表
        if (!systemTemp.has(item.pondNum)) {
          systemTemp.add(item.pondNum);
          this.systemList.push({
            pondNum: item.pondNum,
            pondSystemName: item.pondSystemName
          });
        }
      });
    },
    // 车间选择变化事件
    handleWorkshopChange() {
      this.$emit('workshop-change', this.queryParams.workshopId);
    },
    // 系统选择变化事件
    handleSystemChange() {
      this.$emit('system-change', this.queryParams.pondNum);
    },
    // 跳转到设备列表页面
    goToZkDevice() {
      this.$router.push('/keyshow/zkDevice');
    },
    // 跳转到设备卡片展示页面
    goToDeviceCardShow() {
      this.$router.push('/iot/deviceCardShow');
    }
  }
}
</script>

<style scoped>
.system-header-container {
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid #334155;
  border-radius: 12px;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.header-left {
  flex: 1;
}

.system-title {
  font-size: 24px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0 0 16px 0;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.last-update { font-size: 14px; color: #94a3b8; margin: 0; }

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.refresh-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.refresh-button:hover {
  background: linear-gradient(135deg, #1d4ed8, #3b82f6);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
}

.refresh-icon {
  width: 18px;
  height: 18px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .system-title {
    font-size: 20px;
  }
}
</style>