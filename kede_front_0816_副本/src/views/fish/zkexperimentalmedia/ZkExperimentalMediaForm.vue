<template>
  <div class="app-container">
    <!-- 对话框(添加 / 修改) -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="45%" v-dialogDrag append-to-body>
      <el-form ref="formRef" :model="formData" :rules="formRules" v-loading="formLoading" label-width="100px">
        <!-- <el-form-item label="实验id" prop="experimentId">
          <el-input v-model="formData.experimentId" placeholder="请输入实验id" />
        </el-form-item>
        <el-form-item label="实验编号" prop="experimentNumber">
          <el-input v-model="formData.experimentNumber" placeholder="请输入实验编号" />
        </el-form-item> -->
        <!-- <el-form-item label="文件类型(1:图片;2:视频;)" prop="fileType">
          <el-select v-model="formData.fileType" placeholder="请选择文件类型(1:图片;2:视频;)">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="实验" prop="experimentalId">
          <el-select style="width:100%;" v-model="formData.experimentId" placeholder="请选择实验" filterable clearable  @change="update_ExperimentalId">
            <el-option v-for="(item, index) in experimentalList" :key="item.id+'__'+index" :label="item.experimentTitle"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文件类型" prop="fileType">
          <el-select v-model="formData.fileType" placeholder="请输入文件类型" filterable clearable size="small" style="width: 100%;">
            <el-option v-for="(dict, index) in this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_MEDIA_FILE_TYPE)" :key="dict.value+'_33_'+index"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="体表类别" prop="bodyType">
          <el-select v-model="formData.bodyType" placeholder="请选择体表类别" filterable clearable  @change="update_bodyType" size="small" style="width: 100%;">
            <el-option v-for="(dict, index) in bodyTypeList" :key="dict.value+'_33_'+index"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="子类别" prop="sonBodyType" v-if="sonBodyTypeList && sonBodyTypeList.length>0">
          <el-select v-model="formData.sonBodyType" placeholder="请选择子类别" size="small" style="width: 100%;">
            <el-option v-for="(dict, index) in sonBodyTypeList" :key="dict.value+'_33_'+index"
              :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="文件id" prop="fid">
          <el-input v-model="formData.fid" placeholder="请输入文件id" />
        </el-form-item> -->
        <el-form-item label="媒体文件" prop="fid">
          <FileBusUpload
            ref="uploadRef"
            :key="'media-upload-' + formData.fileType"
            :fid="formData.fid"
            bus-type="zk_experimental_media"
            :file-size="uploadFileSize"
            :file-type="uploadFileTypes"
            :accept="uploadAccept"
            @uploadSuccess="confirmUrl"
          />
        </el-form-item>
        <!-- <el-form-item label="文件名列表" prop="fileNames">
          <el-input v-model="formData.fileNames" placeholder="请输入文件名列表" />
        </el-form-item> -->
        <el-form-item label="文件地址" prop="fileUrls">
          <el-input v-model="formData.fileUrls" placeholder="请输入文件地址" readonly />
        </el-form-item>
        <!-- <el-form-item label="所属部门" prop="deptId">
          <el-input v-model="formData.deptId" placeholder="请输入所属部门" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="formLoading || !hasUploadedFiles">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkExperimentalMediaApi from '@/api/fish/zkexperimentalmedia';
import * as ZkExperimentalApi from '@/api/fish/zkexperimental';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";
export default {
  name: "ZkExperimentalMediaForm",
  components: {
    FileBusUpload,
  },
  data() {
    return {
      // 弹出层标题
      dialogTitle: "",
      // 是否显示弹出层
      dialogVisible: false,
      // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
      formLoading: false,
      // 表单参数
      formData: {
        id: undefined,
        experimentId: undefined,
        experimentNumber: undefined,
        fileType: undefined,
        bodyType: undefined,
        sonBodyType: undefined,
        fid: undefined,
        fileNames: undefined,
        fileUrls: undefined,
        deptId: undefined,
      },
      // 表单校验
      formRules: {
      },
      experimentalList: [], 
      bodyTypeList: [], 
      sonBodyTypeList: [], 
      fileUrls: [],
      fileNames: [],
    };
  },
  created() {
    this.bodyTypeList = this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_MEDIA_JSON);
    this.get_ExperimentalList();
  },
  computed: {
    hasUploadedFiles() {
      const urls = String(this.formData.fileUrls || '')
        .split(',')
        .map(u => u.trim())
        .filter(Boolean)
      return urls.length > 0
    },
    isVideoUpload() {
      const v = this.formData.fileType
      if (v === 2 || v === '2') return true
      const dicts = this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_MEDIA_FILE_TYPE) || []
      const hit = dicts.find(d => String(d.value) === String(v))
      return !!(hit && String(hit.label).includes('视频'))
    },
    uploadFileSize() {
      return this.isVideoUpload ? 500 : 20
    },
    uploadFileTypes() {
      if (this.isVideoUpload) {
        return ['mp4', 'mov', 'avi', 'mkv', 'wmv', 'flv', 'webm', 'm4v', 'mpeg', 'mpg', '3gp', 'ts']
      }
      return ['png', 'jpg', 'jpeg', 'gif', 'bmp', 'webp']
    },
    uploadAccept() {
      if (this.isVideoUpload) {
        return 'video/*,.mp4,.mov,.avi,.mkv,.wmv,.flv,.webm,.m4v,.mpeg,.mpg,.3gp,.ts'
      }
      return 'image/*,.png,.jpg,.jpeg,.gif,.bmp,.webp'
    }
  },
  methods: {
    confirmUrl(obj='') {
      if (!obj || typeof obj !== 'object') return
      const name = obj.name
      const url = obj.url
      if (!url || !String(url).trim()) {
        this.$modal.msgWarning('文件上传未成功，请重新上传')
        return
      }
      this.fileUrls.push(String(url).trim())
      this.fileNames.push(name || String(url).trim())
      this.formData.fileUrls = this.fileUrls.join(',')
      this.formData.fileNames = this.fileNames.join(',')
    },
    update_bodyType(parentValue) {
      let curr = null;
      this.bodyTypeList.forEach(obj => {
        if(obj.value == parentValue) {
          curr = obj;
          return;
        }
      })
      if(!curr || !curr.remark) {
        this.sonBodyTypeList = [];
        this.formData.sonBodyType = "";
        return;
      }
      this.sonBodyTypeList = JSON.parse(curr.remark);
      // console.log(`sonBodyTypeList================>${JSON.stringify(this.sonBodyTypeList)}`);
    },
    update_ExperimentalId(currId){
      let item = null;
      this.experimentalList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.formData.experimentTitle = item.experimentTitle;
      this.formData.experimentalId = item.id;
      this.formData.tenantId = item.tenantId;
      this.formData.deptId = item.deptId;
      this.formData.experimentNumber = item.experimentNumber;
      console.log("===========>"+JSON.stringify(this.form));
    },
    get_ExperimentalList() {
      this.experimentalList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      ZkExperimentalApi.getZkExperimentalPage(params).then(response => {
        this.experimentalList.push(...response.data.list)
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
          const res = await ZkExperimentalMediaApi.getZkExperimentalMedia(id);
          this.formData = res.data;
          this.title = "修改实验多媒体";
        } finally {
          this.formLoading = false;
        }
      }
      if(!this.formData.fid) this.formData.fid = getUUID()
      this.title = "新增实验多媒体";
    },
    /** 提交按钮 */
    async submitForm() {
      await this.$refs['formRef'].validate()
      if (!this.formData.experimentId) {
        this.$modal.msgWarning('请选择实验')
        return
      }
      // 新增必须先上传文件；修改允许沿用已有地址
      if (!this.formData.id && !this.hasUploadedFiles) {
        this.$modal.msgWarning('请先上传文件后再保存')
        return
      }
      if (this.formData.id && !String(this.formData.fileUrls || '').trim()) {
        this.$modal.msgWarning('文件地址不能为空，请重新上传')
        return
      }
      this.formLoading = true
      try {
        let data = { ...this.formData }
        if (data.sonBodyType) data.bodyType += ',' + data.sonBodyType
        if (data.id) {
          await ZkExperimentalMediaApi.updateZkExperimentalMedia(data)
          this.$modal.msgSuccess('修改成功')
          this.dialogVisible = false
          this.$emit('success')
          return
        }
        await ZkExperimentalMediaApi.createZkExperimentalMedia(data)
        this.$modal.msgSuccess('新增成功')
        this.dialogVisible = false
        this.$emit('success')
      } finally {
        this.formLoading = false
      }
    },
    /** 表单重置 */
    reset() {
      this.formData = {
        id: undefined,
        experimentId: undefined,
        experimentNumber: undefined,
        fileType: undefined,
        bodyType: undefined,
        fid: undefined,
        fileNames: undefined,
        fileUrls: undefined,
        deptId: undefined,
      }
      this.fileUrls = []
      this.fileNames = []
      this.resetForm('formRef')
    }
  }
};
</script>