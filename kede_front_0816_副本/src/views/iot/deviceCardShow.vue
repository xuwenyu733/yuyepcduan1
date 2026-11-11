<template>
  <div class="iot-device-card-container">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><i class="el-icon-location"></i> 首页</el-breadcrumb-item>
        <el-breadcrumb-item>物联网管理</el-breadcrumb-item>
        <el-breadcrumb-item>设备卡片展示</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="header-right">
        <el-button type="primary" @click="refreshDevices">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
    </div>
    
    <div class="search-container">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="80px">
        <el-form-item label="设备ID">
          <el-input v-model="queryParams.deviceSn" placeholder="请输入设备ID" clearable style="width: 200px;"></el-input>
        </el-form-item>
        <el-form-item label="运行状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="正常" value="1"></el-option>
            <el-option label="离线" value="0"></el-option>
            <el-option label="异常" value="-1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车间">
          <el-select v-model="queryParams.workshopId" placeholder="请选择车间" clearable style="width: 150px;">
            <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="item.workshopName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="系统">
          <el-select v-model="queryParams.pondNum" placeholder="请选择系统" clearable style="width: 150px;">
            <el-option v-for="(item, index) in systemList" :key="item.id+'__'+index" :label="item.pondSystemName" :value="item.pondNum"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="池号">
          <el-select v-model="queryParams.pondDetailNum" placeholder="请选择池号" clearable style="width: 150px;">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="item.pondName" :value="item.pondDetailNum"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <i class="el-icon-search"></i> 搜索
          </el-button>
          <el-button @click="resetQuery">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="device-card-grid" v-loading="loading">
      <el-card 
        v-for="(device, index) in deviceList" 
        :key="`${device.deviceId}-${index}-${device.metricId}`"
        class="device-card"
        @click.native="goToDeviceDetail(device)"
      >
        <div slot="header" class="card-header">
          <span class="device-name">{{ device.deviceName || '未知设备' }}</span>
          <el-tag 
            :type="getStatusTagType(device.status)"
            size="small"
            class="status-tag"
          >
            {{ getStatusText(device.status) }}
          </el-tag>
        </div>
        <div class="card-body">
          <div class="info-item">
            <span class="info-label">设备ID：</span>
            <span class="info-value">{{ device.deviceId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">指标ID：</span>
            <span class="info-value">{{ device.metricId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">池号：</span>
            <span class="info-value">{{ device.poolId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">最近更新：</span>
            <span class="info-value">{{ parseTime(device.lastUpdateTime) }}</span>
          </div>
        </div>
        <div class="card-footer">
          <el-button type="text" size="small" @click.stop="goToDeviceDetail(device)">
            查看详情 <i class="el-icon-arrow-right"></i>
          </el-button>
          <el-button type="text" size="small" @click.stop="goToRuleSetting(device)">
            阈值设定 <i class="el-icon-setting"></i>
          </el-button>
        </div>
      </el-card>
    </div>
    
    <div v-if="deviceList.length === 0 && !loading" class="empty-state">
      <el-empty description="暂无设备数据"></el-empty>
    </div>
    
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize" @pagination="getDeviceList"/>
    <div style="width: 100%;height: 200px;"></div>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi';
import { getZkDevicePage } from "@/api/fish/zkDevice";
import { getZkPondPage } from "@/api/fish/zkPond";
import pagination from '@/components/Pagination';
import { loadSharedSiteOptions, pickWorkshopOptionItems, pickSystemOptionItems } from '@/utils/siteOptions';

export default {
  name: 'DeviceCardShow',
  components: {
    pagination
  },
  data() {
    return {
      loading: false, // 加载状态
      deviceList: [],
      total: 0, // 总条数
      originalQueryParams: null, // 存储从表格页面传递过来的原始查询参数
      workshopList: [],
      systemList: [],
      pondList: [],
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        deviceSn: '',
        status: null,
        workshopId: null,
        pondNum: null,
        pondDetailNum: null
      }
    };
  },
  created() {
    // 检查URL中是否有查询参数
    if (this.$route.query.queryParams) {
      try {
        this.originalQueryParams = JSON.parse(this.$route.query.queryParams);
        // 转换查询参数格式以适应卡片展示页面
        this.transformQueryParams();
      } catch (e) {
        console.error('解析查询参数失败:', e);
      }
    }
    this.getPondList();
    this.getDeviceList();
  },
  methods: {
    parseTime,
    
    // 获取设备列表
    getDeviceList() {
      this.loading = true;
      
      // 使用与 zkDevice 相同的 API 获取设备列表
      getZkDevicePage(this.queryParams).then(response => {
        // 映射数据字段：
        // deviceSn -> deviceId
        // deviceMetricId -> metricId
        // pondDetailNum -> poolId
        // createTime -> lastUpdateTime
        this.deviceList = response.data.list.map(device => ({
          deviceId: device.deviceSn,
          deviceName: device.deviceName,
          metricId: device.deviceMetricId,
          poolId: device.pondDetailNum,
          status: device.status,
          lastUpdateTime: device.createTime,
          workshopCode: device.workshopId,
          id: device.id
        }));
        this.total = response.data.total;
        this.loading = false;
      }).catch(error => {
        this.$message.error('获取设备列表失败');
        console.error('获取设备列表失败:', error);
        this.loading = false;
        this.deviceList = [];
        this.total = 0;
      });
    },
    
    // 转换查询参数格式
    transformQueryParams() {
      if (!this.originalQueryParams) return;
      
      // 将原始查询参数映射到当前查询参数
      if (this.originalQueryParams.pageNo) {
        this.queryParams.pageNo = this.originalQueryParams.pageNo;
      }
      if (this.originalQueryParams.pageSize) {
        this.queryParams.pageSize = this.originalQueryParams.pageSize;
      }
      if (this.originalQueryParams.deviceName) {
        this.queryParams.deviceSn = this.originalQueryParams.deviceName;
      }
      if (this.originalQueryParams.status !== null && this.originalQueryParams.status !== undefined) {
        this.queryParams.status = this.originalQueryParams.status;
      }
      if (this.originalQueryParams.workshopId) {
        this.queryParams.workshopId = this.originalQueryParams.workshopId;
      }
      if (this.originalQueryParams.pondNum) {
        this.queryParams.pondNum = this.originalQueryParams.pondNum;
      }
      if (this.originalQueryParams.pondDetailNum) {
        this.queryParams.pondDetailNum = this.originalQueryParams.pondDetailNum;
      }
    },
    
    // 获取池号列表（来自 zk_pond）
    async getPondList() {
      try {
        const [res, siteOpts] = await Promise.all([
          getZkPondPage({ pageNo: 1, pageSize: 500 }),
          loadSharedSiteOptions({ force: false }).catch(() => null)
        ]);
        const list = (res && res.data && res.data.list) || [];
        if (siteOpts && siteOpts.workshopOptions && siteOpts.workshopOptions.length) {
          this.workshopList = pickWorkshopOptionItems(siteOpts).map((o) => ({
            id: o.id != null ? o.id : o.value,
            workshopName: o.label
          }));
          this.systemList = pickSystemOptionItems(siteOpts, '').map((o) => ({
            id: o.value,
            pondSystemName: o.label,
            pondNum: o.label
          }));
          this.pondList = (siteOpts.pondOptions || []).map((o) => ({
            id: o.id,
            pondNum: o.systemName || '',
            pondSystemName: o.systemName || '',
            pondDetailNum: o.rawCode || o.value,
            pondName: o.label,
            workshopId: o.workshopValue,
            workshopName: o.workshopName || ''
          }));
          if (!this.pondList.length) {
            this.pondList = list.map((p) => ({
              id: p.id,
              pondNum: p.pondNum || p.pondSystemName || '',
              pondSystemName: p.pondSystemName || p.pondNum || '',
              pondDetailNum: p.pondDetailNum || '',
              pondName: p.pondName || p.pondDetailNum || '',
              workshopId: p.workshopId,
              workshopName: p.workshopName || ''
            }));
          }
          return;
        }
        this.pondList = list.map((p) => ({
          id: p.id,
          pondNum: p.pondNum || p.pondSystemName || '',
          pondSystemName: p.pondSystemName || p.pondNum || '',
          pondDetailNum: p.pondDetailNum || '',
          pondName: p.pondName || p.pondDetailNum || '',
          workshopId: p.workshopId,
          workshopName: p.workshopName || ''
        }));
        const wsMap = {};
        this.pondList.forEach((item) => {
          if (item.workshopId != null && !wsMap[item.workshopId]) {
            wsMap[item.workshopId] = { id: item.workshopId, workshopName: item.workshopName || ('车间' + item.workshopId) };
          }
        });
        this.workshopList = Object.values(wsMap);
        const sysMap = {};
        this.pondList.forEach((item) => {
          const key = item.pondNum || item.pondSystemName;
          if (key && !sysMap[key]) {
            sysMap[key] = { id: key, pondSystemName: item.pondSystemName || key, pondNum: item.pondNum || key };
          }
        });
        this.systemList = Object.values(sysMap);
      } catch (e) {
        console.warn('[deviceCardShow] getPondList', e);
        this.pondList = [];
        this.workshopList = [];
        this.systemList = [];
      }
    },
    
    // 刷新设备列表
    refreshDevices() {
      this.$message.loading('刷新中...', 0);
      this.getDeviceList();
      setTimeout(() => {
        this.$message.closeAll();
        this.$message.success('刷新成功');
      }, 1000);
    },
    
    // 搜索设备
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getDeviceList();
    },
    
    // 重置搜索
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.queryParams = {
        pageNo: 1,
        pageSize: 10,
        deviceSn: '',
        status: null,
        workshopId: null,
        pondNum: null,
        pondDetailNum: null
      };
      this.getDeviceList();
    },
    
    // 获取状态标签类型
    getStatusTagType(status) {
      switch (status) {
        case 1:
          return 'success';
        case -1:
          return 'danger';
        case 0:
          return 'warning';
        default:
          return 'default';
      }
    },
    
    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 1:
          return '正常';
        case -1:
          return '异常';
        case 0:
          return '离线';
        default:
          return '未知';
      }
    },
    
    // 跳转到设备详情页面
    goToDeviceDetail(device) {
      // 直接使用传入的设备对象，避免再次查找
      if (device && device.deviceId) {
        console.log('跳转到详情页，传递参数:', {
          deviceId: device.deviceId,
          metricId: device.metricId,
          poolId: device.poolId
        });
        
        this.$router.push({
          path: `/iot/deviceDetail/${device.deviceId}`,
          query: {
            deviceSn: device.deviceId,
            metricId: device.metricId,
            poolId: device.poolId // 额外传递池号信息
          }
        });
      } else {
        console.error('设备信息不完整，无法跳转到详情页:', device);
        this.$message.error('设备信息不完整，无法查看详情');
      }
    },
    
    // 跳转到阈值设定页面
    goToRuleSetting(device) {
      if (device && device.deviceId) {
        // 为了传递数据库记录id，我们需要先获取完整的设备信息
        // 由于在卡片列表中可能没有直接的id字段，我们需要先查询一下
        this.loading = true;
        getZkDevicePage({
          pageNo: 1,
          pageSize: 1,
          deviceSn: device.deviceId,
          deviceMetricId: device.metricId
        }).then(response => {
          this.loading = false;
          if (response && response.data && response.data.list && response.data.list.length > 0) {
            const deviceRecord = response.data.list[0];
            console.log('跳转到阈值设定页面，传递参数:', {
              deviceId: device.deviceId,
              metricId: device.metricId,
              recordId: deviceRecord.id
            });
            
            this.$router.push({
              path: '/iot/rullSetting',
              query: {
                deviceCode: device.deviceId,
                metricId: device.metricId,
                recordId: deviceRecord.id // 传递数据库记录id作为查询参数
              }
            });
          } else {
            // 如果查询不到完整信息，至少传递设备ID和指标ID
            console.log('跳转到阈值设定页面，传递基本参数:', {
              deviceId: device.deviceId,
              metricId: device.metricId
            });
            
            this.$router.push({
              path: '/iot/rullSetting',
              query: {
                deviceCode: device.deviceId,
                metricId: device.metricId
              }
            });
          }
        }).catch(error => {
          this.loading = false;
          console.error('获取设备完整信息失败:', error);
          // 错误时至少传递基本信息
          this.$router.push({
            path: '/iot/rullSetting',
            query: {
              deviceCode: device.deviceId,
              metricId: device.metricId
            }
          });
        });
      } else {
        console.error('设备信息不完整，无法跳转到阈值设定页面:', device);
        this.$message.error('设备信息不完整，无法设置阈值');
      }
    }
  }
};
</script>

<style scoped>
.iot-device-card-container {
  padding: 20px;
  height: 100%;
  background-color: #0a1520;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-container {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #0f1a26 !important;
  border-radius: 6px;
  border: 1px solid rgba(64, 158, 255, 0.5) !important;
}

.device-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.device-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
}

/* 深度选择器修改 el-card 的背景色和边框 */
.device-card ::v-deep .el-card {
  background-color: #0f1a26 !important;
  border: 1px solid rgba(64, 158, 255, 0.5) !important;
  color: #ffffff !important;
}

.device-card ::v-deep .el-card__header {
  background-color: #0f1a26 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.device-card ::v-deep .el-card__body {
  background-color: #0f1a26 !important;
  color: #ffffff !important;
}

.device-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
}

.device-card:hover ::v-deep .el-card {
  border-color: rgba(64, 158, 255, 0.5) !important;
  box-shadow: 0 10px 20px rgba(64, 158, 255, 0.2) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #0f1a26 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
}

.device-name {
  font-weight: 600;
  color: #ffffff !important;
}

.status-tag {
  margin-left: 10px;
}

.card-body {
  padding: 20px;
  background-color: #0f1a26 !important;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  align-items: center;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6) !important;
  width: 90px;
  flex-shrink: 0;
}

.info-value {
  font-size: 14px;
  color: #ffffff !important;
  flex: 1;
  word-break: break-all;
}

.card-footer {
  padding: 15px 20px;
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-top: 1px solid rgba(255, 255, 255, 0.1) !important;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 修改卡片底部按钮的文字颜色 */
.card-footer ::v-deep .el-button--text {
  color: #409EFF !important;
}

.card-footer ::v-deep .el-button--text:hover {
  color: #66B1FF !important;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

/* 修改表单元素的样式 */
.search-container ::v-deep .el-form-item__label {
  color: rgba(255, 255, 255, 0.8) !important;
}

.search-container ::v-deep .el-input__inner {
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.search-container ::v-deep .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.4) !important;
}

.search-container ::v-deep .el-select .el-input__inner {
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.search-container ::v-deep .el-select .el-input__suffix {
  color: rgba(255, 255, 255, 0.6) !important;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-right {
    margin-top: 10px;
  }
  
  .device-card-grid {
    grid-template-columns: 1fr;
  }
}
</style>