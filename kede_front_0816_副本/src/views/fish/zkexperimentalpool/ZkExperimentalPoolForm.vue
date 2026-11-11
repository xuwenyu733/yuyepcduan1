<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <el-form-item label="池号" prop="poolNumber">
          <el-select
            v-model="formData.poolNumber"
            placeholder="请选择池号"
            filterable
            allow-create
            default-first-option
            clearable
            style="width: 100%;"
            @change="onPoolNumberChange"
          >
            <el-option
              v-for="(item, index) in poolNumberOptions"
              :key="'pool_' + index + '_' + item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="大小(m³)" prop="size">
          <el-input v-model="formData.size" placeholder="请输入大小(m³)" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="负责人" prop="manager">
          <el-select
            v-model="formData.manager"
            placeholder="请选择负责人"
            filterable
            clearable
            style="width: 100%;"
            @change="onManagerChange"
          >
            <el-option
              v-for="(item, index) in userList"
              :key="item.id + '__' + index"
              :label="item.nickname"
              :value="item.nickname"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" filterable clearable size="small" style="width: 100%;">
            <el-option
              v-for="dict in this.getDictDatas(DICT_TYPE.ZK_PURIFY_POOL_STATUS)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="formData.notes" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkExperimentalPoolApi from '@/api/fish/zkexperimentalpool';
import { UserInfoKey, KE_DE_ROLES } from "@/utils/constants";
import { listSimpleUsersByRole } from "@/api/system/user";
import { getZkPondPage } from "@/api/fish/zkPond";

function buildPoolOptionLabel(code, deptName, workshopName) {
  const parts = [deptName, workshopName].filter(Boolean)
  if (!parts.length) return code
  return code + '（' + parts.join(' / ') + '）'
}

export default {
  name: "ZkExperimentalPoolForm",
  data() {
    return {
      userInfo: {},
      dialogTitle: "",
      dialogVisible: false,
      formLoading: false,
      formData: {
        id: undefined,
        poolNumber: undefined,
        size: undefined,
        location: undefined,
        manager: undefined,
        status: "available",
        notes: undefined,
        deptId: undefined,
      },
      formRules: {
        poolNumber: [{ required: true, message: "池号不能为空", trigger: "change" }],
        size: [{ required: true, message: "大小(m³)不能为空", trigger: "change" }],
        location: [{ required: true, message: "位置不能为空", trigger: "change" }],
        manager: [{ required: true, message: "负责人不能为空", trigger: "change" }],
        status: [{ required: true, message: "状态不能为空", trigger: "change" }],
      },
      poolNumberOptions: [],
      userList: [],
    };
  },
  created() {
    this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey) || '{}');
    this.loadPoolOptions();
    this.loadUserList();
  },
  methods: {
    async fetchAllZkPonds() {
      const pageSize = 200
      let pageNo = 1
      let list = []
      let total = Infinity
      while (list.length < total && pageNo <= 20) {
        const res = await getZkPondPage({ pageNo, pageSize })
        const data = (res && res.data) || {}
        const chunk = Array.isArray(data.list) ? data.list : []
        total = Number(data.total != null ? data.total : chunk.length) || chunk.length
        list = list.concat(chunk)
        if (!chunk.length || chunk.length < pageSize) break
        pageNo += 1
      }
      return list
    },
    async loadPoolOptions() {
      const map = new Map()
      try {
        const ponds = await this.fetchAllZkPonds()
        ponds.forEach(p => {
          const num = String(p.pondDetailNum || p.pondName || p.pondNum || '').trim()
          if (!num) return
          const deptName = String(p.deptName || '').trim()
          const workshopName = String(p.workshopName || '').trim()
          // 同池号优先保留带基地/车间信息的项
          const prev = map.get(num)
          if (prev && prev.deptName && prev.workshopName && !(deptName && workshopName)) return
          map.set(num, {
            value: num,
            label: buildPoolOptionLabel(num, deptName, workshopName),
            deptName,
            workshopName
          })
        })
      } catch (e) { /* ignore */ }
      try {
        const expRes = await ZkExperimentalPoolApi.getZkExperimentalPoolPage({ pageNo: 1, pageSize: 500 })
        const pools = (expRes && expRes.data && expRes.data.list) || []
        pools.forEach(p => {
          const num = String(p.poolNumber || '').trim()
          if (!num || map.has(num)) return
          map.set(num, {
            value: num,
            label: num,
            deptName: '',
            workshopName: ''
          })
        })
      } catch (e) { /* ignore */ }
      this.poolNumberOptions = Array.from(map.values()).sort((a, b) =>
        String(a.value).localeCompare(String(b.value), 'zh-CN')
      )
    },
    loadUserList() {
      this.userList = [];
      listSimpleUsersByRole(KE_DE_ROLES).then(response => {
        this.userList = (response && response.data) || [];
      }).catch(() => {
        this.userList = [];
      });
    },
    onPoolNumberChange(val) {
      const opt = this.poolNumberOptions.find(o => o.value === val)
      if (!opt) return
      // 位置为空时，自动带出「基地 / 车间」
      if (!this.formData.location) {
        const loc = [opt.deptName, opt.workshopName].filter(Boolean).join(' / ')
        if (loc) this.formData.location = loc
      }
    },
    onManagerChange(nickname) {
      const user = this.userList.find(u => u.nickname === nickname);
      if (user) {
        this.formData.userId = user.id;
        this.formData.deptId = user.deptId;
      }
    },
    async open(id) {
      this.dialogVisible = true;
      this.reset();
      this.loadPoolOptions();
      if (id) {
        this.formLoading = true;
        try {
          const res = await ZkExperimentalPoolApi.getZkExperimentalPool(id);
          this.formData = res.data;
          this.dialogTitle = "修改实验鱼池";
        } finally {
          this.formLoading = false;
        }
      } else {
        this.dialogTitle = "新增实验鱼池";
      }
    },
    async submitForm() {
      await this.$refs["formRef"].validate();
      this.formLoading = true;
      try {
        if (!this.formData.tenantId) this.formData.tenantId = this.userInfo.tenantId;
        if (!this.formData.deptId) this.formData.deptId = this.userInfo.deptId;
        if (!this.formData.userId) this.formData.userId = this.userInfo.userId;

        const data = this.formData;
        if (data.id) {
          await ZkExperimentalPoolApi.updateZkExperimentalPool(data);
          this.$modal.msgSuccess("修改成功");
          this.dialogVisible = false;
          this.$emit('success');
          return;
        }
        await ZkExperimentalPoolApi.createZkExperimentalPool(data);
        this.$modal.msgSuccess("新增成功");
        this.dialogVisible = false;
        this.$emit('success');
      } finally {
        this.formLoading = false;
      }
    },
    reset() {
      this.formData = {
        id: undefined,
        poolNumber: undefined,
        size: undefined,
        location: undefined,
        manager: undefined,
        status: "available",
        notes: undefined,
        deptId: undefined,
      };
      this.resetForm("formRef");
    }
  }
};
</script>
