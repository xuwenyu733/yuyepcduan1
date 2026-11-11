<template>
  <div>
    <el-row>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">

        <el-form-item label="车间选择" prop="pondId">
            <el-select style="width:100%;" v-model="queryParams.pondId" placeholder="请选择存塘系统编号" filterable clearable @change="handleQuery">
            <el-option v-for="(item, index) in pondList" :key="item.id+'__'+index" :label="`${item.pondNum}(${item.pondName}:${item.pondScale}:${item.fishNum}条)`"
                :value="item.id"></el-option>
            </el-select>
        </el-form-item>

        <el-form-item label="系统选择" prop="systemSelect">
            <el-select v-model="queryParams.fishTypes" placeholder="请输入鱼苗种类" filterable clearable size="small" style="width: 100%;" @change="handleQuery">
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES)" :key="dict.value"
                :label="dict.label" :value="dict.value" />
            </el-select>
        </el-form-item>

        <el-form-item label="时间选择" prop="timeSelect">
            <el-input v-model="queryParams.userName" placeholder="请输入投放人" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="规格选择" prop="fishTypes">
            <el-input v-model="queryParams.userName" placeholder="请输入投放人" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>

        <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
    </el-form>

    </el-row>
    <el-row >
      <el-col :span="3" class="module-button">
        <div class="module-box">
          <el-button type="info" plain>显示方式</el-button>
          <el-button type="info" plain>模型设置</el-button>
          <el-button type="info" plain>运行设置</el-button>
        </div>

      </el-col>
      <el-col :span="21">
        <el-table v-loading="loading" :data="list">
          <el-table-column label="车间" align="center" prop="pondNum" />
          <el-table-column label="系统" align="center" prop="pondNum" />
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
              <template v-slot="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
          </el-table-column>
          <el-table-column label="呼吸频率" align="center" prop="pondNum" />
          <el-table-column label="摄食状态" align="center" prop="pondScale" />
          <el-table-column label="销售数量" align="center" prop="historyFishNum" />
          <el-table-column label="分塘数量" align="center" prop="fishTypes" show-overflow-tooltip>
              <template v-slot="scope">
              <dict-tag :type="DICT_TYPE.ZK_POND_FISH_TYPES" :value="scope.row.fishTypes" />
              </template>
          </el-table-column>
          <el-table-column label="存塘数量" align="center" prop="fishNum" />
          <el-table-column label="规格（克/尾）" align="center" prop="fishSpec" />
          <el-table-column label="总体重量" align="center" prop="currFishNum" />
          <!-- <el-table-column label="投放类型(-2:损失;-1:卖出;1:投放;)" align="center" prop="addType" /> -->
          <el-table-column label="生物资产" align="center" prop="addType" show-overflow-tooltip>
              <template v-slot="scope">
              <dict-tag :type="DICT_TYPE.ZK_POND_FISH_DETAIL_ADD_TYPE" :value="scope.row.addType" />
              </template>
          </el-table-column>

          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
              <template v-slot="scope">
              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="false"
                          v-hasPermi="['pl:zk-pond-fish-detail:update']">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                          v-hasPermi="['pl:zk-pond-fish-detail:delete']">删除</el-button>
              </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
              <template v-slot="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
              <template v-slot="scope">
              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-if="false"
                          v-hasPermi="['pl:zk-pond-fish-detail:update']">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                          v-hasPermi="['pl:zk-pond-fish-detail:delete']">删除</el-button>
              </template>
          </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"@pagination="getList"/>
      </el-col>
    </el-row>
  </div>

</template>

<script>
import { createZkPondFishDetail, updateZkPondFishDetail, deleteZkPondFishDetail, getZkPondFishDetail, getZkPondFishDetailPage, exportZkPondFishDetailExcel } from "@/api/fish/zkPondFishDetail";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { createZkPond, updateZkPond, deleteZkPond, getZkPond, getZkPondPage, exportZkPondExcel } from "@/api/fish/zkPond";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";

export default {
  name: "HealthStatus",
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
      // 存塘鱼苗明细列表
      list: [],
      pondList: [],
      userList1: [],
      dict_flushTypes: [], 
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        zkPondFishId: null,
        workshopId: null,
        pondName: null,
        pondId: null,
        fishTypes: null,
        fishName: null,
        fishNum: null,
        addType: "-2", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
        createTime: [],
        userId: null,
        deptId: null,
        fid: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        zkPondFishId: [{ required: true, message: "存塘鱼苗表id不能为空", trigger: "blur" }],
        fishNum: [{ required: true, message: "鱼苗数量不能为空", trigger: "blur" }],
        fid: [{ required: true, message: "文件id不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    console.log(`投放记录 查看当前页面名称：`, this.$router.currentRoute.name);
    this.getList();
    this.getPondList();
    this.getUserList();
    this.dict_flushTypes = this.getDictDatas(DICT_TYPE.ZK_POND_FISH_TYPES);
    console.log("created=======>"+JSON.stringify(this.dict_flushTypes));
  },
  watch: {
    'form.fishNum': {
      handler(newV, oldV) {
        console.log("===>newV:{},oldV:{}",newV, oldV); // 
        if (this.form.pondId) {
          let newNum = parseInt(this.form.historyFishNum) - parseInt(newV);
          if(newNum < 0) {
            this.$modal.msgWarning("存塘鱼数不能小于0！");
            this.form.fishNum = 0;
          } else {
            this.form.currFishNum = newNum;
          }
        } else {
          this.$modal.msgWarning("请先选择存塘！");
          this.form.fishNum = 0;
        }
        // console.log(`===>newV:${newV},oldV:${oldV},当前鱼数量：${this.form.currFishNum},pondId:${this.form.pondId}`); // 
      }
    },
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkPondFishDetailPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    updateFlushTypes(currType) {
      // 1. 找到对应值
      let item = null;
      this.dict_flushTypes.forEach(obj => {
        if(obj.value == currType) {
          item = obj;
          return;
        }
      })
      // 2. 执行更新操作
      this.form.fishName = item.label;
      console.log(`=====>form:${JSON.stringify(this.form)}`)
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
      this.form.workshopName = item.workshopName;
      this.form.workshopId = item.workshopId;
      this.form.pondName = item.pondName;
      this.form.pondNum = item.pondNum;
      this.form.pondScale = item.pondScale;
      this.form.historyFishNum = item.fishNum;
      this.form.pondId = item.id;
      this.form.userName = item.userName;
      this.form.userId = item.userId;
      console.log("===========>"+JSON.stringify(this.form));
    },
    getPondList() {
      this.pondList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      return getZkPondPage(params).then(response => {
        this.pondList.push(...response.data.list)
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
        zkPondFishId: undefined,
        workshopId: undefined,
        pondName: undefined,
        pondId: undefined,
        fishTypes: undefined,
        fishNum: undefined,
        addType: "-2", // 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
        userId: undefined,
        deptId: undefined,
        fid: undefined,

        pondScale: undefined, //存塘规格
        historyFishNum: undefined, //历史鱼数
        currFishNum: undefined, //现在鱼数
        fishSpec: '400g/尾', //鱼苗规格
        salePrice: undefined, //销售价格
        saleWeight: undefined, //销售重量
        saleIncome: undefined, //销售收入
        saleCost: undefined, //销售成本
        targetPondNum: undefined, //目标存塘编号
        targetPondSpec: undefined, //目标存塘规格
        targetPondId: undefined, //目标存塘Id
        targetPondName: undefined, //目标存塘名称
        targetHistoryFishNum: undefined, //目标历史鱼数
        targetCurrFishNum: undefined, //目标现在鱼数
        remark: undefined, //备注
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
    async handleAdd() {
      await this.getPondList();
      this.reset();
      this.open = true;
      this.title = "添加死亡记录";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkPondFishDetail(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改死亡记录";
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
          updateZkPondFishDetail(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkPondFishDetail(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除存塘鱼苗明细编号为"' + id + '"的数据项?').then(function() {
          return deleteZkPondFishDetail(id);
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
      this.$modal.confirm('是否确认导出所有存塘鱼苗明细数据项?').then(() => {
          this.exportLoading = true;
          return exportZkPondFishDetailExcel(params);
        }).then(response => {
          this.$download.excel(response, '存塘鱼苗明细.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>


<style scoped>
  .el-header, .el-footer {
    background-color: #B3C0D1;
    color: #333;
    text-align: center;
    line-height: 60px;
  }
  
  .el-aside {
    background-color: #D3DCE6;
    color: #333;
    text-align: center;
    line-height: 200px;
  }
  
  .el-main {
    background-color: #E9EEF3;
    color: #333;
    text-align: center;
    line-height: 160px;
  }
  
  body > .el-container {
    margin-bottom: 40px;
  }
  
  .el-container:nth-child(5) .el-aside,
  .el-container:nth-child(6) .el-aside {
    line-height: 260px;
  }
  
  .el-container:nth-child(7) .el-aside {
    line-height: 320px;
  }
  .module-button {
    height:200px; 
    display: flex;
  }
  .module-box {
    width:100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
  }
</style>
