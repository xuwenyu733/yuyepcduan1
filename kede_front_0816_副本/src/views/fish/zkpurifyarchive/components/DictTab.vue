<template>
  <div class="tab-content">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="字典类型" prop="dictType">
        <el-select v-model="queryParams.dictType" placeholder="请选择字典类型" style="width: 300px"
          @change="handleQuery">
          <el-option v-for="item in dictTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="字典标签" prop="label">
        <el-input v-model="queryParams.label" placeholder="请输入字典标签" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openForm(undefined)"
          :disabled="!queryParams.dictType">新增数据</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column label="字典标签" align="center" prop="label" />
      <el-table-column label="字典键值" align="center" prop="value" />
      <el-table-column label="排序" align="center" prop="sort" width="80" />
      <el-table-column label="状态" align="center" prop="status">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openForm(scope.row)">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNo"
      :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 字典数据弹窗 -->
    <el-dialog :title="dialog.title" :visible.sync="dialog.visible" width="500px" append-to-body>
      <el-form ref="formRef" :model="dialog.form" :rules="dialog.rules" label-width="90px">
        <el-form-item label="字典类型">
          <el-input v-model="dialog.form.dictType" :disabled="true" />
        </el-form-item>
        <el-form-item label="数据标签" prop="label">
          <el-input v-model="dialog.form.label" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据键值" prop="value">
          <el-input v-model="dialog.form.value" placeholder="请输入数据键值" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="dialog.form.sort" controls-position="right" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dialog.form.status">
            <el-radio v-for="dict in getDictDatas(DICT_TYPE.COMMON_STATUS)" :key="dict.value"
              :label="parseInt(dict.value)">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dialog.form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialog.visible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as DictDataApi from '@/api/system/dict/data';
import { DICT_TYPE } from "@/utils/dict";

export default {
  name: "DictTab",
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        dictType: DICT_TYPE.ZK_PURIFY_POOL_STATUS,
        label: null
      },
      dictTypeOptions: [
        { label: '鱼池管理表状态', value: DICT_TYPE.ZK_PURIFY_POOL_STATUS },
        { label: '业务字典明细表字典类型', value: DICT_TYPE.ZK_PURIFY_DICT_DATA_DICT_TYPE },
        { label: '业务字典明细表品类', value: DICT_TYPE.ZK_PURIFY_DICT_DATA_CATEGORY },
        { label: '业务字典明细表来源', value: DICT_TYPE.ZK_PURIFY_DICT_DATA_SOURCE },
        { label: '业务字典明细表规格', value: DICT_TYPE.ZK_PURIFY_DICT_DATA_SPEC },
        { label: '业务字典明细表运输方式', value: DICT_TYPE.ZK_PURIFY_DICT_DATA_TRANSPORT },
        { label: '入池档案表优化等级', value: DICT_TYPE.ZK_PURIFY_ARCHIVE_OPTIMIZATION_LEVEL },
        { label: '入池档案表表现等级', value: DICT_TYPE.ZK_PURIFY_ARCHIVE_SHOW_LEVEL },
        { label: '入池档案表高低等级', value: DICT_TYPE.ZK_PURIFY_ARCHIVE_HIGH_LEVEL },
        { label: '优化流程记录表状态', value: DICT_TYPE.ZK_PURIFY_PROCESS_STATUS },
        { label: '日常监测记录表健康状况', value: DICT_TYPE.ZK_PURIFY_DAILY_MONITORING_HEALTH_STATUS },
        { label: '处理措施记录表措施类型', value: DICT_TYPE.ZK_PURIFY_TREATMENT_TYPE },
        { label: '成本明细记录表成本类型', value: DICT_TYPE.ZK_PURIFY_COST_DETAIL_COST_TYPE }
      ],
      dialog: {
        title: "",
        visible: false,
        form: {
          id: undefined,
          dictType: undefined,
          label: undefined,
          value: undefined,
          sort: 0,
          status: 0,
          remark: undefined
        },
        rules: {
          label: [{ required: true, message: "数据标签不能为空", trigger: "blur" }],
          value: [{ required: true, message: "数据键值不能为空", trigger: "blur" }],
          sort: [{ required: true, message: "数据顺序不能为空", trigger: "blur" }]
        }
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    async getList() {
      this.loading = true;
      try {
        const res = await DictDataApi.listData(this.queryParams);
        this.list = res.data.list;
        this.total = res.data.total;
      } finally {
        this.loading = false;
      }
    },
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.dictType = undefined;
      this.handleQuery();
    },
    openForm(row) {
      this.resetDictForm();
      if (row && row.id) {
        DictDataApi.getData(row.id).then(response => {
          this.dialog.form = response.data;
          this.dialog.title = "修改字典数据";
          this.dialog.visible = true;
        });
      } else {
        this.dialog.form.dictType = this.queryParams.dictType;
        this.dialog.title = "添加字典数据";
        this.dialog.visible = true;
      }
    },
    resetDictForm() {
      this.dialog.form = {
        id: undefined,
        dictType: undefined,
        label: undefined,
        value: undefined,
        sort: 0,
        status: 0,
        remark: undefined
      };
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields();
      }
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          if (this.dialog.form.id !== undefined) {
            DictDataApi.updateData(this.dialog.form).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.dialog.visible = false;
              this.getList();
            });
          } else {
            DictDataApi.addData(this.dialog.form).then(() => {
              this.$modal.msgSuccess("新增成功");
              this.dialog.visible = false;
              this.getList();
            });
          }
        }
      });
    },
    async handleDelete(row) {
      await this.$modal.confirm('确认删除吗？');
      try {
        await DictDataApi.delData(row.id);
        this.$modal.msgSuccess("删除成功");
        this.getList();
      } catch { }
    }
  }
};
</script>
