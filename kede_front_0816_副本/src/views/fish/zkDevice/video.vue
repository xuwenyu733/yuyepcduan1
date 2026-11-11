<template>
  
  <div class="app-container">
  <el-container>
    <el-header>
      <el-row class="search_box">
        <el-col :span="24">
          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
            <el-form-item label="车间" prop="workshopId">
              <el-select style="width:100%;" v-model="queryParams.workshopId" placeholder="请选择车间" filterable clearable @change="querySearch($event, -2.2)">
                <el-option v-for="(item, index) in workshopList" :key="item.id+'__'+index" :label="`${item.workshopName}`"
                  :value="item.workshopId"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="系统" prop="pondNum">
              <el-select style="width:100%;" v-model="queryParams.pondNum" placeholder="请选择系统" filterable clearable @change="querySearch($event, -2.3)">
                <el-option v-for="(item, index) in systemList" :key="item.id+'__'+index" :label="`${item.pondSystemName}`"
                  :value="item.pondNum"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="池号" prop="pondDetailNum">
              <el-select style="width:100%;" v-model="queryParams.pondDetailNum" placeholder="请选择池号" filterable clearable @change="querySearch($event, -2.4)">
                <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondName}`"
                  :value="item.pondDetailNum"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="起始时间" prop="createTime">
              <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                              range-separator="-" start-placeholder="起始时间" end-placeholder="截至时间" :default-time="['00:00:00', '23:59:59']" />
            </el-form-item>
  

            <!-- <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请输入状态" filterable clearable size="small">
                <el-option v-for="dict in this.getDictDatas(DICT_TYPE.USE_MEDICINE_STATUS)" :key="dict.value"
                  :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item> -->
            <!-- <el-form-item label="附件" prop="fids">
              <el-input v-model="queryParams.fids" placeholder="请输入附件" clearable @keyup.enter.native="handleQuery"/>
            </el-form-item>
            <el-form-item label="流程实例" prop="processInstanceId">
              <el-input v-model="queryParams.processInstanceId" placeholder="请输入流程实例" clearable @keyup.enter.native="handleQuery"/>
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
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <div class="zk-device-video-container">
        <div class="left">
          <div class="head">监控列表</div>
          <!-- <div class="middle">
            <el-input placeholder="摄像头名称" v-model="queryParams.deviceName" clearable class="input-with-select">
              <el-button slot="append" icon="el-icon-search" @click="handleQuery"></el-button>
            </el-input>
          </div> -->
          <div class="bottom">
            <template v-for="(item, index) in list">
              <div class="item" @click="handleVideoItemClick(item)">
                <div class="icon"><i class="el-icon-video-camera"></i></div>
                <div class="itemName">{{ item.deviceName+`(${item.pondName})` }}</div>
              </div>
            </template>
          </div>
          <div class="footer">
            <el-pagination
              small
              layout="prev, pager, next"
              :page.sync="queryParams.pageNo"
              :limit.sync="queryParams.pageSize"
              @pagination="getList"
              :total="total">
            </el-pagination>
          </div>
        </div>
        <div class="right">
          <div class="right_head">{{ currentDeviceName || '请选择左侧摄像头' }}</div>
          <div class="right_middle">
            <ez-player
              v-if="currentDeviceSerial"
              :key="currentDeviceSerial + '-' + currentChannelNo"
              :deviceSerial="currentDeviceSerial"
              :channelNo="currentChannelNo"
            />
            <div v-else class="video-placeholder">点击左侧监控列表播放</div>
          </div>
        </div>
      </div>
    </el-main>
  </el-container>
  </div>
</template>

<script>
import { createZkDevice, updateZkDevice, deleteZkDevice, getZkDevice, getZkDevicePage, exportZkDeviceExcel } from "@/api/fish/zkDevice";
import { KE_DE_ROLES } from "@/utils/constants";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { listSimpleUsersByRole } from "@/api/system/user";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkWorkshop, updateZkWorkshop, deleteZkWorkshop, getZkWorkshop, getZkWorkshopPage, exportZkWorkshopExcel } from "@/api/fish/zkWorkshop";
import EzPlayer from '@/components/EzvizPlayer/EzPlayer.vue'
import { isRowInUserSiteScope, peekSharedSiteOptions, loadSharedSiteOptions, SITE_OPTIONS_CHANGED_EVENT } from '@/utils/siteOptions'
import { USER_SCOPE_CHANGED_EVENT } from '@/utils/deptScope'

export default {
  name: "ZkDeviceVideo",
  components: {
    FileBusUpload,
    EzPlayer
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
      myList: [
        "办公室摄像头", "会议室nvr"
      ],
      list: [],
      currentDeviceSerial: '',
      currentChannelNo: 1,
      currentDeviceName: '',
      pondList: [],
      workshopList: [],
      systemList: [],
      userList1: [],
      sharedSiteOptions: peekSharedSiteOptions() || { workshopOptions: [], systemOptions: [], pondOptions: [] },
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
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
        deviceType: "1",
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        workshopId: [{ required: true, message: "所在车间id不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
    this.getUserList();
    // this.getWorkshopList();
    this.getPondList();
    this.loadSharedSiteOptions(true);
  },
  mounted() {
    var self = this
    this._onSiteOptionsChanged = function () { self.loadSharedSiteOptions(true) }
    this._onUserScopeChanged = function () {
      self.list = []
      self.currentDeviceSerial = ''
      self.currentDeviceName = ''
      self.loadSharedSiteOptions(true).finally(function () {
        self.getPondList()
        self.getList()
      })
    }
    window.addEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged)
    window.addEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged)
  },
  beforeDestroy() {
    if (this._onSiteOptionsChanged) {
      window.removeEventListener(SITE_OPTIONS_CHANGED_EVENT, this._onSiteOptionsChanged)
    }
    if (this._onUserScopeChanged) {
      window.removeEventListener(USER_SCOPE_CHANGED_EVENT, this._onUserScopeChanged)
    }
  },
  methods: {
    loadSharedSiteOptions(force) {
      var self = this
      return loadSharedSiteOptions({ includePonds: true, force: !!force }).then(function (opts) {
        self.sharedSiteOptions = opts || self.sharedSiteOptions
      }).catch(function () { /* ignore */ })
    },
    filterDevicesByDeptScope(list) {
      var opts = this.sharedSiteOptions
      return (list || []).filter(function (item) {
        return isRowInUserSiteScope({
          workshop: item.workshopName || item.workshop,
          workshopName: item.workshopName || item.workshop,
          workshopId: item.workshopId,
          systemName: item.pondSystemName || item.systemName,
          pondSystemName: item.pondSystemName || item.systemName,
          pondDetailNum: item.pondDetailNum,
          pondName: item.pondName
        }, opts)
      })
    },
    querySearch(item, searchType) {
      this.searchType = searchType;
      this.queryParams.batchNumber = "";
      this.queryParams.workshopId = "";
      this.queryParams.pondNum = "";
      this.queryParams.pondDetailNum = "";
      this.queryParams.logType = searchType;
      // console.log(`================>${JSON.stringify(this.pondList)}`);
      if(this.searchType == -2.1) {
        this.queryParams.batchNumber = item;
      } else if(this.searchType == -2.2) {
        this.queryParams.workshopId = item;
      } else if(this.searchType == -2.3) {
        // 系统
        let currPond = this.getPondInfo(this.pondList, null, item, null);
        this.queryParams.workshopId = currPond.workshopId;
        this.queryParams.pondNum = item;
      } else if(this.searchType == -2.4) {
        // 池号
        let currPond = this.getPondInfo(this.pondList, null, null, item);
        this.queryParams.workshopId = currPond.workshopId;
        this.queryParams.pondNum = currPond.pondNum;
        this.queryParams.pondDetailNum = item;
      }
      this.getList();
    },
    getPondInfo(list, workshopId, pondNum, pondDetailNum) {
      if(!(list && list.length)) return;
      let item = null;
      list.forEach((curr, index) => {
        if(workshopId == curr.workshopId) {
          item = curr;
          return;
        }
        if(pondNum == curr.pondNum) {
          item = curr;
          return;
        }
        if(pondDetailNum == curr.pondDetailNum) {
          item = curr;
          return;
        }
      })
      return item;
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询（接口已带 deptId；再按站点白名单兜底）
      getZkDevicePage(this.queryParams).then(response => {
        var raw = (response && response.data && response.data.list) || [];
        this.list = this.filterDevicesByDeptScope(raw);
        this.total = Number((response && response.data && response.data.total) || this.list.length);
        this.loading = false;
      }).catch(() => {
        this.list = [];
        this.loading = false;
      });
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
      // console.log("===========>"+JSON.stringify(this.form));
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
      // console.log("===========>"+JSON.stringify(this.form));
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
        // console.log(`=============>${JSON.stringify(this.pondList)}`);
        this.getWorkshopList();
      })
    },
    getWorkshopList() {
      let shopTemp = [];
      let systemTemp = [];
      this.pondList.forEach((item, index) => {
        if(!shopTemp.includes(item.workshopId)) {
          this.workshopList.push(item);
        }
        if(!systemTemp.includes(item.pondNum)) {
          this.systemList.push(item);
        }
      })
      // console.log(`workshopList====================>${JSON.stringify(this.workshopList)}`);
      // console.log(`systemList====================>${JSON.stringify(this.systemList)}`);
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
    /** 视频项点击事件 */
    handleVideoItemClick(item) {
      const serial = item.deviceSn || item.deviceSerial || item.deviceId || ''
      if (!serial) {
        this.$message.warning('该设备缺少序列号(deviceSn)，无法播放萤石流')
        return
      }
      this.currentDeviceSerial = String(serial)
      this.currentChannelNo = Number(item.channelNo || item.channel || 1) || 1
      this.currentDeviceName = (item.deviceName || '') + (item.pondName ? `（${item.pondName}）` : '')
      // 同步到萤石页，便于新开页
      this.$router.replace({
        path: this.$route.path,
        query: {
          deviceSerial: this.currentDeviceSerial,
          channelNo: String(this.currentChannelNo),
          deviceName: item.deviceName || ''
        }
      }).catch(() => {})
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
<style lang="scss" scoped>
.zk-device-video-container {
  display: flex;
  .left {
    flex: 1;
    height: 100%;
    .head {
      font-size: 15px;
      padding: 15px 0;
    }
    .bottom {
      min-height: 650px;
      .item {
        display: flex;
        flex-direction: row;
        font-size: 14px;
        padding: 15px 0;
        cursor: pointer;
        .icon {
          margin-right: 7px;
        }
      }
    }
    .footer {
      display: flex;
      justify-content: center;
      margin-left: -26px;
    }
  }
  .right {
    flex: 5;
    height: 100%;
    margin-left: 15px;
    .right_head {
      padding: 12px 0;
      font-size: 14px;
      color: #606266;
    }
    .right_middle {
      height: 640px;
      background: #0b1220;
      border-radius: 6px;
      overflow: hidden;
    }
    .video-placeholder {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #8b9bb4;
      font-size: 14px;
    }
  }
}
</style>
