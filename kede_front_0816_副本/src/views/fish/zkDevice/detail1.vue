<template>
  <div class="app-container">

    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="所在车间" prop="workshopName">
        <el-input v-model="queryParams.workshopName" placeholder="请输入所在车间" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="系统编号" prop="pondNum">
        <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统编号" filterable clearable>
          <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName})`"
            :value="item.pondNum"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small"  @change="handleQuery">
          <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_DEVICE_STATUS)" :key="dict.value"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="所在车间id" prop="workshopId">
        <el-input v-model="queryParams.workshopId" placeholder="请输入所在车间id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态(0:关闭中;1:开启中;-1:已废弃;)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0:关闭中;1:开启中;-1:已废弃;)" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件id" prop="fid">
        <el-input v-model="queryParams.fid" placeholder="请输入文件id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 图数据 -->
    <el-row v-if="dataList.length">
      <template v-for="(item, index) in dataList">
        <el-col :span="8" class="card-box">
          <el-card>
            <div slot="header"><span>{{ item.title }}</span></div>
            <div class="el-table el-table--enable-row-hover el-table--medium">
              <div
                :ref="`commandstats_${index}`"
                :id="`commandstats_${index}`"
                style="height: 320px"
              />
            </div>
          </el-card>
        </el-col>
      </template>
    </el-row>
  </div>
</template>

<script>
import { createZkDevice, updateZkDevice, deleteZkDevice, getZkDevice, getZkDevicePage, exportZkDeviceExcel } from "@/api/fish/zkDevice";
import { getZkDeviceRecordPage } from "@/api/fish/zkDeviceRecord";
import { KE_DE_ROLES } from "@/utils/constants";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { listSimpleUsersByRole } from "@/api/system/user";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkWorkshop, updateZkWorkshop, deleteZkWorkshop, getZkWorkshop, getZkWorkshopPage, exportZkWorkshopExcel } from "@/api/fish/zkWorkshop";
import * as echarts from "echarts";
require("echarts/theme/macarons"); // echarts theme

export default {
  name: "ZkDevice",
  components: {
    FileBusUpload, 
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 设备列表
      list: [],
      dataList: [],
      workshopList: [], 
      userList1: [],
      pondList: [], // 存塘列表
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 500,
        deviceName: null,
        pondNum: null,
        workshopName: null,
        workshopId: null,
        userName: null,
        status: null,
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
        deviceType: "0",
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        workshopId: [{ required: true, message: "所在车间id不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      },
      divList: [],
    };
  },
  created() {
    this.getList();
    this.getUserList();
    // this.getWorkshopList();
    this.getPondList();
  },
  methods: {
    toDetail(currItem) {
      console.log(`currItem=============>${JSON.stringify(currItem)}`);
      if(currItem.deviceType == 1) {
        // 视频设备
        this.$router.push({
            path: '/device/zk-device-video',
            // path: '/fish/zk-paper-question',
            params: {},
            query: { deviceId: currItem.id },
        });
      } else if(currItem.deviceType == 0) {
        // 数值设备
        this.$router.push({
            path: '/yangzhi/zk-device-detail1',
            // path: '/fish/zk-paper-question',
            params: {},
            query: { deviceId: currItem.id },
        });
      }
    },
    /** 查询列表 */
    getList() {
      let that = this;
      that.dataList = [];
      this.divList = [];
      this.loading = true;
      getZkDevicePage(this.queryParams).then(async response => {
        this.list = response.data.list || [];
        this.total = response.data.total;
        this.loading = false;
        const chartItems = [];
        for (const item of this.list) {
          const deviceId = item.deviceSn || item.id;
          const metricId = item.deviceMetricId;
          let xData = item.dateList || [];
          let yData = [];
          if (deviceId && metricId) {
            try {
              const rec = await getZkDeviceRecordPage({
                pageNo: 1,
                pageSize: 48,
                deviceId: deviceId,
                metricId: metricId
              });
              const rows = ((rec && rec.data && rec.data.list) || [])
                .map(r => ({ t: r.recordTime, v: Number(r.metricValue) }))
                .filter(r => r.t && !isNaN(r.v))
                .sort((a, b) => new Date(a.t) - new Date(b.t));
              if (rows.length) {
                xData = rows.map(r => {
                  const d = new Date(r.t);
                  return String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0');
                });
                yData = rows.map(r => r.v);
              }
            } catch (e) {
              console.warn('[zkDevice/detail1] record', e);
            }
          }
          chartItems.push({
            title: `${item.pondName || ''}(${item.pondNum || ''})`,
            data: {
              title: { text: `${item.deviceName}-数据监控值` },
              xAxis: { type: 'category', data: xData },
              yAxis: { type: 'value' },
              series: [{ data: yData, type: 'line', smooth: true }]
            }
          });
        }
        that.dataList = chartItems;
        that.initLine();
      });
    },
    /** 已停用随机曲线；注释保留 */
    getRandomData(len) {
      /*
      let arr = [];
      for(let i=0;i<len;i++) {
        arr.push(Math.floor(Math.random() * 10));
      }
      return arr;
      */
      return Array(len || 0).fill(null);
    },
    initLine() {
      let that = this;
      
      this.$nextTick(() => {
        if(that.dataList.length && document.getElementById("commandstats_0")) {
          // 渲染 图形
          that.dataList.forEach((item, index) => {
            let divIndex = `commandstats_${index}`;
            let commandstats = echarts.init(document.getElementById(divIndex), "macarons");
            that.divList.push(commandstats);
            that.divList[index].setOption(item.data);
          });
        }
      })
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    updateUserId(userId) {
      let item = null;
      this.userList1.forEach(obj => {
        if(obj.id == userId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 deptId、userId赋值
      this.form.deptId = item.deptId;
      this.form.userName = item.nickname;
      console.log("===========>"+JSON.stringify(this.form));
    },  
    getUserList() {
      this.userList1 = []
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList1.push(...response.data)
        this.userList1.forEach(obj => {
          let tempStr = `${obj.deptName}-${obj.nickname}`;
          obj.nickname = tempStr;
          // console.log("===========>"+JSON.stringify(obj));
        })
      })
    },
    updatePondId(currId){
      let item = null;
      this.pondList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list)
        // this.pondList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
    },
    updateWorkshopId(currId){
      let item = null;
      this.workshopList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.id;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getWorkshopList() {
      this.workshopList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      getZkWorkshopPage(params).then(response => {
        this.workshopList.push(...response.data.list)
        // this.workshopList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        deviceName: undefined,
        workshopName: "默认车间",
        workshopId: 1,
        pondName: undefined,
        pondId: undefined,
        pondNum: undefined,
        userName: undefined,
        status: undefined,
        userId: undefined,
        deptId: undefined,
        fid: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加设备";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkDevice(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        // 修改的提交
        if (this.form.id != null) {
          updateZkDevice(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkDevice(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除设备编号为"' + id + '"的数据项?').then(function() {
          return deleteZkDevice(id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      // 处理查询参数
      let params = {...this.queryParams};
      params.pageNo = undefined;
      params.pageSize = undefined;
      this.$modal.confirm('是否确认导出所有设备数据项?').then(() => {
          this.exportLoading = true;
          return exportZkDeviceExcel(params);
        }).then(response => {
          this.$download.excel(response, '设备.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
