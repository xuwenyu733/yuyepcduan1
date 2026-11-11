<template>
  <div class="app-container">
    <!-- 返回按钮 -->
    <div class="header-section">

      <h2 class="page-title">阈值设置</h2>
      <p class="page-subtitle">配置设备监控阈值规则</p>
    </div>

    <!-- 设备信息区域 -->
    <el-card class="device-info-card">
      <div slot="header" class="card-header">
        <span>设备信息</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="4">
          <div class="info-item">
            <label>设备ID</label>
            <span>{{ deviceInfo.deviceCode || 'IoT-001' }}</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="info-item">
            <span class="info-label">指标ID：</span>
            <span class="info-value">{{ deviceInfo.metricId }}</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="info-item">
            <label>车间</label>
            <span>{{ deviceInfo.workshop || 'WS-101' }}</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="info-item">
            <label>系统</label>
            <span>{{ deviceInfo.system || '温度监控系统' }}</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="info-item">
            <label>池塘标号</label>
            <span>{{ deviceInfo.pondNo || 'P-001' }}</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="info-item">
            <label>负责人</label>
            <span>{{ deviceInfo.userName || 'yangzhi' }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 阈值配置区域 -->
    <el-card class="threshold-config-card">
      <div slot="header" class="card-header">
        <span>阈值配置</span>
      </div>
      
      <el-form ref="thresholdForm" :model="thresholdForm" :rules="rules" label-width="120px">
        <!-- 阈值配置行 -->
        <div class="threshold-config-row">
          <!-- 最大值配置 -->
          <div class="config-item">
            <el-input v-model.number="thresholdForm.standardEnd" placeholder="最大值" type="number" step="0.01" style="width: 150px;">
              <template slot="append">{{ thresholdForm.deviceUnit }}</template>
            </el-input>
            <el-select v-model="thresholdForm.endComputer" placeholder="符号" style="width: 120px;">
              <el-option label="小于等于(≤)" value="le" />
              <el-option label="小于(<)" value="lt" />
            </el-select>
          </div>
          
          <!-- 测量值 -->
          <div class="config-item measure-value">
            <el-button type="info" size="small" disabled>{{ realtimeValue }}</el-button>
          </div>
          
          <!-- 最小值配置 -->
          <div class="config-item">
            <el-select v-model="thresholdForm.startComputer" placeholder="符号" style="width: 120px;">
              <el-option label="大于等于(≥)" value="ge" />
              <el-option label="大于(>)" value="gt" />
            </el-select>
            <el-input v-model.number="thresholdForm.standardStart" placeholder="最小值" type="number" step="0.01" style="width: 150px;">
              <template slot="append">{{ thresholdForm.deviceUnit }}</template>
            </el-input>
          </div>
          
          <!-- 规则状态开关 -->
          <div class="config-item">
            <el-switch 
              v-model="thresholdForm.ruleStatus" 
              :active-value="1" 
              :inactive-value="0" 
              active-text="启用" 
              inactive-text="禁用" 
              active-color="#13ce66" 
              inactive-color="#ff4949"
              style="margin-left: 20px;"
            />
          </div>
        </div>
        
        <!-- 判断表达式 -->
        <el-form-item label="判断表达式:">
          <el-input v-model="expression" disabled type="textarea" :rows="1" placeholder="表达式将根据阈值自动生成" />
        </el-form-item>
        
        
        <!-- 当前值 -->
        <el-form-item label="当前值:">
          <el-input v-model="realtimeValue" disabled />
          <el-button type="primary" size="small" @click="refreshRealtimeValue" style="margin-left: 10px;">刷新</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 逻辑说明区域 -->
    <div class="logic-explanation">
      <div class="explanation-title">
        <i class="el-icon-info"></i>
        <span>逻辑说明:</span>
      </div>
      <div class="explanation-content">
        <p>请设置至少一个阈值，当实时监测值超出设定范围时，系统将触发告警。</p>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="footer-actions">
      <el-button @click="goBack">取消</el-button>
      <el-button type="primary" @click="saveThresholdConfig">保存设置</el-button>
    </div>
  </div>
</template>

<script>
import { getZkDevicePage, updateZkDevice } from "@/api/fish/zkDevice";

export default {
  name: "ThresholdSetting",
  data() {
    return {
      // 设备信息
      deviceInfo: {},
      // 设备ID和指标ID
      deviceId: '',
      metricId: '',
      recordId: null, // 数据库记录ID
      // 阈值配置表单
      thresholdForm: {
        id: null,
        deviceCode: '',
        metricId: '',
        deviceUnit: '',
        standardStart: null,
        startComputer: 'ge',
        standardEnd: null,
        endComputer: 'le',
        ruleStatus: 1 // 规则状态：1-启用，0-禁用，默认为启用
      },
      // 实时值
      realtimeValue: '实时采集',
      // 表达式
      expression: '[最大值] <= 测量值 >= [最小值]',
      // 表单验证规则
      rules: {
        deviceUnit: [
          { required: true, message: '请输入设备单位', trigger: 'blur' }
        ]
      },
      // 加载状态
      loading: false
    };
  },
  created() {
    // 从URL获取设备ID、指标ID和记录ID
    this.deviceId = this.$route.query.deviceId || this.$route.query.deviceCode || '';
    this.metricId = this.$route.query.metricId || '';
    this.recordId = this.$route.query.recordId || null;
    
    console.log('从路由获取的参数 - deviceId:', this.deviceId, 'metricId:', this.metricId, 'recordId:', this.recordId);
    
    // 加载设备信息
    this.loadDeviceInfo();
  },
  watch: {
    // 监听阈值变化，更新表达式
    'thresholdForm.standardStart': 'updateExpression',
    'thresholdForm.standardEnd': 'updateExpression',
    'thresholdForm.startComputer': 'updateExpression',
    'thresholdForm.endComputer': 'updateExpression'
  },
  methods: {
    /** 加载设备信息 */
    async loadDeviceInfo() {
      // 如果有recordId，优先使用recordId作为查询唯一键
      if (this.recordId) {
        console.log('使用recordId作为查询唯一键:', this.recordId);
        // 直接设置表单的id，稍后保存时直接使用
        this.thresholdForm.id = this.recordId;
      }
      
      // 验证必要参数
      if (!this.deviceId) {
        this.$message.warning('未指定设备ID');
        return;
      }
      
      this.loading = true;
      try {
        // 使用与deviceDetail.vue相同的接口getZkDevicePage获取设备信息
        const queryParams = {
          pageNo: 1,
          pageSize: 1,
          deviceSn: this.deviceId
        };
        
        // 如果有metricId，也作为查询条件
        if (this.metricId) {
          queryParams.deviceMetricId = this.metricId;
        }
        
        // 如果有recordId，可以考虑添加到查询参数中（如果接口支持）
        // 但由于我们使用的是分页接口，可能不支持直接按id查询
        // 不过我们已经在表单中设置了id，保存时会直接使用
        
        console.log('查询设备信息参数:', queryParams);
        const response = await getZkDevicePage(queryParams);
        
        if (!response.data || !response.data.list || response.data.list.length === 0) {
          console.error('获取设备信息失败: 设备数据为空');
          this.$message.error('获取设备信息失败: 设备数据为空');
          // 无数据保持空态（原 setMockDeviceInfo 已停用，方法体注释保留）
          this.setEmptyDeviceInfo(this.deviceId, this.metricId);
          this.loading = false;
          return;
        }
        
        const deviceData = response.data.list[0];
        // 映射数据字段，与deviceDetail.vue保持一致
        this.deviceInfo = {
          deviceCode: deviceData.deviceSn || '',
          metricId: deviceData.deviceMetricId || '',
          workshop: deviceData.workshopId || '',
          system: this.getSystemNameByDeviceType(deviceData.deviceType || ''),
          pondNo: deviceData.pondDetailNum || '',
          userName: deviceData.userName || ''
          // ruleStatus不在deviceInfo中存储，只在thresholdForm中使用
        };
        
        // 更新本地保存的metricId（如果从API返回中获取到）
        if (deviceData.deviceMetricId) {
          this.metricId = deviceData.deviceMetricId;
        }
        
        // 如果从URL获取了recordId，则优先使用URL中的recordId
        const recordIdToUse = this.recordId || deviceData.id;
        
        this.thresholdForm = {
          id: recordIdToUse,
          deviceCode: deviceData.deviceSn || '',
          metricId: deviceData.deviceMetricId || '',
          deviceUnit: deviceData.deviceUnit || '',
          standardStart: parseFloat(deviceData.standardStart) || null,
          startComputer: deviceData.startComputer || 'ge',
          standardEnd: parseFloat(deviceData.standardEnd) || null,
          endComputer: deviceData.endComputer || 'le',
          ruleStatus: deviceData.ruleStatus !== undefined ? Number(deviceData.ruleStatus) : 1 // 确保从数据库读取的值是数字类型，默认为启用(1)
        };
        
        this.realtimeValue = deviceData.currValue || '实时采集';
        this.updateExpression();
        
      } catch (error) {
        console.error('加载设备信息失败:', error);
        this.$message.error('加载设备信息失败，请检查网络或稍后重试');
        this.setEmptyDeviceInfo(this.deviceId, this.metricId);
      } finally {
        this.loading = false;
      }
    },
    
    /** 根据设备类型获取系统名称 */
    getSystemNameByDeviceType(deviceType) {
      const systemMap = {
        '温度传感器': '温度监控系统',
        '湿度传感器': '湿度监控系统',
        '压力传感器': '压力监控系统',
        '流量传感器': '流量监控系统',
        '液位传感器': '液位监控系统'
      };
      return systemMap[deviceType] || '未知监控系统';
    },

    /** 空态（无库数据时）；不再用假设备信息 */
    setEmptyDeviceInfo(deviceCode, metricId) {
      this.deviceInfo = {
        deviceCode: deviceCode || '',
        metricId: metricId || '',
        workshop: '',
        system: '',
        pondNo: '',
        userName: ''
      };
      this.thresholdForm = {
        id: this.recordId || null,
        deviceCode: deviceCode || '',
        metricId: metricId || '',
        deviceUnit: '',
        standardStart: null,
        startComputer: 'ge',
        standardEnd: null,
        endComputer: 'le',
        ruleStatus: 1
      };
      this.realtimeValue = '';
      this.updateExpression();
    },
    
    /** 设置模拟设备信息（已停用，整段注释保留备查） */
    setMockDeviceInfo(deviceCode, metricId) {
      /*
      this.deviceInfo = {
        deviceCode: deviceCode || 'IoT-001',
        metricId: metricId || 'METRIC001',
        workshop: 'WS-101',
        system: '温度监控系统',
        pondNo: 'P-001'
      };
      const recordIdToUse = this.recordId || null;
      this.thresholdForm = {
        id: recordIdToUse,
        deviceCode: deviceCode || 'IoT-001',
        metricId: metricId || 'METRIC001',
        deviceUnit: '',
        standardStart: null,
        startComputer: 'ge',
        standardEnd: null,
        endComputer: 'le',
        ruleStatus: 1
      };
      this.realtimeValue = '实时采集';
      this.updateExpression();
      */
      this.setEmptyDeviceInfo(deviceCode, metricId);
    },
    
    /** 更新表达式 */
    updateExpression() {
      const { standardStart, startComputer, standardEnd, endComputer } = this.thresholdForm;
      
      // 获取符号显示文本
      const getSymbolText = (symbol) => {
        const symbolMap = {
          'gt': '>',
          'ge': '>=',
          'lt': '<',
          'le': '<='
        };
        return symbolMap[symbol] || symbol;
      };
      
      // 构建表达式
      let expr = '';
      if (standardEnd !== null) {
        expr += `${standardEnd} ${getSymbolText(endComputer)} 测量值`;
      }
      
      if (standardStart !== null && standardEnd !== null) {
        expr += ' 且 ';
      }
      
      if (standardStart !== null) {
        expr += `测量值 ${getSymbolText(startComputer)} ${standardStart}`;
      }
      
      this.expression = expr || '[最大值] <= 测量值 >= [最小值]';
    },
    
    /** 刷新实时值 */
    async refreshRealtimeValue() {
      try {
        // 使用与设备信息相同的接口刷新实时值
        // 同时使用deviceId和metricId作为查询条件
        const queryParams = {
          pageNo: 1,
          pageSize: 1,
          deviceSn: this.thresholdForm.deviceCode
        };
        
        // 如果有metricId，也作为查询条件
        if (this.thresholdForm.metricId) {
          queryParams.deviceMetricId = this.thresholdForm.metricId;
        }
        
        console.log('刷新实时值参数:', queryParams);
        const response = await getZkDevicePage(queryParams);
        if (response && response.data && response.data.list && response.data.list.length > 0 && response.data.list[0].currValue) {
          this.realtimeValue = response.data.list[0].currValue;
          this.$message.success('实时值刷新成功');
        }
      } catch (error) {
        console.error('刷新实时值失败:', error);
        this.$message.error('刷新实时值失败，请重试');
      }
    },
    
    /** 保存阈值配置 */
    async saveThresholdConfig() {
      this.$refs['thresholdForm'].validate(async valid => {
        if (!valid) {
          return false;
        }
        
        // 验证至少设置一个阈值
        if (this.thresholdForm.standardStart === null && this.thresholdForm.standardEnd === null) {
          this.$message.error('请至少设置一个阈值');
          return false;
        }
        
        // 如果两个阈值都设置了，验证最大值必须大于最小值
        if (this.thresholdForm.standardEnd !== null && this.thresholdForm.standardStart !== null) {
          if (this.thresholdForm.standardEnd <= this.thresholdForm.standardStart) {
            this.$message.error('最大值必须大于最小值');
            return false;
          }
        }
        
        // 如果没有id，需要先查询获取唯一记录的id
        if (!this.thresholdForm.id) {
          try {
            const queryParams = {
              pageNo: 1,
              pageSize: 1,
              deviceSn: this.thresholdForm.deviceCode
            };
            
            // 如果有metricId，也作为查询条件
            if (this.thresholdForm.metricId) {
              queryParams.deviceMetricId = this.thresholdForm.metricId;
            }
            
            console.log('查询设备记录ID参数:', queryParams);
            const response = await getZkDevicePage(queryParams);
            
            if (response && response.data && response.data.list && response.data.list.length > 0) {
              this.thresholdForm.id = response.data.list[0].id;
              console.log('获取到设备记录ID:', this.thresholdForm.id);
            } else {
              this.$message.error('无法找到对应的设备记录');
              return false;
            }
          } catch (error) {
            console.error('查询设备记录失败:', error);
            this.$message.error('查询设备记录失败，请重试');
            return false;
          }
        }
        
        this.loading = true;
        try {
          // 构建更新对象，确保包含所有必要字段
          const updateData = {
            id: Number(this.thresholdForm.id), // 确保id是数字类型
            deviceSn: this.deviceInfo.deviceCode, // 设备编码，必填项
            deviceMetricId: this.deviceInfo.metricId, // 指标ID，必填项
            standardStart: this.thresholdForm.standardStart,
            startComputer: this.thresholdForm.startComputer,
            standardEnd: this.thresholdForm.standardEnd,
            endComputer: this.thresholdForm.endComputer,
            ruleStatus: Number(this.thresholdForm.ruleStatus), // 规则状态：1-启用，0-禁用
            workshopId: this.thresholdForm.workshopId || 1, // 添加workshopId字段，默认为1
            fid: this.thresholdForm.fid || 'default_fid' // 添加fid字段，默认为default_fid
          };
          
          console.log('保存阈值配置参数:', updateData);
          await updateZkDevice(updateData);
          this.$message.success('阈值配置保存成功');
        } catch (error) {
          console.error('保存阈值配置失败:', error);
          this.$message.error('保存阈值配置失败，请重试');
        } finally {
          this.loading = false;
        }
      });
    },
    
    /** 返回上一页 */
    goBack() {
      this.$router.back();
    }
  }
};
</script>

<style scoped>
.app-container {
  background-color: #0a1520;
  padding: 20px;
  min-height: 100%;
}

.header-section {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  margin: 10px 0 5px 0;
  color: #ffffff !important;
}

.page-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6) !important;
  margin: 0;
}

.device-info-card,
.threshold-config-card {
  margin-bottom: 20px;
}

/* 深度选择器修改 el-card 的背景色和边框 */
.device-info-card ::v-deep .el-card,
.threshold-config-card ::v-deep .el-card {
  background-color: #0f1a26 !important;
  border: 1px solid rgba(64, 158, 255, 0.5) !important;
  color: #ffffff !important;
}

.device-info-card ::v-deep .el-card__header,
.threshold-config-card ::v-deep .el-card__header {
  background-color: #0f1a26 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.device-info-card ::v-deep .el-card__body,
.threshold-config-card ::v-deep .el-card__body {
  background-color: #0f1a26 !important;
  color: #ffffff !important;
}

.card-header {
  font-weight: bold;
  color: #ffffff !important;
}

.info-item {
  margin-bottom: 10px;
}

.info-item label {
  display: inline-block;
  width: 80px;
  color: rgba(255, 255, 255, 0.6) !important;
}

.info-item span {
  color: #ffffff !important;
  font-weight: 500;
}

.info-label {
  color: rgba(255, 255, 255, 0.6) !important;
}

.info-value {
  color: #ffffff !important;
}

.threshold-config-row {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid rgba(64, 158, 255, 0.5) !important;
  border-radius: 4px;
  background-color: rgba(255, 255, 255, 0.05) !important;
}

.config-item {
  display: flex;
  align-items: center;
  margin: 0 10px;
}

.config-label {
  display: block;
  margin-bottom: 8px;
  color: rgba(255, 255, 255, 0.6) !important;
  font-size: 14px;
  text-align: center;
}

.measure-value {
  margin: 0 40px;
}

.measure-value .el-button {
  padding: 15px 30px;
  font-size: 16px;
  font-weight: bold;
}

.logic-explanation {
  background-color: rgba(64, 158, 255, 0.1) !important;
  border-left: 4px solid rgba(64, 158, 255, 0.5) !important;
  padding: 15px;
  margin-bottom: 30px;
}

.explanation-title {
  font-weight: bold;
  color: #409EFF !important;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.explanation-title i {
  margin-right: 8px;
}

.explanation-content {
  color: rgba(255, 255, 255, 0.8) !important;
  line-height: 1.6;
}

.footer-actions {
  text-align: center;
  margin-top: 30px;
  padding: 20px 0;
}

.footer-actions .el-button {
  min-width: 100px;
  margin: 0 10px;
}

/* 修改表单元素的样式 */
.threshold-config-card ::v-deep .el-form-item__label {
  color: rgba(255, 255, 255, 0.8) !important;
}

.threshold-config-card ::v-deep .el-input__inner {
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.threshold-config-card ::v-deep .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.4) !important;
}

.threshold-config-card ::v-deep .el-input-group__append {
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.8) !important;
}

.threshold-config-card ::v-deep .el-select .el-input__inner {
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.threshold-config-card ::v-deep .el-select .el-input__suffix {
  color: rgba(255, 255, 255, 0.6) !important;
}

.threshold-config-card ::v-deep .el-textarea__inner {
  background-color: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

.threshold-config-card ::v-deep .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.4) !important;
}

/* 修改开关样式 */
.threshold-config-card ::v-deep .el-switch__label {
  color: rgba(255, 255, 255, 0.8) !important;
}

.threshold-config-card ::v-deep .el-switch__label.is-active {
  color: #409EFF !important;
}
</style>