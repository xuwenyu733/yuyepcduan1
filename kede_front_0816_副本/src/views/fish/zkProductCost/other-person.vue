<template>
  <!-- 7. 其他人工 -->
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <!-- <el-form-item label="人工品牌" prop="productBrand">
        <el-input v-model="queryParams.productBrand" placeholder="请输入品牌" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="人工规格" prop="productSpec">
        <el-input v-model="queryParams.productSpec" placeholder="请输入规格" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="人工单价" prop="productPrice">
        <el-input v-model="queryParams.productPrice" placeholder="请输入单价" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="人工成本" prop="productCost">
        <el-input v-model="queryParams.productCost" placeholder="请输入成本" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="最终成本" prop="finalCost">
        <el-input v-model="queryParams.finalCost" placeholder="请输入最终成本" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="queryParams.remark" placeholder="请输入备注" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <!-- <el-form-item label="类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="请选择类型" clearable size="small">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="负责人id" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入负责人id" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="负责人所属部门" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入负责人所属部门" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item> -->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="queryParams.createTime" style="width: 240px" value-format="yyyy-MM-dd HH:mm:ss" type="daterange"
                        range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" :default-time="['00:00:00', '23:59:59']" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
      <!--  v-hasPermi="['pl:zk-product-cost:create']" -->
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                  >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" :loading="exportLoading"
                   v-hasPermi="['pl:zk-product-cost:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <!-- <el-table-column label="编号" align="center" prop="id" /> -->
      <!-- <el-table-column label="人工品牌" align="center" prop="productBrand" /> -->
      <el-table-column label="用工类型" align="center" prop="remark" />
      <el-table-column label="人数时间" align="center" prop="productSpec" />
      <!-- <el-table-column label="人工单价(元)" align="center" prop="productPrice" />
      <el-table-column label="人工成本(元)" align="center" prop="productCost" /> -->
      <el-table-column label="最终成本(元)" align="center" prop="finalCost" />
      <!-- <el-table-column label="类型" align="center" prop="productType" /> -->
      <!-- <el-table-column label="负责人id" align="center" prop="userId" />
      <el-table-column label="负责人" align="center" prop="userName" />
      <el-table-column label="负责人所属部门" align="center" prop="deptId" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
          <!-- <span>{{ scope.row.createTime }}</span> -->
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <!-- 
           v-hasPermi="['pl:zk-product-cost:update']"
           v-hasPermi="['pl:zk-product-cost:delete']"
           -->
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                    >修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                    >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="title" :visible.sync="open" width="800px" v-dialogDrag append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="160px">
        <!-- <el-form-item label="人工品牌" prop="productBrand">
          <el-input v-model="form.productBrand" placeholder="请输入品牌" />
        </el-form-item> -->
        <el-form-item label="用工类型" prop="remark">
          <el-select style="width:100%;" v-model="form.remark" placeholder="请选择用工类型" filterable clearable>
            <el-option v-for="(item, index) in employmentTypeList" :key="item.label+'__'+index" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="人数时间" prop="productSpec">
          <el-input v-model="form.productSpec" placeholder="请输入 几人 几次/几月" />
        </el-form-item>
        <!-- <el-form-item label="人工单价(元)" prop="productPrice">
          <el-input v-model="form.productPrice" placeholder="请输入单价" 
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item>
        <el-form-item label="人工成本(元)" prop="productCost">
          <el-input v-model="form.productCost" placeholder="请输入成本" 
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item> -->
        <el-form-item label="最终成本(元)" prop="finalCost">
          <el-input v-model="form.finalCost" placeholder="请输入最终成本" 
            oninput="value= value.match(/\d+(\.\d{0,4})?/) ? value.match(/\d+(\.\d{0,4})?/)[0] : ''" />
        </el-form-item>
        <!-- <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item> -->
        <el-form-item label="负责人" prop="userId">
          <el-select style="width:100%;" v-model="form.userId" placeholder="请选择负责人" filterable clearable  @change="updateUserId">
            <el-option v-for="(item, index) in userList1" :key="item.id+'__'+index" :label="item.nickname"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="类型" prop="productType">
          <el-select v-model="form.productType" placeholder="请选择类型">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="负责人id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入负责人id" />
        </el-form-item>
        <el-form-item label="负责人" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="负责人所属部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入负责人所属部门" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createZkProductCost, updateZkProductCost, deleteZkProductCost, getZkProductCost, getZkProductCostPage, exportZkProductCostExcel } from "@/api/fish/zkProductCost";
import FileBusUpload from "@/components/FileUpload";
import { getUUID } from "@/utils";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import { KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { UserInfoKey } from "@/utils/constants";

export default {
  name: "ZkProductCostOtherPerson",
  components: {
      FileBusUpload,
  },
  data() {
    return {
      userInfo: {},
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 成本列表
      list: [],
      userList1: [],
      employmentTypeList: [
        {label: '固定用工', value: '固定用工'},
        {label: '临时用工', value: '临时用工'},
      ],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        productBrand: null,
        productSpec: null,
        productPrice: null,
        productCost: null,
        finalCost: null,
        remark: null,
        productType: "8", // 类型(1:饲料;2:药品;3:设备;4:能源;5:额外;6:固定人工;7:库存;)
        userId: null,
        userName: null,
        deptId: null,
        createTime: [],
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productSpec: [{ required: true, message: "规格不能为空", trigger: "blur" }],
        productPrice: [{ required: true, message: "单价不能为空", trigger: "blur" }],
        productCost: [{ required: true, message: "成本不能为空", trigger: "blur" }],
        finalCost: [{ required: true, message: "最终成本不能为空", trigger: "blur" }],
        userId: [{ required: true, message: "负责人不能为空", trigger: "blur" }],
        remark: [{ required: true, message: "用工类型不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getList();
    this.getUserList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 执行查询
      getZkProductCostPage(this.queryParams).then(response => {
        this.list = response.data.list;
        this.total = response.data.total;
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
      this.form.userId = item.id;
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
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        productBrand: undefined,
        productSpec: undefined,
        productPrice: undefined,
        productCost: undefined,
        finalCost: undefined,
        remark: undefined,
        productType: "8", // 类型(1:人工;2:药品;3:设备;4:能源;5:额外;6:固定人工;7:库存;)
        userId: undefined,
        userName: undefined,
        deptId: undefined,
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
      this.updateUserId(parseInt(this.userInfo.userId));
      this.title = "添加成本";
      this.form.fid = getUUID().replaceAll('-', '');
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id;
      getZkProductCost(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改成本";
        if (!this.form.fid) {
          this.form.fid = getUUID().replaceAll("-", "");
        }
        if(!this.form.userId) {
          this.updateUserId(parseInt(this.userInfo.userId));
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
          updateZkProductCost(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          return;
        }
        // 添加的提交
        createZkProductCost(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除成本编号为"' + id + '"的数据项?').then(function() {
          return deleteZkProductCost(id);
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
      this.$modal.confirm('是否确认导出所有成本数据项?').then(() => {
          this.exportLoading = true;
          return exportZkProductCostExcel(params);
        }).then(response => {
          this.$download.excel(response, '成本.xls');
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
