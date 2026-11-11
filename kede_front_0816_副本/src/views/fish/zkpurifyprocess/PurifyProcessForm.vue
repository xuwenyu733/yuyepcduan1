<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <!-- <el-form-item label="关联档案ID" prop="archiveId">
          <el-input v-model="formData.archiveId" placeholder="请输入关联档案ID" />
        </el-form-item> -->
        <el-form-item label="关联档案" prop="archiveId">
          <el-select style="width:100%;" v-model="formData.archiveId" placeholder="请选择关联档案" filterable clearable  @change="updatePurifyArchiveId">
            <el-option v-for="(item, index) in purifyArchiveList" :key="item.id+'__'+index" :label="`${item.purchaseOrderNo}-${item.source}-${item.inPoolWeight}Kg-${item.inPoolCount}尾`"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="流程单号" prop="recordNumber">
          <el-input v-model="formData.recordNumber" placeholder="请输入流程单号" />
        </el-form-item>
        <!-- <el-form-item label="状态(farming:优化中, out_pool:已完成)" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="1">请选择字典生成</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请输入状态" filterable clearable size="small" style="width: 100%;"> 
            <el-option v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_PROCESS_STATUS)" :key="dict.value"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始确认时间" prop="startTime">
          <el-date-picker clearable v-model="formData.startTime" type="date" value-format="timestamp" style="width: 100%;"
            placeholder="选择开始确认时间" />
        </el-form-item>
        <el-form-item label="负责人" prop="operator">
          <el-input v-model="formData.operator" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="累计死亡数量" prop="totalDeathCount">
          <el-input v-model="formData.totalDeathCount" placeholder="请输入累计死亡数量" />
        </el-form-item>
        <el-form-item label="累计死亡重量" prop="totalDeathWeight">
          <el-input v-model="formData.totalDeathWeight" placeholder="请输入累计死亡重量" />
        </el-form-item>
        <el-form-item label="累计出池数量" prop="totalOutCount">
          <el-input v-model="formData.totalOutCount" placeholder="请输入累计出池数量" />
        </el-form-item>
        <el-form-item label="累计出池重量" prop="totalOutWeight">
          <el-input v-model="formData.totalOutWeight" placeholder="请输入累计出池重量" />
        </el-form-item>
        <el-form-item label="当前存塘数量" prop="currentStockCount">
          <el-input v-model="formData.currentStockCount" placeholder="请输入当前存塘数量" />
        </el-form-item>
        <!-- <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="formData.deptId" placeholder="请输入所属部门" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as PurifyProcessApi from '@/api/fish/zkpurifyprocess';
import { UserInfoKey } from "@/utils/constants";
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import * as PurifyArchiveApi from '@/api/fish/zkpurifyarchive';
export default {
  name: "PurifyProcessForm",
  components: {
  },
  data() {
    return {
      userInfo: {},
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
      formLoading: false,
      // 表单参数
      formData: {
        id: undefined,
        archiveId: undefined,
        recordNumber: undefined,
        status: undefined,
        startTime: undefined,
        operator: undefined,
        totalDeathCount: undefined,
        totalDeathWeight: undefined,
        totalOutCount: undefined,
        totalOutWeight: undefined,
        currentStockCount: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
        archiveId: [{ required: true, message: '关联档案ID不能为空', trigger: 'blur' }],
      },
      purifyArchiveList: [],
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
    this.getPurifyArchiveList();
  },
  methods: {
    updatePurifyArchiveId(currId) {
      let item = null;
      this.purifyArchiveList.forEach(obj => {
        if (obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.formData.purifyArchiveName = item.purifyArchiveName;
      this.formData.purifyArchiveId = item.id;
      console.log("===========>" + JSON.stringify(this.form));
    },
    getPurifyArchiveList() {
      this.purifyArchiveList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      PurifyArchiveApi.getPurifyArchivePage(params).then(response => {
        this.purifyArchiveList.push(...response.data.list);
        // console.log(`purifyArchiveList==================>${JSON.stringify(this.purifyArchiveList)}`);
        // this.purifyArchiveList.forEach(obj => {
        //   let tempStr = `${obj.deptName}-${obj.nickname}`;
        //   obj.nickname = tempStr;
        //   console.log("===========>"+JSON.stringify(obj));
        // })
      })
    },
    /** 打开弹窗 */
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      // 修改时，设置数据
      if (id) {
        this.formLoading = true;
        try {
          const res = await PurifyProcessApi.getPurifyProcess(id);
          this.formData = res.data;
          this.title = "修改优化流程记录";
        } finally {
          this.formLoading = false;
        }
      }
      this.title = "新增优化流程记录";
    },
    /** 提交按钮 */
    async submitForm() {
      // 校验主表
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        if (!this.formData.tenantId) this.formData.tenantId = this.userInfo.tenantId;
        if (!this.formData.deptId) this.formData.deptId = this.userInfo.deptId;
        if (!this.formData.userId) this.formData.userId = this.userInfo.userId;
        const data = this.formData;
        // 修改的提交
        if (data.id) {
          await PurifyProcessApi.updatePurifyProcess(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        // 添加的提交
        await PurifyProcessApi.createPurifyProcess(data);
        this.$modal.msgSuccess("新增成功");
        this.dialogVisible = false;
        this.$emit('success');
      } finally {
        this.formLoading = false;
      }
    },
    /** 表单重置 */
    reset() {
      this.formData = {
        id: undefined,
        archiveId: undefined,
        recordNumber: undefined,
        status: undefined,
        startTime: undefined,
        operator: undefined,
        totalDeathCount: undefined,
        totalDeathWeight: undefined,
        totalOutCount: undefined,
        totalOutWeight: undefined,
        currentStockCount: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>